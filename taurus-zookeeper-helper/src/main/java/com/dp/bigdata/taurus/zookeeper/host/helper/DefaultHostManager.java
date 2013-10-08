package com.dp.bigdata.taurus.zookeeper.host.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dp.bigdata.taurus.zookeeper.common.infochannel.guice.ScheduleInfoChanelModule;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.interfaces.ScheduleInfoChannel;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DefaultHostManager implements HostManager {
	private static final Log LOGGER = LogFactory.getLog(HostManager.class);

	private ScheduleInfoChannel dic;

	private static final int TIMEOUT = 60 * 1000;

	private static final int INTERVAL = 5 * 1000;

	public DefaultHostManager() {
		Injector injector = Guice
				.createInjector(new ScheduleInfoChanelModule());
		dic = injector.getInstance(ScheduleInfoChannel.class);
	}

	@Override
	public void operate(String op, List<String> ips) {
		List<String> ipList = new ArrayList<String>();
		Set<String> successList = new HashSet<String>();
		int timeWait = 0;
		for (String ip : ips) {
			try {
				if (!dic.newOperate(ip, op)) {
					LOGGER.error("Do operating " + op + " to ip " + ip
							+ " fail!");
					continue;
				}
				ipList.add(ip);
			} catch (Exception e) {
				LOGGER.error(ip + " is not legal!\n", e);
			}
		}
		while (true) {
			try {
				Thread.sleep(INTERVAL);
				timeWait += INTERVAL;
			} catch (InterruptedException e) {
				LOGGER.error(e, e);
				// continue
			}
			for (String ip : ips) {
				boolean isFinished = false;
				try {
					isFinished = !dic.operateCompleted(ip, op);
				} catch (Exception e) {
					LOGGER.error(e, e);
				}
				if (isFinished) {
					successList.add(ip);
					LOGGER.info(ip + " finish to " + op);
				}
			}
			if (ipList.size() == successList.size()) {
				LOGGER.info("All finish to " + op);
				break;
			}
			if (timeWait >= TIMEOUT) {
				LOGGER.info("Time out!");
				for (String ip : ipList) {
					if (!successList.contains(ip)) {
						LOGGER.info("Fail to " + op + " " + ip);
						try {
							dic.completeOperate(ip, op);
						} catch (Exception e) {
							LOGGER.error(e, e);
						}
					}
				}
				throw new RuntimeException("Fail to "  + op + " " + ips);
			}
		}
	}
}
