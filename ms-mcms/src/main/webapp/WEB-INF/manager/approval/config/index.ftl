<!DOCTYPE html>
<html>

<head>
    <title>审批配置</title>
    <#include "../../include/head-file.ftl">
</head>

<body>
<div id="index" class="ms-index" v-cloak>
    <el-header class="ms-header" height="50px">
        <el-col :span="4" :offset="20" style="textAlign: right;paddingRight: 10px">
            <@shiro.hasPermission name="approval:config:save">
                <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveLoading"  >保存
                </el-button>
            </@shiro.hasPermission>
        </el-col>
    </el-header>
    <el-main class="ms-container" v-loading="saveLoading">
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info">
            此功能用于配置不同角色拥有的栏目审批权限,当一个角色拥有一个栏目的相连审批权限,审核时会实现跳级<br/>
            如:A角色拥有一二审权限,审核完会跳转到三审待其他拥有权限的进行审核 <br/>
            如果想要新增或减少审批级数可以在进度方案-文章审核处修改
        </el-alert>
        <div class="ms-index-body" style="height: 78vh">
            <div class="ms-layout-left">
                <el-scrollbar style="height: 100%;width: 100%">
                    <el-tree

                     ref="categoryTree"
                     :default-checked-keys="defaultSelectedKey"
                     :expand-on-click-node="false"
                     @check="handleClickChangeCategory"
                     :data="categorys"
                     :indent="5"
                     node-key="id"
                     :highlight-current='true'
                     :check-on-click-node="true"
                     highlight-current
                     default-expand-all
                     v-loading="categorysLoading"
                     style="padding: 10px;height: 100%;"
                     >
                        <span class="custom-tree-node" slot-scope="scope">
                            <span :style="scope.data.categoryType == '3' ? 'color: #dcdfe6' : ''" :title="scope.data.categoryTitle">{{ scope.data.categoryTitle }}</span>
                        </span>
                    </el-tree>
                </el-scrollbar>
            </div>
            <el-scrollbar style="height: 100%;width: 100%">
                <div class="ms-layout-right" v-loading="progressLoading">
                    <el-row :gutter="20" style="margin-top: -10px;" v-show="curCongfigData">
                        <template v-for="(item,i) in progresss" :key="'progress'+i">
                            <el-col :span="4" style='margin: 10px 0'>
                                <el-table
                                 ref="roleTable"
                                 :data="roles"
                                 :key="'role'+i"
                                 :row-key="function(row){return row.key}"
                                 border
                                 tooltip-effect="dark"
                                 v-loading="rolesLoading"
                                 @select="function(selection,row){handleSelectedRoles(selection,row,item)}"
			    				 @select-all="function(selection){handleSelectedAllRoles(selection,item)}"
                                 >
                                    <el-table-column type="selection" align="left" :reserve-selection="true">
                                    </el-table-column>
                                    <el-table-column :label="item.progressNodeName" :formatter="formatter">
                                    </el-table-column>
                                </el-table>
                            </el-col>
                        </template>
                    </el-row>
                    <el-empty description="请选择左侧栏目" v-show="!curCongfigData"></el-empty>
                </div>
            </el-scrollbar>
        </div>
    </el-main>
</div>
<#include "/approval/config/form.ftl">
</body>

