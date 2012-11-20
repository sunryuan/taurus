package com.dp.bigdata.taurus.restlet.resource;

import java.util.ArrayList;

import org.restlet.resource.Get;
import org.restlet.resource.Put;

import com.dp.bigdata.taurus.restlet.shared.HostDTO;

/**
 * 
 * IHostResource
 * @author damon.zhu
 *
 */
public interface IHostResource {

    @Get
    public ArrayList<HostDTO> retrieve();
    
    @Put
    public void update(HostDTO host);
}
