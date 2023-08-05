<!DOCTYPE html>
<html>
<head>
	<title>日志管理</title>
		<#include "../../include/head-file.ftl">
	<script src="${base}/static/plugins/codemirror/5.48.4/codemirror.js"></script>
	<link href="${base}/static/plugins/codemirror/5.48.4/codemirror.css" rel="stylesheet">
	<script src="${base}/static/plugins/codemirror/5.48.4/mode/css/css.js"></script>
	<script src="${base}/static/plugins/vue-codemirror/vue-codemirror.js"></script>
	<script src="${base}/static/plugins/codemirror/5.48.4/addon/scroll/annotatescrollbar.js"></script>
	<script src="${base}/static/plugins/codemirror/5.48.4/mode/xml/xml.js"></script>
	<script src="${base}/static/plugins/codemirror/5.48.4/mode/javascript/javascript.js"></script>
</head>
<body>
	<div id="index" class="ms-index" v-cloak class="ms-index">
			<el-header class="ms-header" height="50px">
				<el-col span=24>
					<el-button size="mini" plain onclick="" style="float: right; margin-right: 14px" @click="back"><i class="iconfont icon-fanhui"></i>返回</el-button>
				</el-col>
			</el-header>
		<el-main class="ms-container" style="flex-direction: row" v-loading="loading">
			<#--表格-->
			<div style="overflow: hidden; background-color: rgb(242, 246, 252);  display: flex;position: relative;margin-right: 10px;width: 100%">
				<el-table
						highlight-current-row
						height="calc(100vh - 80px)"
						ref="multipleTable"
						border
						:data="logData.fileNameList"
						tooltip-effect="dark">
					<template slot="empty">
						{{emptyText}}
					</template>
					<el-table-column label="日志名称" align="left" prop="name" width="300">
						<template slot-scope="scope">
							<div style="cursor: pointer; margin-left: 5px;display: flex;align-items: center;justify-content: flex-start;">
								<svg class="icon" aria-hidden="true">
									<use :xlink:href="scope.row.icon"></use>
								</svg>
								<span style="margin-left: 5px">{{scope.row.name}}</span>
							</div>
						</template>
					</el-table-column>
					<el-table-column label="日志路径" align="left" prop="name" >
						<template slot-scope="scope">
							<div style="cursor: pointer; margin-left: 5px;display: flex;align-items: center;justify-content: flex-start;">
								<span style="margin-left: 5px">{{ logData.uploadPath+ '/' + scope.row.name}}</span>
							</div>
						</template>
					</el-table-column>
					<el-table-column label="操作" align="center" width="150">
						<template slot-scope="scope">
							<el-link type="primary" :underline="false" v-if="scope.row.folderType == '返回'" @click="view(scope.row)">返回</el-link>
							<@shiro.hasPermission name="gov:log:download">
								<el-link type="primary" :underline="false" @click="download(scope.row.name)">下载</el-link>
							</@shiro.hasPermission>
						</template>
					</el-table-column>
				</el-table>
			</div>
         </el-main>
	</div>
</body>
</html>

