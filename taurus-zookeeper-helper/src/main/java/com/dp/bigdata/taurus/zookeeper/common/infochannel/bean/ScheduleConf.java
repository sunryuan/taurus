package com.dp.bigdata.taurus.zookeeper.common.infochannel.bean;

import java.io.Serializable;

public class ScheduleConf implements Serializable{
	
	private static final long serialVersionUID = -3536162251727272809L;
	private String taskID;
	private String attemptID;
	private String command;
	private String taskType;
	private String userName;
	private String pid;
	public String getTaskID() {
		return taskID;
	}
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
	public String getAttemptID() {
		return attemptID;
	}
	public void setAttemptID(String attemptID) {
		this.attemptID = attemptID;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}

}
