<!DOCTYPE html>
<html>
<head>
    <title>广告</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="ad:ads:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="ad:ads:del">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"
                           :disabled="!selectionList.length">删除
                </el-button>
            </@shiro.hasPermission>
        </el-col>
    </el-header>
    <div class="ms-search">
        <el-row>
            <el-form :model="form" :rules="rules" ref="searchForm" label-width="120px" size="mini">
                <el-row>
					<el-col span="5">
                        <el-form-item label="广告位" prop="messageReceive">
                            <el-select v-model="form.positionName"
                                       :style="{width: '100%'}"
                                       :remote="true"
                                       :disabled="false"
                                       :remote-method="positionNameOptionsGet"
                                       :clearable="true"
                                       allow-create
                                       default-first-option
                                       placeholder="请选择广告位">
                                <el-option v-for='item in positionOptions' :key="item.id"
                                           :value="item.positionName"
                                           :label="item.positionName"></el-option>
                            </el-select>
                        </el-form-item>
					</el-col>
					<el-col span="5">
						<el-form-item  label="广告名称" prop="adsName">
							<el-input v-model="form.adsName"
									  :disabled="false"
									  :style="{width:  '100%'}"
									  :clearable="true"
									  placeholder="请输入广告名称">
							</el-input>
						</el-form-item>
					</el-col>
                    <el-col :span="4">

                        <el-form-item label="广告类型" prop="adsType">
                            <el-select v-model="form.adsType"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="false"
                                       :multiple="false" :clearable="true"
                                       placeholder="请选择广告类型">
                                <el-option v-for='item in adsTypeOptions' :key="item.dictValue" :value="item.dictValue"
                                           :label="item.dictLabel"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="10" style="text-align: right;padding-right: 10px;">
                        <el-button type="primary" icon="el-icon-search" size="mini"
                                   @click="loading=true;currentPage=1;list()">查询
                        </el-button>
                        <el-button @click="rest" icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-main class="ms-container">
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info"
                description="可以设定各种广告，例如京东淘宝等非常显目的位置，都可以通过广告插件设定并引用">
            <template slot="title">
                功能介绍 <a href='http://doc.mingsoft.net/plugs/guang-gao-cha-jian/jie-shao.html' target="_blank">开发手册</a>
            </template>
        </el-alert>
        <el-table v-loading="loading" ref="multipleTable" height="calc(100vh - 68px)" class="ms-table-pagination" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column label="广告位" align="left" prop="positionName">
            </el-table-column>
            <el-table-column label="广告名称" align="left" prop="adsName">
            </el-table-column>
            <el-table-column label="广告类型" align="center" prop="adsType" :formatter="adsTypeFormat">
            </el-table-column>
            <el-table-column label="开始时间" width="180" align="center" prop="adsStartTime">
            </el-table-column>
            <el-table-column label="结束时间" width="180" align="center" prop="adsEndTime">
            </el-table-column>
            <el-table-column label="是否开启" align="center" prop="adsState" :formatter="stateFormatter">
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="ad:ads:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="ad:ads:del">
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
        data:function() {
            return {
                dataList: [], //广告列表
                selectionList: [],//广告列表选中
                total: 0, //总记录数量
                pageSize: 10, //页面数量
                currentPage: 1, //初始页
                manager: ms.manager,
                loadState: false,
                loading: true,//加载状态
                emptyText: '',//提示文字
                adsTypeOptions: [],
                positionOptions: [],
                //搜索表单
                form: {
                    // 广告类型
                    adsType: null,
                    // 广告位
                    positionName: null,
                    //广告名称
                    adsName: null,
                    adsEndTime:[]
                },
                rules:{
                    // 广告名称
                    adsName: [{"min":0,"max":50,"message":"广告名称长度应在0-50之间"}]
                },
            }
        },
        methods: {
            //查询列表
            list: function () {
                var that = this;
                that.loading = true;
                that.loadState = false;
                var page={
                    pageNo: that.currentPage,
                    pageSize : that.pageSize
                }
                if (that.form.adsTime){
                    that.form.adsStartTime = that.form.adsTime[0];
                    that.form.adsEndTime = that.form.adsTime[1];
                }else {
                    that.form.adsStartTime = null
                    that.form.adsEndTime = null
                }
                var form = JSON.parse(JSON.stringify(that.form))
                form.adsTime = []

                for (key in form){
                    if(!form[key]){
                        delete  form[key]
                    }
                }

                history.replaceState({form:form,page:page,total:that.total},"");
                ms.http.get(ms.manager + "/ad/ads/list.do", Object.assign({},form,page)).then(
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
                            that.total = res.data.total;
                            that.dataList = res.data.rows;
                        }
                    }).catch(function (err) {
                        console.log(err);
                    });
                setTimeout(function(){
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true
                    }
                }, 500);
            },
            //广告列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            adsTypeFormat:function(row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.adsTypeOptions.find(function (value) {
                        return value.dictValue == cellValue;
                    })
                    if (data && data.dictLabel) {
                        value = data.dictLabel;
                    }
                }
                return value;
            },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function() {
                    ms.http.post(ms.manager + "/ad/ads/delete.do", row.length ? row : [row], {
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
                            }else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        });
                }).catch(function() {
                    that.$notify({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            stateFormatter:function(row, column) {
                if (row.adsState == "open"){
                    return "开启";
                } else{
                    return "关闭";
                }

            },
            //新增
            save: function (id) {
                if (id) {
                    location.href = this.manager + "/ad/ads/form.do?id=" + id;
                } else {
                    location.href = this.manager + "/ad/ads/form.do";
                }
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
            rest:function() {
                this.$refs.searchForm.resetFields();
                this.form.positionName=null;
                this.form.adsTime=[];
                this.list();
            },
            //获取广告位数据源
            positionNameOptionsGet:function(positionName) {
                var that = this;
                ms.http.get(ms.manager + "/ad/position/list.do", {positionName:positionName}).then(function(data) {
                    that.positionOptions = data.data.rows;
                }).catch(function(err) {
                    console.log(err);
                });
            },
            //获取adsType数据源
            adsTypeOptionsGet:function() {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {dictType: '广告类型', pageSize: 99999}).then(function (data) {
                    if(data.result){
                        data = data.data;
                        that.adsTypeOptions = data.rows;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
        },
        created:function() {
            this.adsTypeOptionsGet();
            this.positionNameOptionsGet();
            this.list();
        },
    })
</script>
<style>
    #index .ms-search {
        padding: 20px 0 0;
    }
</style>