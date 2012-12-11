<html lang="en">
<head>
	<%@ include file="jsp/common-header.jsp"%>
    <link rel="stylesheet" type="text/css" href="css/DT_bootstrap.css">
</head>
<body data-spy="scroll">
	<%@ include file="jsp/common-nav.jsp"%>
    <%@ include file="jsp/common-api.jsp"%>
    <%@page import="org.restlet.data.MediaType, org.restlet.resource.ClientResource,
		com.dp.bigdata.taurus.restlet.resource.IAttemptsResource,
		com.dp.bigdata.taurus.restlet.shared.AttemptDTO,
		java.text.SimpleDateFormat,
		java.util.ArrayList"%>

	<div class="container" style="margin-top: 10px">
		<table cellpadding="0" cellspacing="0" border="0"
			class="table table-striped table-format" id="example">
			<thead>
				<tr>
					<th>ID</th>
					<th>启动时间</th>
					<th>结束时间</th>
					<th>调度时间</th>
                    <th>IP</th>
					<th>返回值</th>
					<th>状态</th>
					<th>-</th>
				</tr>
			</thead>
			<tbody>
				<%
					String taskID = request.getParameter("taskID");
					String url =  host + "attempt?task_id=" + taskID; 
					ClientResource cr = new ClientResource(url);
					cr.setRequestEntityBuffering(true);
					IAttemptsResource resource = cr.wrap(IAttemptsResource.class);
					cr.accept(MediaType.APPLICATION_XML);
					ArrayList<AttemptDTO> attempts = resource.retrieve();
					
					SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
					for(AttemptDTO dto : attempts){
						String state = dto.getStatus();
				%>
				<tr id="<%=dto.getAttemptID()%>">
					<td><%=dto.getId()%></td>
                    <%if(dto.getStartTime()!=null){%>
						<td><%=formatter.format(dto.getStartTime())%></td>
                    <%}else{%>
                    	<td>NULL</td>
                    <%}%> 
                    <%if(dto.getEndTime()!=null){%>
						<td><%=formatter.format(dto.getEndTime())%></td>
                    <%}else{%>
                    	<td>NULL</td>
                    <%}%>
                    <%if(dto.getScheduleTime()!=null){%>
						<td><%=formatter.format(dto.getScheduleTime())%></td>
                    <%}else{%>
                    	<td>NULL</td>
                    <%}%> 
                    <td><%=dto.getExecHost()%></td>
					<td><%=dto.getReturnValue()%></td>
					<td><%if(state.equals("RUNNING")){%>
                    	<span class="label label-info"><%=state%></span>
                        <%}else if(state.equals("SUCCEEDED")){%>
                        <span class="label label-success"><%=state%></span>
                        <%}else{%>
                        <span class="label label-important"><%=state%></span>
                        <%}%>              
                    </td>

					<td>
						<div class="btn-group">
							<button class="btn  btn-primary btn-small dropdown-toggle" data-toggle="dropdown">
								Action<span class="caret"></span>
							</button>
							<ul class="dropdown-menu">
                            	<%if(state.equals("RUNNING")){%>
								<li><a href="#confirm" onClick="action($(this).parents('tr').find('td')[0].textContent)">Kill</a>
								</li>
                                <%}else {%>
								<li><a href="#log">日志</a>
								</li>
                                <%}%>
							</ul>
						</div></td>
				</tr>
              <% } %>
			</tbody>
		</table>
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
    
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/DT_bootstrap.js"></script>
    <script type="text/javascript" charset="utf-8" language="javascript" src="js/attempt.js"></script>
</body>
</html>
