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

public class ZooKeeperCleaner {
	private static Log LOGGER = LogFactory.getLog(ZooKeeperCleaner.class);
	private CleanInfoChannel zkChannel;
	private TaurusZKInfoChannel readChannel;
	public static String SCHEDULE_PATH = "taurus/schedules";
	
	public ZooKeeperCleaner() {
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
        } catch(Exception e) {
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
		
		System.out.println( result.getStatus());
	}
	
	public static void main(String []args) {
		ZooKeeperCleaner cleaner = new ZooKeeperCleaner();
		int offset = -7;
		if(args.length == 1 && args[0] != null){
            try { 
                offset = Integer.parseInt(args[0]);
            } catch(Exception e) {
                LOGGER.error("args error",e);
            }
		}
		if(args.length == 2 && args[0] != null && args[1] != null){
		    int start = 0;
		    int end = -1;
            try { 
                start = Integer.parseInt(args[0]);
                end  = Integer.parseInt(args[1]);
            } catch(Exception e) {
                LOGGER.error("args error",e);
            }
            for(int i = start; i <= end; i ++){
                cleaner.run(i);
            }
            return;
        }
		cleaner.run(offset);
		
//		cleaner.set();
//		if(args.length == 1 && args[0] != null)
//		{
//		    System.out.println(args[0]);
//		    cleaner.read(args[0]);
//		}
		cleaner.read("taurus/schedules/10.1.1.161//status");
//		cleaner.read("taurus/schedules/192.168.7.80/attempt_201209241101_0009_1439_0001/status");
//		cleaner.read("taurus/schedules/192.168.7.80/attempt_201209241101_0009_1440_0001/status");

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
