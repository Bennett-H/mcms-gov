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
					<@shiro.hasPermission name="datascope:data:save">
					<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
				</@shiro.hasPermission>
					<@shiro.hasPermission name="datascope:data:del">
					<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
					</@shiro.hasPermission>
				</el-col>
			</el-header>
		<el-main class="ms-container">
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" width="40"></el-table-column>
                <el-table-column label="关联id"   align="right" prop="dataId">
                </el-table-column>
            <el-table-column label="目标编号"   align="left" prop="dataTargetId" :formatter="dataTargetIdFormat">
            </el-table-column>
            <el-table-column label="业务分类"   align="left" prop="dataType" :formatter="dataTypeFormat">
            </el-table-column>
				<el-table-column label="操作"  width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="datascope:data:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="datascope:data:del">
						<el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="datascope:data:copy">
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
		<#include "/datascope/data/form.ftl">
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		conditionList:[
        {action:'and', field: 'data_id', el: 'eq', model: 'dataId', name: '关联id', type: 'number'},
            {action:'and', field: 'data_target_id', el: 'eq', model: 'dataTargetId', name: '目标编号', type: 'cascader', multiple: false},
            {action:'and', field: 'data_type', el: 'eq', model: 'dataType', name: '业务分类', key: 'dictValue', title: 'dictLabel', type: 'select', multiple: false},
		],
		conditions:[],
		dataList: [], //数据权限列表
		selectionList:[],//数据权限列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
                dataTargetIdOptions:[],
                dataTargetIdProps:{"emitPath":false,"checkStrictly":true,"value":"categoryId","label":"categoryTitle","expandTrigger":"hover","children":"children"},
                dataTypeOptions:[],
		//搜索表单
		form:{
			sqlWhere:null,
		},
	},
	watch:{
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
			history.replaceState({form:form,page:page},"");
			ms.http.post(ms.manager+"/datascope/data/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
				:Object.assign({},form, page)).then(
					function(res) {
						if(that.loadState){
							that.loading = false;
						}else {
							that.loadState = true
						}
						if (!res.result||res.data.total <= 0) {
							that.emptyText ="暂无数据"
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
		//数据权限列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},
		//复制
		copy: function (row) {
			var that = this;
			delete row.id
			ms.http.post(ms.manager + "/datascope/data/save.do", row).then(function (res) {
				if (res.result) {
					that.$notify({
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
					    	ms.http.post(ms.manager+"/datascope/data/delete.do", row.length?row:[row],{
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
					    })
        		},
		//新增
        save:function(id){
			form.open(id);
        },
        //表格数据转换
		dataTargetIdFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.dataTargetIdOptions.find(function(value){
					return value.categoryId==cellValue;
				})
				if(data&&data.categoryTitle){
					value = data.categoryTitle;
				}
			}
            return value;
		},
		dataTypeFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.dataTypeOptions.find(function(value){
					return value.dictValue==cellValue;
				})
				if(data&&data.dictLabel){
					value = data.dictLabel;
				}
			}
            return value;
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
            //获取dataTargetId数据源
            dataTargetIdOptionsGet:function() {
                var that = this;
                ms.http.get(ms.manager + '/organization/employee/list.do?pigeSize=9999', {}).then(function (res) {
                    that.dataTargetIdOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取dataType数据源
            dataTypeOptionsGet:function() {
                    var that = this;
                    ms.http.get(ms.base+'/mdiy/dict/list.do', {dictType:'业务分类',pageSize:99999}).then(function (res) {
                        that.dataTypeOptions = res.data.rows;
                    }).catch(function (err) {
                        console.log(err);
                    });
                },
	},
created:function(){
            this.dataTargetIdOptionsGet();
                this.dataTypeOptionsGet();
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
		height: calc(100vh - 78px);
	}
</style>
