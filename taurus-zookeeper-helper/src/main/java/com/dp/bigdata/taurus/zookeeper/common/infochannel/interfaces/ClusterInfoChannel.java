package com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces;

import java.util.Set;

import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.HeartbeatInfo;

public interface ClusterInfoChannel {
    
	public void connectToCluster(MachineType mt, String ip);

	public boolean exists(MachineType mt, String ip);
	
	public void updateRealtimeHeartbeatInfo(MachineType mt, String ip);

	public void updateHeartbeatInfo(MachineType mt, String ip, HeartbeatInfo info);

	public HeartbeatInfo getHeartbeatInfo(MachineType mt, String ip);
	
	public Set<String> getAllConnectedMachineIps(MachineType mt);
		
	public void close();


}
