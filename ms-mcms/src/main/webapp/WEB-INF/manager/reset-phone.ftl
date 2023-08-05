<!-- 修改密码 增加密码复杂度的验证 -->
<script src="${base}/static/mdiy/index.js"></script>
<div id="reset-phone" class="reset-phone">
        <el-dialog title="修改手机" :visible.sync="isShow" width="30%" :close-on-click-modal="false">
            <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-form :model="resetPhoneForm" ref="resetPhoneForm" :rules="resetPhoneFormRule" label-width='100px' size="mini">
                <el-form-item label="账号">
                    <el-input v-model="resetPhoneForm.managerName" size="mini" autocomplete="off" readonly disabled></el-input>
                </el-form-item>
                <el-form-item label="手机号" prop="managerPhone" style="margin-bottom: 30px;">
                    <el-input v-model="resetPhoneForm.managerPhone" size="mini" autocomplete="off" ></el-input>
                </el-form-item>
            </el-form>
            </el-scrollbar>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="isShow = false;">取 消</el-button>
                <el-button type="primary" size="mini" @click="updatePhone">确定</el-button>
            </div>
    </el-dialog>
</div>
<script>
    var resetPhoneVue = new Vue({
        el: '#reset-phone',
        data: {
            // 模态框的显示
            isShow: false,
            resetPhoneForm: {
                managerName: '',
                managerPhone: '',
            },
            resetPhoneFormRule: {
                managerPhone: [{
                    required: true,
                    message: '请输入',
                    trigger: 'blur'
                }, {"pattern":/^[1][0-9]{10}$/,"message":"手机号格式错误"}]
            }
        },
        methods: {
            // 更新密码
            updatePhone: function () {
                var that = this;
                this.$refs['resetPhoneForm'].validate(function (valid) {
                    if (valid) {
                        ms.http.post(ms.manager + "/gov/managerInfo/saveOrUpdatePhone.do", {
                            managerPhone : that.resetPhoneForm.managerPhone
                        }).then(function (data) {
                            if (data.result == true) {
                                that.isShow = false;
                                that.$notify({
                                    title: '提示',
                                    message: "修改成功！",
                                    type: 'success'
                                });
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
            getManagerPhone:function (){
                var that = this
                ms.http.get(ms.manager + "/gov/managerInfo/getManagerInfo.do").then(function(data){
                    if (data.result) {
                        if (data.data && data.data.managerPhone){
                            that.resetPhoneForm.managerPhone = data.data.managerPhone
                        }
                    }
                })
            }
        },
        created:function () {
        }
    });
</script>
