<!DOCTYPE html>
<html>
<head>
	<title>自定义表单数据列表</title>
	<#include "../../include/head-file.ftl">
</head>
<body>

<#include "../../mdiy/form/data/index-table.ftl">
<div id="index" v-cloak >

</div>
</body>

</html>
<script>
	var indexVue = new Vue({
		el: '#index',
		data: function () {
			return {
				mdiyModel: {},
				customModel: {},
			}
		},
		watch: {
		},
		methods: {
			async render(modelName){
				var that = this;
				var searchJson
				await ms.http.get(ms.manager + "/mdiy/form/get.do", {modelName:modelName}).then(function (res) {
					if (res.result && res.data) {
						that.mdiyModel = res.data;
						modelId = res.data.id;
						modelName = res.data.modelName;
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
						modelName: '',
						tableField: [],
						dataList: [],
						//自定义模型列表
						selectionList: [],
						//自定义模型列表选中
						total: 0,
						//总记录数量
						pageSize: 50,
						//页面数量
						currentPage: 1,
						//初始页
						mananger: ms.manager,
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
						save: function (id) {
							if (id) {
								// location.href = this.mananger + "/mdiy/formData/form.do?id=" + id+"&modelName="+this.modelName;
								ms.util.openSystemUrl("/mdiy/form/data/form.do?id=" + id+"&modelName="+this.modelName);
							} else {
								// location.href = this.mananger + "/mdiy/formData/form.do?modelName="+this.modelName;
								ms.util.openSystemUrl("/mdiy/form/data/form.do?modelName="+this.modelName);
							}
						},
						//获取表格字段
						getTableField: function () {
							var that = this;
							ms.http.get(ms.manager + "/mdiy/form/get.do",{modelName:this.modelName}).then(function (data) {
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

								}else {
									that.$notify({
										title: '失败',
										message: data.msg,
										type: 'warning'
									});
								}
							});
						},

						// 小写转换
						lowerJSONKey: function (jsonObj){
							for (var key in jsonObj){
								jsonObj[key.toLowerCase()] = jsonObj[key];
								//排除小写字段 如：id
								if(key.toLowerCase()!=key) {
									delete(jsonObj[key]);
								}
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
								for (var key in form){
									if(form[key] === undefined || form[key] === null){
										delete  form[key]
									}
								}
								form.sqlWhere ? data = Object.assign({modelId:that.modelId}, {sqlWhere: form.sqlWhere}, page) : data = Object.assign({modelId:that.modelId}, form, page)
							} else {
								data = Object.assign( {modelId:that.modelId}, page);
							}

							history.replaceState({form:form,page:page},"");
							ms.http.post(ms.manager + "/mdiy/form/data/queryData.do",data).then(function (data) {
								if (data.result){
									data = data.data;
									if (data.total <= 0) {
										that.emptyText = '暂无数据';
										that.dataList = [];
									} else {
										that.total = data.total;
										data.rows.forEach(function (item) {
											item = that.lowerJSONKey(item);
											Object.keys(item).forEach(function (field) {
												field = field.toLowerCase()
												try {
													if (item[field] != "" && JSON.parse(item[field]).length > 0) {
														var picture = [];
														JSON.parse(item[field]).forEach(function (img) {
															picture.push(img);
														});
														item[field] = picture;
													}
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
								}else {
									that.$notify({
										title: '失败',
										message: data.msg,
										type: 'warning'
									});
								}
								that.loading = false;
							});
						},
						//自定义模型列表选中
						handleSelectionChange: function (val) {
							this.selectionList = val;
						},
						//删除
						del: function (row) {
							var that = this;
							var ids = [];
							if (row.length > 0) {
								row.forEach(function (item, index) {
									ids.push(item.id);
								});
							} else {
								ids.push(row.id);
							}
							that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
								confirmButtonText: '确定',
								cancelButtonText: '取消',
								type: 'warning'
							}).then(function () {
								ms.http.post(ms.manager + "/mdiy/form/data/delete.do", {
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
						this.modelId = '${modelId}';
						this.modelName = ms.util.getParameter("modelName");
						this.getTableField();
						this.list();
					}
				});

			}
		},
		created: function () {
			var that = this;
			this.modelName = ms.util.getParameter("modelName");
			this.modelId = '${modelId}'
			this.render(this.modelName).then(function(obj) {
				that.customModel = obj;
			});
		}
	});
</script>
<style>
	#index .iconfont{
		font-size: 12px;
		margin-right: 5px;
	}
</style>

