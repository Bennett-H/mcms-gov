<!DOCTYPE html>
<html>
<head>
    <title>在线管理员</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <!--div class="ms-search">
        <el-row>
            <el-form :model="form" ref="searchForm" label-width="120px" size="mini">
                <el-row>
                    <el-col :span="8">

                        <el-form-item label="帐号" prop="managerName">
                            <el-input
                                    v-model="form.managerName"
                                    :disabled="false"
                                    :readonly="false"
                                    :style="{width:  '100%'}"
                                    :clearable="true"
                                    placeholder="请输入帐号">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16" style="text-align: right;">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list()">搜索
                        </el-button>
                        <el-button @click="rest" icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div-->
    <el-main class="ms-container">
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info">
           如果后台配置允许重复登录,则只记录最后一次登录的数据;若登录地点有误,可能是当前ip还没被收录
        </el-alert>

        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList"
                  tooltip-effect="dark">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column label="帐号" align="left" prop="managerName">
            </el-table-column>

            <el-table-column label="登录失败次数" align="center" prop="loginFailCount">
            </el-table-column>
            <el-table-column label="登录ip" min-width="150" align="center" prop="loginIpAddress">
            </el-table-column>
            <el-table-column label="登录地点" min-width="150" align="left" prop="loginAddress">
            </el-table-column>
            <el-table-column label="最后登录时间" min-width="200" align="center" prop="lastLoginTime">
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="co:onlineManger:kick">
                        <el-link type="primary" :underline="false" @click="kick(scope.row.managerName)">强制下线</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="co:onlineManger:lock">
                        <el-link v-if="scope.row.onOfLockStatus==='unlock'" type="primary" :underline="false" @click="lock(scope.row.managerName)">锁定</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="co:onlineManger:unlock">
                        <el-link  v-if="scope.row.onOfLockStatus==='lock'" type="primary" :underline="false" @click="unlock(scope.row.managerName)">解锁</el-link>
                    </@shiro.hasPermission>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                background
                :page-sizes="[30,50,100]"
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
            dataList: [], //在线管理员列表
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage: 1, //初始页
            manager: ms.manager,
            loadState: false,
            loading: true,//加载状态
            emptyText: '',//提示文字
            //搜索表单
            form: {},
        },
        watch: {},
        methods: {
            //查询列表
            list: function () {
                var that = this;
                that.loading = true;
                that.loadState = false;
                var page = {
                    pageNumber: that.currentPage,
                    pageSize: that.pageSize
                }
                var form = JSON.parse(JSON.stringify(that.form))
                for (key in form) {
                    if (!form[key]) {
                        delete form[key]
                    }
                }
                history.replaceState({form: form, page: page}, "");
                ms.http.post(ms.manager + "/basic/co/onlineManager/list.do", form.sqlWhere ? Object.assign({}, {sqlWhere: form.sqlWhere}, page)
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
                    console.log(err);
                });
                if (that.loadState) {
                    that.loading = false;
                } else {
                    that.loadState = true
                }
            },
            //强制下线
            kick: function (manageName) {
                var that = this;
                that.$confirm("此操作将该用户强制下线, 是否继续", "提示", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/basic/co/onlineManager/kick.do", {manageName:manageName}).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: "成功",
                                    type: 'success',
                                    message: "操作成功"
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
            //强制锁定账号
            lock: function (manageName) {
                var that = this;
                that.$confirm("此操作将该用户强制锁定, 是否继续", "提示", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/basic/co/onlineManager/lock.do", {manageName:manageName}).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: "成功",
                                    type: 'success',
                                    message: "操作成功"
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
            //强制锁定账号
            unlock: function (manageName) {
                var that = this;
                that.$confirm("此操作将该用户强制解锁, 是否继续", "提示", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/basic/co/onlineManager/unlock.do", {manageName:manageName}).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: "成功",
                                    type: 'success',
                                    message: "操作成功"
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
                this.currentPage = history.state.page.pageNumber;
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
