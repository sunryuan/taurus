package com.dp.bigdata.taurus.zookeeper.heartbeat.helper;

/**
 * AgentHandler
 * 
 * @author damon.zhu
 */
public interface AgentHandler {

    public void disConnected(String ip);

    public void connected(String ip);

    public void update(String ip);
}
