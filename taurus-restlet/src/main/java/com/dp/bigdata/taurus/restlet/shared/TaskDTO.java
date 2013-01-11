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

    private final Task task;
    private final AlertRule rule;

    public TaskDTO() {
        task = new Task();
        rule = new AlertRule();
    }

    public Task getTask() {
        return task;
    }

    public AlertRule getAlertRule() {
        return rule;
    }

    public String getTaskid() {
        return task.getTaskid();
    }

    public void setTaskid(String taskid) {
        task.setTaskid(taskid);
    }

    public String getName() {
        return task.getName();
    }

    public void setName(String name) {
        task.setName(name);
    }

    public String getCreator() {
        return task.getCreator();
    }

    public void setCreator(String creator) {
        task.setCreator(creator);
    }

    public String getDependencyexpr() {
        return task.getDependencyexpr();
    }

    public void setDependencyexpr(String dependencyexpr) {
        task.setDependencyexpr(dependencyexpr);
    }

    public Date getAddtime() {
        return task.getAddtime();
    }

    public void setAddtime(Date addtime) {
        task.setAddtime(addtime);
    }

    public Date getLastscheduletime() {
        return task.getLastscheduletime();
    }

    public void setLastscheduletime(Date lastscheduletime) {
        task.setLastscheduletime(lastscheduletime);
    }

    public Date getUpdatetime() {
        return task.getUpdatetime();
    }

    public void setUpdatetime(Date updatetime) {
        task.setUpdatetime(updatetime);
    }

    public String getCrontab() {
        return task.getCrontab();
    }

    public void setCrontab(String crontab) {
        task.setCrontab(crontab);
    }

    public String getStatus() {
        if(task.getStatus() == TaskStatus.RUNNING){
            return "RUNNING";
        }else if(task.getStatus() == TaskStatus.SUSPEND){
            return "SUSPEND";
        }else{
            return "UNKNOW";
        }
    }

    public void setStatus(Integer status) {
        task.setStatus(status);
    }

    public Integer getAllowmultiinstances() {
        return task.getAllowmultiinstances();
    }

    public void setAllowmultiinstances(Integer allowmultiinstances) {
        task.setAllowmultiinstances(allowmultiinstances);
    }

    public String getProxyuser() {
        return task.getProxyuser();
    }

    public void setProxyuser(String proxyuser) {
        task.setProxyuser(proxyuser);
    }

    public Integer getWaittimeout() {
        return task.getWaittimeout();
    }

    public void setWaittimeout(Integer waittimeout) {
        task.setWaittimeout(waittimeout);
    }

    public Integer getExecutiontimeout() {
        return task.getExecutiontimeout();
    }

    public void setExecutiontimeout(Integer executiontimeout) {
        task.setExecutiontimeout(executiontimeout);
    }

    public Boolean getIsautoretry() {
        return task.getIsautoretry();
    }

    public void setIsautoretry(Boolean isautoretry) {
        task.setIsautoretry(isautoretry);
    }

    public Integer getRetrytimes() {
        return task.getRetrytimes();
    }

    public void setRetrytimes(Integer retrytimes) {
        task.setRetrytimes(retrytimes);
    }

    public String getCommand() {
        return task.getCommand();
    }

    public void setCommand(String command) {
        task.setCommand(command);
    }

    public String getHostname() {
        return task.getHostname();
    }

    public void setHostname(String hostname) {
        task.setHostname(hostname);
    }

    public Integer getPoolid() {
        return task.getPoolid();
    }

    public void setPoolid(Integer poolid) {
        task.setPoolid(poolid);
    }

    public String getType() {
        return task.getType();
    }

    public void setType(String type) {
        task.setType(type);
    }

    public String getFilename() {
        return task.getFilename();
    }

    public void setFilename(String filename) {
        task.setFilename(filename);
    }

    public String getDescription() {
        return task.getDescription();
    }

    public void setDescription(String description) {
        task.setDescription(description);
    }

    public Integer getId() {
        return rule.getId();
    }

    public void setId(Integer id) {
        rule.setId(id);
    }

    public String getJobid() {
        return rule.getJobid();
    }

    public void setJobid(String jobid) {
        rule.setJobid(jobid);
    }

    public Boolean getHassms() {
        return rule.getHassms();
    }

    public void setHassms(Boolean hassms) {
        rule.setHassms(hassms);
    }

    public Boolean getHasmail() {
        return rule.getHasmail();
    }

    public void setHasmail(Boolean hasmail) {
        rule.setHasmail(hasmail);
    }

    public String getUserid() {
        return rule.getUserid();
    }

    public void setUserid(String userid) {
        rule.setUserid(userid);
    }

    public String getGroupid() {
        return rule.getGroupid();
    }

    public void setGroupid(String groupid) {
        rule.setGroupid(groupid);
    }

    public String getConditions() {
        return rule.getConditions();
    }

    public void setConditions(String conditions) {
        rule.setConditions(conditions);
    }

}
