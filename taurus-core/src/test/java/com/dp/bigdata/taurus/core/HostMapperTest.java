package com.dp.bigdata.taurus.core;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.bigdata.taurus.generated.mapper.HostMapper;
import com.dp.bigdata.taurus.generated.module.Host;

/**
 * 
 * ImportDataTest
 * @author damon.zhu
 *
 */
public class HostMapperTest extends AbstractDaoTest {

    @Autowired
    private HostMapper hostMapper;
    
    @Test
    public void insertHostData(){
        Host record = hostMapper.selectByPrimaryKey(1);
        int exprected = record.getId();
        assertEquals(1, exprected);
    }
    
    @Override
    protected void loadData() {
        Host record = new Host();
        record.setIp("10.1.77.84");
        record.setName("HADOOP");
        record.setPoolid(1);
        hostMapper.insertSelective(record);
    }
    
    
}
