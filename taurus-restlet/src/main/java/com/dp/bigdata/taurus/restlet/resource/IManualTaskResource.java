package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

/**
 * 
 * IManualTaskResource is to start a task instantly
 * @author damon.zhu
 *
 */
public interface IManualTaskResource {

    @Get
    public void start();
    
    @Post
    public void resume();

    @Put
    public void suspend();
    
}
