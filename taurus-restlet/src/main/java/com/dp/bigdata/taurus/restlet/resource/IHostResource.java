package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import com.dp.bigdata.taurus.restlet.shared.HostDTO;

/**
 * 
 * IHostResource
 * 
 * @author damon.zhu
 * 
 */
public interface IHostResource {

	@Get
	public HostDTO retrieve();

	@Put
	public void update(HostDTO host);

	@Post
	public void operate(String op);
}
