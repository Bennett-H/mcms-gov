<!--图片素材页-->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>图片</title>
    <#include "/include/head-file.ftl"/>
    <#include "/mweixin/include/head-file.ftl"/>
</head>
<body style="overflow: hidden">
<#include "/mweixin/file/upload-dialog.ftl">
<div id="picture" class="ms-weixin-content" v-cloak>
    <el-container class="ms-admin-picture">
        <!--右侧头部-->
        <el-header class="ms-header" height="50px">
            <el-row>
                <@shiro.hasPermission name="picture:save"><el-button type="primary" size="mini" icon="el-icon-plus" onclick="fileUploadDialogVue.open('picture')">新增</el-button></@shiro.hasPermission>
                <@shiro.hasPermission name="picture:sync"><el-button :loading="syncLoading" @click='sync' size="mini" icon="el-icon-upload" class="ms-fr">同步微信图片</el-button></@shiro.hasPermission>
            </el-row>
        </el-header>
        <el-container>
            <!--内容头部-->
            <el-header class="ms-tr ms-header ms-header-select">
                <el-row type="flex" align='middle' gutter='20' :span='12' justify="end" style="height: 36px;">
<#--                    <el-col :span="3">-->
<#--                        <el-select v-model="picSort" placeholder="请选择" @change="categoryList();picList()" size='mini'>-->
<#--                            <el-option v-for="item in selectSort" :key="item.value" :label="item.label" :value="item.value"> </el-option>-->
<#--                        </el-select>-->
<#--                    </el-col>-->
                    <el-col :span="7">
                        <el-input size='mini'  placeholder="请输入图片名称" v-model='fileName' class='basic-title-input ms-fr' clearable>
                        </el-input>
                    </el-col>
                    <el-col :span="2" class="ms-weixin-picture-button">
                        <el-button type="primary" icon="el-icon-search" size="mini" class="ms-fr" @click='picList'>查询</el-button>
                    </el-col>
                </el-row>
                </el-row>
            </el-header>
            <!--素材列表-->
            <el-main class="ms-admin-picture-list">
                <ms-empty :size="'large'" msg='图片' :click="picSave"   v-if="!pictureList.length"></ms-empty>
                <el-container>
                    <el-aside class="ms-admin-picture-item">
                        <el-scrollbar style="height: calc(100vh - 160px); width:100%;" ref="pictureScoll">
                            <div v-for="(picture,index) in pictureList">
                                <div class="body">
                                    <img class="border-radius-five" :src="ms.web + picture.fileUrl"
				onerror="this.src='${base}/static/mweixin/image/pic-thumbnail.png'" />
                                    <div>
                                        <span v-text="picture.fileName"></span>
                                    </div>
                                </div>
                                <div class="footer">
                                    <@shiro.hasPermission name="picture:save,picture:update">
                                    <i class="el-icon-edit" @click="pictureFormVue.open(picture)"></i>
                                    </@shiro.hasPermission>
                                    <em></em>
                                    <@shiro.hasPermission name="picture:down">
                                    <a :href='ms.web + picture.fileUrl' :download='picture.fileName'>
                                        <i class="el-icon-download"></i></a>
                                    </@shiro.hasPermission>
                                    <em></em>
                                    <@shiro.hasPermission name="picture:del">
                                    <i class="el-icon-delete" @click='del(picture,index)'></i>
                                    </@shiro.hasPermission>
                                </div>
                            </div>
                        </el-scrollbar>
                    </el-aside>
                    <el-main class="ms-admin-picture-show"> <span class="link ms-hover"
		@click="categoryId = '';">全部图片（{{fileTotal}}）</span>
                        <el-scrollbar style="height:360px;">
                            <ul class="pictrue-group">
                                <li class="link ms-hover" @click="categoryId = '';showClickGruoup" >
                                    <span>全部</span>
                                </li>
                                <template v-for="(group,index) of picGroup">
                                    <li class="link ms-hover" @click="categoryId = group.category.id;showClickGruoup" :label="group.category.categoryTitle + '('+group.total+')'" :key="index" @mouseenter="changeShowStatus(index,true)" @mouseleave="changeShowStatus(index,false)"><span>{{group.category.categoryTitle+'('+group.total+')'}}</span>
                                        <div class="operation" style="display: none;">
                                            <sapn class="link">
                                                <ms-create-group :edit="true" :tips="'修改分组'" :id="group.category.id" :category-Title="group.category.categoryTitle" @save-update="updateGroup">
                                                </ms-create-group>
                                            </sapn>
                                            <sapn class="link"> <i class="el-icon-delete" :category-Id="group.category.id" :category-Title="group.category.categoryTitle" @click="groupDel(group.category)"></i></sapn>
                                        </div>
                                    </li>
                                </template>
                                <!--  <el-scrollbar> -->
                            </ul>
                        </el-scrollbar>
                        <ms-create-group @save-update="saveGroup" />
                    </el-main>
                </el-container>
            </el-main>
        </el-container>
    </el-container>
