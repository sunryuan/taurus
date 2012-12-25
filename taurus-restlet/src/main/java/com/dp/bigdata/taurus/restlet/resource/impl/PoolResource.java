package com.dp.bigdata.taurus.restlet.resource.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.HostMapper;
import com.dp.bigdata.taurus.generated.mapper.PoolMapper;
import com.dp.bigdata.taurus.generated.module.Host;
import com.dp.bigdata.taurus.generated.module.HostExample;
import com.dp.bigdata.taurus.generated.module.Pool;
import com.dp.bigdata.taurus.restlet.resource.IPoolResource;

/**
 * Resource url : http://xxx.xxx/api/pool/{pool_id}
 * 
 * @author damon.zhu
 */
public class PoolResource extends ServerResource implements IPoolResource {

    private static final Log LOG = LogFactory.getLog(PoolsResource.class);
    public static final int UNALLOCATED = 1;

    @Autowired
    private PoolMapper poolMapper;

    @Autowired
    private HostMapper hostMapper;

    @Override
    @Delete
    public void remove() {
        int poolID = 0;
        try {
            poolID = Integer.parseInt((String) getRequest().getAttributes().get("pool_id"));
        } catch (Exception e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }
        if (poolID == UNALLOCATED) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        try {
            Pool pool = poolMapper.selectByPrimaryKey(poolID);
            if (pool == null) {
                setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                return;
            }
            poolMapper.deleteByPrimaryKey(poolID);
            HostExample example = new HostExample();
            example.or().andPoolidEqualTo(poolID);
            Host record = new Host();
            record.setPoolid(UNALLOCATED);
            hostMapper.updateByExampleSelective(record, example);
            setStatus(Status.SUCCESS_OK);
        } catch (RuntimeException e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
            LOG.error("Fail to remove the pool : " + poolID, e);
        }
    }
}
