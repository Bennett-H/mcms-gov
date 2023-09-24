<!-- 回复表单 -->
<#include "/mweixin/picture/picture-dialog.ftl">
<#include "/mweixin/draft/draft-dialog.ftl">
<#include "/mweixin/voice/voice-dialog.ftl">
<#include "/mweixin/video/video-dialog.ftl">
<script type="text/x-template" id='message-reply'>

 <div >
     <el-tabs  v-model="content.type"   @tab-click="onTabClick" class="message-tabs">
         <!--文字-->
         <el-tab-pane :disabled="disabled" label="文字" name="text" >
             <div class="ms-message-content">
                 <!-- quill-editor富文本 -->
                 <quill-editor ref="quillEditor" v-model="content.text" :options="editorOption"></quill-editor>

                 <div class="footer">
                     <el-popover v-if="!disabled" placement="top-start" width="350" trigger="click" v-model='popoverShow'>
                         <el-form label-width="100px" :model="hyperlinkForm" ref="hyperlinkForm" :rules='hyperlinkRule'>
                             <el-form-item label="文本内容" prop='text'>
                                 <el-input v-model="hyperlinkForm.text" size='mini'></el-input>
                             </el-form-item>
                             <el-form-item label="链接地址" prop='link'>
                                 <el-input v-model="hyperlinkForm.link" size='mini'></el-input>
                             </el-form-item>
                             <el-form-item style="margin:0">
                                 <el-row type='flex' justify='end'>
                                     <el-col span='6' style="margin-right:10px;">
                                         <el-button type="primary" @click="saveLink" size='mini'>保存</el-button>
                                     </el-col>
                                     <el-col span='6'>
                                         <el-button @click="cancelLink" size='mini'>取消</el-button>
                                     </el-col>
                                 </el-row>
                             </el-form-item>
                         </el-form>

                         <span slot="reference"> <el-link type="primary" icon="el-icon-star-off">插入链接</el-link></span>
                     </el-popover>
                 </div>
             </div>
         </el-tab-pane>
         <!--图片-->
         <el-tab-pane :disabled="disabled" label="图片" name="image" style="border-bottom: 1px solid #e6e6e6;">
             <div v-if="content.type=='image'" style="display: flex;flex: 1">
                 <template  v-if="content.obj.fileUrl==''">
                     <div @click="$refs.picture.open()" class="chooseLabel">
                         <i class="el-icon-picture-outline"></i>
                         <span>从素材库选择</span>
                     </div>
                 </template>
                 <template v-else>
                     <div class="ms-message-content">
                         <img :src="ms.base+content.obj.fileUrl" style="width: 360px; margin: 30px;height: 200px "/>
                         <div class="footer" style="line-height: 40px;
    border-top: 1px solid rgb(230, 230, 230);
    padding: 0 10px;">
                             <el-link type="primary" icon="el-icon-delete" @click="onDelete"  v-if="!disabled">删除内容</el-link>
                         </div>
                     </div>

                 </template>
             </div>
         </el-tab-pane>
         <!--图文-->
         <el-tab-pane :disabled="disabled" label="图文" name="imageText">
             <div v-if="content.type=='imageText'" style="display: flex;flex: 1">
                 <template v-if="!content.obj.masterArticle">
                     <div  @click="$refs.draft.open()" class="chooseLabel">
                         <i class="el-icon-picture-outline"></i>
                         <span>从已发布选择</span>
                     </div>
                 </template>
                 <template v-else>
                     <div class="ms-message-content">

                         <div style="display: flex;flex-direction: column;width: 360px;margin: 30px;border: 1px solid #e6e6e6;">
                             <div style="display: flex;flex-direction: column; position: relative">
                                 <img :src="ms.base+content.obj.masterArticle.articleThumbnails" onerror="this.src='${base}/static/mweixin/image/cover.jpg'"
                                      style="max-height: 200px"/>
                                 <div v-text="content.obj.masterArticle && content.obj.masterArticle.articleTitle"
                                      style=" position: absolute;bottom: 0px; line-height: 55px; width: 100%;
                                    background-image:linear-gradient(rgb(0 0 0 / 0%), rgb(56 56 56 / 59%));
                                    color: white;padding: 0px 20px;
                                    font-size: 18px;"></div>
                             </div>
                             <div v-for="(element,index) in content.obj.articleList" :key="index"
                                  style="display: flex;flex-direction: row;border-top: 1px solid #e6e6e6;padding: 20px">
                                 <div style="flex: 1" v-text='element.articleTitle'></div>
                                 <img :src="ms.base+element.articleThumbnails"
                                      onerror="this.src='${base}/static/mweixin/image/thumbnail.jpg'"  style="width: 50px;height: 50px">
                             </div>
                         </div>

                         <div class="footer" style="line-height: 40px;
    border-top: 1px solid rgb(230, 230, 230);
   
    padding: 0 10px;">
                             <el-link type="primary" icon="el-icon-delete" @click="onDelete"  v-if="!disabled">删除内容</el-link>
                         </div>
                     </div>
                 </template>
            </div>
         </el-tab-pane>
         <!--语音-->
         <el-tab-pane :disabled="disabled" label="语音" name="voice">
             <div v-if="content.type=='voice'" style="display: flex;flex: 1">
                 <template  v-if="content.obj.fileUrl==''">
                     <div @click="$refs.voice.open()" class="chooseLabel">
                         <i class="el-icon-picture-outline"></i>
                         <span>从素材库选择</span>
                     </div>
                 </template>

                 <template v-else>
                     <div class="ms-message-content">
                         <audio controls style="margin: 30px ">
                             <source :src="content.obj.fileUrl" type="audio/ogg">
                             <source :src="content.obj.fileUrl" type="audio/mpeg">
                         </audio>
                         <div class="footer" style="line-height: 40px;
    border-top: 1px solid rgb(230, 230, 230);
   
    padding: 0 10px;">
                             <el-link type="primary" icon="el-icon-delete" @click="onDelete"  v-if="!disabled">删除内容</el-link>
                         </div>
                     </div>
                 </template>
            </div>
         </el-tab-pane>
         <!--视频-->
         <el-tab-pane :disabled="disabled" label="视频" name="video"  style="border-bottom: 1px solid #e6e6e6;">
             <div v-if="content.type=='video'" style="display: flex;flex: 1">
                 <template v-if="content.obj.fileUrl==''">
                     <div @click="$refs.video.open()" class="chooseLabel">
                         <i class="el-icon-picture-outline"></i>
                         <span>从素材库选择</span>
                     </div>
                 </template>
                 <template v-else>
                     <div class="ms-message-content">
                         <video :src="content.obj.fileUrl"  controls="controls"  style="margin: 30px ">
                             您的浏览器不支持 video 标签。
                         </video>
                         <div class="footer" style="line-height: 40px;
    border-top: 1px solid rgb(230, 230, 230);
   
    padding: 0 10px;">
                             <el-link type="primary" icon="el-icon-delete" @click="onDelete"  v-if="!disabled">删除内容</el-link>
                         </div>
                     </div>

                 </template>
             </div>
         </el-tab-pane>

     </el-tabs>

     <ms-voice ref="voice" @on-select="onSelect($event)"></ms-voice>
     <ms-video ref="video" @on-select="onSelect($event)"></ms-video>
     <ms-draft ref="draft" @on-select="onSelect($event)"></ms-draft>
     <ms-picture ref="picture" @on-select="onSelect($event)"></ms-picture>
 </div>
