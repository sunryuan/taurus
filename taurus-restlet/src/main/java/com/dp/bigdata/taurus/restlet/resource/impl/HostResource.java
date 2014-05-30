package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.HostMapper;
import com.dp.bigdata.taurus.generated.module.Host;
import com.dp.bigdata.taurus.generated.module.HostExample;
import com.dp.bigdata.taurus.restlet.resource.IHostResource;
import com.dp.bigdata.taurus.restlet.shared.HostDTO;
import com.dp.bigdata.taurus.zookeeper.common.infochannel.bean.HeartbeatInfo;
import com.dp.bigdata.taurus.zookeeper.host.helper.HostManager;

/**
 * Resource url : http://xxx.xxx/api/host/{hostname}
 * 
 * @author damon.zhu
 */
public class HostResource extends ServerResource implements IHostResource {

	private static final Log LOG = LogFactory.getLog(HostResource.class);

	private enum Operate {
		UP, DOWN, RESTART, UPDATE
	}

	@Autowired
	private HostMapper hostMapper;

	@Autowired
	private HostManager hostManager;

	@Override
	@Get
	public HostDTO retrieve() {
		String hostname = (String) getRequest().getAttributes().get("hostname");
		Host host = hostMapper.selectByPrimaryKey(hostname);
		if (host != null) {
			setStatus(Status.SUCCESS_OK);
			return convertHost(host);
		} else {
			return null;
		}
	}

	private HostDTO convertHost(Host _host) {
		HeartbeatInfo info = hostManager.read(_host.getIp());
		HostDTO hostDto = new HostDTO();
		hostDto.setConnected(_host.getIsconnected());
		hostDto.setInfo(info);
		hostDto.setIp(_host.getIp());
		hostDto.setName(_host.getName());
		hostDto.setOnline(_host.getIsonline());
		hostDto.setPoolid(_host.getPoolid());
		return hostDto;
	}

	@Override
	@Put
	public void update(HostDTO dto) {

		String hostname = (String) getRequest().getAttributes().get("hostname");

		try {
			Host host = hostMapper.selectByPrimaryKey(hostname);
			if (host == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return;
			}
			host.setIp(dto.getIp());
			host.setIsconnected(dto.isConnected());
			host.setPoolid(dto.getPoolid());
			hostMapper.updateByPrimaryKey(host);
			setStatus(Status.SUCCESS_OK);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			LOG.error("Fail to update the host : " + hostname, e);
		}
	}

	@Override
	@Post
	public void operate(String op) {
		String hostName = (String) getRequest().getAttributes().get("hostname");
		try {
			List<String> ips = new ArrayList<String>();
			HostExample example = new HostExample();
			Host host = null;

			if (op == null) {
				setStatus(Status.SERVER_ERROR_INTERNAL);
				LOG.error("No op to host : " + hostName);
				return;
			}
			if (hostName.toLowerCase().equals("all")) {
				example.or().andIsonlineEqualTo(true).andIsconnectedEqualTo(true);
				List<Host> hosts = hostMapper.selectByExample(example);
				for (Host item : hosts) {
					ips.add(item.getName());
				}
			} else if (hostName.indexOf(",") != -1) {
				String[] hostNames = hostName.split(",");
				for (String name : hostNames) {
					ips.add(name.trim());
				}
			} else {
				ips.add(hostName);
			}
			for (String ip : ips) {
				example.or().andNameEqualTo(ip);
				List<Host> hosts = hostMapper.selectByExample(example);
				if (hosts.size() == 1) {
					host = hosts.get(0);
				}
				if (host == null) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					LOG.error("Host not found: " + ip);
					continue;
				}
				
				if (op.equalsIgnoreCase(Operate.UP.name())) {
					host.setIsonline(true);
					hostMapper.updateByExample(host, example);
				} else if (op.equalsIgnoreCase(Operate.DOWN.name())) {
					host.setIsonline(false);
					hostMapper.updateByExample(host, example);
				}	if (op.equalsIgnoreCase(Operate.RESTART.name()) || op.equalsIgnoreCase(Operate.UPDATE.name())) {
					//do nothing
				} else {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					LOG.error("Not support " + op + " to host " + hostName);
					return;
				}
			}
			if (op.equalsIgnoreCase(Operate.RESTART.name()) || op.equalsIgnoreCase(Operate.UPDATE.name())) {
				hostManager.operate(op, ips);
			}
		} catch (Exception e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			LOG.error("Fail to do operating " + op + " to host " + hostName, e);
			return;
		}
		setStatus(Status.SUCCESS_OK);
	}

}
