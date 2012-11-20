package com.dp.bigdata.taurus.core;

/**
 * AttemptID
 * 
 * @author damon.zhu
 */
public class AttemptID extends ID {
    protected static final String ATTEMPT = "attempt";

    public static final String ATTEMPTID_REGEX = ATTEMPT + SEPARATOR + "[0-9]+" + SEPARATOR + "[0-9]+" + SEPARATOR + "[0-9]+"
            + SEPARATOR + "[0-9]+";

    private String instanceID;

    public AttemptID(InstanceID instanceID, int id) {
        super(id);
        String[] parts = instanceID.toString().split(SEPARATOR);
        this.instanceID = parts[1] + SEPARATOR + parts[2] + SEPARATOR + parts[3];
    }

    public StringBuilder appendTo(StringBuilder builder) {
        builder.append(SEPARATOR);
        builder.append(instanceID);
        builder.append(SEPARATOR);
        builder.append(idFormat.format(id));
        return builder;
    }
    public static String getTaskID(String attemptID) {
        String[] splits = attemptID.split(SEPARATOR);
        return TaskID.TASK + SEPARATOR + splits[1] + SEPARATOR + splits[2];
    }

    public String toString() {
        return appendTo(new StringBuilder(ATTEMPT)).toString();
    }
}
