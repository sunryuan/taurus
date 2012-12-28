// 
//	jQuery Validate for task.jsp
//


$(document).ready(function(){
	// Validate

	$('#deploy-form').validate({
		rules: {
    		host: {
        		required: function(element) {
                	return ($("#autodeploy").attr("checked") != "checked" );
        		}
    		}, 
    		uploadFile: {
    			required: function(element) {
                	return ($("#autodeploy").attr("checked") == "checked" );
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
	    		required: true
	    	},
	    	crontab: {
	    		required: true,
	    		minlength: 6
	    	},
	    	taskCommand: {
	    		required: true
	    	},
			proxyUser: {
				required: false
			},
	    	taskMail: {
	    		required: true,
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
			},
			maxWaitTime: {
	    		required: true
	    	},
	    	retryTimes: {
	    		required: true,
	    	},
	    	multiInstance: {
	    		required: true,
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
	  
}); // end document.ready