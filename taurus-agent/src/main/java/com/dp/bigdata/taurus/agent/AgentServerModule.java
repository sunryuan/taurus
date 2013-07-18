package com.dp.bigdata.taurus.agent;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.agent.exec.Executor;
import com.dp.bigdata.taurus.agent.exec.TaurusExecutor;
import com.dp.bigdata.taurus.agent.utils.AgentEnvValue;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.TaurusZKScheduleInfoChannel;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.guice.DeploymentInfoChannelModule;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class AgentServerModule extends DeploymentInfoChannelModule{
	
	private static final Log LOGGER = LogFactory.getLog(AgentServerModule.class);

	@Override
	protected void configureOthers() {
		bind(Executor.class).to(TaurusExecutor.class);
		bind(ScheduleInfoChannel.class).to(TaurusZKScheduleInfoChannel.class);
		bind(AgentServer.class).toProvider(AgentServerProvider.class);
	}
	
	private final static class AgentServerProvider implements Provider<AgentServer>{
		
		DeploymentInfoChannel deployer;
		ScheduleInfoChannel schedule;
		Executor exec;
		
		@Inject
		AgentServerProvider(DeploymentInfoChannel deployer, ScheduleInfoChannel schedule, Executor exec){
			this.deployer = deployer;
			this.schedule = schedule;
			this.exec = exec;
		}

		@Override
		public AgentServer get() {
			try {
				int opTimeout = Integer.parseInt(AgentEnvValue.getValue(AgentEnvValue.KEY_CHECK_INTERVALS));
				return new TaurusAgentServer(deployer,schedule, exec, opTimeout);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(),e);
				return null;
			}
		}
		
	}

}
