package com.dp.bigdata.taurus.zookeeper.common.infochannel.guice;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.ZooKeeper;

import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;
import com.google.inject.Provider;

public final class ZooKeeperProvider implements Provider<ZooKeeper>{
    private static final Log LOG = LogFactory.getLog(ZooKeeperProvider.class);

	
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
			ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, null);
			Thread.sleep(5*1000);
			return zk ;
		} catch (Exception e) {
		    LOG.error(e.getMessage(),e);
			return null;
		}
	}
	
}