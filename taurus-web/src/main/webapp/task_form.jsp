<!-- Task Modal -->
<div id="taskModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="taskModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h2 id="taskModalLabel" class="form-signin-heading">详细信息</h2>
  </div>
  <form method="post" action="task">
      <div class="modal-body">
        <input type="text" name="username" class="input-block-level" placeholder="Email address"/> 
        <input type="password" name="password" class="input-block-level" placeholder="Password"/>

      </div>
      <div class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
        <button class="btn btn-primary" type="submit">确定</button>
      </div>
  </form>
 </div>