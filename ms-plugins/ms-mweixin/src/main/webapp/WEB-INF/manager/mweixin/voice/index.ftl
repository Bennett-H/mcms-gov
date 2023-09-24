<!--语音素材页-->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>图文</title>
    <#include "/include/head-file.ftl"/>
    <#include "/mweixin/include/head-file.ftl"/>
</head>
<body style="overflow: hidden">
<#include "/mweixin/file/upload-dialog.ftl">
<div id="voice" class="ms-weixin-content" v-cloak>
    <el-container class="ms-admin-voice">
        <!--右侧头部-->
        <el-header class="ms-header" height="50px">
            <el-row>
                <@shiro.hasPermission name="voice:save"> <el-button type="primary" size="mini" icon="el-icon-plus" @click="fileUploadDialogVue.open('voice')">新增</el-button></@shiro.hasPermission>
                <@shiro.hasPermission name="voice:sync"><el-button  @click='sync' :loading="syncLoading"  size="mini" icon="el-icon-upload" class="ms-fr">同步微信语音</el-button></@shiro.hasPermission>
            </el-row>
        </el-header>
        <el-container>
            <!--内容头部-->
            <el-header class="ms-tr ms-header ms-header-select">
                <el-row type="flex" align='middle' gutter='20' :span='12' justify="end" style="height: 36px;">
<#--                    <el-col :span="3">-->
<#--                        <el-select v-model="picSort" placeholder="请选择" @change="voiceInitData();" size='mini'>-->
<#--                            <el-option v-for="item in selectSort" :key="item.value" :label="item.label" :value="item.value"> </el-option>-->
<#--                        </el-select>-->
<#--                    </el-col>-->
                    <el-col :span="7">
                        <el-input size='mini' placeholder="请输入语音名称" v-model='fileName' class='basic-title-input ms-fr' clearable>
                        </el-input>
                    </el-col>
                    <el-col :span="2" class="ms-weixin-voice-button">
                        <el-button type="primary" icon="el-icon-search" size="mini" class="ms-fr" @click='voiceInitData'>查询</el-button>
                    </el-col>
                </el-row>
                </el-row>
            </el-header>
            <!--素材列表-->
            <el-main class="ms-admin-voice-list">
                <el-table class="ms-table-pagination" :data="voiceList" border="true"   class="tableRowClass" row-class-name='people-tab-row' :max-height="tableHeight">
                    <el-table-column label="" width="80" align="center">
                        <template  slot-scope="scope">
                            <img :src="ms.web+'/mweixin/image/voice.png'" style="border-radius: 50px" min-width="35" height="35" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="fileName" label="文件名" align='left' >
                    </el-table-column>
                    <el-table-column prop="fileMediaId" label="MediaId" align='left' >
                    	<span slot="header">
							<el-popover placement="top-start" title="标题" width="200" trigger="hover" content="微信上传文件后返回编号  ">
								<i class="el-icon-question" slot="reference"></i>
							</el-popover>MediaId
						</span>
                    </el-table-column>
                    <el-table-column prop="createDate" label="更新时间" align="center" width="200" >
                    </el-table-column>
                    <el-table-column
                            align="center"
                            label="操作"
                            width="100">
                        <template slot-scope="scope">
                            <el-button @click="rename(scope.row)" type="text"  size="medium">编辑</el-button>
                            <el-button  type="text" @click="del(scope.row)" size="medium">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination background :page-sizes="[5, 10, 20]" layout="total, sizes, prev, pager, next, jumper" :current-page="currentPage"  :page-size="pageSize"  :total="total" class="ms-pagination" @current-change='currentChange' @size-change="sizeChange">
                </el-pagination>
            </el-main>
        </el-container>
    </el-container>
</div>

