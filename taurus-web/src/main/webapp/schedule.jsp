<html lang="en">
<head>
	<%@ include file="jsp/sub-header.jsp"%>
</head>
<body data-spy="scroll">
	<%@ include file="jsp/common-nav.jsp"%>

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

				<tr>
					<td>task_201209211345_0001</td>
					<td>wordcount</td>
					<td>damon.zhu</td>
					<td>arch</td>
					<td>2012/11/30</td>
					<td>? * * * * ?</td>
					<td>
						<div class="btn-group">
							<button class="btn btn-small">Action</button>
							<button class="btn dropdown-toggle" data-toggle="dropdown">
								<span class="caret"></span>
							</button>
							<ul class="dropdown-menu">
								<li><a href="#delete">删除</a>
								</li>
								<li><a href="#suspend">暂停</a>
								</li>
								<li><a href="#instant">立即执行</a>
								</li>
								<li><a href="#info">详细</a>
								</li>
							</ul>
						</div></td>
					<td><button id="attempts" class="btn"
							onClick="javascript:window.location.href='attempt.jsp'">运行历史</button>
					</td>
				</tr>
				<tr>
					<td>task_201209211345_0002</td>
					<td>hadoop</td>
					<td>renyuan.sun</td>
					<td>arch</td>
					<td>2012/11/30</td>
					<td>? * * * * ?</td>
					<td>
						<div class="btn-group">
							<button class="btn btn-small">Action</button>
							<button class="btn dropdown-toggle" data-toggle="dropdown">
								<span class="caret"></span>
							</button>
							<ul class="dropdown-menu">
								<li><a href="#delete">删除</a>
								</li>
								<li><a href="#suspend">暂停</a>
								</li>
								<li><a href="#instant">立即执行</a>
								</li>
								<li><a href="#info">详细</a>
								</li>
							</ul>
						</div></td>
					<td><button id="attempts" class="btn" onClick="window.open("./attempt.jsp")">运行历史</button>
					</td>
				</tr>
			</tbody>
		</table>
	</div>

	<%@ include file="jsp/common-footer.jsp"%>
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" charset="utf-8" language="javascript" src="js/DT_bootstrap.js"></script>
</body>
</html>