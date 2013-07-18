package com.dp.bigdata.taurus.agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.dp.bigdata.taurus.agent.exec.Executor;
import com.dp.bigdata.taurus.agent.utils.AgentEnvValue;
import com.dp.bigdata.taurus.agent.utils.AgentServerHelper;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;

public class ScheduleUtility {
	private static final Log s_logger = LogFactory.getLog(ScheduleUtility.class);
	private static Map<String, Lock> jobInstanceToLockMap = new HashMap<String, Lock>();
	
	private static String agentRoot = "/data/app/taurus-agent";
	private static String jobPath = "/data/app/taurus-agent/jobs";
	private static String logPath = "/data/app/taurus-agent/logs";
	private static String hadoopAuthority = "/script/hadoop-authority.sh";
    private static String logFileUpload = "/script/log-upload.sh";
    private static String killJob = "/script/kill-tree.sh";
    private static String env = "/script/agent-env.sh";
    private static String running = "/running";
    private static String hadoop = "/hadoop";
    private static String homeDir = "/home";
    private static boolean needHadoopAuthority;
    private static boolean needSudoAuthority;

	private static ExecutorService killThreadPool;
	private static ExecutorService executeThreadPool;
	
	private static final int INTERVAL = 60 * 1000;
	private static final String FILE_SEPRATOR = File.separator;
	private static final String KINIT_COMMAND_PATTERN = "kinit -r 12l -k -t %s/%s/.keytab %s@DIANPING.COM;kinit -R;";
	private static final String KDESTROY_COMMAND = "kdestroy;";
	private static final String COMMAND_PATTERN_WITHOUT_SUDO 
	    = "echo %s; %s %s echo $$ >%s;  [ -f %s ] && cd %s; source %s %s; %s; echo $? >%s;echo %s; %s rm -f %s; %s";
	private static final String COMMAND_PATTERN_WITH_SUDO 
	    = "sudo -u %s %s bash -c \"%s echo $$ >%s; [ -f %s ] && cd %s; source %s %s; %s\"; echo $? >%s; sudo -u %s %s bash -c \"rm -f %s; %s\"";
	private static final String KILL_COMMAND = "%s %s %s";
	private static final String REMOVE_COMMAND = "rm -f %s";
	private static final String UPDATE_COMMAND = "source /etc/profile; %s/script/update-agent.sh > %s/agent-logs/update.log";
	
	private static String krb5Path = "KRB5CCNAME=%s/%s";
	//private static String kinitCommand = "";
	//private static String kdestroyCommand = "";
    private static String command_pattern;
    private static boolean on_windows = false;
    private static TaskHelper taskLogUploader = new TaskHelper();

