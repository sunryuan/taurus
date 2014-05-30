package com.dp.bigdata.taurus.zookeeper.deploy.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.I0Itec.zkclient.IZkDataListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.DeploymentConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.DeploymentStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.guice.DeploymentInfoChannelModule;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;
import com.dp.bigdata.taurus.zookeeper.common.utils.IPUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DefaultDeployerManager implements Deployer {

	private static final Log LOGGER = LogFactory.getLog(DefaultDeployerManager.class);

	private static final int DEFAULT_TIME_OUT_IN_SECONDS = 60;

	private static Map<String, Lock> taskIDToLockMap = new HashMap<String, Lock>();

	private final DeploymentInfoChannel dic;

	private final int opTimeout = DEFAULT_TIME_OUT_IN_SECONDS;

	public DefaultDeployerManager() {
		Injector injector = Guice.createInjector(new DeploymentInfoChannelModule());
		dic = injector.getInstance(DeploymentInfoChannel.class);
		dic.connectToCluster(MachineType.SERVER, IPUtils.getFirstNoLoopbackIP4Address());
	}

	private static Lock getLock(String taskID) {
		synchronized (taskIDToLockMap) {
			Lock lock = taskIDToLockMap.get(taskID);
			if (lock == null) {
				lock = new ReentrantLock();
				taskIDToLockMap.put(taskID, lock);
			}
			return lock;
		}
	}

	@Override
	public String deploy(String agentIp, DeploymentContext context) throws DeploymentException {
		String deployId = context.getDepolyId();
		String name = context.getName();
		String url = context.getUrl();
		DeploymentStatus status = (DeploymentStatus) dic.getStatus(agentIp, deployId);
		if (status == null || status.getStatus() != DeploymentStatus.DEPLOY_SUCCESS) {
			DeploymentConf conf = new DeploymentConf();
			conf.setUrl(url);
			conf.setName(name);
			status = new DeploymentStatus();
			status.setStatus(DeploymentStatus.DEPLOY_SUBMITTED);
			Lock lock = getLock(deployId);
			try {
				lock.lock();
				Condition deployFinish = lock.newCondition();
				DeploymentListener listener = new DeploymentListener(lock, deployFinish, dic, agentIp, deployId);
				dic.deploy(agentIp, deployId, conf, status, listener);
				if (!deployFinish.await(opTimeout, TimeUnit.SECONDS)) {
					status = (DeploymentStatus) dic.getStatus(agentIp, deployId);
					if (status != null && status.getStatus() == DeploymentStatus.DEPLOY_SUBMITTED) {
						status.setStatus(DeploymentStatus.DEPLOY_TIMEOUT);
						dic.updateStatus(agentIp, deployId, status);
						LOGGER.error(deployId + " deploy time out");
						throw new DeploymentException(deployId + " deploy time out").setStatus(DeployStatus.TIMEOUT);
					}
				} else {
					status = listener.getDeploymentStatus();
				}

				if (status != null && status.getStatus() == DeploymentStatus.DEPLOY_SUCCESS) {
					dic.completeDeploy(agentIp, deployId, listener);
				}
			} catch (InterruptedException e) {
				LOGGER.error(deployId + " deploy failed", e);
				throw new DeploymentException(e).setStatus(DeployStatus.FAIL);
			} finally {
				lock.unlock();
			}
			if (status.getStatus() != DeploymentStatus.DEPLOY_SUCCESS) {
				LOGGER.error(deployId + " deploy failed");
				throw new DeploymentException(deployId + " deploy failed").setStatus(DeployStatus.FAIL);
			}
			conf = (DeploymentConf) dic.getConf(agentIp, deployId);
			return conf.getLocalPath();
		} else {
			LOGGER.error(deployId + " deploy failed");
			throw new DeploymentException(deployId + " is already deployed!").setStatus(DeployStatus.DUPLICATE);
		}

	}

	@Override
	public void undeploy(String agentIp, DeploymentContext context) throws DeploymentException {
		String deployId = context.getDepolyId();
		DeploymentStatus status = new DeploymentStatus();
		status = (DeploymentStatus) dic.getStatus(agentIp, deployId);
		if (status == null) {
			LOGGER.error("Task " + deployId + " is already deleted!");
			throw new DeploymentException("Task " + deployId + " is already deleted!");
		} else {
			status.setStatus(DeploymentStatus.DELETE_SUBMITTED);
			Lock lock = getLock(deployId);
			try {
				lock.lock();
				Condition deployFinish = lock.newCondition();
				DeploymentListener listener = new DeploymentListener(lock, deployFinish, dic, agentIp, deployId);
				dic.undeploy(agentIp, deployId, status, listener);
				if (!deployFinish.await(opTimeout, TimeUnit.SECONDS)) {
					status = (DeploymentStatus) dic.getStatus(agentIp, deployId);
					if (status != null && status.getStatus() == DeploymentStatus.DELETE_SUBMITTED) {
						status.setStatus(DeploymentStatus.DELETE_TIMEOUT);
					}
				} else {
					status = listener.getDeploymentStatus();
				}

				if (status != null && status.getStatus() == DeploymentStatus.DELETE_SUCCESS) {
					dic.completeUndeploy(agentIp, deployId, listener);
				}
			} catch (InterruptedException e) {
				LOGGER.error("Task " + deployId + "delete failed", e);
				throw new DeploymentException(e);
			} finally {
				lock.unlock();
			}
			if (status.getStatus() != DeploymentStatus.DELETE_SUCCESS) {
				LOGGER.error("Task " + deployId + "delete failed");
				throw new DeploymentException("Task " + deployId + "delete failed");
			}
		}
	}

	private static final class DeploymentListener implements IZkDataListener {

		private final Condition deployFinish;

		private final Lock lock;

		private DeploymentStatus status;

		private final DeploymentInfoChannel dic;

		private final String agentIp;

		private final String taskID;

		DeploymentListener(Lock lock, Condition deployFinish, DeploymentInfoChannel cs, String agentIp, String taskID) {
			this.lock = lock;
			this.deployFinish = deployFinish;
			this.dic = cs;
			this.agentIp = agentIp;
			this.taskID = taskID;
		}

		public DeploymentStatus getDeploymentStatus() {
			return status;
		}

		/*
		 * (non-Javadoc)
		 * @see org.I0Itec.zkclient.IZkDataListener#handleDataChange(java.lang.String, java.lang.Object)
		 */
		@Override
		public void handleDataChange(String dataPath, Object data) throws Exception {
			try {
				lock.lock();
				status = (DeploymentStatus) dic.getStatus(agentIp, taskID);
				deployFinish.signal();
			} finally {
				lock.unlock();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.I0Itec.zkclient.IZkDataListener#handleDataDeleted(java.lang.String)
		 */
		@Override
		public void handleDataDeleted(String dataPath) throws Exception {

		}
	}


}
