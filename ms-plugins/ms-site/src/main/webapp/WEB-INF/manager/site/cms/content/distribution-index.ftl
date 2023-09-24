<!DOCTYPE html>
<html>
<head>
    <title>文章分发</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">
    <el-header class="ms-header ms-tr" height="50px">

        <@shiro.hasPermission name="cms:content:distribution">
            <el-button type="primary" icon="el-icon-document-copy" size="mini" @click="contentForm.operationType='copy';saveRemoveOrCopy()" :loading="saveDisabled">批量复制</el-button>
        </@shiro.hasPermission>
    </el-header>
    <div class="ms-search">
        <el-row>
            <el-form :model="form"  ref="searchForm"  label-width="80px" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item label="所属站点" prop="appId">
                            <ms-tree-select :props="{value: 'id',label: 'appName',children: 'children'}"
                                         :options="appTreeData" :style="{width:'100%'}"
                                         v-model="form.appId"></ms-tree-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item label="所属栏目" prop="categoryId">
                            <ms-tree-select :props="{value: 'id',label: 'categoryTitle',children: 'children'}"
                                         :options="dataIdList" :style="{width:'100%'}"
                                         v-model="form.categoryId"></ms-tree-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item  label="文章标题" prop="contentTitle">
                            <el-input v-model="form.contentTitle"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      maxlength="50"
                                      placeholder="请输入文章标题">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item label="录入人" prop="managerName">
                            <el-input v-model="form.managerName"
                                      :disabled="false"
                                      :style="{width:  ''}"
                                      :clearable="true"
                                      maxlength="18"
                                      placeholder="请输入录入人">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6" style="padding-left: 10px;text-align: right;">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="form.sqlWhere=null;contentList()">查询</el-button>
                        <el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-main class="ms-container" >
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info">
            只能分发各站点审核通过的文章
        </el-alert>

        <div style="flex-wrap:nowrap;;box-sizing:border-box;flex-direction:row;display:flex;flex:1 ">
            <div style="flex-wrap:nowrap;height:100%;box-sizing:border-box;flex-direction:column;display:flex;width:48%; background-color: rgb(242, 246, 252);">
                    <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
                        <template slot="empty">
                            {{emptyText}}
                        </template>
                        <el-table-column type="selection" width="40"></el-table-column>
                        <el-table-column label="文章标题" align="left" prop="contentTitle" show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column label="所属栏目" align="left" prop="categoryId" :formatter="contentCategoryIdFormat" width="180">
                        </el-table-column>
                        <el-table-column label="录入人" align="left" prop="createBy" width="100">
                        </el-table-column>
                        <el-table-column label="发布时间" align="center" prop="contentDatetime" :formatter="dateFormat" width="120">
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
            </div>
            <div style="flex-wrap:nowrap;height:100%;align-items:center;justify-content:center;box-sizing:border-box;flex-direction:column;display:flex;width:80px">
                <i class="iconfont icon-jiantou-you" style="font-size: 40px; color: rgb(242, 246, 252)"></i>
            </div>
            <div style="flex-wrap:nowrap;height:100%;box-sizing:border-box;flex-direction:column;display:flex;width:48%; background-color: rgb(242, 246, 252);">
                    <el-scrollbar style="overflow-x: hidden">
                        <el-table ref="dataIdMultipleTable" :indent="6"
                                  border :data="dataIdList"
                                  row-key="id"
                                  height="calc(100vh - 200px)"
                                  v-loading="dataIdLoading"
                                  default-expand-all='true'
                                  :tree-props="{children: 'children'}"
                                  :select-on-indeterminate="true"
                                  tooltip-effect="dark"
                                  @select-all="selectAll"
                                  @selection-change="dataIdHandleSelectionChange">
                            <template slot="empty">
                                {{dataIdEmptyText}}
                            </template>
                            <el-table-column type="selection" :selectable="isChecked" width="40">

                            </el-table-column>
                            <el-table-column label="栏目名称" align="left" prop="categoryTitle">
                            </el-table-column>
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
            saveDisabled: false,
            manager: ms.manager,
            // 站点数据
            appList: [],
            appTreeData: [],
            //左侧选择框列表

            contentCategoryIdOptions: [],
            dataList: [],
            //文章列表
            selectionList: [],
            //文章列表选中
            total: 0,
            //总记录数量
            pageSize: 20,
            //页面数量
            currentPage: 1,
            loadState: false,
            loading: true,
            //加载状态
            emptyText: '',

            //搜索表单
            form: {
                sqlWhere: null,
                //  录入人
                createBy: null,
                // 文章标题
                contentTitle: null,
                categoryId: '',
                // 录入人名称
                managerName: '',
                // 站点id
                appId: null,
            },
            operationType:"",

            // 右侧数据
            dataIdList: [],
            //加载状态
            dataIdLoading: true,
            //提示文字
            dataIdEmptyText: '',
            //右侧列表选中
            dataIdSelectionList: [],

            // 右侧树形表格全选
            dataIdAllSelect:false,
            contentForm: {
                categoryIds:'',
                // 数据权限类型
                operationType:'remove',
                contentIds:"",
            },
        },
        watch: {
            'form.appId': function(n){
                if(n){
                    this.dataIdQuery()
                }
            }
        },
        methods: {
            //查询列表
            contentList: function () {
                var that = this;
                that.loading = true;
                that.loadState = false;
                var page = {
                    pageNo: that.currentPage,
                    pageSize: that.pageSize
                };
                var form = JSON.parse(JSON.stringify(that.form));
                for (var key in form) {
                    if (!form[key]) {
                        delete form[key];
                    }
                }
                form.progressStatus = "终审通过"
                ms.http.post(ms.manager + "/cms/content/list.do", form.sqlWhere ? Object.assign({}, {
                    sqlWhere: form.sqlWhere
                }, page) : Object.assign({}, form, page)).then(function (res) {
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true;
                    }

                    if (!res.result || res.data.length <= 0) {
                        that.emptyText = '当前选择站点没有栏目数据';
                        that.dataList = [];
                        that.total = 0;
                    } else {
                        that.emptyText = '';
                        that.total = res.data.total;
                        that.dataList = res.data.rows;
                    }
                }).catch(function (err) {
                    that.loading = false;
                    console.log(err);
                });
                setTimeout(function () {
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true;
                    }
                }, 500);
            },
            //文章列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //表格数据转换
            contentCategoryIdFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.contentCategoryIdOptions.find(function (value) {
                        return value.id == cellValue;
                    });

                    if (data && data.categoryTitle) {
                        value = data.categoryTitle;
                    }
                }

                return value;
            },
            dateFormat: function (row, column, cellValue, index) {
                if (cellValue) {
                    return ms.util.date.fmt(cellValue, 'yyyy-MM-dd');
                } else {
                    return '';
                }
            },
            //pageSize改变时会触发
            sizeChange: function (pagesize) {
                this.loading = true;
                this.pageSize = pagesize;
                this.contentList();
            },
            //currentPage改变时会触发
            currentChange: function (currentPage) {
                this.loading = true;
                this.currentPage = currentPage;
                this.contentList();
            },
            search: function (data) {
                this.form.sqlWhere = JSON.stringify(data);
                this.contentList();
            },
            //重置表单
            rest: function () {
                this.form.sqlWhere = null;
                this.form.categoryId = "";
                this.$refs.searchForm.resetFields();
                this.contentList();
            },
            // 获取站点信息
            listOnwerApp: function () {
                var that = this;
                ms.http.get(ms.manager + "/site/listOwnerApp.do").then(function (res) {
                    that.appList = res.data
                    that.appTreeData = ms.util.treeData(that.appList,'id','parentId','children')
                    if(that.appTreeData.length == 0) {
                        that.appTreeData = that.appList;
                    }
                    that.$nextTick(function(){
                        that.form.appId = that.appList[0].id
                    })
                }, function (err) {
                    that.$message.error(err);
                });
            },
            //查询列表
            dataIdQuery: function () {
                var that = this;
                this.dataIdLoading = true;
                ms.http.get(ms.manager + "/site/cms/category/getCategoryByAppId.do", {
                    appId: that.form.appId
                }).then(function (res) {
                    that.dataIdLoading = false;
                    if (!res.result || res.data.total <= 0) {
                        that.dataIdEmptyText = '暂无数据';
                        that.dataIdList = [];
                    } else {
                        that.dataIdEmptyText = '';
                        that.dataIdList = ms.util.treeData(res.data, 'id', 'categoryId', 'children');
                    }
                }).catch(function (err) {
                    console.log(err);
                    that.dataIdLoading = false;
                });
            },
            // 查询栏目
            getCategoryList: function () {
                var that = this;
                ms.http.get(ms.manager + "/cms/category/list.do", {
                    pageSize: 9999
                }).then(function (res) {
                    if (res.result) {
                        that.contentCategoryIdOptions = res.data.rows;
                    } else {
                        that.contentCategoryIdOptions = [];
                    }
                    that.contentList();
                })
            },
            //分类列表选中
            dataIdHandleSelectionChange: function (val) {
                if(val.length > 1){
                    this.$refs.dataIdMultipleTable.clearSelection()
                    this.$refs.dataIdMultipleTable.toggleRowSelection(val.pop())
                }else if(val.length == 1){
                    this.contentForm.categoryIds = val[0].id;
                }
            },
            // 全选/取消选操作
            selectAll: function(selection, first) {
                this.$refs.dataIdMultipleTable.clearSelection()
            },
            //栏目类型为链接并且栏目为子栏目的才可以选
            isChecked: function (row, index) {
                if (row.categoryType != '1' || !row.leaf) {
                    return false;
                } else {
                    return true;
                }
            },
            //保存
            saveRemoveOrCopy: function () {
                var that = this;
                that.saveDisabled = true;
                that.contentForm.contentIds = [];
                that.selectionList.forEach( function (item) {
                    that.contentForm.contentIds.push(item.id)
                })
                that.contentForm.contentIds = that.contentForm.contentIds.join(',');
                if(!that.contentForm.categoryIds || !that.contentForm.contentIds){
                    that.$notify({
                        title: '提示',
                        message: '请选择文章和栏目',
                        type: 'warning'
                    });
                    that.saveDisabled = false;
                    return;
                }
                that.$confirm(' 被分发的文章会丢失自定义模型,发布到需重新绑定, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/site/cms/content/distribute.do", {categoryId:that.contentForm.categoryIds,contentIds:that.contentForm.contentIds}).then(function (res) {
                        that.dataIdLoading = false;
                        if (res.result) {
                            that.$notify({
                                title: '成功',
                                message: '设置成功',
                                type: 'success'
                            });
                            that.contentList();
                            that.dataIdQuery();
                        } else {
                            that.$notify({
                                title: '失败',
                                message: res.msg,
                                type: 'warning'
                            });
                        }
                        that.saveDisabled = false;
                    }).catch(function (err) {
                        console.log(err);
                        that.dataIdLoading = false;
                    });
                });
                that.saveDisabled = false;
            }
        },
        created: function () {

            this.getCategoryList();
            this.listOnwerApp();
        }
    });
</script>
<style>
    .el-radio{
        margin: 10px
    }
    .el-radio.is-bordered+.el-radio.is-bordered{
        margin-right: 10px;
    }
    .el-scrollbar__wrap {
        margin-bottom:unset !important;
    }
</style>