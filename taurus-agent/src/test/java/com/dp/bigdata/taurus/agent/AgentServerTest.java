package com.dp.bigdata.taurus.agent;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AgentServerTest {

    public static void main(String []args) {
        Injector injector = Guice.createInjector(new AgentServerModule());
        AgentServer as = injector.getInstance(AgentServer.class);
        as.start();     
    }
}
