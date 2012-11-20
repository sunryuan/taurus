package com.dp.bigdata.taurus.agent;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class StartServer {
	public static void main(String []args) {
		Injector injector = Guice.createInjector(new AgentServerModule());
		AgentServer as = injector.getInstance(AgentServer.class);
		as.start();		
	}
	@Override
	public  StartServer clone() {
		StartServer result = null;
			try {
				result = (StartServer) super.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		
	}
}
