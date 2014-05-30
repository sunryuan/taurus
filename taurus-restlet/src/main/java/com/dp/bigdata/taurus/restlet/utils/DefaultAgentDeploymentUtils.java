package com.dp.bigdata.taurus.restlet.utils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.HostMapper;
import com.dp.bigdata.taurus.generated.module.Host;
import com.dp.bigdata.taurus.generated.module.HostExample;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.Deployer;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.DeploymentContext;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.DeploymentException;

/**
 * DefaultAgentDeploymentUtils
 * 
 * @author damon.zhu
 */
public class DefaultAgentDeploymentUtils implements AgentDeploymentUtils {

    private static final Log LOG = LogFactory.getLog(DefaultAgentDeploymentUtils.class);

    private final long WAITTIME = 3000;

    @Autowired
    private HostMapper hostMapper;

    @Autowired
    private Deployer deployer;

    @SuppressWarnings("unused")
    @Autowired
    private FilePathManager filePathManager;

    @Override
    @Deprecated
    public void notifyAllAgent(final Task task, final DeployOptions options) throws DeploymentException {
        HostExample example = new HostExample();
        example.or().andPoolidEqualTo(task.getPoolid());
        List<Host> hosts = hostMapper.selectByExample(example);
        final DeploymentContext context = new DeploymentContext();
//        context.setHdfsPath(filePathManager.getRemotePath(task.getTaskid(), task.getFilename()));
//        context.setTaskID(task.getTaskid());
        if (hosts.size() <= 0) {
            throw new DeploymentException("No agent available to deploy task");
        }

        final CountDownLatch end = new CountDownLatch(hosts.size());
        final AtomicInteger isOK = new AtomicInteger(hosts.size());
        for (final Host host : hosts) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        switch (options) {
                            case DEPLOY:
                                deployer.deploy(host.getIp(), context);
                                break;
                            case UNDEPLOY:
                                deployer.undeploy(host.getIp(), context);
                                break;
                        }
                        isOK.decrementAndGet();
                        LOG.info("Successfully deploy task on agent " + host.getIp());
                    } catch (DeploymentException e) {
                        LOG.error("Failed to deploy task on agent " + host.getIp(), e);
                    } finally {
                        end.countDown();
                    }
                }
            });
            thread.run();
        }
        try {
            end.await(WAITTIME, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new DeploymentException("Agent deploy timeout!");
        }

        if (isOK.get() != 0) {
            throw new DeploymentException("Fail to deploy task onto all agents");
        }

    }
}
