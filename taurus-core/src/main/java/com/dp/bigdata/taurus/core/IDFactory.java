package com.dp.bigdata.taurus.core;

/**
 * 
 * @author damon.zhu
 *
 */
public interface IDFactory {
    
    public void setIdentity(String identity);
	
	public String newTaskID();
	
	public String newInstanceID(String taskID);
	
	public String newInstanceID(TaskID taskID);
	
	public String newAttemptID(String instanceID);
	
	public String newAttemptID(InstanceID instanceID);
	
}
