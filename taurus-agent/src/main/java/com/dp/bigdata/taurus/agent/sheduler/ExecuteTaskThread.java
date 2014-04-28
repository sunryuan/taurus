/**
 * Project: taurus-agent
 * 
 * File Created at 2013-5-21
 * $Id$
 * 
 * Copyright 2013 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dp.bigdata.taurus.agent.sheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.agent.common.BaseEnvManager;
import com.dp.bigdata.taurus.agent.exec.Executor;
import com.dp.bigdata.taurus.agent.utils.LockHelper;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;

/**
 * TODO Comment of ExecuteThread
 * @author renyuan.sun
 *
 */
public final class ExecuteTaskThread extends BaseEnvManager{
    Executor executor;
    String localIp;
    ScheduleInfoChannel cs;
    String taskAttempt;
    
    
    private static final Log LOGGER = LogFactory.getLog(ExecuteTaskThread.class);
    
    private static final String COMMAND_PATTERN_WITH_SUDO = "sudo -u %s %s bash -c \"%s\"";
    private static final String COMMAND_PATTERN_WITHOUT_SUDO = "echo %s; %s%s";
    private static final String MAIN_COMMAND_PATTERN = "echo $$ >%s; [ -f %s ] && cd %s; source %s %s; %s";
    private static final String STORE_RETURN_VALUE_PATTERN = "echo $? >%s;";
    private static final String CLEAN_FILES_PATTERN = "rm -f %s;%s";
    private static final String KINIT_COMMAND_PATTERN = "kinit -r 12l -k -t %s/%s/.keytab %s@DIANPING.COM;kinit -R;";
    private static final String KDESTROY_COMMAND = "kdestroy;";
    private static final String HADOOP_NAME = "hadoopName";
    
    
    private String command_pattern;
    private String krb5Path = "KRB5CCNAME=%s/%s";

    ExecuteTaskThread(Executor executor, String localIp, ScheduleInfoChannel cs, String taskAttempt){
        this.executor = executor;
        this.localIp = localIp;
        this.cs = cs;
        this.taskAttempt = taskAttempt;
        if(needSudoAuthority) {
            command_pattern = COMMAND_PATTERN_WITH_SUDO;
        } else {
            command_pattern = COMMAND_PATTERN_WITHOUT_SUDO;
            krb5Path = "export " + krb5Path + ";";
        }
    }
    
