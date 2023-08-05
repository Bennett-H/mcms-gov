<html>
<head>
  <meta charset="utf-8" />
  <title>后台主界面</title>
  <#include "../../include/head-file.ftl">
  <link rel="stylesheet" src="${base}/static/plugins/jquery.liMarquee/liMarquee.css">


  <style>
    .str_wrap {
      overflow:hidden;
      width:100%;
      font-size:12px;
      line-height:16px;
      position:relative;
      -moz-user-select: none;
      -khtml-user-select: none;
      user-select: none;
      white-space:nowrap;
      z-index: 9;
    }

    .str_wrap.str_active {
    }
    .str_move {
      white-space:nowrap;
      position:absolute;
      top:0;
      left:0;
      cursor:move;
    }
    .str_move_clone {
      display:inline-block;
      vertical-align:top;
      position:absolute;
      left:100%;
      top:0;
    }
    .str_vertical .str_move_clone {
      left:0;
      top:100%;
    }
    .str_down .str_move_clone {
      left:0;
      bottom:100%;
    }
    .str_vertical .str_move,
    .str_down .str_move {
      white-space:normal;
      width:100%;
    }
    .str_static .str_move,
    .no_drag .str_move,
    .noStop .str_move{
      cursor:inherit;
    }
    .str_wrap img {
      max-width:none !important;
    }
  </style>
