package com.dp.bigdata.taurus.zookeeper.common.infochannel.bean;

import java.io.Serializable;

public class DeploymentStatus implements Serializable{

	private static final long serialVersionUID = 7941127217044708903L;
	public static final int AGENT_UNAVAILABLE = -1;
	
	public static final int DEPLOY_SUBMITTED = 0;
	public static final int DEPLOY_SUCCESS = 1;
	public static final int DEPLOY_FAILED = 2;
	public static final int DEPLOY_TIMEOUT = 3;
	public static final int DEPLOYED = 4;
	
	public static final int DELETE_SUBMITTED = 100;
	public static final int DELETE_SUCCESS = 101;
	public static final int DELETE_FAILED = 102;
	public static final int DELETE_TIMEOUT = 103;
	public static final int DELETED = 104;
	
	public DeploymentStatus(int status) {
		super();
		this.status = status;
	}
	
	public DeploymentStatus(){}
	
	private int status;
	private String failureInfo;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getFailureInfo() {
		return failureInfo;
	}
	public void setFailureInfo(String description) {
		this.failureInfo = description;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((failureInfo == null) ? 0 : failureInfo.hashCode());
		result = prime * result + status;
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
		DeploymentStatus other = (DeploymentStatus) obj;
		if (failureInfo == null) {
			if (other.failureInfo != null)
				return false;
		} else if (!failureInfo.equals(other.failureInfo))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "DeploymentStatus [status=" + status + ", description="
				+ failureInfo + "]";
	}
}
