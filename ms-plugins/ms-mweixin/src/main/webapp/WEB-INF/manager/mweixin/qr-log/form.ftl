<!DOCTYPE html>
<html>

<head>
    <title>场景二维码日志</title>
    <#include "../../include/head-file.ftl">

</head>

<body>
	<div id="form" v-loading="loading" v-cloak>
		<el-header class="ms-header ms-tr" height="50px">
			<el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存</el-button>
			<el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
		</el-header>
		<el-main class="ms-container">
			<el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

            <!--会员openId-->
            <el-form-item label="会员openId" prop="openId">
                <el-input
                        v-model="form.openId"
                        :disabled="true"
                        :readonly="true"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入会员openId">
                </el-input>
	        </el-form-item>
            <!--场景二维码编号-->
            <el-form-item label="场景二维码编号" prop="qcId">
                <el-input
                        v-model="form.qcId"
                        :disabled="true"
                        :readonly="true"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        placeholder="请输入场景二维码编号">
                </el-input>
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
                loading: false,
                saveDisabled: false,
                //表单数据
                form: {
                    // 会员openId
                    openId: '',
                    // 微信编号
                    weixinId: '',
                    // 场景二维码编号
                    qcId: '',

                },
                rules: {
                    // 会员openId
                    openId: [{"min": 0, "max": 255, "message": "会员openId长度必须为0-255"}],
                    // 场景二维码编号
                    qcId: [{"min": 0, "max": 255, "message": "场景二维码编号长度必须为0-255"}],

                },

            }
        },
        watch: {},
        components: {},
        computed: {},
        methods: {

            save:function() {
                var that = this;
                var url = ms.manager + "/mweixin/qrLog/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/mweixin/qrLog/update.do";
                }
                this.$refs.form.validate(function(valid) {
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
                                location.href = ms.manager + "/mweixin/qrLog/index.do";
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

            //获取当前场景二维码日志
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/mweixin/qrLog/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data) {
                                                         
                        that.form = res.data;
                    }
                });
            },
        },
        created: function () {
            var that = this;

            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
        }
    });

</script>
<style>
</style>
