<!DOCTYPE html>
<html>
<head>
    <title>任务实体表</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="quartz:job:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="quartz:job:del">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"
                           :disabled="!selectionList.length">删除
                </el-button>
            </@shiro.hasPermission>
        </el-col>
    </el-header>
    <div class="ms-search">
        <el-row>
            <el-form :model="form" ref="searchForm" label-width="120px" size="mini">
                <el-row>
                    <el-col :span="8">
                        <el-form-item label="任务名称" prop="qjName">
                            <el-input v-model="form.qjName"
                                      :disabled="false"

                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入任务名称">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16" style="text-align: right;padding-right: 10px;">
                        <el-button type="primary" icon="el-icon-search" size="mini"
                                   @click="loading=true;currentPage=1;list()">查询
                        </el-button>
                        <el-button type="primary" icon="iconfont icon-shaixuan1" size="mini" @click="$refs.search.open()">筛选</el-button>
                        <el-button @click="rest" icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-main class="ms-container">
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info"
                description="低代码实现一些定时执行的业务，如：定时采集、定时静态化、定时发短信">
            <template slot="title">
                功能介绍 <a href='http://doc.mingsoft.net/plugs/ren-wu-diao-du/jie-shao.html' target="_blank">开发手册</a>
            </template>
        </el-alert>
        <el-table v-loading="loading"  height="calc(100vh - 68px)" ref="multipleTable" class="ms-table-pagination" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column label="任务名称" width="150" align="left" prop="qjName" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="任务组" width="100" align="left" prop="qjGroup" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="调用目标" align="left" prop="qjTarget" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="执行表达式" width="150" align="left" prop="qjCron" show-overflow-tooltip>
            </el-table-column>
            <@shiro.hasPermission name="quartz:job:update">
                <el-table-column label="开启" width="80" align="left" prop="qjStatus" show-overflow-tooltip>
                    <template slot-scope="scope">
                        <el-switch v-model="scope.row.qjStatus" @change="update(scope.$index)">
                        </el-switch>
                    </template>
                </el-table-column>
            </@shiro.hasPermission>

            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="quartz:job:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="quartz:job:del">
                        <el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="quartz:job:update">
                        <el-link type="primary" :underline="false" @click="runNow(scope.row)">立即执行</el-link>
                    </@shiro.hasPermission>

                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                background
                :page-sizes="[5, 10, 20]"
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
        data:function(){
            return{
                conditionList:[
                    {action:'and', field: 'qj_name', el: 'eq', model: 'qjName', name: '任务名称', type: 'input'},
                    {action:'and', field: 'qj_group', el: 'eq', model: 'qjGroup', name: '任务组', type: 'input'},
                    {action:'and', field: 'qj_status', el: 'eq', model: 'qjStatus', name: '状态', type: 'switch'},
                    {action:'and', field: 'qj_async', el: 'eq', model: 'qjAsync', name: '开启并发', type: 'switch'},
                    {action:'and', field: 'qj_target', el: 'eq', model: 'qjTarget', name: '调用目标', type: 'input'},
                    {action:'and', field: 'qj_cron', el: 'eq', model: 'qjCron', name: '执行表达式', type: 'input'},
                    {action:'and', field: 'create_date', el: 'gt', model: 'createDate', name: '创建时间', type: 'date'},
                    {action:'and', field: 'update_date', el: 'gt', model: 'updateDate', name: '修改时间', type: 'date'},
                ],
                conditions:[],
                dataList: [], //任务实体表列表
                selectionList: [],//任务实体表列表选中
                total: 0, //总记录数量
                pageSize: 10, //页面数量
                currentPage: 1, //初始页
                mananger: ms.manager,
                loading: true,//加载状态
                emptyText: '',//提示文字
                //搜索表单
                form: {
                    // 任务名称
                    qjName: null,
                },
                qjStatus: true,
                qjAsync: true,
            }
        },
        methods: {
            //查询列表
            list: function () {
                var that = this;
                that.loading = true;
                var page={
                    pageNo: that.currentPage,
                    pageSize : that.pageSize
                }
                var form = JSON.parse(JSON.stringify(that.form))
                for (var key in form){
                    if(!form[key]){
                        delete  form[key]
                    }
                }
                history.replaceState({form:form,page:page,total:that.total},"");
                setTimeout(function (){
                    ms.http.post(ms.manager+"/quartz/job/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
                        :Object.assign({},form, page)).then(
                        function (res) {
                            that.loading = false;
                            if (!res.result || res.data.total <= 0) {
                                that.emptyText = '暂无数据'
                                that.dataList = [];
                            } else {
                                that.emptyText = '';
                                that.loading = false;
                                that.total = res.data.total;
                                that.dataList = res.data.rows;
                            }
                        }).catch(function (err) {
                        console.log(err);
                    });
                }, 500);
            },
            //任务实体表列表选中
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
                }).then(function() {
                    ms.http.post(ms.manager + "/quartz/job/delete.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: '成功',
                                    type: 'success',
                                    message: '删除成功!'
                                });
                                //删除成功，刷新列表
                                that.list();
                            }else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        });
                }).catch(function () {
                    that.$notify({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            //立即执行
            runNow: function(row) {
                var that = this;
                that.$notify({
                    type: 'info',
                    title: '任务执行中',
                    message: '请到后台查看进度'
                });
                ms.http.get(ms.manager + "/quartz/job/runNow.do", row).then(
                    function (res) {
                        if (!res.result) {
                            that.$notify({
                                title: '失败',
                                message: res.msg,
                                type: 'warning'
                            });
                        }
                    });
            },
            //新增
            save: function (id) {
                if (id) {
                    location.href = this.mananger + "/quartz/job/form.do?id=" + id;
                } else {
                    location.href = this.mananger + "/quartz/job/form.do";
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
            search: function(data){
                this.form.sqlWhere = JSON.stringify(data);
                this.list();
            },
            //重置表单
            rest: function() {
                this.currentPage = 1;
                this.loading = true;
                this.form.sqlWhere = null;
                this.$refs.searchForm.resetFields();
                this.list();
            },
            //更新状态
            update: function (index) {
                var data = this.dataList[index];
                var that = this;
                ms.http.post(ms.manager + "/quartz/job/update.do", data).then(function (data) {
                    if (data.result) {
                        that.$notify({
                            title: '成功',
                            message: '保存成功',
                            type: 'success'
                        });
                    } else {
                        that.$notify({
                            title: '失败',
                            message: data.msg,
                            type: 'warning'
                        });
                    }
                });
            }
        },
        created: function() {
            if(history.state){
                this.form = history.state.form;
                this.total = history.state.total;
                this.currentPage = history.state.page.pageNo;
                this.pageSize = history.state.page.pageSize;
            }
            this.list();
        },
    })
</script>
<style>
    #index .ms-search {
        padding: 20px 0 0;
    }
    .el-notification {
        white-space:pre !important;
    }
</style>

