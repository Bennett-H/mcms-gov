<!-- 素材库 -->
<script type="text/x-template" id='voice'>
	<el-dialog  id='voice' title="选择语音" :visible.sync="isShow"  custom-class='ms-weixin-dialog border-radius-five'>
		<el-container>
			<el-main>
				<el-scrollbar style="height:100%;">
					<el-row class='showImg' height="88%">
						<template v-for="(voice,index) in voiceList">
							<el-col :span="5" class='border-radius-five'>
								<el-card @click.native="chooseVoice = (index+1)">
									<img class="border-radius-five" :src="ms.web+'/mweixin/image/voice.png'" alt=""  />
									<div class="img-title">
										<span>{{voice.fileName}}</span>
									</div>
								</el-card>
								<div class="clicked-background" v-if="chooseVoice == (index+1)">
									<i class="iconfont icon-icon"></i>
								</div>
							</el-col>
						</template>
						<el-col :span="5" class='border-radius-five'>
							<el-upload
									class="avatar-uploader"
									:data="{uploadPath:'/upload/'+weixinId+'/weixin/voice','isRename':true}"
									:action="ms.manager + '/file/upload.do'"
									:show-file-list="false"
									:on-success="handleSuccess"
									:accept="acceptType"
									:before-upload="beforePicUpload">
								<i class="el-icon-plus avatar-uploader-icon"></i>
							</el-upload>
						</el-col>
					</el-row>
				</el-scrollbar>
				<div class="picture-page">
					<el-pagination
							:current-pag="currentPage"
							:page-size="pageSize"
							layout="prev, pager, next, jumper"
							@current-change="currentChange"
							:total="total" background>
					</el-pagination>
				</div>
			</el-main>
		</el-container>
		<div slot="footer" class="dialog-footer">
			<el-button type="primary" @click="getChooseData" size='mini'>确 定</el-button>
			<el-button @click="isShow = false" size='mini'>取 消</el-button>
		</div>
	</el-dialog>
</script>
<script>
	Vue.component('ms-voice', {
		template: '#voice',
		data:function() {
			return {
				checked:false,
				isShow: false,
				flieName:"",//放大语音的文件名
				total: 0, //总记录数量
				pageSize: 10, //页面数量
				currentPage:1, //初始页
				voiceList:[],   //语音
				chooseVoice:0,   //选中的语音
				acceptType:".mp3,.wma,.wav,.amr",//限制上传类型
				fileForm: {
					fileName:"",
					fileUrl:"",
					fileSize:"",
					fileType:"",
				},
			}
		},
		computed: {
			weixinId: function () {
				return localStorage.getItem('weixinId')
			},
			menuActive: function () {
				return localStorage.getItem('menuActive')
			}
		},
		watch:{
			isShow:function (n,o) {
				if(!n){
					//取消选中
					this.chooseVoice = 0
				}
			}
		},
		methods: {
			// 表单打开
			open: function () {
				this.isShow = true;
				var that = this;
				that.voiceListData();
			},
			//语音加载完
			beforePicUpload:function(file) {
				this.fileForm.fileName = file.name;
				this.fileForm.fileSize = file.size;
				this.fileForm.fileType="voice";
				//音频限制大小为20M
				if (file.size>1024*1024*20) {
					this.$notify({
						title: '提示',
						message:  "语音超出大小，上传语音最大20M",
						type: 'warning'
					});
					return false;
				}
			},
			handleSuccess:function(response) {
				if(response.result){
					this.fileForm.fileUrl = response.data;
					var that = this;
					ms.http.post(ms.manager + "/mweixin/file/save.do",that.fileForm).then(function (res) {
						if(res.result){
							that.voiceListData();
							that.$notify({
								title:'成功',
								type:'success',
								message:'上传成功'
							});
						}
					}, function (err) {
						that.$notify.error(err);
					})
				}else {

					this.$notify({
						title: '提示',
						message:  response.msg,
						type: 'warning'
					});
				}
			},
			getChooseData:function(){		//获取选择的语音并渲染
				this.isShow = false;
				var chooseVoice = this.voiceList[this.chooseVoice-1];
				this.$emit('on-select',chooseVoice);
			},
			// 语音列表
			voiceListData: function () {
				var that = this;
				ms.http.get(ms.manager + "/mweixin/file/list.do", {
					pageNo:this.currentPage,
					pageSize:this.pageSize,
					fileType:"voice",
				}).then(function (res) {
					if(res.result){
						that.voiceList = res.data.rows;
						that.total = res.data.total;
					}
				}, function (err) {
					that.$notify.error(err);
				})
			},
			//pageSize改变时会触发
			sizeChange:function(pageSize) {
				this.pageSize = pageSize;
				this.voiceListData();
			},
			//currentPage改变时会触发
			currentChange:function(currentPage) {
				this.currentPage = currentPage;
				this.voiceListData();
			},
		},
		mounted: function() {
		}
	})
