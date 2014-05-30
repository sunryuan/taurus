<%@ page contentType="text/html;charset=utf-8" %>
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a class="brand" href="./index.jsp">Taurus</a>
			<div class="nav-collapse collapse">
				<p class="navbar-text pull-right">
    				<%@page import="org.restlet.resource.ClientResource"%>
					<%@page import="com.dp.bigdata.taurus.restlet.resource.IUsersResource"%>
				    <%@page import="com.dp.bigdata.taurus.restlet.shared.UserDTO"%>
				    <%@page import="java.util.ArrayList"%>
    				<%@page import="org.restlet.data.MediaType"%>
                	<% 
						String currentUser = (String) session.getAttribute(com.dp.bigdata.taurus.web.servlet.LoginServlet.USER_NAME);
						if(currentUser != null){
					%>
                    	<a  class="btn-link"><i class="icon-user icon-white"></i> <%=currentUser%></a>
                    	<a  class="btn-link" href="javascript:logout('<%=currentUser%>')"><i class="icon-off icon-white"></i>退出</a>
                    <%}else{%>
						<a href="./signin.jsp" class="btn-link" data-toggle="modal"><i class="icon-user icon-white"></i> 登录</a>
                    <%}%>
                    <!-- Global variable -->
                    <%
                    	String host = config.getServletContext().getInitParameter("RESTLET_SERVER");
  						//String host = "http://10.1.77.85:8182/api/";
						boolean isAdmin = false;
						ClientResource cr = new ClientResource(host + "user");
          	    		IUsersResource userResource = cr.wrap(IUsersResource.class);
          	    		cr.accept(MediaType.APPLICATION_XML);
          	    		ArrayList<UserDTO> users = userResource.retrieve();
          	    		for(UserDTO user:users){
          	    			if(user.getName().equals(currentUser)){
          	        			isAdmin = "admin".equals(user.getGroup());
          	    			}
          	    		}
          			%>	
          		</p>

				<ul class="nav">
					<li class=""><a href="task.jsp">新建任务</a></li>
					<!--li class=""><a href="batch-task.jsp">批量设置</a></li-->
					<li class=""><a href="schedule.jsp">调度中心</a></li>
					
					<li class=""><a href="user.jsp">用户设置</a></li>
					<li class=""><a href="about.jsp">帮助</a></li>
				</ul>
			</div>
		</div>
	</div>
</div>