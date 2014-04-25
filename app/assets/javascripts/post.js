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


});


function initCheckBtn() {
	$(".js_check").on("click", function(ev){
	    var checkbox = $(this).find(".js_checkHidden");
	    checkbox.prop('checked', !checkbox.is(":checked"));
	    $(this).find(".js_checkIcon").toggle();
	});
}