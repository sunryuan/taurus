<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Sign in &middot; Taurus</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
	<%@ include file="jsp/common-header.jsp"%>
    <script src="js/login.js"></script>

  </head>
	
  <body>
	<%
	String url = (String) request.getParameter("redirect-url");
		if(url==null){
			url="";
		}
	%>
    <div class="container">
        <div id="alertContainer" class="container">
        </div>
      <form class="form-signin">
        <h2 class="form-signin-heading">Please sign in</h2>
        <input type="text" id="username" class="input-block-level" placeholder="Domain accout">
        <input type="password" id="password" class="input-block-level" placeholder="Password"  onKeyPress="EnterTo('<%=url%>')">
        <button class="btn btn-large btn-primary" type="button" onClick="login('<%=url%>')">Sign in</button>
      </form>

    </div> <!-- /container -->
  </body>
</html>