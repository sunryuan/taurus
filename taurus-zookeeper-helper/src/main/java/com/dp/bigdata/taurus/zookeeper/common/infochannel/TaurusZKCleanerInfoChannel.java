package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.CleanInfoChannel;
import com.google.inject.Inject;

public class TaurusZKCleanerInfoChannel extends TaurusZKInfoChannel implements CleanInfoChannel{

	private final Log LOGGER = LogFactory.getLog(TaurusZKCleanerInfoChannel.class);
	
	@Inject
	TaurusZKCleanerInfoChannel(ZooKeeper zk) {
        super(zk);
    }

	@Override
	public boolean rmrPath(String path) {
		try{
			LOGGER.info("Child node name: " + path);
			Stat stat = zk.exists(path, null);
			
			if(stat != null){
				List<String> children = zk.getChildren(path, false);
				for(String childPath:children) {
					rmrPath(path+"/"+childPath);
				}
				zk.delete(path, stat.getVersion());
			}
		} catch(KeeperException e){
			if(e.code() != Code.NONODE){
				LOGGER.error(e,e);
				return false;
			}
		} catch(InterruptedException e) {
			LOGGER.error(e,e);
			return false;
		}
		return true;
	} 


}
