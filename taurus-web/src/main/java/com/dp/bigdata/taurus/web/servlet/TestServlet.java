package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet  extends HttpServlet {

   private static final long serialVersionUID = -266710177838386463L;

	@SuppressWarnings("rawtypes")
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		 Enumeration en = req.getParameterNames();
       while (en.hasMoreElements()) {
           String paramName = (String) en.nextElement();
           System.out.println(paramName + " = " + req.getParameter(paramName));
       }
       resp.setStatus(200);
	}
	@Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		 doPost(req,resp);
	}
	
}
