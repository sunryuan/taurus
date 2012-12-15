<%@ page contentType="text/html;charset=utf-8" %>
<html lang="zh-CN">
<head>
	<%@ include file="jsp/common-header.jsp"%>
</head>
<body data-spy="scroll">
	<%@ include file="jsp/common-nav.jsp"%>
<head>
	<meta charset="utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<link href="./css/bootstrap.min.css" rel="stylesheet" />
	<style>body{padding-top: 60px;}</style>
	<link href="./css/bootstrap-responsive.min.css" rel="stylesheet" />
	<link href="./css/bwizard.min.css" rel="stylesheet" />
</head>
<body>
	<div class="container">
		<div id="wizard">
			<ol>
				<li>作业部署</li>
				<li>基本设置</li>
				<li>其他设置</li>
			</ol>
			<form id="taskForm">
			<div>
  					<fieldset>
					<label>
      					<input type="checkbox"  id="autodeploy"> 使用Pool部署
    				</label><br/>
					
                    <div class="host">
                    	<label>部署的机器</label>
    					<input type="text"   id="host" name="host" style="width: 500"  class="field"><br/><br/>
					</div>
                    
                    <div class="poolDeployDiv" style="display: none;">
                   		<label>部署的Pool</label>
    					<select id="poolId" name="poolId" style="width: 500"  class="field">
							<option>hadoop(10.1.1.161,10.1.1.162)</option>
							<option>其他</option>
						</select><br/><br/>
					
						<label>选择作业类型</label>
    					<select  id="taskType" name="taskType"  class="field">
    						<option>hadoop</option>
							<option>wormhole</option>
							<option>其他</option>
						</select><br/><br/>
						<label>上传作业</label>
						<input type="file" id="uploadFile" name="uploadFile"  class="field"/>
                    </div>
                    </fieldset>

			</div>
				
			<div id="base">
				<fieldset>
					<legend>必填设置</legend>
					<label class="text">名称	<input type="text"  id="taskName" name="taskName"  style="width: 500"  class="field"></label>
					<label class="text">Crontab<input type="text"  id="crontab" name="crontab"  style="width: 500"  class="field"></label>
                    <label class="text">命令	<input type="text"  id="taskCommand" name="taskCommand"  style="width: 500"  class="field"></label>
					<label class="text">以该用户身份运行（默认nobody）	<input type="text" id="proxyUser" name="proxyUser"  style="width: 500"  class="field"></label>
					<label class="text">报警收件人邮件（逗号分隔）	<input type="text"  id="mail" name="mail"  style="width: 500"  class="field"></label>
  				</fieldset>
			</div>
			<div  id="extion">
				<fieldset>
					<legend>可选设置</legend>
					<label class="text">最长执行时间（分钟）<input type="text" id="maxExecutionTime" name="maxExecutionTime" style="width: 100" class="field"></label>
					<label class="text">最长等待时间（分钟）<input type="text" id="maxWaitTime" name="maxWaitTime"  style="width: 100"  class="field"></label>
					<label class="text">依赖 <input type="text" id="dependency" name="dependency"  style="width: 100"  class="field"></label>
					<label class="text">重试次数	<input type="text" id="retryTimes" name="retryTimes"  style="width: 100"  class="field"></label>
					<label class="checkbox"><input type="checkbox" id="multiInstance" name="multiInstance"  class="field"> 允许多个实例</label>
                    <input type="submit" id="submitButton" />
  				</fieldset>
			</div>
			</form>
		</div>
	</div>

	<script src="./js/jquery.min.js"></script>
	<script src="./js/bootstrap.min.js"></script>
	<script src="./js/bwizard.js" type="text/javascript"></script>
	<script type="text/javascript">
		$("#wizard").bwizard();
		$("#autodeploy").change(function(e) {
            if($("#autodeploy").attr("checked")=="checked"){
				$(".poolDeployDiv").show();
				$(".host").hide();
			} else {
				$(".poolDeployDiv").hide();
				$(".host").show();
			}
        });
		function post_to_url(path, params,file) {
    		// The rest of this code assumes you are not using a library.
    		// It can be made less wordy if you use one.
   			var form = document.createElement("form");
    		form.setAttribute("method", "post");
    		form.setAttribute("action", path);
			form.setAttribute("enctype","multipart/form-data");
    		for(var key in params) {
        		if(params.hasOwnProperty(key)) {
            		var hiddenField = document.createElement("input");
            		if(key!="uploadFile") {
            			hiddenField.setAttribute("type", "hidden");
            			hiddenField.setAttribute("name", key);
                		hiddenField.setAttribute("value", params[key]);
            		}
            		
					if(params[key]!=null && params[key]!=""){
            			form.appendChild(hiddenField);
					}
         		}
    		}
			var hiddenField = document.createElement("input");
			hiddenField.setAttribute("type", "hidden");
			hiddenField.setAttribute("name", "creator");
            hiddenField.setAttribute("value", "renyuan.sun");
			form.appendChild(hiddenField);

			var hiddenField = document.createElement("input");
			hiddenField.setAttribute("type", "hidden");
			hiddenField.setAttribute("name", "description");
            hiddenField.setAttribute("value", "real task");
			form.appendChild(hiddenField);
			file.setAttribute("style","visibility:hidden");
			form.appendChild(file);
    		document.body.appendChild(form);
    		form.submit();
		}
		
		$("#submitButton").click(function(e) {
			var params={};
			var l=$('.field').length;
			var file;
			for(var id = 0; id< l; id++)
     		{
          		var element = $('.field').get(id);
				params[element.id] = element.value;
				if(element.id=="uploadFile"){
					file = $('.field').get(id);
				}
			}
            post_to_url("create_task",params,file);
        });
	</script>
</body> 
	
</body>
</html>