</head>
<body class="custom-body">
<div id="app" v-cloak>


  <div  class="class-1" >
    <div  class="class-2" >
      <div  class="class-3" >
        <div  class="class-4" >
        </div>
        <div  class="class-5" >
        </div>
        <div  class="class-6" >
        </div>
        <div  class="panel" style="min-height:300px;overflow:hidden;">
            <div   class="panel-title" style="display:inline;">
              常用功能
            </div>
            <div class="panel-content" style="flex-direction: row;flex-wrap: wrap; flex: unset;margin-top:20px;">
              <div class="mitem"
                   @click="window.parent.indexVue.openParentMenuInId(item.id)"
                   v-for="item in alwaysList">
                <!--图标开始-->
                <i :class="['iconfont',item.icon]"></i>
                <div class="item-title">
                  {{ item.title }}
                </div>
                <!--文本结束-->
              </div>
              <!-- 空状态提示 -->
              <el-empty v-if="alwaysList.length===0" description="暂无常用功能，鼠标移至上方功能大全，点击菜单右侧五角星即可添加到常用功能。" style="width:100%;padding:0;"></el-empty>
            </div>
        </div>
        <span style="color: #333333;
    word-wrap: break-word;
    font-weight: bold;
    display: inline-block;
    animation-duration: 1s;
    font-size: 16px;
    padding-left: 30px;
}">MS平台</span>
        <!--大容器开始-->
        <div  class="class-43" >
          <!--大容器开始-->
          <a  class="class-44" href="http://code.mingsoft.net/#/" target="_blank">
            <!--文本开始-->
            <div class="class-45">
              代码生成器
            </div>
            <!--文本结束-->
            <!--文本开始-->
            <div class="class-46">
              快速生成业务基础功能
            </div>
            <!--文本结束-->
          </a>
          <!--大容器结束-->
          <!--大容器开始-->
          <a  class="class-47" href="https://designer.mingsoft.net/#/" target="_blank">
            <!--文本开始-->
            <div class="class-48">
              皮肤设计器
            </div>
            <!--文本结束-->
            <!--文本开始-->
            <div class="class-49">
              快速进行页面布局设计
            </div>
            <!--文本结束-->
          </a>
          <!--大容器结束-->
          <!--大容器开始-->
          <a  class="class-50" href="http://store.mingsoft.net/#/" target="_blank">
            <!--文本开始-->
            <div class="class-51">
              MStore
            </div>
            <!--文本结束-->
            <!--文本开始-->
            <div class="class-52">
              丰富皮肤与插件
            </div>
            <!--文本结束-->
          </a>
          <!--大容器结束-->
          <!--大容器开始-->
          <a  class="class-53" href="https://www.mingsoft.net" target="_blank">
            <!--文本开始-->
            <div class="class-54">
              更多服务
            </div>
            <!--文本结束-->
            <!--文本开始-->
            <div class="class-55">
              模版定制、业务定制
            </div>
            <!--文本结束-->
          </a>
          <!--大容器结束-->
        </div>



      </div>
    </div>
    <div  class="class-56" >
      <div  class="class-57" >
        <div  class="class-58" >
          <div  class="class-59" >
          </div>
          <div class="class-60 news-list" style="width: 320px; height:50px; padding: 20px 0">
             <ul v-show="liMarquee" >
               <li v-for="(item,index) in msNews" :key="index" style="line-height: 28px">
                 <el-link type="primary" :href="'https://www.mingsoft.net/'+item.id+'.html'" target="_blank">{{item.contentTitle}}</el-link>
               </li>
             </ul>
          </div>
        </div>
      </div>
      <div  class="class-61" @click="jumpMCMSDocument">
        <div  class="class-62" >
        </div>
        <div class="class-63">
          铭飞MCms在线文档
        </div>
      </div>
      <!--小容器开始-->
      <div  class="class-64" >
        <div  class="class-65" @click="enterQQOneGroup">
          <div  class="class-66" >
            <!--图片开始-->
            <img
                    src="${base}/static/images/icon-qq.png"
                    class="class-67" />
            <!--图片结束-->
          </div>
          <div  class="class-68" >
            <div class="class-69">
              铭飞MS平台（一）
            </div>
            <div class="class-70">
              231212174
            </div>
          </div>
        </div>
        <div  class="class-71" @click="enterQQTwoGroup">
          <div  class="class-72" >
            <!--图片开始-->
            <img
                    src="${base}/static/images/icon-qq.png"
                    class="class-73" />
            <!--图片结束-->
          </div>
          <div  class="class-74" >
            <div class="class-75">
              铭飞MS平台（二）
            </div>
            <div class="class-76">
              221335098
            </div>
          </div>
        </div>
        <div  class="class-77" @click="enterQQThreeGroup">
          <div  class="class-78" >
            <!--图片开始-->
            <img
                    src="${base}/static/images/icon-qq.png"
                    class="class-79" />
            <!--图片结束-->
          </div>
          <div  class="class-80" >
            <div class="class-81">
              铭飞MS平台（三）
            </div>
            <div class="class-82">
              242805203
            </div>
          </div>
        </div>
        <div  class="class-83" @click="enterQQFourGroup">
          <div  class="class-84" >
            <!--图片开始-->
            <img
                    src="${base}/static/images/icon-qq.png"
                    class="class-85" />
            <!--图片结束-->
          </div>
          <div  class="class-86" >
            <div class="class-87">
              铭飞MS平台（四）
            </div>
            <div class="class-88">
              881894877
            </div>
          </div>
        </div>
      </div>
      <!--小容器结束-->
      <div  class="class-89" >
        <div  class="class-90" >
          <div class="class-91">
            商务技术支持
          </div>
        </div>
        <div  class="class-92" >
        </div>
        <div  class="class-93" >
        </div>
        <div  class="class-94" >
          <div  class="class-95" >
            <div  class="class-96" >
            </div>
            <div class="class-97">
              功能更丰富
            </div>
          </div>
          <div  class="class-98" >
            <div  class="class-99" >
            </div>
            <div class="class-100">
              审批
            </div>
          </div>
          <div  class="class-101" >
            <div  class="class-102" >
            </div>
            <div class="class-103">
              在线Office
            </div>
          </div>
          <div  class="class-104" >
            <div  class="class-105" >
            </div>
            <div class="class-106">
              远程协助
            </div>
          </div>
          <div  class="class-107" >
            <div  class="class-108" >
            </div>
            <div class="class-109">
              语音协助
            </div>
          </div>
          <div  class="class-110" >
            <div  class="class-111" >
            </div>
            <div class="class-112">
              专属VIP群
            </div>
          </div>
          <div  class="class-113" >
            <div  class="class-114" >
            </div>
            <div class="class-115">
              提供发票
            </div>
          </div>
          <div  class="class-116" >
            <div  class="class-117" >
            </div>
            <div class="class-118">
              终身授权
            </div>
          </div>
          <div  class="class-119" >
            <div  class="class-120" >
            </div>
            <div class="class-121">
              授权证明书
            </div>
          </div>
        </div>
        <div  class="class-122" >
          <!--横分割线开始-->
          <div class="class-123"></div>
          <!--横分割线结束-->
          <!--小容器开始-->
          <div  class="class-124" >
            <div  class="class-125" >
              <div  class="class-126" >
                <!--图片开始-->
                <img
                        src="${base}/static/images/icon-telephone.png"
                        class="class-127" />
                <!--图片结束-->
              </div>
              <div  class="class-128" >
                <div class="class-129">
                  商务电话
                </div>
                <div class="class-130">
                  19970180163
                </div>
              </div>
            </div>
            <div  class="class-131" @click="addBusinessQQ">
              <div  class="class-132" >
                <!--图片开始-->
                <img
                        src="${base}/static/images/icon-qq.png"
                        class="class-133" />
                <!--图片结束-->
              </div>
              <div  class="class-134" >
                <div class="class-135">
                  商务QQ
                </div>
                <div class="class-136">
                  3336073455
                </div>
              </div>
            </div>
          </div>
          <!--小容器结束-->
        </div>
        <div style="padding: 10px;">

          <center>
            <el-link type="primary" target="_blank" href="http://doc.mingsoft.net/mcms/er-ci-kai-fa.html#%E4%BF%AE%E6%94%B9%E4%B8%BB%E7%95%8C%E9%9D%A2" style="align-self: center" icon="el-icon-question">怎么修改当前的页面内容？</el-link>
          </center>

        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
