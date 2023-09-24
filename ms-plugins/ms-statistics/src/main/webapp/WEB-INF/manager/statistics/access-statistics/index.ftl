<!DOCTYPE html>
<html>
<head>
	<title>访问统计</title>
	<#include "../../include/head-file.ftl">
	<script src="${base}/static/statistics/index.js"></script>
	<script src="${base}/static/plugins/composition-api/1.4.3/vue-composition-api.js"></script>
	<script src="${base}/static/plugins/echarts/5.2.2/echarts.js"></script>
	<script src="${base}/static/plugins/vue-echarts/6.0.2/index.umd.min.js"></script>
</head>
<body>

<div id="index" class="ms-index" v-cloak v-loading="loading">

	<el-main class="ms-container">
		<el-row :gutter="12">
			<el-col :span="6">
				<el-descriptions title="页面访问量(PV)" :column="1" border label-class-name="label">
					<el-descriptions-item  label="今日访问量">{{accessStatisticsData.pv.today}}</el-descriptions-item>
					<el-descriptions-item label="昨日访问量">{{accessStatisticsData.pv.yesterday}}</el-descriptions-item>
					<el-descriptions-item label="总访问量">{{accessStatisticsData.pv.total}}</el-descriptions-item>
				</el-descriptions>
			</el-col>
			<el-col :span="6">
				<el-descriptions title="网站访客数(UV)"  :column="1" border label-class-name="label">
					<el-descriptions-item label="今日访客数">{{accessStatisticsData.uv.today}}</el-descriptions-item>
					<el-descriptions-item label="昨日访客数">{{accessStatisticsData.uv.yesterday}}</el-descriptions-item>
					<el-descriptions-item label="总访客数">{{accessStatisticsData.uv.total}}</el-descriptions-item>
				</el-descriptions>
			</el-col>
			<el-col :span="6">
				<el-descriptions title=" IP数量"  :column="1" border label-class-name="label">
					<el-descriptions-item label="今日IP数量">{{accessStatisticsData.ip.today}}</el-descriptions-item>
					<el-descriptions-item label="昨日IP数量">{{accessStatisticsData.ip.yesterday}}</el-descriptions-item>
					<el-descriptions-item label="总IP数量">{{accessStatisticsData.ip.total}}</el-descriptions-item>
				</el-descriptions>
			</el-col>
			<el-col :span="6">
				<el-descriptions title="内容发布数量"  :column="1" border label-class-name="label">
					<el-descriptions-item label="今日发布">{{accessStatisticsData.content.today}}</el-descriptions-item>
					<el-descriptions-item label="昨日发布">{{accessStatisticsData.content.yesterday}}</el-descriptions-item>
					<el-descriptions-item label="总数">{{accessStatisticsData.content.total}}</el-descriptions-item>
				</el-descriptions>
			</el-col>
		</el-row>

		<el-row :gutter="12" style="margin: 10px 0;background-color: rgb(242 248 255);  padding: 8px;" >
			<el-col :span="12">
				类型：
				<el-button-group>
					<el-button size="mini" :type="switchSl=='pv'?'primary':''"  @click="switchSl='pv';getHourPageView()">访问量(PV)</el-button>
					<el-button size="mini" :type="switchSl=='uv'?'primary':''" @click="switchSl='uv';getHourUserView()">访客数(UV)</el-button>
					<el-button size="mini" :type="switchSl=='ip'?'primary':''" @click="switchSl='ip';getHourIP()">IP数量</el-button>
				</el-button-group>
			</el-col>
		</el-row>
		<el-row  style="margin: 10px 0;">
			<el-col :span="18">
				<v-chart style="height: 500px;width: 100%" autoresize :option="hourViewOption"></v-chart>
			</el-col>
			<el-col :span="6">
				<el-table height="520" ref="multipleTable" stripe :data="dataList">
					<el-table-column label="时间" align="left" width="120"  prop="HOUR">
					</el-table-column>

					<el-table-column label="访问量"  align="left" prop="num">
					</el-table-column>

					<el-table-column label="访客数"   align="left" prop="uv">
					</el-table-column>

					<el-table-column label="IP数量"   align="left" prop="pv">
					</el-table-column>
				</el-table>
			</el-col>
		</el-row>

		<el-row  :gutter="12" style="margin: 10px 0;">
			<el-col :span="12">
				<!--图表-->
				<v-chart style="height: 500px" align="center" autoresize :option="deviceOption"></v-chart>
			</el-col>
			<!--图表-->

			<el-col :span="12">
				<el-descriptions title="今日Top10受访页面" :column="1" :colon="false">
					<el-descriptions-item>
						<el-table border :data="viewDataList" tooltip-effect="dark" style="margin-left: -10px">
							<el-table-column label="来源" align="left" prop="url">
							</el-table-column>
							<el-table-column label="访问量" width="80" align="center" prop="pv">
							</el-table-column>
							<el-table-column label="浏览时长(S)" width="120" width="80" align="center" prop="TIME">
							</el-table-column>
						</el-table>
					</el-descriptions-item>

				</el-descriptions>
			</el-col>
		</el-row>

		<!--指定天数查看图表-->
		<el-row :gutter="12" style="margin: 10px 0;background-color: rgb(242 248 255);  padding: 8px;" >
			<el-col :span="12">
				时间：
				<el-button-group>
					<el-button :type="switchDay=='1'?'primary':''" @click="switchDay='1';getHourViewOfDay('today')" size="mini">今日</el-button>
					<el-button :type="switchDay=='2'?'primary':''" @click="switchDay='2';getHourViewOfDay('yesterday')" size="mini">昨天</el-button>
					<el-button :type="switchDay=='7'?'primary':''" @click="switchDay='7';getViewOfDay(7)" size="mini">近7天</el-button>
					<el-button :type="switchDay=='30'?'primary':''" @click="switchDay='30';getViewOfDay(30)" size="mini">近30天</el-button>
				</el-button-group>
			</el-col>
			<el-col :span="12" style="text-align: right">
				<el-date-picker
						v-model="form.dayViewDate"
						@change="switchDay='0';getViewOfDateRange()"
						size="mini"
						type="daterange"
						range-separator="至"
						start-placeholder="开始日期"
						end-placeholder="结束日期">
				</el-date-picker>
			</el-col>
		</el-row>
		<el-row  :gutter="12">
			<el-col :span="18">
				<!--图表-->
				<v-chart style="height: 600px" autoresize :option="totalSiteViewOption"></v-chart>
			</el-col>
			<el-col :span="6">
				<!--图表-->
				<el-table  height="540" :data="dayDataList" stripe>
					<el-table-column label="时间" width="120"  align="left" prop="day">
					</el-table-column>

					<el-table-column label="访问量" align="left" prop="num">
					</el-table-column>

					<el-table-column label="访客数"   align="left" prop="uv">
					</el-table-column>

					<el-table-column label="IP数量" align="left" prop="pv">
					</el-table-column>
				</el-table>
			</el-col>

		</el-row>

		<el-row :gutter="12" style="margin: 10px 0;">
			<el-col>
				<el-descriptions title="网站明细" :column="1" :colon="false">
					<el-descriptions-item>
						<!--显示所有的访问记录详细数据，带分页-->
						<el-table border :data="allViewDataList" tooltip-effect="dark" style="margin-left: -10px"
								  :default-sort = "{prop: 'pv', order: 'descending'}">
							<el-table-column label="来源入口" align="left" prop="url">
							</el-table-column>
							<el-table-column label="访问量" sortable width="80" align="center" prop="pv">
							</el-table-column>
							<el-table-column label="位置" width="120" align="center" prop="address">
							</el-table-column>
							<el-table-column label="IP" width="120" align="center" prop="ip">
							</el-table-column>
							<el-table-column label="平均浏览时长" sortable width="120" width="80" align="center" prop="time">
							</el-table-column>
						</el-table>
					</el-descriptions-item>
				</el-descriptions>
				<el-pagination
						background
						:page-sizes="[5,10,20,30,40,50,100]"
						layout="total, sizes, prev, pager, next, jumper"
						:current-page="currentPage"
						:page-size="pageSize"
						:total="total"
						class="ms-pagination"
						@current-change='currentChange'
						@size-change="sizeChange">
				</el-pagination>
			</el-col>
		</el-row>
	</el-main>
