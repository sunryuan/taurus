package com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces;

import java.util.Set;

import org.apache.zookeeper.Watcher;

public interface DeploymentInfoChannel extends ClusterInfoChannel{

	//Deployment
	public void deploy(String ip, String taskID, Object conf, Object status, Watcher watcher);

	public void completeDeploy(String ip, String taskID);
	
	public Set<String> getNewDeploymentTaskIds(String ip, Watcher watcher);

	//Undeployment
	public void undeploy(String ip, String taskID, Object status, Watcher watcher);

	public void completeUndeploy(String ip, String taskID);
	
	public Set<String> getNewUnDeploymentTaskIds(String ip, Watcher watcher);
	
	//Share Methods
	public Object getConf(String ip, String taskID);
	
	public Object getStatus(String ip, String taskID, Watcher watcher);

	public void updateStatus(String ip, String taskID, Object status);
	
	public void updateConf(String ip, String taskID, Object conf);
}
