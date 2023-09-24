/**
 * 微信分享、授权
 */

(function () {

    /**
     * 需要全局定义一个微信号 ms.weixinNo
     * @param redirectUri 授权成功后重定向的页面
     * @returns {string}
     */
    function oauth(redirectUri) {
        if(ms.weixinNo){
            var ua = window.navigator.userAgent.toLowerCase();
            if(ua.match(/MicroMessenger/i) == 'micromessenger'){
                return  location.origin+ms.base+"/mweixin/oauth/getUrl.do?weixinNo="+weixinNo+"&url="+encodeURIComponent(location.origin+ms.base+redirectUri)
            }
        }
        return location.origin+ms.base+redirectUri
    }

    /**
     * 需要配置一个全局的ms.weixinNo
     * @param title 分享标题
     * @param desc 分享描述
     * @param link 分享链接（绝对地址），该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
     * @param imgUrl 分享图片（绝对地址）
     * @param debug false/true
     * @returns {string}
     */
    function share(title,desc,link,imgUrl,debug) {
        if(ms.weixinNo){
            var ua = window.navigator.userAgent.toLowerCase();
            if(ua.match(/MicroMessenger/i) == 'micromessenger'){
                ms.http.post(ms.base+"/mweixin/jsSdk/createJsapiSignature.do",{url:link,weixinNo:ms.weixinNo}).then(function (data) {
                    if(data.result){
                        wx.config({
                            debug: debug, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                            appId: data.data.appId, // 必填，公众号的唯一标识
                            timestamp: data.data.timestamp, // 必填，生成签名的时间戳
                            nonceStr: data.data.nonceStr, // 必填，生成签名的随机串
                            signature: data.data.signature,// 必填，签名
                            jsApiList: [
                                'checkJsApi',
                                'onMenuShareTimeline',
                                'onMenuShareAppMessage',
                                'onMenuShareQQ',
                                'onMenuShareWeibo',
                                'onMenuShareQZone',
                                'hideAllNonBaseMenuItem',
                                'showAllNonBaseMenuItem',
                                'closeWindow',
                                'updateAppMessageShareData',
                                'updateTimelineShareData'
                            ]
                        });
                        //自定义“分享给朋友”及“分享到QQ”按钮的分享内容（1.4.0）
                        wx.ready(function () {   //需在用户可能点击分享按钮前就先调用
                            wx.updateAppMessageShareData({
                                title: title, // 分享标题
                                desc: desc, // 分享描述
                                link: link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
                                imgUrl: imgUrl, // 分享图标
                                success: function () {
                                    // 设置成功
                                }
                            });
                            //自定义“分享到朋友圈”及“分享到QQ空间”按钮的分享内容
                            wx.updateTimelineShareData({
                                title: title, // 分享标题
                                link: link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
                                imgUrl: imgUrl, // 分享图标
                                success: function () {
                                    // 设置成功
                                }
                            })


                        });
                    }
                });
            }
        }
    }

    var weixin = {
        oauth:oauth,
        share:share,
    }

    if (typeof ms != "object") {
        window.ms = {};
    }
    window.ms.weixin = weixin;
}());