<!DOCTYPE html>
<html>
<head>
    <title>员工</title>
    <#include "../../include/head-file.ftl">

</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="50px">
        <el-col :span="12">
            <@shiro.hasPermission name="organization:employee:save">
                <el-button type="primary" icon="el-icon-plus" size="mini" @click="save()">新增</el-button>
            </@shiro.hasPermission>
            <@shiro.hasPermission name="organization:employee:del">
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
                    <el-col :span="8">
                        <el-form-item label="姓名:" prop="employeeNickName" size="mini" >
                            <el-input placeholder="请输入姓名" v-model="form.employeeNickName" :clearable="true">

                            </el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="岗位" prop="postIds"
                        >
                            <el-select v-model="form.postIds"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="false"
                                       :clearable="true"
                                       placeholder="请选择岗位">
                                <el-option v-for='item in postIdsOptions' :key="item.id" :value="item.id"
                                           :label="item.postName"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="手机号" prop="employeePhone"
                        >
                            <el-input v-model="form.employeePhone"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入手机号">
                            </el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>

                    <el-col :span="8">
                        <el-form-item label="员工状态" prop="employeeStatus"
                        >
                            <el-select v-model="form.employeeStatus"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="false"
                                       :multiple="false" :clearable="true"
                                       placeholder="请选择员工状态">
                                <el-option v-for='item in employeeStatusOptions' :key="item.value" :value="item.value"
                                           :label="item.label"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16" style="text-align: right;">
                        <el-button type="primary" icon="el-icon-search" size="mini"
                                   @click="form.sqlWhere=null;currentPage=1;list()">查询
                        </el-button>
                        <#--                        <el-button type="primary" icon="iconfont icon-shaixuan1" size="mini" @click="$refs.search.open()">筛选-->
                        <#--                        </el-button>-->
                        <el-button @click="rest" icon="el-icon-refresh" size="mini">重置</el-button>
                    </el-col>

                </el-row>
            </el-form>
        </el-row>
    </div>
    <el-alert
            class="ms-alert-tip"
            title="功能介绍"
            type="info">
        <template slot="title">
            功能介绍 <a href='http://doc.mingsoft.net/plugs/zhu-zhi-ji-gou/jie-shao.html' target="_blank">开发手册</a>
        </template>
        员工需要设置账号才能登录,设置数据权限,同时员工账号设置为离职状态会将账号进行锁定 </br>

    </el-alert>
    <el-main class="ms-container" style="flex-direction: row">

        <div style="background-color: rgb(242, 246, 252);overflow: hidden;   display: flex;position: relative;margin-right: 10px;">
            <el-scrollbar style="width: 160px;">
                <el-aside width=null style="overflow: hidden;display: inline">
                    <el-tree :data="organizationIdTreeDatas"
                             ref="tree"
                             empty-text=""
                             :highlight-current="true"
                             default-expand-all="true"
                             node-key="id"
                             indent="10"
                             :props="{
                                      label: 'organizationTitle',
                                       value: 'id',
                                      children: 'children'
                                    }"
                             :check-strictly="true"
                             style="display: inline-block;width:100%;"
                             @node-click="handleNodeClick">
                        <div class="custom-tree-node" slot-scope="scope">
                            {{scope.data.organizationTitle}}
                        </div>
                    </el-tree>
            </el-scrollbar>
            </el-aside>
            </el-scrollbar>
        </div>
        <div style="display: flex;flex-direction: column;flex: 1;">
            <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList"
                      tooltip-effect="dark" @selection-change="handleSelectionChange">
                <template slot="empty">
                    {{emptyText}}
                </template>
                <el-table-column type="selection" width="40"></el-table-column>
                <el-table-column label="员工编号" align="left" prop="employeeCode" show-overflow-tooltip>
                </el-table-column>
                <el-table-column width="120" label="姓名" align="left" prop="employeeNickName" show-overflow-tooltip>
                </el-table-column>
                <el-table-column width="120" label="员工状态" align="center" prop="employeeStatus" min-width="100"
                                 :formatter="employeeStatusFormat">
                </el-table-column>
                <el-table-column width="120" label="岗位" align="left" prop="postIds" :formatter="postIdsFormat" show-overflow-tooltip>
                </el-table-column>
                <el-table-column label="所属部门" align="left" prop="organizationId" :formatter="organizationIdFormat" show-overflow-tooltip>
                </el-table-column>
                <el-table-column width="120" label="手机号" align="center" prop="employeePhone" show-overflow-tooltip>
                </el-table-column>
                <el-table-column width="120" label="性别" align="center" prop="employeeSex" :formatter="employeeSexFormat">
                </el-table-column>

                <el-table-column label="操作"  width="300" align="center">
                    <template slot='header'>
                        操作
                        <el-popover placement="top-start" title="提示" trigger="hover">
                            给员工设置账号后可以分配组织权限和内容权限;管理权限可以控制数据，例如：部门一的员工只能看到隶属部门一的业务数据,部门二可以看到部门一和部门二所属的人员发的文章
                            <el-link href="http://doc.mingsoft.net/plugs/zhu-zhi-ji-gou/ye-wu-kai-fa.html" type="primary"
                                     target="_blank">业务开发
                            </el-link>
                            <i class="el-icon-question" slot="reference"></i>
                        </el-popover>
                    </template>
                    <template slot-scope="scope">
                        <@shiro.hasPermission name="organization:employee:update">
                            <el-link type="primary" :underline="false" @click="save(scope.row.id)">编辑</el-link>
                        </@shiro.hasPermission>
                        <@shiro.hasPermission name="organization:employee:update">
                            <el-link type="primary" :underline="false" @click="setUpAccount(scope.row)">设置账号</el-link>
                        </@shiro.hasPermission>

                        <@shiro.hasPermission name="organization:employee:authorization">
                            <el-link v-if="scope.row.managerId != null"  type="primary" :underline="false" @click="scopeDate.dataType = '组织机构员工部门权限';openScope(scope.row.id)">
                                组织权限
                            </el-link>
                        </@shiro.hasPermission>
                        <@shiro.hasPermission name="organization:employee:authorization">
                            <el-link v-if="scope.row.managerId != null"  type="primary" :underline="false" @click="scopeDate.dataType = '组织机构员工内容权限';openScope(scope.row.id);">
                                内容权限
                            </el-link>
                        </@shiro.hasPermission>
                        <@shiro.hasPermission name="organization:employee:del">
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
        </div>

    </el-main>
    <el-dialog
            :close-on-click-modal="false"
            title="数据权限"
            :visible.sync="scopeDialogVisible"
            width="50%">
        <div slot="title" style="font-size: 20px">
            <span v-show="scopeDate.dataType">{{scopeDate.dataType=='组织机构员工部门权限'?'组织机构员工部门权限':'组织机构员工内容权限'}}</span>
        </div>
        <el-alert
                type="warning"
                class="ms-alert-tip"
                :closable="false">
            {{scopeDate.dataType!='组织机构员工内容权限'?'设置组织权限后，员工只能看到当前勾选权限下所有员工数据及部门数据'
            :'设置内容权限后，员工只能看到分配部门权限下所有员工发布的文章数据，包括新增修改等操作 '}}</br>
            此处对权限做了细分，支持单选父节点，若希望获得某公司下所有部门权限（包括该公司），除勾选该公司外还需勾选其所有子部门
        </el-alert>
        <el-scrollbar style="height: 350px;">
            <el-tree
                    ref="tree"
                    :data="organizationIdTreeDatas"
                    :props="{
                      label: 'organizationTitle',
                       value: 'id',
                      children: 'children'
                    }"
                    node-key="id"
                    :default-expand-all="true"
                    :check-strictly="true"
                    show-checkbox>
            </el-tree>
        </el-scrollbar>
        <span slot="footer" class="dialog-footer">
    <el-button size="mini" @click="scopeDialogVisible = false">取 消</el-button>
    <el-button size="mini" type="primary" @click="saveScope">确 定</el-button>
  </span>
    </el-dialog>
