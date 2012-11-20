package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import java.io.InputStream;
import java.util.Properties;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;
import com.google.inject.Provider;

public final class ZooKeeperProvider implements Provider<ZooKeeper>{
	
	private static final String ZK_CONF = "zooKeeper.properties";
	
	private static final String KEY_CONNECT_STRING = "connectionString";
	private static final String KEY_SESSION_TIMEOUT = "sessionTimeout";
	@Override
	public ZooKeeper get() {
		Properties props = new Properties();
		try {
			InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(ZK_CONF);
			props.load(in);
			in.close();
			String connectString = props.getProperty(KEY_CONNECT_STRING);
			int sessionTimeout = Integer.parseInt(props.getProperty(KEY_SESSION_TIMEOUT));
			Watcher watcher = new DefaultZKWatcher();
			return new ZooKeeper(connectString, sessionTimeout, watcher);
		} catch (Exception e) {
			return null;
		}
	}
	
}