</div>
<#include "/mweixin/picture/form.ftl">
<script>
var pictureVue = new Vue({
    el: "#picture",
    data:function(){return{
        // selectSort: [{
        //     value: '全部图片',
        //     label: '全部图片'
        // }, {
        //     value: '微信图片',
        //     label: '微信图片'
        // }],
        fileName: "", //搜索图片名称
        picSort: '全部图片',
        pictureList: [], //全部照片
        picGroup: [],
        categoryId: '', //当前选择的分组ID
        isLoadMore: true, //是否懒加载
        fileTotal: 0, //图片的总数
        page: 1, //加载图片的页数
        fileType:"image",
        syncLoading:false,
    }},
    watch: {
        categoryId: function(id) {
            this.page = 1;
            this.isLoadMore = true;
            this.picList();
        },
    },
    computed:{

    },
    methods: {
        picSave: function() {
            fileUploadDialogVue.open('picture');
        },
        categoryList: function() {
            var that = this;
            ms.http.get(ms.manager + "/mweixin/file/categoryFile.do", {
                fileType: that.fileType,
            }).then(function(res) {
                if(res.result){
                    that.picGroup = res.data;
                }
            })
        },
        showClickGruoup: function() { //图片分组被点击
            if (this.fileName != '') {
                this.fileName == '';
            }
        },
        getData:function(){
            var that = this;
            ms.http.get(ms.manager + "/mweixin/file/list.do", {
                fileName: that.fileName,
                fileType: that.fileType,
                categoryId:that.categoryId,
                // isSync:this.picSort=='全部图片'?'':true,
                pageSize:10,
                pageNo:this.page
            }).then(function(res) { 
                that.loading = false
                if (res.result) {
                    //只取没过滤分组的数据
                   if(!that.categoryId){
                       that.fileTotal = res.data.total
                   }
                    //追加数据
                    that.pictureList = that.pictureList.concat(res.data.rows);
                } else {
                    //没数据加载禁用懒加载
                    that.isLoadMore = false;
                    //最初加载不显示
                   if(that.page!=1) {
                       that.$notify({
                           title: '成功',
                           message: "没有更多图片加载了!",
                           type: 'success'
                       });
                   }
                }
            });
        },
        picList: function() {
            var that = this;
            that.pictureList =[];
            this.page = 1;
            that.getData();
            <!-- 如果后期需要实现懒加载功能，这是监听滚动条的事件。具体实现可以参考图文（newsVue）的 思想-->
            that.$nextTick(function() {
                that.$refs.pictureScoll.wrap.addEventListener('scroll', that.loadMore);
            });
        },
        changeShowStatus: function(index, val) {
            var dd = $('.operation').eq(index);
            if (val) {
                $(dd).show();
            } else {
                $(dd).hide();
            }
        },
        loadMore: function() {
            var scrollElt = this.$refs.pictureScoll.wrap; //滚动条节点
            <!-- 滚动条所能滚动的高度 = (滚动条滑动的总高度 - 滚动条的高度) -->
            var scrollTop = scrollElt.scrollTop; //滚动条的位置
            var scrollHeight = scrollElt.scrollHeight; //滚动条滑动的总高度
            var scrollsetHeight = scrollElt.offsetHeight; //滚动条高度
            if ((scrollHeight - scrollsetHeight) - scrollTop <10) { //判断滚动条是否到最底部
                if (this.isLoadMore) {
                    this.page += 1;
                   this.getData();
                }
            }
        },
        // 删除
        del: function(picture, index) {
            var that = this;
            this.$confirm('您正在执行删除该照片的操作，是否继续？', '提示 !', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function() {
                var delList = [];
                delList.push(picture);
                ms.http.post(ms.manager + "/mweixin/file/delete.do", delList, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(function(res) {
                    if (res.result) {
                        that.$notify({
                            title: '成功',
                            message: "图片删除成功",
                            type: 'success'
                        });
                        that.picList();
                    }
                })
            });

        },
        // 编辑分组
        updateGroup: function(group) {
            var that = this;
            // 校验成功
            ms.http.post(ms.manager + "/mweixin/category/update.do", {
                categoryTitle: group.categoryTitle,
                id: group.id,
            }).then(function(res) {
                that.popoverEditShow = false
                that.categoryList();
            })
        },
        // 保存分组
        saveGroup: function(groupForm) {
            var that = this;
            // 校验成功
            ms.http.post(ms.manager + "/mweixin/category/save.do", {
                categoryTitle: groupForm.categoryTitle,
            }).then(function(res) {
                that.popoverShow = false
                that.categoryList();
            })
        },
        // 删除分组
        groupDel: function(group) {
            var that = this;
            that.$confirm('您正在执行删除该分组的操作,该操作会导致分组下的照片也会一起被删除，是否继续?', '提示 !', {
                confirmButtomText: '确定',
                cacelButtomText: '取消',
                type: 'warning'
            }).then(function() {
                ms.http.post(ms.manager + "/mweixin/category/delete.do",[group], {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(function(res) {
                    that.picList();
                    that.categoryList();
                });
            });
        },
        // 同步微信素材
        sync: function () {
            var that = this;
            that.syncLoading = true;
            that.$notify({
                title: '提示',
                message: "正在同步，请不要刷新页面，预计需要1-2分钟",
                type: 'warning'
            });
            ms.http.get(ms.manager + "/mweixin/file/syncImages.do").then(function (res) {
                if(res.result){
                    that.$notify({
                        title: '成功',
                        message: "同步成功",
                        type: 'success'
                    });

                    that.syncLoading = false;
                }else {
                    that.$notify({
                        title: '失败',
                        message: res.msg,
                        type: 'error'
                    });
                    that.syncLoading=false;
                }
                that.picList();
            }, function (err) {
                that.syncLoading = false;
            })
        },
    },
    mounted: function() {
        var that = this;
        that.categoryList();
    },
    created: function (){
        this.picList();
    }
})
</script>
<style>
    #picture .el-scrollbar__wrap {
        overflow-x: hidden;
    }
    #picture > .ms-admin-picture-list {
        background: #fff;
        padding: 0 14px 0 0;
        display: flex
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item {
        display: flex;
        flex-wrap: wrap;
        align-items: flex-start;
        width: calc(100% - 220px) !important;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-item>div {
        margin: 0 24px 0 0;
        width: 230px;
        display: flex;
        flex-direction: column;
        border-radius: 4px;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-item>div .body {
        display: flex;
        flex-direction: column;
        line-height: 2em;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-item>div .body div {
        margin-top: .5em;
        display: flex;
        align-items: center;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-item>div .body div span {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: block;
        cursor: pointer;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-item>div .body div span:hover {
        color: #09f;
        background: #fff;
        border-color: #09f;
    }

    .ms-admin-picture-list .ms-admin-picture-item>div .body img {
        width: 100%;
        height: 130px;
        object-fit: cover;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-item>div .footer {
        display: flex;
        padding: 14px 0;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-item>div .footer i {
        color: #333;
        margin: auto;
        cursor: pointer;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-item>div .footer i:hover {
        color: #09f;
        background: #fff;
        border-color: #09f;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-item>div .footer em {
        width: 1px;
        height: 1em;
        margin:0px 25px 0px 25px;
        background: #e6e6e6;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-show {
        min-width: 220px;
        height: 100%;
        margin: -14px 0;
        padding: 14px 0 14px 14px;
        border-left: 1px solid #e6e6e6;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-show>span {
        padding: 10px;
        display: flex;
        background: #f2f2f6;
        font-weight: 700;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-show>div {
        display: flex;
        align-items: center;
        padding-bottom: 10px;
        font-size: 14px;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-show>div span {
        margin-right: auto;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-show>div i {
        margin-left: 10px;
        cursor: pointer;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-show>div i:hover {
        color: #09f;
        background: #fff;
        border-color: #09f;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-show>p {
        margin: 0;
        padding: 10px;
        border: 1px solid #e6e6e6;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-show>p:hover {
        color: #09f;
        background: #fff;
        border-color: #09f;
    }

    #picture .ms-admin-picture {
      height: 100%;
    }
    #picture .ms-admin-picture .ms-weixin-picture-button {
      width: auto;
    }
    #picture .ms-admin-picture-list {
      background: #fff;
      padding: 14px 14px 0 14px;
      display: flex;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item {
      display: flex;
      flex-wrap: wrap;
      align-items: flex-start;
      width: calc(100% - 220px) !important;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item .el-scrollbar__view{
        display: flex;
        flex-flow: row wrap;
        justify-content: flex-start;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item .el-scrollbar__view> div {
      margin: 0 24px 20px 0;
      padding: 10px;
      width: 230px;
      display: flex;
      flex-direction: column;
      border: 1px solid #e6e6e6;
      border-radius: 4px;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item .el-scrollbar__view> div .body {
      display: flex;
      flex-direction: column;
      line-height: 2em;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item .el-scrollbar__view> div .body div {
      margin-top: 0.5em;
      display: flex;
      align-items: center;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item .el-scrollbar__view> div .body div span {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      display: block;
      cursor: pointer;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item .el-scrollbar__view> div .body div span:hover {
      color: #0099ff;
      background: #fff;
      border-color: #0099ff;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item .el-scrollbar__view> div .body img {
      width: 100%;
      height: 130px;
      object-fit: contain;
      object-position: center center;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item .el-scrollbar__view> div .footer {
      display: flex;
      padding: 8px 0;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item > div .footer i {
      color: #333;
      margin: auto;
      cursor: pointer;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item > div .footer i:hover {
      color: #0099ff;
      background: #fff;
      border-color: #0099ff;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-item > div .footer em {
      width: 1px;
      height: 1em;
      background: #e6e6e6;
    }

    #picture .ms-admin-picture-list .ms-admin-picture-show .el-scrollbar__wrap{
        width:100%;
     }

    #picture .ms-admin-picture-list .ms-admin-picture-show .el-scrollbar__view> span {
      padding: 10px;
      display: flex;
      background: #f2f2f6;
      font-weight: bold;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-show .el-scrollbar__view> div.operation {
      display: flex;
      align-items: center;
      padding: 10px;
      font-size: 14px;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-show .el-scrollbar__view> div.operation span {
      margin-right: auto;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-show .el-scrollbar__view> div.operation i {
      margin-left: 10px;
      cursor: pointer;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-show .el-scrollbar__view> div.operation i:hover {
      color: #0099ff;
      background: #fff;
      border-color: #0099ff;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-show .el-scrollbar__view> p {
      margin: 0;
      padding: 10px;
      border: 1px solid #e6e6e6;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
    }
    #picture .ms-admin-picture-list .ms-admin-picture-show .el-scrollbar__view> p:hover {
      color: #0099ff;
      background: #fff;
      border-color: #0099ff;
    }
    #picture .ms-admin-picture-item .el-scrollbar__wrap{
        margin-right: -9px !important;
    }
    /* 图文素材 > 图片  分组标签操作icon的样式 */
    #picture  span>button.el-popover__reference{
        margin:0 auto;
    }
    /* 图文素材 > 图片  分组标签的样式End */
</style>
</body>
</html>