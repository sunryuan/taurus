package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.core.AttemptStatus;
import com.dp.bigdata.taurus.generated.mapper.TaskAttemptMapper;
import com.dp.bigdata.taurus.generated.module.TaskAttempt;
import com.dp.bigdata.taurus.generated.module.TaskAttemptExample;
import com.dp.bigdata.taurus.restlet.resource.IAttemptsResource;
import com.dp.bigdata.taurus.restlet.shared.AttemptDTO;

/**
 * Resource url : http://xxx.xxx/api/attempt?task_id={task_id}&index={index}&pageSize={pageSize}
 * 
 * @author damon.zhu
 */
public class AttemptsResource extends ServerResource implements IAttemptsResource {

    private static final String TASK = "task_id";
    private static final String INDEX = "index";
    private static final String PAGESIZE = "pageSize";

    private static final int DEFAULT_PAGE_SIZE = 500;

    ArrayList<AttemptDTO> attempts = new ArrayList<AttemptDTO>();

    @Autowired
    private TaskAttemptMapper taskAttemptMapper;

    @Override
    @Get
    public ArrayList<AttemptDTO> retrieve() {
        Form form = getRequest().getResourceRef().getQueryAsForm();
        String parameterName = "";
        String taskID = "";
        int index = 0;
        int pageSize = DEFAULT_PAGE_SIZE;
        for (Parameter parameter : form) {
            parameterName = parameter.getName();
            if (parameterName.equals(TASK)) {
                taskID = parameter.getValue();
            } else if (parameterName.equals(INDEX)) {
                try {
                    index = Integer.parseInt(parameter.getValue());
                } catch (Exception e) {
                    setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    return attempts;
                }
            } else if (parameterName.equals(PAGESIZE)) {
                try {
                    pageSize = Integer.parseInt(parameter.getValue());
                } catch (Exception e) {
                    setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    return attempts;
                }
            } else {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return attempts;
            }
        }

        TaskAttemptExample example = new TaskAttemptExample();
        example.or().andTaskidEqualTo(taskID);
        String orderByClause = "scheduleTime desc limit " + index * pageSize + "," + pageSize;
        example.setOrderByClause(orderByClause);
        List<TaskAttempt> ats = taskAttemptMapper.selectByExample(example);
        toDtoList(ats);
        return attempts;
    }

    private void toDtoList(List<TaskAttempt> ats) {
        for (int i = 0; i < ats.size(); i++) {
            TaskAttempt at = ats.get(i);
            AttemptDTO dto = new AttemptDTO();
            dto.setId(i);
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
            dto.setStatus(AttemptStatus.getInstanceRunState(at.getStatus()));
            dto.setTaskID(at.getTaskid());
            attempts.add(dto);
        }
    }

}
