<html lang="en">
<head>
	<%@ include file="jsp/common-header.jsp"%>
	<link rel="stylesheet" href="css/batch-task.css">
</head>
<body data-spy="scroll">
	<%@ include file="jsp/common-nav.jsp"%>
	<div class="container" style="margin-top: 10px">
		<div class="row-fluid">
			<div class="span6">
				<h4>Please upload your excel file:</h4>
				<br> <input id="fileupload" type="file" data-url="batch_upload">
			</div>
			<div class="span5">
				<h4>Please download excel template here:</h4>
				<br> <a class=".act-info" href="files/template.xls">Excel
					Template</a>
			</div>
		</div>
		<div id="progress"></div>
		<br>
		<div id="msg"></div>
		<br>
		<table class="table table-striped table-bordered" id="result"
			hidden="true">
		</table>
	</div>

	<script src="js/jquery.ui.widget.js"></script>
	<script src="js/jquery.iframe-transport.js"></script>
	<script src="js/jquery.fileupload.js"></script>
	<script src="js/batch-task.js"></script>
</body>
</html>