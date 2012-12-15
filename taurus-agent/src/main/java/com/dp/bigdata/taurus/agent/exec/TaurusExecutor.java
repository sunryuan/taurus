package com.dp.bigdata.taurus.agent.exec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Singleton;

@Singleton
public class TaurusExecutor implements Executor{
	
	private static final Log LOG = LogFactory.getLog(TaurusExecutor.class);
	private Map<String,DefaultExecutor> executorMap = new HashMap<String,DefaultExecutor>();
	

	@Override
	public int execute(String id, long maxExecutionTime, Map env, OutputStream stdOut, OutputStream stdErr,
			String cmd) throws IOException {
		CommandLine commandLine = CommandLine.parse(cmd);
		return execute(id, maxExecutionTime, env, commandLine, stdOut, stdErr);
	}

	@Override
	public int execute(String id, long maxExecutionTime, Map env, OutputStream stdOut, OutputStream stdErr,
			String baseCmd, String... parameters) throws IOException {
		CommandLine commandLine = new CommandLine(baseCmd);
		for(String parameter : parameters){
			commandLine.addArgument(parameter);
		}
		return execute(id, maxExecutionTime, env, commandLine, stdOut, stdErr);
	}

	@Override
	public int execute(String id, OutputStream stdOut, OutputStream stdErr, String cmd)
			throws IOException {
		return execute(id, 0, null, stdOut, stdErr, cmd);
	}

	@Override
	public int execute(String id, OutputStream stdOut, OutputStream stdErr,
			String baseCmd, String... parameters) throws IOException{
		return execute(id, 0, null, stdOut, stdErr, baseCmd, parameters);
	}
	
	@Override
	public int execute(String id, long maxExecutionTime, Map env, CommandLine cmdLine, OutputStream stdOut, OutputStream stdErr) 
		throws IOException{
		DefaultExecutor executor = new DefaultExecutor();
		executor.setWatchdog(new ExecuteWatchdog(-1));
        LOG.debug("Ready to Execute " + id);
		if(id != null) {
			executorMap.put(id, executor);
		}
		executor.setExitValues(null);
		PumpStreamHandler streamHandler = new PumpStreamHandler(stdOut,stdErr);
		executor.setStreamHandler(streamHandler);
		if(maxExecutionTime > 0){
			executor.setWatchdog(new ExecuteWatchdog(maxExecutionTime));
		}
		LOG.debug("Command is "+ cmdLine);
		return executor.execute(cmdLine, env);
	}
	
	@Override
	public int kill(String id) {
		try{
		    LOG.debug("Ready to kill " + id);
			System.out.println(executorMap.get(id));
			System.out.println(executorMap.get(id).getWatchdog());
			executorMap.get(id).getWatchdog().destroyProcess();
		} catch(Exception e) {
			LOG.error(e,e);
			return 1;
		}
		return 0;
	}

}
