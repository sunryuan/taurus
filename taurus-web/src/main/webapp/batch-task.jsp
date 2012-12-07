<html lang="en">
<head>
<%@ include file="jsp/sub-header.jsp"%>
<link rel="stylesheet" href="css/batch-task.css">
</head>
<body data-spy="scroll">
	<%@ include file="jsp/common-nav.jsp" %>
	<div class="container" style="margin-top: 10px">
		<h4 class="title">Please upload your excel file:</h4>
		<br>
		<input id="fileupload" type="file" data-url="batch_upload">
		<br>
		<div id="progress">
		</div>
		<div id="msg"></div>
		<br>
		<table class="table table-striped table-bordered" id="result" hidden = "true">
		</table>
	</div>
	<%@ include file="jsp/common-footer.jsp"%>
	<script src="js/jquery.ui.widget.js"></script>
	<script src="js/jquery.iframe-transport.js"></script>
	<script src="js/jquery.fileupload.js"></script>
	<script src="js/batch-task.js"></script>
</body>
</html>