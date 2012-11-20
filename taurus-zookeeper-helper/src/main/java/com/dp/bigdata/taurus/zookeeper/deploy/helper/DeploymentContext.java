package com.dp.bigdata.taurus.zookeeper.deploy.helper;

import java.io.Serializable;

public class DeploymentContext implements Serializable{

	private static final long serialVersionUID = 6735053205566336847L;
	
	private String hdfsPath;
	private String taskID;

	public String getHdfsPath() {
        return hdfsPath;
    }
    public String getTaskID() {
        return taskID;
    }
    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }
    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }
  
}
