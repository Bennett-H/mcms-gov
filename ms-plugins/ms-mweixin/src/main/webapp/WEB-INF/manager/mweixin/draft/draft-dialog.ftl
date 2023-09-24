<!-- 选择图文 -->
<script type="text/x-template" id='draft-tmp'>
    <el-dialog id='draft' title="选择图文" :visible.sync="isShow" custom-class='ms-weixin-dialog border-radius-five'>
        <el-main  v-loading="loading" class="ms-weixin-news-list" style="position:relative">
            <ms-empty :size="'large'" msg='图文' :click="openNew" v-if='!showNewsList.length && !loading'></ms-empty>
                <el-scrollbar style="height: calc(100vh - 150px);width:100%" ref='graphicsScroll'>
            	    <waterfall  :col='3' width="300"  :gutter-Width="gutterWidth" :data="showNewsList">
                        <template v-for="(article,index) of showNewsList">
                        <div class="graphic-model" @click="clickedImageText(article,index)">
                            <div class="ms-weixin-news-item"  :key='index' style="border-radius: 9px;">
                                <div class="head hidden-long-words">
                                    <span v-text="'更新于'+formmateTime(article.updateDate)"></span>
                                    <i class="iconfont icon-weixin" v-if='article.isSync'></i>
                                </div>
                                <div class="body hidden-long-words" style="position: relative">
                                    <img :src="ms.base+article.masterArticle.articleThumbnails" onerror="this.src='${base}/static/mweixin/image/cover.jpg'"/>
                                    <div style="position: absolute; bottom: 0px;line-height: 55px; width: 100%; background-image: linear-gradient(rgba(0, 0, 0, 0), rgba(56, 56, 56, 0.59));color: white;padding: 0px 20px; font-size: 18px;" v-text="article.masterArticle && article.masterArticle.articleTitle"></div>
                                </div>
                                <div v-for="(element,index) in article.articleList" :key="index" class="ms-article-item">
                                    <p class="ms-two-line" v-text='element.articleTitle'></p>
                                    <img :src="imgUrl(element.articleThumbnails)"
                                         onerror="this.src='${base}/static/mweixin/image/thumbnail.jpg'" class="subimg">
                                </div>
                            </div>
                            <div class="clicked-background" v-if="chooseImgText == (index+1)">
                                <i class="iconfont icon-icon"></i>
                            </div>
                          </div>
                        </template>
                    </waterfall>
                </el-scrollbar>
        </el-main>
        <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="onSelect() " size='mini'>确 定</el-button>
            <el-button @click="isShow = false" size='mini'>取 消</el-button>
        </div>
    </el-dialog>
</script>
<script>
    Vue.component('ms-draft', {
        template: '#draft-tmp',
        computed:{
			gutterWidth:function(){
				return (9*0.5*(document.documentElement.clientWidth/375))
			},
            //判断是否是外链接生成图片地址,是则直接返回链接不然使用项目名拼接
            imgUrl:function(url){
                return function (url) {
                    reg = /(http:\/\/|https:\/\/)((\w|=|\?|\.|\/|&|-)+)/g;  //正则表达式判断http：// https：// 为合法
                    objExp = new RegExp(reg);
                    if (reg.test(url)) {
                        return url;
                    }else {
                        return ms.base + url
                    }
                }
            },
            menuActive: function () {
            return localStorage.getItem('menuActive')
          }
        },
        data:function(){return{
            isShow: false,
            showNewsList:[],
            loading:true,//加载状态
            chooseImgText:0,		//选中的图文
            isLoadMore: true,	    //是否懒加载
            page:0,
            publishState:"2",//已发布
        }},
        watch:{
            isShow:function (n,o) {
                if(!n){//取消选中
                    this.chooseImgText = 0
                }
            }
        },
        methods: {
            // 表单打开
            open: function () {
                // this.publishState = state;
                this.isShow = true;
                this.newsList();
            },
            openNew:function (){
                this.isShow = false;
                location.href = ms.manager + "/mweixin/draft/form.do"
            },
            // 获取微信素材
            newsList: function () {
                var that = this;
                //刷新列表
                this.showNewsList = [];
                this.isLoadMore = true; //懒加载功能
                this.page = 1;
                this.getNewsData();//初始化
                this.$nextTick(function () {
                   // that.addScrollEvent();
                    that.$waterfall.resize();		//瀑布流刷新
                });
            },
            clickedImageText: function(article,index){  //图文点击事件处理
            	this.chooseImgText = (index+1);
            	},
            onSelect:function() {  //将选中图文传入显示页面
                this.isShow = false
                var graphicObj = this.showNewsList[this.chooseImgText - 1];
                this.$emit('on-select', graphicObj);

            },
            getNewsData:function(){
                var that = this
                this.loading = true
                ms.http.get(ms.manager + "/mweixin/draft/list.do", {
                    articleTitle: that.articleTitle,
                    publishState: that.publishState,
                    pageSize:10,
                    pageNo:this.page
                }).then(function (res) {
                    that.loading = false
                    if (res.data.rows.length > 0) {
                        //追加数据
                        that.showNewsList = that.showNewsList.concat(res.data.rows);
                        that.$nextTick(function () {
                            that.$waterfall.resize();		//瀑布流刷新
                        });
                    }

                }, function (err) {
                    that.loading = false
                });
            },
            addScrollEvent:function(){		//添加滚动条监听事件
                this.$refs.graphicsScroll.wrap.addEventListener('scroll', this.loadmore);
            },
            loadmore:function(){
                var scrollElt = this.$refs.graphicsScroll.wrap;	//滚动条节点
                <!-- 滚动条所能滚动的高度 = (滚动条滑动的总高度 - 滚动条的高度) -->
                var scrollTop =  scrollElt.scrollTop;		//滚动条的位置
                var scrollHeight = scrollElt.scrollHeight;   	//滚动条滑动的总高度
                var scrollsetHeight = scrollElt.offsetHeight;			//滚动条高度
                if(scrollTop === (scrollHeight-scrollsetHeight)){
                    if(this.isLoadMore){
                        this.page += 1;
                        //加载数据
                        this.getNewsData();
                    }
                }
            },
            formmateTime: function (time) {
                var updateTime = /^[0-9]{4}-[0-9]{2}-[0-9]{2}/.exec(time)
                if (updateTime != null) {
                    return updateTime[0]
                }
            }
        },

        mounted: function() {

        }
    })
