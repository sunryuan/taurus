<html lang="en">
<head>
	<%@ include file="jsp/sub-header.jsp"%>
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
			class="table table-striped table-bordered" id="example">
			<thead>
				<tr>
					<th>ID</th>
					<th>启动时间</th>
					<th>结束时间</th>
					<th>调度时间</th>
					<th>返回值</th>
					<th>状态</th>
					<th>IP</th>
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
				%>
				<tr>
					<td><%=dto.getAttemptID()%></td>
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
					<td><%=dto.getReturnValue()%></td>
					<td><%=dto.getStatus()%></td>
					<td><%=dto.getExecHost()%></td>
					<td>
						<div class="btn-group">
							<button class="btn  btn-primary dropdown-toggle" data-toggle="dropdown">
								<span class="caret"></span>
							</button>
							<ul class="dropdown-menu">
								<li><a href="#suspend">Kill</a>
								</li>
								<li><a href="#suspend">日志</a>
								</li>
							</ul>
						</div></td>
				</tr>
              <% } %>
			</tbody>
		</table>
	</div>

	<%@ include file="jsp/common-footer.jsp"%>
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/DT_bootstrap.js"></script>
</body>
</html>
