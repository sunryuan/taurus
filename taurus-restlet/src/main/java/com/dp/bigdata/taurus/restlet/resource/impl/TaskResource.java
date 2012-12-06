package com.dp.bigdata.taurus.restlet.resource.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.core.ScheduleException;
import com.dp.bigdata.taurus.core.Scheduler;
import com.dp.bigdata.taurus.core.TaskID;
import com.dp.bigdata.taurus.generated.module.Task;
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
    private HdfsUtils hdfsUtils;

    @Autowired
    private AgentDeploymentUtils agentDeployUtils;

    @Autowired
    private RequestExtrator<Task> requestExtractor;

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
        }
        return dto;
    }

    @Override
    public void update(Representation re) {
        if (re == null || MediaType.MULTIPART_FORM_DATA.equals(re.getMediaType(), false)) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        final Task task;
        try {
            task = requestExtractor.extractTask(getRequest(), true);
        } catch (Exception e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        final String srcPath = filePathManager.getLocalPath(task.getFilename());
        final String destPath = filePathManager.getRemotePath(task.getTaskid(), task.getFilename());
        try {
            hdfsUtils.removeFile(destPath); //TODO
            hdfsUtils.writeFile(srcPath, destPath);
            agentDeployUtils.notifyAllAgent(task, DeployOptions.UNDEPLOY);
            agentDeployUtils.notifyAllAgent(task, DeployOptions.DEPLOY);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return;
        }
        try {
            scheduler.updateTask(task);
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
        } catch (ScheduleException e) {
            LOG.error(e.getMessage(), e);
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }
}
