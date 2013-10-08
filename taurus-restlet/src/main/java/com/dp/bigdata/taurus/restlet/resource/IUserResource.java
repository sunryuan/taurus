package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface IUserResource {
	@Get
	public boolean hasRegister();

	@Post
	public void update(Representation re);

}
