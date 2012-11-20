package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.CleanInfoChannel;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ZooKeeperCleaner {
	private Log LOGGER = LogFactory.getLog(ZooKeeperCleaner.class);
	private CleanInfoChannel zkChannel;
	private TaurusZKInfoChannel readChannel;
	public ZooKeeperCleaner() {
		Injector injector = Guice.createInjector(new CleanInfoChannelModule());
		zkChannel = injector.getInstance(CleanInfoChannel.class);
		injector = Guice.createInjector(new ReadInfoChannelModule());
		readChannel = injector.getInstance(TaurusZKInfoChannel.class);
	}
	
	public void run(String []args){
		if(args.length != 1) {
			LOGGER.error("Need one path to clean");
		}
		
		if(zkChannel.rmrPath(args[0])) {
			LOGGER.info("delete " + args[0] + " success");
		} else {
			LOGGER.info("delete " + args[0] + " fail");
		}
	}
	
	public void read(String arg) {

		ScheduleStatus result = null;
		try {
			result = (ScheduleStatus)readChannel.getData(arg);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println( result.getStatus());
	}
	
	public static void main(String []args) {
		ZooKeeperCleaner cleaner = new ZooKeeperCleaner();
		cleaner.run(args);
//		cleaner.read("taurus/schedules/192.168.7.80/attempt_201209241101_0001_0175_0001/status");
//		cleaner.read("taurus/schedules/192.168.7.80/attempt_201209241101_0009_1439_0001/status");
//		cleaner.read("taurus/schedules/192.168.7.80/attempt_201209241101_0009_1440_0001/status");

	}
	
	class CleanInfoChannelModule extends AbstractModule{
		@Override
		protected void configure() {
			bind(CleanInfoChannel.class).to(TaurusZkCleanerInfoChannel.class);
			bindZooKeeper();
		}
		
		protected void bindZooKeeper() {
			bind(ZooKeeper.class).toProvider(ZooKeeperProvider.class);
		}
	}
	
	class ReadInfoChannelModule extends AbstractModule{
		@Override
		protected void configure() {
			bind(TaurusZKInfoChannel.class).to(TaurusZKScheduleInfoChannel.class);
			bindZooKeeper();
		}
		
		protected void bindZooKeeper() {
			bind(ZooKeeper.class).toProvider(ZooKeeperProvider.class);
		}
	}
}
