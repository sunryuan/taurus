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
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IUsersResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IUserGroupsResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IUserGroupMappingsResource"%>
	
    <%@page import="com.dp.bigdata.taurus.restlet.shared.UserDTO"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.UserGroupDTO"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.UserGroupMappingDTO"%>
    
    
    <%@page import="java.util.ArrayList"%>
    <%@page import="org.restlet.data.MediaType"%>
    
      	<%
          		ClientResource cr = new ClientResource(host + "user");
          	    IUsersResource userResource = cr.wrap(IUsersResource.class);
          	    cr.accept(MediaType.APPLICATION_XML);
          	    ArrayList<UserDTO> users = userResource.retrieve();
          	          	   			
          	    cr = new ClientResource(host + "group");
          	    IUserGroupsResource groupResource = cr.wrap(IUserGroupsResource.class);
          	    cr.accept(MediaType.APPLICATION_XML);
          	    ArrayList<UserGroupDTO> groups = groupResource.retrieve();
          	          	   			
          	    String userName = (String)session.getAttribute(com.dp.bigdata.taurus.web.servlet.LoginServlet.USER_NAME);
          	          	   			
          	    for(UserDTO user:users){
          	        if(user.getName().equals(userName)){
          				if(user.getGroup() == null || user.getMail() == null || user.getTel() == null 
          						|| user.getGroup().equals("") || user.getMail().equals("") || user.getTel().equals("") ){
          	%>
             	<div id="alertContainer" class="container"><div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">×</button> 请完善你的信息！</div></div>
     		<%}  else{%>
     			<div id="alertContainer" class="container"></div>
     		<%} %>
    <form id="user_form" class="form-horizontal">
			<fieldset>
				<div style="display:none">
					<input type="text" class="input-large field"  id="id" name="id" value="<%=user.getId()%>">
				</div>
             	<div class="control-group">
            		<label class="control-label"  for="userName">用户名</label>
            		<div class="controls">
              			<input type="text" readonly class="input-large field"  id="userName" name="userName" value="<%=userName%>">
            		</div>
          		</div>
          		<div class="control-group">
            		<label class="control-label"  for="groupName">组名</label>
            		<div class="controls">
              			<input type="text" class="input-large field"  id="groupName" name="groupName"  value="<%=user.getGroup()%>">
            		</div>
          		</div>
          		<div class="control-group">
            		<label class="control-label"  for="email">邮件地址</label>
            		<div class="controls">
              			<input type="text" class="input-large field"  id="email" name="email"  value="<%=user.getMail()%>">
            		</div>
          		</div>
          			<div class="control-group">
            		<label class="control-label"  for="tel">手机号码</label>
            		<div class="controls">
              			<input type="text" class="input-large field"  id="tel" name="tel"  value="<%=user.getTel()%>">
            		</div>
          		</div>
				 <div class="control-group">
    				<div class="controls">
          				<button type="submit" class="btn btn-primary">保存</button>
    				</div>
  				</div>
          		
          	</fieldset>
	</form>    
    			<%}
   			}%>
    <script src="js/user.js" type="text/javascript" ></script>

</body>

</html>