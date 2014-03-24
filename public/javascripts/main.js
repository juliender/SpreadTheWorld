$(document).ready(function() {
	$('.button').on('click',function(){
		$('.picture').attr('src','img/feedback.png');
		$('.button').css('background-color','#46a546');
		$('.button').html('Great job !');
		$('.bottom').css('background-color','#46a546');
	});
});