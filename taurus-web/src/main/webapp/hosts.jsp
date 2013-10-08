<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=utf-8"%>
<html lang="en">
<head>
<%@ include file="jsp/common-header.jsp"%>
<%@ include file="jsp/common-nav.jsp"%>
</head>
<body data-spy="scroll">
	
	<%@page import="org.restlet.resource.ClientResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IHostsResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.shared.HostDTO"%>
	<%@page import="java.util.Map"%>
	<%@page import="java.util.HashMap"%>
	<%@page import="org.restlet.data.MediaType"%>
	<%@page import="java.text.SimpleDateFormat"%>
	<%
	cr = new ClientResource(host + "host");
	IHostsResource hostResource = cr.wrap(IHostsResource.class);
	cr.accept(MediaType.APPLICATION_XML);
	ArrayList<HostDTO> hosts = hostResource.retrieve();
	%>

	<div class="row-fluid">
		<div class="span2">
      		<%@include file="hostList.jsp"%>
      	</div>
	  	<div class="span10">
	  		<%
	  			String statusCode = (String)(request.getAttribute("statusCode"));
	  			String hostName = request.getParameter("hostName");
	  			String op = request.getParameter("op");
	  			Map<String,String> maps = new HashMap<String,String>();
	  			maps.put("up","上线");
	  			maps.put("dowan","下线");
	  			maps.put("restart","重启");
	  			maps.put("update","升级");
	  			String opChs = maps.get(op);
				if(opChs == null){
					opChs = "操作";
				}
	  			if("200".equals(statusCode)){
	  		%>
	  				<div id="alertContainer" class="container"><div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">×</button> <%=opChs %>成功</div></div>
	  		<%
	  			} else if("500".equals(statusCode)){
	  		%>
	  				<div id="alertContainer" class="container"><div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">×</button> <%=opChs %>失败</div></div>
	  		<%  }
	  		for (HostDTO dto : hosts) {
	  			if(dto.getName()!= null && dto.getName().equals(hostName)){
	  		%>
	  		<ul  class="nav nav-tabs">
            	<li class="active"><a href="#state" data-toggle="tab">状态</a></li>
            	<li><a href="#config" data-toggle="tab">配置</a></li>
            	<li><a href="#statistics" data-toggle="tab">统计</a></li>
        	</ul>

       		<div class="tab-content">
            	<div class="tab-pane active" id="state">
           	<table class="table" id="host_state">
              <thead>
                <tr>
                  <th>#</th>
                  <th>属性</th>
                  <th>值</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr class="success">
                  <td>1</td>
                  <td>机器IP</td>
                  <td><%=hostName%></td>
                  <td></td>
                </tr>
                <tr class="error">
                  <td>2</td>
                  <td>机器状态</td>
                  <%if(dto.isOnline()){%>
                  	<td>在线</td>
                  	<td><a id="down" class="btn btn-primary btn-small" href="updateHost?hostName=<%=hostName%>&op=down">下线</a></td>
                  <%} %><%else{%>
                  	<td>下线</td>
                  	<td><a id="up" class="btn btn-primary btn-small" href="updateHost?hostName=<%=hostName%>&op=up">上线</a></td>
                  <%} %>
                </tr>
                <tr class="warning">
                  <td>3</td>
                  <td>心跳状态</td>
                  <%if(dto.isConnected()){%><td>正常</td>
                  	<td><a id="restart" class="btn btn-primary btn-small" href="updateHost?hostName=<%=hostName%>&op=restart">重启</a></td>
                  <%} else{%>   <td>异常  </td>
                  <td></td>
                  <%} %>
                </tr>
                <tr class="info">
                  <td>4</td>
                  <td>版本</td>
                  <td><%=dto.getName() %></td>
                  <td><a id="restart" class="btn btn-primary btn-small" href="updateHost?hostName=<%=hostName%>&op=update">升级</a></td>
                </tr>
              </tbody>
            </table>
            	</div>
            	<div class="tab-pane" id="config">
            		
            	</div>
            	<div class="tab-pane" id="statistics">

				</div>
        	</div>
	  		<% }}%>
	  	</div>
</div>

</body>
    <script src="js/hosts.js" type="text/javascript"></script>
</html>