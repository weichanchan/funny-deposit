$(function() {
	// focus部分
	var fo = $("#focus");
	var left_div = $(".fo_left");
	var right_div = $(".fo_right");
	var l_li = left_div.find("li");
	var r_li = right_div.find("li");
	
	var nn = 0;
	var timer = null;
	starTimer();
	r_li.click(function() {
		stopTimer();
		nn = $(this).index();
		$(this).addClass('active').siblings().removeClass();
		l_li.eq(nn).addClass("active").siblings().removeClass();
		l_li.eq(nn).stop().animate({'opacity':1},300).siblings().stop().animate({'opacity':0},300);
		starTimer();
	})
	
	function starTimer() {
		timer = setInterval(function () {
			startFocus();
		},2000);
	}
	
	function stopTimer() {
		clearInterval(timer);
	}
	
	
	function startFocus() {
		nn++;
		nn = nn > l_li.size() - 1 ? 0 : nn; 
		l_li.eq(nn).addClass("active").siblings().removeClass();
		l_li.eq(nn).stop().animate({'opacity':1},300).siblings().stop().animate({'opacity':0},300);
		r_li.eq(nn).addClass("active").siblings().removeClass();
	}
	
	// 轮播图
	$(".wrap_div").on('click', '.prev a', function(){
		var vcon = $(".wrap_div .v_cont");
		var offset = -1 * $(".wrap_div .v_cont ul li").width();
		var lastItem = $(".vr_show .v_cont ul li").last();
		vcon.find("ul").prepend(lastItem);
		vcon.css("left", offset);
		vcon.stop().animate({
			left: "0px"
		}, "slow" , function(){
			circle2();
		});
	});
	
	$(".wrap_div").on('click', '.next a', function() {
		var vcon = $(".wrap_div .v_cont");
		var offset = -1 * $(".wrap_div .v_cont ul li").width();
		vcon.stop().animate({
			left: offset
		}, "slow", function() {
			var firstItem = $(".vr_show .v_cont ul li").first();
			vcon.find("ul").append(firstItem);
			$(this).css("left", "0px");
			circle2();
		});
	});
	
	
	$(".video_num").on('click', '.next a' ,function() {
		var vcon = $(".video_num .v_cont");
		var offset = -1 * ($(".video_num .v_cont li").width())
		vcon.stop().animate({
			left: offset
		}, "slow", function() {
			// 获取第一个li元素
			var firstItem = $(".video_num .v_cont ul li").first();
			// 把第一个li加到ul最后面，这时候vcon的left为-390px；
			vcon.find("ul").append(firstItem);
			// 把vcon的left设为0
			$(this).css("left", "0px");
			circle();
		});
	})
	
	$(".video_num").on('click', '.prev a' ,function() {
		var vcon = $(".video_num .v_cont");
		var offset = -1 * ($(".video_num .v_cont li").width());
		// 获取最后一个li，并且添加到ul的最前边。
		var lastItem = $(".video_num .v_cont ul li").last();
		// 显示为第一个
		vcon.find("ul").prepend(lastItem);
		// 这时候把left设置成-390px，先隐藏起来，
		vcon.css("left", offset);
		// 这时候做动画，把left设置成0px，即第一个显示出来
		vcon.stop().animate({
			left: "0px"
		}, "slow" ,function() {
			circle();
		});
	});
	
	// 定义小圆圈
	function circle() {
		// 获取当前第一个的li
		var currentItem = $(".video_num .v_cont ul li").first();
		// 然后获取他的index属性值
		var currentIndex = currentItem.attr("index");
		// 先把所有li的cur类名清除掉
		$(".video_num .circle li").removeClass("circle-cur");
		// 把对应第几个小圆点加上cur类名
		$(".video_num .circle li").eq(currentIndex).addClass("circle-cur");
	}
	
	function circle2() {
		var curItem = $(".wrap_div .v_cont ul li").first();
		var curIndex = curItem.attr("index");
		$(".wrap_div .circle li").removeClass("li-cur");
		$(".wrap_div .circle li").eq(curIndex).addClass("li-cur");
	}
	
	// 点击上面的小圆点调到指定的index
	$(".wrap_div").on('click', '.circle li', function() {
		var that = $(this);
		circleClick(that,"wrap_div", "li-cur")
	});
	
	$(".video_num").on('click', '.circle li', function() {
		var that = $(this);
		circleClick(that,"video_num", "circle-cur");
	});
	
	var animateEnd = 1;
	function circleClick(jq,obj,cls) {
			if (animateEnd == 0) {
				return false;
			}
			jq.addClass(cls).siblings().removeClass(cls);
			var nextIndex = jq.index();
			var curIndex = $("."+obj+" .v_cont li").first().attr("index");
			var cur = $("."+obj+" .v_cont li").first().clone();
			if(nextIndex > curIndex) {
				for (var i = 0; i < nextIndex - curIndex; i++) {
					var firstItem = $("."+obj+" .v_cont li").first();
					$("."+obj+" .v_cont ul").append(firstItem);
				}
				$("."+obj+" .v_cont ul").prepend(cur);
				var offset = -1 * $("."+obj+" .v_cont li").width();
				if (animateEnd == 1) {
					animateEnd =0;
					$("."+obj+" .v_cont").stop().animate({
						left: offset
					}, "slow" ,function(){
						$("."+obj+" .v_cont ul li").first().remove();
						$("."+obj+" .v_cont").css("left", "0px");
						animateEnd = 1;
					});
				}
			} else {
				var cur = $("."+obj+" .v_cont li").last().clone();
				for (var i = 0; i < curIndex - nextIndex; i++) {
					var lastItem = $("."+obj+" .v_cont li").last();
					$("."+obj+" .v_cont ul").prepend(lastItem);
				}
				$("."+obj+" .v_cont ul").append(cur);
				var offset = -1 * $("."+obj+" .v_cont li").width();
				$("."+obj+" .v_cont").css("left", offset);
				if (animateEnd == 1) {
					animateEnd = 0;
					$("."+obj+" .v_cont").stop().animate({
						left: "0px"
					}, "slow" ,function() {
						$("."+obj+" .v_cont ul li").last().remove();
						animateEnd = 1;
					});
				}
			}
			
		
	}
	
	
	/*------------VR 游戏推荐榜-----------*/
	$(".news_right_tabs").on('click', 'li' , function() {
		$(this).addClass("spanColor").siblings().removeClass("spanColor");
		var curIndex = $(this).index();
		$(".mews_right_a a").hide();
		$(".mews_right_a a").eq(curIndex).show();
	})
	
	/*------------行业推荐榜--------------*/
	$(".af2").slide({
		affect: 4,
		time: 3000,
		speed: 400,
	})
	
});