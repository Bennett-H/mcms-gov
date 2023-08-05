<!DOCTYPE html>
<html>

<head>
	<title>评论表</title>
	<#include "../../include/head-file.ftl">

</head>

<body>
<div id="form" v-loading="loading" v-cloak>
	<el-header class="ms-header ms-tr" height="50px">
		<el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存</el-button>
		<el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
	</el-header>
	<el-main class="ms-container">
		<el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">

			<el-row
					gutter="0"
					justify="start" align="top">
				<el-col span="12">
					<!--评论者id-->
					<el-form-item  label="评论者id" prop="peopleId">
						<el-input
								v-model.number="form.peopleId"
								:disabled="false"
								:style="{width:  '100%'}"
								:clearable="true"
								placeholder="请输入评论者id">
						</el-input>
					</el-form-item>
				</el-col>
				<el-col span="12">
					<!--父评论id-->
					<el-form-item  label="父评论id" prop="commentId">
						<el-input
								v-model="form.commentId"
								:disabled="false"
								:style="{width:  '100%'}"
								:clearable="true"
								placeholder="请输入父评论id">
						</el-input>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row
					gutter="0"
					justify="start" align="top">
				<el-col span="12">
					<!--业务数据 id-->
					<el-form-item  label="业务数据id" prop="dataId">
						<el-input
								v-model="form.dataId"
								:disabled="false"
								:style="{width:  '100%'}"
								:clearable="true"
								placeholder="请输入业务数据id">
						</el-input>
					</el-form-item>
				</el-col>
				<el-col span="12">
					<!--业务数据 id-->
					<el-form-item  label="被评论数据" prop="dataTitle">
						<el-input
								v-model="form.dataTitle"
								:disabled="false"
								:style="{width:  '100%'}"
								:clearable="true"
								placeholder="请输入被评论数据">
						</el-input>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row
					gutter="0"
					justify="start" align="top">
				<el-col span="12">
					<!--日期选择器-->

					<el-form-item  label="日期选择器" prop="commentTime">
						<el-date-picker
								v-model="form.commentTime"
								placeholder="请选择日期"                    :readonly="false"
								:disabled="false"
								:editable="true"
								:clearable="true"
								value-format="yyyy-MM-dd HH:mm:ss"
								:style="{width:'100%'}"
								type="datetime">
						</el-date-picker>
					</el-form-item>
				</el-col>
				<el-col span="12">
					<!--点赞字段-->
				</el-col>
			</el-row>
			<!--评论类型-->

			<el-form-item  label="评论类型" prop="dataType">
				<el-select v-model="form.dataType"
						   @visible-change="dataTypeOptionsGet"
						   :style="{width: '100%'}"
						   :filterable="false"
						   :disabled="false"
						   :multiple="false" :clearable="true"
						   placeholder="请选择评论类型">
					<el-option v-for='item in dataTypeOptions' :key="item.dictValue" :value="item.dictLabel"
							   :label="item.dictLabel"></el-option>
				</el-select>
			</el-form-item>

			<!--0默认 显示 1:审核不通过-->
			<!--评价打分-->

			<el-form-item  label="评价打分" prop="commentPoints">
				<el-rate  v-model="form.commentPoints" :max="5"
						  :disabled="false"
						  :allow-half="false"></el-rate>
			</el-form-item>

			<!--评论的内容-->
			<el-form-item  label="评论的内容" prop="commentContent">
				<el-input
						type="textarea" :rows="5"
						:disabled="false"
						v-model="form.commentContent"
						:style="{width: '100%'}"
						placeholder="请输入评论的内容">
				</el-input>
			</el-form-item>

			<!--图片-->

			<el-form-item  label="图片" prop="commentPicture">
				<ms-upload
						v-model="form.commentPicture"
						:action="ms.base+'/file/upload.do'"
						:limit="5"
						:disabled="false"
						:data="{uploadPath:'/comment/','isRename':true,'appId':true}"
						accept="image/*"
						list-type="picture-card">
					<i class="el-icon-plus"></i>
					<div slot="tip" class="el-upload__tip">最多上传5张图片</div>
				</ms-upload>
			</el-form-item>

			<el-row
					gutter="0"
					justify="start" align="top">
				<el-col span="12">
					<!--匿名-->

					<el-form-item  label="匿名" prop="commentIsAnonymous">
						<el-switch v-model="form.commentIsAnonymous"
								   :disabled="false">
						</el-switch>
					</el-form-item>

				</el-col>
				<el-col span="12">
					<!--附件json-->

<#--					<el-form-item  label="附件json" prop="commentFileJson">-->
<#--						<ms-upload-->
<#--								v-model="form.commentFileJson"-->
<#--								:action="ms.base+'/file/upload.do'"-->
<#--								:limit=""-->
<#--								accept=".zip"-->
<#--								:on-error="uploadError"-->
<#--								:disabled="false"-->
<#--								:data="{uploadPath:'/comment/','isRename':true,'appId':true}">-->
<#--							<el-button size="small" type="primary">点击上传</el-button>-->
<#--							<div slot="tip" class="el-upload__tip">最多上传个文件</div>-->
<#--						</ms-upload>-->
<#--					</el-form-item>-->

				</el-col>
			</el-row>
			<!--标题-->
		</el-form>
	</el-main>
