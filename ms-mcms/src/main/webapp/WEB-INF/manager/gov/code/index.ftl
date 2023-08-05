<html>
<head>
    <meta charset="utf-8" />
    <title>手机验证</title>
    <script>
        function browserVersion() {
            //  检测浏览器版本相关信息，对低版本进行消息提示
            var browserInfo = navigator                 //  获取浏览器对象信息
            var broVersion = browserInfo.appVersion     //  浏览器版本信息
            var version = broVersion.split(";");        //  提取浏览器版本信息关键字段
            var trim_version = version[1].replace(/[ ]/g, "");
            //  是否为ie浏览器
            if (browserInfo.appName == "Microsoft Internet Explorer") {
                //  包含ie标识且不为ie10版本信息
                if (trim_version.indexOf("MSIE")>-1 && trim_version !== "MSIE10.0") {

                    return 'MSIE9.0'

                    //  包含ie10浏览器标识
                }else if(trim_version.indexOf("MSIE10.0")>-1) {

                    return 'MSIE10.0'
                }
            }else {
                return 'other'
            }
        }
        //控制ie9以下提示信息
        window.onload = function(){
            var browserinfo = browserVersion()
            if(browserinfo == 'MSIE10.0' || browserinfo == 'MSIE9.0') {
                document.body.style.width ="100%";
                document.body.style.height ="100%";
                document.body.innerHTML = '<div style="height:100vh;width:100vw;background-image:url(/static/images/1577692394350.jpg);"><div style="background-color:#ffffff;border-radius:12px;height:540px;width:1000px;margin:0 auto;position:relative;top:20%;text-align:center;padding:50px;line-height:50px;color:#333333;font-size:30px;"><img src="/static/images/upgrade.png" /><p>当前浏览器版本过低,无法登录，推荐使用ie11及以上、360、谷歌等浏览器达到最佳体验。</p><a target="_blank" href="https://pc.qq.com/detail/6/detail_11526.html">升级IE浏览器</a><span>    </span><a target="_blank" href="https://pc.qq.com/detail/1/detail_2661.html">下载谷歌浏览器</a></div></div>';
            }
        };
    </script>
    <#include "/include/head-file.ftl"/>
    <#include "/include/head-file-gov.ftl"/>
    <link rel="stylesheet" type="text/css" href="${base}/static/plugins/TextInputEffects/css/normalize.css" />
    <link rel="stylesheet" type="text/css" href="${base}/static/plugins/TextInputEffects/css/set1.css" />
    <style>
        [v-cloak]{
            display: none;
        }
    </style>
</head>
<body class="custom-body">
<!---gov login-->
<div id="app" v-cloak>
    <!--大容器开始-->
    <div  class="class-1" :style="{backgroundImage:'url('+uiConfig.uiLoginBg+')'}" @keydown.13='login'>
        <!--大容器开始-->
        <div  class="class-2">
            <!--大容器开始-->
            <div  class="class-3" >
                <!--图片开始-->
                <img
                        :src="uiConfig.uiLoginSlogin"
                        class="class-4" />
                <!--图片结束-->
            </div>
            <!--大容器结束-->
            <!--大容器开始-->
            <div  class="class-5">
                <!--小容器开始-->
                <div  class="class-6" >
                    <!--文本开始-->
                    <div class="class-7">
                        手机验证
                    </div>
                    <!--文本结束-->
                    <!--小容器开始-->
                    <el-form :model="form" ref="form" :rules="rules">
                        <div  class="class-8" >
                            <el-form-item prop="phone">
                     <span class="input input--hoshi">
                          <input v-model="form.phone" class="input__field input__field--hoshi" type="text" :disabled="true" id="input-name"/>
                          <label class="input__label input__label--hoshi input__label--hoshi-color-1" for="input-name">
                              <span class="input__label-content input__label-content--hoshi">手机号</span>
                          </label>
                      </span>
                            </el-form-item>
                        </div>
                        <!--小容器结束-->

                        <!--小容器开始-->
                        <div  class="class-16"  >
                            <!--小容器开始-->
                            <div  class="class-17" >
                                <!--文本开始-->
                                <el-form-item prop="code">
                     <span class="input input--hoshi" style="width: 86%">
                          <input v-model="form.code" class="input__field input__field--hoshi" type="text" id="input-rand-code"  />
                          <label class="input__label input__label--hoshi input__label--hoshi-color-1" for="input-rand-code">
                              <span class="input__label-content input__label-content--hoshi">验证码</span>
                          </label>
                      </span>
                                </el-form-item>
                            </div>
                            <!--小容器结束-->
                            <!--大容器开始-->
                            <div class="class-20" >
                                <el-button type="primary" icon="el-icon-position" plain style="margin-left: 8px" v-bind:type="buttonCodeType" v-on:click="sendCode" v-bind:disabled="buttonCodeDisable">
                                    {{buttonCodeValue}}
                                </el-button>
                            </div>
                            <!--大容器结束-->
                        </div>
                        <!--小容器结束-->
                    </el-form>
                    <!--小容器开始-->
                    <div  class="class-25" >
                    </div>
                    <!--小容器结束-->
                    <!--按钮开始-->
                    <el-button @click="checkCode" type="primary" :loading="loading"
                               class="class-26">
                        {{loading?'验证中':'验证'}}
                    </el-button>
                    <!--按钮结束-->

                </div>
                <!--小容器结束-->
            </div>
            <!--大容器结束-->
        </div>
        <!--大容器结束-->
    </div>
    <!--大容器结束-->
