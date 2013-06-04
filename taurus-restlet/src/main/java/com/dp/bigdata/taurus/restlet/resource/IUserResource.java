package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.resource.Get;

public interface IUserResource {
	@Get
	public boolean hasRegister();

}
