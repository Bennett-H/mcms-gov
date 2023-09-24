<!DOCTYPE html>
<html>
<head>
    <title>按管理员统计</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/statistics/index.js"></script>

</head>
<body>
<div id="index" v-cloak class="ms-index">
    <el-form :model="form"  :rules="rules" ref="searchForm"  label-width="100px" size="mini">
        <div class="ms-search" style="padding: 20px 10px 0 10px;">
            <el-row>
                <el-col :span="8">
                    <el-form-item  label="时间范围" prop="startDate">
                        <ms-date-picker v-model="form.startDate"
                                        :start-date.sync="form.startDate"
                                        :end-date.sync="form.endDate"
                                        :readonly="false"
                                        :disabled="false"
                                        :editable="true"
                                        :clearable="true"
                                        format="yyyy-MM-dd"
                                        value-format="yyyy-MM-dd"
                                        :style="{width:'100%'}"
                                        placeholder="请选择时间范围"
                                        start-placeholder="选择开始时间"
                                        end-placeholder="选择结束时间"
                                        type="daterange">
                        </ms-date-picker>
                    </el-form-item>
                </el-col>
                <el-col :span="16" style="text-align: right;paddingRight: 12px">
                    <el-button type="primary" icon="el-icon-search" size="mini" @click="getStatisticsData">查询</el-button>
                    <el-button @click="rest"  icon="el-icon-refresh" size="mini">重置</el-button>
                </el-col>
            </el-row>
        </div>
    </el-form>

    <el-main class="ms-container">
        <div class="index-data-body">
            <div class="index-layout">
                <el-row :gutter="24">
                    <el-col :span="12" >
                        <el-table ref="category" :indent="6"
                                  v-loading="categoryLoading"
                                  class="ms-table-pagination"
                                  border :data="contentCategoryIdOptions"
                                  height="calc(100vh - 120px)"
                                  :row-key="function(row){return row.id}"
                                  :select-on-indeterminate="true"
                                  tooltip-effect="dark"
                                  @select="rowSelect"
                                  @select-all="selectAll">
                            <template slot="empty">
                                暂无数据
                            </template>
                            <el-table-column type="selection"  width="40" reserve-selection="true" :selectable="selectable" class-name="isCheck">
                            </el-table-column>
                            <el-table-column label="栏目名称" align="left" prop="categoryTitle">
                            </el-table-column>
                        </el-table>
                    </el-col>
                    <el-col :span="12" >
                        <el-table
                                v-loading="managerLoading"
                                :data="configManagerIdsOptions"
                                class="ms-table-pagination"
                                height="calc(100vh - 120px)"
                                ref="manager"
                                @select="managerSelectionChange"
                                @select-all="managerAllSelectionChange"
                                @row-click="managerNameSelect"
                                border>
                            <el-table-column
                                    type="selection"

                                    width="40">
                            </el-table-column>
                            <el-table-column
                                    prop="managerName"
                                    label="管理员"
                                    sortable>
                            </el-table-column>
                        </el-table>
                    </el-col>
                </el-row>
            </div>
            <el-dialog
                    title="查询结果"
                    :visible.sync="dialogVisible"
                    fullscreen>
                <el-table v-loading="resultLoading" height="calc(100vh - 120px)" class="ms-table-pagination" border :data="dataList" tooltip-effect="dark" :key="random">
                    <template slot="empty">
                        {{emptyText}}
                    </template>
                    <template  v-for="(field,index) in tableField">
                        <el-table-column :width="field.width" :label="field.name" :prop="field.key">
                        </el-table-column>
                    </template>
                </el-table>
            </el-dialog>
        </div>
    </el-main>

