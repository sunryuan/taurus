package com.dp.bigdata.taurus.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.restlet.resource.ClientResource;

import com.dp.bigdata.taurus.restlet.resource.IUserResource;
import com.dp.bigdata.taurus.restlet.resource.IUsersResource;
import com.dp.bigdata.taurus.restlet.shared.UserDTO;

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

	private String RESTLET_URL_BASE;

	private String USER_API;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = getServletContext();
		RESTLET_URL_BASE = context.getInitParameter("RESTLET_SERVER");
		USER_API = RESTLET_URL_BASE + "user";
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 只销毁了session。在线用户库里的注销工作在session的SessionDestroyedListener里完成
        request.getSession().invalidate(); 
        
        response.setContentType("text/html;charset=GBK");
        PrintWriter out = response.getWriter();
       
        out.print("登出成功");
        out.flush();
        out.close();

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("username");
		String password = request.getParameter("password");

		if (StringUtils.isBlank(password)) {
			response.setStatus(401);
			return;
		}

		LDAPAuthenticationService authService = new LDAPAuthenticationService();
		boolean isAuthenticated = false;

		try {
			isAuthenticated = authService.authenticate(userName, password);
		} catch (Exception e) {
			isAuthenticated = false;
			e.printStackTrace();
		}

		if (!isAuthenticated) {
			ClientResource cr = new ClientResource(String.format("%s/%s", USER_API, userName));
			IUserResource resource = cr.wrap(IUserResource.class);
			boolean hasRegister = resource.hasRegister();
			if (hasRegister) {
				HttpSession session = request.getSession();
				session.setAttribute(USER_NAME, userName);
				response.setStatus(200);
				System.out.println("login in database success!");
			} else {
				response.setStatus(401);
				System.out.println("longin fail!");
			}
		} else {
			HttpSession session = request.getSession();
			session.setAttribute(USER_NAME, userName);
			System.out.println("login success!");

			ClientResource cr = new ClientResource(USER_API);
			IUsersResource resource = cr.wrap(IUsersResource.class);
			UserDTO dto = new UserDTO();
			dto.setName(userName);
			dto.setMail(String.format("%s@dianping.com", userName));
			resource.createIfNotExist(dto);

			response.setStatus(200);
		}
	}

	public static void main(String[] args) throws Exception {
		LDAPAuthenticationService authService = new LDAPAuthenticationService();
		System.out.println(authService.authenticate("damon.zhu", "Kellyzxy321d"));
	}
}
