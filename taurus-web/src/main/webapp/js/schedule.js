var taskID;
var action_chinese;
$(document).ready(function() {
	$(document).delegate('.detailBtn', 'click', function(e){
		var anchor = this;
		if(e.ctrlKey || e.metaKey){
			return true;
		}else{
			e.preventDefault();
		}
		$.ajax({
			type: "get",
			url: anchor.href,
			error: function(){
				$("#alertContainer").html('<div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>获取详情失败</strong></div>');
				$(".alert").alert();
			},
			success: function(response, textStatus) {
				$("#detailModal").html(response);
				$("#detailModal").modal();
			}
		});
	});
});

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


function action_update(id) {
	var btn = $('#updateBtn',$('#detailModal'));
	var form =  $("#form_"+id);
	if(btn.text() == "修改"){
		btn.html("保存");
		$('.field',form).removeAttr("disabled", "disabled");
		$('#alertUser',form).autocomplete({
	        width: 448,
	        delimiter: /(,|;)\s*/,
	        zIndex: 9999,
	        lookup: userList.split(',')});
		$('#alertGroup',form).autocomplete({
	        width: 448,
	        delimiter: /(,|;)\s*/,
	        zIndex: 9999,
	        lookup: groupList.split(',')});
	} else {
		if(!(form.validate().form())){
			return false;
		}
		btn.button('loading');
		var params={};
		var file = $('#uploadFile',form).get(0);
		var newForm = document.createElement('form');
		var len=$(".field",form).length;
		for(var i = 0; i < len; i++)
 		{
      		var element = $(".field",form).get(i);
			if(element.id=="uploadFile" || element.id=="alertCondition"  || element.id=="alertType"){
				//do nothing
			}else if(element.id=="alertUser" || element.id=="alertGroup") {
				var result = element.value;
				if(result[result.length-1]==';')
					params[element.id] = result.substr(0,result.length-1);
				else
					params[element.id] = element.value;
			} else {
				params[element.id] = element.value;
			}
		}
		params["taskName"]=$("#taskName",form).get(0).value;
		var condition = $('.alertCondition',form).map(function() {
			if($(this).prop("checked"))
				return this.name;
		    }).get().join(";");
		var type = $('#alertType',form).children().map(function() {
			if($(this).prop("selected"))
				return this.id;
		    }).get().join();
		
		params["alertCondition"] = condition;
		params["alertType"] = type;
		params["poolId"] = $('#poolIdReal',form).val();
		if(params["dependency"]!=null && params["dependency"]!=''){
			params["alertCondition"] = params["alertCondition"] + ";DEPENDENCY_TIMEOUT";
		}
		for(var key in params) {
    		if(params.hasOwnProperty(key)) {
        		var hiddenField = document.createElement("input");
        		hiddenField.setAttribute("type", "hidden");
    			hiddenField.setAttribute("name", key);
        		hiddenField.setAttribute("value", params[key]);
				newForm.appendChild(hiddenField);
     		}
		}		
		if($('#uploadFile',form).val() == null || $('#uploadFile',form).val() == '') {
			newForm.setAttribute("enctype","application/x-www-form-urlencoded");
			$.ajax({
				type: "POST",
	            url: 'create_task?update='+id, 
	            data: $(newForm).serialize(), // serializes the form's elements.
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
	            	$("#detail_"+id).modal("hide");
	            	$('#modal-confirm').modal('toggle');
	            },
	            cache: false,
		        contentType: 'application/xml',
		        processData: false
		    });
		} else {	
			function progressHandlingFunction(e){
			    if(e.lengthComputable){
			        $('progress').attr({value:e.loaded,max:e.total});
			    }
			}
			newForm.setAttribute('enctype','multipart/form-data');
			newForm.appendChild(file);
			$.ajax({
				type: "POST",
	           	url: 'create_task?update='+id, 
	           	data: new FormData(newForm),
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
	            	$("#detail_"+id).modal("hide");
	            	$('#modal-confirm').modal('toggle');
	            },
	            cache: false,
		        contentType: false,
		        processData: false
		    });
			
		}
		$('#fileDiv',form).append(file);
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
			if(action_chinese == '执行') {
				$("#alertContainer").html('<div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>开始执行..</strong></div>');
			} else {
				$("#alertContainer").html('<div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>'
						+ action_chinese + '成功</strong></div>');
			}
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
	var action = null;
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