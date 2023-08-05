<!DOCTYPE html>
<html>
<head>
	<title>自定义模型</title>
	<#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">

	<el-header class="ms-header" height="50px">
		<el-col :span="24">
			<@shiro.hasPermission name="mdiy:model:del">
				<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
			</@shiro.hasPermission>
			<@shiro.hasPermission name="mdiy:model:importJson">
				<el-button type="primary" icon="iconfont icon-daoru" size="mini" @click="dialogImportVisible=true" style="float: right">导入</el-button>
			</@shiro.hasPermission>
		</el-col>
	</el-header>

	<el-dialog title="导入自定义模型配置json" :visible.sync="dialogImportVisible" width="600px" :close-on-press-escape="false" :close-on-click-modal="false" append-to-body v-cloak>
		<el-form ref="form" :model="form" :rules="rules" size="mini" label-width="130px" label-position="top" >
			<el-form-item  label="类型" prop="modelType" >
				<el-select v-model="form.modelType"
						   :style="{width: '100%'}"
						   :disabled="false"
						   :multiple="false" :clearable="true"
						   placeholder="请选择类型">
					<el-option v-for='item in modelTypeOptions' :key="item.dictValue" :value="item.dictValue"
							   :label="item.dictLabel"></el-option>
				</el-select>
				<div class="ms-form-tip">
					可以通过 <b>自定义字典</b> 配置，根据不同的业务模块定义，方便业务开发及扩展模型的管理。例如：内容管理的扩展模型、会员中心的扩展模型，
				</div>
			</el-form-item>
			<el-form-item prop="modelJson">
                    <span slot='label'>自定义模型json
