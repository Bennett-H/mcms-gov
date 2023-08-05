<!DOCTYPE html>
<html>
<head>
	<title>进度方案</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
			<el-header class="ms-header" height="50px">
				<el-col :span="12">
					<@shiro.hasPermission name="progress:scheme:save">
					<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
				</@shiro.hasPermission>
					<@shiro.hasPermission name="progress:scheme:del">
					<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
					</@shiro.hasPermission>
				</el-col>
			</el-header>
		<div class="ms-search">
			<el-row>
				<el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
					<el-row>
						<el-col :span="8">
							<el-form-item  label="方案名称" prop="schemeName">
								<el-input
										v-model="form.schemeName"
										:disabled="false"
										  :readonly="false"
										  :style="{width:  '100%'}"
										  :clearable="true"
										placeholder="请输入方案名称">
								</el-input>
							</el-form-item>
						</el-col>
						<el-col :span="16" style="text-align: right;">
							<el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list()">搜索</el-button>
							<el-button type="primary" icon="iconfont icon-shaixuan1" size="mini" @click="currentPage=1;$refs.search.open()">筛选</el-button>
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
					type="info"
					description="可以定义不同的进度的方案，每个进度方案定义多个进度节点，每个进度节点都会产生多条进度日志。">
				<template slot="title">
					功能介绍 <a href='http://doc.mingsoft.net/plugs/jin-du-cha-jian/jie-shao.html' target="_blank">开发手册</a>
				</template>
			</el-alert>
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" :selectable="isChecked" width="40"></el-table-column>
				<el-table-column label="方案名称" align="left" prop="sName">
					<template slot-scope="scope">
						<el-link type="primary" :underline="false" @click="detail(scope.row.id)" >{{scope.row.schemeName}}</el-link>
					</template>
				</el-table-column>
                 <el-table-column label="回调表名"   align="left" prop="schemeTable">
                 </el-table-column>
            <el-table-column label="类型"   align="left" prop="schemeType" :formatter="schemeTypeFormat">
            </el-table-column>
				<el-table-column label="操作"  width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="progress:scheme:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="progress:scheme:del">
						<el-link type="primary" :underline="false" v-if="scope.row.notDel == 0" @click="del([scope.row])">删除</el-link>
						</@shiro.hasPermission>
						<el-link type="primary" class="copyBtn" :underline="false" :data-clipboard-text="JSON.stringify(progressJson)"  @click="copyJson([scope.row])">复制菜单json</el-link>
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
		<#include "/progress/scheme/form.ftl">
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		conditionList:[
        {action:'and', field: 'scheme_name', el: 'eq', model: 'schemeName', name: '方案名称', type: 'input'},
             {action:'and', field: 'scheme_type', el: 'eq', model: 'schemeType', name: '类型', key: 'value', title: 'label', type: 'radio', multiple: false},
         {action:'and', field: 'scheme_table', el: 'eq', model: 'schemeTable', name: '回调表名', type: 'input'},
            {action:'and', field: 'create_date', el: 'eq', model: 'createDate', name: '创建时间', type: 'date'},
           {action:'and', field: 'update_date', el: 'eq', model: 'updateDate', name: '修改时间', type: 'date'},
 		],
		conditions:[],
		dataList: [], //进度方案列表
		selectionList:[],//进度方案列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
                schemeTypeOptions:[{"value":"add","label":"递增"},{"value":"set","label":"赋值"}],
		//搜索表单
		form:{
			sqlWhere:null,
			// 方案名称
			schemeName:null,
		},
		progressJson:[{
			modelIsMenu:1,
			modelTitle:"",
			modelUrl:"",
			modelChildList:[
				{
					modelIsMenu:0,
					modelTitle:"查看",
					modelUrl:"progress:progress:view"
				},
				{
					modelIsMenu:0,
					modelTitle:"新增",
					modelUrl:"progress:progress:save"
				},
				{
					modelIsMenu:0,
					modelTitle:"更新",
					modelUrl:"progress:progress:update"
				},
				{
					modelIsMenu:0,
					modelTitle:"删除",
					modelUrl:"progress:progress:del"
				}
			]
		}]
	},
	watch:{
 	},
	methods:{
		detail:function(id){
			location.href = ms.manager + "/progress/progress/index.do?id="+id
		},
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
			ms.http.post(ms.manager+"/progress/scheme/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
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
		//进度方案列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},
		isChecked: function (row) {
			return row.notDel == 0
		},
		//复制菜单
		copyJson: function (row) {
			if (!row){
				this.$notify.error({
					title: '错误',
					message: '请选择具体一条方案',
					type: 'warning'
				});
				return;
			}
			this.progressJson[0].modelTitle = row[0].schemeName;
			this.progressJson[0].modelUrl = "/progress/progress/index.do?id=" + row[0].id + "&showBack=false";
			var clipboard = new ClipboardJS('.copyBtn');
			var self = this;
			clipboard.on('success', function (e) {
				self.$notify({
					title: '提示',
					message: '菜单json已保存到剪切板，可在菜单管理中导入',
					type: 'success'
				});
				clipboard.destroy();
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
					    	ms.http.post(ms.manager+"/progress/scheme/delete.do", row.length?row:[row],{
            					headers: {
                					'Content-Type': 'application/json'
                				}
            				}).then(
	            				function(res){
		            				if (res.result) {
										that.$notify({
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
		schemeTypeFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.schemeTypeOptions.find(function(value){
					return value.value==cellValue;
				})
				if(data&&data.label){
					value = data.label;
				}
			}
            return value;
		},
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
		height: calc(100vh - 141px);
	}
</style>
