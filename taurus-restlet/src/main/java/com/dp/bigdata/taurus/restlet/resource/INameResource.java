package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.resource.Get;

/**
 * INameResource
 * 
 * @author damon.zhu
 */
public interface INameResource {

    @Get
    public boolean hasName();
}
