package com.dp.bigdata.taurus.restlet.resource;

import java.util.ArrayList;

import org.restlet.resource.Get;

import com.dp.bigdata.taurus.restlet.shared.UserGroupMappingDTO;

public interface IUserGroupMappingsResource {

    @Get
    public ArrayList<UserGroupMappingDTO> retrieve();
}
