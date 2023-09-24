<!DOCTYPE html>
<html>

<head>
    <title>场景二维码管理</title>
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
        <el-form ref="form" :model="form" :rules="rules" label-width="140px" label-position="right" size="small">
            <!--微信编号-->
            <el-form-item label="微信编号" prop="weixinId">
                <el-input
                        v-model="form.weixinId"
                        :disabled="true"
                        :readonly="true"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入微信编号">
                </el-input>
            </el-form-item>
            <!--场景值id(名称)-->
            <el-form-item  label="场景值id(名称)" prop="qcSceneStr">
                <el-input
                        v-model="form.qcSceneStr"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入场景值id(名称)">
                </el-input>
            </el-form-item>
            <!--二维码类型-->
            <el-form-item label="二维码类型" prop="qcActionName">
                <el-radio-group v-model="form.qcActionName"
                                :style="{width: ''}"
                                :disabled="false">
                    <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.value"
                              v-for='(item, index) in qcActionNameOptions' :key="item.value + index">
                        {{item.label}}
                    </el-radio>
                </el-radio-group>
                <div class="ms-form-tip">
                    字符串参数值
                </div>
            </el-form-item>
            <!--二维码有效期-->
            <el-form-item label="二维码有效期" prop="qcExpireSeconds">
                <el-input
                        v-model="form.qcExpireSeconds"
                        :disabled="disabled"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入二维码有效期">
                </el-input>
                <div class="ms-form-tip">
                    该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为60秒
                </div>
            </el-form-item>
            <!--实现类bean名称-->
            <el-form-item label="实现类bean名称" prop="qcBeanName">
                <el-input
                        v-model="form.qcBeanName"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入实现类bean名称">
                </el-input>
                <div class="ms-form-tip">
                    公众号二维码在用户扫码后关注的事件处理bean名称，需要有对应实现类
                </div>
            </el-form-item>
        </el-form>
    </el-main>
</div>
</body>

</html>

<script>
    var formVue = new Vue({
        el: '#form',
        data: function () {
            return {
                disabled: false,
                loading: false,
                saveDisabled: false,
                // 二维码类型
                qcActionNameOptions: [{"value": "QR_STR_SCENE", "label": "临时"}, {
                    "value": "QR_LIMIT_STR_SCENE",
                    "label": "永久"
                }],
                //表单数据
                form: {
                    // 微信编号
                    weixinId: '',
                    // 场景值id(名称)
                    qcSceneStr: '',
                    //二维码类型  
                    qcActionName: '',
                    // 二维码有效期
                    qcExpireSeconds: '',
                    // 实现类bean名称
                    qcBeanName:'',
                },
                rules: {
                    // 微信编号
                    weixinId: [{"min":0,"max":255,"message":"微信编号长度必须为0-255"}],
                    // 场景值id(名称)
                    qcSceneStr: [{"required":true,"message":"场景值id(名称)不能为空"},{"min":0,"max":255,"message":"场景值id(名称)长度必须为0-255"}],
                    // 二维码类型
                    qcActionName: [{"required":true,"message":"二维码类型不能为空"}],
                        // 实现类bean名称
                    qcBeanName: [{"min":0,"max":255,"message":"实现类bean名称长度必须为0-255"}],

                },

            }
        },
        watch: {
            "form.qcActionName": function (n) {
                if (this.form.qcActionName == 'QR_LIMIT_STR_SCENE') {
                    this.form.qcExpireSeconds = '';
                    this.disabled = true;
                } else {
                    this.disabled = false;
                }
            }
        },
        components: {},
        computed: {},
        methods: {
            save: function () {
                var that = this;
                var url = ms.manager + "/mweixin/qrCode/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/mweixin/qrCode/update.do";
                }
                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var form = JSON.parse(JSON.stringify(that.form));
                        ms.http.post(url, form).then(function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: "成功",
                                    message: "保存成功",
                                    type: 'success'
                                });
                                location.href = ms.manager + "/mweixin/qrCode/index.do";
                            } else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }

                            that.saveDisabled = false;
                        }).catch(function (err) {
                            console.err(err);
                            that.saveDisabled = false;
                        });
                    } else {
                        return false;
                    }
                })
            },
            //获取当前场景二维码管理
            get: function (id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/mweixin/qrCode/get.do", {"id": id}).then(function (res) {
                    that.loading = false
                    if (res.result && res.data) {

                        that.form = res.data;
                    }
                });
            },
            //二维码类型  列表格式化
            qcActionNameFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.qcActionNameOptions.find(function (value) {
                        return value.value == cellValue;
                    })
                    if (data && data.label) {
                        value = data.label;
                    }
                }
                return value;
            },
        },
        created: function () {
            var that = this;
            //加载场景值id(名称) 数据
            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
            this.rules.qcSceneStr.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/mweixin/qrCode/verify.do", {
                        qcSceneStr: value,
                        id: that.form.id,
                    }).then(function(res){
                        if (res.result) {
                            if (res.data) {
                                callback("场景值id(名称)已存在！");
                            } else {
                                callback();
                            }
                        }
                    });
                },
                trigger: ['change']
            })
        }
    });

</script>
<style>
</style>