<script>
	Vue.use(VueCodemirror);
	var indexVue = new Vue({
		el: '#index',
		data: {
			//数据stak
			dataStack: [],
			manager: ms.manager,
			loading: true,
			//加载状态
			emptyText: '',
			//提示文字
			fileList: [],
			logData: {
				fileNameList: [],
				uploadPath: null,
				path: null,
				websiteId: null
			},
			saveDisabled: false,
			currentRow:'',
			imgPath:'',
			//是否显示图片
			imgFlag:false,
			//设置
			cmOption: {
				tabSize: 4,
				styleActiveLine: true,
				lineNumbers: true,
				line: true,
				styleSelectedText: true,
				lineWrapping: true,
				mode: 'css',
				matchBrackets: true,
				showCursorWhenSelecting: true,
				hintOptions: {
					completeSingle: false
				}
			},
			fileName: "",
			//表单数据
			form: {
				// 文件名称
				name: '',
				fileName: '',
				fileNamePrefix: '',
				fileContent: ''
			},
		},
		methods: {
			codemirrorSuccess() {
			},
			//查询列表
			list: function (skinFolderName) {
				var that = this;
				that.loading = true;
				setTimeout(function () {
					ms.http.get(ms.manager + "/gov/log/showChildFileAndFolder.do", {
						skinFolderName: skinFolderName
					}).then(function (res) {
						var folderNum = res.data.folderNum;
						var folderIndex = 0;
						var data = {
							fileNameList: [],
							uploadFileUrl: null
						};
						//最上层目录没有返回
						if (that.dataStack.length > 0) {
							//返回一层目录（参考linux目录结构）
							data.fileNameList.push({
								name:"...",
								icon: "#icon-wenjianjia2",
								folderName: '',
								folderType: "返回"
							});
						}

						res.data.fileNameList.forEach(function (item) {
							var type = "文件夹";
							var icon = "#icon-wenjianjia2";

							// 路径排序中，文件夹在前面，根据文件夹数量判断是否文件夹
							if (folderIndex >= folderNum) {
								type = "文件";
								icon = "#icon-wenjian2";
							}
							folderIndex++;

							var suffix = item.substring(item.lastIndexOf(".") + 1);

							if (suffix == "jpg" || suffix == "gif" || suffix == "png") {
								type = "图片";
								icon = "#icon-tupian1";
							}
							var name = item.substring(item.lastIndexOf("\\") + 1);
							name = name.substring(name.lastIndexOf("/") + 1);
							data.fileNameList.push({
								name:name,
								icon: icon,
								folderName: item,
								folderType: type
							});
						}); //保存当前路径以及相关信息

						data.path = skinFolderName;
						data.uploadPath = res.data.uploadFileUrl;
						data.websiteId = res.data.websiteId;
						that.logData = data;
						that.dataStack.push(data);

						// for (var key in that.logData.fileNameList) {
						// 	if (that.logData.fileNameList[key].name == that.currentRow) {
						// 		that.$refs.multipleTable.setCurrentRow(that.logData.fileNameList[key]);
						// 		break;
						// 	}
						// }
					}).finally(function () {
						that.loading = false;
					});
				}, 500);
			},
			download : function (fileName){
				var that = this;
				ms.http.download(ms.manager + "/gov/log/downLogFile.do", {
					"folderName" : that.logData.path,
					"fileName": fileName
				});
			},
			back: function () {
				window.location.href = ms.manager + "/gov/log/index.do";
			},
			//表格数据转换
			//pageSize改变时会触发
			sizeChange: function (pagesize) {
				this.loading = true;
				this.pageSize = pagesize;
				this.list();
			},
			//currentPage改变时会触发
			currentChange: function (currentPage) {
				this.loading = true;
				this.currentPage = currentPage;
				this.list();
			},
			search: function (data) {
				this.form.sqlWhere = JSON.stringify(data);
				this.list();
			},
			//重置表单
			rest: function () {
				this.form.sqlWhere = null;
				this.$refs.searchForm.resetFields();
				this.list();
			},
		},
		mounted: function () {
			this.template = ms.util.getParameter("name");

			if (this.template) {
				this.list(this.template);
			}
		}
	});

</script>
<style>
	#index .ms-container {
		height: calc(100vh - 78px);
	}

	.icon {
		width: 2em;
		height: 2em;
		vertical-align: -0.15em;
		fill: currentColor;
		overflow: hidden;
	}
	#index .file-img {
		display: flex;
		flex-direction: column;
		flex: 1;
		justify-content: center;
		align-items: center;
	}
	#index .file-edit {
		display: flex;
		flex-direction: column;
		flex: 1;
		width: calc(100vh - 300px);
	}
	.CodeMirror {
		border: 1px solid #eee;
		height: auto;
	}

	.CodeMirror-scroll {
		overflow-y: hidden;
		overflow-x: auto;
	}
	.el-table--scrollable-x .el-table__body-wrapper {
		overflow-x: hidden;
	}
</style>
