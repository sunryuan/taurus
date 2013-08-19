	<%@ page contentType="text/html;charset=utf-8"%>
	<%@page import="org.restlet.resource.ClientResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IHostsResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.shared.HostDTO"%>
	<%@page import="java.util.ArrayList"%>
	<%@page import="org.restlet.data.MediaType"%>
	<%@page import="java.text.SimpleDateFormat"%>
	<script type="text/javascript">
		$(document).ready(function() {
			$('li[id="#host_<%=request.getParameter("hostName")%>"]').addClass("active");
		});
	</script>
		 <div class="well sidebar-nav" >
          <ul class="nav nav-list">
	       <li class='nav-header'><h4>所有机器</h4></li>
	       <%
	    		for (HostDTO dto : hosts) {
			%>
					<li class="text-right" id="host_<%=dto.getName()%>"><a href="hosts.jsp?hostName=<%=dto.getName()%> ">
					<%if(!dto.isOnline()){ %>
					<font color=grey><strong><%=dto.getName()%></strong></font>
					<%}else if(dto.isConnected()){ %>
					<font color=green><strong><%=dto.getName()%></strong></font>
					<%} else{ %>
					<font color=red><strong><%=dto.getName()%></strong></font>
					<%} %>
					</a></li>
			<%
	    		}
			%>
	     
          </ul>
		 </div>
	  
	<style>
		.nav-list  li  a{
			padding:2px 15px;
		}
		.nav li  +.nav-header{
			margin-top:2px;
		}
		.nav-header{
			padding:5px 3px;
		}
		.row-fluid .span2{
			width:12%;
		}
	</style>

	  	
