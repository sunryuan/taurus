package com.dp.bigdata.taurus.restlet.resource;

import java.util.ArrayList;

import org.restlet.resource.Get;

import com.dp.bigdata.taurus.restlet.shared.UserDTO;

/**
 * IUersResource
 * 
 * @author damon.zhu
 */
public interface IUersResource {

    @Get
    public ArrayList<UserDTO> retrieve();

}
