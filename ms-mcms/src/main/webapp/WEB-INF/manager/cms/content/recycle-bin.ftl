<#--列表新增权限处理，文章状态处理-->
<#--批量导入-->
<!DOCTYPE html>
<html>
<head>
    <title>回收站</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/datascope/index.js"></script>
</head>
<body>
<div id="main" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">

        <el-col :span="7" style="display: flex;    width: 426px;">
            <@shiro.hasPermission name="cms:content:reduction">
                <el-button style="margin-right: 8px" type="success" icon="iconfont icon-oper-auto" :loading="saveDisabled" size="mini" @click="reduction(selectionList)" :disabled="!selectionList.length">还原</el-button>
            </@shiro.hasPermission>

            <@shiro.hasPermission name="cms:content:cleanRecycleBin">
                <el-button style="margin-right: 8px" type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">彻底删除</el-button>
            </@shiro.hasPermission>
        </el-col>
    </el-header>
    <div class="ms-search">
        <el-row>
            <el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
                <el-row>
                    <el-col :span="8">
                        <el-form-item  label="文章标题" prop="contentTitle">
                            <el-input v-model="form.contentTitle"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入文章标题">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item  label="发布范围" prop="contentStyle">
                            <el-select v-model="form.contentStyle"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="false"
                                       :multiple="false" :clearable="true"
                                       placeholder="请选择内外网">
                                <el-option v-for='item in contentStyleOptions' :key="item.dictValue"
                                           :value="item.dictValue"
                                           :label="item.dictLabel"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8" style="text-align: right;padding-right: 10px;">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="form.sqlWhere=null;currentPage=1;list()">查询</el-button>
                        <el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
                        <el-button type="primary" size="mini" @click="$refs.search.open()"><i class="iconfont icon-shaixuan"></i>筛选</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-main class="ms-container">
        <el-alert
                class="ms-alert-tip"
                title="注意事项"
                type="info"
                description="注意！若回收站内文章栏目名为空，则表示该文章所属栏目已被删除，不可被还原，建议彻底删除，在还原时会与其他不可被还原文章会被过滤，不影响正常文章的还原操作。">
        </el-alert>
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column label="编号" width="100" prop="id" show-overflow-tooltip>
                <template slot='header'>编号
                    <el-popover placement="top-start" title="提示" trigger="hover" >
                        <a href="http://doc.mingsoft.net/plugs-cms/biao-qian/wen-zhang-lie-biao-ms-arclist.html" target="_blank">${'$'}{field.id}</a>
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column label="栏目名" align="left" prop="categoryTitle" width="180">
            </el-table-column>
            <el-table-column label="文章标题" align="left" prop="contentTitle" show-overflow-tooltip>
                <template slot-scope="scope">
                    {{scope.row.contentTitle}}
                </template>
            </el-table-column>
            <el-table-column label="发布范围" align="left" prop="categoryFlag" width="140" show-overflow-tooltip>
                <template slot-scope="scope">
                    {{getDictLabel(scope.row.contentStyle)}}
                </template>
            </el-table-column>
<#--            <el-table-column label="作者" align="left" prop="contentAuthor" width="100" show-overflow-tooltip>-->
<#--            </el-table-column>-->
            <el-table-column label="录入人" width="100" align="right" prop="createBy">
            </el-table-column>
            <el-table-column label="审核状态" width="100" align="center" prop="progressStatus"  >
            </el-table-column>
