package com.dp.bigdata.taurus.core;


/**
 * 
 * TaskStatus
 * @author damon.zhu
 *
 */
public class TaskStatus {
	
    public static final int UNKNOWN = 0;
	public static final int RUNNING = 1;
	public static final int SUSPEND = 2;
	public static final int DELETED = 3;

	private static final String[] runStates = { "UNKNOWN", "SCHEDULED",
			"SUSPEND", "DELETED"};

	private int status;

	public TaskStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}

	/**
	 * Helper method to get human-readable state of the job.
	 * 
	 * @param state
	 *            job state
	 * @return human-readable state of the job
	 */
	public static String getTaskRunState(int state) {
		if (state < 1 || state >= runStates.length) {
			return runStates[0];
		}
		return runStates[state];
	}
	
	public static int getTaskRunState(String state){
	    for(int i = 0 ; i < runStates.length; i++){
	        if(runStates[i].equalsIgnoreCase(state)){
	            return i;
	        }
	    }
	    return UNKNOWN;
	}
}
