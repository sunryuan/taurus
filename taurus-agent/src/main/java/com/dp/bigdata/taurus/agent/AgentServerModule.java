package com.dp.bigdata.taurus.agent;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.agent.exec.Executor;
import com.dp.bigdata.taurus.agent.exec.TaurusExecutor;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.DeploymentInfoChannelModule;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.TaurusZKScheduleInfoChannel;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class AgentServerModule extends DeploymentInfoChannelModule{
	
	private static final Log LOG = LogFactory.getLog(AgentServerModule.class);
	
	private static final String CONF = "agentConf.properties";
	
	private static final String KEY_CHECK_INTERVALS = "checkIntervals";

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
			Properties props = new Properties();
			try {
				InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(CONF);
				props.load(in);
				in.close();
				int opTimeout = Integer.parseInt(props.getProperty(KEY_CHECK_INTERVALS));
				return new TaurusAgentServer(deployer,schedule, exec, opTimeout);
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
				return null;
			}
		}
		
	}

}
