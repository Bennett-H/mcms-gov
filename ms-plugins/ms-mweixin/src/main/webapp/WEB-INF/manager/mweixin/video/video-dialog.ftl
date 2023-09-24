<!-- 素材库 -->
<script type="text/x-template" id='video-tmp'>
	<el-dialog id="video" title="选择视频" :visible.sync="isShow" custom-class='ms-weixin-dialog border-radius-five'>
		<el-container>
			<el-main>
				<el-scrollbar style="height:100%;">
					<el-row class='showImg' height="88%">
						<template v-for="(video,index) in videoList">
							<el-col :span="5" class='border-radius-five'>
								<el-card @click.native="chooseVideo = (index+1)">
									<img class="border-radius-five" :src="ms.web+'/mweixin/image/video.png'" alt=""  />
									<div class="img-title">
										<span>{{video.fileName}}</span>
									</div>
								</el-card>
								<div class="clicked-background" v-if="chooseVideo == (index+1)">
									<i class="iconfont icon-icon"></i>
								</div>
							</el-col>
						</template>
						<el-col :span="5" class='border-radius-five'>
							<el-upload

									class="avatar-uploader"
									:data="{uploadPath:'/upload/'+weixinId+'/weixin/video','isRename':true}"
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
			<el-button type="primary" @click="onSelect" size='mini'>确 定</el-button>
			<el-button @click="isShow = false" size='mini'>取 消</el-button>
		</div>
	</el-dialog>
</script>
<script>
	Vue.component('ms-video', {
		template: '#video-tmp',
		data:function() {
			return{
			checked:false,
			isShow: false,
			flieName:"",//放大视频的文件名
			total: 0, //总记录数量
			pageSize: 10, //页面数量
			currentPage:1, //初始页
			videoList:[],   //视频
			chooseVideo:0,   //选中的视频
			acceptType:".MP4",//限制上传类型
			fileForm: {
				fileName:"",
				fileUrl:"",
				fileSize:"",
				fileType:"",
			},
		}},
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
					this.chooseVideo = 0
				}
			}
		},
		methods: {
			// 表单打开
			open: function () {
				this.isShow = true;
				this.videoListData();
			},
			//视频加载完
			beforePicUpload:function(file) {
				//视频限制大小为10M
				if (file.size>1024*1024*200) {
					this.$notify({
						title: '提示',
						message: "视频超出大小，上传视频最大200M",
						type: 'warning'
					});
					return false;
				}
				this.fileForm.fileName = file.name;
				this.fileForm.fileSize = file.size;
				this.fileForm.fileType = "video";
			},
			handleSuccess:function(response) {
				if(response.result){
					this.fileForm.fileUrl = response.data;
					var that = this;
					ms.http.post(ms.manager + "/mweixin/file/save.do",that.fileForm).then(function (res) {
						if(res.result){
							that.videoListData();

							that.$notify({
								title: '成功',
								message: "上传成功",
								type: 'success'
							});
						}
					})
				}else {

					this.$notify({
						title: '提示',
						message:  response.msg,
						type: 'warning'
					});
				}

			},
			onSelect:function(){		//获取选择的视频并渲染
				this.isShow = false;
				var chooseVideo = this.videoList[this.chooseVideo-1];
				this.$emit('on-select',chooseVideo);
			},
			// 视频列表
			videoListData: function () {
				var that = this;
				ms.http.get(ms.manager + "/mweixin/file/list.do", {
					pageNo:this.currentPage,
					pageSize:this.pageSize,
					fileType:"video",
				}).then(function (res) {
					if(res.result){
						that.videoList = res.data.rows;
						that.total = res.data.total;
					}
				})
			},
			//pageSize改变时会触发
			sizeChange:function(pageSize) {
				this.pageSize = pageSize;
				this.videoListData();
			},
			//currentPage改变时会触发
			currentChange:function(currentPage) {
				this.currentPage = currentPage;
				this.videoListData();
			},
		},
		mounted: function() {

		}
	})
</script>
<style scoped>

	/* 素材库选择视频 */
	#video .active{	/* 菜单栏选中的样式 */
		background:#ecf5ff;
	}

	#video .border-radius-five{	/*5px圆角*/
		border-radius:5px;
	}

	#video .ms-weixin-dialog.border-radius-five{
		width:845px;
		height:570px;
	}
	#video .ms-weixin-dialog.border-radius-five .el-dialog__header{
		padding:15px 10px 5px 10px;
	}
	#video .ms-weixin-dialog.border-radius-five .el-dialog__body{
		height:457px;
		padding:0px 0px;
	}
	#video .ms-weixin-dialog.border-radius-five .el-dialog__body .el-container{
		height:100%;
	}

	#video .ms-weixin-dialog.border-radius-five .el-dialog__body .el-aside{
		height:100%;
		border-left:1px solid #e6e6e6;
		padding:10px 5px;
	}

	#video .ms-weixin-dialog.border-radius-five .el-dialog__body .el-aside .pictrue-group li{
		justify-content: center;
	}

	#video .ms-weixin-dialog.border-radius-five .el-dialog__body .el-main{
		height:88%;
		padding:0px;
	}
	#video .ms-weixin-dialog.border-radius-five .el-dialog__body .el-main .picture-page{
		position: absolute;
		bottom: 67px;
		right: 10px;
	}
	#video .showImg.el-row .el-col{
		margin:10px;
		position:relative;
	}

	#video .showImg.el-row .el-card .el-card__body{
		height:168px;
		padding:10px;
		display: flex;
		flex-flow: column nowrap;
		justify-content: center;
		align-items: center;
	}
	#video .showImg.el-row .el-card .el-card__body img{
		height:120px;
		width:120px;
	}
	#video .showImg.el-row .el-card .el-card__body .img-title{
		width: 180px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		padding: 0 10px;
		text-align: center;
	}

	}
	/* 素材库选择视频 End*/
	/* 上传视频框 */
	#video .avatar-uploader{
		display:inline;
	}
	#video .avatar-uploader .el-upload {
		border: 1px solid #d9d9d9;
		border-radius: 6px;
		cursor: pointer;
		position: relative;
		overflow: hidden;


	}
	#video .avatar-uploader .el-upload:hover {
		border-color: #409EFF;
	}
	#video .avatar-uploader-icon {
		font-size: 60px;
		color: rgb(203, 203, 203);
		width: 180px;
		height: 168px;
		line-height: 178px !important;
		text-align: center;
	}
	#video .clicked-background{
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
	#video  .clicked-background i{
		font-size: 52px;
		color: white;
		position: absolute;
	}
</style>
