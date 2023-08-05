<script type="text/x-template" id="ms-ai">
<div class="ms-ai">
  <el-button size="mini" @click="orgSave" icon="el-icon-zoom-in" type="success" :loading="saveDisabled">错词检测</el-button>
	<el-dialog :title="title" :visible.sync="dialogVisiable" append-to-body width="40%">
		<el-alert
				type="warning"
				class="ms-alert-tip"
				:closable="false" >
			{{tipType=='错词提示'?'错词可以不用修改，不影响文章的保存':'开启敏感词功能后，检测到敏感词必须修改才能保存文章，请打开自动替换敏感词开关'}}
		</el-alert>
		<el-scrollbar style="height: 100%;width: 100%">
			<div  style="height: 210px;margin: 20px 20px;">
				<div v-for="item in errorKeywords" style="margin: 6px;" :key="item">
					{{item.ori_frag}} -> {{item.correct_frag}}
				</div>
			</div>
		</el-scrollbar>
	</el-dialog>
</div>
</script>
<script>
	Vue.component('ms-ai',{
		template: "#ms-ai",
		name: "ms-ai",
		props: {
			//错词检测的文章内容
      		content: String,
		},
		data: function() {
			return {
				//提示类型
				tipType: '',
				//弹窗标题
				title: '',
				// 错词弹窗显示隐藏
				dialogVisiable: false,
				// 错敏词
				errorKeywords: [],
				// 按钮loading
				saveDisabled: false,
			}
		},
		methods: {
			//错词检测
			orgSave: function() {
				//将弹窗信息转换为错词提示
				this.title = '错词检测';
				this.tipType = '错词提示';
				var that = this; //自定义模型需要验证
				//	保存按钮取消审批错词状态
				const loading = that.$loading({
					lock: true,
					text: '错词检测中...'
				});
				that.saveDisabled = true;
				var data = {contentText:that.content};
				ms.http.post(ms.manager + "/baidu/ai/nlp/ecnet.do", data, {
					headers: {
						'timeout': 1000*60*3,
						'Content-Type': 'application/x-www-form-urlencoded',
					}
				}).then(function(res) {
					if (res.result) {
						that.saveDisabled = false;
						loading.close();
					   if(res.data){
						   that.errorKeywords = [];
						   res.data.forEach(function (word) {
							  if (word.correct_frag != word.ori_frag) {
								  that.errorKeywords.push(word);
							  }
						   })
						   that.dialogVisiable = true
					   } else {
						   that.$notify({
							  title: '成功',
							  message: '暂无错词',
							  type: 'success'
						   });
					   }
					} else {
						that.$notify({
						  title: '错误',
						  message: res.msg || '',
						  type: 'error'
						});
						loading.close();
						that.saveDisabled = false;
					}
				}).catch(function(err){
					that.saveDisabled = false;
				})
			},
			//敏感词检测
			sensitiveSave: function (data) {
				var that = this;
				//将弹窗信息转换成敏感词提示
				that.tipType = '敏感词提示';
				that.title = '敏感词检测';
				//清空展示数据,赋值敏感词数据
				that.errorKeywords = [];
				if(data!=null && data.length>0){
					data.forEach(function (word) {
						if (word.correct_frag != word.ori_frag) {
							that.errorKeywords.push(word);
						}
					})
				}
				that.dialogVisiable = true
			}
		},
	})
</script>
<style scoped>
.ms-ai{
  display: inline-block;
}
 .el-dialog__body{
	padding: 0 0 20px 0;
}
</style>
