<!-- 自定义菜单 -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>自定义菜单</title>
    <#include "/include/head-file.ftl"/>
    <#include "/mweixin/include/head-file.ftl"/>
</head>
<body style="overflow: hidden">
<#include "/mweixin/component/ms-message-reply.ftl">
<div id="ms-menu" class="ms-weixin-content" v-cloak>
    <el-container class="ms-custom-container">
        <el-header class="ms-header" height="50px">
            <el-row>
                <@shiro.hasPermission name="weixin:menu:sync">
                    <el-button type="success" class="ms-fl" size="mini" @click='menuCreate'>
                        <i class="iconfont icon-14fabu"></i>发布菜单
                    </el-button>
                </@shiro.hasPermission>
                <@shiro.hasPermission name="weixin:menu:save">
                    <el-button type="primary" class="ms-fr" size="mini" @click='menuSave'><i
                                class="iconfont icon-icon-"></i>保存
                    </el-button>
                </@shiro.hasPermission>


            </el-row>
        </el-header>

        <el-container class="ms-container">

            <el-aside>
                <el-container>
                    <el-header>公众号</el-header>
                    <el-main></el-main>
                    <el-footer>
                        <el-button icon="iconfont icon-dakaijianpan"></el-button>
                        <div class="ms-create-menu">
                            <draggable v-model="allMenuList" :options="{draggable:'.ms-create-sub-menu'}"
                                       @start="drag=true" @end="dragEnd(allMenuList)">
                                <div class="ms-create-sub-menu" v-for="(menu,index) of allMenuList" :key="index" draggable="true">
                                    <!-- 父菜单 -->
                                    <!-- 拖拽模块 -->
                                    <el-button class="list-group-item" type="primary" @click="openSubMenu(index,menu)"
                                               v-on:mouseenter.native="visible(menu)"
                                               @mouseleave.native="invisible(menu)"
                                               draggable="true">{{menu.menuTitle }}
                                        <div @click.stop="menuDel(menu)" v-show="menu.seen" class="el-badge__content el-badge__content--undefined ms-menu-del">
                                            <i class='el-icon-close'></i>
                                        </div>
                                    </el-button>

                                    <!-- 修改全局影响子菜单列表问题 添加sub-menu-list-nopadding类名-->
                                    <div class="sub-menu-list sub-menu-list-nopadding" v-show="menu.addSubMenuShow">
                                        <draggable v-model="menu.subMenuList" :options="{draggable:'.sub-menu-item'}"
                                                   @start="drag=true" @end="dragEnd(menu.subMenuList)">
                                            <!-- 子菜单 -->
                                            <el-button v-for="(sub,index) of menu.subMenuList" :key="index"
                                                       class="sub-menu-item" v-on:mouseenter.native="visible(sub)"
                                                       @mouseleave.native="invisible(sub)"
                                                       @click='onClickSubMenu(sub)' draggable="true">
                                                {{sub.menuTitle}}
                                                <div v-show="sub.seen" @click.stop="menuDel(menu,sub)" id="sub-menu-del"
                                                     class="el-badge__content el-badge__content--undefined ms-menu-del">
                                                    <i class='el-icon-close'></i></div>
                                            </el-button>
                                        </draggable>
                                        <!-- 添加子菜单的加号按钮 -->
                                        <el-button class="son-button" icon="el-icon-plus" class="ms-create-btn"
                                                   @click="addSubMenu(menu.subMenuList)"></el-button>
                                    </div>
                                </div>
                                <!-- 添加父菜单的加号按钮 -->
                                <el-button icon="el-icon-plus" @click="onClickMenu" v-show="allMenuList.length<3"
                                           class="add-menu"></el-button>
                            </draggable>
                        </div>
                    </el-footer>

                </el-container>

            </el-aside>
            <el-main class='ms-relative' v-if="">
                <el-alert
                        class="ms-alert-tip"
                        title="功能介绍"
                        type="info"
                        description="拖动可以改变菜单的显示顺序，切换选项卡会清除之前的内容，请谨慎操作，如果有子菜单，父菜单的内容设置是无效的">
                </el-alert>
                <el-card class="menu-card" shadow="never">
                    <div slot="header" class="clearfix">
                        <span v-text="menuForm.id ? '修改菜单' : '新建菜单'"></span>
                    </div>
                    <el-form ref="menuForm" :rules="menuFormRules" :model="menuForm" label-width="100px" size="mini">
                        <el-form-item label="菜单名称" prop="menuTitle" class="ms-menu-name">
                            <el-input ref="menuTitle" v-model="menuForm.menuTitle" :disabled="!menuForm.menuStatus"  style="width:180px!important"></el-input>
                            <div class="ms-form-tip">菜单名称字数不多于5个汉字或10个字母</div>
                        </el-form-item>
                        <el-form-item label="菜单内容" class="ms-menu-content">
                            <el-radio-group v-model="menuForm.menuType" @change="radioChange"
                                            :disabled="!menuForm.menuStatus">
                                <el-radio :label="1">发送消息</el-radio>
                                <#--								<el-radio :label="2">扫码</el-radio>-->
                                <#--								<el-radio :label="3">扫码（等待消息）</el-radio>-->
                                <el-radio :label="0">跳转网页</el-radio>
                                <#--								<el-radio :label="5">地理位置</el-radio>-->
                                <#--								<el-radio :label="6">拍照发图</el-radio>-->
                                <#--								<el-radio :label="7">拍照相册</el-radio>-->
                                <#--								<el-radio :label="8">相册发图</el-radio>-->
                                <el-radio :label="9">跳转小程序</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item v-if="menuForm.menuType == 0" label="链接" class="ms-menu-content" prop='menuUrl'>
                            <el-input v-model="menuForm.menuUrl" type="input" style="width: 100%!important" placeholder="请输入菜单地址"  :disabled="!menuForm.menuStatus"></el-input>
                            <div class="ms-form-tip">
                                链接地址必须以http://或https://开头。
                            </div>
                        </el-form-item>
                        <el-form-item v-if="menuForm.menuType == 9" label="小程序路径" class="ms-menu-content" prop='menuPagePath'>
                            <el-input v-model="menuForm.menuPagePath" type="input" style="width: 100%!important" placeholder="请输入小程序路径"  :disabled="!menuForm.menuStatus"></el-input>
                            <div class="ms-form-tip">
                                小程序的页面路径
                            </div>
                        </el-form-item>
                        <el-form-item v-if="menuForm.menuType == 9" label="小程序网址" class="ms-menu-content" prop='menuUrl'>
                            <el-input v-model="menuForm.menuUrl" type="input" style="width: 100%!important" placeholder="请输入小程序网址"  :disabled="!menuForm.menuStatus"></el-input>
                            <div class="ms-form-tip">
                                链接地址必须以http://或https://开头，不支持小程序的老版本客户端将打开本网址
                            </div>
                        </el-form-item>
                        <el-form-item v-if="menuForm.menuType == 9" label="AppId" class="ms-menu-content" prop='miniprogramAppid'>
                            <el-input v-model="menuForm.miniprogramAppid" type="input" style="width: 100%!important" placeholder="请输入小程序的appid"  :disabled="!menuForm.menuStatus"></el-input>
                            <div class="ms-form-tip">
                                小程序的appid（仅认证公众号可配置）
                            </div>
                        </el-form-item>
                        <el-form-item v-show="menuForm.menuType == 1" label="发送消息"  prop='menuContent'>
                            <ms-message-reply ref="msMessageReply" @on-editor-change="onEditorChange"></ms-message-reply>
                            <!--利用隐藏表单来满足错误提示-->
                            <el-input v-model="menuForm.menuContent" class="hide-menu-content"  style="width:180px!important"></el-input>
                        </el-form-item>

                    </el-form>
                </el-card>
            </el-main>
        </el-container>
    </el-container>
