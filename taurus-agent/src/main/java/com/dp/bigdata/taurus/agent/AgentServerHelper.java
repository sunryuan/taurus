package com.dp.bigdata.taurus.agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dp.bigdata.taurus.zookeeper.common.utils.IPUtils;


public class AgentServerHelper {
	
	public static String getLocalIp(){
		return IPUtils.getFirstNoLoopbackIP4Address();
	}
	
	public static ExecutorService createThreadPool(int corePoolSize, int maxPoolSize){
		return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 1L,TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
	}
}
