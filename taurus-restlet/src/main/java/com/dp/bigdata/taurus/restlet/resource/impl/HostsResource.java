package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.HostMapper;
import com.dp.bigdata.taurus.generated.module.Host;
import com.dp.bigdata.taurus.generated.module.HostExample;
import com.dp.bigdata.taurus.restlet.resource.IHostsResource;
import com.dp.bigdata.taurus.restlet.shared.HostDTO;

/**
 * Resource url : http://xxx.xxx/api/host?pool_id={xxx}
 * 
 * @author damon.zhu
 */
public class HostsResource extends ServerResource implements IHostsResource {

	private static final Log logger = LogFactory.getLog(HostsResource.class);

	@Autowired
	private HostMapper hostMapper;

	@Override
	public ArrayList<HostDTO> retrieve() {
		ArrayList<HostDTO> hosts = new ArrayList<HostDTO>();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		if (form.size() == 0) {
			ArrayList<Host> _hosts = (ArrayList<Host>) hostMapper.selectByExample(null);
			int i = 1;
			for (Host _host : _hosts) {
				hosts.add(new HostDTO(i++, _host.getName(), _host.getIp(), _host.getPoolid(), _host.getIsconnected(), _host
				      .getIsonline()));
			}
		} else {
			if (form.size() != 1) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return hosts;
			}
			String parameterName = "";
			String poolID = "";
			for (Parameter parameter : form) {
				parameterName = parameter.getName();
				poolID = parameter.getValue();
			}
			if (!parameterName.equals("pool_id")) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return hosts;
			}
			HostExample example = new HostExample();
			example.or().andPoolidEqualTo(Integer.parseInt(poolID));
			ArrayList<Host> _hosts = (ArrayList<Host>) hostMapper.selectByExample(example);
			int i = 1;
			for (Host _host : _hosts) {
				hosts.add(new HostDTO(i++, _host.getName(), _host.getIp(), _host.getPoolid(), _host.getIsconnected(), _host
				      .getIsonline()));
			}

		}
		Collections.sort(hosts, new HostComparator());
		return hosts;
	}

	private final class HostComparator implements Comparator<HostDTO> {
		@Override
		public int compare(HostDTO host1, HostDTO host2) {
			if (host1.isOnline() && !host2.isOnline()) {
				return -1;
			} else if (!host1.isOnline() && host2.isOnline()) {
				return 1;
			}
			if (host1.isConnected() && !host2.isConnected()) {
				return -1;
			} else if (!host1.isConnected() && host2.isConnected()) {
				return 1;
			}
			long ip1 = convertIptoNumber(host1.getIp());
			long ip2 = convertIptoNumber(host2.getIp());
			if (ip1 < ip2) {
				return -1;
			} else if (ip1 > ip2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private long convertIptoNumber(String ip) {
		long result = 0;
		try {
			String[] ipArr = ip.split("\\.");
			if (ipArr.length != 4) {
				return Long.MAX_VALUE;
			}

			for (String item : ipArr) {
				result = result * 256 + Long.parseLong(item);
			}
		} catch (Exception e) {
			logger.error(e, e);
			return Long.MAX_VALUE;
		}
		return result;
	}
}
