<!-- 增加管理员锁定与解锁功能 -->
<!DOCTYPE html>
<html>
<head>
	<title>管理员管理</title>
	<#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">
	<el-header class="ms-header" height="50px">
		<el-col :span="12">
			<@shiro.hasPermission name="basic:manager:save">
				<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
			</@shiro.hasPermission>
			<@shiro.hasPermission name="basic:manager:del">
				<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
			</@shiro.hasPermission>
		</el-col>
	</el-header>
	<div class="ms-search" style="padding: 20px 10px 0 10px;">
		<el-row>
			<el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
				<el-row>
					<el-col :span="8">
						<el-form-item  label="管理员账号" prop="managerName">
							<el-input v-model="form.managerName"
									  :disabled="false"
									  :clearable="true"
									  placeholder="请输入管理员账号">
							</el-input>
						</el-form-item>
					</el-col>
					<el-col :span="8">
						<el-form-item  label="管理员昵称" prop="managerNickName">
							<el-input v-model="form.managerNickName"
									  :disabled="false"
									  :clearable="true"
									  placeholder="请输入管理员昵称">
							</el-input>
						</el-form-item>
					</el-col>
					<el-col :span="8" style="text-align: right">
						<el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list()">查询</el-button>
						<el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
					</el-col>
				</el-row>
			</el-form>
		</el-row>
	</div>
	<el-main class="ms-container">
		<el-table v-loading="loading"  height="calc(100vh - 68px)" ref="multipleTable" class="ms-table-pagination" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
			<template slot="empty">
				{{emptyText}}
			</template>
			<el-table-column type="selection" :selectable="isChecked" width="40" ></el-table-column>
			<el-table-column label="账号" min-width="120" align="left" prop="managerName" show-overflow-tooltip>
				<template slot-scope="scope">
					{{ scope.row.managerName }}
					<el-tag size="medium" v-if="scope.row.managerLock==='lock'" type="warning">已锁定</el-tag>
					<el-tag size="medium" v-if="scope.row.managerAdmin == 'super'" type="success">超级管理员</el-tag>
					<el-tag size="medium" v-if="scope.row.id == manager.id" type="primary">当前登陆管理员</el-tag>
				</template>
			</el-table-column>
			<el-table-column label="昵称" min-width="120" align="left" prop="managerNickName" show-overflow-tooltip>
			</el-table-column>
			<el-table-column label="角色名称" min-width="120"  align="left" prop="roleNames" show-overflow-tooltip>
			</el-table-column>
			<el-table-column label="创建时间" width="210" align="center" prop="createDate">
			</el-table-column>
			<el-table-column label="操作" align="center" width="180">
				<template slot='header'>操作
					<el-popover placement="top-start" title="提示" trigger="hover">
						锁定：被锁定的用户不能登录到系统，反之为解锁
						<i class="el-icon-question" slot="reference"></i>
					</el-popover>
				</template>
				<template slot-scope="scope">
					<@shiro.hasPermission name="basic:manager:update">
						<el-link type="primary" v-if="manager.id != scope.row.id && (manager.managerAdmin == 'super' || scope.row.managerAdmin != 'super')" :underline="false" @click="save(scope.row.id)">编辑</el-link>
					</@shiro.hasPermission>
					<@shiro.hasPermission name="basic:manager:del">
						<el-link type="primary" v-if="scope.row.managerAdmin != 'super' && manager.id != scope.row.id" :underline="false" @click="del([scope.row])">删除</el-link>
					</@shiro.hasPermission>
					<@shiro.hasPermission name="basic:manager:lock">
						<el-link  v-if="scope.row.managerLock==='lock'" type="primary" :underline="false" @click="lock(scope.row.id)">解锁</el-link>
						<el-link v-else type="primary" :underline="false" v-if="manager.id != scope.row.id && (manager.managerAdmin == 'super' && scope.row.managerAdmin != 'super')" @click="lock(scope.row.id)">锁定</el-link>
					</@shiro.hasPermission>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
				background
				:page-sizes="[5, 10, 20, 30, 40, 50]"
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
<#include "/basic/manager/form.ftl">
</body>