</script>
<style>
    #draft .hidden-long-words{/* 让过长的文字隐藏  */
        overflow: hidden;
        text-overflow:ellipsis;
        white-space: nowrap;
    }
    #draft .hidden-long-words p{
        margin: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
        font-weight: initial;
        font-size: 12px;
        color: #999;
    }

    #draft .ms-weixin-dialog.border-radius-five{
        width:950px;
        height:570px;
    }
    #draft .ms-weixin-dialog.border-radius-five .el-dialog__header{
        padding:15px 10px 5px 10px;
    }
    #draft .ms-weixin-dialog.border-radius-five .el-dialog__body{
        height:450px;
        padding:0px 0px;
    }
    #draft .ms-weixin-dialog.border-radius-five .el-dialog__body .el-container{
        height:100%;
    }

    #draft .ms-weixin-dialog.border-radius-five .el-dialog__body .el-aside{
        height:100%;
        border-left:1px solid #e6e6e6;
        padding:10px 5px;
    }

    #draft .ms-weixin-dialog.border-radius-five .el-dialog__body .el-aside .pictrue-group li{
        justify-content: center;
    }

    #draft .ms-weixin-dialog.border-radius-five .el-dialog__body .el-main{
        height:100%;
        padding:0px;
    }

    #draft > .border-radius-five .ms-weixin-dialog  .el-main.ms-weixin-news-list {
        background: #fff;
        margin: 12px;
        padding: 14px;
        display: flex;
        flex-flow: row wrap;
        justify-content: space-between;
        padding: 10px 0px;
        font-size: 10px;
    }
    #draft .ms-weixin-news-item .ms-article-item {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: space-between;
        padding: 10px 0;
        border-top: 1px solid #e6e6e6;
        position: relative;
    }

    #draft .ms-two-line{
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        word-break: break-all;
    }

    #draft .el-main.ms-weixin-news-list  .graphic-model{
        border-top-left-radius: 9px;
        border-top-right-radius: 9px;
        margin: 10px;
        border: 1px solid rgb(237,237,237);
        position: relative;
    }
    #draft .el-main.ms-weixin-news-list  .graphic-model .ms-weixin-news-item{
        padding: 0px 10px;
    }
    #draft .clicked-background{
        background:rgba(127, 127, 127,0.7);
        position: absolute;
        top: 0px;
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 9px;
    }

    #draft .clicked-background i{
        font-size: 52px;
        color: white;
        position: absolute;
    }

    #draft .el-main.ms-weixin-news-list  .graphic-model .head{
        border-bottom: 2px solid rgb(237, 237, 237);
        height: 47px;
        display: flex;
        flex-flow: row nowrap;
        justify-content: space-between;
        align-items: flex-end;
        padding: 6px 0px;
    }
    #draft .el-main.ms-weixin-news-list  .graphic-model .body{
        display: flex;
        flex-flow: column nowrap;
        margin-bottom: 5px;
    }

    #draft .el-main.ms-weixin-news-list  .graphic-model .body span:first-child{
        padding: 10px 0px;
        font-weight: bolder;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: block;
        cursor: pointer;
    }
    #draft .el-main.ms-weixin-news-list  .graphic-model .body img{
        width:100%;
        max-height: 150px;
        border-radius:5px;
    }

    #draft .subimg{
        width: 50px;
        height: 50px;
        margin-right: 10px;
    }
</style>
