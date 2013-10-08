<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="jsp/common-nav.jsp"%>

<%@page import="com.dp.bigdata.taurus.restlet.resource.ITaskResource"%>
<%@page	import="com.dp.bigdata.taurus.restlet.resource.IAttemptStatusResource"%>
<%@page import="com.dp.bigdata.taurus.restlet.resource.IUsersResource"%>
<%@page	import="com.dp.bigdata.taurus.restlet.resource.IUserGroupsResource"%>

<%@page import="com.dp.bigdata.taurus.restlet.shared.TaskDTO"%>
<%@page import="com.dp.bigdata.taurus.restlet.shared.StatusDTO"%>
<%@page import="com.dp.bigdata.taurus.restlet.shared.UserGroupDTO"%>
<%@page import="java.util.ArrayList"%>

<%
	String []types={"hadoop","spring","other"};

	cr = new ClientResource(host + "status");
	IAttemptStatusResource attemptResource = cr.wrap(IAttemptStatusResource.class);
	cr.accept(MediaType.APPLICATION_XML);
	ArrayList<StatusDTO> statuses = attemptResource.retrieve();
	
	cr = new ClientResource(host + "group");
	IUserGroupsResource groupResource = cr.wrap(IUserGroupsResource.class);
	cr.accept(MediaType.APPLICATION_XML);
	ArrayList<UserGroupDTO> groups = groupResource.retrieve(); 	
	cr = new ClientResource(host + "task/" + request.getParameter("task_id"));
	ITaskResource taskResource = cr.wrap(ITaskResource.class);
	cr.accept(MediaType.APPLICATION_XML);
	TaskDTO dto = taskResource.retrieve();
