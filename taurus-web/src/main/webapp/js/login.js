function login(){
	$.ajax({
		url: 'login.do',
		data: {
			username : $('#username').attr("value"),
			password:  $('#password').attr("value")
		},
		type:"POST",
		statusCode:{
			401 : function(){
				$('#alertContainer').html('<div id="alertContainer" class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button> <strong>用户名或密码错误，登陆失败！</strong></div>');
				$(".alert").alert();
			},
			200 : function(){
				window.location="index.jsp";
			}
		}
	});
	return false;
}