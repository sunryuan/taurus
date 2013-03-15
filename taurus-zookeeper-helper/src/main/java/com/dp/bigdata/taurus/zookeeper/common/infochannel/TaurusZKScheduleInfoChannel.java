package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.TaurusZKException;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.Inject;

public class TaurusZKScheduleInfoChannel extends TaurusZKInfoChannel implements ScheduleInfoChannel{

	private static final String SCHEDULE = "schedules";
	private static final String CONF = "conf";
	private static final String STATUS = "status";
	private static final String NEW = "new";
	private static final String DELETE = "delete";
	private static final String RUNNING = "running";
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
				mkPath(BASE, SCHEDULE, ip, RUNNING);
			}
			
		} catch(Exception e){
			LOGGER.error("Connect to cluster failed. ",e);
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

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel#addRunningJob(java.lang.String, java.lang.String)
     */
    @Override
    public void addRunningJob(String ip, String taskAttempt) {
        try {
            mkPath(BASE, SCHEDULE, ip, RUNNING,taskAttempt);
        } catch (Exception e) {
            LOGGER.error("Add running job failed.", e);
        }
    }

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel#removeRunningJob(java.lang.String, java.lang.String)
     */
    @Override
    public void removeRunningJob(String ip, String taskAttempt) {
        try {
            rmPath(BASE, SCHEDULE, ip, RUNNING, taskAttempt);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date = format.format(new Date());
            try{    
                mkPath(BASE, SCHEDULE, ip, date, taskAttempt);
            } catch ( NoNodeException e){
                mkPath(BASE, SCHEDULE, ip, date);
                mkPath(BASE, SCHEDULE, ip, date, taskAttempt);
            }
        } catch (Exception e) {
            LOGGER.error("Remove running job failed.", e);
        }
        
    }

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel#getRunningJObs(java.lang.String)
     */
    @Override
    public Set<String> getRunningJobs(String ip) {
        try{
            return Collections.unmodifiableSet(new HashSet<String>(
                    getChildrenNodeName(watcher, BASE, SCHEDULE, ip, RUNNING)));
        } catch(Exception e){
            return Collections.unmodifiableSet(new HashSet<String>(0));
        }
    }
}
