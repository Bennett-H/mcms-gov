<!DOCTYPE html>
<html>
<head>
	 <title>任务调度日志</title>
        <#include "../../include/head-file.ftl">
</head>
<body>
	<div id="form" v-cloak>
		<el-header class="ms-header ms-tr" height="50px">
<#--			<el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存</el-button>-->
			<el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
		</el-header>
		<el-main class="ms-container">
            <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="mini">
            <el-form-item  label="任务名称" prop="qjlName">
                    <el-input v-model="form.qjlName"
                         :readonly="true"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入内容">
                </el-input>
            </el-form-item>
            <el-form-item  label="任务组" prop="qjlGroup">
                    <el-input v-model="form.qjlGroup"
                         :readonly="true"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入内容">
                </el-input>
            </el-form-item>
            <el-form-item  label="调用目标" prop="qjlTarget">
                    <el-input v-model="form.qjlTarget"
                         :readonly="true"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入内容">
                </el-input>
            </el-form-item>
            <el-form-item  label="执行状态" prop="qjlStatus">
                <el-tag v-if="form.qjlStatus" type="success">正常</el-tag>
                <el-tag v-else type="danger">错误</el-tag>
            </el-form-item>
            <el-form-item  label="日志信息" prop="qjlMsg">
                    <el-input
                        type="textarea" :rows="5"
                        :readonly="true"
                        v-model="form.qjlMsg"
                        :style="{width: '100%'}"
                        placeholder="请输入日志信息">
                </el-input>
            </el-form-item>
            <el-form-item  label="错误信息" prop="qjlErrorMsg">
                    <el-input
                        type="textarea" :rows="5"
                        :readonly="true"
                        v-model="form.qjlErrorMsg"
                        :style="{width: '100%'}"
                        placeholder="请输入错误信息">
                </el-input>
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
        data: function() {
            return {
                saveDisabled: false,
                //表单数据
                form: {
                    // 任务名称
                    qjlName:'',
                    // 任务组
                    qjlGroup:'',
                    // 调用目标
                    qjlTarget:'',
                    // 执行状态
                    qjlStatus:false,
                    // 日志信息
                    qjlMsg:'',
                    // 错误信息
                    qjlErrorMsg:'',
                },
                rules:{
                },

            }
        },
        watch:{
        },
        computed:{
        },
        methods: {
            // save: function() {
            //     var that = this;
            //     var url = ms.manager + "/quartz/jobLog/save.do"
            //     if (that.form.id > 0) {
            //         url = ms.manager + "/quartz/jobLog/update.do";
            //     }
            //     this.$refs.form.validate(function(valid) {
            //         if (valid) {
            //             that.saveDisabled = true;
            //             var data = JSON.parse(JSON.stringify(that.form));
            //             ms.http.post(url, data).then(function (data) {
            //                 if (data.result) {
            //                     that.$notify({
            //                         title: '成功',
            //                         message: '保存成功',
            //                         type: 'success'
            //                     });
            //                     location.href = ms.manager + "/quartz/jobLog/index.do";
            //                 } else {
            //                     that.$notify({
            //                         title: '失败',
            //                         message: data.msg,
            //                         type: 'warning'
            //                     });
            //                 }
            //                 that.saveDisabled = false;
            //             });
            //         } else {
            //             return false;
            //         }
            //     })
            // },

            //获取当前任务调度日志
            get: function(id) {
                var that = this;
                ms.http.get(ms.manager + "/quartz/jobLog/get.do", {"id":id}).then(function (res) {
                    if(res.result&&res.data){
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created: function() {
            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
        }
    });
</script>
