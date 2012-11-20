package com.dp.bigdata.taurus.frontend.client.scheduler;

public enum SchedulerTaskListField {
    TASKID("taskId"),
    
    TASKNAME("taskName"),
    
    EXECUTOR("executor"),
    
    CRONTAB("crontab"),
    
    TASKTYPE("taskType"),
    
    TASKCOMMAND("taskCommand");

    private String value;

    SchedulerTaskListField(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