    @Override
    public void run() {
        Lock lock = LockHelper.getLock(taskAttempt);
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
            LOGGER.error(e,e);
        } finally{
            lock.unlock();
        }
        try {
            executeJob(conf, status);
        } catch(Exception e){
            LOGGER.error(e,e);
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
            LOGGER.debug(taskAttempt + " end execute");
        } catch(Exception e){
            LOGGER.error(e,e);
        } finally{
            lock.unlock();
        }
    }
    
    private void executeJob(ScheduleConf conf, ScheduleStatus status) {
        if(conf == null){
            LOGGER.error("SchduleConf is empty!");
            status.setStatus(ScheduleStatus.EXECUTE_FAILED);
            status.setFailureInfo("SchduleConf is empty!");
            cs.updateStatus(localIp, taskAttempt, status);
            return;
        }
        String attemptID = conf.getAttemptID();
        String taskID = conf.getTaskID();
        String command = conf.getCommand();
        String userName = conf.getUserName();
        String taskType = conf.getTaskType();
        Map<String,String> extendedConfs= conf.getExtendedMap();
        String hadoopName = "hadoop";
        if(extendedConfs != null && extendedConfs.containsKey(HADOOP_NAME)){
            hadoopName = conf.getExtendedMap().get(HADOOP_NAME);
        }
        if(userName.isEmpty()) {
            userName = "nobody";
        }
        
        if(attemptID == null || taskID == null || command == null
                || attemptID.isEmpty() || taskID.isEmpty() || command.isEmpty()){
            LOGGER.error("Configure is not completed!");
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
            LOGGER.error(e.getMessage(),e);
        }
        
        
        String path = jobPath + FILE_SEPRATOR + taskID + FILE_SEPRATOR;
        String pidFile = running + FILE_SEPRATOR + '.' + attemptID;
        String returnValueFile = running + FILE_SEPRATOR + "rv." + attemptID;
        int returnCode = 0;
        try {
            LOGGER.debug(taskAttempt + " start execute");
            
            if(ON_WINDOWS){
                String escapedCmd = command.replaceAll("\\\\", "\\\\\\\\");
                escapedCmd = escapedCmd.replaceAll("\"", "\\\\\\\"");
                returnCode = executor.execute(attemptID, logFileStream, errorFileStream, escapedCmd);
            }
            else {
                //申请hadoop权限
                String kdestroyCommand = "";
                String krb5PathCommand = "";
                CommandLine cmdLine;  
                if(taskType!=null && taskType.equals("hadoop")){
                    krb5PathCommand = String.format(krb5Path, hadoop,"krb5cc_"+attemptID);
                    String kinitCommand = String.format(KINIT_COMMAND_PATTERN,homeDir,hadoopName,hadoopName);
                    kinitCommand = String.format(command_pattern, userName, krb5PathCommand,
                            kinitCommand);
                    
                    kdestroyCommand = KDESTROY_COMMAND;
                    cmdLine = new CommandLine("bash");
                    cmdLine.addArgument("-c");
                    cmdLine.addArgument(kinitCommand,false);
                    int kinitRV = 0;
                    try{
                        kinitRV = executor.execute(attemptID, 0, null, cmdLine, logFileStream, errorFileStream);
                    } catch(Exception e) {
                        LOGGER.error(e,e);
                        setFailResult(status);
                        return;
                    }
                    returnCode = kinitRV;
                }
                if(returnCode == 0) {
                  //execute cmd
                    String escapedCmd = command.replaceAll("\\\\", "\\\\\\\\");
                    escapedCmd = escapedCmd.replaceAll("\"", "\\\\\\\"");
                    cmdLine = new CommandLine("bash");
                    cmdLine.addArgument("-c");
                    String mainCmd = String.format(MAIN_COMMAND_PATTERN, pidFile, path, path, env, env, escapedCmd);
                    String sotreRvCmd = String.format(STORE_RETURN_VALUE_PATTERN, returnValueFile);
                    mainCmd = String.format(command_pattern, userName, krb5PathCommand, mainCmd);
                    if(!kdestroyCommand.isEmpty()){
                        kdestroyCommand = String.format(command_pattern,userName, krb5PathCommand, kdestroyCommand);
                    }
                    String postCmd = String.format(CLEAN_FILES_PATTERN,pidFile,kdestroyCommand);
                   
                    cmdLine.addArgument(mainCmd + ";" + sotreRvCmd  + postCmd, false);
                    
                    executor.execute(attemptID, 0, null, cmdLine, logFileStream, errorFileStream);
                    
                    try{
                        BufferedReader br = new BufferedReader(new FileReader((new File(returnValueFile)))); 
                        String returnValStr = br.readLine();
                        br.close();
                        returnCode = Integer.parseInt(returnValStr);
                        new File(returnValueFile).delete();
                    } catch (NumberFormatException e){
                        LOGGER.error(e,e);
                        returnCode = 1;
                    } catch (IOException e){
                        LOGGER.error(e,e);
                        returnCode = 1;
                    }
                }
            }
            setResult( returnCode,  status);              
        } catch (Exception e) {
            LOGGER.error(e,e);
            setFailResult(status);
        }
        
        try {
            taskHelper.uploadLog(returnCode,errorFilePath, logFilePath, htmlFilePath, htmlFileName);
        } catch (IOException e) {
            LOGGER.error(e,e);
        }

    }
    
    private void setResult(int returnCode, ScheduleStatus status){
        if(returnCode == 0) {
            status.setStatus(ScheduleStatus.EXECUTE_SUCCESS);
        } else  {
            status.setStatus(ScheduleStatus.EXECUTE_FAILED);
        }
        status.setReturnCode(returnCode);
    }
    
    private void setFailResult(ScheduleStatus status){
        status.setStatus(ScheduleStatus.EXECUTE_FAILED);
        status.setFailureInfo("Job failed!");
    }
}


