package com.dp.bigdata.taurus.core;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.dp.bigdata.taurus.generated.mapper.TaskAttemptMapper;
import com.dp.bigdata.taurus.generated.mapper.TaskMapper;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.TaskAttempt;
import com.dp.bigdata.taurus.generated.module.TaskAttemptExample;
import com.dp.bigdata.taurus.generated.module.TaskExample;

/**
 * 
 * CrontabTriggleTest
 * @author damon.zhu
 *
 */
public class CrontabTriggleTest extends AbstractDaoTest{
    
    @Autowired
    @Qualifier("triggle.crontab")
    private CrontabTriggle crontabTriggle;
    
    @Autowired
    private Engine engine;
    
    @Autowired
    private TaskMapper taskMapper;
    
    @Autowired
    private TaskAttemptMapper attemptMapper;
    
    @Autowired
    private IDFactory idFactory;

    private final String TASKID1 = "task_201210171442_0001";
    private final String TASKID2 = "task_201210171442_0002";
    
    @Override
    protected void loadData() {
        /*
         * insert tasks
         */
        Task task1 = new Task();
        task1.setTaskid(TASKID1);
        task1.setName("test1");
        task1.setCrontab("0 10 19 * * ?");
        task1.setStatus(TaskStatus.RUNNING);
        Calendar cal = Calendar.getInstance();
        cal.set(2012, Calendar.OCTOBER, 14, 19, 9, 0);
        task1.setLastscheduletime(cal.getTime());
        task1.setUpdatetime(cal.getTime());
        taskMapper.insertSelective(task1);
        
        Task task2 = new Task();
        task2.setTaskid(TASKID2);
        task2.setName("test2");
        task2.setCrontab("0 19 10 * * ?");
        task2.setStatus(TaskStatus.RUNNING);
        Calendar ca2 = Calendar.getInstance();
        ca2.set(2012, Calendar.OCTOBER, 17, 10, 18, 0);
        task2.setLastscheduletime(ca2.getTime());
        task2.setUpdatetime(ca2.getTime());
        taskMapper.insertSelective(task2);
        
        engine.load();
    }
    
    @Test
    public void testLoadData(){
        TaskExample tae = new TaskExample();
        tae.or();
        List<Task> tasks = taskMapper.selectByExample(tae);
        assertEquals(2, tasks.size());
    }

    @Test
    public void testTriggleFirstTime(){
        /*
         * verify
         */
        Calendar cal = Calendar.getInstance();
        cal.set(2012, Calendar.OCTOBER, 19, 19, 10, 0);
        crontabTriggle.triggle(cal.getTime());
        TaskAttemptExample example = new TaskAttemptExample();
        example.or();
        List<TaskAttempt> attempts = attemptMapper.selectByExample(example);
        assertEquals(1, attempts.size());
        cleanTableTaskAttempt();
    }
    
    @Test
    public void testTriggleFromPrevious(){
        /*
         * insert previous task attempt
         */
        TaskAttempt attempt = new TaskAttempt();
        attempt.setTaskid(TASKID1);
        String instanceID = idFactory.newInstanceID(TASKID1);
        attempt.setInstanceid(instanceID);
        attempt.setAttemptid(idFactory.newAttemptID(instanceID));
        Calendar cal = Calendar.getInstance();
        cal.set(2012, Calendar.OCTOBER, 15, 19, 10, 0);
        attempt.setScheduletime(cal.getTime());
        attemptMapper.insert(attempt);
        
        /*
         * verify
         */
        Calendar now = Calendar.getInstance();
        now.set(2012, Calendar.OCTOBER, 19, 19, 11, 0);
        crontabTriggle.triggle(now.getTime());
        TaskAttemptExample example = new TaskAttemptExample();
        example.or();
        List<TaskAttempt> attempts = attemptMapper.selectByExample(example);
        assertEquals(5, attempts.size());
        cleanTableTaskAttempt();
    }
    
    @Test
    public void testTriggleFromLastScheduleTime(){
        /*
         * insert previous task attempt
         */
        TaskAttempt attempt = new TaskAttempt();
        attempt.setTaskid(TASKID2);
        String instanceID = idFactory.newInstanceID(TASKID2);
        attempt.setInstanceid(instanceID);
        attempt.setAttemptid(idFactory.newAttemptID(instanceID));
        Calendar cal = Calendar.getInstance();
        cal.set(2012, Calendar.OCTOBER, 15, 10, 19, 0);
        attempt.setScheduletime(cal.getTime());
        attemptMapper.insert(attempt);
        
        
        /*
         * verify
         */
        Calendar now = Calendar.getInstance();
        now.set(2012, Calendar.OCTOBER, 19, 10, 19, 0);
        crontabTriggle.triggle(now.getTime());
        TaskAttemptExample example = new TaskAttemptExample();
        example.or();
        List<TaskAttempt> attempts = attemptMapper.selectByExample(example);
        assertEquals(4, attempts.size());
        
        cleanTableTaskAttempt();
    }
    
    /*
     * clean the table TaurusTaskAttempt
     */
    private void cleanTableTaskAttempt(){
        TaskAttemptExample example = new TaskAttemptExample();
        example.or();
        attemptMapper.deleteByExample(example);
    }

}
