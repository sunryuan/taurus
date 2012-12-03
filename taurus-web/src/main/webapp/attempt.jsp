<html lang="en">
<head>
    <meta charset="utf-8">
    <title>点评作业调度平台Taurus</title>
    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
    <style type="text/css">
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
      .sidebar-nav {
        padding: 9px 0;
      }
    </style>
    <link rel="stylesheet" type="text/css" href="css/DT_bootstrap.css">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body  data-spy="scroll">

<%@ include file="header.jsp" %>

    <div class="container"  style="margin-top: 10px">
    	<table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered"  id="example">
         <thead>
                <tr>
                  <th>ID</th>
                  <th>启动时间</th>
                  <th>结束时间</th>
                  <th>调度时间</th>
                  <th>返回值</th>
                  <th>状态</th>
                  <th>IP</th>
                  <th>-</th>
                </tr>
              </thead>
              <tbody>
              
              <tr>
                <td>attempt_201209211345_0001_0001_0001</td>
                <td>2012/12/30/15/41</td>
                <td>2012/12/30/15/42</td>
                <td>2012/12/30/15/41</td>
                <td>0</td>
                <td>running</td>
                <td>192.1.6.10.1</td>
                <td>
                   <div class="btn-group">
                    <button class="btn btn-small">Action</button>
                    <button class="btn dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
                    <ul class="dropdown-menu">
                      <li><a href="#suspend">Kill</a></li>
                      <li><a href="#suspend">日志</a></li>
                    </ul>
                  </div>
                </td>
             </tr>
        	<tr>
                <td>attempt_201209211345_0001_0001_0001</td>
                <td>2012/12/30/15/41</td>
                <td>2012/12/30/15/42</td>
                <td>2012/12/30/15/41</td>
                <td>0</td>
                <td>succeed</td>
                <td>192.1.6.10.1</td>
                <td>
                   <div class="btn-group">
                    <button class="btn btn-small">Action</button>
                    <button class="btn dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
                    <ul class="dropdown-menu">
                      <li><a href="#suspend">日志</a></li>
                    </ul>
                  </div>
                </td>
             </tr>
   			<tr>
                <td>attempt_201209211345_0001_0002_0001</td>
                <td>2012/12/30/15/46</td>
                <td>2012/12/30/15/47</td>
                <td>2012/12/30/15/46</td>
                <td>1</td>
                <td>failed</td>
                <td>192.1.6.10.1</td>
                <td>
                   <div class="btn-group">
                    <button class="btn btn-small">Action</button>
                    <button class="btn dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
                    <ul class="dropdown-menu">
                      <li><a href="#suspend">日志</a></li>
                    </ul>
                  </div>
                </td>
             </tr>
             </tbody>
        </table>
</div>

<script src="http://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript" charset="utf-8" language="javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" charset="utf-8" language="javascript" src="js/DT_bootstrap.js"></script>
<script src="js/bootstrap.min.js"></script>

</body>
</html>
