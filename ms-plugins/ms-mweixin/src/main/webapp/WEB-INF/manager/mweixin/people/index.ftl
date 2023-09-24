<!-- 微信用户列表 -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>微信用户</title>
    <#include "/include/head-file.ftl"/>
    <#include "/mweixin/include/head-file.ftl"/>
</head>
<body style="overflow: hidden">
<div id="weixin-people" class="ms-weixin-content" v-show="isShow" v-cloak>
   <el-container>
      <!--右侧头部-->
      <el-header class="ms-header" height="50px">
         <el-row>
            <@shiro.hasPermission name="weixin:people:sync"><el-button :loading="syncLoading" class="ms-fr" size="mini" icon="el-icon-upload" @click="synchronousPeople">一键同步</el-button></@shiro.hasPermission>
         </el-row>
      </el-header>
      <el-container>
         <!--内容头部-->
         <el-header class="ms-tr ms-header">
         <el-row type='flex' align='middle' gutter='20'  justify='end'  style="height: 38px;">
            <el-col :span='7'>
               <el-input  placeholder="请输入会员Id" v-model='form.peopleId' maxlength="30" size='mini' clearable>
               </el-input>
            </el-col>
            <el-col :span='7'>
            	<el-input  placeholder="请输入用户openId" v-model='form.openId' maxlength="30" size='mini' clearable>
            	</el-input>
            </el-col>
            <el-col :span='2' style="width: auto; text-align: right;">
               <el-button type="primary" icon="el-icon-search" size="mini" @click="currentChange(1)">查询</el-button>
               <el-button @click="rest" icon="el-icon-refresh" size="mini">重置</el-button>
            </el-col>

         </el-row>

         </el-header>
         <!--素材列表-->
         <el-main class="ms-container">
            <template>
               <el-table class="ms-table-pagination" v-loading="loading" :data="tableData"  border="true"  class="tableRowClass" row-class-name='people-tab-row' :max-height="tableHeight">
                  <el-table-column prop="id" label="会员Id" align='left' width="100">
                  </el-table-column>
                  <el-table-column prop="openId" label="openId" align='left'>
                  </el-table-column>
                  <el-table-column prop="subscribeScene" label="关注来源" align='left' width="150">
                  </el-table-column>
                  <el-table-column prop="subscribe" label="是否关注" :formatter="subscribeFormat" align='center' width="100">
                  </el-table-column>
                  <el-table-column prop="subscribeTime" :formatter="dateFormat" label="关注时间" align='center' width="200">
                  </el-table-column>
<#--                  <el-table-column prop="weixinPeopleProvince" label="用户所在地" align='left' width="200" >-->
<#--                  </el-table-column>-->

                  <el-table-column
                          align="center"
                          label="操作"
                          width="100">
                     <template slot-scope="scope">
                        <el-button @click="handleClick(scope.row)" type="text" size="medium">发送消息</el-button>
                     </template>
                  </el-table-column>
               </el-table>
               <el-pagination background :page-sizes="[50, 100, 200]" layout="total, sizes, prev, pager, next, jumper" :current-page="currentPage"  :page-size="pageSize"  :total="total" class="ms-pagination" @current-change='currentChange' @size-change="sizeChange">
               </el-pagination>
            </template>
         </el-main>

      </el-container>
   </el-container>
</div>
<script>
   var weixinPeopleVue = new Vue({
      el: '#weixin-people',
     data:function(){return{
        tableData: [],
        form: {
           sqlWhere: null,
           openId:'',
           peopleId:''
        },
        total: 0, //总记录数量
        pageSize: 50, //页面数量
        currentPage:1, //初始页
        syncLoading:false,//按钮状态
        loading:false
     }},
      computed:{
        //显示隐藏
        isShow: function () {
          return true
        },
        //当前选中
        menuActive: function () {
          return localStorage.getItem('menuActive')
        },
         //表格最大高度 用来自适应
         tableHeight:function () {
            return document.documentElement.clientHeight - 210;
         }
      },
      methods: {
         //关注格式化
         subscribeFormat:function(row, column) {
            var date = row[column.property];
          return date ? "是":"否"
         },
         //时间格式化
         dateFormat:function(row, column) {
            var date = row[column.property];
            if (date == undefined) {
               return "";
            }
            return moment(date).format("YYYY-MM-DD HH:mm:ss");
         },
         // 获取关键词列表
         list: function(isSearch) {
            var that = this;
            var data = {}; //搜索参数
            that.loading = true;
            var page = {
               pageNo: that.currentPage,
               pageSize: that.pageSize
            }
            var form = JSON.parse(JSON.stringify(that.form))


            if (isSearch) {
               //删除空字符串
               for (var key in form) {
                  if (form[key] === undefined || form[key] === null) {
                     delete form[key]
                  }
               }
               form.sqlWhere ? data = Object.assign({}, {sqlWhere: form.sqlWhere}, page) : data = Object.assign({}, form, page)
            } else {
               data = page;
            }

            history.replaceState({form: that.form, page: page}, "");
            ms.http.get(ms.manager + "/mweixin/weixinPeople/list.do", data)
                    .then(function (data) {
                       if (data.data.rows.length > 0) {
                          that.tableData = data.data.rows;
                          that.total = data.data.total;
                       } else {
                          that.tableData = [];
                       }
                       that.loading = false;
                    }, function (err) {
                       that.$notify.error(err);
                       that.loading = false;
                    })
         },
         // 同步微信用户
         synchronousPeople: function() {
         	var that = this;
            that.syncLoading = true;
            that.$notify({
               title: "提示",
               message: "正在同步，请不要刷新页面，预计需要1-5分钟",
               type: 'warning'
            });
         	ms.http.get(ms.manager + "/mweixin/weixinPeople/importPeople.do")
         	.then(function(data){
         		if(data.result){
                   that.$notify({
                      title: "成功",
                      message: "同步成功!",
                      type: 'success'
                   });
				} else {
                   that.$notify({
                      title: "失败",
                      message: "同步失败!",
                      type: 'error'
                   });
				}
            that.syncLoading = false;
               that.list()
         	},function(err) {
               that.$notify({
                  title: "失败",
                  message: err,
                  type: 'error'
               });
               that.loading = false;
			})
         },
         handleClick: function(row) {
         	//这里暂时使用一键群发页面来发送
            //weixinVue.menuActive = '单独发送'
            localStorage.setItem('menuActive','单独发送')
            row && row.openId ? location.href = ms.manager + '/mweixin/wxGroupMessage/reply/form.do?openId='+row.openId : ''
            //groupReplyVue.openId = row.openId;

         },
         //pageSize改变时会触发
         sizeChange: function (pagesize) {
            this.loading = true;
            this.pageSize = pagesize;
            this.list(true);
         },
         //currentPage改变时会触发
         currentChange: function (currentPage) {
            this.loading = true;
            this.currentPage = currentPage;
            this.list(true);
         },
         search: function (data) {
            this.form.sqlWhere = JSON.stringify(data);
            this.list(true);
         },
         //重置表单
         rest: function () {
            this.currentPage = 1;
            this.form = {
               sqlWhere: null
            };
            this.list();
         },
      },
      mounted: function() {
      	this.list();
      }
   })
</script>
<style>
    #weixin-people .people-tab-row th,
    #weixin-people .people-tab-row td {
      padding: 8px 0;
      min-width: 0;
    }
    #weixin-people .ms-container{
       padding: 0 10px 0 10px;
    }
    #weixin-people .ms-table-pagination {
       height: calc(100vh - 160px);
    }
    #weixin-people .el-container {
      height: 100%;
    }
</style>
</body>
</html>
