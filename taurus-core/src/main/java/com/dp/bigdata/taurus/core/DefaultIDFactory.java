package com.dp.bigdata.taurus.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.InstanceIDCounterMapper;
import com.dp.bigdata.taurus.generated.mapper.TaskIDCounterMapper;
import com.dp.bigdata.taurus.generated.module.InstanceIDCounter;
import com.dp.bigdata.taurus.generated.module.TaskIDCounter;

/**
 * @author damon.zhu
 */
public class DefaultIDFactory implements IDFactory {

    public static AtomicInteger taskCounter = new AtomicInteger(0);
    public static String IDENTITY = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());

    @Autowired
    private TaskIDCounterMapper taskIDMapper;
    @Autowired
    private InstanceIDCounterMapper instanceIDMapper;
    
    public void setIdentity(String identity) {
        IDENTITY = identity;
    }

    public synchronized String newTaskID() {
        return new TaskID(IDENTITY, taskCounter.incrementAndGet()).toString();
    }

    public String newInstanceID(String taskID) {
        return newInstanceID(TaskID.forName(taskID));
    }

    public synchronized String newInstanceID(TaskID taskID) {
        String taskid = taskID.toString();
        TaskIDCounter counter = taskIDMapper.selectByPrimaryKey(taskid);
        InstanceID instanceID;
        if (counter == null) {
            instanceID = new InstanceID(taskID, 1);
            TaskIDCounter record = new TaskIDCounter();
            record.setCounter(1);
            record.setTaskid(taskID.toString());
            taskIDMapper.insertSelective(record);
        } else {
            int c = counter.getCounter();
            c++;
            instanceID = new InstanceID(taskID, c);
            counter.setCounter(c);
            taskIDMapper.updateByPrimaryKey(counter);
        }
        return instanceID.toString();
    }

    public String newAttemptID(String instanceID) {
        return newAttemptID(InstanceID.forName(instanceID));
    }

    public synchronized String newAttemptID(InstanceID instanceID) {
        String instanceid = instanceID.toString();
        InstanceIDCounter counter = instanceIDMapper.selectByPrimaryKey(instanceid);
        AttemptID attemptID;
        if (counter == null) {
            attemptID = new AttemptID(instanceID, 1);
            InstanceIDCounter record = new InstanceIDCounter();
            record.setCounter(1);
            record.setInstanceid(instanceID.toString());
            instanceIDMapper.insert(record);
        } else {
            int c = counter.getCounter();
            c++;
            attemptID = new AttemptID(instanceID, c);
            counter.setCounter(c);
            instanceIDMapper.updateByPrimaryKey(counter);
        }
        return attemptID.toString();

    }
}
