<!DOCTYPE html>
<html>
<head>
	<title>进度日志</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<el-header class="ms-header ms-tr" height="50px">
			<el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
		</el-header>
		<el-main class="ms-container">
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>

                 <el-table-column label="进度名称"   align="left" prop="plNodeName">
                 </el-table-column>
                 <el-table-column label="操作人"   align="left" prop="plOperator">
                 </el-table-column>
                 <el-table-column label="状态"  :formatter="progressLogStatusFormat"  align="left" prop="plStatus">
                 </el-table-column>
				 <el-table-column label="操作日期"  align="left" prop="updateDate">
				 </el-table-column>
                 <el-table-column label="操作内容"   align="left" prop="plContent">
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
		dataList: [], //进度日志列表
		selectionList:[],//进度日志列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
		plStatusOptions: [{
			"value": "adopt",
			"label": "通过"
		}, {
			"value": "reject",
			"label": "不通过"
		}],
		//搜索表单
		form:{
			sqlWhere:null,
			dataId:"",
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
			ms.http.post(ms.manager+"/progress/progressLog/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
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
		//进度日志列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},


        //表格数据转换
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

		//状态数据转换
		progressLogStatusFormat: function (row, column, cellValue) {
			var value = "";

			if (cellValue) {
				var data = this.plStatusOptions.find(function (value) {
					return value.value == cellValue;
				});

				if (data && data.label) {
					value = data.label;
				}
			}

			return value;
		},

	},
created:function(){
	this.form.dataId = ms.util.getParameter("dataId");
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
