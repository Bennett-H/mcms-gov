<!DOCTYPE html>
<html>
<head>
    <title>任务实体表</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="form" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
        </el-button>
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container">
        <el-scrollbar class="ms-scrollbar" style="height: 100%;">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px" size="mini">
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <el-form-item label="任务名称" prop="qjName">
                        <el-input v-model="form.qjName"
                                  :disabled="false"
                                  :style="{width:  '100%'}"
                                  :clearable="true"
                                  maxlength="30"
                                  placeholder="请输入任务名称">
                        </el-input>
                        <div class="ms-form-tip">
                            任务名称不能重复
                        </div>
                    </el-form-item>
                </el-col>
                <el-col span="12">
                    <el-form-item label="" prop="qjGroup">
                        <template slot='label'>任务组
                        </template>
                        <el-input v-model="form.qjGroup"
                                  :disabled="false"
                                  :clearable="true"
                                  maxlength="30"
                                  placeholder="请输入任务组">
                        </el-input>
                        <div class="ms-form-tip">
                            任务的分组,建议同一个调用目标的任务防止相同的线程中执行
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <el-form-item label="" prop="qjStatus">
                        <template slot='label'>状态
                        </template>
                        <el-switch v-model="form.qjStatus"
                                   :disabled="false">
                        </el-switch>
                        <div class="ms-form-tip">
                            开启状态，如果是开启状态系统重启后任务也会被执行
                        </div>
                    </el-form-item>
                </el-col>
                <el-col span="12">
                    <el-form-item label="" prop="qjAsync">
                        <template slot='label'>开启并发
                        </template>
                        <el-switch v-model="form.qjAsync"
                                   :disabled="false">
                        </el-switch>
                        <div class="ms-form-tip">
                            开启：多线程执行，注意调用目标的实现符合多线程设计，
                            关闭：任务会排队等待执行
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="" prop="qjTarget">
                <template slot='label'>调用目标
                </template>
                <el-input v-model="form.qjTarget"
                          :disabled="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          maxlength="150"
                          placeholder="请输入调用目标">
                </el-input>
                <div class="ms-form-tip">
                    可以通过类的完整路径方式net.mingsoft.Test.helloWord()或spring定义的bean名称 test.helloWord()调用类的方法<br/>
                    方法参数说明：<br/>
                    字符串类型必须要用""双引号号,如：("ms")<br/>
                    long类型必须L结尾，如:("ms",1L)<br/>
                    double浮点类型必须D结尾，如:("ms",1L1,1D)<br/>
                    boolean布尔类型,如:("ms",1L1,1D,true)<br/>
                </div>
            </el-form-item>
            <el-form-item label="" prop="qjCron">
                <template slot='label'>执行表达式
                </template>
                <el-input v-model="form.qjCron"
                          :disabled="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          maxlength="20"
                          placeholder="请输入执行表达式">
                </el-input>
                <div class="ms-form-tip">
                    cron表达式,如：<br/>
                    0/30 * * * * ? *   每30秒执行一次<br/>
                    0 0/2 * * * ?    表示每2分钟 执行任务<br/>
                    0 0 0/2 * * ?    表示每2小时 执行任务<br/>
                    0 0 12 * * ?   每天中午12点触发<br/>
                    0 15 10 * * ?     每天上午10:15触发<br/>
                    0 0 10,14,16 * * ?   每天上午10点，下午2点，4点<br/>
                    0 0 2 1 * ?   表示在每月的1日的凌晨2点调整任务<br/>
                    0 15 10 ? * MON-FRI    周一至周五的上午10:15触发<br/>
                    表达式生成器 <el-link type="primary" target="_blank" href="https://www.bejson.com/othertools/cron/">https://www.bejson.com/othertools/cron/</el-link>
                </div>
            </el-form-item>
        </el-form>
        </el-scrollbar>
    </el-main>
</div>
</body>
</html>
<script>
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                saveDisabled: false,
                websiteId: null,
                //表单数据
                form: {
                    // 任务名称
                    qjName: '',
                    // 任务组
                    qjGroup: '',
                    // 状态
                    qjStatus: false,
                    // 开启并发
                    qjAsync: false,
                    // 调用目标
                    qjTarget: '',
                    // 执行表达式
                    qjCron: '',
                },
                rules: {
                    // 任务名称
                    qjName: [{"required": true, "message": "请输入任务名称"}],
                    qjGroup: [{"required": true, "message": "请输入任务组"}],
                    qjTarget: [{"required": true, "message": "请输入调用目标"}],
                    qjCron: [{"required": true, "message": "请输入执行表达式"}],
                },

            }
        },
        watch: {},
        computed: {},
        methods: {
            save: function () {
                var that = this;
                that.saveDisabled = true;
                var url = ms.manager + "/quartz/job/save.do";
                if (that.form.id > 0) {
                    url = ms.manager + "/quartz/job/update.do";
                }
                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        var data = JSON.parse(JSON.stringify(that.form));
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success'
                                });
                                location.href = ms.manager + "/quartz/job/index.do";
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: data.msg,
                                    type: 'warning'
                                });
                            }
                            that.saveDisabled = false;
                        }).catch(function(err){
                          that.saveDisabled = false;
                        });
                    } else {
                        that.saveDisabled = false;
                        return false;
                    }
                })
            },

            //获取当前任务实体表
            get: function (id) {
                var that = this;
                ms.http.get(ms.manager + "/quartz/job/get.do", {"id": id}).then(function (res) {
                    if (res.result && res.data) {
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取当前站点
            getWebsiteId: function() {
                var that = this;
                ms.http.get(ms.manager + "/quartz/job/getWebsiteId.do").then(function (res) {
                    if (res.result && res.data) {
                        that.websiteId = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                    console.log("获取站点失败");
                });
            }
        },
        created: function () {
            var that = this;
            that.getWebsiteId();
            that.form.id = ms.util.getParameter("id");
            if (that.form.id) {
                that.get(that.form.id);
            }
            this.rules.qjName.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/quartz/job/verify.do", {
                        fieldName: "qj_name",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function(res){
                        if (res.result) {
                            if (!res.data) {
                                callback("任务名称已存在");
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
