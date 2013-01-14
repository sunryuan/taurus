var taskID;
var action_chinese;
function action(id, index) {
	action_chinese = $("#" + id + " .dropdown-menu li:nth-child(" + index + ") a").html();
	taskID = id;
	if (action_chinese == '详细') {
		$("#detail_"+id).modal('toggle');
	}
	else {
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
}

function action_update(id) {
	var btn = $('#updateBtn',$('#detail_'+id));
	var form =  $("#form_"+id);
	if(btn.text() == "修改"){
		btn.html("保存");
		$('.field',$('#detail_'+id)).removeAttr("disabled", "disabled");
	} else {
		if(!(form.validate().form())){
			return false;
		}
		btn.button('loading');
		if($('#uploadFile',form).val() == null || $('#uploadFile',form).val() == '') {
			form.attr('enctype','application/x-www-form-urlencoded');
			$.ajax({
				type: "POST",
	            url: 'create_task?update='+id, 
	            data: $("#form_"+id).serialize(), // serializes the form's elements.
	            enctype: 'application/x-www-form-urlencoded',

	            error: function(data)
	            {
	            	$("#alertContainer").html('<div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>'
	    					+ '修改失败</strong></div>');
	    			$(".alert").alert();
	    			$("#detail_"+id).modal("hide");
	    			btn.button('reset');
	            },
	            success: function(data)
	            {
	    			window.location.reload();
	            	$("#alertContainer").html('<div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>'
	    					+ '修改成功</strong></div>');
	    			$(".alert").alert();
	    			$("#detail_"+id).modal("hide");
	            },
	            cache: false,
		        contentType: false,
		        processData: false
		    });
		} else {	
			function progressHandlingFunction(e){
			    if(e.lengthComputable){
			        $('progress').attr({value:e.loaded,max:e.total});
			    }
			}
			form.attr('enctype','multipart/form-data');
			$.ajax({
				type: "POST",
	           	url: 'create_task?update='+id, 
	           	data: new FormData(form[0]),
	           	enctype: 'multipart/form-data',
	           	xhr: function() {  // custom xhr
		            myXhr = $.ajaxSettings.xhr();
		            if(myXhr.upload){ // check if upload property exists
		                myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // for handling the progress of the upload
		            }
		            return myXhr;
		        },
	           	error: function(data)
	            {
	            	$("#alertContainer").html('<div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>'
	    					+ '修改失败</strong></div>');
	    			$(".alert").alert();
	    			$("#detail_"+id).modal("hide");
	    			btn.button('reset');
	            },
	            success: function(data)
	            {
	    			//window.location.reload();
	            	$("#alertContainer").html('<div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>'
	    					+ '修改成功</strong></div>');
	    			$(".alert").alert();
	    			$("#detail_"+id).modal("hide");
	            },
	            cache: false,
		        contentType: false,
		        processData: false
		    });
		}

	    return false; // avoid to execute the actual submit of the form.

	}
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