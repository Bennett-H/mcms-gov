<!DOCTYPE html>
<html>
<head>
    <title>应用</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="form" v-loading="loading" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
        </el-button>
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">
            <el-form-item v-if="form.parentId != 0" label="上级站点" prop="parentId">
                <ms-tree-select :props="{value: 'id',label: 'appName',children: 'children'}"
                             :options="treeList[0].children"
                             v-model="form.parentId"></ms-tree-select>
            </el-form-item>
            <el-form-item label="站点名称" prop="appName">
                <el-input
                        v-model="form.appName"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        maxlength="18"
                        placeholder="请输入站点名称">
                </el-input>
            </el-form-item>
            <el-row
                    :gutter="0"
                    justify="start" align="top">
                <el-col :span="12">
                    <el-form-item label="网站状态" prop="appState">
                        <el-radio-group v-model="form.appState"
                                        :style="{width: ''}"
                                        :disabled="false">
                            <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.value"
                                      v-for='(item, index) in appStateOptions' :key="item.value + index">
                                {{item.label}}
                            </el-radio>
                        </el-radio-group>
                        <div class="ms-form-tip">
                            可以控制网站的运行状态，如果网站已停止，预览用户会看网站暂停的相关信息。
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="网站生成目录" prop="appDir">
                        <el-input
                                v-model="form.appDir"
                                :disabled="false"
                                :readonly="false"
                                :style="{width:  '100%'}"
                                :clearable="false"
                                placeholder="请输入网站生成目录">
                        </el-input>
                        <div class="ms-form-tip">
                            application.yml中配置ms.html-dir了父目录,这里配置的是站点再父目录中生成的文件夹，建议不同站设置成不同的目录
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-form-item label="网站Logo" prop="appLogo">
                <el-upload
                        :file-list="form.appLogo"
                        :action="ms.manager+'/file/upload.do'"
                        :on-remove="appLogohandleRemove"
                        :style="{width:''}"
                        :limit="1"
                        :on-exceed="appLogohandleExceed"
                        :disabled="false"
                        :data="{uploadPath:'/basic/app','isRename':true}"
                        :on-success="appLogoSuccess"
                        accept="image/*"
                        list-type="picture-card">
                    <i class="el-icon-plus"></i>
                    <div slot="tip" class="el-upload__tip">最多上传1张图片</div>
                </el-upload>
                <div class="ms-form-tip">
                    支持jpg,png格式
                </div>
            </el-form-item>
            <el-form-item label="域名" prop="appUrl">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        :readonly="false"
                        v-model="form.appUrl"
                        :style="{width: '100%'}"
                        minlength="10"
                        maxlength="150"
                        placeholder="请输入正确域名，必须要加http://">
                </el-input>
                <div class="ms-form-tip">
                    必须http或https打头,这里的域名决定了具体的前台及后台的访问地址,结尾必须/结束,不存在多余路径,如：http://localhost:8080/ <br/>
                    只能存在一个域名,不支持多个域名
                </div>
            </el-form-item>
            <el-form-item label="关键字" prop="appKeyword">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        :readonly="false"
                        v-model="form.appKeyword"
                        :style="{width: '100%'}"
                        maxlength="200"
                        placeholder="请输入关键字">
                </el-input>
            </el-form-item>
            <el-form-item label="描述" prop="appDescription">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        :readonly="false"
                        v-model="form.appDescription"
                        :style="{width: '100%'}"
                        maxlength="200"
                        placeholder="请输入描述">
                </el-input>
            </el-form-item>
            <el-form-item label="版权信息" prop="appCopyright">
                <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        :readonly="false"
                        v-model="form.appCopyright"
                        :style="{width: '100%'}"
                        maxlength="200"
                        placeholder="请输入版权信息">
                </el-input>
            </el-form-item>

        </el-form>

    </el-main>
