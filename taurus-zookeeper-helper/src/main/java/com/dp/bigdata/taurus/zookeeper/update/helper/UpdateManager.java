/**
 * Project: taurus-zookeeper-helper
 * 
 * File Created at 2013-5-10
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
package com.dp.bigdata.taurus.zookeeper.update.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.guice.ScheduleInfoChanelModule;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Update taurus agent
 * @author renyuan.sun
 *
 */
public class UpdateManager {
    private static final Log LOGGER = LogFactory.getLog(UpdateManager.class);

    private ScheduleInfoChannel dic;
    
    private static final int TIMEOUT = 10 * 60 *1000;
    
    private static final int INTERVAL = 30 * 1000;
    
    public UpdateManager(){
        Injector injector = Guice.createInjector(new ScheduleInfoChanelModule());
        dic = injector.getInstance(ScheduleInfoChannel.class);
    }
    
    public static void main(String []args){
        UpdateManager updater = new UpdateManager();
        List<String> ipList = new ArrayList<String>();
        Set<String> successList = new HashSet<String>();
        int timeWait = 0;

        for(String ip:args){
            try{
                updater.dic.newUpdate(ip);
                ipList.add(ip);
            } catch(Exception e){
                LOGGER.error(ip + " is not legal!\n" ,e);
            }
        }
        while(true){
            try {
                Thread.sleep(INTERVAL);
                timeWait += INTERVAL;
            } catch (InterruptedException e) {
                LOGGER.error(e,e);
            }
            for(String ip:args){
                boolean isFinished = false;
                try{
                    isFinished = !updater.dic.needUpdate(ip);
                }  catch(Exception e){
                    LOGGER.error(e,e);
                }
                if(isFinished){
                    successList.add(ip);
                    LOGGER.info(ip+" finish to update!");
                }
            }
            if(ipList.size() == successList.size()){
                LOGGER.info("All finish to update!");
                break;
            }
            if(timeWait >= TIMEOUT){
                LOGGER.info("Time out!");
                for(String ip:ipList){
                    if(!successList.contains(ip)) {
                        LOGGER.info("Fail to update "+ip);
                        try{
                            updater.dic.completeUpdate(ip);
                        } catch(Exception e){
                            LOGGER.error(e,e);
                        }
                    }
                }
            }
            
        }
    }
}
