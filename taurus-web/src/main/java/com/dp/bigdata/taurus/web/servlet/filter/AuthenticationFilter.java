package com.dp.bigdata.taurus.web.servlet.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dp.bigdata.taurus.web.servlet.LoginServlet;

/**
 * AuthenticationFilter
 * 
 * @author damon.zhu
 */
public class AuthenticationFilter implements Filter {

	private String loginPage;

	private String[] excludePages;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (filterConfig != null) {
			loginPage = filterConfig.getInitParameter("loginPage");
			String excludePage = filterConfig.getInitParameter("excludePage");
			excludePages = excludePage.split(",");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	      ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String requestURL = req.getRequestURI();

		if (req.getQueryString() != null) {
			requestURL = requestURL + "?" + req.getQueryString();
		}
		for (String uri : excludePages) {
			if (uri.equalsIgnoreCase(req.getRequestURI().substring(req.getContextPath().length()))) {
				System.out.println("excludePage : " + uri);
				chain.doFilter(request, response);
				return;
			}
		}

		HttpSession session = req.getSession(true);

		Object currentUser = session.getAttribute(LoginServlet.USER_NAME);
		if (currentUser == null) {
			String loginUrl = loginPage + "?redirect-url="+URLEncoder.encode(requestURL, "UTF-8");
//			req.getRequestDispatcher(loginUrl).forward(request, response);
			res.sendRedirect(loginUrl);
		
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

}