%>
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">x</button>
		<h3 id="myModalLabel">Task详细信息</h3>
	</div>
	<div class="modal-body">
		<form id="form_<%=dto.getTaskid()%>" class="form-horizontal task-form">
			<fieldset>
				<div id="host" class="control-group">
					<label class="control-label" for="host">部署的机器*</label>
					<div class="controls">
						<input type="text" id="hostname" name="hostname"
							class="input-big field" value="<%=dto.getHostname()%>" disabled>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="taskType">选择作业类型*</label>
					<div class="controls">
						<input type="text" id="taskType"
								name="taskType" class="input-big"  value="<%=dto.getType()%>" readonly>
					</div>
				</div>
				<%
					if(dto.getType() != null && dto.getType().equals("hadoop")){
				%>
				<div class="control-group">
					<label class="control-label" for="hadoopName">hadoop用户名*</label>
					<div class="controls">
						<input type="text" class="input-big field" id="hadoopName"
							name="hadoopName" value="<%=dto.getHadoopName()%>" disabled>
					</div>
				</div>
				<%
					}
				%>
				
				<div class="control-group">
					<label class="control-label" for="taskName">名称*</label>
					<div class="controls">
						<input type="text" class="input-big" id="taskName"
							name="taskName" value="<%=dto.getName()%>" readonly>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="crontab">Crontab*</label>
					<div class="controls">
						<input type="text" class="input-xxlarge field " id="crontab"
							name="crontab" value="<%=dto.getCrontab()%>" disabled>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="taskCommand">命令*</label>
					<div class="controls">
						<input type="text" class="input-xxlarge field " id="taskCommand"
							name="taskCommand" value="<%=dto.getHtmlCommand()%>" disabled>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="proxyUser">以该用户身份运行（不可为root）*</label>
					<div class="controls">
						<input type="text" class="input-xxlarge field " id="proxyUser"
							name="proxyUser" value="<%=dto.getProxyuser()%>" disabled>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="description">描述*</label>
					<div class="controls">
						<input type="text" class="input-xxlarge field " id="description"
							name="description" value="<%=dto.getDescription()%>" disabled>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">最长执行时间（分钟）*</label>
					<div class="controls">
						<input type="number" class="input-small field "
							id="maxExecutionTime" name="maxExecutionTime"
							style="text-align: right" value="<%=dto.getExecutiontimeout()%>" disabled>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">依赖</label>
					<div class="controls">
						<input type="text" class="input-large field " id="dependency"
							name="dependency" placeholder="dependency expression"
							value="<%=dto.getDependencyexpr()%>" disabled>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">最长等待时间（分钟）*</label>
					<div class="controls">
						<input type="number" class="input-small field " id="maxWaitTime"
							name="maxWaitTime" style="text-align: right"
							value=<%=dto.getWaittimeout()%> disabled>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">重试次数*</label>
					<div class="controls">
						<input type="number" class="input-small field " id="retryTimes"
							name="retryTimes" style="text-align: right"
							value=<%=dto.getRetrytimes()%> disabled>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">自动kill timeout实例*</label>
					<div class="controls field" id="isAutoKill">
						<%
							if(dto.isAutoKill()){
						%>
						<input type="radio" value="1" name="isAutoKill" checked>是
						<input type="radio" value="0" name="isAutoKill">否
						<%
							}else {
						%>
						<input type="radio" value="1" name="isAutoKill">是
						<input type="radio" value="0" name="isAutoKill" checked>否
						<%
							}
						%>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">选择何时收到报警</label>
					<div class="controls">
						<%
							String conditionStr = dto.getAlertRule().getConditions();
							for(StatusDTO status:statuses) {
								if(conditionStr != null && conditionStr.contains(status.getStatus())) {
						%>
						<input type="checkbox" class="input-large field alertCondition"
							id="alertCondition" name="<%=status.getStatus()%>"
							checked="checked" disabled>
						<%=status.getCh_status()%>
						<%
							} else {
						%>
						<input type="checkbox" class="input-large field alertCondition"
							id="alertCondition" name="<%=status.getStatus()%>" disabled>
						<%=status.getCh_status()%>
						<%
							}}
						%>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="alertType">选择报警方式</label>
					<div class="controls">
						<select class="input-small field" id="alertType" name="alertType"
							disabled>
							<%
								if(dto.getAlertRule().getHasmail() && dto.getAlertRule().getHassms()) {
							%>
							<option id="1">邮件</option>
							<option id="2">短信</option>
							<option id="3" selected="selected">邮件和短信</option>
							<%
								} else if(!dto.getAlertRule().getHasmail() && dto.getAlertRule().getHassms()) {
							%>
							<option id="1">邮件</option>
							<option id="2" selected="selected">短信</option>
							<option id="3">邮件和短信</option>
							<%
								} else {
							%>
							<option id="1" selected="selected">邮件</option>
							<option id="2">短信</option>
							<option id="3">邮件和短信</option>
							<%
								}
							%>
						</select>
					</div>
				</div>


				<div class="control-group">
					<label class="control-label" for="alertUser">选择报警接收人(分号分隔)</label>
					<div class="controls">
						<input type="text" class="input-large field" id="alertUser"
							name="alertUser" <%if(dto.getAlertRule().getUserid() != null){%>
							value="<%=dto.getAlertRule().getUserid()%>" <%}%> disabled>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="alertGroup">选择报警接收组(分号分隔)</label>
					<div class="controls">
						<input type="text" class="input-large field" id="alertGroup"
							name="alertGroup"
							<%if(dto.getAlertRule().getGroupid() != null){%>
							value="<%=dto.getAlertRule().getGroupid()%>" <%}%> disabled>
					</div>
				</div>
				<input type="text" class="field" style="display: none" id="creator"
					name="creator"
					value="<%=currentUser%>">
			</fieldset>
		</form>
	</div>
	<div class="modal-footer">
		<button id="updateBtn" class="btn btn-primary"
			onClick="action_update('<%=dto.getTaskid()%>')"
			data-loading-text='正在保存..'>修改</button>
		<button class="btn" data-dismiss="modal">关闭</button>
	</div>


<div id="modal-confirm" class="modal hide fade">
	<div class="modal-header">
		<a href="#" class="close">&times;</a>
		<h3>修改成功</h3>
	</div>
	<div class="modal-body">
		<p>您的修改已经生效！</p>
	</div>
	<div class="modal-footer">
		<button id="confirmBtn" class="btn btn-primary"
			onClick="window.location.reload();">确定</button>
	</div>
</div>
<script type="text/javascript">  
      	var userList="",groupList="";
      	<%for(UserDTO user:users) {%>
      		userList=userList+",<%=user.getName()%>";
      	<%}%>
      	<%for(UserGroupDTO group:groups) {%>
      		groupList=groupList+",<%=group.getName()%>";
<%}%>
	userList = userList.substr(1);
	groupList = groupList.substr(1);
</script>
<script src="js/jquery.autocomplete.min.js" type="text/javascript"></script>

