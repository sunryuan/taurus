package com.dp.bigdata.taurus.zookeeper.common.infochannel.bean;

import java.io.Serializable;

public class ScheduleStatus implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2768469281475075308L;

	public static final int AGENT_UNAVAILABLE = -1;
	public static final int UNKNOWN = 0;
	
	public static final int SCHEDULE_SUCCESS = 1;
	public static final int SCHEDULE_FAILED = 2;
	//public static final int SCHEDULED = 3;
	
	public static final int EXECUTING = 200;
	public static final int EXECUTE_SUCCESS = 201;
	public static final int EXECUTE_FAILED = 202;
	
//	public static final int DELETE_SUBMITTED = 100;
	public static final int DELETE_SUCCESS = 101;
//	public static final int DELETE_FAILED = 102;
//	public static final int DELETE_TIMEOUT = 103;
//	public static final int DELETED = 104;
	
	public ScheduleStatus(int status) {
		super();
		this.status = status;
	}
	
	public ScheduleStatus(){}
	
	private int status;
	private String failureInfo;
	private int returnCode;
	
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
	
	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((failureInfo == null) ? 0 : failureInfo.hashCode());
		result = prime * result + returnCode;
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
		ScheduleStatus other = (ScheduleStatus) obj;
		if (failureInfo == null) {
			if (other.failureInfo != null)
				return false;
		} else if (!failureInfo.equals(other.failureInfo))
			return false;
		if (returnCode != other.returnCode)
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ScheduleStatus [status=" + status + ", failureInfo="
				+ failureInfo + ", returnCode=" + returnCode + "]";
	}
}
