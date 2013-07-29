package com.dp.bigdata.taurus.zookeeper.common.infochannel;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;


//import org.apache.zookeeper.Watcher;


import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.TaurusZKException;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ClusterInfoChannel;
import com.google.inject.Inject;

public abstract class TaurusZKInfoChannel implements ClusterInfoChannel{

    private static final Log LOG = LogFactory.getLog(TaurusZKInfoChannel.class);

	private static final int MAX_HEARTBEAT_LENGTH = 100;

	protected static final String SEP = "/";
	protected static final String BASE = "taurus";
	private static final String HEARTBEATS = "heartbeats";
	private static final String REALTIME = "realtime";
	private static final String INFO = "info";

	
	protected ZkClient zk;
	protected MachineType mt;
	protected String ip;

	@Inject
	TaurusZKInfoChannel(ZkClient zk){
		this.zk = zk;
		
	}
	
	@Override
	public void connectToCluster(MachineType mt, String ip) {
	    this.mt = mt;
	    this.ip = ip;
		try{
			if(!existPath(BASE)){
				setupBasePath();
			}
			if(existPath(BASE, HEARTBEATS, mt.getName(), REALTIME, ip)){
			    rmPath(BASE, HEARTBEATS, mt.getName(), REALTIME, ip);
			}
			mkPathIfNotExists(BASE, HEARTBEATS, mt.getName(), REALTIME, ip);
			mkPathIfNotExists(BASE, HEARTBEATS, mt.getName(), INFO, ip);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public boolean exists(MachineType mt, String ip) {
	    return zk.exists(getFullPath(BASE, HEARTBEATS, mt.getName(), REALTIME, ip)); 
	}

    @Override
    public void updateRealtimeHeartbeatInfo(MachineType mt, String ip) {
        try{
            setData(System.currentTimeMillis(), BASE, HEARTBEATS, mt.getName(), REALTIME, ip);
        } catch(Exception e){
            throw new TaurusZKException(e);
        }
    }
    
	@Override
	public void updateHeartbeatInfo(MachineType mt, String ip, Object info) {
		try{
			List<Object> heartbeatInfoList = new ArrayList<Object>(getHeartbeatInfo(mt, ip));
			if(heartbeatInfoList.size() >= MAX_HEARTBEAT_LENGTH){
				heartbeatInfoList.remove(heartbeatInfoList.size()-1);
			}
			heartbeatInfoList.add(info);

			setData(heartbeatInfoList, BASE, HEARTBEATS, mt.getName(), INFO, ip);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}
	

	@SuppressWarnings("unchecked")
    @Override
	public List<Object> getHeartbeatInfo(MachineType mt, String ip) {
		try{
			return Collections.unmodifiableList(
					(List<Object>)getData(BASE, HEARTBEATS, mt.getName(), INFO, ip));
		} catch(Exception e){
			return Collections.unmodifiableList(new ArrayList<Object>());
		}	
	}

	@Override
	public Set<String> getAllConnectedMachineIps(MachineType mt) {
		try{
			List<String> ips = getChildrenNodeName( BASE, HEARTBEATS, mt.getName(), REALTIME);
			Set<String> newIps = new HashSet<String>();
			for(String ip: ips){
				newIps.add(ip);
			}
			return Collections.unmodifiableSet(newIps);
		} catch(Exception e){
			return Collections.unmodifiableSet(new HashSet<String>(0));
		}
	}

	@Override
	public void close(){
		try{
			zk.close();
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}
	
	
	private void setupBasePath() 
	throws KeeperException, InterruptedException, IOException{
		mkPathIfNotExists(BASE);
		mkPathIfNotExists(BASE, HEARTBEATS);
		mkPathIfNotExists(BASE, HEARTBEATS, MachineType.SERVER.getName());
		mkPathIfNotExists(BASE, HEARTBEATS, MachineType.SERVER.getName(), REALTIME);
		mkPathIfNotExists(BASE, HEARTBEATS, MachineType.SERVER.getName(), INFO);
		mkPathIfNotExists(BASE, HEARTBEATS, MachineType.AGENT.getName());
		mkPathIfNotExists(BASE, HEARTBEATS, MachineType.AGENT.getName(), REALTIME);
		mkPathIfNotExists(BASE, HEARTBEATS, MachineType.AGENT.getName(), INFO);
	}

	protected List<String> getChildrenNodeName(String ... path)
	throws KeeperException, InterruptedException{
	    return zk.getChildren(getFullPath(path));
	}

	protected void addChildrenWatcher(String path) 
	throws KeeperException, InterruptedException{
	    zk.getChildren(path);
	}



	private String getFullPath(String... node){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < node.length; i++){
			sb.append(SEP).append(node[i]);
		}
		return sb.toString();
	}

	protected void mkPathIfNotExists(CreateMode mode, String... node) {
        try{
            zk.create(getFullPath(node), null, mode);
        } catch (ZkNodeExistsException e){
            //Do nothing.
        }
	}
		
	protected void mkPathIfNotExists(String... node) {
	    try{
	        zk.create(getFullPath(node), null, CreateMode.PERSISTENT);
	    } catch (ZkNodeExistsException e){
            //Do nothing.
        }
	}
	

//    protected void mkPath(CreateMode mode, String... node) {
//            zk.create(getFullPath(node), null, mode);
//    }
//        
//    protected void mkPath(String... node) {
//            zk.create(getFullPath(node), null, CreateMode.PERSISTENT);
//    }

	protected boolean existPath(String... node) {
        return zk.exists(getFullPath(node));     
	}

	protected void rmPath(String... node) {
	    zk.delete(getFullPath(node));
	} 
	
	protected void setData(Object data, String... node) {
	    String path = getFullPath(node);
	    if(!zk.exists(path)){
	        LOG.error(path + " not exists");
	        throw new RuntimeException();
	    }
	    zk.writeData( getFullPath(node),data);
	}

	protected Object getData(String... node) {
	    return zk.readData(getFullPath(node), true);
	}
	
	protected void addDataListener(IZkDataListener dataListener, String... node){
	    zk.subscribeDataChanges(getFullPath(node), dataListener);
	}
	
	protected void rmDataListener(IZkDataListener dataListener, String... node){
        zk.unsubscribeDataChanges(getFullPath(node), dataListener);
    }
	
	protected void addChildListener(IZkChildListener childListener, String... node){
        zk.subscribeChildChanges(getFullPath(node), childListener);
    }
	
	protected void rmChildListener(IZkChildListener childListener, String... node){
        zk.unsubscribeChildChanges(getFullPath(node), childListener);
    }
	
}
