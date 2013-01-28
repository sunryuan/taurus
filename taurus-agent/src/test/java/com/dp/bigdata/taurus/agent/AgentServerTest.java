package com.dp.bigdata.taurus.agent;

import java.io.IOException;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;



public class AgentServerTest {

	@Test
	public void test() throws IOException{
		Injector injector = Guice.createInjector(new AgentServerModule());
		AgentServer as = injector.getInstance(AgentServer.class);
		as.start();
	}

//	private static final class AgentServerThread implements Runnable{
//
//		@Override
//		public void run() {
//			try {
//				AgentServer as = new AgentServer(CONNECT_STRING, SESSION_TIMEOUT, 0);
//				as.start();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
