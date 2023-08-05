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
<div id="index" class="ms-index" v-cloak >
	<el-main class="ms-container"  >
		<el-alert
				class="ms-alert-tip"
				title="功能介绍"
				type="info">
			<template slot="title">
				功能介绍 <a href='http://doc.mingsoft.net/plugs/zi-dong-sheng-cheng-bian-hao/jie-shao.html' target="_blank">开发手册</a>
			</template>
			创建ES库:根据ESContentBean创建索引 </br>
			删除ES库:删除文章索引</br>
			同步ES库:把所有文章都保存到es中,同步文章时先创建es索引,避免索引不正确</br>
			查询ES库:查询文章索引的映射信息
		</el-alert>

		<div v-loading="loading">
			<template v-if="dictList == null">
				<el-empty description="暂无ES库"></el-empty>
			</template>
			<template  v-else >
				<template v-for='(item,i) in dictList' :key="'es-'+i">
					<div style="border-bottom: 2px dashed #eee;margin: 20px 0;height: 1px"></div>
					<el-row type="flex">
						<@shiro.hasPermission name="gov:es:create">
							<el-button type="primary" icon="el-icon-plus" size="mini" @click="createDoc(item.dictLabel)" style="margin-right: 8px">创建ES库</el-button>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="gov:es:sync">
							<el-button type="success" icon="el-icon-refresh" size="mini" @click="esSync(item.url)"  style="margin-right: 8px">同步ES库</el-button>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="gov:es:delete">
							<el-button type="danger" icon="el-icon-delete"  size="mini" @click="deleteDoc(item.dictLabel)" style="margin-right: 8px">删除ES库</el-button>
						</@shiro.hasPermission>
						<el-button  icon="el-icon-search" size="mini" @click="esMappingInfo(item.dictLabel)" style="margin-right: 8px">查询ES库</el-button>
					</el-row>

					<el-row style="margin-top: 8px">
						<el-descriptions :title="'ES库：'+ item.dictLabel">
							<el-descriptions-item label="索引名称">{{item.indexName}}</el-descriptions-item>
							<el-descriptions-item label="数据分片数">{{item.numberOfShards}}</el-descriptions-item>
							<el-descriptions-item label="数据备份数">{{item.numberOfReplicas}}</el-descriptions-item>
							<el-descriptions-item label="索引文章总数">{{item.count}}</el-descriptions-item>
							<el-descriptions-item label="索引存储类型">{{item.storeType}}</el-descriptions-item>
							<el-descriptions-item label="索引创建时间">{{item.indexCreateDate}}</el-descriptions-item>
						</el-descriptions>

					</el-row>

				</template>
			</template>
		</div>

	</el-main>

	<el-dialog
			:close-on-click-modal="false"
			:visible.sync="dialogVisible"
			title="查询ES库结果"
			width="80%">
		<el-scrollbar style="height: 550px;">
			<#--编辑框-->
				<codemirror v-model="esMappingJson" :options="codemirrorOptions" style="height: 100%">
				</codemirror>
		</el-scrollbar>
	</el-dialog>

</div>
</body>

</html>
<script>
	Vue.use(VueCodemirror);
	var indexVue = new Vue({
		el: '#index',
		data:{
			loading: true,
			// es索引
			dictList: null,
			esMappingJson: null,
			dialogVisible: false,
			//设置
			codemirrorOptions: {
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
		},
		watch:{

		},
		methods:{
			//创建索引
			createDoc: function(docName){
				var that = this;
				that.loading = true
				ms.http.post(ms.manager + '/es/createDoc.do', {docName:docName}).then(function(res) {
					if(res.result) {
						that.$notify({
							title: '成功',
							message: res.msg,
							type: 'success'
						});
						//缓存成功，刷新列表
						that.getDictList()
					} else {
						that.$notify({
							title: "错误",
							message: res.msg,
							type: 'warning'
						});
					}
					that.loading = false;
				});
			},


			//删除索引
			deleteDoc: function(docName){
				var that = this;
				that.$confirm("此操作将永久删除ES索引, 是否继续", "提示", {
					confirmButtonText: "确认",
					cancelButtonText: "取消",
					type: 'warning'
				}).then(function() {
					that.loading = true
					ms.http.post(ms.manager+"/es/deleteDoc.do", {docName:docName}).then(
							function(res){
								that.loading = false
								if (res.result) {
									that.$notify({
										title: '成功',
										type: 'success',
										message:res.msg,
									});
									//删除成功，刷新列表
									that.getDictList();
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
			esSync:function(url){
				var that = this
				that.esSyncLoading = true
				that.loading = true
				ms.http.post(ms.manager + url).then(function(res) {
					that.esSyncLoading = false
					that.loading = false
					if(res.result) {
						that.$notify({
							title: '成功',
							message: res.msg,
							type: 'success'
						});
						//缓存成功，刷新列表
						that.getDictList();
					} else {
						that.$notify({
							title: "错误",
							message: res.msg,
							type: 'warning'
						});
						that.loading = false
					}
				})
			},

			//  查询ES Mappings
			esMappingInfo: function(docName) {
				var that = this
				that.loading = true
				ms.http.post(ms.manager + '/es/esMappingInfo.do', {docName:docName}).then(function(res) {
					if(res.result) {
						that.esMappingJson = JSON.stringify(res.data,null,4);
						that.dialogVisible = true
					}else {
						that.$notify({
							title: '失败',
							message: res.msg,
							type: 'warning'
						});
					}
					that.loading = false
				})
			},

			 getDictList:  function () {
				var that = this;
				that.dictList = [];
				ms.http.get(ms.base + '/mdiy/dict/list.do', {
					dictType: 'ES索引',
					pageSize: 99999
				}).then(function (res) {
					if(res.result){
						var dicts = res.data.rows;
						var i;
						for(i = 0; i < dicts.length; i++) {
							//es配置参数
							var esConfig = JSON.parse(dicts[i].dictValue);
							dicts[i] = Object.assign(dicts[i], esConfig);
							 //标记索引
							 var index = 0;
							 ms.http.post(ms.manager + "/es/esInfo.do", {docName:dicts[i].dictLabel}).then(function(_res) {
								if (_res.result) {
									//组装json
									var dict = Object.assign(dicts[index++], _res.data);
									that.dictList.push(dict);
								}else {
									that.dictList.push(dicts[index++]);
								}
							});
							that.loading = false;
						}
					}
				})
			},

		},
		created:function(){
			this.getDictList();
		},
	})
</script>
<style>
	#index .ms-container {
		height: calc(100vh - 78px);
	}

	#index .CodeMirror {
		height: auto;
	}


</style>
