
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<button type="button" class="btn btn-navbar" data-toggle="collapse"
				data-target=".nav-collapse">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="brand" href="./index.jsp">Taurus</a>
			<div class="nav-collapse collapse">
				<p class="navbar-text pull-right">
					<a href="#myModal" role="button" class="btn-link"
						data-toggle="modal">登陆</a>
				</p>

				<ul class="nav">
					<li class=""><a href="index.jsp">Home</a>
					</li>
					<li class=""><a href="task.jsp">新建任务</a>
					</li>
					<li class=""><a href="batch-task.jsp">批量设置</a>
					</li>
					<li class=""><a href="schedule.jsp">调度中心</a>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>
<!-- Modal -->
<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">×</button>
		<h2 id="myModalLabel" class="form-signin-heading">登陆</h2>
	</div>
	<div class="modal-body">
		<form method="get" action="/login">
			<input type="text" name="username" class="input-block-level"
				placeholder="Email address" /> <input type="password"
				name="password" class="input-block-level" placeholder="Password" />
			<div class="modal-footer">
				<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
				<button class="btn btn-large btn-primary" type="submit">确定</button>
			</div>
		</form>
	</div>
</div>