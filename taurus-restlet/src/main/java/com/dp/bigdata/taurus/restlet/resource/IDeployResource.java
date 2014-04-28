package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * ITaskResource
 * 
 * @author renyuan.sun
 */
public interface IDeployResource {
	@Get
	public Representation status();

	@Post
	public void deploy(Representation re);

}
