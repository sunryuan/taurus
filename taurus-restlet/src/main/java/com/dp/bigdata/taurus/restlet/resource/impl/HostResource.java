package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;

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
 * Resource url : http://xxx.xxx/api/host/{host_id}
 * 
 * @author damon.zhu
 */
public class HostResource extends ServerResource implements IHostResource {

    private static final Log LOG = LogFactory.getLog(HostResource.class);

    @Autowired
    private HostMapper hostMapper;
    
    @Override
    @Get
    public ArrayList<HostDTO> retrieve() {
        int hostID = Integer.parseInt((String) getRequest().getAttributes().get("host_id"));
        Host host = hostMapper.selectByPrimaryKey(hostID);
        ArrayList<HostDTO> dtos = new ArrayList<HostDTO>();
        if (host != null) {
            dtos.add(new HostDTO(host.getId(), host.getName(), host.getIp(), host.getPoolid(), host.getIsconnected()));
        }
        return dtos;
    }

    @Override
    @Put
    public void update(HostDTO dto) {
        int hostID = 0;

        try {
            hostID = Integer.parseInt((String) getRequest().getAttributes().get("host_id"));
        } catch (Exception e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return;
        }

        try {
            Host host = hostMapper.selectByPrimaryKey(hostID);
            if (host == null) {
                setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                return;
            }
            //TODO:modify poolID only
            dto.setId(hostID);
            host.setIp(dto.getIp());
            host.setIsconnected(dto.isConnected());
            host.setName(dto.getName());
            host.setPoolid(dto.getPoolid());
            hostMapper.updateByPrimaryKey(host);
            setStatus(Status.SUCCESS_OK);
        } catch (RuntimeException e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
            LOG.error("Fail to update the host : " + hostID, e);
        }
    }

}
