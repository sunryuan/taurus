package com.dp.bigdata.taurus.frontend.client.workflow;

public enum AddWorkflowListField {
    TASK("task"),
    
    DEPENDENTTASKSTATEXPR("dependentTaskStatExpr");

    private String value;

    AddWorkflowListField(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
