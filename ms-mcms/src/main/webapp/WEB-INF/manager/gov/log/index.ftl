<!DOCTYPE html>
<html>
<head>
    <title>日志备份管理</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak class="ms-index">
    <el-header class="ms-header" height="50px">
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info"
                description="用查看与下载备份的操作日志文件">
        </el-alert>
    </el-header>

    <el-main class="ms-container">
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column label="日志月份" align="left" prop="folderName">
                <template slot-scope="scope">
                    <div style="margin-left: 5px;display: flex;align-items: center;justify-content: flex-start;">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-wenjianjia2"></use>
                    </svg>
                    <span style="margin-left: 5px">{{scope.row.folderName}}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="类型" width="120" align="center" prop="folderType">
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <el-link type="primary" :underline="false" @click="view(scope.row.folderName)">查看</el-link>
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
            uploadLoading:false,
            dataList: [],
            //模板管理列表
            selectionList: [],
            //模板管理列表选中
            manager: ms.manager,
            loading: true,
            //加载状态
            emptyText: '',
            fileList: [],
            websiteId: null
        },
        methods: {
            //查询列表
            list: function () {
                var that = this;
                that.loading = true;
                setTimeout(function () {
                    ms.http.get(ms.manager + "/gov/log/queryLogBackSkin.do").then(function (res) {
                        that.dataList = [];
                        if(res.data.folderNameList!=null){
                            res.data.folderNameList.forEach(function (item) {
                                var type = "文件夹";
                                that.dataList.push({
                                    folderName: item,
                                    folderType: type
                                });
                            });
                        }
                        that.websiteId = res.data.websiteId;
                    }).finally(function () {
                        that.loading = false;
                    });
                }, 500);
            },
            //模板管理列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //查看
            view: function (name) {
                window.location.href = ms.manager + "/gov/log/list.do?name="+name;
            },
            //表格数据转换
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
            //重置表单
            rest: function () {
                this.list();
            },

        },
        mounted: function () {
            this.list();
        }
    });
</script>
<style>
    #index .ms-container {
        height: calc(100vh - 78px);
    }
    .icon {
        width: 2em;
        height: 2em;
        vertical-align: -0.15em;
        fill: currentColor;
        overflow: hidden;
    }
</style>
