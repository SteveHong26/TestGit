$(function(){
	$(".user").hover(function(){
		$(this).addClass("user-hover");
	},function(){
		$(this).removeClass("user-hover");
	});
})

$(function(){
	$(".addr-item .name").click(function(){
		 $(this).toggleClass("selected").siblings().removeClass("selected");	
	});
	$(".payType li").click(function(){
		 $(this).toggleClass("selected").siblings().removeClass("selected");	
	});
})
