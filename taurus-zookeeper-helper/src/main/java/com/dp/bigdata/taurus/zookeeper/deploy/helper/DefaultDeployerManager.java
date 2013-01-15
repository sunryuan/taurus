package com.dp.bigdata.taurus.zookeeper.deploy.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.DeploymentConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.DeploymentStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.guice.DeploymentInfoChannelModule;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DefaultDeployerManager implements Deployer{


	private static final Log LOGGER = LogFactory.getLog(DefaultDeployerManager.class);
	private static final int DEFAULT_TIME_OUT_IN_SECONDS = 60;
	private static Map<String, Lock> taskIDToLockMap = new HashMap<String, Lock>();

	private final DeploymentInfoChannel dic;
	private final int opTimeout = DEFAULT_TIME_OUT_IN_SECONDS;
	
	public DefaultDeployerManager(){
		Injector injector = Guice.createInjector(new DeploymentInfoChannelModule());
		dic = injector.getInstance(DeploymentInfoChannel.class);
	}
	
	private static Lock getLock(String taskID){
		synchronized(taskIDToLockMap){
			Lock lock = taskIDToLockMap.get(taskID);
			if(lock == null){
				lock = new ReentrantLock();
				taskIDToLockMap.put(taskID, lock);
			}
			return lock;
		}
	}
    @Override
    public void deploy(String agentIp, DeploymentContext context) throws DeploymentException {
    	String taskId = context.getTaskID();
    	String hdfsPath = context.getHdfsPath();
    	
    	if(!dic.exists(MachineType.AGENT,agentIp)){
    	    LOGGER.error("Agent unavailable");
			throw new DeploymentException("Agent unavailable");
		}else{
			DeploymentStatus status = (DeploymentStatus) dic.getStatus(agentIp, taskId, null);
			if(status == null || status.getStatus() != DeploymentStatus.DEPLOY_SUCCESS){
				DeploymentConf conf = new DeploymentConf();
				conf.setHdfsPath(hdfsPath);
				conf.setTaskID(taskId);
				status = new DeploymentStatus();
				status.setStatus(DeploymentStatus.DEPLOY_SUBMITTED);
				Lock lock = getLock(taskId);
				try{
					lock.lock();
					Condition deployFinish = lock.newCondition();
					DeploymentStatusWatcher w = new DeploymentStatusWatcher(lock, deployFinish, dic, agentIp, taskId);
					dic.deploy(agentIp, taskId, conf, status, w);
					if(!deployFinish.await(opTimeout, TimeUnit.SECONDS)){
						status = (DeploymentStatus) dic.getStatus(agentIp, taskId, null);
						if(status != null && status.getStatus() == DeploymentStatus.DEPLOY_SUBMITTED){
							status.setStatus(DeploymentStatus.DEPLOY_TIMEOUT);
							dic.updateStatus(agentIp, taskId, status);
					        LOGGER.error("Task " + taskId + "deploy time out");
							throw new DeploymentException("Task " + taskId + "deploy time out");
						}
					}else{
						status = w.getDeploymentStatus();
					}
					
					if(status != null && status.getStatus() == DeploymentStatus.DEPLOY_SUCCESS){
						dic.completeDeploy(agentIp, taskId);
					}
				} catch (InterruptedException e){
                    LOGGER.error("Task " + taskId + " deploy failed",e);
					throw new DeploymentException(e);
				}
				finally{
					lock.unlock();
				}
				if(status.getStatus() != DeploymentStatus.DEPLOY_SUCCESS) {
                    LOGGER.error("Task " + taskId + " deploy failed");
                    throw new DeploymentException("Task " + taskId + " deploy failed");
				}
				
			}
			else{
                LOGGER.error("Task " + taskId + " deploy failed");
				throw new DeploymentException("Task " + taskId + " is already deployed!");
			}
		}
        
    }

    @Override
    public void undeploy(String agentIp, DeploymentContext context) throws DeploymentException {
    	String taskId = context.getTaskID();
//    	String hdfsPath = context.getHdfsPath();
    	
    	DeploymentStatus status = new DeploymentStatus();
		if(!dic.exists(MachineType.AGENT, agentIp)){
		    LOGGER.error("Agent unavailable");
			throw new DeploymentException("Agent unavailable");
		}else{
			status = (DeploymentStatus) dic.getStatus(agentIp, taskId, null);
			if(status == null){
		        LOGGER.error("Task " + taskId + " is already deleted!");
				throw new DeploymentException("Task " + taskId + " is already deleted!");
			}else{
				status.setStatus(DeploymentStatus.DELETE_SUBMITTED);
				Lock lock = getLock(taskId);
				try{
					lock.lock();
					Condition deployFinish = lock.newCondition();
					DeploymentStatusWatcher w = new DeploymentStatusWatcher(lock, deployFinish, dic, agentIp, taskId);
					dic.undeploy(agentIp, taskId, status, w);
					if(!deployFinish.await(opTimeout, TimeUnit.SECONDS)){
						status = (DeploymentStatus) dic.getStatus(agentIp, taskId, null);
						if(status != null && status.getStatus() == DeploymentStatus.DELETE_SUBMITTED){
							status.setStatus(DeploymentStatus.DELETE_TIMEOUT);
						}
					}else{
						status = w.getDeploymentStatus();
					}
					
					if(status != null && status.getStatus() == DeploymentStatus.DELETE_SUCCESS){
						dic.completeUndeploy(agentIp, taskId);
					}
				} catch (InterruptedException e){
				    LOGGER.error("Task " + taskId + "delete failed",e);
					throw new DeploymentException(e);
				} finally{
					lock.unlock();
				}
				if(status.getStatus() != DeploymentStatus.DELETE_SUCCESS) {
                    LOGGER.error("Task " + taskId + "delete failed");
					throw new DeploymentException("Task " + taskId + "delete failed");
				}
			}
		}
    }
    
    private static final class DeploymentStatusWatcher implements Watcher{

		private final Condition deployFinish;
		private final Lock lock;
		private DeploymentStatus status;
		private final DeploymentInfoChannel dic;
		private final String agentIp;
		private final String taskID;

		DeploymentStatusWatcher(Lock lock, Condition deployFinish, DeploymentInfoChannel cs, String agentIp,
				String taskID){
			this.lock = lock;
			this.deployFinish = deployFinish;
			this.dic = cs;
			this.agentIp = agentIp;
			this.taskID = taskID;
		}

		@Override
		public void process(WatchedEvent event) {
			try{
				lock.lock();
				status = (DeploymentStatus) dic.getStatus(agentIp, taskID, null);
				deployFinish.signal();
			} finally{
				lock.unlock();
			}
		}

		public DeploymentStatus getDeploymentStatus(){
			return status;
		}
	}
}
