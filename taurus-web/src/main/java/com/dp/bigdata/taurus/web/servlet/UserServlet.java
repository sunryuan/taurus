package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.dp.bigdata.taurus.restlet.resource.IUserResource;

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
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	ClientResource cr = new ClientResource(RESTLET_URL_BASE + "user" );
    	
        IUserResource userResource = cr.wrap(IUserResource.class);
        Representation re = new InputRepresentation(request.getInputStream());
        if(userResource.update(re)){
        	response.setStatus(200);
        } else {
        	response.setStatus(500);
        }
    
    }

}
