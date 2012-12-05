package com.dp.bigdata.taurus.agent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;


public final class AgentEnvValue {
	
	private static final Log LOG = LogFactory.getLog(AgentEnvValue.class);

	static final String CONF = "agentConf.properties";
	static final String KEY_CHECK_INTERVALS = "checkIntervals";
	static final String AGENT_ROOT_PATH = "taurusAgentPath";
	static final String JOB_PATH = "taurusJobPath";
	static final String HADOOP_AUTHORITY = "hadoopAuthority";
	static final String WORMHOLE_COMMAND = "wormholeCommand";
	
	public static String getValue(String key){
		try{
			Properties props = new Properties();
			InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(CONF);
			props.load(in);
			String result = props.getProperty(key);
			in.close();
			return result;
		} catch(IOException e) {
			LOG.error(e.getMessage(),e);
			return null;
		}
	}
	
}
