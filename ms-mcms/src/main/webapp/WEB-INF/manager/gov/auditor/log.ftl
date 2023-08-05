<!DOCTYPE html>
<html>
<head>
    <title>用户密码审计日志</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-main class="ms-container">
        <el-table height="calc(100vh - 68px)" v-loading="loading" ref="multipleTable" border :data="dataList" tooltip-effect="dark" @selection-change="handleSelectionChange">
            <template slot="empty">
                {{emptyText}}
            </template>
            <el-table-column label="管理员帐号" width="200"  align="left" prop="managerName" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="管理员昵称"  width="200" align="left" prop="managerNickName" show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="审计原因"   align="left" prop="managerAdmin">
            </el-table-column>
            <el-table-column label="最后修改密码时间" width="200"  align="center" prop="createDate">
                <template slot-scope="scope">
                    <el-tag size="medium" v-if="scope.row.createDate=='' || scope.row.createDate==null" type="warning">管理员未修改过密码</el-tag>
                    <el-tag size="medium" v-else>{{scope.row.createDate}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column width="100" label="状态" align="center" prop="managerLock">
                <template slot-scope="scope">
                    <el-tag size="medium" v-if="scope.row.managerLock==='lock'" type="warning">已锁定</el-tag>
                    <el-tag size="medium" v-else type="success">正常</el-tag>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                background
                :page-sizes="[10,20,30,40,50,100]"
                layout="total, sizes, prev, pager, next, jumper"
                :current-page="currentPage"
                :page-size="pageSize"
                :total="total"
                class="ms-pagination"
                @current-change='currentChange'
                @size-change="sizeChange">
        </el-pagination>
    </el-main>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data:{
            conditionList:[
                //管理员帐号
                {'action':'and', 'field': 'MANAGER_NAME', 'el': 'eq', 'model': 'managerName', 'name': '管理员帐号', 'type': 'input'},
                //管理员昵称
                {'action':'and', 'field': 'MANAGER_NICK_NAME', 'el': 'eq', 'model': 'managerNickName', 'name': '管理员昵称', 'type': 'input'},
                //管理员锁定状态
                {'action':'and', 'field': 'MANAGER_LOCK', 'el': 'eq', 'model': 'managerLock', 'name': '管理员锁定状态', 'type': 'input'},
                //审计原因
                {'action':'and', 'field': 'MANAGER_ADMIN', 'el': 'eq', 'model': 'managerAdmin', 'name': '审计原因', 'type': 'input'},
            ],
            conditions:[],
            dataList: [], //用户审计日志列表
            selectionList:[],//用户审计日志列表选中
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage:1, //初始页
            manager: ms.manager,
            loading: true,//加载状态
            emptyText:'',//提示文字
            //搜索表单
            form:{
                sqlWhere:null,
                // 时间天数
                day: 0,
                // 管理员帐号
                managerName:'',
                // 管理员昵称
                managerNickName:'',
                // 管理员锁定状态
                managerLock:'',
                // 审计原因
                managerAdmin:'',
                // 最后修改密码时间
                createDate: '',
            },
            rules:{
                // 单行文本
                day: [{"type":"number","message":"天数格式不正确"}],

            },
        },
        watch:{

        },
        methods:{
            isChecked: function(row) {
                if(row.del == 3) {
                    return false;
                }
                return true;
            },
            //查询列表
            list: function(isSearch) {
                var that = this;
                var data = {}; //搜索参数
                that.loading = true;
                var page={
                    pageNo: that.currentPage,
                    pageSize : that.pageSize
                }
                var form = JSON.parse(JSON.stringify(that.form))


                if(isSearch) {
                    //删除空字符串
                    for (key in form){
                        if(form[key] === undefined || form[key] === null){
                            delete  form[key]
                        }
                    }
                    form.sqlWhere ? data = Object.assign({}, {sqlWhere: form.sqlWhere}, page) : data = Object.assign({}, form, page)
                } else {
                    data = page;
                }

                data.day = form.day;

                history.replaceState({form:form,page:page},"");
                ms.http.post(ms.manager+"/gov/auditor/auditManager.do",data).then(
                    function(res) {
                        if (!res.result||res.data.total <= 0) {
                            that.emptyText ="暂无数据"
                            that.dataList = [];
                            that.total = 0;
                        } else {
                            that.emptyText = '';
                            that.total = res.data.total;
                            that.dataList = res.data.rows;
                        }
                        that.loading = false;
                    }).catch(function(err) {
                    that.loading = false;
                    console.log(err);
                });

            },
            //用户审计日志列表选中
            handleSelectionChange:function(val){
                this.selectionList = val;
            },
            //pageSize改变时会触发
            sizeChange:function(pagesize) {
                this.loading = true;
                this.pageSize = pagesize;
                this.list(true);
            },
            //currentPage改变时会触发
            currentChange:function(currentPage) {
                this.loading = true;
                this.currentPage = currentPage;
                this.list(true);
            },
            search:function(data){
                this.form.sqlWhere = JSON.stringify(data);
                this.list(true);
            },
            //重置表单
            rest:function(){
                this.currentPage = 1;
                this.form.sqlWhere = null;
                this.$refs.searchForm.resetFields();
                this.list();
            },

        },
        created:function(){
            this.list();
        },
    })
</script>
<style>
    #index .ms-container {
        height: calc(100vh - 78px);
    }
</style>
