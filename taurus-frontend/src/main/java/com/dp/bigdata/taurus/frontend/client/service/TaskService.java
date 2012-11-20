package com.dp.bigdata.taurus.frontend.client.service;

import java.util.ArrayList;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Get;
import org.restlet.client.resource.Put;
import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.restlet.shared.TaskDTO;


public interface TaskService extends ClientProxy {
	
	@Put
	public void update(TaskDTO t, Result<Void> callback);
	
	@Get
	public void retrieve(Result<ArrayList<TaskDTO>> callback);
}
