package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;

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

    //private static final Log logger = LogFactory.getLog(HostsResource.class);

    @Autowired
    private HostMapper hostMapper;

    @Override
    public ArrayList<HostDTO> retrieve() {
        ArrayList<HostDTO> hosts = new ArrayList<HostDTO>();
        Form form = getRequest().getResourceRef().getQueryAsForm();
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
        for (Host _host : _hosts) {
            hosts.add(new HostDTO(_host.getId(), _host.getName(), _host.getIp(), _host.getPoolid(), _host.getIsconnected()));
        }
        return hosts;
    }
}
