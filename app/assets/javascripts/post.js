$(document).ready(function() {
	initCheckBtn();

	$('#js_postPicUpload').click(function(){
	    document.getElementById("postpic").click();
	});

    $('#js_modifBackPicUpload').click(function(){
        document.getElementById("backpic").click();
    });

    $('#js_modifMidPicUpload').click(function(){
        document.getElementById("midpic").click();
    });

    initCheckInput($('.js_fbPostMessage'));
    initCheckInput($('.js_modifMessage'));

    initCheckLinks();
});


var urlPattern = new RegExp('(http|ftp|https)://[a-z0-9\-_]+(\.[a-z0-9\-_]+)+([a-z0-9\-\.,@\?^=%&;:/~\+#]*[a-z0-9\-@\?^=%&;/~\+#])?', 'i');

function initCheckLinks(){
    var inputLink=$(".js_link");
    inputLink.on("keyup", function(){

        if(!urlPattern.test(inputLink.val())){
            $(".js_link_error").removeClass("hide");
        }else{
            $(".js_link_error").addClass("hide");
        }
    });

}

function initCheckBtn() {
	$(".js_check").on("click", function(ev){
	    var checkbox = $(this).find(".js_checkHidden");
	    checkbox.prop('checked', !checkbox.is(":checked"));
	    $(this).find(".js_checkIcon").toggle();
	});
}

function initCheckInput(textarea){

    textarea.on("keyup", function(){
    var count=$(this).val().length;
    var countDiv=$(this).parent().find(".js_count");
        countDiv.html(count);
        if(count>300){
            countDiv.css("color","red");
        }else{
            countDiv.css("color","");
        }
    });
}
