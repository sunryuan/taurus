package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.TaurusZKException;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;
import com.google.inject.Inject;

public class TaurusZKDeploymentInfoChannel extends TaurusZKInfoChannel implements DeploymentInfoChannel{

	private static final String ASSIGNMENTS = "assignments";
	private static final String CONF = "conf";
	private static final String STATUS = "status";
	private static final String NEW = "new";
	private static final String DELETE = "delete";
	   private static final Log LOGGER = LogFactory.getLog(TaurusZKDeploymentInfoChannel.class);

	@Inject
	TaurusZKDeploymentInfoChannel(ZkClient zk) {
        super(zk);
    }
	
	@Override
	public void connectToCluster(MachineType mt, String ip) {
		try{
			super.connectToCluster(mt, ip);
			if(!existPath(BASE, ASSIGNMENTS)){
				mkPathIfNotExists(BASE, ASSIGNMENTS);
			}
			if(mt == MachineType.AGENT){
				mkPathIfNotExists(BASE, ASSIGNMENTS, ip);
				mkPathIfNotExists(BASE, ASSIGNMENTS, ip, NEW);
				mkPathIfNotExists(BASE, ASSIGNMENTS, ip, DELETE);
			}
		} catch(Exception e){
		    LOGGER.error(e,e);
		    throw new TaurusZKException(e);
		}
	}
	
	@Override
	public void deploy(String ip, String taskID, Object conf,
			Object status, IZkDataListener dataListener) {
		try{
			mkPathIfNotExists(BASE, ASSIGNMENTS, ip, taskID);
			mkPathIfNotExists(BASE, ASSIGNMENTS, ip, taskID, CONF);
			mkPathIfNotExists(BASE, ASSIGNMENTS, ip, taskID, STATUS);
			setData(conf, BASE, ASSIGNMENTS, ip, taskID, CONF);
			setData(status, BASE, ASSIGNMENTS, ip, taskID, STATUS);
			addDeploymentStatusListener(ip, taskID, dataListener);
			mkPathIfNotExists(BASE, ASSIGNMENTS, ip, NEW, taskID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void completeDeploy(String ip, String taskID,  IZkDataListener dataListener) {
		try{
			rmPath(BASE, ASSIGNMENTS, ip, NEW, taskID);
			rmDataListener(dataListener,BASE, ASSIGNMENTS, ip, taskID, STATUS);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public Set<String> getNewDeploymentTaskIds(String ip) {
		try{
			return Collections.unmodifiableSet(new HashSet<String>(
					getChildrenNodeName(BASE, ASSIGNMENTS, ip, NEW)));
		} catch(Exception e){
			return Collections.unmodifiableSet(new HashSet<String>(0));
		}
	}

	@Override
	public void undeploy(String ip, String taskID, Object status,
			IZkDataListener dataListener) {
		try{
			setData(status, BASE, ASSIGNMENTS, ip, taskID, STATUS);
			mkPathIfNotExists(BASE, ASSIGNMENTS, ip, DELETE, taskID);
			
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void completeUndeploy(String ip, String taskID, IZkDataListener dataListener) {
		try{
		    rmDataListener(dataListener, BASE, ASSIGNMENTS, ip, taskID, STATUS);
			rmPath(BASE, ASSIGNMENTS, ip, DELETE, taskID);
			rmPath(BASE, ASSIGNMENTS, ip, taskID, STATUS);
			rmPath(BASE, ASSIGNMENTS, ip, taskID, CONF);
			rmPath(BASE, ASSIGNMENTS, ip, taskID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public Set<String> getNewUnDeploymentTaskIds(String ip) {
		try{
			return Collections.unmodifiableSet(new HashSet<String>(
					getChildrenNodeName(BASE, ASSIGNMENTS, ip, DELETE)));
		} catch(Exception e){
			return Collections.unmodifiableSet(new HashSet<String>(0));
		}
	}

	@Override
	public Object getConf(String ip, String taskID) {
		try{
			return getData(BASE, ASSIGNMENTS, ip, taskID, CONF);
		} catch(Exception e){
			return null;
		}
	}

	@Override
	public Object getStatus(String ip, String taskID) {
		try{
			return getData(BASE, ASSIGNMENTS, ip, taskID, STATUS);
		} catch(Exception e){
			return null;
		}
	}

	@Override
	public void updateStatus(String ip, String taskID, Object status) {
		try{
			setData(status, BASE, ASSIGNMENTS, ip, taskID, STATUS);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void updateConf(String ip, String taskID, Object conf) {
		try{
			setData(conf, BASE, ASSIGNMENTS, ip, taskID, CONF);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}
	
	private void addDeploymentStatusListener(String ip, String taskID, IZkDataListener dataListener) {
		try{
			addDataListener(dataListener, BASE, ASSIGNMENTS, ip, taskID, STATUS);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel#setNewDeployDirListen(org.I0Itec.zkclient.IZkChildListener)
     */
    @Override
    public void setNewDeployDirListen(IZkChildListener childListener) {
        addChildListener(childListener, BASE, ASSIGNMENTS, ip, NEW);   
    }

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel#setNewUndeployDirListen(org.I0Itec.zkclient.IZkChildListener)
     */
    @Override
    public void setNewUndeployDirListen(IZkChildListener childListener) {
        addChildListener(childListener, BASE, ASSIGNMENTS, ip, DELETE);   
    }



}
