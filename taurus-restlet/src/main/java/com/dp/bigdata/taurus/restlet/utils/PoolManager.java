package com.dp.bigdata.taurus.restlet.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.PoolMapper;
import com.dp.bigdata.taurus.generated.module.Pool;
import com.dp.bigdata.taurus.generated.module.PoolExample;

/**
 * 
 * PoolManager
 * @author damon.zhu
 *
 */
public class PoolManager {
    
    @Autowired
    private PoolMapper poolMapper;
    
    private Map<Integer, String> idMap;
    
    private Map<String, Integer> nameMap;
    
    public void init(){
        idMap = new HashMap<Integer, String>();
        nameMap = new HashMap<String, Integer>();
        PoolExample example = new PoolExample();
        example.or();
        List<Pool> pools = poolMapper.selectByExample(example);
        for(Pool pool : pools){
            idMap.put(pool.getId(), pool.getName().toLowerCase());
            nameMap.put(pool.getName().toLowerCase(), pool.getId());
        }
    }
    
    public int getID(String name){
        return nameMap.get(name.toLowerCase());
    }
}