</div>
<script>
    var menuVue = new Vue({
        el: "#ms-menu",
        data: function () {
            return {
                menuForm: {},//表单
                //所有菜单list
                allMenuList: [],

                //用来初始化菜单
                defaultMenu: {
                    menuTitle: "新建菜单",
                    menuContent: "",
                    menuUrl: "",
                    subMenuList: [],
                    menuStatus: 1,
                    menuStyle: 'text',//类型：text文本 image图片 imageText图文 link外链接
                    menuType: 1,//菜单属性 0:链接 1:回复
                    seen: false, //删除按钮可见
                },
                // subArticleList:[],
                menuFormRules: {
                    menuTitle: [{
                        required: true,
                        message: "请输入菜单名称",
                        trigger: ["blur", "change"]
                    },
                        {
                            validator: function (rule, value, callback) {
                                //获取文本长度
                                var length = 0;
                                for (var i = 0; i < value.length; i++) {
                                    if (value.charCodeAt(i) > 127 || value.charCodeAt(i) == 94) {
                                        length += 2;
                                    } else {
                                        length++;
                                    }
                                }
                                if (length > 10) {
                                    callback('菜单名称最多5个汉字或者10个字母');
                                } else {
                                    callback();
                                }
                            }
                        }
                    ],
                    menuPagePath: [{
                       required: true,
                       message: '请输入小程序路径',
                    }],
                    miniprogramAppid: [{
                        required: true,
                        message: '请输入小程序Appid'
                    }],
                    miniprogramUrl: [{
                        required: true,
                        message: '请输入小程序链接'
                    }],
                    menuUrl: [
                        {
                            required: true,
                            message: '请输入地址',
                            trigger: 'change'
                        }, {
                            validator: function (rule, value, callback) {
                                /^(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?$/.test(value) ?
                                    callback() : callback('链接不合法')
                            }
                        }

                    ],
                    menuContent: [{
                        required: true,
                        message: '内容不能为空',
                        trigger: 'change'
                    }]
                },
            }
        },
        computed: {
            menuWeixinId: function () {
                return localStorage.getItem('weixinId')
            }
        },

        methods: {
            menuList: function () {
                var that = this;
                ms.http.get(ms.manager + "/mweixin/menu/list.do")
                    .then(function (res) {
                        that.allMenuList = []
                        if (res.data.rows) {
                            res.data.rows.forEach(function (item, index) {
                                //父菜单没有menuMenuId
                                if (!item.menuMenuId) {
                                    item.subMenuList = [];
                                    item.seen = false;
                                    that.allMenuList.push(item);
                                    //添加子菜单
                                    res.data.rows.forEach(function (val) {
                                        if (val.menuMenuId && val.menuMenuId == item.id) {
                                            val.seen = false;
                                            //判断是否是子菜单
                                            val.isSubMenu = true;
                                            item.subMenuList.push(val)
                                        }
                                    })
                                }
                            })
                            // 初始化显示第一个菜单
                            if (that.allMenuList.length) {
                                that.menuForm = that.allMenuList[that.allMenuList.length-1];
                                var content = {};
                                content.type = that.menuForm.menuStyle;
                                content.text = that.menuForm.menuContent;
                                that.$refs.msMessageReply.load(content);
                            }
                        }
                        //父菜单排序
                        that.allMenuList.sort(function (a, b) {
                            return a.menuSort - b.menuSort;
                        })

                    }, function (err) {
                    })
            },
            // 菜单排序
            menuSort: function () {
                ms.http.post(ms.manager + "/mweixin/menu/menuSort.do", this.allMenuList,
                    {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    })
            },
            onEditorChange: function (content) {
               this.menuForm.menuStyle = content.type;
               this.menuForm.menuContent = content.text;
            },

            //菜单类型改变
            radioChange: function (val) {
                switch (val) {
                    case 0:
                        this.menuForm.menuStyle = 'link';
                        break;
                    case 1:
                        if (this.menuForm.menuType == 1 && this.menuForm.menuStyle == 'link') {
                            this.menuForm.menuStyle = 'text';
                        }
                        break;
                }
            },
            //拖拽事件结束
            dragEnd: function (menu) {
                //拖动位置即保存一次
                menu.forEach(function (value, index, array) {
                    value.menuSort = index + 1;
                })
                this.menuSort();
            },

            openSubMenu: function (index, menu) {
                var that = this;
               if(!this.isValid()) {
                   return;
               }

                if ( that.menuForm == menu || !(that.menuForm.menuType == 0)) {
                    //判断是否是子菜单
                    that.menuForm = menu
                    var content = {};
                    content.type = that.menuForm.menuStyle;
                    content.text = that.menuForm.menuContent;
                    that.$refs.msMessageReply.load(content);

                    if (!menu.isSubMenu) {
                        that.closeAllSubMenu(index);
                        //排序
                        menu.subMenuList.sort(function (a, b) {
                            return a.menuSort - b.menuSort;
                        })
                        menu.addSubMenuShow = !menu.addSubMenuShow;
                    }
                }
            },
            // 添加菜单
            onClickMenu: function () {
                var that = this;
                if(!this.isValid()) {
                    return;
                }
                var content = {};
                content.type = "text";
                content.text = "";
                that.$refs.msMessageReply.load(content);
                var menu = JSON.parse(JSON.stringify(that.defaultMenu));
                that.allMenuList.push(menu);
                that.menuForm = menu;
                that.$nextTick(function () {
                    Array.prototype.forEach.call(
                        document.querySelectorAll(".ms-create-sub-menu"),
                        function (item, index) {
                            item.style.width = '80px';
                        }
                    );
                    document.querySelector(".add-menu").style.width = '80px';
                });

            },
            // 添加子菜单
            addSubMenu: function (subMenuList) {
                var that = this;
                if(!this.isValid()) {
                    return;
                }

                if (subMenuList.length > 4) {
                    that.$notify({
                        title: '提示',
                        message: "子菜单最多5项",
                        type: 'warning'
                    });
                    return;f
                }
                //默认菜单模板拷贝
                var menu = JSON.parse(JSON.stringify(that.defaultMenu));
                menu.menuTitle = "新建子菜单";
                menu.menuContent = "";
                menu.isSubMenu = true;
                subMenuList.push(menu);
                that.menuForm = menu;

                var content = {};
                content.type = that.menuForm.menuStyle;
                content.text = that.menuForm.menuContent;
                that.$refs.msMessageReply.load(content);

            },
            //点击子菜单
            onClickSubMenu: function (menu) {
                var that = this;
                if(!this.isValid()) {
                    return;
                }
                this.menuForm = menu;
                var content = {};
                content.type = this.menuForm.menuStyle;
                content.text = this.menuForm.menuContent;
                that.$refs.msMessageReply.load(content);
            },
            // 关闭所有的子菜单弹出层
            closeAllSubMenu: function (num) {
                // 确保当前的菜单不被重置成false
                this.allMenuList.forEach(function (item, index) {
                    num != index && (item.addSubMenuShow = false)
                })
            },
            // 保存菜单
            menuSave: function () {
                // 表单校验
                var that = this;


                //设置排序
                that.allMenuList.forEach(function (value, index, array) {
                    value.menuSort = index + 1;
                    value.subMenuList.forEach(function (value, index, array) {
                        value.menuSort = index + 1;
                    })
                })


                for (var i in that.allMenuList) {
                    if (!that.allMenuList[i].subMenuList.length) {
                        if (that.allMenuList[i].menuType == 1) {
                            if (!that.allMenuList[i].menuContent) {
                                that.$notify({
                                    title: '提示',
                                    message: "菜单内容不能为空",
                                    type: 'warning'
                                });
                                return;
                            }
                        } else if (that.allMenuList[i].menuType == 0) {
                            if (!that.allMenuList[i].menuUrl) {
                                that.$notify({
                                    title: '提示',
                                    message: "菜单内容不能为空",
                                    type: 'warning'
                                });
                                return;
                            }
                        } else if (that.allMenuList[i].menuType == 9) {
                            // 当选择小程序时把LinK替换成miniprogram
                            that.allMenuList[i].menuStyle = 'miniprogram';
                            // 小程序必须要这三个条件，缺一不可
                            if (!that.allMenuList[i].menuPagePath || !that.allMenuList[i].menuUrl || !that.allMenuList[i].miniprogramAppid) {
                                that.$notify({
                                    title: '提示',
                                    message: "菜单内容不能为空",
                                    type: 'warning'
                                });
                                return;
                            }
                        }
                    } else {
                        for (var p in that.allMenuList[i].subMenuList) {
                            if (that.allMenuList[i].subMenuList[p].menuType == 1) {
                                if (!that.allMenuList[i].subMenuList[p].menuContent) {
                                    that.$notify({
                                        title: '提示',
                                        message: "子菜单内容不能为空",
                                        type: 'warning'
                                    });
                                    return;
                                }
                            } else if (that.allMenuList[i].subMenuList[p].menuType == 0) {
                                if (!that.allMenuList[i].subMenuList[p].menuUrl) {
                                    that.$notify({
                                        title: '提示',
                                        message: "子菜单内容不能为空",
                                        type: 'warning'
                                    });
                                    return;
                                }
                            } else if (that.allMenuList[i].subMenuList[p].menuType == 9) {
                                // 当选择小程序时把LinK替换成miniprogram
                                that.allMenuList[i].subMenuList[p].menuStyle = 'miniprogram';
                                // 小程序必须要这三个条件，缺一不可
                                if (!that.allMenuList[i].subMenuList[p].menuPagePath || !that.allMenuList[i].subMenuList[p].menuUrl || !that.allMenuList[i].subMenuList[p].miniprogramAppid) {
                                    that.$notify({
                                        title: '提示',
                                        message: "子菜单内容不能为空",
                                        type: 'warning'
                                    });
                                    return;
                                }
                            }
                        }
                    }
                }

                var param = JSON.parse(JSON.stringify(that.allMenuList))
                ms.http.post(ms.manager + "/mweixin/menu/saveOrUpdate.do",param,
                    {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(function (res) {
                    if (res.result) {
                        that.$notify({
                            title: '成功',
                            message: "菜单修改成功",
                            type: 'success'
                        });
                        that.menuList();
                    } else {
                        that.$notify.error({
                            title: '失败',
                            message: res.msg
                        })
                    }
                }, function (err) {
                })

            },
            // 删除菜单
            menuDel: function (mainMenu, subMenu) {
                var that = this;

                //取要删除菜单的id
                var id = subMenu ? subMenu.id : mainMenu.id;

                this.$confirm('此操作将永久删除该菜单, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    if (id) {
                        ms.http.post(ms.manager + "/mweixin/menu/delete.do", {
                            ids: id
                        }).then(function (res) {
                            if (res.result) {
                                that.$notify({
                                    type: 'success',
                                    title: '成功',
                                    message: '删除成功!'
                                });
                            } else {
                                that.$notify({
                                    type: 'error',
                                    title: '失败',
                                    message: res.msg
                                });
                            }
                            // 清空表单值
                            that.resetForm();
                            // 刷新菜单列表
                            that.menuList();
                        }, function (err) {
                        })
                    } else {
                        //如果是父菜单
                        if (!subMenu) {
                            for (var i = 0; i < that.allMenuList.length; i++) {
                                if (that.allMenuList[i] == mainMenu) {
                                    that.allMenuList.splice(i, 1);
                                    if (that.allMenuList.length) {
                                        //删除之后到其他菜单
                                        that.menuForm = that.allMenuList[0]
                                    } else {
                                        that.menuForm = {};
                                        that.$nextTick(function () {
                                            that.$refs.menuForm.resetFields();
                                        })
                                    }
                                }
                            }
                        } else {
                            // 如果还没保存到数据库直接删除数组中的对象
                            for (var i = 0; i < mainMenu.subMenuList.length; i++) {
                                if (mainMenu.subMenuList[i] == subMenu) {
                                    mainMenu.subMenuList.splice(i, 1);
                                    if (mainMenu.subMenuList.length) {
                                        //删除之后跳转
                                        that.menuForm = mainMenu.subMenuList[0]
                                    } else {
                                        //如果没有子菜单了跳转到父菜单
                                        that.menuForm = mainMenu
                                    }
                                }
                            }
                        }
                    }
                })

            },
            // 清空表单值
            resetForm: function () {
                this.$refs.menuForm.resetFields();
            },
            // 发布菜单
            menuCreate: function () {
                var that = this;
                this.$confirm('此操作将发布公众号菜单, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    ms.http.get(ms.manager + "/mweixin/menu/create.do")
                        .then(function (res) {
                            if (res.result) {
                                that.$notify({
                                    type: 'success',
                                    title: '成功',
                                    message: '发布成功,重新关注即可刷新!'
                                });
                            } else {
                                that.$notify({
                                    type: 'error',
                                    title: '失败',
                                    message: '发布失败！'
                                });
                            }
                        }, function (err) {
                        })
                })
            },
            visible: function (menu) {
                menu.seen = true;
            },
            invisible: function (menu) {
                menu.seen = false;
            },
            /**
             * 验证表单是否完整
             * @returns {boolean}
             */
            isValid: function () {
                var that = this;
               if(this.menuForm.menuStyle=="text") {
                   var isValid = false;
                   //因为$refs.menuForm.validate 方法会重置内容，导致下面替换p标签失效，所以需要提前验证之后再来进行下面的业务
                   that.$refs.menuForm.validate(function (valid) {
                       if (!valid) {
                           that.$notify.error({
                               title: '验证失败',
                               message: '验证失败，请检查菜单内容',
                               type:"warning"
                           })
                       } else {
                           isValid = true;
                       }
                   });
                  return isValid;
               }
               return true;
            }

        },
        mounted: function () {
            this.menuList();
        },

    });