<#--            <el-table-column label="点击量" width="90" align="right" prop="contentHit">-->
<#--                <template slot='header'>点击量-->
<#--                    <el-popover placement="top-start" title="提示" trigger="hover" >-->
<#--                        <a href="http://doc.mingsoft.net/plugs-cms/biao-qian/wen-zhang-lie-biao-ms-arclist.html" target="_blank">${'$'}{field.hit}</a>-->
<#--                        <i class="el-icon-question" slot="reference"></i>-->
<#--                    </el-popover>-->
<#--                </template>-->
<#--                <template slot-scope="scope">-->
<#--                    {{scope.row.contentHit?scope.row.contentHit:0}}-->
<#--                </template>-->
<#--            </el-table-column>-->
            <el-table-column label="发布时间" align="center" prop="contentDatetime" :formatter="dateFormat" width="120">
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
                <template slot-scope="scope" >
                    <@shiro.hasPermission name="cms:content:update">
                        <el-link  type="primary" :underline="false"  @click="reduction([scope.row])">还原</el-link>
                    </@shiro.hasPermission>

                    <@shiro.hasPermission name="cms:content:del">
                        <el-link type="primary" :underline="false" @click="del([scope.row])">彻底删除</el-link>
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
        el: '#main',
        data: {
            power:[],
            conditionList: [
                {
                    action: 'and',
                    field: 'content_title',
                    el: 'eq',
                    model: 'contentTitle',
                    name: '文章标题',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'ct.category_id',
                    el: 'eq',
                    model: 'ct.category_id',
                    name: '栏目ID',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'ct.id',
                    el: 'eq',
                    model: 'id',
                    name: '文章编号',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'manager.manager_name',
                    el: 'eq',
                    model: 'managerName',
                    name: '录入人',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'organization.organization_title',
                    el: 'eq',
                    model: 'organization_title',
                    name: '部门',
                    type: 'input'
                },{
                    action: 'and',
                    field: 'content_type',
                    el: 'eq',
                    model: 'contentType',
                    name: '文章类型',
                    key: 'dictValue',
                    title: 'dictLabel',
                    type: 'checkbox',
                    label: false,
                    multiple: true
                }, {
                    action: 'and',
                    field: 'content_author',
                    el: 'eq',
                    model: 'contentAuthor',
                    name: '文章作者',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'content_source',
                    el: 'eq',
                    model: 'contentSource',
                    name: '文章来源',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'content_datetime',
                    model: 'contentDatetime',
                    el: 'gt',
                    name: '发布时间',
                    type: 'date'
                }, {
                    action: 'and',
                    field: 'content_description',
                    el: 'eq',
                    model: 'contentDescription',
                    name: '描述',
                    type: 'textarea'
                }, {
                    action: 'and',
                    field: 'content_keyword',
                    el: 'eq',
                    model: 'contentKeyword',
                    name: '关键字',
                    type: 'textarea'
                }, {
                    action: 'and',
                    field: 'content_url',
                    el: 'eq',
                    model: 'contentUrl',
                    name: '文章跳转链接地址',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'ct.create_date',
                    el: 'eq',
                    model: 'createDate',
                    name: '创建时间',
                    type: 'date'
                }, {
                    action: 'and',
                    field: 'ct.update_date',
                    el: 'eq',
                    model: 'updateDate',
                    name: '修改时间',
                    type: 'date'
                }],
            conditions: [],
            contentCategoryIdOptions: [],
            dataList: [],
            //文章列表
            selectionList: [],
            //文章列表选中
            total: 0,
            //总记录数量
            pageSize: 10,
            //页面数量
            currentPage: 1,
            //初始页
            manager: ms.manager,
            loadState: false,
            loading: true,
            //加载状态
            emptyText: '',
            // loading
            saveDisabled: false,
            //提示文字
            contentDisplayOptions: [{
                "value": "0",
                "label": "是"
            }, {
                "value": "1",
                "label": "否"
            }],
            //搜索表单
            form: {
                sqlWhere: null,
                // 文章标题
                contentTitle: null,
                // 文章类型
                contentType: null,
                contentStyle:null,
                id: '', //  文章id
                createBy: '',   //  录入人
            },
            //文章模型选项
            contentStyleOptions: [],
            ids:[],
            leaf:1

        },
        methods: {

            //查询列表
            list: function () {
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

                history.replaceState({
                    form: form,
                    page: page
                }, "");
                //筛选栏目类型，1=列表
                that.form.categoryType = '1';
                ms.http.post(ms.manager + "/cms/content/recycleBin.do", form.sqlWhere ? Object.assign({}, {
                    categoryType: '1',
                    sqlWhere: form.sqlWhere
                }, page) : Object.assign({}, form, page)).then(function (res) {
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true;
                    }

                    if (!res.result || res.data.total <= 0) {
                        that.emptyText = '暂无数据';
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
                var that = this;
                that.ids = [];
                val.forEach(function (item) {that.ids.push(item.id)})
            },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/cms/content/cleanRecycleBin.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(function (res) {
                        if (res.result) {
                            that.$notify({
                                title:'成功',
                                type: 'success',
                                message: '删除成功!'
                            }); //删除成功，刷新列表

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

            //还原
            reduction: function(row) {
                var that = this;
                that.saveDisabled = true;
                ms.http.post(ms.manager + "/cms/content/reduction.do", row.length ? row : [row] , {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(function(res) {
                    if (res.result) {
                        that.$notify({
                            title: '成功',
                            message: res.msg,
                            type: 'success',
                        });
                        that.saveDisabled = false;
                        that.list();
                    } else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                        that.saveDisabled = false;
                    }
                });
            },

            dateFormat: function (row, column, cellValue, index) {
                if (cellValue) {
                    return ms.util.date.fmt(cellValue, 'yyyy-MM-dd');
                } else {
                    return '';
                }
            },
            contentDisplayFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.contentDisplayOptions.find(function (value) {
                        return value.value == cellValue;
                    });

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
                this.form.sqlWhere = null;
                this.$refs.searchForm.resetFields();
                this.list();
            },
            //根据字典数据值获取字典标签名
            getDictLabel: function (v) {
                var that = this;
                var labels = [];
                if(v){
                    v.split(",").forEach(function (item) {
                        for (var key in that.contentStyleOptions) {
                            if (item == that.contentStyleOptions[key].dictValue) {
                                labels.push(that.contentStyleOptions[key].dictLabel);
                                break;
                            }
                        }
                    });
                }
                return labels.toString();
            },
            //获取contentCategoryId数据源
            contentCategoryIdOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/cms/category/list.do").then(function (res) {
                    if (res.result) {
                        that.contentCategoryIdOptions = res.data.rows;
                    }

                    that.list();
                }).catch(function (err) {
                    console.log(err);
                });
            },

            // 查看日志
            log: function (id) {
                location.href=ms.manager +"/progress/progressLog/index.do?dataId="+id;
            },
            contentStyleOptionsGet: function () {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {
                    dictType: '模板类型',
                    pageSize: 99999
                }).then(function (data) {
                    if (data.result) {
                        data = data.data;
                        that.contentStyleOptions = data.rows;
                    }
                })
            }

        },

        mounted: function () {
            this.contentCategoryIdOptionsGet();
            this.contentStyleOptionsGet();
            if (history.hasOwnProperty("state")) {
                this.form = history.state.form;
                this.currentPage = history.state.page.pageNo;
                this.pageSize = history.state.page.pageSize;
            }

        }
    });
</script>
<style>
    #main .ms-search {
        padding: 20px 0 0;
    }
    #main .ms-container {
        height: calc(100vh - 141px);
    }
    body{
        overflow: hidden;
    }
</style>
