package com.dp.bigdata.taurus.agent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;


public final class AgentEnvValue {
	
	private static final Log LOG = LogFactory.getLog(AgentEnvValue.class);

	public static final String CONF = "agentConf.properties";
	public static final String KEY_CHECK_INTERVALS = "checkIntervals";
	public static final String AGENT_ROOT_PATH = "taurusAgentPath";
	public static final String JOB_PATH = "taurusJobPath";
	public static final String HADOOP_AUTHORITY = "hadoopAuthority";
	public static final String WORMHOLE_COMMAND = "wormholeCommand";
	
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
