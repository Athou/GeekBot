$(function(){
	$(".add-merge-request").click(function(){
		var n = $(".add-merge-request:checked").size();
		$("#add-request-submit").prop('disabled', n == 2);
	});	
});