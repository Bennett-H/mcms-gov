<!DOCTYPE html>
<html>
<head>
    <title>微信消息模板</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">
        <el-col :span="24">
            <@shiro.hasPermission name="mweixin:template:sync"><el-button :loading="syncLoading" @click='sync' size="mini" icon="el-icon-upload" class="ms-fr">同步微信消息模板</el-button></@shiro.hasPermission>
        </el-col>
    </el-header>
    <div class="ms-search">
        <el-row>
            <el-form :model="form" ref="searchForm" label-width="120px" size="mini">
                <el-row>
                    <el-col :span="6">
                        <!--模板名称-->
                        <el-form-item  label="模板名称" prop="templateTitle">
                            <el-input
                                    v-model="form.templateTitle"
                                    :disabled="false"
                                    :readonly="false"
                                    :style="{width:  '100%'}"
                                    :clearable="true"
                                    placeholder="请输入模板名称">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <!--模板编码-->
                        <el-form-item  label="模板编码" prop="templateCode">
                            <el-input
                                    v-model="form.templateCode"
                                    :disabled="false"
                                    :readonly="false"
                                    :style="{width:  '100%'}"
                                    :clearable="true"
                                    placeholder="请输入模板名称">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="text-align: right;">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list(true)">搜索</el-button>
                        <el-button type="primary" icon="iconfont icon-shaixuan1" size="mini" @click="currentPage=1;$refs.search.open()">筛选</el-button>
                        <el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-main class="ms-container">
        <el-alert
                class="ms-alert-tip"
                title="提示"
                type="info">
            <template slot='title'>
                <div><STRONG>提示:</STRONG></div>
                <div>更多开发方法请查看文档：<a href="http://doc.mingsoft.net/plugs/weixin/jie-shao.html" target="_blank">微信文档</a></div>
            </template>
        </el-alert>
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40" :selectable="isChecked"></el-table-column>
            <el-table-column label="标题" align="left" prop="templateTitle" width="150">
            </el-table-column>
            <el-table-column label="模板编码" align="left" prop="templateCode" width="150">
                <template slot='header'>模板编码
                    <el-popover placement="top-start" title="提示" trigger="hover" >
                        模板编码：全表唯一，建议命名时用不同微信前缀开头，在业务开发时用这个字段做唯一标识
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column label="模板ID" align="left" prop="templateId">
            </el-table-column>
            <el-table-column label="模板关键词" align="left" prop="templateKeyword">
                <template slot='header'>模板关键词
                    <el-popover placement="top-start" title="提示" trigger="hover" >
                        模板关键词：基于FreeMarker写法
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column label="同步时间" align="center" prop="createDate" width="180">
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="mweixin:template:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                    </@shiro.hasPermission>
                </template>
            </el-table-column>
        </el-table>
    </el-main>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: {
            conditionList: [
                //微信编号
                {
                    'action': 'and',
                    'field': 'WEIXIN_ID',
                    'el': 'eq',
                    'model': 'weixinId',
                    'name': '微信编号',
                    'type': 'input'
                },
                //标题
                {
                    'action': 'and',
                    'field': 'TEMPLATE_TITLE',
                    'el': 'eq',
                    'model': 'templateTitle',
                    'name': '标题',
                    'type': 'input'
                },
                //模板编码
                {
                    'action': 'and',
                    'field': 'TEMPLATE_CODE',
                    'el': 'eq',
                    'model': 'templateCode',
                    'name': '模板编码',
                    'type': 'input'
                },
                //模板ID
                {
                    'action': 'and',
                    'field': 'TEMPLATE_ID',
                    'el': 'eq',
                    'model': 'templateId',
                    'name': '模板ID',
                    'type': 'input'
                },
                //主属行业
                {
                    'action': 'and',
                    'field': 'TEMPLATE_PRIMARY_INDUSTRY',
                    'el': 'eq',
                    'model': 'templatePrimaryIndustry',
                    'name': '主属行业',
                    'type': 'input'
                },
                //副属行业
                {
                    'action': 'and',
                    'field': 'TEMPLATE_DEPUTY_INDUSTRY',
                    'el': 'eq',
                    'model': 'templateDeputyIndustry',
                    'name': '副属行业',
                    'type': 'input'
                },
                // 内容
                {
                    'action': 'and',
                    'field': 'TEMPLATE_CONTENT',
                    'el': 'eq',
                    'model': 'templateContent',
                    'name': '内容',
                    'type': 'textarea'
                },
                // 样例
                {
                    'action': 'and',
                    'field': 'TEMPLATE_EXAMPLE',
                    'el': 'eq',
                    'model': 'templateExample',
                    'name': '样例',
                    'type': 'textarea'
                },
                //模板关键词
                {
                    'action': 'and',
                    'field': 'TEMPLATE_KEYWORD',
                    'el': 'eq',
                    'model': 'templateKeyword',
                    'name': '模板关键词',
                    'type': 'input'
                },
            ],
            conditions: [],
            dataList: [], //微信消息模板列表
            selectionList: [],//微信消息模板列表选中
            manager: ms.manager,
            syncLoading: false, // 同步按钮状态
            loading: true,//加载状态
            emptyText: '',//提示文字
            //搜索表单
            form: {
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
                var form = JSON.parse(JSON.stringify(that.form))


                if (isSearch) {
                    //删除空字符串
                    for (var key in form) {
                        if (form[key] === undefined || form[key] === null) {
                            delete form[key]
                        }
                    }
                    form.sqlWhere ? data = Object.assign({}, {sqlWhere: form.sqlWhere}) : data = Object.assign({}, form)
                }
                ms.http.post(ms.manager + "/mweixin/template/list.do", data).then(
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
            //微信消息模板列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //新增
            save: function (id) {
                if (id) {
                    location.href = this.manager + "/mweixin/template/form.do?id=" + id;
                }
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
            // 同步微信消息模板
            sync: function () {
                var that = this;
                that.syncLoading = true;
                that.$notify({
                    title: '提示',
                    message: "正在同步，请不要刷新页面，预计需要1-2分钟",
                    type: 'warning'
                });
                ms.http.post(ms.manager + "/mweixin/template/sync.do").then(function (res) {
                    if(res.result){
                        that.$notify({
                            title: '成功',
                            message: "同步成功",
                            type: 'success'
                        });
                        that.list();
                    }else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'error'
                        });
                    }
                    that.syncLoading = false;
                }, function (err) {
                    that.syncLoading = false;
                })
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
        height: calc(100vh - 78px);
    }
</style>
