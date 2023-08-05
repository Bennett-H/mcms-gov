<!DOCTYPE html>
<html>
<head>
    <title>权限审计</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">
    <div class="ms-search">
        <el-row>
            <el-form :model="form" ref="searchForm" label-width="80px" size="mini">
                <el-row>

                    <el-col :span="8">
                        <el-form-item label="管理员" prop="managerName">
                            <el-input v-model="form.managerName"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入管理员账号">
                            </el-input>
                        </el-form-item>
                    </el-col>


                    <el-col :span="16" style="padding-left: 10px;text-align: right;">
                        <el-button type="primary" icon="el-icon-search" size="mini"
                                   @click="form.sqlWhere=null;managerList()">查询
                        </el-button>
                        <el-button @click="rest" icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-main class="ms-container" style="flex-direction: row">

        <div style="flex-wrap:nowrap;height:100%;box-sizing:border-box;flex-direction:row;display:flex;width:100%; ">
            <div style="flex-wrap:nowrap;height:100%;box-sizing:border-box;flex-direction:column;display:flex;width:48%; background-color: rgb(242, 246, 252);">
                <el-main class="ms-container">
                    <el-table height="calc(100vh - 68px)" v-loading="loading"
                              ref="multipleTable"
                              highlight-current-row
                              border
                              tooltip-effect="dark"
                              :data="dataList"
                              tooltip-effect="dark"
                              @row-click="rowClick">
                        <template slot="empty">
                            <el-empty description="请先添加管理员"></el-empty>
                        </template>
                        <el-table-column label="管理员" align="left" prop="managerName" show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column label="所属角色" align="left" prop="roleNames"
                                          width="180">
                        </el-table-column>

                    </el-table>
                    <el-pagination
                            background
                            :page-sizes="[10,20,30,40,50,100]"
                            layout="total, sizes, prev, pager, next"
                            :current-page="currentPage"
                            :page-size="pageSize"
                            :total="total"
                            class="ms-pagination"
                            @current-change='currentChange'
                            @size-change="sizeChange">
                    </el-pagination>
                </el-main>
            </div>
            <div style="flex-wrap:nowrap;height:100%;align-items:center;justify-content:center;box-sizing:border-box;flex-direction:column;display:flex;width:80px">
                <i class="iconfont icon-jiantou-you" style="font-size: 40px; color: rgb(242, 246, 252)"></i>
            </div>
            <div style="flex-wrap:nowrap;height:100%;box-sizing:border-box;flex-direction:column;display:flex;width:48%; background-color: rgb(242, 246, 252);">
                <el-main class="ms-container">
                    <el-scrollbar style="overflow-x: hidden">
                        <el-table ref="dataIdMultipleTable" :indent="6"
                                  border :data="dataIdList"
                                  row-key="id"
                                  height="calc(100vh - 200px)"
                                  v-loading="roleModelListLoading"
                                  default-expand-all='true'
                                  :tree-props="{children: 'modelChildList'}"
                                  :select-on-indeterminate="true"
                                  tooltip-effect="dark">
                            <template slot="empty">
                                <el-empty description="点击左侧管理员查看对应权限"></el-empty>
                            </template>

                            </el-table-column>
                            <el-table-column label="拥有权限" align="left" prop="modelTitle">
                            </el-table-column>
                        </el-table>
                    </el-scrollbar>
                </el-main>
            </div>
        </div>
    </el-main>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: {
            manager: ms.manager,
            dataList: [],
            total: 0,
            //总记录数量
            pageSize: 20,
            //页面数量
            currentPage: 1,
            loading: true,
            dataIdList:[],
            roleModelListLoading:false,
            //搜索表单
            form: {
                sqlWhere: null,
                //  创建人
                createBy: null,
                roleNames: '',
                // 管理员
                managerName: '',
            },
        },
        methods: {
            rowClick: function (val) {
               // val 为选中对象
                this.roleModelList(val.id);
            },

            managerList: function () {
                var that = this;
                that.loading = true;
                var page = {
                    pageNo: that.currentPage,
                    pageSize: that.pageSize
                };

                var form = JSON.parse(JSON.stringify(that.form));

                ms.http.post(ms.manager + "/basic/manager/list.do", form.sqlWhere ? Object.assign({}, {
                    sqlWhere: form.sqlWhere
                }, page) : Object.assign({}, form, page)).then(function (res) {
                    if (!res.result || res.data.total <= 0) {
                        that.dataList = [];
                        that.total = 0;
                    } else {
                        that.total = res.data.total;
                        that.dataList = res.data.rows;
                    }
                    that.loading = false;
                }).catch(function (err) {
                    that.loading = false;
                    console.log(err);
                });

            },

            sizeChange: function (pagesize) {
                this.loading = true;
                this.pageSize = pagesize;
                this.managerList();
            },
            currentChange: function (currentPage) {
                this.loading = true;
                this.currentPage = currentPage;
                this.managerList();
            },
            search: function (data) {
                this.form.sqlWhere = JSON.stringify(data);
                this.managerList();
            },
            //重置表单
            rest: function () {
                this.form.sqlWhere = null;
                this.form.roleNames = "";
                this.$refs.searchForm.resetFields();
                this.managerList();
            },
            roleModelList: function (id) {
                var that = this;
                this.roleModelListLoading = true;
                ms.http.post(ms.manager + "/gov/auditor/managerModel.do", {
                    managerId: id
                }).then(function (res) {
                    that.roleModelListLoading = false;
                    if (!res.result || res.data.total <= 0) {
                        that.dataIdList = [];
                    } else {
                        that.dataIdList = ms.util.treeData(res.data.rows, 'id', 'modelId', 'modelChildList');
                    }
                }).catch(function (err) {
                    console.log(err);
                    that.roleModelListLoading = false;
                });
            },

        },
        created: function () {
            this.managerList();
        }
    });
</script>
<style>
    .el-radio {
        margin: 10px
    }

    .el-radio.is-bordered + .el-radio.is-bordered {
        margin-right: 10px;
    }

    .el-scrollbar__wrap {
        margin-bottom: unset !important;
    }

   .el-table--enable-row-transition .el-table__body td.el-table__cell {
        cursor: pointer;
    }
    .el-table__body tr.current-row>td.el-table__cell {
        background-color: #409EFF;
        color: #fff;
    }
    .el-table--enable-row-hover .el-table__body tr:hover>td.el-table__cell {
        background-color: #409EFF;
    }
</style>
