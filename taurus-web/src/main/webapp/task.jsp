<%@ page contentType="text/html;charset=utf-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<%@ include file="jsp/common-header.jsp"%>
	<link href="css/bwizard.min.css" rel="stylesheet" />
</head>
<body>
	<%@ include file="jsp/common-nav.jsp"%>
    <%@ include file="jsp/common-api.jsp"%>
    <%@page import="org.restlet.resource.ClientResource"%>
	<%@page import="com.dp.bigdata.taurus.restlet.resource.IPoolsResource"%>
    <%@page import="com.dp.bigdata.taurus.restlet.shared.PoolDTO"%>
    <%@page import="java.util.ArrayList"%>
    <%@page import="org.restlet.data.MediaType"%>
    <%@page import="java.text.SimpleDateFormat"%>
   	<% ClientResource cr = new ClientResource(host + "pool");
   		IPoolsResource resource = cr.wrap(IPoolsResource.class);
    	cr.accept(MediaType.APPLICATION_XML);
   		ArrayList<PoolDTO> pools = resource.retrieve();
   		int UNALLOCATED = 1;
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
						<label class="control-label"  for="autodeploy">部署方式</label>
						<div class="controls">
      						<input type="checkbox"  id="autodeploy">使用Pool部署
	      				</div>
      				</div>

                    <div id="host"  class="control-group">
                    	<label class="control-label"  for="host">部署的机器*</label>
                    	<div class="controls">
    						<input type="text" id="host" name="hostname" class="input-big field"  placeholder="10.0.0.1">
    					</div>
					</div>
                    
                    <div id="poolDeployDiv" style="display: none;">
                    	<div class="control-group">
                   			<label   class="control-label"  for="poolId">部署的Pool*</label>
                            <div class="controls">
    						<select id="poolId" name="poolId"  class="input-big  field" >
                        		<% for(PoolDTO dto : pools){
                        	    	if(dto.getId()!=UNALLOCATED){%>
                            		<option><%=dto.getName()%></option>
								<%}}%>
							</select>
                       		</div>
                        </div>

						<div class="control-group">
							<label  class="control-label"  for="taskType">选择作业类型*</label>
							<div class="controls">
    						<select  id="taskType" name="taskType"  class="input-big  field" >
    							<option>hadoop</option>
								<option>wormhole</option>
								<option>其他</option>
							</select>
                       		</div>
                       	</div>

                        <div class="control-group">
							<label class="control-label"  for="uploadFile">上传作业*</label>
                        	<div id="fileDiv"  class="controls">
								<input type="file" id="uploadFile" name="uploadFile" class="input-big  field"/>
                        	</div>
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
              			<input type="text" class="input-xxlarge field"  id="taskName" name="taskName"  placeholder="name">
            		</div>
          		</div>
                <div class="control-group">
            		<label class="control-label" for="crontab">Crontab*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field" id="crontab" name="crontab"  value="0 0 * * ?">
            		</div>
          		</div>
                <div class="control-group">
            		<label class="control-label" for="taskCommand">命令*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field" id="taskCommand" name="taskCommand"  placeholder="command">
            		</div>
          		</div>
                <div class="control-group">
            		<label class="control-label" for="proxyUser">以该用户身份运行（默认nobody）</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field" id="proxyUser" name="proxyUser"  placeholder="userName">
            		</div>
          		</div>
                <div class="control-group">
            		<label class="control-label" for="taskMail">报警收件人邮件（逗号分隔）*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field" id="taskMail" name="taskMail" placeholder="example1,example2(default add @dianping)">
            		</div>
          		</div>
                <div class="control-group">
            		<label class="control-label" for="description">描述*</label>
            		<div class="controls">
              			<input type="text" class="input-xxlarge field" id="description" name="description" value="" placeholder="description of task">
            		</div>
          		</div>
                <input type="text" class="field" style="display:none" id="creator" name="creator" value="<%=(String)session.getAttribute(com.dp.bigdata.taurus.web.servlet.LoginServlet.USER_NAME)%>">
          	</fieldset>
			</form>	
			</div>
			<div  id="extention">
			<form id="extended-form" class="form-horizontal">
				<fieldset>
					<legend>可选设置</legend>
					<div class="control-group">
            			<label class="control-label">最长执行时间（分钟）*</label>
            			<div class="controls">
              				<input type="number" class="input-small field" id="maxExecutionTime" name="maxExecutionTime" style="text-align:right" value=60>
            			</div>
          			</div>
          			<div class="control-group">
            			<label class="control-label">依赖</label>
            			<div class="controls">
              				<input type="text" class="input-small field" id="dependency" name="dependency" placeholder="dependency expression"  value="">
            			</div>
          			</div>
          			<div class="control-group">
            			<label class="control-label">最长等待时间（分钟）*</label>
            			<div class="controls">
              				<input type="number" class="input-small field" id="maxWaitTime" name="maxWaitTime" style="text-align:right" value=60>
            			</div>
          			</div>
          			
          			<div class="control-group">
            			<label class="control-label">重试次数*</label>
            			<div class="controls">
              				<input type="number" class="input-small field" id="retryTimes" name="retryTimes" style="text-align:right" value=0>
            			</div>
          			</div>
          			<div class="control-group">
            			<label class="control-label">允许同时执行实例个数*</label>
            			<div class="controls">
              				<input type="number" class="input-small field" id="multiInstance" name="multiInstance" style="text-align:right" value=1>
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
   
    
	<script src="js/bwizard.js" type="text/javascript"></script>
    <script src="js/jquery.validate.min.js" type="text/javascript"></script>
    <script src="js/taurus_validate.js" type="text/javascript"></script>
	<script src="js/task.js" type="text/javascript"></script>
</body> 
</html>