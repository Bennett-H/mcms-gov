<!DOCTYPE html>
<html>
<head>
	<title>自定义业务模型表单页面模板</title>
	<#include "../../include/head-file.ftl">
	<script src="${base}/static/mdiy/index.js"></script>
</head>
<body>
<div id="form" v-loading="loading" v-cloak>
	<el-main class="ms-container" >
		<el-scrollbar class="ms-scrollbar" style="height: calc(100vh - 38px);">
		<div id="formModel"> </div>
		</el-scrollbar>
	</el-main>
</div>
</body>
</html>
<script>
	var _custom_model = null;
	var _formVue = new Vue({
		el: '#form',
		data:function() {
			return {
				loading:false,
			}
		},
		methods: {

		},
		created:function() {
			var that = this;
			this.loading = true;
			ms.http.get("${url}", ${model}).then(function (res) {
					if (res.result && res.data) {
						var data = JSON.parse(res.data.modelJson);

						_formVue.$nextTick(function () {
							var modelDom = document.getElementById("formModel");
							modelDom.innerHTML="";
							var scriptDom = document.createElement('script');
							scriptDom.innerHTML = data.script;
							var divDom = document.createElement('div');
							divDom.id = 'custom-model';
							divDom.innerHTML = data.html;
							modelDom.appendChild(scriptDom);
							modelDom.appendChild(divDom);
							//  promise抛出resolve进行外部调用自定义模型
							var tmpModel =new custom_model({
								data: {
									modelName: res.data.modelName,
									modelId: res.data.id,
									formURL: ${formUrls}
								}
							});
							//触发父窗口的方法
							window.parent._callbackCustomModel(tmpModel);
							//方便ms.mdiy.model.modelForm(); 当前model对象
							_custom_model = tmpModel;
						});

					}
					that.loading = false;
			});
		}
	});
</script>
<style>
	body {
		background-color: white;
	}
	#form .ms-container{
		height: unset;
	}
</style>
