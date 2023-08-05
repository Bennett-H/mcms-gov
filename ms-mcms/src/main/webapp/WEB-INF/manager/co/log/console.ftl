<!DOCTYPE html>
<html>
<head>
    <title>监控日志</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/plugins/sockjs/1.4.0/sockjs.min.js"></script>
    <script src="${base}/static/plugins/stomp/2.3.3/stomp.min.js"></script>
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <el-button-group>
                <el-button type="primary" icon="el-icon-video-play" size="mini" @click="start()" :disabled="isStart">启动</el-button>

                <el-button type="primary" icon="el-icon-video-pause" size="mini" @click="stop()" :disabled="!isStart">关闭</el-button>

                <el-button type="primary" icon="el-icon-monitor" size="mini" @click="clearData()">清屏</el-button>
            </el-button-group>
        </el-col>

    </el-header>
    <el-main class="ms-container">
        <div v-html="logMsg" v-if="isStart"></div>
        <el-empty v-else description="日志监控没有启动"></el-empty>
    </el-main>
</div>

</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data:{
            // 日志信息
            logMsg: '',
            //webSocket客户端
            stompClient: null,
            stopDisabled: true,
            isStart:false,
            loadState:false,
            loading: true,//加载状态
            //搜索表单
            form:{
                sqlWhere:null,
            },
        },
        watch:{
        },
        mounted:function() {
            var that = this;
            this.connection();
        },
        methods:{

            connection:function() {
                var that = this;
                // 更换that指针
                const socket = new SockJS(ms.base + '/logwebsocket');
                that.stompClient = Stomp.over(socket);
                //建立连接，订阅主题
                that.stompClient.connect({}, function(frame) {
                    ms.mdiy.config("监控日志配置","logSocket").then(function(res) {
                        that.stompClient.subscribe(res.data, (val) => {
                            that.logMsg = that.logMsg + '<br/>' + val.body;
                        })
                    })

                })
            },

            start:function(){
                var that = this;
                that.isStart = true;
                that.clearData();
                ms.http.get(ms.manager+"/co/log/checkLog.do").then(function (res) {
                    if (res.result) {
                        var body = {"action":"START","user":that.user};
                        body = JSON.stringify(body);
                        that.stompClient.send("/logclient/control",{"Content-Type":"application/json"},body);
                    }else {
                        that.isStart = false;
                        that.$notify({
                            title: "错误",
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                })
            },

            stop:function(){
                var that = this;
                that.isStart = false;
                var body = {"action":"STOP","user":this.user};
                body = JSON.stringify(body);
                that.stompClient.send("/logclient/control",{"Content-Type":"application/json"},body)
            },

            isRun:function(){
                var that = this;
                ms.http.get(ms.manager+"/co/log/isRun.do").then(function (res) {
                    if (res.result) {
                        that.isStart = true;
                    }
                })
            },

            clearData:function(){
                this.logMsg = '';
            },
        },
        created:function(){
            var that = this;
            that.isRun();

        },
        beforeDestroy:function(){
            var that = this;
            if (that.stompClient){

                that.stop();
                that.stompClient.disconnect();
            }
        },
    })

    window.onbeforeunload = function (e) {
        if (indexVue.stompClient){
            console.log('close socket');
            indexVue.stop();
            indexVue.stompClient.disconnect();
        }
    };
</script>
<style>
    #index .ms-container {
        height: calc(100vh - 78px);
    }
</style>
