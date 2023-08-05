<!DOCTYPE html>
<html>
<head>
	<title>发送日志</title>
		<link rel="stylesheet" href="${base}/static/iconfont/iconfont.css"/>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" v-cloak class="ms-index">
		<div class="ms-search" style="padding: 20px 10px 0 10px;">
			<el-row>
				<el-form :model="searchForm"  ref="searchForm"  label-width="90px" size="mini">
					<el-row>
						<el-col :span="8">
							<el-form-item  label="接收人" prop="logReceive">
								<el-input v-model="searchForm.logReceive"
										  :disabled="false"
										  :style="{width:  '100%'}"
										  :clearable="true"
										  placeholder="请输入接收人">
								</el-input>
							</el-form-item>
						</el-col>
						<el-col :span="8">
							<el-form-item label="日志类型" prop="logType">
								<el-select v-model="searchForm.logType" class="ms-select" filterable placeholder="请选择日志类型" size="mini">
									<el-option v-for="item in logTypeList" :key="item.id" :data-label='item.id' :label="item.value" :value="item.id"></el-option>
								</el-select>
							</el-form-item>
						</el-col>
						<el-col :span="8" style="text-align: right">
							<el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list()">查询</el-button>
							<el-button @click="clear"  icon="el-icon-refresh" size="mini">重置</el-button>
						</el-col>
					</el-row>
				</el-form>
			</el-row>
		</div>
		<el-main class="ms-container" style="height: calc(100vh - 91px);">
			<el-table  height="calc(100% - 68px)" ref="multipleTable" class="ms-table-pagination" border :data="treeList" tooltip-effect="dark" >
            <el-table-column label="日志类型" width="200" align="center" prop="logType" :formatter="formatterType">
            </el-table-column>
            <el-table-column label="接收人" width="200" align="left" prop="logReceive">
            </el-table-column>
            <el-table-column label="接收内容" align="left" prop="logContent">
            </el-table-column>
            <el-table-column label="发送时间" width="200" align="center" prop="logDatetime">
            </el-table-column>
			</el-table>
            <el-pagination
					background
					:page-sizes="[50, 100, 200]"
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
	data:function(){
		return{
			treeList: [], //发送日志列表
			selectionList:[],//发送日志列表选中
			total: 0, //总记录数量
			pageSize: 50, //页面数量
			currentPage:1, //初始页
			logTypeList:[{"id":"sms","value":"短信"},{"id":"mail","value":"邮件"}],
			mananger: ms.manager,
			//搜索表单
			searchForm :{
				// 日志类型
				logType:'',
				// 接收人
				logReceive:'',
				// 接收内容
				logContent:'',
				// 接收内容
				logDatetime:'',
			},
		}
	},
	methods:{
	    //查询列表
	    list: function() {
	    	var that = this;
			that.searchForm.pageNo = that.currentPage;
			that.searchForm.pageSize = that.pageSize;
	    	ms.http.get(ms.manager+"/msend/log/list.do",that.searchForm).then(
				function(data){
						that.total = data.data.total;
						that.treeList = data.data.rows;
					}).catch(function(err){
						console.log(err);
					});
				},
		formatterType:function(row,index){
			switch(row.logType){
				case "sms" : return "短信";break;
				case "mail" : return "邮件";break;
			}
		},
        //pageSize改变时会触发
        sizeChange:function(pagesize) {
            this.pageSize = pagesize;
            this.list();
        },
        //currentPage改变时会触发
        currentChange:function(currentPage) {
            this.currentPage = currentPage;
            this.list();
        },
		//重置表单
		clear:function(){
			this.$refs.searchForm.resetFields();
			this.list();
		}
	},
	mounted:function(){
		this.list();
	},
})
</script>
