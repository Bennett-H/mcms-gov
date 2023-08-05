<!DOCTYPE html>
<html>
<head>
    <title>字典</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"  :disabled="!selectionList.length">删除</el-button>
        </el-col>
    </el-header>

    <el-main class="ms-container">
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" :selectable="isChecked" width="40"></el-table-column>
            <el-table-column label="标签名" align="left" prop="dictLabel" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="数据值" width="150" align="left" prop="dictValue" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="排序" align="right" width="100" prop="dictSort">
            </el-table-column>
            <el-table-column label="来源" align="center" width="100" prop="del">
                <template slot='header'>来源
                    <el-popover placement="top-start" title="提示" trigger="hover">
                        系统默认：系统字典数据不能删除<br/>
                        用户创建：业务创建字典数据
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                </template>
                <template slot-scope="scope">
                    <template v-if="scope.row.notDel == 1">系统默认</template>
                    <template v-else>用户创建</template>
                </template>
            </el-table-column>
                <el-table-column label="启用状态" align="right" width="100" prop="dictSort">
                    <template slot-scope="scope">
                        <el-switch v-model="scope.row.dictEnable"
                                   @change="update(scope.$index)">
                        </el-switch>
                    </template>
                </el-table-column>
                <el-table-column label="操作" fixed="right" align="center" width="180">
                    <template slot-scope="scope">
                            <el-link :underline="false" type="primary" size="medium"
                                     @click="save(scope.row.id)">编辑
                            </el-link>
                            <el-link :underline="false" v-if="scope.row.notDel == 0" type="primary"
                                     @click="del([scope.row])">删除
                            </el-link>
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
        data: function () {
            return {
                conditionList: [
                    //标签名
                    {'action': 'and', 'field': 'SFDSF', 'el': 'eq', 'model': 'sfdsf', 'name': '标签名', 'type': 'input'},
                    //数据值
                    {'action': 'and', 'field': 'SDFSD', 'el': 'eq', 'model': 'sdfsd', 'name': '数据值', 'type': 'input'},
                    //排序
                    {'action': 'and', 'field': 'SDFDS', 'el': 'eq', 'model': 'sdfds', 'name': '排序', 'type': 'input'},
                    //来源
                    {'action': 'and', 'field': 'AD', 'el': 'eq', 'model': 'ad', 'name': '来源', 'type': 'input'},
                    // 启用状态
                    {
                        'action': 'and',
                        'field': 'SDFDFSFS',
                        'el': 'eq',
                        'model': 'sdfdfsfs',
                        'name': '启用状态',
                        'type': 'switch'
                    },
                ],
                conditions: [],
                dataList: [], //字典列表
                selectionList: [],//字典列表选中
                total: 0, //总记录数量
                pageSize: 10, //页面数量
                currentPage: 1, //初始页
                manager: ms.manager,
                dictType: '',//字典类型
                loading: true,//加载状态
                emptyText: '',//提示文字
                //搜索表单
                form: {
                    sqlWhere: null
                },
            }
        },
        watch:{

        },
        methods:{
            isChecked: function (row) {
                return row.notDel == 0
            },
            //查询列表
            list: function(isSearch) {
                var that = this;
                var data = {}; //搜索参数
                that.loading = true;
                var page={
                    pageNo: that.currentPage,
                    pageSize : that.pageSize,
                    dictType :  that.dictType
                }
                var form = JSON.parse(JSON.stringify(that.form))
                form.dictType = that.dictType;

                if(isSearch) {
                    //删除空字符串
                    for (var key in form){
                        if(form[key] === undefined || form[key] === null){
                            delete  form[key]
                        }
                    }
                    form.sqlWhere ? data = Object.assign({}, {sqlWhere: form.sqlWhere}, page) : data = Object.assign({}, form, page)
                } else {
                    data = page;
                }

                history.replaceState({form:that.form,page:page},"");
                ms.http.post(ms.manager+"/mdiy/dict/data/list.do",data).then(
                    function(res) {
                        if (!res.result||res.data.total <= 0) {
                            that.emptyText ="暂无数据"
                            that.dataList = [];
                            that.total = 0;
                        } else {
                            that.emptyText = '';
                            that.total = res.data.total;
                            that.dataList = res.data.rows;
                        }
                        that.loading = false;
                    }).catch(function(err) {
                    that.loading = false;
                    console.log(err);
                });

            },
            //字典列表选中
            handleSelectionChange:function(val){
                this.selectionList = val;
            },
            //删除
            del: function(row){
                var that = this;
                that.$confirm("此操作将永久删除所选内容, 是否继续", "提示", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: 'warning'
                }).then(function() {
                    ms.http.post(ms.manager+"/mdiy/dict/data/delete.do", row.length?row:[row],{
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(
                        function(res){
                            if (res.result) {
                                that.$notify({
                                    title: '成功',
                                    type: 'success',
                                    message:"删除成功"
                                });
                                //删除成功，刷新列表
                                that.list();
                            }else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        });
                }).catch(function(err) {
                    //删除如果用户取消会抛出异常，所以需要catch一下
                });
            },
            //新增
            save:function(id){
                var that = this
                if(id){
                    // location.href=this.manager+"/mdiy/dictData/form.do?id="+id;
                    ms.util.openSystemUrl("/mdiy/dict/data/form.do?id="+id);
                }else {
                    //对uri进行编码
                    // location.href = that.manager + "/mdiy/dictData/form.do?dictType=" + that.dictType;
                    ms.util.openSystemUrl("/mdiy/dict/data/form.do?dictType="+that.dictType);

                }
            },
            //更新状态
            update: function (index) {
                var that = this;
                ms.http.post(ms.manager + "/mdiy/dict/data/update.do", that.dataList[index]).then(function (data) {
                    if (data.result) {
                        that.$notify({
                            title: '成功',
                            message: '更新成功',
                            type: 'success'
                        });
                    } else {
                        that.$notify({
                            title: '失败',
                            message: data.msg,
                            type: 'warning'
                        });
                    }
                });
            },

            //pageSize改变时会触发
            sizeChange:function(pagesize) {
                this.loading = true;
                this.pageSize = pagesize;
                this.list(true);
            },
            //currentPage改变时会触发
            currentChange:function(currentPage) {
                this.loading = true;
                this.currentPage = currentPage;
                this.list(true);
            },
            search:function(data){
                this.form.sqlWhere = JSON.stringify(data);
                this.list(true);
            },
            //重置表单
            rest:function(){
                this.currentPage = 1;
                this.form = {
                    sqlWhere:null
                };
                this.list();
            },

        },
        created:function(){
            var that = this;
            that.dictType = ms.util.getParameter("dictType");
            if(history.state){
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
