package com.dp.bigdata.taurus.restlet.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang.StringUtils;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.core.AttemptStatus;
import com.dp.bigdata.taurus.core.CronExpression;
import com.dp.bigdata.taurus.core.IDFactory;
import com.dp.bigdata.taurus.core.TaskStatus;
import com.dp.bigdata.taurus.core.parser.DependencyParser;
import com.dp.bigdata.taurus.generated.mapper.UserGroupMapper;
import com.dp.bigdata.taurus.generated.mapper.UserMapper;
import com.dp.bigdata.taurus.generated.module.User;
import com.dp.bigdata.taurus.generated.module.UserExample;
import com.dp.bigdata.taurus.generated.module.UserGroup;
import com.dp.bigdata.taurus.generated.module.UserGroupExample;
import com.dp.bigdata.taurus.restlet.exception.DuplicatedNameException;
import com.dp.bigdata.taurus.restlet.exception.InvalidArgumentException;
import com.dp.bigdata.taurus.restlet.resource.impl.NameResource;
import com.dp.bigdata.taurus.restlet.shared.TaskDTO;
import com.dp.bigdata.taurus.restlet.shared.TaskDetailControlName;

/**
 * TaskRequestExtractor
 * 
 * @author damon.zhu
 */
public class TaskRequestExtractor implements RequestExtrator<TaskDTO> {

	private static final String MAIL_ONLY = "1";

	private static final String SMS_ONLY = "2";

	private static final String MAIL_AND_SMS = "3";

	@Autowired
	private IDFactory idFactory;

	@Autowired
	private PoolManager poolManager;

	@Autowired
	private FilePathManager filePathManager;

	@Autowired
	private NameResource nameResource;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserGroupMapper userGroupMapper;

