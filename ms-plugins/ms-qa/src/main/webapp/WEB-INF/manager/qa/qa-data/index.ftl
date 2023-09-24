<!DOCTYPE html>
<html>
<head>
	<title>问卷调查数据列表</title>
	<#include "../../include/head-file.ftl">
	<script src="${base}/static/plugins/composition-api/1.4.3/vue-composition-api.js"></script>
	<script src="${base}/static/plugins/echarts/5.2.2/echarts.js"></script>
	<script src="${base}/static/plugins/vue-echarts/6.0.2/index.umd.min.js"></script>
</head>
<body >
	<div id="index" v-cloak >
		<div id="chartPanel" style="width: 400px; float: left; height: 100vh; border: 2px solid white">
			<el-scrollbar class="ms-scrollbar" style="height: 100%">
			<template v-for="(option,index) in options">
				<v-chart style="height: 200px; width: 400px" align="center" autoresize :option="option"></v-chart>
			</template>
			</el-scrollbar>
		</div>
	</div>
	<#include "../../qa/qa-data/index-table.ftl">
</body>
</html>
<script>
	Vue.component("v-chart", VueECharts);

	var indexVue = new Vue({
		el: '#index',
		data: {
			mdiyModel:{},
			customModel:{},
			//图表数据模版
			optionConf: {
				title: {
					text: '某站点用户访问来源',
					left: 'center',
					top: 'top'
				},
				tooltip: {
					trigger: 'item'
				},
				legend: {
					top: '5%',
					orient: 'vertical',
					left: '0'
				},
				series: [
					{
						type: 'pie',
						radius: ['40%', '70%'],
						avoidLabelOverlap: false,
						itemStyle: {
							borderRadius: 10,
							borderColor: '#fff',
							borderWidth: 2
						},
						label: {
							show: false,
							position: 'center'
						},
						emphasis: {
							label: {
								show: true,
								fontSize: '30',
								fontWeight: 'bold'
							}
						},
						labelLine: {
							show: false
						},
						data: []
					}
				]
			},
			//动态图表数据
			options:[],

		},
		watch: {
		},
		methods: {
			qaList: function () {
				var that = this;
				ms.http.get(ms.base +"/qa/qaData/statistics/info.do",{qaName:this.qaName}).then(function (res) {
					if(res.result){
						res.data.forEach(function (item) {
							//复制 opetionConf
							var optionString = JSON.stringify(that.optionConf);
							var option = JSON.parse(optionString);
							option.title.text = item.title;
							option.series[0].data = item.data;
							that.options.push(option);
						})
					}
				});
			},

			async render(qaName){
				var that = this;
				var searchJson
				await ms.http.get(ms.manager + "/qa/qa/get.do", {qaName:qaName}).then(function (res) {
					if (res.result && res.data) {
						that.mdiyModel = res.data;
						modelId = res.data.id;
						qaName = res.data.qaName;
						var data = JSON.parse(that.mdiyModel.modelJson);
						searchJson = eval(data.searchJson);
						that.$nextTick(function () {
							// 获取引入的组件dom节点
							var modelDom = document.getElementById("list-temp");
							var scriptDom = document.createElement('script');
							scriptDom.innerHTML = data.script;
							modelDom.appendChild(scriptDom); //初始化自定义模型并传入关联参数
						});
					}
				});
				return  new custom_model({
					data: {
						loading: true,//加载状态
						conditionList:searchJson,
						conditions:[],
						modelId: that.modelId,
						qaName: '',
						tableField: [],
						dataList: [],
						//自定义模型列表
						selectionList: [],
						//自定义模型列表选中
						total: 0,
						//总记录数量
						pageSize: 10,
						//页面数量
						currentPage: 1,
						//初始页
						mananger: ms.manager,
						loading: true,
						emptyText: '',
						form: {},
					},
					methods: {
						fmt:function(row, column, cellValue, index) {
							var fieldFmt = searchJson.filter(item => item.field==column.property)
							if(fieldFmt.length>0){
								return eval('this.'+fieldFmt[0].model+'Format').call(null,row, column, cellValue, index);
							}else {
								return cellValue;
							}
						},
						//新增
						save: function (row) {
							var that = this;
							row = that.lowerJSONKey(row);
							if (row) {
								location.href = this.mananger + "/qa/qaData/form.do?id=" + row.id+"&qaName="+this.qaName;
							} else {
								location.href = this.mananger + "/qa/qaData/form.do?qaName="+this.qaName;
							}
						},

						//获取表格字段
						getTableField: function () {
							var that = this;
							ms.http.get(ms.manager + "/qa/qa/get.do",{qaName:this.qaName}).then(function (data) {
								if(data.result){
									//得到的内容为json字符串，需要转对象
									//创建时间

									var createDate = {
										"model": "createDate",
										"key": "create_date",
										"field": "CREATE_DATE",
										"javaType": "date",
										"jdbcType": "date",
										"name": "创建时间",
										"type": "date",
										"isShow": "true"
									}
									that.tableField = JSON.parse(data.data.modelField);
									that.tableField.push(createDate);
									that.tableField.forEach(function (item) {
										item.key = item.key.toLowerCase();
									})

								}
							});
						},

						// 小写转换
						lowerJSONKey: function (jsonObj){
							for (var key in jsonObj){
								jsonObj[key.toLowerCase()] = jsonObj[key];
								delete(jsonObj[key]);
							}
							return jsonObj;
						},
						//查询列表
						list: function (isSearch) {
							var that = this;
							var data = null; //搜索参数
							that.loading = true;
							var page={
								pageNo: that.currentPage,
								pageSize : that.pageSize
							}
							var form = JSON.parse(JSON.stringify(that.form))


							if(isSearch) {
								//删除空字符串
								for (key in form){
									if(form[key] === undefined || form[key] === null){
										delete  form[key]
									}
								}
								form.sqlWhere ? data = Object.assign({}, {sqlWhere: form.sqlWhere}, page) : data = Object.assign({}, form, page)
							} else {
								data = Object.assign( {modelId:that.modelId}, page);
							}

							history.replaceState({form:form,page:page},"");
							ms.http.post(ms.manager + "/qa/qaData/queryData.do",data).then(function (data) {
								if (data.result){
									data = data.data;
									if (data.total <= 0) {
										that.emptyText = '暂无数据';
										that.dataList = [];
									} else {
										that.total = data.total;
										data.rows.forEach(function (item) {
											Object.keys(item).forEach(function (field) {
												try {
													if (item[field] != "" && JSON.parse(item[field]).length > 0) {
														var picture = [];
														JSON.parse(item[field]).forEach(function (img) {
															picture.push(img);
														});
														item[field] = picture;
													}
													item = that.lowerJSONKey(item);
												} catch (e) {
													console.log(e);
												}
											});
										});
										that.dataList = data.rows;
									}
									that.$nextTick(function(){
										that.$refs.multipleTable.doLayout();
									})
									that.loading = false;
								}
							});
						},
						//自定义模型列表选中
						handleSelectionChange: function (val) {
							this.selectionList = val;
						},
						//小写转换
						lowerJSONKey: function (jsonObj) {
							for (var key in jsonObj){
								jsonObj[key.toLowerCase()] = jsonObj[key];
								//排除小写字段,如：id
								if (key.toLocaleLowerCase() != key) {
									delete (jsonObj[key]);
								}
							}
							return jsonObj;
						},
						//删除
						del: function (row) {
							var that = this;
							var ids = [];
							if (row.length > 0) {
								row.forEach(function (item, index) {
									item = that.lowerJSONKey(item);
									ids.push(item.id);
								});
							} else {
								row = that.lowerJSONKey(row);
								ids.push(row.id);
							}
							that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
								confirmButtonText: '确定',
								cancelButtonText: '取消',
								type: 'warning'
							}).then(function () {
								ms.http.post(ms.manager + "/qa/qaData/delete.do", {
									modelId: that.modelId,
									ids: ids.join(',')
								}).then(function (data) {
									if (data.result) {
										that.$notify({
											title: '成功',
											type: 'success',
											message: '删除成功!'
										}); //删除成功，刷新列表

										that.list();
									} else {
										that.$notify({
											title: '失败',
											message: data.msg,
											type: 'warning'
										});
									}
								});
							});
						},
						//pageSize改变时会触发
						sizeChange: function (pagesize) {
							this.loading = true;
							this.pageSize = pagesize;
							this.list();
						},
						search:function(data){
							this.form.sqlWhere = JSON.stringify(data);
							this.list(true);
						},
						//currentPage改变时会触发
						currentChange: function (currentPage) {
							this.loading = true;
							this.currentPage = currentPage;
							this.list();
						},
						get: function() {
						}
					},
					created: function () {
						if(history.state){
							this.form = history.state.form;
							this.currentPage = history.state.page.pageNo;
							this.pageSize = history.state.page.pageSize;
						}
						this.modelId = ms.util.getParameter("modelId");
						this.qaName = ms.util.getParameter("qaName");
						this.getTableField();
						this.list();
					}
				});

			}
		},
		created: function () {

			var that = this;
			that.qaName = ms.util.getParameter("qaName");
			that.modelId = ms.util.getParameter("modelId");
			that.render(this.qaName).then(function(obj) {
				that.customModel = obj;
			});
			that.qaList()
		}
	});
</script>
<style>
	body {
		background-color: white;
	}
	#index .iconfont{
		font-size: 12px;
		margin-right: 5px;
	}
	.ms-index {
		height: calc(100vh - 30px);
	}

	.ms-header,.ms-container {
		padding-left: 0px;
	}
</style>
