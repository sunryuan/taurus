package com.dp.bigdata.taurus.restlet.resource;

import java.util.ArrayList;

import org.restlet.resource.Get;

import com.dp.bigdata.taurus.restlet.shared.HostDTO;

/**
 * 
 * IHostsResource
 * @author damon.zhu
 *
 */
public interface IHostsResource {

    @Get
    public ArrayList<HostDTO> retrieve();
    
}
