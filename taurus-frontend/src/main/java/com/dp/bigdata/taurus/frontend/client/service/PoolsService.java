package com.dp.bigdata.taurus.frontend.client.service;

import java.util.ArrayList;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Get;
import org.restlet.client.resource.Post;
import org.restlet.client.resource.Put;
import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.restlet.shared.PoolDTO;


public interface PoolsService extends ClientProxy {
	
	@Get
	public void retrieve(Result<ArrayList<PoolDTO>> callback);
	
	@Post
	public void create(PoolDTO t, Result<Void> callback);
	//public void update(Pool pool, Result<Boolean> callback);
	
	@Put
	public void update(PoolDTO pool, Result<Void> callback);
}
