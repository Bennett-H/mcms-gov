<#-- <#include "/component/ms-cropper.ftl"> -->
<!-- 增加了资源库上传及选择 -->
<script type="text/x-template" id="ms-upload-img">
	<div class="upload-body" v-loading="imgListLoading">
		<el-tabs v-model="activeKey" @tab-click="toggleTabs">
			<el-tab-pane v-if="fileType==='img'" label="资源上传" name="fileUpload">
				<ms-cropper
						v-if="form.contentImg.length"
						:img-file="form.contentImg[0]"
						ref="vueCropper">
				</ms-cropper>
				<el-upload
						v-else
						:file-list="form.contentImg"
						:auto-upload="false"
						:on-change="onChange"
						:style="{width:'100%'}"
						:limit="1"
						:disabled="false"
						accept="image/*"
						list-type="picture-card">
					<i class="el-icon-plus"></i>
				</el-upload>
			</el-tab-pane>
			<el-tab-pane label="资源库" name="fileLib">
				<el-scrollbar class="ms-scrollbar" style="height: 100%">
					<div class="file-list">
						<div class="file-img" v-for="(item,index) in fileLibList" :key="index" @click="setUploadImg(item)">
							<div v-if="reg.img.test(item.fileType)" class="img-url">
								<img :src="ms.base+item.filePath"/>
							</div>
							<div v-else-if="reg.video.test(item.fileType)" class="img-url">
								<img src="${base}/static/images/file_video@3x.png" style="width:50%;height:50%;"/>
								<span>{{item.fileName}}</span>
							</div>
							<div v-else class="img-url">
								<img src="${base}/static/images/file_none@3x.png" style="width:50%;height:50%;"/>
								<span>{{item.fileName}}</span>
							</div>
						</div>
						<el-empty v-show="!imgListLoading&&fileLibList.length==0" description="资源库暂无资源，请先上传。"></el-empty>
					</div>
				</el-scrollbar>
				<div style="paddingBottom: 20px">
					<el-pagination background small :page-sizes="[10,20,30,40,50,100]"
								   layout="total, prev, pager, next, jumper" :current-page="currentPage"
								   :page-size="pageSize" :total="total" :pager-count="5" class="ms-pagination"
								   @current-change='currentChange' @size-change="sizeChange">
					</el-pagination>
				</div>
			</el-tab-pane>
		</el-tabs>

		<el-footer height="auto" v-if="activeKey=='fileUpload' && form.contentImg.length">
			<el-button type="primary" size='mini' icon="iconfont icon-baocun"  plain @click="saveImg(false)">不裁切保存</el-button>
			<el-button type="primary" size='mini' icon="iconfont icon-baocun" @click="saveImg(true)">保存</el-button>
			<el-button size="mini" icon="iconfont icon-fanhui" plain @click="$emit('on-close')">返回</el-button>
		</el-footer>
	</div>
