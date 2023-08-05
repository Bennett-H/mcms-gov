<!DOCTYPE html>
<html>
<head>
    <title>缓存管理</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="index" class="ms-index" v-cloak>

    <el-main class="ms-container"  v-loading="loading">
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info">
            缓存可以提高文章静态化效率,修改缓存类型需修改配置文件application.yml的spring.cache.type属性
        </el-alert>

        <el-row :gutter="10" justify="start" align="top">
            <el-col :span="12">
                <div class="panel">
                    <el-form :model="cache" label-width="210px" label-position="right" size="small">

                        <el-form-item label="缓存类型:" >
                            {{cache['缓存类型']}}
                        </el-form-item>

                        <el-form-item label="缓存路径:" >
                            {{cache['缓存路径']}}
                            <div class="ms-form-tip">
                                缓存文件存放的位置,具体在yml中配置，注：企业版本默认只支持ehcache,政务版本可以使用redis
                            </div>
                        </el-form-item>

                        <el-form-item label="已缓存文章总数/实际文章总数:">
                            {{cache['缓存总数']}} / {{cache['文章总数']}} 篇
                            <div class="ms-form-tip">
                                重启系统后或缓存总数为0需要重新缓存所有文章避免静态化出现异常<br/>
                                缓存总数一般会小于或等于文章总数,如果大于文章总数则说明缓存中存在脏数据,需要清除缓存并重新缓存
                            </div>
                        </el-form-item>



                        <el-form-item label="" style="height: 50px">
                            <@shiro.hasPermission name="co:cache:clear">
                                <el-button type="danger" icon="el-icon-delete" size="mini" @click="clear()" :loading='clearLoading'>清空缓存</el-button>
                            </@shiro.hasPermission>
                        </el-form-item>
                    </el-form>
                </div>
            </el-col>
            <el-col :span="12">
                <div class="panel">
                    <el-form ref="form" :model="form" :rules="rules" label-width="180px" label-position="right" size="small" @submit.native.prevent>
                        <el-form-item label="文章缓存核对" prop="id">
                            <el-input v-model="form.id"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入查询的文章编号">
                            </el-input>
                            <div class="ms-form-tip">
                                如果发现只有某一篇文章数据没有,可以尝试只缓存对应的文章数据
                            </div>
                        </el-form-item>
                        <el-form-item label="">
                            <el-button type="primary" icon="el-icon-search" size="mini" @click="getCacheById()" :loading='searchLoading'>查询</el-button>
                            <el-button type="primary" icon="el-icon-refresh" size="mini" @click="cacheContent()" :loading='cacheContentLoading'>缓存文章</el-button>
                        </el-form-item>
                        <el-form-item label="栏目ID" prop="categoryId">
                            <el-input v-model="form.categoryId"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="输入表示缓存指定栏目，默认缓存所有">
                            </el-input>
                            <div class="ms-form-tip">
                                输入栏目ID表示缓存此栏目下的文章,不输入则会缓存所有的文章
                            </div>
                        </el-form-item>
                        <el-form-item label="">
                            <@shiro.hasPermission name="co:cache:cacheAll">
                                <el-button type="primary" icon="el-icon-refresh" size="mini" @click="cacheCategory()" :loading='cacheAllLoading'>刷新缓存</el-button>
                            </@shiro.hasPermission>
                        </el-form-item>
                    </el-form>
                </div>
            </el-col>
        </el-row>
    </el-main>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data:{
            //  loading区域
            cacheAllLoading: false,
            cacheContentLoading: false,
            clearLoading: false,
            loading: false,
            searchLoading: false,

            dataList: [], //文件列表
            selectionList:[],//文件列表选中
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage:1, //初始页
            manager: ms.manager,
            loadState:false,
            emptyText:'',//提示文字
            cache:{},
            form: {
                id: '',
                categoryId: '',
            },
            rules: {
                // 文章编号
                id: [{
                    "required": true,
                    "message": "请选择文章编号"
                }],
            },
        },
        watch:{

        },
        methods:{

            //查询列表
            getInfo: function() {
                var that = this;
                that.loading = true
                ms.http.get(ms.manager + "/co/cache/info.do").then(function(res) {

                    that.loading = false

                    if (res.result && res.data) {
                        that.cache = res.data;
                    }
                }).catch(function(err) {
                    that.loading = false
                    that.$notify({
                        title: '失败',
                        message: err.msg,
                        type: 'error'
                    });
                });
            },

            //清空缓存
            clear: function(row){
                var that = this;
                that.$confirm("此操作将永久删除所有缓存, 是否继续", "提示", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: 'warning'
                }).then(function() {
                    that.clearLoading = true
                    ms.http.get(ms.manager+"/co/cache/clear.do").then(
                        function(res){
                            that.clearLoading = false
                            if (res.result) {
                                that.$notify({
                                    title: '成功',
                                    type: 'success',
                                    message:"删除成功"
                                });
                                //删除成功，刷新列表
                                that.getInfo();
                            }else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        });
                })
            },

            //缓存单个栏目或所有文章接口
            cacheCategory:function(id){
                var that = this
                that.cacheAllLoading = true
                ms.http.get(ms.manager + '/co/cache/cacheCategory.do',{
                    id: that.form.categoryId
                }).then(function(res) {

                    that.cacheAllLoading = false

                    if(res.result) {
                        that.$notify({
                            title: '成功',
                            message: id ? '当前栏目缓存完毕!' : '所有栏目已缓存完毕!',
                            type: 'success'
                        });
                        //缓存成功，刷新列表
                        that.getInfo()
                    } else {
                        that.$notify({
                            title: "错误",
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                }).catch(function(err) {
                    that.$notify({
                        title: "错误",
                        message: err.msg,
                        type: 'warning'
                    });
                    console.log(err)
                    that.cacheAllLoading = false

                })
            },

            //  查询搜索结果
            getCacheById: function() {
                var that = this
                that.$refs.form.validate(function(valid) {
                    if(valid) {
                        that.searchLoading = true
                        ms.http.get(ms.manager + '/co/cache/getCacheById.do', {
                            id: that.form.id
                        }).then(function(res) {
                            that.searchLoading = false
                            if(res.data && res.data.title) {
                                that.$notify({
                                    title: '成功',
                                    message: "文章<<" + res.data.title + ">>存在缓存中。",
                                    type: 'success'
                                });
                            }else {
                                that.$notify({
                                    title: '失败',
                                    message: "文章未被缓存!",
                                    type: 'warning'
                                });
                            }
                        }).catch(function(err) {
                            console.log(err)
                            that.searchLoading = false
                        })
                    }
                })
            },
            cacheContent: function() {
                var that = this
                that.$refs.form.validate(function(valid) {
                    if(valid) {
                        that.cacheContentLoading = true
                        ms.http.get(ms.manager + '/co/cache/cacheContent.do', {
                            id: that.form.id
                        }).then(function(res) {

                            that.cacheContentLoading = false
                            if(res.result) {
                                that.$notify({
                                    title: '成功',
                                    message: '当前文章缓存完毕!',
                                    type: 'success'
                                });
                                //缓存成功，刷新列表
                                that.getInfo()
                            }else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        }).catch(function(err) {
                            that.cacheContentLoading = false
                            that.$notify({
                                title: '失败',
                                message: err.msg,
                                type: 'error'
                            });
                        })
                    }
                })
            }


        },
        created:function(){
            var that = this;
            that.getInfo();
        },
    })
</script>

<style>
    #index .ms-container {
        height: calc(100vh - 78px);
    }
    #index {
        background-color: white;
    }

    #index .panel {
        background-color: white;padding: 20px 40px 20px 40px; height: 400px;border: 1px solid #EBEEF5;
    }

    .el-divider__text {
        background-color: unset;
    }
</style>
