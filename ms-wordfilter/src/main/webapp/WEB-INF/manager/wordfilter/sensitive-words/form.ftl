<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="敏感词" :visible.sync="dialogVisible" width="50%">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

        <el-form-item  label="检测词" prop="word">
                <el-input
                        v-model="form.word"
                        :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                        placeholder="请输入检测词">
                </el-input>
        </el-form-item>

        <el-form-item  label="替换词" prop="replaceWord">
                <el-input
                        v-model="form.replaceWord"
                        :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                        placeholder="请输入替换词">
                </el-input>
        </el-form-item>
        <el-form-item  label="词汇类型" prop="wordType">
            <el-radio-group v-model="form.wordType"
                            :style="{width: ''}"
                            :disabled="false">
                <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.value"
                          v-for='(item, index) in wordTypeOptions' :key="item.value + index">
                    {{item.label}}
                </el-radio>
            </el-radio-group>
            <div class="ms-form-tip">
                敏感词： 检测到敏感词会直接替换 <br/>
                纠错词： 检测到纠错词会进行提示
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
                    // 检测词
                    word:'',
                    // 替换词
                    replaceWord:'',
                    // 词汇类型
                    wordType: 'correction',
                },
                // 词汇类型数组
                wordTypeOptions:[{"value":"correction","label":"纠错词"},{"value":"sensitive","label":"敏感词"}],
                rules:{
                    // 检测词
                    word: [{"required":true,"message":"检测词不能为空"},{"min":1,"max":20,"message":"检测词长度必须为1-20"}],
                    // 替换词
                    replaceWord: [{"min":0,"max":20,"message":"替换词长度必须为0-20"}],
                    // 词汇类型
                    wordType: [{"required":true,"message":"词汇类型不能为空"}],
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
                var url = ms.manager + "/wordfilter/sensitiveWords/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/wordfilter/sensitiveWords/update.do";
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

            //获取当前敏感词
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/wordfilter/sensitiveWords/get.do", {"id":id}).then(function (res) {
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
            that.rules.word.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/wordfilter/sensitiveWords/verify.do", {
                        fieldName: "word",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function (res) {
                        if (res.result) {
                            if (!res.data) {
                                callback("检测词已存在");
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
