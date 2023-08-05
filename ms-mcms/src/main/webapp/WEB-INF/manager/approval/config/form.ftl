<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="审批配置" :visible.sync="dialogVisible" width="50%">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

        <el-form-item  label="方案名称" prop="schemeId">
                    <el-select v-model="form.schemeId"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               @change="progressIdOptionsGet()"
                    :multiple="false" :clearable="true"
                placeholder="请选择方案名称">
                        <el-option v-for='item in schemeIdOptions' :key="item.id" :value="item.id"
                                   :label="item.schemeName"></el-option>
                    </el-select>
        </el-form-item>

        <el-form-item  label="审批节点" prop="progressId">
                    <el-select v-model="form.progressId"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                    :multiple="false" :clearable="true"
                placeholder="请选择审批节点">
                        <el-option v-for='item in progressIdOptions' :key="item.id" :value="item.id"
                                   :label="item.progressNodeName"></el-option>
                    </el-select>
        </el-form-item>

        <el-form-item  label="等级管理员配置" prop="configManagerIds">
                    <el-select v-model="form.configManagerIds"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                    :multiple="true" :clearable="true"
                placeholder="请选择等级管理员配置">
                        <el-option v-for='item in configManagerIdsOptions' :key="item.id" :value="item.id"
                                   :label="item.managerName"></el-option>
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
                    // 方案名称
                    schemeId:'',
                    // 审批节点
                    progressId:'',
                    // 等级管理员配置

                    configManagerIds: [],
                },
                schemeIdOptions:[],
                progressIdOptions:[],
                configManagerIdsOptions:[],
                rules:{
                // 方案名称
                schemeId: [{"required":true,"message":"请选择方案名称"}],
                // 审批节点
                progressId: [{"required":true,"message":"请选择审批节点"}],
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
                this.schemeIdOptionsGet();
                this.configManagerIdsOptionsGet();
                if (id) {
                    this.get(id);
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
                this.progressIdOptionsGet();
            },
            save:function() {
                var that = this;
                var url = ms.manager + "/approval/config/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/approval/config/update.do";
                }
                this.$refs.form.validate(function(valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        if(data.configManagerIds){
                            data.configManagerIds = data.configManagerIds.join(',');
                        }
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

            //获取当前审批配置
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/approval/config/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data){
                        if(res.data.configManagerIds){
                            res.data.configManagerIds = res.data.configManagerIds.split(',');
                        }
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取schemeId数据源
            schemeIdOptionsGet:function() {
                var that = this;
                ms.http.get(ms.manager + "/progress/scheme/list.do", {}).then(function (res) {
                    that.schemeIdOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取progressId数据源
            progressIdOptionsGet:function() {
                var that = this;
                ms.http.get(ms.manager + "/progress/progress/list.do", {schemeId:that.form.schemeId}).then(function (res) {
                    that.progressIdOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取configManagerIds数据源
            configManagerIdsOptionsGet:function() {
                var that = this;
                ms.http.get(ms.manager + "/basic/manager/list.do", {pageSize:100}).then(function (res) {
                    that.configManagerIdsOptions = res.data.rows;
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
