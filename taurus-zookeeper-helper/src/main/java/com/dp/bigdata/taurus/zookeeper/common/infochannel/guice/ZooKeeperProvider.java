package com.dp.bigdata.taurus.zookeeper.common.infochannel.guice;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;
import com.google.inject.Provider;

public final class ZooKeeperProvider implements Provider<ZooKeeper>{
    private static final Log LOG = LogFactory.getLog(ZooKeeperProvider.class);
	private static final String ZK_CONF = "zooKeeper.properties";
	
	private static final String KEY_CONNECT_STRING = "connectionString";
	private static final String KEY_SESSION_TIMEOUT = "sessionTimeout";
	private static final int RETRY_TIME = 30;
	@Override
	public ZooKeeper get() {
		Properties props = new Properties();
		try {
			InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(ZK_CONF);
			props.load(in);
			in.close();
			String connectString = props.getProperty(KEY_CONNECT_STRING);
			int sessionTimeout = Integer.parseInt(props.getProperty(KEY_SESSION_TIMEOUT));
	        ZKConnectWatcher w = new ZKConnectWatcher();

			ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, w);
			int count = 0;
            LOG.info("Start Connectting to zookeeper: " + connectString);

			while(zk.getState() != ZooKeeper.States.CONNECTED){
			    Thread.sleep(1000);
			    //LOG.debug("connecting..");
			    count++;
			    if(count > RETRY_TIME){
			        throw new Exception("Fail to connect to " + connectString);
			    }
			}
			LOG.info("Connected to "+connectString);
			return zk ;
		} catch (Exception e) {
		    LOG.error(e.getMessage(),e);
			return null;
		}
	}
	private class ZKConnectWatcher implements Watcher{

        /* (non-Javadoc)
         * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
         */
        @Override
        public void process(WatchedEvent event) {
         
        }
	    
	}
	
}