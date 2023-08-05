<!-- 增加等保相关 -->
<!DOCTYPE html>
<html>
<head>
    <title>修改密码</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="form" v-loading="loading" v-cloak>
    <el-main class="ms-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small" style=" width: 500px; margin: 0 auto;">
            <el-form-item  label="账号" prop="managerName">
                <el-input
                        v-model="form.managerName"
                        :disabled="true"
                        :readonly="true"
                        :style="{width:  '100%'}"
                        :clearable="false"
                        placeholder="">
                </el-input>
            </el-form-item>

            <el-form-item  label="旧密码" prop="oldManagerPassword">
                <el-input type="password"
                          :show-password="false"
                          :clearable="true" autocomplete="off"
                          v-model="form.oldManagerPassword"  :style="{width:'100%'}"
                          :disabled="false" placeholder="请输入旧密码"></el-input>
            </el-form-item>

            <el-form-item  label="新密码" prop="newManagerPassword">
                <el-input type="password"
                          :show-password="true"
                          :clearable="true" autocomplete="off"
                          v-model="form.newManagerPassword"  :style="{width:'100%'}"
                          :disabled="false" placeholder="请输入新密码"></el-input>
            </el-form-item>

            <el-form-item  label="确认新密码" prop="newComfirmManagerPassword">
                <el-input type="password"
                          :show-password="true"
                          :clearable="true" autocomplete="off"
                          v-model="form.newComfirmManagerPassword"  :style="{width:'100%'}"
                          :disabled="false" placeholder="请确认新密码"></el-input>
            </el-form-item>
            <el-form-item  label="" prop="">
                <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="updatePassword" :loading="saveDisabled">修改密码</el-button>
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
                    // 账号
                    managerName:'${Session["manager_session"].managerName}',
                    // 旧密码
                    oldManagerPassword:'',
                    // 新密码
                    newManagerPassword:'',
                    // 确认新密码
                    newComfirmManagerPassword:'',
                },
                rules:{
                    oldManagerPassword: [{
                        required: true,
                        message: '请输入旧密码',
                        trigger: 'blur'
                    }, {
                        min: 6,
                        max: 30,
                        message: '长度在 6 到 30 个字符',
                        trigger: 'blur'
                    }],
                    newManagerPassword: [{
                        required: true,
                        message: '请输入新密码',
                        trigger: 'blur'
                    }, {
                        min: 6,
                        max: 30,
                        message: '长度在 6 到 30 个字符',
                        trigger: 'blur'
                    }],
                    newComfirmManagerPassword: [{
                        required: true,
                        message: '请再次输入确认密码',
                        trigger: 'blur'
                    }, {
                        min: 6,
                        max: 30,
                        message: '长度在 6 到 30 个字符',
                        trigger: 'blur'
                    },{
                        validator: function (rule, value, callback) {
                            if (form.form.newManagerPassword === value) {
                                callback();
                            } else {
                                callback('新密码和确认新密码不一致');
                            }
                        }
                    }]
                },

            }
        },
        watch:{
        },
        computed:{
        },
        methods: {
            // 更新密码
            updatePassword: function () {
                var that = this;
                this.$refs['form'].validate(function (valid) {
                    if (valid) {
                        ms.http.post(ms.manager + "/updatePassword.do", that.form).then(function (data) {
                            if (data.result == true) {
                                that.form.oldManagerPassword = '';
                                that.form.newManagerPassword = '';
                                that.isShow = false;
                                that.$notify({
                                    title: '提示',
                                    message: "修改成功,重新登录系统！",
                                    type: 'success'
                                });
                                location.reload();
                            } else {
                                that.$notify({
                                    title: '错误',
                                    message: data.msg,
                                    type: 'error'
                                });
                            }
                        }, function (err) {
                            that.$notify({
                                title: '错误',
                                message: err,
                                type: 'error'
                            });
                        });
                    }
                });
            },
            getManager:function (){
                var that = this
                ms.http.get(ms.manager + "/basic/manager/info.do").then(function(res){
                    if (res.result) {
                        that.form.managerName = res.data.managerName;
                    }
                })
            }

        },
        created:function() {
            var that = this;
            this.getManager();
            // 根据安全设置中的密码复杂度，调整密码的验证规则
            var obj = {pattern:'',message:'密码格式错误，具体查看安全设置中的密码验证规则'};
            ms.mdiy.config("安全设置","password").then(function (res) {
                if(res.result){
                    obj.pattern = res.data
                }
            })
            ms.mdiy.config("安全设置","passwordMsg").then(function (res) {
                if(res.result){
                    obj.message = res.data
                    that.rules.newManagerPassword.push(obj)
                }
            })
        }
    });
</script>
