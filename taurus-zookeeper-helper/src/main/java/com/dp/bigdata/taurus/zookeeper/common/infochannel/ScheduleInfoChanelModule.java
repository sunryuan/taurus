package com.dp.bigdata.taurus.zookeeper.common.infochannel;


import org.apache.zookeeper.ZooKeeper;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.AbstractModule;

public class ScheduleInfoChanelModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(ScheduleInfoChannel.class).to(TaurusZKScheduleInfoChannel.class);
		bindZooKeeper();
	}
	
	protected void bindZooKeeper() {
		bind(ZooKeeper.class).toProvider(ZooKeeperProvider.class);
	}
	
}
