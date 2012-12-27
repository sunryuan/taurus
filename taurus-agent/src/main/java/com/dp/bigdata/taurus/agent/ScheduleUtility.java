package com.dp.bigdata.taurus.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.dp.bigdata.taurus.agent.exec.Executor;
import com.dp.bigdata.taurus.agent.utils.AgentServerHelper;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;

public class ScheduleUtility {
	private static final Log s_logger = LogFactory.getLog(ScheduleUtility.class);
	private static Map<String, Lock> jobInstanceToLockMap = new HashMap<String, Lock>();
	
	private static String agentRoot = "/data/app/taurus";
	private static String jobPath = "jobs";
	private static String logPath = "logs";
	private static String hadoopAuthority = "/script/hadoop-authority.sh";
    private static String logFileUpload = "/script/log-upload.sh";


	private static String wormholeCmd;
	private static ExecutorService killThreadPool;
	private static ExecutorService executeThreadPool;
	
	
	private static final String FILE_SEPRATOR = File.separator;
	private static final String HADOOP_JOB = "hadoop";
	private static final String WORMHOLE_JOB = "wormhole";
	private static final String HIVE_JOB = "hive";
	private static final String SHELL_JOB = "shell script";
	private static final String COMMAND_PATTERN = "sudo -u %s -i \"cd %s && %s\"";
	
	static{
		killThreadPool = AgentServerHelper.createThreadPool(2, 4);
		executeThreadPool = AgentServerHelper.createThreadPool(4, 10);
	    wormholeCmd = AgentEnvValue.getValue(AgentEnvValue.WORMHOLE_COMMAND);
		agentRoot = AgentEnvValue.getValue(AgentEnvValue.AGENT_ROOT_PATH,agentRoot);
		jobPath = agentRoot + AgentEnvValue.getValue(AgentEnvValue.JOB_PATH,jobPath);
        logPath = agentRoot + AgentEnvValue.getValue(AgentEnvValue.LOG_PATH,logPath);
		hadoopAuthority = agentRoot + AgentEnvValue.getValue(AgentEnvValue.HADOOP_AUTHORITY, hadoopAuthority);
		logFileUpload =   agentRoot + AgentEnvValue.getValue(AgentEnvValue.LOG_FILE_UPLOAD, logFileUpload);
	}

	private static Lock getLock(String jobInstanceId){
		synchronized(jobInstanceToLockMap){
			Lock lock = jobInstanceToLockMap.get(jobInstanceId);
			if(lock == null){
				lock = new ReentrantLock();
				jobInstanceToLockMap.put(jobInstanceId, lock);
			}
			return lock;
		}
	}
	
	public static void checkAndKillTasks(Executor executor, String localIp, ScheduleInfoChannel cs, boolean addWatcher) {
		s_logger.debug("Start checkAndKillTasks");
		Watcher watcher = null;
		if(addWatcher) {
			watcher = new TaskKillWatcher(executor, localIp, cs);
		} 
		Set<String> currentNew = cs.getNewKillingJobInstanceIds(localIp, watcher);
		for(String attemptID: currentNew){
			Runnable killThread = new KillTaskThread(executor, localIp, cs, attemptID);
			killThreadPool.submit(killThread);
		}
		s_logger.debug("End checkAndKillTasks");
	}

	public static void checkAndRunTasks(Executor executor, String localIp, ScheduleInfoChannel cs, boolean addWatcher) {
		s_logger.debug("Start checkAndRunTasks");
		Watcher watcher = null;
		if(addWatcher) {
			watcher = new TaskExcuteWatcher(executor, localIp, cs);
		} 
		Set<String> currentNew = cs.getNewExecutionJobInstanceIds(localIp, watcher);
		for(String attemptID: currentNew){
			System.out.println(attemptID);
			submitTask(executor, localIp, cs, attemptID);
		}
		s_logger.debug("End checkAndRunTasks");
	}
	
