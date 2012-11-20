package com.dp.bigdata.taurus.frontend.client.workflow;

public enum WorkflowListField {
    WORKFLOWID("workflowId"),
    
    WORKFLOWNAME("workflowName"),
    
    CREATOR("creator"),
    
    CREATIONTIME("creationTime"),
    
    DELETE("delete");

    private String value;

    WorkflowListField(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
