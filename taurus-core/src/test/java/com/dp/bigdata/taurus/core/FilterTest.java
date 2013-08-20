package com.dp.bigdata.taurus.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.dp.bigdata.taurus.generated.mapper.TaskAttemptMapper;
import com.dp.bigdata.taurus.generated.mapper.TaskMapper;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.TaskAttempt;

public class FilterTest extends AbstractDaoTest{
    
    private final String TASKID1 = "task_201210171442_0001";
    private final String TASKID2 = "task_201210171442_0002";
    private final String TASKID3 = "task_201210171442_0003";
    
    @Autowired
    private Engine engine;
    
    @Autowired
    private IDFactory idFactory;
    
    @Autowired
    private TaskMapper taskMapper;
    
    @Autowired
    private TaskAttemptMapper attemptMapper;
    
    @Autowired
    @Qualifier("filter.maxConcurrency")
    private MaximumConcurrentTaskFilter filter2;
    
    private List<AttemptContext> contexts = new ArrayList<AttemptContext>();

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
        taskMapper.insertSelective(task3);

        
        /*
         * insert attempt
         */
        TaskAttempt attempt1 = new TaskAttempt();
        attempt1.setTaskid(TASKID1);
        String instanceID1 = idFactory.newInstanceID(TASKID1);
        attempt1.setInstanceid(instanceID1);
        attempt1.setAttemptid(idFactory.newAttemptID(instanceID1));
        attempt1.setStatus(AttemptStatus.RUNNING);
        attemptMapper.insertSelective(attempt1);
        
        TaskAttempt attempt2 = new TaskAttempt();
        attempt2.setTaskid(TASKID2);
        String instanceID2 = idFactory.newInstanceID(TASKID2);
        attempt2.setInstanceid(instanceID2);
        attempt2.setAttemptid(idFactory.newAttemptID(instanceID2));
        attempt2.setStatus(AttemptStatus.RUNNING);
        attemptMapper.insertSelective(attempt2);
        
        TaskAttempt attempt3 = new TaskAttempt();
        attempt3.setTaskid(TASKID3);
        String instanceID3 = idFactory.newInstanceID(TASKID3);
        attempt3.setInstanceid(instanceID3);
        attempt3.setAttemptid(idFactory.newAttemptID(instanceID3));
        attempt3.setStatus(AttemptStatus.RUNNING);
        attemptMapper.insertSelective(attempt3);
        
        /*
         * Engine load task
         */
        engine.load();
        engine.setMaxConcurrency(5);
        
        /*
         * set next to null
         */
        filter2.setNext(null);
        
        /*
         * load attemptcontext
         */
        
        TaskAttempt attempt4 = new TaskAttempt();
        attempt4.setTaskid(TASKID3);
        String instanceID4 = idFactory.newInstanceID(TASKID3);
        attempt4.setInstanceid(instanceID4);
        attempt4.setAttemptid(idFactory.newAttemptID(instanceID4));
        attempt4.setStatus(AttemptStatus.DEPENDENCY_PASS);
        contexts.add(new AttemptContext(attempt4 , task3));
        
        TaskAttempt attempt5 = new TaskAttempt();
        attempt5.setTaskid(TASKID3);
        String instanceID5 = idFactory.newInstanceID(TASKID3);
        attempt5.setInstanceid(instanceID5);
        attempt5.setAttemptid(idFactory.newAttemptID(instanceID5));
        attempt5.setStatus(AttemptStatus.DEPENDENCY_PASS);
        contexts.add(new AttemptContext(attempt5 , task3));
        
        TaskAttempt attempt6 = new TaskAttempt();
        attempt6.setTaskid(TASKID3);
        String instanceID6 = idFactory.newInstanceID(TASKID3);
        attempt6.setInstanceid(instanceID6);
        attempt6.setAttemptid(idFactory.newAttemptID(instanceID6));
        attempt6.setStatus(AttemptStatus.DEPENDENCY_PASS);
        contexts.add(new AttemptContext(attempt6 , task3));
        
        TaskAttempt attempt7 = new TaskAttempt();
        attempt7.setTaskid(TASKID1);
        String instanceID7 = idFactory.newInstanceID(TASKID1);
        attempt7.setInstanceid(instanceID7);
        attempt7.setAttemptid(idFactory.newAttemptID(instanceID7));
        attempt7.setStatus(AttemptStatus.DEPENDENCY_PASS);
        contexts.add(new AttemptContext(attempt7 , task1));
        
        TaskAttempt attempt8 = new TaskAttempt();
        attempt8.setTaskid(TASKID2);
        String instanceID8 = idFactory.newInstanceID(TASKID2);
        attempt8.setInstanceid(instanceID8);
        attempt8.setAttemptid(idFactory.newAttemptID(instanceID8));
        attempt8.setStatus(AttemptStatus.DEPENDENCY_PASS);
        contexts.add(new AttemptContext(attempt8 , task2));
    }
    
    
    @Test
    public void testFilter(){
        
        List<AttemptContext> _context = filter2.filter(contexts);
        assertEquals(2, _context.size());
        
        AttemptContext context0 = _context.get(0);
        assertEquals(TASKID3, context0.getTaskid());

        AttemptContext context1 = _context.get(1);
        assertEquals(TASKID3, context1.getTaskid());
    }

}
