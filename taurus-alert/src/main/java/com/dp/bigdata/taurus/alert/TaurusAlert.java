package com.dp.bigdata.taurus.alert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.dianping.cat.Cat;
import com.dp.bigdata.taurus.core.AttemptStatus;
import com.dp.bigdata.taurus.generated.mapper.AlertRuleMapper;
import com.dp.bigdata.taurus.generated.mapper.TaskAttemptMapper;
import com.dp.bigdata.taurus.generated.mapper.TaskMapper;
import com.dp.bigdata.taurus.generated.mapper.UserGroupMappingMapper;
import com.dp.bigdata.taurus.generated.mapper.UserMapper;
import com.dp.bigdata.taurus.generated.module.AlertRule;
import com.dp.bigdata.taurus.generated.module.AlertRuleExample;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.TaskAttempt;
import com.dp.bigdata.taurus.generated.module.TaskAttemptExample;
import com.dp.bigdata.taurus.generated.module.User;
import com.dp.bigdata.taurus.generated.module.UserExample;
import com.dp.bigdata.taurus.generated.module.UserGroupMapping;
import com.dp.bigdata.taurus.generated.module.UserGroupMappingExample;

/**
 * TaurusAlert
 * 
 * @author damon.zhu
 */
public class TaurusAlert {

	private static final Log LOG = LogFactory.getLog(TaurusAlert.class);

	private static final int ALERT_INTERVAL = 5 * 1000;

	private static final int META_INTERVAL = 60 * 1000;

	private List<AlertRule> commonRules;

	private final AtomicBoolean isLoading = new AtomicBoolean(false);

	private Map<String, AlertRule> ruleMap;

	@Autowired
	private AlertRuleMapper rulesMapper;

	@Autowired
	private TaskAttemptMapper taskAttemptMapper;

	@Autowired
	private TaskMapper taskMapper;

	@Autowired
	private UserGroupMappingMapper userGroupMappingMapper;

	@Autowired
	private UserMapper userMapper;

	private Map<Integer, User> userMap;

	public class AlertThread implements Runnable {

		private Date m_lastNotifyTime;

		public AlertThread(Date now) {
			m_lastNotifyTime = now;
		}

		private void handle(TaskAttempt attempt) {
			for (AlertRule commonRule : commonRules) {
				ruleHandler(attempt, commonRule);
			}

			AlertRule rule = ruleMap.get(attempt.getTaskid());

			if (rule != null) {
				ruleHandler(attempt, rule);
			}
		}

		private void ruleHandler(TaskAttempt attempt, AlertRule rule) {
			Set<Integer> ids = new HashSet<Integer>();
			String[] whens = StringUtils.isBlank(rule.getConditions()) ? null
					: rule.getConditions().split(";");
			String[] userId = StringUtils.isBlank(rule.getUserid()) ? null
					: rule.getUserid().split(";");
			String[] groupId = StringUtils.isBlank(rule.getGroupid()) ? null
					: rule.getGroupid().split(";");

			if (whens == null) {
				return;
			}

			for (String when : whens) {
				if (when.equalsIgnoreCase(AttemptStatus
						.getInstanceRunState(attempt.getStatus()))) {
					LOG.info("Condition matched : " + when);
					if (userId != null) {
						for (String id : userId) {
							ids.add(Integer.parseInt(id));
						}
					}

					if (groupId != null) {
						for (String id : groupId) {
							UserGroupMappingExample ugm_example = new UserGroupMappingExample();
							ugm_example.or().andGroupidEqualTo(
									Integer.parseInt(id));
							List<UserGroupMapping> userGroupMappings = userGroupMappingMapper
									.selectByExample(ugm_example);
							for (UserGroupMapping userGroupMapping : userGroupMappings) {
								ids.add(userGroupMapping.getUserid());
							}
						}
					}
				}
			}

			// Send alert
			for (Integer id : ids) {
				User user = userMap.get(id);

				if (user != null) {
					if (rule.getHasmail()
							&& StringUtils.isNotBlank(user.getMail())) {
						sendMail(user.getMail(), attempt);
					}

					if (rule.getHassms()
							&& StringUtils.isNotBlank(user.getTel())) {
						sendSMS(user.getTel(), attempt);
					}
				} else {
					Cat.logError("Cannot find user id : " + id, null);
				}
			}
		}