</script>
<style>
    #ms-menu {
        color: #f2f2f6;
        background-color: #fff;
    }

    #ms-menu .hide-menu-content {
        display: none;
    }
    #ms-menu .iconfont {
        font-size: 12px;
    }

    #ms-menu .ms-custom-container {
        display: flex;
        justify-content: space-between;
    }

    #ms-menu .ms-custom-container .el-aside {
        background: #fff;
        width: 320px !important;
        height: 500px !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container {
        overflow: hidden;
        height: 100%;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-header {
        height: 40px !important;
        line-height: 40px !important;
        font-weight: initial;
        font-size: 16px;
        color: #fff;
        text-align: center;
        background: #323232;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-main {
        padding: 0;
        width: 280px !important;
        height: 380px !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer {
        white-space: nowrap;
        padding: 0;
        font-size: 0;
        background-color: #FAFAFA;
        width: 320px !important;
        height: 50px !important;
        display: flex;
        justify-content: flex-start;
        border-top: 1px solid #e6e6e6 !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer > .el-button {
        width: 40px !important;
        height: 50px !important;
        min-width: 40px;
        padding: 0 !important;
        border: none !important;
        border-right: 1px solid #e6e6e6 !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .el-button {
        border-radius: 0 !important;
        height: 50px !important;
        background: transparent !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu {
        width: 280px;
        font-size: 0;
        display: flex;
        justify-content: space-between;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .el-button {
        flex: 1;
        border: none !important;
        background: transparent !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .el-button span {
        color: #333;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu > div {
        width: 100%;
        display: flex;
        justify-content: space-between;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu {
        flex: 1;
        position: relative;
        display: inline-block;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu > .el-button:first-child {
        width: 100%;
        border-right: 1px solid #e6e6e6 !important;
        padding: 10px !important;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
        font-size: 10px;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu .sub-menu-list {
        position: absolute;
        bottom: 60px;
        left: 3%;
        border: 1px solid #e6e6e6 !important;
        width: 94%;
        display: flex;
        justify-content: space-between;
        flex-direction: column;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu .sub-menu-list > button {
        margin-left: 0 !important;
        border: none !important;
        border-bottom: 1px solid #e6e6e6 !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu .sub-menu-list .ms-create-btn {
        border-bottom: none !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu .sub-menu-list .sub-menu-item {
        font-size: 10px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
        padding: 10px !important;
        width: 100%;
        border-bottom: 1px solid #e6e6e6 !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu .sub-menu-list-nopadding .el-button + .el-button {
        margin-left: 0px;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .el-button--default {
        padding: 0 !important;
        flex: 1;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .menu-validate-font + .el-form-item__error {
        margin-top: -2px !important;
        top: 98% !important;
    }

    #ms-menu .ms-custom-container .el-main {
        padding: 0;
        padding-left: 20px;
        flex: 1;
    }


    #ms-menu .ms-custom-container .el-main .ms-menue-change {
        top: 196px;
        left: 124px;
    }

    #ms-menu .ms-custom-container .el-main .ms-menue-change > button:first-child {
        margin-right: 20px;
    }


    #ms-menu .ms-custom-container .el-main .menu-card .ms-menu-content .el-tabs {
        border: 1px solid #e6e6e6;
        margin-bottom: 10px;
    }


    #ms-menu {
        color: #f2f2f6;
    }

    #ms-menu .ms-custom-container {
        display: flex;
        justify-content: space-between;
    }

    #ms-menu .ms-custom-container > .ms-container {
        padding: 0;
        display: flex;
        margin: 12px;
        flex-flow: row nowrap;
        background: 0;
    }

    #ms-menu .ms-custom-container .el-aside {
        background: #fff;
        width: 280px !important;
        border: 1px solid #ddd;
    }

    #ms-menu .ms-custom-container .el-aside .el-container {
        overflow: hidden;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-header {
        height: 40px !important;
        line-height: 40px !important;
        font-weight: initial;
        font-size: 16px;
        color: #fff;
        text-align: center;
        background: #323232;
    }

    .ms-pagination, .ms-tr {
        text-align: right;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-main {
        padding: 0;
        width: 280px !important;
        height: 380px !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer {
        white-space: nowrap;
        padding: 0;
        font-size: 0;
        background-color: #FAFAFA;
        width: 280px !important;
        height: 50px !important;
        display: flex;
        justify-content: flex-start;
        border-top: 1px solid #e6e6e6 !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer > .el-button {
        width: 40px !important;
        height: 50px !important;
        min-width: 40px;
        padding: 0 !important;
        border: none !important;
        border-right: 1px solid #e6e6e6 !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .el-button {
        border-radius: 0 !important;
        height: 50px !important;
        background: 0 0 !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu {
        width: 240px;
        font-size: 0;
        display: flex;
        justify-content: space-between;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .el-button {
        flex: 1;
        border: none !important;
        background: 0 0 !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .el-button span {
        color: #333;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu {
        flex: 1;
        position: relative;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu > .el-button:first-child {
        width: 100%;
        border-right: 1px solid #e6e6e6 !important;
        padding: 10px !important;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu .sub-menu-list {
        position: absolute;
        bottom: 60px;
        border: 1px solid #e6e6e6 !important;
        width: 85%;
        display: flex;
        justify-content: flex-start;
        flex-direction: column;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu .sub-menu-list > button {
        margin-left: 0 !important;
        border: none !important;
        border-bottom: 1px solid #e6e6e6 !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu .sub-menu-list .ms-create-btn {
        border-bottom: none !important;
    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .ms-create-sub-menu .sub-menu-list .sub-menu-item {
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
        padding: 10px !important;

    }

    #ms-menu .ms-custom-container .el-aside .el-container .el-footer .ms-create-menu .el-button--default {
        padding: 0 !important;
        flex: 1
    }

    #ms-menu .ms-custom-container .el-main {
        padding: 0 0 0 20px;
        flex: 1;
    }


    #ms-menu .ms-menu-del {
        position: absolute;
        margin-top: -12px;
        right: 14px;
        transform: translateY(-50%) translateX(100%);
    }

    #ms-menu .el-badge__content {
        right: 14px;
        height: 14px;
        padding: 0px 0px;
        width: 14px;
        border-width: 0px;
        line-height: 14px;
    }

    #ms-menu .iconfont {
        font-size: 12px;
        margin-right: 5px;
    }

    .son-button .el-icon-plus {
        height: 50px;
        padding: 18px 0px;
    }


</style>
</body>
</html>