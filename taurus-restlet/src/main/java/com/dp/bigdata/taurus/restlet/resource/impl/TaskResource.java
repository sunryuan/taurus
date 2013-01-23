package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.core.ScheduleException;
import com.dp.bigdata.taurus.core.Scheduler;
import com.dp.bigdata.taurus.core.TaskID;
import com.dp.bigdata.taurus.generated.mapper.AlertRuleMapper;
import com.dp.bigdata.taurus.generated.mapper.UserGroupMapper;
import com.dp.bigdata.taurus.generated.mapper.UserMapper;
import com.dp.bigdata.taurus.generated.module.AlertRule;
import com.dp.bigdata.taurus.generated.module.AlertRuleExample;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.User;
import com.dp.bigdata.taurus.generated.module.UserExample;
import com.dp.bigdata.taurus.generated.module.UserGroup;
import com.dp.bigdata.taurus.generated.module.UserGroupExample;
import com.dp.bigdata.taurus.restlet.resource.ITaskResource;
import com.dp.bigdata.taurus.restlet.shared.TaskDTO;
import com.dp.bigdata.taurus.restlet.utils.AgentDeploymentUtils;
import com.dp.bigdata.taurus.restlet.utils.DeployOptions;
import com.dp.bigdata.taurus.restlet.utils.FilePathManager;
import com.dp.bigdata.taurus.restlet.utils.HdfsUtils;
import com.dp.bigdata.taurus.restlet.utils.RequestExtrator;
import com.dp.bigdata.taurus.restlet.utils.TaskConverter;

/**
 * Resource url : http://xxx.xxx/api/task/{task_id}
 * 
 * @author damon.zhu
 */
public class TaskResource extends ServerResource implements ITaskResource {

    private static final Log LOG = LogFactory.getLog(TaskResource.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private AlertRuleMapper alertRuleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private HdfsUtils hdfsUtils;

    @Autowired
    private AgentDeploymentUtils agentDeployUtils;

    @Autowired
    private RequestExtrator<TaskDTO> requestExtractor;

    @Autowired
    private FilePathManager filePathManager;

    @Override
    public TaskDTO retrieve() {
        String taskID = (String) getRequestAttributes().get("task_id");
        TaskDTO dto = new TaskDTO();
        try {
            TaskID.forName(taskID);
        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            LOG.error(e.getMessage());
            return dto;
        }

        Task task = scheduler.getAllRegistedTask().get(taskID);
        if (task == null) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            LOG.info("Cannot find the task by taskID = " + taskID);
        } else {
            dto = TaskConverter.toDto(task);
            AlertRuleExample example = new AlertRuleExample();
            example.or().andJobidEqualTo(taskID);
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
                            userName.append(userList.get(0).getId());
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
        return dto;
    }

    @Override
    public void update(Representation re) {
        if (re == null) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        final TaskDTO task;
        Request req = getRequest();
        try {
            task = requestExtractor.extractTask(req, true);
            String taskID = (String) getRequestAttributes().get("task_id");
            task.setTaskid(taskID);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        if (MediaType.MULTIPART_FORM_DATA.equals(re.getMediaType(), true) && !StringUtils.isBlank(task.getFilename())) {
            final String srcPath = filePathManager.getLocalPath(task.getFilename());
            final String destPath = filePathManager.getRemotePath(task.getTaskid(), task.getFilename());
            try {
                hdfsUtils.removeFile(destPath);
                hdfsUtils.writeFile(srcPath, destPath);
                agentDeployUtils.notifyAllAgent(task.getTask(), DeployOptions.UNDEPLOY);
                agentDeployUtils.notifyAllAgent(task.getTask(), DeployOptions.DEPLOY);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                setStatus(Status.SERVER_ERROR_INTERNAL);
                return;
            }
        }
        try {
            scheduler.updateTask(task.getTask());
            AlertRuleExample example = new AlertRuleExample();
            example.or().andJobidEqualTo(task.getTaskid());
            List<AlertRule> rules = alertRuleMapper.selectByExample(example);
            if (rules != null && rules.size() == 1) {
                AlertRule updatedRule = task.getAlertRule();
                AlertRule originRule = rules.get(0);
                updatedRule.setId(originRule.getId());
                alertRuleMapper.updateByPrimaryKeySelective(updatedRule);
            }
            setStatus(Status.SUCCESS_CREATED);
        } catch (ScheduleException e) {
            LOG.error(e.getMessage(), e);
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }

    @Override
    public void remove() {
        String taskID = (String) getRequest().getAttributes().get("task_id");

        try {
            TaskID.forName(taskID);
        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        try {
            Task task = scheduler.getAllRegistedTask().get(taskID);
            if (task == null) {
                setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                return;
            }

            scheduler.unRegisterTask(taskID);
            AlertRuleExample example = new AlertRuleExample();
            example.or().andJobidEqualTo(taskID);
            alertRuleMapper.deleteByExample(example);
        } catch (ScheduleException e) {
            LOG.error(e.getMessage(), e);
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }
}
