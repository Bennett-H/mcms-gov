<!-- 图片编辑表单 -->
<div id="picture-form" class="ms-weixin-dialog" v-cloak>
	<el-dialog title="图片编辑" :close-on-click-modal="false" :visible.sync="isShow" :show-close="false">
		<el-form :model="pictureForm" :rules='pictureFormRules' label-width="81px" ref='pictureForm'>
			<el-form-item label="图片">
				<img :src='ms.web + pictureForm.fileUrl'>
			</el-form-item>
			<el-form-item label="图片名称" prop='fileNameNoType'>
				<el-input placeholder="请输入内容" v-model="pictureForm.fileName"  size="mini" maxlength="20"></el-input>
			</el-form-item>
			<el-form-item label="选择分组" prop="categoryId">
				<ms-create-group  @save-update="saveGroup"></ms-create-group>
				<el-select v-model="pictureForm.categoryId" placeholder="请选择分组" size='mini'>
					<el-option v-for="item in picGroup" :key="item.id" :label="item.categoryTitle" :value="item.id">
					</el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="存储大小">
				<div>{{formmateFileSize(pictureForm.fileSize)}}</div>
			</el-form-item>
			<el-form-item label="更新时间">
				<div>{{pictureForm.updateDate || '暂无数据'}}</div>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer">
			<el-button @click="cancel" size="mini">取 消</el-button>
			<el-button type="primary" @click="pictureUpdate" size="mini">确 定</el-button>
		</div>
	</el-dialog>
</div>
<script>
	var pictureFormVue = new Vue({
		el: '#picture-form',
		data:function(){return{
			pictureForm: {
				fileId:'',
				fileUrl:'',
				fileName: '',
				fileNameNoType:'',//没有后缀的文件名
				fileType: '',
				categoryId:"",
				suffix:''//文件后缀名
			},
			pictureFormRules: {
				fileName:[{
					required: true,
					message: '请输入文件名',
					trigger: ['blur', 'change']
				},
					{
						min: 1,
						max: 20,
						message: '长度在 1 到 20个字符',
						trigger: ['blur', 'change']
					},
					{
						validator:function(rule, value, callback){
							if(/[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im.test(value) || /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im.test(value)){
								callback('文件名不得包含非法字符')
							}
						},
						trigger: ['blur', 'change']
					}],
				categoryId:[
					{
						required: false,
						message: '请选择分组',
						trigger: ['blur', 'change']
					},
				]
			},
			popoverShow: false, //分组弹框是否显示
			isShow:false,//模态框显示状态
			// 规则
			groupRule: {
				categoryTitle: [{
					required: true,
					message: '请输入分组名称',
					trigger: ['blur', 'change']
				},
					{
						min: 1,
						max: 5,
						message: '长度在 1 到 5 个字符',
						trigger: ['blur', 'change']
					}
				]
			},
			picGroup:[],//下拉分组
		}},
		methods: {
			open: function (picture) {
				
				this.categoryList();
				this.pictureForm = picture
				this.pictureForm.suffix = this.pictureForm.fileUrl.split('.')[1];
				this.pictureForm.fileName = this.pictureForm.fileName.split('.')[0];
				this.isShow = true;
			},
			categoryList: function() {
            	var that = this;
	            ms.http.get(ms.manager + "/mweixin/category/list.do").then(function (res) {
	                res.data.rows.forEach(function (item){item.id = parseInt(item.id)});
	                that.picGroup = res.data.rows;
	            })
	        },
			saveGroup: function (groupForm) {
                var that = this;
                        // 校验成功
			            ms.http.post(ms.manager + "/mweixin/category/save.do",{
			            	categoryTitle: groupForm.categoryTitle,
			            }).then(function (res) {
			            	that.popoverShow = false
		                    that.categoryList();
                            pictureVue.categoryList();
			            })
            },
			 // 保留小数点后一位
			 changeNumType: function (num) {
                if (num.toString().indexOf('.') > -1) {
                    return /[0-9]+\.[0-9]{1}/.exec(num.toString())[0] //保留小数点后一位
                } else {
                    return num
                }
            },
            // 对文件尺寸大小进行转换
            formmateFileSize: function (size) {
                if (size / 1024 < 1024) {
                    return this.changeNumType((size / 1024)) + 'KB'
                } else {
                    return this.changeNumType((size / 1024 / 1024)) + 'MB'
                }
            },

			// 图片信息编辑保存
			pictureUpdate:function(){
				var that = this;
				// 更新文件信息接口
				this.$refs.pictureForm.validate(function(pass,object){
					if(pass){
						that.pictureForm.fileName += "."+that.pictureForm.suffix
						// 校验通过
			            ms.http.post(ms.manager + "/mweixin/file/update.do",that.pictureForm).then(function (res) {
							if(res.result){
								pictureVue.picList();
								pictureVue.categoryList();
								that.categoryList();
								that.isShow = false;
							}else {
								that.isShow = true;
							}

			            })
					}
				})
			},
			cancel: function () {
				this.pictureForm.fileName += "."+this.pictureForm.suffix
				this.isShow = false
            },
		},
		mounted: function() {
          this.categoryList();
        }
	})
</script>
<style>
    #picture-form img {
        width: 140px;
        height: 140px;
    }
	.el-form-item__content .el-input-group{
		vertical-align: middle;
	}
</style>
