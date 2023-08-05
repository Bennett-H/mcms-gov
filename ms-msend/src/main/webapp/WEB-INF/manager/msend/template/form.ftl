<!DOCTYPE html>
<html>
<head>
	 <title>消息模板</title>
        <#include "../../include/head-file.ftl">
</head>
<body>
	<div id="form" v-cloak>
		<el-header class="ms-header ms-tr" height="50px">
			<el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存</el-button>
			<el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
		</el-header>
		<el-main class="ms-container">
            <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-form ref="form" :model="form" :rules="rules" label-width="150px" size="mini">
                <el-row>
                    <el-col :span="12">
                        <el-form-item  label="标题" prop="templateTitle">
                            <el-input v-model="form.templateTitle"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入标题">
                            </el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item prop="templateCode" label="模板编码">
                            <el-input v-model="form.templateCode"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入模板编码">

                            </el-input>
                            <div class="ms-form-tip">用于接口请求</div>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item  prop="templateSms">
                            <template slot='label'>短信内容模版
                            </template>
                            <el-input
                                    type="textarea" :rows="5"
                                    :disabled="false"
                                    :readonly="false"
                                    v-model="form.templateSms"
                                    :style="{width: '100%'}"
                                    placeholder="请输入短信消息内容">
                            </el-input>

                            <div class="ms-form-tip" >
                                <#noparse>
                                只支持基本的文本格式，可以使用变量 ${*}，例如：姓名:${name},name直接被
                                    <a target="_blank" href="http://doc.mingsoft.net/plugs/fa-song-cha-jian/ye-wu-kai-fa.html#http%E5%8F%91%E9%80%81%E6%8E%A5%E5%8F%A3">接口</a>
                                    的 <b>content</b> 参数替换<br>
                                如果使用了第三方平台，具体根据第三方平台模版要求填写。
                                例如:sendcould发送消息，必须是sendcould消息模板的id;
                                </#noparse>
                            </div>

                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col>
                        <el-form-item  prop="templateMail" >
                            <template slot='label'>邮件消息内容

                            </template>
                            <vue-ueditor-wrap style="line-height: 0px" v-model="form.templateMail" :config="editorConfig"></vue-ueditor-wrap>
                            <div class="ms-form-tip">
                                <#noparse>
                                    持HTML格式，可以使用变量 ${*}，例如：姓名:${name},name直接被
                                    <a target="_blank" href="http://doc.mingsoft.net/plugs/fa-song-cha-jian/ye-wu-kai-fa.html#http%E5%8F%91%E9%80%81%E6%8E%A5%E5%8F%A3">接口</a>
                                    的 <b>content</b> 参数替换<br>
                                    如果使用了第三方平台，具体根据第三方平台模版要求填写。
                                    例如:sendcould发送消息，必须是sendcould消息模板的id;
                                </#noparse>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            </el-scrollbar>
		</el-main>
	</div>
	</body>
	</html>
<script>
    Vue.component('vue-ueditor-wrap', VueUeditorWrap);
        var form = new Vue({
        el: '#form',
        data:function() {
            return {
                editorConfig:ms.editorConfig,
                saveDisabled: false,
                //表单数据
                form: {
                    id:'',
                    // 标题
                    templateTitle:'',
                    // 邮件模板代码
                    templateCode:'',
                    // 邮件内容
                    templateMail:'',
                    // 消息内容
                    templateSms:'',
                },
                rules:{
                // 标题
                templateTitle: [{"required":true,"message":"标题必须填写"},{"min":1,"max":50,"message":"标题长度必须为1-50"}],
                // 邮件模板代码
                templateCode: [{"required":true,"message":"邮件模板代码必须填写"},{"min":1,"max":50,"message":"邮件模板代码长度必须为1-50"}],
                // 邮件内容
                templateMail: [{"min":1,"max":4500,"message":"邮件内容长度必须为1-4500"}],
                // 消息内容
                templateSms: [{"min":1,"max":4500,"message":"消息内容长度必须为1-4500"}],
                },

            }
        },
        watch:{
        },
        computed:{
        },
        methods: {
            save:function() {
                var that = this;
                var url = ms.manager + "/msend/template/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/msend/template/update.do";
                }
                this.$refs.form.validate(function(valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        ms.http.post(url, data).then(function (data) {
                            if (data.data.templateTitle!="") {
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success'
                                });
                                that.saveDisabled = false;
                                location.href = ms.manager + "/msend/template/index.do";
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: data.msg,
                                    type: 'warning'
                                });
                                that.saveDisabled = false;
                            }
                        });
                    } else {
                        return false;
                    }
                })
            },

            //获取当前消息模板
            get:function(id) {
                var that = this;
                ms.http.get(ms.manager + "/msend/template/get.do", {"id":id}).then(function (data) {
                    if(data.data.id){
                        that.form = data.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created:function() {
            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }

            var that = this;
            this.rules.templateCode.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/msend/template/verify.do", {
                        fieldName: "template_code",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function(res){
                        if (res.result) {
                            if (!res.data) {
                                callback("邮件模板编码已经存在");
                            } else {
                                callback();
                            }
                        }
                    });
                },
                trigger: ['blur']
            })

        }
    });
</script>
