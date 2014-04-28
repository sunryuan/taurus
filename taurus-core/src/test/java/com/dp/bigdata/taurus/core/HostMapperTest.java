package com.dp.bigdata.taurus.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dp.bigdata.taurus.generated.mapper.HostMapper;
import com.dp.bigdata.taurus.generated.mapper.TaskAttemptMapper;
import com.dp.bigdata.taurus.generated.module.Host;
import com.dp.bigdata.taurus.generated.module.TaskAttempt;

/**
 * 
 * ImportDataTest
 * 
 * @author damon.zhu
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext-test.xml" })
public class HostMapperTest {

	@Autowired
	private HostMapper hostMapper;

	@Autowired
	private TaskAttemptMapper taskAttemptMapper;

	// @Autowired
	// private TaskMapper taskMapper;

	@Test
	public void insertHostData() {
		Host record = hostMapper.selectByPrimaryKey("HADOOP");
		String ip = record.getIp();
		assertEquals("10.1.77.84", ip);
	}
	
	@Test
	public void selectHostData() {
		List<Host> results = hostMapper.selectByExample(null);
		assertEquals(1, results.size());
	}

	protected void loadData() {
		Host record = new Host();
		record.setIp("10.1.77.84");
		record.setName("HADOOP");
		record.setPoolid(1);
		hostMapper.insertSelective(record);
	}

	@Test
	public void insertTaskData() {
		List<TaskAttempt> selectByGroupAndStatus = taskAttemptMapper
				.selectByGroupAndStatus();

		for(TaskAttempt at : selectByGroupAndStatus){
			System.out.println(at.getTaskid() + " at " + at.getStarttime());
			
		}
	}
}
