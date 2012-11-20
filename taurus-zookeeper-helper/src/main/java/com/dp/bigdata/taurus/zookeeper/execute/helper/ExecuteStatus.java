package com.dp.bigdata.taurus.zookeeper.execute.helper;

/**
 * 
 * ExecuteStatus
 * @author damon.zhu
 *
 */
public class ExecuteStatus {

    public static final int INITIALIZED = 1;
    public static final int DEPENDENCY_PASS = 2;
    public static final int DEPENDENCY_TIMEOUT = 3;
    public static final int SUBMIT_SUCCESS = 4;
    public static final int SUBMIT_FAIL = 5;
    public static final int RUNNING = 6;
    public static final int SUCCEEDED = 7;
    public static final int FAILED = 8;
    public static final int TIMEOUT = 9;
    public static final int KILLED = 10;
    public static final int UNKNOWN = 11;

    private static final String[] runStates = { "INITIALIZED", "DEPENDENCY_PASS", "DEPENDENCY_TIMEOUT", "SUBMIT_SUCCESS",
            "SUBMIT_FAIL", "RUNNING", "SUCCEEDED", "FAILED", "TIMEOUT", "KILLED", "UNKNOWN" };

    private int status;
    private int returnCode;

    public ExecuteStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    /**
     * Helper method to get human-readable state of the job.
     * 
     * @param state job state
     * @return human-readable state of the job
     */
    public static String getInstanceRunState(int state) {
        if (state < 1 || state >= runStates.length) {
            return "UNKNOWN";
        }
        return runStates[state - 1];
    }
    
    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}