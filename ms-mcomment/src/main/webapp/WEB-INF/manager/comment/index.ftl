<#-- 重写原因:因为评论用户信息查询不通用-->
<!DOCTYPE html>
<html>
<head>
	<title>评论</title>
	<#include "../../include/head-file.ftl">
</head>
<body>
<div id="comment" v-cloak class="ms-index">
	<el-header class="ms-header" height="50px">
		<el-col :span="24">
			<@shiro.hasPermission name="comment:comment:save">
				<el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
			</@shiro.hasPermission>
			<@shiro.hasPermission name="comment:comment:del">
				<el-button type="danger" icon="el-icon-delete" size="mini" style="margin-right: 8px"
						   @click="del(commentSelectionList)"  :disabled="!commentSelectionList.length">删除</el-button>
			</@shiro.hasPermission>
			<el-button type="primary" plain
					   icon="el-icon-document-copy" size="mini" class="copyBtn" style="float: right; "
					   :data-clipboard-text="JSON.stringify(menuJson)" @click="copyMenuJson()">复制菜单JSON
			</el-button>
		</el-col>
	</el-header>
	<div class="ms-search">
		<el-form :model="searchForm"  ref="searchForm" :rules="rules"  label-width="120px" size="mini">
			<el-row>
				<el-col :span='4'>
					<el-form-item label="评论者编号" prop="peopleId">
						<el-input v-model="searchForm.peopleId" clearable placeholder="请输入评论者编号"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span='4'>
					<el-form-item label="评论者昵称" prop="puNickname">
						<el-input v-model="searchForm.puNickname" clearable placeholder="请输入昵称"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span='4'>
					<el-form-item  label="被评论数据" prop="dataTitle">
						<el-input v-model="searchForm.dataTitle" clearable placeholder="请输入被评论数据(支持模糊查询)"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span='4'>
					<!--下拉选择框-->
					<el-form-item  label="审核状态" prop="commentAudit">
						<el-select v-model="searchForm.commentAudit"
								   :style="{width: '100%'}"
								   :filterable="false"
								   :disabled="false"
								   :multiple="false" :clearable="true"
								   placeholder="请选择审核状态">
							<el-option v-for='item in commentAuditOptions' :key="item.value" :value="item.value"
									   :label="item.label"></el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span='7'>
					<el-form-item label="评价时间" prop="commentTime">
						<el-date-picker
								class="ms-datetimerange"
								size="mini"
								v-model="searchForm.commentDateTimes"
								type="daterange"
								start-placeholder="开始日期"
								end-placeholder="结束日期"
								align="right"
								format="yyyy-MM-dd"
								value-format="yyyy-MM-dd"
						>
						</el-date-picker>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col style="text-align: right;padding:  8px 0">
					<el-button type="primary" icon="el-icon-search" size="mini" @click="loading=true;list()">查询</el-button>
					<el-button @click="resetForm()" size="mini"><i class="el-icon-refresh"></i>重置</el-button>
				</el-col>
			</el-row>
		</el-form>
	</div>
	<el-main class="ms-container">
		<div style="display:flex;flex-direction: row;flex:1">
			<div style="overflow: hidden; background-color: rgb(242, 246, 252);  display: flex;position: relative;margin-right: 10px;flex-shrink: 0;">
				<el-scrollbar>
					<el-aside width="160px" style="background-color: #f2f6fc;">

						<el-menu
								:default-active="defaultActive"
								v-if="dataTypeOptions && dataTypeOptions.length">
							<template v-for="(item,i) in dataTypeOptions">
								<!--单个选项-->
								<el-menu-item
										:index="item.dictLabel"
										@click="list(item.dictLabel)"
										v-text="item.dictLabel">
								</el-menu-item>
							</template>
						</el-menu>
					</el-aside>
				</el-scrollbar>
			</div>
			<div style="display: flex;flex-direction: column;flex: 1;">
				<el-table v-loading="loading" height="calc(100% - 68px)" class="ms-table-pagination"
						  ref="multipleTable" :data="commentDataList" border tooltip-effect="dark"
						  @selection-change="commentHandleSelectionChange">
					<template slot="empty">
						{{emptyText}}
					</template>
					<el-table-column type="selection" width="40"></el-table-column>
					<el-table-column type="expand">
						<template slot-scope="scope">
							<el-form label-position="left" inline class="comment-table-expand">
								<el-form-item label="评论内容" style="display: inline;">
									<span>{{ scope.row.commentContent }}</span>
								</el-form-item>
								<el-form-item label="评论图片" v-if="scope.row.commentPicture" style="display: inline;">
								<span v-for="(picture,index) in JSON.parse(scope.row.commentPicture)">
									<el-image :preview-src-list="[ms.base+picture.path]" :src="ms.base + picture.path"
											  style="width: 50px;height: 50px;line-height: 50px;font-size: 30px; margin-right: 10px">
										<template slot="error" class="image-slot">
											<i class="el-icon-picture-outline"></i>
										</template>
									</el-image>
								</span>
								</el-form-item>
								<el-form-item label="评论附件" v-if="scope.row.commentFileJson" style="display: inline;">
								<span v-for="(commentFile,index) in JSON.parse(scope.row.commentFileJson)">
									<a :href="commentFile.path" target="_blank" style="margin-right: 10px">{{commentFile.name}}</a>
								</span>
								</el-form-item>
								<el-form-item label="评论点赞数" style="display: inline;">
									<span>{{ scope.row.commentLike }}</span>
								</el-form-item>
							</el-form>
						</template>
					</el-table-column>
					<el-table-column label="评论者编号" prop="peopleId" width="100"></el-table-column>
					<el-table-column label="评论者昵称" prop="peopleName" width="150">
						<template slot-scope="scope">
							{{scope.row.peopleName?scope.row.peopleName:'匿名用户'}}
						</template>
					</el-table-column>
					<el-table-column label="被评论数据" prop="dataTitle" show-overflow-tooltip></el-table-column>
					<el-table-column label="评论内容" prop="commentContent" show-overflow-tooltip></el-table-column>
					<el-table-column label="评分等级" width="150" align="center">
						<template slot-scope="scope">
							<el-rate v-model="scope.row.commentPoints" disabled allow-half
									 text-color="#ff9900"></el-rate>
						</template>
					</el-table-column>
					<el-table-column label="所在地区" width="180" prop="commentIp" align="center"></el-table-column>
					<el-table-column label="评价时间" width="180" prop="commentTime" align="center"></el-table-column>
					<el-table-column label="状态" width="180" prop="commentAudit" align="center">
						<template slot-scope="scope">
							<el-tag v-if="scope.row.commentAudit" type="success">
								已审核
							</el-tag>
							<el-tag v-else type="danger">
								未审核
							</el-tag>
						</template>
					</el-table-column>
					<el-table-column label="操作" width="200" align="center">
						<template slot-scope="scope">
							<@shiro.hasPermission name="comment:comment:audit">
								<el-link v-if="scope.row.commentAudit" type="primary" :underline="false"
									@click="audit(scope.row, false)">不通过
								</el-link>
								<el-link v-else type="primary" :underline="false"
									@click="audit(scope.row, true)">通过
								</el-link>
							</@shiro.hasPermission>
							<@shiro.hasPermission name="comment:comment:update">
								<el-link type="primary" :underline="false"
										 @click="save(scope.row.id)">编辑
								</el-link>
							</@shiro.hasPermission>
							<@shiro.hasPermission name="comment:comment:reply">
								<el-link type="primary" :underline="false"
										 :href="manager+'/comment/reply.do?id='+scope.row.id">回复
								</el-link>
							</@shiro.hasPermission>
							<@shiro.hasPermission name="comment:comment:del">
								<el-link type="primary" :underline="false" @click="del([scope.row])">删除
								</el-link>
							</@shiro.hasPermission>
							<el-badge :value="scope.row.unAuditedChildCommentCount" :max="99" class="item" style=" margin-top: 10px;margin-right: 40px;" v-if="scope.row.unAuditedChildCommentCount > 0">
								<el-link type="primary" :underline="false" :href="manager+'/comment/reply.do?id='+scope.row.id">待审子评论数
								</el-link>
							</el-badge>
						</template>
					</el-table-column>
				</el-table>
				<el-pagination
						background
						:page-sizes="[5, 10, 20, 50]"
						layout="total, sizes, prev, pager, next, jumper"
						:current-page="currentPage"
						:page-size="pageSize"
						:total="total"
						class="ms-pagination"
						@current-change='currentChange'
						@size-change="sizeChange">
				</el-pagination>
			</div>
		</div>
	</el-main>