</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data:{
            random: Math.random(),
            pin: false,
            //  栏目复选控制
            categoryOrigin: -1,
            //  管理员复选控制
            managerOrigin: -1,
            form: {
                startDate:'',
                endDate:'',
                //栏目标题
                categoryTitles:[],
                //管理员名称
                managerNames:[],
            },
            //栏目选项
            contentCategoryIdOptions:[],
            //管理员选项
            configManagerIdsOptions:[],
            rules:{
                // 时间范围
                startDate: [{"required":true,"message":"请选择时间范围", "trigger": "blur"}],
            },
            tableField: [],
            dataList: [],
            emptyText: '',
            categoryLoading: false,
            managerLoading: false,
            resultLoading: false,
            dialogVisible: false
        },
        methods:{
            selectable: function(row,index) {
                //列表可选
               if (row.categoryType == "1" ) {
                   return true;
               }  else {
                   return false;
               }

            },
            /*注意在获取初始数据时，所有节点（包括子节点）都增加一个isChecked 标志参数*/
            rowSelect: function(selection, row) {
                var that = this;
                let selected = selection.length && selection.indexOf(row) !== -1
                if (row.children) { //只对有子节点的行响应
                    if (selected) {   //由行数据中的元素isChecked判断当前是否被选中
                        row.check = true; //当前行isChecked标志元素切换为false
                        that.$refs.category.toggleRowSelection(row, true);
                        that.forList(row.children,true)
                    } else {
                        row.check = false; //当前行isChecked标志元素切换为false
                        that.$refs.category.toggleRowSelection(row, false);
                        that.forList(row.children,false)
                    }
                }
                //功能按钮选中
                row.check = !row.check;
            },
            //    遍历树形选择框的勾选和反选状态
            forList: function(list,flag) {
                var that = this
                list.forEach(function(item) {
                    if(item.children && item.children.length){
                        item.check = flag;
                        that.$refs.category.toggleRowSelection(item, flag);
                        that.forList(item.children,flag)
                    }else {
                        item.check = flag;
                        that.$refs.category.toggleRowSelection(item, flag);
                    }
                })
            },
            selectAll: function(selection) {
                var that = this;
                let dom = document.querySelector(".isCheck>div>label");
                if (!dom.className.includes("is-checked")) {
                    // 全选
                    that.forList(that.$refs.category.data,true)
                } else {
                    // 取消全选
                    that.forList(that.$refs.category.data,false)
                }
            },
            managerAllSelectionChange: function(selection) {
                var that = this
                that.form.managerNames = selection
            },
            //    点击名称复选
            managerNameSelect: function(row) {
                var that = this
                if(!that.form.managerNames.includes(row)) {
                    that.form.managerNames.push(row)
                    this.$refs.manager.toggleRowSelection(row, true);
                    that.pinSelect(that.form.managerNames,row)
                }
            },
            //  点击复选框
            managerSelectionChange: function(selection,row){
                var that = this
                that.pinSelect(selection,row)
            },

            //  shift多选功能
            pinSelect: function(selection,row) {
                const data = this.$refs.manager.tableData;  // 获取所以数据
                const origin = this.managerOrigin;  // 起点数 从-1开始
                const endIdx = row.index;  // 终点数
                this.form.managerNames = selection
                if (this.pin && selection.includes(data[origin])) {  // 判断按住
                    const sum = Math.abs(origin - endIdx) +  1;// 这里记录终点
                    const min = Math.min(origin, endIdx);// 这里记录起点
                    let  i =  0;
                    while (i <  sum) {
                        const  index =  min +  i;
                        if(!this.form.managerNames.includes(data[index])){
                            this.form.managerNames.push(data[index])
                        }
                        this.$refs.manager.toggleRowSelection(data[index], true);  // 通过ref打点调用toggleRowSelection方法，第二个必须为true
                        i++;
                    }
                } else {
                    this.managerOrigin =  row.index;  // 没按住记录起点
                }
            },

            //获取统计数据
            getStatisticsData:function (){
                var that = this;
                that.tableField = []
                that.form.categoryTitles = that.$refs.category.selection
                if(!that.form.categoryTitles.length) {
                    that.$notify({
                        title: '提示',
                        message: '栏目名称未勾选!',
                        type: 'warning'
                    })
                    return false
                }
                if(!that.form.managerNames.length) {
                    that.$notify({
                        title: '提示',
                        message: '管理员未勾选!',
                        type: 'warning'
                    })
                    return false
                }
                that.$refs.searchForm.validate(function(valid) {
                    if (valid) {
                        that.dialogVisible = true
                        that.resultLoading = true
                        that.dataList = [];
                        // 栏目名称集合
                        var tempCategorys = [];
                        that.form.managerNames.forEach(function(item){
                            // 组织表头数据
                            that.tableField.unshift({
                                key: item.managerName,
                                name: item.managerName,
                                width:100,
                            });
                        })
                        // 查询结果中栏目无数据，手动组织列表数据
                        that.form.categoryTitles.forEach(function(category) {
                            if(!tempCategorys.includes(category.categoryTitle)){
                                if(!category.children && typeof(category.children) == "undefined"){
                                    var temp = { category:category.categoryTitle, id: category.id };
                                    that.dataList.push(temp)
                                }
                            }
                        })
                        // 所有管理员的管理数为0
                        that.dataList.forEach(function(data){
                            that.form.managerNames.forEach(function(manager){
                                if(data[manager] == undefined){
                                    data[manager.managerName] = 0
                                }
                            })
                        })
                        ms.statistics(
                            [{"name": "按管理员统计", "params": {startDate:that.form.startDate[0],endDate:that.form.startDate[1]}}]
                        ).then(function (res) {
                            if(res.result && res.data['按管理员统计'] != null){
                                res.data['按管理员统计'].forEach(function(item){
                                    if(undefined == item || undefined ==item.managers){
                                        return;
                                    }
                                    // 组织表格数据
                                    var flag = false;
                                    that.dataList.forEach(function(_item){
                                        if(_item.id == item.id){
                                            flag = true;
                                            // 重复的数据添加字段保存数据
                                            _item[item.managers] = item.num;
                                        }
                                    })
                                });
                                that.random =  Math.random()
                                that.resultLoading = false
                            }else {
                                that.resultLoading = false
                            }
                        })
                        that.loading = false
                    }
                });

                that.tableField.unshift({ "key":"category",  "name":"栏目名称","width":80});
            },
            //获取栏目数据
            contentCategoryIdOptionsGet: function () {
                var that = this
                var data = []
                that.categoryLoading = true
                ms.http.get(ms.manager + "/cms/category/list.do",{pageSize:9999}).then(function (res) {
                    if (res.result) {
                        res.data.rows.forEach(function (item,index) {
                            item.index = index
                            if (item.categoryType == '2' || item.categoryType == '3') {
                                item.isDisabled = true;
                            }
                            data.push(item);
                        });
                        that.contentCategoryIdOptions = ms.util.treeData(data, 'id', 'categoryId', 'children');
                        that.categoryLoading = false
                    }
                });
            },
            //获取管理员数据
            configManagerIdsOptionsGet:function() {
                var that = this;
                that.managerLoading = true
                ms.http.get(ms.manager + "/basic/manager/list.do", {pageSize:999}).then(function (res) {
                    res.data.rows.forEach(function(item,index) {
                        item.index = index
                    });
                    that.configManagerIdsOptions = res.data.rows
                    that.managerLoading = false
                });
            },
            rest:function (){
                this.$refs.searchForm.resetFields();
                this.dataList = [];
                this.tableField = [];
                this.form.managerNames = []
                this.$refs.category.clearSelection()
                this.$refs.manager.clearSelection()
            },
        },
        created: function () {
            this.contentCategoryIdOptionsGet();
            this.configManagerIdsOptionsGet();
        },
        mounted: function () {
            var that = this
            window.addEventListener('keydown',function(code){
                if (code.keyCode == 16) {
                    that.pin = true
                }
            })
            window.addEventListener('keyup',function(code){
                if (code.keyCode == 16) {
                    that.pin = false
                }
            })
        },
    });
</script>
<style scoped>
    .index-data-body {
        display:flex;
        flex-direction: row;
        flex:1
    }

    .index-data-body .index-layout {
        position: relative;
        width: 100%;
    }

    .el-table--border th.gutter:last-of-type {
        border: 0 !important;
    }
</style>