</script>
<style scoped>

	/* 素材库选择语音 */
	#voice .active{	/* 菜单栏选中的样式 */
		background:#ecf5ff;
	}

	#voice .border-radius-five{	/*5px圆角*/
		border-radius:5px;
	}

	#voice .ms-weixin-dialog.border-radius-five{
		width:845px;
		height:570px;
	}
	#voice .ms-weixin-dialog.border-radius-five .el-dialog__header{
		padding:15px 10px 5px 10px;
	}
	#voice .ms-weixin-dialog.border-radius-five .el-dialog__body{
		height:457px;
		padding:0px 0px;
	}
	#voice .ms-weixin-dialog.border-radius-five .el-dialog__body .el-container{
		height:100%;
	}

	#voice .ms-weixin-dialog.border-radius-five .el-dialog__body .el-aside{
		height:100%;
		border-left:1px solid #e6e6e6;
		padding:10px 5px;
	}

	#voice .ms-weixin-dialog.border-radius-five .el-dialog__body .el-aside .pictrue-group li{
		justify-content: center;
	}

	#voice .ms-weixin-dialog.border-radius-five .el-dialog__body .el-main{
		height:88%;
		padding:0px;
	}
	#voice .ms-weixin-dialog.border-radius-five .el-dialog__body .el-main .picture-page{
		position: absolute;
		bottom: 67px;
		right: 10px;
	}
	#voice .showImg.el-row .el-col{
		margin: 10px 0 10px 10px;
		position:relative;
	}

	#voice .showImg.el-row .el-card .el-card__body{
		height:168px;
		padding:10px;
		display: flex;
		flex-flow: column nowrap;
		justify-content: center;
		align-items: center;
	}
	#voice .showImg.el-row .el-card .el-card__body img{
		height:120px;
		width:120px;
	}
	#voice .showImg.el-row .el-card .el-card__body .img-title{
		width: 178px;
		height: 28px;
		padding: 0 10px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		text-align: center;
	}

	}
	/* 素材库选择语音 End*/
	/* 上传语音框 */
	#voice .avatar-uploader{
		display:inline;
	}
	#voice .avatar-uploader .el-upload {
		border: 1px solid #d9d9d9;
		border-radius: 6px;
		cursor: pointer;
		position: relative;
		overflow: hidden;


	}
	#voice .avatar-uploader .el-upload:hover {
		border-color: #409EFF;
	}
	#voice .avatar-uploader-icon {
		font-size: 60px;
		color: rgb(203, 203, 203);
		width: 180px;
		height: 168px;
		line-height: 178px !important;
		text-align: center;
	}
	#voice .clicked-background{
		background:rgba(127, 127, 127,0.7);
		position: absolute;
		top: 0px;
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		border-radius: 9px;
	}
	#voice  .clicked-background i{
		font-size: 52px;
		color: white;
		position: absolute;
	}
</style>
