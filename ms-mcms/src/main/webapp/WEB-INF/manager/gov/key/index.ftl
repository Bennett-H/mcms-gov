<!DOCTYPE html>
<html>
<head>
	<title>生成公钥私钥</title>
	<#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">

	<el-header class="ms-header" height="50px">
		<el-col :span="24">
			<!--生成-->
			<@shiro.hasPermission name="gov:publicKey:initKey">
				<el-button type="primary" icon="el-icon-refresh" size="mini" @click="initKey()" >生成</el-button>
			</@shiro.hasPermission>
		</el-col>
	</el-header>



	<el-main class="ms-container">
		<el-alert
				class="ms-alert-tip"
				title="功能介绍"
				type="info"
				description="生成公钥私钥不具备保存功能,需要复制到自定义配置的公私钥配置中保存后才能生效">
			<template slot="title">
				功能介绍
			</template>
		</el-alert>
		<el-form ref="form" :model="form" label-width="120px" label-position="right" size="small">

			<!--公钥-->
			<el-form-item label="公钥" prop="publicKey">
				<el-input
						type="textarea" :rows="5"
						:disabled="false"
						:readonly="false"
						v-model="form.publicKey"
						:style="{width: '100%'}"
						placeholder="请输入内容">
				</el-input>
				<div class="ms-form-tip">
					前端登入时,加密的公钥,需要引入static/plugins/jsencrypt/jsencrypt.min.js使用
				</div>
			</el-form-item>

			<!--私钥-->
			<el-form-item label="私钥" prop="privateKey">
				<el-input
						type="textarea" :rows="5"
						:disabled="false"
						:readonly="false"
						v-model="form.privateKey"
						:style="{width: '100%'}"
						placeholder="请输入内容">
				</el-input>
				<div class="ms-form-tip">
					后端解密的秘钥,采用非对称加密
				</div>
			</el-form-item>


		</el-form>

	</el-main>
</div>
</body>

</html>
<script>

	var indexVue = new Vue({
		el: '#index',
		data: function () {
			return {
				//表单数据
				form: {
					// 公钥
					publicKey: '',
					// 私钥
					privateKey: '',

				},
			}
		},

		methods: {
			initKey:function() {
				var that = this;
				ms.http.get(ms.manager + "/gov/publicKey/initKey.do").then(function (res) {
					if (res.result && res.data) {

						that.form = res.data;
					}
				});
			}
		},
		created: function () {

		},

	});

</script>
<style>
	#index .iconfont{
		font-size: 12px;
		margin-right: 5px;
	}
	.el-dialog__body {
		padding: 0 20px 30px 20px;
	}
	.CodeMirror {
		border: 1px solid #eee;
	}
</style>
