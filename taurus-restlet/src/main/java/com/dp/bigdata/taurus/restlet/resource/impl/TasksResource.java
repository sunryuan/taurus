package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.core.ScheduleException;
import com.dp.bigdata.taurus.core.Scheduler;
import com.dp.bigdata.taurus.core.TaskStatus;
import com.dp.bigdata.taurus.generated.mapper.AlertRuleMapper;
import com.dp.bigdata.taurus.generated.mapper.TaskMapper;
import com.dp.bigdata.taurus.generated.mapper.UserGroupMapper;
import com.dp.bigdata.taurus.generated.mapper.UserGroupMappingMapper;
import com.dp.bigdata.taurus.generated.mapper.UserMapper;
import com.dp.bigdata.taurus.generated.module.AlertRule;
import com.dp.bigdata.taurus.generated.module.AlertRuleExample;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.TaskExample;
import com.dp.bigdata.taurus.generated.module.User;
import com.dp.bigdata.taurus.generated.module.UserExample;
import com.dp.bigdata.taurus.generated.module.UserGroup;
import com.dp.bigdata.taurus.generated.module.UserGroupExample;
import com.dp.bigdata.taurus.generated.module.UserGroupMapping;
import com.dp.bigdata.taurus.generated.module.UserGroupMappingExample;
import com.dp.bigdata.taurus.restlet.resource.ITasksResource;
import com.dp.bigdata.taurus.restlet.shared.TaskDTO;
import com.dp.bigdata.taurus.restlet.utils.AgentDeploymentUtils;
import com.dp.bigdata.taurus.restlet.utils.DeployOptions;
import com.dp.bigdata.taurus.restlet.utils.FilePathManager;
import com.dp.bigdata.taurus.restlet.utils.HdfsUtils;
import com.dp.bigdata.taurus.restlet.utils.RequestExtrator;
import com.dp.bigdata.taurus.restlet.utils.TaskConverter;

/**
 * Resource url : http://xxx.xxx/api/task?user={xxx}
 * 
 * @author damon.zhu
 */
public class TasksResource extends ServerResource implements ITasksResource {

	private static final Log LOG = LogFactory.getLog(TasksResource.class);

	private static final String USER_FILTER = "user";

	private static final String NAME_FILTER = "name";

	private static final String APP_NAME_FILTER = "appname";

	private static final int ADMIN_GOURP_ID = 1;

	@Autowired
	private TaskMapper taskMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserGroupMapper userGroupMapper;

	@Autowired
	private UserGroupMappingMapper userGroupMappingMapper;

	@Autowired
	private AlertRuleMapper alertRuleMapper;

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private HdfsUtils hdfsUtils;

	@Autowired
	private AgentDeploymentUtils agentDeployUtils;

	@Autowired
	private RequestExtrator<TaskDTO> requestExtractor;

	@Autowired
	private FilePathManager filePathManager;

