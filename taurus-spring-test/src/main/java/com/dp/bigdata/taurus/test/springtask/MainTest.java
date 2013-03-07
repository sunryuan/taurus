package com.dp.bigdata.taurus.test.springtask;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class MainTest {
	public static void main(String[] args) {
		URLClassLoader ul;
		try {
			ul = new URLClassLoader(new URL[]{new URL("file:///Users/damonzhu/Projects/Taurus-1/taurus-agent/jobs/jar1234/taurus-spring-test-jar-with-dependencies.jar")});
			Thread.currentThread().setContextClassLoader(ul);
			//load mainClass
			Class<?> claz = ul.loadClass("com.dp.bigdata.taurus.test.springtask.TestJob");
			Method[] methods = claz.getDeclaredMethods();
			for(Method m : methods){
				System.out.println(m.getName());
			}
			String[] parameters = null;
			claz.getMethod("main", String[].class).invoke(null,(Object)parameters);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