</html>
<script>
	var indexVue = new Vue({
		el: '#index',
		data: {
			dataList: [],
			//管理员管理列表
			selectionList: [],
			//管理员管理列表选中
			total: 0,
			//总记录数量
			pageSize: 20,
			//页面数量
			currentPage: 1,
			manager: null,
			loading: true,
			//加载状态
			emptyText: '',
			//提示文字
			//搜索表单
			form: {},
		},
		methods: {
			//查询列表
			list: function () {
				var that = this;
				var page = {
					pageNo: that.currentPage,
					pageSize: that.pageSize
				};
				var form = JSON.parse(JSON.stringify(that.form));

				for (var key in form) {
					if (!form[key]) {
						delete form[key];
					}
				}
				history.replaceState({
					form: form,
					page: page,
					total: that.total
				}, "");
				that.loading = true;
				ms.http.get(ms.manager + "/basic/manager/list.do", Object.assign({},that.form, page)).then(function (data) {
					that.loading = false;
					if (data.data.total <= 0) {
						that.emptyText = '暂无数据';
						that.dataList = [];
					} else {
						that.emptyText = '';
						that.total = data.data.total;
						that.dataList = data.data.rows;
					}
				});
			},
			//管理员管理列表选中
			handleSelectionChange: function (val) {
				this.selectionList = val;
			},
			//不能删除自己
			isChecked: function (row, index) {
				return !(row.managerAdmin == 'super' || this.manager.id == row.id);
			},
			//删除
			del: function (row) {
				var that = this;
				that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(function () {
					ms.http.post(ms.manager + "/basic/manager/delete.do", row.length ? row : [row], {
						headers: {
							'Content-Type': 'application/json'
						}
					}).then(function (data) {
						if (data.result) {
							that.$notify({
								title: '成功',
								type: 'success',
								message: '删除成功!'
							}); //删除成功，刷新列表

							that.list();
						} else {
							that.$notify({
								title: '失败',
								message: data.msg,
								type: 'warning'
							});
						}
					});
				})
			},
			//新增
			save: function (id) {
				form.open(id);
			},
			//表格数据转换
			//pageSize改变时会触发
			sizeChange: function (pagesize) {
				this.loading = true;
				this.pageSize = pagesize;
				this.list();
			},
			//currentPage改变时会触发
			currentChange: function (currentPage) {
				this.loading = true;
				this.currentPage = currentPage;
				this.list();
			},
			//重置表单
			rest: function () {
				this.currentPage = 1;
				this.loading = true;
				this.$refs.searchForm.resetFields();
				if (history.hasOwnProperty("state")&&history.state) {
					this.form = history.state.form;
					this.total = history.state.total;
					this.currentPage = history.state.page.pageNo;
					this.pageSize = history.state.page.pageSize;
				}
				this.list();
			},
			lock: function (id) {
				var that = this;

				ms.http.post(ms.manager + "/basic/manager/managerLock.do", {id:id}).then(function (data) {
					if (data.result) {
						if(data.data.managerLock=='lock') {
							that.$notify({
								title: '锁定',
								type: 'success',
								message: '管理员已锁定!'
							}); //状态更改成功，刷新列表
						} else {
							that.$notify({
								title: '解锁',
								type: 'success',
								message: '管理员已解锁!'
							}); //状态更改成功，
						}
						that.list();
					} else {
						that.$notify({
							title: '失败',
							message: data.msg,
							type: 'warning'
						});
					}
				});
			},
			getManager:function (){
				var that = this
				ms.http.get(ms.manager + "/basic/manager/info.do").then(function(res){
					if (res.result) {
						that.manager = res.data;
						that.list();
					}
				})
			}
		},
		created: function () {
			this.getManager();

		}
	});
</script>
<style>
</style>
