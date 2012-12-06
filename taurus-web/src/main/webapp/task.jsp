<%@ page contentType="text/html;charset=utf-8" %>
<html lang="zh-CN">
<head>
	<%@ include file="jsp/sub-header.jsp"%>
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
			<form id="taskForm" action="schedule.jsp">
			<div>
  					<fieldset>
					<label>
      					<input type="checkbox"  id="autodeploy"> 使用Pool部署
    				</label><br/>
					
                    <div class="host">
                    	<label>部署的机器</label>
    					<input type="text" style="width: 500"><br/><br/>
					</div>
                    
                    <div class="poolDeployDiv" style="display: none;">
                   		<label>部署的Pool</label>
    					<select style="width: 500">
							<option>hadoop(10.1.1.161,10.1.1.162)</option>
							<option>其他</option>
						</select><br/><br/>
					
						<label>选择作业类型</label>
    					<select>
							<option>wormhole</option>
							<option>其他</option>
						</select><br/><br/>
						<label>上传作业</label>
						<input type="file" name="uploadFile"/>
                    </div>
                    </fieldset>

			</div>
				
			<div id="base">
				<fieldset>
					<legend>必填设置</legend>
					<label class="text">名称	<input type="text"></label>
					<label class="text">Cron	<input type="text"></label>
					<label class="text">以该用户身份运行（默认nobody）	<input type="text"></label>
					<label class="text">报警收件人邮件（逗号分隔）	<input type="text"></label>
  				</fieldset>
			</div>
			<div  id="extion">
				<fieldset>
					<legend>可选设置</legend>
					<label class="text">最长执行时间（分钟）<input type="text"></label>
					<label class="text">最长等待时间（分钟）<input type="text"></label>
					<label class="text">依赖 <input type="text"></label>
					<label class="text">重试次数	<input type="text"></label>
					<label class="text">重试超期时间	<input type="text"></label>
					<label class="checkbox"><input type="checkbox"> 允许多个实例</label>
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
		$('input#submitButton').click( function() {
    		$.ajax({
        		url: 'createTask',
        		type: 'post',
        		dataType: 'jsonp',
        		data: $('form#taskForm').serialize(),
        		jsonp: "callback",//传递给请求处理程序或页面的，用以获得jsonp回调函数名的参数名(一般默认为:callback)
             	jsonpCallback:"flightHandler",//自定义的jsonp回调函数名称，默认为jQuery自动生成的随机函数名，也可以写"?"，jQuery会自动为你处理数据
            	 success: function(json){
                 alert('您查询到航班信息：票价： ' + json.price + ' 元，余票： ' + json.tickets + ' 张。');
             },
             error: function(){
                 alert('fail');
             }
    		});
		});
	</script>
</body>
	<%@ include file="jsp/common-footer.jsp"%>
</body>
</html>