		@Override
		public void run() {
			while (true) {
				while (isLoading.get()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						LOG.error("InterruptedException ", e);
					}
				}

				try {
					Date now = new Date();
					TaskAttemptExample example = new TaskAttemptExample();
					example.or()
							.andEndtimeGreaterThanOrEqualTo(m_lastNotifyTime)
							.andEndtimeLessThan(now);
					List<TaskAttempt> attempts = taskAttemptMapper
							.selectByExample(example);
					m_lastNotifyTime = now;
					if (attempts != null && attempts.size() == 0) {
						continue;
					}
					for (TaskAttempt at : attempts) {
						handle(at);
					}

					Thread.sleep(ALERT_INTERVAL);
				} catch (Throwable e) {
					LOG.error(e, e);
				}

				m_lastNotifyTime = new Date();
			}
		}

		private void sendMail(String mailTo, TaskAttempt attempt) {
			LOG.info("Send mail to " + mailTo);
			Task task = taskMapper.selectByPrimaryKey(attempt.getTaskid());
			StringBuilder sbMailContent = new StringBuilder();

			sbMailContent.append("<table>");
			sbMailContent.append("<tr>");
			sbMailContent.append("<td>任务名</td><td>" + task.getName() + "</td>");
			sbMailContent.append("</tr>");
			sbMailContent.append("<tr>");
			sbMailContent.append("<td>任务状态</td><td> "
					+ AttemptStatus.getInstanceRunState(attempt.getStatus())
					+ "</td>");
			sbMailContent.append("</tr>");
			sbMailContent.append("<tr>");
			sbMailContent.append("<td>日志查看</td><td>"
					+ "http://taurus.dp/attempts.do?id="
					+ attempt.getAttemptid() + "&action=view-log</td>");
			sbMailContent.append("</tr>");
			sbMailContent.append("</table>");

			try {
				sendMail(mailTo, sbMailContent.toString());
			} catch (Exception e) {
				LOG.error("fail to send mail to " + mailTo, e);
				Cat.logError(e);
			}
		}

		private void sendMail(String to, String content)
				throws MessagingException {
			MailInfo mail = new MailInfo();
			mail.setTo(to);
			mail.setContent(content);
			mail.setFormat("text/html");
			mail.setSubject("Taurus告警服务");
			MailHelper.sendMail(mail);
		}

		private void sendSMS(String tel, TaskAttempt attempt) {
			LOG.info("Send SMS to " + tel);
			Task task = taskMapper.selectByPrimaryKey(attempt.getTaskid());
			StringBuilder sbMailContent = new StringBuilder();

			sbMailContent.append("任务名： " + task.getName() + "</br>");
			sbMailContent.append("任务状态： "
					+ AttemptStatus.getInstanceRunState(attempt.getStatus())
					+ "</br>");

			try {
				Map<String, String> messageContent = new HashMap<String, String>();
				messageContent.put("body", sbMailContent.toString());

				//smsService.send(801, tel, messageContent);
			} catch (Exception e) {
				LOG.error("fail to send sms to " + tel, e);
				Cat.logError(e);
			}
		}
	}

	public class MetaDataUpdatedThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					isLoading.set(true);
					load();
					isLoading.set(false);
					Thread.sleep(META_INTERVAL);
				} catch (Throwable e) {
					Cat.logError(e);
					LOG.error(e, e);
				}
			}
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = new FileSystemXmlApplicationContext(
				"classpath:applicationContext.xml");
		TaurusAlert alert = (TaurusAlert) context.getBean("alert");
		try {
			if (args.length == 0) {
				alert.start(-1);
			} else if (args.length == 1) {
				alert.start(Integer.parseInt(args[0]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load() {
		ruleMap = new ConcurrentHashMap<String, AlertRule>();
		commonRules = new ArrayList<AlertRule>();
		userMap = new ConcurrentHashMap<Integer, User>();

		// load alert rules
		AlertRuleExample ruleExample = new AlertRuleExample();
		ruleExample.or();

		List<AlertRule> rules = rulesMapper.selectByExample(ruleExample);
		for (AlertRule ar : rules) {
			if (ar.getJobid().equals("*")) {
				commonRules.add(ar);
			} else {
				ruleMap.put(ar.getJobid(), ar);
			}
		}

		// load user
		UserExample userExample = new UserExample();
		userExample.or();

		List<User> users = userMapper.selectByExample(userExample);
		for (User user : users) {
			userMap.put(user.getId(), user);
		}
	}

	public void start(int interval) {
		Thread updated = new Thread(new MetaDataUpdatedThread());
		updated.setName("MetaDataThread");
		updated.start();

		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.MINUTE, interval);

		Thread alert = new Thread(new AlertThread(calendar.getTime()));
		alert.setName("AlertThread");
		alert.start();
	}

}
