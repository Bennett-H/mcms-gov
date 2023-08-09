<el-dialog id="form" v-cloak title="广告位" :visible.sync="dialogVisible" width="50%">
            <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="mini">
            <el-form-item  label="广告位名称" prop="positionName">
                    <el-input v-model="form.positionName"
                          :disabled="isPositionName"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入广告位名称">
                </el-input>
                <div class="ms-form-tip">
                    广告位名称不允许修改<br/>
                </div>
            </el-form-item>
            <el-form-item  label="广告位宽度" prop="positionWidth">
                    <el-input v-model.number="form.positionWidth"
                          :disabled="false"
                          :style="{width:  '50%'}"
                          :clearable="true"
                          placeholder="请输入广告位宽度(px)">
                </el-input>
            </el-form-item>
            <el-form-item  label="广告位高度" prop="positionHeight">
                    <el-input v-model.number="form.positionHeight"
                          :disabled="false"
                          :style="{width:  '50%'}"
                          :clearable="true"
                          placeholder="请输入广告位高度(px)">
                </el-input>
            </el-form-item>
            <el-form-item  label="广告位描述" prop="positionDesc">
                    <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        v-model="form.positionDesc"
                        :style="{width: '100%'}"
                        placeholder="请输入广告位描述">
                </el-input>
            </el-form-item>
            </el-form>   <div slot="footer">
        <el-button size="mini" @click="dialogVisible = false">取 消</el-button>
        <el-button size="mini" type="primary" @click="save()" :loading="saveDisabled">保存</el-button>
    </div>
</el-dialog>
<script>
        var form = new Vue({
        el: '#form',
        data:function() {
            return {
                saveDisabled: false,
                dialogVisible:false,
                //表单数据
                form: {
                    // 广告位名称
                    positionName:'',
                    // 广告位宽度
                    positionWidth:'',
                    // 广告位高度
                    positionHeight:'',
                    // 广告位描述
                    positionDesc:'',
                },
                rules:{
                    // 广告位名称
                    positionName: [{"type":"string","message":"广告位名称格式不正确"},{"required":true,"message":"请选择广告位名称"}],
                    // 广告位宽度
                    positionWidth: [{"type":"number","message":"广告位宽度格式不正确"},{"required":true,"message":"请选择广告位宽度"}],
                    // 广告位高度
                    positionHeight: [{"type":"number","message":"广告位高度格式不正确"},{"required":true,"message":"请选择广告位高度"}],
                    // 广告位描述
                    positionDesc: [{"min":0,"max":200,"message":"广告位描述长度应在0-200之间"}],
                },
                // 广告位只读
                isPositionName: false
            }
        },
        watch:{
            dialogVisible:function (v) {
                if(!v){
                    this.form.id=null;
                    this.$refs.form.resetFields();
                }
            }
        },
        computed:{
        },
        methods: {
            open:function(id){
                this.isPositionName = false
                if (id) {
                    // 广告位名称输入框只读
                    this.isPositionName = true
                    this.get(id);
                }
                this.$nextTick(function () {
                    this.dialogVisible = true;
                })
            },
            save:function() {
                var that = this;
                var url = ms.manager + "/ad/position/save.do"

                if (that.form.id > 0) {
                    url = ms.manager + "/ad/position/update.do";
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
                                that.form.id = 0;
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
            //获取当前广告位
            get:function(id) {
                var that = this;
                ms.http.get(ms.manager + "/ad/position/get.do", {"id":id}).then(function (res) {
                    if(res.result&&res.data){
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            }
        },
    });
</script>
