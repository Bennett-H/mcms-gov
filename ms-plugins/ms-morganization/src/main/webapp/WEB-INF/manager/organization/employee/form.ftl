<el-dialog id="empform" v-cloak title="员工" :visible.sync="dialogVisible" width="50%" :close-on-click-modal="false">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

        <el-row>
            <el-col :span="12">
                <el-form-item label="姓名" prop="employeeNickName" size="mini">
                    <el-input placeholder="请输入姓名" v-model="form.employeeNickName"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="性别" prop="employeeSex">
                    <el-select v-model="form.employeeSex"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="false" :clearable="true"
                               placeholder="请选择性别">
                        <el-option v-for='item in employeeSexOptions' :key="item.value" :value="item.value"
                                   :label="item.label"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
<#--            <el-col :span="12">-->
<#--                <el-form-item label="账号" prop="managerName">-->
<#--                    <el-input placeholder="请输入账号" v-model="form.managerName" size="mini"></el-input>-->
<#--                </el-form-item>-->
<#--            </el-col>-->
<#--            <el-col :span="12">-->
<#--                <el-form-item prop="managerPassword">-->
<#--						<span slot='label'>密码-->
<#--						</span>-->
<#--                    <el-input v-model="form.managerPassword" placeholder="请输入密码" size="mini" autocomplete='new-password'-->
<#--                              type='password'/>-->
<#--                    </el-input>-->
<#--                    <div class="ms-form-tip">-->
<#--                        注：编辑时，可不填-->
<#--                        </el-popover>-->
<#--                    </div>-->
<#--                </el-form-item>-->
<#--            </el-col>-->
        </el-row>
        <el-row
                gutter="0"
                justify="start" align="top">
            <el-col span="12">
                <el-form-item label="员工编号" prop="employeeCode">
                    <el-input v-model="form.employeeCode"
                              :disabled="true"
                              :style="{width:  '100%'}"
                              :clearable="true"
                              placeholder="系统生成">
                    </el-input>
                </el-form-item>
            </el-col>
            <el-col span="12">
                <el-form-item label="员工状态" prop="employeeStatus">
                    <el-select v-model="form.employeeStatus"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="false" :clearable="true"
                               placeholder="请选择员工状态">
                        <el-option v-for='item in employeeStatusOptions' :key="item.value" :value="item.value"
                                   :label="item.label"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>

        <el-row
                gutter="0"
                justify="start" align="top">
            <el-col span="24">
                <el-form-item label="岗位" prop="postIds">

                    <el-select v-model="form.postIds"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="true" :clearable="true"
                               placeholder="请选择岗位">
                        <el-option v-for='item in postIdsOptions' :key="item.id" :value="item.id"
                                   :label="item.postName"></el-option>
                    </el-select>

                    <div class="ms-form-tip">
                        员工的岗位标签，只是显示作用，不具备权限控制。
                    </div>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row
                gutter="0"
                justify="start" align="top">
            <el-col span="24">
                <el-form-item label="所属部门" prop="organizationId">
                    <el-cascader v-model="form.organizationId"
                                 :disabled="false"
                                 :clearable="true"
                                 placeholder="请选择所属部门"
                                 :style="{width:'100%'}"
                                 :options="organizationIdTreeDatas"
                                 :props="organizationIdProps">
                    </el-cascader>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row
                gutter="0"
                justify="start" align="top">
            <el-col span="12">
                <el-form-item label="政治面貌" prop="employeePolitics"
                >
                    <el-select v-model="form.employeePolitics"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="false" :clearable="true"
                               placeholder="请选择政治面貌">
                        <el-option v-for='item in employeePoliticsOptions' :key="item.value" :value="item.value"
                                   :label="item.value"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col span="12">
                <el-form-item label="员工学历" prop="employeeEducation"
                >
                    <el-select v-model="form.employeeEducation"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="false" :clearable="true"
                               placeholder="请选择员工学历">
                        <el-option v-for='item in employeeEducationOptions' :key="item.value" :value="item.value"
                                   :label="item.value"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row
                gutter="0"
                justify="start" align="top">
            <el-col span="12">
                <el-form-item label="出生日期" prop="employeeBirthDate">
                    <el-date-picker
                            v-model="form.employeeBirthDate"
                            placeholder="请选择出生日期"
                            start-placeholder=""
                            end-placeholder=""
                            :readonly="false"
                            :disabled="false"
                            :editable="false"
                            :clearable="true"
                            format="yyyy-MM-dd"
                            value-format="yyyy-MM-dd"
                            :style="{width:'100%'}"
                            type="date">
                    </el-date-picker>
                </el-form-item>
            </el-col>
            <el-col span="12">
                <el-form-item label="手机号" prop="employeePhone"
                >
                    <el-input v-model="form.employeePhone"
                              :disabled="false"
                              :style="{width:  '100%'}"
                              :clearable="true"
                              placeholder="请输入手机号">
                    </el-input>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
    <div slot="footer">
        <el-button size="mini" @click="dialogVisible = false">取 消</el-button>
        <el-button size="mini" type="primary" @click="save()" :loading="saveDisabled">保存</el-button>
    </div>
