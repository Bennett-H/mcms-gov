<!DOCTYPE html>
<html>
<head>
    <title>采集任务</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="spider:task:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="spider:task:del">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"
                           :disabled="!selectionList.length">删除
                </el-button>
            </@shiro.hasPermission>
        </el-col>
    </el-header>


    <el-main class="ms-container">
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info"
                description="理论上可以采集任意数据到当前系统的任意表，第一步：添加采集任务，第二步：添加采集规则">
        </el-alert>
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column align="left" prop="taskName" width="180">
                <template slot='header'>采集名称
                    <el-popover placement="top-start" title="提示" trigger="hover">
                        配合任务调度插件可以达到自动采集的效果 spidertaskBizImpl.job("采集名称")
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column label="导入表" align="left" prop="importTable" width="180">
            </el-table-column>
            <el-table-column label="自动导入" align="center" prop="isAutoImport" width="80" :formatter="isAutoImportFormat">
            </el-table-column>
            <el-table-column>
            </el-table-column>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="spider:task:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                    </@shiro.hasPermission>

                    <@shiro.hasPermission name="spider:taskRegular:view">
                        <el-link type="primary" :underline="false" @click="regularList(scope.row)">规则列表</el-link>
                    </@shiro.hasPermission>


                    <@shiro.hasPermission name="spider:task:del">
                        <el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
                    </@shiro.hasPermission>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                background
                :page-sizes="[10,20,30,40,50,100]"
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
<#include "/spider/task/form.ftl">
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: {
            conditionList: [
                {action: 'and', field: 'task_name', el: 'eq', model: 'taskName', name: '采集名称', type: 'input'},
                {
                    action: 'and',
                    field: 'import_table',
                    el: 'eq',
                    model: 'importTable',
                    name: '导入表',
                    key: 'value',
                    title: 'label',
                    type: 'select',
                    multiple: false
                },
                {
                    action: 'and',
                    field: 'is_auto_import',
                    el: 'eq',
                    model: 'isAutoImport',
                    name: '自动导入',
                    key: 'value',
                    title: 'label',
                    type: 'radio',
                    multiple: false
                },
                {
                    action: 'and',
                    field: 'is_repeat',
                    el: 'eq',
                    model: 'isRepeat',
                    name: '是否去重',
                    key: 'value',
                    title: 'label',
                    type: 'radio',
                    multiple: false
                },
            ],
            conditions: [],
            dataList: [], //采集任务列表
            selectionList: [],//采集任务列表选中
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage: 1, //初始页
            manager: ms.manager,
            loadState: false,
            loading: true,//加载状态
            emptyText: '',//提示文字
            importTableOptions: [],
            isAutoImportOptions: [{"value": "yes", "label": "是"}, {"value": "no", "label": "否"}],
            isRepeatOptions: [{"value": "yes", "label": "是"}, {"value": "no", "label": "否"}],
            //搜索表单
            form: {
                sqlWhere: null,
            },
        },
        watch: {},
        methods: {
            //表格数据转换
            isAutoImportFormat(row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    if (cellValue == 'yes') {
                        return '是'
                    } else {
                        return '否'
                    }
                }
                return cellValue;
            },
            regularList(row) {
                location.href = ms.manager + "/spider/taskRegular/index.do?taskId=" + row.id + "&tableName=" + row.importTable;
            },


            //查询列表
            list: function () {
                var that = this;
                that.loading = true;
                that.loadState = false;
                var page = {
                    pageNo: that.currentPage,
                    pageSize: that.pageSize
                }
                var form = JSON.parse(JSON.stringify(that.form))
                for (key in form) {
                    if (!form[key]) {
                        delete form[key]
                    }
                }
                history.replaceState({form: form, page: page}, "");
                ms.http.post(ms.manager + "/spider/task/list.do", form.sqlWhere ? Object.assign({}, {sqlWhere: form.sqlWhere}, page)
                    : Object.assign({}, form, page)).then(
                    function (res) {
                        if (that.loadState) {
                            that.loading = false;
                        } else {
                            that.loadState = true
                        }
                        if (!res.result || res.data.total <= 0) {
                            that.emptyText = '暂无数据'
                            that.dataList = [];
                            that.total = 0;
                        } else {
                            that.emptyText = '';
                            that.total = res.data.total;
                            that.dataList = res.data.rows;
                        }
                    }).catch(function (err) {
                    console.log(err);
                });
                setTimeout(function () {
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true
                    }
                }, 500);
            },
            //采集任务列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/spider/task/delete.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    type: 'success',
                                    message: '删除成功!'
                                });
                                //删除成功，刷新列表
                                that.list();
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        });
                })
            },
            //新增
            save: function (id) {
                form.open(id);
            },

            isRepeatFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.isRepeatOptions.find(function (value) {
                        return value.value == cellValue;
                    })
                    if (data && data.label) {
                        value = data.label;
                    }
                }
                return value;
            },
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
                this.currentPage = 1;
                this.form.sqlWhere = null;
                this.$refs.searchForm.resetFields();
                this.list();
            },
        },
        created: function () {
            if (history.hasOwnProperty("state") && history.state) {
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
        height: calc(100vh - 78px);
    }
</style>
