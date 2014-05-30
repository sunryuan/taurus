package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface IHostIpResource {
	@Get
	public Representation retrieve();
}
