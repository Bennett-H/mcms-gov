
<!DOCTYPE html>
<html>
<head>
	<title>文章主体</title>
	<#include "../../include/head-file.ftl">

</head>
<body>
<div id="main" class="ms-index" v-cloak>

	<div class="ms-search">
		<el-row>
			<el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
				<el-row>
					<el-col :span="8">
						<el-form-item  label="文章标题" prop="contentTitle">
							<el-input v-model="form.contentTitle"
									  :disabled="false"
									  :style="{width:  '100%'}"
									  :clearable="true"
									  placeholder="请输入文章标题">
							</el-input>
						</el-form-item>
					</el-col>
					<el-col :span="8">
						<el-form-item  label="文章类型" prop="contentType">
							<el-select v-model="form.contentType"
									   :style="{width: '100%'}"
									   :filterable="false"
									   :disabled="false"
									   :multiple="true" :clearable="true"
									   placeholder="请选择文章类型">
								<el-option v-for='item in contentTypeOptions' :key="item.dictValue" :value="item.dictValue"
										   :label="item.dictLabel"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="8" style="text-align: right;padding-right: 10px;">
						<el-button type="primary" icon="el-icon-search" size="mini" @click="form.sqlWhere=null;currentPage=1;list()">查询</el-button>
						<el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
					</el-col>
				</el-row>
			</el-form>
		</el-row>
	</div>

	<el-main class="ms-container">
		<el-table height="calc(100vh - 68px)"
				  v-loading="loading"
				  highlight-current-row
				  ref="multipleTable" border :data="dataList" tooltip-effect="dark"
				  @row-click="rowClick">
			<template slot="empty">
				{{emptyText}}
			</template>

			<el-table-column label="栏目名" align="left" prop="categoryId" :formatter="contentCategoryIdFormat" width="180">
			</el-table-column>
			<el-table-column label="文章标题" align="left" prop="contentTitle" show-overflow-tooltip>
			</el-table-column>
			<el-table-column label="发布时间" align="center" prop="contentDatetime" :formatter="dateFormat" width="120">
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
		el: '#main',
		data: {
			contentCategoryIdOptions: [],
			dataList: [],
			//文章列表
			content: [],
			//文章列表选中
			total: 0,
			//总记录数量
			pageSize: 10,
			//页面数量
			currentPage: 1,
			//初始页
			manager: ms.manager,
			loadState: false,
			loading: true,
			//加载状态
			emptyText: '',
			//提示文字
			contentTypeOptions: [],
			contentDisplayOptions: [{
				"value": "0",
				"label": "是"
			}, {
				"value": "1",
				"label": "否"
			}],
			//搜索表单
			form: {
				sqlWhere: null,
				// 文章标题
				contentTitle: null,
				// 文章类型
				contentType: null,
				categoryId: ''
			},
			leaf:true
		},
		methods: {
			//查询列表
			list: function () {
				var that = this;
				that.loading = true;
				that.loadState = false;
				var page = {
					pageNo: that.currentPage,
					pageSize: that.pageSize
				};
				var form = JSON.parse(JSON.stringify(that.form));

				if (form.contentType!=null && form.contentType.length > 0) {
					form.contentType = form.contentType.join(',');
				}

				for (var key in form) {
					if (!form[key]) {
						delete form[key];
					}
				}

				history.replaceState({
					form: form,
					page: page
				}, "");
				//筛选栏目类型，1=列表
				that.form.categoryType = '1';
				ms.http.post(ms.manager + "/cms/content/list.do", form.sqlWhere ? Object.assign({}, {
					categoryType: '1',
					sqlWhere: form.sqlWhere
				}, page) : Object.assign({}, form, page)).then(function (res) {
					if (that.loadState) {
						that.loading = false;
					} else {
						that.loadState = true;
					}

					if (!res.result || res.data.total <= 0) {
						that.emptyText = '暂无数据';
						that.dataList = [];
						that.total = 0;
					} else {
						that.emptyText = '';
						that.total = res.data.total;
						that.dataList = res.data.rows;
					}
				}).finally(function () {
					that.loading = false;
				});
				setTimeout(function () {
					if (that.loadState) {
						that.loading = false;
					} else {
						that.loadState = true;
					}
				}, 500);
			},
			//文章列表选中
			rowClick: function (val) {
				this.content = val;
			},
			getSelectionContents: function() {
				return this.content;
			
			},
			//表格数据转换
			contentCategoryIdFormat: function (row, column, cellValue, index) {
				var value = "";

				if (cellValue) {
					var data = this.contentCategoryIdOptions.find(function (value) {
						return value.id == cellValue;
					});

					if (data && data.categoryTitle) {
						value = data.categoryTitle;
					}
				}

				return value;
			},
			dateFormat: function (row, column, cellValue, index) {
				if (cellValue) {
					return ms.util.date.fmt(cellValue, 'yyyy-MM-dd');
				} else {
					return '';
				}
			},
			contentDisplayFormat: function (row, column, cellValue, index) {
				var value = "";

				if (cellValue) {
					var data = this.contentDisplayOptions.find(function (value) {
						return value.value == cellValue;
					});

					if (data && data.label) {
						value = data.label;
					}
				}

				return value;
			},
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
			search: function (data) {
				this.form.sqlWhere = JSON.stringify(data);
				this.list();
			},
			//重置表单
			rest: function () {
				this.form.sqlWhere = null;
				this.$refs.searchForm.resetFields();
				this.list();
			},
			//获取contentCategoryId数据源
			contentCategoryIdOptionsGet: function () {
				var that = this;
				ms.http.get(ms.manager + "/cms/category/list.do", {
					pageSize: 9999
				}).then(function (res) {
					if (res.result) {
						that.contentCategoryIdOptions = res.data.rows;
					}

					that.list();
				});
			},
			//获取contentType数据源
			contentTypeOptionsGet: function () {
				var that = this;
				ms.http.get(ms.base + '/mdiy/dict/list.do', {
					dictType: '文章属性',
					pageSize: 99999
				}).then(function (data) {
					if(data.result){
						data = data.data;
						that.contentTypeOptions = data.rows;
					}
				});
			}
		},
		mounted: function () {
			this.contentCategoryIdOptionsGet();
			this.contentTypeOptionsGet();
			this.form.categoryId = ms.util.getParameter("categoryId");
			this.leaf = ms.util.getParameter("leaf")==null?true:JSON.parse(ms.util.getParameter("leaf"));
			if (history.hasOwnProperty("state")) {
				this.form = history.state.form;
				this.currentPage = history.state.page.pageNo;
				this.pageSize = history.state.page.pageSize;
			}
		}
	});
</script>
<style>
	#main .ms-search {
		padding: 20px 0 0;
	}
	#main .ms-container {
		height: calc(100vh - 141px);
	}
	#main .el-table--enable-row-transition .el-table__body td.el-table__cell {
		cursor: pointer;
	}
	#main .el-table__body tr.current-row>td.el-table__cell {
		background-color: #409EFF;
		color: #fff;
	}

	body{
		overflow: hidden;
	}
</style>
