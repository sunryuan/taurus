package com.dp.bigdata.taurus.zookeeper.common.infochannel.bean;

import java.io.Serializable;
import java.util.Map;

public class ScheduleConf implements Serializable{
	
	private static final long serialVersionUID = -3536162251727272809L;
	private String taskID;
	private String attemptID;
	private String command;
	private String taskType;
	private String userName;
	private String pid;
	private String taskUrl;
	private Map<String,String> extendedMap;
	
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
	public String getTaskUrl() {
		return taskUrl;
	}
	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}
    public Map<String, String> getExtendedMap() {
        return extendedMap;
    }
    public void setExtendedMap(Map<String, String> extendedMap) {
        this.extendedMap = extendedMap;
    }
}