</div>
</body>

</html>
<script>
	Vue.component("v-chart", VueECharts);

	var indexVue = new Vue({
		el: '#index',
		data:{
			loading: false,
			switchSl:'pv', //数量切换
			switchDay: '7', //天数切换
			// 当天表单数据(小时)
			dataList: [],
			// 前十页面数据
			viewDataList: [],
			// 所有页面数据
			allViewDataList: [],
			// 每天表单数据(天数)
			dayDataList:[],
			total: 0, //总记录数量
			pageSize: 10, //页面数量
			currentPage:1, //初始页
			form: {
				// 时间范围
				dayViewDate:[],
				structs: {
					"pv" : {
						"num" : {
							"total" : {"name": "网站访问量"},
							"today" : {"name": "网站访问量", "params": {"day":"today"}},
							"yesterday" : {"name": "网站访问量", "params": {"day":"yesterday"}},
							"range" : {"name": "指定时间前N天内每天访问量","params": {"time":"","days":7}},
						},
						"hour" : {
							"today" : {"name": "网站每小时访问量"},
							"yesterday" : {"name": "网站每小时访问量", "params": {"day":"yesterday"}},
							"range" : {"name": "网站每小时访问量", "params": {"day":""}},
						},
						"top" : {
							"today" : {"name": "获取指定范围访问量前十的页面"},
							"range" : {"name": "获取指定范围访问量前十的页面", "params": {"startTime":"", "endTime":""}},
						}
					},
					"uv" : {
						"num" : {
							"today" : {"name": "网站访客数", "params": {"day":"today"}},
							"yesterday" : {"name": "网站访客数", "params": {"day":"yesterday"}},
							"range" : {"name": "指定时间前N天内每天访客数","params": {"time":"","days":7}},
							"total" : {"name": "网站访客数"},
						},
						"hour" : {
							"today" : {"name": "网站每小时访客数"},
							"yesterday" : {"name": "网站每小时访客数", "params": {"day":"yesterday"}},
							"range" : {"name": "网站每小时访客数", "params": {"day":""}},
						}
					},
					"ip" : {
						"num" : {
							"today" : {"name": "网站IP数", "params": {"day":"today"}},
							"yesterday" : {"name": "网站IP数", "params": {"day":"yesterday"}},
							"range" : {"name": "指定时间前N天内每天IP数","params": {"time":"","days":7}},
							"total" : {"name": "网站IP数"},
						},
						"hour" : {
							"today" : {"name": "网站每小时IP数"},
							"yesterday" : {"name": "网站每小时IP数", "params": {"day":"yesterday"}},
							"range" : {"name": "网站每小时IP数", "params": {"day":""}},
						}
					},
					"content" : {
						"num" : {
							"today" : {"name": "网站文章数", "params": {"day":"today"}},
							"yesterday" : {"name": "网站文章数", "params": {"day":"yesterday"}},
							"total" : {"name": "网站文章数"},
						},
					},
					"device" : {
						"num" : {
							"total" : {"name": "设备浏览量情况"},
						},

					},
				},
				params: [],
			},
			rules:{
				// 时间范围
				dayViewDate: [{"required":true,"message":"请选择时间范围", "trigger": "blur"}],
			},
			// 顶部概况数据
			accessStatisticsData: {
				pv: {
					today: 0,
					yesterday:0,
					total:0,
				},
				uv: {
					today: 0,
					yesterday:0,
					total:0,
				},
				ip: {
					today: 0,
					yesterday:0,
					total:0,
				},
				content: {
					today: 0,
					yesterday:0,
					total:0,
				},
			},
			// 设备访问量图表
			deviceOption :  {
				title: {
					text: '设备访问量'
				},
				tooltip: {
					trigger: 'item',
					formatter: '{a} <br/>{b}: {c} ({d}%)'
				},
				legend: {
					data: [],
					orient: "vertical",
					left: "left",
					top: "10%"
				},
				series: [
					{
						name: '访问量',
						type: 'pie',
						radius: ['50%', '70%'],
						avoidLabelOverlap: false,
						label: {
							show: false,
							position: 'center'
						},
						data: [
						]
					}
				]
			},

			// 趋势图表
			totalSiteViewOption : {
				title: {
					text: '趋势图'
				},
				legend: {
					data: ['访问量(PV)', '访客数(UV)', 'IP数'],
					right: "2%",
					orient: "horizontal"
				},
				tooltip: {
					trigger: 'axis'
				},
				xAxis: {
					type: 'category',
					data: []
				},
				yAxis: {
					type: 'value'
				},
				series: [
					{
						name: '访问量(PV)',
						data: [],
						areaStyle: {},
						type: 'line',
						smooth: true
					},
					{
						name: '访客数(UV)',
						data: [],
						areaStyle: {},
						type: 'line',
						smooth: true
					},{
						name: 'IP数',
						data: [],
						areaStyle: {},
						type: 'line',
						smooth: true
					}
				],
				grid: {
					left:"2%",
					right:"2%"
				}
			},
			// 实时访问量
			hourViewOption : {
				title: {
					text: '实时访问量'
				},
				tooltip: {
					trigger: 'axis'
				},
				legend: {
					orient: "horizontal",
					right: "2%",
					data: ['今日访问量', '昨日访问量'],
				},
				xAxis: {
					type: 'category',
					data: []
				},
				yAxis: {
					type: 'value'
				},
				series: [
					{
						name: '今日访问量',
						data: [],
						type: 'line',
						smooth: true
					},
					{
						name: '昨日访问量',
						data: [],
						type: 'line',
						smooth: true
					}
				],
				grid: {
					left:"2%",
					right:"2%"
				}
			},
		},
		watch:{

		},
		methods:{

			//pageSize改变时会触发
			sizeChange:function(pagesize) {
				this.loading = true;
				this.pageSize = pagesize;
				this.list();
			},
			//currentPage改变时会触发
			currentChange:function(currentPage) {
				this.loading = true;
				this.currentPage = currentPage;
				this.list();
			},

			//底部详情列表
			list: function() {
				var that = this;
				that.loading = true;
				var page={
					pageNo: that.currentPage,
					pageSize : that.pageSize
				}
				ms.http.post(ms.manager+"/statistics/accessStatistics/list.do",page).then(
						function(res) {
							if (!res.result||res.data.total <= 0) {
								that.emptyText ="暂无数据"
								that.allViewDataList = [];
								that.total = 0;
							} else {
								that.emptyText = '';
								that.total = res.data.total;
								that.allViewDataList = res.data.rows;
							}
							that.loading = false;
						}).catch(function(err) {
					that.loading = false;
					console.log(err);
				});

			},

			// 实时访问图-今日每小时网站访问量与昨日每小时网站访问量
			getHourPageView: function () {
				var that = this;
				that.hourViewOption.series[0].data = [];
				that.hourViewOption.series[1].data = [];
				ms.statistics([that.form.structs.pv.hour.today]).then(function (res) {
					if(res.result){
						that.hourViewOption.legend.data[0] = '今日访问量'
						that.hourViewOption.series[0].name = '今日访问量'
						res.data["网站每小时访问量"].forEach(function (value) {
							that.hourViewOption.series[0].data.push(value.num);
						});
					}else {
					}
				})
				ms.statistics([that.form.structs.pv.hour.yesterday]).then(function (res) {
					if(res.result){
						that.hourViewOption.legend.data[1] = '昨日访问量'
						that.hourViewOption.series[1].name = '昨日访问量'
						res.data["网站每小时访问量"].forEach(function (value) {
							that.hourViewOption.series[1].data.push(value.num);
						});
					}else {
					}
				})
			},

			// 实时访问图-今日每小时ip数与昨日每小时ip数
			getHourIP: function () {
				var that = this;
				that.hourViewOption.series[0].data = [];
				that.hourViewOption.series[1].data = [];
				ms.statistics([that.form.structs.ip.hour.today]).then(function (res) {
					if(res.result){
						that.hourViewOption.legend.data[0] = '今日IP数'
						that.hourViewOption.series[0].name = '今日IP数'
						res.data["网站每小时IP数"].forEach(function (value) {
							that.hourViewOption.series[0].data.push(value.num);
						});

					}else {
					}
				})
				ms.statistics([that.form.structs.ip.hour.yesterday]).then(function (res) {
					if(res.result){
						that.hourViewOption.legend.data[1] = '昨日IP数'
						that.hourViewOption.series[1].name = '昨日IP数'
						res.data["网站每小时IP数"].forEach(function (value) {
							that.hourViewOption.series[1].data.push(value.num);
						});

					}else {
					}
				})
			},

			// 实时访问图-今日每小时访客数与昨日每小时访客数
			getHourUserView: function () {
				var that = this;
				that.hourViewOption.series[0].data = [];
				that.hourViewOption.series[1].data = [];
				ms.statistics([that.form.structs.uv.hour.today]).then(function (res) {
					if(res.result){
						that.hourViewOption.legend.data[0] = '今日访客数'
						that.hourViewOption.series[0].name = '今日访客数'
						res.data["网站每小时访客数"].forEach(function (value) {
							that.hourViewOption.series[0].data.push(value.num);
						});

					}else {
					}
				})

				ms.statistics([that.form.structs.uv.hour.yesterday]).then(function (res) {
					if(res.result){
						that.hourViewOption.legend.data[1] = '昨日访客数'
						that.hourViewOption.series[1].name = '昨日访客数'
						res.data["网站每小时访客数"].forEach(function (value) {
							that.hourViewOption.series[1].data.push(value.num);
						});

					}else {
					}
				})
			},

			// 趋势图-获取指定范围
			getViewOfDateRange: function () {
				var that = this;

				var myDate = (new Date(that.form.dayViewDate[1].getTime() + 8*60*60*1000)).toJSON().split('T').join(' ').substr(0,10);
				that.form.structs.pv.num.range.params.time = myDate;
				that.form.structs.uv.num.range.params.time = myDate;
				that.form.structs.ip.num.range.params.time = myDate;
				// 清空数据
				that.totalSiteViewOption.series[0].data = [];
				that.totalSiteViewOption.series[1].data = [];
				that.totalSiteViewOption.series[2].data = [];
				that.totalSiteViewOption.xAxis.data = [];
				// 设置时间
				var day = ((that.form.dayViewDate[1] - that.form.dayViewDate[0]) / (1000*60*60*24)) + 1
				that.form.structs.pv.num.range.params.days = day;
				that.form.structs.uv.num.range.params.days = day;
				that.form.structs.ip.num.range.params.days = day;
				ms.statistics([that.form.structs.pv.num.range,that.form.structs.uv.num.range, that.form.structs.ip.num.range]).then(function (res) {
					if(res.result){
						that.dayTableData(res);
						// 设置数据 - 访问量
						res.data[that.form.structs.pv.num.range.name].forEach(function (value) {
							that.totalSiteViewOption.series[0].data.push(value.num);
							that.totalSiteViewOption.xAxis.data.push(value.day);
						});
						// 设置数据 - 访客数
						res.data[that.form.structs.uv.num.range.name].forEach(function (value) {
							that.totalSiteViewOption.series[1].data.push(value.num);
						});
						// 设置数据 - IP数
						res.data[that.form.structs.ip.num.range.name].forEach(function (value) {
							that.totalSiteViewOption.series[2].data.push(value.num);
						});
					}else {
					}
				})
			},

			// 趋势图-指定时间前N天内每天数据
			getViewOfDay: function (day) {
				var that = this;
				// 清空数据
				that.form.dayViewDate = [];
				var myDate = (new Date((new Date).getTime() + 8*60*60*1000)).toJSON().split('T').join(' ').substr(0,10);
				that.form.structs.pv.num.range.params.time = myDate;
				that.form.structs.uv.num.range.params.time = myDate;
				that.form.structs.ip.num.range.params.time = myDate;
				that.totalSiteViewOption.series[0].data = [];
				that.totalSiteViewOption.series[1].data = [];
				that.totalSiteViewOption.series[2].data = [];
				that.totalSiteViewOption.xAxis.data = [];
				// 设置时间
				that.form.structs.pv.num.range.params.days = day;
				that.form.structs.uv.num.range.params.days = day;
				that.form.structs.ip.num.range.params.days = day;
				ms.statistics([that.form.structs.pv.num.range,that.form.structs.uv.num.range, that.form.structs.ip.num.range]).then(function (res) {
					if(res.result){
						that.dayTableData(res);
						// 设置数据 - 访问量
						res.data[that.form.structs.pv.num.range.name].forEach(function (value) {
							that.totalSiteViewOption.series[0].data.push(value.num);
							that.totalSiteViewOption.xAxis.data.push(value.day);
						});
						// 设置数据 - 访客数
						res.data[that.form.structs.uv.num.range.name].forEach(function (value) {
							that.totalSiteViewOption.series[1].data.push(value.num);
						});
						// 设置数据 - IP数
						res.data[that.form.structs.ip.num.range.name].forEach(function (value) {
							that.totalSiteViewOption.series[2].data.push(value.num);
						});
					}else {
					}
				})
			},


			// 趋势图-指定日期网站每小时数据
			getHourViewOfDay: function (day) {
				var that = this;
				// 初始化数据
				that.totalSiteViewOption.series[0].data = [];
				that.totalSiteViewOption.series[1].data = [];
				that.totalSiteViewOption.series[2].data = [];
				that.totalSiteViewOption.xAxis.data = [];
				var params = [];
				if (day == "today") {
					params.push(that.form.structs.pv.hour.today);
					params.push(that.form.structs.uv.hour.today);
					params.push(that.form.structs.ip.hour.today);
				} else if (day == "yesterday") {
					params.push(that.form.structs.pv.hour.yesterday);
					params.push(that.form.structs.uv.hour.yesterday);
					params.push(that.form.structs.ip.hour.yesterday);
				} else {
					that.form.structs.pv.hour.range.params.day = day;
					that.form.structs.uv.hour.range.params.day = day;
					that.form.structs.ip.hour.range.params.day = day;
					params.push(that.form.structs.pv.hour.range);
					params.push(that.form.structs.uv.hour.range);
					params.push(that.form.structs.ip.hour.range);
				}
				ms.statistics(params).then(function (res) {
					if(res.result){
						res.data["网站每小时访问量"].forEach(function (value) {
							that.totalSiteViewOption.xAxis.data.push(value.HOUR);
							that.totalSiteViewOption.series[0].data.push(value.num);
						});
						res.data["网站每小时访客数"].forEach(function (value) {
							that.totalSiteViewOption.series[1].data.push(value.num);
						});
						res.data["网站每小时IP数"].forEach(function (value) {
							that.totalSiteViewOption.series[2].data.push(value.num);
						});
					}else {
					}
				})
			},

			// 日期表单数据渲染
			dayTableData: function (res) {
				var that = this;
				that.dayDataList = res.data[that.form.structs.pv.num.range.name];
				// 表单数据
				for(var i = 0; i < res.data[that.form.structs.ip.num.range.name].length; i++) {
					if (res.data[that.form.structs.ip.num.range.name][i].day == that.dayDataList[i].day) {
						that.dayDataList[i].pv = res.data[that.form.structs.ip.num.range.name][i].num;
						that.dayDataList[i].uv = res.data[that.form.structs.uv.num.range.name][i].num;
					}
				}
			},
			// 小时表单数据渲染
			hourTableData: function (res) {
				var that = this;
				that.dataList = res.data[that.form.structs.pv.hour.today.name];
				// 表单数据
				for(var i = 0; i < res.data[that.form.structs.ip.hour.today.name].length; i++) {
					if (res.data[that.form.structs.ip.hour.today.name][i].HOUR == that.dataList[i].HOUR) {
						that.dataList[i].pv = res.data[that.form.structs.ip.hour.today.name][i].num;
						that.dataList[i].uv = res.data[that.form.structs.uv.hour.today.name][i].num;
					}
				}
			},
			// 参数组装
			getRequestParams: function () {
				var that = this;
				var params = [];
				// 浏览量
				params.push(that.form.structs.pv.num.range);
				params.push(that.form.structs.pv.top.today);
				// 访客数
				params.push(that.form.structs.uv.num.range);
				// IP数
				params.push(that.form.structs.ip.num.range);
				// 设备数
				params.push(that.form.structs.device.num.total);
				return params;
			},

			// 获取所有统计数据
			getAccessStatisticsData:function (){
				var that = this;
				that.loading = true;
				// 顶部概况数据
				// 今日
				ms.statistics([that.form.structs.pv.num.today, that.form.structs.uv.num.today,
					that.form.structs.ip.num.today, that.form.structs.content.num.today]).then(function (res) {
					if(res.result){
						that.accessStatisticsData.pv.today = res.data[that.form.structs.pv.num.today.name][0].num;
						that.accessStatisticsData.uv.today = res.data[that.form.structs.uv.num.today.name][0].num;
						that.accessStatisticsData.ip.today = res.data[that.form.structs.ip.num.today.name][0].num;
						that.accessStatisticsData.content.today = res.data[that.form.structs.content.num.today.name][0].newContent;
					}else {
					}
				})
				// 昨日
				ms.statistics([that.form.structs.pv.num.yesterday,that.form.structs.uv.num.yesterday,
					that.form.structs.ip.num.yesterday, that.form.structs.content.num.yesterday]).then(function (res) {
					if(res.result){
						that.accessStatisticsData.pv.yesterday = res.data[that.form.structs.pv.num.yesterday.name][0].num;
						that.accessStatisticsData.uv.yesterday = res.data[that.form.structs.uv.num.yesterday.name][0].num;
						that.accessStatisticsData.ip.yesterday = res.data[that.form.structs.ip.num.yesterday.name][0].num;
						that.accessStatisticsData.content.yesterday = res.data[that.form.structs.content.num.yesterday.name][0].newContent;
					}else {
					}
				})
				// 总数
				ms.statistics([that.form.structs.pv.num.total, that.form.structs.uv.num.total,
					that.form.structs.ip.num.total, that.form.structs.content.num.total]).then(function (res) {
					if(res.result){
						that.accessStatisticsData.pv.total = res.data[that.form.structs.pv.num.total.name][0].num;
						that.accessStatisticsData.uv.total = res.data[that.form.structs.uv.num.total.name][0].num;
						that.accessStatisticsData.ip.total = res.data[that.form.structs.ip.num.total.name][0].num;
						that.accessStatisticsData.content.total = res.data[that.form.structs.content.num.total.name][0].newContent;
					}else {
					}
				})

				// 实时访问数据 - 今日
				ms.statistics([that.form.structs.pv.hour.today, that.form.structs.uv.hour.today, that.form.structs.ip.hour.today]).then(function (res) {
					if(res.result){
						// 图表数据
						res.data[that.form.structs.pv.hour.today.name].forEach(function (value) {
							that.hourViewOption.xAxis.data.push(value.xAxis);
							that.hourViewOption.series[0].data.push(value.num);
						});
						// 表单数据
						that.hourTableData(res);
					}else {
					}
				})
				// 昨日
				ms.statistics([that.form.structs.pv.hour.yesterday]).then(function (res) {
					if(res.result){
						// 图表数据
						res.data[that.form.structs.pv.hour.yesterday.name].forEach(function (value) {
							that.hourViewOption.series[1].data.push(value.num);
						});
					}else {
					}
				})


				ms.statistics(that.form.params).then(function (res) {
					if(res.result){
						// 渲染当天前十页面表单
						that.viewDataList = res.data[that.form.structs.pv.top.today.name];
						that.dayTableData(res);
						// 趋势图数据
						res.data[that.form.structs.pv.num.range.name].forEach(function (value) {
							that.totalSiteViewOption.xAxis.data.push(value.day);
							that.totalSiteViewOption.series[0].data.push(value.num);
						});
						res.data[that.form.structs.uv.num.range.name].forEach(function (value) {
							that.totalSiteViewOption.series[1].data.push(value.num);
						});
						res.data[that.form.structs.ip.num.range.name].forEach(function (value) {
							that.totalSiteViewOption.series[2].data.push(value.num);
						});
						// 设备数据
						res.data[that.form.structs.device.num.total.name].forEach(function (value) {
							that.deviceOption.series[0].data.push(value);
							that.deviceOption.legend.data.push(value.name);
						});
					}else {
					}
				})

			},
		},
		created:function(){
			var that = this;
			// 初始化值
			that.form.params = that.getRequestParams();
			// 设置时间为当天
			var myDate = (new Date((new Date).getTime() + 8*60*60*1000)).toJSON().split('T').join(' ').substr(0,10);
			that.form.structs.pv.num.range.params.time = myDate;
			that.form.structs.uv.num.range.params.time = myDate;
			that.form.structs.ip.num.range.params.time = myDate;
			that.getAccessStatisticsData();
			that.list();
		},
	})
</script>
<style>
	#index .ms-container {
		height: calc(100vh - 78px);
	}
	#index .label {
		width: 180px;
		background-color: rgb(242 248 255);
	}
</style>