</div>
</body>

</html>
<script>
	var commentVue = new Vue({
		el: '#comment',
		data:function(){return{
			dataTypeOptions: [],//评论类型
			defaultActive: '',
			commentDataList: [], //评论表列表
			commentSelectionList:[],//评论表列表选中
			total: 0, //总记录数量
			pageSize: 50, //页面数量
			currentPage:1, //初始页
			manager: ms.manager,
			loading: true,
			emptyText:'',
			//菜单json
			menuJson:[
				{
					modelIsMenu:1,
					modelTitle:"",
					modelUrl:"",
					modelChildList:[
						{
							modelIsMenu:0,
							modelTitle:"查看",
							modelUrl:""
						},
						{
							modelIsMenu:0,
							modelTitle:"审核",
							modelUrl:""
						},
						{
							modelIsMenu:0,
							modelTitle:"回复",
							modelUrl:""
						},
						{
							modelIsMenu:0,
							modelTitle:"删除",
							modelUrl:""
						}
					]
				}
			],
			//搜索表单
			searchForm:{
				peopleId:null,
				puNickname:null,
				commentTime: null,
				commentDateTimes:null,
				commentAudit:null,
				dataTitle:null,
				dataType: null
			},

			rules: {
				// 被评论数据
				dataTitle: [{
					min: 0,
					max: 40,
					message: "评论内容长度限制为0-40个字符"
				}],
				// 评论者昵称
				puNickname: [{
					min: 0,
					max: 30,
					message: '评论者昵称长度为0-30个字符！',
				}],
				// 评论者编号
				peopleId: [{
					min: 0,
					max: 20,
					message: '评论者编号长度为0-20个字符!',
				}],
			},

			searchIsExpand: true,
			pickerOptions: {
				shortcuts: [{
					text: '最近一周',
					onClick(picker) {
						const end = new Date();
						const start = new Date();
						start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
						picker.$emit('pick', [start, end]);
					}
				}, {
					text: '最近一个月',
					onClick(picker) {
						const end = new Date();
						const start = new Date();
						start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
						picker.$emit('pick', [start, end]);
					}
				}, {
					text: '最近三个月',
					onClick(picker) {
						const end = new Date();
						const start = new Date();
						start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
						picker.$emit('pick', [start, end]);
					}
				}]
			},
			commentAuditOptions: [{"value": "0", "label": "未审核"}, {"value": "1", "label": "已审核"}],
			options:[{
				value: 1,
				label: '匿名'
			}, {
				value: 2,
				label: '公开'
			}]
		}},
		computed:{
			//表格最大高度 用来自适应
			tableHeight:function () {
				return document.documentElement.clientHeight - 171;
			}
		},
		methods:{
			//复制菜单json
			copyMenuJson: function () {
				if(this.commentDataList.length==0){
					this.$notify.error({
						title: '错误',
						message: '当前没有评论记录',
						type: 'warning'
					});
					return;
				}
				var dataType = this.commentDataList[0].dataType
				var data = this.dataTypeOptions.find(function (data) {
					return data.dictValue == dataType;
				})
				this.menuJson[0].modelChildList[0].modelUrl = 'comment:dataData:'+dataType+':view'
				this.menuJson[0].modelChildList[1].modelUrl = 'comment:dataData:'+dataType+':audit'
				this.menuJson[0].modelChildList[2].modelUrl = 'comment:dataData:'+dataType+':reply'
				this.menuJson[0].modelChildList[3].modelUrl = 'comment:dataData:'+dataType+':del'
				this.menuJson[0].modelTitle = data.dictLabel;
				this.menuJson[0].modelUrl="/comment/data/index.do?dataType="+data.dictLabel;

				var clipboard = new ClipboardJS('.copyBtn');
				var self = this;
				clipboard.on('success', function (e) {
					self.$notify({
						title: '提示',
						message: '菜单json已保存到剪切板，可在菜单管理中导入',
						type: 'success'
					});
					clipboard.destroy();
				});
			},
			save:function (id) {
				if (id){
					ms.util.openSystemUrl("/comment/form.do?dataType=" + this.defaultActive +"&id="+id);
				}else {
					ms.util.openSystemUrl("/comment/form.do?dataType=" + this.defaultActive );
				}
			},
			getCommentType:function() {
				var that = this;
				ms.http.get(ms.base+'/mdiy/dict/list.do', {dictType:'评论类型',pageSize:99999}).then(function (res) {
					that.dataTypeOptions = res.data.rows;
					var dataTypeParam = ms.util.getParameter("dataType");
					// that.defaultActive = that.searchForm.dataType = dataTypeParam == null && that.dataTypeOptions.length > 0 ? that.dataTypeOptions[0].dictValue : dataTypeParam;
					that.defaultActive = dataTypeParam == null && that.dataTypeOptions.length > 0 ? that.dataTypeOptions[0].dictLabel : dataTypeParam;
					that.list(that.defaultActive);
				}).catch(function (err) {
					console.log(err);
				});
			},
			//查询列表
			list: function(dataType) {
				var that = this;
				if (dataType){
					that.searchForm.dataType = dataType;
					that.defaultActive = dataType;
					that.currentPage = 1;
				}
				that.loading = true;
				that.loadState = false;
				var times= '';
				//将时间选择器的值转成字符串
				if(that.searchForm.commentDateTimes != null){
					times =  that.searchForm.commentDateTimes[0] + "至"+that.searchForm.commentDateTimes[1];
				}
				ms.http.post(ms.manager+"/comment/list.do",{
					pageNo:that.currentPage,
					pageSize:that.pagesize,
					peopleId:that.searchForm.peopleId,
					peopleName: that.searchForm.puNickname,
					commentAudit:that.searchForm.commentAudit,
					commentDateTimes:times,
					dataType: that.searchForm.dataType,
					dataTitle: that.searchForm.dataTitle,
				}).then(function(res){
					times='';
					if(res.result){
						if(res.data.total <=0){
							that.loading = false;
							that.emptyText='暂无数据'
							that.commentDataList =[];
						}else{
							that.emptyText='';
							that.total = res.data.total;
							var _commentDataList = res.data.rows;
							// 循环取出用户信息
							_commentDataList.forEach(function (commentData) {
								// 处理会员相关
								if (commentData.peopleInfo){
									var peopleInfo = JSON.parse(commentData.peopleInfo);
									// commentData.puNickname = peopleInfo.puNickname;
									commentData.puIcon = peopleInfo.puIcon;
								}
								// 处理ip
								if (commentData.commentIp){
									commentData.commentIp = JSON.parse(commentData.commentIp).addr;
								}
							});
							that.commentDataList = _commentDataList;
							that.loading = false;
						}
					}
				}).catch(function(err){
					console.log(err);
				});
			},
			//评论表列表选中
			commentHandleSelectionChange:function(val){
				this.commentSelectionList = val;
			},
			//删除
			del: function(row){
				var that = this;
				that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(function() {
					ms.http.post(ms.manager+"/comment/delete.do", row.length?row:[row],{
						headers: {
							'Content-Type': 'application/json'
						}
					}).then(
							function(res){
								if (res.result) {
									that.$notify({
										title:'成功',
										type: 'success',
										message: '删除成功!'
									});
									//删除成功，刷新列表
									that.list(that.defaultActive);
								}else {
									that.$notify({
										title: '失败',
										message: data.msg,
										type: 'warning'
									});
								}
							});
				}).catch(function() {
					that.$notify({
						type: 'info',
						title:'成功',
						message: '已取消删除'
					});
				});
			},
			//审核
			audit: function(row, commentAudit){
				var that = this;
				row.commentAudit = commentAudit;
				ms.http.post(ms.manager + "/comment/updateComment.do",row	, {
					headers: {
						'Content-Type': 'application/json'
					}
				}).then(function(res) {
					if (res.result) {
						if (commentAudit) {
							that.$notify({
								title: '成功',
								message: '修改为审核通过!',
								type: 'success'
							});
						}else {
							that.$notify({
								title: '成功',
								message: '修改为审不通过!',
								type: 'success'
							});
						}
					}
				});
			},
			//pageSize改变时会触发
			sizeChange:function(pagesize) {
				this.loading = true;
				this.pagesize = pagesize;
				this.list();
			},
			//currentPage改变时会触发
			currentChange:function(currentPage) {
				this.loading = true;
				this.currentPage = currentPage;
				this.list();
			},
			//清空搜索项
			resetForm:function() {
				this.$refs["searchForm"].resetFields();
				this.searchForm.commentDateTimes=null;
				this.loading = true;
				this.list();
			},
		},
		mounted:function(dataType){
			// this.getDataType(dataType)
			this.getCommentType();
		},
	})
</script>
<style>
	body{
		overflow: hidden
	}
	#comment  a {
		color: #0099ff;
		cursor: pointer;
	}
	#comment .iconfont {
		font-size: 12px;
		margin-right: 5px;
	}

	#comment .el-date-editor--datetime.el-input--mini{
		width:100%;
	}

	#comment .comment-table-expand {
		font-size: 0;
		margin: 10px;
	}
	#comment .comment-table-expand  label {
		width: 90px;
		color: #99a9bf;
	}
	#comment .comment-table-expand el-form-item {
		margin-right: 0;
		margin-bottom: 0;
		width: 50%;
	}
</style>