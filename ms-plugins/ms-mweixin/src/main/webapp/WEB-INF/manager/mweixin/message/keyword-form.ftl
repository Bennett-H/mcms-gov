<!-- 关键词回复 -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>关键字</title>
    <#include "/include/head-file.ftl"/>
    <#include "/mweixin/include/head-file.ftl"/>
</head>
<body style="overflow: hidden">
<#include "/mweixin/component/ms-message-reply.ftl">
<div id="keyword-form" class="ms-weixin-content" v-cloak>
    <el-container>
        <el-header class="ms-header" height="50px">
            <el-row>
                <el-button class="ms-fr" size="mini" @click="back()"><i
                            class="iconfont icon-fanhui"></i>返回
                </el-button>
                <el-button class="ms-fr" type="primary" size="mini" @click="messageSaveOrUpdate()"><i
                            class="iconfont icon-icon-"></i>保存
                </el-button>
            </el-row>
        </el-header>
        <el-main class="ms-container" width="100%">
            <el-form :model="passiveMessageForm" status-icon :rules="passiveMessageFormRules" ref="passiveMessageForm"
                     label-width="100px">
                <el-form-item label="关键词" ref="form" prop="pmKey" class="ms-keyword-input">
                    <el-row type='flex' justify='space-between' align='center'>
                        <el-col :span='12'>
                            <el-input placeholder="请输入关键词" v-model="passiveMessageForm.pmKey" class="input-with-select"
                                      size='mini' maxlength='30' >
                            </el-input>
                        </el-col>
                        <el-col>
                        </el-col>
                    </el-row>
                </el-form-item>
                <el-form-item label="回复内容">

                    <ms-message-reply ref="msMessageReply" @on-editor-change="onEditorChange"></ms-message-reply>
                    <el-input v-model="passiveMessageForm.pmContent" style="display: none"></el-input>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</div>

<script>
    var passiveMessageFormVue = new Vue({
        el: '#keyword-form',
        data: function () {
            return {

                passiveMessageForm: {
                    pmKey: "", //关键词
                    select: '',
                    pmContent: "",
                    pmWeixinId: "",
                    pmType: "",
                    id: "",
                    pmImgId: "",
                },
                passiveMessageFormRules: {
                    pmKey: [
                        {required: true, message: '请输入关键词', trigger: 'blur'}
                    ],
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
        methods: {
            //清除编辑数据
            rest: function () {
                this.passiveMessageForm = {
                    pmKey: "", //关键词
                    select: '',
                    pmContent: "",
                    pmWeixinId: "",
                    pmType: "",
                    id: "",
                    pmImgId: "",
                }
                this.activeName = 'text'
            },
            // 添加关键词
            addKeyWord: function () {

            },
            //返回
            back: function () {
                history.back()
            },
            onEditorChange: function(content) {
                this.passiveMessageForm.pmContent = content.text;
                this.passiveMessageForm.pmNewType = content.type;
            },
            // 保存关键字消息回复
            messageSaveOrUpdate: function () {
                
                var that = this;
                var url = "/mweixin/message/update.do"
                if (!this.passiveMessageForm.id && this.passiveMessageForm.id == ''){
                    this.passiveMessageForm.pmType = 'keyword';
                    this.passiveMessageForm.pmWeixinId = that.weixinId;
                    url =  "/mweixin/message/save.do"
                }
                ms.http.post(ms.manager + url, this.passiveMessageForm)
                    .then(function (res) {
                        
                        if (res.result) {
                            that.$notify({
                                title: '成功',
                                message: "修改成功",
                                type: 'success'
                            });

                            that.back()
                            that.rest();

                        } else {
                            that.$notify.error({
                                title: '失败',
                                message: res.msg,
                                type: 'error'
                            });
                        }
                    })

            },

            //获取数据
            get: function (id) {
                var that = this
                if(id){
                    ms.http.get(ms.manager + '/mweixin/message/get.do', {id:id}).then(function(res){
                        if( res.result ){
                            var content = {};
                            that.passiveMessageForm = res.data;
                            content.type = res.data.pmNewType;
                            content.text = res.data.pmContent;
                            that.$refs.msMessageReply.load(content);
                        }else{
                            that.$notify.error("获取失败，"+res.msg);
                        }
                    }).catch(function(err){
                        that.$notify.error(err);
                    })
                }
            }

        },
        created: function () {
            var that = this
            var id = ms.util.getParameter('id')
            id && that.get(id)
        }
    })
</script>

</body>
</html>