package com.dp.bigdata.taurus.test.springtask;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.bigdata.taurus.framework.TaskBean;


public class TestJob implements TaskBean {

	private int i;
	
	public TestJob(int i){
		this.i = i;
	}
	
	@Override
	public void execute() throws Exception {
		System.out.println(i);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
	}
}
