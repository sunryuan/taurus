package com.dp.bigdata.taurus.frontend.client.task;

public enum TaskType {
    HADOOP("hadoop"),
    
    HIVE("hive"),
    
    SHELL_SCRIPT("shell script"),
    
    WORMHOLE("wormhole"),
    
    GREENPLUM("greenplum");

    private String value;

    TaskType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

}
