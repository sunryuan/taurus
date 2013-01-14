package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Request;
import org.restlet.data.MediaType;
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
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.TaskExample;
import com.dp.bigdata.taurus.restlet.resource.ITasksResource;
import com.dp.bigdata.taurus.restlet.shared.TaskDTO;
import com.dp.bigdata.taurus.restlet.utils.AgentDeploymentUtils;
import com.dp.bigdata.taurus.restlet.utils.DeployOptions;
import com.dp.bigdata.taurus.restlet.utils.FilePathManager;
import com.dp.bigdata.taurus.restlet.utils.HdfsUtils;
import com.dp.bigdata.taurus.restlet.utils.RequestExtrator;
import com.dp.bigdata.taurus.restlet.utils.TaskConverter;

/**
 * Resource url : http://xxx.xxx/api/task/
 * 
 * @author damon.zhu
 */
public class TasksResource extends ServerResource implements ITasksResource {

	private static final Log LOG = LogFactory.getLog(TasksResource.class);

    @Autowired
    private TaskMapper taskMapper;

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
		TaskExample example = new TaskExample();
        example.or().andStatusEqualTo(TaskStatus.RUNNING);
        example.or().andStatusEqualTo(TaskStatus.SUSPEND);
		example.or();
        List<Task> tasks = taskMapper.selectByExampleWithBLOBs(example);
		List<TaskDTO> result = new ArrayList<TaskDTO>();
		for (Task task : tasks) {
			TaskDTO dto = TaskConverter.toDto(task);
			result.add(dto);
		}
		return (ArrayList<TaskDTO>) result;
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
			LOG.error(e.getMessage() , e);
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
