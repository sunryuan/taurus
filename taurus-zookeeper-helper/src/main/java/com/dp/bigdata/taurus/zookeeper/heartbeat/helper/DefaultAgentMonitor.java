package com.dp.bigdata.taurus.zookeeper.heartbeat.helper;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;

/**
 * DefaultAgentMonitor
 * 
 * @author damon.zhu
 */
public class DefaultAgentMonitor implements AgentMonitor {

    private ZkClient zkClient;
    private static final String WARCH_PACH = "/taurus/heartbeats/agent/realtime";
    private List<String> lastAgentsIp;

    public DefaultAgentMonitor() {
        Properties props = new Properties();
        try {
            InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream("zooKeeper.properties");
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
        lastAgentsIp = zkClient.getChildren(WARCH_PACH);
        for (String ip : lastAgentsIp) {
            handler.update(ip);
        }

        zkClient.subscribeChildChanges(WARCH_PACH, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                for (String disConnectedIP : lastAgentsIp) {
                    boolean hasDisConnected = true;
                    for (String tmp : currentChilds) {
                        if (disConnectedIP.equals(tmp)) {
                            hasDisConnected = false;
                            break;
                        }
                    }
                    if (hasDisConnected) {
                        handler.disConnected(disConnectedIP);
                    }
                }
                
                for (String connectedIP : currentChilds) {
                    boolean hasConnectedIP = true;
                    for (String tmp : lastAgentsIp) {
                        if (connectedIP.equals(tmp)) {
                            hasConnectedIP = false;
                            break;
                        }
                    }
                    if (hasConnectedIP) {
                        handler.connected(connectedIP);
                    }
                }
                lastAgentsIp = currentChilds;
            }
        });
    }

}
