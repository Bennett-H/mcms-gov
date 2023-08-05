<#--在ms-mcms版本上进行了优化,不能直接覆盖开源版本-->
<#--增加多模版选择，废弃默认皮肤 -->
<!DOCTYPE html>
<html>
<head>
    <title>应用设置</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="form" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" plain style="float: left; margin-right: 8px"
                   icon="el-icon-refresh-left" size="mini"
                   @click="refreshCache()">刷新缓存
        </el-button>
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存</el-button>
    </el-header>
    <el-main class="ms-container">
        <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-form ref="form" :model="form" :rules="rules" label-width="140px" size="mini">
                <el-row>
                    <el-col span="12">
                        <el-form-item label="网站标题" prop="appName">
                            <el-input v-model="form.appName"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入网站标题">
                            </el-input>
                            <div class="ms-form-tip">
                             标签<a href="http://doc.mingsoft.net/mcms/biao-qian/tong-yongquan-ju-ms-global.html" target="_blank">{ms:global.name/}</a>
                            </div>
                        </el-form-item>
                    </el-col>
                    <!--el-col span="12">
                        <el-form-item  label="站点风格" prop="appStyle">
                            <el-select v-model="form.appStyle"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="false"
                                       :multiple="false" :clearable="true"
                                       placeholder="请选择站点风格">
                                <el-option v-for='item in appTemplates' :key="item" :value="item"
                                           :label="item"></el-option>
                            </el-select>
                        </el-form-item>

                    </el-col-->
                    <el-col :span="12">

                        <el-form-item  label="网站生成目录" prop="appDir">
                            <el-input
                                    v-model="form.appDir"
                                    :disabled="false"
                                    :readonly="false"
                                    :style="{width:  '100%'}"
                                    :clearable="false"
                                    placeholder="请输入网站生成目录">
                            </el-input>
                            <div class="ms-form-tip">
                             站点静态化之后静态文件保存目录，开启短链且没有站群的情况下，不会拼接这层目录
                            </div>
                        </el-form-item>
                    </el-col>

                </el-row>
                <el-row
                        :gutter="0"
                        justify="start" align="top">
                    <template v-for='(item,index) in styles' :key="index" >
                        <el-col span="12">
                            <el-form-item  :label="item.name+'站点风格'" >
                                <el-select v-model="styles[index].template"
                                           :value="item.template"
                                           :style="{width: '100%'}"
                                           :filterable="false"
                                           :disabled="false"
                                           :multiple="false" :clearable="true"
                                           placeholder="请选择站点风格">
                                    <el-option v-for='(template,i) in appTemplates' :key="i" :value="template"
                                               :label="template"></el-option>
                                </el-select>
                                <div class="ms-form-tip">
                                 标签<a href="http://doc.mingsoft.net/mcms/biao-qian/tong-yongquan-ju-ms-global.html" target="_blank">{ms:global.style/}</a>,一个站点下可以有多套前端皮肤，需要切换皮肤时，需要切换模板风格，保存后再次生成新模板的首页、栏目及文章，预览不同模板风格的页面效果;
                                    注意，多个风格必须保证绑定的模板名称不同
                                </div>
                            </el-form-item>

                        </el-col>
                    </template>
                </el-row>

                <el-form-item  label="网站Logo" prop="appLogo">
                    <el-upload
                            :file-list="form.appLogo"
                            :action="ms.manager+'/file/upload.do'"
                            :on-remove="appLogohandleRemove"
                            :style="{width:''}"
                            :limit="1"
                            :on-exceed="appLogohandleExceed"
                            :disabled="false"
                            :data="{uploadPath:'/appLogo','isRename':true,'appId':true}"
                            :on-success="appLogoSuccess"
                            accept="image/*"
                            list-type="picture-card">
                        <i class="el-icon-plus"></i>
                        <div slot="tip" class="el-upload__tip">支持jpg,png格式，最多上传1张图片</div>
                    </el-upload>
                    <div class="ms-form-tip">
                     标签<a href="http://doc.mingsoft.net/mcms/biao-qian/tong-yongquan-ju-ms-global.html" target="_blank">{ms:global.logo/}</a>
                    </div>
                </el-form-item>
                <el-form-item label="关键字" prop="appKeyword">
                    <#--  <template slot='label'>关键字
                        <el-popover placement="top-start" title="提示" trigger="hover" >
                            <a href="http://doc.ms.mingsoft.net/plugs-cms/biao-qian/tong-yongquan-ju-ms-global.html" target="_blank">{ms:global.keyword/}</a>
                            <i class="el-icon-question" slot="reference"></i>
                        </el-popover>
                    </template>  -->
                    <el-input
                            type="textarea" :rows="5"
                            :disabled="false"
                            v-model="form.appKeyword"
                            :style="{width: '100%'}"
                            placeholder="请输入关键字">
                    </el-input>
                    <div class="ms-form-tip">
                        标签<a href="http://doc.mingsoft.net/mcms/biao-qian/tong-yongquan-ju-ms-global.html" target="_blank">{ms:global.keyword/}</a>,设置百度搜索的关键字，网站上线后将进行收录，多个关键字用“,”隔开，关键字设置的越精确越多，可以提高搜索准确率
                    </div>
                </el-form-item>
                <el-form-item label="描述" prop="appDescription">
                    <#--  <template slot='label'>描述
                        <el-popover placement="top-start" title="提示" trigger="hover" >
                            <a href="http://doc.ms.mingsoft.net/plugs-cms/biao-qian/tong-yongquan-ju-ms-global.html" target="_blank">{ms:global.descrip/}</a>
                            <i class="el-icon-question" slot="reference"></i>
                        </el-popover>
                    </template>  -->
                    <el-input
                            type="textarea" :rows="5"
                            :disabled="false"
                            v-model="form.appDescription"
                            :style="{width: '100%'}"
                            placeholder="请输入描述">
                    </el-input>
                    <div class="ms-form-tip">
                     标签<a href="http://doc.mingsoft.net/mcms/biao-qian/tong-yongquan-ju-ms-global.html" target="_blank">{ms:global.descrip/}</a>,百度搜索到当前网站时，会显示当前网站的标题和描述，描述内容即在此进行设置
                    </div>
                </el-form-item>
                <el-form-item label="版权信息" prop="appCopyright">
                    <#--  <template slot='label'>版权信息
                        <el-popover placement="top-start" title="提示" trigger="hover" >
                            <a href="http://doc.ms.mingsoft.net/plugs-cms/biao-qian/tong-yongquan-ju-ms-global.html" target="_blank">{ms:global.copyright/}</a>
                            <i class="el-icon-question" slot="reference"></i>
                        </el-popover>
                    </template>  -->
                    <el-input
                            type="textarea" :rows="5"
                            :disabled="false"
                            v-model="form.appCopyright"
                            :style="{width: '100%'}"
                            placeholder="请输入版权信息">
                    </el-input>
                    <div class="ms-form-tip">
                     标签<a href="http://doc.mingsoft.net/mcms/biao-qian/tong-yongquan-ju-ms-global.html" target="_blank">{ms:global.copyright/}</a>
                    </div>
                </el-form-item>
            </el-form>
        </el-scrollbar>
    </el-main>
