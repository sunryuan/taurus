package com.dp.bigdata.taurus.zookeeper.execute.helper;

import java.util.List;

/**
 * 
 * ExecutorManager
 * @author damon.zhu
 *
 */
public interface ExecutorManager {

    /**
     * execute the attempt
     * @param context
     * @throws ScheduleException
     */
    public void execute(ExecuteContext context) throws ExecuteException;

    /**
     * kill the attempt
     * @param context
     * @throws ScheduleException
     */
    public void kill(ExecuteContext context) throws ExecuteException;

    /**
     * get attempt status for the given context
     * @param context
     * @return
     * @throws ScheduleException
     */
    public ExecuteStatus getStatus(ExecuteContext context) throws ExecuteException;
    
    /**
     * get all new agent hosts List<ip>
     * @return List<Host>
     */
    public List<String> registerNewHost();

}
