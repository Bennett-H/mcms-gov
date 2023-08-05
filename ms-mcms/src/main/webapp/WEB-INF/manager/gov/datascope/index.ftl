<!DOCTYPE html>
<html>
<head>
    <title>会员权限分配</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">
    <el-header class="ms-header ms-tr" height="50px">
        <@shiro.hasPermission name="datascope:data:save">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveLoading">保存
        </el-button>
        </@shiro.hasPermission>
    </el-header>
    <el-main class="ms-container">
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info"
                description="此功能必须超级管理员才能使用，通过对会员等级的栏目授权，可以控制对应会员等级下会员在对应栏目下的文章权限, 注意：只针对栏目类型为'链接'的类型，其他不会显示">
        </el-alert>

        <div style="display:flex;flex-direction: row;flex:1" v-loading="dataIdLoading">
            <div style="   display: flex;position: relative;margin-right: 10px;">
                <el-scrollbar>
                    <el-aside width="260px" style="background-color: #f2f6fc;">
                        <el-table v-loading="loading" ref="targetIdMultipleTable" height="calc(100vh - 150px)"
                                  class="ms-table-pagination"
                                  border
                                  stripe
                                  :header-cell-class-name="cellClass"
                                  @select="targetIdSelectionRow"
                                  @selection-change="targetIdHandleSelectionChange"
                                  :data="targetIdList" tooltip-effect="dark">
                            <template slot="empty">
                                {{emptyText}}
                            </template>
                            <el-table-column type="selection" width="55"></el-table-column>
                            <el-table-column label="会员等级" align="left" prop="dictLabel" show-overflow-tooltip>
                            </el-table-column>
                        </el-table>
                    </el-aside>
                </el-scrollbar>
            </div>
            <div style="display: contents;flex-direction: column;flex: 1;">
                <el-scrollbar style="width: 100%; overflow-x: hidden">
                    <el-table ref="multipleTable" :indent="6"
                              border :data="dataIdList"
                              stripe
                              height="calc(100vh - 150px)"
                              :row-key="function(row){return row.id}"
                              default-expand-all='true'
                              :tree-props="{children: 'children'}"
                              :select-on-indeterminate="true"
                              tooltip-effect="dark"
                              @select="rowSelect"
                              @select-all="selectAll"
                    >
                        <template slot="empty">
                            {{dataIdEmptyText}}
                        </template>
                        <el-table-column type="selection" width="40" reserve-selection="true"
                                         class-name="isCheck"></el-table-column>
                        <el-table-column label="栏目名称" align="left" prop="categoryTitle">
                        </el-table-column>
