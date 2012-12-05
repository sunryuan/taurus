<html lang="en">
<head>
	<%@ include file="jsp/sub-header.jsp"%>
</head>
<body data-spy="scroll">
	<%@ include file="jsp/common-nav.jsp"%>
    <%@page import="org.restlet.resource.ClientResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.ITasksResource"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.TaskDTO"%>
    <%@page import="java.util.ArrayList"%>
    <%@page import="org.restlet.data.MediaType"%>
    <%@page import="java.text.SimpleDateFormat"%>

	<div class="container" style="margin-top: 10px">
		<table cellpadding="0" cellspacing="0" border="0"
			class="table table-striped table-bordered" id="example">
			<thead>
				<tr>
					<th>ID</th>
					<th>名称</th>
					<th>调度人</th>
					<th>组</th>
					<th>创建时间</th>
					<th>Crontab</th>
					<th class="center">-</th>
					<th class="center">-</th>
				</tr>
			</thead>
			<tbody>
				<% ClientResource cr = new ClientResource("http://192.168.26.87:8182/api/task");
                    ITasksResource resource = cr.wrap(ITasksResource.class);
                     cr.accept(MediaType.APPLICATION_XML);
                    ArrayList<TaskDTO> tasks = resource.retrieve();
					SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for(TaskDTO dto : tasks){
                %>
                <tr>
                    <td><%=dto.getTaskid()%></td>
                    <td><%=dto.getName()%></td>
                    <td><%=dto.getCreator()%></td>
                    <td>arch(mock)</td>
                    <td><%=formatter.format(dto.getAddtime())%></td>
                    <td><%=dto.getCrontab()%></td>
                    <td>
                       <div class="btn-group">
                        <button class="btn btn-small">Action</button>
                        <button class="btn dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
                        <ul class="dropdown-menu">
                          <li><a href="#delete?<%=dto.getTaskid()%>" onClick="">删除</a></li>
                          <li><a href="#suspend?<%=dto.getTaskid()%>" onClick="">暂停</a></li>
                          <li><a href="#instant?<%=dto.getTaskid()%>" onClick="">立即执行</a></li>
                          <li><a href="#info?<%=dto.getTaskid()%>" onClick="">详细</a></li>
                        </ul>
                      </div>
                    </td>
                    <td><button id="attempts" class="btn"  onClick="javascript:window.location.href='attempt.jsp?taskID=<%=dto.getTaskid()%>'">运行历史</button></td>
                 </tr>
               <% } %>
			</tbody>
		</table>
	</div>

    <div id="taskModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h2 id="myModalLabel" class="form-signin-heading">登陆</h2>
      </div>
      <form method="get" action="/login">
          <div class="modal-body">
            <input type="text" name="username" class="input-block-level" placeholder="Email address"/> 
            <input type="password" name="password" class="input-block-level" placeholder="Password"/>
    
          </div>
          <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
            <button class="btn btn-primary" type="submit">确定</button>
          </div>
      </form>
     </div>
	<%@ include file="jsp/common-footer.jsp"%>
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/DT_bootstrap.js"></script>
</body>
</html>