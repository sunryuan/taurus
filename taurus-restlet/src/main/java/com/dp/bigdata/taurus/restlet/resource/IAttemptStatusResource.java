package com.dp.bigdata.taurus.restlet.resource;

import java.util.ArrayList;

import org.restlet.resource.Get;

import com.dp.bigdata.taurus.restlet.shared.StatusDTO;

/**
 * IAttemptStatusResource
 * 
 * @author damon.zhu
 */
public interface IAttemptStatusResource {

    @Get
    public ArrayList<StatusDTO> retrieve();
}
