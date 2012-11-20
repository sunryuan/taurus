package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.TaurusZKException;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;
import com.google.inject.Inject;

class TaurusZKDeploymentInfoChannel extends TaurusZKInfoChannel implements DeploymentInfoChannel{

	private static final String ASSIGNMENTS = "assignments";
	private static final String CONF = "conf";
	private static final String STATUS = "status";
	private static final String NEW = "new";
	private static final String DELETE = "delete";
	
	@Inject
	TaurusZKDeploymentInfoChannel(ZooKeeper zk){
		super(zk);
	}
	
	@Override
	public void connectToCluster(MachineType mt, String ip) {
		try{
			super.connectToCluster(mt, ip);
			if(!existPath(BASE, ASSIGNMENTS)){
				mkPath(BASE, ASSIGNMENTS);
			}
			if(mt == MachineType.AGENT){
				mkPath(BASE, ASSIGNMENTS, ip);
				mkPath(BASE, ASSIGNMENTS, ip, NEW);
				mkPath(BASE, ASSIGNMENTS, ip, DELETE);
			}
		} catch(Exception e){
			
		}
	}
	
	@Override
	public void deploy(String ip, String taskID, Object conf,
			Object status, Watcher watcher) {
		try{
			mkPath(BASE, ASSIGNMENTS, ip, taskID);
			mkPath(BASE, ASSIGNMENTS, ip, taskID, CONF);
			mkPath(BASE, ASSIGNMENTS, ip, taskID, STATUS);
			setData(conf, BASE, ASSIGNMENTS, ip, taskID, CONF);
			setData(status, BASE, ASSIGNMENTS, ip, taskID, STATUS);
			addDeploymentStatusWatcher(ip, taskID, watcher);
			mkPath(BASE, ASSIGNMENTS, ip, NEW, taskID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void completeDeploy(String ip, String taskID) {
		try{
			rmPath(BASE, ASSIGNMENTS, ip, NEW, taskID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public Set<String> getNewDeploymentTaskIds(String ip, Watcher watcher) {
		try{
			return Collections.unmodifiableSet(new HashSet<String>(
					getChildrenNodeName(watcher, BASE, ASSIGNMENTS, ip, NEW)));
		} catch(Exception e){
			return Collections.unmodifiableSet(new HashSet<String>(0));
		}
	}

	@Override
	public void undeploy(String ip, String taskID, Object status,
			Watcher watcher) {
		try{
			setData(status, BASE, ASSIGNMENTS, ip, taskID, STATUS);
			addDeploymentStatusWatcher(ip, taskID, watcher);
			mkPath(BASE, ASSIGNMENTS, ip, DELETE, taskID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void completeUndeploy(String ip, String taskID) {
		try{
			rmPath(BASE, ASSIGNMENTS, ip, DELETE, taskID);
			rmPath(BASE, ASSIGNMENTS, ip, taskID, STATUS);
			rmPath(BASE, ASSIGNMENTS, ip, taskID, CONF);
			rmPath(BASE, ASSIGNMENTS, ip, taskID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public Set<String> getNewUnDeploymentTaskIds(String ip,
			Watcher watcher) {
		try{
			return Collections.unmodifiableSet(new HashSet<String>(
					getChildrenNodeName(watcher, BASE, ASSIGNMENTS, ip, DELETE)));
		} catch(Exception e){
			return Collections.unmodifiableSet(new HashSet<String>(0));
		}
	}

	@Override
	public Object getConf(String ip, String taskID) {
		try{
			return getData(BASE, ASSIGNMENTS, ip, taskID, CONF);
		} catch(Exception e){
			return null;
		}
	}

	@Override
	public Object getStatus(String ip, String taskID, Watcher watcher) {
		try{
			return getData(BASE, ASSIGNMENTS, ip, taskID, STATUS);
		} catch(Exception e){
			return null;
		}
	}

	@Override
	public void updateStatus(String ip, String taskID, Object status) {
		try{
			setData(status, BASE, ASSIGNMENTS, ip, taskID, STATUS);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void updateConf(String ip, String taskID, Object conf) {
		try{
			setData(conf, BASE, ASSIGNMENTS, ip, taskID, CONF);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}
	
	private void addDeploymentStatusWatcher(String ip,
			String taskID, Watcher watcher) {
		try{
			addDataWatcher(watcher, BASE, ASSIGNMENTS, ip, taskID, STATUS);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

}
