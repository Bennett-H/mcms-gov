<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="规则数据" :visible.sync="dialogVisible" width="50%">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">
            <el-form-item  label="组成类型" prop="irdType">
                    <el-select  v-model="form.irdType"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="false" :clearable="true"
                               placeholder="请选择组成类型">
                        <el-option v-for='item in irdTypeOptions' :key="item.value" :value="item.value"
                                   :label="item.label"></el-option>
                    </el-select>
                <div class="ms-form-tip">
                    所需编号：DEP—202006131212001<br/>
                    配置规则为：<br/>
                    固定文字：DEP<br/>
                    分割符：— <br/>
                    日期变量：yyyyMMddhh<br/>
                    序号：3<br/>
                    序号位数不足由0补全,序号和自定义变量需要业务开发
                </div>
            </el-form-item>
            <el-form-item v-if="options[form.irdType]" :label="options[form.irdType]" prop="irdOption">
                <el-select v-model="form.irdOption"
                           v-if="form.irdType=='date'"
                           filterable
                           allow-create
                           default-first-option
                           :style="{width: '100%'}"
                       :clearable="true"
                           placeholder="请选择类型">
                    <el-option v-for='item in dateOptions' :key="item" :value="item"
                               :label="item">
                        <span style="float: left">{{ item }}</span>
                        <span style="float: right; color: #8492a6; font-size: 13px">{{ ms.util.date.fmt(new Date(),item) }}</span>
                    </el-option>
                </el-select>
                <el-input
                        v-else
                        v-model="form.irdOption"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        maxlength="100"
                        placeholder="请输入选项">
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
        data:function() {
            return {
                loading:false,
                saveDisabled: false,
                dialogVisible:false,
                dateOptions:[
                    'yyMMdd',
                    'yyyyMMdd',
                    'yMd',
                    'yyyyMMddhhmm',
                ],
                //表单数据
                form: {
                    // 组成类型
                    irdType:'',
                    // 选项
                    irdOption:'',
                    irId:ms.util.getParameter("id"),
                },
                options:{
                    text:'内容',
                    date:'时间格式',
                    number:'位数',
                    separator:'符号',
                    custom:'变量名',
                },
                irdTypeOptions:[{"value":"text","label":"固定文字"},{"value":"date","label":"日期变量"},{"value":"number","label":"序号"},{"value":"separator","label":"分隔符"},{"value":"custom","label":"自定义变量"}],
                rules:{
                // 组成类型
                irdType: [{"required":true,"message":"请选择组成类型"}],
                },

            }
        },
        watch:{
            dialogVisible:function (v) {
                if(!v){
                    this.$refs.form.resetFields();
                    this.form.id = 0
                    this.form.irdOption = ''
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
                var url = ms.manager + "/id/ruleData/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/id/ruleData/update.do";
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

            //获取当前规则数据
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/id/ruleData/get.do", {"id":id}).then(function (res) {
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
        }
    });
</script>