</div>
</body>
</html>
<script src="${base}/static/plugins/TextInputEffects/js/classie.js"></script>
<script>
    var app = new Vue({
        el: '#app',
        watch:{

        },
        data: {
            //验证码按钮样式
            buttonCodeType: 'primary',
            //验证码按钮显示值
            buttonCodeValue: '发送验证码',
            //验证码按钮是否显示
            buttonCodeDisable: false,
            //验证码倒计时
            codeTimeCount: 60,
            //验证码计时器
            codeTimer: null,
            base:ms.base,
            loading:false,
            form:{
                // 手机号
                phone: '',
                // 验证码
                code: '',
            },
            rules: {
                // 手机号
                phone: [{
                    "pattern": /^([0-9]{3,4}-)?[0-9]{7,8}$|^\d{3,4}-\d{3,4}-\d{3,4}$|^1[0-9]{10}$/,
                    "message": "手机号格式不匹配"
                }, {"min": 0, "max": 255, "message": "手机号长度必须为0-255"}],
                // 验证码
                code: [{"min": 0, "max": 255, "message": "验证码长度必须为0-255"}],
            },
            uiConfig: {
                uiLoginSlogin: "",
                uiLoginBg: "",
            }
        },
        methods: {
            codeCountDown: function () {
                // 验证码倒计时
                if (!this.codeTimer) {
                    this.count = this.codeTimeCount
                    //验证码发送成功，倒计时
                    this.buttonCodeType = 'info'
                    this.buttonCodeDisable = true
                    this.buttonCodeValue = "已发送(" + this.count + ")"

                    //第一种缩写写法
                    this.codeTimer = setInterval(() => {
                        if (this.count > 0 && this.count <= this.codeTimeCount) {
                            this.count--

                            this.buttonCodeValue = "已发送(" + this.count + ")"
                            console.log(this.count)
                        } else {
                            clearInterval(this.codeTimer)
                            this.codeTimer = null
                            console.log('倒计时结束')
                            this.buttonCodeType = 'success'
                            this.buttonCodeDisable = false
                            this.buttonCodeValue = '验证码'

                        }
                    }, 1000)
                }
            },
            sendCode: function () {
                var that = this
                ms.http.post(ms.manager + "/gov/code/sendCode.do").then(function (res) {
                    if (res.result) {
                        that.$notify({
                            title: "成功",
                            message: "发送验证码成功!",
                            type: 'success'
                        });
                        that.codeCountDown();
                    } else {
                        that.$notify({
                            title: "错误",
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                })

            },
            checkCode: function () {
                var that = this
                ms.http.post(ms.manager + "/gov/code/checkCode.do", {
                    code: that.form.code
                }).then(function (res) {
                    if (res.result) {
                        location.href = ms.manager + "/index.do";
                    } else {
                        that.$notify({
                            title: "错误",
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                })

            },

            getuiConfig: function () {
                var that = this;
                ms.mdiy.config("后台UI配置",'uiLoginSlogin').then(function (res){
                    if(res.result){
                        res.data && JSON.parse(res.data) ? that.uiConfig.uiLoginSlogin = JSON.parse(res.data)[0].url : that.uiConfig.uiLoginSlogin = "${base}/static/images/login-banner.png"
                    } else {
                        that.uiConfig.uiLoginSlogin = "${base}/static/images/login-banner.png"
                    }
                }).catch(function(err){
                    that.uiConfig.uiLoginSlogin = "${base}/static/images/login-banner.png"
                })
                ms.mdiy.config("后台UI配置",'uiLoginBg').then(function (res){
                    if(res.result){
                        res.data && JSON.parse(res.data) ? that.uiConfig.uiLoginBg = JSON.parse(res.data)[0].url : that.uiConfig.uiLoginBg = "${base}/static/images/login-bg.jpg"
                    } else {
                        that.uiConfig.uiLoginBg = "${base}/static/images/login-bg.jpg"
                    }
                }).catch(function(err){
                    that.uiConfig.uiLoginBg = "${base}/static/images/login-bg.jpg"
                })
            },
        },
        created:function(){
            var that = this;
            that.form.phone = '${phone}',
            that.getuiConfig();
        }
    })
</script>
<script>
    (function() {
        // trim polyfill : https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/Trim
        if (!String.prototype.trim) {
            (function() {
                // Make sure we trim BOM and NBSP
                var rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
                String.prototype.trim = function() {
                    return this.replace(rtrim, '');
                };
            })();
        }

        [].slice.call( document.querySelectorAll( 'input.input__field' ) ).forEach( function( inputEl ) {
            // in case the input is already filled..
            if( inputEl.value.trim() !== '' ) {
                classie.add( inputEl.parentNode, 'input--filled' );
            }

            // events:
            inputEl.addEventListener( 'focus', onInputFocus );
            inputEl.addEventListener( 'blur', onInputBlur );
        } );

        function onInputFocus( ev ) {
            classie.add( ev.target.parentNode, 'input--filled' );
        }

        function onInputBlur( ev ) {
            if( ev.target.value.trim() === '' ) {
                classie.remove( ev.target.parentNode, 'input--filled' );
            }
        }
    })();
</script>

<style>
    .el-form-item{
        margin-bottom: 0px;
    }
    .el-form-item__content{
        line-height: initial;
    }
    .custom-body {
    }
    .class-1
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        background-size:cover;
        background-position:center;
        height:100%;
        max-width:100%;
        align-items:center;
        flex-direction:row;
        display:flex;
        justify-content:center;
        animation-duration:1s;
        width:100%;
        background-repeat:no-repeat;
    }
    .class-2
    {

        color:#333333;
        outline:none;
        outline-offset:-1px;
        height:540px;
        max-width:100%;
        background-color:rgba(255, 255, 255, 1);
        flex-direction:row;
        display:flex;
        animation-duration:1s;
        border-radius:12px;
        width:1000px;
        background-repeat:no-repeat;
        -webkit-box-shadow: 0px 20px 80px 0px rgb(0 0 0 / 30%);
        box-shadow: 0px 80px 80px 0px rgb(0 0 0 / 30%);
    }
    .class-3
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        height:100%;
        max-width:100%;
        align-items:flex-start;
        flex-direction:row;
        display:flex;
        justify-content:flex-start;
        animation-duration:1s;
        width:460px;
        background-repeat:no-repeat;
    }
    .class-4
    {
        height:100%;
        animation-duration:1s;
        width:100%;
    }
    .class-5
    {
        color:#333333;
        outline:none;
        padding-bottom:20px;
        outline-offset:-1px;
        flex:1;
        padding-top:20px;
        height:100%;
        max-width:100%;
        align-items:center;
        flex-direction:column;
        display:flex;
        justify-content:flex-start;
        animation-duration:1s;
        width:200px;
        background-repeat:no-repeat;
    }
    .class-6
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        max-width:100%;
        flex-direction:column;
        display:flex;
        animation-duration:1s;
        width:330px;
        background-repeat:no-repeat;
        margin-top:20px;
    }
    .class-7
    {
        color:#333333;
        word-wrap:break-word;
        display:inline-block;
        animation-duration:1s;
        font-size:36px;
        line-height:1.4;
        margin-bottom:20px;
    }
    .class-8
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        height:80px;
        max-width:100%;
        flex-direction:column;
        display:flex;
        justify-content:flex-end;
        animation-duration:1s;
        width:100%;
        background-repeat:no-repeat;
    }
    .class-9
    {
        color:#BBBBBB;
        word-wrap:break-word;
        display:inline-block;
        animation-duration:1s;
        font-size:12px;
        line-height:1.4;
    }
    .class-10
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        height:40px;
        max-width:100%;
        align-items:center;
        flex-direction:row;
        display:flex;
        animation-duration:1s;
        width:100%;
        background-repeat:no-repeat;
    }
    .class-11
    {
        color:#333333;
        word-wrap:break-word;
        display:inline-block;
        animation-duration:1s;
        font-size:14px;
        line-height:1.4;
    }
    .class-12
    {
        margin-right:auto;
        animation-duration:1s;
        background-color:#eee;
        border-radius:1px;
        width:100%;
        height:1px;
        margin-left:auto;
    }
    .class-13
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        height:80px;
        max-width:100%;
        flex-direction:column;
        display:flex;
        justify-content:flex-end;
        animation-duration:1s;
        width:100%;
        background-repeat:no-repeat;
    }
    .class-14
    {
        color:#BBBBBB;
        word-wrap:break-word;
        padding-bottom:10px;
        display:inline-block;
        animation-duration:1s;
        font-size:14px;
        line-height:1.4;
    }
    .class-15
    {
        margin-right:auto;
        animation-duration:1s;
        background-color:#eee;
        border-radius:1px;
        width:100%;
        height:1px;
        margin-left:auto;
    }
    .class-16
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        height:80px;
        max-width:100%;
        align-items:flex-end;
        flex-direction:row;
        display:flex;
        justify-content:flex-start;
        animation-duration:1s;
        background-repeat:no-repeat;
    }
    .class-17
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        flex:1;
        height:80px;
        max-width:100%;
        flex-direction:column;
        display:flex;
        justify-content:flex-end;
        animation-duration:1s;
        width:200px;
        background-repeat:no-repeat;
    }
    .class-18
    {
        color:#BBBBBB;
        word-wrap:break-word;
        display:inline-block;
        animation-duration:1s;
        font-size:14px;
        line-height:1.4;
        margin-bottom:10px;
    }
    .class-19
    {
        margin-right:auto;
        animation-duration:1s;
        background-color:#eee;
        border-radius:1px;
        width:100%;
        height:1px;
        margin-left:auto;
    }
    .class-20
    {
        cursor:pointer;
        color:#333333;
        margin-right:10px;
        outline-offset:-1px;
        height:40px;
        max-width:100%;
        align-items:center;
        flex-direction:row;
        display:flex;
        justify-content:center;
        margin-left:10px;
        animation-duration:1s;
        width:88px;
        background-repeat:no-repeat;
        margin-bottom: 0.85em;
    }
    .class-21
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        max-width:100%;
        align-items:flex-end;
        flex-direction:column;
        display:flex;
        justify-content:flex-end;
        animation-duration:1s;
        background-repeat:no-repeat;
        margin-bottom: 0.85em;
    }
    .class-22
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        max-width:100%;
        flex-direction:column;
        display:flex;
        animation-duration:1s;
        background-repeat:no-repeat;
    }
    .class-23
    {
        color:#BBBBBB;
        word-wrap:break-word;
        display:inline-block;
        animation-duration:1s;
        font-size:12px;
        line-height:1.4;
    }
    .class-24
    {
        cursor:pointer;
        color:#0099FF;
        word-wrap:break-word;
        display:inline-block;
        animation-duration:1s;
        font-size:12px;
        line-height:1.4;
    }
    .class-25
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        height:40px;
        max-width:100%;
        flex-direction:row;
        display:flex;
        animation-duration:1s;
        width:100px;
        background-repeat:no-repeat;
    }
    .class-26
    {
        background-color:#0099ff;
    }
    .class-27
    {
        color:#333333;
        outline:none;
        outline-offset:-1px;
        max-width:100%;
        align-items:center;
        flex-direction:row;
        display:flex;
        animation-duration:1s;
        width:100px;
        background-repeat:no-repeat;
        margin-top:20px;
    }
    .class-28
    {
        color:#333333;
        outline:1px dashed hsla(0, 0%, 66.7%, .7);
        outline-offset:-1px;
        height:14px;
        max-width:100%;
        flex-direction:row;
        display:flex;
        animation-duration:1s;
        width:14px;
        background-repeat:no-repeat;
    }
    .class-29
    {
        color:#999999;
        word-wrap:break-word;
        display:inline-block;
        margin-left:10px;
        animation-duration:1s;
        font-size:14px;
        line-height:1.4;
    }
    @media (max-width: 768px){
    }
    .input__label--hoshi::before{
        content: '';
        position: absolute;
        top: 1px;
        left: 0;
        width: 100%;
        height: calc(100% - 10px);
        border-bottom: 1px solid #B9C1CA;
    }
</style>
