/**
 * Project: taurus-zookeeper-helper
 * 
 * File Created at 2013-4-15
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
package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.guice.ZooKeeperProvider;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.CleanInfoChannel;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * TODO Comment of ZooKeeperReader
 * @author renyuan.sun
 *
 */
public class ZooKeeperReader {
    private static Log LOGGER = LogFactory.getLog(ZooKeeperReader.class);
    private CleanInfoChannel zkChannel;
    private TaurusZKInfoChannel readChannel;
    public static String SCHEDULE_PATH = "taurus/schedules";
    
    public ZooKeeperReader() {
        Injector injector = Guice.createInjector(new CleanInfoChannelModule());
        zkChannel = injector.getInstance(CleanInfoChannel.class);
        injector = Guice.createInjector(new ReadInfoChannelModule());
        readChannel = injector.getInstance(TaurusZKScheduleInfoChannel.class);
    }
    
    public void run(int offset) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(calendar.getTime());
        List<String> ipList = null;
        try {
            ipList = readChannel.getChildrenNodeName(null, SCHEDULE_PATH);
            
        } catch (KeeperException e) {
            LOGGER.error(e,e);
            return;
        } catch (InterruptedException e) {
            LOGGER.error(e,e);
            return;
        }
        for(String ip:ipList) {
            List<String> attemptList = null;
            try {
                attemptList = readChannel.getChildrenNodeName(null, SCHEDULE_PATH + "/" + ip + "/" + dateString);
                
            } catch (KeeperException e) {
                LOGGER.error(e,e);
                continue;
            } catch (InterruptedException e) {
                LOGGER.error(e,e);
                continue;
            }
            for(String attemptId:attemptList){
                
                if(!zkChannel.rmrPath("/"+ SCHEDULE_PATH + "/" + ip + "/" + attemptId)){
                    LOGGER.error("faile to delete " + SCHEDULE_PATH + "/" + ip + "/" + attemptId);
                }
                
            }
            zkChannel.rmrPath("/"+ SCHEDULE_PATH + "/" + ip + "/" + dateString);
            
        }
        
    }
    
    
    public void set() {
        try {
            readChannel.mkPathIfNotExists("taurus/schedules/192.168.26.22/2013-04-05");

            readChannel.mkPathIfNotExists("taurus/schedules/192.168.26.22/2013-04-05/attempt_201302041628_0003_0014_0001");
        } catch (Exception e) {
            LOGGER.error(e,e);
        }
    }
    public void read(String arg) {
    
        ScheduleStatus result = null;
        try {
            result = (ScheduleStatus)readChannel.getData(arg);
        } catch (Exception e) {
            LOGGER.error(e,e);
        }
        System.out.println( result.getStatus()+result.getFailureInfo()+result.getReturnCode());
    }
    
    public static void main(String []args) {
        ZooKeeperReader reader = new ZooKeeperReader();
       
        
//      cleaner.set();
//      if(args.length == 1 && args[0] != null)
//      {
//          System.out.println(args[0]);
//          cleaner.read(args[0]);
//      }
        
        reader.read("taurus/schedules/"+ args[0] + "/" + args[1] + "/status");
//      cleaner.read("taurus/schedules/192.168.7.80/attempt_201209241101_0009_1439_0001/status");
//      cleaner.read("taurus/schedules/192.168.7.80/attempt_201209241101_0009_1440_0001/status");

    }
    
    class CleanInfoChannelModule extends AbstractModule{
        @Override
        protected void configure() {
            bind(CleanInfoChannel.class).to(TaurusZKCleanerInfoChannel.class);
            bindZooKeeper();
        }
        
        protected void bindZooKeeper() {
            bind(ZkClient.class).toProvider(ZooKeeperProvider.class);
        }
    }
    
    class ReadInfoChannelModule extends AbstractModule{
        @Override
        protected void configure() {
            bind(TaurusZKInfoChannel.class).to(TaurusZKScheduleInfoChannel.class);
            bindZooKeeper();
        }
        
        protected void bindZooKeeper() {
            bind(ZkClient.class).toProvider(ZooKeeperProvider.class);
        }
    }
}
