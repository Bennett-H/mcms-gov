<!DOCTYPE html>
<html>
<head>
    <title>用户</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/mdiy/index.js"></script>
</head>
<body>
<div id="form" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
        </el-button>
        <el-button size="mini" plain onclick="javascript:history.go(-1)"><i class="iconfont icon-fanhui"></i>返回
        </el-button>
    </el-header>
    <el-main class="ms-container" style="position:relative;">
        <el-scrollbar style="height: calc(100vh - 50px)">
            <el-tabs v-model="activeName" style="height: calc(100% - 10px);">

                <el-tab-pane label="基本信息" name="基本信息">
                    <el-form  ref="form" :model="form" :rules="rules" label-width="100px" size="mini">
                        <el-main class="ms-container ms-margin-bottom-zero">
                            <el-row
                                    gutter="0"
                                    justify="start" align="top">
                                <el-col span="12">
                                    <el-form-item label="账号" prop="peopleName">
                                        <el-input v-model="form.peopleName"
                                                  :disabled="false"
                                                  :style="{width:  '100%'}"
                                                  :clearable="true"
                                                  placeholder="请输入账号">
                                        </el-input>
                                    </el-form-item>
                                </el-col>
                                <el-col span="12">
                                    <el-form-item label="密码" prop="peoplePassword">
                                        <el-input v-model="form.peoplePassword"
                                                  type="password"
                                                  autocomplete='new-password'
                                                  :disabled="false"
                                                  :style="{width:  '100%'}"
                                                  :clearable="true"
                                                  placeholder="请输入密码">
                                        </el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row
                                    gutter="0"
                                    justify="start" align="top">
                                <el-col span="12">
                                    <el-form-item label="手机号码" prop="peoplePhone">
                                        <el-input v-model="form.peoplePhone"
                                                  :disabled="false"
                                                  :style="{width:  '100%'}"
                                                  :clearable="true"
                                                  placeholder="请输入手机号码">
                                        </el-input>
                                    </el-form-item>
                                </el-col>
                                <el-col span="12">
                                    <el-form-item label="手机绑定" prop="peoplePhoneCheck">
                                        <el-switch v-model="form.peoplePhoneCheck"
                                                   :active-value="1"
                                                   :inactive-value="0"
                                                   :disabled="false">
                                        </el-switch>
                                        <div class="ms-form-tip">手机号绑定可以用来登录</div>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row
                                    gutter="0"
                                    justify="start" align="top">
                                <el-col span="12">
                                    <el-form-item label="邮箱" prop="peopleMail">
                                        <el-input v-model="form.peopleMail"
                                                  :disabled="false"
                                                  :style="{width:  '100%'}"
                                                  :clearable="true"
                                                  placeholder="请输入邮箱">
                                        </el-input>
                                    </el-form-item>
                                </el-col>
                                <el-col span="12">
                                    <el-form-item label="邮箱绑定" prop="peopleMailCheck">
                                        <el-switch v-model="form.peopleMailCheck"
                                                   :active-value="1"
                                                   :inactive-value="0"
                                                   :disabled="false">
                                        </el-switch><div class="ms-form-tip">邮箱绑定可以用来登录，未绑定则不能通过邮箱登录</div>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row
                                    gutter="0"
                                    justify="start" align="top">
                                <el-col span="12">
                                    <el-form-item label="昵称" prop="puNickname">
                                        <el-input v-model="form.puNickname"
                                                  :disabled="false"
                                                  :style="{width:  '100%'}"
                                                  :clearable="true"
                                                  placeholder="请输入昵称">
                                        </el-input>
                                    </el-form-item>
                                </el-col>
                                <el-col span="12">
                                    <el-form-item label="性别" prop="puSex">
                                        <el-select v-model="form.puSex"
                                                   :style="{width: '100%'}"
                                                   :disabled="false"
                                                   :multiple="false" :clearable="true"
                                                   placeholder="请选择性别">
                                            <el-option v-for='item in puSexOptions' :key="item.value"
                                                       :value="item.value"
                                                       :label="item.label"
                                                       :label="false?item.label:item.value"></el-option>
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                        </el-main>
                    </el-form>
                </el-tab-pane>

                <el-tab-pane label="详细信息" name="详细信息">
                    <el-form  ref="form-more" :model="form"  label-width="100px" size="mini">
                        <el-main class="ms-container ms-margin-bottom-zero">

                            <el-row
                                    gutter="0"
                                    justify="start" align="top">
                                <el-col span="12">
                                    <el-form-item label="真实姓名" prop="puRealName">
                                        <el-input v-model="form.puRealName"
                                                  :disabled="false"
                                                  :style="{width:  '100%'}"
                                                  :clearable="true"
                                                  placeholder="请输入真实姓名">
                                        </el-input>
                                    </el-form-item>
                                </el-col>
                                <el-col span="12">
                                    <el-form-item label="身份证" prop="puCard">
                                        <el-input v-model="form.puCard"
                                                  :disabled="false"
                                                  :style="{width:  '100%'}"
                                                  :clearable="true"
                                                  placeholder="请输入身份证">
                                        </el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row
                                    gutter="0"
                                    justify="start" align="top">
                                <el-col span="12">
                                    <el-form-item label="生日" prop="puBirthday">
                                        <el-date-picker
                                                v-model="form.puBirthday"
                                                placeholder="请选择用户生日"
                                                :readonly="false"
                                                :disabled="false"
                                                :editable="true"
                                                :clearable="true"
                                                format="yyyy-MM-dd"
                                                value-format="yyyy-MM-dd"
                                                :style="{width:'100%'}"
                                                type="date">
                                        </el-date-picker>
                                    </el-form-item>
                                </el-col>

                                <el-col span="12">
                                    <el-form-item label="用户等级" prop="puLevel">
                                        <template slot='label'>用户等级
                                        </template>
                                        <el-select v-model="form.puLevel"
                                                   @change="puLevelSelectChange"
                                                   :style="{width: '100%'}"
                                                   :filterable="false"
                                                   :disabled="false"
                                                   :multiple="false" :clearable="true"
                                                   placeholder="请选择用户等级">
                                            <el-option v-for='item in puLevelOptions' :key="item.dictValue"
                                                       :value="item.dictValue"
                                                       :label="item.dictLabel"></el-option>
                                        </el-select>

                                        <div class="ms-form-tip">通过 自定义字典 配置，类型为“用户等级类型”</div>

                                    </el-form-item>

                                </el-col>
                            </el-row>


                            <el-form-item label="城市选择" prop="provinceId">
                                <el-row gutter="10" justify="start" align="top">
                                    <el-col span="6">
                                        <el-select v-model="form.provinceId"
                                                   :style="{width: '100%'}"
                                                   :disabled="false"
                                                   :multiple="false" :clearable="true"
                                                   @change="form.cityId='';form.cityName='';form.countyId='';form.countyName='';form.provinceName=cityData(provinces,$event).name"
                                                   placeholder="请选择省份">
                                            <el-option v-for='(item,index) in provinces' :key="index" :value="item.id"
                                                       :data-label='item.name'  :label="item.name"></el-option>
                                        </el-select>
                                    </el-col>
                                    <el-col span="8">

                                        <el-select v-model="form.cityId"
                                                   :style="{width: '100%'}"
                                                   :disabled="false"
                                                   :multiple="false" :clearable="true"
                                                   @change="form.countyId='';form.countyName='';form.cityName=cityData(cityData(provinces,form.provinceId).childrensList,$event).name"
                                                   placeholder="请选择城市">
                                            <el-option
                                                    v-for='(item,index) in cityData(provinces,form.provinceId).childrensList'
                                                    :key="index" :value="item.id"
                                                    :data-label='item.name' :label="item.name"></el-option>
                                        </el-select>
                                    </el-col>
                                    <el-col span="8">
                                        <el-select v-model="form.countyId"
                                                   :style="{width: '100%'}"
                                                   :disabled="false"
                                                   :multiple="false" :clearable="true"
                                                   @change="form.countyName=cityData(cityData(cityData(provinces,form.provinceId).childrensList,form.cityId).childrensList,$event).name"
                                                   placeholder="请选择区域">
                                            <el-option
                                                    v-for='(item,index) in cityData(cityData(provinces,form.provinceId).childrensList,form.cityId).childrensList'
                                                    :key="index" :value="item.id"
                                                    :data-label='item.name'  :label="item.name"></el-option>
                                        </el-select>
                                    </el-col>
                                </el-row>
                            </el-form-item>
                            <el-form-item label="地址" prop="puAddress">
                                <el-input
                                        type="textarea" :rows="5"
                                        :disabled="false"
                                        v-model="form.puAddress"
                                        :style="{width: '100%'}"
                                        placeholder="请输入地址">
                                </el-input>
                            </el-form-item>

                            <el-form-item label="头像" prop="puIcon">
                                <el-upload
                                        :file-list="puIcon"
                                        :action="ms.manager+'/file/upload.do'"
                                        :on-remove="puIconhandleRemove"
                                        :style="{width:''}"
                                        :limit="1"
                                        :disabled="false"
                                        :data="{uploadPath:'/people/user','isRename':true,'appId':true}"
                                        :on-success="puIconBasicPicSuccess"
                                        :on-exceed="puIconhandleExceed"
                                        accept="image/*"
                                        list-type="picture-card">
                                    <i class="el-icon-plus"></i>
                                    <div slot="tip" class="el-upload__tip">最多上传1张头像</div>
                                </el-upload>
                            </el-form-item>
                        </el-main>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane label="扩展会员信息" name="扩展会员信息">
                    <div id="peopleUserModel"></div>
                </el-tab-pane>
            </el-tabs>
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
                provinces: [],
                activeName: '基本信息',
                //自定义模型实例
                peopleUserModel: undefined,

                categoryIdOptions: [],
                //表单数据
                form: {
                    // 用户名
                    peopleName: '',
                    // 密码
                    peoplePassword: '',
                    // 昵称
                    puNickname: '',
                    // 性别
                    puSex: '',
                    //用户生日
                    puBirthday: '',
                    // 真实姓名
                    puRealName: '',
                    // 身份证
                    puCard: '',
                    // 手机号码
                    peoplePhone: '',
                    // 邮箱
                    peopleMail: '',
                    // 手机验证
                    peoplePhoneCheck: '',
                    // 邮箱验证
                    peopleMailCheck: '',
                    // 用户状态
                    peopleState: '',
                    // 头像
                    puIcon: '',
                    // 城市选择
                    provinceId: '',
                    // 省
                    provinceName: '',
                    // 城市
                    cityName: '',
                    // 区
                    countyName: '',
                    // 城市id
                    cityId: '',
                    // 区id
                    countyId: '',
                    // 地址
                    puAddress: '',
                    //用户等级
                    puLevel: null,
                    //用户等级名称
                    puLevelName: null
                },
                puIcon: [],
                puLevelOptions: [],
                puSexOptions: [{
                    "value": 1,
                    "label": "男"
                }, {
                    "value": 2,
                    "label": "女"
                }],
                rules: {
                    // 登录名
                    peopleName: [{
                        "required": true,
                        "message": "登录名不能为空"
                    }, {"pattern": "^[^[!@#$%^&*()_+-/~?！@#￥%…&*（）——+—？》《：“‘’]+$", "message": "登录名格式不匹配"}, {
                        "min": 6,
                        "max": 30,
                        "message": "登录名长度必须为6-30"
                    }],
                    // 登录密码
                    peoplePassword: [{
                        "required": true,
                        "message": "登录密码不能为空"
                    }, {"pattern": "^[^[!@#$%^&*()_+-/~?！@#￥%…&*（）——+—？》《：“‘’]+$", "message": "登录密码格式不匹配"}, {
                        "min": 6,
                        "max": 30,
                        "message": "登录密码长度必须为6-30"
                    }],
                    // 昵称
                    puNickname: [{
                        "min": 1,
                        "max": 50,
                        "message": "昵称长度必须为1-50"
                    }],
                    // 真实姓名
                    puRealName: [{
                        "min": 1,
                        "max": 50,
                        "message": "真实姓名长度必须为1-50"
                    }],
                    // 身份证
                    puCard: [{
                        "pattern": "^[X0-9]+$",
                        "message": "身份证格式不匹配"
                    }, {
                        "min": 1,
                        "max": 50,
                        "message": "身份证长度必须为1-50"
                    }],
                    // 手机号码
                    peoplePhone: [{
                        "pattern": /^[1][0-9]{10}$/,
                        "message": "手机号码格式不匹配"
                    }],
                    // 邮箱
                    peopleMail: [{
                        "pattern": "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$",
                        "message": "邮箱格式不匹配"
                    }, {
                        "min": 1,
                        "max": 50,
                        "message": "邮箱长度必须为1-50"
                    }]
                }
            };
        },
        watch: {},
        computed: {
            cityData: function (data, id) {
                return function (data, id) {
                    var city = [];

                    for (var i in data) {
                        if (data[i].id == id) {
                            city = data[i];
                            break;
                        }
                    }

                    return city;
                };
            }
        },
        methods: {
            //对象拷贝 用来去除多余额数据
            objectCopy: function (src, o) {
                for (key in src) {
                    if (o[key] != undefined) {
                        src[key] = o[key];
                    }
                }
            },
            save: function () {
                var that = this;
                var url = ms.manager + "/people/peopleUser/save.do";
                if (that.form.peopleId > 0) {
                    url = ms.manager + "/people/peopleUser/update.do";
                    // 更新的时候不用判断是否填写密码
                    that.rules.peoplePassword[0].required = false;
                }
                // 判断自定义模型的表单是否通过校验
                if (that.peopleUserModel && !that.peopleUserModel.validate()) {
                    that.activeName = '扩展信息';
                    return;
                }

                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        if (that.puIcon && that.puIcon.length > 0) {
                            that.form.puIcon = that.puIcon[0].path;
                        }
                        var data = JSON.parse(JSON.stringify(that.form));

                        //保存用户信息
                        ms.http.post(url, data).then(function (res) {
                            if (res.result) {
                                // 自定义模型保存,
                                if (that.peopleUserModel) {
                                    //将自定义模型的linkId与用户的Id，形成一对一关系
                                    that.peopleUserModel.form.linkId = res.data.id;
                                    //执行保存请求
                                    that.peopleUserModel.save();
                                }
                                that.$notify({
                                    title: '成功',
                                    message: res.data.peopleState == 0 ? '保存成功,已将数据纳入待审核中!' : '保存成功!',
                                    type: 'success',
                                    duration: 1500,
                                    onClose: function () {
                                        window.history.go(-1);
                                    }
                                });
                                that.saveDisabled = false;

                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg,
                                    type: 'warning'
                                });
                                that.saveDisabled = false;
                            }
                        });
                    } else {
                        // 自定义模型
                        that.activeName = '基本信息';
                        return false;
                    }
                });
            },
            //获取当前用户
            get: function (id) {
                var that = this;
                ms.http.get(ms.manager + "/people/peopleUser/get.do", {
                    "peopleId": id
                }).then(function (res) {
                    if (res.result) {
                        that.objectCopy(that.form, res.data);
                        that.form.peopleDateTime = '';
                        // that.puIcon = that.form.puIcon;
                        if (that.form.puIcon) {
                            that.puIcon=[{url: that.form.puIcon, path: that.form.puIcon, uid: ""}];
                        }
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            puLevelSelectChange: function (level) {
                var dict = this.puLevelOptions.find(function (item) {
                    return item.dictValue == level;
                });
                this.form.puLevelName = dict.dictLabel;
            },
            getCityData: function () {
                var that = this;
                ms.http.get(ms.base + "/basic/city/query.do", {
                    pageSize: 9999
                }).then(function (data) {
                    if (data.result) {
                        that.provinces = data.data;
                    }
                });
            },
            //获取puLevel数据源
            puLevelOptionsGet: function () {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {
                    dictType: '用户等级类型',
                    pageSize: 99999
                }).then(function (res) {
                    if (res.result) {
                        res = res.data;
                        that.puLevelOptions = res.rows;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //上传超过限制
            puIconhandleExceed: function (files, fileList) {
                this.$notify({
                    title: '失败',
                    message: '当前最多上传1张头像',
                    type: 'warning'
                });
            },
            //puIcon文件上传完成回调
            puIconBasicPicSuccess: function (response, file, fileList) {
                if (response.result) {
                    this.puIcon = response.data;
                    this.puIcon = [];
                    this.puIcon.push({url: file.url, path: response.data, uid: file.uid});
                } else {
                    this.$notify({
                        title: '失败',
                        message: response.msg,
                        type: 'warning'
                    });
                }
            },
            puIconhandleRemove: function (file, files) {
                // var index = -1;
                this.form.puIcon = '';
                this.puIcon = [];
                // index = this.form.puIcon.findIndex(function (text) {
                //     return text == file;
                // });
                //
                // if (index != -1) {
                //     this.form.puIcon.splice(0, 1);
                // }
            },

        },
        created: function () {
            this.puLevelOptionsGet();
            this.getCityData();
            this.form.peopleId = ms.util.getParameter("peopleId");

            if (this.form.peopleId) {
                this.get(this.form.peopleId);
            } else {
                this.rules.peoplePassword.push({
                    "required": true,
                    "message": "密码必须填写"
                });
            }
            var that = this;

            //加载自定义模型，modelName：模型名称，linkId：主表ID
            ms.mdiy.model.extend("peopleUserModel", {"modelName":"扩展会员信息"},{ linkId: this.form.peopleId },true).then(function(obj) {
                //赋值
                that.peopleUserModel = obj;
            });

        }
    });
</script>
<style>
    #form .ms-container {
        margin: 12px;
        height: calc(100% - 24px);
        padding: 14px;
        background: #fff;
    }

    #form .ms-footer {
        margin: 0px 12px 12px 12px;
        height: 100% !important;
        padding: 14px;
        background: #fff;
    }

    .el-select {
        width: 100%;
    }

    body {
        overflow: hidden;
    }

    #form {
        overflow: hidden;
    }

    .el-scrollbar__bar.is-vertical {
        width: 6px !important;
    }
</style>