	@Override
	public TaskDTO extractTask(Request request, boolean isUpdateAction) throws Exception {
		TaskDTO task = new TaskDTO();
		Date current = new Date();
		if (!isUpdateAction) {
			task.setAddtime(current);
			task.setLastscheduletime(current);
			String id = idFactory.newTaskID();
			task.setTaskid(id);
			task.setStatus(TaskStatus.getTaskRunState(TaskStatus.RUNNING));
		}
		task.setUpdatetime(current);
		Map<String, String> formMap;
		Representation re = request.getEntity();
		if (MediaType.MULTIPART_FORM_DATA.equals(re.getMediaType(), true)) {
			formMap = new HashMap<String, String>();
			List<FileItem> items = getFileItem(request);
			for (final Iterator<FileItem> it = items.iterator(); it.hasNext();) {
				FileItem fi = it.next();
				if (fi.isFormField()) {
					formMap.put(fi.getFieldName(), fi.getString("UTF-8"));
				} else {
					if (StringUtils.isNotEmpty(fi.getName()) && StringUtils.isNotBlank(fi.getName())) {
						String filePath = filePathManager.getLocalPath(fi.getName());
						task.setFilename(fi.getName());
						File file = new File(filePath);
						fi.write(file);
					} else {
						throw new FileNotFoundException("Task file not found!");
					}
				}
			}
		} else {
			Form form = new Form(re);
			formMap = new HashMap<String, String>(form.getValuesMap());
		}

		for (Entry<String, String> entry : formMap.entrySet()) {
			String key = entry.getKey();

			String value = entry.getValue() == null ? "" : entry.getValue().trim();
			if (key.equals(TaskDetailControlName.HADOOPNAME.getName())) {
				task.setHadoopName(value);
			} else if (key.equals(TaskDetailControlName.APPNAME.getName())) {
				task.setAppName(value);
			} else if (key.equals(TaskDetailControlName.TASKNAME.getName())) {
				task.setName(value);
			} else if (key.equals(TaskDetailControlName.TASKTYPE.getName())) {
				task.setType(value);
			} else if (key.equals(TaskDetailControlName.TASKPOOL.getName())) {
				int pid = poolManager.getID(value);
				task.setPoolid(pid);
			} else if (key.equals(TaskDetailControlName.TASKHOSTNAME.getName())) {
				if (StringUtils.isNotBlank(value)) {
					task.setHostname(value);
				}
			} else if (key.equals(TaskDetailControlName.TASKCOMMAND.getName())) {
				task.setCommand(value);
			} else if (key.equals(TaskDetailControlName.CRONTAB.getName())) {
				if (StringUtils.isNotBlank(value)) {
					task.setCrontab("0 " + value);
				}
			} else if (key.equals(TaskDetailControlName.DEPENDENCY.getName())) {
				task.setDependencyexpr(value);
			} else if (key.equals(TaskDetailControlName.PROXYUSER.getName())) {
				task.setProxyuser(value);
			} else if (key.equals(TaskDetailControlName.MAXEXECUTIONTIME.getName())) {
				task.setExecutiontimeout(Integer.parseInt(value));
			} else if (key.equals(TaskDetailControlName.MAXWAITTIME.getName())) {
				task.setWaittimeout(Integer.parseInt(value));
			} else if (key.equals(TaskDetailControlName.CREATOR.getName())) {
				task.setCreator(value);
			} else if (key.equals(TaskDetailControlName.DESCRIPTION.getName())) {
				task.setDescription(value);
			} else if (key.equals(TaskDetailControlName.ISAUTOKILL.getName())) {
				if (value.equals("1")) {
					task.setAutoKill(true);
				} else {
					task.setAutoKill(false);
				}
			} else if (key.equals(TaskDetailControlName.RETRYTIMES.getName())) {
				int retryNum = Integer.parseInt(value);
				task.setRetrytimes(retryNum);
				if (retryNum > 0) {
					task.setIsautoretry(true);
				} else {
					task.setIsautoretry(false);
				}
			} else if (key.equals(TaskDetailControlName.ALERTCONDITION.getName())) {
				if (StringUtils.isBlank(value)) {
					task.setConditions(AttemptStatus.getInstanceRunState(AttemptStatus.FAILED));
				} else {
					task.setConditions(value);
				}
			} else if (key.equals(TaskDetailControlName.ALERTGROUP.getName())) {
				if (StringUtils.isNotBlank(value)) {
					String[] groups = value.split(";");
					StringBuilder groupId = new StringBuilder();
					for (int i = 0; i < groups.length; i++) {
						String group = groups[i];

						if (group != null && group.length() > 0) {
							UserGroupExample example = new UserGroupExample();
							example.or().andGroupnameEqualTo(group);

							List<UserGroup> userGroups = userGroupMapper.selectByExample(example);

							if (userGroups != null && userGroups.size() == 1) {
								groupId.append(userGroups.get(0).getId());

								if (i < groups.length - 1) {
									groupId.append(";");
								}
							}
						}
					}
					task.setGroupid(groupId.toString());
				} else {
					task.setGroupid("");
				}
			} else if (key.equals(TaskDetailControlName.ALERTUSER.getName())) {
				if (StringUtils.isNotBlank(value)) {
					String[] users = value.split(";");
					StringBuilder userId = new StringBuilder();

					for (int i = 0; i < users.length; i++) {
						String user = users[i];
						if (user != null & user.length() > 0) {
							UserExample example = new UserExample();

							example.or().andNameEqualTo(user);
							List<User> userList = userMapper.selectByExample(example);

							if (userList != null && userList.size() == 1) {
								userId.append(userList.get(0).getId());
								if (i < users.length - 1) {
									userId.append(";");
								}
							}
						}
					}

					task.setUserid(userId.toString());
				} else {
					task.setUserid("");
				}
			} else if (key.equals(TaskDetailControlName.ALERTTYPE.getName())) {
				if (StringUtils.isNotBlank(value)) {
					if (value.equalsIgnoreCase(MAIL_ONLY)) {
						task.setHasmail(true);
					} else if (value.equalsIgnoreCase(SMS_ONLY)) {
						task.setHassms(true);
					} else if (value.equalsIgnoreCase(MAIL_AND_SMS)) {
						task.setHasmail(true);
						task.setHassms(true);
					} else {
						task.setHasmail(true);
					}
				}
			} else if (key.equals(TaskDetailControlName.MAINCLASS.getName())) {
				task.setMainClass(value);
			} else if (key.equals(TaskDetailControlName.TASKURL.getName())) {
				task.setTaskUrl(value);
			}
		}
		validate(task, isUpdateAction);
		return task;
	}

	private List<FileItem> getFileItem(Request request) throws FileUploadException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1000240);
		RestletFileUpload upload = new RestletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);
		return items;
	}

	private void validate(TaskDTO task, boolean isUpdateAction) throws Exception {
		if (StringUtils.isBlank(task.getCreator())) {
			throw new InvalidArgumentException("Cannot get creator name from request");
		}

		if (StringUtils.isBlank(task.getUserid())) {
			String name = task.getCreator();
			UserExample example = new UserExample();
			example.or().andNameEqualTo(name);
			List<User> user = userMapper.selectByExample(example);
			if (user == null || user.size() != 1) {
				throw new InvalidArgumentException("Cannot get mail user from request");
			} else {
				User u = user.get(0);
				task.setUserid(u.getId().toString());
			}
		}

		if (StringUtils.isBlank(task.getProxyuser())) {
			throw new InvalidArgumentException("Cannot get proxy user from request");
		}

		if (StringUtils.isNotBlank(task.getDependencyexpr())) {
			if (!DependencyParser.isValidateExpression(task.getDependencyexpr())) {
				throw new InvalidArgumentException("Invalid dependency expression : " + task.getDependencyexpr());
			}
		}

		if (StringUtils.isBlank(task.getName())) {
			throw new InvalidArgumentException("Cannot get task name from request");
		}

		if (!isUpdateAction && nameResource.isExistTaskName(task.getName())) {
			throw new DuplicatedNameException("Duplicated Name : " + task.getName());
		}

		try {
			new CronExpression(task.getCrontab());
		} catch (Exception e) {
			throw e;
		}
	}

}
