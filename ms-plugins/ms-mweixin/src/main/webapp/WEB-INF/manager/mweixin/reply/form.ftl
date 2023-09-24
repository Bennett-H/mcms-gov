<!-- 消息发送页面 -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>一键群发</title>
    <#include "/include/head-file.ftl"/>
    <#include "/mweixin/include/head-file.ftl"/>
</head>
<body style="overflow: hidden">
<#include "/mweixin/component/ms-message-reply.ftl">
<div id="group-reply" class="group-reply ms-weixin-content"  v-cloak>
    <el-container>
        <el-header class="ms-header" height="50px">
            <el-row>
                <@shiro.hasPermission name="reply:save"><el-button v-if="menuActive != '群发记录页面'" class="ms-fr" type="primary" size="mini"  @click="messageSend"> <i style="margin-right: 5px;font-size: 12px; " class="iconfont icon-14fabu"></i>发送</el-button></@shiro.hasPermission>
                <el-button v-if="menuActive == '群发记录页面'||menuActive == '单独发送'" class="ms-fr"  size="mini"  @click="back()"> <i class="iconfont icon-fanhui"></i>返回</el-button>
            </el-row>
        </el-header>
        <el-main class="ms-container" width="100%">
            <el-form :model="groupMessageForm" status-icon  ref="groupMessageForm" :rules="groupMessageFormRules"  label-width="100px" size="mini">

                <ms-message-reply ref="msMessageReply" @on-editor-change="onEditorChange"></ms-message-reply>
                <el-input v-model="groupMessageForm.pmContent" style="display: none"></el-input>
            </el-form>
        </el-main>
    </el-container>
</div>
</body>
</html>
<script>
    var groupReplyVue = new Vue({
        el: '#group-reply',
        data:function(){return{
            messageContent:'',//文本消息
            openId:'',//发送给用户的id
            groupMessageForm: {
                pmContent: "",
                pmNewType:"text",
                pmTag:'全体粉丝',
                pmType:'all',
            },
            groupMessageFormRules: {
                pmContent: [
                    {required: true, message: '请输入回复内容', trigger: 'blur'}
                ],
            },

        }},
        computed: {
          menuActive: function () {
            return localStorage.getItem('menuActive')
          }
        }, 
        watch:{

            //值改变重置表单
            openId:function(openId){
                this.groupMessageForm ={
                    pmContent: "",
                    pmNewType:"",
                    pmTag:'全体粉丝',
                    pmType:'all',
                }
            }

        },
        methods: {
          back: function () {
            history.go(-1)
          },
            onEditorChange: function(content) {
                this.groupMessageForm.pmContent = content.text;
                this.groupMessageForm.pmNewType = content.type;
            },

            // 发送消息
            messageSend: function() {
                var that = this;
                //判断是否是群发
                if(!that.openId){
                    //群发
                    that.$confirm('群发一个月最多发送4次，是否继续发送？', '提示 !', {
                        confirmButtomText: '确定',
                        cacelButtomText: '取消',
                        type: 'warning'
                    }).then(function() {
                        ms.http.post(ms.manager + "/mweixin/message/sendAll.do",that.groupMessageForm)
                            .then(function(data){
                                if(data.result > 0){
                                    that.$notify({
                                        title: "成功",
                                        message: "发送成功",
                                        type: 'success'
                                    });
                                }else{
                                    that.$notify({
                                        title: "失败",
                                        message: "发送失败，" + data.msg,
                                        type: 'error'
                                    });
                                }
                            }, function(err) {
                                that.$notify({
                                    title: "失败",
                                    message: err,
                                    type: 'error'
                                });
                            })
                    })
                }else {
                    //发送给单独用户
                    this.groupMessageForm.openId = that.openId
                    ms.http.post(ms.manager + "/mweixin/message/sendToUser.do",that.groupMessageForm)
                        .then(function(data){
                            if(data.result > 0){
                                that.$notify({
                                    title: "成功",
                                    message: "发送成功",
                                    type: 'success'
                                });
                                //weixinVue.menuActive = '微信用户'
                                that.back()
                            }else{
                                that.$notify({
                                    title: "失败",
                                    message: "发送失败，" + data.msg,
                                    type: 'error'
                                });
                            }
                        }, function(err) {
                            that.$notify({
                                title: "失败",
                                message: err,
                                type: 'error'
                            });
                        })
                }
            },
            get: function(id) {
              var that = this
              if(id) {
                ms.http.get(ms.manager + '/mweixin/message/get.do', {id:id}).then(function(res){
                  if( res.result ){
                    //当res.data不为null时赋值
                    if(res.data){
                        var content = {};
                        content.type = res.data.pmNewType;
                        content.text = res.data.pmContent;
                        that.groupMessageForm = res.data;
                        that.$refs.msMessageReply.load(content);
                    }
                  }else{
                      that.$notify({
                          title: "失败",
                          message: "获取失败，" + res.msg,
                          type: 'error'
                      });
                  }
                }).catch(function(err){
                    that.$notify({
                        title: "失败",
                        message: err,
                        type: 'error'
                    });
                })
              }
            }
        },
        created:function() {
          var id = ms.util.getParameter('id')
          //微信用户id
          ms.util.getParameter('openId') ? this.openId = ms.util.getParameter('openId') : ''
          if(id){
            this.get(id)
          }
        }
    })
</script>
<style>
    #group-reply {
      background-color: #fff;
    }

    #group-reply .iconfont{
    	font-size: 12px;
    	margin-right: 5px;
    }
</style>