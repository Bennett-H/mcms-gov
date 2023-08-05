<!DOCTYPE html>
<html>

<head>
    <title>管理员扩展信息</title>
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

            <!--管理员id-->
	        <el-form-item  label="管理员id" prop="managerId">
	            <el-input
                        v-model="form.managerId"
                         :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                        placeholder="请输入管理员id">
                </el-input>
	        </el-form-item>   
            <!--手机号-->
	        <el-form-item  label="手机号" prop="managerPhone">
	            <el-input
                        v-model="form.managerPhone"
                         :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                        placeholder="请输入手机号">
                </el-input>
	        </el-form-item>   
            <!--验证码-->
	        <el-form-item  label="验证码" prop="managerCode">
	            <el-input
                        v-model="form.managerCode"
                         :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                        placeholder="请输入验证码">
                </el-input>
	        </el-form-item>   
        <!--验证码发送时间-->
    
        <el-form-item  label="验证码发送时间" prop="sendTime">
             <el-time-picker
                  
                    v-model="form.sendTime"
                   
                    :is-range="false"
placeholder="请选择验证码发送时间"start-placeholder=""end-placeholder=""                    :readonly="false"
                    :disabled="false"
										:editable="true" :clearable="true"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    :arrow-control="true" :style="{width:'100%'}">
            </el-time-picker>
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
                //表单数据
                form: {
                    // 管理员id
                    managerId:'',
                    // 手机号
                    managerPhone:'',
                    // 验证码
                    managerCode:'',
                    //验证码发送时间
                    sendTime:'',

                },
                rules:{
                        // 管理员id
                        managerId: [{"min":0,"max":255,"message":"管理员id长度必须为0-255"}],
                        // 手机号
                        managerPhone: [{"pattern":/^([0-9]{3,4}-)?[0-9]{7,8}$|^\d{3,4}-\d{3,4}-\d{3,4}$|^1[0-9]{10}$/,"message":"手机号格式不匹配"},{"min":0,"max":255,"message":"手机号长度必须为0-255"}],
                        // 验证码
                        managerCode: [{"min":0,"max":255,"message":"验证码长度必须为0-255"}],

                },

            }
        },
        watch:{

        },
        components:{
        },
        computed:{
        },
        methods: {

            save:function() {
                var that = this;
                var url = ms.manager + "/gov/managerInfo/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/gov/managerInfo/update.do";
                }
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
                                location.href = ms.manager + "/gov/managerInfo/index.do";
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

            //获取当前管理员扩展信息
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/gov/managerInfo/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data) {
                                                                                

                        that.form = res.data;
                    }
                });
            },
        },
        created:function() {
            var that = this;

            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
        }
    });

</script>
<style>
</style>
