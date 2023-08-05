<#--
	页面重写： 提供单篇是否拥有查看权限，准许放行
  -->

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>文章</title>
	<#include "../../include/head-file.ftl">
	<script src="${base}/static/datascope/index.js"></script>
</head>
<body style="overflow: hidden">
<div id="index"  v-cloak>
	<!--左侧-->
	<el-container class="index-menu">
		<div class="left-tree" style="position:relative;">
		  <el-input
				size="mini"
				style="margin:12px 8px 0; width:calc(100% - 16px);"
				clearable="true"
				placeholder="请输入栏目名称"
				v-model="categoryTitle"
				@keyup.enter.native="filterTreeData()">
				  <el-button slot="append" icon="el-icon-search" @click="filterTreeData()"></el-button>
		  </el-input>
			<el-scrollbar style="height: 95%;">
				<el-tree
            ref="tree"
						:indent="5"
						v-loading="loading"
						highlight-current
						:expand-on-click-node="false"
						default-expand-all
            node-key="id"
						:empty-text="emptyText"
						:data="treeData"
						:props="defaultProps"
						@node-click="handleNodeClick"
						style="padding: 10px;height: 100%;">
						<span class="custom-tree-node" slot-scope="scope" >
							<span :style="scope.data.categoryType == '3' ? 'color: #dcdfe6' : ''" :title="scope.data.categoryTitle">{{ scope.data.categoryTitle }}</span>
						</span>
				</el-tree>
			</el-scrollbar>
		</div>
		<iframe :src="action" class="ms-iframe-style ms-loading">
		</iframe>
	</el-container>
</div>
</body>
</html>
<script>
	var indexVue = new Vue({
		el: "#index",
		data: {
      categoryTitle: '',
			action: "",
			//跳转页面
			defaultProps: {
				children: 'children',
				label: 'categoryTitle'
			},
      //所有栏目数据
      treeDataAll: [],
      //树形栏目数据
			treeData: [],
			loading: true,
			emptyText: ''
		},
		methods: {
			handleNodeClick: function (data) {
				var that = this
				//	判断点击不为列表且不为undefind（全部栏目）
				if( data.categoryType!== undefined) {
					ms.datascope({
						dataType: "管理员栏目权限",
						dataId: data.id,
						isSuper: true,
					}).then(function(datascopesModel) {
						//	false 表示没权限访
						if(!datascopesModel && data.categoryType!=1 && data.categoryType!=3) {
							that.$notify({
								title: '提示',
								message: '无访问权限',
								type: 'warning'
							});
							return
						//超级管理员与有具备权限的管理员可以看到
						} else {
							if(datascopesModel || datascopesModel.includes('cms:content:view')) {
								//	为单篇跳转
								if (data.categoryType == '2') {
									that.action = ms.manager + "/cms/content/form.do?categoryId=" + data.id + "&type=2";
								}
								//	业务没有匹配成功
							}else {
								that.$notify({
									title: '提示',
									message: '无访问权限',
									type: 'warning'
								});
								return
							}
						}

					});
				}
				if (data.categoryType == '1') {
					this.action = ms.manager + "/cms/content/main.do?categoryId=" + data.id+"&leaf="+data.leaf;
				}else if (data.id == 0){
					this.action = ms.manager + "/cms/content/main.do?leaf=false";
				}
			},
			treeList: function (id) {
				var that = this;
				this.loading = true;
				ms.http.get(ms.manager + "/cms/category/list.do").then(function (res) {

					if (!res.result || res.data.total <= 0) {
						that.emptyText = '暂无数据';
						that.treeData = [];
					} else {
						that.emptyText = '';
						// 过滤掉栏目类型为链接属性
						that.treeDataAll = res.data.rows;
						that.filterTreeData(id)
						//增加全部文章，方便文章查找
						that.treeData = [{
							id: 0,
							categoryTitle: '全部',
							children: that.treeData
						}];
					}
					that.loading = false;
				}).catch(function(err) {
					that.loading = false;
				})
			},
      //	过滤查询栏目数据
			filterTreeData: function(id) {
				var that = this
				that.treeData = []
				//	搜索关键字为空则默认为所有树形
				if(that.categoryTitle.trim()=='') {
					that.treeData = ms.util.treeData(that.treeDataAll, 'id', 'categoryId', 'children');
				}else {
					//	搜索关键字有查询值的时候
					var parentIds = []
					//	匹配中相应数据存储
					var tree = []
					//	循环匹配关键字栏目，记录匹配元素的父级ids，并返回自身
					that.treeDataAll.forEach(function(item) {
						if(item.categoryTitle.indexOf(that.categoryTitle.trim()) >= 0) {
							//	添加当前匹配栏目
							tree.push(item)
							//	当前ids为null时不添加
							if(item.parentids) {
								var ids = item.parentids.split(',')
								ids.forEach(function(ite) {
									parentIds.push(ite)
								})
							}
						}
					})
					//	循环匹配包含ids的数据
					that.treeDataAll.forEach(function(item) {
						//	ids中有匹配，且不在已添加的数据中出现
						if(parentIds.includes(item.id) && !tree.includes(item)) {
							//	添加在数据的最前面
							tree.unshift(item)
						}
					})
					that.treeData = ms.util.treeData(tree, 'id', 'categoryId', 'children');
					//增加全部文章，方便文章查找
					that.treeData = [{
						id: 0,
						categoryTitle: '全部',
						children: that.treeData
					}];

				}
				this.$nextTick().then(function(){
					if(id!=''){
						//如果新增就选中新增的
						that.$refs.tree.setCheckedKeys([id]);
						that.$refs.tree.setCurrentKey(id);
					} else {
						//默认选中第一个
						const firstNode = document.querySelector('.el-tree-node')
						firstNode.click();
					}
				})
			},
		},
		mounted: function () {
			this.action = ms.manager + "/cms/content/main.do";
			this.treeList(0);
		}
	});
</script>
<style>
  #index .el-input-group__append {
		padding: 0 8px;
		background-color: #409eff;
		color: #ecf5ff;
	}

	#index .el-input-group__append:hover {
		background-color: #66b1ff;
		color: #fff;
	}

	#index .index-menu {
		height: 100vh;
		min-height: 100vh;
		min-width: 140px;
	}
	#index .ms-iframe-style {
		width: 100%;
		height: 100%;
		border: 0;
	}

	#index .index-menu .el-main {
		padding: 0;
	}
	#index .left-tree{
		min-height: 100vh;
		background: #fff;
		width: 220px;
		border-right: solid 1px #e6e6e6;
	}

	#index .index-menu .el-main .index-menu-menu .el-menu-item {
		min-width: 140px;
		width: 100%;
	}
	#index .index-menu .el-main .index-material-item {
		min-width: 100% !important
	}
	#index .index-menu-menu-item , .el-submenu__title {
		height: 40px !important;
		line-height: 46px !important;
	}
  /* 选中样式 */
  #index .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content {
		background-color: rgb(137 140 145);
		color: #fff;
		border-radius: 2px;
	}
	body{
		overflow: hidden;
	}

</style>
