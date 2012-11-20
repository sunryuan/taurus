package com.dp.bigdata.taurus.frontend.client.service;

import java.util.ArrayList;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Get;
import org.restlet.client.resource.Result;
import org.restlet.client.resource.Post;

import com.dp.bigdata.taurus.restlet.shared.TaskDTO;


public interface TasksService extends ClientProxy {
	
	@Get
	public void retrieve(Result<ArrayList<TaskDTO>> callback);
	
	@Post
	public void create(TaskDTO task, Result<Void> callback);

}