</div>
</body>
</html>
<script>
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                loading: false,
                saveDisabled: false,
                sourceList: [],
                treeList: [{
                    children: []
                }
                ],
                //表单数据
                form: {
                    //parentId:'0',
                    // 站点名称
                    appName: '',
                    // 网站状态
                    appState: '0',
                    // 移动站状态
                    appMobileState: '0',
                    // 站点风格
                    appStyle: '',
                    // 网站Logo

                    appLogo: [],
                    // 每年续费日期
                    appPayDate: '',
                    // 域名
                    appUrl: '',
                    // 关键字
                    appKeyword: '',
                    // 描述
                    appDescription: '',
                    // 版权信息
                    appCopyright: '',
                    // 续费清单
                    appPay: '',
                    // 网站生成目录
                    appDir: '',
                },
                appStateOptions: [{"value": "0", "label": "运行"}, {"value": "1", "label": "停止"}],
                appMobileStateOptions: [{"value": "0", "label": "启用"}, {"value": "1", "label": "禁用"}],
                appStyleOptions: [],
                rules: {
                    // 站点名称
                    appName: [{"required": true, "message": "站点名称不能为空"}],
                    appStyle: [{"required": true, "message": "站点模板不能为空"}],
                    appUrl: [{"required": true, "message": "域名必须填写"}, {
                        "min": 10,
                        "max": 150,
                        "pattern": /^http(s)?:\/\/[a-zA-Z0-9.]+(\:\d+)?/,
                        "message": "请检查域名格式是否正确"
                    }],
                    // 网站生成目录
                    appDir: [
                        {"required": true, "message": "网站生成目录不能为空"},
                        {"min": 1, "max": 50, "message": "网站生成目录长度必须为1-50"},
                        {
                            "pattern": /^[^[!@#$"'%^&*()_+-/~?！@#￥%…&*（）——+—？》《：“‘’]+$/,
                            "message": "网站生成目录格式不匹配"
                        }
                    ],
                },

            }
        },
        watch: {},
        computed: {},
        methods: {
            save: function () {
                console.log(this.form.appLogo);
                var that = this;
                var url = ms.manager + "/site/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/site/update.do";
                }
                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var form = JSON.parse(JSON.stringify(that.form));
                        if (form.appLogo) {
                            form.appLogo = JSON.stringify(form.appLogo);
                        }
                        //上级站点
                        if (form.id && form.id == form.parentId) {
                            that.$notify({
                                title: "成功",
                                message: '上级站点不能为自身',
                                type: 'warning'
                            })
                            that.saveDisabled = false;
                            return
                        }
                        if (!ms.util.childValidate(that.sourceList, form.id, form.parentId, "id", "parentId")) {
                            that.$notify({
                                title: "错误",
                                message: '上级站点不能选择子集',
                                type: 'warning'
                            })
                            that.saveDisabled = false;
                            return
                        }
                        ms.http.post(url, form).then(function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: "成功",
                                    message: "保存成功",
                                    type: 'success'
                                });
                                location.href = ms.manager + "/site/index.do";
                            } else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }

                            that.saveDisabled = false;
                        }).catch(function (err) {
                            //console.err(err);
                            that.saveDisabled = false;
                        });
                    } else {
                        return false;
                    }
                })
            },

            //获取当前应用
            get: function (appId) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/basic/app/get.do",{appId: appId}).then(function (res) {
                    that.loading = false
                    if (res.result && res.data) {
                        if (res.data.appLogo) {
                            res.data.appLogo = JSON.parse(res.data.appLogo);
                            res.data.appLogo.forEach(function (value) {
                                value.url = ms.base + value.path
                            })
                        } else {
                            res.data.appLogo = []
                        }
                        that.form = res.data;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            getTop:function() {
                var that = this;

                ms.http.get(ms.manager + "/site/getTop.do").then(function (res) {

                    if (res.result && res.data) {
                        that.treeList[0].id = res.data.id;
                        that.treeList[0].appName = res.data.appName;
                    }

                }).catch(function (err) {
                    console.log(err);
                });
            },
            //上级站点
            getTree:function() {
                var that = this;
                ms.http.get(ms.manager + "/site/list.do", {pageSize: 9999}).then(function (res) {
                    if (res.result) {
                        that.sourceList = res.data.rows;
                        that.treeList[0].children = ms.util.treeData(res.data.rows, 'id', 'parentId', 'children');
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取appStyle数据源
            appStyleOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/basic/template/queryAppTemplateSkin.do", {}).then(function (res) {
                    if (res.result) {
                        that.appStyleOptions = res.data.appTemplates
                    } else {
                        this.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //appLogo文件上传完成回调
            appLogoSuccess: function (response, file, fileList) {
                this.form.appLogo.push({url: file.url, name: file.name, path: response.data, uid: file.uid});
            },
            //上传超过限制
            appLogohandleExceed: function (files, fileList) {
                this.$notify({title: '当前最多上传1张图片', type: 'warning'});
            },
            appLogohandleRemove: function (file, files) {
                var index = -1;
                index = this.form.appLogo.findIndex(function (e) {
                    return e == file
                });
                if (index != -1) {
                    this.form.appLogo.splice(index, 1);
                }
            },
        },
        created: function () {
            var that = this;
            that.getTree();
            this.getTop();
            this.appStyleOptionsGet();
            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
            this.rules.appName.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/site/verify.do", {
                        fieldName: "app_name",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function (res) {
                        if (res.result) {
                            if (!res.data) {
                                callback("站点名称已存在！");
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