</div>
</body>
</html>
<script>
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                saveDisabled: false,
                //表单数据
                form: {
                    // 站点名称
                    appName: '',
                    // 站点风格
                    appStyle: [],
                    // 站点多风格
                    appStyles: '',
                    // 网站Logo
                    appLogo: [],
                    // 关键字
                    appKeyword: '',
                    // 描述
                    appDescription: '',
                    // 版权信息
                    appCopyright: '',
                    // 网站生成目录
                    appDir:'',
                },

                rules: {
                    // 网站标题
                    appName: [{
                        "required": true,
                        "message": "网站标题必须填写"
                    }, {
                        "min": 1,
                        "max": 50,
                        "message": "站点名称长度必须为10-150"
                    }],
                    appDescription: [{
                        "min": 0,
                        "max": 1000,
                        "message": "描述长度必须小于1000"
                    }],
                    appKeyword: [{
                        "min": 0,
                        "max": 1000,
                        "message": "关键字长度必须小于1000"
                    }],
                    appCopyright: [{
                        "min": 0,
                        "max": 1000,
                        "message": "版权信息长度必须小于1000"
                    }],
                    // 网站生成目录
                    appDir: [
                        {"required":true,"message":"网站生成目录不能为空"},
                        {"min":0,"max":10,"message":"网站生成目录长度必须为0-10"},
                        {
                            "pattern": /^[^[!@#$"'%^&*()_+-/~?！@#￥%…&*（）——+—？》《：“‘’]+$/,
                            "message": "网站生成目录格式不匹配"
                        }
                    ],
                },
                styles:[], //多模版绑定json结构
                dictTemplates:[],  //风格定义列表，数据来自字典
                appTemplates: [],//当前站点模版文件夹下所有的模版
            };
        },
        watch: {},
        computed: {},
        methods: {
            save: function () {
                var that = this;
                url = ms.manager + "/basic/app/update.do";
                this.$refs.form.validate(function(valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        that.form.appStyles = JSON.stringify(that.styles);
                        var data = JSON.parse(JSON.stringify(that.form));
                        if(data.appLogo){
                            data.appLogo = JSON.stringify(data.appLogo);
                        }
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success'
                                });
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: data.msg,
                                    type: 'warning'
                                });
                            }
                            that.saveDisabled = false;
                        });
                    } else {
                        return false;
                    }
                });
            },
            //获取当前应用表
            get: function (appId) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/basic/app/get.do", {"appId":appId}).then(function (res) {
                    that.loading = false
                    if(res.result && res.data){
                        if(res.data.appLogo){
                            res.data.appLogo = JSON.parse(res.data.appLogo);
                            res.data.appLogo.forEach(function(value){
                                value.url= ms.base + value.path
                            })
                        }else{
                            res.data.appLogo=[]
                        }
                        that.form = res.data;
                        that.styles = that.buildStyles(that.dictTemplates,that.form.appStyles);
                    }
                })
            },
            //上传超过限制
            appLogohandleExceed:function(files, fileList) {
                this.$notify({ title: '当前最多上传1张图片', type: 'warning' });},
            appLogohandleRemove:function(file, files) {
                var index = -1;
                index = this.form.appLogo.findIndex(function(e){return e == file} );
                if (index != -1) {
                    this.form.appLogo.splice(index, 1);
                }
            },

            //appLogo文件上传完成回调
            appLogoSuccess: function (response, file, fileList) {
                if(response.result){
                    this.form.appLogo.push({url:file.url,name:file.name,path:response.data,uid:file.uid});
                }else {
                    this.$notify({
                        title: '失败',
                        message: response.msg,
                        type: 'warning'
                    });
                }

            },

            /**
             * 获取当前站点下所有的模版文件夹
             * @returns {[]}
             */
            queryAppTemplateSkin: function () {
                var that = this;
                ms.http.get(ms.manager + '/basic/template/queryAppTemplateSkin.do').then(function (data) {
                    that.appTemplates = data.data.appTemplates;
                    that.get(-1);
                })
            },

            /**
             * 获取字典对应的 '模板类型' 数据
             * @returns {[]}
             */
            queryDictForTemplate: function () {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {
                    dictType: '模板类型'
                }).then(function (res) {
                    if(res.result){
                        that.queryAppTemplateSkin();
                        res = res.data;
                        that.dictTemplates = res.rows;
                    }
                })
            },
            /**
             * 组装多站点风格json,对应app实体的appStyles
             * @param dictTemplates 字典风格列表
             * @param appStyles 当前站点应用风格
             * @returns [{name:'字典风格',template:'站点模版文件夹名称'}]
             */
            buildStyles:function (dictTemplates,appStyles) {
                var resultList = [];
                var list = [];
                if(appStyles!="" && appStyles!=undefined){
                    list =  JSON.parse(appStyles)
                    dictTemplates.forEach(function(item,x){
                        // 过滤出相符合的数据
                        var filtList  = list.filter(function(temp) {
                            return  item.dictLabel == temp.name;
                        })
                        if(filtList !=""){
                            resultList.push(filtList[0])
                        }else {
                            var obj = {};
                            obj.name = item.dictLabel;
                            obj.template = "";
                            resultList.push(obj)
                        }
                    })
                }else {
                    //初始化模板选择数据
                    dictTemplates.forEach(function(item) {
                        var obj = {};
                        obj.name = item.dictLabel;
                        obj.template = "";
                        resultList.push(obj)
                    })
                }
                return resultList;
            },
            //刷新缓存
            refreshCache: function() {
                var that = this;
                ms.http.post(ms.manager + "/basic/app/refreshCache.do").then(function (data) {
                    if (data.result) {
                        that.$notify({
                            title: '成功',
                            type: 'success',
                            message: '刷新成功!'
                        });
                        that.get(-1);
                    } else {
                        that.$notify({
                            title: '失败',
                            message: data.msg,
                            type: 'warning'
                        });
                    }
                });

            },
        },
        created: function () {
            this.queryDictForTemplate();

        }
    });
</script>
