package com.dp.bigdata.taurus.zookeeper.execute.helper;

import java.util.Map;

/**
 * ExecuteContext
 * 
 * @author damon.zhu
 */
public class ExecuteContext {

    private String taskID;
    private String attemptID;
    private String agentIP;
    private String proxyUser;
    private String type;
    private String command;
    private String taskUrl;
    private Map<String, String> extendedConfs;

    public ExecuteContext(String taskID, String attemptID, String agentIP, String proxyUser, String type, String command,
                          String taskUrl, Map<String, String> extendedConfs) {
        super();
        this.taskID = taskID;
        this.attemptID = attemptID;
        this.agentIP = agentIP;
        this.proxyUser = proxyUser;
        this.type = type;
        this.command = command;
        this.taskUrl = taskUrl;
        this.extendedConfs = extendedConfs;
    }

    public String getTaskID() {
        return taskID;
    }

    public String getAttemptID() {
        return attemptID;
    }

    public String getAgentIP() {
        return agentIP;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public String getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public void setAttemptID(String attemptID) {
        this.attemptID = attemptID;
    }

    public void setAgentIP(String agentIP) {
        this.agentIP = agentIP;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
    }

    public Map<String, String> getExtendedConfs() {
        return extendedConfs;
    }

    public void setExtendedConfs(Map<String, String> extendedConfs) {
        this.extendedConfs = extendedConfs;
    }

}
