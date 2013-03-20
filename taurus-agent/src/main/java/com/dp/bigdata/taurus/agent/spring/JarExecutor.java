package com.dp.bigdata.taurus.agent.spring;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.springframework.context.ApplicationContext;

import com.dp.bigdata.taurus.agent.TaskHelper;
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
	public static final String SCHEDULE_RUNNING_PATH = SCHEDULE_PATH
			+ "/running";
	public static final String SCHEDULE_NEW_PATH = SCHEDULE_PATH + "/new";
	public static final String SCHEDULE_DELETE_PATH = SCHEDULE_PATH + "/delete";
	public static final String CONF = "conf";
	public static final String STATUS = "status";
	public static final String ZKCONFIG = "zooKeeper.properties";
	public static String JobPath = "/data/app/taurus-agent/jobs";
	public static String LogPath = "/data/app/taurus-agent/logs";
	private static final String LOGNAME = "spring-task.log";
	private static TaskHelper taskLogUploader;

	static {
		JobPath = AgentEnvValue.getValue(AgentEnvValue.JOB_PATH, JobPath);
		LogPath = AgentEnvValue.getValue(AgentEnvValue.LOG_PATH, LogPath);
		taskLogUploader = new TaskHelper();
	}

	private ZkClient zkClient;
	private ExecutorService threadPool;
	private ConcurrentHashMap<String, Object> contextMap; // jarPackage
															// ->
															// ApplicationContext
	private ConcurrentHashMap<String, JarClassLoader> classloaderMap; // jarPackage
																		// ->
																		// applicationContextClazz
	private ConcurrentHashMap<String, Future<Integer>> futureMap; // attemptID
																	// ->
																	// Future;

	public JarExecutor() {
		Properties props = new Properties();
		try {
			InputStream in = ClassLoaderUtils.getDefaultClassLoader()
					.getResourceAsStream(ZKCONFIG);
			props.load(in);
			in.close();
			String connectString = props.getProperty("connectionString");
			int sessionTimeout = Integer.parseInt(props
					.getProperty("sessionTimeout"));
			zkClient = new ZkClient(connectString, sessionTimeout);
			threadPool = new ThreadPoolExecutor(2, 4, 1L, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>());
			contextMap = new ConcurrentHashMap<String, Object>();
			classloaderMap = new ConcurrentHashMap<String, JarClassLoader>();
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
						LOG.info("Scan the executing progress of attempt : "
								+ attemptID);
						Future<Integer> future = futureMap.get(attemptID);
						boolean isSuccess = true;
						boolean isKill = false;
						try {
							future.get(1000, TimeUnit.MILLISECONDS);
						} catch (InterruptedException e) {
							LOG.error("task is interrupted, it is possibly being killed.");
							isSuccess = false;
							isKill = true;
						} catch (ExecutionException e) {
							LOG.error("fail to execute attempt : " + attemptID,
									e.getCause());
							isSuccess = false;
						} catch (TimeoutException e) {
							LOG.error("task execution timeout : " + attemptID,
									e);
						} finally {
							SimpleDateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd");
							String date = format.format(new Date());
							if (future.isDone()) {
								int returnValue = 0;
								futureMap.remove(attemptID);
								// delete attempt in the running folder
								if (zkClient.exists(SCHEDULE_RUNNING_PATH + "/"
										+ attemptID)) {
									zkClient.delete(SCHEDULE_RUNNING_PATH + "/"
											+ attemptID);
								}
								String attemptPath = SCHEDULE_PATH + "/"
										+ attemptID + "/" + STATUS;
								Object object = zkClient.readData(attemptPath);
								ScheduleStatus status = object instanceof ScheduleStatus ? (ScheduleStatus) object
										: null;
								if (isSuccess) {
									status.setStatus(ScheduleStatus.EXECUTE_SUCCESS);
									status.setReturnCode(0);
									returnValue = 0;
								} else if (isKill) {
									// delete attempt in the delete folder
									if (zkClient.exists(SCHEDULE_DELETE_PATH
											+ "/" + attemptID)) {
										zkClient.delete(SCHEDULE_DELETE_PATH
												+ "/" + attemptID);
									}
									status.setStatus(ScheduleStatus.DELETE_SUCCESS);
									status.setReturnCode(1);
									returnValue = 1;
								} else {
									status.setStatus(ScheduleStatus.EXECUTE_FAILED);
									status.setReturnCode(1);
									returnValue = 1;
								}

								// add attempt in the date folder
								zkClient.createPersistent(SCHEDULE_PATH + "/"
										+ date + "/" + attemptID, true);
								zkClient.writeData(attemptPath, status);

								// upload log
								Object confObject = zkClient
										.readData(SCHEDULE_PATH + "/"
												+ attemptID + "/" + CONF);
								ScheduleConf conf = (ScheduleConf) confObject;
								String logPath = LogPath + "/"
										+ conf.getTaskID() + "/";
								try {
									taskLogUploader.uploadLog(returnValue,
											null, logPath + LOGNAME, logPath
													+ "tmp.log", attemptID
													+ ".html");
								} catch (IOException e) {
									LOG.error(
											"fail to Upload log onto hdfs for attempt : "
													+ attemptID, e);
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

		zkClient.subscribeChildChanges(SCHEDULE_NEW_PATH,
				new IZkChildListener() {

					@Override
					public void handleChildChange(String parentPath,
							List<String> currentChilds) throws Exception {
						for (String child : currentChilds) {
							LOG.info("detect new attempt : " + child);
							Object object = zkClient.readData(SCHEDULE_PATH
									+ "/" + child + "/" + CONF);
							String attemptPath = SCHEDULE_PATH + "/" + child
									+ "/" + STATUS;
							if (object instanceof ScheduleConf) {
								final ScheduleConf conf = (ScheduleConf) object;
								if (StringUtils.isNotBlank(conf.getTaskType())
										&& conf.getTaskType()
												.equalsIgnoreCase(
														TaskType.getString(TaskType.SPRING))) {
									// delete zookeeper node first
									zkClient.delete(SCHEDULE_NEW_PATH + "/"
											+ child);
									// update attempt status
									Object object2 = zkClient
											.readData(attemptPath);
									ScheduleStatus status = object2 instanceof ScheduleStatus ? (ScheduleStatus) object2
											: null;
									if (status != null) {
										status.setStatus(ScheduleStatus.EXECUTING);
										zkClient.writeData(attemptPath, status);
									}
									// execute bean in thread pool and get
									// result update status
									Callable<Integer> call = createCallable(conf);
									Future<Integer> future = threadPool
											.submit(call);
									futureMap.put(conf.getAttemptID(), future);
								}
							}
						}
					}

					public Callable<Integer> createCallable(
							final ScheduleConf conf) {
						Callable<Integer> call = new Callable<Integer>() {

							@Override
							public Integer call() throws Exception {
								String command = conf.getCommand();
								// command format: com.xxx.xxx.mainClass
								// beanName method
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
										throw new Exception(
												"Command is invalid!");
									}
								} else {
									throw new Exception("Command is blank!");
								}
								String jarPackage = conf.getTaskID();
								String jarPath = JobPath + "/" + jarPackage;
								// download the necessary jar
								if (!hasDownload(jarPath)) {
									download(conf.getTaskUrl(), jarPath);
								}
								// loading the application context
								Object context = contextMap.get(jarPackage);

								if (context == null) {
									JarClassLoader ucl = new JarClassLoader(
											JarClassLoader
													.getClassLoader(jarPath),
											JarExecutor.class.getClassLoader());
									try {
										Thread.currentThread()
												.setContextClassLoader(ucl);
										// load mainClass
										LOG.info("loading main class...");
										Class<?> mainClaz = ucl
												.loadClass(mainClass);
										String[] parameters = null;
										mainClaz.getMethod("main",
												String[].class).invoke(null,
												(Object) parameters);
										// load Logger
										Class<?> rootLoggerClass = ucl
												.loadClass("org.apache.log4j.Logger");
										Object rootLogger = rootLoggerClass
												.getMethod("getRootLogger")
												.invoke(null);
										System.out.println(rootLoggerClass
												.getClassLoader());
										System.out.println(rootLogger
												.getClass().getClassLoader());

										String fileName = LogPath + "/"
												+ jarPackage + "/" + LOGNAME;
										Class<?> layoutClazz0 = ucl
												.loadClass(Layout.class
														.getName());
										Class<?> layoutClazz = ucl
												.loadClass(PatternLayout.class
														.getName());
										Class<?> appenderClazz0 = ucl
												.loadClass(Appender.class
														.getName());
										Class<?> appenderClazz = ucl
												.loadClass(DailyRollingFileAppender.class
														.getName());

										Object layout = layoutClazz
												.getDeclaredConstructor(
														String.class)
												.newInstance(
														"%d %-5p [%c] %m%n");
										Object appender = appenderClazz
												.getDeclaredConstructor(
														layoutClazz0,
														String.class,
														String.class)
												.newInstance(layout, fileName,
														"yyyy-MM-dd");
										appenderClazz.getMethod("setAppend",
												boolean.class).invoke(appender,
												true);
										appenderClazz.getMethod("setName",
												String.class).invoke(appender,
												"taurus");

										rootLoggerClass.getMethod(
												"addAppender", appenderClazz0)
												.invoke(rootLogger, appender);

										// get applicationContext
										LOG.info("initial application context...");
										Class<?> contextClaz = ucl
												.loadClass(ApplicationContextProvider.class
														.getName());
										context = contextClaz
												.getDeclaredMethod(
														"getApplicationContext")
												.invoke(null);
										contextMap.put(jarPackage, context);
										classloaderMap.put(jarPackage, ucl);
									} catch (Exception e) {
										throw new Exception(
												"Fail to initialize jar.", e);
									}
								}
								// execute the target bean method
								try {
									JarClassLoader ucl = classloaderMap
											.get(jarPackage);
									Class<?> applicationContextClazz = ucl
											.loadClass(ApplicationContext.class
													.getName());
									Object bean = applicationContextClazz
											.getMethod("getBean", String.class)
											.invoke(context, beanName);
									if (StringUtils.isNotBlank(beanName)
											&& StringUtils.isBlank(beanMethod)) {
										LOG.info("start to invoke Taskbean.execute...");
										if (bean instanceof TaskBean) {
											TaskBean taskBean = (TaskBean) bean;
											taskBean.execute();
										} else {
											throw new Exception(
													"bean is not an instance of TaskBean.");
										}
									} else if (StringUtils.isNotBlank(beanName)
											&& StringUtils
													.isNotBlank(beanMethod)) {
										LOG.info("start to invoke bean's method...");
										Method beanMt = bean.getClass()
												.getDeclaredMethod(beanMethod);
										beanMt.invoke(bean);
									} else {
										throw new Exception(
												"bean name and bean method are not properly configured.");
									}
								} catch (Exception e) {
									throw e;
								}
								return 0;
							}

						};

						return call;

					}

					public boolean hasDownload(String jarPath) {
						File file = new File(jarPath);
						if (file.exists()) {
							File[] files = file.listFiles();
							if (files != null && files.length != 0) {
								return true;
							} else {
								return false;
							}
						} else {
							return false;
						}
					}

					public void download(String url, String jarPath) {
						File file = new File(jarPath);
						if (!file.exists()) {
							file.mkdirs();
							JarDownloadUtil.downloadFromFTP(url, jarPath);
						}
					}
				});

		zkClient.subscribeChildChanges(SCHEDULE_DELETE_PATH,
				new IZkChildListener() {

					@Override
					public void handleChildChange(String parentPath,
							List<String> currentChilds) throws Exception {
						for (String child : currentChilds) {
							ScheduleConf conf = (ScheduleConf) zkClient
									.readData(SCHEDULE_DELETE_PATH + "/"
											+ child + "/" + CONF);
							if (conf.getTaskType().equalsIgnoreCase(
									TaskType.SPRING.name())) {
								Future<Integer> futureAttempt = futureMap
										.get(child);
								if (!futureAttempt.isDone()) {
									futureAttempt.cancel(true);
								}
							}

						}

					}
				});
	}

}
