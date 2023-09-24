<!DOCTYPE html>
<html>
<head>
    <title>应用</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="site:app:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="site:app:del">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"
                           :disabled="!selectionList.length">删除
                </el-button>
            </@shiro.hasPermission>


        </el-col>

        <el-col :span="12">
            <div style="float: right;">
                <@shiro.hasPermission name="site:app:init">
                    <el-button type="success" icon="el-icon-document-copy" size="mini" @click="init()" >
                        初始化APPID
                    </el-button>
                </@shiro.hasPermission>
                <@shiro.hasPermission name="site:app:remove">
                    <el-button type="danger"  icon="el-icon-delete" size="mini" @click="remove()" >
                        移除APPID
                    </el-button>
                </@shiro.hasPermission>

            </div>
        </el-col>
    </el-header>
    <el-main class="ms-container">
        <el-table ref="multipleTable"
                  style="width:100%;"
                  height="calc(100vh - 20px)"
                  border :data="dataList"
                  row-key="id"
                  v-loading="loading"
                  default-expand-all='true'
                  :tree-props="{children: 'children'}"
                  tooltip-effect="dark"
                  @select="rowSelect"
                  @select-all="selectAll"
                  @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40" :selectable="isChecked" class-name="isCheck"></el-table-column>
            <el-table-column label="站点编号" align="left" prop="appId" width="100">
            </el-table-column>
            <el-table-column label="站点名称" align="left" prop="appName" width="300">
            </el-table-column>
            <el-table-column label="域 名" align="left" prop="appUrl">
            </el-table-column>

            <el-table-column label="状态" align="center" width="100">
                <template slot-scope="scope">
                    <div class="lamp">
                        <em :class="scope.row.appState=='0' ? 'yes' : 'no'"></em>
                        <span v-if="scope.row.appState == '0'">运行中</span>
                        <span v-else>已停止</span>
                    </div>
                </template>
            </el-table-column>

            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="site:app:update">
                        <el-link type="primary" :underline="false" @click="setManager(scope.row.id)">设置管理员</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="site:app:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="site:app:del">
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
                {action: 'and', field: 'app_name', el: 'eq', model: 'appName', name: '站点名称', type: 'input'},
                {
                    action: 'and',
                    field: 'app_state',
                    el: 'eq',
                    model: 'appState',
                    name: '网站状态',
                    key: 'value',
                    title: 'label',
                    type: 'radio',
                    multiple: false
                },
                {
                    action: 'and',
                    field: 'app_mobile_state',
                    el: 'eq',
                    model: 'appMobileState',
                    name: '移动站状态',
                    key: 'value',
                    title: 'label',
                    type: 'radio',
                    multiple: false
                },
                {
                    action: 'and',
                    field: 'app_style',
                    el: 'eq',
                    model: 'appStyle',
                    name: '站点风格',
                    key: 'value',
                    title: 'label',
                    type: 'select',
                    multiple: false
                },
                {action: 'and', field: 'app_pay_date', model: 'appPayDate', el: 'gt', name: '每年续费日期', type: 'date'},
                {action: 'and', field: 'app_url', el: 'eq', model: 'appUrl', name: '域名', type: 'textarea'},
                {action: 'and', field: 'app_keyword', el: 'eq', model: 'appKeyword', name: '关键字', type: 'textarea'},
                {
                    action: 'and',
                    field: 'app_description',
                    el: 'eq',
                    model: 'appDescription',
                    name: '描述',
                    type: 'textarea'
                },
                {
                    action: 'and',
                    field: 'app_copyright',
                    el: 'eq',
                    model: 'appCopyright',
                    name: '版权信息',
                    type: 'textarea'
                },
                {action: 'and', field: 'app_pay', el: 'eq', model: 'appPay', name: '续费清单', type: 'textarea'},
                {action: 'and', field: 'create_date', el: 'eq', model: 'createDate', name: '创建时间', type: 'date'},
                {action: 'and', field: 'update_date', el: 'eq', model: 'updateDate', name: '修改时间', type: 'date'},
            ],
            conditions: [],
            dataList: [], //应用列表
            selectionList: [],//应用列表选中
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage: 1, //初始页
            manager: ms.manager,
            loadState: false,
            loading: true,//加载状态
            emptyText: '',//提示文字
            appStateOptions: [{"value": "0", "label": "运行"}, {"value": "1", "label": "停止"}],
            appMobileStateOptions: [{"value": "0", "label": "启用"}, {"value": "1", "label": "禁用"}],
            appStyleOptions: [],
            sourceList: [],
            treeList: [{
                id: '0',
                appName: '顶级分类',
                children: []
            }
            ],
            //搜索表单
            form: {
                sqlWhere: null,
                appName: null,
            },
        },
        watch: {},
        methods: {
            list: function () {
                var that = this;
                this.loadState = false;
                this.loading = true;
                ms.http.get(ms.manager + "/site/list.do", {pageSize: 9999}).then(
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
                            //树形关联
                            that.dataList = ms.util.treeData(res.data.rows, 'id', 'parentId', 'children');
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
            //站点管理列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //上级站点
            getTree: function () {
                var that = this;
                ms.http.get(ms.manager + "/site/list.do", {pageSize: 9999}).then(function (res) {
                    if (res.result) {
                        that.sourceList = res.data.rows;
                        that.treeList[0].children = ms.util.treeData(res.data.rows, 'id', 'parentId', 'children');
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //重置表单
            rest: function () {
                this.$refs.searchForm.resetFields();
            },

            //注意在获取初始数据时，所有节点（包括子节点）都增加一个isChecked 标志参数
            rowSelect: function (selection, row) {
                var that = this
                let selected = selection.length && selection.indexOf(row) !== -1
                if (row.children) { //只对有子节点的行响应
                    that.setSelectAll(row.children, selected)
                }
            },
            //全选
            selectAll: function (selection) {
                var that = this;
                let dom = document.querySelector(".isCheck>div>label");
                if (!dom.className.includes("is-checked")) {
                    // 全选
                    that.setSelectAll(that.$refs.multipleTable.data, true)
                } else {
                    // 取消全选
                    that.setSelectAll(that.$refs.multipleTable.data, false)
                }
            },
            //  设置全选状态的递归函数，arr为递归数组，bool为操作状态（布尔型）
            setSelectAll: function (arr, bool) {
                var that = this
                arr.forEach(function (item, index) {
                    item.check = bool
                    that.$refs.multipleTable.toggleRowSelection(item, bool); //行变选中状态
                    if (item.children && item.children.length) {
                        that.setSelectAll(item.children, bool)
                    }
                })
            },
            //设置禁止选中的条件： 若返回为 true， 则可以选中，否则禁止选中。
            isChecked: function (row) {
                return row.notDel == 0
            },
            //查询列表
            // list: function () {
            //     var that = this;
            //     that.loading = true;
            //     that.loadState = false;
            //     var page = {
            //         pageNo: that.currentPage,
            //         pageSize: that.pageSize
            //     }
            //     var form = JSON.parse(JSON.stringify(that.form))
            //     for (key in form) {
            //         if (!form[key]) {
            //             delete form[key]
            //         }
            //     }
            //     history.replaceState({form: form, page: page}, "");
            //     ms.http.post(ms.manager + "/site/list.do",
            //         form.sqlWhere ? Object.assign({}, {sqlWhere: form.sqlWhere}, page)
            //         : Object.assign({}, form, page)).then(
            //         function (res) {
            //             if (that.loadState) {
            //                 that.loading = false;
            //             } else {
            //                 that.loadState = true
            //             }
            //             if (!res.result || res.data.total <= 0) {
            //                 that.emptyText = '暂无数据'
            //                 that.dataList = [];
            //                 that.total = 0;
            //             } else {
            //                 that.emptyText = '';
            //                 that.total = res.data.total;
            //                 that.dataList = res.data.rows;
            //             }
            //         }).catch(function (err) {
            //         console.log(err);
            //     });
            //     setTimeout(function () {
            //         if (that.loadState) {
            //             that.loading = false;
            //         } else {
            //             that.loadState = true
            //         }
            //     }, 500);
            // },
            //应用列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //删除
            // del: function (row) {
            //     var that = this;
            //     that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
            //         confirmButtonText: '确定',
            //         cancelButtonText: '取消',
            //         type: 'warning'
            //     }).then(function () {
            //         ms.http.post(ms.manager + "/site/delete.do", row.length ? row : [row], {
            //             headers: {
            //                 'Content-Type': 'application/json'
            //             }
            //         }).then(
            //             function (res) {
            //                 if (res.result) {
            //                     that.$notify({
            //                         type: 'success',
            //                         message: '删除成功!'
            //                     });
            //                     //删除成功，刷新列表
            //                     that.list();
            //                 } else {
            //                     that.$notify({
            //                         title: '失败',
            //                         message: res.msg,
            //                         type: 'warning'
            //                     });
            //                 }
            //             });
            //     })
            // },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/site/delete.do", row.length ? row : [row], {
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
                });
            },
            //新增
            save: function (id) {
                if (id) {
                    location.href = this.manager + "/site/form.do?id=" + id;
                } else {
                    location.href = this.manager + "/site/form.do";
                }
            },
            //新增
            setManager: function (id) {
                if (id) {
                    location.href = this.manager + "/site/role/form.do?appId=" + id;
                }
            },

            //表格数据转换
            appStateFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.appStateOptions.find(function (value) {
                        return value.value == cellValue;
                    })
                    if (data && data.label) {
                        value = data.label;
                    }
                }
                return value;
            },
            appMobileStateFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.appMobileStateOptions.find(function (value) {
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
            init: function () {
                var that = this;
                that.$confirm('此操作将根据站群配置下涉及表来插入APPID字段, 是否继续? 初始化成功后需要手动更新表的app_id值', '警告', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    this.loadState = false;
                    this.loading = true;
                    var url = ms.manager + "/site/init.do"
                    ms.http.post(url).then(function (res) {
                        if (that.loadState) {
                            that.loading = false;
                        } else {
                            that.loadState = true
                        }
                        if (res.result) {
                            that.$notify({
                                title: "成功",
                                message: "插入字段成功",
                                type: 'success'
                            });
                        } else {
                            that.$notify({
                                title: "错误",
                                message: res.msg,
                                type: 'warning'
                            });
                        }
                    });
                    setTimeout(function () {
                        if (that.loadState) {
                            that.loading = false;
                        } else {
                            that.loadState = true
                        }
                    }, 500);

                }).catch(function () {
                    that.$notify({
                        type: 'info',
                        message: '已取消删除'
                    });
                });

            },

            remove: function () {
                var that = this;
                that.$confirm('此操作将删除站群配置下涉及表的APPID字段,且不可恢复, 是否继续?', '警告', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    this.loadState = false;
                    this.loading = true;
                    var url = ms.manager + "/site/remove.do"
                    ms.http.post(url).then(function (res) {
                        if (that.loadState) {
                            that.loading = false;
                        } else {
                            that.loadState = true
                        }
                        if (res.result) {
                            that.$notify({
                                title: "成功",
                                message: "删除数据库字段成功",
                                type: 'success'
                            });
                        } else {
                            that.$notify({
                                title: "错误",
                                message: res.msg,
                                type: 'warning'
                            });
                        }
                    });
                    setTimeout(function () {
                        if (that.loadState) {
                            that.loading = false;
                        } else {
                            that.loadState = true
                        }
                    }, 500);

                })
            }
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
        height: calc(100vh - 141px);
    }

    .lamp {
        align-items: center;
    }

    .lamp .yes {
        background: #009900 !important;
    }

    .lamp .no {
        background: #F04134 !important;
    }

    .lamp em {
        display: inline-block;
        background: #0099ff;
        margin: 0 4px 1px 0;
        width: 6px;
        height: 6px;
        -webkit-border-radius: 50%;
        -moz-border-radius: 50%;
        -ms-border-radius: 50%;
        -o-border-radius: 50%;
        border-radius: 50%;
        text-align: center;
        line-height: 6px;
    }
</style>
