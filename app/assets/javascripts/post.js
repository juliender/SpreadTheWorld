$(document).ready(function() {
	initCheckBtn();
	initSubmitBtnPost();
	initSubmitBtnPage();

	$('.js_profilPicUpload').click(function(){
	    document.getElementById("upfile").click();
	});

	$("#upfile").change(function(){
		$("#pic_form").submit();
	});

});


function initCheckBtn() {
	$(".js_check").on("click", function(ev){
	    var checkbox = $(this).find(".js_checkHidden");
	    checkbox.prop('checked', !checkbox.is(":checked"));
	    $(this).find(".js_checkIcon").toggle();
	});
}

function initSubmitBtnPost(){
    $('.js_btnSubmit').on('click', function(){

        $("#contact-mail").val($("#contact-mail").val().toLowerCase());
		var btn=$(this);
		btn.html('<i class="fa fa-refresh fa-spin"></i>'+btn.data("sending-message"));

    	//log beginning of the flash
		loggr("modifying settings "+$("#contact-mail").val()+" "+version, "created settings");

    	// send form
        $.ajax({
            type: 'POST',
            data : $("#post").serializeArray(),
            success: function (data) {
    			$('.error-message').hide();
    			console.log(data);


         		//erros from validation form
            	if(data.error!=null){

						var errorFound = false;
						if(data.error.firstName){
							errorFound=true;
							loggr("error name"+version, "trace settings");
							$(".js_name_error").show();
						}
						if(data.error.email){
							errorFound=true;
							loggr("error mail"+version, "trace settings");
							console.log(data.error["user.email"]);
							$(".js_email_error").show();
							$(".js_email_error").html(data.error.email);
						}
						if(data.error.newPassword){
							errorFound=true;
							loggr("error password"+version, "trace settings");
							console.log("show");
							$(".js_error_pw").show();
							$(".js_error_pw").html(data.error.newPassword);
						}
						if(data.error.confirmNewPassword){
							errorFound=true;
							loggr("error password"+version, "trace settings");
							console.log("show");
							$(".js_error_pw_confirm").show();
							$(".js_error_pw_confirm").html(data.error.password);
						}

						if(!errorFound){
							loggr("error unknow callback submit"+version, "error settings");
							$(".js_btnSubmit").after("<p class='error'>" + Messages('error.bug') + "</p>");
						}
						btn.html('<i class="fa fa-times"></i>'+btn.data("error-message"));
						timerErrorBtn(btn);
				} else{
					console.log("success");
			    	loggr("success "+version, "created settings");
					timerErrorBtn(btn);
					btn.html('<i class="fa fa-check"></i>'+btn.data("ok-message"));
				}


            },
            error: function(data, textStatus){
            console.log(data);
		        if(textStatus == 'timeout'){
		        	$(".js_btnSubmit").after("<p class='error'>" + Messages('error.server.connexion') + "</p>");
					loggr("error timeout ajax "+version, "error settings");

		        }else{
					$(".js_btnSubmit").after("<p class='error'>" + Messages('error.bug') + "</p>");
		        	loggr("error ajax"+version, "error settings");
		        }
		        btn.html('<i class="fa fa-times"></i>'+btn.data("error-message"));
				timerErrorBtn(btn);
            },
            timeout: 10000
        });
    });
}


function initSubmitBtnPage(){
    $('.js_submitPage').on('click', function(){
  		var btn=$(this);

     	$('.error-message,.error,.js_totem_question_error').hide();
		btn.html('<i class="fa fa-refresh fa-spin"></i>'+btn.data("sending-message"));

 		var totemForm=btn.closest(".totemForm");

	   	// send form
        $.ajax({
            url: '/settingsPageTotem/',
            type: 'POST',
            data : btn.closest(".totemForm").serializeArray(),
            //add beforesend handler to validate or something
            //beforeSend: functionname,
            success: function (data) {
            	console.log(data);
            	if(data.error!=null){
	            	if(data.error==="questionEmpty"){
						loggr("totem question empty "+version, "trace", data);
	            		totemForm.find(".js_totem_question_error").show();
	            		btn.html('<i class="fa fa-times"></i>'+btn.data("error-message"));
						timerErrorBtn(btn);
					}
					if(data.error==="emailInvalid"){
	            		loggr("email vcard invalid "+version, "trace", data);
	            		totemForm.find(".js_totem_ownerEmail_error").show();
	            		btn.html('<i class="fa fa-times"></i>'+btn.data("error-message"));
						timerErrorBtn(btn);
					}
            	}else{
			    	loggr("success "+version, "created settings");
					timerErrorBtn(btn);
					btn.html('<i class="fa fa-check"></i>'+btn.data("ok-message"));
            	}
            },
            error: function(data, textStatus){
            	if(textStatus == 'timeout'){
		        	$(".js_submitTotem").after("<p class='error'>" + Messages('error.server.connexion') + "</p>");
					loggr("error timeout ajax "+version, "error settings", data);

		        }else{
					$(".js_submitTotem").after("<p class='error'>" + Messages('error.bug') + "</p>");
		        	loggr("error ajax"+version, "error settings", data);
		        }
		        btn.html('<i class="fa fa-times"></i>'+btn.data("error-message"));
				timerErrorBtn(btn);
            },
            timeout: 10000
        });
    });
}

function timerErrorBtn(btn){
	setTimeout(function(){
		btn.html('Save');
		btn.width('auto');
	},2000);
}