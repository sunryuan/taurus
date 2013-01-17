$(document).ready(function() {
	$("#wizard").bwizard();
	$("#autodeploy").change(function(e) {
        if($("#autodeploy").prop("checked")){
			$("#poolDeployDiv").show();
			$("#host").hide();
		} else {
			$("#poolDeployDiv").hide();
			$("#host").show();
		}
    });

	function post_to_url(path, form) {
		var jForm = jQuery(form);
		//submit
		$.ajax({
	        url: 'create_task',  //server script to process data
	        type: 'POST',
	        //Ajax events
	        success:function(data){
	            $("#id_header").html("成功");
				$("#id_body").html("添加作业成功!");
				$(".modal-footer").html('<a href="schedule.jsp" class="btn btn-info">确定</a>');
				$("#confirm").modal('toggle');
				$("#submitButton").button('reset');			
	    	},
	    	error:function(data){
	    		$("#id_header").html("失败");
				$("#id_body").html("添加作业失败!");
				$(".modal-footer").html('<a href="#" class="btn btn-info" data-dismiss="modal">确定</a>');
				$("#confirm").modal('toggle');
				$("#submitButton").button('reset');
	    	},
	        enctype: 'application/x-www-form-urlencoded',
	        // Form data
	        data: jForm.serialize(),
	        cache: false,
	        contentType: false,
	        processData: false
	    });
	}
	
	function post_to_url_with_file(path, form) {
		var formData = new FormData(form);
		function progressHandlingFunction(e){
		    if(e.lengthComputable){
		        $('progress').attr({value:e.loaded,max:e.total});
		    }
		}
		//submit
		$.ajax({
	        url: 'create_task',  //server script to process data
	        type: 'POST',
	        xhr: function() {  // custom xhr
	            myXhr = $.ajaxSettings.xhr();
	            if(myXhr.upload){ // check if upload property exists
	                myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // for handling the progress of the upload
	            }
	            return myXhr;
	        },
	        //Ajax events
	        success:function(data){
	            $("#id_header").html("成功");
				$("#id_body").html("添加作业成功!");
				$(".modal-footer").html('<a href="schedule.jsp" class="btn btn-info">确定</a>');
				$("#confirm").modal('toggle');
				$("#submitButton").button('reset');
	    	},
	    	error:function(data){
	    		$("#id_header").html("失败");
				$("#id_body").html("添加作业失败!");
				$(".modal-footer").html('<a href="#" class="btn btn-info" data-dismiss="modal">确定</a>');
				$("#confirm").modal('toggle');
				$("#submitButton").button('reset');
	    	},
	        enctype: 'multipart/form-data',
	        // Form data
	        data: formData,
	        //Options to tell JQuery not to process data or worry about content-type
	        cache: false,
	        contentType: false,
	        processData: false
	    });
	}
	
	$('#alertUser').autocomplete({
        width: 448,
        delimiter: /(,|;)\s*/,
        zIndex: 0,
        lookup: userList.split(',')});
	$('#alertGroup').autocomplete({
        width: 448,
        delimiter: /(,|;)\s*/,
        zIndex: 0,
        lookup: groupList.split(',')});
        	
	$("#submitButton").click(function(e) {
		if($("#submitButton").attr("disabled") == "disabled") {
			return false;
		}
		//validate
		if(!($('#deploy-form').validate().form()
				&&$('#basic-form').validate().form()
				&&$('#extended-form').validate().form())){
			return false;
		}	
		$("#submitButton").button('loading');
		var params={};
		var len=$(".field").length;
		var file = $('#uploadFile').get(0);
		var autodeploy = ($("#autodeploy").prop("checked"));
		var form = document.createElement('form');
		for(var i = 0; i < len; i++)
 		{
      		var element = $(".field").get(i);
			if(element.id=="hostname") {
				if(!autodeploy){
					params[element.id] = element.value;					
				}
			} else if(element.id=="uploadFile" || element.id=="alertCondition"  || element.id=="alertType"){
				//do nothing
			} else if(element.id=='poolId' || element.id=='taskType') {
				if(autodeploy){
					params[element.id] = element.value;
				}
			} else if(element.id=="alertUser" || element.id=="alertGroup") {
				var result = element.value;
				if(result[result.length-1]==';')
					params[element.id] = result.substr(0,result.length-1);
				else
					params[element.id] = element.value;
			} else {
				params[element.id] = element.value;
			}
		}
		var condition = $('.alertCondition').map(function() {
			if($(this).prop("checked"))
				return this.name;
		    }).get().join(";");
		var type = $('#alertType').children().map(function() {
			if($(this).prop("selected"))
				return this.id;
		    }).get().join();
		$('#alertUser').val().split(';').map(function() {
				return 1;
		    }).join();
		params["alertCondition"] = condition;
		params["alertType"] = type;
		
		for(var key in params) {
    		if(params.hasOwnProperty(key)) {
        		var hiddenField = document.createElement("input");
        		hiddenField.setAttribute("type", "hidden");
    			hiddenField.setAttribute("name", key);
        		hiddenField.setAttribute("value", params[key]);
				form.appendChild(hiddenField);
     		}
		}		
		if(autodeploy){
			form.setAttribute("enctype","multipart/form-data");
			form.appendChild(file);
			post_to_url_with_file("create_task",form);
			$('#fileDiv').append(file);
		} else {
			form.setAttribute("enctype","application/x-www-form-urlencoded");
			post_to_url("create_task",form);
		}
    });
});
