<!DOCTYPE html>
<html>
<head>
    <title>异常日志</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="form" v-loading="loading" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">
            <el-row
                    :gutter="0"
                    justify="start" align="top">
                <el-col :span="12">

                    <el-form-item label="异常文件" prop="logTitle">
                        <el-input
                                v-model="form.logTitle"
                                :disabled="true"
                                :style="{width:  '100%'}"
                                placeholder="">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">

                    <el-form-item label="IP" prop="logIp">
                        <el-input
                                v-model="form.logIp"
                                :disabled="true"
                                :style="{width:  '100%'}"
                                placeholder="">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    :gutter="0"
                    justify="start" align="top">
                <el-col :span="12">

                    <el-form-item label="异常抛出方法" prop="logMethod">
                        <el-input
                                v-model="form.logMethod"
                                :disabled="true"
                                :style="{width:  '100%'}"
                                placeholder="">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="请求地址" prop="logUrl">
                        <el-input
                                v-model="form.logUrl"
                                :disabled="true"
                                :style="{width:  '100%'}"
                                placeholder="">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    :gutter="0"
                    justify="start" align="top">
                <el-col :span="12">
                    <el-form-item label="异常创建时间" prop="createDate">
                        <el-input
                                v-model="form.createDate"
                                :disabled="true"
                                :style="{width:  '100%'}">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item  label="IP所在地区" prop="logLocation">
                        <el-input
                                v-model="form.logLocation"
                                :disabled="true"
                                :style="{width:  '100%'}"
                                placeholder="">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    :gutter="0"
                    justify="start" align="top">
                <el-col :span="24">

                    <el-form-item label="错误原因" prop="logResult">
                        <el-input
                                type="textarea" :rows="2"
                                :disabled="true"
                                v-model="form.logResult"
                                :style="{width: '100%'}"
                                placeholder="">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-form-item label="异常堆栈" prop="异常堆栈">
                <el-input
                        type="textarea" :rows="20"
                        :disabled="true"
                        v-model="form.logErrorMsg"
                        :style="{width: '100%'}"
                        placeholder="">
                </el-input>
            </el-form-item>
        </el-form>
    </el-main>
</div>
</body>
</html>
<script>
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                loading: false,
                saveDisabled: false,
                //表单数据
                form: {
                    // 异常文件
                    logTitle: '',
                    // IP
                    logIp: '',
                    // 异常抛出方法
                    logMethod: '',
                    // 请求地址
                    logUrl: '',
                    // 请求状态
                    logStatus: '',
                    // 错误原因
                    logResult: '',
                    // 异常堆栈
                    logErrorMsg: '',
                    // IP所在地区
                    logLocation:'',
                },
                rules: {},
            }
        },
        watch: {},
        computed: {},
        methods: {
            //获取当前系统日志
            get: function (id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/basic/log/get.do", {"id": id}).then(function (res) {
                    that.loading = false
                    if (res.result && res.data) {
                        that.form = res.data;
                    }
                });
            },
        },
        created: function () {
            var that = this;
            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
        }
    });
</script>
