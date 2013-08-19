// 
//	jQuery Validate for task.jsp
//

$.validator.addMethod("fileNameTest",function(value,element,params){
	 var p = /\s/;
	 var q = /(.*)[\u4E00-\u9FA5]+(.*)/; 
	 if(q.test(value) || p.test(value)) return false; 
	 return true; 
},"文件名必须没有中文,且不包含空格");

$.validator.addMethod("alertWho",function(value,element,params){
	 var p = /^[0-9a-zA-Z|_|;|\.|\-]*$/;
	 if(p.test(value)) return true; 
	 return false; 
},"不能包含空格、逗号和其他非法字符");
  
$.validator.addMethod("notRoot",function(value,element,params){
	 if(value=="root")
		 return false;
	 else 
		 return true;
},"ProxyUser不能使root");	 

$.validator.addMethod("validIP",function(value,element,params){
	var p = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
	if(p.test(value)) return true; 
	return false; 
},"Invalid IP");	 

$.validator.addMethod("taskNameTest",function(value,element,params){
	 var p = /^[0-9a-zA-Z|_|\.|\-|(|)]*$/;
	 if(p.test(value)) return true; 
	 return false; 
},"名称中可使用的字符包括：数字、字母、下划线、横线、点和括号");

$.validator.addMethod("dependencyTest",function(value,element,params){
	 var p = /^[0-9a-zA-Z|_|\.|\-|(|)|\[|\]|\||&|\s]*$/;
	 if(p.test(value)) return true; 
	 return false; 
},"表达式中含有非法字符");


$.validator.addMethod("validName",function(value,element,params){
	var result = false;

	$.ajax({
	    url: 'create_task?name='+$("#taskName").val(),  //server script to process data
	    type: 'get',
	    //Ajax events
	    success:function(data){
	    	if(data == "0" ) {
	    		result = true;
	    	}
	    	else if(data == "1" ){
		    	result = false;
	    	}
		},
		
	    enctype: 'application/x-www-form-urlencoded',
	    cache: false,
	    contentType: false,
	    processData: false,
	    async: false
	});
	
	return result;
},"该名字已经存在！");	 


$(document).ready(function(){
	// Validate
	
	$('#deploy-form').validate({
		rules: {
    		hostname: {
        		required: function(element) {
                	return (!$("#autodeploy").prop("checked") );
        		},
        		validIP: true
    		}, 
    		uploadFile: {
    			required: function(element) {
                	return ($("#autodeploy").prop("checked") );
        		},
    			fileNameTest: function(element) {
                	return ($("#autodeploy").prop("checked"));
        		}
    		},
    		hadoopName: {
    			required:function(element) {
                	return ($("#taskType").val() == "hadoop");
        		}
    		}
		}, 
	    highlight: function(label) {
	    	$(label).closest('.control-group').removeClass('success').addClass('error');
	    },
	    success: function(label) {
	    	label.addClass('valid').text('OK!')
	    		.closest('.control-group').removeClass('error').addClass('success');
	    },			
	});
	$('#basic-form').validate({
	    rules: {
	    	taskName: {
	    		minlength: 2,
	    		required: true,
	    		taskNameTest:true,
	    		validName: true
	    	},
	    	crontab: {
	    		required: true,
	    		minlength: 6
	    	},
	    	taskCommand: {
	    		required: true
	    	},
			proxyUser: {
				notRoot: function(element) {
                	return ($("#creator").val()!="renyuan.sun");
        		},
			},
	    	description: {
	    		required: true,
	    	}
	    },
	    highlight: function(label) {
	    	$(label).closest('.control-group').removeClass('success').addClass('error');
	    },
	    success: function(label) {
	    	label.addClass('valid').text('OK!')
	    		.closest('.control-group').removeClass('error').addClass('success');
	    },
	  });
	  
	  $('#extended-form').validate({
	    rules: {
	    	maxExecutionTime: {
	    		required: true,
	    	},
			dependency: {
				dependencyTest:true
			},
			maxWaitTime: {
	    		required: true
	    	},
	    	retryTimes: {
	    		required: true,
	    	},
	    	multiInstance: {
	    		required: true,
	    	},
	    	alertUser:{
	    		alertWho:true
	    	},
	    	alertGroup:{
	    		alertWho:true
	    	}
	    },
	    highlight: function(label) {
	    	$(label).closest('.control-group').removeClass('success').addClass('error');
	    },
	    success: function(label) {
	    	label.text('OK!').addClass('valid')
	    		.closest('.control-group').removeClass('error').addClass('success');
	    },
	  });
	  
	  var forms = $('.task-form');	  
	  for(var i=0;i<forms.length;i++){
		  forms.eq(i).validate({
			  rules: {
				  	hostname: {
				  		required: true,
				  		validIP: true
		    		},
		    		uploadFile: {
		    			fileNameTest: true
		    		},
		    		
			    	crontab: {
			    		required: true,
			    		minlength: 6
			    	},
			    	taskCommand: {
			    		required: true
			    	},
					proxyUser: {
						required: true,
						notRoot: true
					},
			    	description: {
			    		required: true,
			    	},
			    	maxExecutionTime: {
			    		required: true,
			    	},
					dependency: {
						dependencyTest:true
					},
					maxWaitTime: {
			    		required: true
			    	},
			    	retryTimes: {
			    		required: true,
			    	},
			    	multiInstance: {
			    		required: true,
			    	},
			    	alertUser:{
			    		alertWho:true
			    	},
			    	alertGroup:{
			    		alertWho:true
			    	}
			    },
			    highlight: function(label) {
			    	$(label).closest('.control-group').removeClass('success').addClass('error');
			    },
			    success: function(label) {
			    	label.text('OK!').addClass('valid')
			    		.closest('.control-group').removeClass('error').addClass('success');
			    },
		  });
	  }	  
}); // end document.ready