</script>
<script>
	Vue.component('ms-upload-img', {
		template: "#ms-upload-img",
		name: "ms-upload-img",
		props: {
			//	图片上传接口
			action: {
				type: String
			},
			//	关闭函数
			onClose: {
				type: Function
			},
			//	同步外部
			onSync: {
				type: Function
			},
			//	反显上传图片信息
			imgJson: {
				type: Array
			},
			// 类型，默认为img（包括图片裁切选项卡），可选值img(图片)、file（文件，不含图片裁切）
			fileType: {
				type: String,
				default: 'img'
			}
		},
		data: function() {
			return {
				total: 0, //总记录数量
				pageSize: 20, //页面数量
				currentPage: 1,
				imgListLoading: true,
				activeKey: 'fileUpload',
				uploadForm: {
					contentImg: [],
				},
				fileLibList: [],
				form: {
					contentImg: []
				},
				// 正则，用于匹配文件类型
				reg: {
					img: /(bmp|jpg|gif|jpeg|png)/i,
					video: /(mp4|rmvb|flv|mpeg|avi)/i
				}
			}
		},
		watch: {
			//'form.contentImg': function (n) {
			//var that = this
			//this.$emit('on-sync',n)
			//},
		},
		methods: {
			//  解析file文件，转化为base64进行返回
			fileToBase64: function(file) {
				let that = this
				reader = new FileReader();
				reader.readAsDataURL(file);
				return new Promise(function(resolve, reject) {
					reader.onload = function(e) { //这里是一个异步，所以获取数据不好获取在实际项目中，就用new Promise解决
						if (this.result) {
							resolve(this.result)
						} else {
							reject("err")
						}
					}
				})
			},
			//  图片上传的数据变化时调用
			onChange: function(file) {
				var that = this
				//  上传之前保存上传文件格式
				var data
				//  将文件格式转为base64传入切图
				this.fileToBase64(file.raw).then(function(res) {
					//  组织剪切前的数据
					data = {
						name: file.name,
						url: res
					}
					that.form.contentImg.push(data)
				})
			},
			saveImg: function(isCut) {
				var that = this
				var formData = new FormData();
				formData.append('uploadPath', '/cms/content');
				formData.append('isRename', true);
				formData.append('appId', true);
				if (isCut) {
					that.$refs.vueCropper.$refs.cropper.getCropBlob(function(Blob) {
						//  创建文件流格式
						formData.append('file', Blob, that.form.contentImg[0].name);
						that.upload(formData);
					})
				} else {
					const file = this.dataURIToBlob(that.form.contentImg[0].url);
					formData.append('file', file, that.form.contentImg[0].name);
					this.upload(formData);
				}

			},
			/**
			 * base64转Blob
			 * @param dataURI base64图片地址
			 * @returns {Blob}
			 */
			dataURIToBlob: function(dataURI) {
				const splitDataURI = dataURI.split(',')
				const byteString = splitDataURI[0].indexOf('base64') >= 0 ? atob(splitDataURI[1]) : decodeURI(splitDataURI[1])
				const mimeString = splitDataURI[0].split(':')[1].split(';')[0]

				const ia = new Uint8Array(byteString.length)
				for (let i = 0; i < byteString.length; i++)
					ia[i] = byteString.charCodeAt(i)

				return new Blob([ia], { type: mimeString })
			},
			/**
			 * 上传文件
			 * @param formData form表单
			 */
			upload: function(formData) {
				var that = this
				ms.http.post(ms.manager + '/file/upload.do', formData, {
					headers: {
						'Content-Type': 'multipart/form-data'
					}
				}).then(function(res) {
					if (res.result) {
						//  重组资源数据文件
						that.form.contentImg = [{
							url: ms.base+res.data,
							name: that.form.contentImg[0].name,
							path: res.data,
						}]
						that.$notify({
							title: '成功',
							message: '缩略图设置成功!',
							type: 'success'
						});
						that.$emit('on-sync', that.form.contentImg)
					} else {
						that.$notify({
							title: '失败',
							message: res.msg,
							type: 'warning'
						});
					}
					//  关闭资源库模态框
					that.$emit('on-close')
				})
			},
			//	设置资源库里点击的图片,index为下标
			setUploadImg: function(item) {
				var that = this
				//	创建图片对象数据,并设置为缩略图
				that.form.contentImg = [{
					url: ms.base+item.filePath,
					name: item.fileName,
					path: ms.base+item.filePath,
					type: item.fileType
				}]
				that.$emit('on-sync', that.form.contentImg)
				that.$emit('on-close')

			},
			//pageSize改变时会触发
			sizeChange: function(pagesize) {
				this.fileLibList = []
				this.imgListLoading = true;
				this.pageSize = pagesize;
				this.imgList(this.fileType);
			},
			//currentPage改变时会触发
			currentChange: function(currentPage) {
				var that = this
				this.fileLibList = []
				this.imgListLoading = true;
				this.currentPage = currentPage;
				this.imgList(this.fileType);
			},
			// tabs切换的状态改变
			toggleTabs: function(key) {
				var that = this
				that.activeKey = key.name
			},
			// 获取资源库列表
			imgList: function(type) {
				var that = this
				that.imgListLoading = true;
				var page = {
					pageNo: that.currentPage,
					pageSize: that.pageSize
				};
				ms.http.get(ms.manager + '/co/file/listImageAndVideoForManager.do', page).then(function(res) {
					if (res.result) {
						if (type === 'img') {
							// 正则匹配图片
							that.fileLibList = res.data.rows.filter(function(item) {
								return that.reg.img.test(item.fileType)
							})
						} else {
							that.fileLibList = res.data.rows
						}
						that.total = res.data.total;
						that.imgListLoading = false;
					} else {
						that.$notify({
							title: '失败',
							message: '当前网络波动,请稍后再试!',
							type: 'warning'
						});
					}
				})
			},
		},
		created: function() {
			var that = this
		},
		mounted: function() {
			var that = this
			if (that.fileType === 'file') {
				that.activeKey = 'fileLib'
			} else if (that.fileType === 'img') {
				that.activeKey = 'fileUpload'
			}
			that.imgList(that.fileType)
		}
	})
</script>
<style scoped>
	.upload-body {
		padding: 0 24px 24px 24px
	}

	.ms-scrollbar .el-scrollbar__view {
		padding-right: 0 !important;
	}

	.file-list {
		height: 360px;
	}

	.file-img {
		width: 123px !important;
		height: 123px !important;
		cursor: pointer;
		overflow: hidden;
		display: inline-block;
		margin-right: 12px;
		margin-top: 12px;
		margin-bottom: -3px;
	}

	.file-img .img-url {
		overflow: hidden;
		background-color: #eee;
		width: 100%;
		height: 100%;
		border-radius: 8px;
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
	}

	.file-img .img-url>img {
		width: 100%;
		height: 100%;
	}

	.upload-body .el-scrollbar__wrap {
		margin-bottom: 12px !important;
		margin-right: -26px !important;
	}

	.el-dialog__body {
		padding: 0;
	}

	.el-footer {
		text-align: right;
		padding: 0 0 0px 0 !important;
	}
</style>
