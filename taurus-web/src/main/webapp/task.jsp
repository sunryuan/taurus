<%@ page contentType="text/html;charset=utf-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<%@ include file="jsp/common-header.jsp"%>
	<%@ include file="jsp/common-nav.jsp"%>
	<link href="css/bwizard.min.css" rel="stylesheet" />
</head>
<body>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IPoolsResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IAttemptStatusResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IUserGroupsResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IHostsResource"%>
	
    <%@page import="com.dp.bigdata.taurus.restlet.shared.PoolDTO"%> 
    <%@page import="com.dp.bigdata.taurus.restlet.shared.StatusDTO"%> 
    <%@page import="com.dp.bigdata.taurus.restlet.shared.UserGroupDTO"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.HostDTO"%>
    
   	<%
   		cr = new ClientResource(host + "pool");
   		IPoolsResource poolResource = cr.wrap(IPoolsResource.class);
   		cr.accept(MediaType.APPLICATION_XML);
   		ArrayList<PoolDTO> pools = poolResource.retrieve();
	   	int UNALLOCATED = 1;
	   		
	   	cr = new ClientResource(host + "host");
   		IHostsResource hostResource = cr.wrap(IHostsResource.class);
    	cr.accept(MediaType.APPLICATION_XML);
   		ArrayList<HostDTO> hosts = hostResource.retrieve();
	   		
	   	cr = new ClientResource(host + "status");
		IAttemptStatusResource attemptResource = cr.wrap(IAttemptStatusResource.class);
		cr.accept(MediaType.APPLICATION_XML);
		ArrayList<StatusDTO> statuses = attemptResource.retrieve();
			
		cr = new ClientResource(host + "group");
		IUserGroupsResource groupResource = cr.wrap(IUserGroupsResource.class);
		cr.accept(MediaType.APPLICATION_XML);
		ArrayList<UserGroupDTO> groups = groupResource.retrieve();
		String name = request.getParameter("appname");
		String path = request.getParameter("path");
		String ip = request.getParameter("ip");
		if(name==null){
			name="";
		}
		if(ip==null){
			ip="";
		}
   	%>
	<div class="container">
		<div id="wizard">
			<ol>
				<li>作业部署</li>
				<li>基本设置</li>
				<li>其他设置</li>
			</ol>
			<div id="deploy">
				<form id="deploy-form" class="form-horizontal">
  					<fieldset>
                    <legend>部署设置</legend>
					<div class="control-group">
						<label  class="control-label"  for="taskType">作业类型*</label>
						<div class="controls">
   						<select  id="taskType" name="taskType"  class="input-big  field" >
   							<option>default</option>
   							<option>hadoop</option>
							<option>spring</option>
						</select>
						<a href="about.jsp#config">帮助</a>
                      	</div>
                    </div>
                    <div id="jarAddress" style="display:none;">
						<div class="control-group">
            				<label class="control-label"  for="taskUrl">Jar包ftp地址*</label>
            				<div class="controls">
              					<input type="text" class="input-xxlarge field"  id="taskUrl" name="taskUrl"  placeholder="ftp://10.1.1.81/{project-name}/{date}/{jarName}">
            				</div>
          				</div>
          			</div>
          			
					<div id="hadoopName" style="display:none;">
						<div class="control-group">
            				<label class="control-label"  for="hadoopName">hadoop用户名*</label>
            				<div class="controls">
              					<input type="text" class="input-large field"  id="hadoopName" name="hadoopName"  placeholder="kerberos principle (wwwcron)">
								<a href="about.jsp#config">帮助</a>
            				</div>
          				</div>
          			</div>
          			
                    <div id="host"  class="control-group">
                    	<label class="control-label"  for="hostname">部署的机器*</label>
                    	<div class="controls">
    						<input type="text" id="hostname" name="hostname" class="input-big field"  value="<%=ip%>" placeholder="10.0.0.1">
    					</div>
					</div>
                    </fieldset>
				</form>
			</div>
				
			<div id="base">
			<form id="basic-form" class="form-horizontal">
			<fieldset>
					<legend>必要设置</legend>
             	<div class="control-group">
            		<label class="control-label"  for="taskName">名称*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field"  id="taskName" name="taskName" value="<%=name%>"  placeholder="作业的名称，可以作为被依赖的对象，不可修改">
            		</div>
          		</div>
          		<div class="control-group" style='display:none'>
            		<label class="control-label"  for="taskName">应用名称*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field"  id="appName" name="appName" value="<%=name%>"  >
            		</div>
          		</div>
          		<div id="mainClassCG" class="control-group">
            		<label class="control-label" for="mainClass">MainClass*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field required" id="mainClass" name="mainClass"  placeholder="mainClass">
            		</div>
          		</div>
                <div class="control-group">
            		<label class="control-label" for="crontab">Crontab*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field" id="crontab" name="crontab"  value="0 0 * * ?">
              			<a href="about.jsp#crontab">帮助</a>
            		</div>
          		</div>
          		
                <div class="control-group">
            		<label class="control-label" for="taskCommand">命令*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field" id="taskCommand" name="taskCommand"  placeholder="执行作业的命令">
              			<%if(path!=null&&!path.equals("")){
              				%>
              				<br/><span>提示:已部署的作业文件的路径为<%=path%></span>
              			<%}%>
            		</div>
          		</div>
          		
          		<div id="beanCG" class="control-group">
          		    <label class="control-label" for="taskCommand"></label>
          		    <div class="controls">
          		        <button id="addNewBeanbtn" class="btn btn-small">增加Bean</button>
          		        <button id="rmBeanbtn" class="btn btn-small" disabled>删除Bean</button>
            		</div>
          		</div>

                <div class="control-group">
            		<label class="control-label" for="proxyUser">以该用户身份运行（不可为root）*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field" id="proxyUser" name="proxyUser"  placeholder="执行作业的用户身份">
            		</div>
          		</div>
                <div class="control-group">
            		<label class="control-label" for="description">描述*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field" id="description" name="description" placeholder="请尽可能用中文描述作业的用途">
            		</div>
          		</div>
                <input type="text" class="field" style="display:none" id="creator" name="creator" value="<%=currentUser%>">
          	</fieldset>
			</form>	
			</div>
			<div  id="extention">
			<form id="extended-form" class="form-horizontal">
				<fieldset>
					<legend>可选设置</legend>
					<div class="control-group">
            			<label class="control-label" for="maxExecutionTime">最长执行时间（分钟）*</label>
            			<div class="controls">
              				<input type="number" class="input-small field" id="maxExecutionTime" name="maxExecutionTime" style="text-align:right" value=60>
            				<a href="about.jsp#config">帮助</a>
            			</div>
          			</div>
          			<div class="control-group">
            			<label class="control-label" for="dependency" >依赖</label>
            			<div class="controls">
              				<input type="text" class="input-large field" id="dependency" name="dependency" placeholder="dependency expression"  value="">
            				<a href="about.jsp#config">帮助</a>
            			</div>
          			</div>
          			<div class="control-group">
            			<label class="control-label" for="maxWaitTime">最长等待时间（分钟）*</label>
            			<div class="controls">
              				<input type="number" class="input-small field" id="maxWaitTime" name="maxWaitTime" style="text-align:right" value=60>
            				<a href="about.jsp#config">帮助</a>
            			</div>
          			</div>
          			
          			<div class="control-group">
            			<label class="control-label" for="retryTimes">重试次数*</label>
            			<div class="controls">
              				<input type="number" class="input-small field" id="retryTimes" name="retryTimes" style="text-align:right" value=0>
            				<a href="about.jsp#config">帮助</a>
            			</div>
          			</div>
          			<div class="control-group">
            			<label class="control-label" for="multiInstance">自动kill timeout实例*</label>
            			<div class="controls field" id="isAutoKill">
				            <input type="radio" value="1" name="isAutoKill" checked> 是
				            <input type="radio" value="0" name="isAutoKill"> 否
				            <span class="label">不要轻易修改，除非你确定其含义：<a href="about.jsp#config">帮助</a></span>
            			</div>
          			</div>
          			<p><span class="label">注意：使用以下配置项，你需要在用户<a href="user.jsp">用户设置</a>填写您的联系方式</span></p>
          			<br/>
          			<div class="control-group">
            			<label class="control-label">选择何时收到报警</label>
            			<div class="controls">
            					<%
            						for(StatusDTO status:statuses) {
            					    	if(status.getStatus().equals("FAILED")||status.getStatus().equals("TIMEOUT")) {
            					%>
    									<input type="checkbox" class="input-large field alertCondition" id="alertCondition" name="<%=status.getStatus()%>" checked="checked"> <%=status.getCh_status()%>
    								<%
    									} else {
    								%>
    									<input type="checkbox" class="input-large field alertCondition" id="alertCondition" name="<%=status.getStatus()%>"> <%=status.getCh_status()%>
    							<%
    								}}
    							%>
            			</div>
          			</div>
          			
          			<div class="control-group">
            			<label class="control-label"  for="alertType">选择报警方式</label>
            			<div class="controls">
              				<select class="input-small field" id="alertType" name="alertType">
              					<option id="1">邮件</option>
               					<option id="2">短信</option>
               					<option id="3">邮件和短信</option>
              				</select>		
            			</div>
          			</div>
          			
          			<div class="control-group">
            			<label class="control-label">选择报警接收人(分号分隔)</label>
            			<div class="controls">
              				<input type="text" class="input-large field" id="alertUser" name="alertUser"  value="<%=(String)session.getAttribute(com.dp.bigdata.taurus.web.servlet.LoginServlet.USER_NAME)%>;">
            			</div>
          			</div>
          			
          			<div class="control-group">
            			<label class="control-label">选择报警接收组(分号分隔)</label>
            			<div class="controls">
              				<input type="text" class="input-large field" id="alertGroup" name="alertGroup" placeholder="group name split with ;">
            			</div>
          			</div>
  				</fieldset>
  			</form>	
			</div>
		</div>
	</div>
    <div id="confirm" class="modal hide fade">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" >&times;</button>
        <h3 id="id_header"></h3>
      </div>
      <div class="modal-body">
        <p id="id_body"></p>
      </div>
      <div class="modal-footer">
      </div>
    </div>
   
    <script type="text/javascript">  
      	var userList="",groupList="",ipList="";
      	<%for(UserDTO user:users) {%>
      		userList=userList+",<%=user.getName()%>";
      	<%}%>
      	<%for(UserGroupDTO group:groups) {%>
      		groupList=groupList+",<%=group.getName()%>";
  		<%}%>
  		<% for(HostDTO hostDto:hosts) {%>
  			ipList=ipList+",<%=hostDto.getName()%>";
		<%}%>
		ipList = ipList.substr(1);
      	userList = userList.substr(1);
      	groupList = groupList.substr(1);
		
    </script> 
	<script src="js/bwizard.js" type="text/javascript"></script>
    <script src="js/jquery.validate.min.js" type="text/javascript"></script>
    <script src="js/taurus_validate.js" type="text/javascript"></script>
    <script src="js/jquery.autocomplete.min.js" type="text/javascript"></script>
	<script src="js/task.js" type="text/javascript"></script>
</body> 
</html>