$(document).ready(function() {
	$("#wizard").bwizard();
	$("#autodeploy").change(function(e) {
        if($("#autodeploy").attr("checked")=="checked"){
			$("#poolDeployDiv").show();
			$("#host").hide();
		} else {
			$("#poolDeployDiv").hide();
			$("#host").show();
		}
    });
	    a = $('#query').autocomplete({
	        width: 448,
	        delimiter: /(,|;)\s*/,
	        zIndex: 0,
	        lookup: 'Andorra,Azerbaijan,Bahamas,Bahrain,Bangladesh,Barbados,Belarus,Belgium,Belize,Benin,Bhutan,Bolivia,Bosnia Herzegovina,Botswana,Brazil,Brunei,Bulgaria,Burkina, Burundi,Cambodia,Cameroon,Canada,Cape Verde,Central African Rep,Chad,Chile,China,Colombia,Comoros,Congo,Congo {Democratic Rep},Costa Rica,Croatia,Cuba,Cyprus,Czech Republic,Denmark,Djibouti,Dominica,Dominican Republic,East Timor,Ecuador,Egypt,El Salvador,Equatorial Guinea,Eritrea,Estonia,Ethiopia,Fiji,Finland,France,Gabon,Gambia,Georgia,Germany,Ghana,Greece,Grenada,Guatemala,Guinea,Guinea-Bissau,Guyana,Haiti,Honduras,Hungary,Iceland,India,Indonesia,Iran,Iraq,Ireland {Republic},Israel,Italy,Ivory Coast,Jamaica,Japan,Jordan,Kazakhstan,Kenya,Kiribati,Korea North,Korea South,Kosovo,Kuwait,Kyrgyzstan,Laos,Latvia,Lebanon,Lesotho,Liberia,Libya,Liechtenstein,Lithuania,Luxembourg,Macedonia,Madagascar,Malawi,Malaysia,Maldives,Mali,Malta,Marshall Islands,Mauritania,Mauritius,Mexico,Micronesia,Moldova,Monaco,Mongolia,Montenegro,Morocco,Mozambique,Myanmar, {Burma},Namibia,Nauru,Nepal,Netherlands,New Zealand,Nicaragua,Niger,Nigeria,Norway,Oman,Pakistan,Palau,Panama,Papua New Guinea,Paraguay,Peru,Philippines,Poland,Portugal,Qatar,Romania,Russian Federation,Rwanda,St Kitts & Nevis,St Lucia,Saint Vincent & the Grenadines,Samoa,San Marino,Sao Tome & Principe,Saudi Arabia,Senegal,Serbia,Seychelles,Sierra Leone,Singapore,Slovakia,Slovenia,Solomon Islands,Somalia,South Africa,South Sudan,Spain,Sri Lanka,Sudan,Suriname,Swaziland,Sweden,Switzerland,Syria,Taiwan,Tajikistan,Tanzania,Thailand,Togo,Tonga,Trinidad & Tobago,Tunisia,Turkey,Turkmenistan,Tuvalu,Uganda,Ukraine,United Arab Emirates,United Kingdom,United States,Uruguay,Uzbekistan,Vanuatu,Vatican City,Venezuela,Vietnam,Yemen,Zambia,Zimbabwe'.split(',')
	    });
	function post_to_url(path, form) {
		var jForm = jQuery(form);
		//submit
		$.ajax({
	        url: 'create_task',  //server script to process data
	        type: 'POST',
	        //Ajax events
	        success:function(data){
	            $("#id_header").html("成功");
				$("#id_body").html("添加作业成功!");
				$(".modal-footer").html('<a href="schedule.jsp" class="btn btn-info">确定</a>');
				$("#confirm").modal('toggle');
	    	},
	    	error:function(data){
	    		$("#id_header").html("失败");
				$("#id_body").html("添加作业失败!");
				$(".modal-footer").html('<a href="#" class="btn btn-info" data-dismiss="modal">确定</a>');
				$("#confirm").modal('toggle');
	    	},
	        enctype: 'application/x-www-form-urlencoded',
	        // Form data
	        data: jForm.serialize(),
	        cache: false,
	        contentType: false,
	        processData: false
	    });
	}
	
	function post_to_url_with_file(path, form) {
		$("submitButton").button('loading');
		var formData = new FormData(form);
		function progressHandlingFunction(e){
		    if(e.lengthComputable){
		        $('progress').attr({value:e.loaded,max:e.total});
		    }
		}
		//submit
		$.ajax({
	        url: 'create_task',  //server script to process data
	        type: 'POST',
	        xhr: function() {  // custom xhr
	            myXhr = $.ajaxSettings.xhr();
	            if(myXhr.upload){ // check if upload property exists
	                myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // for handling the progress of the upload
	            }
	            return myXhr;
	        },
	        //Ajax events
	        success:function(data){
	            $("#id_header").html("成功");
				$("#id_body").html("添加作业成功!");
				$(".modal-footer").html('<a href="schedule.jsp" class="btn btn-info">确定</a>');
				$("#confirm").modal('toggle');
				$("submitButton").button('reset');
	    	},
	    	error:function(data){
	    		$("#id_header").html("失败");
				$("#id_body").html("添加作业失败!");
				$(".modal-footer").html('<a href="#" class="btn btn-info" data-dismiss="modal">确定</a>');
				$("#confirm").modal('toggle');
				$("submitButton").button('reset');
	    	},
	        enctype: 'multipart/form-data',
	        // Form data
	        data: formData,
	        //Options to tell JQuery not to process data or worry about content-type
	        cache: false,
	        contentType: false,
	        processData: false
	    });
	}
	
	$("#submitButton").click(function(e) {
		//validate
		if(!($('#deploy-form').validate().form()
				&&$('#basic-form').validate().form()
				&&$('#extended-form').validate().form())){
			return false;
		}
		var params={};
		var len=$(".field").length;
		var file = $('#uploadFile').get(0);
		var autodeploy = ($("#autodeploy").attr("checked")=="checked");
		var form = document.createElement('form');
		for(var i = 0; i < len; i++)
 		{
      		var element = $(".field").get(i);
			if(element.id=="host") {
				if(!autodeploy){
					params[element.id] = element.value;					
				}
			} else if(element.id=="uploadFile"){
				//do nothing
			} else if(element.id=='poolId' || element.id=='taskType') {
				if(autodeploy){
					params[element.id] = element.value;
				}
			}
			else {
				params[element.id] = element.value;
			}
		}
		
		for(var key in params) {
    		if(params.hasOwnProperty(key)) {
        		var hiddenField = document.createElement("input");
        		hiddenField.setAttribute("type", "hidden");
    			hiddenField.setAttribute("name", key);
        		hiddenField.setAttribute("value", params[key]);
				form.appendChild(hiddenField);
     		}
		}		
		if(autodeploy){
			form.setAttribute("enctype","multipart/form-data");
			form.appendChild(file);
			post_to_url_with_file("create_task",form);
			$('#fileDiv').append(file);
		} else {
			form.setAttribute("enctype","application/x-www-form-urlencoded");
			post_to_url("create_task",form);
		}
    });
});
