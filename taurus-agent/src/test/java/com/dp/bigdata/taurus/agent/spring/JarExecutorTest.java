package com.dp.bigdata.taurus.agent.spring;

import java.io.InputStream;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;

public class JarExecutorTest {

    private ZkClient zkClient;

    public JarExecutorTest() {
        Properties props = new Properties();
        try {
            InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(JarExecutor.ZKCONFIG);
            props.load(in);
            in.close();
            String connectString = props.getProperty("connectionString");
            int sessionTimeout = Integer.parseInt(props.getProperty("sessionTimeout"));
            zkClient = new ZkClient(connectString, sessionTimeout);

        } catch (Exception e) {
            throw new RuntimeException("Error initialize zookeeper client");
        }
    }

    public void executeAttempt(String attemptID, ScheduleConf conf, ScheduleStatus status) {
        String schedulePath = JarExecutor.SCHEDULE_PATH + "/" + attemptID;
        String scheduleNewPath = JarExecutor.SCHEDULE_NEW_PATH + "/" + attemptID;
        String scheduleConfPath = schedulePath + "/" + JarExecutor.CONF;
        String scheduleStatusPath = schedulePath + "/" + JarExecutor.STATUS;
        zkClient.createPersistent(schedulePath, true);
        zkClient.createPersistent(scheduleConfPath);
        zkClient.createPersistent(scheduleStatusPath);
        zkClient.writeData(scheduleConfPath, conf);
        zkClient.writeData(scheduleStatusPath, status);
        zkClient.createPersistent(scheduleNewPath, true);
    }

    //@Test
    public void cleanup(){
    	//System.out.println(JarExecutor.SCHEDULE_PATH + "/new");
    	zkClient.deleteRecursive(JarExecutor.BASE);
    }
    
    //@Test
    public void getData(){
    	String path = "/taurus/schedules/192.168.7.80/attempt_201303151700_0002_1693_0001/status";
    	ScheduleStatus conf = (ScheduleStatus)zkClient.readData(path);
    	System.out.println(conf.getStatus());
    }
   
   //@Test
    public void testAddTestData() {
        String attemptID = "attempt_6";
        ScheduleConf conf = new ScheduleConf();
        conf.setAttemptID(attemptID);
        conf.setCommand("com.dp.bigdata.taurus.test.springtask.TestJob task1 execute");
        conf.setPid("1234");
        conf.setTaskID("123456");
        conf.setTaskType("spring");
        conf.setUserName("hadoop");

        ScheduleStatus status = new ScheduleStatus();
        status.setStatus(6);

        executeAttempt(attemptID, conf, status);
    }

}