<script>
    var voiceVue = new Vue({
        el: "#voice",
        data:function(){return{
            // selectSort: [{
            //     value: '全部语音',
            //     label: '全部语音'
            // }, {
            //     value: '微信语音',
            //     label: '微信语音'
            // }],
            fileForm: {
                fileName:"",
                fileUrl:"",
                fileSize:"",
                fileType:"",
            },
            fileName: "", //搜索语音名称
            picSort: '全部语音',
            voiceList: [], //语音列表
            isLoadMore: true, //是否懒加载
            page: 1, //加载语音的页数
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage:1, //初始页
            fileType:"voice",
            syncLoading:false,
        }},
        watch: {

        },
        computed:{
            //表格最大高度 用来自适应
            tableHeight:function () {
                return document.documentElement.clientHeight - 200;
            }
        },
        methods: {
            picSave: function() {
                fileUploadDialogVue.open('voice');
            },
            rename:function(voice) {
                var that = this;
                this.$prompt( '请输入文件名', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputValue:voice.fileName,
                    inputPattern:/\S/,
                    inputErrorMessage:'文件名不能为空',
                }).then(function(value){
                    voice.fileName = value.value;
                    ms.http.post(ms.manager + "/mweixin/file/update.do",voice
                    ).then(function(res) {
                        if(res.result){
                            that.$notify.success({title:"成功",message:'修改成功'})
                        }else {
                            that.$notify.error({title:"失败",message:res.msg})
                        }

                    }, function(err) {
                        that.$notify.error({title:"失败",message:err});
                    });
            })
            },
            getData:function(){
                var that = this;
                that.voiceList = [];
                ms.http.get(ms.manager + "/mweixin/file/list.do", {
                    fileName: that.fileName,
                    fileType: that.fileType,
                    // isSync:this.picSort=='全部语音'?'':true,
                    pageSize:this.pageSize,
                    pageNo:this.currentPage
                }).then(function(res) {
                    if(res.result){
                        that.voiceList = res.data.rows;
                        that.total = res.data.total
                    }
                }, function(err) {
                    that.$notify.error(err);
                });
            },
            // 删除
            del: function(voice, index) {
                var that = this;
                this.$confirm('您正在执行删除该语音的操作，是否继续？', '提示 !', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function() {
                    var delList = [];
                    delList.push(voice);
                    ms.http.post(ms.manager + "/mweixin/file/delete.do", delList, {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(function(res) {
                        if (res.result) {
                            that.$notify.success('语音删除成功');
                            that.voiceInitData();
                        }
                    }, function(err) {
                        that.$notify.error(err);
                    })
                })
            },
            voiceInitData:function(){
                this.pageSize = 10;
                this.currentPage = 1;
                this.getData();
            },
            //pageSize改变时会触发
            sizeChange:function(pageSize) {
                this.pageSize = pageSize;
                this.getData();
            },
            //currentPage改变时会触发
            currentChange:function(currentPage) {
                this.currentPage = currentPage;
                this.getData();
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
                ms.http.get(ms.manager + "/mweixin/file/syncVoice.do",{
                    fileType: that.fileType,
                }).then(function (res) {
                    if(res.result){
                        that.$notify({
                            title: '成功',
                            message: "同步成功",
                            type: 'success'
                        });
                        that.voiceInitData();
                        that.syncLoading=false;
                    }else {
                        that.$notify.error(res.msg);
                        that.syncLoading=false;
                    }
                }, function (err) {
                    that.$notify.error(err)
                    that.syncLoading=false;
                })
            },
        },
        mounted: function() {
            var that = this;
        },
        created: function (){
            this.getData();
        }
    })
</script>
<style>
    #voice .el-scrollbar__wrap {
        overflow-x: hidden;
    }
    #voice .ms-admin-voice-list .ms-admin-voice-item>div {
        width: 100%;
        display: flex;
        flex-direction: column;
        border-radius: 4px;
    }

    #voice .ms-admin-voice-list .ms-admin-voice-item>div .body div span:hover {
        color: #09f;
        background: #fff;
        border-color: #09f;
    }

    .ms-admin-voice-list .ms-admin-voice-item>div .body img {
        width: 100%;
        height: 130px;
        object-fit: cover;
    }

    #voice .ms-admin-voice-list .ms-admin-voice-show>span {
        padding: 10px;
        display: flex;
        background: #f2f2f6;
        font-weight: 700;
    }

    #voice .ms-admin-voice-list .ms-admin-voice-show>div {
        display: flex;
        align-items: center;
        padding: 10px;
        font-size: 14px;
    }

    #voice .ms-admin-voice-list .ms-admin-voice-show>div span {
        margin-right: auto;
    }

    #voice .ms-admin-voice-list .ms-admin-voice-show>div i {
        margin-left: 10px;
        cursor: pointer;
    }

    #voice .ms-admin-voice-list .ms-admin-voice-show>div i:hover {
        color: #09f;
        background: #fff;
        border-color: #09f;
    }

    #voice .ms-admin-voice-list .ms-admin-voice-show>p {
        margin: 0;
        padding: 10px;
        border: 1px solid #e6e6e6;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
    }

    #voice .ms-admin-voice-list .ms-admin-voice-show>p:hover {
        color: #09f;
        background: #fff;
        border-color: #09f;
    }

    #voice .ms-admin-voice {
        height: 100%;
    }
    #voice .ms-admin-voice .ms-weixin-voice-button {
        width: auto;
    }
    #voice .ms-admin-voice-list {
        background: #fff;
        padding: 15px;
        display: block;
        padding-bottom: 0;
    }
    /* 图文素材 > 语音  分组标签操作icon的样式 */
    #voice  span>button.el-popover__reference{
        margin:0 auto;
    }
    /* 图文素材 > 语音  分组标签的样式End */
</style>
</body>
</html>