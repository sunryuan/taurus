package com.dp.bigdata.taurus.core;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.TaskAttempt;
import com.dp.bigdata.taurus.zookeeper.execute.helper.ExecuteContext;

/**
 * <code>AttemptContext</code> is the scheduling context for each attempt.
 * 
 * @author damon.zhu
 */
public class AttemptContext {

    private TaskAttempt attempt;
    private Task task;

    public AttemptContext(TaskAttempt attempt, Task task) {
        super();
        this.attempt = attempt;
        this.task = task;
    }

    public TaskAttempt getAttempt() {
        return attempt;
    }

    public Task getTask() {
        return task;
    }

    public ExecuteContext getContext() {
        if (StringUtils.isNotBlank(task.getType()) && task.getType().equalsIgnoreCase("spring")) {
        	String taskUrl = getFilename();
            return new ExecuteContext(JarID.getID(taskUrl), getAttemptid(), getExechost(), getProxyuser(), getType(), getCommand(),taskUrl);
        } else {
            return new ExecuteContext(getTaskid(), getAttemptid(), getExechost(), getProxyuser(), getType(), getCommand(),null);
        }
    }

    public void setAttempt(TaskAttempt attempt) {
        this.attempt = attempt;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getAttemptid() {
        return attempt.getAttemptid();
    }

    public String getInstanceid() {
        return attempt.getInstanceid();
    }

    public Date getStarttime() {
        return attempt.getStarttime();
    }

    public Date getEndtime() {
        return attempt.getEndtime();
    }

    public Date getScheduletime() {
        return attempt.getScheduletime();
    }

    public Integer getStatus() {
        return attempt.getStatus();
    }

    public Integer getReturnvalue() {
        return attempt.getReturnvalue();
    }

    public String getExechost() {
        return attempt.getExechost();
    }

    public String getTaskid() {
        return task.getTaskid();
    }

    public String getName() {
        return task.getName();
    }

    public String getCreator() {
        return task.getCreator();
    }

    public String getDependencyexpr() {
        return task.getDependencyexpr();
    }

    public Date getAddtime() {
        return task.getAddtime();
    }

    public Date getLastscheduletime() {
        return task.getLastscheduletime();
    }

    public Date getUpdatetime() {
        return task.getUpdatetime();
    }

    public String getCrontab() {
        return task.getCrontab();
    }

    public int getAllowmultiinstances() {
        return task.getAllowmultiinstances();
    }

    public String getProxyuser() {
        return task.getProxyuser();
    }

    public Integer getWaittimeout() {
        return task.getWaittimeout();
    }

    public Integer getExecutiontimeout() {
        return task.getExecutiontimeout();
    }

    public Boolean getIsautoretry() {
        return task.getIsautoretry();
    }

    public Integer getRetrytimes() {
        return task.getRetrytimes();
    }

    public String getCommand() {
        return task.getCommand();
    }

    public Integer getPoolid() {
        return task.getPoolid();
    }

    public String getType() {
        return task.getType();
    }

    public void setStatus(int status) {
        attempt.setStatus(status);
    }

	public String getFilename() {
		return task.getFilename();
	}

}