	private static void submitTask(Executor executor, String localIp, ScheduleInfoChannel cs, String attemptID){
		Lock lock = getLock(attemptID);
		try{
			lock.lock();
			cs.completeExecution(localIp, attemptID);
			s_logger.debug(attemptID + " start schedule");
			ScheduleStatus status = (ScheduleStatus) cs.getStatus(localIp, attemptID, null);
			if(status == null){
				s_logger.error("status is null");
				return;
			} else if (status.getStatus() != ScheduleStatus.SCHEDULE_SUCCESS) {
				status.setStatus(ScheduleStatus.SCHEDULE_FAILED);
				cs.updateStatus(localIp, attemptID,status);
				return;
			}	
		} catch(RuntimeException e) {
			s_logger.error(e,e);
			ScheduleStatus status = new ScheduleStatus();
			status.setStatus(ScheduleStatus.SCHEDULE_FAILED);
			cs.updateStatus(localIp, attemptID,status);
		} finally{
			lock.unlock();
		}
		Runnable executeThread = new ExecuteThread(executor, localIp, cs, attemptID);
		executeThreadPool.submit(executeThread);
		s_logger.debug(attemptID + " end schedule");
	}

	private static final class ExecuteThread implements Runnable{
		Executor executor;
		String localIp;
		ScheduleInfoChannel cs;
		String taskAttempt;
		ExecuteThread(Executor executor, String localIp, ScheduleInfoChannel cs, String taskAttempt){
			this.executor = executor;
			this.localIp = localIp;
			this.cs = cs;
			this.taskAttempt = taskAttempt;
		}
		
		@Override
		public void run() {
			Lock lock = getLock(taskAttempt);
			ScheduleConf conf = null;
			ScheduleStatus status = null;
			try{
				lock.lock();
				conf = (ScheduleConf) cs.getConf(localIp, taskAttempt);
				status = (ScheduleStatus) cs.getStatus(localIp, taskAttempt, null);
				status.setStatus(ScheduleStatus.EXECUTING);
				cs.updateStatus(localIp, taskAttempt, status);
			} finally{
				lock.unlock();
			}
			try {
				executeJob(conf, status);
			} catch(RuntimeException e){
				s_logger.error(e,e);
				status.setStatus(ScheduleStatus.EXECUTE_FAILED);
			}
			try {
				lock.lock();
				cs.updateConf(localIp, taskAttempt, conf);
				ScheduleStatus thisStatus = (ScheduleStatus) cs.getStatus(localIp, taskAttempt, null);
				if(thisStatus.getStatus()!=ScheduleStatus.DELETE_FAILED && thisStatus.getStatus()!=ScheduleStatus.DELETE_SUCCESS
						&& thisStatus.getStatus()!=ScheduleStatus.DELETE_SUBMITTED) {
					cs.updateStatus(localIp, taskAttempt, status);
				}
				s_logger.debug(taskAttempt + " end execute");
			} finally{
				lock.unlock();
			}
		}
		
