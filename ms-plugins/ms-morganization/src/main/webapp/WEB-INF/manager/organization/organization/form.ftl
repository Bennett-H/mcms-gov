<el-dialog id="form" v-cloak title="组织机构" :visible.sync="dialogVisible" width="50%" :close-on-click-modal="false">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">
        <el-row
                gutter="0"
                justify="start" align="top">
            <el-col span="12">
                <el-form-item label="部门名称" prop="organizationTitle"
                >
                    <el-input v-model="form.organizationTitle"
                              :disabled="false"
                              :style="{width:  '100%'}"
                              :clearable="true"
                              placeholder="请输入部门名称">
                    </el-input>
                </el-form-item>
            </el-col>
            <el-col span="12">
                <el-form-item label="所属部门" prop="organizationId"
                >
                    <ms-tree-select ref="treeSelelct" style="{width: '100%'}"
                                 :props="{value: 'id',label: 'organizationTitle',children: 'children'}"
                                 :options="treeList"
                                 v-model="form.organizationId"></ms-tree-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row
                gutter="0"
                justify="start" align="top">
            <el-col span="12">
                <el-form-item label="部门状态" prop="organizationStatus"
                >
                    <el-select v-model="form.organizationStatus"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="false" :clearable="true"
                               placeholder="请选择部门状态">
                        <el-option v-for='item in organizationStatusOptions' :key="item.value" :value="item.value"
                                   :label="item.label"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col span="12">
                <el-form-item label="部门编号" prop="organizationCode"
                >
                    <el-input v-model="form.organizationCode"
                              :disabled="true"
                              :style="{width:  '100%'}"
                              :clearable="true"
                              placeholder="系统生成">
                    </el-input>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row
                gutter="0"
                justify="start" align="top">
            <el-col span="12">
                <el-form-item label="机构类型" prop="organizationType"
                >
                    <el-select v-model="form.organizationType"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="false" :clearable="true"
                               placeholder="请选择机构类型">
                        <el-option v-for='item in organizationTypeList' :key="item.dictValue" :value="item.dictValue"
                                   :label="item.dictLabel"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col span="12">
                <el-form-item label="负责人" prop="organizationLeader">
                    <ms-employee
                            v-model="form.organizationLeader"
                            :disabled="false"
                            :clearable="true"
                            placeholder="请选择负责人"
                            :style="{width:'100%'}">
                    </ms-employee>
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
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                sourceList: [],
                treeList: [{
                    id: '0',
                    organizationTitle: '顶级分类',
                    children: []
                }
                ],
                saveDisabled: false,
                dialogVisible: false,
                //表单数据
                form: {
                    // 部门名称
                    organizationTitle: '',
                    // 所属部门
                    organizationId: '',
                    // 部门状态
                    organizationStatus: '',
                    // 部门编号
                    organizationCode: '',
                    // 分管领导
                    organizationLeaders: '',
                    // 负责人
                    organizationLeader: '',
                    // 机构类型
                    organizationType: '',
                },
                organizationStatusOptions: [{"value": "normal", "label": "正常"}, {"value": "pause", "label": "停用"}],
                organizationLeadersOptions: [],
                organizationLeaderOptions: [],
                //部门枚举，用来验证父子关系
                organizationEnum:[
                    {
                        "key":'省级公司',
                        value:0,
                    }, {
                        "key":'市级公司',
                        value:1,
                    }, {
                        "key":'部门',
                        value:2,
                    },
                ],
                organizationTypeOptions: [],
                rules: {
                    // 部门名称
                    organizationTitle: [
                        {"required":true,"message":"请输入部门名称"},
                        { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
                        ],
                    // 部门状态
                    organizationStatus: [{"required":true,"message":"请选择部门状态"}],
                    // 机构类型
                    organizationType: [{"required":true,"message":"请选择机构类型"}],
                },

            }
        },
        watch: {
            dialogVisible: function (v) {
                if (!v) {
                    this.form.id=0
                    this.$refs.form.resetFields();
                    this.$refs.treeSelelct.clearHandle()
                }
            }
        },
        computed: {
            //获取当前选中Id
            currOrganization: function (){
                var that = this
                return this.sourceList.find(function(x) {return  x.id==that.form.organizationId});
            },
            //返回过滤之后的机构类型
            organizationTypeList: function(){
                var that = this
                var data = this.organizationTypeOptions.filter(function(o) {
                  //业务逻辑不确定，暂时全部返回
                  return true
                   //return !that.form.organizationId||that.organizationId=='0'||!that.currOrganization||o.dictValue=='部门' || that.organizationEnum.find(function (x){return x.key==o.dictValue}) && that.organizationEnum.find(function (x){return x.key==that.currOrganization.organizationType}) && that.organizationEnum.find(function (x){return x.key==o.dictValue}).value > that.organizationEnum.find(function (x){return x.key==that.currOrganization.organizationType}).value
                })
                return  data;

            },

        },
        methods: {
            getTree: function() {
                var that = this;
                ms.http.get(ms.manager + "/organization/organization/list.do", {pageSize: 9999,organizationStatus:"normal"}).then(function (res) {
                    if (res.result) {
                        that.sourceList = res.data.rows;
                        that.treeList[0].children = ms.util.treeData(res.data.rows, 'id', 'organizationId', 'children');
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            open: function(id) {
                this.getTree()
                this.organizationLeadersOptionsGet();
                this.organizationLeaderOptionsGet();
                this.organizationTypeOptionsGet();
                if (id) {
                    this.get(id);
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
            },
            save: function() {
                var that = this;
                var url = ms.manager + "/organization/organization/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/organization/organization/update.do";
                }
                this.$refs.form.validate(function(valid)  {
                    if (valid) {
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        if (data.id && data.id == data.organizationId) {
                            that.$message.error('所属部门不能为自身')
                            that.saveDisabled = false;
                            return
                        }
                        if (!ms.util.childValidate(that.sourceList, data.id, data.organizationId, "id", "organizationId")) {
                            that.$message.error('所属部门不能选择子集')
                            that.saveDisabled = false;
                            return
                        }
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
                        }).catch(function(err) {
                            that.saveDisabled = false;
                        })
                    } else {
                        return false;
                    }
                })
            },

            //获取当前组织机构
            get: function(id) {
                var that = this;
                ms.http.get(ms.manager + "/organization/organization/get.do", {"id": id}).then(function (res) {
                    if (res.result && res.data) {
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取organizationLeaders数据源
            organizationLeadersOptionsGet: function() {
                var that = this;
                ms.http.get(ms.manager + "/organization/employee/list.do", {}).then(function (res) {
                    that.organizationLeadersOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取organizationLeader数据源
            organizationLeaderOptionsGet: function() {
                var that = this;
                ms.http.get(ms.manager + "/organization/employee/list.do", {pageSize:99999}).then(function (res) {
                    that.organizationLeaderOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取organizationType数据源
            organizationTypeOptionsGet: function() {
                var that = this;
                ms.http.get(ms.base+'/mdiy/dict/list.do', {dictType:'机构类型',pageSize:99999}).then(function (res) {
                    if (res.result){
                        that.organizationTypeOptions = res.data.rows;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created: function() {
            var that = this;
            this.rules.organizationTitle.push({
                validator: function (rule, value, callback) {
                    var fields = {
                        'organization_title':value,
                        'organization_id':that.form.organizationId?that.form.organizationId:0
                    };
                    ms.http.get(ms.manager + "/organization/organization/verify.do", {
                        'fields': JSON.stringify(fields),
                        id: that.form.id,
                        idName: 'id',
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

<style scope>
    .el-select {
        width: 100%;
    }
</style>
