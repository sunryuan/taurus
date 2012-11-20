package com.dp.bigdata.taurus.frontend.client.service;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Delete;
import org.restlet.client.resource.Result;

public interface TemplateService extends ClientProxy {
	
	@Delete
	public void remove(Result<Void> callback);
}
