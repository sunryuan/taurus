package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.restlet.resource.IDeployResource;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.DeployStatus;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.Deployer;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.DeploymentContext;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.DeploymentException;

public class DeployResource extends ServerResource implements IDeployResource {

	@Autowired
	private Deployer deployer;

	private static final Log LOG = LogFactory.getLog(DeployResource.class);

	private static final String TAURUS_URL_PATTERN = "http://taurus.dp/task.jsp?name=%s&path=%s";

	@Override
	public Representation status() {
		String deployId = getQueryValue("deployId");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", deployer.status(deployId));
		return new JsonRepresentation(result);

	}

	@Override
	public void deploy(Representation re) {
		String ip = "", file = "", id = "", callback = "", name = "", taurusUrl = "";
		String path = null;
		try {
			Form form = new Form(re);
			Map<String, String> valueMap = form.getValuesMap();
			DeploymentContext context = new DeploymentContext();
			id = valueMap.get("deployId");
			ip = valueMap.get("ip");
			file = valueMap.get("file");
			callback = valueMap.get("url");
			name = valueMap.get("name");
			context.setDepolyId(id);
			context.setName(name);
			context.setUrl(file);
			LOG.info(String.format("Start to depoly %s to %s", file, ip));
			path = deployer.deploy(ip, context);
			setStatus(Status.SUCCESS_OK);
			taurusUrl = String.format(TAURUS_URL_PATTERN, name, path);
			callback(callback, id, DeployStatus.SUCCESS, taurusUrl);
		} catch (DeploymentException e) {
			LOG.error(String.format("Fail to depoly %s to %s", file, ip), e);
			setStatus(Status.SUCCESS_OK);
			callback(callback, id, e.getStatus(), taurusUrl);
		} catch (Exception e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			callback(callback, id, DeployStatus.FAIL, taurusUrl);
			LOG.error(String.format("Fail to depoly %s to %s", file, ip), e);
		}
	}

	private void callback(String callback, String id, int statusCode, String url) {
		callback = callback + "?id=" + id + "&status=" + statusCode + "&url=" + url;
		LOG.info("Callback " + callback);
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(callback);
		method.addParameter("id", id);
		method.addParameter("status", String.valueOf(statusCode));
		method.addParameter("url", url);

		try {
			client.executeMethod(method);
		} catch (Exception e) {
			LOG.error(e, e);
		}
		method.releaseConnection();

	}
}
