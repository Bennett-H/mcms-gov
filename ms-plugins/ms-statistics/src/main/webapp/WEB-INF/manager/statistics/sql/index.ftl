<!DOCTYPE html>
<html>
<head>
	<title>统计</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
		<el-header class="ms-header" height="50px">
			<el-col :span="12">
				<@shiro.hasPermission name="statistics:sql:save">
				<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
			</@shiro.hasPermission>
				<@shiro.hasPermission name="statistics:sql:del">
				<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
				</@shiro.hasPermission>
			</el-col>
		</el-header>
		<div class="ms-search">
			<el-row>
				<el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
					<el-row>
						<el-col :span="8">
							<el-form-item  label="统计名称" prop="ssName">
								<el-input v-model="form.ssName"
										  :disabled="false"
										  :style="{width:  '100%'}"
										  :clearable="true"
										  maxlength="20"
										  placeholder="请输入统计名称">
								</el-input>
							</el-form-item>
						</el-col>

						<el-col :span="16" style="text-align: right;padding-right: 10px;">
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
					description="用于统计相应的信息，核心是根据自定义SQL进行查询相应的信息，如:统计会员的相关信息">
				<template slot="title">
					功能介绍 <a href='http://doc.mingsoft.net/plugs/tong-ji-cha-jian/ye-wu-kai-fa.html' target="_blank">开发手册</a>
				</template>
			</el-alert>
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" width="40"></el-table-column>
                 <el-table-column label="统计名称" width="200"  align="left" prop="ssName">
                 </el-table-column>
<#--            <el-table-column label="统计类型"   align="left" prop="ssType" :formatter="ssTypeFormat">-->
<#--            </el-table-column>-->
                 <el-table-column label="统计SQL"   align="left" prop="ssSql">
                 </el-table-column>
				<el-table-column label="操作"  width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="statistics:sql:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="statistics:sql:del">
						<el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="statistics:sql:copy">
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
		<#include "/statistics/sql/form.ftl">
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		conditionList:[
        {action:'and', field: 'ss_name', el: 'eq', model: 'ssName', name: '统计名称', type: 'input'},
            // {action:'and', field: 'ss_type', el: 'eq', model: 'ssType', name: '统计类型', key: 'dictValue', title: 'dictLabel', type: 'select', multiple: false},
        {action:'and', field: 'ss_sql', el: 'eq', model: 'ssSql', name: '统计SQL', type: 'textarea'},
		],
		conditions:[],
		dataList: [], //统计列表
		selectionList:[],//统计列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
                // ssTypeOptions:[],
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
			ms.http.post(ms.manager+"/statistics/sql/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
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
		//统计列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},
		//复制
		copy: function (row) {
			var that = this;
			var rowCopy = JSON.parse(JSON.stringify(row));
			delete rowCopy.id;
			rowCopy.ssName += "副本"+(Math.round(Math.random()*10000)).toString();
			if(rowCopy.ssName.length > 20){
				that.$notify({
					title: "错误",
					message: "统计名称长度不能超过20",
					type: 'warning'
				});
			}
			ms.http.post(ms.manager + "/statistics/sql/save.do", rowCopy).then(function (res) {
				if (res.result) {
					that.$notify({
						title: '成功',
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
					    	ms.http.post(ms.manager+"/statistics/sql/delete.do", row.length?row:[row],{
            					headers: {
                					'Content-Type': 'application/json'
                				}
            				}).then(
	            				function(res){
		            				if (res.result) {
										that.$notify({
											title: '成功',
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
		// ssTypeFormat:function(row, column, cellValue, index){
		// 	var value="";
		// 	if(cellValue){
		// 		var data = this.ssTypeOptions.find(function(value){
		// 			return value.dictValue==cellValue;
		// 		})
		// 		if(data&&data.dictLabel){
		// 			value = data.dictLabel;
		// 		}
		// 	}
        //     return value;
		// },
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
            // //获取ssType数据源
            // ssTypeOptionsGet:function() {
            //         var that = this;
            //         ms.http.get(ms.base+'/mdiy/dict/list.do', {dictType:'statisticsType',pageSize:99999}).then(function (res) {
            //             that.ssTypeOptions = res.data.rows;
            //         }).catch(function (err) {
            //             console.log(err);
            //         });
            //     },
	},
created:function(){
                // this.ssTypeOptionsGet();
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
