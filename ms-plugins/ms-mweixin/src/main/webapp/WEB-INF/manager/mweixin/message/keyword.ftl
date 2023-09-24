<!-- 关键字列表 -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>关键字</title>
    <#include "/include/head-file.ftl"/>
    <#include "/mweixin/include/head-file.ftl"/>
</head>
<body style="overflow: hidden">

<div id="keyword" class="ms-weixin-content" v-cloak>
   <el-container>
      <!--右侧头部-->
      <el-header class="ms-header" height="50px">
         <el-row>
          <el-col span='12'>
              <@shiro.hasPermission name="keywordMessage:save"><el-button type="primary" size="mini" icon="el-icon-plus" @click="handleClick()">新增</el-button></@shiro.hasPermission>
              <@shiro.hasPermission name="keywordMessage:del"><el-button @click="del()" type="danger" icon='el-icon-delete' size="mini">删除</el-button></@shiro.hasPermission>
          </el-col>
         </el-row>
      </el-header>
      <el-container>
         <!--内容头部-->
         <el-header class="ms-tr ms-header">
         <el-row gutter='20' type='flex' align='middle' justify='end' style="height: 38px;">
            <el-col :span='7'>
              <el-input  placeholder="请输入关键字" v-model='keywordForm.pmKey' size='mini'>
              </el-input>
            </el-col>
            <el-col :span='2' style="width: auto">
              <el-button class="ms-fr" type="primary" icon="el-icon-search" size="mini" @click="list">查询</el-button>
            </el-col>
         </el-row>
         </el-header>
         <!--素材列表-->
         <el-main class="ms-container">
   		  <template>
            <el-table class="ms-table-pagination" :data="tableData" border="true" @selection-change="handleSelectionChange" :max-height="tableHeight">
               <el-table-column type="selection" width="50"></el-table-column>
               <el-table-column prop="pmKey" label="关键词"  align='center'>
               </el-table-column>
               <el-table-column prop="pmNewType" width="180" label="消息回复类型" align='center' :formatter='formatter'>
               </el-table-column>
               <el-table-column
                align="center"
                label="操作"
                width="100">
                <template slot-scope="scope">
                    <@shiro.hasPermission name="keywordMessage:edit"> <el-button @click="handleClick(scope.row)" type="text" size="medium">编辑</el-button></@shiro.hasPermission>
                </template>
                </el-table-column>
            </el-table>
            <el-pagination background :page-sizes="[5, 10, 20]" layout="total, sizes, prev, pager, next, jumper" :current-page="keywordForm.currentPage"  :page-size="keywordForm.pageSize"  :total="keywordForm.total" class="ms-pagination" @current-change='currentChange' @size-change="sizeChange">
            </el-pagination>
          </template>
         </el-main>
      </el-container>
   </el-container>
</div>
<script>
   var keywordVue = new Vue({
      el: '#keyword',
       data:function(){return{
           tableData: [],
           keywordForm: {
               pmType: '', //回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发
               pmWeixinId: '', //微信编号
               pmKey: "", //关键词
               pageNo: 0,
               id: "",
               total: 0, //总记录数量
               pageSize: 10, //页面数量
               currentPage: 1, //初始页
               selData: [], //选中列表
           },
       }},
       computed:{
          weixinId: function () {
            return localStorage.getItem('weixinId')
          },
           //表格最大高度 用来自适应
           tableHeight:function () {
               return document.documentElement.clientHeight - 230;
           }
       },
      methods: {
        handleSelectionChange: function(selection) {//列表选中项
          this.selData = selection;
      },
         // 获取关键词列表
         list: function() {
      var that = this;
      that.keywordForm.pageSize =10; //页面数量
       that.currentPage = 1; //初始页
      if(that.keywordForm.pmKey){
              that.keywordForm.pageSize = '';
              that.keywordForm.pageNo = '';
            }
            that.keywordForm.pmType = 'keyword';
            that.keywordForm.pmWeixinId = that.weixinId;

        ms.http.get(ms.manager + "/mweixin/message/list.do",that.keywordForm)
        .then(function(data){
          if(data){
            that.tableData = data.data.rows;
            //passiveMessageFormVue.chooseImgId = data.rows[0].pmContent;
            that.keywordForm.total = data.data.total;
          } else {
            that.tableData = [];
          }
        })
         },
         reset: function() {
          var that = this;
          that.keywordForm.pmKey = "";
         },
         formatter:function (row, column, cellValue, index){
          var type={
              'text': '文本',
              'image': '图片',
              'imageText': '单图文',
              'voice': '语音',
              'video': '视频',
          }
          return type[cellValue] || ''

         },
         del: function() {
          var that = this;
             if(that.selData && that.selData.length > 0 ){
                that.$confirm('确认删除?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(function() {
              ms.http.post(ms.manager + "/mweixin/message/delete.do",that.selData,{
                headers: {
              'Content-Type': 'application/json'
            }
              }).then(function (data) {
                if(data.result){
                    that.$notify({
                        title: '成功',
                        message: "删除成功",
                        type: 'success'
                    });
                  that.list();

                }
              });
          })
         }else {
                 that.$notify({
                     title: '请选择',
                     message: "请选择",
                     type: 'info'
                 });
             }
         },
         handleClick: function(row) {
          var that = this;
          location.href = ms.manager + ( row&&row.id ? '/mweixin/message/keyword/form.do?id='+row.id : '/mweixin/message/keyword/form.do' )
          //weixinVue.menuActive = jump;
          //passiveMessageFormVue.passiveMessageForm = row;
         },
        //pageSize改变时会触发
        sizeChange:function(pageSize) {
            this.pageSize = pageSize;
            this.keywordForm.pageSize = pageSize;
            this.list();
        },
        //currentPage改变时会触发
        currentChange:function(currentPage) {
            this.currentPage = currentPage;
            this.keywordForm.pageNo = currentPage;
            this.list();
        },
      },
      mounted: function() {
        this.list();
      }
   })
</script>
<style>
    #keyword .ms-container{
        height: calc(100vh - 110px);
    }
</style>
</body>
</html>
