package com.dp.bigdata.taurus.core;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.dp.bigdata.taurus.generated.mapper.TaskAttemptMapper;
import com.dp.bigdata.taurus.generated.mapper.TaskMapper;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.TaskAttempt;

/**
 * DependencyTriggleTest
 * 
 * @author damon.zhu
 */
public class DependencyTriggleTest extends AbstractDaoTest {

    private final String DEPEX1 = "[test1][2][0]&[test2][2][1]";
    private final String TASKID1 = "task_201210171442_0001";
    private final String TASKID2 = "task_201210171442_0002";
    private final String TASKID3 = "task_201210171442_0003";

    @Qualifier("triggle.dependency")
    @Autowired
    private Triggle triggle;

    @Autowired
    private Engine engine;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskAttemptMapper attemptMapper;

    @Autowired
    private IDFactory idFactory;

    @Override
    protected void loadData() {

        /*
         * insert tasks
         */
        Task task1 = new Task();
        task1.setTaskid(TASKID1);
        task1.setName("test1");
        task1.setStatus(TaskStatus.RUNNING);
        taskMapper.insertSelective(task1);

        Task task2 = new Task();
        task2.setTaskid(TASKID2);
        task2.setName("test2");
        task2.setStatus(TaskStatus.RUNNING);
        taskMapper.insertSelective(task2);

        Task task3 = new Task();
        task3.setTaskid(TASKID3);
        task3.setName("test3");
        task3.setStatus(TaskStatus.RUNNING);
        task3.setDependencyexpr(DEPEX1);
        taskMapper.insertSelective(task3);

        /*
         * insert attempt
         */
        TaskAttempt attempt1 = new TaskAttempt();
        attempt1.setTaskid(TASKID1);
        String instanceID1 = idFactory.newInstanceID(TASKID1);
        attempt1.setInstanceid(instanceID1);
        attempt1.setAttemptid(idFactory.newAttemptID(instanceID1));
        attempt1.setReturnvalue(0);
        attempt1.setStatus(AttemptStatus.SUCCEEDED);
        attemptMapper.insertSelective(attempt1);
        
        TaskAttempt attempt2 = new TaskAttempt();
        attempt2.setTaskid(TASKID1);
        String instanceID2 = idFactory.newInstanceID(TASKID1);
        attempt2.setInstanceid(instanceID2);
        attempt2.setAttemptid(idFactory.newAttemptID(instanceID2));
        attempt2.setReturnvalue(0);
        attempt2.setStatus(AttemptStatus.INITIALIZED);
        attemptMapper.insertSelective(attempt2);
        
        TaskAttempt attempt3 = new TaskAttempt();
        attempt3.setTaskid(TASKID2);
        String instanceID3 = idFactory.newInstanceID(TASKID2);
        attempt3.setInstanceid(instanceID3);
        attempt3.setAttemptid(idFactory.newAttemptID(instanceID3));
        attempt3.setReturnvalue(1);
        attempt3.setStatus(AttemptStatus.SUCCEEDED);
        attemptMapper.insertSelective(attempt3);
        
        TaskAttempt attempt4 = new TaskAttempt();
        attempt4.setTaskid(TASKID2);
        String instanceID4 = idFactory.newInstanceID(TASKID2);
        attempt4.setInstanceid(instanceID4);        
        attempt4.setAttemptid(idFactory.newAttemptID(instanceID4));
        attempt4.setStatus(AttemptStatus.INITIALIZED);
        attemptMapper.insertSelective(attempt4);
        
        TaskAttempt attempt5 = new TaskAttempt();
        attempt5.setTaskid(TASKID3);
        String instanceID5 = idFactory.newInstanceID(TASKID3);
        attempt5.setInstanceid(instanceID5);
        attempt5.setAttemptid(idFactory.newAttemptID(instanceID5));
        attempt5.setStatus(AttemptStatus.INITIALIZED);
        attemptMapper.insertSelective(attempt5);
        
        /*
         * Engine load task
         */
        engine.load();
    }
    
    @Test
    public void testTriggle(){
        triggle.triggle();
        
        TaskAttempt attempt1 = attemptMapper.selectByPrimaryKey("attempt_201210171442_0001_0002_0001");
        assertEquals(AttemptStatus.DEPENDENCY_PASS, attempt1.getStatus().intValue());
        
        TaskAttempt attempt2 = attemptMapper.selectByPrimaryKey("attempt_201210171442_0002_0002_0001");
        assertEquals(AttemptStatus.DEPENDENCY_PASS, attempt2.getStatus().intValue());
        
        TaskAttempt attempt3 = attemptMapper.selectByPrimaryKey("attempt_201210171442_0003_0001_0001");
        assertEquals(AttemptStatus.DEPENDENCY_PASS, attempt3.getStatus().intValue());
    }

}
