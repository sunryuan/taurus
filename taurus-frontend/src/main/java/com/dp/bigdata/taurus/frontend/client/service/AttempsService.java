package com.dp.bigdata.taurus.frontend.client.service;

import java.util.ArrayList;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Get;
import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.restlet.shared.AttemptDTO;


public interface AttempsService extends ClientProxy {
	
	@Get
	public void retrieve(Result<ArrayList<AttemptDTO>> callback);

}
