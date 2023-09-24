<!DOCTYPE html>
<html>
<head>
    <title>场景二维码管理</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="mweixin:qrCode:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="mweixin:qrCode:del">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"
                           :disabled="!selectionList.length">删除
                </el-button>
            </@shiro.hasPermission>
        </el-col>
    </el-header>

    <div class="ms-search">
        <el-row>
            <el-form :model="form" ref="searchForm" label-width="120px" size="mini">
                <el-row>
                    <el-col :span="6">
                        <!--场景值id(名称)-->
                        <el-form-item  label="场景值id(名称)" prop="qcSceneStr">
                            <el-input
                                    v-model="form.qcSceneStr"
                                    :disabled="false"
                                    :readonly="false"
                                    :style="{width:  '100%'}"
                                    :clearable="true"
                                    placeholder="请输入场景值id(名称)">
                            </el-input>
                        </el-form-item>
                    </el-col>
					<el-col :span="6">
						<!--二维码类型-->
						<el-form-item label="二维码类型" prop="qcActionName">
							<el-select v-model="form.qcActionName"
									   :style="{width: ''}"
									   :disabled="false">
								<el-option v-for='item in qcActionNameOptions' :key="item.value" :value="item.value"
										   :label="item.label"></el-option>
							</el-select>
						</el-form-item>

					</el-col>
                    <el-col :span="6">
                        <!--二维码有效期-->
                        <el-form-item label="二维码有效期" prop="qcExpireSeconds">
                            <el-input
                                    v-model="form.qcExpireSeconds"
                                    :disabled="false"
                                    :readonly="false"
                                    :style="{width:  '100%'}"
                                    :clearable="true"
                                    placeholder="请输入二维码有效期，以秒为单位。">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6" style="text-align: right;">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="currentPage=1;list(true)">
                            搜索
                        </el-button>
                        <el-button type="primary" icon="iconfont icon-shaixuan1" size="mini"
                                   @click="currentPage=1;$refs.search.open()">筛选
                        </el-button>
                        <el-button @click="rest" icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-main class="ms-container">
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList"
                  tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column type="selection" width="40" :selectable="isChecked"></el-table-column>
            <el-table-column width="320" label="场景值id(名称)" align="left" prop="qcSceneStr">
            </el-table-column>
            <el-table-column label="二维码类型" align="left" prop="qcActionName" :formatter="qcActionNameFormat">
            </el-table-column>
            <el-table-column label="二维码有效期" align="left" prop="qcExpireSeconds">
            </el-table-column>
            <el-table-column width="320" label="实现类bean名称"   align="left" prop="qcBeanName">
            </el-table-column>
            <el-table-column label="操作" width="240" align="center">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="mweixin:qrCode:update">
                        <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="mweixin:qrCode:del">
                        <el-link type="primary" :underline="false" @click="del([scope.row])" v-if="scope.row.del!=3">
                            删除
                        </el-link>
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
                //场景值id(名称)
                {
                    'action': 'and',
                    'field': 'QR_CODE_SCENE_STR',
                    'el': 'eq',
                    'model': 'qcSceneStr',
                    'name': '场景值id(名称)',
                    'type': 'input'
                },
                //二维码类型
                {
                    'action': 'and',
                    'field': 'QR_CODE_ACTION_NAME',
                    'el': 'eq',
                    'model': 'qcActionName',
                    'name': '二维码类型',
                    'key': 'value',
                    'title': 'label',
                    'type': 'radio',
                    'multiple': false
                },
                //二维码有效期
                {
                    'action': 'and',
                    'field': 'QR_CODE_EXPIRE_SECONDS',
                    'el': 'eq',
                    'model': 'qcExpireSeconds',
                    'name': '二维码有效期',
                    'type': 'input'
                },
            ],
            conditions: [],
            dataList: [], //场景二维码管理列表
            selectionList: [],//场景二维码管理列表选中
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage: 1, //初始页
            manager: ms.manager,
            loading: true,//加载状态
            emptyText: '',//提示文字
            // 二维码类型
            qcActionNameOptions: [{"value": "QR_STR_SCENE", "label": "临时"}, {
                "value": "QR_LIMIT_STR_SCENE",
                "label": "永久"
            }],
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
                var page = {
                    pageNo: that.currentPage,
                    pageSize: that.pageSize
                }
                var form = JSON.parse(JSON.stringify(that.form))


                if (isSearch) {
                    //删除空字符串
                    for (var key in form) {
                        if (form[key] === undefined || form[key] === null) {
                            delete form[key]
                        }
                    }
                    form.sqlWhere ? data = Object.assign({}, {sqlWhere: form.sqlWhere}, page) : data = Object.assign({}, form, page)
                } else {
                    data = page;
                }

                history.replaceState({form: that.form, page: page}, "");
                ms.http.post(ms.manager + "/mweixin/qrCode/list.do", data).then(
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
            //场景二维码管理列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm("此操作将永久删除所选内容, 是否继续", "提示", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/mweixin/qrCode/delete.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: '成功',
                                    type: 'success',
                                    message: "删除成功"
                                });
                                //删除成功，刷新列表
                                that.list();
                            } else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        });
                }).catch(function (err) {
                    //删除如果用户取消会抛出异常，所以需要catch一下
                    console.log(err)
                });
            },
            //新增
            save: function (id) {
                if (id) {
                    location.href = this.manager + "/mweixin/qrCode/form.do?id=" + id;
                } else {
                    location.href = this.manager + "/mweixin/qrCode/form.do";
                }
            },

            //pageSize改变时会触发
            sizeChange: function (pagesize) {
                this.loading = true;
                this.pageSize = pagesize;
                this.list(true);
            },
            //currentPage改变时会触发
            currentChange: function (currentPage) {
                this.loading = true;
                this.currentPage = currentPage;
                this.list(true);
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

            //二维码类型  列表格式化
            qcActionNameFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.qcActionNameOptions.find(function (value) {
                        return value.value == cellValue;
                    })
                    if (data && data.label) {
                        value = data.label;
                    }
                }
                return value;
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
        height: calc(100vh - 141px);
    }
</style>
