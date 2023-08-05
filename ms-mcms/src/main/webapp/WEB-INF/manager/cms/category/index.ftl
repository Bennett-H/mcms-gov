<!DOCTYPE html>
<html>
<head>
	<title>分类</title>
	<#include "../../include/head-file.ftl">
	<script src="${base}/static/plugins/clipboard/clipboard.js"></script>
</head>
<body style="overflow: hidden">
<div id="index" v-cloak>
	<!--左侧-->
	<el-container class="index-menu">
		<div class="left-tree" style="position:relative;">
			<el-row style="height:100%;">
				<el-col style="padding: 8px; height: 50px;line-height: 32px;">
					<@shiro.hasPermission name="cms:category:save">
						<el-button-group>
							<el-button type="primary" size="mini" icon="el-icon-plus" @click="add(true)">顶级</el-button>
							<el-button type="primary" size="mini" icon="el-icon-plus" @click="add(false)">子栏目</el-button>
						</el-button-group>

					</@shiro.hasPermission>
				</el-col>
				<el-col  style="padding:12px 8px 0px 8px">
					<el-input
							size="mini"
							style="width: 100%"
							placeholder="请输入栏目名称"
							v-model="categoryTitle">
						<el-button slot="append" icon="el-icon-search" @click="filterTreeData()"></el-button>
					</el-input>
				</el-col>
				<el-col style="padding:0px;height: 100%;height: 90%;">
					<el-scrollbar style="height: 100%;">
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
								:default-checked-keys="defaultCheckedKey"
								@node-click="handleNodeClick"
								style="padding: 10px;height: 100%;">
						</el-tree>
					</el-scrollbar>
				</el-col>
			</el-row>
		</div>

		<iframe v-show="action" :src="action" class="ms-iframe-style" style="background:url('${base}/static/images/loading.gif') no-repeat center;"></iframe>
		<el-empty v-show="!(loading||action)" :image-size="240" style="width:100%;height:100%;background:#fff;" description="请选择左侧栏目"></el-empty>
	</el-container>
</div>
</body>
</html>
<script>
	var indexVue = new Vue({
		el: "#index",
		data: {
			action: "",
			categoryTitle: '',
			defaultCheckedKey:null,//默认选中
			//跳转页面
			defaultProps: {
				children: 'children',
				label: 'categoryTitle'
			},
			//	返回的树形所有栏目数据
			treeDataAll: [],
			treeData: [],

			loading: true,
			emptyText: ''
		},
		methods: {

			handleNodeClick: function (data) {
				this.action = ms.manager + "/cms/category/form.do?id=" + data.id;
			},
			//新增
			add: function (isTop) {
				if(isTop) {
					this.action = ms.manager + "/cms/category/form.do?t="+new Date().getTime();
				} else {
					// 未选择栏目时提示
					var curNode = this.$refs.tree.getCurrentNode()
					if(curNode){
						var id = curNode.id;
						if (id) {
							this.action = ms.manager + "/cms/category/form.do?id=" + id + "&childId=" + id;
						} else {
							this.action = ms.manager + "/cms/category/form.do";
						}
					} else {
						this.$notify({
							title: '提示信息',
							message: '暂未选择栏目，请选择栏目。',
							type: 'info'
						})
					}
				}

			},
			//id 默认选中的栏目
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
						that.treeDataAll = res.data.rows
						that.filterTreeData(id)
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
			this.treeList(0);
		}
	});
</script>
<style scoped>
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
	#index .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content {
		background-color: rgb(137 140 145);
		color: #fff;
		border-radius: 2px;
	}
	body{
		overflow: hidden;
	}

</style>
