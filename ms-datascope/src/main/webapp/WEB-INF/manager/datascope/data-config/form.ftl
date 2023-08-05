<!DOCTYPE html>
<html>

<head>
    <title>数据权限</title>
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

            <!--配置名称-->
	        <el-form-item  label="配置名称" prop="configName">
	            <el-input
                        v-model="form.configName"
						:disabled="form.id>0"
						:readonly="false"
						:style="{width:  '100%'}"
						:clearable="true"
						maxlength="25"
                        placeholder="请输入配置名称">
                </el-input>
                <div class="ms-form-tip">
					数据权限标识不能重复，业务代码中需要用到。新增后不能修改
				</div>
	        </el-form-item>
        <!--子查询-->
	        <el-form-item  label="子查询" prop="configSubsql">
	            <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        :readonly="false"
                        v-model="form.configSubsql"
                        :style="{width: '100%'}"
                        placeholder="请输入子查询">
                </el-input>
                <div class="ms-form-tip">
填写sql语句                </div>
	        </el-form-item>

        <!--配置描述-->
	        <el-form-item  label="配置描述" prop="configDesc">
	            <el-input
                        type="textarea" :rows="5"
                        :disabled="false"
                        :readonly="false"
                        v-model="form.configDesc"
                        :style="{width: '100%'}"
                        placeholder="请输入配置描述">
                </el-input>
                <div class="ms-form-tip">
用于描述此条配置的意义                </div>
	        </el-form-item>

			</el-form>
		</el-main>
	</div>
</body>

</html>

<script>
    var form = new Vue({
        el: '#form',
        data:function() {
            return {
                loading:false,
                saveDisabled: false,
                //表单数据
                form: {
                    // 配置名称
                    configName:'',
                    // 子查询
                    configSubsql:'',
                    // 配置描述
                    configDesc:'',

                },
                rules:{
                        // 配置名称
                        configName: [{"required":true,"message":"配置名称不能为空"},{"min":0,"max":25,"message":"配置名称长度必须为0-25"}]
                },

            }
        },
        watch:{

        },
        components:{
        },
        computed:{
        },
        methods: {

            save:function() {
                var that = this;
                var url = ms.manager + "/datascope/dataConfig/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/datascope/dataConfig/update.do";
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
                                location.href = ms.manager + "/datascope/dataConfig/index.do";
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

            //获取当前数据权限
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/datascope/dataConfig/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data) {

                        that.form = res.data;
                    }
                });
            },
        },
        created:function() {
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
