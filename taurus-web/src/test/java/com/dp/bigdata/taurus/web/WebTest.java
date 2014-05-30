package com.dp.bigdata.taurus.web;

import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.webapp.WebAppContext;
import org.unidal.test.jetty.JettyServer;

public class WebTest extends JettyServer {

	@Before
	public void before() throws Exception {
		System.setProperty("devMode", "true");
		super.startServer();
	}

	@Override
	protected String getContextPath() {
		return "/";
	}

	@Override
	protected int getServerPort() {
		return 2281;
	}

	@Override
	protected void postConfigure(WebAppContext context) {
	}

	@Test
	public void startWebApp() throws Exception {
		// open the page in the default browser
		display("/");
		waitForAnyKey();
	}
}
