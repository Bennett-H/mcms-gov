<#--在ms-mcms版本上进行了优化,不能直接覆盖开源版本-->
<#--增加多模版生成-->
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>静态化</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/plugins/moment/2.24.0/moment.min.js"></script>
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body class="custom-body">
<div id="app" v-cloak class="ms-index">
    <el-header class="ms-header" height="50px">

        <el-col >
            <el-tooltip class="item" effect="dark" content="释放静态化操作正在占用的系统资源，请等待成功提示信息返回后再进行静态化操作" placement="bottom">
                <el-button type="success" plain style="float: right;"
                           icon="el-icon-refresh-left" size="mini" @click="resetGenerate">释放静态化资源</el-button>
            </el-tooltip>
        </el-col>

    </el-header>
    <el-main class="ms-container">
        <el-alert
                class="ms-alert-tip"
                title="功能介绍"
                type="info"
                description="此功能用于网页静态化,模板修改后必须要重新静态化才能生效.如果发现生成的文章内容为空,请立即检查 系统设置-缓存管理,并重新缓存文章,静态时使用了线程池操作，提示成功后台实际还在执行，不要频繁点击生成，多人同时操作后台静态化或者生产环境推荐使用定时调度或者启用自动静态化。
                             重置静态化功能会释放静态化操作正在占用的系统资源，重置成功后，请在提示信息出现后等待片刻再使用静态化功能">
        </el-alert>
        <el-form ref="form" label-width="100px" size="mini">
            <el-alert
                    v-if="form.length==0"
                    title="未绑定模版"
                    type="warning"
                    description="请先在应用设置模版"
                    :closable="false"
                    show-icon>
            </el-alert>
            <template v-for='(item,i) in form' :key="i" >
                <el-divider content-position="left">{{item.name}}</el-divider>

                <el-row :gutter="10" style="margin-bottom: 10px" v-if="item.style!=''">
                    <el-col :span="8">
                        <div class="panel">
                            <el-row>
                                <el-col :span="24">
                                    <el-form-item label="主页模板">
                                        <el-select v-model="form[i].index.file"
                                                   @visible-change="queryTemplateFileForColumn(item.template)"
                                                   :filterable="true"
                                                   :clearable="false"
                                                   :loading="loading"
                                                   placeholder="请选择主页模板">
                                            <el-option v-for='item in templateOptions' :key="item" :value="item"
                                                       :label="item"></el-option>
                                        </el-select>
                                        <div class="ms-form-tip">
                                            更新主页，如果系统存在引导页面可以手动修改主页位置文件名,default.html引导页面index.html主页。
                                        </div>
                                    </el-form-item>

                                    <el-form-item label="主页位置">
                                        <el-input v-model="form[i].index.path"
                                                  :disabled="false"
                                                  :style="{width:  '100%'}"
                                                  :clearable="true"
                                                  placeholder="请输入主页位置">
                                        </el-input>
                                        <div class="ms-form-tip">
                                            主页位置htm文件名一般为index.html或default.html
                                        </div>
                                    </el-form-item>

                                    <el-form-item>
                                        <el-row>
                                            <el-col :span="24">
                                                <@shiro.hasPermission name="cms:generate:index">
                                                    <el-button type="primary" @click="updataIndex(form[i])" :disabled="hasGenernate"
                                                               :loading="form[i].index.loading">
                                                        {{form[i].index.loading?'更新中':'生成主页'}}
                                                    </el-button>
                                                </@shiro.hasPermission>
                                                <el-button plain @click="viewIndex(form[i])" style="margin-left:10px">预览主页</el-button>
                                            </el-col>

                                        </el-row>
                                    </el-form-item>


                                </el-col>
                                <#--                            <el-col :span="10" style="display: flex;justify-content: center;">-->
                                <#--                                <el-progress type="circle" :percentage="0"></el-progress>-->

                                <#--                            </el-col>-->
                            </el-row>

                        </div>


                    </el-col>
                    <el-col :span="8">
                        <div  class="panel">
                            <el-row>
                                <el-col :span="24">
                                    <el-form-item label="文章栏目">
                                        <ms-tree-select v-model="form[i].content.categoryId"
                                                     :props="{value: 'id',label: 'categoryTitle',children: 'children'}"
                                                     :options="categoryList" :style="{width:'100%'}"
                                                     placeholder="请选择文章栏目">
                                        </ms-tree-select>
                                        <div class="ms-form-tip">
                                            生成对应栏目属性为列表的内容数据，例如：新闻详情、产品详情
                                        </div>
                                    </el-form-item>

                                    <el-form-item label="指定时间">
                                        <el-date-picker
                                                v-model="form[i].content.date"
                                                placeholder="请选择指定时间"
                                                start-placeholder=""
                                                end-placeholder=""
                                                :readonly="false"
                                                :disabled="false"
                                                :editable="false"
                                                :clearable="false"
                                                format="yyyy-MM-dd"
                                                value-format="yyyy-MM-dd"
                                                :style="{width:'100%'}"
                                                type="date">
                                        </el-date-picker>
                                        <div class="ms-form-tip">
                                            根据内容的更新时间来生成，例如：2020-10-10，则生成10月10号以后更新过的文章,如果遇到内容没有生成或者内容样式没有更新可以调整时间
                                        </div>
                                    </el-form-item>

                                    <@shiro.hasPermission name="cms:generate:article">
                                        <el-form-item>
                                            <el-button type="primary" @click="generateContent(form[i])" :disabled="hasGenernate"
                                                       :loading="form[i].content.loading">
                                                {{form[i].content.loading?'更新中':'生成文章'}}
                                            </el-button>
                                        </el-form-item>
                                    </@shiro.hasPermission>

                                </el-col>
                                <#--                            <el-col :span="10" style="display: flex;justify-content: center;">-->
                                <#--                                <el-progress type="circle" :percentage="0"></el-progress>-->

                                <#--                            </el-col>-->
                            </el-row>
                            <el-row v-show="form[i].content.progress.isShow">
                                <el-col :span="24">
                                    <el-form-item>
                                        <el-progress :percentage="form[i].content.progress.data"
                                                     :status="form[i].content.progress.status"></el-progress>
                                        <div class="ms-form-tip-err"
                                             v-if="form[i].content.progress.status=='exception'">
                                            生成失败，具体查看系统后台错误日志，通常是模版不存在、模版标签错误
                                        </div>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                        </div>

                    </el-col>
                    <el-col :span="8">
                        <div  class="panel">
                            <el-row>
                                <el-col :span="24">
                                    <el-form-item label="生成栏目">
                                        <ms-tree-select v-model="form[i].category.categoryId"
                                                     :props="{value: 'id',label: 'categoryTitle',children: 'children'}"
                                                     :options="categoryList" :style="{width:'100%'}"
                                                     placeholder="请选择文章栏目">
                                        </ms-tree-select>
                                        <div class="ms-form-tip">
                                            生成栏目属性为列表、封面的内容数据，例如：关于我们、公司介绍、新闻列表、产品列表
                                        </div>
                                    </el-form-item>
                                    <@shiro.hasPermission name="cms:generate:column">
                                        <el-form-item>
                                            <el-button type="primary" @click="generateColumn(form[i])" :disabled="hasGenernate"
                                                       :loading="form[i].category.loading">
                                                {{form[i].category.loading?'更新中':'生成栏目'}}
                                            </el-button>

                                        </el-form-item>
                                    </@shiro.hasPermission>
                                </el-col>
                                <#--                            <el-col :span="10" style="display: flex;justify-content: center;">-->
                                <#--                                <el-progress type="circle" :percentage="0"></el-progress>-->

                                <#--                            </el-col>-->
                            </el-row>
                            <el-row v-show="form[i].category.progress.isShow">
                                <el-col :span="24">
                                    <el-form-item>
                                        <el-progress :percentage="form[i].category.progress.data"
                                                     :status="form[i].category.progress.status"></el-progress>
                                        <div class="ms-form-tip-err"
                                             v-if="form[i].category.progress.status=='exception'">
                                            生成失败，具体查看系统后台错误日志，通常是模版不存在、模版标签错误
                                        </div>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                        </div>
                    </el-col>
                </el-row>
                <el-row v-else>
                    <el-col :span="24">
                        <el-alert
                                title="未绑定模版"
                                type="warning"
                                :description="item.name+'没有对应模版，请先在应用设置选择模版'"
                                closable="false"
                                show-icon>
                        </el-alert>
                    </el-col>
                </el-row>
            </template>

        </el-form>
    </el-main>
