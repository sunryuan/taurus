package com.dp.bigdata.taurus.agent;

public final class AgentEnvValue {

	private static final String AGENT_HOME = "taurusAgentPath";
	private static final String AGENT_JOB_PATH = "taurusJobPath";
	private static final String DEFAULT_JOB_PATH = "/data/app/taurus/jobs/";
	private static final String DEFAULT_AGENT_PATH = "/data/app/taurus";
	
	static String getAgentPath() {
		String path = System.getenv(AgentEnvValue.AGENT_HOME);
		if(path == null) {
			path = AgentEnvValue.DEFAULT_AGENT_PATH;
		}
		return path;
	}
	
	static String getJobPath() {
		String path = System.getenv(AgentEnvValue.AGENT_JOB_PATH);
		if(path == null) {
			path = AgentEnvValue.DEFAULT_JOB_PATH;
		}
		return path;
	}
}
