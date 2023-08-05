<!-- 增加密码复杂度的验证 -->
<script src="${base}/static/mdiy/index.js"></script>
<el-dialog id="form" title="管理员" :visible.sync="dialogVisible" width="50%" v-cloak :close-on-click-modal="false">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px" size="mini">
            <el-form-item  label="管理员账号" prop="managerName">
                    <el-input v-model="form.managerName"
                          :disabled="nameDisabled"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入管理员名">
                </el-input>
            </el-form-item>
            <el-form-item  label="管理员昵称" prop="managerNickName">
                    <el-input v-model="form.managerNickName"
                          :disabled="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入管理员昵称">
                </el-input>
            </el-form-item>
            <el-form-item  label="管理员密码" prop="managerPassword">
                    <el-input v-model="form.managerPassword"
                          type="password"
                          :disabled="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入管理员密码">
                </el-input>
            </el-form-item>
            <el-form-item  label="确认新密码" prop="">
                    <el-input v-model="managerNewPassword"
                          type="password"
                          :disabled="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入管理员密码">
                </el-input>
            </el-form-item>

            <el-form-item  label="角色名称" prop="roleIds">
                        <el-select v-model="form.roleIds"
                               :style="{width: ''}"
                               :filterable="false"
                               :disabled="false"
                               multiple
                               :clearable="true"
                               placeholder="请选择角色名称">
                        <el-option v-for='item in managerroleIdsOptions' :key="item.id" :value="item.id"
                                   :label="item.roleName"></el-option>
                    </el-select>
            </el-form-item>
            </el-form>   <div slot="footer">
        <el-button size="mini" @click="dialogVisible = false">取 消</el-button>
        <el-button size="mini" type="primary" @click="save()" :loading="saveDisabled">保存</el-button>
    </div>
</el-dialog>
<script>
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                saveDisabled: false,
                dialogVisible: false,
                nameDisabled: false,
                //表单数据
                form: {
                    // 管理员账号
                    managerName: '',
                    // 管理员昵称
                    managerNickName: '',
                    // 管理员密码
                    managerPassword: '',

                    // 角色名称
                    roleIds: ''
                },
                //  管理员密码二次确认
                managerNewPassword: '',
                managerroleIdsOptions: [],
                              rules: {
                    // 管理员账号
                    managerName: [{
                        "required": true,
                        "message": "管理员账号必须填写"
                    }, {
                        min: 3,
                        max: 30,
                        message: '管理员账号长度为3-30个字符!',
                        trigger: 'change'
                    }],
                    // 管理员昵称
                    managerNickName: [{
                        "required": true,
                        "message": "管理员昵称必须填写",
                    }, {
                        min: 1,
                        max: 15,
                        message: '管理员昵称长度为1-15个字符!',
                        trigger: 'change'
                    }],
                    // 管理员密码
                    managerPassword: [{
                        "required": true,
                        "message": "管理员密码必须填写"
                    }],
                    // 管理员密码二次确认
                    managerNewPassword: [{
                        "required": true,
                        "message": "管理员密码必须填写"
                    }],

                    // 角色名称
                    roleIds: [{
                        "required": true,
                        "message": "角色名称必须填写"
                    }]
                }
            };
        },
        watch: {
            dialogVisible: function (v) {
                if (!v) {
                    this.$refs.form.resetFields();
                    this.form.id = 0;
                    this.managerNewPassword = "";
                }
            }
        },

        methods: {
            open: function (id) {
                this.nameDisabled = false;
                this.managerroleIdsOptionsGet();
                if (id) {
                    this.get(id);
                }

                this.$nextTick(function () {
                    this.dialogVisible = true;
                });
            },
            save: function () {
                var that = this;
                var url = ms.manager + "/basic/manager/save.do";

                if (that.form.id > 0) {
                    url = ms.manager + "/basic/manager/update.do"; //更新时密码不必填
                    this.rules.managerPassword[0].required = false;
                    this.rules.managerNewPassword[0].required = false;
                }
                if(that.form.managerPassword !== that.managerNewPassword){
                    that.$notify({
                        title: '提示',
                        message: '两次密码输入不一致!',
                        type: 'warning'
                    });
                    return false
                }
                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var roleIds = that.form.roleIds;
                        if(typeof that.form.roleIds === 'object'){
                          that.form.roleIds = that.form.roleIds.join(',')
                        }
                        var data = JSON.parse(JSON.stringify(that.form));
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success'
                                });
                                that.saveDisabled = false;
                                that.dialogVisible = false;
                                that.rules.managerPassword[0].required = true;
                                that.form.id = 0;
                                indexVue.list();
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: data.msg,
                                    type: 'warning'
                                });
                                that.form.roleIds = roleIds;
                                that.saveDisabled = false;
                            }
                        });
                    } else {
                        return false;
                    }
                });
            },
            //获取当前管理员管理
            get: function (id) {
                var that = this;
                ms.http.get(ms.manager + "/basic/manager/get.do", {
                    "id": id
                }).then(function (data) {
                    if (data.data.id) {
                        that.form = data.data;
                        that.form.roleIds += "";
                        if (that.form.roleIds == "0") {
                            that.form.roleIds = '';
                        } else {
                          that.form.roleIds = that.form.roleIds.split(',')
                        }
                        if (that.form.id) {
                            that.nameDisabled = true;
                        }
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取managerroleIds数据源
            managerroleIdsOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/basic/role/list.do?pageSize=9999", {}).then(function (data) {
                    that.managerroleIdsOptions = data.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            }

        },
        created: function () {
            var that = this;
            // 根据安全设置中的密码复杂度，调整密码的验证规则
            var obj = {pattern:'',message:'密码格式错误，具体查看安全设置中的密码验证规则'};
            ms.mdiy.config("安全设置","password").then(function (res) {
                if(res.result){
                    //  字符串正则转为正则验证格式
                    obj.pattern = RegExp(res.data)
                }
            })
            ms.mdiy.config("安全设置","passwordMsg").then(function (res) {
                if(res.result){
                    obj.message = res.data
                    that.rules.managerPassword.push(obj)
                }
            })
        }
    });
</script>