<script src="${base}/static/plugins/jquery/3.6.3/jquery-3.6.3.min.js"></script>
<script src="${base}/static/plugins/jquery.liMarquee/jquery.liMarquee.js"></script>
<script>
  var app = new Vue({
    el: '#app',
    watch: {},
    data: function () {
      return {
        liMarquee:false,
        listTop:0,
        base: ms.base,
        msNews: [],
        msNewsPath: 'https://www.mingsoft.net/',
        alwaysList: [], //常用功能列表
      }
    },
    methods: {
      getAlwaysList: function () {
        var markList = window.parent.indexVue.markList;
        if (markList) {
          this.alwaysList = markList
        }
      },
      setCallBackFun: function () {
        window.parent.indexVue.addCallBackFun(this.getAlwaysList);
      },
      //铭飞开发文档
      jumpMCMSDocument: function () {
        window.open("http://doc.mingsoft.net/mcms/");
      },
      enterQQOneGroup: function () {
        window.open("https://shang.qq.com/wpa/qunwpa?idkey=ebf251dc9758de6b9c78c499956431cba73e28b3f0b72c0fc28242e98b20fca2");
      },
      enterQQTwoGroup: function () {
        window.open("http://shang.qq.com/wpa/qunwpa?idkey=cfb32b0f47d89d7ef1c3a9493984d4ffbdfe14049fdedd90c517a072e90d68b9");
      },
      enterQQThreeGroup: function () {
        window.open("http://shang.qq.com/wpa/qunwpa?idkey=5dd11fdb492c4ded090fa1f78a166583978e33c4a61301b136d31e9e3eb7df72");
      },
      enterQQFourGroup: function () {
        window.open("http://shang.qq.com/wpa/qunwpa?idkey=565f1e4c4fabeee42947f6c6b96ac7ca4853dece16559d3d78e944ca2931b7f5");
      },
      addBusinessQQ: function () {
        window.open("http://wpa.qq.com/msgrd?v=3&uin=3336073455&site=qq&menu=yes");
      },

      getNewsLast: function () {
        var that = this;
        axios.create({
          withCredentials: true
        }).post("https://www.mingsoft.net/cms/content/list.do?categoryId=160&pageSize=999&flag=c").then(function (res) {
          if (res.data.result && res.data.data.total > 0) {
            that.msNews = res.data.data.rows;

          }
        });
      }
    },
    created: function () {
      this.getNewsLast();
      this.getAlwaysList();
    },
    mounted:function () {
      setTimeout("i()",3000)
    }
  });

  function i () {
    $('.news-list').liMarquee({
      direction: 'up',
      scrollamount: 20,
      drag:false
    })
    app.liMarquee=true;
  }
</script>

