package com.dp.bigdata.taurus.agent.common;

import java.io.File;

import com.dp.bigdata.taurus.agent.utils.AgentEnvValue;
import com.dp.bigdata.taurus.agent.utils.TaskHelper;

/**
 * 设置taurus agent需要的大部分环境变量
 * 
 * @author renyuan.sun
 */
public abstract class BaseEnvManager implements Runnable {
    public static String agentRoot = "/data/app/taurus-agent";
    public static String jobPath = "/data/app/taurus-agent/jobs";
    public static String logPath = "/data/app/taurus-agent/logs";
    public static String hadoopAuthority = "/script/hadoop-authority.sh";
    public static String logFileUpload = "/script/log-upload.sh";
    public static String killJob = "/script/kill-tree.sh";
    public static String env = "/script/agent-env.sh";
    public static String running = "/running";
    public static String hadoop = "/hadoop";
    public static String homeDir = "/home";
    public static boolean needSudoAuthority;

    public static final boolean ON_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    public static final String USER = System.getProperty("user.name");
    public static final String AGENT_VERSION = AgentEnvValue.getVersion();
    public static final String CONFIGS = AgentEnvValue.getConfigs();
    public static TaskHelper taskHelper = new TaskHelper();

    public static final String FILE_SEPRATOR = File.separator;

    static {
        agentRoot = AgentEnvValue.getValue(AgentEnvValue.AGENT_ROOT_PATH, agentRoot);
        jobPath = AgentEnvValue.getValue(AgentEnvValue.JOB_PATH, jobPath);
        logPath = AgentEnvValue.getValue(AgentEnvValue.LOG_PATH, logPath);
        hadoopAuthority = agentRoot + hadoopAuthority;
        logFileUpload = agentRoot + logFileUpload;
        killJob = agentRoot + killJob;
        running = jobPath + running;
        hadoop = jobPath + hadoop;
        env = agentRoot + env;
        homeDir = AgentEnvValue.getValue(AgentEnvValue.HOME_PATH, homeDir);
        needSudoAuthority = new Boolean(AgentEnvValue.getValue(AgentEnvValue.NEED_SUDO_AUTHORITY, "true"));
    }
}
