//微信小程序js
(function () {
    /**
     * 微信小程序分享
     * @param title 分享标题
     * @param desc 分享描述
     * @param url 点击跳转地址
     * @param image 分享显示的图片
     */
    function share(title,desc,url,image) {
        var postData = {
            url:"route/route?share="+encodeURIComponent(url),
            title,
            image,
            desc
        };
        wx.miniProgram.postMessage({ data: JSON.stringify(postData) });
    }

    var miniapp = {
        share:share,
    }

    if (typeof ms != "object") {
        window.ms = {};
        window.ms.weixin = {};
    }
    window.ms.weixin.miniapp = miniapp;
    //默认分享当前页面
    ms.weixin.miniapp.share(document.title,"分享",window.location.href,"")
}());

