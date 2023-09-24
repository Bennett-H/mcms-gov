<!DOCTYPE html>
<html>
<head>
	<title>采集规则</title>
	<#include "../../include/head-file.ftl">
	<script src="${base}/static/plugins/sockjs/1.4.0/sockjs.min.js"></script>
	<script src="${base}/static/plugins/stomp/2.3.3/stomp.min.js"></script>
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
		<ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
			<el-header class="ms-header" height="50px">
					<el-col :span="12">

						<@shiro.hasPermission name="spider:taskRegular:up">
						<el-button type="primary" icon="el-icon-video-play" size="mini" @click="startUp(selectionList)"  :disabled="!selectionList.length" :loading="startUpDisabled">启动</el-button>
						</@shiro.hasPermission>

						<@shiro.hasPermission name="spider:taskRegular:save">
							<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
						</@shiro.hasPermission>

						<@shiro.hasPermission name="spider:taskRegular:del">
						<el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
						</@shiro.hasPermission>

					</el-col>

					<el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)" style="float: right">返回</el-button>

			</el-header>
		<el-main class="ms-container">
			<el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
				<el-table-column type="selection" width="40"></el-table-column>

                 <el-table-column width="100" label="规则名称" align="left" prop="regularName" :show-overflow-tooltip="true">
                 </el-table-column>
<#--                <el-table-column label="线程数" align="center" prop="threadNum">-->
<#--                </el-table-column>-->
<#--            <el-table-column label="字符编码" align="left" prop="charset" :formatter="charsetFormat">-->
<#--            </el-table-column>-->
                 <el-table-column width="300" label="被采集页面" align="left" prop="linkUrl" :show-overflow-tooltip="true">
                 </el-table-column>
<#--            <el-table-column label="是否分页" align="center" prop="isPage" :formatter="isPageFormat">-->
<#--            </el-table-column>-->
<#--                 <el-table-column label="起始页" align="center" prop="startPage" >-->
<#--                 </el-table-column>-->
<#--                 <el-table-column label="结束页" align="center" prop="endPage">-->
<#--                 </el-table-column>-->
                 <el-table-column width="200" label="列表开始区域" align="left" prop="startArea" :show-overflow-tooltip="true">
                 </el-table-column>
                 <el-table-column width="200" label="列表结束区域" align="left" prop="endArea" :show-overflow-tooltip="true">
                 </el-table-column>
                 <el-table-column  label="内容链接匹配" align="left" prop="articleUrl" :show-overflow-tooltip="true">
                 </el-table-column>