</html>
<script>

    //关键结构设计
    // congfigDatas: [ //用于渲染配置结构
    //     {
    //         categoryId: 0, //栏目id
    //         progresss: [{
    //             progressId: 0, //审批节点
    //             configroleIds: [{
    //                 id: 0, //角色编号
    //             }], //角色id,最终要转换成 1,2,3,4
    //         }]
    //     }
    // ],

    var indexVue = new Vue({
        el: '#index',
        data: {

            manager: ms.manager,
            saveLoading: false,
            level:4,//审批级数
            defaultSelectedKey: [],
            categorys: [], //栏目
            categorysLoading: true,

            roles: [], //角色
            rolesLoading: true,

            progresss: [], //进度
            progressLoading: false,

            congfigDatas: [
                {
                    categoryId: 0, //栏目id
                    progresss: [{
                        progressId: 0, //审批节点
                        configRoleIds: [{
                            id: 0, //角色编号
                        }], //角色id,最终要转换成 1,2,3,4
                    }]
                }
            ], //用于渲染配置结构
            curCongfigData: null, //当前选中到配置对象,是congfigDatas中的其中一个对象
            saveConfigDatas: [], //保持数据对象，用于提交

            configs:null,//配置接口返回的配置数据
            configErr:true,//标记当前配置是否完整

        },
        methods: {

            //保存配置数据
            save: function() {
                var that = this
                that.saveLoading = true;

                if(that.confingDataToConfig()) {
                    ms.http.post(ms.manager + "/approval/config/save.do", that.saveConfigDatas, { headers: { 'Content-Type': 'application/json' } }).then(function(res) {
                        if (res.result) {
                            that.$notify({
                                title: '成功',
                                message: res.msg,
                                type: 'success'
                            });
                        } else {
                            that.$notify({
                                title: '失败',
                                message: res.msg,
                                type: 'warning'
                            });
                        }
                        that.saveLoading = false;
                    })
                }

            },
            //	审批类型
            schemeList: function() {
                var that = this
                ms.http.post(ms.manager + "/progress/scheme/list.do", {
                    pageSize: 999,
                    pageNo: 1,
                    schemeName: '文章审核'
                }).then(function(res) {
                    if (res.result) {
                        that.repolyTypeList = res.data.rows
                        that.configList();
                        that.progressList();
                    } else {
                        that.$notify({
                            title: "错误",
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                })
            },
            //	栏目分类列表
            categoryList: function() {
                var that = this
                this.categorysLoading=true;
                ms.http.post(ms.manager + "/cms/category/list.do", { pageSize: 999, pageNo: 1 }).then(function(res) {
                    if (res.result) {
                        var datas = []
                        res.data.rows.forEach(function(category) {
                            if(!category.leaf) {
                                datas.push(category)
                            } else {
                                if(category.categoryType!=3) {
                                    datas.push(category)
                                }
                            }


                        })
                        that.categorys = ms.util.treeData(datas, 'id', 'categoryId', 'children');

                        that.categorysLoading=false;


                    }
                })
            },

            //	角色列表
            roleList: function() {
                var that = this
                that.rolesLoading=true;
                ms.http.get(ms.manager + "/basic/role/list.do", { pageSize: 999, pageNo: 1 }).then(function(res) {
                    if (res.result) {
                        that.roles = res.data.rows;
                        that.rolesLoading=false;
                    }
                })
            },

            //	审批进度
            progressList: function() {
                var that = this;
                var schemeId = null;
                if (that.repolyTypeList) {
                    schemeId = that.repolyTypeList[0].id;
                }
                ms.http.post(ms.manager + "/progress/progress/list.do", {
                    pageSize: 999,
                    pageNo: 1,
                    schemeId: schemeId
                }).then(function(res) {
                    if (res.result) {
                        that.progresss = res.data.rows.map(function(item,index) {
                            item.index = index
                            return item
                        });
                    }
                })
            },

            //配置数据
            configList: function() {
                var that = this
                var schemeId = null;
                if (that.repolyTypeList) {
                    schemeId = that.repolyTypeList[0].id;
                }
                ms.http.post(ms.manager + "/approval/config/list.do",{
                    schemeId: schemeId
                }).then(function(res) {
                    if (res.result) {
                        that.congfigDatas = [];//初始化配置
                        that.configs = res.data;
                        that.congfigData = that.configListToConfingData(res.data);
                    }
                })
            },

            //数据转换,将配置数据转换成ConfigData，方便渲染
            configListToConfingData: function(data) {
                var that = this;
                data.forEach(function(item) {
                    var configDataObj = that.buildConfigDataObj(item.categoryId);
                    //判断congfigData是否已经存在
                    if (item.categoryId != '' && that.getCongfigDataById(item.categoryId) == null) {
                        that.congfigDatas.push(configDataObj);
                    }
                })

            },

            //组装单个ConfigData对象
            buildConfigDataObj: function(categoryId) {
                var that = this;
                var congfigData = {
                    categoryId: categoryId, //栏目id
                    progresss:[],
                }
                that.configs.forEach(function(item) {
                    var _configroleIds = item.configRoleIds.split(",");
                    if (item.categoryId == categoryId) {
                        var progresss = {
                            progressId: item.progressId, //审批节点
                            configRoleIds: _configroleIds
                        }
                        congfigData.progresss.push(progresss);
                    }

                })
                return congfigData;
            },


            //数据转换,将渲染转换成提交数据
            //返回 true：成功 false失败
            confingDataToConfig: function() {
                var that = this
                try {
                    that.saveConfigDatas = [];
                    if(that.congfigDatas.length==0) {
                        throw new Error("congfigDatas 为空");
                    }
                    that.congfigDatas.forEach(function(congfigData) {
                        var _saveConfigData = {}
                        // if(congfigData.progresss.length<that.progresss.length && congfigData.progresss.length>0) {
                        //     throw new Error("progress 为空");
                        // }
                        if(congfigData.progresss.length>that.progresss.length) {
                            congfigData.progresss.splice(0,that.progresss.length);
                        }
                        congfigData.progresss.forEach(function(progress) {
                            // if(progress.configRoleIds.length==0){
                            //     throw new Error("progress 为空");
                            // }
                            _saveConfigData = {
                                categoryId: congfigData.categoryId,
                                progressId: progress.progressId,
                                configRoleIds: progress.configRoleIds.toString(),
                                schemeId: "1",
                            }

                            if(!that.saveConfigDatas.includes(_saveConfigData) && _saveConfigData.configRoleIds != ""){
                                that.saveConfigDatas.push(_saveConfigData);
                            }
                        });
                    })
                    return true;
                } catch (e){
                    that.$notify({
                        title: "错误",
                        message: '审批设置不完整',
                        type: 'warning'
                    });
                    that.saveLoading = false;
                    that.saveConfigDatas = [];
                    return false;
                }




                return true;
            },

            //根据栏目id获取到配置，可能不存在
            //categoryId:栏目id
            getCongfigDataById: function(categoryId) {
                //便利congfigData ，没有返回null
                var has = null
                this.congfigDatas.forEach(function(congfigData) {
                    if (congfigData.categoryId == categoryId) {
                        has = congfigData;
                    }
                })
                return has ;
            },

            //获取角色
            //提供给回显使用
            getRoleById: function(roleId) {
                var has = null
                this.roles.forEach(function(role,index) {
                    if (role.id == roleId) {
                        has = role;
                    }
                })
                return has ;
            },


            //左侧栏目切换
            handleClickChangeCategory: function(data, checked) {
                var that = this;

                that.progressLoading=true;
                if(data.leaf || data.categoryType=='2'){
                    // try {
                    //     if(that.congfigDatas.indexOf(that.curCongfigData)>0) {
                    //         //判断上次是否设置了完整的审核
                    //         // if(this.curCongfigData!=null &&  this.curCongfigData.progresss.length>0 && this.curCongfigData.progresss.length<that.progresss.length ) {
                    //         //     throw new Error("progress 为空");
                    //         // }
                    //
                    //         //检查所有的角色是否有设置
                    //         // if(this.curCongfigData!=null) {
                    //         //     this.curCongfigData.progresss.forEach(function (progress) {
                    //         //         if(progress.configRoleIds.length==0){
                    //         //             throw new Error("progress 为空");
                    //         //         }
                    //         //     })
                    //         // }
                    //     }
                    //
                    // } catch(e) {
                    //     that.$notify({
                    //         title: "错误",
                    //         message: '审批设置不完整',
                    //         type: 'warning'
                    //     });
                    //     //设置选中
                    //     this.$refs.categoryTree.setCheckedKeys([this.curCongfigData.categoryId]);
                    //     //设置高亮
                    //     this.$refs.categoryTree.setCurrentKey(this.curCongfigData.categoryId);
                    //     this.configErr = true;
                    //     that.progressLoading=false;
                    //     return;
                    // }


                    //从congfigData 获取对于对应的配置信息
                    this.curCongfigData = this.getCongfigDataById(data.id);
                    this.configsErr = false;
                    //如果没找到，需要赋值空结构体
                    if (this.curCongfigData == null) {

                        var newCongfigData = {
                            categoryId: data.id, //栏目id
                            progresss: [],      //  审批进度以及审批角色
                        }
                        //并存放到congfigData
                        this.congfigDatas.push(newCongfigData);
                        //设置当前
                        this.curCongfigData = newCongfigData;
                    }
                      //设置单选
                      that.$refs.categoryTree.setCheckedKeys([data.id])
                      //清空所有表格选中状态
                      if(that.$refs.roleTable) {
                          that.$refs.roleTable.forEach(function(item){
                              item.clearSelection()
                          })
                      }

                    //设置回显
                    this.curCongfigData.progresss.forEach(function(progress,index) {
                        progress.configRoleIds.forEach(function(configroleId) {
                            var _role = that.getRoleById(configroleId);
                            if(_role) {
                                that.$refs.roleTable[index].toggleRowSelection(that.getRoleById(configroleId));
                            }
                        })
                    })
              } else {

                    that.$notify({
                        title: "栏目选择提示",
                        message: '列表只能选择子栏目',
                        type: 'warning'
                    });
                    if (this.curCongfigData) {
                        //设置选中
                        this.$refs.categoryTree.setCheckedKeys([this.curCongfigData.categoryId]);
                        //设置高亮
                        this.$refs.categoryTree.setCurrentKey(this.curCongfigData.categoryId);
                    } else {
                        //设置选中
                        this.$refs.categoryTree.setCheckedKeys([]);
                        //设置高亮
                        this.$refs.categoryTree.setCurrentKey(null);
                    }
                }
                that.progressLoading=false;
            },

            //列格式化
            formatter: function(row, column, cellValue, index) {
                return row.roleName;
            },

			//  选中角色的事件,参数依次为   1.当前已选中的数组,2.当前审批进度信息对象
			handleSelectedAllRoles: function(selection,progress) {
				var that = this
			},

            //  选中角色的事件,参数依次为   1.当前已选中的数组,2.当前选中对象,3.当前审批进度信息对象
            handleSelectedRoles: function(selection,row,progress) {
                var that = this

                if(that.curCongfigData==null) {
                    that.$notify({
                        title: "错误",
                        message: '请先选择左侧栏目',
                        type: 'warning'
                    });
                    that.$refs.roleTable.forEach(function(table) {
                        table.clearSelection();
                    })

                    return;
                }

                if(that.congfigDatas.indexOf(that.curCongfigData)<0) {
                    that.congfigDatas.push(that.curCongfigData);
                }

                //  是否初次进入,并创建configroleIds数组数据对象
                if(!that.curCongfigData.progresss[progress.progressNumber-1]){
                    that.curCongfigData.progresss[progress.progressNumber-1]={configRoleIds:[]};
                }
                that.curCongfigData.progresss[progress.progressNumber-1].progressId = progress.id
                //  判断当前数据中是否已经包含当前角色
                if(that.curCongfigData.progresss[progress.progressNumber-1].configRoleIds.includes(row.id)){
					//	数组中有点击的数据则为反选,删除数组中的点击元素
                    var i = that.curCongfigData.progresss[progress.progressNumber-1].configRoleIds.indexOf(row.id);
                    that.curCongfigData.progresss[progress.progressNumber-1].configRoleIds.splice(i,1);
                    //判断是否全部清空
                    if(that.curCongfigData.progresss[progress.progressNumber-1].configRoleIds.length==0) {

                        //如果所有审批取消，从congfigDatas删除
                        var clear = true;
                        that.curCongfigData.progresss.forEach(function (progress) {
                            if(progress.configRoleIds.length>0) {
                                clear = false;
                            }
                        })
                        if(clear) {
                            that.congfigDatas.splice(that.congfigDatas.indexOf(that.curCongfigData),1);
                        }

                    }
                }else {
					that.curCongfigData.progresss[progress.progressNumber-1].configRoleIds.push(row.id)
				}

                if(that.curCongfigData.progresss.length==0) {
                    that.congfigDatas.splice(that.congfigDatas.indexOf(that.curCongfigData),1);
                }

                //配置不完整，保存按钮警用

                if(this.curCongfigData.progresss.length==4) {
                    this.configErr = false;
                }
            },

            //  根据传入数组,返回数据内部是否有长度为0的子数组
            getBoolforArray: function(array) {
                var has = null
                //  传入数组及名称循环数组,长度为0
                array.forEach(function(item) {
                    if(!item['configRoleIds'].length){
                        has = false
                    }
                    if(has === null && item['configRoleIds'].length) {
                        has = true
                    }
                })
                if(array.length == 0){
                    has = false
                }
                return has
            }
        },

        created: function() {
            this.categoryList();
            this.roleList();
            this.schemeList();
        },

    })
</script>
<style>
    #index .ms-container {
        height: calc(100vh - 78px);
    }

    #index .ms-container .ms-index-body {
        display: flex;
        flex-direction: row;
        flex: 1;
        height: 90vh;
    }

    #index .ms-container .ms-index-body .ms-layout-left {
        background-color: rgb(242, 246, 252);
        display: flex;
        position: relative;
        margin-right: 10px;
        min-width: 280px;
    }

    #index .ms-container .ms-index-body .ms-layout-right {
        flex: 1;
    }

    #index .ms-container .ms-index-body .ms-layout-left .el-aside {
        overflow: hidden;
    }

    .el-tree {
        background-color: unset;
    }
    .el-scrollbar__wrap {
        padding-right: 15px;
    }

    .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content {
        background-color: #409EFF;
        color: white;
    }

    .el-table__header-wrapper  .el-checkbox {
        display: none
    }
    .el-header {
        padding: 10px ;
    }

</style>
