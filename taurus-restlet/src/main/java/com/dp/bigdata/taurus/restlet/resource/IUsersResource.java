package com.dp.bigdata.taurus.restlet.resource;

import java.util.ArrayList;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.dp.bigdata.taurus.restlet.shared.UserDTO;

/**
 * IUersResource
 * 
 * @author damon.zhu
 */
public interface IUsersResource {

    @Get
    public ArrayList<UserDTO> retrieve();
    
    @Post
    public void createIfNotExist(UserDTO user);

}