	@Get
	@Override
	public ArrayList<TaskDTO> retrieve() {
		List<TaskDTO> result = new ArrayList<TaskDTO>();

		Form form = getRequest().getResourceRef().getQueryAsForm();
		String creatorName = null, taskName = null, appName = null;
		int userId = 0;

		for (Parameter parameter : form) {
			String parameterName = parameter.getName();
			if (parameterName.equals(USER_FILTER)) {
				creatorName = parameter.getValue();
			} else if (parameterName.equals(NAME_FILTER)) {
				taskName = parameter.getValue();
			} else if (parameterName.equals(APP_NAME_FILTER)) {
				appName = parameter.getValue();
			}
		}
		boolean isAdmin = false;

		if (StringUtils.isNotBlank(creatorName)) {
			UserExample uExample = new UserExample();
			uExample.or().andNameEqualTo(creatorName);
			List<User> queryUsers = userMapper.selectByExample(uExample);
			if (queryUsers != null && queryUsers.size() == 1) {
				User user = queryUsers.get(0);
				userId = user.getId();
				UserGroupMappingExample ugmExample = new UserGroupMappingExample();
				ugmExample.or().andUseridEqualTo(userId);
				List<UserGroupMapping> ugmapping = userGroupMappingMapper.selectByExample(ugmExample);
				if (ugmapping != null) {
					for (UserGroupMapping ugm : ugmapping) {
						if (ugm.getGroupid() == ADMIN_GOURP_ID) {
							isAdmin = true;
							break;
						}
					}
				} else {
					// do nothing
				}
			} else {
				return (ArrayList<TaskDTO>) result;
			}
		}

		TaskExample taskExample = new TaskExample();
		List<String> sameGroupUsers = getSameGroupUsers(userId, creatorName);
		if (StringUtils.isNotBlank(taskName)) {
			taskExample.createCriteria().andNameEqualTo(taskName);
		} else if (StringUtils.isNotBlank(appName)) {
			taskExample.createCriteria().andAppnameEqualTo(appName).andStatusEqualTo(TaskStatus.RUNNING);
			taskExample.or().andAppnameEqualTo(appName).andStatusEqualTo(TaskStatus.SUSPEND);
		} else if (!isAdmin && StringUtils.isNotBlank(creatorName)) {
			taskExample.or().andCreatorIn(sameGroupUsers).andStatusEqualTo(TaskStatus.RUNNING);
			taskExample.or().andCreatorIn(sameGroupUsers).andStatusEqualTo(TaskStatus.SUSPEND);
		} else {
			taskExample.or().andStatusEqualTo(TaskStatus.RUNNING);
			taskExample.or().andStatusEqualTo(TaskStatus.SUSPEND);
		}

		List<Task> tasks = taskMapper.selectByExample(taskExample);

		for (Task task : tasks) {
			TaskDTO dto = TaskConverter.toDto(task);
			result.add(dto);

			AlertRuleExample example = new AlertRuleExample();
			example.or().andJobidEqualTo(task.getTaskid());
			List<AlertRule> rules = alertRuleMapper.selectByExample(example);
			if (rules != null && rules.size() == 1) {
				AlertRule rule = rules.get(0);
				dto.setHasmail(rule.getHasmail());
				dto.setHassms(rule.getHassms());
				dto.setConditions(rule.getConditions().toUpperCase());
				String userID = rule.getUserid();
				if (StringUtils.isNotBlank(userID)) {
					String[] users = userID.split(";");
					StringBuilder userName = new StringBuilder();
					for (int i = 0; i < users.length; i++) {
						String user = users[i];
						UserExample userExample = new UserExample();
						userExample.or().andIdEqualTo(Integer.parseInt(user));
						List<User> userList = userMapper.selectByExample(userExample);
						if (userList != null && userList.size() == 1) {
							userName.append(userList.get(0).getName());
						}
						if (i < users.length - 1) {
							userName.append(";");
						}
					}
					dto.setUserid(userName.toString());
				}
				String groudID = rule.getGroupid();
				if (StringUtils.isNotBlank(groudID)) {
					String[] groups = groudID.split(";");
					StringBuilder groupName = new StringBuilder();
					for (int i = 0; i < groups.length; i++) {
						String group = groups[i];
						UserGroupExample groupExample = new UserGroupExample();
						groupExample.or().andIdEqualTo(Integer.parseInt(group));
						List<UserGroup> userGroups = userGroupMapper.selectByExample(groupExample);
						if (userGroups != null && userGroups.size() == 1) {
							groupName.append(userGroups.get(0).getGroupname());
						}
						if (i < groups.length - 1) {
							groupName.append(";");
						}
					}
					dto.setGroupid(groupName.toString());
				}
			}

		}
		return (ArrayList<TaskDTO>) result;
	}

	private List<String> getSameGroupUsers(int userID, String userName) {
		List<String> userNames = new ArrayList<String>();
		userNames.add(userName);
		UserGroupMappingExample example = new UserGroupMappingExample();
		example.or().andUseridEqualTo(userID);
		List<UserGroupMapping> queryMappings = userGroupMappingMapper.selectByExample(example);
		if (queryMappings.size() > 0) {
			int groupID = queryMappings.get(0).getGroupid();
			example = new UserGroupMappingExample();
			example.or().andGroupidEqualTo(groupID);
			queryMappings = userGroupMappingMapper.selectByExample(example);
			List<Integer> userIDs = new ArrayList<Integer>();
			for (UserGroupMapping mapping : queryMappings) {
				userIDs.add(mapping.getUserid());
			}
			UserExample userExample = new UserExample();
			userExample.or().andIdIn(userIDs);
			List<User> users = userMapper.selectByExample(userExample);
			for (User user : users) {
				userNames.add(user.getName());
			}
		}
		return userNames;
	}

	@Post
	@Override
	public void create(Representation re) {
		if (re == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return;
		}

		final TaskDTO task;
		Request req = getRequest();
		try {
			task = requestExtractor.extractTask(req, false);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return;
		}

		if (MediaType.MULTIPART_FORM_DATA.equals(re.getMediaType(), true)) {
			final String srcPath = filePathManager.getLocalPath(task.getFilename());
			final String destPath = filePathManager.getRemotePath(task.getTaskid(), task.getFilename());
			try {
				hdfsUtils.writeFile(srcPath, destPath);
				agentDeployUtils.notifyAllAgent(task.getTask(), DeployOptions.DEPLOY);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				setStatus(Status.SERVER_ERROR_INTERNAL);
				return;
			}
		}
		try {
			scheduler.registerTask(task.getTask());
			alertRuleMapper.insertSelective(task.getAlertRule());
			setStatus(Status.SUCCESS_CREATED);
		} catch (ScheduleException e) {
			LOG.error(e.getMessage(), e);
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
	}

}
