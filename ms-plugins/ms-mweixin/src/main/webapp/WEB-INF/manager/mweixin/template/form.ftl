<!DOCTYPE html>
<html>

<head>
    <title>微信消息模板</title>
    <#include "../../include/head-file.ftl">

</head>

<body>
<div id="form" v-loading="loading" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存</el-button>
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

            <!--微信编号-->
            <!--标题-->
            <el-form-item label="标题" prop="templateTitle">
                <el-input
                        v-model="form.templateTitle"
                        :disabled="true"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入标题">
                </el-input>
            </el-form-item>
            <!--模板编码-->
            <el-form-item label="模板编码" prop="templateCode">
                <el-input
                        v-model="form.templateCode"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入模板编码">
                </el-input>
                <div class="ms-form-tip">
                    模板编码自定义，不可重复
                </div>
            </el-form-item>
            <!--模板ID-->
            <el-form-item label="模板ID" prop="templateId">
                <el-input
                        v-model="form.templateId"
                        :disabled="true"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入模板ID">
                </el-input>
            </el-form-item>
            <!--主属行业-->
            <el-form-item label="主属行业" prop="templatePrimaryIndustry">
                <el-input
                        v-model="form.templatePrimaryIndustry"
                        :disabled="true"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入主属行业">
                </el-input>
            </el-form-item>
            <!--副属行业-->
            <el-form-item label="副属行业" prop="templateDeputyIndustry">
                <el-input
                        v-model="form.templateDeputyIndustry"
                        :disabled="true"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入副属行业">
                </el-input>
            </el-form-item>
            <!--内容-->
            <el-form-item label="内容" prop="templateContent">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="true"
                        v-model="form.templateContent"
                        :style="{width: '100%'}"
                        placeholder="请输入内容">
                </el-input>
            </el-form-item>

            <!--样例-->
            <el-form-item label="样例" prop="templateExample">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="true"
                        v-model="form.templateExample"
                        :style="{width: '100%'}"
                        placeholder="请输入样例">
                </el-input>
            </el-form-item>
            <!--模板关键词-->
            <el-form-item  label="模板关键词" prop="templateKeyword">
                <el-input
                        v-model="form.templateKeyword"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入模板关键词">
                </el-input>
                <div class="ms-form-tip">
                    注意设置参数对应值格式如下${'$'}{keyword1},${'$'}{keyword2},${'$'}{keyword3}。多个参数对应值用英文逗号“,”分隔 </div>
            </el-form-item>
        </el-form>
    </el-main>
</div>
</body>

</html>

<script>
    var formVue = new Vue({
        el: '#form',
        data: function () {
            return {
                loading: false,
                saveDisabled: false,
                //表单数据
                form: {
                    // 微信编号
                    weixinId: '',
                    // 标题
                    templateTitle: '',
                    // 模板编码
                    templateCode: '',
                    // 模板ID
                    templateId: '',
                    // 主属行业
                    templatePrimaryIndustry: '',
                    // 副属行业
                    templateDeputyIndustry: '',
                    // 内容
                    templateContent: '',
                    // 样例
                    templateExample: '',
                    // 模板关键词
                    templateKeyword: '',

                },
                rules: {
                    // 微信编号
                    weixinId: [{"min": 0, "max": 255, "message": "微信编号长度必须为0-255"}],
                    // 标题
                    templateTitle: [{"min": 0, "max": 255, "message": "标题长度必须为0-255"}],
                    // 模板编码
                    templateCode: [{"min": 0, "max": 255, "message": "模板编码长度必须为0-255"}],
                    // 模板ID
                    templateId: [{"min": 0, "max": 255, "message": "模板ID长度必须为0-255"}],
                    // 主属行业
                    templatePrimaryIndustry: [{"min": 0, "max": 255, "message": "主属行业长度必须为0-255"}],
                    // 副属行业
                    templateDeputyIndustry: [{"min": 0, "max": 255, "message": "副属行业长度必须为0-255"}],
                    // 模板关键词
                    templateKeyword: [{"min": 0, "max": 255, "message": "模板关键词长度必须为0-255"}],
                },
            }
        },
        watch: {},
        components: {},
        computed: {},
        methods: {
            //获取当前微信消息模板
            get: function (id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/mweixin/template/get.do", {"id": id}).then(function (res) {
                    that.loading = false
                    if (res.result && res.data) {
                        that.form = res.data;
                    }
                });
            },

            save:function() {
                var that = this;
                var url = ms.manager + "/mweixin/template/update.do";
                this.$refs.form.validate(function(valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var form = JSON.parse(JSON.stringify(that.form));
                        ms.http.post(url, form).then(function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: "成功",
                                    message: "保存成功",
                                    type: 'success'
                                });
                                location.href = ms.manager + "/mweixin/template/index.do";
                            } else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }

                            that.saveDisabled = false;
                        });
                    } else {
                        return false;
                    }
                })
            },
        },
        created: function () {
            var that = this;

            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
            this.rules.title.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/mweixin/template/verify.do", {
                        fieldName: "template_code",
                        fieldValue: value,
                        id: that.form.id,
                        idName:'id',
                    }).then(function(res){
                        if (res.result) {
                            if (!res.data) {
                                callback("微信模板编码已存在！");
                            } else {
                                callback();
                            }
                        }
                    });
                },
                trigger: ['change']
            })
        }
    });

</script>
<style>
</style>
