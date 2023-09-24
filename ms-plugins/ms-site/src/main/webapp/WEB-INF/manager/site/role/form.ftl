<!DOCTYPE html>
<html>
<head>
    <title>角色管理</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="form" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
        </el-button>
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container" style="flex-direction: row">
    <div style="background-color: rgb(242, 246, 252);overflow: hidden;   display: flex;position: relative;margin-right: 10px;">
            <el-scrollbar style="width: 160px;">
                <el-aside width=null style="overflow: hidden;display: inline">
                    <el-tree :data="roleIdOptions"
                    empty-text=""
                             ref="tree"
                             :highlight-current="true"
                             default-expand-all="true"
                             node-key="id"
                             indent="10"
                             :props="{
                                      label: 'roleName',
                                      value: 'id',
                                      children: 'children'
                                    }"
                             :check-strictly="true"
                             style="display: inline-block;width:100%;"
                             @node-click="handleNodeClick">
                        <div class="custom-tree-node" slot-scope="scope">
                            <i v-if="scope.data.id==0" class="el-icon-menu" style="margin-right:4px;"></i>{{scope.data.roleName}}
                        </div>
                    </el-tree>
            </el-scrollbar>
            </el-aside>
            </el-scrollbar>
        </div>
        <div style="display: flex;flex-direction: column;flex: 1;">
            <el-table height="calc(100vh - 68px)" ref="multipleTable" border :data="filterManagerOptions"
                      tooltip-effect="dark" @selection-change="handleSelectionChange" :row-key="function(row){return row.id}">
                <el-table-column type="selection" width="40" reserve-selection="true"></el-table-column>
                <el-table-column label="管理员账号" align="left" prop="managerName" show-overflow-tooltip>
                </el-table-column>
                <el-table-column label="管理员名称" align="left" prop="managerNickName" show-overflow-tooltip>
                </el-table-column>
                <el-table-column  label="所属角色" align="left" prop="roleNames" show-overflow-tooltip>
                </el-table-column>
            </el-table>
        </div>
    </el-main>
</div>
</body>
</html>
<script>
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                saveDisabled: false,
                selectionList: [],//管理员列表选中
                loading: true,//加载状态
                emptyText: '',//提示文字
                roleIdOptions: [],
                //表单数据
                form: {
                    // 角色名称
                    managerIds: '',
                    roleId: 0,
                    appId: "",
                    //角色编号
                    ids: "" //菜单编号集合

                },
                modelList: [],
                // 管理员集合
                managerOptions: [],
                // 右侧显示
                filterManagerOptions: [],
                //组菜单
                loading: true,
                rules: {
                    managerIds: [{
                        "required": true,
                        "message": "请选择管理员"
                    }]
                }
            };
        },
        watch: {},
        computed: {
        },
        methods: {
            //获取roleId数据源
            roleIdOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/basic/role/all.do").then(function (res) {
                    that.roleIdOptions = res.data.rows;
                    // 插入所有角色列
                    that.roleIdOptions.unshift({id:0,roleName:'所有角色'})
                }).catch(function (err) {
                    console.log(err);
                });
            },
            // 角色节点点击事件
            handleNodeClick: function (data, node, self) {
              var rule = ''
              // 所有角色时
              if(data.id == 0){
                rule = 'all'
              }
              this.filterManagerOptions = this.managerOptions.filter(function(item){
                if(rule === 'all'){
                  return true
                }else {
                  return item.roleIds.split(',').includes(data.id)
                }
              })
            },
            //管理员列表选中
            handleSelectionChange: function (val) {
              var rowIdChecked = val.map(function(item){
                return item.id
              })
              this.managerOptions.map(function(item){
                if(rowIdChecked.includes(item.id)){
                  item.checked = true
                }else{
                  item.checked = false
                }
              })
            },
            // 回显
            toggleSelection: function () {
              var rowChecked = []
              var that = this
              // 暂存checked状态行数据
              this.filterManagerOptions.forEach(function(row) {
                if(row.checked){
                  rowChecked.push(JSON.parse(JSON.stringify(row)))
                }
              });
              // 选中checked状态行
              rowChecked.map(function(row){
                that.$refs.multipleTable.toggleRowSelection(row,true);
              })
            },
            // 保存
            save: function () {
                var that = this;
                var url = ms.manager + "/site/saveWebSiteRole.do";
                that.saveDisabled = true;
                var data = JSON.parse(JSON.stringify(that.form));
                var managerIds = []
                that.managerOptions.map(function(item){
                  if(item.checked){
                    managerIds.push(item.id)
                  }
                })
                data.managerIds = managerIds.join(",");
                ms.http.post(url, data).then(function (data) {
                    if (data.result ) {
                        that.$notify({
                            title: '成功',
                            message: '保存成功',
                            type: 'success'
                        });
                        that.saveDisabled = false;
                        location.href = ms.manager + "/site/index.do";
                    } else {
                        that.$notify({
                            title: '失败',
                            message: data.msg,
                            type: 'warning'
                        });
                        that.saveDisabled = false;
                    }
                });
            },
            //获取管理员数据源
            managerOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/basic/manager/list.do", {pageSize: 9999}).then(function (res) {
                    that.managerOptions = res.data.rows;
                    that.getWebSiteRole(that.form.appId)
                }).catch(function (err) {
                    console.log(err);
                });
            },
            getWebSiteRole:function(appid) {
                var that = this
                var url = ms.manager + "/site/getWebSiteRole.do";
                ms.http.get(url, {
                    "appId": appid
                }).then(function (data) {
                    that.form.managerIds = data.data
                    that.managerOptions.map(function(item){
                      if(that.form.managerIds.includes(item.id)){
                        item.checked = true
                      }
                    })
                    that.$nextTick().then(function(){
                      // 默认选中第一个
                      document.getElementsByClassName('el-tree-node')[0] && document.getElementsByClassName('el-tree-node')[0].click()
                      that.$nextTick().then(that.toggleSelection())
                    })
                })
            }
        },
        created: function () {
            this.form.id = ms.util.getParameter("id");
            this.form.appId = ms.util.getParameter("appId");
            this.managerOptionsGet();
            this.roleIdOptionsGet();
        }
    });
</script>
<style>
    .tree-table > div:nth-of-type(1) {
        text-align: right;
    }

    .tree-table > div:nth-of-type(2) {
        margin-left: -10px;
        padding-right: 5px;
    }

    .el-form .ms-table-head {
        line-height: 0px;
    }

    .tree-table .cell.el-tooltip .ms-check {
        display: inline-block;
        margin-top: 3px;
        margin-right: 11px;
    }
    #form .el-tree {
        background: none !important;
        margin-top: 4px;
    }
    #form .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content{
      background: #409EFF !important;
      color: white;
    }
    #form .ms-container .el-tree-node__content {
      height: 32px;
      margin-top: 6px;
      margin-bottom: 6px;
    }
</style>
