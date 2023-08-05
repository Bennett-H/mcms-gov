<!DOCTYPE html>
<html>
<head>
    <title>会员日志</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <div class="ms-search">
        <el-row>
            <el-form :model="form" ref="searchForm" label-width="120px" size="mini">
                <el-row>
                    <el-col :span="8">
                        <!--会员编号-->
                        <el-form-item label="会员编号" prop="peopleId">
                            <el-input
                                    v-model="form.peopleId"
                                    :style="{width:  '100%'}"
                                    :clearable="true"
                                    placeholder="请输入会员编号">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <!--会员编号-->
                        <el-form-item label="标题" prop="logTitle">
                            <el-input
                                    v-model="form.logTitle"
                                    :style="{width:  '100%'}"
                                    :clearable="true"
                                    placeholder="请输入标题">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <!--日志类型-->
                        <el-form-item label="日志类型" prop="logType">
                            <el-select v-model="form.logType"
                                       :style="{width: '100%'}"
                                       :filterable="true"
                                       :disabled="false"
                                       :multiple="false" :clearable="true"
                                       placeholder="请选择日志类型">
                                <el-option v-for='item in logTypeOptions' :key="item" :value="item"
                                           :label="item"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="8">
                        <!--日志类型-->
                        <el-form-item label="请求状态" prop="logStatus">
                            <el-select v-model="form.logStatus"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :multiple="false" :clearable="true"
                                       placeholder="请选择请求状态">
                                <el-option v-for='item in logStatusOptions' :key="item.value" :value="item.value"
                                           :label="item.label"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="创建时间" prop="createDateScope">
                            <el-date-picker
                                    v-model="form.createDateScope"
                                    value-format="yyyy-MM-dd HH:mm:ss"
                                    type="datetimerange"
                                    :style="{width:  '100%'}"
                                    start-placeholder="开始时间"
                                    end-placeholder="结束时间">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8" style="text-align: right;">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list(true)">
                            搜索
                        </el-button>
                        <el-button type="primary" icon="iconfont icon-shaixuan1" size="mini"
                                   @click="currentPage=1;$refs.search.open()">筛选
                        </el-button>
                        <el-button @click="rest" icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-main class="ms-container">
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40" :selectable="isChecked"></el-table-column>
            <el-table-column label="标题" align="left" prop="logTitle">
            </el-table-column>
            <el-table-column label="会员编号" align="left" prop="peopleId">
            </el-table-column>
            <el-table-column label="IP" align="left" prop="logIp">
            </el-table-column>
            <el-table-column label="所在地区" align="left" prop="logAddr">
            </el-table-column>
            <el-table-column label="请求状态" width="80" align="center" prop="logStatus" :formatter="logStatusFormat">
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createDate">
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">详情</el-link>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                background
                :page-sizes="[50,100,200,500]"
                layout="total, sizes, prev, pager, next, jumper"
                :current-page="currentPage"
                :page-size="pageSize"
                :total="total"
                class="ms-pagination"
                @current-change='currentChange'
                @size-change="sizeChange">
        </el-pagination>
    </el-main>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: {
            conditionList: [
                //会员编号
                {
                    'action': 'and',
                    'field': 'PEOPLE_ID',
                    'el': 'eq',
                    'model': 'peopleId',
                    'name': '会员编号',
                    'type': 'input'
                },
                //IP
                {'action': 'and', 'field': 'LOG_IP', 'el': 'eq', 'model': 'logIp', 'name': 'IP', 'type': 'input'},
                //所在地区
                {'action': 'and', 'field': 'LOG_ADDR', 'el': 'eq', 'model': 'logAddr', 'name': '所在地区', 'type': 'input'},
                // 日志类型
                {
                    'action': 'and',
                    'field': 'LOG_TYPE',
                    'el': 'eq',
                    'model': 'logType',
                    'name': '日志类型',
                    'key': 'dictValue',
                    'title': 'dictLabel',
                    'type': 'select',
                    'multiple': false
                },
                //日志信息
                {'action': 'and', 'field': 'LOG_INFO', 'el': 'eq', 'model': 'logInfo', 'name': '日志信息', 'type': 'input'},
            ],
            logStatusOptions: [{"value": "success", "label": "成功"}, {"value": "error", "label": "失败"}],
            conditions: [],
            dataList: [], //会员日志列表
            selectionList: [],//会员日志列表选中
            total: 0, //总记录数量
            pageSize: 50, //页面数量
            currentPage: 1, //初始页
            manager: ms.manager,
            loading: true,//加载状态
            emptyText: '',//提示文字
            // 日志类型
            logTypeOptions: [],
            //搜索表单
            form: {
                sqlWhere: null,
                logStatus: null,
                createDateScope: null,
            },
        },
        watch: {},
        methods: {
            isChecked: function (row) {
                if (row.del == 3) {
                    return false;
                }
                return true;
            },
            //查询列表
            list: function (isSearch) {
                var that = this;
                var data = {}; //搜索参数
                that.loading = true;
                var page = {
                    pageNo: that.currentPage,
                    pageSize: that.pageSize
                }
                var form = JSON.parse(JSON.stringify(that.form))

                //处理时间范围
                if (form.createDateScope) {
                    form.startTime = form.createDateScope[0];
                    form.endTime = form.createDateScope[1];
                }

                if (isSearch) {
                    //删除空字符串
                    for (var key in form) {
                        if (form[key] === undefined || form[key] === null) {
                            delete form[key]
                        }
                    }
                    form.sqlWhere ? data = Object.assign({}, {sqlWhere: form.sqlWhere}, page) : data = Object.assign({}, form, page)
                } else {
                    data = page;
                }

                history.replaceState({form: that.form, page: page}, "");
                ms.http.post(ms.manager + "/people/peopleLog/list.do", data).then(
                    function (res) {
                        if (!res.result || res.data.total <= 0) {
                            that.emptyText = "暂无数据"
                            that.dataList = [];
                            that.total = 0;
                        } else {
                            that.emptyText = '';
                            that.total = res.data.total;
                            that.dataList = res.data.rows;
                        }
                        that.loading = false;
                    }).catch(function (err) {
                    that.loading = false;
                    console.log(err);
                });

            },
            //会员日志列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //新增
            save: function (id) {
                if (id) {
                    location.href = this.manager + "/people/peopleLog/form.do?id=" + id;
                }
            },

            //pageSize改变时会触发
            sizeChange: function (pagesize) {
                this.loading = true;
                this.pageSize = pagesize;
                this.list(true);
            },
            //currentPage改变时会触发
            currentChange: function (currentPage) {
                this.loading = true;
                this.currentPage = currentPage;
                this.list(true);
            },
            search: function (data) {
                this.form.sqlWhere = JSON.stringify(data);
                this.list(true);
            },
            //重置表单
            rest: function () {
                this.currentPage = 1;
                this.form = {
                    sqlWhere: null
                };
                this.list();
            },
            //表格数据转换
            logStatusFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.logStatusOptions.find(function (value) {
                        return value.value == cellValue;
                    })
                    if (data && data.label) {
                        value = data.label;
                    }
                }
                return value;
            },
            //获取logType数据源
            logTypeOptionsGet: function () {
                var that = this;
                ms.http.post(ms.manager + '/people/peopleLog/queryLogType.do').then(function (res) {
                    that.logTypeOptions = res.data;
                }).catch(function (err) {
                    console.log(err);
                });
            },

        },
        created: function () {
            var that = this;
//加载日志类型 数据
            this.logTypeOptionsGet();

            if (history.state) {
                this.form = history.state.form;
                this.currentPage = history.state.page.pageNo;
                this.pageSize = history.state.page.pageSize;
            }

            this.list();
        },
    })
</script>
<style>
    #index .ms-container {
        height: calc(100vh - 141px);
    }
</style>
