package com.dp.bigdata.taurus.restlet.resource.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.HostMapper;
import com.dp.bigdata.taurus.generated.module.Host;
import com.dp.bigdata.taurus.restlet.resource.IHostResource;
import com.dp.bigdata.taurus.restlet.shared.HostDTO;

/**
 * Resource url : http://xxx.xxx/api/host/{hostname}
 * 
 * @author damon.zhu
 */
public class HostResource extends ServerResource implements IHostResource {

    private static final Log LOG = LogFactory.getLog(HostResource.class);

    @Autowired
    private HostMapper hostMapper;
    
    @Override
    @Get
    public HostDTO retrieve() {
        String hostname = (String) getRequest().getAttributes().get("hostname");
        Host host = hostMapper.selectByPrimaryKey(hostname);
        if (host != null) {
            setStatus(Status.SUCCESS_OK);
            return new HostDTO(1, host.getName(), host.getIp(), host.getPoolid(), host.getIsconnected(),host.getIsonline());
        } else {
            return new HostDTO();
        }
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

}
