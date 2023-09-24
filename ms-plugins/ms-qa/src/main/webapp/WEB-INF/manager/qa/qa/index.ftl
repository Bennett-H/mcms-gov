<!DOCTYPE html>
<html>
<head>
	<title>问卷管理</title>
	<#include "../../include/head-file.ftl">

</head>
<body>
<div id="index" v-cloak class="ms-index">
	<el-header class="ms-header" height="50px">
		<el-col :span="24">
			<@shiro.hasPermission name="qa:qa:del">
				<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
			</@shiro.hasPermission>
			<@shiro.hasPermission name="qa:qa:importJson">
				<el-button type="primary" icon="iconfont icon-daoru" size="mini" @click="dialogImportVisible=true;impForm.id='';impForm.isWebSubmit=false" style="float: right">导入</el-button>
			</@shiro.hasPermission>
		</el-col>
	</el-header>
	<el-dialog title="导入自定义模型配置json" :visible.sync="dialogImportVisible" width="600px" append-to-body v-cloak>

		<el-scrollbar class="ms-scrollbar" style="height: 100%;">
			<el-form ref="form" :model="impForm" :rules="rules" size="mini" label-width="180px" position="right" >

				<el-form-item  label="自定义模型json" prop="modelJson">
					<el-input
							type="textarea" :rows="10"
							:disabled="false"
							:readonly="false"
							v-model="impForm.modelJson"
							:style="{width: '100%'}"
							placeholder="请粘贴来自代码生器中自定义模型的配置json">
					</el-input>
					<div class="ms-form-tip">
						通过 <a href="https://code.mingsoft.net" target="_blank">代码生成器</a> 在线设计表单，
						打开 <b>代码预览</b> 菜单，选择 <b>自定义模型</b>并<b>复制代码</b>粘贴到当前文本框 <br/>
						<b>注意:</b> 更新模型时会根据新的 <b>自定义模型</b>配置 来修改对应的表结构，包括删除字段、新增字段。字段有任何设置的变化都会进行先删除再新增，所以更新模型时谨慎操作
					</div>
				</el-form-item>
			</el-form>
		</el-scrollbar>
		<div slot="footer">
			<el-button size="mini" @click="dialogImportVisible = false">取 消</el-button>
			<el-button size="mini" type="primary" @click="imputJson()">确 定</el-button>
		</div>
	</el-dialog>
	<el-dialog title="表单预览" :visible.sync="dialogViewVisible" width="70%" append-to-body v-cloak v-loading="loading">
		<el-tabs value="first"  @tab-click="handleClick">
			<el-tab-pane label="显示效果" name="first">
				<div id="model"></div>
			</el-tab-pane>
			<el-tab-pane label="表单代码" name="second">
				<codemirror ref="second" v-model="modelFormHtml" :options="codemirrorOption" >
				</codemirror>
				<div class="ms-form-tip">根据实际业务情况，修改表单代码来满足业务需求</div>
			</el-tab-pane>
			<el-tab-pane label="字段信息" name="third">
				<codemirror ref="third" v-model="modelField" :options="codemirrorOption">
				</codemirror>
				<div class="ms-form-tip">表单项的 <span class="key">name</span> 对应字段信息的<span class="key"> model</span> 属性</div>
			</el-tab-pane>
		</el-tabs>
	</el-dialog>
	<div class="ms-search" style="padding: 20px 10px 0 10px;">
		<el-row>
			<el-form :model="form"  ref="searchForm"  label-width="85px" size="mini">
				<el-row>
					<el-col :span="8">
						<el-form-item  label="问卷名称" prop="qaName">
							<el-input v-model="form.qaName"
									  :disabled="false"
									  :style="{width:  '100%'}"
									  :clearable="true"
									  maxlength="30"
									  placeholder="请输入问卷名称">
							</el-input>
						</el-form-item>
					</el-col>
					<el-col :span="16" style="text-align: right">
						<el-button type="primary" icon="el-icon-search" size="mini" @click="loading=true;currentPage=1;list(true)">查询</el-button>
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
				description="通过 代码生成器 导入自定义模型，可以快速实现调查问卷开发">
			<template slot="title">
				功能介绍 <a href='http://doc.mingsoft.net/plugs/zi-ding-yi-cha-jian/ye-wu-kai-fa/zi-ding-yi-biao-dan.html' target="_blank">开发手册</a>
			</template>
		</el-alert>
		<el-table v-loading="loading" ref="multipleTable" height="calc(100vh-68px)" class="ms-table-pagination" border :data="treeList" tooltip-effect="dark" @selection-change="handleSelectionChange">
			<template slot="empty">
				{{emptyText}}
			</template>
			<el-table-column type="selection" width="40" :selectable="isChecked"></el-table-column>
			<#--<el-table-column  min-width="120" align="left" prop="qaName">-->
			<#--	<template slot='header'>问卷名称-->
			<#--		<el-popover placement="top-start" title="提示" trigger="hover" >-->
			<#--			提供给问卷中调用 如：qa.model.form(divID, "问卷名称");-->
			<#--			<i class="el-icon-question" slot="reference"></i>-->
			<#--		</el-popover>-->
			<#--	</template>-->
			<#--</el-table-column>-->
			<el-table-column label="问卷名称" align="left" prop="qaName">
				<template slot-scope="scope">
					<el-link type="primary" :underline="false"
							 @click="check(scope.row)"
							 :href="ms.manager+'/qa/qaData/index.do?modelId=' + scope.row.id+'&qaName='+scope.row.qaName">{{scope.row.qaName}}</el-link>
				</template>
			</el-table-column>
			<el-table-column label="问卷表名称" min-width="200" align="left" prop="qaTableName">

			</el-table-column>
			<el-table-column label="开始时间" min-width="200" align="left" prop="startTime">

			</el-table-column>
			<el-table-column label="结束时间" min-width="200" align="left" prop="endTime">

			</el-table-column>
			<el-table-column label="创建人" min-width="200" align="left" prop="createBy" :formatter="createByFormat">

			</el-table-column>
			<el-table-column label="创建时间" min-width="200" align="center"  prop="createDate">

			</el-table-column>
			<el-table-column  align="center"  width="400">
				<template slot='header'>操作
					<el-popover placement="top-start" title="提示" trigger="hover" >
						更新问卷：可以通过重复导入JSON配置来修改对应的表结构 <br/>
						问卷预览：显示问卷各个控件的效果 <br/>
						数据预览：管理问卷数据入口
						<i class="el-icon-question" slot="reference"></i>
					</el-popover>
				</template>
				<template slot-scope="scope">
					<@shiro.hasPermission name="qa:qa:update">
						<el-link type="primary" :underline="false"  @click="dialogImportVisible=true;impForm.id=scope.row.id;impForm.isWebSubmit=JSON.parse(scope.row.modelJson).isWebSubmit">更新问卷</el-link>
					</@shiro.hasPermission>
					<@shiro.hasPermission name="qa:qa:view">
						<el-link type="primary" :underline="false"  @click="view(scope.row)">问卷预览</el-link>
					</@shiro.hasPermission>
					<@shiro.hasPermission name="qa:qa:update">
						<el-link type="primary" :underline="false"  @click="config(scope.row)">问卷设置</el-link>
					</@shiro.hasPermission>
					<@shiro.hasPermission name="qa:qa:del">
						<el-link :underline="false"  v-if="scope.row.notDel == 0" type="primary" @click="del([scope.row])">删除</el-link>
					</@shiro.hasPermission>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
				background
				:page-sizes="[5, 10, 20, 50, 100]"
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
	Vue.use(VueCodemirror);
	var indexVue = new Vue({
		el: '#index',
		data: {
			treeList: [],
			managerList:[],
			//自定义模型列表
			selectionList: [],
			//自定义模型列表选中
			total: 0,
			//总记录数量
			pageSize: 20,
			//页面数量
			currentPage: 1,
			//初始页
			mananger: ms.manager,
			qaTypeOptions: [],
			dialogViewVisible: false,
			dialogImportVisible: false,
			loading: true,
			emptyText: '',
			//自定义模型html
			modelFormHtml:'',
			//表单字段
			modelField:'',
			//搜索表单
			form: {
				// 投票名称
				qaName: '',
			},
			impForm: {
				//模型编号
				id:'',
				// 模型名称
				modelJson: '',
				// 是否允许前端提交
				isWebSubmit:true,
			},
			//表单验证
			rules: {
				modelJson: [{
					required: true,
					message: 'json数据不能为空',
					trigger: 'blur'
				}],
				// 是否允许前端提交
				isWebSubmit: [{"required":true,"message":"是否允许前端提交不能为空"}]
			},
			menuJson:[
				{
					modelIsMenu:1,
					modelTitle:"",
					modelUrl:"",
					modelChildList:[
						{
							modelIsMenu:0,
							modelTitle:"新增",
							modelUrl:"mdiy:formData:save"
						},
						{
							modelIsMenu:0,
							modelTitle:"删除",
							modelUrl:"mdiy:formData:del"
						},
						{
							modelIsMenu:0,
							modelTitle:"更新",
							modelUrl:"mdiy:formData:update"
						},
						{
							modelIsMenu:0,
							modelTitle:"查看",
							modelUrl:"mdiy:formData:view"
						}
					]
				}
			],
			loading:false,
			//设置
			codemirrorOption: {
				tabSize: 4,
				styleActiveLine: true,
				lineNumbers: true,
				line: true,
				styleSelectedText: true,
				lineWrapping: true,
				mode: 'text/html',
				matchBrackets: true,
				showCursorWhenSelecting: true,
				hintOptions: {
					completeSingle: false
				},
				tags: {
					style: [["type", /^text\/(x-)?scss$/, "text/x-scss"],
						[null, null, "css"]],
					custom: [[null, null, "customMode"]]
				}
			},
		},
		watch: {
			'dialogImportVisible': function (n, o) {
				if (!n) {
					this.$refs.form.resetFields();
					this.form.id = '';
				}
			}
		},
		methods: {
			//查询列表
			list: function (isSearch) {
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

				history.replaceState({form:form,page:page},"");
				ms.http.get(ms.manager + "/qa/qa/list.do", data).then(function (data) {
					if(data.result){
						if (data.data.total <= 0) {
							that.loading = false;
							that.emptyText = '暂无数据';
							that.treeList = [];
						} else {
							that.emptyText = '';
							that.loading = false;
							that.treeList = data.data.rows;
						}
						that.total = data.data.total;
					}
				})
			},
			//自定义模型列表选中
			handleSelectionChange: function (val) {
				this.selectionList = val;
			},
			//删除
			del: function(row) {
				var that = this;
				that.$confirm('此操作将永久删除所选内容，以及对应业务表, 是否继续?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(function () {
					ms.http.post(ms.manager + "/qa/qa/delete.do", row.length ? row : [row], {
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
				});
			},

			//创建人  列表格式化
			createByFormat: function (row, column, cellValue, index) {
				var value = "";


				if (cellValue) {
					var data = this.managerList.find(function (value) {
						return value.id == cellValue;
					})
					if (data && data.managerNickName) {
						value = data.managerNickName;
					}
				}

				return value;
			},

			//获取管理员信息
			managerListGet: function () {
				var that = this
				var url = "/basic/manager/list.do"
				ms.http.get(ms.manager + url).then(function (data) {
					if (data.result) {
						that.managerList = data.data.rows
					}
				})
			},

			//预览
			view: function(row) {
				var data = JSON.parse(row.modelJson);
				var modelField = row.modelField;
				this.loading = true;
				var that = this;
				this.dialogViewVisible = true;
				this.$nextTick(function () {
					var model = document.getElementById('model');
					var custom = document.getElementById('c_model');

					if (custom) {
						model.removeChild(custom);
					}
					var div = document.createElement('div');
					div.id = 'c_model';
					model.appendChild(div);
					var s = document.createElement('script');
					s.innerHTML = data.script.replaceAll("this.get(this.form.linkId);","");
					var con = document.createElement('div');
					con.id = 'custom-model';
					con.innerHTML = data.html;
					div.appendChild(s);
					div.appendChild(con); //初始化自定义模型并传入关联参数

					that.modelFormHtml = data.html + "<script>"+data.script+"<\/script>";
					that.modelField = modelField;
					that.model = new custom_model();
					this.loading = false;

				});
			},
			//编辑问卷
			config: function(row) {
				location.href = ms.manager+'/qa/qa/form.do?qaName='+row.qaName
			},
			imputJson: function () {
				var that = this;
				var url = "/qa/qa/importJson.do";
				if(that.impForm.id &&  that.impForm.id!=''){
					url = "/qa/qa/updateJson.do";
				}
				that.$confirm('此操作会进行更新表操作，请谨慎操作', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(function () {
					// 控制前端接口isWebSubmit，保存至json
					that.$refs.form.validate(function (valid) {
						var object="";
						try {
							object = JSON.parse(that.impForm.modelJson);
						}catch(e) {
							that.$notify({
								title: '失败',
								message: "json格式不匹配，请复制 代码生成器 中的 自定义模型代码",
								type: 'warning'
							});
						};
						object.isWebSubmit = that.impForm.isWebSubmit;
						that.impForm.modelJson = JSON.stringify(object);

						if (valid) {
							ms.http.post(ms.manager + url, that.impForm).then(function (data) {
								if (data.result) {
									that.form = {};
									that.dialogImportVisible=false;
									that.list();
									that.$notify({
										title: '成功',
										message: "更新成功",
										type: 'success'
									});
								} else {
									that.$notify({
										title: '失败',
										message: data.msg,
										type: 'warning'
									});
								}
							})
						}
					});
				});

			},
			//监听代码预览窗口tab点击
			handleClick(tab, event) {
				tab.name && this.$nextTick(function(){
					this.$refs[tab.name].codemirror.refresh()
				})
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
			isChecked: function (row) {
				return row.notDel == 0
			},
			//重置表单
			rest: function () {
				this.currentPage = 1;
				this.loading = true;
				this.$refs.searchForm.resetFields();
				this.list();
			},
		},
		created: function () {
			if(history.state){
				this.form = history.state.form;
				this.currentPage = history.state.page.pageNo;
				this.pageSize = history.state.page.pageSize;
			}
			this.managerListGet()
			this.list();
		}
	});
</script>
<style>
	#index .iconfont{
		font-size: 12px;
		margin-right: 5px;
	}
	.el-dialog__body {
		padding: 0 20px 30px 20px;
	}
	.CodeMirror {
		border: 1px solid #eee;
	}
	/*饿了么滚动样式*/
	.el-scrollbar__wrap{
		margin-right: -30px!important;
	}
</style>
