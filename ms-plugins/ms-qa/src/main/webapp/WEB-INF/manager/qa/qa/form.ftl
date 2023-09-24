<!DOCTYPE html>
<html>

<head>
    <title>调查问卷主表</title>
    <#include "../../include/head-file.ftl">
</head>

<body>
<div id="form" v-loading="loading" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <@shiro.hasPermission name="qa:qa:save">
            <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
            </el-button>
        </@shiro.hasPermission>
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="180px" label-position="right" size="small" style="width: 30%">
            <!--时间范围-->
            <el-form-item  label="时间范围: " prop="datetimerange">
                <el-date-picker
                        v-model="form.datetimerange"
                        type="datetimerange"
                        @input="changeTime"
                        format="yyyy-MM-dd HH:mm:ss"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期">
                </el-date-picker>
            </el-form-item>
            <!--每个IP提交次数限制-->
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="14">
                    <!--答题限制-->
                    <el-form-item  label="每个IP提交次数限制: " prop="type">
                        <el-select  v-model="form.answerLimit.type"
                                    :filterable="false"
                                    :disabled="false"
                                    :multiple="false" :clearable="true"
                                    placeholder="请输入内容">
                            <el-option v-for='item in typeOptions' :key="item.value" :value="item.value"
                                       :label="item.label"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col span="10">
                    <!--答题次数-->
                    <el-form-item label-width="10px" prop="answerNumber">
                        <el-input
                                v-model.number="form.answerNumber"
                                :disabled="false"
                                :readonly="false"
                                :style="{width:  '30%'}"
                                :clearable="true"
                                placeholder="请输入每个IP答题次数限制">
                        </el-input>&nbsp;次
                    </el-form-item>
                </el-col>
            </el-row>
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
                    //开始时间
                    startTime: "",
                    //结束时间
                    endTime: "",
                    // 每个IP答题次数限制
                    answerLimit: {
                        type:'',
                        value: Number('1'),
                    },
                    // 用于临时存储 答题次数
                    answerNumber: -1,
                    datetimerange: []//时间范围
                },
                typeOptions:[{"value":"only","label":"只能"},{"value":"hour","label":"每小时"},{"value":"day","label":"每天"}],
                rules: {
                    // 投票名称
                    qaName: [{"min": 0, "max": 255, "message": "投票名称长度必须为0-255"}],
                    // 投票表名
                    qaTableName: [{"min": 0, "max": 255, "message": "投票表名长度必须为0-255"}],
                    // 投票类型
                    qaType: [{"min": 0, "max": 255, "message": "投票类型长度必须为0-255"}],
                    // 每个IP答题次数限制
                    answerLimit: [{"type": "number", "message": "每个IP答题次数限制格式不正确"}],
                    // 答题次数
                    answerNumber : [{"min": 1, "max": 20, "message": "答题次数必须为0-20","type": "number", "message": "答题次数限制格式不正确"}],
                    // json
                    modelJson: [{"min": 0, "max": 255, "message": "json长度必须为0-255"}],
                    // 自定义字段
                    modelField: [{"min": 0, "max": 255, "message": "自定义字段长度必须为0-255"}],
                    // 时间范围
                    datetimerange: [{"required":true,"message":"时间范围不能为空"}],
                },
            }
        },
        watch: {},
        components: {},
        computed: {},
        methods: {
            //重新渲染时间
            changeTime: function (e) {
                this.$forceUpdate()
            },
            save: function () {
                var that = this;
                // 将答题次数赋值
                that.form.answerLimit.value = that.form.answerNumber
                var url = ms.manager + "/qa/qa/save.do"
                if (that.form.qaName) {
                    url = ms.manager + "/qa/qa/update.do";
                }
                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var form = JSON.parse(JSON.stringify(that.form));
                        if (form.datetimerange) {
                            form.startTime = form.datetimerange[0];
                            form.endTime = form.datetimerange[1];
                        }
                        if(form.answerLimit){
                            form.answerLimit = JSON.stringify(form.answerLimit)
                        }
                        ms.http.post(url, form).then(function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: "成功",
                                    message: "保存成功",
                                    type: 'success'
                                });
                                location.href = ms.manager + "/qa/qa/index.do";
                            } else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }

                            that.saveDisabled = false;
                        }).catch(function (err) {
                            console.err(err);
                            that.saveDisabled = false;
                        });
                    } else {
                        return false;
                    }
                })
            },

            //获取当前调查问卷主表
            get: function (qaName) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/qa/qa/get.do", {"qaName": qaName}).then(function (res) {
                    that.loading = false
                    if (res.result && res.data) {
                        if (res.data.startTime && res.data.endTime) {
                            res.data.datetimerange = [res.data.startTime, res.data.endTime]
                        }
                        if (res.data.answerLimit){
                            res.data.answerLimit = JSON.parse(res.data.answerLimit);
                            res.data.answerNumber = res.data.answerLimit.value
                        } else {
                            res.data.answerLimit = {
                                type:'',
                                value: Number('1'),
                            }
                            res.data.answerNumber = res.data.answerLimit.value
                        }
                        that.form = res.data
                    }
                });
            },
        },
        created: function () {
            var that = this;
            this.form.qaName = ms.util.getParameter("qaName");
            if (this.form.qaName) {
                this.get(this.form.qaName);
            }
        }
    });
</script>
<style>
</style>
