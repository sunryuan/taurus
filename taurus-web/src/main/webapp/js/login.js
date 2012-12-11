var User = {};

User.Login = function(){
	$.ajax({
		url: './login',
		data: {
			username : $('#username').text(),
			password:  $('#password').text()
		},
		type:"POST",
		complete: function(){
			
		}
		
		
	});
}

/**
 * 填充登录者信息以及注销url
 */
var getLoginList = function() {
	jQuery('.icon-user-name:first').replaceWith(userVerify.getUserName());
	
	$('.logout:first').attr('href', Global.domain + '/logout.aspx');
};