</el-dialog>
<script>
    var empform = new Vue({
        el: '#empform',
        data: function () {
            return {
                saveDisabled: false,
                dialogVisible: false,
                //表单数据
                form: {
                    // 员工编号
                    employeeCode: '',
                    // 员工状态
                    employeeStatus: '',
                    // 性别
                    employeeSex: '',
                    managerName: '',
                    employeeNickName: '',
                    managerPassword: '',
                    // 所属角色
                    //
                    // employeeRole: [],
                    // 岗位

                    postIds: [],
                    // 所属部门
                    organizationId: '',
                    // 政治面貌
                    employeePolitics: '',
                    // 员工学历
                    employeeEducation: '',
                    // 出生日期
                    employeeBirthDate: '',
                    // 手机号
                    employeePhone: '',
                },
                employeeStatusOptions: [{"value": "in", "label": "在职"}, {
                    "value": "try",
                    "label": "试用"
                }, {"value": "out", "label": "离职"}],
                employeeSexOptions: [{"value": 1, "label": "男"}, {"value": 2, "label": "女"}],
                employeeRoleOptions: [],
                postIdsOptions: [],
                organizationIdOptions: [],
                organizationIdProps: {
                    "emitPath": false,
                    "checkStrictly": true,
                    "value": "id",
                    "label": "organizationTitle",
                    "expandTrigger": "hover"
                },
                employeePoliticsOptions: [{"value": "党员"},{"value": "预备党员"}, {"value": "团员"}, {"value": "群众"}, {"value": "无党派人士"}],
                employeeEducationOptions: [{"value": "中专"}, {"value": "高中"}, {"value": "大专"}, {"value": "本科"}, {"value": "硕士研究生"}, {"value": "博士研究生"}, {"value": "博士后"}],
                rules: {
                    // 员工状态
                    employeeStatus: [{"required": true, "message": "请选择员工状态"}],
                    // 性别
                    employeeSex: [{"required": true, "message": "请选择性别"}],
                    employeeNickName: [{"required": true, "message": "请输入姓名"}, {
                        min: 1,
                        max: 50,
                        message: '长度在1到50个字符',
                        trigger: ['change', 'blur']
                    },],
                    managerName: [{"required": true, "message": "请输入账号"}],
                    managerPassword: [],
                    // // 所属角色
                    // employeeRole: [{"required": true, "message": "请选择所属角色"}],
                    //所属部门
                    organizationId: [{"required": true, "message": "请选择所属部门"}, {
                        validator: function (rule, value, callback) {
                            var data = empform.organizationIdOptions.find(function (x) {
                                return x.id == empform.form.organizationId;
                            })
                            if (!data) {
                                callback(new Error("所属部门不能为空"));
                            } else {
                                callback();
                            }
                        },
                    }],
                    // 岗位
                    postIds: [{"required": true, "message": "请选择岗位"}],
                    // 政治面貌
                    employeePolitics: [{"required": true, "message": "请选择政治面貌"}],
                    // 员工学历
                    employeeEducation: [{"required": true, "message": "请选择员工学历"}],
                    // 年龄
                    employeeBirthDate: [{"required": true, "message": "请选择出生日期"}],
                    // 手机号
                    employeePhone: [{"required": true, "message": "请选择手机号"}, {
                        "pattern": /^[1][0-9]{10}$/,
                        "message": "手机号格式不匹配"
                    }],
                },

            }
        },
        watch: {
            dialogVisible: function (v) {
                if (!v) {
                    this.form.id = 0;
                    this.$refs.form.resetFields();
                } else {
                    if (this.form.id && this.form.id != '0') {
                        this.rules.managerPassword = [];
                    } else {
                        this.rules.managerPassword = [{"required": true, "message": "请输入密码"}]
                    }
                }
            },
            //  是否对密码进行正则匹配

        },
        computed: {
            organizationIdTreeDatas: function () {
                if (!this.organizationIdOptions || !this.organizationIdOptions.length) {
                    return []
                }
                return ms.util.treeData(this.organizationIdOptions, 'id', 'organizationId', 'children');
            },
        },
        methods: {
            open: function (id, organizationId) {
                this.employeeRoleOptionsGet();
                this.postIdsOptionsGet();
                this.organizationIdOptionsGet();
                if (id) {
                    this.get(id);
                    this.form.id = id
                }
                if (organizationId) {
                  this.form.organizationId = organizationId
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
            },
            save: function () {
                var that = this;
                var url = ms.manager + "/organization/employee/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/organization/employee/update.do";
                }
                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        if (data.employeeRole) {
                            data.employeeRoleIds = data.employeeRole.join(',');
                        }
                        if (data.postIds) {
                            data.postIds = data.postIds.join(',');
                        }
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success'
                                });
                                that.form.id=0;//解决编辑功能走新增接口问题
                                that.form.managerId = '';//移至此处，清空管理员ID，否则导致新增的员工拥有此管理员账号
                                that.dialogVisible = false;
                                indexVue.list();
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: data.msg,
                                    type: 'warning'
                                });
                            }
                            that.saveDisabled = false;
                        }).catch(function (err) {
                            that.saveDisabled = false;
                        })
                    } else {
                        return false;
                    }
                })
            },

            //获取当前员工
            get: function (id) {
                var that = this;
                ms.http.get(ms.manager + "/organization/employee/get.do", {"id": id}).then(function (res) {
                    if (res.result && res.data) {
                        if (res.data.employeeRoleIds) {
                            res.data.employeeRole = res.data.employeeRoleIds.split(',');
                        }
                        if (res.data.postIds) {
                            res.data.postIds = res.data.postIds.split(',');
                        }
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取employeeRole数据源
            employeeRoleOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/basic/role/all.do", {pageSize: 9999}).then(function (res) {
                    that.employeeRoleOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取postIds数据源
            postIdsOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/organization/post/list.do", {pageSize: 9999}).then(function (res) {
                    that.postIdsOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取organizationId数据源
            organizationIdOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/organization/organization/list.do", {
                    pageSize: 9999,
                    organizationStatus: "normal"
                }).then(function (res) {
                    that.organizationIdOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created: function () {
        }
    });
</script>
