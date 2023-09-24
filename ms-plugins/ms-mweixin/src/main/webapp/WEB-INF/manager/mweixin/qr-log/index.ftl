<!DOCTYPE html>
<html>
<head>
    <title>场景二维码日志</title>
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
                        <!--场景二维码编号-->
                        <el-form-item label="场景二维码名称" prop="qcSceneStr">
                            <el-input
                                    v-model="form.qcSceneStr"
                                    :style="{width:  '100%'}"
                                    :clearable="true"
                                    placeholder="请输入场景二维码名称">
                            </el-input>
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
            <el-table-column label="会员编号" align="left" prop="peopleId" width="120">
            </el-table-column>
            <el-table-column label="会员openId" align="left" prop="openId">
            </el-table-column>
            <el-table-column label="场景二维码名称" align="left" prop="qcSceneStr" width="150">
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createDate" width="180">
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="mweixin:qrLog:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">查看</el-link>
                    </@shiro.hasPermission>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                background
                :page-sizes="[50,100,200]"
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
                //会员openId
                {
                    'action': 'and',
                    'field': 'OPEN_ID',
                    'el': 'eq',
                    'model': 'openId',
                    'name': '会员openId',
                    'type': 'input'
                },
                //微信编号
                {
                    'action': 'and',
                    'field': 'WEIXIN_ID',
                    'el': 'eq',
                    'model': 'weixinId',
                    'name': '微信编号',
                    'type': 'input'
                },
                //场景二维码编号
                {'action': 'and', 'field': 'QC_ID', 'el': 'eq', 'model': 'qcId', 'name': '场景二维码编号', 'type': 'input'},
            ],
            conditions: [],
            dataList: [], //场景二维码日志列表
            selectionList: [],//场景二维码日志列表选中
            total: 0, //总记录数量
            pageSize: 50, //页面数量
            currentPage: 1, //初始页
            manager: ms.manager,
            loading: true,//加载状态
            emptyText: '',//提示文字
            //搜索表单
            form: {
                peopleId:'',
                qcSceneStr:'',
                sqlWhere: null
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
                ms.http.post(ms.manager + "/mweixin/qrLog/list.do", data).then(
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
            //场景二维码日志列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm("此操作将永久删除所选内容, 是否继续", "提示", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/mweixin/qrLog/delete.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: '成功',
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
                }).catch(function (err) {
                    //删除如果用户取消会抛出异常，所以需要catch一下
                    console.log(err)
                });
            },
            //新增
            save: function (id) {
                if (id) {
                    location.href = this.manager + "/mweixin/qrLog/form.do?id=" + id;
                } else {
                    location.href = this.manager + "/mweixin/qrLog/form.do";
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

        },
        created: function () {
            var that = this;

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
