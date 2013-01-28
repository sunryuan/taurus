package com.dp.bigdata.taurus.zookeeper.execute.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.DefaultZKWatcher;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.guice.ScheduleInfoChanelModule;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * DefaultExecutorManager
 * @author damon.zhu
 *
 */
public class DefaultExecutorManager implements ExecutorManager{
	
	private static final Log LOGGER = LogFactory.getLog(DefaultExecutorManager.class);
	private static final int DEFAULT_TIME_OUT_IN_SECONDS = 10;
	private static final int RETRY_SLEEP_TIME = 20*1000;           //20s
	private static Map<String, Lock> attemptIDToLockMap = new HashMap<String, Lock>();

	private ScheduleInfoChannel dic;
	private int opTimeout = DEFAULT_TIME_OUT_IN_SECONDS;
	
	public DefaultExecutorManager(){
		Injector injector = Guice.createInjector(new ScheduleInfoChanelModule());
		dic = injector.getInstance(ScheduleInfoChannel.class);
		Watcher zkWatcher = new DefaultZKWatcher(dic);
		dic.registerWatcher(zkWatcher);
	}
	
	private static Lock getLock(String attemptID){
		synchronized(attemptIDToLockMap){
			Lock lock = attemptIDToLockMap.get(attemptID);
			if(lock == null){
				lock = new ReentrantLock();
				attemptIDToLockMap.put(attemptID, lock);
			}
			return lock;
		}
	}
	
	private void executeInternal(ExecuteContext context)  throws ExecuteException {
	    String agentIP = context.getAgentIP();
        String taskID = context.getTaskID();
        String attemptID = context.getAttemptID();
        String cmd = context.getCommand();
        String taskType = context.getType();
        String proxyUser = context.getProxyUser();
        
        if(!dic.exists(MachineType.AGENT,agentIP)){
            ScheduleStatus status = new ScheduleStatus();
            status.setStatus(ScheduleStatus.AGENT_UNAVAILABLE);
            LOGGER.error("Agent unavailable");
            throw new ExecuteException("Agent unavailable");
        }else{
            ScheduleStatus status = (ScheduleStatus) dic.getStatus(agentIP, attemptID, null);
            if(status == null){
                ScheduleConf conf = new ScheduleConf();
                conf.setTaskID(taskID);
                conf.setAttemptID(attemptID);
                conf.setCommand(cmd);
                conf.setTaskType(taskType);
                conf.setUserName(proxyUser);
                status = new ScheduleStatus();
                status.setStatus(ScheduleStatus.SCHEDULE_SUCCESS);
                Lock lock = getLock(attemptID);
                try{
                    lock.lock();
                    dic.execute(agentIP, attemptID, conf, status);
                } catch (RuntimeException e) {
                    LOGGER.error("Attempt "+attemptID + " schedule failed",e);
                    status.setStatus(ScheduleStatus.SCHEDULE_FAILED);
                    throw new ExecuteException(e);
                }   
                finally{
                    lock.unlock();
                }
            }
            else{
                LOGGER.error("Attempt "+attemptID + " has already scheduled");
                throw new ExecuteException("Attempt "+attemptID + " has already scheduled");
            }
        }
	}
	
    public void execute(ExecuteContext context) throws ExecuteException {
    	try{
    	    this.executeInternal(context);
    	} catch(ExecuteException e){
    	    //if get Agent unavailable exception, wait 20s and retry;
    	    if(e.getMessage().equals("Agent unavailable")) {
    	        try {
                    Thread.sleep(RETRY_SLEEP_TIME);
                } catch (InterruptedException e1) {
                    LOGGER.error(e1,e1);
                    throw e;
                }
                this.executeInternal(context);
    	    } else {
    	        throw e;
    	    }
    	}
    	
    }