</div>
<#include "/organization/employee/form.ftl">
<#include "/basic/manager/form.ftl">
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: {
            conditionList: [
                {action: 'and', field: 'employee_code', el: 'eq', model: 'employeeCode', name: '员工编号', type: 'input'},
                {
                    action: 'and',
                    field: 'employee_status',
                    el: 'eq',
                    model: 'employeeStatus',
                    name: '员工状态',
                    key: 'value',
                    title: 'label',
                    type: 'select',
                    multiple: false
                },
                {
                    action: 'and',
                    field: 'employee_sex',
                    el: 'eq',
                    model: 'employeeSex',
                    name: '性别',
                    key: 'value',
                    title: 'label',
                    type: 'select',
                    multiple: false
                },
                {
                    action: 'and',
                    field: 'employee_role',
                    el: 'eq',
                    model: 'employeeRole',
                    name: '所属角色',
                    key: 'roleId',
                    title: 'roleName',
                    type: 'select',
                    multiple: true
                },
                {
                    action: 'and',
                    field: 'post_ids',
                    el: 'eq',
                    model: 'postIds',
                    name: '岗位',
                    key: 'id',
                    title: 'postName',
                    type: 'select',
                    multiple: true
                },
                {
                    action: 'and',
                    field: 'employeePolitics',
                    el: 'eq',
                    model: 'employeePolitics',
                    name: '政治面貌',
                    key: 'value',
                    title: 'value',
                    type: 'select',
                    multiple: false
                },
                {
                    action: 'and',
                    field: 'employeeEducation',
                    el: 'eq',
                    model: 'employeeEducation',
                    name: '员工学历',
                    key: 'value',
                    title: 'value',
                    type: 'select',
                    multiple: false
                },
                {action: 'and', field: 'age', el: 'eq', model: 'age', name: '年龄', type: 'number'},
                {action: 'and', field: 'phone', el: 'eq', model: 'phone', name: '手机号', type: 'input'},
                {action: 'and', field: 'manager_id', el: 'eq', model: 'managerId', name: '管理员id', type: 'number'},
                {action: 'and', field: 'create_date', el: 'eq', model: 'createDate', name: '创建时间', type: 'date'},
                {action: 'and', field: 'update_date', el: 'eq', model: 'updateDate', name: '修改时间', type: 'date'},
            ],
            conditions: [],
            dataList: [], //员工列表
            selectionList: [],//员工列表选中
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage: 1, //初始页
            manager: ms.manager,
            loadState: false,
            scopeDialogVisible: false,
            loading: true,//加载状态
            emptyText: '',//提示文字
            employeeStatusOptions: [{"value": "in", "label": "在职"}, {"value": "try", "label": "试用"}, {
                "value": "out",
                "label": "离职"
            }],
            employeeSexOptions: [{"value": "1", "label": "男"}, {"value": "2", "label": "女"}],
            employeeRoleOptions: [],
            postIdsOptions: [],
            organizationIdOptions: [],
            organizationIdProps: {
                "emitPath": false,
                "checkStrictly": true,
                "value": "id",
                "label": "organizationTitle",
                "expandTrigger": "hover"
            },
            employeePoliticsOptions: [{"value": "党员"}, {"value": "团员"}, {"value": "群众"}],
            employeeEducationOptions: [{"value": "中专"}, {"value": "高中"}, {"value": "大专"}, {"value": "本科"}, {"value": "硕士研究生"}, {"value": "博士研究生"}, {"value": "博士后"}],
            scopeDate: {
                employeeId: 0
            },
            //搜索表单
            form: {
                sqlWhere: null,
                // 员工状态
                employeeStatus: null,
                // 岗位
                postIds: null,
                // 所属部门
                organizationId: null,
                // 手机号
                employeePhone: null,
                employeeNickName: "",
                managerName: null,

            },
        },
        watch: {
            scopeDialogVisible: function (n, o) {
                if (!n) {
                    this.scopeDate = {}
                    this.$refs.tree.setCheckedKeys([])
                }
            },
        },
        computed: {
            organizationIdTreeDatas: function () {
                return ms.util.treeData(this.organizationIdOptions, 'id', 'organizationId', 'children');
            },
        },
        methods: {
            //新增管理员
            setUpAccount: function (row) {
                form.form.employeeId = row.id;
                form.form.managerNickName = row.employeeNickName;
                form.open(row.managerId);
            },
            //打开数据权限表单回显数据函数
            openScope: function (id) {
                var that = this
                this.scopeDate.employeeId = id
                // this.scopeDate.dataType = "employeeOrganization"
                ms.http.get(ms.manager + "/organization/employeeData/get.do", this.scopeDate).then(
                    function (res) {
                        if (res.result) {
                            that.scopeDialogVisible = true;
                            if (res.data) {
                                that.scopeDate.id = res.data.id
                                var ids = res.data.organizationIds
                                var keys = []
                                if (ids) {
                                  // 只保留根节点
                                  ids.split(',').map(function(item){
                                    var node = that.$refs.tree.getNode(item)
                                    // if(!node.childNodes || node.childNodes.length === 0){/
                                      keys.push(Number(node.data.id))
                                    // }
                                  })
                                  // 勾选根节点
                                    that.$nextTick(function () {
                                        that.$refs.tree.setCheckedKeys(keys)
                                    })
                                }
                            }
                        }
                    })
            },
            //保存数据权限函数
            saveScope: function () {
                var that = this
                // 已勾选节点和半选节点
                var list = this.$refs.tree.getCheckedNodes(false,true)
                list.map(function(item){
                  return item.id
                })
                this.scopeDate.organizationIds = list.map(function(item){
                                                  return item.id
                                                 }).join(",")
                // this.scopeDate.dataType = "employeeOrganization";
                // this.scopeDate.subSql = "SELECT id as data_id FROM cms_content WHERE organization_id IN({})";
                ms.http.post(ms.manager + "/organization/employeeData/save.do", this.scopeDate).then(
                    function (res) {
                        if (res.result) {
                            that.$notify({
                                title: '成功',
                                message: '保存成功',
                                type: 'success'
                            });
                            that.scopeDialogVisible = false;
                        } else {
                            that.$notify({
                                title: '失败',
                                message: data.msg,
                                type: 'warning'
                            });
                        }
                    })
            },
            //查询列表
            list: function () {
                var that = this;
                that.loading = true;
                that.loadState = false;
                var page = {
                    pageNo: that.currentPage,
                    pageSize: that.pageSize
                }
                var form = JSON.parse(JSON.stringify(that.form))
                for (var key in form) {
                    if (!form[key]) {
                        delete form[key]
                    }
                }
                history.replaceState({form: form, page: page}, "");
                ms.http.post(ms.manager + "/organization/employee/list.do", form.sqlWhere ? Object.assign({sqlWhere: form.sqlWhere},
                    page)
                    : Object.assign(form,
                        page
                    )).then(
                    function (res) {
                        if (that.loadState) {
                            that.loading = false;
                        } else {
                            that.loadState = true
                        }
                        if (!res.result || !res.data) {
                            that.emptyText = '暂无数据'
                            that.dataList = [];
                            that.total = 0;
                        } else {
                            that.emptyText = '';
                            that.total = res.data.total;
                            that.dataList = res.data.rows;
                        }
                    }).catch(function (err) {
                    console.log(err);
                });
                setTimeout(function () {
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true
                    }
                }, 500);
            },
            //员工列表选中
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
                    ms.http.post(ms.manager + "/organization/employee/delete.do", row.length ? row : [row], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: '成功',
                                    type: 'success',
                                    message: '删除成功!'
                                });
                                //删除成功，刷新列表
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
            //新增或编辑
            save: function (id) {
              // 编辑
              if( id ) {
                empform.open(id);
              } else { // 新增
                empform.open(id,this.form.organizationId);
              }
                this.employeeRoleOptionsGet();
                this.postIdsOptionsGet();
                this.organizationIdOptionsGet();
            },
            //表格数据转换
            employeeStatusFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.employeeStatusOptions.find(function (value) {
                        return value.value == cellValue;
                    })
                    if (data && data.label) {
                        value = data.label;
                    }
                }
                return value;
            },
            employeeSexFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.employeeSexOptions.find(function (value) {
                        return value.value == cellValue;
                    })
                    if (data && data.label) {
                        value = data.label;
                    }
                }
                return value;
            },
            employeeRoleFormat: function (row, column, cellValue, index) {
                var value = "";
                var that = this
                var con = []
                if (cellValue) {
                    cellValue.split(',').forEach(function (item) {
                        var data = that.employeeRoleOptions.find(function (value) {
                            return value.roleId == item;
                        })
                        if (data && data.roleName) {
                            con.push(data.roleName);
                        }
                    })
                }
                value = con.join(',');
                return value;
            },
            postIdsFormat: function (row, column, cellValue, index) {
                var value = "";
                var that = this
                var con = []
                if (cellValue) {
                    cellValue.split(',').forEach(function (item) {
                        var data = that.postIdsOptions.find(function (value) {
                            return value.id == item;
                        })
                        if (data && data.postName) {
                            con.push(data.postName);
                        }
                    })
                }
                value = con.join(',');
                return value;
            },
            organizationIdFormat: function (row, column, cellValue, index) {
                var value = "";
                if (cellValue) {
                    var data = this.organizationIdOptions.find(function (value) {
                        return value.id == cellValue;
                    })
                    if (data && data.organizationTitle) {
                        if (data.organizationParentId) {
                            var pIds = data.organizationParentId.split(",");
                            for (var i = 0; i < pIds.length; i++) {
                                for (var key in this.organizationIdOptions) {
                                    if (this.organizationIdOptions[key].id == pIds[i]) {
                                        value += this.organizationIdOptions[key].organizationTitle + "/";
                                        break;
                                    }
                                }
                            }
                        }
                        value += data.organizationTitle;
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
                this.currentPage = 1;
                this.form.sqlWhere = null;
                this.$refs.searchForm.resetFields();
                this.$refs.tree.setCurrentKey(null);
                this.form.organizationId = null;
                this.list();
            },
            //获取employeeRole数据源
            employeeRoleOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/basic/role/list.do", {
                    pageSize: 999999
                }).then(function (res) {
                    that.employeeRoleOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取postIds数据源
            postIdsOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/organization/post/list.do", {pageSize: 9999}).then(function (res) {
                    that.postIdsOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取organizationId数据源
            organizationIdOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/organization/organization/list.do", {
                    organizationStatus: 'normal',
                    pageSize: 99999
                }).then(function (res) {
                    that.organizationIdOptions = res.data.rows;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            handleNodeClick: function (data, node, self) {
                var that = this;
                that.form.sqlWhere = null;
                that.form.organizationId = data.id;
                that.currentPage = 1;
                that.list();
            },
        },
        created: function () {
            this.employeeRoleOptionsGet();
            this.postIdsOptionsGet();
            this.organizationIdOptionsGet();
            if (history.hasOwnProperty("state")) {
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

    #index .el-tree {
        background: none !important;
        margin-top: 4px;
    }

    #index .ms-container .el-tree-node__content {
        height: 32px;
        margin-top: 6px;
        margin-bottom: 6px;
    }
    #index .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content{
        background: #409EFF !important;
        color: white;
    }
    .el-dialog__body .el-scrollbar__wrap {
        overflow-x: hidden;
    }
</style>
