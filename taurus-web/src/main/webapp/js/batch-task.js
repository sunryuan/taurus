$(function() {
	$('#fileupload').fileupload({
		dataType : 'json',
		multipart : true,
		add : function(e, data) {
			$('#msg p').empty();
			$('#result').html('');
			$('#result').attr('hidden',true);
			file = data.files[0];
			var filename_pattern = new RegExp("(.*)\.xls$");
			if(filename_pattern.test(file.name)){
				$('#progress').html('<img src="img/loading.gif" class="img-rounded" width="64" height="64"/>');
				data.submit();
			}
			else{
				$('<p class="text-error"/>').text("目前系统只支持扩展名为xls的Excel文件").appendTo('#msg');
			}
		},
		done : function(e, data) {
			$('#progress').html('');
			$('#result').attr('hidden', true);
			var tb_html = '<tr><td>作业名</td><td>配置结果</td></tr>';
			$.each(data.result, function(index, file) {
				tb_html += '<tr><td>' + file.name + '</td><td>';
				if(file.success){
					tb_html += '<p class="text-success">Success</p>';
				}else{
					tb_html += '<p class="text-error">Failed</p>';
				}
				tb_html += '</td></tr>';
			});
			$('#result').html(tb_html);
			$('#result').attr('hidden',false);
		}
		
	});
});