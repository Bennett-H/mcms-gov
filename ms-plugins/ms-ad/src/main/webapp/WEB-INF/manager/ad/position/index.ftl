<!DOCTYPE html>
<html>
<head>
	<title>广告位</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" v-cloak class="ms-index">
			<el-header class="ms-header" height="50px">
				<el-col :span="12">
					<@shiro.hasPermission name="ad:position:save">
					<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
				</@shiro.hasPermission>
					<@shiro.hasPermission name="ad:position:del">
					<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
					</@shiro.hasPermission>
				</el-col>
			</el-header>
		<div class="ms-search">
			<el-row>
				<el-form :model="form"  :rules="rules" ref="searchForm"  label-width="120px" size="mini">
							<el-row>
											<el-col :span="8">
            <el-form-item  label="广告位名称" prop="positionName">
                    <el-input v-model="form.positionName"
                          :disabled="false"
                          :style="{width:  '100%'}"
                          :clearable="true"
                          placeholder="请输入广告位名称">
                </el-input>
             </el-form-item>
											</el-col>
										<el-col :span="16" style="text-align: right;padding-right: 10px;">
												<el-button type="primary" icon="el-icon-search" size="mini" @click="loading=true;currentPage=1;list()">查询</el-button>
												<el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
										</el-col>
							</el-row>
				</el-form>
			</el-row>
		</div>
		<el-main class="ms-container">
			<el-table v-loading="loading" height="calc(100vh - 68px)" ref="multipleTable" class="ms-table-pagination" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" width="40"></el-table-column>
				<el-table-column label="编号" width="60" prop="id"></el-table-column>
                 <el-table-column width="200px" align="left" prop="positionName">
					 <template slot='header'>广告位名称
						 <el-popover placement="top-start" title="提示" trigger="hover">
							 可以通过标签快速取到广告信息
							 <i class="el-icon-question" slot="reference"></i>
						 </el-popover>
					 </template>
                 </el-table-column>
                <el-table-column label="宽度" width="80px" align="right" prop="positionWidth">
                </el-table-column>
                <el-table-column label="高度" width="80px" align="right" prop="positionHeight">
                </el-table-column>
                 <el-table-column label="广告位描述" align="left" prop="positionDesc">
                 </el-table-column>
				<el-table-column label="操作" width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="ad:position:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="ad:position:del">
						<el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
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
		<#include "/ad/position/form.ftl">
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		dataList: [], //广告位列表
		selectionList:[],//广告位列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
		//搜索表单
		form:{
			// 广告位名称
			positionName:null,
		},
		rules:{
			// 广告位名称
			positionName: [{"min":0,"max":50,"message":"广告位名称长度应在0-50之间"}]

		},
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
			history.replaceState({form:form,page:page,total:that.total},"");
			ms.http.get(ms.manager+"/ad/position/list.do",Object.assign({},that.form,page)).then(
					function(res) {
						if(that.loadState){
							that.loading = false;
						}else {
							that.loadState = true
						}
						if (!res.result||res.data.total <= 0) {
							that.emptyText = '暂无数据'
							that.dataList = [];
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
		//广告位列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},
		//删除
        del: function(row){
        	var that = this;
        	that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
					    	confirmButtonText: '确定',
					    	cancelButtonText: '取消',
					    	type: 'warning'
					    }).then(function() {
					    	ms.http.post(ms.manager+"/ad/position/delete.do", row.length?row:[row],{
            					headers: {'Content-Type': 'application/json'}
            				}).then(function(res){
		            				if (res.result) {
										that.$notify({
						     				type: 'success',
						        			message: '删除成功!'
						    			});
					    				//删除成功，刷新列表
					      				that.list();
					      			}else {
										that.$notify({
											title: '失败',
											message: res.msg,
											type: 'warning'
										});
									}
	            				});
					    }).catch(function() {
					    	that.$notify({
					        	type: 'info',
					        	message: '已取消删除'
					    	});          
				    	});	            	
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
		//重置表单
		rest:function(){
			this.currentPage = 1;
			this.$refs.searchForm.resetFields();
			this.list();
		},
	},
	created:function(){
		this.list();
	},
})
</script>
<style>
	#index .ms-search {
		padding: 20px 0 0;
	}
</style>