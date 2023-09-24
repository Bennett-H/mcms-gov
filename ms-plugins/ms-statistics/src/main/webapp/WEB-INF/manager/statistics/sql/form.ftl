<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="统计" :visible.sync="dialogVisible" width="50%">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

        <el-form-item  label="统计名称" prop="ssName">
                <el-input
                        v-model="form.ssName"
                        :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                        maxlength="50"
                        placeholder="请输入统计名称">
                </el-input>
        </el-form-item>

<#--        <el-form-item  label="统计类型" prop="ssType">-->
<#--                <template slot='label'>统计类型-->
<#--                    <el-popover placement="top-start" title="提示" trigger="hover">-->
<#--请先在字典管理选项卡添加类型                        <i class="el-icon-question" slot="reference"></i>-->
<#--                    </el-popover>-->
<#--                </template>-->
<#--                    <el-select v-model="form.ssType"-->
<#--                               @visible-change="ssTypeOptionsGet"-->
<#--                               :style="{width: '100%'}"-->
<#--                               :filterable="false"-->
<#--                               :disabled="false"-->
<#--                               :multiple="false" :clearable="true"-->
<#--                              placeholder="请选择统计类型">-->
<#--                        <el-option v-for='item in ssTypeOptions' :key="item.dictValue" :value="item.dictValue"-->
<#--                                   :label="item.dictLabel"></el-option>-->
<#--                    </el-select>-->
<#--        </el-form-item>-->

        <el-form-item  label="统计SQL" prop="ssSql">
            <div class="ms-form-tip"> <#noparse>条件可以使用${}替代，前端请求param参数会替换${}<br/>
                注: 参数是String类型需要写成 '${}'<br/>
                示例：SELECT count(0) as 'num' FROM cms_content where category_id=${category_id}</#noparse></div>
                <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        :readonly="false"
                        v-model="form.ssSql"
                        :style="{width: '100%'}"
                        placeholder="请输入统计SQL">
                </el-input>
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
                    // 统计名称
                    ssName:'',
                    // 统计类型
                    ssType:'',
                    // 统计SQL
                    ssSql:'',
                },
                ssTypeOptions:[],
                rules:{
                // 统计名称
                ssName: [{"required":true,"message":"统计名称不能为空"},{"min":0,"max":50,"message":"统计名称长度必须为0-50"}],
                // 统计类型
                ssType: [{"required":true,"message":"请选择统计类型"}],
                // 统计SQL
                ssSql: [{"required":true,"message":"统计SQL不能为空"}],
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
                this.ssTypeOptionsGet();
                if (id) {
                    this.get(id);
                }else {
                    this.form.id = 0;
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
            },
            save:function() {
                var that = this;
                var url = ms.manager + "/statistics/sql/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/statistics/sql/update.do";
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
                                that.form.id = 0;
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

            //获取当前统计
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/statistics/sql/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data){
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取ssType数据源
            ssTypeOptionsGet:function() {
                    var that = this;
                    ms.http.get(ms.base+'/mdiy/dict/list.do', {dictType:'statisticsType',pageSize:99999}).then(function (res) {
                        that.ssTypeOptions = res.data.rows;
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
