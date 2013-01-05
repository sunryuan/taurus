var taskID;
var action_chinese;
function action(id, index) {
	action_chinese = $("#" + id + " .dropdown-menu li:nth-child(" + index + ") a").html();
	taskID = id;
	if (action_chinese == '删除') {
		$("#id_header").html("删除");
		$("#id_body").html("确定要删除任务<strong>" + id + "</strong>");
	} else if (action_chinese == '暂停') {
		$("#id_header").html("暂停");
		$("#id_body").html("确定要暂停任务<strong>" + id + "</strong>");
	} else if (action_chinese == '执行') {
		$("#id_header").html("执行");
		$("#id_body").html("确定要执行任务<strong>" + id + "</strong>");
	} else if (action_chinese == '恢复') {
		$("#id_header").html("恢复");
		$("#id_body").html("确定要恢复任务<strong>" + id + "</strong>");
	}
	$("#confirm").modal('toggle');
}

function action_ok() {
	$.ajax({
		url : "tasks.do",
		data : {
			action : toAction(action_chinese),
			id : taskID
		},
		type : 'POST',
		error: function(){
			$("#alertContainer").html('<div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>'
					+ action_chinese + '失败</strong></div>');
			$(".alert").alert();
			$('#confirm').modal("hide");
		},
		success: function(){
			$("#alertContainer").html('<div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>'
					+ action_chinese + '成功</strong></div>');
			$(".alert").alert();
			$('#confirm').modal("hide");
			if(action_chinese == '删除'){
				$('#' + taskID).remove();
			}else if(action_chinese == '暂停'){
				$('#' + taskID).addClass("error");
				$('#' + taskID + ' td .label').addClass("label-important").removeClass('label-info');
				$('#' + taskID + ' td .label').html('SUSPEND');
				$('#' + taskID + ' .dropdown-menu li:nth-child(2) a').html("恢复");
			}else if(action_chinese == '恢复'){
				$('#' + taskID).removeClass("error");
				$('#' + taskID + ' td .label').addClass("label-info").removeClass('label-important');
				$('#' + taskID + ' td .label').html('RUNNING');
				$('#' + taskID + ' .dropdown-menu li:nth-child(2) a').html("暂停");
			}
		}
	});
}

function toAction(chinese){
	var action;
	if(chinese == "删除"){
		action = "delete";
	}else if(chinese == "暂停"){
		action = "suspend";
	}else if(chinese == "恢复"){
		action = "resume";
	}else if(chinese == "执行"){
		action = "execute";
	}
	return action;
}