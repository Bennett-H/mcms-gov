<!DOCTYPE html>
<html>
<head>
    <title>字典表</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" v-cloak class="ms-index">

    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="mdiy:dict:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="mdiy:dict:del">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="del(selectionList)"
                           :disabled="!selectionList.length">删除
                </el-button>
            </@shiro.hasPermission>

        </el-col>
        <el-col :span="12">

            <@shiro.hasPermission name="mdiy:dict:importJson">
                <el-button type="primary" icon="iconfont icon-daoru" size="mini" @click="dialogImportVisible=true"
                           style="float: right;">导入
                </el-button>
            </@shiro.hasPermission>

            <el-tooltip class="item" effect="dark" content="如果字典数据有修改，数据获取不正确，尝试刷新缓存" placement="bottom">
                <el-button type="success" plain style="float: right;margin-right: 8px "
                           icon="el-icon-refresh-left" size="mini"
                           @click="updateCache()">刷新缓存
                </el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="快速复制当前字典类型导入另一套系统的字典中" placement="bottom">
                <el-button type="primary" plain
                           icon="el-icon-document-copy" size="mini" class="copyBtn" style="float: right; margin-right: 8px"
                           :data-clipboard-text="JSON.stringify(treeList)" @click="copyJson()">复制字典JSON
                </el-button>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="复制当前字典菜单，导入到对应的菜单，管理字典更方便" placement="bottom">
                <el-button type="primary" plain
                           icon="el-icon-document-copy" size="mini" class="copyBtn" style="float: right; margin-right: 8px"
                           :data-clipboard-text="JSON.stringify(menuJson)" @click="copyMenuJson()">复制菜单JSON
                </el-button>
            </el-tooltip>
        </el-col>

    </el-header>

    <el-dialog title="导入自定义字典json" :visible.sync="dialogImportVisible" width="600px" :close-on-press-escape="false" :close-on-click-modal="false" append-to-body v-cloak>
        <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-form ref="form" :model="impForm" :rules="rules" size="mini" label-width="130px"  label-position="top" >
                <el-form-item prop="modelJson">
                    <span slot='label'>自定义字典json</span>
                    <el-input :rows="10" type="textarea" v-model="impForm.modelJson"
                              placeholder="请粘贴来自自定义字典的json"></el-input>
                    <div class="ms-form-tip">
                        粘贴来自<b>自定义字典</b> 的<b>复制字典JSON</b>数据
                    </div>
                </el-form-item>

            </el-form>

        </el-scrollbar>
        <div slot="footer">
            <el-button size="mini" @click="dialogImportVisible = false">取 消</el-button>
            <el-button size="mini" type="primary" @click="imputJson()">确 定</el-button>
        </div>
    </el-dialog>


    <el-main class="ms-container">
        <div>
            <el-alert
                    class="ms-alert-tip"
                    title="功能介绍"
                    type="info"
                    description="字典可以用来实现下拉或单选、多选数据源，例如：文章属性、用户等级、订单状态">
                <template slot="title">
                    功能介绍 <a href='http://doc.mingsoft.net/plugs/zi-ding-yi-cha-jian/ye-wu-kai-fa/zi-ding-yi-zi-dian.html'
                            target="_blank">开发手册</a>
                </template>
            </el-alert>
        </div>

        <div style="display:flex;flex-direction: row;flex:1">
            <div style="overflow: hidden; background-color: rgb(242, 246, 252);  display: flex;position: relative;margin-right: 10px;">
                <el-scrollbar style="height: calc(100vh - 145px)">
                    <el-aside width="160px" style="background-color: #f2f6fc;">

                        <el-menu
                                :default-active="defaultActive"
                                v-if="dictTypeOptions && dictTypeOptions.length">
                            <template v-for="(item,i) in dictTypeOptions">
                                <!--单个选项-->
                                <el-menu-item
                                        :index="item.dictType"
                                        @click="list(item.dictType)"
                                        v-text="item.dictType">
                                </el-menu-item>
                            </template>
                        </el-menu>
                    </el-aside>
                </el-scrollbar>
            </div>
            <div style="display: flex;flex-direction: column;flex: 1;">
                <el-table v-loading="loading" ref="multipleTable" height="calc(100% - 68px)" class="ms-table-pagination"
                          border
                          :data="treeList" tooltip-effect="dark" @selection-change="handleSelectionChange">
                    <template slot="empty">
                        {{emptyText}}
                    </template>
                    <el-table-column type="selection" :selectable="isChecked" width="40"></el-table-column>
                    <el-table-column label="名称" align="left" prop="dictLabel" show-overflow-tooltip>
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
                    <@shiro.hasPermission name="mdiy:dict:update">
                        <el-table-column label="启用状态" align="right" width="100" prop="dictSort">
                            <template slot-scope="scope">
                                <el-switch v-model="scope.row.dictEnable"
                                           @change="update(scope.$index)">
                                </el-switch>
                            </template>
                        </el-table-column>
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="mdiy:dict:update">
                        <el-table-column label="操作" fixed="right" align="center" width="180">
                            <template slot-scope="scope">
                                <@shiro.hasPermission name="mdiy:dict:update">
                                    <el-link :underline="false" type="primary" size="medium"
                                             @click="save(scope.row.id)">编辑
                                    </el-link>
                                </@shiro.hasPermission>
                                <@shiro.hasPermission name="mdiy:dict:del">
                                    <el-link :underline="false" v-if="scope.row.notDel == 0" type="primary"
                                             @click="del([scope.row])">删除
                                    </el-link>
                                </@shiro.hasPermission>
                            </template>
                        </el-table-column>
                    </@shiro.hasPermission>
                </el-table>
                <el-pagination
                        background
                        :page-sizes="[5, 10, 20, 50, 100]"
                        layout="total, sizes, prev, pager, next, jumper"
                        :current-page.sync="currentPage"
                        :page-size="pageSize"
                        :total="total"
                        class="ms-pagination"
                        @current-change='currentChange'
                        @size-change="sizeChange">
                </el-pagination>
            </div>
        </div>
    </el-main>
