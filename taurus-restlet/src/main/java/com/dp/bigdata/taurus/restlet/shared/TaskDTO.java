package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;
import java.util.Date;

import com.dp.bigdata.taurus.core.TaskStatus;
import com.dp.bigdata.taurus.generated.module.AlertRule;
import com.dp.bigdata.taurus.generated.module.Task;

public class TaskDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 482732054965365244L;

    private String taskid;

    private String name;

    private String creator;

    private String dependencyexpr;

    private Date addtime;

    private Date lastscheduletime;

    private Date updatetime;

    private String crontab;

    private String status;

    private Integer allowmultiinstances;

    private String proxyuser;

    private Integer waittimeout;

    private Integer executiontimeout;

    private Boolean isautoretry;

    private String filename;

    private Integer retrytimes;

    private String command;

    private Integer poolid;

    private String hostname;

    private String type;

    private String description;

    private int ruleID;

    private boolean hassms;

    private boolean hasmail;

    private String userid;

    private String groupid;

    private String conditions;

    public Task getTask() {
        Task task = new Task();
        task.setAddtime(addtime);
        task.setAllowmultiinstances(allowmultiinstances);
        task.setCommand(command);
        task.setCreator(creator);
        task.setCrontab(crontab);
        task.setDependencyexpr(dependencyexpr);
        task.setDescription(description);
        task.setExecutiontimeout(executiontimeout);
        task.setFilename(filename);
        task.setHostname(hostname);
        task.setIsautoretry(isautoretry);
        task.setName(name);
        task.setLastscheduletime(lastscheduletime);
        task.setPoolid(poolid);
        task.setProxyuser(proxyuser);
        task.setRetrytimes(retrytimes);
        task.setStatus(TaskStatus.getTaskRunState(status));
        task.setTaskid(taskid);
        task.setType(type);
        task.setUpdatetime(updatetime);
        task.setWaittimeout(waittimeout);
        return task;
    }

    public AlertRule getAlertRule() {
        AlertRule rule = new AlertRule();
        rule.setConditions(conditions);
        rule.setGroupid(groupid);
        rule.setHasmail(hasmail);
        rule.setHassms(hassms);
        rule.setId(ruleID);
        rule.setJobid(taskid);
        rule.setUserid(userid);
        return rule;
    }


    public String getStatus() {
        return status;
    }

    public String getTaskid() {
        return taskid;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public String getDependencyexpr() {
        return dependencyexpr;
    }

    public Date getAddtime() {
        return addtime;
    }

    public Date getLastscheduletime() {
        return lastscheduletime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public String getCrontab() {
        return crontab;
    }

    public Integer getAllowmultiinstances() {
        return allowmultiinstances;
    }

    public String getProxyuser() {
        return proxyuser;
    }

    public Integer getWaittimeout() {
        return waittimeout;
    }

    public Integer getExecutiontimeout() {
        return executiontimeout;
    }

    public Boolean getIsautoretry() {
        return isautoretry;
    }

    public Integer getRetrytimes() {
        return retrytimes;
    }

    public String getCommand() {
        return command;
    }

    public Integer getPoolid() {
        return poolid;
    }

    public String getHostname() {
        return hostname;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDependencyexpr(String dependencyexpr) {
        this.dependencyexpr = dependencyexpr;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public void setLastscheduletime(Date lastscheduletime) {
        this.lastscheduletime = lastscheduletime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public void setCrontab(String crontab) {
        this.crontab = crontab;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAllowmultiinstances(Integer allowmultiinstances) {
        this.allowmultiinstances = allowmultiinstances;
    }

    public void setProxyuser(String proxyuser) {
        this.proxyuser = proxyuser;
    }

    public void setWaittimeout(Integer waittimeout) {
        this.waittimeout = waittimeout;
    }

    public void setExecutiontimeout(Integer executiontimeout) {
        this.executiontimeout = executiontimeout;
    }

    public void setIsautoretry(Boolean isautoretry) {
        this.isautoretry = isautoretry;
    }

    public void setRetrytimes(Integer retrytimes) {
        this.retrytimes = retrytimes;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setPoolid(Integer poolid) {
        this.poolid = poolid;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRuleID() {
        return ruleID;
    }

    public boolean isHassms() {
        return hassms;
    }

    public boolean isHasmail() {
        return hasmail;
    }

    public String getUserid() {
        return userid;
    }

    public String getGroupid() {
        return groupid;
    }

    public String getConditions() {
        return conditions;
    }

    public void setRuleID(int ruleID) {
        this.ruleID = ruleID;
    }

    public void setHassms(boolean hassms) {
        this.hassms = hassms;
    }

    public void setHasmail(boolean hasmail) {
        this.hasmail = hasmail;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
