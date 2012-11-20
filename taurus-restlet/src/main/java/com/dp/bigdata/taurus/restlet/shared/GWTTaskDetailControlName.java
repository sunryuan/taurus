package com.dp.bigdata.taurus.restlet.shared;

/**
 * 
 * GWTTaskDetailControlName
 * @author damon.zhu
 *
 */
public enum GWTTaskDetailControlName {

    TASKNAME("taskName"),
    TASKTYPE("taskType"),
    TASKPOOL("poolId"),
    TAKSSTATE("taskState"),
    TASKFILE("taskFile"),
    TASKCOMMAND("taskCommand"),
    MULTIINSTANCE("multiInstance"),
    CRONTAB("crontab"),
    DEPENDENCY("dependency"),
    PROXYUSER("proxyUser"),
    MAXEXECUTIONTIME("maxExecutionTime"),
    MAXWAITTIME("maxWaitTime"),
    ISAUTORETRY("isAutoRetry"),
    RETRYTIMES("retryTimes"),
    TASKID("taskId"),//TODO
	TASKMAIL("taskMail");//TODO
    
    private String name;
    
    GWTTaskDetailControlName(String name){
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
