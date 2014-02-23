package com.dp.bigdata.taurus.core;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.dianping.cat.Cat;

/**
 * AttemptStatusMonitor is to update the TaskAttmpt status.
 * 
 * @author damon.zhu
 * @see Engine
 */
public class AttemptStatusMonitor implements Runnable {

	private static final Log LOG = LogFactory
			.getLog(AttemptStatusMonitor.class);

	private final AtomicBoolean isInterrupt = new AtomicBoolean(false);

	private final Scheduler scheduler;

	@Autowired
	public AttemptStatusMonitor(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public static void main(String args[]) {
		ApplicationContext context = new FileSystemXmlApplicationContext(
				"classpath:applicationContext.xml");
		Engine engine = (Engine) context.getBean("engine");
		Runnable runnable = new AttemptStatusMonitor(engine);
		Thread t = new Thread(runnable);
		t.start();
	}

	@Override
	public void run() {
		LOG.info("Starting to monitor attempts status");
		while (!isInterrupt.get()) {
			try {
				List<AttemptContext> runningAttempts = scheduler.getAllRunningAttempt();
				for (AttemptContext attempt : runningAttempts) {
					AttemptStatus sstatus = scheduler.getAttemptStatus(attempt.getAttemptid());
					int status = sstatus.getStatus();
					LOG.info("Current status for attempt " + attempt.getAttemptid() + " : " + status);
					
					switch (status) {
					case AttemptStatus.SUCCEEDED:
						attempt.getAttempt().setReturnvalue(sstatus.getReturnCode());
						scheduler.attemptSucceed(attempt.getAttemptid());
						break;
					case AttemptStatus.FAILED:
						attempt.getAttempt().setReturnvalue(sstatus.getReturnCode());
						scheduler.attemptFailed(attempt.getAttemptid());
						break;
					case AttemptStatus.RUNNING: {
						if (attempt.getStatus() != AttemptStatus.TIMEOUT) {
							int timeout = attempt.getExecutiontimeout();
							Date start = attempt.getStarttime();
							long now = System.currentTimeMillis();
							if (now > start.getTime() + timeout * 1000 * 60) {
								LOG.info("attempt " + attempt.getAttemptid()+ " executing timeout ");
								scheduler.attemptExpired(attempt.getAttemptid());
							}
						} else {
							try {
								if (attempt.getIsautokill()) {
									String taskID = attempt.getTaskid();
									String previousAttemptID = attempt.getAttemptid();
									String newFiredAttemptID = scheduler.getRecentFiredAttemptByTaskID(taskID);

									if (newFiredAttemptID != null && !newFiredAttemptID.equalsIgnoreCase(previousAttemptID)) {
										scheduler.killAttempt(previousAttemptID);
									}
								}
							} catch (ScheduleException e) {
								Cat.logError(e);
							}
						}
						break;
					}
					case AttemptStatus.UNKNOWN: {
						scheduler.attemptUnKnowed(attempt.getAttemptid());
					}
					}
				}
				Thread.sleep(Engine.SCHDUELE_INTERVAL);

			} catch (Exception ie) {
				LOG.error(ie);

				Cat.logError(ie);
			}
		}
	}

	public void isInterrupt(boolean interrupt) {
		isInterrupt.compareAndSet(false, true);
	}
}
