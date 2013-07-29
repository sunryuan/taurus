package com.dp.bigdata.taurus.zookeeper.common.infochannel;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	private static final String RUNNING = "running";
	private static final String UPDATE = "update";
	private static final Log LOGGER = LogFactory.getLog(TaurusZKScheduleInfoChannel.class);
	
	@Inject
	TaurusZKScheduleInfoChannel(ZkClient zk) {
		super(zk);
	}

	@Override
	public void connectToCluster(MachineType mt, String ip) {
		try{
			super.connectToCluster(mt, ip);
			if(!existPath(BASE, SCHEDULE)){
				mkPathIfNotExists(BASE, SCHEDULE);
			}
			if(mt == MachineType.AGENT){
				mkPathIfNotExists(BASE, SCHEDULE, ip);
				mkPathIfNotExists(BASE, SCHEDULE, ip, NEW);
				mkPathIfNotExists(BASE, SCHEDULE, ip, DELETE);
				mkPathIfNotExists(BASE, SCHEDULE, ip, RUNNING);
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
	public Object getStatus(String ip, String attemptID) {
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
	
	
	

	@Override
	public void execute(String ip, String attemptID, Object conf,
			Object status) {
		try{
			mkPathIfNotExists(BASE, SCHEDULE, ip, attemptID);
			mkPathIfNotExists(BASE, SCHEDULE, ip, attemptID, CONF);
			mkPathIfNotExists(BASE, SCHEDULE, ip, attemptID, STATUS);
			setData(conf, BASE, SCHEDULE, ip, attemptID, CONF);
			setData(status, BASE, SCHEDULE, ip, attemptID, STATUS);
			mkPathIfNotExists(BASE, SCHEDULE, ip, NEW, attemptID);

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
	public Set<String> getNewExecutionJobInstanceIds(String ip) {
		try{
			return Collections.unmodifiableSet(new HashSet<String>(
					getChildrenNodeName(BASE, SCHEDULE, ip, NEW)));
		} catch(Exception e){
			return Collections.unmodifiableSet(new HashSet<String>(0));
		}
	}

	@Override
	public void killTask(String ip, String attemptID, Object status, IZkDataListener dataListener) {
		try{
			setData(status, BASE, SCHEDULE, ip, attemptID, STATUS);
			addDataListener(dataListener, BASE, SCHEDULE, ip, attemptID, STATUS);
			mkPathIfNotExists(BASE, SCHEDULE, ip, DELETE, attemptID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public void completeKill(String ip, String attemptID, IZkDataListener dataListener) {
		try{
		    rmDataListener(dataListener, BASE, SCHEDULE, ip, attemptID, STATUS);
			rmPath(BASE, SCHEDULE, ip, DELETE, attemptID);
		} catch(Exception e){
			throw new TaurusZKException(e);
		}
	}

	@Override
	public Set<String> getNewKillingJobInstanceIds(String ip) {
		try{
			return Collections.unmodifiableSet(new HashSet<String>(
					getChildrenNodeName(BASE, SCHEDULE, ip, DELETE)));
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
            mkPathIfNotExists(BASE, SCHEDULE, ip, RUNNING,taskAttempt);
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
                mkPathIfNotExists(BASE, SCHEDULE, ip, date, taskAttempt);
            } catch ( ZkNoNodeException e){
                mkPathIfNotExists(BASE, SCHEDULE, ip, date);
                mkPathIfNotExists(BASE, SCHEDULE, ip, date, taskAttempt);
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
                    getChildrenNodeName(BASE, SCHEDULE, ip, RUNNING)));
        } catch(Exception e){
            return Collections.unmodifiableSet(new HashSet<String>(0));
        }
    }

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel#setExecutionJobListener(org.I0Itec.zkclient.IZkChildListener)
     */
    @Override
    public void setExecutionJobListener(IZkChildListener childListener) {
        addChildListener(childListener, BASE, SCHEDULE, ip, NEW);    
    }

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel#setKillingJobListener(org.I0Itec.zkclient.IZkChildListener)
     */
    @Override
    public void setKillingJobListener(IZkChildListener childListener) {
        addChildListener(childListener, BASE, SCHEDULE, ip, DELETE);   
        
    }

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel#needUpdate(java.lang.String)
     */
    @Override
    public boolean needUpdate(String ip) {
        return existPath(BASE, SCHEDULE, ip, UPDATE);
    }

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel#completeUpdate(java.lang.String)
     */
    @Override
    public void completeUpdate(String ip) {
        rmPath(BASE, SCHEDULE, ip, UPDATE);
    }

    /* (non-Javadoc)
     * @see com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel#newUpdate(java.lang.String)
     */
    @Override
    public void newUpdate(String ip) {
        mkPathIfNotExists(BASE, SCHEDULE, ip, UPDATE);
    }

}
