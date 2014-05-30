<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=utf-8" %>
<html lang="en">
<head>
	<%@ include file="jsp/common-header.jsp"%>
	<%@ include file="jsp/common-nav.jsp"%>
    <link rel="stylesheet" type="text/css" href="css/DT_bootstrap.css">
    <style type="text/css">.fixLength-table{table-layout:fixed}
		.fixLength-td{overflow:hidden;text-overflow:ellipsis;white-space:nowrap}</style>
</head>
<body data-spy="scroll">
	<%@page import="com.dp.bigdata.taurus.restlet.resource.ITasksResource"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.TaskDTO"%>
    <%@page import="java.text.SimpleDateFormat"%>
	<div class="container" style="margin-top: 10px">
        <div id="alertContainer" class="container"></div>
          <div class="row">
            <div class="span3 hide">
                <div class="well well-large">
                  <form class="form-horizontal selector-form">
					<div class="control-group hide">
						<label class="control-label">任务组</label>
						
					<div class="controls">
							<select id="selector-task-group-id">
								<option value="">--选择全部--</option>
								<option value="1">wormhole</option>
								<option value="2">mid/dim</option>
								<option value="3">dm</option>
								<option value="4">rpt</option>
								<option value="5">mail</option>
								<option value="6">dw</option>
							</select>
						</div></div>
					<div class="control-group">
						<label class="control-label">创建人</label>
						<div class="controls">
							<select id="selector-cycle">
								
							</select>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">任务名称</label>
						<div class="controls">
							<input type="text" id="selector-task-name" placeholder="模糊查询...">
						</div>
					</div>
					<div class="control-group">
						<div class="controls">
							<button type="submit" id="search-btn" class="btn btn-large btn-primary pull-right">开始查询</button>
						</div>
					</div>
				</form>
                </div>
    	 	</div>
         <div class="span12">
         <ul class="breadcrumb">
				<li><a href="./index.jsp">首页</a> <span class="divider">/</span></li>
				<li><a href="#" class="active">调度中心</a> <span class="divider">/</span></li>
		 </ul>
		<table cellpadding="0" cellspacing="0" border="0"
			class="fixLength-table table table-striped table-format table-hover" id="example">
			<thead>
				<tr>
					<th class="hide">ID</th>
					<th width = "15%">名称</th>
					<th>IP</th>
					<th>调度人</th>
					<th>调度身份</th>
					<th class="hide">组</th>
					<th>创建时间</th>
					<th>Crontab</th>		
                    <th>状态</th>
					<th class="center">-</th>
					<th class="center">-</th>
				</tr>
			</thead>
			<tbody>
				<%  String task_api = host + "task";
					String name = request.getParameter("name");
					String path = request.getParameter("path");
					String appname = request.getParameter("appname");
					if(name!=null && !name.isEmpty()){
						task_api = task_api + "?name=" + name;
					} else if(appname !=null){
						task_api = task_api + "?appname=" + appname;
					} else if(currentUser != null){
						task_api = task_api + "?user=" + currentUser;
					}
					if(path!=null&&!path.equals("")){
        		%>
        				<span  style="color:red">提示:已部署的作业文件的路径为<%=path%></span>
        		<%	}
        			cr = new ClientResource(task_api);
                    ITasksResource resource = cr.wrap(ITasksResource.class);
                    cr.accept(MediaType.APPLICATION_XML);
                    ArrayList<TaskDTO> tasks = resource.retrieve();
					SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for(TaskDTO dto : tasks){
						String state = dto.getStatus();
						boolean isRunning = true;
						if(state.equals("SUSPEND")){  isRunning = false;  }
						if(isRunning) {
                %>
                <tr id="<%=dto.getTaskid()%>">
                <% } else { %>
                <tr id="<%=dto.getTaskid()%>" class="error" >
                <%}%>
                    <td class="hide"><%=dto.getTaskid()%></td>
                    <td class="fixLength-td" ><%=dto.getName()%></td>
                    <td><%=dto.getHostname()%></td>
                    <td><%=dto.getCreator()%></td>
					<td><%=dto.getProxyuser()%></td>
                    <td class="hide">arch(mock)</td>
                    <td><%=formatter.format(dto.getAddtime())%></td>
                    <td><%=dto.getCrontab()%></td>
                    <td><%if(isRunning){%>
                    	<span class="label label-info"><%=state%></span>
                        <%}else{%>
                        <span class="label label-important"><%=state%></span>
                        <%}%>              
                    </td>
                    <td>
                       <div class="btn-group">
                        <button class="btn btn-primary btn-small dropdown-toggle" data-toggle="dropdown">Action<span class="caret"></span></button>
                        <ul class="dropdown-menu">
                          <li><a href="#confirm" onClick="action($(this).parents('tr').find('td')[0].textContent,1)">删除</a></li>
                          <% if(isRunning) {%>
                          <li><a href="#confirm" onClick="action($(this).parents('tr').find('td')[0].textContent,2)">暂停</a></li>               
                          <%}else { %>
                          <li><a href="#confirm" onClick="action($(this).parents('tr').find('td')[0].textContent,2)">恢复</a></li>   
                          <%}%>
                          <li><a href="#confirm" onClick="action($(this).parents('tr').find('td')[0].textContent,3)">执行</a></li>
                          <li><a class="detailBtn" href="task_form.jsp?task_id=<%=dto.getTaskid()%>">详细</a></li>
                        </ul>
                      </div>
                    </td>
                    <td><a id="attempts" class="btn btn-primary btn-small"  href="attempt.jsp?taskID=<%=dto.getTaskid()%>">运行历史</a></td>
                 </tr>
               <% } %>
			</tbody>
		</table>
            </div>
  		</div>
	</div>

	<div id="confirm" class="modal hide fade">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3 id="id_header"></h3>
      </div>
      <div class="modal-body">
        <p id="id_body"></p>
      </div>
      <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal" aria-hidden="true">取消</a>
        <a href="#" class="btn btn-danger" onClick="action_ok()">确定</a>
      </div>
    </div>
    
    <!-- detailModal -->
	<div id="detailModal" class="modal hide fade" style="width: 600px">
	</div>
    
	<script type="text/javascript" charset="utf-8" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" charset="utf-8" src="js/DT_bootstrap.js"></script>
    <script type="text/javascript" charset="utf-8" src="js/schedule.js"></script>
    <script src="js/jquery.validate.min.js" type="text/javascript"></script>
    <script src="js/taurus_validate.js" type="text/javascript"></script>
</body>
</html>