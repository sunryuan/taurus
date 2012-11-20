package com.dp.bigdata.taurus.restlet.utils;

import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.DeploymentException;

/**
 * 
 * AgentDeploymentUtils
 * @author damon.zhu
 *
 */
public interface AgentDeploymentUtils {

    /**
     * notify all agent to deploy or un-deploy a task
     * @param task
     * @param option
     * @throws DeploymentException
     */
    public void notifyAllAgent(final Task task, final DeployOptions option) throws DeploymentException;
    
}
