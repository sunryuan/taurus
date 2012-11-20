package com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces;

public interface CleanInfoChannel extends ClusterInfoChannel{
	
	public boolean rmrPath(String node);

}
