<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <#include "/include/head-file.ftl"/>
    <#include "/mweixin/include/head-file.ftl"/>
    <style>
        [v-cloak] {
            display: none;
        }
        #ms-weixin {
          display: flex;
          width: 100%;
          height: 100%;
        }

        #ms-weixin iframe {
          border: 0;
          flex: 6;
        }
        #ms-weixin .ms-weixin-menu {
            min-height: 100vh;
            min-width: 140px;
            flex: 1;
        }

        #ms-weixin .ms-weixin-menu .ms-header {
            border-right: solid 1px #e6e6e6;
            padding: 10px 20px;
        }

        #ms-weixin .ms-weixin-menu .ms-header div {
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: flex-start;
        }
        #ms-weixin .ms-weixin-menu .ms-header div i {
            display: inline-block;
            text-align: center;
            line-height: 1.4em;
            width: 1.4em;
            height: 1.4em;
            border-radius: 4px;
            color: #fff;
            font-size: 1.4em;
            background: #02CF5D;
        }

        #ms-weixin .ms-weixin-menu .ms-header >div>span.el-tooltip.ms-ellipsis{
            height:17px;
            width:100px;
            margin:0 10px;
        }

        #ms-weixin .ms-weixin-menu .el-main {
            padding: 0;
        }

        #ms-weixin .ms-weixin-menu .el-main .ms-weixin-menu-menu {
            min-height: calc(100vh - 50px);
            background: #fff;
        }

        #ms-weixin .ms-weixin-menu .el-main .ms-weixin-menu-menu .el-menu-item {
            min-width: 140px;
            width: 100%;
        }


        #ms-weixin .ms-weixin-menu .el-main .ms-weixin-material-item {
            min-width: 100% !important
        }
        #ms-weixin .ms-weixin-menu-menu-item , .el-submenu__title {
            height: 40px !important;
            line-height: 46px !important;
        }
        .ms-table-pagination {
            height: calc(100% - 70px);
        }
    </style>
