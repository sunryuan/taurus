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
 * deploy task thread
 * @author renyuan.sun
 *
 */
public class DeploymentThread extends BaseEnvManager {

    private Log LOGGER = LogFactory.getLog(DeploymentThread.class);

    String localIp;
    DeploymentInfoChannel cs;
    String task;

    DeploymentThread( String localIp, DeploymentInfoChannel cs, String task){
        this.localIp = localIp;
        this.cs = cs;
        this.task = task;
    }
    
    @Override
    public void run() {
        LOGGER.debug("start deploy");
        Lock lock = LockHelper.getLock(task);
        try{
            lock.lock();
            DeploymentConf conf = (DeploymentConf) cs.getConf(localIp, task);
            DeploymentStatus status = (DeploymentStatus) cs.getStatus(localIp, task);
            if(status == null || status.getStatus() == DeploymentStatus.DEPLOY_SUCCESS){
                return;
            }

            deployTask(conf, status);
            cs.updateStatus(localIp, task, status);
            cs.updateConf(localIp, task, conf);
        } catch(Exception e){
            LOGGER.error(e,e);
        } finally{
            lock.unlock();
        }
    }
    private void deployTask(DeploymentConf conf, DeploymentStatus status){
        if(conf == null){
            LOGGER.error("DeploymentConf is empty!");
            return;
        }

        String ftpUrl = conf.getUrl();
        if(ftpUrl == null){
            LOGGER.error("HDFS path is empty!");
            return;
        }
        String name = conf.getName();

        if(name == null){
            LOGGER.error("Job  ID is empty!");
            return;
        }
        File hdfsFile = new File(ftpUrl);
        String fileName = hdfsFile.getName();
        String localParentPath =  jobPath + FILE_SEPRATOR + name;
        String localPath = localParentPath + FILE_SEPRATOR + fileName;
        
        StringBuilder stdErr = new StringBuilder();
        try{
            if(new File(localParentPath).exists()) {
            	//删除老文件
            	FileUtils.deleteDirectory(new File(localParentPath));
            }
            LOGGER.debug("hdfsPath:" + ftpUrl + ";localPath:" +fileName);
            //int returnCode = executor.execute("deploy task",System.out, System.err, taskDeploy, hdfsPath,localPath);
            try{
                taskHelper.deployTask(ftpUrl, localPath);
                status.setStatus(DeploymentStatus.DEPLOY_SUCCESS);
                conf.setLocalPath(localParentPath);
                LOGGER.debug("Job " + name + " deploy successed");
            } catch(Exception e ){
                LOGGER.debug("Job " + name + " deploy failed",e);
                status.setStatus(DeploymentStatus.DEPLOY_FAILED);
                status.setFailureInfo(stdErr.toString());
            }
        } catch(Exception e){
            LOGGER.error(e,e);
            status.setStatus(DeploymentStatus.DEPLOY_FAILED);
            status.setFailureInfo(stdErr.toString());
        }
    }      

}
