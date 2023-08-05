<!-- 因为审批，重写进度 -->
<!DOCTYPE html>
<html>
<head>
	<title>审批级数表</title>
		<#include "../../include/head-file.ftl">
</head>
<body>
	<div id="index" class="ms-index" v-cloak>
			<el-header class="ms-header" height="50px">
			<el-col :span="12">
				<@shiro.hasPermission name="progress:progress:save">
				<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
				</@shiro.hasPermission>
			</el-col>
				<el-button style="float: right" v-if="!showBack" size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
			</el-header>

		<el-main class="ms-container">
            <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info">
				此功能新增审批节点时，需要注意修改对应配置描述，原先的审批配置可以在编辑进行描述修改,最后一个节点配置的ADOPT必须为终审通过。<br/>
				新增或删除节点时建议复制最后一个节点的配置,然后修改或删除原来的节点<br/>
				新增审批节点后需要在审批配置配置新增节点对应的角色权限
            </el-alert>
			<el-table ref="multipleTable"
					  height="calc(100vh - 20px)"
					  border :data="dataList"
					  row-key="id"
					  v-loading="loading"
					  default-expand-all='true'
					  :tree-props="{children: 'children'}"
					  tooltip-effect="dark"
					  @selection-change="handleSelectionChange">
				<template slot="empty">
					{{emptyText}}
				</template>
                 <el-table-column label="审批节点" align="left" width='120' prop="progressNodeName">
                 </el-table-column>
                 <el-table-column label="审批配置" align="left" prop="progressStatus">
                 </el-table-column>
                <el-table-column label="审批级数" align="right" width='120' prop="progressNumber">
                </el-table-column>
				<el-table-column label="操作" width="180" align="center">
					<template slot-scope="scope">
						<@shiro.hasPermission name="progress:progress:update">
						<el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
						</@shiro.hasPermission>
						<@shiro.hasPermission name="progress:progress:del">
						<el-link type="primary" v-if="dataList.length==scope.$index+1 && dataList.length!= 1" :underline="false" @click="del([scope.row])"> 删除</el-link>
						</@shiro.hasPermission>
					</template>
				</el-table-column>
			</el-table>
         </el-main>
	</div>
		<#include "/progress/progress/form.ftl">
</body>

</html>
<script>
var indexVue = new Vue({
	el: '#index',
	data:{
		showBack: '',
		dataList: [], //进度表列表
		selectionList:[],//进度表列表选中
		loading: true,//加载状态
		emptyText:'',//提示文字
		manager: ms.manager,
		loadState:false,
		//搜索表单
		form:{

		},
	},
	methods:{
		getSummaries: function(param) {
			var columns = param.columns;
			var data = param.data;
			var sums = [];
			columns.forEach(function (column, index) {
				if (index === 0) {
					sums[index] = '总价';
					return;
				}
				var values = data.map(function(item){return Number(item[column.property])}  );
				if (!values.every(function(value){ return isNaN(value)})) {
					sums[index] = values.reduce(function (prev, curr) {
						var value = Number(curr);
						if (!isNaN(value)) {
							return prev + curr;
						} else {
							return prev;
						}
					}, 0);
					sums[index] += ' 元';
				} else {
					sums[index] = 'N/A';
				}
			});

			return sums;
		},
	    //查询列表
	    list: function() {
	    	var that = this;
	    	this.loadState = false;
	    	this.loading = true;
			var id = ms.util.getParameter("id")
			ms.http.get(ms.manager+"/progress/progress/list.do",{schemeId:id,pageSize:9999}).then(
					function(res) {
						if(that.loadState){
							that.loading = false;
						}else {
							that.loadState = true
						}
						if (!res.result||res.data.total <= 0) {
							that.emptyText = '暂无数据'
							that.dataList = [];
						} else {
							that.emptyText = '';
							that.dataList = ms.util.treeData(res.data.rows,'id','progressId','children');
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
		//进度表列表选中
		handleSelectionChange:function(val){
			this.selectionList = val;
		},
		handleClose:function(done) {
			this.$refs.form.resetFields();
			done();
		},
		//删除
        del: function(row){
        	var that = this;
        	that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
					    	confirmButtonText: '确定',
					    	cancelButtonText: '取消',
					    	type: 'warning'
					    }).then(function()  {
					    	ms.http.post(ms.manager+"/progress/progress/delete.do", row.length?row:[row],{
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
											message: res.data.msg,
											type: 'warning'
										});
									}
	            				});
					    }).catch(function(){
					    	that.$notify({
                                title: '失败',
					        	type: 'info',
					        	message: '已取消删除'
					    	});
				    	});
        		},
		//表格数据转换
		//新增
        save:function(id){
            //  当没有传入id时表示新增级数，审批数量自动加一
            if(id==undefined) {
                form.form.progressNumber = this.dataList.length + 1
            }
			form.open(id)

        },
		//重置表单
		rest:function(){
			this.$refs.searchForm.resetFields();
		},
            getTree: function(){
                var that = this;
				var id = ms.util.getParameter("id")
                ms.http.get(ms.manager+"/progress/progress/list.do",{schemeId:id,pageSize:9999}).then(function(res){
                    if(res.result){
                        that.sourceList = res.data.rows;
                        that.treeList[0].children = ms.util.treeData(res.data.rows,'id','progressId','children');
                    }
                }).catch(function(err){
                    console.log(err);
                });
            },
	},
	created:function(){
		var showBack = ms.util.getParameter("showBack");
		if (showBack){
			this.showBack = showBack;
		}
            this.getTree()
		this.list();
	},
})
</script>
