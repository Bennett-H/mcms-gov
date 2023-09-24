<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="采集任务" :visible.sync="dialogVisible" width="50%">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">
            <el-form-item  label="采集名称" prop="taskName">
                <el-input
                        v-model="form.taskName"
                        :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                            maxlength="10"
                          placeholder="请输入采集名称">
                </el-input>
            </el-form-item>
            <el-form-item  label="导入表" prop="importTable">
                    <el-select v-model="form.importTable"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="importTableDisabled"
                               :multiple="false"
                               :clearable="true"
                               placeholder="请选择导入表">
                        <el-option v-for='item in importTableOptions' :key="item" :value="item"
                                   :label="item"></el-option>
                    </el-select>
            </el-form-item>
            <el-form-item  label="自动导入" prop="isAutoImport">
                    <el-radio-group v-model="form.isAutoImport"
                                    :style="{width: ''}"
                                    :disabled="false">
                        <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.value"
                                  v-for='(item, index) in isAutoImportOptions' :key="item.value + index">
                            {{item.label}}
                        </el-radio>
                    </el-radio-group>
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
        data:function() {
            return {
                loading:false,
                saveDisabled: false,
                dialogVisible:false,
                importTableDisabled: false,
                //表单数据
                form: {
                    // 采集名称
                    taskName:'',
                    // 导入表
                    importTable:'',
                    // 自动导入
                    isAutoImport:'yes',
                    // 是否去重
                    isRepeat:'yes',
                },
                importTableOptions:[],
                isAutoImportOptions:[{"value":"yes","label":"是"},{"value":"no","label":"否"}],
                isRepeatOptions:[{"value":"yes","label":"是"},{"value":"no","label":"否"}],
                rules:{
                // 采集名称
                taskName: [{"type":"string","message":"采集名称格式不正确"},{"required":true,"message":"采集名称不能为空"}],
                //导入表
                importTable: [{"required": true, "message": "请选择要导入的表"}],
                },

            }
        },
        watch:{
            dialogVisible:function (v) {
                if(!v){
                    this.$refs.form.resetFields();
					 this.form.id =0
                }
            }
        },
        computed:{   
        },
        methods: {
            open:function(id){
                this.importTableOptionsGet();
                if (id) {
                    this.importTableDisabled = true;
                    this.get(id);
                }else {
                    this.importTableDisabled = false;
                }
                this.$nextTick(function () {
                        this.dialogVisible = true;
                })
            },
            save:function() {
                var that = this;
                var url = ms.manager + "/spider/task/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/spider/task/update.do";
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

            //获取当前采集任务
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/spider/task/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data){
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取importTable数据源
            importTableOptionsGet:function() {
                var that = this;
                ms.http.get(ms.manager + "/spider/task/tables", {}).then(function (res) {
                    that.importTableOptions = res.data;
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created:function() {
            var that = this;
            this.rules.taskName.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/spider/task/verify.do", {
                        fieldName: "task_name",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function(res){
                        if (res.result) {
                            if (!res.data) {
                                callback("采集名称已存在！");
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
