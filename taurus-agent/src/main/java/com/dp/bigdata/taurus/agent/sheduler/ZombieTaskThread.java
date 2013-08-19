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
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.agent.common.BaseEnvManager;
import com.dp.bigdata.taurus.agent.utils.TaskHelper;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;

/**
 * 维护agent上次退出时遗留的作业。
 * 负责上传作业日志，维护作业状态等等
 * @author renyuan.sun
 *
 */
public final class ZombieTaskThread  extends BaseEnvManager{
    
    String localIp;
    ScheduleInfoChannel cs;
  
    TaskHelper taskLogUploader;
    
    private Log LOGGER = LogFactory.getLog(ZombieTaskThread.class);
    private static final int INTERVAL = 60 * 1000;
   
    ZombieTaskThread( String localIp, ScheduleInfoChannel cs){
        this.localIp = localIp;
        this.cs = cs;
    }
    @Override
    public void run() {
        Set<String> jobInstanceIds = cs.getRunningJobs(localIp);
        int num = 0;
        if(ON_WINDOWS) {
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
                File returnValueFile = new File(running + FILE_SEPRATOR + "rv." + attemptID);
                int returnCode =1 ;
               
                if(!new File(pidFile).exists() && returnValueFile.exists()){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String date = format.format(new Date());
                    //忽略跨天日志问题
                    String logFilePath = logPath + FILE_SEPRATOR + date + FILE_SEPRATOR + attemptID + ".log";
                    String errorFilePath = logPath + FILE_SEPRATOR + date + FILE_SEPRATOR + attemptID + ".error";
                    String htmlFileName = attemptID + ".html";
                    String htmlFilePath = logPath + FILE_SEPRATOR + date + FILE_SEPRATOR + htmlFileName;
                    try{
                        BufferedReader br = new BufferedReader(new FileReader(returnValueFile)); 
                        String returnValStr = br.readLine();
                        br.close();
                        returnCode = Integer.parseInt(returnValStr);
                    } catch (Exception e){
                        LOGGER.error(e,e);
                        returnCode = 1;
                    }
                    try {
                        returnValueFile.delete();
                        taskLogUploader.uploadLog(returnCode, errorFilePath, logFilePath, htmlFilePath, htmlFileName);
                    } catch (IOException e) {
                        LOGGER.error(e,e);
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
                LOGGER.error(e,e);
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