		private void executeJob(ScheduleConf conf, ScheduleStatus status) {
			if(conf == null){
				s_logger.error("SchduleConf is empty!");
				status.setStatus(ScheduleStatus.EXECUTE_FAILED);
				status.setFailureInfo("SchduleConf is empty!");
				cs.updateStatus(localIp, taskAttempt, status);
				return;
			}
			String attemptID = conf.getAttemptID();
			String taskType = conf.getTaskType();
			String taskID = conf.getTaskID();
			String command = conf.getCommand();
			String userName = conf.getUserName();
			if(userName.isEmpty()) {
                userName = "nobody";
            }
			
			if(attemptID == null || taskID == null || command == null || taskType == null){
				s_logger.error("Configure is not completed!");
				status.setStatus(ScheduleStatus.EXECUTE_FAILED);
				status.setFailureInfo("Configure is not completed!");
				cs.updateStatus(localIp, taskAttempt, status);
				return;
			}
			
            //create log file stream
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String date = format.format(new Date());
			String logFilePath = logPath + FILE_SEPRATOR + date + FILE_SEPRATOR + attemptID + ".log";
            String errorFilePath = logPath + FILE_SEPRATOR + date + FILE_SEPRATOR + attemptID + ".error";
            String htmlFileName = attemptID + ".html";
            String htmlFilePath = logPath + FILE_SEPRATOR + date + FILE_SEPRATOR + htmlFileName;
            FileOutputStream logFileStream = null;
            FileOutputStream errorFileStream = null;
            try{
                File logFile = new File(logFilePath);
                File errorFile = new File(errorFilePath);
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
                errorFile.createNewFile();
                logFileStream = new FileOutputStream(logFilePath);
                errorFileStream = new FileOutputStream(errorFilePath);
            } catch (IOException e) {
                s_logger.error(e,e);
            }
            	
			if(taskType.equals(HADOOP_JOB)||taskType.equals(HIVE_JOB)||taskType.equals(WORMHOLE_JOB)) {
				try {
					int returnCode = executor.execute(null, logFileStream, errorFileStream, hadoopAuthority,userName);
					if(returnCode != 0) {
						s_logger.error("Hadoop authority script executing failed");
					}
				} catch (IOException e) {
					s_logger.error(e.getMessage(),e);
				}		
			}
			if(taskType.equals(WORMHOLE_JOB)) {
				command = wormholeCmd + " " + command;
			}
			String path = jobPath + FILE_SEPRATOR + taskID + FILE_SEPRATOR;
			
			int returnCode = 0;
			try {
				if(!command.isEmpty()) {
					
					//execute
					CommandLine cmdLine;
					
				    String escapedCmd = command.replaceAll("\\\\", "\\\\\\\\");
				    escapedCmd = escapedCmd.replaceAll("\"", "\\\\\\\"");
				      
				    cmdLine = new CommandLine("bash");
				    cmdLine.addArgument("-c");
                    cmdLine.addArgument(String.format(COMMAND_PATTERN, userName, path, escapedCmd), false);

				    cmdLine.addArgument("sudo -u " + userName + " -s \"cd "+ path  +" && " + escapedCmd + "\"", false);
					s_logger.debug(taskAttempt + " start execute");
					returnCode = executor.execute(attemptID, 0, null, cmdLine, logFileStream, errorFileStream);
					executor.execute(null, logFileStream, errorFileStream, logFileUpload,logFilePath,errorFilePath,htmlFilePath,htmlFileName);
				}
				if(returnCode == 0) {
					status.setStatus(ScheduleStatus.EXECUTE_SUCCESS);
				} else  {
					status.setStatus(ScheduleStatus.EXECUTE_FAILED);
				} 
				status.setReturnCode(returnCode);
			} catch (IOException e) {
				s_logger.error(e,e);
				status.setStatus(ScheduleStatus.EXECUTE_FAILED);
				status.setFailureInfo("Job failed to execute");
			}
		}
	}
	
	
	
	private static final class KillTaskThread implements Runnable{
		
		Executor executor;
		String localIp;
		ScheduleInfoChannel cs;
		String jobInstanceId;
		
		KillTaskThread(Executor executor, String localIp, ScheduleInfoChannel cs, String jobInstanceId){
			this.executor = executor;
			this.localIp = localIp;
			this.cs = cs;
			this.jobInstanceId = jobInstanceId;
		}

		@Override
		public void run() {
			Lock lock = getLock(jobInstanceId);
			try{
				lock.lock();
				ScheduleConf conf = (ScheduleConf) cs.getConf(localIp, jobInstanceId);
				ScheduleStatus status = (ScheduleStatus) cs.getStatus(localIp, jobInstanceId, null);
				killTask(localIp,conf, status);
				cs.updateStatus(localIp, jobInstanceId, status);
			} finally{
				lock.unlock();
			}
		}
		
		private void killTask(String ip,ScheduleConf conf, ScheduleStatus status){
			String attemptID = conf.getAttemptID();
			int returnCode = executor.kill(attemptID);
			if(returnCode == 0)  {
				status.setStatus(ScheduleStatus.DELETE_SUCCESS);
			} else {
				status.setStatus(ScheduleStatus.DELETE_FAILED);
				status.setFailureInfo("Job failed to delete");
			}
			cs.completeKill(ip, attemptID);
		}
		
	}
	
	private static abstract class BaseTaskWatcher implements Watcher{

		protected String localIp;
		protected ScheduleInfoChannel cs;
		protected Executor executor;

		private BaseTaskWatcher(Executor executor, String localIp, ScheduleInfoChannel cs){
			this.localIp = localIp;
			this.cs = cs;
			this.executor = executor;
		}
	}

	private static final class TaskExcuteWatcher extends BaseTaskWatcher{

		private TaskExcuteWatcher(Executor executor, String localIp, ScheduleInfoChannel cs){
			super(executor, localIp, cs);
		}

		@Override
		public void process(WatchedEvent event) {
				checkAndRunTasks(executor, localIp, cs, true);
		}
	}

	private static final class TaskKillWatcher extends BaseTaskWatcher{
		private TaskKillWatcher(Executor executor, String localIp, ScheduleInfoChannel cs){
			super(executor, localIp, cs);
		}

		@Override
		public void process(WatchedEvent event) {
			checkAndKillTasks(executor, localIp, cs, true);
		}
	}
}
