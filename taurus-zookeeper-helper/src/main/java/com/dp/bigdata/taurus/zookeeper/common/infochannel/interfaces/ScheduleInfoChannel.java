package com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces;

import java.util.Set;

import org.apache.zookeeper.Watcher;

public interface ScheduleInfoChannel extends ClusterInfoChannel{
	//execute task
	public void execute(String ip, String taskAttempt, Object conf, Object status);

	public void completeExecution(String ip, String taskAttempt);
	
	public Set<String> getNewExecutionJobInstanceIds(String ip, Watcher watcher);

	//kill task
	public void killTask(String ip, String taskAttempt, Object status, Watcher watcher);

	public void completeKill(String ip, String taskAttempt);
	
	public Set<String> getNewKillingJobInstanceIds(String ip, Watcher watcher);
	
	//Share Methods
	public Object getConf(String ip, String taskAttempt);
	
	public Object getStatus(String ip, String taskAttempt, Watcher watcher);

	public void updateStatus(String ip, String taskAttempt, Object status);
	
	public void updateConf(String ip, String taskAttempt, Object conf);
}
