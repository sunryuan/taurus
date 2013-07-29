/**
 * Project: taurus-zookeeper-helper
 * 
 * File Created at 2013-7-23
 * $Id$
 * 
 * Copyright 2013 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dp.bigdata.taurus.zookeeper.heartbeat.helper;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;

/**
 * PollingAgentMonitor monitor agent by checkout zookeeper heart beat termly
 * 
 * @author renyuan.sun
 */
public class PollingAgentMonitor implements AgentMonitor {

    private ZkClient zkClient;
    private List<String> lastAgents;
    protected static final long THIRTY_SECONDS = 30 * 1000;

    private static final String WATCH_PATH = "/taurus/heartbeats/agent/realtime";
    private static final String ZKCONFIG = "zooKeeper.properties";
    private static final long TIMEOUT = 5 * 60 * 1000;
    private static final Log LOGGER = LogFactory.getLog(PollingAgentMonitor.class);

    public PollingAgentMonitor() {
        Properties props = new Properties();
        try {
            InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(ZKCONFIG);
            props.load(in);
            in.close();
            String connectString = props.getProperty("connectionString");
            int sessionTimeout = Integer.parseInt(props.getProperty("sessionTimeout"));
            zkClient = new ZkClient(connectString, sessionTimeout);
        } catch (Exception e) {
            throw new RuntimeException("Error initialize zookeeper client");
        }
    }

    @Override
    public void agentMonitor(final AgentHandler handler) {
        lastAgents = handler.getConnectedFromDB();
        Thread monitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        checkAgentStatus(handler);
                        Thread.sleep(THIRTY_SECONDS);
                    } catch (InterruptedException e) {
                        LOGGER.error(e, e);
                    }
                }
            }
        });
        monitorThread.start();
    }

    private void checkAgentStatus(AgentHandler handler) {
        List<String> currentAgents = getConnectedHosts();
        for (String disConnectedIP : lastAgents) {
            boolean hasDisConnected = true;
            for (String tmp : currentAgents) {
                if (disConnectedIP.equals(tmp)) {
                    hasDisConnected = false;
                    break;
                }
            }
            if (hasDisConnected) {
                handler.disConnected(disConnectedIP);
            }
        }

        for (String connectedIP : currentAgents) {
            boolean hasConnectedIP = true;
            for (String tmp : lastAgents) {
                if (connectedIP.equals(tmp)) {
                    hasConnectedIP = false;
                    break;
                }
            }
            if (hasConnectedIP) {
                handler.connected(connectedIP);
            }
        }
        lastAgents = currentAgents;
    }

    private List<String> getConnectedHosts() {
        List<String> ips = zkClient.getChildren(WATCH_PATH);
        for (int i = 0; i < ips.size(); i++) {
            if (!isConnected(ips.get(i))) {
                ips.remove(i);
                i--;
            }
        }
        return ips;
    }

    private boolean isConnected(String ip) {
        Long time = zkClient.readData(WATCH_PATH + "/" + ip, true);
        long now = System.currentTimeMillis();
        if (time == null) {
            return false;
        }
        if (now - time > TIMEOUT)
            return false;
        else {
            return true;
        }
    }

}
