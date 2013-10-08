<%@ page contentType="text/html;charset=utf-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<%@ include file="jsp/common-header.jsp"%>
	<%@ include file="jsp/common-nav.jsp"%>
	<link href="css/bwizard.min.css" rel="stylesheet" />
</head>
<body>
    <%@page import="java.util.Map"%>
    <%@page import="java.util.HashMap"%>
    
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IUserGroupsResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IUserGroupMappingsResource"%>
	
    <%@page import="com.dp.bigdata.taurus.restlet.shared.UserGroupDTO"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.UserGroupMappingDTO"%>
   
      	<%          	          	   			
          	    cr = new ClientResource(host + "group");
          	    IUserGroupsResource groupResource = cr.wrap(IUserGroupsResource.class);
          	    cr.accept(MediaType.APPLICATION_XML);
          	    ArrayList<UserGroupDTO> groups = groupResource.retrieve();
          	          	   			          	   	
          	    Map<String,String> map = new HashMap<String,String>();
          	    
          	    for(UserDTO user:users){
          	    	String group = user.getGroup();
          	    	if(group == null || group.equals("")){
          	    		group = "未分组";
          	    	}
          	    	if(map.containsKey(group)){
      	    			map.put(group,map.get(group) + ", " + user.getName() );
      	    		} else {
      	    			map.put(group,user.getName());
      	    		}
          	        if(user.getName().equals(currentUser)){
          				if(user.getGroup() == null || user.getMail() == null || user.getTel() == null 
          						|| user.getGroup().equals("") || user.getMail().equals("") || user.getTel().equals("") ){
          	%>
             	<div id="alertContainer" class="container"><div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">×</button> 请完善你的信息！</div></div>
     		<%}  else{%>
     			<div id="alertContainer" class="container"></div>
     		<%} %>
     <div class="container" style="margin-top: 10px">
     <div class="row">
     <div class="span5">
    	<form id="user-form" class="form-horizontal">
			<fieldset>
				<div style="display:none">
					<input type="text" class="input-large field"  id="id" name="id" value="<%=user.getId()%>">
				</div>
             	<div class="control-group">
            		<label class="control-label"  for="userName">用户名</label>
            		<div class="controls">
              			<input type="text" readonly class="input-large field"  id="userName" name="userName" value="<%=currentUser%>">
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
            			<% if(user.getTel()==null){%>
            				<input type="text" class="input-large field"  id="tel" name="tel"  value="">
            			<%} else{ %>
              				<input type="text" class="input-large field"  id="tel" name="tel"  value="<%=user.getTel()%>">
            			<%} %>
            		</div>
          		</div>
				 <div class="control-group">
    				<div class="controls">
          				<button type="submit" id="submit" class="btn btn-primary">保存</button>
    				</div>
  				</div>
          		
          	</fieldset>
	</form>    
    			<%}
   			}%>
   		</div>
   		         <div class="span7"  style="opacity: 0.5">
   		
   			
   				分组情况请参考下面的列表：<br/>
   				<ul>
   				<li>如果列表中没有你想要的分组，你可以填写一个组名，这个组名请尽可能地细化。</li>
				<li>这个选项的重要性在于，你可以在作业的通知选项中，选择通知一个组的人。</li>
				<li>并且同组的人可以操作彼此的作业。</li>
				</ul>
   			
   			<table  class="table table-striped table-bordered table-condensed">
				<tr>
					<th align="left" width="15%">组名</th>
					<th align="left" width="85%">成员</th>
				</tr>
				<%
   					for(String group:map.keySet()){
   				%>
					<tr>
						<td align="left"><%=group%></td>
						<td align="left"><%=map.get(group)%></td>
					</tr>
   				<%  }%>
   			
   			</table>
   			</div>
   		</div>
   	</div>
   	<script type="text/javascript">  
      	var userList="",groupList="",isAdmin;
      	<%for(UserDTO user:users) {%>
      		userList=userList+",<%=user.getName()%>";
      	<%}%>
      	<%for(UserGroupDTO group:groups) {%>
      		groupList=groupList+",<%=group.getName()%>";
	<%}%>
	isAdmin = <%=isAdmin%>;
	userList = userList.substr(1);
	groupList = groupList.substr(1);
	</script>
	<script src="js/jquery.validate.min.js" type="text/javascript"></script>
	<script src="js/jquery.autocomplete.min.js" type="text/javascript"></script>
    <script src="js/user.js" type="text/javascript" ></script>

</body>

</html>