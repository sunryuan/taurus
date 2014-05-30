package com.dp.bigdata.taurus.restlet.utils;

import com.dp.bigdata.taurus.core.TaskStatus;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.restlet.shared.TaskDTO;

/**
 * 
 * TaskConverter is a converter between Task and TaskDTO
 * @author damon.zhu
 *
 */
public class TaskConverter {

    /*
     * public static Task toBean(TaskDTO dto, boolean isNew) { Task task = new Task(); Date current = new Date(); if (isNew) {
     * dto.setAddtime(current); dto.setLastscheduletime(current); dto.setUpdatetime(current); } else {
     * dto.setTaskid(dto.getTaskid()); dto.setLastscheduletime(dto.getLastscheduletime()); dto.setUpdatetime(dto.getUpdatetime()); }
     * task.setAllowmultiinstances(dto.getAllowmultiinstances()); task.setCommand(dto.getCommand());
     * task.setCreator(dto.getCreator()); task.setCrontab(dto.getCrontab().trim()); task.setDependencyexpr(dto.getDependencyexpr());
     * task.setExecutiontimeout(dto.getExecutiontimeout()); task.setIsautoretry(dto.getIsautoretry()); task.setName(dto.getName());
     * task.setPoolid(dto.getPoolid()); task.setProxyuser(dto.getProxyuser()); task.setType(dto.getType());
     * task.setWaittimeout(dto.getWaittimeout()); task.setRetrytimes(dto.getRetrytimes()); task.setHostname(dto.getHostname());
     * task.setDescription(dto.getDescription()); return dto.getTask(); }
     */

    public static TaskDTO toDto(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskid(task.getTaskid());
        dto.setAddtime(task.getAddtime());
        dto.setLastscheduletime(task.getLastscheduletime());
        dto.setUpdatetime(task.getUpdatetime());
        dto.setCommand(task.getCommand());
        dto.setCreator(task.getCreator());
        dto.setCrontab(task.getCrontab().substring(2));
        dto.setDependencyexpr(task.getDependencyexpr());
        dto.setExecutiontimeout(task.getExecutiontimeout());
        dto.setIsautoretry(task.getIsautoretry());
        dto.setAutoKill(task.getIsautokill());
        dto.setName(task.getName());
        dto.setPoolid(task.getPoolid());
        dto.setProxyuser(task.getProxyuser());
        dto.setType(task.getType());
        dto.setWaittimeout(task.getWaittimeout());
        
        if (task.getStatus() == TaskStatus.RUNNING) {
            dto.setStatus("SCHEDULED");
        } else if (task.getStatus() == TaskStatus.SUSPEND) {
            dto.setStatus("SUSPEND");
        } else {
            dto.setStatus("UNKNOW");
        }
        dto.setRetrytimes(task.getRetrytimes());
        dto.setHostname(task.getHostname());
        dto.setDescription(task.getDescription());
        dto.setHadoopName(task.getHadoopname());
        dto.setAppName(task.getAppname());
        return dto;
    }
}
