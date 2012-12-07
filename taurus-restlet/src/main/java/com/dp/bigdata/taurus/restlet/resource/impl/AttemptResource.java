package com.dp.bigdata.taurus.restlet.resource.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.core.ScheduleException;
import com.dp.bigdata.taurus.core.Scheduler;
import com.dp.bigdata.taurus.restlet.resource.IAttemptResource;

/**
 * Resource url : http://xxx.xxx/api/attempt/{attempt_id}
 * 
 * @author damon.zhu
 */
public class AttemptResource extends ServerResource implements IAttemptResource {

    private static final Log LOG = LogFactory.getLog(AttemptResource.class);

    @Autowired
    private Scheduler scheduler;

    @Override
    public void kill() {
        String attemptID = (String) getRequest().getAttributes().get("attempt_id");

        boolean isRunning = scheduler.isRuningAttempt(attemptID);
        
        if(!isRunning){
            setStatus(Status.CLIENT_ERROR_CONFLICT);
            return;
        }
        
        try {
            scheduler.killAttempt(attemptID);
        } catch (ScheduleException se) {
            LOG.error(se.getMessage(),se);
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }

    }

    @Override
    @Get
    public String log() {
        // TODO Auto-generated method stub
        return null;
    }

}
