package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * TaskDTO
 * @author damon.zhu
 *
 */
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

    private Integer retrytimes;

    private String command;

    private Integer poolid;

    private String hostname;

    private String type;
    
    private String description;

    private String alertCondition;

    private String alertType;

    private String alertUser;

    private String alertGrup;

    public TaskDTO(){}

    public TaskDTO(String taskid, String name, String creator, String dependencyexpr, Date addtime, Date lastscheduletime,
                   Date updatetime, String crontab, String status, Integer allowmultiinstances, String proxyuser,
                   Integer waittimeout, Integer executiontimeout, Boolean isautoretry, Integer retrytimes, String command,
                   Integer poolid, String hostname, String type, String description, String alertCondition, String alertType,
                   String alertUser, String alertGrup) {
        super();
        this.taskid = taskid;
        this.name = name;
        this.creator = creator;
        this.dependencyexpr = dependencyexpr;
        this.addtime = addtime;
        this.lastscheduletime = lastscheduletime;
        this.updatetime = updatetime;
        this.crontab = crontab;
        this.status = status;
        this.allowmultiinstances = allowmultiinstances;
        this.proxyuser = proxyuser;
        this.waittimeout = waittimeout;
        this.executiontimeout = executiontimeout;
        this.isautoretry = isautoretry;
        this.retrytimes = retrytimes;
        this.command = command;
        this.poolid = poolid;
        this.hostname = hostname;
        this.type = type;
        this.description = description;
        this.alertCondition = alertCondition;
        this.alertType = alertType;
        this.alertUser = alertUser;
        this.alertGrup = alertGrup;
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

    public String getStatus() {
        return status;
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

    public String getAlertCondition() {
        return alertCondition;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getAlertUser() {
        return alertUser;
    }

    public String getAlertGrup() {
        return alertGrup;
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

    public void setAlertCondition(String alertCondition) {
        this.alertCondition = alertCondition;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public void setAlertUser(String alertUser) {
        this.alertUser = alertUser;
    }

    public void setAlertGrup(String alertGrup) {
        this.alertGrup = alertGrup;
    }

}
