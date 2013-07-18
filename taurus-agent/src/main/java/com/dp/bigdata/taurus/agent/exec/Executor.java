package com.dp.bigdata.taurus.agent.exec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.exec.CommandLine;

public interface Executor {
	
	@SuppressWarnings("rawtypes")
    public int execute(String id, long maxExecutionTime, Map env, OutputStream stdOut, OutputStream stdErr, String cmd) 
		throws IOException;
	
	@SuppressWarnings("rawtypes")
    public int execute(String id, long maxExecutionTime, Map env, OutputStream stdOut, OutputStream stdErr, String baseCmd, String... parameters) 
		throws IOException;
	
	public int execute(String id, OutputStream stdOut, OutputStream stdErr, String cmd) 
		throws IOException;
	
	public int execute(String id, OutputStream stdOut, OutputStream stdErr, String baseCmd, String... parameters) 
		throws IOException;

	@SuppressWarnings("rawtypes")
    public int execute(String id, long maxExecutionTime, Map env, CommandLine cmdLine,
			OutputStream stdOut, OutputStream stdErr) throws IOException;
	
	public int kill(String id);
}
