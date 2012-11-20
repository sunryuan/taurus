package com.dp.bigdata.taurus.restlet.utils;

import java.util.Date;

import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.restlet.shared.TaskDTO;

/**
 * 
 * TaskConverter is a converter between Task and TaskDTO
 * @author damon.zhu
 *
 */
public class TaskConverter {

    public static Task toBean(TaskDTO dto, boolean isNew) {
        Task task = new Task();
        Date current = new Date();
        if (isNew) {
            task.setAddtime(current);
            task.setLastscheduletime(current);
            task.setUpdatetime(current);
        } else {
            task.setTaskid(dto.getTaskid());
            task.setLastscheduletime(dto.getLastscheduletime());
            task.setUpdatetime(dto.getUpdatetime());
        }
        task.setAllowmultiinstances(dto.getAllowmultiinstances());
        task.setCommand(dto.getCommand());
        task.setCreator(dto.getCreator());
        task.setCrontab(dto.getCrontab());
        task.setDependencyexpr(dto.getDependencyexpr());
        task.setExecutiontimeout(dto.getExecutiontimeout());
        task.setIsautoretry(dto.getIsautoretry());
        task.setName(dto.getName());
        task.setPoolid(dto.getPoolid());
        task.setProxyuser(dto.getProxyuser());
        task.setType(dto.getType());
        task.setWaittimeout(dto.getWaittimeout());
        return task;
    }

    public static TaskDTO toDto(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskid(task.getTaskid());
        dto.setAddtime(task.getAddtime());
        dto.setLastscheduletime(task.getLastscheduletime());
        dto.setUpdatetime(task.getUpdatetime());
        dto.setAllowmultiinstances(task.getAllowmultiinstances());
        dto.setCommand(task.getCommand());
        dto.setCreator(task.getCreator());
        dto.setCrontab(task.getCrontab());
        dto.setDependencyexpr(task.getDependencyexpr());
        dto.setExecutiontimeout(task.getExecutiontimeout());
        dto.setIsautoretry(task.getIsautoretry());
        dto.setName(task.getName());
        dto.setPoolid(task.getPoolid());
        dto.setProxyuser(task.getProxyuser());
        dto.setType(task.getType());
        dto.setWaittimeout(task.getWaittimeout());
        return dto;
    }
}
