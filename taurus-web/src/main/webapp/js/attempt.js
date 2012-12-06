var attemptID;
function action(id) {
	attemptID = id;	
	$("#id_header").html("Kill");
	$("#id_body").html("确定要Kill任务<strong>" + id + "</strong>");
	$("#confirm").modal('toggle');
}

function action_ok() {
	$.ajax({
		url : "./attempts",
		data : {
			id : taskID
		},
		type : 'POST',
		statusCode : {
			200 : function() {
				$("#alertContainer").html('<div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button> Kill <strong>' + attemptID + '</strong>成功</div>');
				$(".alert").alert();
				$('#confirm').modal("hide");
			},
			400 : function() {
				$("#alertContainer").html('<div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button> Kill <strong>' + attemptID + '</strong>失败</div>');
				$(".alert").alert();
				$('#confirm').modal("hide");
			}
		}
	});
}