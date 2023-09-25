//页面加载执行
window.onload = function() {
	//加载弹窗主体
	var cont = "<div class='ui-zdqh-container' id='ui-zdqh-container' style='position: fixed; margin-left: auto; margin-right: auto; left: 0px; right: 0px; top: 20%; display: none;'>" +
		"<div class='modal-bg'>" + "</div>" + "<img src='http://zwfw.sd.gov.cn/sdzw/resources/head/images/city_close.jpg' alt='' class='close_btn' id='close_btn'>" +
		"<h5 class='content1 place1'>" + "<span class='item selected sd' onclick='city2(this)' value='370000000000'>" + "山东省" + "</span>" + "</h5>" +
		"<div class='zd_list content1'>" + "<ul class='clearfix'>" + "<li id='itemt' class='item child' onclick='city(370100000000,'济南')'>" + "济南" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(370200000000,'青岛')'>" + "青岛" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(370300000000,'淄博')'>" + "淄博" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(370400000000,'枣庄')'>" + "枣庄" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(370500000000,'东营')'>" + "东营" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(370600000000,'烟台')'>" + "烟台" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(370700000000,'潍坊')'>" + "潍坊" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(370800000000,'济宁')'>" + "济宁" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(370900000000,'泰安')'>" + "泰安" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(371000000000,'威海')'>" + "威海" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(371100000000,'日照')'>" + "日照" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(371200000000,'临沂')'>" + "临沂" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(371300000000,'德州')'>" + "德州" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(371400000000,'聊城')'>" + "聊城" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(371500000000,'滨州')'>" + "滨州" + "</li>" +
		"<li id='itemt' class='item child' onclick='city(371600000000,'菏泽')'>" + "菏泽" + "</li>" + "</ul>" + "</div>" + "<div class='choosed content1 clearfix'>" +
		"<div class='rightItem'>" + "<h5 class='place2'>" + "<p style='display: inline'>" + "您的选择是：" + "</p>" + "<span data-code='370000000000'>" + "山东省" + "</span>" + "</h5>" + "</div>" +
		"<div class='buttoncontainer'>" + "<button class='submit1 mqy_click_button' mqy_params='%7B%22siteName%22:%22%E5%B1%B1%E4%B8%9C%E7%9C%81%22%7D' value='370000000000' onclick='goTourl(370000000000)'>" + "确定" +
		"</button>" + "<button class='quit'>" + "取消" + "</button>" + "</div>" + "</div>" + "</div>";
	//js渲染页面
	$("body").append(cont);

	city2();
	//弹窗显示
	$("#logosjxl").click(function() {
		$("#ui-zdqh-container").show();
	})
	//x弹窗关闭
	$("#close_btn").click(function() {
		$("#ui-zdqh-container").css("display", "none");
	})
	//取消弹窗关闭
	$(".quit").click(function() {
		$("#ui-zdqh-container").css("display", "none");
	})
}
//全局接口地址
var sdurl = "http://zwfw.sd.gov.cn/sdzw/front/head/findRegionsByParentCode.do";
//当前位置删除判断
var a = 1;
//city2顶部数值获取，加载数据
function city2($this) {
	//确定获取value值
	$(".submit1").attr("value", 370000000000);
	//判断空值
	a = 1
	$(".content1 ul").html('');
	//ajax兼容解决，开启cors
	jQuery.support.cors = true;
	$.ajax({
		url: sdurl,
		type: "Get",
		data: {
			parentCode: 370000000000
		},
		dataType: "json",
		success: function(res) {
			var data = res.data;
			for(var i = 0; i < data.length; i++) {
				var list = "<li id=\"itemt\" class=\"item child\"  onclick=city('" + data[i].code + "','" + data[i].name + "')>" + data[i].name + "</li>"
				$(".content1 ul").append(list);
			}
			$($this).nextAll().remove();
			var poistion = $(".place1")[0].innerText
			$(".rightItem .place2 span").html(poistion);
		},
		fail: function(err) {
			console.log(err)
		}

	})

}

//数据加载主函数
function city(code, name) {
	//确定获取value值
	$(".submit1").attr("value", code);
	//ajax兼容解决，开启cors
	jQuery.support.cors = true;
	$.ajax({
		url: sdurl,
		data: {
			parentCode: code
		},
		type: "Get",
		dataType: "json",
		success: function(res) {
			//上下添加当前位置
			var wz = "<span class='item selected' onclick=city1(" + code + ",'" + name + "',this)>" + "<b>></b>" + name + "</span>";

			var data = res.data;
			//判断最后一级code值，最后一级无法点击
			if(res.code == 200) {

				$(".content1 ul").html('');
				for(var i = 0; i < data.length; i++) {
					var list = "<li id=\"itemt\" class=\"item child\" onclick=city('" + data[i].code + "','" + data[i].name + "')>" + data[i].name + "</li>"
					$(".content1 ul").append(list);

				}
				// $(".place1").append(wz);
				if(a == 0) {
					$(".place1 span").last().remove();
					$(".place1").append(wz)
					a = 1

				} else {
					$(".place1").append(wz);
				}
				var poistion = $(".place1")[0].innerText
				$(".rightItem .place2 span").html(poistion);
			} else {
				//顶部当前位置删除后方节点
				if(a == 1) {
					$(".place1").append(wz);
					a = 0;
					var poistion = $(".place1")[0].innerText;
					$(".rightItem .place2 span").html(poistion);
				} else {
					$(".place1 span").last().html("<b>></b>" + name);
					var poistion = $(".place1")[0].innerText;
					$(".rightItem .place2 span").html(poistion);
				}

			}

		},
		fail: function(err) {
			console.log(err);
		}
	})

}
//city1获取底部当前位置
function city1(code, name, $this) {
	//确定获取value值
	$(".submit1").attr("value", code);
	a = 1;
	//ajax兼容解决，开启cors
	jQuery.support.cors = true;
	$.ajax({
		url: sdurl,
		type: "Get",
		data: {
			parentCode: code
		},
		dataType: "json",
		success: function(res) {
			var data = res.data;
			if(res.code == 200) {
				$(".content1 ul").html('')
				for(var i = 0; i < data.length; i++) {
					var list = "<li class=\"item child\" onclick=city('" + data[i].code + "','" + data[i].name + "')>" + data[i].name + "</li>"
					$(".content1 ul").append(list)
				}
			} else {
				return false;
			}
			//底部当前位置移除节点
			$($this).nextAll().remove();
			var poistion = $(".place1")[0].innerText;
			$(".rightItem .place2 span").html(poistion);
		},
		fail: function(err) {
			console.log(err)
		}

	})

}
//goTourl页面跳转函数
function goTourl() {
	goUrl($(".submit1").val());
}