<#--						<el-popover placement="top-start" title="提示" width="200" trigger="hover" content="可通过代码生成器代码预览中自定义模型获取">-->
<#--							<i class="el-icon-question" slot="reference"></i>-->
<#--						</el-popover>-->
					</span>
				<el-input :rows="10" type="textarea" v-model="form.modelJson" placeholder="请粘贴来自代码生器中自定义模型的配置json"></el-input >
				<div class="ms-form-tip">
					通过 <a href="https://code.mingsoft.net" target="_blank">代码生成器</a> 在线设计表单，
					打开 <b>代码预览</b> 菜单，选择 <b>自定义模型</b>并<b>复制代码</b>粘贴到当前文本框 <br/>
					<b>注意:</b> 更新模型时会根据新的 <b>自定义模型</b>配置 来修改对应的表结构，包括删除字段、新增字段。字段有任何设置的变化都会进行先删除再新增，所以更新模型时谨慎操作，权限必须分配好，如果出现数据异常第一时间查看日志分析原因
				</div>
			</el-form-item>
		</el-form>
		<div slot="footer">
			<el-button size="mini" @click="dialogImportVisible = false">取 消</el-button>
			<el-button size="mini" type="primary" @click="imputJson()">确 定</el-button>
		</div>
	</el-dialog>
	<el-dialog title="预览" :visible.sync="dialogViewVisible" width="70%" append-to-body v-cloak>
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
			<el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
				<el-row>
					<el-col :span="8">
						<el-form-item  label="模型名称" prop="modelName">
							<el-input v-model="form.modelName"
									  :disabled="false"
									  :style="{width:  '100%'}"
									  :clearable="true"
									  placeholder="请输入完整名称，不支持模糊查询">
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
				description="可以快速扩展现有的业务表数据，例如：文章内容字段不满足，可以通过 文章栏目 来绑定模型，还有 会员管理 的 会员扩展信息。">
			<template slot="title">
				功能介绍 <a href='http://doc.mingsoft.net/plugs/zi-ding-yi-cha-jian/ye-wu-kai-fa/zi-ding-yi-mo-xing.html' target="_blank">开发手册</a>
			</template>
		</el-alert>
		<el-table v-loading="loading" ref="multipleTable" height="calc(100vh-68px)" class="ms-table-pagination" border :data="treeList" tooltip-effect="dark" @selection-change="handleSelectionChange">
			<template slot="empty">
				{{emptyText}}
			</template>
			<el-table-column type="selection" width="40"></el-table-column>
			<el-table-column label="模型名称"  align="left" prop="modelName">
			</el-table-column>
			<el-table-column label="模型表名" align="left" prop="modelTableName">
			</el-table-column>
			<el-table-column label="类型" width="100" align="center" prop="modelType" :formatter="typeFormat">
			</el-table-column>
			<#--				<@shiro.hasPermission name="mdiy:model:update">-->
			<el-table-column  align="center"  width="270">
				<template slot='header'>操作
					<el-popover placement="top-start" title="提示" trigger="hover" >
						更新模型：可以通过重复导入JSON配置来修改对应的表结构 <br/>
						表单预览：显示表单各个控件的效果 <br/>
						<i class="el-icon-question" slot="reference"></i>
					</el-popover>
				</template>
				<template  slot-scope="scope">
					<@shiro.hasPermission name="mdiy:model:update">
						<el-link type="primary" :underline="false"  @click="dialogImportVisible=true;form.id=scope.row.id;form.modelType=scope.row.modelType">更新模型</el-link>
					</@shiro.hasPermission>
					<el-link type="primary" :underline="false" class="copyBtn" :data-clipboard-text="JSON.stringify(modelJson)" @click="copyModelJson(scope.row)">复制模型JSON</el-link>
					<el-link type="primary" :underline="false"  @click="view(scope.row)">表单预览</el-link>
					<@shiro.hasPermission name="mdiy:model:del">
						<el-link :underline="false" type="primary" @click="del([scope.row])">删除</el-link>
					</@shiro.hasPermission>
				</template>
			</el-table-column>
			<#--				</@shiro.hasPermission>-->
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
		data: function () {
			return {
				treeList: [],
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
				modelTypeOptions: [],
				dialogViewVisible: false,
				dialogImportVisible: false,
				loading: true,
				emptyText: '',
				//自定义模型html
				modelFormHtml: '',
				//表单字段
				modelField: '',
				//搜索表单
				form: {
					// 模型名称
					modelName: '',
					modelType: '',
					// 模型表名
					modelTableName: '',
					// json
					modelJson: ''
				},
				//表单验证
				rules: {
					modelType: [{
						required: true,
						message: '类型不能为空',
						trigger: 'blur'
					}],
					modelJson: [{
						required: true,
						message: 'json数据不能为空',
						trigger: 'blur'
					}]
				},
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
				modelJson: {
					searchJson: '',
					field: '',
					html: '',
					title: '',
					script: '',
					sql: '',
					tableName: ''
				}
			}
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
			typeFormat: function (row) {
				var data = this.modelTypeOptions.find(function (item) {
					return item.dictValue == row.modelType;
				});

				if (data) {
					return data.dictLabel;
				} else {
					return '';
				}
			},
			//复制自定义模型json
			copyModelJson: function (row) {
				var _modelJson = JSON.parse(row.modelJson);
				this.modelJson.searchJson = _modelJson.searchJson;
				this.modelJson.field = row.modelField;
				this.modelJson.html = _modelJson.html;
				this.modelJson.title = row.modelName;
				this.modelJson.script = _modelJson.script;
				this.modelJson.sql = _modelJson.sql;
				this.modelJson.tableName = _modelJson.tableName;
				var clipboard = new ClipboardJS('.copyBtn');
				var that = this;
				clipboard.on('success', function (e) {
					that.$notify({
						title: '提示',
						message: '模型json已保存到剪切板',
						type: 'success'
					});
					clipboard.destroy();
				});
			},
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
					for (var key in form){
						if(form[key] === undefined || form[key] === null){
							delete  form[key]
						}
					}
					form.sqlWhere ? data = Object.assign({}, {sqlWhere: form.sqlWhere}, page) : data = Object.assign({}, form, page)
				} else {
					data = page;
				}

				history.replaceState({form:form,page:page},"");
				ms.http.get(ms.manager + "/mdiy/model/list.do", data).then(function (data) {
					if (data.result){
						if (data.data.total <= 0) {
							that.loading = false;
							that.emptyText = '暂无数据';
							that.treeList = [];
						} else {
							that.emptyText = '';
							that.loading = false;
							that.treeList = data.data.rows;
						}
						that.$nextTick(function(){
							that.$refs.multipleTable.doLayout();
						})
						that.total = data.data.total;
					}
				});
			},
			//自定义模型列表选中
			handleSelectionChange: function (val) {
				this.selectionList = val;
			},
			//删除
			del: function (row) {
				var that = this;
				that.$confirm('此操作将永久删除所选内容，以及对应业务表, 是否继续?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(function () {
					ms.http.post(ms.manager + "/mdiy/model/delete.do", row.length ? row : [row], {
						headers: {
							'Content-Type': 'application/json'
						}
					}).then(function (data) {
						if (data.result) {
							that.$notify({
								title: '成功 ',
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
					s.innerHTML = data.script.replace("this.get(this.form.linkId);","");
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
			imputJson: function () {
				var url = "/mdiy/model/importJson.do";
				if(this.form.id && this.form.id!=''){
					url = "/mdiy/model/updateJson.do";
				}

				var that = this;
				that.$confirm('此操作会进行更新表操作，请谨慎操作', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(function () {
					that.$refs.form.validate(function (valid) {
						if (valid) {
							ms.http.post(ms.manager +  url, {
								id:that.form.id,
								modelType: that.form.modelType,
								modelJson: that.form.modelJson
							}).then(function (data) {
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
							}).catch(function (err) {
								console.log(err)
							});
						}
					});
				}).catch(function (e){
					that.$notify({
						title: '失败',
						message: '导入模型错误，请检查模型是否正确或删除模型重新导入',
						type: 'warning'
					});
				})
			},
			//监听代码预览窗口tab点击
			handleClick:function(tab, event) {
				tab.name && this.$nextTick(function(){
					if (this.$refs[tab.name]) {
						this.$refs[tab.name].codemirror.refresh()
					}
				})
			},
			//pageSize改变时会触发
			sizeChange: function (pagesize) {
				this.loading = true;
				this.pageSize = pagesize;
				this.list();
			},
			//获取modelType数据源
			modelTypeOptionsGet: function () {
				var that = this;
				ms.http.get(ms.base + '/mdiy/dict/list.do', {
					dictType: '自定义模型类型',
					pageSize: 99999
				}).then(function (data) {
					if(data.result){
						data = data.data;
						that.modelTypeOptions = data.rows;
					}
				});
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
				this.list();
			}
		},
		created: function () {
			if(history.state){
				this.form = history.state.form;
				this.currentPage = history.state.page.pageNo;
				this.pageSize = history.state.page.pageSize;
			}

			this.list();
			this.modelTypeOptionsGet();
		},
		beforUpdate:function() {
			this.$nextTick(function(){
				this.$refs.multipleTable.doLayout();
			})
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
