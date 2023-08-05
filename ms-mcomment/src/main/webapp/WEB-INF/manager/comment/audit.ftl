<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>评论审核</title>
	<#include "/include/head-file.ftl"/>

</head>

<body>
<div id="form" v-cloak>
	<el-header class="ms-header ms-tr" height="50px">

		<@shiro.hasPermission name="comment:comment:audit">
		<el-button  v-if="comment.commentAudit ==1" type="primary" icon="el-icon-check" size="mini" @click="pass()">审核通过</el-button>
		<el-button v-if="comment.commentAudit ==0" type="primary" icon="el-icon-close"  size="mini" @click="noPass()">取消审核</el-button>
		</@shiro.hasPermission>
		<el-button size="mini" icon="iconfont icon-fanhui" plain @click="back">返回</el-button>
	</el-header>
	<el-main class="ms-container">
		<el-scrollbar class="ms-scrollbar" style="height: 100%;">
			<el-divider content-position="left">评论详情</el-divider>
			<el-row>
				<el-col :span="24">评价者 ：<span v-text="comment.puNickname"></span></el-col>
			</el-row>
			<el-row>
				<el-col :span="24">评价时间：<span v-text="comment.commentTime"></span></el-col>
			</el-row>
			<el-row>
				<el-col :span="2">评分 ：</el-col>
				<el-col :span="22">
					<el-rate v-model="comment.commentPoints" disabled show-text :texts="comment.texts"></el-rate>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="24">评价内容 ：{{comment.commentContent}}
				</el-col>
			</el-row>
			<el-row class="background-display" v-if="comment.commentPicture">
				<el-col :span="2">
					<el-image style="width: 100px; height: 100px" :src="comment.commentPicture" fit="cover">
					</el-image>
				</el-col>
				<#--					  <el-col :span="22">-->
				<#--					  	 <div style="padding-left: 20px;" v-model="comment.basicTitle">{{comment.basicTitle}}</div>-->
				<#--					  </el-col>-->
			</el-row>

			<el-divider content-position="left">评价回复记录</el-divider>
			<div style="min-height: 300px;" v-loading="loading">
				<el-row v-for="item in replyList" >
					<el-col :span="24">{{!item.createBy?'匿名用户 于':(item.peopleId==0?'管理员 于':item.puNickname)}}({{item.commentTime}}) 回复：<span
								v-text="item.commentContent"></span>
					</el-col>
				</el-row>
			</div>

		</el-scrollbar>
	</el-main>
</div>
</body>


</html>
<script>
	var replyVue = new Vue({
		el: '#form',
		data: {
			loading: true,
			commentId: 0, //评论编号
			comment: {
				texts: ['1分', '2分', '3分', '4分', '5分'],		//评分辅助提示文字
				orderNum: '',		//订单号
				againValuate: '',			//追加评价
				commentContent: '' //评价商品
			},
			replyComment: {},
			replyList: null,
			rules: {
				//标签名
				'replyComment.commentContent': [{
					"required": true,
					"message": "标签名必须填写"
				}]
			}
		},
		methods: {
			//返回
			back: function () {
				window.history.back(-1);
			},
			//通过
			pass: function(){
				this.audit(0);
			},
			//不通过
			noPass: function(){
				this.audit(1);
			},
			//审核
			audit: function(commentAudit){
				var that = this;
				that.comment.commentAudit = commentAudit;
				ms.http.post(ms.manager + "/comment/updateComment.do",that.comment, {
					headers: {
						'Content-Type': 'application/json'
					}
				}).then(function(data) {
					if (data.result) {
						if (commentAudit == 1){
							that.$notify({
								title: '成功',
								message: '修改为审核不通过!',
								type: 'success'
							});
						}else if (commentAudit ==0){
							that.$notify({
								title: '成功',
								message: '修改为审核通过!',
								type: 'success'
							});
						}
					}
				});
			},
			//获取评论信息
			get: function (id) {
				let that = this;
				ms.http.get(ms.manager + "/comment/get.do", {
					id: id
				}).then(function (data) {
					if (data.data.id > 0) {
						that.comment = data.data;
						that.getByParentCommentId(that.comment);
					} else {
						that.$notify({
							title: '失败',
							message: data.msg,
							type: 'warning'
						});
					}
				});
			},
			//根据父评论id查询子评论
			getByParentCommentId: function (comment) {
				let that = this;
				ms.http.post(ms.manager + "/comment/list.do", {
					commentId: comment.id,
					pageSize: 999999
				}).then(function (data) {
					if (data.data.total > 0) {
						that.replyList = data.data.rows;
					}
					that.loading=false;
				});
			},
		},
		mounted() {
			this.id = ms.util.getParameter("id");
			this.get(this.id);
		}
	});
</script>
