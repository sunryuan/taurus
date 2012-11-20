package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.TaskAttemptMapper;
import com.dp.bigdata.taurus.generated.module.TaskAttempt;
import com.dp.bigdata.taurus.generated.module.TaskAttemptExample;
import com.dp.bigdata.taurus.restlet.resource.IAttemptsResource;
import com.dp.bigdata.taurus.restlet.shared.AttemptDTO;

/**
 * Resource url : http://xxx.xxx/api/attempt?task_id={task_id}
 * 
 * @author damon.zhu
 */
public class AttemptsResource extends ServerResource implements IAttemptsResource {

    private static final String TASK = "task_id";

    @Autowired
    private TaskAttemptMapper taskAttemptMapper;

    @Override
    @Get
    public ArrayList<AttemptDTO> retrieve() {
        ArrayList<AttemptDTO> attempts = new ArrayList<AttemptDTO>();

        Form form = getRequest().getResourceRef().getQueryAsForm();
        if (form.size() != 1) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return attempts;
        }

        String parameterName = "";
        String taskID = "";
        for (Parameter parameter : form) {
            parameterName = parameter.getName();
            taskID = parameter.getValue();
        }
        if (!parameterName.equals(TASK)) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return attempts;
        }

        TaskAttemptExample example = new TaskAttemptExample();
        example.or().andTaskidEqualTo(taskID);
        List<TaskAttempt> ats = taskAttemptMapper.selectByExample(example);
        for (TaskAttempt at : ats) {
            AttemptDTO dto = new AttemptDTO();
            dto.setAttemptID(at.getAttemptid());
            if (at.getEndtime() != null) {
                dto.setEndTime(at.getEndtime());
            }
            if (at.getExechost() != null) {
                dto.setExecHost(at.getExechost());
            }
            dto.setInstanceID(at.getInstanceid());
            if (at.getReturnvalue() != null) {
                dto.setReturnValue(at.getReturnvalue());
            }
            dto.setScheduleTime(at.getScheduletime());
            if (at.getStarttime() != null) {
                dto.setStartTime(at.getStarttime());
            }
            dto.setStatus(at.getStatus());
            dto.setTaskID(at.getTaskid());
            attempts.add(dto);
        }
        return attempts;
    }

}
