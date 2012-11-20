package com.dp.bigdata.taurus.frontend.client.service;


import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Result;
import org.restlet.client.resource.Delete;


public interface PoolService extends ClientProxy {
	
	@Delete
	public void remove(Result<Void> callback);
	
}