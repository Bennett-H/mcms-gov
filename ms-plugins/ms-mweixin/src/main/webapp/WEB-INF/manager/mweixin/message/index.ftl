<!-- 被动回复 && 关注回复 -->
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>被动回复 关注回复</title>
    <#include "/include/head-file.ftl" />
    <#include "/mweixin/include/head-file.ftl" />
</head>

<body style="overflow: hidden">
<#include "/mweixin/component/ms-message-reply.ftl">
<div id="ms-message" class="ms-weixin-content" v-cloak>
    <el-container class="ms-admin-message">
        <el-header class="ms-header" height="50px">
            <el-row>
                <el-button type="text"></el-button>
                <@shiro.hasPermission name="passiveMessage:save">
                    <el-button type="primary" class="ms-fr" size="mini" @click="saveOrUpdate" v-if="menuActive == '被动回复'"><i class="iconfont icon-icon-"></i>保存被动回复消息
                    </el-button>
                </@shiro.hasPermission>
                <@shiro.hasPermission name="followMessage:save">
                    <el-button type="primary" class="ms-fr" size="mini" @click="saveOrUpdate" v-if="menuActive == '关注时回复'"><i class="iconfont icon-icon-"></i>保存关注回复消息
                    </el-button>
                </@shiro.hasPermission>
            </el-row>
        </el-header>
        <el-main class="ms-container" width="100%">
            <el-form label-width="100px" size="mini" :rules="rules" :model="messageForm">
                <el-form-item  label="回复消息"  prop='pmContent'>
                    <ms-message-reply ref="msMessageReply" @on-editor-change="onEditorChange"></ms-message-reply>
                    <el-input v-model="messageForm.pmContent" style="display: none"></el-input>
                </el-form-item>
            </el-form>
        </el-main>
</div>
</body>
</html>

<script>
    var messageVue = new Vue({
        el: "#ms-message",
        data: function() {
            return {

                messageForm: {
                    pmContent: '', //消息回复内容
                    pmType: '', //回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发
                    id: '', //消息回复编号
                    pmWeixinId: '', //微信编号
                    pmNewType: "text"
                },
                rules: {
                    pmContent: [{
                        required: true,
                        message: '内容不能为空',
                        trigger: 'change'
                    }]
                },
            }
        },
        computed: {
            weixinId: function() {
                return localStorage.getItem('weixinId')
            },
            menuActive: function() {
                return localStorage.getItem('menuActive')
            }
        },
        created: function() {
            this.messageList();
        },
        methods: {
            // 设置消息回复类型
            messageType: function() {
                if (this.menuActive == '被动回复') {
                    this.messageForm.pmType = 'passivity';
                } else if (this.menuActive == '关注时回复') {
                    this.messageForm.pmType = 'attention';
                }
            },

            // 获取消息回复
            messageList: function() {
                var that = this;
                //初始化
                that.messageForm = {
                    pmContent: '',
                    pmType: '',
                    id: '',
                    pmWeixinId: '',
                    pmNewType: ''
                };
                that.messageType();
                if (that.messageForm.pmType) {
                    that.messageForm.pmWeixinId = that.weixinId;
                    ms.http.get(ms.manager + "/mweixin/message/list.do", {
                        pmWeixinId: that.messageForm.pmWeixinId,
                        pmType: that.messageForm.pmType,
                    }).then(function(res) {
                        if(res.result) {
                            if (res.data.rows.length > 0) {
                                var content = {};
                                that.messageForm = res.data.rows[0];
                                content.type = res.data.rows[0].pmNewType;
                                content.text = res.data.rows[0].pmContent;
                                that.$refs.msMessageReply.load(content);

                            } else {
                                that.messageForm.id = '';
                            }
                        }
                    })
                }
            },

            onEditorChange: function(content) {
                this.messageForm.pmContent = content.text;
                this.messageForm.pmNewType = content.type;
            },

            saveOrUpdate: function() {

                var that = this;
                if (this.messageForm.id != '') {
                    ms.http.post(ms.manager + "/mweixin/message/update.do", this.messageForm)
                        .then(function(res) {
                            if (res.result) {
                                that.$notify({
                                    title: '成功',
                                    message: "修改成功",
                                    type: 'success'
                                });
                                that.messageList();
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        })
                } else {
                    that.messageType();
                    that.messageForm.pmWeixinId = that.weixinId;
                    ms.http.post(ms.manager + "/mweixin/message/save.do", this.messageForm)
                        .then(function(res) {
                            if (res.result) {
                                that.$notify({
                                    title: '成功',
                                    message: "保存成功",
                                    type: 'success'
                                });
                                that.messageList();
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        })
                }
            }
        },
    })
</script>
