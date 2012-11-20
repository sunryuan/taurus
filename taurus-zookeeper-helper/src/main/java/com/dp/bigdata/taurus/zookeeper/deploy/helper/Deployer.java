package com.dp.bigdata.taurus.zookeeper.deploy.helper;

public interface Deployer {

    public void deploy(String agentIp, DeploymentContext context) throws DeploymentException;

    public void undeploy(String agentIp, DeploymentContext context) throws DeploymentException;

}
