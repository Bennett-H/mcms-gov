<!-- 素材库 -->
<script type="text/x-template" id='picture-tmp'>
    <el-dialog id="picture" title="选择图片" :visible.sync="isShow" :close-on-click-modal="false" custom-class='ms-weixin-dialog border-radius-five'>
	    <el-container>
	  		<el-aside width="160px">
					<ul class="pictrue-group">
					<li @click="currentPage = 1;categoryId = ''; picList()" label="all" :class="{active:categoryId === ''}">
								<span>全部</span>
							</li>
						<template v-for="(group,index) in picGroup" :key="group.category.categoryId">
							<li @click="currentPage = 1;categoryId = group.category.id; picList()" :label="group.category.categoryTitle + '('+group.total+')'" :class="{active:categoryId === group.category.id}">
								<span>{{group.category.categoryTitle+'('+group.total+')'}}</span>
							</li>
						</template>
					</ul>
			</el-aside>
	  		<el-main>
	  		<el-scrollbar style="height:100%;">
	  			<el-row class='showImg' height="88%">
		  			  <template v-for="(pic,index) in pictureList">
		  				<el-col :span="7" class='border-radius-five'  v-if="categoryId == ''||categoryId == pic.categoryId">
		  					<el-card @click.native="chooseImg = (index+1)" shadow="never">
			  					<img class="border-radius-five" :src="ms.base+pic.fileUrl"   />
			  					<div class="img-title">
			  						{{pic.fileName}}
			  					</div>
			  				</el-card>
			  				<div class="clicked-background" v-if="chooseImg == (index+1)">
                    			<i class="iconfont icon-icon"></i>
                    		</div>
		  				</el-col>
		  			  </template>
		  		 <el-col :span="7" class='border-radius-five'>
		  		 	<el-upload
							class="avatar-uploader"
							:action="ms.manager + '/file/upload.do'"
							:data="{uploadPath:'/upload/'+weixinId+'/weixin/picture'}"
							:show-file-list="false"
							accept=".bpm,.png,.jpeg,.gif,.jpg"
							:on-success="handleSuccess"
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
	Vue.component('ms-picture', {
		template: '#picture-tmp',
		data:function(){return{
			checked:false,
			isShow: false,
			materialGroup: [],
			flieName:"",//放大图片的文件名
			total: 0, //总记录数量
			pageSize: 10, //页面数量
			currentPage:1, //初始页
			picGroup:[],
			categoryId:'', //当前选中分组id
			pictureList:[],   //分组中的图片
			chooseImg:0,   //选中的图片
			fileForm: {
				fileName:"",
				fileUrl:"",
				categoryId:"",
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
					this.chooseImg = 0
				}
			}
        },
        methods: {
            // 表单打开
            open: function () {
                this.isShow = true;
				this.categoryList();
				this.picList();
            },
			// 图片刚加载完
			beforePicUpload:function(file) {
				this.fileForm.fileName = file.name;
				this.fileForm.fileSize = file.size;
				this.fileForm.fileType="image";
			},
			handleSuccess:function(response) {
				if(response.result){
					this.fileForm.fileUrl = response.data;
					var that = this;
					this.fileForm.categoryId = this.categoryId;
					ms.http.post(ms.manager + "/mweixin/file/save.do",that.fileForm).then(function (res) {
						//that.isShow = false
						if(res.result){
							that.picList();
							that.categoryList();
							that.$notify({
								title: '成功',
								type:'success',
								message:'上传成功'
							});
						}else {
							that.$notify({
								title: '提示',
								type:'warning',
								message:res.msg
							});
						}

					}, function (err) {
						that.$notify({
							title: '错误',
							type: 'err',
							message: err
						});
					})

				}else {
					this.$notify({
						title: '提示',
						type: 'warning',
						message: response.msg
					});
				}
            },
			onSelect:function(){		//获取选择的图片并渲染
            	this.isShow = false;
            	var chooseImg = this.pictureList[this.chooseImg-1];
				this.$emit('on-select',chooseImg);
            },

			categoryList: function () {
				var that = this;
				ms.http.get(ms.manager + "/mweixin/file/categoryFile.do").then(function (res) {
					if(res.result){
						that.picGroup = res.data;
					}
				}, function (err) {
					that.$notify({
						title: '错误',
						type: 'err',
						message: err
					});
				})
			},

	        // 图片列表
			picList: function () {
				var that = this;
				ms.http.get(ms.manager + "/mweixin/file/list.do", {pageNo:this.currentPage,
					pageSize:this.pageSize,
					categoryId : this.categoryId,
					fileType:"image",
				}).then(function (res) {
					if(res.result){
						that.pictureList = res.data.rows;
						that.total = res.data.total;
					}
				}, function (err) {
					that.$notify({
						title: '错误',
						type: 'err',
						message: err
					});
				})
			},
	         //pageSize改变时会触发
        	sizeChange:function(pageSize) {
            	this.pageSize = pageSize;
            	this.picList();
        	},
        	//currentPage改变时会触发
        	currentChange:function(currentPage) {
            	this.currentPage = currentPage;
            	this.picList();
        	},
        },
        mounted: function() {
       }
    })
</script>
<style>

    /* 素材库选择图片 */
    #picture .active{	/* 菜单栏选中的样式 */
        background:#ecf5ff;
    }


    #picture .ms-weixin-dialog.border-radius-five{
        width:900px;
        height:614px;
    }
    #picture .ms-weixin-dialog.border-radius-five .el-dialog__header{
        padding:15px 10px 5px 10px;
    }
    #picture .ms-weixin-dialog.border-radius-five .el-dialog__body{
        padding:0px 0px;
    }
    #picture .ms-weixin-dialog.border-radius-five .el-dialog__body .el-container{
        height:100%;
    }

    #picture .ms-weixin-dialog.border-radius-five .el-dialog__body .el-aside{
        height:100%;
		border: none;
        border-right:1px solid #e6e6e6;
        padding:10px 5px;
    }

    #picture .ms-weixin-dialog.border-radius-five .el-dialog__body .el-aside .pictrue-group li{
        justify-content: center;
    }

    #picture .ms-weixin-dialog.border-radius-five .el-dialog__body .el-main{
        height:88%;
        padding:0px;
    }
    #picture .ms-weixin-dialog.border-radius-five .el-dialog__body .el-main .picture-page{
        position: absolute;
        bottom: 67px;
        right: 10px;
    }
    #picture .showImg.el-row .el-col{
        margin:10px;
        position:relative;
    }

    #picture .showImg.el-row .el-card .el-card__body{
        height:168px;
        padding:10px;
        flex-flow: column nowrap;
        justify-content: center;
        align-items: center;
    }
    #picture .showImg.el-row .el-card .el-card__body img{
        width:100%
    }
    #picture .showImg.el-row .el-card .el-card__body .img-title{
		width: 150px;
		padding: 8px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		position: absolute;
		bottom: 0;
		text-align: center;
		padding: 6px 6px;
    }
    /* 素材库选择图片 End*/
    /* 上传图片框 */
    #picture .avatar-uploader{
        display:inline;
    }
    #picture .avatar-uploader .el-upload {
        border: 1px solid #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;


    }
    #picture .avatar-uploader .el-upload:hover {
        border-color: #409EFF;
    }
    #picture .avatar-uploader-icon {
        font-size: 60px;
        color: rgb(203, 203, 203);
        width: 180.83px;
        height: 168px;
        line-height: 178px !important;
        text-align: center;
    }
	#picture .clicked-background{
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
	#picture  .clicked-background i{
		font-size: 52px;
		color: white;
		position: absolute;
	}
</style>
