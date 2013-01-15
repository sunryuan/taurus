package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.TaurusZKException;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.Inject;

public class TaurusZKScheduleInfoChannel extends TaurusZKInfoChannel implements ScheduleInfoChannel{

	private static final String SCHEDULE = "schedules";
	private static final String CONF = "conf";
	private static final String STATUS = "status";
	private static final String NEW = "new";
	private static final String DELETE = "delete";
	private static final Log LOGGER = LogFactory.getLog(TaurusZKScheduleInfoChannel.class);
	
	@Inject
	TaurusZKScheduleInfoChannel(ZooKeeper zk) {
		super(zk);
	}

	@Override
	public void connectToCluster(MachineType mt, String ip) {
		try{
			super.connectToCluster(mt, ip);
			if(!existPath(BASE, SCHEDULE)){
				mkPath(BASE, SCHEDULE);
			}
			if(mt == MachineType.AGENT){
				mkPath(BASE, SCHEDULE, ip);
				mkPath(BASE, SCHEDULE, ip, NEW);
				mkPath(BASE, SCHEDULE, ip, DELETE);
			}
		} catch(Exception e){
			LOGGER.error("Connect To cluster failed. ",e);
			throw new TaurusZKException(e);
		}
	}

	@Override
	public Object getConf(String ip, String attemptID) {
		try{
			return getData(BASE, SCHEDULE, ip, attemptID, CONF);
		} catch(Exception e){
			return null;
		}
	}

	@Override
	public Object getStatus(String ip, String attemptID, Watcher watcher) {
		try{
			return getData(BASE, SCHEDULE, ip, attemptID, STATUS);
		} catch(Exception e){
			return null;
		}
	}

	@Override
	public void updateStatus(String ip, String attemptID, Object status) {
		try{
			setData(status, BASE, SCHEDULE, ip, attemptID, STATUS);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void updateConf(String ip, String attemptID, Object conf) {
		try{
			setData(conf, BASE, SCHEDULE, ip, attemptID, CONF);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}
	
	private void addKillStatusWatcher(String ip,
			String attemptID, Watcher watcher) {
		try{
			addDataWatcher(watcher, BASE, SCHEDULE, ip, attemptID, STATUS);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void execute(String ip, String attemptID, Object conf,
			Object status) {
		try{
			mkPath(BASE, SCHEDULE, ip, attemptID);
			mkPath(BASE, SCHEDULE, ip, attemptID, CONF);
			mkPath(BASE, SCHEDULE, ip, attemptID, STATUS);
			setData(conf, BASE, SCHEDULE, ip, attemptID, CONF);
			setData(status, BASE, SCHEDULE, ip, attemptID, STATUS);
			mkPath(BASE, SCHEDULE, ip, NEW, attemptID);

		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void completeExecution(String ip, String attemptID) {
		try{
			rmPath(BASE, SCHEDULE, ip, NEW, attemptID);
			
		} catch(Exception e){
			throw new TaurusZKException(e);
		}		
	}

	@Override
	public Set<String> getNewExecutionJobInstanceIds(String ip, Watcher watcher) {
		try{
			return Collections.unmodifiableSet(new HashSet<String>(
					getChildrenNodeName(watcher, BASE, SCHEDULE, ip, NEW)));
		} catch(Exception e){
			return Collections.unmodifiableSet(new HashSet<String>(0));
		}
	}

	@Override
	public void killTask(String ip, String attemptID, Object status,
			Watcher watcher) {
		try{
			setData(status, BASE, SCHEDULE, ip, attemptID, STATUS);
			addKillStatusWatcher(ip, attemptID, watcher);
			mkPath(BASE, SCHEDULE, ip, DELETE, attemptID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void completeKill(String ip, String attemptID) {
		try{
			rmPath(BASE, SCHEDULE, ip, DELETE, attemptID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public Set<String> getNewKillingJobInstanceIds(String ip,
			Watcher watcher) {
		try{
			return Collections.unmodifiableSet(new HashSet<String>(
					getChildrenNodeName(watcher, BASE, SCHEDULE, ip, DELETE)));
		} catch(Exception e){
			return Collections.unmodifiableSet(new HashSet<String>(0));
		}
	}

}
