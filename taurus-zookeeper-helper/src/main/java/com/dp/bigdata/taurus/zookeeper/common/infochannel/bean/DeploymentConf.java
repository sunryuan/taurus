package com.dp.bigdata.taurus.zookeeper.common.infochannel.bean;

import java.io.Serializable;

public class DeploymentConf implements Serializable{

	private static final long serialVersionUID = 6735053205566336847L;
	
	private String hdfsPath;
	private String taskID;
	private String localPath;
	
	public String getHdfsPath() {
		return hdfsPath;
	}
	public void setHdfsPath(String hdfsPath) {
		this.hdfsPath = hdfsPath;
	}
	public String getTaskID() {
		return taskID;
	}
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hdfsPath == null) ? 0 : hdfsPath.hashCode());
		result = prime * result
				+ ((taskID == null) ? 0 : taskID.hashCode());
		result = prime * result
				+ ((localPath == null) ? 0 : localPath.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeploymentConf other = (DeploymentConf) obj;
		if (hdfsPath == null) {
			if (other.hdfsPath != null)
				return false;
		} else if (!hdfsPath.equals(other.hdfsPath))
			return false;
		if (taskID == null) {
			if (other.taskID != null)
				return false;
		} else if (!taskID.equals(other.taskID))
			return false;
		if (localPath == null) {
			if (other.localPath != null)
				return false;
		} else if (!localPath.equals(other.localPath))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "DeploymentConf [hdfsPath=" + hdfsPath + ", taskID="
				+ taskID + ", localPath=" + localPath + "]";
	}
}
