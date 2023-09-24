<!-- 新建图文 -->
<!DOCTYPE html>
<html>
<head>
   <meta charset="UTF-8">
   <title>草稿箱</title>
   <#include "/include/head-file.ftl"/>
   <#include "/mweixin/include/head-file.ftl"/>
   <script src="${base}/static/plugins/tinymce/5.10.2/tinymce.min.js"></script>
   <script src="${base}/static/plugins/tinymce-vue/4.0.6-rc/tinymce-vue.min.js"></script>
</head>
<body style="overflow: hidden">
<div id='news-form' class="ms-news-form" v-cloak>
   <el-container class="ms-admin-picture">
      <!--右侧头部-->
      <el-header class="ms-header" height="50px">
         <el-row :gutter="20">
            <el-col :span="12"  >
               <@shiro.hasPermission name="wxDraft:save">
                  <el-button v-if="draftBean.publishState == '0' || draftBean.publishState == '3'"  type="success" size="mini"  @click="importContentDialogVisible = true">
                     <i class="iconfont el-icon-finished"></i>导入文章
                  </el-button>
               </@shiro.hasPermission>
            </el-col>
            <el-col :span="12" style="float: right">
               <!-- 添加隐藏按钮，主要是为了产生间距 -->
               <el-button class="ms-fr" size="mini"  @click="back"><i class="iconfont icon-fanhui"></i>返回</el-button>
               <@shiro.hasPermission name="wxDraft:save">
                  <el-button v-if="draftBean.publishState == '0' || draftBean.publishState == '3'" class="ms-fr" type="primary" size="mini"  @click="save" :loading="saveDisabled"><i class="iconfont icon-icon-"></i>保存</el-button>
               </@shiro.hasPermission>
            </el-col>
         </el-row>
      </el-header>
      <el-container class="ms-container">
         <el-aside width="280px">
            <!-- 主图文章 -->
            <div class="ms-main-article" @click='openMainArticle'>
               <img :src="ms.base+draftBean.masterArticle.articleThumbnails ||　ms.base+'/WEB-INF/manager/images/article-default.png'" onerror="this.src='${base}/static/mweixin/image/cover.jpg'">
               <div class="ms-article-mask" v-show="masterMaskShow">
                  <i class="el-icon-edit"></i>
               </div>
               <span v-text='draftBean.masterArticle.articleTitle'></span>
            </div>
            <!-- 子文章列表 -->
            <draggable v-model="draftBean.articleList" :options="{draggable:'.ms-article-item'}">
               <div v-for="(element,index) in draftBean.articleList" :key="index" class="ms-article-item" @click='updateSubArticle(element,index)'>
                  <p>
                     <span v-text='element.articleTitle'></span>
                  </p>
                  <img :src="ms.base+element.articleThumbnails ||　ms.base+'/WEB-INF/manager/images/article-default-thumb.jpg'"
                       onerror="this.src='${base}/static/mweixin/image/thumbnail.jpg'">
                  <div class="ms-article-item-mask" v-show="childIndexMaskShow == index">
                     <i class="el-icon-edit"></i>
                     <el-tooltip class="item" effect="dark" content="删除图文" placement="bottom">
                        <el-button type="danger" icon="el-icon-delete" circle @click='deleteArticle(element,index)' v-if="draftBean.publishState == '0' || draftBean.publishState == '3'"></el-button>
                     </el-tooltip>
                  </div>
               </div>
            </draggable>
            <div class="ms-article-footer">
               <el-button v-if="draftBean.publishState == '0' || draftBean.publishState == '3'"type="primary"  icon='el-icon-plus' @click='addArticle'>添加图文</el-button>
            </div>
         </el-aside>
         <el-main>
           <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-form :model="articleForm" :rules="rules" ref="articleForm" label-width='90px'>
               <div class="ms-main-header">
                  <div style="display: flex; justify-content: center; align-items: center;flex: 0.2;">
                     <el-upload list-type="picture-card"
                                :show-file-list="false"
                                :on-success="basicPicSuccess" :before-upload='beforeBasicPicUpload'
                                :data="{uploadPath:'/weixin/'+weixinId,'isRename':true ,'appId':true}"
                                accept=".jpg,.jpeg,.png"
                                :action="ms.manager+'/file/upload.do'"
                     >

                        <img :src=" ms.base+articleForm.articleThumbnails"
                             class="article-thumbnail"
                             @mouseover='headMask=true;'
                             @mouseleave='headMask=false;'
                             v-if="articleForm.articleThumbnails" style="max-width: 100%"/>
                        <template v-else="!articleForm.articleThumbnails">
                           <i class="el-icon-picture"></i>
                           <span>添加封面</span>
                        </template>
                     </el-upload>
                  </div>

                  <div style=" flex: 0.95;">
                     <el-row>
                        <el-col :span="24">
                           <el-form-item label="标题" prop="articleTitle">
                              <el-input size='small' placeholder="请输入图文标题" v-model='articleForm.articleTitle' class='basic-title-input'
                                        maxlength="64" minlength="0" show-word-limit>
                              </el-input>
                           </el-form-item>
                        </el-col>
                     </el-row>
                     <el-row>
                        <el-col :span="12">
                           <el-form-item label="作者" prop="articleAuthor">
                              <el-input size='small' placeholder="请输入图文作者" v-model='articleForm.articleAuthor'
                                        maxlength="8" minlength="0" show-word-limit >

                              </el-input>
                           </el-form-item>
                        </el-col>
                     </el-row>
                     <el-row>
                        <el-col :span="24">
                           <el-form-item label="摘要" prop="articleDescription" >
                              <el-input size='small'  type='textarea' placeholder="请输入摘要" :autosize="{ minRows: 3, maxRows: 3}"
                                        resize='none' v-model='articleForm.articleDescription'
                                        maxlength="120" minlength="0" show-word-limit>
                              </el-input>
                           </el-form-item>
                        </el-col>
                     </el-row>
                     <el-row>
                        <el-col :span="24">
                           <el-form-item label="原文链接" prop="articleUrl" >
                              <el-input size='small'  type='small' placeholder="请输入原文链接"
                                         v-model='articleForm.articleUrl'
                                        maxlength="300" minlength="0" >
                              </el-input>
                              <div class="ms-form-tip">
                                 微信里面阅读全文的链接，没有就会回显示阅读全文
                              </div>
                           </el-form-item>
                        </el-col>
                     </el-row>
                  </div>


               </div>
               <div class="ms-main-body">
                  <el-form-item label="" prop="articleContent" style="height: 80vh;display: flex;">
                     <editor v-model="articleForm.articleContent" :init="tinyInit"></editor>
                     <!--利用隐藏表单来满足错误提示-->
                     <el-input v-model="articleForm.articleContent" class="hide-menu-content"
                               style="width:180px!important;"></el-input>
                  </el-form-item>
               </div>
            </el-form>
            </el-scrollbar>
         </el-main>
      </el-container>
   </el-container>

   <el-dialog
           :close-on-click-modal="false"
           title="导入文章"
           :visible.sync="importContentDialogVisible"
           width="80%">
      <iframe src="${managerPath}/mweixin/content/main.do" style="width:100%;height:500px;" frameborder="0" id="importContentIframe"></iframe>
      <span slot="footer" class="dialog-footer">
       <el-button size="mini" @click="importContentDialogVisible = false">取 消</el-button>
       <el-button size="mini" type="primary" @click="importContent">确 定</el-button>
      </span>
   </el-dialog>

