<!DOCTYPE html>
<html>
<head>
	<title>关注记录</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<div class="ms-search">
			<el-row>
				<el-form :model="form"  ref="searchForm"  label-width="85px" size="mini">
					<el-row>
						<el-col :span="8">
							<el-form-item  label="业务ID" prop="dataId">
								<el-input v-model="form.dataId"
										  :disabled="false"
										  :style="{width:  '100%'}"
										  :clearable="true"
										  placeholder="请输入完整业务ID，不支持模糊查询">
								</el-input>
							</el-form-item>
						</el-col>
						<el-col :span="8">
							<el-form-item  label="业务标题" prop="collectionDataTitle">
								<el-input v-model="form.collectionDataTitle"
										  :disabled="false"
										  :style="{width:  '100%'}"
										  :clearable="true"
										  placeholder="请输入完整业务标题，不支持模糊查询">
								</el-input>
							</el-form-item>
						</el-col>
						<el-col :span="8" style="text-align: right">
							<el-button type="primary" icon="el-icon-search" size="mini" @click="loading=true;currentPage=1;list()">查询</el-button>
							<el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
						</el-col>
					</el-row>
				</el-form>
			</el-row>
		</div>
		<el-main class="ms-container" style="display: flex;flex-direction: row">
			<div style="display: flex;flex-direction: column;flex: 1;">
				<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
					<template slot="empty">
						{{emptyText}}
					</template>
					<el-table-column label="业务id" prop="dataId">
					</el-table-column>
					<el-table-column label="业务类型"  align="center" prop="dataType" :formatter="dataTypeFormat">
					</el-table-column>
					<el-table-column label="业务标题" prop="collectionDataTitle" show-overflow-tooltip>
					</el-table-column>
					<el-table-column label="关注数" width="200" prop="dataCount">
					</el-table-column>
					<el-table-column label="创建时间" align="center" width="200"prop="createDate">
					</el-table-column>
					<el-table-column label="操作"  width="180" align="center">
						<template slot-scope="scope">
							<el-link type="primary" :underline="false" @click="open(scope.row.dataId)">详情</el-link>
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
			</div>
		</el-main>
	</div>
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		// 关注类型
		dataTypeOptions:[],
		// 左侧默认选中
		defaultActive: '',
		conditions:[],
		dataList: [], //关注记录列表
		selectionList:[],//关注记录列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loading: true,//加载状态
		emptyText:'',//提示文字
		//搜索表单
		form:{
			// 数据id
			dataId:'',
			// 业务类型
			dataType:'',
			// 关注数
			dataCount:Number(''),
			// 业务标题
			collectionDataTitle:null,
		}
	},
	watch:{

	},
	methods:{
		isChecked: function(row) {
			if(row.del == 3) {
				return false;
			}
			return true;
		},
		// 进入详情页
		open:function(dataId){
			location.href=this.manager+"/attention/collectionLog/detail.do?id="+dataId+"&type="+this.defaultActive;
		},
		//获取关注类型
		getDataType: function () {
			var that = this;
			that.form.dataType = '';
			ms.http.get(ms.base + '/mdiy/dict/list.do', {dictType: '关注类型', pageSize: 99999})
				.then(function (res) {
				if (res.result) {
					that.dataTypeOptions = res.data.rows;
					//跳转历史导航栏
					var dataTypeParam = ms.util.getParameter("dataType");
					that.defaultActive = that.form.dataType = dataTypeParam == null && that.dataTypeOptions.length > 0 ? that.dataTypeOptions[0].dictLabel : dataTypeParam;
					that.list(that.defaultActive);
				}
			});
		},
	    //查询列表
	    list: function(dataType) {
	    	var that = this;
			that.loading = true;
			// 存储当前左侧菜单选中的类型
			if (dataType!=undefined){
				that.defaultActive = dataType;
				that.currentPage = 1;
			}
			ms.http.post(ms.manager+"/attention/collectionLog/list.do", {
				pageNo:that.currentPage,
				pageSize:that.pagesize,
				dataId: that.form.dataId,
				dataType: dataType == undefined?that.defaultActive:dataType,
				collectionDataTitle: that.form.collectionDataTitle,
			}).then(
					function(res) {
						if (!res.result||res.data.total <= 0) {
							that.emptyText ="暂无数据"
							that.dataList = [];
							that.total = 0;
						} else {
							that.emptyText = '';
							that.total = res.data.total;
							that.dataList = res.data.rows;
						}
						that.loading = false;
					}).catch(function(err) {
                        that.loading = false;
                        console.log(err);
                    });
		},
		//关注记录列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
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
		//消息发送类型  列表格式化
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

	},
	created:function(){
		this.getDataType()
		if(history.state){
			this.form = history.state.form;
			this.currentPage = history.state.page.pageNo;
			this.pageSize = history.state.page.pageSize;
		}
	},
})
</script>
<style>
	#index .ms-container {
		height: calc(100vh - 78px);
	}
</style>
