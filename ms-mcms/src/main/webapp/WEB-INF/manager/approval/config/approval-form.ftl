<!DOCTYPE html>
<html>
<head>
    <title>审批</title>
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

            <el-form-item  label="审核状态" prop="plStatus">
                <el-radio-group v-model="form.plStatus"
                                :style="{width: ''}"
                                :disabled="false">
                    <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.value"
                              v-for='(item, index) in plStatusOptions' :key="item.value + index">
                        {{item.label}}
                    </el-radio>
                </el-radio-group>
            </el-form-item>

            <el-form-item  label="审核意见" prop="plContent">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        :readonly="false"
                        v-model="form.plContent"
                        :style="{width: '100%'}"
                        placeholder="请输入审核意见">
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
        data:function() {
            return {
                loading:false,
                saveDisabled: false,
                //栏目id
                categoryId:"",
                // 方案名称
                schemeName:"",
                //表单数据
                form: {
                    // 审核状态
                    plStatus:'adopt',
                    // 审核意见
                    plContent:'',
                },
                plStatusOptions:[{"value":"adopt","label":"通过"},{"value":"reject","label":"不通过"}],
                rules:{
                    // 审核状态
                    plStatus: [{"required":true,"message":"审核状态不能为空"}],
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
                var url = ms.manager + "/approval/config/approval.do"
                this.$refs.form.validate(function(valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        data.categoryId = that.categoryId;
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.saveDisabled = false;
                                if (data.result) {
                                    that.$notify({
                                        title: "成功",
                                        message: "审核成功",
                                        type: 'success'
                                    });
                                    location.href = ms.manager + "/cms/content/review.do";
                                } else {
                                    that.$notify({
                                        title: "错误",
                                        message: data.msg,
                                        type: 'warning'
                                    });
                                }
                            } else {
                                that.$notify({
                                    title: "错误",
                                    message: data.msg,
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

            //获取当前进度日志
            get:function() {

                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/approval/config/getProgressLog.do",{
                    "schemeName": that.schemeName,
                    "dataId": that.form.dataId
                }).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data){
                        //  循环给null值修改赋值为空字符串
                        for (var key in res.data) {
                          that.form[key] = res.data[key] || ''
                        }
                        if(!that.form.plStatus){
                            that.form.plStatus = "adopt";
                        }
                    }else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                        setTimeout(function () {
                            history.back();
                        }, 500);
                    }
                })
            },
        },
        created:function() {
            var that = this;
            this.form.dataId = ms.util.getParameter("dataId");
            this.categoryId = ms.util.getParameter("categoryId");
            this.schemeName = decodeURI(ms.util.getParameter("schemeName"));
            if (this.form.dataId) {
                this.get();
            }

        }
    });
</script>
