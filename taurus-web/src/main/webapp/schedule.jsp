<html lang="en">
<head>
	<%@ include file="jsp/sub-header.jsp"%>
</head>
<body data-spy="scroll">
	<%@ include file="jsp/common-nav.jsp"%>
    <%@ include file="jsp/common-api.jsp"%>
    <%@page import="org.restlet.resource.ClientResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.ITasksResource"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.TaskDTO"%>
    <%@page import="java.util.ArrayList"%>
    <%@page import="org.restlet.data.MediaType"%>
    <%@page import="java.text.SimpleDateFormat"%>


	<div class="container" style="margin-top: 10px">
        <div id="alertContainer" class="container">
        </div>
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
				<% ClientResource cr = new ClientResource(host + "task");
                    ITasksResource resource = cr.wrap(ITasksResource.class);
                     cr.accept(MediaType.APPLICATION_XML);
                    ArrayList<TaskDTO> tasks = resource.retrieve();
					SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for(TaskDTO dto : tasks){
                %>
                <tr id="<%=dto.getTaskid()%>" class="in" >
                    <td><%=dto.getTaskid()%></td>s
                    <td><%=dto.getName()%></td>
                    <td><%=dto.getCreator()%></td>
                    <td>arch(mock)</td>
                    <td><%=formatter.format(dto.getAddtime())%></td>
                    <td><%=dto.getCrontab()%></td>
                    <td>
                       <div class="btn-group">
                        <button class="btn btn-primary dropdown-toggle" data-toggle="dropdown">Action<span class="caret"></span></button>
                        <ul class="dropdown-menu">
                          <li><a href="#confirm" onClick="action($(this).parents('tr').find('td')[0].textContent,'delete')">删除</a></li>
                          <li><a href="#confirm" onClick="action($(this).parents('tr').find('td')[0].textContent,'suspend')">暂停</a></li>
                          <li><a href="#confirm" onClick="action($(this).parents('tr').find('td')[0].textContent,'execute')">立即执行</a></li>
                          <li><a href="#confirm" onClick="">详细</a></li>
                        </ul>
                      </div>
                    </td>
                    <td><button id="attempts" class="btn btn-primary"  onClick="javascript:window.location.href='attempt.jsp?taskID=<%=dto.getTaskid()%>'">运行历史</button></td>
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


    <%@ include file="jsp/common-footer.jsp"%>
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/DT_bootstrap.js"></script>
    
    <script>
		
		var taskID;
		var action_type;
		var action_chinese;
        function action(id,action)
        {
			taskID = id;
			action_type = action;
			if(action == 'delete'){
				action_chinese = '删除';
				$("#id_header").html("删除");
				$("#id_body").html("确定要删除任务<strong>"+ id + "</strong>");

			}else if(action == 'suspend'){
				action_chinese = '暂停';
				$("#id_header").html("暂停");
				$("#id_body").html("确定要暂停任务<strong>"+ id + "</strong>");
			}else if(action == 'execute'){
				action_chinese = '立刻执行';
				$("#id_header").html("立刻执行");
				$("#id_body").html("确定要立刻执行任务<strong>"+ id + "</strong>");
			}
			$("#confirm").modal('toggle');
        }
		
		function action_ok(){
			$.ajax({
			    url: "./tasks",
				data: {action:action_type,id:taskID},
				type: 'POST',
				statusCode:{
					200 : function(){
						    $("#alertContainer").html('<div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>' + action_chinese + '成功</strong></div>');
							$(".alert").alert();
							$('#confirm').modal("hide");
							$('#' + taskID).remove();
						},
					400 : function(){
							$("#alertContainer").html('<div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>' + action_chinese + '失败</strong></div>');
							$(".alert").alert();
							$('#confirm').modal("hide");
						}
				}
			})
		}
    </script>
</body>
</html>