<!DOCTYPE html>
<html>
<head>
	<title>规则</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
			<el-header class="ms-header" height="50px">
				<el-col :span="12">
					<@shiro.hasPermission name="id:rule:save">
					<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
				</@shiro.hasPermission>
					<@shiro.hasPermission name="id:rule:del">
					<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
					</@shiro.hasPermission>
				</el-col>
			</el-header>
		<div class="ms-search">
			<el-row>
				<el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
							<el-row>
											<el-col :span="8">
            <el-form-item  label="规则名称" prop="irName">
                <el-input
                        v-model="form.irName"
                        :disabled="false"
					  	:readonly="false"
					  	:style="{width:  '100%'}"
					  	:clearable="true"
						maxlength="20"
					  	placeholder="请输入规则名称">
                </el-input>
             </el-form-item>
											</el-col>
											<el-col :span="8">
            <el-form-item  label="类型" prop="irType">
                    <el-select v-model="form.irType"
                               :style="{width: '100%'}"
                               :filterable="false"
                               :disabled="false"
                               :multiple="false" :clearable="true"
                               placeholder="请选择类型">
                        <el-option v-for='item in irTypeOptions' :key="item.dictValue" :value="item.dictValue"
                                   :label="item.dictLabel"></el-option>
                    </el-select>
             </el-form-item>
											</el-col>
										<el-col :span="8" style="text-align: right;">
												<el-button type="primary" icon="el-icon-search" size="mini" @click="form.sqlWhere=null;currentPage=1;list()">查询</el-button>
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
					description="业务系统经常会用到自动编码，如：员工编号、房间编号、货架编号等，都可以使用该插件来灵活配置 ">
				<template slot="title">
					功能介绍 <a href='http://doc.mingsoft.net/plugs/id-bian-ma-gui-zhe/ye-wu-kai-fa.html' target="_blank">开发手册</a>
				</template>
			</el-alert>
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" width="40"></el-table-column>
                 <el-table-column label="规则名称" align="left" prop="irName" show-overflow-tooltip>
					 <template slot-scope="scope">
						 <el-link type="primary" :underline="false" @click="detail(scope.row.id)" >{{scope.row.irName}}</el-link>
					 </template>
                 </el-table-column>
            <el-table-column label="类型" align="left" prop="irType" :formatter="irTypeFormat" show-overflow-tooltip>
            </el-table-column>
				<el-table-column label="操作" width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="id:rule:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="id:rule:del">
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
	</div>
		<#include "/id/rule/form.ftl">
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		conditionList:[
        {action:'and', field: 'ir_name', el: 'eq', model: 'irName', name: '规则名称', type: 'input'},
             {action:'and', field: 'ir_type', el: 'eq', model: 'irType', name: '类型', key: 'dictValue', title: 'dictLabel', type: 'select', multiple: false},
            {action:'and', field: 'create_date', el: 'eq', model: 'createDate', name: '创建时间', type: 'date'},
           {action:'and', field: 'update_date', el: 'eq', model: 'updateDate', name: '修改时间', type: 'date'},
 		],
		conditions:[],
		dataList: [], //规则列表
		selectionList:[],//规则列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
                irTypeOptions:[],
		//搜索表单
		form:{
			sqlWhere:null,
			// 规则名称
			irName:null,
			// 类型
			irType:null,
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
			ms.http.post(ms.manager+"/id/rule/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
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
		//规则列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},
		detail:function(id){
	    	location.href = ms.manager + "/id/ruleData/index.do?id="+id
		},
		//删除
        del: function(row){
        	var that = this;
        	that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
					    	confirmButtonText: '确定',
					    	cancelButtonText: '取消',
					    	type: 'warning'
					    }).then(function() {
					    	ms.http.post(ms.manager+"/id/rule/delete.do", row.length?row:[row],{
            					headers: {
                					'Content-Type': 'application/json'
                				}
            				}).then(
	            				function(res){
		            				if (res.result) {
										that.$notify({
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
		//新增
        save:function(id){
			form.open(id);
        },
        //表格数据转换
		irTypeFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.irTypeOptions.find(function(value){
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
            //获取irType数据源
            irTypeOptionsGet:function() {
                    var that = this;
                    ms.http.get(ms.base+'/mdiy/dict/list.do', {dictType:'编码规则类型',pageSize:99999}).then(function (res) {
						if(res.result) {
							res = res.data;
							that.irTypeOptions = res.rows;
						}
                    }).catch(function (err) {
                        console.log(err);
                    });
                },
	},
created:function(){
            this.irTypeOptionsGet();
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
</style>
