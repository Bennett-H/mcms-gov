<!DOCTYPE html>
<html>
<head>
	<title>敏感词</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
		<el-header class="ms-header" height="50px">
			<el-col :span="12">
				<@shiro.hasPermission name="wordfilter:sensitiveWords:save">
				<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
			</@shiro.hasPermission>
				<@shiro.hasPermission name="wordfilter:sensitiveWords:del">
				<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
				</@shiro.hasPermission>
			</el-col>
		</el-header>
		<div class="ms-search">
			<el-row>
				<el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
					<el-row>
						<el-col :span="8">
							<el-form-item  label="词汇类型" prop="wordType">
								<el-select v-model="form.wordType"
										   :style="{width: '100%'}"
										   :filterable="false"
										   :disabled="false"
										   :multiple="false" :clearable="true"
										   placeholder="请选择词汇类型">
									<el-option v-for='item in wordTypeOptions' :key="item.value"
											   :value="item.value"
											   :label="item.label"></el-option>
								</el-select>
							</el-form-item>
						</el-col>
						<el-col :span="8">
							<el-form-item  label="检测词" prop="word">
								<el-input
										v-model="form.word"
										:disabled="false"
										:readonly="false"
										:style="{width:  '100%'}"
										:clearable="true"
										placeholder="请输入检测词">
								</el-input>
							</el-form-item>
						</el-col>
						<el-col :span="8" style="text-align: right;">
							<el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list()">搜索</el-button>
							<el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
						</el-col>
					</el-row>
				</el-form>
			</el-row>
		</div>
		<el-main class="ms-container">
			<el-alert
					class="ms-alert-tip"
					title="功能介绍"
					type="info">
				<template slot="title">
					功能介绍 <a href='http://doc.mingsoft.net/plugs/min-gan-ci-cha-jian/ye-wu-kai-fa.html' target="_blank">开发手册</a>
				</template>
				敏感词: 将内容中出现的敏感词替换成指定的内容，例如：毛泽东 可替换 *** 或 毛主席</br>
				纠错词: 通过百度ai错词检测,检测内容中的易错词
			</el-alert>
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" width="40"></el-table-column>
                 <el-table-column label="检测词"   align="left" prop="word">
                 </el-table-column>
                 <el-table-column label="替换词"   align="left" prop="replaceWord">
                 </el-table-column>
				<el-table-column label="词汇类型" :formatter="wordTypeFormat" align="left" prop="wordType">
				</el-table-column>
				<el-table-column label="操作"  width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="wordfilter:sensitiveWords:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="wordfilter:sensitiveWords:del">
						<el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="wordfilter:sensitiveWords:copy">
						<el-link type="primary" :underline="false" @click="copy(scope.row)">复制</el-link>
						</@shiro.hasPermission>
					</template>
				</el-table-column>
			</el-table>
            <el-pagination
					background
					:page-sizes="[10,20,30,40,50,100]"
					layout="total, sizes, prev, pager, next, jumper"
					:current-page="currentPage"
					:page-size="pageSize"
					:total="total"
					class="ms-pagination"
					@current-change='currentChange'
					@size-change="sizeChange">
            </el-pagination>
         </el-main>
	</div>
		<#include "/wordfilter/sensitive-words/form.ftl">
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		conditionList:[],
		conditions:[],
		dataList: [], //检测词列表
		selectionList:[],//检测词列表选中
		// 词汇类型数组
		wordTypeOptions:[{"value":"sensitive","label":"敏感词"},{"value":"correction","label":"纠错词"}],
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
		//搜索表单
		form:{
			sqlWhere:null,
		},
	},
	watch:{
	},
	methods:{
	    //查询列表
	    list: function() {
	    	var that = this;
			that.loading = true;
			that.loadState = false;
			var page={
				pageNo: that.currentPage,
				pageSize : that.pageSize
			}
			var form = JSON.parse(JSON.stringify(that.form))
			for (key in form){
				if(!form[key]){
					delete  form[key]
				}
			}
			history.replaceState({form:form,page:page},"");
			ms.http.post(ms.manager+"/wordfilter/sensitiveWords/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
				:Object.assign({},form, page)).then(
					function(res) {
						if(that.loadState){
							that.loading = false;
						}else {
							that.loadState = true
						}
						if (!res.result||res.data.total <= 0) {
							that.emptyText ="暂无数据"
							that.dataList = [];
							that.total = 0;
						} else {
							that.emptyText = '';
							that.total = res.data.total;
							that.dataList = res.data.rows;
						}
					}).catch(function(err) {
				console.log(err);
			});
			setTimeout(function(){
				if(that.loadState){
					that.loading = false;
				}else {
					that.loadState = true
				}
			}, 500);
			},
		//敏感词列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},
		//复制
		copy: function (row) {
			var that = this;
			delete row.id
			row.word += Math.round(Math.random()*10000).toString()
			ms.http.post(ms.manager + "/wordfilter/sensitiveWords/save.do", row).then(function (res) {
				if (res.result) {
					that.$notify({
						title: "提示",
						type: 'success',
						message: "复制成功"
					});
					//复制成功，刷新列表
					that.list();
				} else {
					that.$notify({
						title: "错误",
						message: res.msg,
						type: 'warning'
					});
				}
			});
		},
		//删除
        del: function(row){
        	var that = this;
        	that.$confirm("此操作将永久删除所选内容, 是否继续", "提示", {
					    	confirmButtonText: "确认",
					    	cancelButtonText: "取消",
					    	type: 'warning'
					    }).then(function() {
					    	ms.http.post(ms.manager+"/wordfilter/sensitiveWords/delete.do", row.length?row:[row],{
            					headers: {
                					'Content-Type': 'application/json'
                				}
            				}).then(
	            				function(res){
		            				if (res.result) {
										that.$notify({
											title: "提示",
						     				type: 'success',
						        			message:"删除成功"
						    			});
					    				//删除成功，刷新列表
					      				that.list();
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
		//新增
        save:function(id){
			form.open(id);
        },
        //表格数据转换
        //pageSize改变时会触发
        sizeChange:function(pagesize) {
			this.loading = true;
            this.pageSize = pagesize;
            this.list();
        },
        //currentPage改变时会触发
        currentChange:function(currentPage) {
			this.loading = true;
			this.currentPage = currentPage;
            this.list();
        },
		search:function(data){
        	this.form.sqlWhere = JSON.stringify(data);
        	this.list();
		},
		//重置表单
		rest:function(){
			this.currentPage = 1;
			this.form.sqlWhere = null;
			this.$refs.searchForm.resetFields();
			this.list();
		},
		// 词汇类型数据转换
		wordTypeFormat: function (row, column, cellValue) {
			var value = "";

			if (cellValue) {
				var data = this.wordTypeOptions.find(function (value) {
					return value.value == cellValue;
				});

				if (data && data.label) {
					value = data.label;
				}
			}

			return value;
		},
	},
created:function(){
	if(history.hasOwnProperty("state")&& history.state){
		this.form = history.state.form;
		this.currentPage = history.state.page.pageNo;
		this.pageSize = history.state.page.pageSize;
	}
		this.list();
	},
})
</script>
<style>
	#index .ms-container {
		height: calc(100vh - 78px);
	}
</style>
