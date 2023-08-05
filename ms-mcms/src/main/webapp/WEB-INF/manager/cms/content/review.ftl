<!DOCTYPE html>
<html>
<head>
    <title>文章审批</title>
    <#include "../../include/head-file.ftl">

</head>
<body>
<div id="main" class="ms-index" v-cloak>
    <div class="ms-search">
        <el-row>
            <el-form :model="form"  ref="searchForm"  label-width="120px" size="mini">
                <el-row>

                    <el-col :span="8">
                        <el-form-item  label="审批类型" prop="plStatus">
                            <el-select v-model="form.plStatus"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="false"
                                        :clearable="true"
                                       placeholder="请选择审批类型">
                                <el-option v-for='item in contentTypeOptions' :key="item.value" :value="item.value"
                                           :label="item.label"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
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
                    <el-col :span="8" style="text-align: right;padding-right: 10px;">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="form.sqlWhere=null;currentPage=1;list()">查询</el-button>
                        <el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
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
            <el-table-column label="编号" width="210" prop="id">
            </el-table-column>
            <el-table-column label="栏目名" align="left" prop="categoryTitle" width="180">
            </el-table-column>
            <el-table-column label="文章标题" align="left" prop="contentTitle" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="录入人" align="left" prop="createBy" width="100" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="审核状态" width="100" align="center" prop="progressStatus"  >
            </el-table-column>
            <el-table-column label="发布时间" align="center" prop="contentDatetime" :formatter="dateFormat" width="120">
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="cms:content:submit">
                        <el-link type="primary" v-if="scope.row.progressStatus.indexOf('不通过') > -1" :underline="false" @click="submit(scope.row.id)">重新提交</el-link>
                    </@shiro.hasPermission>
                    <el-link type="primary" :underline="false" @click="view(scope.row.id)">预览</el-link>
                    <@shiro.hasPermission name="approval:config:approval">
                        <el-link type="primary" v-if="bottonFlag=='audit'" :underline="false" @click="approval(scope.row.id,scope.row.categoryId)">审核</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="cms:content:log">
                        <el-link type="primary" :underline="false" @click="log(scope.row.id)">审批日志</el-link>
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
            //提示文字
            contentTypeOptions: [{
                "value": "audit",
                "label": "待审核"
            }, {
                "value": "reject",
                "label": "审核失败"
            }, {
                "value": "adopt",
                "label": "审核通过"
            }],
            contentDisplayOptions: [{
                "value": "0",
                "label": "是"
            }, {
                "value": "1",
                "label": "否"
            }],
            //搜索表单
            form: {
                // 文章标题
                contentTitle: null,
                // 文章类型
                plStatus: 'audit',
            },
            bottonFlag:"",
        },
        methods: {
            //查询列表
            list: function () {
                var that = this;
                that.loading = true;
                that.loadState = false;
                that.bottonFlag=that.form.plStatus;
                var form = JSON.parse(JSON.stringify(that.form));
                // 去除审批状态
                if("audit"==form.plStatus){
                    form.plStatus=null;
                }
                var page = {
                    pageNo: that.currentPage,
                    pageSize: that.pageSize
                };
                ms.http.get(ms.manager + "/cms/content/auditList.do",  Object.assign({}, form, {schemeName:"文章审核"},page)).then(function (res) {
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true;
                    }
                    if (res.result) {
                        if(res.data.total > 0){
                            that.emptyText = '';
                            that.total = res.data.total;
                            that.dataList = res.data.rows;
                        }else {
                            that.emptyText = '暂无数据';
                            that.dataList = [];
                            that.total = 0;
                        }
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

            view: function (id) {
                if (id) {
                    window.open(ms.manager+"/cms/content/view.do?id="+id);
                }
            },

            // 查看日志
            log: function (id) {
                location.href=ms.manager +"/progress/progressLog/index.do?dataId="+id;
            },
            approval: function (dataId,categoryId) {
                location.href = ms.manager +"/approval/config/approvalForm.do?categoryId="+categoryId+"&dataId="+dataId+"&schemeName="+encodeURI("文章审核");
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
                    }

                    that.list();
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        mounted: function () {
            this.contentCategoryIdOptionsGet();
            if (history.hasOwnProperty("state")) {
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
