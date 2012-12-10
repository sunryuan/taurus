var taskID;
var action_type;
var action_chinese;
function action(id, action) {
	taskID = id;
	action_type = action;
	if (action == 'delete') {
		action_chinese = '删除';
		$("#id_header").html("删除");
		$("#id_body").html("确定要删除任务<strong>" + id + "</strong>");

	} else if (action == 'suspend') {
		action_chinese = '暂停';
		$("#id_header").html("暂停");
		$("#id_body").html("确定要暂停任务<strong>" + id + "</strong>");
	} else if (action == 'execute') {
		action_chinese = '立刻执行';
		$("#id_header").html("立刻执行");
		$("#id_body").html("确定要立刻执行任务<strong>" + id + "</strong>");
	} else if (action == 'resume') {
		action_chinese = '恢复';
		$("#id_header").html("恢复");
		$("#id_body").html("确定要恢复任务<strong>" + id + "</strong>");
	}
	$("#confirm").modal('toggle');
}

function action_ok() {
	$.ajax({
		url : "./tasks",
		data : {
			action : action_type,
			id : taskID
		},
		type : 'POST',
		statusCode : {
			200 : function() {
				$("#alertContainer").html('<div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>'
						+ action_chinese + '成功</strong></div>');
				$(".alert").alert();
				$('#confirm').modal("hide");
				if(action_type == 'delete'){
					$('#' + taskID).remove();
				}else if(action_type == 'suspend'){
					$('#' + taskID + ' td .label').addClass("label-important").removeClass('label-info');
					$('#' + taskID + ' td .label').html('SUSPEND');
				}else if(action_type == 'resume'){
					$('#' + taskID + ' td .label').addClass("label-info").removeClass('label-important');
					$('#' + taskID + ' td .label').html('SUSPEND');
				}
				
			},
			400 : function() {
				$("#alertContainer").html('<div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>'
						+ action_chinese + '失败</strong></div>');
				$(".alert").alert();
				$('#confirm').modal("hide");
			}
		}
	});
}