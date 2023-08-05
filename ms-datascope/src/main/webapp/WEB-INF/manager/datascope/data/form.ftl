<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="数据权限" :visible.sync="dialogVisible" width="50%">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

        <el-form-item  label="目标编号" prop="dataTargetId">
            <el-cascader
                    v-model="form.dataTargetId"
                    :disabled="false"
                    :clearable="true"
placeholder="请输入目标编号"                    :style="{width:'100%'}"
                    :options="dataTargetIdTreeDatas"
                    :props="dataTargetIdProps">
            </el-cascader>
        </el-form-item>

        <el-form-item  label="业务分类" prop="dataType">
                <template slot='label'>业务分类
                    <el-popover placement="top-start" title="提示" trigger="hover">
请先在字典管理选项卡添加类型                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
                    <el-select v-model="form.dataType"
                               @visible-change="dataTypeOptionsGet"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="false" :clearable="true"
                              placeholder="请选择业务分类">
                        <el-option v-for='item in dataTypeOptions' :key="item.dictValue" :value="item.dictValue"
                                   :label="item.dictLabel"></el-option>
                    </el-select>
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
                    // 目标编号
                    dataTargetId:'',
                    // 业务分类
                    dataType:'',
                },
                dataTargetIdOptions:[],
                dataTargetIdProps:{"emitPath":false,"checkStrictly":true,"value":"categoryId","label":"categoryTitle","expandTrigger":"hover","children":"children"},
                dataTypeOptions:[],
                rules:{
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
        dataTargetIdTreeDatas:function(){
        return ms.util.treeData(this.dataTargetIdOptions,'categoryId','children','children');
        },
        },
        methods: {
            open:function(id){
            this.dataTargetIdOptionsGet();
                this.dataTypeOptionsGet();
                if (id) {
                    this.get(id);
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
            },
            save:function() {
                var that = this;
                var url = ms.manager + "/datascope/data/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/datascope/data/update.do";
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

            //获取当前数据权限
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/datascope/data/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data){
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取dataTargetId数据源
            dataTargetIdOptionsGet:function() {
                var that = this;
                ms.http.get(ms.manager + '/organization/employee/list.do?pigeSize=9999', {}).then(function (res) {
                    that.dataTargetIdOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取dataType数据源
            dataTypeOptionsGet:function() {
                    var that = this;
                    ms.http.get(ms.base+'/mdiy/dict/list.do', {dictType:'业务分类',pageSize:99999}).then(function (res) {
                        that.dataTypeOptions = res.data.rows;
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
