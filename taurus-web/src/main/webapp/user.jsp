<%@ page contentType="text/html;charset=utf-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<%@ include file="jsp/common-header.jsp"%>
	<link href="css/bwizard.min.css" rel="stylesheet" />
</head>
<body>
	<%@ include file="jsp/common-nav.jsp"%>
    <%@ include file="jsp/common-api.jsp"%>
    <%@page import="org.restlet.resource.ClientResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IPoolsResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IAttemptStatusResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IUsersResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IUserGroupsResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IHostsResource"%>
	
    <%@page import="com.dp.bigdata.taurus.restlet.shared.PoolDTO"%> 
    <%@page import="com.dp.bigdata.taurus.restlet.shared.StatusDTO"%> 
    <%@page import="com.dp.bigdata.taurus.restlet.shared.UserDTO"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.UserGroupDTO"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.HostDTO"%>
    
    
    <%@page import="java.util.ArrayList"%>
    <%@page import="org.restlet.data.MediaType"%>
    
      	<%
   		ClientResource cr = new ClientResource(host + "pool");
   	   		IPoolsResource poolResource = cr.wrap(IPoolsResource.class);
   	    	cr.accept(MediaType.APPLICATION_XML);
   	   		ArrayList<PoolDTO> pools = poolResource.retrieve();
   	   		int UNALLOCATED = 1;
   			
   			cr = new ClientResource(host + "user");
   			IUsersResource userResource = cr.wrap(IUsersResource.class);
   			cr.accept(MediaType.APPLICATION_XML);
   			ArrayList<UserDTO> users = userResource.retrieve();
   			
   			cr = new ClientResource(host + "group");
   			IUserGroupsResource groupResource = cr.wrap(IUserGroupsResource.class);
   			cr.accept(MediaType.APPLICATION_XML);
   			ArrayList<UserGroupDTO> groups = groupResource.retrieve();
   	%>
    <form id="user-form" class="form-horizontal">
			<fieldset>
             	<div class="control-group">
            		<label class="control-label"  for="userName">用户名</label>
            		<div class="controls">
              			<input type="text" readonly class="input-xxlarge field"  id="userName" name="userName" value="<%=(String)session.getAttribute(com.dp.bigdata.taurus.web.servlet.LoginServlet.USER_NAME)%>">
            		</div>
          		</div>
          		<div class="control-group">
            		<label class="control-label"  for="groupName">组名</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field"  id="groupName" name="groupName"  placeholder="name">
            		</div>
          		</div>
          		<div class="control-group">
            		<label class="control-label"  for="email">邮件地址</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field"  id="email" name="email"  placeholder="name">
            		</div>
          		</div>
          			<div class="control-group">
            		<label class="control-label"  for="tel">手机号码</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field"  id="tel" name="tel"  placeholder="name">
            		</div>
          		</div>
          	</fieldset>
	</form>    
    
</body>