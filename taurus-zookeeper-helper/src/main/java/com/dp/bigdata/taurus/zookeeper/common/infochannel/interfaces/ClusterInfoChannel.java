package com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces;

import java.util.List;
import java.util.Set;


import com.dp.bigdata.taurus.zookeeper.common.MachineType;

public interface ClusterInfoChannel {
    
	public void connectToCluster(MachineType mt, String ip);

	public boolean exists(MachineType mt, String ip);

	public void updateHeartbeatInfo(MachineType mt, String ip, Object info);

	public List<Object> getHeartbeatInfo(MachineType mt, String ip);
	
	public Set<String> getAllConnectedMachineIps(MachineType mt);
		
	public void close();


}
