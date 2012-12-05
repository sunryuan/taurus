package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * AttemptDTO
 * @author damon.zhu
 *
 */
public class AttemptDTO implements Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = -989989746827642633L;

   private String attemptID;
   
   private String instanceID;
   
   private String taskID;
   
   private Date startTime;
   
   private Date endTime;
   
   private Date scheduleTime;
   
   private int status;
   
   private int returnValue;
   
   private String execHost;

   public AttemptDTO(){}

   public AttemptDTO(String attemptID, String instanceID, String taskID, Date startTime, Date endTime,
                     Date scheduleTime, int status, int returnValue, String execHost) {
      super();
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

   public String getAttemptID() {
      return attemptID;
   }

   public void setAttemptID(String attemptID) {
      this.attemptID = attemptID;
   }

   public String getInstanceID() {
      return instanceID;
   }

   public void setInstanceID(String instanceID) {
      this.instanceID = instanceID;
   }

   public String getTaskID() {
      return taskID;
   }

   public void setTaskID(String taskID) {
      this.taskID = taskID;
   }

   public Date getStartTime() {
      return startTime;
   }

   public void setStartTime(Date startTime) {
      this.startTime = startTime;
   }

   public Date getEndTime() {
      return endTime;
   }

   public void setEndTime(Date endTime) {
      this.endTime = endTime;
   }

   public Date getScheduleTime() {
      return scheduleTime;
   }

   public void setScheduleTime(Date scheduleTime) {
      this.scheduleTime = scheduleTime;
   }

   public int getStatus() {
      return status;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public int getReturnValue() {
      return returnValue;
   }

   public void setReturnValue(int returnValue) {
      this.returnValue = returnValue;
   }

   public String getExecHost() {
      return execHost;
   }

   public void setExecHost(String execHost) {
      this.execHost = execHost;
   }
   
}