</head>
<body style="overflow: hidden">

    <div id="ms-weixin"  v-cloak>
        <!--左侧-->
        <el-container class="ms-weixin-menu">
            <!--右侧头部-->
            <el-header class="ms-header" height="50px">
                <div @click="home">
                    <i class="iconfont icon-weixin" style="cursor:pointer" ></i>
                    <el-tooltip content="${weixinName}" placement="top-end" effect="light">
                    <span class="ms-ellipsis ms-hover">
                       ${weixinName}
                       </span>
                    </el-tooltip> 
                </div>
            </el-header>
            <el-main>
                <el-menu class="ms-weixin-menu-menu" default-active="0-0">
                    <template v-for="(menu,i) in menuList">
                        <!--单个选项-->
                        <el-menu-item :index="i+''" @click="menuActive = menu" v-if="!menu.sub" v-text="menu.title"
                            class="ms-weixin-menu-menu-item"></el-menu-item>
                        <!--多个选项-->
                        <el-submenu :index="i+''" v-if="menu.sub" class="ms-weixin-submenu">
                            <template slot="title">
                                <span v-text="menu.title"></span>
                            </template>
                            <el-menu-item class="ms-weixin-menu-menu-item" @click="menuActive ='';menuActive = sub" :index="i+'-'+index"
                                v-for="(sub,index) in menu.sub" v-text="sub.title"></el-menu-item>
                        </el-submenu>
                    </template>
                </el-menu>
            </el-main>
        </el-container>
        <iframe :src="menuActive.url" name="iframe" class="ms-iframe-style ms-loading"></iframe>
    </div>

    <script>
        var weixinVue = new Vue({
            el: "#ms-weixin",
            data:function(){return{
                menuList: [
                    <@shiro.hasAnyPermissions name="news:view, picture:view,voice:view,video:view" >
                    {
                        title: '素材管理',
                        sub: [

                            <@shiro.hasPermission name="picture:view">
                            {
                                title: '图片',
                                url: ms.manager+'/mweixin/file/picture/index.do'
                            },
                            </@shiro.hasPermission>
                            <@shiro.hasPermission name="voice:view">
                            {
                                title: '语音',
                                url: ms.manager+'/mweixin/file/voice/index.do'
                            },
                            </@shiro.hasPermission>
                            <@shiro.hasPermission name="video:view">
                            {
                                title: '视频',
                                url: ms.manager+'/mweixin/file/video/index.do'
                            }
                            </@shiro.hasPermission>
                        ],
                    },
                    {
                        title: '草稿箱',
                        url: ms.manager+'/mweixin/draft/index.do'
                    },
                    </@shiro.hasAnyPermissions>
                    <@shiro.hasPermission name="weixin:menu:view">
                    {
                        title: '自定义菜单',
                        url: ms.manager+'/mweixin/menu/index.do'
                    },
                    </@shiro.hasPermission>
                    <@shiro.hasPermission name="weixin:people:view">
                    {
                        title: '微信用户',
                        url: ms.manager+'/mweixin/weixinPeople/index.do'
                    },
                    </@shiro.hasPermission>
                    <@shiro.hasAnyPermissions name="followMessage:view, passiveMessage:view,keywordMessage:view" >
                    {
                        title: '自动回复',
                        sub: [
                            <@shiro.hasPermission name="followMessage:view">
                            {
                                title: '关注时回复',
                                url: ms.manager+'/mweixin/message/index.do'
                            },
                            </@shiro.hasPermission>
                            <@shiro.hasPermission name="passiveMessage:view">
                            {
                                title: '被动回复',
                                url: ms.manager+'/mweixin/message/message/index.do'
                            },
                            </@shiro.hasPermission>
                            <@shiro.hasPermission name="keywordMessage:view">
                            {
                                title: '关键词回复',
                                url: ms.manager+'/mweixin/message/keyword.do'
                            }
                            </@shiro.hasPermission>
                        ]
                    },
                    </@shiro.hasAnyPermissions>
                    <@shiro.hasPermission name="reply:view">
                    {
                        title: '群发',
                        sub: [
                            {
                                title: '一键群发',
                                url: ms.manager+'/mweixin/wxGroupMessage/reply/form.do'
                            },
                            {
                                title: '群发记录',
                                url: ms.manager+'/mweixin/wxGroupMessage/reply/index.do'
                            },
                        ]
                    },
                    </@shiro.hasPermission>
                    <@shiro.hasAnyPermissions name="mweixin:qrCode:view" >
                    {
                        title: '场景二维码管理',
                        sub: [
                            <@shiro.hasPermission name="mweixin:qrCode:view">
                            {
                                title: '二维码管理',
                                url: ms.manager+'/mweixin/qrCode/index.do'
                            },
                            </@shiro.hasPermission>
                            <@shiro.hasPermission name="mweixin:qrLog:view">
                            {
                                title: '二维码日志管理',
                                url: ms.manager+'/mweixin/qrLog/index.do'
                            },
                            </@shiro.hasPermission>
                        ]
                    },
                    </@shiro.hasAnyPermissions>
                    <@shiro.hasAnyPermissions name="mweixin:template:view" >
                    {
                        title: '微信消息模板',
                        url: ms.manager+'/mweixin/template/index.do'
                    },
                    </@shiro.hasAnyPermissions>

                ], //左侧导航列表
                menuActive: {
                    title: '图片',
                    url: ms.manager+'/mweixin/file/picture/index.do'
                },       //默认选中
            }},
            watch: {
              'menuActive.title': function (n,o) {
                if(n){
                  localStorage.setItem('menuActive',n)
                }
              }
                //menuActive: function (n,o) {
                //    switch(this.menuActive){
                //        case '关注时回复':
                //        case '被动回复':
                //            messageVue.messageList();
                //            break;
                //        case '关键词回复':
                //            keywordVue.list();
                //            break;
                //        case '图片':
                //            pictureVue.picList();
                //            break;
                //        case '图文':
                //            newsVue.newsList();
                //            break;
                //        case '语音':
                //            voiceVue.voiceInitData();
                //            break;
                //        case '视频':
                //            videoVue.videoInitData();
                //            break;
                //        case '一键群发':
                //            groupReplyVue.newsContent = '';
                //            groupReplyVue.imgContent = '';
                //            groupReplyVue.chooseGraphic = {};
                //            groupReplyVue.messageContent = '';
                //            groupReplyVue.voiceContent = '';
                //            groupReplyVue.videoContent = '';
                //            groupReplyVue.openId = '';
                //            break;
                //        case '群发记录':
                //            groupMessageLogVue.list();
                //            break;
//
                //        default:
                //            break;
                //    }
                //},
            },
            methods:  {
                home: function () {
                    window.location.href = ms.manager + '/mweixin/index.do'
                }
            },
            created: function () {
              localStorage.setItem("weixinId",${weixinId})
                ms.manager+'/mweixin/wxGroupMessage/reply/index.do'

            }
        })
    </script>
</body>
</html>