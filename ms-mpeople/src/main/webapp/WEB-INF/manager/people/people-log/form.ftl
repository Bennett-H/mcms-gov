<!DOCTYPE html>
<html>

<head>
    <title>会员日志</title>
    <#include "../../include/head-file.ftl">

</head>

<body>
<div id="form" v-loading="loading" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container">
        <el-form ref="form" :model="form" label-width="120px" label-position="right" size="small">

            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <!--标题-->
                    <el-form-item label="标题" prop="logTitle">
                        <el-input
                                v-model="form.logTitle"
                                :disabled="true"
                                :style="{width:  '100%'}"
                                :clearable="true"
                                placeholder="请输入标题">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col span="12">
                    <!--会员编号-->
                    <el-form-item label="会员编号" prop="peopleId">
                        <el-input
                                v-model="form.peopleId"
                                :disabled="true"
                                :readonly="true"
                                :style="{width:  '100%'}"
                                :clearable="true"
                                placeholder="请输入会员编号">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <!--IP-->
                    <el-form-item label="IP" prop="logIp">
                        <el-input
                                v-model="form.logIp"
                                :disabled="true"
                                :style="{width:  '100%'}"
                                :clearable="false"
                                placeholder="请输入IP">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col span="12">
                    <!--所在地区-->
                    <el-form-item label="所在地区" prop="logAddr">
                        <el-input
                                v-model="form.logAddr"
                                :disabled="true"
                                :style="{width:  '100%'}"
                                :clearable="false"
                                placeholder="请输入所在地区">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <!--日志类型-->
                    <el-form-item label="日志类型" prop="logType">
                        <el-select v-model="form.logType"
                                   :style="{width: '100%'}"
                                   :filterable="true"
                                   :disabled="true"
                                   :multiple="false" :clearable="false"
                                   placeholder="请选择日志类型">
                            <el-option v-for='item in logTypeOptions' :key="item" :value="item"
                                       :label="item"></el-option>
                        </el-select>
                    </el-form-item>

                </el-col>
                <el-col span="12">
                    <!--日志状态-->
                    <el-form-item label="日志状态" prop="logStatus">
                        <el-select v-model="form.logStatus"
                                   :style="{width: '100%'}"
                                   :filterable="false"
                                   :disabled="true"
                                   :multiple="false" :clearable="true"
                                   placeholder="请选择日志状态">
                            <el-option v-for='item in logStatusOptions' :key="item.value" :value="item.value"
                                       :label="item.label"></el-option>
                        </el-select>
                    </el-form-item>

                </el-col>
            </el-row>
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <el-form-item label="创建时间" prop="createDate">
                        <el-input
                                v-model="form.createDate"
                                :disabled="true"
                                :style="{width:  '100%'}"
                                :clearable="false"
                                placeholder="请输入创建时间">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col span="12">
                </el-col>
            </el-row>
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <!--请求参数-->
                    <el-form-item label="请求参数" prop="logParam">
                        <el-input
                                type="textarea" :rows="5"
                                :disabled="true"
                                v-model="form.logParam"
                                :style="{width: '100%'}"
                                placeholder="请输入请求参数">
                        </el-input>
                    </el-form-item>

                </el-col>
                <el-col span="12">
                    <!--返回参数-->
                    <el-form-item label="返回参数" prop="logResult">
                        <el-input
                                type="textarea" :rows="5"
                                :disabled="true"
                                v-model="form.logResult"
                                :style="{width: '100%'}"
                                placeholder="请输入返回参数">
                        </el-input>
                    </el-form-item>

                </el-col>
            </el-row>
            <!--日志信息-->
            <el-form-item label="日志信息" prop="logInfo">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="true"
                        :readonly="true"
                        v-model="form.logInfo"
                        :style="{width: '100%'}"
                        placeholder="请输入日志信息">
                </el-input>
                <div class="ms-form-tip">
                    日志信息，存储登录的浏览器信息或者换绑邮箱的信息等
                </div>
            </el-form-item>

            <!--错误消息-->
            <el-form-item label="错误消息" prop="logErrorMsg">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="true"
                        v-model="form.logErrorMsg"
                        :style="{width: '100%'}"
                        placeholder="请输入错误消息">
                </el-input>
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
                // 日志类型
                logTypeOptions: [],
                logStatusOptions:[{"value":"success","label":"成功"},{"value":"error","label":"失败"}],
                //表单数据
                form: {
                    // 标题
                    logTitle: '',
                    // 会员编号
                    peopleId:'',
                    // IP
                    logIp:'',
                    // 所在地区
                    logAddr:'',
                    // 日志类型
                    logType:"",
                    // 日志状态
                    logStatus:"",
                    // 请求参数
                    logParam:'',
                    // 返回参数
                    logResult:'',
                    // 日志信息
                    logInfo:'',
                    // 错误消息
                    logErrorMsg:'',
                },
            }
        },
        watch: {},
        components: {},
        computed: {},
        methods: {
            //获取当前会员日志
            get: function (id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/people/peopleLog/get.do", {"id": id}).then(function (res) {
                    that.loading = false
                    if (res.result && res.data) {
                        that.form = res.data;
                    }
                });
            },

            //获取logType数据源
            logTypeOptionsGet: function () {
                var that = this;
                ms.http.post(ms.manager + '/people/peopleLog/queryLogType.do').then(function (res) {
                    that.logTypeOptions = res.data;
                }).catch(function (err) {
                    console.log(err);
                });
            },


        },
        created: function () {
            var that = this;
//加载日志类型 数据
            this.logTypeOptionsGet();

            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
        }
    });

</script>
<style>
</style>