</div>
<script>
   var newsFormVue = new Vue({
      el: '#news-form',
      data:function(){return{
         saveDisabled: false,
         //是否显示导入文章的弹窗
         importContentDialogVisible: false,
         tinyInit:[],

         //默认选中主图片master
         masterMaskShow: true,
         childIndexMaskShow: -1,

         articleForm: {
            articleTitle: '', //标题
            articleAuthor: '', //作者
            articleDescription: '', //摘要
            articleContent: '', //正文
            articleThumbnails: '', //上传封面图片的url
            articleUrl:'', //原文链接
         },

         thumbnailShow: false, //显示缩略图
         headMask: false, //缩略图删除

         //素材实体
         draftBean: {
            articleList: [],
            masterArticle: {},
            publishState:'0'
         },

         //表单验证
         rules: {
            articleTitle: [{
               required: true,
               message: '请输入标题',
               trigger: 'blur'
            }
            ],
            articleAuthor: [{
               required: true,
               message: '请输入作者',
               trigger: 'blur'
            }
            ],
            articleDescription: [{
               required: true,
               message: '请输入摘要',
               trigger: 'blur'
            }],
            articleContent: [{
               required: true,
               message: '请输入内容,且长度必须为10-20000',
               trigger: 'change'
            },{"min":10,"max":20000,"message":"长度必须为10-20000"}],
            articleUrl: [{"pattern":/^((https|http|ftp|rtsp|mms){0,1}(:\/\/){0,1})www\.(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/,"message":"原文链接地址必须http或https完整地址"}],
         }
      }},
      computed: {
         weixinId: function () {
            return localStorage.getItem('weixinId')
         },
      },
      components: {
         'editor': Editor,
      },
      // watch: {
      //    articleContent: {
      //       handler: function (n, o) {
      //          this.articleForm.articleContent = n
      //       },
      //       deep: true,
      //    }
      // },
      methods: {
         //返回
         back: function () {
            history.back()
         },
         open: function (id) {
            var that = this
            if(id){
               //根据id查询图文
               ms.http.get(ms.manager+'/mweixin/draft/'+id+'/get.do').then(function(res){
                  if(res.result){
                     that.draftBean = res.data
                     //如果图文素材没有子文章重新初始化
                     if(!that.draftBean.articleList){
                        that.draftBean.articleList =[]
                     }
                     that.$nextTick(function(){
                        this.resetForm()
                     })
                  } else {
                     that.$notify({
                        title: '获取失败',
                        message: res.msg,
                        type: 'warning'
                     });
                  }
               }).catch( function (err) {
                  that.$notify({
                     title: '获取失败',
                     message: err.msg,
                     type: 'warning'
                  });
               })
            }else {
               this.draftBean = {
                  articleList: [],
                  masterArticle: {
                     articleTitle: '', //标题
                     articleAuthor: '', //作者
                     articleDescription: '', //摘要
                     articleContent: '', //正文
                     articleThumbnails: '', //上传封面图片的url
                  },
                  publishState:'0'
               };
               that.$nextTick(function(){
                  this.resetForm()
               })
            }
         },
         //   图片上传之前进行数据校验
         beforeBasicPicUpload: function (file) {
            var fileType = null;
            ['image/jpeg', 'image/png', 'image/jpg'].indexOf(file.type) > -1 ? fileType = true : fileType =
                    false
            var isLt2M = file.size / 1024 / 1024 < 2;
            !fileType &&  this.$notify({
               title: "提示",
               message: '文章配图只能是 JPG、JPEG、PNG 格式!',
               type: 'warning'
            });
            !isLt2M &&  this.$notify({
               title: "提示",
               message: '文章配图大小不能超过 2MB!',
               type: 'warning'
            });
            return fileType && isLt2M;
         },
         //   图片上传成功函数
         basicPicSuccess: function (response) {

            if(response.result){
               this.articleForm.articleThumbnails = response.data;
               this.$forceUpdate();
            }else {
               this.$notify({
                  title: "提示",
                  message: response.msg,
                  type: 'warning'
               });
            }
         },
         deleteArticle:function(data,index){
            var that = this;
            this.$confirm('此操作将永久删除该图文, 是否继续?', '提示', {
               confirmButtonText: '确定',
               cancelButtonText: '取消',
               type: 'warning'
            }).then(function() {
               //将要删除的文章存入集合
               if(data.id){
                  ms.http.post(ms.manager + "/mweixin/article/delete.do",[data],{
                     headers: {
                        'Content-Type': 'application/json'
                     }
                  })
               }
               that.draftBean.articleList.splice(index,1);
            })
         },
         // 添加文章
         addArticle: function () {
            if (this.draftBean.articleList.length > 6) {
               return this.$notify({
                  title: '添加失败',
                  message: '最大图文数量为7',
                  type: 'warning'
               });
            }
            // this.articleContent = ''
            var form={
               articleTitle: '',
               articleAuthor: '',
               articleDescription: '',
               articleContent: '',
               articleThumbnails: '',
            }
            // 将左侧表单的内容存放到数组中
            this.draftBean.articleList.push(form)
            // 清空表单
            this.articleForm = form
            this.thumbnailShow = false //显示上传图标
         },
         // 打开修改子文章
         updateSubArticle: function (element, index) {
            this.masterMaskShow = false;
            this.childIndexMaskShow = index;
            this.articleForm = element
            // 设置百度编辑器
            //this.editor.setContent(this.draftBean.articleList[index].articleContent)
            this.articleForm.articleContent = this.draftBean.articleList[index].articleContent;
         },

         // 打开 主文章
         openMainArticle: function () {
            this.masterMaskShow = true;
            this.childIndexMaskShow = -1;

            this.articleForm = this.draftBean.masterArticle
            // thumbnailShow=true 显示图片，否则显示图标
            this.articleForm.articleThumbnails ? this.thumbnailShow = true : this.thumbnailShow = false
            // 设置百度编辑器
            //this.editor.setContent(this.draftBean.masterArticle.articleContent)
            this.articleForm.articleContent = this.draftBean.masterArticle.articleContent

         },

         //  保存微信文章
         save: function () {
            //   表单校验

            var that = this;
            this.$refs.articleForm.validate(function (pass,object){
               if (pass) {
                  if (Object.keys(that.draftBean.masterArticle).length <=1) {
                     that.$notify({
                        title: '失败',
                        message: "主文章不能为空",
                        type: 'warning'
                     });
                     return;
                  }

                  if (!that.draftBean.masterArticle.articleThumbnails || that.draftBean.masterArticle.articleThumbnails == '' || that.draftBean.masterArticle.articleThumbnails == null){
                     that.$notify({
                        title: '失败',
                        message: "封面不能为空",
                        type: 'warning'
                     });
                     return;
                  }
                  that.saveDisabled = true;
                  //保存或者更新
                  ms.http.post(ms.manager + "/mweixin/draft/saveOrUpdate.do", that.draftBean,{
                     headers: {
                        'Content-Type': 'application/json'
                     }
                  }).then(function (res) {
                     if (res.result) {
                        that.$notify({
                           title: '成功',
                           message: '保存成功',
                           type: 'success',
                           duration: "2000",
                           onClose: function (){
                              that.back();
                           }

                        });
                     } else {
                        that.$notify({
                           title: '失败',
                           message: res.msg,
                           type: 'warning'
                        });
                     }
                     that.saveDisabled = false;
                  }).catch(function (err) {
                     console.err(err);
                     that.saveDisabled = false;
                  })
               } else {
                  return false;
               }

            });
         },

         // 表单重置 新建和修改场景
         resetForm: function () {
            this.thumbnailShow = this.draftBean.id ? true : false
            //默认表单打开第一个文章
            this.articleForm = this.draftBean.masterArticle
            //判断是否存是编辑
            if(this.draftBean.id){
               var result = this.articleForm.articleContent ? this.articleForm.articleContent : ''
               //this.editor.setContent(result)
               this.articleForm.articleContent = result
            }else {
               this.articleForm.articleContent = ''
            }


         },

         //导入文章弹窗保存后的逻辑
         importContent: function () {
            var content = document.getElementById("importContentIframe").contentWindow.document.getElementById("contentIframe").contentWindow.indexVue.getSelectionContents();
            //content 文章内容
            this.articleForm.articleTitle = content.contentTitle
            this.articleForm.articleAuthor = content.contentAuthor
            this.articleForm.articleDescription = content.contentDescription.slice(0,120);
            this.articleForm.articleContent = content.contentDetails
            this.articleForm.contentId = content.id
            var img = JSON.parse(content.contentImg);
            if (img.length > 0 && img[0].path != '' && img[0].path != null){//如果有图片
               this.articleForm.articleThumbnails = img[0].path
            }
            this.importContentDialogVisible = false;
            this.$notify({
               title: '成功',
               message: "导入文章:" +content.contentTitle,
               type: 'success'
            });
         },
      },

      created: function() {
         var id = ms.util.getParameter('id')
         id ? this.open(id) : this.open();
         var that = this;
         //  编辑器配置挂载，在编辑器实例创建完成后进行
         that.tinyInit = {
            //  主程序目录
            base_url: '${base}/static/plugins/tinymce/5.10.2/',
            // 关闭路径转换功能
            convert_urls: false,
            //  语言包路径
            language_url: '${base}/static/plugins/tinymce/5.10.2/zh_CN.js',
            //  语言格式
            language: 'zh_CN',
            //  编辑器高度
            height: '70%',
            //  是否开启头部文字菜单
            menubar: false,
            //placeholder（内容预展示文本）
            placeholder: '在此处编辑内容!',
            //  工具栏展示工具
            toolbar: [
               'undo redo | onKeyFormat fontselect fontsizeselect formatselect | bold italic insertfile underline strikethrough | ' +
               'alignleft aligncenter alignright alignjustify | outdent indent ltr rtl | numlist bullist | ' +
               'forecolor backcolor | pagebreak hr | charmap emoticons removeformat code searchreplace | ' +
               'image   link table anchor codesample insertdatetime | fullscreen preview visualblocks wordcount print'
            ],  //  bdmap
            //  工具栏依赖插件包
            plugins: 'print preview paste importcss searchreplace autolink bdmap directionality code visualblocks visualchars fullscreen image link media template codesample table charmap hr pagebreak nonbreaking anchor insertdatetime advlist lists wordcount textpattern noneditable charmap emoticons',
            //  引入外部样式对编辑器进行样式设置
            content_css: '${base}/static/plugins/tinymce/5.10.2/skins/content/snow/content.css',
            //  编辑器内部自定义样式
            content_style: '.example { text-indent: 2em;width: 100%;overflow: hidden; }' + '.tc {text-align: center}' + 'span {text-indent: initial;}',
            //  字体格式默认值
            fontsize_formats: '12px 14px 16px 18px 24px 36px 48px 56px 72px',
            //  字体风格默认值
            font_formats: '微软雅黑=Microsoft YaHei,Helvetica Neue,PingFang SC,sans-serif;苹果苹方=PingFang SC,Microsoft YaHei,sans-serif;宋体=simsun,serif;仿宋体=FangSong,serif;黑体=SimHei,sans-serif;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats;知乎配置=BlinkMacSystemFont, Helvetica Neue, PingFang SC, Microsoft YaHei, Source Han Sans SC, Noto Sans CJK SC, WenQuanYi Micro Hei, sans-serif;小米配置=Helvetica Neue,Helvetica,Arial,Microsoft Yahei,Hiragino Sans GB,Heiti SC,WenQuanYi Micro Hei,sans-serif',
            //  工具栏模式
            toolbar_mode : 'wrap',

            //  附件标题框的显示
            link_title: false,
            //  当前编辑器上传支持上传类型
            file_picker_types: 'file image media',//   file image media
            video_template_callback: function(data) {
               debugger
               return '<video width="' + data.width + '" height="' + data.height
                       + '"' + (data.poster ? ' poster="' + data.poster + '"' : '')
                       + ' controls="controls" src="' + data.source + '">\n'
                       + '</video>';
            },
            audio_template_callback: function(data) {
               debugger
               return '<audio controls src="' + data.source1 + '"' + (data.source1mime ? ' type="' + data.source1mime + '"' : '') + ' ></audio>';
            },
            //自定义文件选择器的回调内容
            file_picker_callback: function (callback, value, meta) {
               //  创建表单，类型为文件格式
               var input = document.createElement('input');
               input.setAttribute('type', 'file');
               var fileType = '附件'
               //  上传类型为图片时，设置控件上传为image格式
               if(meta.filetype == 'image') {
                  input.setAttribute('accept', 'image/*');
                  fileType = '图片'
                  //  上传类型为media媒体，设置上传格式为video和audio格式
               }else if(meta.filetype == 'media') {
                  input.setAttribute('accept', 'video/*,audio/*');
                  fileType = '媒体'
               }

               input.onchange = function () {
                  var file = this.files[0];
                  var reader = new FileReader();
                  reader.onload = function () {
                     var id = 'blobid' + (new Date()).getTime();
                     var blobCache =  tinymce.activeEditor.editorUpload.blobCache;
                     var base64 = reader.result.split(',')[1];
                     var blobInfo = blobCache.create(id, file, base64);
                     var formData = new FormData();
                     formData.append('file', blobInfo.blob(), file.name);
                     formData.append('uploadPath', '/weixin/'+that.weixinId);
                     formData.append('isRename', true);
                     formData.append('appId', true);
                     //  上传类型为图片
                     ms.http.post(ms.manager + '/file/upload.do', formData,{headers: {
                           'Content-Type': 'multipart/form-data'
                        }}).then(function(res) {
                        //  成功回调设置图片路径为服务器的路径
                        callback(res.data, { title: file.name, text: file.name });
                        that.$notify({
                           title: '成功',
                           message: fileType + '成功上传资源库!',
                           type: 'success'
                        });
                     }).catch(function(err) {
                        //  图片同步上传服务器失败，提示并移除插入的本地图片
                        that.$notify({
                           title: '失败',
                           message: fileType + '上传资源库失败!',
                           type: 'error'
                        });
                     })
                  };
                  reader.readAsDataURL(file);
                  document.body.removeChild(input)
               };
               //添加到body
               document.body.appendChild(input)
               input.click();
            },
         }

      },
      mounted: function () {

      }
   })
</script>
<style>
   #news-form .ms-container .el-main .ms-main-header .el-form-item .el-form-item__content .el-form-item__error {
      margin-top: -11px;
   }
   #news-form .ms-container .el-main .ms-main-header .el-form-item .el-form-item__content .el-textarea + .el-form-item__error {
      margin-top: 0px !important;
   }
   #news-form .ms-container .el-main .ms-main-header .el-form-item .el-form-item__content input {
      margin-bottom: 9px;
      height: 25px;
      line-height: 25px;
   }
   #news-form .ms-container .el-main .ms-main-header .el-form-item .el-form-item__content .el-input__suffix .el-textarea__inner {
      margin-top: 5px;
   }

   #news-form .hide-menu-content {
      display: none;
   }

   #news-form {
      display: flex;
      justify-content: space-between;
      width: 100%;
      background: #fff;
      padding: 0 !important;
   }

   #news-form .ms-main-article:hover {
      cursor: pointer;
   }

   #news-form .ms-main-article .ms-article-mask {
      background: #f2f2f670;
      width: 100%;
      height: 146px;
      position: absolute;
      top: 0;
      left: 0;
      justify-content: center;
      justify-items: center;
      display: flex;
      align-items: center;
   }

   #news-form .ms-main-article .ms-article-mask i{
      font-size: 60px;
      color: #C0C4CC
   }

   #news-form .ms-main-article .ms-article-mask:hover {
      cursor: pointer;
   }

   #news-form .el-container .ms-container {
      padding: 0;
      align-items: flex-start;
      display: flex;
      flex-flow:row nowrap;
      background: 0;
   }

   #news-form .el-container .el-aside {
      padding: 14px;
      background: #fff;
      border: 1px solid #F2F6FC;
      margin-top: 14px;
      margin-left: 14px;
   }

   #news-form .el-container .el-aside .ms-main-article {
      position: relative;
   }

   #news-form .el-container .el-aside .ms-main-article img {
      width: 100%;
      height: 146px;
   }

   #news-form .el-container .el-aside .ms-main-article span {
      position: absolute;
      bottom: 0;
      margin: 15px;
      color: #fff;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      word-break: break-all;
   }

   #news-form .el-container .el-aside .ms-article-item {

      width: 100%;
      height: 70px;
      display: flex;
      justify-content: space-between;
      padding: 10px 0;
      border-bottom: 1px solid #F2F6FC;
      position: relative;
   }

   #news-form .el-container .el-aside .ms-article-item p {
      margin: 0 10px;
      display: flex;
      justify-content: space-between;
      align-items: center
   }

   #news-form .el-container .el-aside .ms-article-item p span {
      width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      word-break: break-all;
   }

   #news-form .el-container .el-aside .ms-article-item img {
      width: 50px;
      height: 50px;
      margin-right: 10px;
   }

   #news-form .el-container .el-aside .ms-article-item .ms-article-item-mask{
      width: 100%;
      height: 100%;
      position: absolute;
      bottom: 0;
      justify-content: space-between;
      align-items: center;
      background-color: #f2f2f670;
      display: flex;
      padding: 0 16px;
   }

   #news-form .el-container .el-aside .ms-article-item .ms-article-item-mask>i {
      font-size: 30px;
      color: #C0C4CC;
      margin-left: 90px;
   }

   #news-form .el-container .el-aside .ms-article-item .ms-article-item-mask>i.el-icon-delete {
      color:#F56C6C;
      cursor: pointer;

   }

   #news-form .el-container .el-aside .ms-article-item:hover .ms-article-item-mask{
      display: flex;
   }

   #news-form .el-container .el-aside .ms-article-footer {
      background: #fff;
      padding-top: 20px
   }

   #news-form .el-container .el-aside .ms-article-footer .el-button {
      width: 100%;
   }



   #news-form .el-container .el-main {
      flex: 1;
      margin-left: 14px;
      padding: 0 !important;
      border-left: 1px solid #E4E7ED;
   }

   #news-form .el-container .el-main .ms-main-header {
      position: relative;
      background: #fff;
      display: flex;
      justify-content: space-between;
      padding: 20px 20px 24px;
      box-sizing: border-box;
      border-bottom: 1px solid #e6e6e6;
   }


   #news-form .el-container .el-main .ms-main-header .el-form {
      flex: 1;
      margin: 0 !important;
      display: flex;
      justify-content: space-between;
      flex-direction: column;
      padding-left: 20px;
   }

   #news-form .el-container .el-main .ms-main-header .el-form .el-form-item
   {
      margin-bottom: 0 !important;
   }


   #news-form .el-container .el-main .ms-main-header .el-form .el-form-item .el-input__suffix>input
   {
      padding-right: 50px !important
   }

   #news-form .el-container .el-main .ms-main-header .el-form .el-form-item .basic-title-input>.el-input__inner
   {
      padding-right: 54px !important
   }

   #news-form .el-container .el-main .ms-main-header .el-form .el-form-item:last-child
   {
      padding-top: 4px
   }

   #news-form .el-container .el-main .ms-main-body {
      height: calc(100vh - 275px);
      background: #fff;
      padding:20px
   }

   #news-form .el-container .el-main .ms-main-body .el-form-item__error {
      color: #F56C6C;
      font-size: 12px;
      line-height: 1;
      padding-top: 4px;
      position: absolute;
      top: 70%;
      left: 0;
   }

   #news-form .el-container .el-main .ms-main-body .edui-editor-toolbarbox
   {
      border: none !important;
      box-shadow: none !important
   }

   #news-form .el-container .el-main .ms-main-body .edui-default .edui-editor
   {
      border: none !important
   }

   #news-form .el-container .el-main .ms-main-body .edui-default .edui-editor .edui-editor-toolbarboxouter
   {
      background-color: none !important;
      background-image: none !important;
      box-shadow: none !important;
      border-bottom: none !important
   }

   #news-form .el-container .el-main .ms-main-body .edui-default .edui-editor .edui-editor
   {
      border: none !important
   }

   #news-form .el-container .el-main .ms-main-body .edui-default .edui-editor .edui-editor .edui-editor-toolbarbox
   {
      box-shadow: none !important
   }

   #news-form .el-container .el-main .ms-main-body .edui-default .edui-editor .edui-editor .edui-editor-toolbarbox .edui-editor-toolbarboxouter
   {
      background-color: transparent !important;
      background-image: none !important;
      border: none !important;
      border-radius: 0 !important;
      box-shadow: none !important
   }

   #news-form .el-container .el-main .ms-main-body .edui-editor-toolbarboxinner
   {
      background: #fff;
   }
   #news-form .iconfont{
      font-size: 12px;
      margin-right: 5px;
   }

   #news-form .ms-main-body .el-form-item__content {
      margin-left: unset!important;
      width: 100%;
   }
   #news-form .el-form-item {
      margin-bottom: 10px;
   }
</style>
</body>
</html>
