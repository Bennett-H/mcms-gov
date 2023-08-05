<!DOCTYPE html>
<html>
<head>
	<title>系统异常日志</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<div class="ms-search">
			<el-row>
				<el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
					<el-row>
						<el-col :span="8">
							<el-form-item  label="异常文件" prop="logTitle">
								<el-input
									v-model="form.logTitle"
									:disabled="false"
									  :style="{width:  '100%'}"
									  :clearable="true"
									placeholder="请输入异常文件">
								</el-input>
							 </el-form-item>
						</el-col>
						<el-col :span="8">

							<el-form-item  label="请求地址" prop="logUrl">
								<el-input
										v-model="form.logUrl"
										:disabled="false"
										  :style="{width:  '100%'}"
										  :clearable="true"
										placeholder="请输入请求地址">
								</el-input>
							 </el-form-item>
						</el-col>
						<el-col :span="8">
							<el-form-item label="创建时间" prop="createDateScope">
								<el-date-picker
										v-model="form.createDateScope"
										value-format="yyyy-MM-dd HH:mm:ss"
										type="datetimerange"
										:style="{width:  '100%'}"
										start-placeholder="开始时间"
										end-placeholder="结束时间">
								</el-date-picker>
							</el-form-item>
						</el-col>
					</el-row>

					<el-row :style="{padding: '10px'}">
						<el-col push="16" :span="8" style="text-align: right;">
							<el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list()">搜索</el-button>
							<el-button type="primary" icon="iconfont icon-shaixuan1" size="mini" @click="currentPage=1;$refs.search.open()">筛选</el-button>
							<el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
						</el-col>
					</el-row>
				</el-form>
			</el-row>
		</div>
		<el-main class="ms-container">
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark">
				<template slot="empty">
					{{emptyText}}
				</template>
                <el-table-column label="异常文件" align="left" prop="logTitle" show-overflow-tooltip>
                </el-table-column>
                <el-table-column label="请求地址"   align="left" prop="logUrl" show-overflow-tooltip >
                </el-table-column>
				<el-table-column label="操作人"  width="180"  align="left" prop="logUser">
				</el-table-column>
				<el-table-column label="创建时间"  width="180"  align="left" prop="createDate">
				</el-table-column>
				<el-table-column label="操作"  width="60" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="basic:log:view">
						<el-link type="primary" :underline="false" @click="open(scope.row.id)">详情</el-link>
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
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		dataList: [], //系统日志列表
		// selectionList:[],//系统日志列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
		//搜索表单
		form:{
			sqlWhere:null,
			// 标题
			logTitle:null,
			// 请求地址
			logUrl:null,
			// 请求状态
			logStatus:null,
			// 业务类型
			logBusinessType:null,
			// 操作人员
			logUser:null,
			createDateScope:null,
		},
	},
	watch:{
     	},
	methods:{
	    //查询列表
	    list: function() {
	    	var that = this;
			that.loading = true;
			var page={
				pageNo: that.currentPage,
				pageSize : that.pageSize
			}
			var form = JSON.parse(JSON.stringify(that.form))
			for (key in form){
				if(!form[key]){
					delete form[key]
				}
			}

			//处理时间范围
			if (form.createDateScope) {
				form.startTime = form.createDateScope[0];
				form.endTime = form.createDateScope[1];
			}

			history.replaceState({form:form,page:page},"");
			ms.http.post(ms.manager+"/co/log/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
				:Object.assign({},form, page)).then(function(res) {
					that.loading = false;
					if (!res.result||res.data.total <= 0) {
						that.emptyText ="暂无数据"
						that.dataList = [];
						that.total = 0;
					} else {
						that.emptyText = '';
						that.total = res.data.total;
						that.dataList = res.data.rows;
					}
				});
			},
		//系统日志列表选中
		// handleSelectionChange:function(val){
		// 	this.selectionList = val;
		// },

		//详情
        open:function(id){
			if(id){
				location.href=this.manager+"/co/log/form.do?id="+id;
			}else {
				location.href=this.manager+"/co/log/form.do";
			}
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
</style>