<#--                        <el-table-column label="栏目下文章管理权限" align="left">-->
<#--                            <template slot-scope="scope" v-if="scope.row.leaf || scope.row.categoryType == 2">-->
<#--                                <el-checkbox v-for="(model,index) in scope.row.dataIdModelList"-->
<#--                                             v-if="model.modelTitle == '查看' || model.modelTitle == '修改'"-->
<#--                                             :label="model.modelTitle"-->
<#--                                             v-model="model.check"-->
<#--                                             :disabled="!scope.row.check"-->
<#--                                             :key="scope.row.categoryId+index"></el-checkbox>-->
<#--                            </template>-->
<#--                        </el-table-column>-->
                    </el-table>
                </el-scrollbar>
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
            saveLoading: false,
            manager: ms.manager,
            loading: true,
            emptyText: '',
            targetIdList: [],
            checked: 1,
            // 右侧数据
            dataIdList: [],
            //加载状态
            dataIdLoading: true,
            //提示文字
            dataIdEmptyText: '',
            //右侧列表选中
            dataIdSelectionList: [],
            // 右侧树形表格全选
            dataIdAllSelect: false,
            dataScopeForm: {
                dataTargetId: '',
                // 数据权限类型
                dataType: '会员投稿栏目权限',
            },
        },

        methods: {
            //取消角色列表全选
            cellClass:function(row) {
                if (row.columnIndex === 0) {
                    return 'disabledCheck'
                }
            },
            targetIdQuery: function () {
                var that = this;
                that.loading = true;
                ms.http.get(ms.manager + "/mdiy/dict/list.do", { 'dictType':'用户等级类型'}).then(function (data) {
                    if (data.result) {
                        that.loading = false;
                        that.targetIdList = data.data.rows;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },

            targetIdHandleSelectionChange: function (val) {

                if (val.length == 0) {
                    this.dataIdSelectionList = [];
                    this.dataScopeForm.dataTargetId = "";
                    this.$refs.multipleTable.clearSelection();
                } else if (val.length > 1) {
                    this.dataIdSelectionList = [];
                    this.$refs.targetIdMultipleTable.clearSelection()
                    this.$refs.targetIdMultipleTable.toggleRowSelection(val.pop())
                } else {
                    // 目标ID为字典值
                    this.dataScopeForm.dataTargetId = val[val.length - 1].dictValue;
                }
            },

            targetIdSelectionRow: function (selection, row) {
                var selected = selection.length && selection.indexOf(row) !== -1; //为true时选中，为 0 时（false）未选中
                if (selected) {
                    // 目标ID为字典值
                    this.dataScopeForm.dataTargetId = row.dictValue
                    this.getDataScopeData();
                } else {
                    this.dataScopeForm.dataTargetId = "";
                    this.setSelectAll(this.dataIdList,false)
                }
            },

            //获取选中权限数据
            getDataScopeData:function() {
                var that = this;
                if(!that.dataScopeForm.dataTargetId){
                    that.dataIdLoading = false;
                    that.loading = false;
                    that.dataIdEmptyText = '暂无数据';
                    that.dataIdList = [];
                    return
                }
                ms.http.get(ms.manager + "/cms/co/category/categoryList.do", this.dataScopeForm).then(function (data) {
                    if (data.result) {
                        that.dataIdLoading = false;
                        if (data.data.length > 0) {
                            that.dataIdEmptyText = '';
                            // that.dataIdList = data.data;
                            that.dataIdList = ms.util.treeData(data.data, 'id', 'categoryId', 'children');
                            that.$nextTick(function() {
                                that.toggleRowSelection(that.dataIdList);
                            });
                        } else {
                            that.dataIdEmptyText = '暂无数据';
                            that.dataIdList = [];
                        }
                    }
                }).catch(function (err) {
                    that.dataIdLoading = false;
                    console.log(err);
                });
            },
            toggleRowSelection: function (datas) {
                var that = this
                datas.forEach(function(item){
                    if (item.check) {
                        that.$refs.multipleTable.toggleRowSelection(item, true)
                    }
                    if (item.children) {
                        that.toggleRowSelection(item.children);
                    }
                });
            },
            //权限分配
            save: function () {
                var that = this;
                if(!that.dataScopeForm.dataTargetId){
                    that.$notify({
                        title: '提示',
                        message: '请选择角色',
                        type: 'warning'
                    });
                    return
                }
                that.saveLoading = true;
                this.dataIdSelectionList = this.$refs.multipleTable.selection;
                var _data = new Array();
                this.dataIdSelectionList.forEach(function(item) {
                    if (item.leaf ||  item.categoryType == "2") {
                        item.dataTargetId = that.dataScopeForm.dataTargetId;
                        item.dataType = that.dataScopeForm.dataType;
                        _data.push(item);
                    }

                })
                //如果length==0，代表清空权限
                if (_data.length == 0) {
                    _data.push({dataType: that.dataScopeForm.dataType, dataTargetId: that.dataScopeForm.dataTargetId})
                }
                ms.http.post(ms.manager + "/cms/co/category/saveBatch.do", _data, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(function (res) {
                    that.dataIdLoading = false;
                    if (res.result) {
                        that.$notify({
                            title: '成功',
                            message: '权限设置成功',
                            type: 'success'
                        });
                    } else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                    that.saveLoading = false;
                }).catch(function (err) {
                    that.dataIdLoading = false;
                    that.saveLoading = false;
                });
            },
            /*注意在获取初始数据时，所有节点（包括子节点）都增加一个isChecked 标志参数*/
            rowSelect:function(selection, row) {
                if (this.dataScopeForm.dataTargetId == "") {
                    this.$notify({
                        title: '提示',
                        message: '请选勾选会员等级',
                        type: 'warning'
                    });
                    this.$refs.multipleTable.clearSelection()
                    return;
                }
                let selected = selection.length && selection.indexOf(row) !== -1
                this.$refs.multipleTable.toggleRowSelection(row, selected); //行变选中状态

                if (row.children) { //只对有子节点的行响应
                    this.setSelectAll(row.children,selected)
                }

                //功能按钮选中
                if (!row.check) { //如果没有选中
                    row.dataIdModelList.map(function(item) {
                        item.check = true;
                    })
                    row.check = true;
                } else {
                    row.dataIdModelList.map(function(item){
                        item.check = false;
                    })
                    row.check = false;
                }
            },
            selectAll:function(selection) {
                if (this.dataScopeForm.dataTargetId == "") {
                    this.$notify({
                        title: '提示',
                        message: '请选勾选会员等级',
                        type: 'warning'
                    });
                    this.$refs.multipleTable.clearSelection()
                    return;
                }
                var that = this;
                let dom = document.querySelector(".isCheck>div>label");
                if (!dom.className.includes("is-checked")) {
                    // 全选
                    that.setSelectAll(that.$refs.multipleTable.data,true)
                } else {
                    // 取消全选
                    that.setSelectAll(that.$refs.multipleTable.data,false)
                }

            },

            //  设置全选状态的递归函数，arr为递归数组，bool为操作状态（布尔型）
            setSelectAll:function(arr,bool) {
                var that = this

                arr.forEach(function(item,index) {
                    item.check = bool
                    that.$refs.multipleTable.toggleRowSelection(item, bool); //行变选中状态
                    if(item.children && item.children.length) {
                        that.setSelectAll(item.children, bool)
                    }else {
                        item.dataIdModelList.map(function(model) {
                            model.check = bool;
                        })
                    }
                })
            }
        },
        created: function () {
            this.targetIdQuery();
            this.getDataScopeData();
        }
    });
</script>
<style>
    /* 去掉全选按钮 */
    .el-table .disabledCheck .cell .el-checkbox__inner {
        display: none !important;
    }

    .el-table .disabledCheck .cell::before {
        content: '选择';
        text-align: center;
        line-height: 37px;
    }

    .el-scrollbar .el-scrollbar__wrap {
        overflow-x: hidden;
    }
</style>
