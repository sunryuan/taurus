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

    private Integer status;

    private Integer allowmultiinstances;

    private String proxyuser;

    private Integer waittimeout;

    private Integer executiontimeout;

    private Boolean isautoretry;

    private Integer retrytimes;

    private Integer retryexpiretimeout;

    private String command;

    private Integer poolid;

    private String type;

    public TaskDTO(){}
    
    public TaskDTO(String taskid, String name, String creator, String dependencyexpr, Date addtime, Date lastscheduletime,
                   Date updatetime, String crontab, Integer status, Integer allowmultiinstances, String proxyuser,
                   Integer waittimeout, Integer executiontimeout, Boolean isautoretry, Integer retrytimes,
                   Integer retryexpiretimeout, String command, Integer poolid, String type) {
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
        this.retryexpiretimeout = retryexpiretimeout;
        this.command = command;
        this.poolid = poolid;
        this.type = type;
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

    public Integer getStatus() {
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

    public Integer getRetryexpiretimeout() {
        return retryexpiretimeout;
    }

    public String getCommand() {
        return command;
    }

    public Integer getPoolid() {
        return poolid;
    }

    public String getType() {
        return type;
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

    public void setStatus(Integer status) {
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

    public void setRetryexpiretimeout(Integer retryexpiretimeout) {
        this.retryexpiretimeout = retryexpiretimeout;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setPoolid(Integer poolid) {
        this.poolid = poolid;
    }

    public void setType(String type) {
        this.type = type;
    }

	@Override
	public String toString() {
		return "TaskDTO [taskid=" + taskid + ", name=" + name + ", creator="
				+ creator + ", dependencyexpr=" + dependencyexpr + ", addtime="
				+ addtime + ", lastscheduletime=" + lastscheduletime
				+ ", updatetime=" + updatetime + ", crontab=" + crontab
				+ ", status=" + status + ", allowmultiinstances="
				+ allowmultiinstances + ", proxyuser=" + proxyuser
				+ ", waittimeout=" + waittimeout + ", executiontimeout="
				+ executiontimeout + ", isautoretry=" + isautoretry
				+ ", retrytimes=" + retrytimes + ", retryexpiretimeout="
				+ retryexpiretimeout + ", command=" + command + ", poolid="
				+ poolid + ", type=" + type + "]";
	}
	
}
