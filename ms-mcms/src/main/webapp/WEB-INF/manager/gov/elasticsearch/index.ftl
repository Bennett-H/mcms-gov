<!DOCTYPE html>
<html>
<head>
	<title>ES管理</title>
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
<div id="index" class="ms-index" v-cloak>

	<el-header class="ms-header ms-tr" height="50px">
			<div style="float: left;">
				<@shiro.hasPermission name="es:create">
					<el-button type="primary" icon="el-icon-refresh" size="mini" @click="createDoc()" :loading='esCreateLoading'>创建ES库</el-button>
				</@shiro.hasPermission>
				<@shiro.hasPermission name="es:delete">
					<el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteDoc()" :loading='clearLoading'>删除ES库</el-button>
				</@shiro.hasPermission>
			</div>
			<el-button type="primary" icon="el-icon-search" size="mini" @click="esMappingInfo()" :loading='searchLoading'>查询ES库</el-button>
			<@shiro.hasPermission name="es:sync">
				<el-button type="primary" icon="el-icon-refresh" size="mini" @click="esSync()" :loading='esSyncLoading'>同步ES库</el-button>
			</@shiro.hasPermission>
	</el-header>


	<el-main class="ms-container">
		<el-alert
				class="ms-alert-tip"
				title="功能介绍"
				type="info">
			<template slot="title">
				功能介绍 <a href='http://doc.mingsoft.net/plugs/zi-dong-sheng-cheng-bian-hao/jie-shao.html' target="_blank">开发手册</a>
			</template>
			创建ES库:根据ESContentBean创建索引 </br>
			删除ES库:删除文章索引</br>
			同步ES库:把所有文章都保存到es中</br>
			查询ES库:查询文章索引的映射信息
		</el-alert>

		<div>
			<el-form :model="es" label-width="200px" label-position="left" size="small">
				<el-row :gutter="10" justify="start" align="top">
					<el-col :span="8">
						索引名称: {{es['索引名称']}}
					</el-col>
					<el-col :span="8">
						数据分片数: {{es['数据分片数']}}
					</el-col>
					<el-col :span="8">
						数据备份数: {{es['数据备份数']}}
					</el-col>
				</el-row>
				<el-row :gutter="10" justify="start" align="top" style="margin-top: 50px">
					<el-col :span="8">
						索引文章总数: {{es['索引文章总数']}}
					</el-col>
					<el-col :span="8">
						索引存储类型: {{es['索引存储类型']}}
					</el-col>
					<el-col :span="8">
						索引创建时间: {{es['索引创建时间']}}
					</el-col>
				</el-row>
			</el-form>
		</div>
	</el-main>



	<el-dialog
			:close-on-click-modal="false"
			title="查询ES库结果"
			:visible.sync="scopeDialogVisible"
			width="50%">
		<el-scrollbar style="height: 350px;">
			<#--编辑框-->
			<div :class="'file-img'">
				<el-main style="padding: 0px;">
					<codemirror ref="code" v-show="!imgFlag" v-model="searchResult" :options="cmOption" :on-success="codemirrorSuccess">
					</codemirror>
					<img :src="imgPath" v-if="imgFlag" style="max-width: 100%;">
				</el-main>
			</div>
		</el-scrollbar>

  </span>
	</el-dialog>




</div>
</body>

