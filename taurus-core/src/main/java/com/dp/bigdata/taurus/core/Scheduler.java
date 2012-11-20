package com.dp.bigdata.taurus.core;

import java.util.List;
import java.util.Map;

import com.dp.bigdata.taurus.generated.module.Task;

/**
 * 
 * Scheduler is the core of the engine.
 * 
 * @author damon.zhu
 * 
 */
public interface Scheduler {

	/**
	 * Schedule interval is 10 seconds.
	 */
	long SCHDUELE_INTERVAL = 1000 * 10;

	/**
	 * Submit a new task into Engine, waiting to be scheduled.
	 * 
	 * @param task
	 * @throws ScheduleException
	 */
	public void registerTask(Task task) throws ScheduleException;

	/**
	 * Remove a existing Task in the Engine.
	 * 
	 * @param taskID
	 * @throws ScheduleException
	 */
	public void unRegisterTask(String taskID) throws ScheduleException;

	/**
	 * Update a existing Task in the Engine.
	 * 
	 * @param task
	 * @throws ScheduleException
	 */
	public void updateTask(Task task) throws ScheduleException;

	/**
	 * Execute the Task in the timeout period.
	 * 
	 * @param job
	 * @param timeout
	 * @throws ScheduleException
	 */
	public void executeTask(String taskID, long timeout) throws ScheduleException;
	
	/**
	 * Suspend the task, the suspended task will never be scheduled.
	 * @param taskID
	 * @throws ScheduleException
	 */
	public void suspendTask(String taskID) throws ScheduleException;

	/**
	 * Notify the scheduler to kill a attempt.
	 * 
	 * @param job
	 * @throws ScheduleException
	 */
	public void killAttempt(String attemptID) throws ScheduleException;
	
	/**
	 * Notify the scheduler that a attempt has been finished.
	 * @param attemptID
	 */
	public void attemptSucceed(String attemptID);
	
	/**
	 * Notify the scheduler that a attempt has been expired.
	 * @param attemptID
	 */
	public void attemptExpired(String attemptID);
	
	/**
	 * Notify the scheduler that a attempt has been failed.
	 * @param attemptID
	 */
	public void attemptFailed(String attemptID);
	
	/**
	 * get all running attempts
	 * @return List<AttemptContext>
	 */
	public List<AttemptContext> getAllRunningAttempt();
	
	/**
	 * get running attempts to the given taskID
	 * @param taskID
	 * @return List<AttemptContext>
	 */
	public List<AttemptContext> getRunningAttemptsByTaskID(String taskID);
	
	/**
	 * get attempt status to the given attemptID
	 * @param attemptID
	 * @return AttemptStatus
	 */
	public AttemptStatus getAttemptStatus(String attemptID);
	
	/**
	 * get all registed tasks
	 * @return Map<String, Task>
	 */
	public Map<String, Task> getAllRegistedTask();
	
	/**
	 * get task for the given name
	 * @param name
	 * @return Task if task name is found
	 * @throws ScheduleException if task name cannot be found
	 */
	public Task getTaskByName(String name) throws ScheduleException;
	
	/**
	 * 
	 * @return int for maximum concurrency value
	 */
	public int getMaxConcurrency();
	
	/**
	 * 
	 * @param maxConcurrency
	 */
	public void setMaxConcurrency(int maxConcurrency);
	
	/**
	 * Is the given attempt running
	 * @param attemptID
	 * @return true if the given attempt is running,
	 *         false otherwise.
	 */
	 public boolean isRuningAttempt(String attemptID); 

}
