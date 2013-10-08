$.validator.addMethod("notAdmin",function(value,element,params){
	 if(value.toLowerCase( )=="admin")
		 return false;
	 else 
		 return true;
},"嘿嘿，你懂的");	   

$(document).ready(function() {
	$("#submit").click(function(e){
		if(!($("#user-form").validate().form())){
			return false;
		}
		$.ajax({
			url: 'saveUser',
	        type: 'POST',
			error: function(){
				$("#alertContainer").html('<div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>保存失败</strong></div>');
				$(".alert").alert();
			},
			success: function(response, textStatus) {
				$("#alertContainer").html('<div id="alertContainer" class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>保存成功</strong></div>');
				$(".alert").alert();
			},
			data: $("#user-form").serialize()
		});
		return false;
	});
	$('#groupName').autocomplete({
        width: 448,
        delimiter: /(,|;)\s*/,
        zIndex: 9999,
        lookup: groupList.split(',')
    });
	
	  $('#user-form').validate({
		    rules: {
		    	groupName:{
		    		required:true,
		    		notAdmin:!isAdmin
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
	
});