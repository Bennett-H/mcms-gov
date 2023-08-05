<!DOCTYPE html>
<html>
<head>
	<title>消息模板</title>
		<link rel="stylesheet" href="${base}/static/iconfont/iconfont.css"/>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" v-cloak class="ms-index">
		<el-header class="ms-header" height="50px">
			<el-col :span="12">
				<@shiro.hasPermission name="sendTemplate:save">
					<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
				</@shiro.hasPermission>
				<@shiro.hasPermission name="sendTemplate:del">
					<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
				</@shiro.hasPermission>
			</el-col>
		</el-header>
		<div class="ms-search" style="padding: 20px 10px 0 10px;">
			<el-row>
				<el-form :model="searchForm"  ref="searchForm"  label-width="60px" size="mini">
					<el-row>
						<el-col :span="8">
							<el-form-item  label="标题" prop="templateTitle">
								<el-input v-model="searchForm.templateTitle"
										  :disabled="false"
										  :style="{width:  '100%'}"
										  :clearable="true"
										  placeholder="请输入标题">
								</el-input>
							</el-form-item>
						</el-col>
						<el-col :span="16" style="text-align: right">
							<el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list()">查询</el-button>
							<el-button @click="clear"  icon="el-icon-refresh" size="mini">重置</el-button>
						</el-col>
					</el-row>
				</el-form>
			</el-row>
		</div>
		<div class="ms-form-tip">
			<el-alert
					title="功能介绍"
					type="info"
					description="用于重要信息变更验证。支持通过邮件、短信方式进行消息推送，也支持第三方平台sendcloud">
				<template slot="title">
					功能介绍 <a href='http://doc.mingsoft.net/plugs/fa-song-cha-jian/jie-shao.html' target="_blank">开发手册</a>
				</template>
			</el-alert>
		</div>
		<el-main class="ms-container" style="height: calc(100vh - 141px);">
			<el-table  height="calc(100% - 68px)" ref="multipleTable" class="ms-table-pagination" border :data="treeList" tooltip-effect="dark" @selection-change="handleSelectionChange" :default-sort = "{prop: 'templateCode', order: 'ascending'}">
				<el-table-column type="selection" width="40"></el-table-column>
				<el-table-column label="标题" prop="templateTitle" width="300">
				</el-table-column>
				<el-table-column label="模板编码" prop="templateCode" sortable>
				</el-table-column>
				</el-table-column>
				<@shiro.hasPermission name="sendTemplate:update">
				<el-table-column label="操作" align="center" width="180">
					<template slot-scope="scope">
						<el-link :underline="false" type="primary" @click="save(scope.row.id)">编辑</el-link>
                        <@shiro.hasPermission name="sendTemplate:del">
                        <el-link :underline="false" type="primary" @click="del([scope.row])">删除</el-link>
                        </@shiro.hasPermission>
                    </template>
				</el-table-column>
				</@shiro.hasPermission>
			</el-table>
            <el-pagination
					background
					:page-sizes="[20, 50, 100]"
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
	data:function(){
		return {
			treeList: [], //消息模板列表
			selectionList:[],//消息模板列表选中
			total: 0, //总记录数量
			pageSize: 20, //页面数量
			currentPage:1, //初始页
			mananger: ms.manager,
			//搜索表单
			searchForm:{
				// 标题
				templateTitle:'',
				// 邮件模版代码
				templateCode:'',
				// 邮件内容
				templateMail:'',
				// 消息内容
				templateSms:'',
			},
		}
	},
	methods:{
	    //查询列表
	    list: function() {
	    	var that = this;
			that.searchForm.pageNo = that.currentPage;
			that.searchForm.pageSize = that.pageSize;
	    	ms.http.get(ms.manager+"/msend/template/list.do",that.searchForm)
					.then(function(data){
						if(data.result){
							that.total = data.data.total;
							that.treeList = data.data.rows;
						}
					}).catch(function(err){
						console.log(err);
					});
				},
		//消息模板列表选中
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
					    	ms.http.post(ms.manager+"/msend/template/delete.do", row.length?row:[row],{
            					headers: {
                					'Content-Type': 'application/json'
                				}
            				}).then(
	            				function(data){
		            				if (data.result) {
										that.$notify({
											title: '提示',
						     				type: 'success',
						        			message: '删除成功!'
						    			});
					    				//删除成功，刷新列表
					      				that.list();
					      			}else {
										that.$notify({
											title: '失败',
											message: data.msg,
											type: 'warning'
										});
									}
	            				});
					    }).catch(function() {
					    	that.$notify({
								title: '提示',
					        	type: 'info',
					        	message: '已取消删除'
					    	});
				    	});
        		},
        //新增
        save:function(id){
			if(id){
				location.href=this.mananger+"/msend/template/form.do?id="+id;
			}else {
				location.href=this.mananger+"/msend/template/form.do";
			}
        },
        //pageSize改变时会触发
        sizeChange:function(pagesize) {
            this.pageSize = pagesize;
            this.list();
        },
        //currentPage改变时会触发
        currentChange:function(currentPage) {
            this.currentPage = currentPage;
            this.list();
        },
		//重置表单
		clear:function(){
			this.$refs.searchForm.resetFields();
			this.list();
		}
	},
	mounted:function(){
		this.list();
	},
})
</script>
