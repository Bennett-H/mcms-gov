<!DOCTYPE html>
<html>
<head>
    <title>自定义配置</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/mdiy/index.js"></script>
</head>
<body>
<div id="form" v-loading="loading" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
<#--        <@shiro.hasPermission name="mdiy:configData:update">-->
            <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
            </el-button>
<#--        </@shiro.hasPermission>-->
        <el-button size="mini" v-if="!isEditor"   plain onclick="javascript:history.go(-1)"><i class="iconfont icon-fanhui"></i>返回</el-button>
        </el-header>
    <el-main class="ms-container">
        <div id="configModel"></div>
    </el-main>
</div>
</body>
</html>
<script>
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                loading: false,
                saveDisabled: false,
                //自定义模型实例
                configModel: undefined,
                //表单数据
                form: {
                    id:''
                },
                isEditor:false,//是否是编辑状态，如果是就不显示返回按钮
            }
        },
        methods: {
            save: function () {
                var that = this;
                that.saveDisabled = true;
                that.configModel.save(function (res) {
                    if(res.result){
                        that.$notify({
                            title: '成功',
                            type: 'success',
                            message: '保存成功!'
                        });
                    }else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                    that.saveDisabled = false;
                });
            },
        },
        created: function () {
            this.form.id = ms.util.getParameter("id");
            //点击菜单为配置编辑模式，不显示返回按钮
            this.isEditor = ms.util.getParameter("isEditor")==null?false:JSON.parse(ms.util.getParameter("isEditor"));
            this.loading = true;
            var that = this;
            this.$nextTick(function () {
                ms.mdiy.model.config("configModel", {"modelName": ms.util.getParameter("modelName")}, '', true).then(function (obj) {
                    that.configModel = obj;
                });
            });
            this.loading = false;
        }
    });
</script>
