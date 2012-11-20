package com.dp.bigdata.taurus.frontend.client.task;

public enum TaskListField {
    TASKID("taskId"),
    
    TASKNAME("taskName"),
    
    TASKCREATOR("taskCreator"),
    
    CREATIONTIME("creationTime"),
    
    TASKTYPE("taskType"),
    
    TASKPOOL("taskPool"),
    
    TASKDEPLOY("taskDeploy");

    private String value;

    TaskListField(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
