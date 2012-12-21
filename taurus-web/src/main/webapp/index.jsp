<%@ page contentType="text/html;charset=utf-8" %>
<html lang="en">
<head>
    <%@ include file="jsp/common-header.jsp"%>
    <%@ include file="jsp/common-api.jsp"%>
	<link href="css/index.css" rel="stylesheet" type="text/css" >
</head>
<body  data-spy="scroll">

 <%@ include file="jsp/common-nav.jsp" %>
 
<div id="myCarousel" class="carousel slide">
      <div class="carousel-inner">
        <div class="item active">
          <img src="img/taurus_os.png" alt="">
          <div class="container">
            <div class="carousel-caption">
              <h1>Taurus Job Managerment System</h1>
              <p class="lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam. Donec id elit non mi porta gravida at eget metus. Nullam id dolor id nibh ultricies vehicula ut id elit.</p>
            </div>
          </div>
        </div>
        <div class="item">
          <img src="img/hadoop.png" alt="">
          <div class="container">
            <div class="carousel-caption">
              <h1 style="color:#000">Hadoop Job Supported</h1>
              <p class="lead" style="color:#000">Cras justo odio, dapibus ac facilisis in, egestas eget quam.</p>
            </div>
          </div>
        </div>
      </div>
      <a class="left carousel-control" href="#myCarousel" data-slide="prev">&lsaquo;</a>
      <a class="right carousel-control" href="#myCarousel" data-slide="next">&rsaquo;</a>
    </div><!-- /.carousel -->
   
<script>	
      !function ($) {
        $(function(){
          // carousel demo
          $('#myCarousel').carousel()
        })
      }(window.jQuery)
    </script>
</body>
</html>
