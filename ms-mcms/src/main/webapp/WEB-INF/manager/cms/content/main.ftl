<!-- 增加发布到字段 -->
<!-- 增加审批相关功能 -->
<!DOCTYPE html>
<html>
<head>
    <title>文章主体</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/datascope/index.js"></script>
</head>
<body>
<div id="main" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">
        <el-col >
            <@shiro.hasPermission name="cms:content:save">
                <el-button style="margin-right: 8px"
                           v-if="hasPermission('cms:content:save') || (form.categoryId==undefined)"
                           type="primary"  icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="cms:content:del">
                <el-button style="margin-right: 8px"
                           v-if="hasPermission('cms:content:del')"
                           type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"
                           :disabled="!selectionList.length">删除</el-button>
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
                        <el-form-item  label="文章类型" prop="contentType">
                            <el-select v-model="form.contentType"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="false"
                                       :multiple="true" :clearable="true"
                                       placeholder="请选择文章类型">
                                <el-option v-for='item in contentTypeOptions' :key="item.dictValue" :value="item.dictValue"
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
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column label="编号" width="100" prop="id" show-overflow-tooltip>
                <template slot='header'>编号
                    <el-popover placement="top-start" title="提示" trigger="hover" >
                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html#%E6%96%87%E7%AB%A0%E5%88%97%E8%A1%A8-msarclist" target="_blank">${'$'}{field.id}</a>
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column label="栏目名" align="left" prop="categoryId" :formatter="contentCategoryIdFormat" width="150" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="文章标题" align="left" prop="contentTitle" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="审核状态" align="left" prop="progressStatus" width="100" show-overflow-tooltip>
                <template slot='header'>审核状态
                    <el-popover placement="top-start" title="提示" trigger="hover" >
                        提交审核的文章需要在待审文章列表中审核
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column label="作者" align="left" prop="contentAuthor" width="100" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="排序" width="55" align="right" prop="contentSort">
            </el-table-column>
            <el-table-column label="点击量" width="90" align="right" prop="contentHit">
                <template slot='header'>点击量
                    <el-popover placement="top-start" title="提示" trigger="hover" >
                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html#%E6%96%87%E7%AB%A0%E5%88%97%E8%A1%A8-msarclist" target="_blank">${'$'}{field.hit}</a>
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
                <template slot-scope="scope">
                    {{scope.row.contentHit?scope.row.contentHit:0}}
                </template>
            </el-table-column>
            <el-table-column label="发布时间" align="center" prop="contentDatetime" :formatter="dateFormat" width="120">
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
                <template slot='header'>操作
                    <el-popover placement="top-start" title="提示" trigger="hover">
                        审批日志 需要两个角色权限，文章管理的审批日志和进度日志的查看 <br>
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
                <template slot-scope="scope" >
                    <@shiro.hasPermission name="cms:content:view">
                        <el-link type="primary" v-if="hasPermission('cms:content:view')" :underline="false"  @click="save(scope.row)">查看</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="cms:content:submit">
                        <el-link type="primary" v-if="scope.row.progressStatus.indexOf('不通过') > -1" :underline="false" @click="submit(scope.row.id)">重新提交</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="cms:content:del">
                        <el-link v-if="hasPermission('cms:content:del')" type="primary" :underline="false" @click="del([scope.row])">删除</el-link>
                    </@shiro.hasPermission>

                    <template v-if="scope.row.progressStatus=='草稿' ">
                        <@shiro.hasPermission name="cms:content:submit">
                        <el-link  type="primary" :underline="false" @click="submit(scope.row.id)">提交审核</el-link>
                        </@shiro.hasPermission>
                    </template>

                    <template  v-else>
                        <@shiro.hasPermission name="cms:content:log">
                            <el-link v-if="hasPermission('cms:content:log')" type="primary" :underline="false" @click="log(scope.row.id)">审批日志</el-link>
                        </@shiro.hasPermission>
                    </template>
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
        data: function () {
            return {
                datascopes: [false], //默认所有都没有权限
                //搜索列表
                conditionList: [{
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
                    model: 'contentCategoryId',
                    name: '所属栏目',
                    key: 'id',
                    title: 'categoryTitle',
                    type: 'cascader',
                    multiple: false
                }, {
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
                    field: 'content_display',
                    el: 'eq',
                    model: 'contentDisplay',
                    name: '是否显示',
                    key: 'value',
                    title: 'label',
                    type: 'radio',
                    label: true,
                    multiple: false
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
                    field: 'content_sort',
                    el: 'eq',
                    model: 'contentSort',
                    name: '自定义顺序',
                    type: 'number'
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
                    field: 'content_details',
                    el: 'like',
                    model: 'contentDetails',
                    name: '文章内容',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'content_url',
                    el: 'eq',
                    model: 'contentUrl',
                    name: '文章跳转链接地址',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'ct.id',
                    el: 'eq',
                    model: 'ct.id',
                    name: '文章id',
                    type: 'input'
                }, {
                    action: 'and',
                    field: 'ct.create_date',
                    el: 'eq',
                    model: 'ct.createDate',
                    name: '创建时间',
                    type: 'date'
                }, {
                    action: 'and',
                    field: 'ct.update_date',
                    el: 'eq',
                    model: 'ct.updateDate',
                    name: '修改时间',
                    type: 'date'
                }],
                dataList: [],//文章列表
                selectionList: [],//文章列表选中
                total: 0,//总记录数量
                pageSize: 10,//页面数量
                currentPage: 1,//初始页
                manager: ms.manager,
                loadState: false,
                loading: true,//加载状态
                emptyText: '',//提示文字
                conditions: [],//搜索条件
                //搜索选项命名：搜索列表成员model名+'Options'
                contentCategoryIdOptions: [],//搜索所选栏目选项
                contentTypeOptions: [],//搜索文章类型选项
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
                    categoryId: '',
                    // 审批文章状态
                    progressStatus: '',
                },
                leaf:true
            }
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

                if (form.contentType!=null && form.contentType.length > 0) {
                    form.contentType = form.contentType.join(',');
                }

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
                ms.http.post(ms.manager + "/cms/content/list.do", form.sqlWhere ? Object.assign({}, {
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
                var that = this;
                that.selectionList = [];
                val.forEach(function(item){
                    // 防止参数过长
                    item.contentDetails = ''
                    that.selectionList.push(item);
                })
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
            },
            //重新提交
            submit: function (id) {
                var that = this;
                ms.http.post(ms.manager + "/cms/content/submit.do", {"schemeName":"文章审核","dataId": id}).then(function (res) {
                    if (res.result && res.data) {
                        that.$notify({
                            title: '成功',
                            type: 'success',
                            message: '提交成功'
                        });
                    }else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                    that.list();
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/cms/content/delete.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(function (res) {
                        if (res.result) {
                            that.$notify({
                                title: '成功',
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
                })
            },
            //新增
            save: function (row) {
                //id有值时编辑
                if (row && row.id) {
                    location.href = this.manager + "/cms/content/form.do?id=" + row.id + '&categoryId=' + row.categoryId
                }else {
                    //根据当前栏目新增时自动选中栏目
                    var categoryId = this.form.categoryId;
                    if (categoryId) {
                        location.href = this.manager + "/cms/content/form.do?categoryId=" + this.form.categoryId;
                    }else {
                        //如果栏目id没有值就单纯的新增，不自动选定栏目
                        location.href = this.manager + "/cms/content/form.do";
                    }
                }
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
            //获取contentCategoryId数据源
            contentCategoryIdOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/cms/category/list.do").then(function (res) {
                    if (res.result) {
                        that.contentCategoryIdOptions = res.data.rows;
                    }else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                    }

                    that.list();
                });
            },
            //获取contentType数据源
            contentTypeOptionsGet: function () {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {
                    dictType: '文章属性',
                    pageSize: 99999
                }).then(function (data) {
                    if(data.result){
                        data = data.data;
                        that.contentTypeOptions = data.rows;
                    }
                });
            },
            //判断是否有权限
            hasPermission:function (permission) {
                //后台接口因为参数问题直接会返回false
                if(this.datascopes==false){
                    return false;
                }
                var has = this.datascopes == true || this.datascopes.indexOf(permission)>-1;
                return has;
            },
            //查询权限配置
            queryDatascope:function() {
                var that = this;
                ms.datascope({
                    dataType: "管理员栏目权限",
                    dataId: ms.util.getParameter("categoryId"),
                    isSuper: true
                }).then(function(datascopes){
                    that.datascopes = datascopes;
                });
            },
        },

        created: function () {
            var that = this;
            that.form.categoryId = ms.util.getParameter("categoryId");
            this.queryDatascope();

        },
        mounted: function () {
            this.contentCategoryIdOptionsGet();
            this.contentTypeOptionsGet();
            this.leaf = ms.util.getParameter("leaf")==null?true:JSON.parse(ms.util.getParameter("leaf"));
            if (history.hasOwnProperty("state")) {
                this.form = history.state.form;
                this.currentPage = history.state.page.pageNo;
                this.pageSize = history.state.page.pageSize;
            }

        },


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
