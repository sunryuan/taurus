package com.dp.bigdata.taurus.restlet.resource.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.core.ScheduleException;
import com.dp.bigdata.taurus.core.Scheduler;
import com.dp.bigdata.taurus.restlet.resource.IAttemptResource;
import com.dp.bigdata.taurus.restlet.utils.FilePathManager;
import com.dp.bigdata.taurus.restlet.utils.HdfsUtils;

/**
 * Resource url : http://xxx.xxx/api/attempt/{attempt_id}
 * 
 * @author damon.zhu
 */
public class AttemptResource extends ServerResource implements IAttemptResource {

    private static final Log LOG = LogFactory.getLog(AttemptResource.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private FilePathManager pathManager;

    @Autowired
    private HdfsUtils hdfsUtils;

    @Override
    public void kill() {
        String attemptID = (String) getRequest().getAttributes().get("attempt_id");

        if (attemptID.split("_").length != 5) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        boolean isRunning = scheduler.isRuningAttempt(attemptID);

        if (!isRunning) {
            setStatus(Status.CLIENT_ERROR_CONFLICT);
            return;
        }

        try {
            scheduler.killAttempt(attemptID);
        } catch (ScheduleException se) {
            LOG.error(se.getMessage(), se);
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }

    }

    @Override
    @Get
    public FileRepresentation log() {
        //String attemptID = "attempt_201209241101_0009_0001_0001";
        String attemptID = (String) getRequest().getAttributes().get("attempt_id");
        String logPath = pathManager.getRemoteLog(attemptID);
        String localPath = pathManager.getLocalLogPath(attemptID);
        File file = new File(localPath);
        if (!file.exists()) {
            try {
                hdfsUtils.readFile(logPath, localPath);
            } catch (Exception e) {
                LOG.error("File not found", e);
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return null;
            }
        }
        return new FileRepresentation(file, MediaType.TEXT_HTML);
    }
}
