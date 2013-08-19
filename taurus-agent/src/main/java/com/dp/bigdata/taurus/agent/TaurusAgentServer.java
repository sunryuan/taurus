package com.dp.bigdata.taurus.agent;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.agent.common.BaseEnvManager;
import com.dp.bigdata.taurus.agent.deploy.DeploymentUtility;
import com.dp.bigdata.taurus.agent.exec.Executor;
import com.dp.bigdata.taurus.agent.sheduler.ScheduleUtility;
import com.dp.bigdata.taurus.agent.spring.JarExecutor;
import com.dp.bigdata.taurus.agent.utils.AgentServerHelper;
import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.HeartbeatInfo;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.Inject;

public class TaurusAgentServer implements AgentServer{
    private static final Log LOGGER = LogFactory.getLog(TaurusAgentServer.class);

	private static final int CHECK_INTERVALS = 30*1000;

	private String localIp;
	private DeploymentInfoChannel deployer;
	private ScheduleInfoChannel schedule;

	private int interval = CHECK_INTERVALS;
	private Executor executor;
	
	@Inject
	public TaurusAgentServer(DeploymentInfoChannel deployer,ScheduleInfoChannel schedule, Executor executor, int interval){
	    this.deployer = deployer;
		this.schedule = schedule;
		this.executor = executor;

		localIp = AgentServerHelper.getLocalIp();
		if(interval > 0){
			this.interval = interval;
		}
	}

	public void start(){
	    HeartbeatInfo info = new HeartbeatInfo();
        info.setTime(new Date());
        info.setAgentVersion(BaseEnvManager.AGENT_VERSION);
        info.setConfigs(BaseEnvManager.CONFIGS);
        info.setLinux(BaseEnvManager.ON_WINDOWS);
        info.setUser(BaseEnvManager.USER);
		deployer.connectToCluster(MachineType.AGENT, localIp);
		schedule.connectToCluster(MachineType.AGENT, localIp);
		
		schedule.updateHeartbeatInfo(MachineType.AGENT, localIp, info);
		
	    LOGGER.info("Taurus agent starts.");
		DeploymentUtility.checkAndDeployTasks(localIp, deployer,true);
		DeploymentUtility.checkAndUndeployTasks( localIp, deployer,true);
		ScheduleUtility.checkAndRunTasks(executor, localIp, schedule, true);
		ScheduleUtility.checkAndKillTasks(executor, localIp, schedule, true);
		ScheduleUtility.checkAndUpdate(executor, localIp, schedule,true);
		ScheduleUtility.startZombieThread(localIp, schedule);
		JarExecutor jarExecutor = new JarExecutor();
		jarExecutor.monitor();
		
		while(true){
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) { /*do nothing*/}
			DeploymentUtility.checkAndDeployTasks(localIp, deployer, false);
			DeploymentUtility.checkAndUndeployTasks(localIp, deployer, false);
			ScheduleUtility.checkAndRunTasks(executor, localIp, schedule, false);
			ScheduleUtility.checkAndKillTasks(executor, localIp, schedule, false);
			ScheduleUtility.checkAndUpdate(executor, localIp, schedule,false);
			schedule.updateRealtimeHeartbeatInfo(MachineType.AGENT, localIp);
		}
	}

}
