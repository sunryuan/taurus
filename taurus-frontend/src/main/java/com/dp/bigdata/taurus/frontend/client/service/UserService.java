package com.dp.bigdata.taurus.frontend.client.service;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Post;
import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.restlet.shared.UserDTO;

public interface UserService extends ClientProxy {
	
	@Post
	public void logon(UserDTO user, Result<Void> callback);

}
