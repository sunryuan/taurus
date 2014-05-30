package com.dp.bigdata.taurus.restlet.resource.impl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.HostMapper;
import com.dp.bigdata.taurus.generated.mapper.TaskMapper;
import com.dp.bigdata.taurus.generated.module.Host;
import com.dp.bigdata.taurus.generated.module.HostExample;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.generated.module.TaskExample;
import com.dp.bigdata.taurus.restlet.resource.IDeployResource;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.DeployStatus;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.Deployer;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.DeploymentContext;
import com.dp.bigdata.taurus.zookeeper.deploy.helper.DeploymentException;

public class DeployResource extends ServerResource implements IDeployResource {

	@Autowired
	private Deployer deployer;

	@Autowired
	private TaskMapper taskMapper;

	@Autowired
	private HostMapper hostMapper;

	private String webUrl = "taurus.dp";

	private static final Log LOG = LogFactory.getLog(DeployResource.class);

	private static final String createUrlPattern = "http://%s/task.jsp?appname=%s&path=%s&ip=%s";

	private static final String updateUrlPattern = "http://%s/schedule.jsp?appname=%s&path=%s";

	private static Map<String, DeployResult> deployResults = new LinkedHashMap<String, DeployResult>(1000, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry<String, DeployResult> arg0) {
			return size() >= 1000;
		}
	};

	private ExecutorService deployThreadPool = new ThreadPoolExecutor(5, 10, 1L, TimeUnit.SECONDS,
	      new LinkedBlockingQueue<Runnable>());

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	@Override
	@Get
	public Representation status() {
		String deployId = getQueryValue("deployId");
		String name = getQueryValue("appName");
		Map<String, Object> result = new HashMap<String, Object>();
		if (name != null) {
			TaskExample example = new TaskExample();
			example.createCriteria().andAppnameEqualTo(name).andStatusNotEqualTo(3);
			List<Task> tasks = taskMapper.selectByExample(example);
			if (tasks == null || tasks.size() == 0) {
				HostExample he = new HostExample();
				he.createCriteria().andIsonlineEqualTo(true);
				List<Host> hosts = hostMapper.selectByExample(he);
				List<String> ips = new ArrayList<String>();
				for (Host host : hosts) {
					ips.add(host.getIp());
				}
				result.put("hosts", ips);
			} else {
				List<String> ips = new ArrayList<String>();
				String hostIp = tasks.get(0).getHostname();
				ips.add(hostIp);
				result.put("hosts", ips);
			}
		} else {
			DeployResult deployResult = deployResults.get(deployId);
			if (deployResult == null) {
				result.put("status", DeployStatus.UNKNOWN);
			} else {
				result.put("status", deployResult.status);
				result.put("createurl", deployResult.createUrl);
				result.put("updateurl", deployResult.updateUrl);
			}
		}
		return new JsonRepresentation(result);
	}

	@Override
	@Post
	public void deploy(final Representation re) {
		Form form = new Form(re);
		Map<String, String> valueMap = form.getValuesMap();
		final String id = valueMap.get("deployId");
		final String ip = valueMap.get("ip");
		final String file = valueMap.get("file");
		final String callback = valueMap.get("url");
		final String name = valueMap.get("name");

		setStatus(Status.SUCCESS_OK);
		deployThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				deployInternal(ip, file, id, callback, name);
			}
		});

	}

	private void deployInternal(String ip, String file, String id, String callback, String name) {
		String path = null;
		DeployResult dr = new DeployResult();

		try {
			DeploymentContext context = new DeploymentContext();
			deployResults.put(id, dr);
			context.setDepolyId(id);
			context.setName(name);
			context.setUrl(file);
			testFileUrl(file);
			LOG.info(String.format("Start to depoly %s to %s", file, ip));
			dr.status = DeployStatus.DEPLOYING;
			path = deployer.deploy(ip, context);
			String taurusUrl = String.format(createUrlPattern, webUrl, name, path, ip);
			String updateUrl = String.format(updateUrlPattern, webUrl, name, path);
			callback(dr, callback, DeployStatus.SUCCESS, taurusUrl, updateUrl);
			LOG.debug("deploy success");
		} catch (DeploymentException e) {
			LOG.error(String.format("Fail to depoly %s to %s", file, ip), e);
			callback(dr, callback, e.getStatus(), null, null);
		} catch (Exception e) {
			callback(dr, callback, DeployStatus.FAIL, null, null);
			LOG.error(String.format("Fail to depoly %s to %s", file, ip), e);
		}
	}

	private void testFileUrl(String file) throws DeploymentException {
		try {
			URL url = new URL(file);
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			byte[] buffer = new byte[1204];
			if (inStream.read(buffer) == 0) {
				throw new FileNotFoundException();
			}
		} catch (Exception e) {
			DeploymentException de = new DeploymentException("File source not found", e);
			de.setStatus(DeployStatus.NO_SOURCE);
			throw de;
		}
	}

	private void callback(DeployResult dr, String callback, int statusCode, String createUrl, String updateUrl) {
		dr.status = statusCode;
		dr.createUrl = createUrl;
		dr.updateUrl = updateUrl;
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(callback);
		LOG.info("callback:" + callback);
		method.addParameter("status", String.valueOf(statusCode));
		if (createUrl != null) {
			method.addParameter("createurl", createUrl);
		}
		if (updateUrl != null) {
			method.addParameter("updateurl", updateUrl);
		}
		try {
			client.executeMethod(method);
		} catch (Exception e) {
			LOG.error(e, e);
		}
		method.releaseConnection();
	}

	static class DeployResult {
		int status;

		String createUrl;

		String updateUrl;
	}
}
