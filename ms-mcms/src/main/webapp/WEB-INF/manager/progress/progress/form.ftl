<!-- 因为审批，重写进度 -->
<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="审批表" :visible.sync="dialogVisible" width="50%">

            <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

            <el-form-item  label="审批节点" prop="progressNodeName">

                <el-input
                        v-model="form.progressNodeName"
                        :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入进度节点名称">
                </el-input>
                <div class="ms-form-tip">
                    配置当前进度节点对应的任务名
                </div>
            </el-form-item>

        <el-form-item  label="审批状态" prop="progressStatus">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        :readonly="false"
                        v-model="form.progressStatus"
                        :style="{width: '100%'}"
                        placeholder="请输入进度状态">
                </el-input>
                <div class="ms-form-tip">
                    数据格式：{"REJECT":"终审不通过","ADOPT":"终审通过"}<br/>
                    最后一个审批节点的ADOPT必须为终审通过
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
                sourceList:[],
                treeList:[{
                  id:'0',
                progressNodeName:'顶级分类',
                 children:[]}
                ],
                saveDisabled: false,
                dialogVisible:false,
                //表单数据
                form: {
                    // 关联id
                    schemeId:ms.util.getParameter("id"),
                    // 父节点
                    progressId:'0',
                    // 进度数
                    progressNumber: '',
                    // 进度节点名称
                    progressNodeName:'',
                    // 进度状态
                    progressStatus:'',
                },
                rules:{
                // 关联id
                schemeId: [{"type":"number","message":"关联id格式不正确"}],
                // 进度节点名称
                progressNodeName: [{"min":0,"max":11,"message":"审批节点名称长度必须为0-11"}],
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
            getTree: function(){
                var that = this;
                ms.http.get(ms.manager+"/progress/progress/list.do",{pageSize:9999}).then(function(res){
                    if(res.result){
                        that.sourceList = res.data.rows;
                        that.treeList[0].children = ms.util.treeData(res.data.rows,'id','progressId','children');
                    }
                }).catch(function(err){
                    console.log(err);
                });
            },
            open:function(id){
            this.getTree()
                if (id) {
                    this.get(id);
                }else {
                    this.progressNumber
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
            },
            save:function() {
                var that = this;
                var url = ms.manager + "/progress/progress/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/progress/progress/update.do";
                }
                this.$refs.form.validate(function(valid) {
                    if (!that.form.progressNodeName){
                        that.$notify({
                            title: '失败',
                            message: "审批节点不能为空",
                            type: 'warning'
                        });
                        return;
                    }
                    try {
                        JSON.parse(that.form.progressStatus);
                    }catch(e) {
                        that.$notify({
                            title: '失败',
                            message: "json格式不正确",
                            type: 'warning'
                        });
                        return;
                    };
                    if (valid) {
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        if(data.id&&(data.id==data.progressId)){
                            that.$message.error('父节点不能为自身')
                            that.saveDisabled = false;
                            return
                        }
                        if(!ms.util.childValidate(that.sourceList,data.id,data.progressId,"id","progressId")){
                            that.$message.error('父节点不能选择子集')
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
                        });
                    } else {
                        return false;
                    }
                })
            },

            //获取当前进度表
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/progress/progress/get.do", {"id":id}).then(function (res) {
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
