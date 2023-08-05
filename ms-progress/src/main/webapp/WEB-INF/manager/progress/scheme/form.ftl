<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="进度方案" :visible.sync="dialogVisible" width="50%">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

        <el-form-item  label="方案名称" prop="schemeName">
            <el-input
                    v-model="form.schemeName"
                    :disabled="false"
                      :readonly="false"
                      :style="{width:  '100%'}"
                      :clearable="true"
                    placeholder="请输入方案名称">
            </el-input>
        </el-form-item>
        <el-form-item  label="回调表名" prop="schemeTable">
            <el-input
                    v-model="form.schemeTable"
                    :disabled="false"
                    :readonly="false"
                    :style="{width:  '100%'}"
                    :clearable="true"
                    placeholder="请输入回调表名">
            </el-input>
            <div class="ms-form-tip">
                用于此方案回调开发的表名 如文章:cms_content
            </div>
        </el-form-item>
        <el-form-item  label="类型" prop="schemeType">
            <el-radio-group v-model="form.schemeType"
                            :style="{width: ''}"
                            :disabled="false">
                <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.value"
                          v-for='(item, index) in schemeTypeOptions' :key="item.value + index">
                    {{item.label}}
                </el-radio>
            </el-radio-group>
            <div class="ms-form-tip">
                递增： 即每完成单一任务，项目的进度会累加设定的进度数 <br/>
                赋值： 即每完成单一任务，项目的进度会跳转至设定的进度
            </div>
        </el-form-item>
    </el-form>
    <div slot="footer">
        <el-button size="mini" @click="dialogVisible = false">取消</el-button>
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
                //表单数据
                form: {
                    // 方案名称
                    schemeName:'',
                    // 类型
                    schemeType:'add',
                    // 回调表名
                    schemeTable:'',
                },
                schemeTypeOptions:[{"value":"add","label":"递增"},{"value":"set","label":"赋值"}],
                rules:{
                    // 方案名称
                    schemeName: [{"required":true,"message":"方案名称不能为空"}],
                    // 类型
                    schemeType: [{"required":true,"message":"类型不能为空"}],
                    // 回调表名
                    schemeTable: [{"required":true,"message":"回调表名不能为空"}],
                },

            }
        },
        watch:{
            dialogVisible:function (v) {
                if(!v){
                    this.$refs.form.resetFields();
                }
            }
        },
        computed:{
        },
        methods: {
            open:function(id){
                if (id) {
                    this.get(id);
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
            },
            save:function() {
                var that = this;
                var url = ms.manager + "/progress/scheme/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/progress/scheme/update.do";
                }
                this.$refs.form.validate(function(valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.$notify({
                                    title: "成功",
                                    message: "保存成功",
                                    type: 'success'
                                });
                                that.form.id = 0;
                                that.dialogVisible = false;
                                indexVue.list();
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

            //获取当前进度方案
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/progress/scheme/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data){
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created:function() {
            var that = this;
            this.rules.schemeName.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/progress/scheme/verify.do", {
                        fieldName: "scheme_name",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function(res){
                        if (res.result) {
                            if (!res.data) {
                                callback("方案名称已存在");
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
