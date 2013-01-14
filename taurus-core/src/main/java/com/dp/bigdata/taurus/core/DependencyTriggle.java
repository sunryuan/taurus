package com.dp.bigdata.taurus.core;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.core.parser.DependencyParser;
import com.dp.bigdata.taurus.generated.mapper.TaskAttemptMapper;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.TaskAttempt;
import com.dp.bigdata.taurus.generated.module.TaskAttemptExample;
import com.mysql.jdbc.StringUtils;

/**
 * DependencyTriggle
 * 
 * @author damon.zhu
 */
public class DependencyTriggle implements Triggle {

    private static final Log LOG = LogFactory.getLog(DependencyTriggle.class);

    @Autowired
    private TaskAttemptMapper taskAttemptMapper;
    @Autowired
    private AttemptStatusCheck statusCheck;
    private final DependencyParser parser;
    private final Scheduler scheduler;

    @Autowired
    public DependencyTriggle(Scheduler scheduler) {
        this.scheduler = scheduler;
        parser = new DependencyParser();
    }

    @Override
    public void triggle() {
        TaskAttemptExample example = new TaskAttemptExample();
        example.or().andStatusEqualTo(AttemptStatus.INITIALIZED);
        example.or().andStatusEqualTo(AttemptStatus.DEPENDENCY_TIMEOUT);
        List<TaskAttempt> attempts = taskAttemptMapper.selectByExample(example);
        final Map<String, Task> tasks = scheduler.getAllRegistedTask();
        for (TaskAttempt attempt : attempts) {
            Task task = tasks.get(attempt.getTaskid());
            String expression = task.getDependencyexpr();
            boolean isDepencyFinish = false;
            boolean hasDependency = true;
            if (StringUtils.isNullOrEmpty(expression)) {
                //for those tasks who have no dependencies.
                hasDependency = false;
                isDepencyFinish = true;
            } else {
                try {
                    isDepencyFinish = parser.isDepdencySatisfied(expression, statusCheck);
                } catch (ParseException e) {
                    continue;
                }
            }

            /*
             * check whether pass the dependency-check phase
             */
            if (isDepencyFinish) {
                if (hasDependency) {
                    LOG.info("Attempt " + attempt.getAttemptid() + " has passed dependencies");
                } else {
                    LOG.info("Attempt " + attempt.getAttemptid() + " has no dependencies, execute it directly");
                }
                attempt.setStatus(AttemptStatus.DEPENDENCY_PASS);
                taskAttemptMapper.updateByPrimaryKeySelective(attempt);
            } else {
                /*
                 * check whether the attempt has expire the wait-time
                 */
                int timeout = task.getWaittimeout();
                Date start = attempt.getScheduletime();
                long now = System.currentTimeMillis();
                if (now > start.getTime() + 1000L * 60 * timeout && attempt.getStatus() != AttemptStatus.DEPENDENCY_TIMEOUT) {
                    LOG.info("Attempt " + attempt.getAttemptid() + " has dependency waiting timeout ");
                    //I do think dependency_fail status is unnecessary.
                    attempt.setStatus(AttemptStatus.DEPENDENCY_TIMEOUT);
                    attempt.setEndtime(new Date());
                    taskAttemptMapper.updateByPrimaryKeySelective(attempt);
                }
            }
        }
    }
}