</script>
<script>
    Vue.component('ms-message-reply', {
        template: '#message-reply',
        props: {
            publishState:{
                require: false,
                default:"2",//0未发布,1发布中,2已发布,3发布失败
            },
            disabled:{
                require: false,
                default:false,
            },
        },

        data:function() {
            return {
                popoverShow:false,
                //富文本设置
                editorOption: {
                    theme: 'bubble',
                    placeholder: "",
                    modules: {
                        toolbar: [
                            ['bold', 'italic', 'underline', 'strike'],
                            [{
                                'list': 'ordered'
                            }, {
                                'list': 'bullet'
                            }],
                            [{
                                'header': ['text', 'image', 'imageText', 'voice', 'video', false]
                            }],
                            [{
                                'color': []
                            }, {
                                'background': []
                            }],
                            [{
                                'font': []
                            }],
                            [{
                                'align': []
                            }],
                            ['link', 'image'],
                            ['clean']
                        ]
                    }
                },
                hyperlinkForm: {
                    text: "",
                    link: "",
                },
                messageFormRules: {
                    name: [{
                        required: true,
                        message: '请输入菜单名称',
                        trigger: ['blur', 'change']
                    },
                        {
                            min: 1,
                            max: 5,
                            message: '长度在 1 到 5 个字符',
                            trigger: ['blur', 'change']
                        }
                    ],
                },
                hyperlinkRule: {
                    text: [{
                        required: true,
                        message: '请输入超链接显示的文本内容',
                        trigger: 'blur'
                    },
                        {
                            min: 1,
                            max: 50,
                            message: '长度在 1 到 50 个字符',
                            trigger: 'blur'
                        }
                    ],
                    link: [{
                        required: true,
                        message: '请输入超链接地址',
                        trigger: 'change'
                    }, {
                        validator: function (rule, value, callback) {
                            /^(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?$/.test(value) ?
                                callback() : callback('链接不合法')
                        }
                    }],
                },
                text:'',
                content: {
                  type:'text',
                  text:'',
                  obj: {
                      fileUrl:''
                  }
                }


            }
        },
        watch: {
            "content.text": function() {
                if(this.content.type=='text') {
                    var _content = JSON.parse(JSON.stringify(this.content));
                    // _content.text = this.removeP(this.content.text);
                    this.$emit('on-editor-change',_content);
                }
            },
            deep: true
        },
        methods: {
            onTabClick: function (){
                this.content = {
                    type:this.content.type,
                    text:'',
                    obj: {
                        fileUrl:''
                    }
                }
            },

            onDelete:function () {
                var that = this;
                this.$confirm('确认删除当前的内容？', '提示!', {
                    confirmButtomText: '确定',
                    cancelButtomText: '删除',
                    type: 'warning'
                }).then(function () {
                    that.content.text= '';
                    that.content.obj = {
                        fileUrl:''
                    };
                    that.$forceUpdate();
                }).catch(function () {

                });

            },
            onSelect:function (obj) {

                this.content.obj = obj;
                switch (this.content.type) {
                    case "imageText"://图文
                        this.content.text = this.content.obj.id;
                        this.$emit('on-editor-change',this.content);
                        break;
                    case "image"://图片
                        this.content.text = this.content.obj.fileId;
                        this.$emit('on-editor-change',this.content);
                        break;
                    case "text"://文本
                        this.$emit('on-editor-change',this.content.text);
                        break;
                    case "voice"://语音
                        this.content.text = this.content.obj.fileId;
                        this.$emit('on-editor-change',this.content);
                        break;
                    case "video"://视频
                        this.content.text = this.content.obj.id;
                        this.$emit('on-editor-change',this.content);
                        break;
                }
                this.$forceUpdate();

            },
            saveLink: function () {
                var that = this;
                this.$refs.hyperlinkForm.validate(function (boolean, object) {
                    if (boolean) {
                        // 校验成功
                        var quill = that.$refs.quillEditor.quill
                        var index= quill.getSelection(true).index
                        that.$refs.quillEditor.quill.insertEmbed(index,'link',{href:that.hyperlinkForm.link,innerText:that.hyperlinkForm.text},'api')
                        that.cancelLink()
                    }
                })
            },


            cancelLink: function () {
                this.$refs.hyperlinkForm.resetFields();
                this.popoverShow = false
            },
            load:function (content) {
                if(content == null) {
                    content = {type:"text","text":""};
                }
                this.content = content;
                if(!this.content.obj) {
                    this.content.obj = {
                        fileUrl:'',
                        masterArticle:{}
                    }
                }

                var that = this;
                switch (this.content.type) {
                    case "text"://文本类型
                        that.$forceUpdate();
                        break;
                    case "image"://图片类型
                        //获取素材实体
                        ms.http.get(ms.manager + "/mweixin/file/get.do", {
                            fileId: that.content.text,
                        }).then(function (res) {
                                if (res.result) {
                                    that.content.obj = res.data;
                                    that.$forceUpdate();
                                }
                            }
                        )
                        break;
                    case "imageText"://图文类型
                        //获取素材实体
                        ms.http.get(ms.manager + "/mweixin/draft/" + that.content.text + "/get.do")
                            .then(function (res) {
                                if (res.result) {
                                    that.content.obj = res.data;
                                    that.$forceUpdate();
                                }
                            })
                        break;
                    case "voice"://音频类型
                        //获取素材实体
                        ms.http.get(ms.manager + "/mweixin/file/get.do", {
                            fileId: that.content.text,
                        }).then(function (res) {
                                if (res.result) {
                                    that.content.obj = res.data;
                                    that.$forceUpdate();
                                }
                            }
                        )
                        break;
                    case "video":
                        //获取素材实体
                        ms.http.get(ms.manager + "/mweixin/file/get.do", {
                            fileId: that.content.text,
                        }).then(function (res) {
                                if (res.result) {
                                    that.content.obj = res.data;
                                    that.$forceUpdate();
                                }
                            }
                        )
                        break;
                }
            }
        },
        created: function () {//初始赋值

        }
    })
</script>
<style>
.message-tabs{
	flex: 1;
    border-radius: 4px 4px 0 0 !important;
    border: none !important;
	height:100%;
	}
.message-tabs .el-tabs__header{
	margin:0px;
}
.message-tabs .ms-message-content {
    width: 100%;
}
.message-tabs .el-tabs__header .el-tabs__nav-scroll {
    padding: 0 20px;
    border: 1px solid #e6e6e6;
}
/* 回复框内容部分 */
.message-tabs .el-tabs__content {
    border: 1px solid #e6e6e6;
    height: calc(100% - 42px);
    position:relative;
}
.message-tabs .el-tabs__content .el-tab-pane {
  border:0px !important;
  width: 100%;
  height:100%;
  display: flex;
  justify-content: space-between;
}
.message-tabs .el-tabs__content .el-tab-pane > .ms-message-content  {
    width: 100%;
    margin: 0;
}
.message-tabs .el-tabs__content .el-tab-pane > .ms-message-content .footer {
    padding: 0 10px;
    line-height: 40px;
    border-top: 1px solid #e6e6e6;
}
.message-tabs .el-tabs__content .el-tab-pane > .ms-message-content  .ql-container{
	height: 145px;

}

.message-tabs .el-tabs__content .el-tab-pane > span  .show-graphic >.show-image{
	height: 130px;
    display: flex;
    align-items: center;
}

.message-tabs .el-tabs__content .el-tab-pane .chooseLabel{
  flex: 1;
  border: 1px dashed #e6e6e6;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
    cursor: pointer;
    min-height: 200px;
    margin: 10px;
}
.message-tabs .el-tabs__content .el-tab-pane .chooseLabel i{
	font-weight: bolder;
    font-size: 20px;
    color: #09f;
}
.message-tabs .el-tabs__content .el-tab-pane .chooseLabel span{
	margin-top: 8px;
    line-height: 1;
}

.ql-tooltip{
    display: none
}

/* quill富文本内容样式 */
.ql-editor > p{
	display:inline;
}
/* quill富文本内容样式 END */

/* 回复框内样式 */

.show-check-img{
    width: 280px;
    padding: 10px;
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
    justify-content: center;
    position:relative;
    margin: 10px;
}
.show-check-img .show-image-model{
	width: 100%;
    height: 100%;
    display: flex;
    flex-flow: column nowrap;
}
.show-check-img .vedio{
    width: 100%;
    height: 100%;
    display: flex;
    flex-flow: column nowrap;
}

.show-check-img .show-image-model img,
.show-check-img .vedio img{
	width:100%;
	height:85%;
	object-fit: cover;
    object-position: center center;
    border-radius:6px;
}

.show-graphic{
    padding: 0 10px;
    display: flex;
    border-bottom: 1px solid #e6e6e6;
    flex-direction: column;
    line-height: 2em;
    box-shadow: 0px 0px 10px #888;
    border: 1px solid rgb(239, 239, 240);
    max-width: 300px;
}
.show-graphic span{
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: block;
    cursor: pointer;
}
.show-graphic img {
    width: 100%;
    height: 146px;
    margin: 5px auto;
    border-radius: 4px;
    object-fit: contain;
}

.show-graphic p {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    font-weight: initial;
    font-size: 12px;
    color: #999;
}
/* 回复框内样式End */
/* 微信背景log */
.wechat-log{
    display: flex;
    align-items: center;
    justify-content: flex-end;
    position: absolute;
    top: 10px;
    left: 10px;
    width: calc(100% - 20px);
    color: white;
    height: 25% !important;
    background-color: rgba(143, 143, 143,0.6);
    border-top-left-radius: 5px;
    border-top-right-radius: 5px;
}
.wechat-log > i{
	margin-right:10px;
}

.ql-container.ql-bubble:not(.ql-disabled) a:hover::before, .ql-container.ql-bubble:not(.ql-disabled) a:hover::after {
    visibility:hidden;
}

/* 微信背景log End*/
</style>
