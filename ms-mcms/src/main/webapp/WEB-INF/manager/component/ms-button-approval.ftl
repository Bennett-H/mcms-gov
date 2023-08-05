<script type="text/x-template" id="ms-button-approval">
	<el-button
			:loading="msApprovalLoading"
			v-if="visible"
			:type="type"
			:icon="icon"
			:size="size"
			@click="approval">
		{{title}}
	</el-button>
</script>
<script>
	Vue.component('ms-button-approval',{
		template: "#ms-button-approval",
		name: "ms-button-approval",
		props: {
			//	接口action地址
			action: {
				type: String
			},
			//	按钮样式类型
			type: {
				type: String,
				default: 'success',
			},
			//	按钮Icon图标
			icon: {
				type: String,
				default: "iconfont icon-niantie"
			},
			//	按钮大小尺寸
			size: {
				type: String,
				default: 'mini'
			},
			//	按钮描述名称
			title: {
				type: String,
				default: '审核'
			},
			//	传入接口参数对象
			params: {
				type: Object
			},
			//	按钮加载动画
			msApprovalLoading: {
				type: Boolean,
			},
			//	点击审批事件
			onApproval: {
				type: Function
			},
			//	数据传递
			getData: {
				type: Function
			},
		},
		data: function() {
			return {
				visible: false,
			}
		},
		watch:{

		},
		methods: {
			// 文章审核
			approval: function () {
				var that = this
				that.$emit('on-approval')
			},
			//	判断按钮是否显示
			verifyForRole: function() {
				var that = this
				if(that.params.categoryId && that.action) {
					ms.http.get(ms.manager + that.action, that.params).then(function(res) {
						that.visible = res.result
						that.$emit('get-data',that.visible)
					})
				}
			}
		},
		created: function () {
			var that = this
			that.action ? that.verifyForRole() : null
		}
	})
</script>
<style scoped>

</style>
