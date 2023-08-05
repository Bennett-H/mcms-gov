<html>
<head>
    <meta charset="utf-8" />
    <title>${app.appName}</title>
    <script>
        //极速模式提示信息-最简练的判断方式
        if (navigator.userAgent.indexOf(".NET") > -1) {
            var svg_ie = "<svg t='1587836980741' class='icon' style='width: 2em; height: 2em; vertical-align: middle; fill: currentcolor; overflow: hidden; font-size:30px;' viewBox='0 0 1024 1024' version='1.1' xmlns='http://www.w3.org/2000/svg' p-id='2422' width='32' height='32'><path d='M804.5 334c12.7-44 16.7-129.9-62.1-140.5C679 184.9 604 227.2 564.5 253.3c-13-1.8-26.6-3-41-3.1-102.7-1.3-169.4 33.6-225.9 107.5-20.8 27.2-39.3 73.3-45.1 125.6 29.2-49.6 116.3-139.1 209.9-175.9 0 0-140.5 100.4-209.4 243.7l-0.2 0.2c0.1 0.7 0.1 1.4 0.2 2.1-2.9 6.5-6 13-8.8 19.9-68.6 168.5-12.5 241.4 38.8 255 47.3 12.4 113.8-10.7 166.7-67.5 90.6 21.3 179.5-2.6 213.4-21.2 63.6-34.9 106.8-96.5 117-159.8L606.7 579.8c0 0-7.3 56.1-100.4 56.1-85.8 0-89.4-99.3-89.4-99.3l368.7 0c0 0 7-107-45.9-178.3-29.3-39.6-69.5-74.5-125.4-93.3 17.1-12.6 46.6-32.2 71.5-38.7 47.2-12.3 79.5-5 99.8 29.3 27.5 46.7-15.2 156-15.2 156S791.3 380 804.5 334zM423.6 753.4c-73 59.4-133.5 52.9-156.8 17.1-20.2-31.2-23.8-87.3-0.1-163.7 11.1 29.6 28.5 58.3 54.3 83.8C352.4 721.6 387.7 741.3 423.6 753.4zM420.9 444.9c0 0 3.7-70.4 80.2-76.8 66.8-5.5 101.3 23.6 111.6 80.2L420.9 444.9z' p-id='2423' fill='#51b72f' data-spm-anchor-id='a313x.7781069.0.i11'></path></svg>";
            var svg_thunder = "<svg t='1588218903014' class='icon' style='width: 1.5em; height: 1.5em; vertical-align: middle; fill: currentcolor; overflow: hidden; font-size: 30px;' viewBox='0 0 1024 1024' version='1.1' xmlns='http://www.w3.org/2000/svg' p-id='4411' width='30' height='30'><path d='M395.765 586.57H224.032c-22.421 0-37.888-22.442-29.91-43.38L364.769 95.274a32 32 0 0 1 29.899-20.608h287.957c22.72 0 38.208 23.018 29.632 44.064l-99.36 243.882h187.05c27.51 0 42.187 32.427 24.043 53.099l-458.602 522.56c-22.294 25.408-63.627 3.392-54.976-29.28l85.354-322.421z' p-id='4412' fill='#51b72f'></path></svg>";
            var html = document.querySelector("html");
            html.innerHTML = "<div style='position:fixed;width: 100%;height: 100%;background:white;z-index:999999999999;font-size:30px;'><p style='font-family: \"Microsoft YaHei\"'>当前为IE兼容模式，推荐使用谷歌、360、QQ、火狐等新版本浏览器使用系统，如果是360浏览器请开启<b style='color:#51b72f'>极速模式</b><br>操作方式：点击浏览器地址栏右侧的IE符号" + svg_ie + "→选择“" + svg_thunder + "<b style='color:#51b72f;'>极速模式(推荐)</b>”</p></div>";
            html.style.overflow = "hidden";
        }

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
                document.body.innerHTML = '<div style="height:100vh;width:100vw;background-image:url(/static/images/1577692394350.jpg);"><div style="background-color:#ffffff;border-radius:12px;height:540px;width:1000px;margin:0 auto;position:relative;top:20%;text-align:center;padding:50px;line-height:50px;color:#333333;font-size:30px;"><img src="/static/images/upgrade.png" /><p>当前浏览器为IE,推荐360、谷歌、火狐等浏览器达到最佳体验。</p><a target="_blank" href="https://pc.qq.com/detail/6/detail_11526.html">升级IE浏览器</a><span>    </span><a target="_blank" href="https://pc.qq.com/detail/1/detail_2661.html">下载谷歌浏览器</a></div></div>';
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
                        登录
                    </div>
                    <!--文本结束-->
                    <!--小容器开始-->
                    <el-form :model="form" ref="form" :rules="rules">
                        <div  class="class-8" >
                            <el-form-item prop="managerName">
                     <span class="input input--hoshi">
                          <input v-model="form.managerName" class="input__field input__field--hoshi" type="text" id="input-name" />
                          <label class="input__label input__label--hoshi input__label--hoshi-color-1" for="input-name">
                              <span class="input__label-content input__label-content--hoshi">账号</span>
                          </label>
                      </span>
                            </el-form-item>
                        </div>
                        <!--小容器结束-->
                        <!--小容器开始-->
                        <div  class="class-13" >
                            <!--文本开始-->
                            <el-form-item prop="managerPassword">
                  <span class="input input--hoshi">
                          <input v-model="form.managerPassword" class="input__field input__field--hoshi" type="password" id="input-password" />
                          <label class="input__label input__label--hoshi input__label--hoshi-color-1" for="input-password">
                              <span class="input__label-content input__label-content--hoshi">密码</span>
                          </label>
                      </span>
                            </el-form-item>
                        </div>
                        <!--小容器结束-->
                        <!--小容器开始-->
                        <div  class="class-16" v-if="form.codeConfig" >
                            <!--小容器开始-->
                            <div  class="class-17" >
                                <!--文本开始-->
                                <el-form-item prop="rand_code">
                     <span class="input input--hoshi">
                          <input v-model="form.rand_code" class="input__field input__field--hoshi" type="text" id="input-rand-code" />
                          <label class="input__label input__label--hoshi input__label--hoshi-color-1" for="input-rand-code">
                              <span class="input__label-content input__label-content--hoshi">验证码</span>
                          </label>
                      </span>
                                </el-form-item>
                            </div>
                            <!--小容器结束-->
                            <!--大容器开始-->
                            <div class="class-20" >
                                <img :src="verifCode" class="code-img" @click="code" />
                            </div>
                            <!--大容器结束-->
                            <!--小容器开始-->
                            <div  class="class-21" >
                                <!--小容器开始-->
                                <div @click="code"
                                     class="class-22" >
                                    <!--文本开始-->
                                    <div class="class-23">

                                        看不清？

                                    </div>
                                    <!--文本结束-->
                                    <!--文本开始-->
                                    <div class="class-24">

                                        换一张

                                    </div>
                                    <!--文本结束-->
                                </div>
                                <!--小容器结束-->
                            </div>
                            <!--小容器结束-->
                        </div>
                        <!--小容器结束-->
                    </el-form>
                    <!--小容器开始-->
                    <div  class="class-25" >
                    </div>
                    <!--小容器结束-->
                    <!--按钮开始-->
                    <el-button @click="login" type="primary" :loading="loading"
                               class="class-26">
                        {{loading?'登录中':'登录'}}
                    </el-button>
                    <!--按钮结束-->
                    <!--小容器开始-->
                    <div  class="class-27" v-if="form.rememberMeConfig">
                        <el-checkbox v-model="form.rememberMe">记住我</el-checkbox>
                        <!--文本结束-->
                    </div>
                    <!--小容器结束-->
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
            // 登录范围
            timeStart: '',
            timeEnd: '',
            base:ms.base,
            loading:false,
            form:{
                managerName:'',
                managerPassword:'',
                // 验证码显示开关
                codeConfig: true,
                // 记住我显示开关
                rememberMeConfig: true,
                rand_code:'',
                rememberMe:false,
            },
            rules:{
                managerName:[
                    { required: true, message: '请输入账号', trigger: 'blur' },
                    { min: 1, max: 30, message: '长度不能超过30个字符', trigger: 'change' }
                ],
                managerPassword:[
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 1, max: 30, message: '长度不能超过30个字符', trigger: 'change' }
                ],
                rand_code:[
                    { required: true, message: '请输入验证码', trigger: 'blur' },
                    { min: 1, max: 4, message: '长度不能超过4个字符', trigger: 'change' }
                ],
            },
            verifCode: ms.manager + "/code.do?t=" + new Date().getTime(),
            publicKey: '',
            uiConfig: {
                uiLoginSlogin: "",
                uiLoginBg: "",
            },
        },
        methods: {
            //登录
            login:function () {
                var that = this;
                // 登录接口
                that.$refs.form.validate(function(valid){
                    if (valid) {
                        that.loading = true;
                        ms.http.post(ms.manager + "/login.do", {
                            managerName:that.rsaEncrypt(that.form.managerName),
                            managerPassword:that.rsaEncrypt(that.form.managerPassword),
                            rand_code:that.form.rand_code,
                            rememberMe:that.form.rememberMe
                        }).then(function (res) {
                            if(res.result){
                                if(res.data!=null && res.data!='undefined' && res.data.length!=0){
                                    that.$notify({
                                        title: '警告',
                                        message: res.msg,
                                        type: 'warning',
                                        duration: 1000,
                                        onClose:function(){
                                            location.href= ms.manager + "/index.do";
                                        }
                                    });
                                }else{
                                    location.href= ms.manager + "/index.do";
                                }
                            }else {
                                if (res.msg == "passwordMaxDay"){
                                    that.$notify({
                                        title: '密码修改提示',
                                        message: "密码已经超过"+res.day+"天没有修改，请立即修改密码！",
                                        type: 'warning',
                                        close:function() {
                                            location.href = ms.manager + "/password.do";
                                        }
                                    });
                                } else if (res.msg == "error.first.update.passwords"){
                                    that.$notify({
                                        title: '密码修改提示',
                                        message: "新账号登录必须先修改密码才能使用系统",
                                        type: 'warning',
                                        close:function() {
                                            location.href = ms.manager + "/password.do";
                                        }
                                    });
                                } else {
                                    that.$notify({
                                        title: '登录失败',
                                        message: res.msg,
                                        type: 'error'
                                    });
                                    that.code()
                                }
                                that.loading = false;
                            }
                        }).catch(function (err) {
                            that.code()
                            that.loading = false;
                        });
                    }
                });
            },
            //获取验证码
            code:function(){
                this.verifCode = ms.web + "/code.do?t=" + new Date().getTime();
            },
            getCodeConfig: function () {
                var that = this;
                ms.mdiy.config("后台开发配置",'managerCheckCode').then(function (res){
                    if(res.result){
                        if (res.data == 'false') {
                            that.form.codeConfig = false;
                        }
                    }
                })
            },
            getRememberMeConfig: function () {
                var that = this;
                ms.mdiy.config("安全设置",'rememberMeEnable').then(function (res){
                    if(res.result){
                        if (res.data == 'false') {
                            that.form.rememberMeConfig = false;
                        }
                    }
                })
            },
            // 获取登录时间范围
            getLoginTime: function () {
                var that = this;
                var date = new Date();
                // 开始时间
                ms.mdiy.config("安全设置",'timeStart').then(function (res){
                    if (res.data){
                        that.timeStart = date.toLocaleDateString()+' '+res.data
                    }
                })
                // 结束时间
                ms.mdiy.config("安全设置",'timeEnd').then(function (res){
                    if (res.data){
                        that.timeEnd = date.toLocaleDateString()+' '+res.data
                    }
                })
            },
            //初始
            initial:function(){
                this.form.managerName = localStorage.getItem('managerName');
                this.form.managerPassword =  localStorage.getItem('managerPassword');
                top.location != self.location?(top.location = self.location):'';
            },
            //rsa加密
            getPublicKey:function(){
                var that = this;
                ms.http.post(ms.base+"/gov/publicKey/getPublicKey.do", {}).then(function (res) {
                    if(res.result){
                        that.publicKey = res.data
                    }else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'error'
                        });
                    }
                })
            },
            rsaEncrypt:function(pwd){
                var that = this;
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey(that.publicKey);
                var encryptPwd = encrypt.encrypt(pwd)
                return encryptPwd
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
            that.getuiConfig();
            that.getCodeConfig();
            that.getRememberMeConfig();
            that.getLoginTime();
            that.code();
            that.initial();
            that.getPublicKey();
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
