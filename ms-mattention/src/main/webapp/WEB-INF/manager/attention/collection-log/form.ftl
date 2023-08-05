<!DOCTYPE html>
<html>

<head>
    <title>关注记录</title>
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

            <!--文章、商品 id-->
	        <el-form-item  label="文章、商品 id" prop="dataId">
	            <el-input
                        v-model="form.dataId"
                         :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                        placeholder="请输入文章、商品 id">
                </el-input>
	        </el-form-item>   
            <!--业务类型-->
	        <el-form-item  label="业务类型" prop="dataType">
	            <el-input
                        v-model="form.dataType"
                         :disabled="false"
                          :readonly="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                        placeholder="请输入业务类型">
                </el-input>
	        </el-form-item>   
            <!--关注数-->
				<el-form-item  label="关注数" prop="dataCount">
					<el-input
							v-model.number="form.dataCount"
							:disabled="false"
							:readonly="false"
							:style="{width:  '100%'}"
							:clearable="true"
							placeholder="请输入内容">
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
        data:function() {
            return {
                loading:false,
                saveDisabled: false,
                //表单数据
                form: {
                    // 文章、商品 id
                    dataId:'',
                    // 业务类型
                    dataType:'',
                    // 关注数
                    dataCount:0,

                },
				rules:{
					// 文章、商品 id
					dataId: [{"required":true,"message":"文章、商品 id不能为空"},{"min":0,"max":255,"message":"id长度必须为0-255"}],
					// 业务类型
					dataType: [{"required":true,"message":"业务类型不能为空"},{"min":0,"max":255,"message":"业务类型长度必须为0-255"}],
					// 关注数
					dataCount: [{"type":"number","message":"关注数格式不正确"},{"required":true,"message":"关注数不能为空"},{"pattern":/^\d{1,10}$/,"message":"关注数格式不匹配"}],
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
                var url = ms.manager + "/attention/collection/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/attention/collection/update.do";
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
                                location.href = ms.manager + "/attention/collection/index.do";
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

            //获取当前关注记录
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/attention/collection/get.do", {"id":id}).then(function (res) {
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
