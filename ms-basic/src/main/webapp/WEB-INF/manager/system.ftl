<!DOCTYPE html>
<html>

<head>
    <title>系统信息</title>
    <#include "../../include/head-file.ftl">
</head>

<body>
<div id="form" v-loading="loading" v-cloak>
    <el-main class="ms-container">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">
            <el-row :gutter="0" justify="start" align="top">
                <el-row :gutter="0" justify="start" align="top">
                    <el-col :span="12">
                        <el-form-item label="系统版本" >
                            {{form['系统名称']}} {{form['系统版本']}}
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="系统构架">
                            {{form['系统构架']}}
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-col :span="12">
                    <el-form-item label="web容器">
                        {{form['web容器']}}
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="发布路径">
                        {{form['发布路径']}}
                    </el-form-item>
                </el-col>
                <el-row :gutter="0" justify="start" align="top">
                    <el-col :span="12">
                        <el-form-item label="Java版本" >
                            {{form['Java版本']}}
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="Java安装路径">
                            {{form['Java安装路径']}}
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="0" justify="start" align="top">
                    <el-col :span="12">
                        <el-form-item label="数据库信息" >
                            {{form['数据库']}} {{form['数据库版本']}}
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="物理内存">
                            总量{{form['内存总量']}}MB/可用{{form['内存可用']}}MB
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-col :span="12">
                    <el-form-item label="cpu信息">
                        {{form['cpu信息']}}
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="数据库链接" >
                        {{form['数据库链接']}}
                    </el-form-item>
                </el-col>
            </el-row>


        </el-form>
    </el-main>
</div>
</body>

</html>
<script>
    var form = new Vue({
        el: '#form',
        data: function() {
            return {
                loading: false,
                saveDisabled: false,
                //表单数据
                form: {},
                rules: {},

            }
        },
        watch: {},
        computed: {},
        methods: {
            //获取当前获取系统配置
            get: function() {
                var that = this;
                this.loading = true
                ms.http.post(ms.manager + "/system.do").then(function(res) {
                    that.loading = false
                    if (res.result && res.data) {
                        that.form = res.data;
                    }
                }).catch(function(err) {
                    console.log(err);
                });
            },
        },
        created: function() {
            var that = this;
            that.get()
        }
    });
</script>