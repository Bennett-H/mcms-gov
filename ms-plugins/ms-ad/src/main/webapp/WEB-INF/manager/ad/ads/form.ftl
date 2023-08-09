<!DOCTYPE html>
<html>
<head>
    <title>广告</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="form" v-cloak class="ms-index">
    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
        </el-button>
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container">
        <el-scrollbar class="ms-scrollbar" style="height: 100%;">
        <el-form ref="form" :model="form" :rules="rules" label-width="110px" size="mini">
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <el-form-item label="广告位" prop="positionId">
                        <el-select v-model="form.positionId"
                                   :style="{width: '100%'}"
                                   :filterable="false"
                                   :disabled="false"
                                   :multiple="false" :clearable="true"
                                   placeholder="请选择广告位">
                            <el-option v-for='item in positionOptions' :key="item.id"
                                       :value="item.id"
                                       :label="item.positionName"></el-option>
                        </el-select>
                        <div class="ms-form-tip">
                            获取广告位下的所有广告：{@ms:ad "广告位名称"/}
                        </div>
                    </el-form-item>
                </el-col>
                <el-col span="12">
                    <el-form-item label="广告名称" prop="adsName">
                        <el-input v-model="form.adsName"
                                  :disabled="false"
                                  :style="{width:  '100%'}"
                                  :clearable="true"
                                  placeholder="请输入广告名称">
                        </el-input>
                        <div class="ms-form-tip">
                            精确获取当前广告: {@ms:ad "广告位名称" "广告名称"/}
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <el-form-item label="广告类型" prop="adsType">
                        <el-select v-model="form.adsType"
                                   :style="{width: '100%'}"
                                   :filterable="false"
                                   :disabled="false"
                                   :multiple="false" :clearable="true"
                                   placeholder="请选择广告类型">
                            <el-option v-for='item in adsTypeOptions' :key="item.dictValue" :value="item.dictValue"
                                       :label="item.dictLabel"></el-option>
                        </el-select>
                        <div class="ms-form-tip">
                            类型不满足可以在自定义字典菜单中新增,字段类型为“广告类型”<br/>
                        </div>
                    </el-form-item>
                </el-col>
                <el-col span="12">
                    <el-form-item label="是否开启" prop="adsState">
                        <el-radio-group v-model="form.adsState"
                                        :style="{width: ''}"
                                        :disabled="false">
                            <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.value"
                                      v-for='(item, index) in adsStateOptions' :key="item.value + index">
                                {{item.label}}
                            </el-radio>
                        </el-radio-group>
                        <div class="ms-form-tip">
                            关闭后此条广告将被过滤,不再展示至页面<br/>
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <el-form-item label="广告时间" prop="adsTime">

                        <el-date-picker
                                v-model="form.adsTime"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                                type="daterange"
                                :readonly="false"
                                :disabled="false"
                                :editable="true"
                                @input="changeTime"
                                :clearable="true"
                                value-format="yyyy-MM-dd"
                                :style="{width:'100%'}"
                                type="date">
                        </el-date-picker>
                        <div class="ms-form-tip">
                            选择广告时间后广告会在该时间范围内展示，不在范围内则不展示
                        </div>
                    </el-form-item>
                </el-col>
                </el-form-item>
                <el-col span="12">
                    <el-form-item label="广告链接" prop="adsUrl">
                        <el-input v-model="form.adsUrl"
                                  :disabled="false"
                                  :style="{width:  '100%'}"
                                  :clearable="true"
                                  placeholder="请输入广告链接">
                        </el-input>
                        <div class="ms-form-tip">
<#--                            标签：<#noparse>${field.url}</#noparse>-->
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    gutter="0"
                    justify="start" align="top">
                <el-col span="12">
                    <el-form-item label="联系人电话" prop="adsPeoplePhone">
                        <el-input v-model="form.adsPeoplePhone"
                                  :disabled="false"
                                  :style="{width:  '100%'}"
                                  :clearable="true"
                                  placeholder="请输入广告联系人电话">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col span="12">
                    <el-form-item label="联系人邮箱" prop="adsPeopleEmail">
                        <el-input v-model="form.adsPeopleEmail"
                                  :disabled="false"
                                  :style="{width:  '100%'}"
                                  :clearable="true"
                                  placeholder="请输入广告联系人邮箱">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col span="12">
                    <el-form-item label="广告联系人" prop="adsPeopleName">
                        <el-input v-model="form.adsPeopleName"
                                  :disabled="false"
                                  :style="{width:  '100%'}"
                                  :clearable="true"
                                  placeholder="请输入广告联系人">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
                <el-form-item label="广告图片" prop="adsImg">
                    <el-upload
                            :file-list="form.adsImg"
                            :action="ms.base+'/file/upload.do'"
                            :on-remove="adsImghandleRemove"
                            :style="{width:''}"
                            :limit="1"
                            :on-exceed="adsImghandleExceed"
                            :disabled="false"
                            :data="{uploadPath:'/ad/ads','isRename':true,'appId':true}"
                            :on-success="adsImgSuccess"
                            accept="image/*"
                            list-type="picture-card">
                        <i class="el-icon-plus"></i>
                        <div slot="tip" class="el-upload__tip">最多上传1张图片
                        </div>
                    </el-upload>
                    <div class="ms-form-tip">
<#--                        标签：<#noparse>{@ms:file field.img/}</#noparse>-->
                    </div>
                </el-form-item>