<#--                <el-table-column label="元数据" align="left" prop="metaData" :show-overflow-tooltip="true">-->
<#--                </el-table-column>-->

				<el-table-column label="操作" width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="spider:taskRegular:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="spider:taskRegular:test">
						<el-link type="primary" :underline="false" @click="showTestForm(scope.row)">测试</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="spider:taskRegular:del">
						<el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
						</@shiro.hasPermission>
						<el-link type="primary" :underline="false" @click="newLine(scope.row,scope.$index)">复制</el-link>

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

	<#include "/spider/task-regular/test.ftl">
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		//任务绑定的表名
		tableName:'',
		//任务名
		taskId:'',
		//webSocket客户端
		stompClient: null,
		//当前用户名
		curUser:'',
		startUpDisabled:false,
		conditionList:[
        {action:'and', field: 'task_id', el: 'eq', model: 'taskId', name: '任务主键', type: 'input'},
        {action:'and', field: 'regular_name', el: 'eq', model: 'regularName', name: '规则名称', type: 'input'},
        {action:'and', field: 'thread_num', el: 'eq', model: 'threadNum', name: '线程数', type: 'number'},
            {action:'and', field: 'charset', el: 'eq', model: 'charset', name: '字符编码', key: 'value', title: 'label', type: 'select', multiple: false},
        {action:'and', field: 'link_url', el: 'eq', model: 'linkUrl', name: '被采集页面', type: 'input'},
            {action:'and', field: 'is_page', el: 'eq', model: 'isPage', name: '是否分页', key: 'value', title: 'label', type: 'radio', multiple: false},
        {action:'and', field: 'start_page', el: 'eq', model: 'startPage', name: '起始页', type: 'input'},
        {action:'and', field: 'end_page', el: 'eq', model: 'endPage', name: '结束页', type: 'input'},
        {action:'and', field: 'start_area', el: 'eq', model: 'startArea', name: '列表开始区域', type: 'textarea'},
        {action:'and', field: 'end_area', el: 'eq', model: 'endArea', name: '列表结束区域', type: 'textarea'},
        {action:'and', field: 'article_url', el: 'eq', model: 'articleUrl', name: '内容链接匹配', type: 'input'},
        {action:'and', field: 'filed_name', el: 'eq', model: 'filedName', name: '字段匹配名称', type: 'input'},
            {action:'and', field: 'filed_regula', el: 'eq', model: 'filedRegula', name: '字段匹配规则', key: 'dictValue', title: 'dictLabel', type: 'radio', multiple: false},
        {action:'and', field: 'filed_text', el: 'eq', model: 'filedText', name: '字段匹配', type: 'input'},
            {action:'and', field: 'filed_dfiled', el: 'eq', model: 'filedDfiled', name: '关联表列名', key: 'dictValue', title: 'dictLabel', type: 'select', multiple: false},
		],
		conditions:[],
		dataList: [], //采集规则列表
		selectionList:[],//采集规则列表选中
		total: 0, //总记录数量
        pageSize: 10, //页面数量
        currentPage:1, //初始页
        manager: ms.manager,
		loadState:false,
		loading: true,//加载状态
		emptyText:'',//提示文字
                charsetOptions:[{"value":"UTF-8","label":"UTF-8"},{"value":"GBK","label":"GBK"},{"value":"custom","label":"自适应"}],
                isPageOptions:[{"value":"yes","label":"是"},{"value":"no","label":"否"}],
                filedRegulaOptions:[],
                filedDfiledOptions:[],
		//搜索表单
		form:{
			sqlWhere:null,
		},
	},
	watch:{
	},
	mounted() {
		this.initWebSocket()
	},
	methods:{
		showTestForm:function(row){
			//open中清除数据
			form.open(row,this.stompClient,this.curUser);
		},
		initWebSocket() {
			this.connection()
			// 需要有一个失败重连得到问题
		},
		connection() {
			// 更换that指针
			const socket = new SockJS(ms.base + '/websocket');
			this.stompClient = Stomp.over(socket)
			//建立连接，订阅主题
			this.stompClient.connect({}, (frame) => {
				this.stompClient.subscribe('/user/spider/publish', (val) => {
				 var msg = JSON.parse(val.body);
					//URL("11")服务端通知客户端解析出 目标链接的url
					//CONTENT("12")服务端通知客户端解析出 内容数据
					//FINISH("13")服务端通知客户端爬取内容完成
				 if (msg.action == "11"){
					 msg.links.forEach(u=>{
						 form.appendLinkUrl(u);
					 });
				 }else if(msg.action == "12"){
					 msg.links.forEach(u=>{
						 form.appendContentUrl(u);
					 });
					 form.appendData(JSON.parse(msg.data))
				 }else if (msg.action == "13"){
				 	form.finish();
				 }
			})

			})
			// 回调函数 3 end
		},
		//启动
		startUp:function(row){
			var that = this;
			that.$confirm('确定开启采集, 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(function() {
				that.startUpDisabled = true;
				ms.http.post(ms.manager+"/spider/task/start/"+ that.taskId , row.length?row:[row],{
					headers: {
						'Content-Type': 'application/json'
					}
				}).then(function(res){
							if (res.result) {
								that.$notify({
									title: '成功',
									type: 'success',
									message: '系统正在采集,需要稍等片刻等待后台采集完成!'
								});
								//删除成功，刷新列表
								that.list();
							}else {
								that.$notify({
									title: '采集失败',
									message: res.msg,
									type: 'warning'
								});
							}
					that.startUpDisabled = false;
				});
			})
		},
		//复制一列
		newLine: function (row, index) {
			var that = this
			var url = ms.manager + "/spider/taskRegular/save.do"
			var data = JSON.parse(JSON.stringify(row));
			data.id = ''
			data.regularName = data.regularName + (new Date()).getTime();
			ms.http.post(url, data).then(function (data) {
				if (data.result) {
					that.$notify({
						title: '成功',
						message: '复制成功',
						type: 'success'
					});
					that.list();
				} else {
					that.$notify({
						title: '失败',
						message: data.msg,
						type: 'warning'
					});
				}
			});


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
			ms.http.post(ms.manager+"/spider/taskRegular/list.do",form.sqlWhere?Object.assign({
				taskId: that.taskId
					},{sqlWhere:form.sqlWhere}, page)
				:Object.assign({
				taskId: that.taskId
					},form, page)).then(
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
		//采集规则列表选中
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
					    	ms.http.post(ms.manager+"/spider/taskRegular/delete.do", row.length?row:[row],{
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
			if(id){
				location.href=this.manager+"/spider/taskRegular/form.do?id="+id+"&taskId="+this.taskId+"&tableName="+this.tableName;
			}else {
				location.href=this.manager+"/spider/taskRegular/form.do?taskId="+this.taskId+"&tableName="+this.tableName;
			}
        },
        //表格数据转换
		charsetFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.charsetOptions.find(function(value){
					return value.value==cellValue;
				})
				if(data&&data.label){
					value = data.label;
				}
			}
            return value;
		},
		isPageFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.isPageOptions.find(function(value){
					return value.value==cellValue;
				})
				if(data&&data.label){
					value = data.label;
				}
			}
            return value;
		},
		filedRegulaFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.filedRegulaOptions.find(function(value){
					return value.dictValue==cellValue;
				})
				if(data&&data.dictLabel){
					value = data.dictLabel;
				}
			}
            return value;
		},
		filedDfiledFormat:function(row, column, cellValue, index){
			var value="";
			if(cellValue){
				var data = this.filedDfiledOptions.find(function(value){
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

	},
	created:function(){
		this.curUser = '${Session['manager_session'].managerName}';
		if(history.hasOwnProperty("state")&& history.state){
			this.form = history.state.form;
			this.currentPage = history.state.page.pageNo;
			this.pageSize = history.state.page.pageSize;
		}
		var tableName = ms.util.getParameter("tableName");
		if (tableName) {
			this.tableName = tableName;
		}
		var taskId = ms.util.getParameter("taskId");
		if (taskId) {
			this.taskId = taskId;
		}
		this.list();
		},
	beforeDestroy:function(){
		if (this.stompClient){
			this.stompClient.disconnect();
		}
	},
	})
</script>
<style>
	#index .ms-container {
		height: calc(100vh - 78px);
	}
</style>
