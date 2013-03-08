package com.dp.bigdata.taurus.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Watcher;

import com.dp.bigdata.taurus.agent.exec.Executor;
import com.dp.bigdata.taurus.agent.spring.JarClassLoader;
import com.dp.bigdata.taurus.agent.spring.JarExecutor;
import com.dp.bigdata.taurus.agent.utils.AgentServerHelper;
import com.dp.bigdata.taurus.zookeeper.common.MachineType;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.DefaultZKWatcher;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.Inject;

public class TaurusAgentServer implements AgentServer{
    private static final Log LOG = LogFactory.getLog(TaurusAgentServer.class);

	private static final int CHECK_INTERVALS = 30*1000;

	private String localIp;
	private DeploymentInfoChannel deployer;
	private ScheduleInfoChannel schedule;

	private int interval = CHECK_INTERVALS;
	private Executor executor;
	
	@Inject
	public TaurusAgentServer(DeploymentInfoChannel deployer,ScheduleInfoChannel schedule, Executor executor, int interval){
	    LOG.info("Starting..");
	    this.deployer = deployer;
		this.schedule = schedule;
		this.executor = executor;
		Watcher deployWatcher = new DefaultZKWatcher(deployer);
		deployer.registerWatcher(deployWatcher);
	    Watcher scheduleWatcher = new DefaultZKWatcher(schedule);
	    schedule.registerWatcher(scheduleWatcher);

		localIp = AgentServerHelper.getLocalIp();
		if(interval > 0){
			this.interval = interval;
		}
	}

	public void start(){
		deployer.connectToCluster(MachineType.AGENT, localIp);
		schedule.connectToCluster(MachineType.AGENT, localIp);
	    LOG.info("Taurus agent starts.");

		DeploymentUtility.checkAndDeployTasks(executor, localIp, deployer,true);
		DeploymentUtility.checkAndUndeployTasks(executor, localIp, deployer,true);
		ScheduleUtility.checkAndRunTasks(executor, localIp, schedule, true);
		ScheduleUtility.checkAndKillTasks(executor, localIp, schedule, true);
		
		JarExecutor jarExecutor = new JarExecutor();
		jarExecutor.monitor();
		
		while(true){
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) { /*do nothing*/}
			DeploymentUtility.checkAndDeployTasks(executor, localIp, deployer, false);
			DeploymentUtility.checkAndUndeployTasks(executor, localIp, deployer, false);
			ScheduleUtility.checkAndRunTasks(executor, localIp, schedule, false);
			ScheduleUtility.checkAndKillTasks(executor, localIp, schedule, false);
		}
	}
}