</div>
</body>

</html>

<script>
	var formVue = new Vue({
		el: '#form',
		data:function() {
			return {
				// 返回地址
				returnUrl:'',
				id: '',
				loading:false,
				saveDisabled: false,
				// 评论类型
				dataTypeOptions:[],
				//表单数据
				form: {
					// 被评论数据
					dataTitle: '',
					// 评论者id
					peopleId:'',
					// 业务数据 id
					dataId:'',
					// 父评论id
					commentId:'',
					//日期选择器
					commentTime:"2023-05-24 10:55:01",
					// 点赞字段
					commentLike:'',
					// 评论类型
					dataType:"",
					// 0默认 显示 1:审核不通过
					commentAudit:false,
					//评价打分
					commentPoints:5,
					// 评论的内容
					commentContent:'',
					// 图片
					commentPicture: [],
					// 匿名
					commentIsAnonymous:false,
					// 附件json
					commentFileJson: [],
					// 标题
					commentTitle:'',

				},
				rules:{
					// 评论者id
					peopleId: [{"type":"number","message":"评论者id格式不正确"}],
					// 点赞字段
					commentLike: [{"type":"number","message":"点赞字段格式不正确"}],
					// 标题
					commentTitle: [{"type":"string","message":"标题格式不正确"}],
					// 内容
					commentContent: [{
						"required": true,
						"message": "评论内容不能为空"
					}],

				},

			}
		},
		watch:{

		},
		components:{
		},
		computed:{
		},
		methods: {

			save:function() {
				var that = this;
				var url = ms.manager + "/comment/peopleSave.do";
				if (that.form.id){
					url = ms.manager + "/comment/update.do";
					var data = that.dataTypeOptions.find(function (value) {
						return value.dictLabel == that.form.dataType;
					})
					if (data){
						that.form.dataType=data.dictValue;
					}
				}

				this.$refs.form.validate(function(valid) {
					if (valid) {
						that.saveDisabled = true;
						that.form.commentIsAnonymous = 0;
						if (that.form.commentIsAnonymous) {
							that.form.commentIsAnonymous = 1;
						}
						var form = JSON.parse(JSON.stringify(that.form));
						ms.http.post(url, form).then(function (res) {
							if (res.result) {
								that.$notify({
									title: "成功",
									message: "保存成功",
									type: 'success'
								});
								if (that.form.id){
									that.form.dataType = that.dataTypeFormat(null,null,that.form.dataType)
								}
								if (that.returnUrl){
									location.href = that.returnUrl;
								}else {
									ms.util.openSystemUrl("/comment/index.do?dataType=" + that.form.dataType);
								}


							} else {
								that.$notify({
									title: "错误",
									message: res.msg,
									type: 'warning'
								});
							}

							that.saveDisabled = false;
						}).catch(function (err) {
							console.err(err);
							that.saveDisabled = false;
						});
					} else {
						return false;
					}
				})
			},

			//获取当前评论表
			get:function(id) {
				var that = this;
				this.loading = true
				ms.http.get(ms.manager + "/comment/getComment.do", {"id":id}).then(function (res) {
					that.loading = false
					if(res.result&&res.data) {
						debugger
						that.form = res.data;
						var dataType = ms.util.getParameter("dataType");
						if (dataType) {
							that.form.dataType = dataType;
						}
					}
				});
			},
			//日期选择器日期格式化
			commentTimeFormat:function(row, column, cellValue, index){
				return ms.util.date.fmt(new Date(row.COMMENT_TIME),'yyyy-MM-dd HH:mm:ss');
			},
			//评论类型  列表格式化
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

			//获取dataType数据源
			dataTypeOptionsGet:function() {
				var that = this;
				ms.http.get(ms.base+'/mdiy/dict/list.do', {dictType:'评论类型',pageSize:99999}).then(function (res) {
					that.dataTypeOptions = res.data.rows;
				}).catch(function (err) {
					console.log(err);
				});
			},


			update: function (row) {
				var that = this;
				ms.http.post(ms.manager+"/comment/comment/update.do", row).then(function (data) {
					if (data.result) {
						that.$notify({
							title: '成功',
							message: '更新成功',
							type: 'success'
						});

					} else {
						that.$notify({
							title: '失败',
							message: data.msg,
							type: 'warning'
						});
					}
				});
			},     // 上传文件错误回调
			uploadError: function (err, file, fileList) {
				if (err.message) {
					this.$notify({
						type: 'error',
						title: '失败',
						message: '文件上传失败:' + JSON.parse(err.message).msg
					});
				} else {
					this.$notify({
						type: 'error',
						title: '失败',
						message: '文件上传失败'
					});
				}
			},
		},
		created:function() {
			var that = this;
			var id = ms.util.getParameter("id");
			if (id){
				that.id = id;
				that.get(id);
			}
			//加载评论类型 数据
			this.dataTypeOptionsGet();
			var dataType = ms.util.getParameter("dataType");
			if (dataType) {
				that.form.dataType = dataType;
			}
			var returnUrl = ms.util.getParameter("returnUrl");
			if (returnUrl) {
				that.returnUrl = returnUrl;
			}
		}
	});

</script>
<style>
</style>
