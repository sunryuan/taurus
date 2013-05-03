package com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces;

import java.util.Set;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;


public interface DeploymentInfoChannel extends ClusterInfoChannel{

	//Deployment
	public void deploy(String ip, String taskID, Object conf, Object status, IZkDataListener dataListener);

	public void completeDeploy(String ip, String taskID, IZkDataListener dataListener);
	
	public Set<String> getNewDeploymentTaskIds(String ip);
	
	public void setNewDeployDirListen(IZkChildListener childListener);

	//Undeployment
	public void undeploy(String ip, String taskID, Object status, IZkDataListener dataListener);
	
	public void completeUndeploy(String ip, String taskID, IZkDataListener dataListener);
	
	public Set<String> getNewUnDeploymentTaskIds(String ip);
	
	public void setNewUndeployDirListen(IZkChildListener childListener);

	//Share Methods
	public Object getConf(String ip, String taskID);
	
	public Object getStatus(String ip, String taskID);

	public void updateStatus(String ip, String taskID, Object status);
	
	public void updateConf(String ip, String taskID, Object conf);
}
