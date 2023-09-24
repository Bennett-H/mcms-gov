<!DOCTYPE html>
<html>
<head>
	<title>日志表</title>
		<#include "../../include/head-file.ftl">
	<script src="${base}/static/plugins/codemirror/5.48.4/codemirror.js"></script>
	<link href="${base}/static/plugins/codemirror/5.48.4/codemirror.css" rel="stylesheet">
	<script src="${base}/static/plugins/codemirror/5.48.4/mode/css/css.js"></script>
	<script src="${base}/static/plugins/vue-codemirror/vue-codemirror.js"></script>
	<script src="${base}/static/plugins/codemirror/5.48.4/addon/scroll/annotatescrollbar.js"></script>
	<script src="${base}/static/plugins/codemirror/5.48.4/mode/xml/xml.js"></script>
	<script src="${base}/static/plugins/codemirror/5.48.4/mode/javascript/javascript.js"></script>
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
			<el-header class="ms-header" height="50px">
				<el-col :span="12">
					<@shiro.hasPermission name="spider:log:import">
					<el-button type="primary" icon="iconfont icon-daoru" size="mini" @click="toImport(selectionList)"  :disabled="!selectionList.length">导入</el-button>
					</@shiro.hasPermission>
					<@shiro.hasPermission name="spider:log:del">
					<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
					</@shiro.hasPermission>
				</el-col>
			</el-header>
		<div class="ms-search">
			<el-row>
				<el-form :model="form"  ref="searchForm"  label-width="100px" size="mini">
						<el-row>
							<el-col :span="5">
								<el-form-item  label="任务名称" prop="taskName">
									<el-input
											v-model="form.taskName"
											:disabled="false"
											:readonly="false"
											:style="{width:  '100%'}"
											:clearable="true"
											maxlength="20"
											placeholder="请输入任务名称">
									</el-input>
								</el-form-item>
							</el-col>
							<el-col :span="5">
								<el-form-item  label="规则名称" prop="regularName">
									<el-input
											v-model="form.regularName"
											:disabled="false"
											:readonly="false"
											:style="{width:  '100%'}"
											:clearable="true"
											maxlength="20"
											placeholder="请输入规则名称">
									</el-input>
								</el-form-item>
							</el-col>
							<el-col :span="4">
								<el-form-item  label="是否导入过" prop="imported">
										<el-select  v-model="form.imported"
												   :style="{width: '100%'}"
												   :filterable="false"
												   :disabled="false"
												   :multiple="false" :clearable="true"
												   placeholder="请选择是否导入过">
											<el-option v-for='item in importedOptions' :key="item.value" :value="item.value"
													   :label="item.label"></el-option>
										</el-select>
								 </el-form-item>
							</el-col>
							<el-col :span="4">
								<el-form-item  label="创建时间" prop="createDate">
									 <el-date-picker
											v-model="form.createDate"
											placeholder="请选择时间"
											start-placeholder=""
											end-placeholder=""
											value-format="yyyy-MM-dd HH:mm:ss"
											:readonly="false"
											:disabled="false"
											:editable="true"
											:clearable="true"
											:style="{width:'125px'}"
											type="date">
									</el-date-picker>
								</el-form-item>
							</el-col>
							<el-col :span="6" style="text-align: right;">
								<el-button type="primary" icon="el-icon-search" size="mini" @click="form.sqlWhere=null;currentPage=1;list()">查询</el-button>
								<el-button type="primary" icon="el-icon-shaixuan1" size="mini" @click="$refs.search.open()">筛选</el-button>
								<el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
							</el-col>
						</el-row>
				</el-form>
			</el-row>
		</div>
		<el-main class="ms-container">
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" width="40"></el-table-column>
                 <el-table-column width="120px" label="任务名称" align="left" prop="taskName" :show-overflow-tooltip="true">
                 </el-table-column>
                 <el-table-column width="120px" label="规则名称" align="left" prop="regularName" :show-overflow-tooltip="true">
                 </el-table-column>
				<el-table-column width="120px" label="是否导入过" align="center" prop="imported" :formatter="importedFormat">
				</el-table-column>
                 <el-table-column label="内容链接" align="left" prop="sourceUrl" :show-overflow-tooltip="true">
                 </el-table-column>
                 <el-table-column label="采集内容" align="left" prop="content" >
					 <template slot-scope="scope">
						 <el-link type="primary" :underline="false" @click="viewContent(scope.row.content)">{{scope.row.content.length > 50 ? scope.row.content.substr(0,50) + "..." : scope.row.content}}</el-link>
					 </template>
                 </el-table-column>
                 <el-table-column label="创建时间" min-width="80" align="center" prop="createDate">
                 </el-table-column>
				<el-table-column label="操作" width="70" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="spider:log:del">
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


		<el-dialog
				:close-on-click-modal="false"
				:visible.sync="dialogVisible"
				title="采集内容"
				width="80%">
			<el-scrollbar style="height: 550px;">
				<#--编辑框-->
				<codemirror v-model="contentJson" :options="codemirrorOptions" style="height: 100%">
				</codemirror>
			</el-scrollbar>
		</el-dialog>

	</div>


