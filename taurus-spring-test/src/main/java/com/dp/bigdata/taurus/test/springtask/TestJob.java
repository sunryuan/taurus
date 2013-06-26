package com.dp.bigdata.taurus.test.springtask;

import org.apache.log4j.Logger;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.bigdata.taurus.framework.TaskBean;


public class TestJob implements TaskBean {
	
	private Logger LOG = Logger.getLogger(TestJob.class);

	private int i;
	
	public TestJob(int i){
		this.i = i;
	}
	
	@Override
	public void execute() throws Exception {
		LOG.info("value i = "+i);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
//		TaskBean bean = (TaskBean)ctx.getBean("task1");
//		try {
//			bean.execute();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
