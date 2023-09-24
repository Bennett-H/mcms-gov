<!DOCTYPE html>
<html>
<head>
    <title>导入导出配置</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/plugins/codemirror/5.48.4/codemirror.js"></script>
    <link href="${base}/static/plugins/codemirror/5.48.4/codemirror.css" rel="stylesheet">
    <script src="${base}/static/plugins/codemirror/5.48.4/mode/css/css.js"></script>
    <script src="${base}/static/plugins/vue-codemirror/vue-codemirror.js"></script>
    <script src="${base}/static/plugins/codemirror/5.48.4/addon/scroll/annotatescrollbar.js"></script>
    <script src="${base}/static/plugins/codemirror/5.48.4/mode/xml/xml.js"></script>
    <script src="${base}/static/plugins/codemirror/5.48.4/mode/javascript/javascript.js"></script>
</head>
<body>
<div id="form" v-loading="loading" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
        </el-button>
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="140px" label-position="right" size="small">
            <el-form-item label="导入导出标识" prop="name">
                <el-input
                        v-model="form.name"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        maxlength="20"
                        placeholder="请输入导入导出标识">
                </el-input>
                <div class="ms-form-tip">
                    用在 ms-imp、ms-exp 组件的id属性值
                </div>
            </el-form-item>
            <el-form-item label="导出sql配置" prop="exportSql">
                <codemirror ref="code" v-model="form.exportSql" :options="cmOption">
                </codemirror>
                <div class="ms-form-tip">
                    导出采用sql直接查询方式
                    推荐使用as别名、可以使用关联查询、条件参数 {} 定义与组件调用时候定义的 columns 属性值一致；
                    <br/>例如：雪花id转换excel会出现精度丢失，解决方法通过sql将字段转换为字符串 <br/>
                    注意：目前只支持ids这一个参数 ({}) <br/>
                    word导出只支持 docFileName（文档标题）、docFileContent（文档内容）两个字段输出，
                    所以对应sql的格式(select 列名1 as docFileName,列名2 as docFileContent from 表名 )；<br/>
                    excel导出无限制，excel导出的时候还可以支持对应数据库的函数方法；<br/>
                    excel导出不适应用于存在长文本的字段，例如：文章内容的导出，推荐采用word导出
                </div>
            </el-form-item>
            <el-form-item label="导入JSON配置" prop="importJson">
                <codemirror ref="code" v-model="form.importJson" :options="cmOption">
                </codemirror>
                <div class="ms-form-tip">
                    { <br/>
                    "table": "主表", "id": "auto（自增长）/snow（雪花）",<br/>
                    "columns": { "excel中文": "表列名"},<br/>
                    "defaults": { "表列名":"值" }
                    <br/> }, { <br/>
                    "table": "从表", "id": "auto", <br/>
                    "columns": { "id":"从表关联主键字段", "excel列名": "表列名", }<br/>  }<br/>
                    defaults 配置的默认属性在导入文章时会填充到文章数据中
                    <br/>
                    columns格式 <i>属性:表字段名称</i>;
                  注意：word导入字段只支持docFileName、docFileContent两个字段，word导入不支持从表导入; <br/>
                  excel导入字段没有限制，支持从表导入，所以JSON前后需要加[]<br/>
                  word可以通过组件上的columns属性扩充，实现动态传值<br/>
                  业务表中的必填项必须采用defaults属性配置对应默认值，否则导致业务数据错误。
                </div>
            </el-form-item>
        </el-form>
    </el-main>
</div>
</body>
</html>
<script>
    Vue.use(VueCodemirror);
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                loading: false,
                saveDisabled: false,
                //表单数据
                form: {
                    // 导入导出标识
                    name: '',
                    // 导出sql配置
                    exportSql: '',
                    // 导入主表json
                    importJson: '',
                },
                rules: {
                    // 导入导出标识
                    name: [{"required": true, "message": "导入导出标识不能为空"}, {
                        "min": 0,
                        "max": 20,
                        "message": "导入导出标识长度必须为0-20"
                    }],
                    // 导出sql配置
                    exportSql: [{"required": true, "message": "导出sql配置不能为空"}],
                    // 导入主表json
                    importJson: [{"required": true, "message": "导入主表json不能为空"}],
                },
                //设置
                cmOption: {
                    tabSize: 4,
                    styleActiveLine: true,
                    lineNumbers: true,
                    line: true,
                    styleSelectedText: true,
                    lineWrapping: true,
                    mode: 'text/javascript',
                    matchBrackets: true,
                    showCursorWhenSelecting: true,
                    hintOptions: {
                        completeSingle: false
                    }
                },

            }
        },
        watch: {},
        computed: {},
        methods: {
            save: function () {
                var that = this;
                var url = ms.manager + "/impexp/set/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/impexp/set/update.do";
                }
                this.$refs.form.validate(function (valid) {
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
                                location.href = ms.manager + "/impexp/set/index.do";
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

            //获取当前导入导出配置
            get: function (id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/impexp/set/get.do", {"id": id}).then(function (res) {
                    that.loading = false
                    if (res.result && res.data) {
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created: function () {
            var that = this;
            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
            this.rules.name.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/impexp/set/verify.do", {
                        fieldName: "name",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function (res) {
                        if (res.result) {
                            if (!res.data) {
                                callback("导入导出标识已存在");
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
<style>
    .CodeMirror {
        border: 1px solid #eee;
        height: auto;
    }

    .CodeMirror-scroll {
        overflow-y: hidden;
        overflow-x: auto;
    }
</style>
