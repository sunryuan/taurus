package com.dp.bigdata.taurus.zookeeper.common.infochannel.guice;


import org.I0Itec.zkclient.ZkClient;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.TaurusZKScheduleInfoChannel;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.AbstractModule;

public class ScheduleInfoChanelModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(ScheduleInfoChannel.class).to(TaurusZKScheduleInfoChannel.class);
		bindZooKeeper();
	}
	
	protected void bindZooKeeper() {
		bind(ZkClient.class).toProvider(ZooKeeperProvider.class);
	}
	
}