<#--            <el-form-item label="广告内容" prop="adsContent">-->
<#--                <vue-ueditor-wrap style="line-height: 0px" v-model="form.adsContent"-->
<#--                                  :config="ms.editorConfig"></vue-ueditor-wrap>-->
<#--                <div class="ms-form-tip">-->
<#--&lt;#&ndash;                    标签：<#noparse>${field.content}</#noparse>&ndash;&gt;-->
<#--                </div>-->
<#--            </el-form-item>-->
        </el-form>
        </el-scrollbar>
    </el-main>
</div>
</body>
</html>
<script>
    Vue.component('vue-ueditor-wrap', VueUeditorWrap);
    var form = new Vue({
        el: '#form',
        data:function() {
            return {
                saveDisabled: false,
                //表单数据
                form: {
                    // 广告位编号
                    positionId: '',
                    // 广告名称
                    adsName: '',
                    // 广告类型
                    adsType: '',
                    // 广告链接
                    adsUrl: '',
                    // 开始时间
                    adsStartTime: '',
                    // 结束时间
                    adsEndTime: '',
                    // 广告图片
                    adsImg: [],
                    // 是否开启
                    adsState: 'open',
                    // 广告联系人
                    adsPeopleName: '',
                    // 广告联系人电话
                    adsPeoplePhone: '',
                    // 广告联系人邮箱
                    adsPeopleEmail: '',
                    adsTime: [],
                },

                adsTypeOptions: [],
                // 广告位数据
                positionOptions: [],
                adsStateOptions: [{"value": "open", "label": "开启"}, {"value": "close", "label": "关闭"}],
                rules: {
                    // 广告名称
                    adsName: [{"type": "string", "message": "广告名称格式不正确"}, {"required": true, "message": "请选择广告名称"}],
                    // 广告位
                    positionId: [{"required": true, "message": "请选择广告位"}],
                    // 广告类型
                    adsType: [{"required": true, "message": "请选择广告类型"}],
                    adsTime: [{"required": true, "message": "开始时间、结束时间不能为空"}],
                    // 开始时间
                    adsStartTime: [{"required": true, "message": "开始时间不能为空"}],
                    // 结束时间
                    adsEndTime: [{"required": true, "message": "结束时间不能为空"}],
                    // 是否开启
                    adsState: [{"required": true, "message": "是否开启不能为空"}],
                    // 广告联系人电话
                    adsPeoplePhone: [{
                        "pattern": /^([0-9]{3,4}-)?[0-9]{7,8}$|^\d{3,4}-\d{3,4}-\d{3,4}$|^1[0-9]{10}$/,
                        "message": "广告联系人电话格式不匹配"
                    }],
                    // 广告联系人邮箱
                    adsPeopleEmail: [{"type":"email","message":"广告联系人邮箱格式不正确"},{"pattern":"","message":"广告联系人邮箱格式不匹配"}],
                    // 广告联系人
                    adsPeopleName: [{"min":0,"max":50,"message":"广告联系人长度应在0-50之间"}],
                    // 图片必填
                    adsImg: [{"required":true,"message":"图片不能为空"}],
                },

            }
        },
        watch: {},
        computed: {},
        methods: {

            save:function() {
                var that = this;
                var url = ms.manager + "/ad/ads/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/ad/ads/update.do";
                }
                that.form.adsStartTime = that.form.adsTime[0];
                that.form.adsEndTime = that.form.adsTime[1];
                this.$refs.form.validate(function(valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        data.adsImg = JSON.stringify(data.adsImg);
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success'
                                });
                                location.href = ms.manager + "/ad/ads/index.do";
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
                })
            },

            //获取当前广告
            get:function(id) {
                var that = this;
                ms.http.get(ms.manager + "/ad/ads/get.do", {"id": id}).then(function (res) {
                    if (res.data.adsImg) {
                        res.data.adsImg = JSON.parse(res.data.adsImg);
                        res.data.adsImg.forEach(function (value) {
                            value.url = ms.base + value.path
                        })
                    } else {
                        res.data.adsImg = []
                    }


                    if (res.result && res.data) {
                        that.form = res.data;
                        that.form.adsTime = [res.data.adsStartTime, res.data.adsEndTime];
                        res.data.positionId += "";

                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取广告位数据源
            positionNameOptionsGet:function(positionName) {
                var that = this;
                ms.http.get(ms.manager + "/ad/position/list.do", {positionName: positionName}).then(function (data) {
                    if(data.result){
                        that.positionOptions = data.data.rows;
                        console.log(that.positionOptions)
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取adsType数据源
            adsTypeOptionsGet:function() {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {dictType: '广告类型', pageSize: 99999}).then(function (data) {
                    if(data.result){
                        data = data.data;
                        that.adsTypeOptions = data.rows;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //adsImg文件上传完成回调
            adsImgSuccess: function (response, file, fileList) {
                if(response.result){
                    this.form.adsImg.push({url: file.url, name: file.name, path: response.data, uid: file.uid});

                }else {
                    this.$notify({
                        title: response.msg,
                        type: 'warning'
                    });
                }
            },
            adsImghandleRemove: function (file, files) {

                var index = -1;
                index = this.form.adsImg.findIndex(function (text) {
                    return text == file;
                });

                if (index != -1) {
                    this.form.adsImg.splice(index, 1);
                }
            },

            //上传超过限制
            adsImghandleExceed: function (files, fileList) {
                this.$notify({title: '上传提示', message: '当前最多上传1个文件',type: 'warning'});
            },
            //重新渲染时间
            changeTime:function(e){
                this.$forceUpdate()
            },
        },
        created:function() {
            this.adsTypeOptionsGet();
            this.positionNameOptionsGet();
            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
        }
    });
</script>