    public void kill(ExecuteContext context) throws ExecuteException {
    	String agentIP = context.getAgentIP();
    	String attemptID = context.getAttemptID();

    	
    	ScheduleStatus status = new ScheduleStatus();
		if(!dic.exists(MachineType.AGENT, agentIP)){
	        LOGGER.error("Agent unavailable");
			throw new ExecuteException("Agent unavailable");
		}else{
			status = (ScheduleStatus) dic.getStatus(agentIP, attemptID, null);
			if(status == null||status.getStatus() == ScheduleStatus.DELETE_SUCCESS
					||status.getStatus() == ScheduleStatus.EXECUTE_SUCCESS||status.getStatus() == ScheduleStatus.EXECUTE_FAILED){
				LOGGER.error("Job Instance:" + attemptID + " cannot be killed!");
				throw new ExecuteException("Job Instance:" + attemptID + " cannot be killed!");
			}else{
				status.setStatus(ScheduleStatus.DELETE_SUBMITTED);
				Lock lock = getLock(attemptID);
				try{
					lock.lock();
					Condition killFinish = lock.newCondition();
					ScheduleStatusWatcher w = new ScheduleStatusWatcher(lock, killFinish, dic, agentIP, attemptID);
					dic.killTask(agentIP, attemptID, status, w);
					if(!killFinish.await(opTimeout, TimeUnit.SECONDS)){
						status = (ScheduleStatus) dic.getStatus(agentIP, attemptID, null);
						if(status != null && status.getStatus() == ScheduleStatus.DELETE_SUBMITTED){
							status.setStatus(ScheduleStatus.DELETE_TIMEOUT);
			                LOGGER.error("Delete " + attemptID + " timeout");
							throw new ExecuteException("Delete " + attemptID + " timeout");
						}
					}else{
						status = w.getScheduleStatus();
					}
					
					dic.completeKill(agentIP, attemptID);
				} catch(InterruptedException e){
	                LOGGER.error("Delete " + attemptID + " failed" ,e);
					throw new ExecuteException("Delete " + attemptID + " failed");
				}
				finally{
					lock.unlock();
				}
				if(status.getStatus()!=ScheduleStatus.DELETE_SUCCESS) {
                    LOGGER.error("Delete " + attemptID + " failed");
					throw new ExecuteException("Delete " + attemptID + " failed");
				}
			}
		}
    }

    public ExecuteStatus getStatus(ExecuteContext context) throws ExecuteException {
    	String agentIP = context.getAgentIP();
    	String attemptID = context.getAttemptID();

    	if(!dic.exists(MachineType.AGENT, agentIP)){
            LOGGER.error("Agent unavailable");
			throw new ExecuteException("Agent unavailable");
		} else{
			ScheduleStatus status = (ScheduleStatus) dic.getStatus(agentIP, attemptID, null);
			if(status == null) {
		        LOGGER.error("Fail to get status");
				throw new ExecuteException("Fail to get status");
			}
			ExecuteStatus result = null;
			int statusCode = status.getStatus();
			if(statusCode == ScheduleStatus.EXECUTE_FAILED) {
				result = new ExecuteStatus(ExecuteStatus.FAILED);
			} else if(statusCode == ScheduleStatus.EXECUTE_SUCCESS) {
				result = new ExecuteStatus(ExecuteStatus.SUCCEEDED);
			} else if(statusCode == ScheduleStatus.DELETE_SUCCESS) {
				result = new ExecuteStatus(ExecuteStatus.KILLED);
			} else {
				result = new ExecuteStatus(ExecuteStatus.RUNNING);
			}
			result.setReturnCode(status.getReturnCode());
			return result;
		}
    }

    public List<String> registerNewHost() {
        // TODO Auto-generated method stub
        return null;
    }
    private static final class ScheduleStatusWatcher implements Watcher{

		private Condition scheduleFinish;
		private Lock lock;
		private ScheduleStatus status;
		private ScheduleInfoChannel dic;
		private String agentIp;
		private String attemptID;

		ScheduleStatusWatcher(Lock lock, Condition scheduleFinish, ScheduleInfoChannel cs, String agentIp,
				String attemptID){
			this.lock = lock;
			this.scheduleFinish = scheduleFinish;
			this.dic = cs;
			this.agentIp = agentIp;
			this.attemptID = attemptID;
		}

		@Override
		public void process(WatchedEvent event) {
			try{
				lock.lock();
				status = (ScheduleStatus) dic.getStatus(agentIp, attemptID, null);
				scheduleFinish.signal();
			} finally{
				lock.unlock();
			}
		}

		public ScheduleStatus getScheduleStatus(){
			return status;
		}
	}

}

