<!DOCTYPE html>
<html>
<head>
	<title>数据权限</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
			<el-header class="ms-header" height="50px">
				<el-col :span="12">
					<@shiro.hasPermission name="datascope:dataConfig:save">
					<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
				</@shiro.hasPermission>
					<@shiro.hasPermission name="datascope:dataConfig:del">
					<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
					</@shiro.hasPermission>
				</el-col>
			</el-header>

		<el-main class="ms-container">
			<el-alert
					class="ms-alert-tip"
					title="功能介绍"
					type="info"
					description="数据权限中的子查询依赖于数据权限配置。">
				<template slot="title">
					功能介绍 <a href='http://doc.mingsoft.net/plugs/shu-ju-quan-xian/jie-shao.html' target="_blank">开发手册</a>
				</template>
			</el-alert>
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" width="40" :selectable="isChecked"></el-table-column>
									<el-table-column label="配置名称"   align="left" prop="configName">
									</el-table-column>
				<el-table-column label="操作"  width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="datascope:dataConfig:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="datascope:dataConfig:del">
						<el-link type="primary" :underline="false" @click="del([scope.row])" v-if="scope.row.del!=3">删除</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="datascope:dataConfig:copy">
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
        //配置名称
        {'action':'and', 'field': 'CONFIG_NAME', 'el': 'eq', 'model': 'configName', 'name': '配置名称', 'type': 'input'},
        // 子查询
        {'action':'and', 'field': 'CONFIG_SUBSQL', 'el': 'eq', 'model': 'configSubsql', 'name': '子查询', 'type': 'textarea'},
        // 配置描述
        {'action':'and', 'field': 'CONFIG_DESC', 'el': 'eq', 'model': 'configDesc', 'name': '配置描述', 'type': 'textarea'},
		],
		conditions:[],
		dataList: [], //数据权限列表
		selectionList:[],//数据权限列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loading: true,//加载状态
		emptyText:'',//提示文字
		//搜索表单
		form:{
			sqlWhere:null
		},
	},
	watch:{

	},
	methods:{
		isChecked: function(row) {
			if(row.del == 3) {
				return false;
			}
			return true;
		},
	    //查询列表
	    list: function(isSearch) {
	    	var that = this;
	    	var data = {}; //搜索参数
			that.loading = true;
			var page={
				pageNo: that.currentPage,
				pageSize : that.pageSize
			}
			var form = JSON.parse(JSON.stringify(that.form))


			if(isSearch) {
				//删除空字符串
				for (key in form){
					if(form[key] === undefined || form[key] === null){
						delete  form[key]
					}
				}
				form.sqlWhere ? data = Object.assign({}, {sqlWhere: form.sqlWhere}, page) : data = Object.assign({}, form, page)
			} else {
				data = page;
			}

			history.replaceState({form:that.form,page:page},"");
			ms.http.post(ms.manager+"/datascope/dataConfig/list.do",data).then(
					function(res) {
						if (!res.result||res.data.total <= 0) {
							that.emptyText ="暂无数据"
							that.dataList = [];
							that.total = 0;
						} else {
							that.emptyText = '';
							that.total = res.data.total;
							that.dataList = res.data.rows;
						}
						that.loading = false;
					}).catch(function(err) {
                        that.loading = false;
                        console.log(err);
                    });

			},
		//数据权限列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},
		//复制
		copy: function (row) {
			var that = this;
			delete row.id;
			row.configName += Math.round(Math.random()*10000).toString();
			ms.http.post(ms.manager + "/datascope/dataConfig/save.do", row).then(function (res) {
				if (res.result) {
					that.$notify({
						title:'成功',
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
					    	ms.http.post(ms.manager+"/datascope/dataConfig/delete.do", row.length?row:[row],{
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
					    }).catch(function(err) {
							//删除如果用户取消会抛出异常，所以需要catch一下
						});
        		},
		//新增
        save:function(id){
			if(id){
				location.href=this.manager+"/datascope/dataConfig/form.do?id="+id;
			}else {
				location.href=this.manager+"/datascope/dataConfig/form.do";
			}
        },

        //pageSize改变时会触发
        sizeChange:function(pagesize) {
			this.loading = true;
            this.pageSize = pagesize;
            this.list(true);
        },
        //currentPage改变时会触发
        currentChange:function(currentPage) {
			this.loading = true;
			this.currentPage = currentPage;
            this.list(true);
        },
		search:function(data){
        	this.form.sqlWhere = JSON.stringify(data);
        	this.list(true);
		},
		//重置表单
		rest:function(){
			this.currentPage = 1;
			this.form = {
				sqlWhere:null
			};
			this.list();
		},

	},
created:function(){
            var that = this;

		if(history.state){
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
