package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.resource.Delete;

/**
 * 
 * IAttemptResource
 * DELETE is to kill a attempt.
 * @author damon.zhu
 *
 */
public interface IAttemptResource {

    @Delete
    public void remove();

}
