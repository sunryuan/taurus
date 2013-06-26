package com.dp.bigdata.taurus.agent.deploy;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.I0Itec.zkclient.IZkChildListener;

import com.dp.bigdata.taurus.agent.utils.AgentServerHelper;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.DeploymentInfoChannel;



public class DeploymentUtility {

	private static final Log LOGGER = LogFactory.getLog(DeploymentUtility.class);
	private static ExecutorService threadPool;
	
	static{
		threadPool = AgentServerHelper.createThreadPool(2, 4);
		
	}
	
	public static void checkAndUndeployTasks( String localIp, DeploymentInfoChannel cs, boolean addListener) {
		LOGGER.debug("Start checkAndUndeployTasks");
		if(addListener) {
            cs.setNewUndeployDirListen(new TaskUndeployListener(localIp, cs));
        }
		Set<String> currentNew = cs.getNewUnDeploymentTaskIds(localIp);
		for(String task: currentNew){
			Runnable undeploymentThread = new UndeploymentThread( localIp, cs, task);
			threadPool.submit(undeploymentThread);
		}
		LOGGER.debug("End checkAndUndeployTasks");
	}

	public static void checkAndDeployTasks(String localIp, DeploymentInfoChannel cs, boolean addListener) {
		LOGGER.debug("Start checkAndDeployTasks");
		if(addListener) {
		    cs.setNewDeployDirListen(new TaskDeployListener(localIp, cs));
		}
		Set<String> currentNew = cs.getNewDeploymentTaskIds(localIp);		
		for(String task: currentNew){
			Runnable deploymentThread = new DeploymentThread(localIp, cs, task);
			threadPool.submit(deploymentThread);
		}
		LOGGER.debug("End checkAndDeployTasks");
	}
	
	
		
		

	
	private static abstract class BaseTaskListener implements IZkChildListener{
		protected String localIp;
		protected DeploymentInfoChannel cs;

		private BaseTaskListener( String localIp, DeploymentInfoChannel cs){
			this.localIp = localIp;
			this.cs = cs;
		}
	}

	private static final class TaskDeployListener extends BaseTaskListener{
		private TaskDeployListener( String localIp, DeploymentInfoChannel cs){
			super(localIp, cs);
		}

        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            checkAndDeployTasks( localIp, cs, false);
        }
	}

	private static final class TaskUndeployListener extends BaseTaskListener {

		private TaskUndeployListener( String localIp, DeploymentInfoChannel cs){
			super( localIp, cs);
		}

        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            checkAndUndeployTasks( localIp, cs, false);
        }
	}


}
