package com.dp.bigdata.taurus.core;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * IDFactoryTest
 * 
 * @author damon.zhu
 */
public class IDFactoryTest extends AbstractDaoTest {

    @Autowired
    private IDFactory idFactory;

    @Test
    public void testNewTaskID() {
        String identity = "201210171442";
        idFactory.setIdentity(identity);
        assertEquals("task_201210171442_0001", idFactory.newTaskID());
        assertEquals("task_201210171442_0002", idFactory.newTaskID());
        assertEquals("task_201210171442_0003", idFactory.newTaskID());
    }

    @Test
    public void testNewInstanceID() {
        String taskID1 = "task_201210171442_0001";
        assertEquals("instance_201210171442_0001_0001", idFactory.newInstanceID(taskID1));
        assertEquals("instance_201210171442_0001_0002", idFactory.newInstanceID(taskID1));
        assertEquals("instance_201210171442_0001_0003", idFactory.newInstanceID(taskID1));
        String taskID2 = "task_201210171442_0002";
        assertEquals("instance_201210171442_0002_0001", idFactory.newInstanceID(taskID2));
        assertEquals("instance_201210171442_0002_0002", idFactory.newInstanceID(taskID2));
        assertEquals("instance_201210171442_0002_0003", idFactory.newInstanceID(taskID2));

    }

    @Test
    public void testNewAttemptID() {
        String instanceID1 = "instance_201210171442_0001_0001";
        assertEquals("attempt_201210171442_0001_0001_0001", idFactory.newAttemptID(instanceID1));
        assertEquals("attempt_201210171442_0001_0001_0002", idFactory.newAttemptID(instanceID1));
        assertEquals("attempt_201210171442_0001_0001_0003", idFactory.newAttemptID(instanceID1));
        String instanceID2 = "instance_201210171442_0002_0001";
        assertEquals("attempt_201210171442_0002_0001_0001", idFactory.newAttemptID(instanceID2));
        assertEquals("attempt_201210171442_0002_0001_0002", idFactory.newAttemptID(instanceID2));
        assertEquals("attempt_201210171442_0002_0001_0003", idFactory.newAttemptID(instanceID2));
    }

    @Override
    protected void loadData() {
        //do nothing
    }
}
