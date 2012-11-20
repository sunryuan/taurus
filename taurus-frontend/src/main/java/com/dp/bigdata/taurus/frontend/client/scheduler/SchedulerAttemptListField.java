package com.dp.bigdata.taurus.frontend.client.scheduler;

public enum SchedulerAttemptListField {
    ATTEMPTID("attemptID"),
    
    INSTANCEID("instanceID"),
    
    STARTTIME("startTime"),
    
    ENDTIME("endTime"),
    
    RETURNVALUE("returnValue"),
    
    STATUS("status"),
    
    COMMAND("command");

    private String value;

    SchedulerAttemptListField(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
