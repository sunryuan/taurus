/**
 * Project: taurus-restlet File Created at 2013-7-24 $Id$ Copyright 2013 dianping.com. All rights reserved. This software is the
 * confidential and proprietary information of Dianping Company. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with
 * dianping.com.
 */
package com.dp.bigdata.taurus.restlet;

import org.junit.Test;
import org.restlet.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.dp.bigdata.taurus.core.Engine;

/**
 * to test taurus restlet
 * 
 * @author renyuan.sun
 */
public class RestletTest {

	@Test
	public void testMain() {
		ApplicationContext context = new FileSystemXmlApplicationContext("classpath:applicationContext-core.xml",
		      "classpath:applicationContext-restlet.xml");
		Engine engine = (Engine) context.getBean("engine");
		Component restlet = (Component) context.getBean("component");

		try {
			restlet.start();
			engine.start();
			System.out.println(String.format("[%s] [INFO] Press any key to stop server ... ", 1));
			System.in.read();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
