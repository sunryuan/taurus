/**
 * Project: taurus-agent
 * 
 * File Created at 2013-5-22
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
package com.dp.bigdata.taurus.agent.deploy;

import java.io.File;
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.agent.common.BaseEnvManager;
import com.dp.bigdata.taurus.agent.utils.LockHelper;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.DeploymentConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.DeploymentStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;

/**
 * undeploy task thread
 * @author renyuan.sun
 *
 */
public class UndeploymentThread extends BaseEnvManager {

    private String localIp;
    private DeploymentInfoChannel cs;
    private String taskID;
    
    private Log LOGGER = LogFactory.getLog(UndeploymentThread.class);
    
    UndeploymentThread( String localIp, DeploymentInfoChannel cs, String taskID){
        this.localIp = localIp;
        this.cs = cs;
        this.taskID = taskID;
    }

    @Override
    public void run() {
        Lock lock = LockHelper.getLock(taskID);
        try{
            lock.lock();
            DeploymentConf conf = (DeploymentConf) cs.getConf(localIp, taskID);
            DeploymentStatus status = (DeploymentStatus) cs.getStatus(localIp, taskID);
            if(status == null || status.getStatus() == DeploymentStatus.DELETE_SUCCESS){
                return;
            }

            undeployTask(conf, status);
            cs.updateStatus(localIp, taskID, status);
        } catch(Exception e){
            LOGGER.error(e,e);
        } finally{
            lock.unlock();
        }
    }
    
    private void undeployTask(DeploymentConf conf, DeploymentStatus status){
        if(conf == null){
            LOGGER.error("DeploymentConf is empty!");
            return;
        }

        String localPath = conf.getLocalPath();
        if(localPath == null){
            LOGGER.error("local path is empty!");
            return;
        }
        try{
            if(new File(localPath).exists()) {
                FileUtils.deleteDirectory(new File(localPath));
                status.setStatus(DeploymentStatus.DELETE_SUCCESS);
            }
        } catch(Exception e){
            LOGGER.error(e.getMessage(),e);
            status.setStatus(DeploymentStatus.DELETE_FAILED);
            status.setFailureInfo("delete failed");
        }
    }

}