</div>
</body>
</html>
<script>
    "use strict";

    var app = new Vue({
        el: '#app',
        watch: {},
        data: {
            loading: false,
            hasGenernate:false,
            dictList: [],//模板类型字典数据
            templateOptions: [],//下拉选择模版
            categoryList: [{
                id: '0',
                categoryTitle: '顶级栏目',
                children: []
            }],
            form: [],//组织循环铸铁

        },
        methods: {
            //更新主页
            updataIndex: function (form) {
                var that = this;
                if (!form.index.path || form.index.path == '') {
                    this.$notify({
                        title: '失败',
                        message: '请输入主页位置！',
                        type: 'warning'
                    });
                    return;
                }
                if (!form.index.file || form.index.file == '') {
                    this.$notify({
                        title: '失败',
                        message: '请选择主页模版！',
                        type: 'warning'
                    });
                    return;
                }
                form.index.loading = true;
                ms.http.post(ms.manager + '/cms/generate/generateIndex.do', {
                    file: form.index.file,
                    path: form.index.path,
                    style: form.style
                }).then(function (data) {
                    if (data.result) {
                        that.$notify({
                            title: '成功',
                            message: '更新成功！',
                            type: 'success'
                        });
                    } else {
                        that.$notify({
                            title: '更新失败！',
                            message: data.msg,
                            type: 'warning'
                        });
                    }
                }).finally(function () {
                    form.index.loading = false;
                });
            },
            //预览主页
            viewIndex: function (form) {
                if (!form.index.path || form.index.path == '') {
                    this.$notify({
                        message: '请输入主页位置！',
                        title: '警告',
                        type: 'warning'
                    });
                    return;
                }
                window.open(ms.manager + "/cms/generate/viewIndex.do?style=" + form.template + "&path=" + form.index.path);
            },

            //生成文章栏目
            generateContent: function (form) {
                var that = this;
                form.content.loading = true;
                that.hasGenernate=true;
                form.content.progress = {
                    data: 0,
                    status: '',
                    isShow: true
                };
                ms.http.post(ms.manager + '/cms/generate/generateContent.do', {
                    dateTime: form.content.date,
                    categoryId: form.content.categoryId,
                    style: form.style,
                }).then(function (data) {
                    if (data.result) {
                        var timingTask = setInterval(function () {
                            ms.http.get(ms.manager + '/cms/generate/progress.do', {
                                key: "content" + form.style,
                            }).then(function (res) {
                                if (res.result) {
                                    if (res.data == 0) {
                                        clearInterval(timingTask);
                                        form.content.progress.status = "success";
                                        form.content.progress.data = 100;
                                        that.hasGenernate=false;
                                        setTimeout(function () {
                                            form.content.progress = {
                                                data: 0,
                                                status: '',
                                                isShow: false
                                            }
                                            form.content.loading = false;
                                        }, 100);
                                        that.$notify({
                                            title: '成功',
                                            message: '更新成功！',
                                            type: 'success'
                                        });
                                    } else if(res.data==-1) {
                                        clearInterval(timingTask);
                                        setTimeout(form.content.progress.status = "exception", 3000);
                                    }
                                    form.content.progress.data = 100 - parseInt((res.data/100));
                                }
                            });
                        }, 1000);

                    }else {
                        form.content.progress = {
                            data: 0,
                            status: '',
                            isShow: false
                        };
                        that.hasGenernate=false;
                        that.$notify({
                            title: '生成失败',
                            message: data.msg,
                            type: 'warning'
                        });
                        form.content.loading = false;
                    }
                }).finally(function () {
                    form.content.loading = false;
                });
            },

            //更新栏目
            generateColumn: function (form) {

                var that = this;
                form.category.progress = {
                    data: 0,
                    status: '',
                    isShow: true
                };
                that.hasGenernate=true;
                form.category.loading = true;
                ms.http.get(ms.manager + '/cms/generate/generateColumn.do', {
                    categoryId: form.category.categoryId,
                    style: form.style
                }).then(function (data) {
                    if (data.result) {

                        var timingTask = setInterval(function () {
                            ms.http.get(ms.manager + '/cms/generate/progress.do', {
                                key: "category" + form.style,
                            }).then(function (res) {
                                if (res.result) {
                                    if (res.data == 0) {
                                        clearInterval(timingTask);
                                        form.category.progress.status = "success";
                                        form.category.progress.data = 100;
                                        that.hasGenernate=false;

                                        setTimeout(function () {
                                            form.category.progress = {
                                                data: 0,
                                                status: '',
                                                isShow: false
                                            }
                                            form.category.loading = false;
                                        }, 100);
                                        that.$notify({
                                            title: '成功',
                                            message: '更新成功！',
                                            type: 'success'
                                        });

                                    } else if(res.data==-1) {
                                        clearInterval(timingTask);
                                        setTimeout(form.category.progress.status = "exception", 3000);
                                    }
                                    form.category.progress.data = 100 - parseInt((res.data/100));
                                }
                            }).finally(function () {
                                form.category.loading = false;

                            });
                        }, 1000);

                    } else {
                        form.category.progress = {
                            data: 0,
                            status: '',
                            isShow: false
                        };
                        that.hasGenernate=false;
                        that.$notify({
                            title: '生成失败',
                            message: data.msg,
                            type: 'warning'
                        });
                        form.category.loading = false;
                    }
                })
            },

            //获取主题模板数据源
            queryTemplateFileForColumn: function (appStyle) {
                this.loading = true;
                if (appStyle == "") {
                    this.templateOptions = [];
                    this.loading = false;
                    return;
                }

                var that = this;
                ms.http.get(ms.manager + '/basic/template/queryTemplateFileForColumn.do', {"appStyle": appStyle}).then(function (data) {
                    that.templateOptions = data.data; //寻找主页

                    // var template = that.templateOptions.find(function (x) {
                    //     return x.indexOf("index") != -1 || x.indexOf("default") != -1;
                    // }); //没有就找其他的

                    // that.from[0].index.file = template || (that.templateOptions.length > 0 ? that.templateOptions[0] : "");
                }).then(function () {
                    that.loading = false;
                })
            },
            queryCategoryList: function () {
                var that = this;
                ms.http.get(ms.manager + "/cms/category/list.do", {
                    pageSize: 9999
                }).then(function (res) {
                    if (res.result) {
                        //res.data.rows.push({id:0,columnId: null,categoryTitle:'顶级栏目管理'});
                        that.categoryList[0].children = ms.util.treeData(res.data.rows, 'id', 'categoryId', 'children');
                    }
                });
            },
            //获取当前应用表
            getApp: function () {
                var that = this;
                ms.http.get(ms.manager + "/basic/app/get.do", {}).then(function (res) {
                    if (res.result && res.data) {
                        that.styles = JSON.parse(res.data.appStyles);
                        if (!that.styles || that.styles.length == 0) {
                            that.$notify({
                                title: '提示',
                                message: "当前站点没有绑定模版",
                                type: 'warning'
                            });

                        }
                        // that.form.push({
                        //     name: '默认皮肤',
                        //     style: res.data.appStyle,
                        //     index: {
                        //         file: "",
                        //         path: "index.html",
                        //         loading: false,
                        //         progress:{
                        //             data:0,
                        //             status:'',
                        //             isShow:false
                        //         }
                        //     },
                        //     content: {
                        //         columnId: 0,
                        //         date: ms.util.date.fmt(moment().add(-7, 'days'), "yyyy-MM-dd"),
                        //         loading: false,
                        //         progress:{
                        //             data:0,
                        //             status:'',
                        //             isShow:false
                        //         }
                        //     },
                        //     category: {
                        //         columnId: 0,
                        //         loading: false,
                        //         progress:{
                        //             data:0,
                        //             status:'',
                        //             isShow:false
                        //         }
                        //     }
                        // });
                        if (that.styles){
                            that.styles.forEach(function (item) {
                                that.form.push({
                                    name: item.name,
                                    template: item.template,
                                    index: {
                                        file: "",
                                        path: "index.html",
                                        loading: false,
                                        progress: {
                                            data: 0,
                                            status: '',
                                            isShow: false
                                        }
                                    },
                                    content: {
                                        columnId: 0,
                                        date: ms.util.date.fmt(moment().add(-2, 'days'), "yyyy-MM-dd"),
                                        loading: false,
                                        progress: {
                                            data: 0,
                                            status: '',
                                            isShow: false
                                        }
                                    },
                                    category: {
                                        columnId: 0,
                                        loading: false,
                                        progress: {
                                            data: 0,
                                            status: '',
                                            isShow: false
                                        }
                                    }
                                });
                            })
                        }
                        that.dictListGet();
                    }
                })
            },
            // 重置线程，用于频繁静态化操作导致线程卡死之后重置
            resetGenerate: function (){
                var that = this;
                that.$confirm("此操作会消耗一定性能,且需等待片刻, 是否继续", "提示", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: 'warning'
                }).then(function () {
                    ms.http.post(ms.manager + "/cms/generate/resetGenerate.do").then(
                        function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: '成功',
                                    type: 'success',
                                    message: res.msg,
                                });
                            } else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }
                        });
                })
            },

            //获取模板类型 字典数据
            dictListGet: function () {
                var that = this;
                //获取  模板类型 字典数据
                ms.mdiy.dict.list('模板类型').then(function (res) {
                    if (res.result) {
                        that.dictList = res.data.rows;
                        that.loading = false;
                    }
                    that.form.forEach(function (item){
                        that.dictList.forEach(function (dict){
                            if (item.name == dict.dictLabel) {
                                item.style = dict.dictValue;
                            }
                        })
                    })

                })

            },
        },
        created: function () {
            this.queryCategoryList();
            this.queryTemplateFileForColumn();
            // 必须要在最后一位,不然会有异步问题
            this.getApp();
        }
    });
</script>
<style>
    #app {
        background-color: white;
    }

    #app .panel {
        background-color: white;padding: 20px 40px 20px 40px; height: 270px;border: 1px solid #EBEEF5;
    }

    .el-divider__text {
        background-color: unset;
    }
</style>
