<el-dialog  v-cloak id="form" title="标签" :visible.sync="dialogVisible" width="50%" :close-on-click-modal="false">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="mini">
        <el-form-item label="标签名称" prop="tagName">
            <el-input v-model="form.tagName"
                      :disabled="false"
                      :style="{width:  '100%'}"
                      :clearable="true"
                      placeholder="请输入标签名称">
            </el-input>
        </el-form-item>
        <el-form-item label="标签类型" prop="tagType">
            <el-select v-model="form.tagType"
                       :style="{width: ''}"
                       :filterable="false"
                       :disabled="false"
                       :multiple="false" :clearable="true"
                       placeholder="请选择标签类型">
                <el-option v-for='item in tagTypeOptions' :key="item.dictValue" :value="item.dictValue"
                           :label="item.dictLabel"></el-option>
            </el-select>
            <div class="ms-form-tip">
                列表、单标签：通过SQL的select语句返回 <br/> 自定义宏：功能性标签，基于freemarker的宏方法
            </div>
        </el-form-item>
        <el-form-item label="描述" prop="tagDescription">
            <el-input
                    type="textarea" :rows="5"
                    :disabled="false"
                    v-model="form.tagDescription"
                    :style="{width: '100%'}"
                    placeholder="请输入描述">
            </el-input>
        </el-form-item>
        <el-form-item label="内容" prop="tagSql">
            <el-input
                    type="textarea" :rows="15"
                    :disabled="false"
                    v-model="form.tagSql"
                    :style="{width: '100%'}"
                    placeholder="sql">
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
        data: function() {
            return {
                saveDisabled: false,
                dialogVisible: false,
                //表单数据
                form: {
                    // 标签名称
                    tagName: '',
                    // 标签类型
                    tagType: '',
                    // 描述
                    tagDescription: '',
                    //  sql
                    tagSql: '',
                    notDel:0
                },
                tagTypeOptions: null,
                rules: {
                    // 标签名称
                    tagName: [{
                        "required": true,
                        "message": "标签名称必须填写"
                    }],
                    // 标签类型
                    tagType: [{
                        "required": true,
                        "message": "标签类型必须选择"
                    }],
                    // 标签类型
                    tagSql: [{
                        "required": true,
                        "message": "sql语句必须填写"
                    }]
                }
            };
        },
        watch: {
            dialogVisible: function(v) {
                if (!v) {
                    this.$refs.form.resetFields();
                    this.form={};
                }
            }
        },
        computed: {},
        methods: {
            open: function(id) {
                if (id) {
                    this.get(id);
                }

                this.$nextTick(function () {
                    this.dialogVisible = true;
                });
            },
            save: function() {
                var that = this;
                var url = ms.manager + "/mdiy/tag/save.do";

                if (that.form.id > 0) {
                    url = ms.manager + "/mdiy/tag/update.do";
                }

                this.$refs.form.validate(function (valid) {
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
                                that.saveDisabled = false;
                                location.href = ms.manager + "/mdiy/tag/index.do";
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: data.msg,
                                    type: 'warning'
                                });

                            }

                            that.dialogVisible = false;
                        }).catch(function(err){
                            that.saveDisabled = false;
                        });
                    } else {
                        return false;
                    }
                });
            },
            //获取tagType数据源
            tagTypeOptionsGet: function() {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {
                    dictType: '标签类型',
                    pageSize: 99999
                }).then(function (data) {
                    if(data.result){
                        data = data.data;
                        that.tagTypeOptions = data.rows;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取当前标签
            get: function(id) {
                var that = this;
                ms.http.get(ms.manager + "/mdiy/tag/get.do", {
                    "id": id
                }).then(function (data) {
                    if (data.result) {
                        that.form = data.data;
                        that.form.tagType += "";
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            }
        },
        created: function () {
            this.tagTypeOptionsGet();
        }
    });
</script>
