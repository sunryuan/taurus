package com.dp.bigdata.taurus.restlet.resource;

import java.util.ArrayList;

import org.restlet.resource.Get;

import com.dp.bigdata.taurus.restlet.shared.UserGroupDTO;

/**
 * IUserGroupsResource
 * 
 * @author damon.zhu
 */
public interface IUserGroupsResource {
    
    @Get
    public ArrayList<UserGroupDTO> retrieve();

}
