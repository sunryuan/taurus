package com.dp.bigdata.taurus.agent.spring;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.dp.bigdata.taurus.agent.TaskType;
import com.dp.bigdata.taurus.agent.utils.AgentEnvValue;
import com.dp.bigdata.taurus.agent.utils.IPUtils;
import com.dp.bigdata.taurus.framework.ApplicationContextProvider;
import com.dp.bigdata.taurus.framework.TaskBean;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleConf;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.ScheduleStatus;
import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;

/**
 * JarExecutor
 * 
 * @author damon.zhu
 */
public class JarExecutor {

    private static final Log LOG = LogFactory.getLog(JarExecutor.class);

    public static final String IP = IPUtils.getFirstNoLoopbackIP4Address();
    public static final String BASE = "/taurus";
    public static final String DEPLOY_PATH = BASE + "/assignments/" + IP;
    public static final String SCHEDULE_PATH = BASE + "/schedules/" + IP;
    public static final String DEPLOY_NEW_PATH = DEPLOY_PATH + "/new";
    public static final String DEPLOY_DELETE_PATH = DEPLOY_PATH + "/delete";
    public static final String SCHEDULE_NEW_PATH = SCHEDULE_PATH + "/new";
    public static final String SCHEDULE_DELETE_PATH = SCHEDULE_PATH + "/delete";
    public static final String CONF = "conf";
    public static final String STATUS = "status";
    public static final String ZKCONFIG = "zooKeeper.properties";
    public static String JobPath = "/data/app/taurus-agent/jobs";

    static {
        JobPath = AgentEnvValue.getValue(AgentEnvValue.JOB_PATH, JobPath);
    }

    private ZkClient zkClient;
    private ExecutorService threadPool;
    private ConcurrentHashMap<String, ApplicationContext> contextMap; //jarPackage -> ApplicationContext
    private ConcurrentHashMap<String, Future<Integer>> futureMap; // attemptID -> Future;

