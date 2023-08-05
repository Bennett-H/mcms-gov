/**
 * 无障碍
 */
(function () {
    ms.http.get("/gov/accessibility/getToken.do").then(function (res) {
        if(res.data.start === "true"){
            var config = {namespace: "mozi-assist", au_token: res.data.accessToken}
            new AssistEntry(config)
            //更换背景色
            var moziAssist = document.getElementById('mozi-assist-topbar-html');
            moziAssist.style.background = res.data.color;
        }else {
            //关闭无障碍
            var assistOpen = document.getElementById('assist-open')
            if(assistOpen){
                assistOpen.parentNode.removeChild(assistOpen)
            }
        }
    });
})()