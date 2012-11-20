package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.resource.Post;

/**
 * 
 * IManualTaskResource is to start a task instantly
 * @author damon.zhu
 *
 */
public interface IManualTaskResource {

    @Post
    public void start();
}
