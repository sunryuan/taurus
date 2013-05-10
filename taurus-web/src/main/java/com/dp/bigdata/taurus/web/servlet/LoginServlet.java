package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

/**
 * LoginServlet
 * 
 * @author damon.zhu
 */

public class LoginServlet extends HttpServlet {

	/**
     * 
     */
	private static final long serialVersionUID = 8471117450126373174L;

	public static final String USER_NAME = "taurus-user";

	public static final String USER_GROUP = "taurus-group";

	public static final String USER_POWER = "taurus-user-power";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("username");
		String password = request.getParameter("password");

		if (StringUtils.isBlank(password)) {
			response.setStatus(401);
			return;
		}

		System.out.println("login request");
		System.out.println("userName : " + userName);

		LDAPAuthenticationService authService = new LDAPAuthenticationService();
		boolean isAuthenticated = false;

		try {
			isAuthenticated = authService.authenticate(userName, password);
		} catch (Exception e) {
			isAuthenticated = false;
			e.printStackTrace();
		}

		if (!isAuthenticated) {
			response.setStatus(401);
			System.out.println("longin fail!");
		} else {
			HttpSession session = request.getSession();
			session.setAttribute(USER_NAME, userName);
			System.out.println("login success!");
			response.setStatus(200);
		}
	}
}
