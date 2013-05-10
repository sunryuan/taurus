package com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces;

import java.util.Set;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;

public interface ScheduleInfoChannel extends ClusterInfoChannel{
	//execute task
	public void execute(String ip, String taskAttempt, Object conf, Object status);

	public void completeExecution(String ip, String taskAttempt);
	
	public Set<String> getNewExecutionJobInstanceIds(String ip);
	
	public void setExecutionJobListener(IZkChildListener childListener);

	//kill task
	public void killTask(String ip, String taskAttempt, Object status, IZkDataListener dataListener);

	public void completeKill(String ip, String taskAttempt,IZkDataListener dataListener);
	
	public Set<String> getNewKillingJobInstanceIds(String ip);
	
	public void setKillingJobListener(IZkChildListener childListener);
	
	//Share Methods
	public Object getConf(String ip, String taskAttempt);
	
	public Object getStatus(String ip, String taskAttempt);

	public void updateStatus(String ip, String taskAttempt, Object status);
	
	public void updateConf(String ip, String taskAttempt, Object conf);
	
	public Set<String> getRunningJobs(String ip);
	
	public void addRunningJob(String ip, String taskAttempt);
	
	public void removeRunningJob(String ip, String taskAttempt);
	
	public boolean needUpdate(String ip);
	
	public void completeUpdate(String ip);
	
	public void newUpdate(String ip);
	
}
