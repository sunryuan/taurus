package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;

/**
 * 
 * IAttemptResource
 * DELETE is to kill a attempt.
 * @author damon.zhu
 *
 */
public interface IAttemptResource {

    @Get
    public String log();

    @Delete
    public void kill();

}
