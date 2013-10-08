package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class UserServlet extends HttpServlet {

	private static final long serialVersionUID = 9180835489946519989L;
	private String RESTLET_URL_BASE;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = getServletContext();
		RESTLET_URL_BASE = context.getInitParameter("RESTLET_SERVER");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ClientResource cr = new ClientResource(RESTLET_URL_BASE + "user/" + request.getParameter("userName"));

		Form form = new Form();
		for (UserProperty p : UserProperty.values()) {
			form.add(p.getName(), request.getParameter(p.getName()));
		}
		Representation re = form.getWebRepresentation();
		re.setMediaType(MediaType.APPLICATION_XML);
		cr.post(re);
		Status status = cr.getResponse().getStatus();
		response.setStatus(status.getCode());
	}

	private enum UserProperty {
		ID("id"), USERNAME("userName"), GROUPNAME("groupName"), EMAIL("email"), TEL(
				"tel");

		private String name;

		private UserProperty(String name) {
			this.name = name;
		}

		private String getName() {
			return name;
		}
	}

}
