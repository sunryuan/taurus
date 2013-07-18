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
import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.agent.common.BaseTaskThread;
import com.dp.bigdata.taurus.agent.exec.Executor;
import com.dp.bigdata.taurus.agent.utils.LockHelper;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;

/**
 * TODO Comment of KillThread
 * @author renyuan.sun
 *
 */
public class KillTaskThread extends BaseTaskThread{
    
    private static final Log LOGGER = LogFactory.getLog(KillTaskThread.class);
    private static final String KILL_COMMAND = "%s %s %s";

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
        Lock lock = LockHelper.getLock(jobInstanceId);
        try{
            lock.lock();
            ScheduleConf conf = (ScheduleConf) cs.getConf(localIp, jobInstanceId);
            ScheduleStatus status = (ScheduleStatus) cs.getStatus(localIp, jobInstanceId);
            killTask(localIp,conf, status);
            cs.updateStatus(localIp, jobInstanceId, status);
            cs.removeRunningJob(localIp, jobInstanceId);
        } catch(Exception e){
            LOGGER.error(e,e);
        } finally{
            lock.unlock();
        }
    }
    
    private void killTask(String ip,ScheduleConf conf, ScheduleStatus status){
        String attemptID = conf.getAttemptID();
        int returnCode = 1;
        try{
            if(ON_WINDOWS){
                returnCode = executor.kill(attemptID);
            } else{
                String fileName = running + FILE_SEPRATOR + '.' + attemptID;
                BufferedReader br = new BufferedReader(new FileReader((new File(fileName)))); 
                String pid = br.readLine();
                br.close();
                LOGGER.debug("Ready to kill " + attemptID + ", pid is " + pid);
                String kill = String.format(KILL_COMMAND, killJob, pid, "9");
                returnCode = executor.execute("kill",System.out,System.err,kill);
                try {
                    new File(fileName).delete();
                } catch(Exception e) {
                    //do nothing
                }
            }
        } catch(Exception e) {
            LOGGER.error(e,e);
            returnCode = 1;
        }
        
        if(returnCode == 0)  {
            status.setStatus(ScheduleStatus.DELETE_SUCCESS);
        }
    }
    
}