package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;


import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ClusterInfoChannel;
import com.google.inject.Inject;

public class DefaultZKWatcher implements Watcher{
    private static final Log LOGGER = LogFactory.getLog(DefaultZKWatcher.class);
    private ClusterInfoChannel zkCluster;

    
    @Inject
    public DefaultZKWatcher(ClusterInfoChannel zkCluster){
        this.zkCluster = zkCluster;
    }

    public ClusterInfoChannel getZkCluster() {
        return zkCluster;
    }

    public void setZkCluster(ClusterInfoChannel zkCluster) {
        this.zkCluster = zkCluster;
    }

    @Override
	public void process(WatchedEvent event) {
	    if (event.getState() == KeeperState.Expired) {
	        LOGGER.info("Session Expried init");
	        while(true) {  
                try{  
                    zkCluster.reconnectToCluster(this);
                    break;  
                }catch(RuntimeException e) {  
                    LOGGER.error(e.getMessage(), e);
                    try {
                        Thread.sleep(5* 1000);
                    } catch (InterruptedException e1) {
                        LOGGER.error(e1.getMessage(),e1);
                    }  
                }  
            }  
        }   
	}
	
}
