$(function() {
	$("body").css({"overflow-y":"hidden","overflow-x":"hidden"});
	
	function bodyHeight() {
		if (window.innerHeight != undefined) {
			return window.innerHeight;
		} else {
			return Math.min(document.documentElement.clientHeight,document.body.clientHeight);
		}
	}
	
	var bHeight = bodyHeight();
	$(window).scrollTop(0);
	$(".banner").css("top","0");
	$(".move_wrap").css("top",bHeight);
	
	/*-------滚轮滚动--------*/
	var scrollNum = 0;
	var open = true;
	var close = false;
	$(".down_enter").click(function() {
		var bodyNum = "-" + bHeight;
		banHeight(bodyNum);
		close = true;
	});
	
	$(document).on('mousewheel', function(event) {
		// chrome & ie & firefox
		var delta = (event.originalEvent.wheelDelta && (event.originalEvent.wheelDelta > 0 ? 1 : -1)) || (event.originalEvent.detail && (event.originalEvent.detail > 0 ? -1 : 1));
		if (close) {
			if ($(window).scrollTop() == 0) {
				if (delta > 0) {  /*滚轮上滚动*/
					if (scrollNum-- <= 5) {
						$("body").css("overflow-y","hidden");
						banHeight(0);
						open = true;
						close = false;
					}
				}
			} else {
				return;
			}
		}
		if (open) {
			if (delta < 0) { /*滚轮下滚动*/
				if (scrollNum++ > 5) {
					banHeight('-' + bHeight);
					open = false;
					close = true;
				}
			}
		}
	})
	
	/*------banner运动距离-------*/
	function banHeight(data) {
		if (data < 0) {
			$(".move_wrap").animate({
				"top": "90px"
			},500);
			
			$(".banner").animate({
				"top": data
			},500,function() {
				$("body").css({
					"overflow-y": "auto",
					"height": "auto"
				});
				$(".header").css("background","#333333");
			});
		} else {
			$(".header").css("background","none");
			$("body").css("overflow-y","hidden");
			$(".move_wrap").animate({
				"top": bHeight
			},500);
			$(".banner").animate({
				"top":data
			},500);
		}
	}
	
})