<style>
  .dowebok { width: 790px; height: 200px; margin: 100px auto; font-size: 14px;}
  .dowebok ul { margin: 0; line-height: 30px;}
  .dowebok a { color: #333; text-decoration: none;}
  .dowebok a:hover { text-decoration: underline;}
</style>
<style>

  .panel {
    margin-top:10px;
    color: #333333;
    padding-right:20px;
    padding-top:20px;
    max-width:100%;
    padding-left:20px;
    outline-offset: -1px;
    background-color: rgba(255, 255, 255, 1);
    flex-direction: column;
    display: flex;
    animation-duration: 1s;
    background-repeat: no-repeat;
  }
  .panel .panel-content .mitem:hover i ,.panel .panel-content .mitem:hover .item-title{
    color:#409EFF
  }
  .panel-content .mitem i {
    font-size: 40px;
  }
  .panel-content .mitem {
    color: #333333;
    cursor: pointer;
    outline-offset: -1px;
    max-width:100%;
    flex-direction: column;
    display: flex;
    animation-duration: 1s;
    width: 25%;
    background-repeat: no-repeat;
    align-items: center;
    margin-bottom:20px;
  }
  .panel .panel-title {
    color: #333333;
    word-wrap: break-word;
    font-weight: bold;
    display: inline-block;
    animation-duration: 1s;
    font-size: 16px;
  }
  .panel .panel-content {
    color: #333333;
    padding-right: 10px;
    padding-bottom: 10px;
    outline-offset: -1px;
    flex: 1;
    padding-top: 10px;
    max-width: 100%;
    flex-direction: column;
    display: flex;
    animation-duration: 1s;
    width: 100%;
    padding-left: 10px;
    background-repeat: no-repeat;
  }
  .panel .panel-content .mitem .item-title{
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
  }
  .custom-body {
  }
  .class-1
  {
    color:#333333;
    padding-right:10px;
    min-height:720px;
    outline:none;
    padding-bottom:10px;
    overflow:visible;
    outline-offset:-1px;
    flex:6;
    padding-top:10px;
    height:100%;
    background-color:rgba(238, 238, 238, 1);
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:100%;
    padding-left:10px;
    background-repeat:no-repeat;
  }
  .class-2
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    flex:7;
    height:100%;
    max-width:100%;
    flex-direction:column;
    display:flex;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-3
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    flex:4;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    flex-direction:column;
    display:flex;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-4
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    margin-left:20px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-5
  {
    color:#333333;
    margin-right:20px;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-6
  {
    color:#333333;
    margin-right:20px;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    margin-left:20px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-7
  {
    color:#333333;
    padding-right:20px;
    outline:none;
    outline-offset:-1px;
    padding-top:20px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    flex-direction:column;
    display:flex;
    animation-duration:1s;
    padding-left:20px;
    background-repeat:no-repeat;
    margin-top:10px;
  }
  .class-8
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:flex-start;
    animation-duration:1s;
    width:100%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-9
  {
    color:#333333;
    word-wrap:break-word;
    font-weight:bold;
    display:inline-block;
    animation-duration:1s;
    font-size:16px;
    line-height:1.4;
  }
  .class-10
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    flex-wrap:wrap;
    max-width:100%;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:100%;
    background-repeat:no-repeat;
  }
  .class-11
  {
    cursor:pointer;
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    text-align:left;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:25%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-12
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    max-width:100%;
    background-color:rgba(17, 205, 110, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-13
  {
    height:30px;
    animation-duration:1s;
    width:30px;
  }
  .class-14
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-15
  {
    cursor:pointer;
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    text-align:left;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:25%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-16
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    max-width:100%;
    background-color:rgba(0, 153, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-17
  {
    height:24px;
    animation-duration:1s;
    width:24px;
  }
  .class-18
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-19
  {
    cursor:pointer;
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    text-align:left;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:25%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-20
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    max-width:100%;
    background-color:rgba(255, 68, 68, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-21
  {
    height:26px;
    animation-duration:1s;
    width:30px;
  }
  .class-22
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-23
  {
    cursor:pointer;
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    text-align:left;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:25%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-24
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:40px;
    max-width:100%;
    background-color:rgba(0, 153, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-25
  {
    height:28px;
    animation-duration:1s;
    width:28px;
  }
  .class-26
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-27
  {
    cursor:pointer;
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    text-align:left;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:25%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-28
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    max-width:100%;
    background-color:rgba(255, 68, 68, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-29
  {
    height:32px;
    animation-duration:1s;
    width:32px;
  }
  .class-30
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-31
  {
    cursor:pointer;
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    text-align:left;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:25%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-32
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    max-width:100%;
    background-color:rgba(17, 205, 110, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-33
  {
    height:32px;
    animation-duration:1s;
    width:32px;
  }
  .class-34
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-35
  {
    cursor:pointer;
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    text-align:left;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:25%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-36
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    max-width:100%;
    background-color:rgba(0, 153, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-37
  {
    height:28px;
    animation-duration:1s;
    width:28px;
  }
  .class-38
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-39
  {
    cursor:pointer;
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    text-align:left;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:25%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-40
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    max-width:100%;
    background-color:rgba(17, 205, 110, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-41
  {
    height:28px;
    animation-duration:1s;
    width:28px;
  }
  .class-42
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-43
  {
    color:#333333;
    padding-right:20px;
    outline:none;
    outline-offset:-1px;
    height:230px;
    max-width:100%;
    flex-direction:row;
    display:flex;
    justify-content:space-between;
    animation-duration:1s;
    width:100%;
    margin-bottom:20px;
    padding-left:20px;
    background-repeat:no-repeat;
    margin-top:20px;
  }
  .class-43 img {
    width: 100%;
  }
  .class-43 a {
    height: unset;
    border-radius: 20px;
  }
  .class-43 img {
    border-radius: 20px;
  }
  .class-44
  {
    color:#333333;
    background-image:url(${base}/static/images/code-bg.png);
    outline:none;
    outline-offset:-1px;
    background-size:cover;
    background-position:center;
    height:200px;
    max-width:100%;
    background-color:rgba(119, 54, 242, 1);
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:24%;
    background-repeat:no-repeat;

  }
  .class-45
  {
    color:#FFFFFF;
    word-wrap:break-word;
    text-shadow:#666 3px 4px 5px;
    font-weight:bold;
    z-index:1;
    display:inline-block;
    animation-duration:1s;
    font-size:32px;
    line-height:1.4;
  }
  .class-46
  {
    color:#FFFFFF;
    word-wrap:break-word;
    text-shadow:#666 3px 4px 5px;
    z-index:1;
    display:inline-block;
    animation-duration:1s;
    font-size:24px;
    line-height:1.4;
    max-width: 100%;
  }
  .class-47
  {
    color:#333333;
    background-image:url(${base}/static/images/designer-bg.png);
    outline:none;
    outline-offset:-1px;
    background-size:cover;
    background-position:center;
    height:200px;
    max-width:100%;
    background-color:rgba(119, 54, 242, 1);
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:24%;
    background-repeat:no-repeat;
  }
  .class-48
  {
    color:#FFFFFF;
    word-wrap:break-word;
    text-shadow:#666 3px 4px 5px;
    font-weight:bold;
    z-index:1;
    display:inline-block;
    animation-duration:1s;
    font-size:32px;
    line-height:1.4;
  }
  .class-49
  {
    color:#FFFFFF;
    word-wrap:break-word;
    text-shadow:#666 3px 4px 5px;
    z-index:1;
    display:inline-block;
    animation-duration:1s;
    font-size:24px;
    line-height:1.4;
    max-width: 100%;
  }
  .class-50
  {
    color:#333333;
    background-image:url(${base}/static/images/store-bg.png);
    outline:none;
    outline-offset:-1px;
    background-size:cover;
    background-position:center;
    height:200px;
    max-width:100%;
    background-color:rgba(119, 54, 242, 1);
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:24%;
    background-repeat:no-repeat;
  }
  .class-51
  {
    color:#FFFFFF;
    word-wrap:break-word;
    text-shadow:#666 3px 4px 5px;
    font-weight:bold;
    z-index:1;
    display:inline-block;
    animation-duration:1s;
    font-size:32px;
    line-height:1.4;
  }
  .class-52
  {
    color:#FFFFFF;
    word-wrap:break-word;
    text-shadow:#666 3px 4px 5px;
    z-index:1;
    display:inline-block;
    animation-duration:1s;
    font-size:24px;
    line-height:1.4;
    max-width: 100%;
  }
  .class-53
  {
    color:#333333;
    background-image:url(${base}/static/images/dev-bg.png);
    outline:none;
    outline-offset:-1px;
    background-size:cover;
    background-position:center;
    height:200px;
    max-width:100%;
    background-color:rgba(119, 54, 242, 1);
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:24%;
    background-repeat:no-repeat;
  }
  .class-54
  {
    color:#FFFFFF;
    word-wrap:break-word;
    text-shadow:#666 3px 4px 5px;
    font-weight:bold;
    z-index:1;
    display:inline-block;
    animation-duration:1s;
    font-size:32px;
    line-height:1.4;
  }
  .class-55
  {
    color:#FFFFFF;
    word-wrap:break-word;
    text-shadow:#666 3px 4px 5px;
    z-index:1;
    display:inline-block;
    animation-duration:1s;
    font-size:24px;
    line-height:1.4;
    max-width: 100%;
  }
  .class-56
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    flex:1;
    height:100%;
    flex-direction:column;
    display:flex;
    min-width:390px;
    margin-left:10px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-57
  {
    cursor: pointer;
    color:#333333;
    outline:none;
    outline-offset:-1px;
    background-color:rgba(253, 246, 236, 1);
    align-items:flex-start;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:100%;
    background-repeat:no-repeat;
    padding: 10px 20px;
  }
  .class-58
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-59
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-voice.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:20px;
    max-width:100%;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:20px;
    background-repeat:no-repeat;
  }
  .class-60
  {
    color:#E6A23C;
    word-wrap:break-word;
    display:inline-block;
    margin-left:10px;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
  }
  .class-61
  {
    cursor:pointer;
    color:#333333;
    padding-right:20px;
    outline:none;
    padding-bottom:20px;
    outline-offset:-1px;
    padding-top:20px;
    height:80px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    padding-left:20px;
    background-repeat:no-repeat;
    margin-top:10px;
  }
  .class-62
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-file.png);
    outline:none;
    outline-offset:-1px;
    height:40px;
    max-width:100%;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-63
  {
    color:#333333;
    word-wrap:break-word;
    font-weight:bold;
    display:inline-block;
    margin-left:20px;
    animation-duration:1s;
    font-size:16px;
    line-height:1.4;
  }
  .class-64
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    flex-wrap:wrap;
    max-width:100%;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    background-repeat:no-repeat;
    margin-top:10px;
  }
  .class-65
  {
    cursor:pointer;
    color:#333333;
    padding-right:15px;
    outline:none;
    padding-bottom:20px;
    outline-offset:-1px;
    padding-top:20px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:50%;
    padding-left:15px;
    background-repeat:no-repeat;
  }
  .class-66
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    align-items:center;
    flex-direction:row;
    display:flex;
    min-width:40px;
    justify-content:center;
    animation-duration:1s;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-67
  {
    animation-duration:1s;
  }
  .class-68
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    margin-left:20px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-69
  {
    color:#999999;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:12px;
    line-height:1.4;
  }
  .class-70
  {
    color:#333333;
    word-wrap:break-word;
    font-weight:bold;
    display:inline-block;
    animation-duration:1s;
    font-size:16px;
    line-height:1.4;
    margin-top:5px;
  }
  .class-71
  {
    cursor:pointer;
    color:#333333;
    padding-right:15px;
    outline:none;
    padding-bottom:20px;
    outline-offset:-1px;
    padding-top:20px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:50%;
    padding-left:15px;
    background-repeat:no-repeat;
  }
  .class-72
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    align-items:center;
    flex-direction:row;
    display:flex;
    min-width:40px;
    justify-content:center;
    animation-duration:1s;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-73
  {
    animation-duration:1s;
  }
  .class-74
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    margin-left:20px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-75
  {
    color:#999999;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:12px;
    line-height:1.4;
  }
  .class-76
  {
    color:#333333;
    word-wrap:break-word;
    font-weight:bold;
    display:inline-block;
    animation-duration:1s;
    font-size:16px;
    line-height:1.4;
    margin-top:5px;
  }
  .class-77
  {
    cursor:pointer;
    color:#333333;
    padding-right:15px;
    outline:none;
    padding-bottom:20px;
    outline-offset:-1px;
    padding-top:20px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:50%;
    padding-left:15px;
    background-repeat:no-repeat;
  }
  .class-78
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    align-items:center;
    flex-direction:row;
    display:flex;
    min-width:40px;
    justify-content:center;
    animation-duration:1s;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-79
  {
    animation-duration:1s;
  }
  .class-80
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    margin-left:20px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-81
  {
    color:#999999;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:12px;
    line-height:1.4;
  }
  .class-82
  {
    color:#333333;
    word-wrap:break-word;
    font-weight:bold;
    display:inline-block;
    animation-duration:1s;
    font-size:16px;
    line-height:1.4;
    margin-top:5px;
  }
  .class-83
  {
    cursor:pointer;
    color:#333333;
    padding-right:15px;
    outline:none;
    padding-bottom:20px;
    outline-offset:-1px;
    padding-top:20px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:50%;
    padding-left:15px;
    background-repeat:no-repeat;
  }
  .class-84
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    align-items:center;
    flex-direction:row;
    display:flex;
    min-width:40px;
    justify-content:center;
    animation-duration:1s;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-85
  {
    animation-duration:1s;
  }
  .class-86
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    margin-left:20px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-87
  {
    color:#999999;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:12px;
    line-height:1.4;
  }
  .class-88
  {
    color:#333333;
    word-wrap:break-word;
    font-weight:bold;
    display:inline-block;
    animation-duration:1s;
    font-size:16px;
    line-height:1.4;
    margin-top:5px;
  }
  .class-89
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    flex:1;
    padding-top:20px;
    height:980px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    flex-direction:column;
    display:flex;
    animation-duration:1s;
    background-repeat:no-repeat;
    margin-top:10px;
  }
  .class-90
  {
    color:#333333;
    margin-right:20px;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:flex-start;
    margin-left:20px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-91
  {
    color:#333333;
    word-wrap:break-word;
    font-weight:bold;
    display:inline-block;
    animation-duration:1s;
    font-size:16px;
    line-height:1.4;
  }
  .class-92
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    flex-wrap:wrap;
    max-width:100%;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:100%;
    background-repeat:no-repeat;
  }
  .class-93
  {
    color:#333333;
    margin-right:20px;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    margin-left:20px;
    animation-duration:1s;
    width:100%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-94
  {
    color:#333333;
    outline:none;
    overflow:visible;
    outline-offset:-1px;
    flex-wrap:wrap;
    max-width:100%;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:100%;
    background-repeat:no-repeat;
  }
  .class-95
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:33%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-96
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-menu.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:30px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:30px;
    background-repeat:no-repeat;
  }
  .class-97
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-98
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:33%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-99
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-approve.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:30px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:30px;
    background-repeat:no-repeat;
  }
  .class-100
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-101
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:33%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-102
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-office.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:30px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:30px;
    background-repeat:no-repeat;
  }
  .class-103
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-104
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:33%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-105
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-remote.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:30px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:30px;
    background-repeat:no-repeat;
  }
  .class-106
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-107
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:33%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-108
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-voiceAssistance.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:30px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:30px;
    background-repeat:no-repeat;
  }
  .class-109
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-110
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:33%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-111
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-vip.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:30px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:30px;
    background-repeat:no-repeat;
  }
  .class-112
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-113
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:33%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-114
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-invoice.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:30px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:30px;
    background-repeat:no-repeat;
  }
  .class-115
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-116
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:33%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-117
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-authorize.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:30px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:30px;
    background-repeat:no-repeat;
  }
  .class-118
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-119
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    align-items:center;
    flex-direction:column;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    width:33%;
    margin-bottom:20px;
    background-repeat:no-repeat;
  }
  .class-120
  {
    color:#333333;
    background-image:url(${base}/static/images/icon-letterOfAuthorization.png);
    outline:none;
    outline-offset:-1px;
    background-size:contain;
    background-position:center;
    height:30px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    justify-content:center;
    animation-duration:1s;
    border-radius:4px;
    width:30px;
    background-repeat:no-repeat;
  }
  .class-121
  {
    color:#333333;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
    margin-top:10px;
  }
  .class-122
  {
    color:#333333;
    box-sizing:center-box;
    outline:none;
    overflow:visible;
    outline-offset:-1px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    flex-direction:column;
    display:flex;
    animation-duration:1s;
    width:100%;
    background-repeat:no-repeat;
    margin-top:10px;
  }
  .class-123
  {
    margin-right:auto;
    animation-duration:1s;
    background-color:rgba(250, 250, 250, 1);
    border-radius:1px;
    width:100%;
    height:1px;
    margin-left:auto;
  }
  .class-124
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    flex-wrap:wrap;
    max-width:100%;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-125
  {
    color:#333333;
    padding-right:20px;
    outline:none;
    padding-bottom:20px;
    outline-offset:-1px;
    flex-wrap:nowrap;
    padding-top:20px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:50%;
    padding-left:20px;
    background-repeat:no-repeat;
  }
  .class-126
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    max-width:100%;
    align-items:center;
    flex-direction:row;
    display:flex;
    min-width:40px;
    justify-content:center;
    animation-duration:1s;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-127
  {
    animation-duration:1s;
  }
  .class-128
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    margin-left:10px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-129
  {
    color:#999999;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
  }
  .class-130
  {
    color:#333333;
    word-wrap:break-word;
    font-weight:bold;
    display:inline-block;
    animation-duration:1s;
    font-size:16px;
    line-height:1.4;
    margin-top:5px;
  }
  .class-131
  {
    cursor:pointer;
    color:#333333;
    padding-right:20px;
    outline:none;
    padding-bottom:20px;
    outline-offset:-1px;
    padding-top:20px;
    max-width:100%;
    background-color:rgba(255, 255, 255, 1);
    align-items:center;
    flex-direction:row;
    display:flex;
    animation-duration:1s;
    width:50%;
    padding-left:20px;
    background-repeat:no-repeat;
  }
  .class-132
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    height:40px;
    align-items:center;
    flex-direction:row;
    display:flex;
    min-width:40px;
    justify-content:center;
    animation-duration:1s;
    width:40px;
    background-repeat:no-repeat;
  }
  .class-133
  {
    animation-duration:1s;
  }
  .class-134
  {
    color:#333333;
    outline:none;
    outline-offset:-1px;
    max-width:100%;
    flex-direction:column;
    display:flex;
    margin-left:10px;
    animation-duration:1s;
    background-repeat:no-repeat;
  }
  .class-135
  {
    color:#999999;
    word-wrap:break-word;
    display:inline-block;
    animation-duration:1s;
    font-size:14px;
    line-height:1.4;
  }
  .class-136
  {
    color:#333333;
    word-wrap:break-word;
    font-weight:bold;
    display:inline-block;
    animation-duration:1s;
    font-size:16px;
    line-height:1.4;
    margin-top:5px;
  }
  @media (max-width: 768px){
    .class-1
    {
      padding-right:12px;
      box-sizing:border-box;
      margin-right:auto;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:12px;
      flex-wrap:wrap;
      padding-top:12px;
      height:200px;
      max-width:100%;
      flex-direction:column;
      margin-left:auto;
      padding-left:12px;
    }
    .class-2
    {
      padding-right:12px;
      box-sizing:border-box;
      margin-right:auto;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:12px;
      flex-wrap:wrap;
      padding-top:12px;
      height:200px;
      margin-left:auto;
      padding-left:12px;
    }
    .class-3
    {
      padding-right:8px;
      box-sizing:border-box;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:8px;
      padding-top:8px;
      height:80px;
      width:80px;
      padding-left:8px;
    }
    .class-45
    {
      text-shadow:#000 3px 4px 5px;
    }
    .class-46
    {
      text-shadow:#000 3px 4px 5px;
      font-size:32px;
    }
    .class-48
    {
      text-shadow:#000 3px 4px 5px;
    }
    .class-49
    {
      text-shadow:#000 3px 4px 5px;
      font-size:32px;
    }
    .class-51
    {
      text-shadow:#000 3px 4px 5px;
    }
    .class-52
    {
      text-shadow:#000 3px 4px 5px;
      font-size:32px;
    }
    .class-54
    {
      text-shadow:#000 3px 4px 5px;
    }
    .class-55
    {
      text-shadow:#000 3px 4px 5px;
      font-size:32px;
    }
    .class-56
    {
      padding-right:8px;
      box-sizing:border-box;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:8px;
      padding-top:8px;
      height:80px;
      max-width:100%;
      width:80px;
      padding-left:8px;
    }
    .class-57
    {
      padding-right:8px;
      box-sizing:border-box;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:8px;
      padding-top:8px;
      max-width:100%;
      width:80px;
      padding-left:8px;
    }
    .class-58
    {
      padding-right:8px;
      box-sizing:border-box;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:8px;
      padding-top:8px;
      height:80px;
      flex-direction:column;
      width:80px;
      padding-left:8px;
    }
    .class-59
    {
      padding-right:8px;
      box-sizing:border-box;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:8px;
      padding-top:8px;
      height:80px;
      flex-direction:column;
      width:80px;
      padding-left:8px;
    }
    .class-60
    {
      color:#333333;
      font-size:16px;
      line-height:1.5;
    }
    .class-65
    {
      padding-right:20px;
      padding-left:20px;
    }
    .class-69
    {
      font-size:14px;
    }
    .class-71
    {
      padding-right:20px;
      padding-left:20px;
    }
    .class-75
    {
      font-size:14px;
    }
    .class-77
    {
      padding-right:20px;
      padding-left:20px;
    }
    .class-83
    {
      padding-right:20px;
      padding-left:20px;
    }
    .class-89
    {
      padding-right:12px;
      box-sizing:border-box;
      margin-right:auto;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:12px;
      flex-wrap:wrap;
      padding-top:12px;
      height:200px;
      margin-left:auto;
      padding-left:12px;
    }
    .class-90
    {
      padding-right:8px;
      box-sizing:border-box;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:8px;
      padding-top:8px;
      height:80px;
      flex-direction:column;
      width:80px;
      padding-left:8px;
    }
    .class-91
    {
      line-height:1.5;
    }
    .class-92
    {
      padding-right:8px;
      box-sizing:border-box;
      outline:1px dashed hsla(0, 0%, 66.7%, .7);
      padding-bottom:8px;
      padding-top:8px;
      height:80px;
      flex-direction:column;
      width:80px;
      padding-left:8px;
    }
  }
  #app{
    overflow-x: hidden;
  }
</style>