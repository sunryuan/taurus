package com.dp.bigdata.taurus.frontend.client.scheduler;

public class InstanceStatus {
	public static final int INITIALIZED = 1;
	public static final int DEPENDENCY_PASS = 2;
	public static final int DEPENDENCY_FAIL = 3;
	public static final int RUNNING = 4;
	public static final int SUCCEEDED = 5;
	public static final int FAILED = 6;
	public static final int TIMEOUT = 7;
	

	private static final String UNKNOWN = "UNKNOWN";
	private static final String[] runStates = { "INITIALIZED", "DEPENDENCY_PASS",
			"DEPENDENCY_FAIL", "RUNNING", "SUCCEEDED", "FAILED", "TIMEOUT", UNKNOWN };

	private int status;

	public InstanceStatus(int status) {
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
	public static String getInstanceRunState(int state) {
		if (state < 1 || state >= runStates.length) {
			return UNKNOWN;
		}
		return runStates[state - 1];
	}

}