</div>

</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: function () {
            return {
                dict: ms.dict,
                conditionList: [
                    {action: 'and', field: 'dict_label', el: 'eq', model: 'dictLabel', name: '标签名', type: 'input'},
                    {
                        action: 'and',
                        field: 'dict_type',
                        el: 'eq',
                        model: 'dictType',
                        name: '类型',
                        key: 'dictType',
                        title: 'dictType',
                        type: 'select',
                        multiple: false
                    },
                    {action: 'and', field: 'dict_value', el: 'eq', model: 'dictValue', name: '数据值', type: 'input'},
                    {action: 'and', field: 'dict_sort', el: 'eq', model: 'dictSort', name: '排序', type: 'number'},
                    {action: 'and', field: 'is_child', el: 'eq', model: 'isChild', name: '系统扩展', type: 'input'},
                    {action: 'and', field: 'dict_enable', el: 'eq', model: 'dictEnable', name: '启用状态', type: 'switch'},
                    {
                        action: 'and',
                        field: 'dict_remarks',
                        el: 'eq',
                        model: 'dictRemarks',
                        name: '备注信息',
                        type: 'textarea'
                    },
                    {action: 'and', field: 'create_date', el: 'eq', model: 'createDate', name: '创建时间', type: 'date'},
                    {action: 'and', field: 'update_date', el: 'eq', model: 'updateDate', name: '修改时间', type: 'date'},
                ],
                dialogImportVisible: false,
                impForm: {
                    // 模型名称
                    modelJson: '',
                },
                //配置验证
                rules: {
                    modelJson: [{
                        required: true,
                        message: 'json数据不能为空',
                        trigger: 'blur'
                    }]
                },
                //菜单json
                menuJson: [
                    {
                        modelIsMenu: 1,
                        modelTitle: "",
                        modelUrl: "",
                        modelChildList: [
                            {
                                modelIsMenu: 0,
                                modelTitle: "设置",
                                modelUrl: "mdiy:dictData:settings"
                            }
                        ]
                    }
                ],
                conditions: [],
                treeList: [],
                //字典表列表
                selectionList: [],
                //字典表列表选中
                total: 0,
                //总记录数量
                pageSize: 20,
                //页面数量
                currentPage: 1,
                //初始页
                mananger: ms.manager,
                loading: true,
                emptyText: '',
                dictTypeOptions: [],
                selectDictOption: {},
                defaultActive: "",
                //搜索表单
                form: {
                    sqlWhere: null,
                    // 标签名
                    dictLabel: '',
                    // 类型
                    dictType: '',
                    // 数据值
                    dictValue: '',
                    // 子业务数据类型
                    isChild: '',
                    // 排序
                    dictSort: '',
                    // 备注信息
                    dictRemarks: '',
                    // 描述
                    dictDescription: ''
                }
            }
        },
        watch: {
            'dialogImportVisible': function (n, o) {
                if (!n) {
                    this.$refs.form.resetFields();
                    this.form.id = "";
                }
            },
        },
        methods: {

            //查询列表
            list: function (dictType) {
                var that = this;
                if (dictType) {
                    that.form.dictType = dictType;
                }
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
                    page: page,
                    total: that.total
                }, "");
                setTimeout(function () {
                    //后台默认查询条件启用状态为启用，为dictEnable添加参数接口则会查询全部
                    ms.http.post(ms.manager + "/mdiy/dict/list.do", form.sqlWhere ? Object.assign({dictEnable: true}, {sqlWhere: form.sqlWhere}, page)
                        : Object.assign({dictEnable: true}, form, page)).then(
                        function (data) {
                            if (data.result) {
                                if (data.data.total <= 0) {
                                    that.loading = false;
                                    that.emptyText = '暂无数据';
                                    that.treeList = [];
                                } else {
                                    that.emptyText = '';
                                    that.loading = false;
                                    that.total = data.data.total;
                                    that.treeList = data.data.rows;
                                }
                            }
                        });
                }, 500);
            },
            //字典表列表选中
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //删除
            del: function (row) {
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/mdiy/dict/delete.do", row.length ? row : [row], {
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
                            that.getDictType();
                        } else {
                            that.$notify({
                                title: '失败',
                                message: data.msg,
                                type: 'warning'
                            });
                        }
                    });
                });
            },
            //新增
            save: function (id) {
                var that = this;
                //判断当前类型是否存在，不存在默认选择第一个
                if (that.dictTypeOptions.length !== 0) {
                    var type = that.dictTypeOptions.map(function (item) {
                        return item.dictType
                    }).indexOf(that.form.dictType)
                    if (type == -1) {
                        that.form.dictType = that.dictTypeOptions[0].dictType;
                    }
                } else {
                    that.form.dictType = "";
                }
                if (id) {
                    // location.href = this.mananger + "/mdiy/dict/form.do?id=" + id;
                    ms.util.openSystemUrl("/mdiy/dict/form.do?id=" + id);
                } else {
                    //对uri进行编码
                    // location.href = encodeURI(this.mananger + "/mdiy/dict/form.do?dictType=" + that.form.dictType);
                    ms.util.openSystemUrl(encodeURI("/mdiy/dict/form.do?dictType=" + that.form.dictType));
                }
            },
            //更新状态
            update: function (index) {
                var that = this;
                ms.http.post(ms.manager + "/mdiy/dict/update.do", that.treeList[index]).then(function (data) {
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
            //导入json
            imputJson: function () {
                var that = this;
                var url = "/mdiy/dict/importJson.do";
                if (that.impForm.id && that.impForm.id != '') {
                    url = "/mdiy/dict/updateJson.do";
                }

                that.$confirm('此操作会进行更新表操作，请谨慎操作', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    // 控制前端接口isWebSubmit，保存至json
                    that.$refs.form.validate(function (valid) {
                        try {
                            JSON.parse(that.impForm.modelJson);
                        } catch (e) {
                            that.$notify({
                                title: '失败',
                                message: "json格式不匹配",
                                type: 'warning'
                            });
                        };
                        if (valid) {
                            ms.http.post(ms.manager + url, JSON.parse(that.impForm.modelJson),
                                {
                                    headers: {
                                        'Content-Type': 'application/json'
                                    }
                                }
                            ).then(function (data) {
                                if (data.result) {
                                    that.dialogImportVisible = false;
                                    that.getDictType();
                                    that.$notify({
                                        title: '成功',
                                        message: "导入成功",
                                        type: 'success'
                                    });
                                } else {
                                    that.$notify({
                                        title: '失败',
                                        message: data.msg,
                                        type: 'warning'
                                    });
                                }
                            })
                        }
                        that.updateCache();
                    });
                });
            },
            //复制字典json
            copyJson: function () {
                var clipboard = new ClipboardJS('.copyBtn');
                var self = this;
                clipboard.on('success', function (e) {
                    self.$notify({
                        title: '提示',
                        message: '字典json已保存到剪切板，可在字典管理中导入',
                        type: 'success'
                    });
                    clipboard.destroy();
                });
            },
            //复制菜单json
            copyMenuJson: function () {
                var dictType = this.treeList[0].dictType
                this.menuJson[0].modelChildList[0].modelUrl = 'mdiy:dictData:'+dictType+':settings'
                this.menuJson[0].modelTitle=dictType;
                this.menuJson[0].modelUrl="mdiy/dict/data/index.do?dictType="+dictType;

                var clipboard = new ClipboardJS('.copyBtn');
                var self = this;
                clipboard.on('success', function (e) {
                    self.$notify({
                        title: '提示',
                        message: '菜单json已保存到剪切板，可在菜单管理中导入',
                        type: 'success'
                    });
                    clipboard.destroy();
                });
            },
            //刷新缓存
            updateCache: function() {
                var that = this;
                ms.http.post(ms.manager + "/mdiy/dict/updateCache.do").then(function (data) {
                    if (data.result) {
                        that.$notify({
                            title: '成功',
                            type: 'success',
                            message: '刷新成功!'
                        });
                        //跳转历史导航栏
                        var dictTypeParam = ms.util.getParameter("dictType");
                        that.defaultActive = that.form.dictType = dictTypeParam == null && that.dictTypeOptions.length > 0 ? String(that.dictTypeOptions[0].dictType) : String(dictTypeParam);
                        that.list(that.defaultActive);
                    } else {
                        that.$notify({
                            title: '失败',
                            message: data.msg,
                            type: 'warning'
                        });
                    }
                });

            },
            isChecked: function (row) {
                return row.notDel == 0
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
            getDictType: function (isChild) {
                var that = this;
                that.form.dictType = '';
                ms.http.get(ms.manager + "/mdiy/dict/dictType.do?pageSize=999", isChild ? {
                    "isChild": isChild
                } : null).then(function (data) {
                    if (data.result) {
                        that.dictTypeOptions = data.data.rows;
                        //跳转历史导航栏
                        var dictTypeParam = ms.util.getParameter("dictType");
                        var defaultActive = that.form.dictType = dictTypeParam == null && that.dictTypeOptions.length > 0 ? String(that.dictTypeOptions[0].dictType) : String(dictTypeParam);
                        that.defaultActive = defaultActive;
                        that.list(defaultActive);
                    }
                });
            },
            //重置表单
            rest: function () {
                this.currentPage = 1;
                this.loading = true;
                this.form.sqlWhere = null;
                this.$refs.searchForm.resetFields();
                this.list();
            }
        },
        created: function () {
            if (history.hasOwnProperty("state") && history.state) {
                this.form = history.state.form;
                this.total = history.state.total;
                this.currentPage = history.state.page.pageNo;
                this.pageSize = history.state.page.pageSize;
            }
            this.getDictType();
        }
    });
</script>
<style>
    .el-main {
        overflow: unset;
    }
</style>
