<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="规则" :visible.sync="dialogVisible" width="50%">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">
            <el-form-item  label="规则名称" prop="irName">
                <el-input
                        v-model="form.irName"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        maxlength="20"
                        placeholder="请输入规则名称">
                </el-input>
            </el-form-item>
            <el-form-item  label="类型" prop="irType">
                <el-select v-model="form.irType"
                           :style="{width: '100%'}"
                           :filterable="false"
                           :disabled="false"
                           :multiple="false" :clearable="true"
                           placeholder="请选择类型">
                    <el-option v-for='item in irTypeOptions' :key="item.dictValue" :value="item.dictValue"
                               :label="item.dictLabel"></el-option>
                </el-select>
                <div class="ms-form-tip">
                    类型不满足时可以在【系统设置-数据字典】菜单中新增，类型名为“编码规则类型”<br/>
                    新增的类型需要业务开发
                </div>
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
                //表单数据
                form: {
                    // 规则名称
                    irName:'',
                    // 类型
                    irType:'',
                },
                irTypeOptions:[],
                rules:{
                // 规则名称
                irName: [{"required":true,"message":"规则名称不能为空"}, {
                    "min": 1,
                    "max": 20,
                    "message": "桂策名称长度必须为1-20"
                }],
                // 类型
                irType: [{"required":true,"message":"请选择类型"}],
                },

            }
        },
        watch:{
            dialogVisible:function (v) {
                if(!v){
                    this.$refs.form.resetFields();
                    this.form.id = 0
                }
            }
        },
        computed:{
        },
        methods: {
            open:function(id){
            this.irTypeOptionsGet();
                if (id) {
                    this.get(id);
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
            },
            save:function() {
                var that = this;
                var url = ms.manager + "/id/rule/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/id/rule/update.do";
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

            //获取当前规则
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/id/rule/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data){
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取irType数据源
            irTypeOptionsGet:function() {
                    var that = this;
                    ms.http.get(ms.base+'/mdiy/dict/list.do', {dictType:'编码规则类型',pageSize:99999}).then(function (res) {
                        if(res.result){
                            res = res.data;
                            that.irTypeOptions = res.rows;
                        }
                    }).catch(function (err) {
                        console.log(err);
                    });
                },
        },
        created:function() {
            var that = this;
        }
    });
</script>
