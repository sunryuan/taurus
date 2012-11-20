package com.dp.bigdata.taurus.frontend.client.service;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Put;
import org.restlet.client.resource.Result;

import com.dp.bigdata.taurus.restlet.shared.HostDTO;

public interface HostService extends ClientProxy {
	@Put
	public void update(HostDTO hostDto, Result<Void> callback);
}

