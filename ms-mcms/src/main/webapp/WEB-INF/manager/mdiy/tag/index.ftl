<!DOCTYPE html>
<html>
<head>
    <title>标签</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="mdiy:tag:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="mdiy:tag:del">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"
                           :disabled="!selectionList.length">删除
                </el-button>
            </@shiro.hasPermission>
        </el-col>
        <el-tooltip class="item" effect="dark" content="如果标签数据有修改，数据获取不正确，尝试刷新缓存" placement="bottom">
            <el-button type="success" plain style="float: right; "
                       icon="el-icon-refresh-left" size="mini"
                       @click="updateCache()">刷新缓存
            </el-button>
        </el-tooltip>
    </el-header>
    <div class="ms-search">
        <el-row>
            <el-form :model="form" ref="searchForm" label-width="85px" size="mini">
                <el-row>
                    <el-col :span="8">
                        <el-form-item label="标签名称" prop="tagName">
                            <el-input v-model="form.tagName"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入标签名称">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16" style="text-align: right">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list()">查询
                        </el-button>
                        <el-button @click="rest" icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-main class="ms-container">
        <el-table v-loading="loading" ref="multipleTable" height="calc(100vh-68px)" class="ms-table-pagination" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40" :selectable="isChecked"></el-table-column>
            <el-table-column label="标签名称" width="200" align="left" prop="tagName">
            </el-table-column>
            <el-table-column label="标签类型" width="200" align="center" prop="tagType">
                <template slot-scope="scope">
                    {{formatterTagType(scope.row.tagType)}}
                </template>
            </el-table-column>
            <el-table-column label="描述" align="left" prop="tagDescription">
            </el-table-column>
            <el-table-column label="操作" fixed="right" align="center" width="180">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="mdiy:tag:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="mdiy:tag:del">
                        <el-link type="primary" v-if="scope.row.notDel!=1" :underline="false" @click="del([scope.row])">删除</el-link>
                    </@shiro.hasPermission>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                background
                :page-sizes="[5, 10, 20, 50, 100]"
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
<#include "/mdiy/tag/form.ftl">
</body>
</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: {
            dataList: [],
            //标签列表
            selectionList: [],
            //标签类型
            tagTypes:[],
            //标签列表选中
            total: 0,
            //总记录数量
            pageSize: 20,
            //页面数量
            currentPage: 1,
            //初始页
            mananger: ms.manager,
            loading: true,
            //加载状态
            emptyText: '',
            //提示文字
            //搜索表单
            form: {
                // 标签名称
                tagName: null
            }
        },
        methods: {
            //刷新缓存
            updateCache: function() {
                var that = this;
                ms.http.post(ms.manager + "/mdiy/tag/updateCache.do").then(function (data) {
                    if (data.result) {
                        that.$notify({
                            title: '成功',
                            type: 'success',
                            message: '刷新成功!'
                        });
                        that.list();
                    } else {
                        that.$notify({
                            title: '失败',
                            message: data.msg,
                            type: 'warning'
                        });
                    }
                });

            },
            isChecked: function(row) {
                if(row.notDel==1) {
                    return false;
                }
                return true;
            },
            //查询列表
            list: function() {
                var that = this;
                that.loading = true
                var page = {
                    pageNo: that.currentPage,
                    pageSize: that.pageSize
                };
                var form = JSON.parse(JSON.stringify(that.form));

                for (key in form) {
                    if (!form[key]) {
                        delete form[key];
                    }
                }

                history.replaceState({
                    form: form,
                    page: page,
                    total: that.total
                }, "");
                setTimeout(function () {
                    ms.http.get(ms.manager + "/mdiy/tag/list.do", Object.assign({}, that.form, page)).then(function (data) {
                        if(data.result){
                            if (data.data.total <= 0) {
                                that.loading = false;
                                that.emptyText = '暂无数据';
                                that.dataList = [];
                            } else {
                                that.emptyText = '';
                                that.loading = false;
                                that.total = data.data.total;
                                that.dataList = data.data.rows;
                            }
                        }
                    }).catch(function (err) {
                        console.log(err);
                    });
                }, 1000);
            },
            //标签列表选中
            handleSelectionChange: function(val) {
                this.selectionList = val;
            },
            //删除
            del: function(row) {
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/mdiy/tag/delete.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(function (data) {
                        if (data.result) {
                            that.$notify({
                                title: '成功',
                                type: 'success',
                                message: '删除成功!'
                            }); //删除成功，刷新列表

                            that.list();
                        } else {
                            that.$notify({
                                title: '失败',
                                message: data.msg,
                                type: 'warning'
                            });
                        }
                    });
                })
            },
            //新增
            save: function(id) {
                form.open(id);
            },
            //查询标签类型
            queryTagTypes: function() {
                var that = this
                //获取  标签类型 字典数据
                ms.mdiy.dict.list('标签类型').then(function (res) {
                    if(res.result){
                        that.tagTypes = res.data.rows
                    }
                })
            },
            //格式化标签类型
            formatterTagType: function(value) {
                var tagTypes = this.tagTypes
                for (let tagTypesKey in tagTypes) {
                    if(tagTypes[tagTypesKey].dictValue == value){
                        return tagTypes[tagTypesKey].dictLabel;
                    }
                }
            },
            //表格数据转换
            //pageSize改变时会触发
            sizeChange: function(pagesize) {
                this.loading = true;
                this.pageSize = pagesize;
                this.list();
            },
            //currentPage改变时会触发
            currentChange: function(currentPage) {
                this.loading = true;
                this.currentPage = currentPage;
                this.list();
            },
            //重置表单
            rest: function() {
                this.currentPage = 1;
                this.loading = true;
                this.$refs.searchForm.resetFields();
                this.list();
            }
        },
        created: function() {
            if (history.hasOwnProperty("state")&&history.state) {
                this.form = history.state.form;
                this.total = history.state.total;
                this.currentPage = history.state.page.pageNo;
                this.pageSize = history.state.page.pageSize;
            }
            this.queryTagTypes()

            this.list();

        }
    });
</script>

