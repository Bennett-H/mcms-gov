<el-dialog id="form" v-cloak title="岗位管理" :visible.sync="dialogVisible" width="50%" :close-on-click-modal="false">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">
        <el-form-item v-show="false" prop="id">
            <el-input v-model="form.id">
            </el-input>
        </el-form-item>
        <el-row
                gutter="0"
                justify="start" align="top">
            <el-col span="12">
                <el-form-item label="岗位名称" prop="postName">
                    <el-input v-model="form.postName"
                              :disabled="false"
                              :style="{width:  '100%'}"
                              :clearable="true"
                              placeholder="请输入岗位名称">
                    </el-input>
                </el-form-item>
            </el-col>
            <el-col span="12">
                <el-form-item label="岗位编号" prop="postCode"
                >
                    <el-input v-model="form.postCode"
                              :disabled="true"
                              :style="{width:  '100%'}"
                              :clearable="true"
                              placeholder="系统生成">
                    </el-input>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item label="岗位描述" prop="postDesc"
        >
            <el-input
                    type="textarea" :rows="5"
                    :disabled="false"

                    v-model="form.postDesc"
                    :style="{width: '100%'}"
                    placeholder="请输入岗位描述">
            </el-input>
        </el-form-item>
    </el-form>
    <div slot="footer">
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
                //表单数据
                form: {
                    // 岗位名称
                    postName: '',
                    // 岗位编号
                    postCode: '',
                    // 岗位描述
                    postDesc: '',
                },
                rules: {
                    // 部门名称
                    postName: [
                        {"required":true,"message":"请输入岗位名称"},
                        { min: 1, max: 10, message: '长度在 1 到 10 个字符', trigger: 'blur' }
                    ],
                },

            }
        },
        watch: {
            dialogVisible: function (v) {
                if (!v) {
                    this.$refs.form.resetFields();
                    this.form.id = 0
                }
            }
        },
        computed: {},
        methods: {
            open: function (id) {
                if (id) {
                    this.get(id);
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
            },
            save: function () {
                var that = this;
                var url = ms.manager + "/organization/post/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/organization/post/update.do";
                }
                this.$refs.form.validate(function(valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success'
                                });
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
                        });
                    } else {
                        return false;
                    }
                })
            },

            //获取当前岗位管理
            get: function (id) {
                var that = this;
                ms.http.get(ms.manager + "/organization/post/get.do", {"id": id}).then(function (res) {
                    if (res.result && res.data) {
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created: function () {
            var that = this;
            this.rules.postName.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/organization/post/verify.do", {
                        fieldName: "post_name",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function(res){
                        if (res.result) {
                            if (!res.data) {
                                callback("岗位名称已存在！");
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