	static{
	    on_windows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		killThreadPool = AgentServerHelper.createThreadPool(2, 4);
		executeThreadPool = AgentServerHelper.createThreadPool(10, 10);
		agentRoot = AgentEnvValue.getValue(AgentEnvValue.AGENT_ROOT_PATH,agentRoot);
		jobPath = AgentEnvValue.getValue(AgentEnvValue.JOB_PATH,jobPath);
        logPath = AgentEnvValue.getValue(AgentEnvValue.LOG_PATH,logPath);
		hadoopAuthority = agentRoot + hadoopAuthority;
		logFileUpload =   agentRoot + logFileUpload;
		killJob = agentRoot + killJob;
		running = jobPath + running;
		hadoop = jobPath + hadoop;
	    env = agentRoot + env;
	    homeDir = AgentEnvValue.getValue(AgentEnvValue.HOME_PATH,homeDir);
		needHadoopAuthority = new Boolean(AgentEnvValue.getValue(AgentEnvValue.NEED_HADOOP_AUTHORITY, "false"));
		needSudoAuthority = new Boolean(AgentEnvValue.getValue(AgentEnvValue.NEED_SUDO_AUTHORITY, "true"));
		if(needSudoAuthority) {
		    command_pattern = COMMAND_PATTERN_WITH_SUDO;
		} else {
		    command_pattern = COMMAND_PATTERN_WITHOUT_SUDO;
		    krb5Path = "export " + krb5Path + ";";
		}
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
	public static void checkAndKillTasks(Executor executor, String localIp, ScheduleInfoChannel cs, boolean addListener) {
	    synchronized (ScheduleUtility.class) {
    		s_logger.debug("Start checkAndKillTasks");
    		if(addListener) {
                cs.setKillingJobListener(new TaskKillListener(executor, localIp, cs));
            } 
    		Set<String> currentNew = cs.getNewKillingJobInstanceIds(localIp);
    		for(String attemptID: currentNew){
    		    ScheduleConf conf = (ScheduleConf) cs.getConf(localIp, attemptID);
                if(conf != null && StringUtils.isNotBlank(conf.getTaskType()) && conf.getTaskType().equalsIgnoreCase(TaskType.SPRING.name())){
                    return;
                }
    			Runnable killThread = new KillTaskThread(executor, localIp, cs, attemptID);
    			killThreadPool.submit(killThread);
    		}
    		s_logger.debug("End checkAndKillTasks");
	    }

	}

	public static void checkAndRunTasks(Executor executor, String localIp, ScheduleInfoChannel cs, boolean addListener) {
	    synchronized (ScheduleUtility.class) {
    		s_logger.debug("Start checkAndRunTasks");
    		if(addListener) {
    		    cs.setExecutionJobListener(new TaskExcuteListener(executor, localIp, cs));
    		} 
    		Set<String> currentNew = cs.getNewExecutionJobInstanceIds(localIp);
    		for(String attemptID: currentNew){
    			submitTask(executor, localIp, cs, attemptID);
    		}
    		s_logger.debug("End checkAndRunTasks");
	    }
	}
	
	public static void startZombieThread(String localIp, ScheduleInfoChannel cs) {
	    ZombieTaskThread thread = new ZombieTaskThread(localIp, cs);
	    new Thread(thread).start();
	}
	
	private static void submitTask(Executor executor, String localIp, ScheduleInfoChannel cs, String attemptID){
		Lock lock = getLock(attemptID);
		try{
			lock.lock();
			
			ScheduleConf conf = (ScheduleConf) cs.getConf(localIp, attemptID);
			if(conf != null && StringUtils.isNotBlank(conf.getTaskType()) && conf.getTaskType().equalsIgnoreCase(TaskType.SPRING.name())){
				return;
			}
			
			cs.completeExecution(localIp, attemptID);
			s_logger.debug(attemptID + " start schedule");
			ScheduleStatus status = (ScheduleStatus) cs.getStatus(localIp, attemptID);

			if(status == null){
				s_logger.error("status is null");
				return;
			} else if (status.getStatus() != ScheduleStatus.SCHEDULE_SUCCESS) {
				status.setStatus(ScheduleStatus.SCHEDULE_FAILED);
				cs.updateStatus(localIp, attemptID,status);
				return;
			}	
		} catch(Exception e) {
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
				status = (ScheduleStatus) cs.getStatus(localIp, taskAttempt);
				cs.addRunningJob(localIp, taskAttempt);
				status.setStatus(ScheduleStatus.EXECUTING);
				cs.updateStatus(localIp, taskAttempt, status);
			} catch(Exception e){
                s_logger.error(e,e);
            } finally{
				lock.unlock();
			}
			try {
				executeJob(conf, status);
			} catch(Exception e){
				s_logger.error(e,e);
				status.setStatus(ScheduleStatus.EXECUTE_FAILED);
                cs.removeRunningJob(localIp, taskAttempt);
			}
			try {
				lock.lock();
				cs.updateConf(localIp, taskAttempt, conf);
				ScheduleStatus thisStatus = (ScheduleStatus) cs.getStatus(localIp, taskAttempt);
				if(thisStatus.getStatus() != ScheduleStatus.DELETE_SUCCESS) {
					cs.updateStatus(localIp, taskAttempt, status);
	                cs.removeRunningJob(localIp, taskAttempt);
				}
				s_logger.debug(taskAttempt + " end execute");
			} catch(Exception e){
                s_logger.error(e,e);
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
			String taskID = conf.getTaskID();
			String command = conf.getCommand();
			String userName = conf.getUserName();
			if(userName.isEmpty()) {
                userName = "nobody";
            }
			
			if(attemptID == null || taskID == null || command == null
			        || attemptID.isEmpty() || taskID.isEmpty() || command.isEmpty()){
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
                s_logger.error(e.getMessage(),e);
            }
            
            String krb5PathCommand = "";
         	String kinitCommand = "";
         	String kdestroyCommand = "";
			if(needHadoopAuthority) {
			    krb5PathCommand = String.format(krb5Path, hadoop,"krb5cc_"+attemptID);
	            kinitCommand = String.format(KINIT_COMMAND_PATTERN,homeDir,userName,userName);
	            kdestroyCommand = KDESTROY_COMMAND;
	        }
			String path = jobPath + FILE_SEPRATOR + taskID + FILE_SEPRATOR;
            String pidFile = running + FILE_SEPRATOR + '.' + attemptID;
            String returnValueFile = running + FILE_SEPRATOR + "rv." + attemptID;
			int returnCode = 0;
			try {
			    s_logger.debug(taskAttempt + " start execute");
			    if(on_windows){
			        returnCode = executor.execute(attemptID, logFileStream, errorFileStream, command);
			    }
			    else {
					//execute
					CommandLine cmdLine;
					
				    String escapedCmd = command.replaceAll("\\\\", "\\\\\\\\");
				    escapedCmd = escapedCmd.replaceAll("\"", "\\\\\\\"");
				      
				    cmdLine = new CommandLine("bash");
				    cmdLine.addArgument("-c");
                    cmdLine.addArgument(String.format(command_pattern, userName, krb5PathCommand,
                            kinitCommand, pidFile, path, path, env, env, escapedCmd, returnValueFile,
                            userName, krb5PathCommand, pidFile, kdestroyCommand), false);
					executor.execute(attemptID, 0, null, cmdLine, logFileStream, errorFileStream);
//					executor.execute("upload log", logFileStream, errorFileStream, 
//					        logFileUpload,logFilePath,errorFilePath,htmlFilePath,htmlFileName);
					try{
					    BufferedReader br = new BufferedReader(new FileReader((new File(returnValueFile)))); 
					    String returnValStr = br.readLine();
					    br.close();
                        returnCode = Integer.parseInt(returnValStr);
                        new File(returnValueFile).delete();
                    } catch (NumberFormatException e){
                        s_logger.error(e,e);
                        returnCode = 1;
                    } catch (IOException e){
                        s_logger.error(e,e);
                        returnCode = 1;
                    }
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
				status.setFailureInfo("Job failed!");
			}
			
            try {
                taskLogUploader.uploadLog(returnCode,errorFilePath, logFilePath, htmlFilePath, htmlFileName);
            } catch (IOException e) {
                s_logger.error(e,e);
            }

            try {
                if(!on_windows){
                    executor.execute("removePidFile", logFileStream,errorFileStream,String.format(REMOVE_COMMAND, pidFile));
                }
                logFileStream.close();
                errorFileStream.close();
            } catch (IOException e) {
                s_logger.error(e,e);
            }
		}
	}
	
	private static final class ZombieTaskThread implements Runnable{
	    String localIp;
	    ScheduleInfoChannel cs;
	    Set<String> jobInstanceIds;
	    ZombieTaskThread( String localIp, ScheduleInfoChannel cs){
            this.localIp = localIp;
            this.cs = cs;
        }
        @Override
        public void run() {
            jobInstanceIds = cs.getRunningJobs(localIp);
            int num = 0;
            if(on_windows) {
                for(String attemptID : jobInstanceIds){
                    ScheduleStatus status = (ScheduleStatus) cs.getStatus(localIp,attemptID);
                    if(status.getStatus() == ScheduleStatus.EXECUTING){
                        status.setStatus(ScheduleStatus.UNKNOWN);
                        cs.updateStatus(localIp, attemptID, status);
                    }
                    cs.removeRunningJob(localIp,attemptID);
                }
                return;
            }
            while(jobInstanceIds.size() != 0 && num <300){      
                for(String attemptID:jobInstanceIds) {
                    String pidFile = running + FILE_SEPRATOR + '.' + attemptID;
                    String returnValueFile = running + FILE_SEPRATOR + "rv." + attemptID;
                    int returnCode =1 ;
                    try{
                        BufferedReader br = new BufferedReader(new FileReader((new File(returnValueFile)))); 
                        String returnValStr = br.readLine();
                        br.close();
                        returnCode = Integer.parseInt(returnValStr);
                    } catch (Exception e){
                        s_logger.error(e,e);
                        returnCode = 1;
                    }
                    if(!new File(pidFile).exists() && new File(returnValueFile).exists()){
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String date = format.format(new Date());
                        String logFilePath = logPath + FILE_SEPRATOR + date + FILE_SEPRATOR + attemptID + ".log";
                        String errorFilePath = logPath + FILE_SEPRATOR + date + FILE_SEPRATOR + attemptID + ".error";
                        String htmlFileName = attemptID + ".html";
                        String htmlFilePath = logPath + FILE_SEPRATOR + date + FILE_SEPRATOR + htmlFileName;
                        try {
                            new File(returnValueFile).delete();
                            taskLogUploader.uploadLog(returnCode, errorFilePath, logFilePath, htmlFilePath, htmlFileName);
                        } catch (IOException e) {
                            s_logger.error(e,e);
                        }
                        ScheduleStatus status = (ScheduleStatus) cs.getStatus(localIp,attemptID);
                        if(status.getStatus() == ScheduleStatus.EXECUTING){
                            if(returnCode == 0){
                                status.setStatus(ScheduleStatus.EXECUTE_SUCCESS);
                            } else{
                                status.setStatus(ScheduleStatus.EXECUTE_FAILED);
                            }
                            cs.updateStatus(localIp, attemptID, status);
                        }
                        cs.removeRunningJob(localIp,attemptID);
                    }    
                } 
                Set<String> runningInstanceIds = cs.getRunningJobs(localIp);
                Set<String> zombieInstancesIds = new HashSet<String>();
                for(String instanceId:runningInstanceIds){
                    if(jobInstanceIds.contains(instanceId)){
                        zombieInstancesIds.add(instanceId);
                    }
                }
                jobInstanceIds = zombieInstancesIds;
                try {
                    Thread.sleep(INTERVAL);
                    num ++;
                } catch (InterruptedException e) {
                    s_logger.error(e,e);
                }
            }
            if(jobInstanceIds.size() != 0){
                for(String attemptID : jobInstanceIds){
                    ScheduleStatus status = (ScheduleStatus) cs.getStatus(localIp,attemptID);
                    if(status.getStatus() == ScheduleStatus.EXECUTING){
                        status.setStatus(ScheduleStatus.UNKNOWN);
                        cs.updateStatus(localIp, attemptID, status);
                    }
                    cs.removeRunningJob(localIp,attemptID);
                }
                return;
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
				ScheduleStatus status = (ScheduleStatus) cs.getStatus(localIp, jobInstanceId);
				killTask(localIp,conf, status);
				cs.updateStatus(localIp, jobInstanceId, status);
                cs.removeRunningJob(localIp, jobInstanceId);
			} catch(Exception e){
                s_logger.error(e,e);
            } finally{
				lock.unlock();
			}
		}
		
		private void killTask(String ip,ScheduleConf conf, ScheduleStatus status){
			String attemptID = conf.getAttemptID();
			int returnCode = 1;
			try{
			    if(on_windows){
			        returnCode = executor.kill(attemptID);
			    } else{
			        String fileName = running + FILE_SEPRATOR + '.' + attemptID;
	                BufferedReader br = new BufferedReader(new FileReader((new File(fileName)))); 
	                String pid = br.readLine();
	                br.close();
	                s_logger.debug("Ready to kill " + attemptID + ", pid is " + pid);
	                String kill = String.format(KILL_COMMAND, killJob, pid, "9");
	                returnCode = executor.execute("kill",System.out,System.err,kill);
	                try {
	                    new File(fileName).delete();
	                } catch(Exception e) {
	                    //do nothing
	                }
			    }
            } catch(Exception e) {
                s_logger.error(e,e);
                returnCode = 1;
            }
			
			if(returnCode == 0)  {
				status.setStatus(ScheduleStatus.DELETE_SUCCESS);
			}
		}
		
		
	}
	
	private static abstract class BaseTaskListener implements IZkChildListener{

		protected String localIp;
		protected ScheduleInfoChannel cs;
		protected Executor executor;

		private BaseTaskListener(Executor executor, String localIp, ScheduleInfoChannel cs){
			this.localIp = localIp;
			this.cs = cs;
			this.executor = executor;
		}
	}

	private static final class TaskExcuteListener extends BaseTaskListener{

		private TaskExcuteListener(Executor executor, String localIp, ScheduleInfoChannel cs){
			super(executor, localIp, cs);
		}

        /* (non-Javadoc)
         * @see org.I0Itec.zkclient.IZkChildListener#handleChildChange(java.lang.String, java.util.List)
         */
        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            checkAndRunTasks(executor, localIp, cs, false);    
        }
	}

	private static final class TaskKillListener extends BaseTaskListener{
		private TaskKillListener(Executor executor, String localIp, ScheduleInfoChannel cs){
			super(executor, localIp, cs);
		}


        /* (non-Javadoc)
         * @see org.I0Itec.zkclient.IZkChildListener#handleChildChange(java.lang.String, java.util.List)
         */
        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            checkAndKillTasks(executor, localIp, cs, false);
        }
	}

    /**
     * @param executor
     * @param localIp
     * @param schedule
     */
    public static void checkAndUpdate(Executor executor, String localIp, ScheduleInfoChannel cs,boolean atStart) {
        synchronized (ScheduleUtility.class) {
            if(cs.needUpdate(localIp)){
                if(atStart) {
                    cs.completeUpdate(localIp);
                } else {
                    try {
                        CommandLine cmdLine;
                        cmdLine = new CommandLine("bash");
                        cmdLine.addArgument("-c");
                        cmdLine.addArgument(String.format(UPDATE_COMMAND, agentRoot,agentRoot), false);
                        executor.execute("updateAgent", 0, null, cmdLine, null, null);
                        
                    } catch (IOException e) {
                        s_logger.error(e,e);
                    }
                }
            }       
        }
    }
	
}
