<!DOCTYPE html>
<html>
<head>
    <title>任务调度日志</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="quartz:jobLog:del">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
            </@shiro.hasPermission>
        </el-col>
    </el-header>
    <div class="ms-search">
        <el-row>
            <el-form :model="form" ref="searchForm" label-width="120px" size="mini">
                <el-row>
                    <el-col :span="8">
                        <el-form-item label="任务名称" prop="qjlName">
                            <el-input v-model="form.qjlName"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入内容">
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
        <el-table v-loading="loading"  height="calc(100vh - 68px)" ref="multipleTable" class="ms-table-pagination" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column label="任务名称" width="150" align="left" prop="qjlName" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="调用目标" align="left" prop="qjlTarget" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="任务组"  align="left" prop="qjlGroup" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="日志信息" align="left" prop="qjlMsg" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="创建时间" width="200" align="center" prop="createDate">
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="quartz:jobLog:view">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">查看</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="quartz:jobLog:del">
                        <el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
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
        data:function() {
            return{
                conditionList:[
                    {action:'and', field: 'qjl_name', el: 'eq', model: 'qjlName', name: '任务名称', type: 'input'},
                    {action:'and', field: 'qjl_group', el: 'eq', model: 'qjlGroup', name: '任务组', type: 'input'},
                    {action:'and', field: 'qjl_target', el: 'eq', model: 'qjlTarget', name: '调用目标', type: 'input'},
                    {action:'and', field: 'qjl_status', el: 'eq', model: 'qjlStatus', name: '执行状态', type: 'switch'},
                    {action:'and', field: 'qjl_msg', el: 'eq', model: 'qjlMsg', name: '日志信息', type: 'textarea'},
                    {action:'and', field: 'qjl_error_msg', el: 'eq', model: 'qjlErrorMsg', name: '错误信息', type: 'textarea'},
                    {action:'and', field: 'create_date', el: 'gt', model: 'createDate', name: '创建时间', type: 'date'},
                    {action:'and', field: 'update_date', el: 'gt', model: 'updateDate', name: '修改时间', type: 'date'},
                ],
                conditions:[],
                dataList: [], //任务调度日志列表
                selectionList: [],//任务调度日志列表选中
                total: 0, //总记录数量
                pageSize: 10, //页面数量
                currentPage: 1, //初始页
                loading: true,//加载状态
                emptyText: '',//提示文字
                //搜索表单
                form: {
                    // 任务名称
                    qjlName: null,
                },
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
                setTimeout(function () {
                    ms.http.post(ms.manager+"/quartz/jobLog/list.do",form.sqlWhere?Object.assign({},{sqlWhere:form.sqlWhere}, page)
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
            //任务调度日志列表选中
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
                    ms.http.post(ms.manager + "/quartz/jobLog/delete.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: '提示',
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
                        title: '提示',
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            //新增
            save: function (id) {
                if (id) {
                    location.href = ms.manager + "/quartz/jobLog/form.do?id=" + id;
                } else {
                    location.href = ms.manager + "/quartz/jobLog/form.do";
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
            search:function(data){
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
</style>
