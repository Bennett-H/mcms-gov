<!DOCTYPE html>
<html>
<head>
	<title>文件</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
			<el-header class="ms-header" height="50px">
				<el-col :span="12">
					<@shiro.hasPermission name="co:file:save">
					<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
				</@shiro.hasPermission>
					<@shiro.hasPermission name="co:file:del">
					<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
					</@shiro.hasPermission>
				</el-col>
			</el-header>
		<el-main class="ms-container">
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" width="40"></el-table-column>
                 <el-table-column label="文件名称"   align="left" prop="fileName">
                 </el-table-column>
                 <el-table-column label="文件大小"   align="left" prop="fileSize">
                 </el-table-column>
                 <el-table-column label="文件类型"   align="left" prop="fileType">
                 </el-table-column>
                 <el-table-column label="文件路径"   align="left" prop="filePath">
                 </el-table-column>
				<el-table-column label="操作"  width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="co:file:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="co:file:del">
						<el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="co:file:copy">
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
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		conditionList:[
        {'action':'and', 'field': 'file_name', 'el': 'eq', 'model': 'fileName', 'name': '文件名称', 'type': 'input'},
        {'action':'and', 'field': 'file_size', 'el': 'eq', 'model': 'fileSize', 'name': '文件大小', 'type': 'input'},
        {'action':'and', 'field': 'file_type', 'el': 'eq', 'model': 'fileType', 'name': '文件类型', 'type': 'input'},
        {'action':'and', 'field': 'file_path', 'el': 'eq', 'model': 'filePath', 'name': '文件路径', 'type': 'input'},
		],
		conditions:[],
		dataList: [], //文件列表
		selectionList:[],//文件列表选中
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
			ms.http.post(ms.manager+"/co/file/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
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
		//文件列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},
		//复制
		copy: function (row) {
			var that = this;
			delete row.id
			ms.http.post(ms.manager + "/co/file/save.do", row).then(function (res) {
				if (res.result) {
					that.$notify({
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
					    	ms.http.post(ms.manager+"/co/file/delete.do", row.length?row:[row],{
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
			if(id){
				location.href=this.manager+"/co/file/form.do?id="+id;
			}else {
				location.href=this.manager+"/co/file/form.do";
			}
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