</body>

</html>
<script>
	Vue.use(VueCodemirror);
var indexVue = new Vue({
	el: '#index',
	data:{
		conditionList:[
        // {action:'and', field: 'task_id', el: 'eq', model: 'taskId', name: '任务主键', type: 'input'},
        //  {action:'and', field: 'regular_id', el: 'eq', model: 'regularId', name: '规则主键', type: 'input'},
             {action:'and', field: 'imported', el: 'eq', model: 'imported', name: '是否导入过', key: 'value', title: 'label', type: 'select', multiple: false},
         {action:'and', field: 'source_url', el: 'eq', model: 'sourceUrl', name: '内容链接', type: 'input'},
         // {action:'and', field: 'content', el: 'eq', model: 'content', name: '采集内容', type: 'textarea'},
            {action:'and', field: 'create_date', el: 'eq', model: 'createDate', name: '创建时间', type: 'date'},
           {action:'and', field: 'update_date', el: 'eq', model: 'updateDate', name: '修改时间', type: 'date'},
 		],
		contentJson: null,//采集内容json
		dialogVisible: false,
		//设置
		codemirrorOptions: {
			tabSize: 4,
			styleActiveLine: true,
			lineNumbers: true,
			line: true,
			styleSelectedText: true,
			lineWrapping: true,
			mode: 'css',
			matchBrackets: true,
			showCursorWhenSelecting: true,
			hintOptions: {
				completeSingle: false
			}
		},
		conditions:[],
		dataList: [], //日志表列表
		selectionList:[],//日志表列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
		importedOptions:[{"value":"yes","label":"是"},{"value":"no","label":"否"},{"value":"exception","label":"导入异常"}],
		//搜索表单
		form:{
			sqlWhere:null,
			// 是否导入过
			imported:null,
			// 创建时间
			createDate:null,
		},
	},
	watch:{
  	},
	methods:{
		importedFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.importedOptions.find(function(value){
					return value.value==cellValue;
				})
				if(data&&data.label){
					value = data.label;
				}
			}
			return value;
		},
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
			ms.http.post(ms.manager+"/spider/log/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
				:Object.assign({},form, page)).then(
					function(res) {
						if(that.loadState){
							that.loading = false;
						}else {
							that.loadState = true
						}
						if (!res.result||res.data.total <= 0) {
							that.emptyText = '暂无数据'
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
		//日志表列表选中
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
					    	ms.http.post(ms.manager+"/spider/log/delete.do", row.length?row:[row],{
            					headers: {
                					'Content-Type': 'application/json'
                				}
            				}).then(
	            				function(res){
		            				if (res.result) {
										that.$notify({
											title: '成功',
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
					    })
        		},
		//导入
		toImport: function(row){
			var that = this;
			that.$confirm('此操作将永久导入业务表中, 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(function() {
				ms.http.post(ms.manager+"/spider/log/import.do", row.length?row:[row],{
					headers: {
						'Content-Type': 'application/json'
					}
				}).then(
						function(res){
							if (res.result) {
								that.$notify({
									title: '成功',
									type: 'success',
									message: '导入成功!'
								});
								//删除成功，刷新列表
								that.list();
							}else {
								that.$notify({
									title: '导入失败',
									message: res.msg,
									type: 'warning'
								});
							}
						});
			})
		},
        //表格数据转换
		importedFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.importedOptions.find(function(value){
					return value.value==cellValue;
				})
				if(data&&data.label){
					value = data.label;
				}
			}
            return value;
		},
		//查看内容详情
		viewContent:function(content){
			this.contentJson=(JSON.stringify(JSON.parse(content), null, 2))
			this.dialogVisible= true
		},
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
		height: calc(100vh - 141px);
	}

	#index .CodeMirror {
		height: auto;
	}

</style>
