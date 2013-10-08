package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;

public class HostServlet extends HttpServlet {

	private static final long serialVersionUID = 3186453922497394454L;
	private static final String HOST_NAME = "hostName";
	private static final String OP = "op";
	private String RESTLET_URL_BASE;
	private static final Log LOG = LogFactory.getLog(HostServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = getServletContext();
		RESTLET_URL_BASE = context.getInitParameter("RESTLET_SERVER");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String hostName = req.getParameter(HOST_NAME);
			String op = req.getParameter(OP);
			if (hostName == null || op == null || hostName.isEmpty()
					|| op.isEmpty()) {
				LOG.error("hostName = " + hostName + ",op = " + op);
				req.setAttribute("statusCode", "500");
				return;
			}
			ClientResource cr = new ClientResource(RESTLET_URL_BASE + "host/"
					+ req.getParameter("hostName"));
			cr.post(op);
			Status status = cr.getResponse().getStatus();
			if(Status.SUCCESS_OK.equals(status)){
				req.setAttribute("statusCode", "200");
			} else {
				req.setAttribute("statusCode", "500");
			}
		} catch (Exception e) {
			LOG.error(e, e);
			req.setAttribute("statusCode", "500");
		}
		RequestDispatcher requestDispatcher = req
				.getRequestDispatcher("/hosts.jsp");
		requestDispatcher.forward(req, resp);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
