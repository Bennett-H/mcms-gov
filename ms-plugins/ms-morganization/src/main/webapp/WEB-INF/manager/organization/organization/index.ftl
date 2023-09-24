<!DOCTYPE html>
<html>
<head>
    <title>组织机构</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="organization:organization:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="organization:organization:del">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"
                           :disabled="!selectionList.length">删除
                </el-button>
            </@shiro.hasPermission>
        </el-col>
    </el-header>
    <el-main class="ms-container">
        <el-table ref="multipleTable"
                  height="calc(100vh - 20px)"
                  border :data="dataList"
                  row-key="id"
                  v-loading="loading"
                  default-expand-all='true'
                  :tree-props="{children: 'children'}"
                  tooltip-effect="dark"
                  @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column label="部门编号" align="left" prop="organizationCode" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="部门名称" align="left" prop="organizationTitle" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="部门状态" align="center" prop="organizationStatus" :formatter="organizationStatusFormat">
            </el-table-column>
            <#--            <el-table-column label="分管领导" align="left" prop="organizationLeaders" :formatter="organizationLeadersFormat">-->
            <#--            </el-table-column>-->
            <el-table-column label="负责人" align="left" prop="organizationLeader" :formatter="organizationLeaderFormat" show-overflow-tooltip>
            </el-table-column>

            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="organization:organization:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="organization:organization:del">
                        <el-link type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
                    </@shiro.hasPermission>
                </template>
            </el-table-column>
        </el-table>
    </el-main>
</div>
<#include "/organization/organization/form.ftl">
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: {
            dataList: [], //组织机构列表
            sourceList: [],
            selectionList: [],//组织机构列表选中
            loading: true,//加载状态
            emptyText: '',//提示文字
            manager: ms.manager,
            loadState: false,
            organizationStatusOptions: [{"value": "normal", "label": "正常"}, {"value": "pause", "label": "停用"}],
            organizationLeadersOptions: [],
            organizationLeaderOptions: [],
            //搜索表单
            form: {},
        },
        methods: {
            //查询列表
            list: function () {
                var that = this;
                this.loadState = false;
                this.loading = true;
                ms.http.get(ms.manager + "/organization/organization/list.do", {pageSize: 9999}).then(
                    function (res) {
                        if (that.loadState) {
                            that.loading = false;
                        } else {
                            that.loadState = true
                        }
                        if (!res.result || res.data.total <= 0) {
                            that.emptyText = '暂无数据'
                            that.dataList = [];
                        } else {
                            that.emptyText = '';
                            that.dataList = ms.util.treeData(res.data.rows, 'id', 'organizationId', 'children');
                        }
                    }).catch(function (err) {
                    console.log(err);
                });
                setTimeout(function()  {
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true
                    }
                }, 500);
            },
            //组织机构列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            handleClose: function (done) {
                this.$refs.form.resetFields();
                done();
            },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function()  {
                    ms.http.post(ms.manager + "/organization/organization/delete.do", row.length ? row : [row], {
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
                                    message: data.msg,
                                    type: 'warning'
                                });
                            }
                        });
                }).catch(function()  {
                    that.$notify({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            //表格数据转换
            organizationStatusFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.organizationStatusOptions.find(function (value) {
                        return value.value == cellValue;
                    })
                    if (data && data.label) {
                        value = data.label;
                    }
                }
                return value;
            },
            organizationLeadersFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.organizationLeadersOptions.find(function (value) {
                        return value.id == cellValue;
                    })
                    if (data && data.managerNickName) {
                        value = data.managerNickName;
                    }
                }
                return value;
            },
            organizationLeaderFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.organizationLeaderOptions.find(function (value) {
                        return value.id == cellValue;
                    })
                    if (data && data.employeeNickName) {
                        value = data.employeeNickName;
                    }
                }
                return value;
            },
            //新增
            save: function (id) {
                form.open(id);
            },
            //重置表单
            rest: function () {
                this.$refs.searchForm.resetFields();
            },
            //获取organizationLeader数据源
            organizationLeaderOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/organization/employee/queryAll.do", {pageSize:99999}).then(function (res) {
                    that.organizationLeaderOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created: function () {
            this.organizationLeaderOptionsGet();
            this.list();
        },
    })
</script>
