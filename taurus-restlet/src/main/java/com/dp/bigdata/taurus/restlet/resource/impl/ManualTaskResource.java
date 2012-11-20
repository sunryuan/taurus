package com.dp.bigdata.taurus.restlet.resource.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Status;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.core.ScheduleException;
import com.dp.bigdata.taurus.core.Scheduler;
import com.dp.bigdata.taurus.core.TaskID;
import com.dp.bigdata.taurus.restlet.resource.IManualTaskResource;

/**
 * Resource url : http://xxx.xxx/api/manualtask/{task_id}
 * 
 * @author damon.zhu
 */
public class ManualTaskResource extends ServerResource implements IManualTaskResource {

    private static final Log LOG = LogFactory.getLog(AttemptResource.class);

    @Autowired
    private Scheduler scheduler;

    @Override
    @Post
    public void start() {
        String taskID = (String) getRequest().getAttributes().get("task_id");

        try {
            TaskID.forName(taskID);
        } catch (Exception e) {
            LOG.info(e.getMessage());
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        try {
            scheduler.executeTask(taskID, 0);
        } catch (ScheduleException se) {
            LOG.error(se.getMessage(), se);
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }

    }

}
