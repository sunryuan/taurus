package com.dp.bigdata.taurus.restlet.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.PoolMapper;
import com.dp.bigdata.taurus.generated.module.Pool;
import com.dp.bigdata.taurus.generated.module.PoolExample;
import com.dp.bigdata.taurus.restlet.resource.IPoolsResource;
import com.dp.bigdata.taurus.restlet.shared.PoolDTO;
import com.mysql.jdbc.StringUtils;

/**
 * Resource url : http://xxx.xxx/api/pool
 * 
 * @author damon.zhu
 */
public class PoolsResource extends ServerResource implements IPoolsResource {

    private static final Log LOG = LogFactory.getLog(PoolsResource.class);

    @Autowired
    private PoolMapper poolMapper;

    @Override
    public ArrayList<PoolDTO> retrieve() {
        ArrayList<PoolDTO> pools = new ArrayList<PoolDTO>();
        PoolExample example = new PoolExample();
        example.or();
        List<Pool> bPools = poolMapper.selectByExample(example);
        for (Pool p : bPools) {
            PoolDTO dto = new PoolDTO(p.getId(),p.getName(),p.getCreator());
            pools.add(dto);
        }
        return pools;
    }

    @Override
    public void create(PoolDTO t) {
        if (t != null && !StringUtils.isNullOrEmpty(t.getName())) {
            try {
                Pool pool = new Pool();
                pool.setName(t.getName());
                if(StringUtils.isNullOrEmpty(t.getCreator())){
                    pool.setCreator("unknown");
                }else{
                    pool.setCreator(t.getCreator());
                }
                poolMapper.insertSelective(pool);
                setStatus(Status.SUCCESS_CREATED);
            } catch (Exception e) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                LOG.info(e.getMessage(), e);
            }
        } else {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
    }
}