</html>
<script>
	Vue.use(VueCodemirror);
	var indexVue = new Vue({
		el: '#index',
		data:{
			//  loading区域
			esSyncLoading: false,
			esCreateLoading: false,
			cacheContentLoading: false,
			clearLoading: false,
			loading: false,
			searchLoading: false,
			dialogVisible: false,

			dataList: [], //文件列表
			selectionList:[],//文件列表选中
			total: 0, //总记录数量
			pageSize: 10, //页面数量
			currentPage:1, //初始页
			manager: ms.manager,
			loadState:false,
			emptyText:'',//提示文字
			es:{},
			searchResult: '',
			scopeDialogVisible: false,
			//是否显示图片
			imgFlag:false,
			//设置
			cmOption: {
				tabSize: 4,
				styleActiveLine: true,
				lineNumbers: true,
				line: true,
				styleSelectedText: true,
				lineWrapping: true,
				mode: 'css',
				matchBrackets: true,
				showCursorWhenSelecting: true,
				hintOptions: {
					completeSingle: false
				}
			},
			fileName: "",
			//表单数据
			form: {
				// 文件名称
				name: '',
				fileName: '',
				fileNamePrefix: '',
				fileContent: ''
			},
		},
		watch:{

		},
		methods:{

			codemirrorSuccess: function () {
			},


			//查询列表
			esInfo: function() {
				var that = this;
				that.loading = true
				ms.http.post(ms.manager + "/es/esInfo.do").then(function(res) {
					that.loading = false

					that.es = res.data || {};

				}).catch(function(err) {
					that.loading = false
					that.$notify({
						title: '失败',
						message: err.msg,
						type: 'error'
					});
				});
			},

			//创建索引
			createDoc: function(){
				var that = this;
				that.esCreateLoading = true
				ms.http.post(ms.manager + '/es/createDoc.do').then(function(res) {
					that.esCreateLoading = false
					if(res.result) {
						that.$notify({
							title: '成功',
							message: res.msg,
							type: 'success'
						});
						//缓存成功，刷新列表
						that.esInfo()
					} else {
						that.$notify({
							title: "错误",
							message: res.msg,
							type: 'warning'
						});
					}
				}) .catch(function(err) {
					that.esCreateLoading = false
					that.$notify({
						title: '失败',
						message: err.msg,
						type: 'error'
					});
				})
			},


			//删除索引
			deleteDoc: function(){
				var that = this;
				that.$confirm("此操作将永久删除ES索引, 是否继续", "提示", {
					confirmButtonText: "确认",
					cancelButtonText: "取消",
					type: 'warning'
				}).then(function() {
					that.clearLoading = true
					ms.http.post(ms.manager+"/es/deleteDoc.do").then(
							function(res){
								that.clearLoading = false
								if (res.result) {
									that.$notify({
										title: '成功',
										type: 'success',
										message:res.data,
									});
									//删除成功，刷新列表
									that.esInfo();
								}else {
									that.$notify({
										title: "错误",
										message: res.msg,
										type: 'warning'
									});
								}

							});
				})
			},

			// 同步ES文章
			esSync:function(){
				var that = this
				that.esSyncLoading = true
				ms.http.post(ms.manager + '/es/sync.do').then(function(res) {
					that.esSyncLoading = false
					if(res.result) {
						that.$notify({
							title: '成功',
							message: res.msg,
							type: 'success'
						});
						//缓存成功，刷新列表
						that.esInfo()
					} else {
						that.$notify({
							title: "错误",
							message: res.msg,
							type: 'warning'
						});
					}
				}) .catch(function(err) {
					that.esSyncLoading = false
					that.$notify({
						title: '失败',
						message: err.msg,
						type: 'error'
					});
				})
			},

			//  查询ES Mappings
			esMappingInfo: function() {
				var that = this
				that.searchLoading = true
				ms.http.post(ms.manager + '/es/esMappingInfo.do').then(function(res) {

					that.searchLoading = false
					if(res.result) {
						that.searchResult = JSON.stringify(res.data);
						that.$notify({
							title: '成功',
							message: '查询成功!',
							type: 'success'
						});
						that.scopeDialogVisible = true
					}else {
						that.$notify({
							title: '失败',
							message: res.msg,
							type: 'warning'
						});
					}
				}).catch(function(err) {
					that.$notify({
						title: '失败',
						message: err.msg,
						type: 'warning'
					});
					that.searchLoading = false
				})
			},


		},
		created:function(){
			var that = this;
			that.esInfo();
		},
	})
</script>
<style>
	#index .ms-container {
		height: calc(100vh - 78px);
	}

</style>
