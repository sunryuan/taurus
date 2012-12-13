package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * AttemptDTO
 * 
 * @author damon.zhu
 */
public class AttemptDTO implements Serializable {

    /**
    * 
    */
    private static final long serialVersionUID = -989989746827642633L;
    
    private int id;

    private String attemptID;

    private String instanceID;

    private String taskID;

    private Date startTime;

    private Date endTime;

    private Date scheduleTime;

    private String status;

    private int returnValue;

    private String execHost;

    public AttemptDTO() {
    }

    public AttemptDTO(int id, String attemptID, String instanceID, String taskID, Date startTime, Date endTime, Date scheduleTime,
                      String status, int returnValue, String execHost) {
        super();
        this.id = id;
        this.attemptID = attemptID;
        this.instanceID = instanceID;
        this.taskID = taskID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduleTime = scheduleTime;
        this.status = status;
        this.returnValue = returnValue;
        this.execHost = execHost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAttemptID() {
        return attemptID;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public String getTaskID() {
        return taskID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public String getStatus() {
        return status;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public String getExecHost() {
        return execHost;
    }

    public void setAttemptID(String attemptID) {
        this.attemptID = attemptID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
    }

    public void setExecHost(String execHost) {
        this.execHost = execHost;
    }

    @Override
    public String toString() {
        return "AttemptDTO [id=" + id + ", attemptID=" + attemptID + ", instanceID=" + instanceID + ", taskID=" + taskID
                + ", startTime=" + startTime + ", endTime=" + endTime + ", scheduleTime=" + scheduleTime + ", status=" + status
                + ", returnValue=" + returnValue + ", execHost=" + execHost + "]";
    }

}
