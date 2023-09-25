function goUrl(appmark){ 
	var ticket = "";
	var type = "";
	$.ajax( {
		url : "/center/front/forcwticket.do?appmark="+appmark,
		context : document.body,
		datetype : "text",
		async : false,
		success : function(text) {
					if(text.indexOf("errormsg")>-1){
						window.open("http://zwfw.sd.gov.cn");
//						window.open("http://www.shandong.gov.cn");
					}else{
						var obj=document.getElementById("hreftag");
						obj.targe="_blank";
						if(text!='null'){
							ticket = text.substring(0, text.length);
							type = text.substring(text.length-1, text.length);
						}
//						obj.href="http://zwfw.sd.gov.cn/sdzw/web/getUrl.do?ticket="+ticket+"&appmark="+appmark+"&gotourl="; 
						obj.href="http://www.shandong.gov.cn/center/web/getUrl.do?ticket="+ticket+"&appmark="+appmark+"&gotourl="; 
						obj.click(); 
					}
				}
	});
}
