<!DOCTYPE html>
<%@ page contentType="text/html;charset=utf-8"%>
<html lang="en">
<head>
	<%@ include file="jsp/common-header.jsp"%>
	<%@ include file="jsp/common-nav.jsp"%>

	<link href="css/index.css" rel="stylesheet" type="text/css">
	<link href="css/docs.css" rel="stylesheet">
</head>
<body data-spy="scroll" data-target=".bs-docs-sidebar">
	<div class="container">
		<div class="row">
			<div class="span3 bs-docs-sidebar">
				<ul class="nav nav-list bs-docs-sidenav affix">
					<li class="active"><a href="#updated"><i
							class="icon-chevron-right"></i> 服务器更新日志</a></li>
					<li><a href="#author"><i class="icon-chevron-right"></i>开发人员</a></li>
				</ul>
			</div>

			<div class="span9">
				<section id="updated">
					<h1>服务器更新日志</h1>
					<table class="table table-striped table-bordered table-condensed">
						<tbody>
							<tr class="text-success">
								<th width="90%">最新发布功能描述</th>
								<th width="10%">发布时间</th>
							</tr>
							<tr>
								<td>重新组织了<a href="about.jsp">帮助</a>页面</td>
								<td>2013-09-27</td>
							</tr>
							<tr>
								<td>增加了<a href="user.jsp">用户设置</a>页面</td>
								<td>2013-09-27</td>
							</tr>
							<tr>
								<td>用户可以看到同组的所有人的作业</td>
								<td>2013-09-27</td>
							</tr>
							<tr>
								<td>修复了description显示为null和修改告警人的bug</td>
								<td>2013-09-27</td>
							</tr>
							<tr>
								<td>支持自动杀死Timeout作业。具体细节请看<a href="about.jsp#config">帮助</a></td>
								<td>2013-08-20</td>
							</tr>
							<tr>
								<td>修正Bugs。对杀死的作业设置返回值为-1。</td>
								<td>2013-08-20</td>
							</tr>
							<tr>
								<td>增加Cat打点</a></td>
								<td>2013-08-20</td>
							</tr>
						</tbody>
					</table>
				</section>

				<section id="author">
					<h1>开发者</h1>
					<table class="table table-striped table-bordered table-condensed">
						<tbody>
							<tr>
								<td>孙任远</td>
								<td>renyuan.sun@dianping.com</td>
								<td>1815</td>
							</tr>
							<tr>
								<td>朱浩</td>
								<td>hao.zhu@dianping.com</td>
								<td>1815</td>
							</tr>
						</tbody>
					</table>
				</section>

			</div>
		</div>
	</div>
</body>
</html>
