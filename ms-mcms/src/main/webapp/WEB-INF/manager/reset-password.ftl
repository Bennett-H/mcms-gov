<!-- 修改密码 增加密码复杂度的验证 -->
<script src="${base}/static/mdiy/index.js"></script>
<div id="reset-password" class="reset-password">
        <el-dialog title="修改密码" :visible.sync="isShow" width="30%" :close-on-click-modal="false">
            <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-form :model="resetPasswordForm" ref="resetPasswordForm" :rules="resetPasswordFormRule" label-width='100px' size="mini">
                <el-form-item label="账号">
                    <el-input v-model="resetPasswordForm.managerName" size="mini" autocomplete="off" readonly disabled></el-input>
                </el-form-item>
                <el-form-item label="旧密码" prop="oldManagerPassword">
                    <el-input v-model="resetPasswordForm.oldManagerPassword" size="mini" autocomplete="off" show-password></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="newManagerPassword">
                    <el-input v-model="resetPasswordForm.newManagerPassword" size="mini" autocomplete="off" show-password></el-input>
                </el-form-item>
                <el-form-item label="确认新密码" prop="newComfirmManagerPassword" style="margin-bottom: 30px;">
                    <el-input v-model="resetPasswordForm.newComfirmManagerPassword" size="mini" autocomplete="off" show-password></el-input>
                </el-form-item>
            </el-form>
            </el-scrollbar>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="isShow = false;resetPasswordForm.oldManagerPassword = '';resetPasswordForm.newManagerPassword = ''">取 消</el-button>
                <el-button type="primary" size="mini" @click="updatePassword">更新密码</el-button>
            </div>
    </el-dialog>
</div>
<script>
    var resetPasswordVue = new Vue({
        el: '#reset-password',
        data: {
            // 模态框的显示
            isShow: false,
            resetPasswordForm: {
                managerName: '',
                oldManagerPassword: '',
                newManagerPassword: '',
                newComfirmManagerPassword: ''//确认新密码
            },
            resetPasswordFormRule: {
                oldManagerPassword: [{
                    required: true,
                    message: '请输入旧密码',
                    trigger: 'blur'
                }, ],
                newManagerPassword: [{
                    required: true,
                    message: '请输入新密码',
                    trigger: 'blur'
                }, ],
                newComfirmManagerPassword: [{
                    required: true,
                    message: '请再次输入确认密码',
                    trigger: 'blur'
                }, {
                    validator: function (rule, value, callback) {
                        if (resetPasswordVue.resetPasswordForm.newManagerPassword === value) {
                            callback();
                        } else {
                            callback('新密码和确认新密码不一致');
                        }
                    }
                 }]
            }
        },
        methods: {
            // 更新密码
            updatePassword: function () {
                var that = this;
                this.$refs['resetPasswordForm'].validate(function (valid) {
                    if (valid) {
                        ms.http.post(ms.manager + "/updatePassword.do", that.resetPasswordForm).then(function (data) {
                            if (data.result == true) {
                                that.resetPasswordForm.oldManagerPassword = '';
                                that.resetPasswordForm.newManagerPassword = '';
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
            }
        },
        created:function () {
            var that = this;

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
                    that.resetPasswordFormRule.newManagerPassword.push(obj)
                }
            })
        }
    });
</script>
