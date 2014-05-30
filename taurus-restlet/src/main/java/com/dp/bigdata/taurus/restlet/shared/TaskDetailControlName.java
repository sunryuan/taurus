package com.dp.bigdata.taurus.restlet.shared;

/**
 * 
 * TaskDetailControlName
 * @author damon.zhu
 *
 */
public enum TaskDetailControlName {

    TASKNAME("taskName"),
    TASKTYPE("taskType"),
    TASKPOOL("poolId"),
    TASKHOSTNAME("hostname"),
    TAKSSTATE("taskState"),
    TASKFILE("taskFile"),
    TASKCOMMAND("taskCommand"),
    ISAUTOKILL("isAutoKill"),
    CRONTAB("crontab"),
    DEPENDENCY("dependency"),
    PROXYUSER("proxyUser"),
    MAXEXECUTIONTIME("maxExecutionTime"),
    MAXWAITTIME("maxWaitTime"),
    ISAUTORETRY("isAutoRetry"),
    RETRYTIMES("retryTimes"),
    TASKID("taskId"),//TODO
    CREATOR("creator"),
    DESCRIPTION("description"),
    ALERTCONDITION("alertCondition"),
    ALERTTYPE("alertType"),
    ALERTGROUP("alertGroup"),
    ALERTUSER("alertUser"),
    TASKURL("taskUrl"),
    MAINCLASS("mainClass"),
    HADOOPNAME("hadoopName"),
    APPNAME("appName");
    
    private String name;
    
    TaskDetailControlName(String name){
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
