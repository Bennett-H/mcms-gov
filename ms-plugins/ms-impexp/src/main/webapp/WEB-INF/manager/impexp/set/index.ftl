<!DOCTYPE html>
<html>
<head>
    <title>导入导出配置</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="impexp:set:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="impexp:set:del">
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
                description="低代码实现快速excel、word数据的导入导出功能，
                基于apache的poi与docx4j进行二次开发，提供了excel、word的导入导出功能。通常用于列表数据展示页的导入导出">
            <template slot="title">
                功能介绍 <a href='http://doc.mingsoft.net/plugs/exp-imp/jie-shao.html' target="_blank">开发手册</a>
            </template>
        </el-alert>
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column label="名称" align="left" prop="name">
            </el-table-column>


            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="impexp:set:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="impexp:set:del">
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
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: {
            conditionList: [
                {action: 'and', field: 'name', el: 'eq', model: 'name', name: '导入导出标识', type: 'input'},
                {action: 'and', field: 'export_sql', el: 'eq', model: 'exportSql', name: '导出sql配置', type: 'textarea'},
                {
                    action: 'and',
                    field: 'import_json',
                    el: 'eq',
                    model: 'importJson',
                    name: '导入主表json',
                    type: 'textarea'
                },
            ],
            conditions: [],
            dataList: [], //导入导出配置列表
            selectionList: [],//导入导出配置列表选中
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage: 1, //初始页
            manager: ms.manager,
            loadState: false,
            loading: true,//加载状态
            emptyText: '',//提示文字
            //搜索表单
            form: {
                sqlWhere: null,
            },
        },
        watch: {},
        methods: {
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
                ms.http.post(ms.manager + "/impexp/set/list.do", form.sqlWhere ? Object.assign({}, {sqlWhere: form.sqlWhere}, page)
                    : Object.assign({}, form, page)).then(
                    function (res) {
                        if (that.loadState) {
                            that.loading = false;
                        } else {
                            that.loadState = true
                        }
                        if (!res.result || res.data.total <= 0) {
                            that.emptyText = "暂无数据"
                            that.dataList = [];
                            that.total = 0;
                        } else {
                            that.emptyText = '';
                            that.total = res.data.total;
                            that.dataList = res.data.rows;
                        }
                    }).catch(function (err) {
                    that.$notify({
                        title: "错误",
                        message: "列表接口暑数据请求异常",
                        type: 'warning'
                    });
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
            //导入导出配置列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //复制
            copy: function (row) {
                var that = this;
                delete row.id
                ms.http.post(ms.manager + "/impexp/set/save.do", row).then(function (res) {
                    if (res.result) {
                        that.$notify({
                            type: 'success',
                            message: "复制成功"
                        });
                        //复制成功，刷新列表
                        that.list();
                    } else {
                        that.$notify({
                            title: "错误",
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                });
            },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm("此操作将永久删除所选内容, 是否继续", "提示", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/impexp/set/delete.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    type: 'success',
                                    message: "删除成功"
                                });
                                //删除成功，刷新列表
                                that.list();
                            } else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        });
                })
            },
            //新增
            save: function (id) {
                if (id) {
                    location.href = this.manager + "/impexp/set/form.do?id=" + id;
                } else {
                    location.href = this.manager + "/impexp/set/form.do";
                }
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