    public JarExecutor() {
        Properties props = new Properties();
        try {
            InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(ZKCONFIG);
            props.load(in);
            in.close();
            String connectString = props.getProperty("connectionString");
            int sessionTimeout = Integer.parseInt(props.getProperty("sessionTimeout"));
            zkClient = new ZkClient(connectString, sessionTimeout);
            threadPool = new ThreadPoolExecutor(2, 4, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            contextMap = new ConcurrentHashMap<String, ApplicationContext>();
            futureMap = new ConcurrentHashMap<String, Future<Integer>>();
        } catch (Exception e) {
            throw new RuntimeException("Error initialize zookeeper client");
        }
    }

    public void monitor() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    for (String attemptID : futureMap.keySet()) {
                        Future<Integer> future = futureMap.get(attemptID);
                        boolean isSuccess = true;
                        boolean isKill = false;
                        try {
                            future.get(1000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            isSuccess = false;
                            isKill = true;
                        } catch (ExecutionException e) {
                            LOG.error("fail to execute attempt : " + attemptID, e.getCause());
                            isSuccess = false;
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        } finally {
                            if (future.isDone()) {
                                futureMap.remove(attemptID);
                                String attemptPath = SCHEDULE_PATH + "/" + attemptID + "/" + STATUS;
                                Object object = zkClient.readData(attemptPath);
                                ScheduleStatus status = object instanceof ScheduleStatus ? (ScheduleStatus) object : null;
                                if (status != null) {
                                    if (isSuccess) {
                                        status.setStatus(ScheduleStatus.EXECUTE_SUCCESS);
                                        status.setReturnCode(0);
                                    } else if (isKill) {
                                        status.setStatus(ScheduleStatus.DELETE_SUCCESS);
                                        status.setReturnCode(1);
                                    } else {
                                        status.setStatus(ScheduleStatus.EXECUTE_FAILED);
                                        status.setReturnCode(1);
                                    }
                                    zkClient.writeData(attemptPath, status);
                                }
                            }
                        }

                    }

                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        zkClient.subscribeChildChanges(SCHEDULE_NEW_PATH, new IZkChildListener() {

            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                for (String child : currentChilds) {
                    Object object = zkClient.readData(SCHEDULE_PATH + "/" + child + "/" + CONF);
                    String attemptPath = SCHEDULE_PATH + "/" + child + "/" + STATUS;
                    if (object instanceof ScheduleConf) {
                        final ScheduleConf conf = (ScheduleConf) object;
                        if (conf.getTaskType().equalsIgnoreCase(TaskType.getString(TaskType.SPRING))) {
                            //delete zookeeper node first
                            zkClient.delete(SCHEDULE_NEW_PATH + "/" + child);
                            //update attempt status
                            Object object2 = zkClient.readData(attemptPath);
                            ScheduleStatus status = object2 instanceof ScheduleStatus ? (ScheduleStatus) object2 : null;
                            if (status != null) {
                                status.setStatus(ScheduleStatus.EXECUTING);
                                zkClient.writeData(attemptPath, status);
                            }
                            //execute bean in thread pool and get result update status
                            Callable<Integer> call = createCallable(conf);
                            Future<Integer> future = threadPool.submit(call);
                            futureMap.put(conf.getAttemptID(), future);
                        }
                    }
                }
            }

            public Callable<Integer> createCallable(final ScheduleConf conf) {
                Callable<Integer> call = new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        String command = conf.getCommand();
                        //command format: com.xxx.xxx.mainClass beanName method
                        String mainClass = "";
                        String beanName = "";
                        String beanMethod = "";

                        if (StringUtils.isNotBlank(command)) {
                            String[] commands = command.split(" ");
                            if (commands.length == 2) {
                                mainClass = commands[0];
                                beanName = commands[1];
                            } else if (commands.length == 3) {
                                mainClass = commands[0];
                                beanName = commands[1];
                                beanMethod = commands[2];
                            } else {
                                throw new Exception("Command is invalid!");
                            }
                        } else {
                            throw new Exception("Command is blank!");
                        }
                        String jarPackage = conf.getTaskID();
                        String jarPath = JobPath + "/" + jarPackage;
                        //download the necessary jar
                        if(!hasDownload(jarPath)){
                        	download(conf.getTaskUrl(),jarPath);
                        }
                        //loading the application context
                        ApplicationContext context = contextMap.get(jarPackage);
                        if (context == null) {
                            LOG.info("initial application context...");
                            JarClassLoader jcl = new JarClassLoader();
                            try {
                                URLClassLoader ucl = jcl.getClassLoader(jarPath);
                                Thread.currentThread().setContextClassLoader(ucl);
                                //load mainClass
                                Class<?> mainClaz = ucl.loadClass(mainClass);
                                String[] parameters = null;
                                mainClaz.getMethod("main", String[].class).invoke(null,(Object)parameters);
                                //get applicationContext
                    			Class<?> contextClaz = ucl.loadClass(ApplicationContextProvider.class.getName());
                    			Object bean = contextClaz.getDeclaredMethod("getApplicationContext").invoke(null);
                                context = (ApplicationContext) bean;
                                contextMap.put(jarPackage, context);
                            } catch (Exception e) {
                                throw new Exception("Fail to initialize applicationContext.",e);
                            }
                        }
                        //execute the target bean method
                        try {
                            Object bean = context.getBean(beanName);
                            if (bean instanceof TaskBean) {
                                TaskBean taskBean = (TaskBean) bean;
                                taskBean.execute();
                            } else {
                                Method beanMt = bean.getClass().getDeclaredMethod(beanMethod);
                                beanMt.invoke(bean);
                            }
                        } catch (Exception e) {
                            throw new Exception("Error to execute bean. ", e);
                        }
                        return 0;
                    }

                };

                return call;

            }
       
            public boolean hasDownload(String jarPath){
            	File file = new File(jarPath);
            	if(file.exists()){
            		File[] files = file.listFiles();
            		if(files != null && files.length != 0){
            			return true;
            		}else{
            			return false;
            		}
            	}else{
            		return false;
            	}
            }
            
            public void download(String url, String jarPath){
            	File file = new File(jarPath);
            	if(!file.exists()){
            		file.mkdirs();
            		JarDownloadUtil.downloadFromFTP(url, jarPath);
            	}
            }
        });

    }

    public static void main(String[] args) throws InterruptedException {
        JarExecutor executor = new JarExecutor();
        executor.monitor();

        while (true) {
            Thread.sleep(1000);
        }
    }

}
