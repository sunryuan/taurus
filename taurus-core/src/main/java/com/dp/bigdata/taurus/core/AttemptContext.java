package com.dp.bigdata.taurus.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public Date getAddtime() {
        return task.getAddtime();
    }

    public TaskAttempt getAttempt() {
        return attempt;
    }

    public String getAttemptid() {
        return attempt.getAttemptid();
    }

    public String getCommand() {
        return task.getCommand();
    }

    public ExecuteContext getContext() {
        Map<String,String> extendedConfs = new HashMap<String,String>();
        if(StringUtils.isNotBlank(task.getType()) && task.getType().equalsIgnoreCase("hadoop")){
            extendedConfs.put("hadoopName", getHadoopName());
        }
        if (StringUtils.isNotBlank(task.getType()) && task.getType().equalsIgnoreCase("spring")) {
        	String taskUrl = getFilename();
            return new ExecuteContext(JarID.getID(taskUrl), getAttemptid(), getExechost(), getProxyuser(), getType(), getCommand(),taskUrl,extendedConfs);
        } else {
            return new ExecuteContext(getTaskid(), getAttemptid(), getExechost(), getProxyuser(), getType(), getCommand(),null,extendedConfs);
        }
    }

    public String getCreator() {
        return task.getCreator();
    }

    public String getCrontab() {
        return task.getCrontab();
    }

    public String getDependencyexpr() {
        return task.getDependencyexpr();
    }

    public Date getEndtime() {
        return attempt.getEndtime();
    }

    public String getExechost() {
        return attempt.getExechost();
    }

    public Integer getExecutiontimeout() {
        return task.getExecutiontimeout();
    }

    public String getFilename() {
		return task.getFilename();
	}

    private String getHadoopName() {
        return task.getHadoopname();
    }

    public String getInstanceid() {
        return attempt.getInstanceid();
    }

    public Boolean getIsautokill() {
	   return task.getIsautokill();
   }

    public Boolean getIsautoretry() {
        return task.getIsautoretry();
    }

    public Date getLastscheduletime() {
        return task.getLastscheduletime();
    }

    public String getName() {
        return task.getName();
    }

    public Integer getPoolid() {
        return task.getPoolid();
    }

    public String getProxyuser() {
        return task.getProxyuser();
    }

    public Integer getRetrytimes() {
        return task.getRetrytimes();
    }

    public Integer getReturnvalue() {
        return attempt.getReturnvalue();
    }


    public Date getScheduletime() {
        return attempt.getScheduletime();
    }

    public Date getStarttime() {
        return attempt.getStarttime();
    }

    public Integer getStatus() {
        return attempt.getStatus();
    }

    public Task getTask() {
        return task;
    }

    public String getTaskid() {
        return task.getTaskid();
    }

    
    public String getType() {
        return task.getType();
    }

	public Date getUpdatetime() {
        return task.getUpdatetime();
    }

	public Integer getWaittimeout() {
        return task.getWaittimeout();
    }

    public void setAttempt(TaskAttempt attempt) {
        this.attempt = attempt;
    }

    public void setIsautokill(Boolean isautokill) {
	   task.setIsautokill(isautokill);
   }


	public void setStatus(int status) {
        attempt.setStatus(status);
    }

	public void setTask(Task task) {
        this.task = task;
    }

}
