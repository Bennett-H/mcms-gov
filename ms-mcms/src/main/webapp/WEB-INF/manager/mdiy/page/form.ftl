<#--20220306 增加多皮肤-->
<el-dialog :close-on-click-modal="false" v-cloak id="form" title="自定义页面" :visible.sync="dialogVisible" width="50%">
    <el-form ref="form" :model="form" :rules="rules" label-width="130px" size="mini">
        <el-row>
            <el-col :span="24">
                <el-form-item  label="标题" prop="pageTitle">
                    <el-input v-model="form.pageTitle"
                              :disabled="false"
                              :style="{width:  '100%'}"
                              :clearable="true"
                              placeholder="请输入自定义页面标题">
                    </el-input>
                </el-form-item>
            </el-col></el-row>
        <el-row>
            <el-row>
                <el-col span="24">
                    <el-form-item  label="分类" prop="pageType">
                        <el-select v-model="form.pageType"
                                   :style="{width: '100%'}"
                                   :filterable="false"
                                   :disabled="false"
                                   :multiple="false" :clearable="true"
                                   placeholder="请选择分类">
                            <el-option v-for='item in pageTypeOptions' :key="item.dictValue" :value="item.dictValue"
                                       :label="item.dictLabel"></el-option>
                        </el-select>
                        <div class="ms-form-tip">
                            可以通过 <b>自定义字典</b> 配置，根据不同的业务模块定义，方便业务开发管理。
                        </div>

                    </el-form-item>

                </el-col>
            </el-row>

            <#--循环主题-->
            <el-col :span="24" v-for='(item,i) in styles' :key="i">
                <el-form-item :label="item.name">
                    <el-select v-model="form.pagePath[i].file"
                               @visible-change="queryTemplateFileForColumn(item.template)"
                               :filterable="true"
                               :clearable="true"
                               placeholder="请选择模板">
                        <el-option v-for='item in templateOptions' :key="item" :value="item"
                                   :label="item"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>

        <el-form-item  label="路径关键字" prop="pageKey">
            <el-input v-model="form.pageKey"
                      :disabled="false"
                      :style="{width:  '100%'}"
                      :clearable="true"
                      placeholder="请输入自定义页面访问路径">
            </el-input>
            <div class="ms-form-tip">
                路径关键字决定了访问地址的路径<br/>
                例如：输入"login"对应访问的地址为 "域名/mdiyPage/login.do"，<br/>
                注意：会员登录后访问的路径，必须带有"people/"前缀<br/>
                如：个人中心"people/center",对应访问地址："域名/people/center.do"
            </div>

        </el-form-item>
    </el-form>
    <div slot="footer">
        <el-button size="mini" @click="dialogVisible = false">取 消</el-button>
        <el-button size="mini" type="primary" @click="save()" :loading="saveDisabled">保存</el-button>
    </div>
</el-dialog>
<script>
    var form = new Vue({
        el: '#form',
        data: function () {
            return {
                saveDisabled: false,
                dialogVisible: false,
                //表单数据
                form: {
                    pageType: '',
                    // 自定义页面标题
                    pageTitle: '',
                    // 绑定模板
                    pagePath: [{file:""}],
                    // 自定义页面访问路径
                    pageKey: ''
                },
                dictList: [],//模板类型字典数据
                pagePathOptions: [],
                pageTypeOptions: [],
                rules: {
                    pageTitle: [{
                        "required": true,
                        "message": "标题必须填写"
                    }, {
                        "min": 1,
                        "max": 30,
                        "message": "标题长度必须为1-30"
                    }],
                    // 绑定模板
                    pagePath: [{
                        "required": true,
                        "message": "绑定模板必须填写"
                    }],
                    // 访问路径
                    pageKey: [{
                        "required": true,
                        "message": "访问路径必须填写"
                    }, {
                        "min": 1,
                        "max": 300,
                        "message": "访问路径长度必须为1-300"
                    }],
                    // 分类
                    pageType: [{
                        "required": true,
                        "message": "请选择分类"
                    }]
                },
                styles:[], //样式
                templateOptions: [],//下拉选择模版
            };
        },
        watch: {
            dialogVisible: function (v) {
                if (!v) {
                    this.$refs.form.resetFields();
                    this.form.id = 0;
                }
            }
        },
        computed: {},
        methods: {
            open: function (id) {
                var that = this;
                this.getApp();
                if (id) {
                    that.get(id);
                }
                //初始化路径
                that.form.pagePath=[{file:""}];
                that.pagePathOptionsGet();
                that.pageTypeOptionsGet();
                that.$nextTick(function () {
                    that.dialogVisible = true;
                });
            },
            save: function () {
                var that = this;
                var url = ms.manager + "/mdiy/page/save.do";

                if (that.form.id > 0) {
                    url = ms.manager + "/mdiy/page/update.do";
                }
                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        // that.form.pagePath.forEach(function(item) {
                        //     that.dictList.forEach(function(dict) {
                        //        //将模板名替换为字典值
                        //        if (item.name == dict.dictLabel){
                        //            item.template = dict.dictValue
                        //        }
                        //     })
                        // })
                        var data = JSON.parse(JSON.stringify(that.form));
                        //进行深度复制
                        data.pagePath =  JSON.stringify(that.form.pagePath);
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success'
                                });
                                that.saveDisabled = false;
                                indexVue.list();
                            }else {
                                that.$notify({
                                    title: '失败',
                                    message: data.msg,
                                    type: 'warning'
                                });
                                that.saveDisabled = false;
                            }
                            that.dialogVisible = false;
                            // that.form.pagePath = [{file:""}];

                        });
                    } else {
                        return false;
                    }
                });
            },
            //获取当前自定义页面
            get: function (id) {
                var that = this;

                ms.http.get(ms.manager + "/mdiy/page/get.do", {
                    "id": id
                }).then(function (res) {
                    if (res.result) {
                        res.data.pagePath = JSON.parse(res.data.pagePath)

                        //  创建新数组数据存储配置的站点列表名称
                        var pageFile = []
                        res.data.pagePath.forEach(function(item) {
                            //  将已有配置站点名称添加新数组
                            pageFile.push(item.name)
                        })

                        //  循环需要配置的站点名称设置
                        that.styles.forEach(function(item) {
                            //  判断需要配置的站点名称是否在已配置项中有该名称，
                            //  没有出现则添加一组空模板数据在配置项中
                            if(!pageFile.includes(item.name)) {
                                res.data.pagePath.push(item)
                            }
                        })

                        that.form = res.data;
                        that.getApp();
                    }
                })
            },
            //获取pagePath数据源
            pagePathOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/basic/template/queryTemplateFileForColumn.do", {}).then(function (data) {
                    that.pagePathOptions = data.data;
                })
            },
            //获取pageType数据源
            pageTypeOptionsGet: function () {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {
                    dictType: '自定义页面类型'
                }).then(function (data) {
                    if(data.result){
                        data = data.data;
                        that.pageTypeOptions = data.rows;
                    }
                })
            },
            //获取模板类型 字典数据
            dictListGet: function () {
                var that = this;
                //获取  模板类型 字典数据
                ms.mdiy.dict.list('模板类型').then(function (res) {
                    that.dictList = res.data.rows
                })
            },

            //获取当前应用表
            getApp: function () {
                var that = this;

                ms.http.get(ms.manager + "/basic/app/get.do", {}).then(function (res) {
                    if (res.result && res.data) {
                        try {
                            that.styles = JSON.parse(res.data.appStyles);
                        } catch (e) {
                            that.$notify({
                                title: '提示',
                                message: "当前站点没有绑定模版",
                                type: 'warning'
                            });
                        }
                        if (that.styles.length==0) {
                            that.$notify({
                                title: '提示',
                                message: "当前站点没有绑定模版",
                                type: 'warning'
                            });
                        }

                        if(that.form.pagePath!="") {

                            var tempListUrls = []
                            that.styles.forEach(function(app){
                                var isExits = false;
                                that.form.pagePath.forEach(function(item){
                                    if(item.name==app.name) {
                                        item.template = app.template; //纠正模版
                                        tempListUrls.push(item);
                                        isExits = true;
                                    }
                                });
                                if(!isExits) {
                                    app.file="";
                                    tempListUrls.push(app);
                                }
                            });

                            that.form.pagePath = tempListUrls;

                        }


                        if(that.form.pagePath.length==0 ) {
                            //组织封面与列表模版
                            let _appStyles = JSON.parse(JSON.stringify(that.styles));
                            _appStyles.forEach(function(item){
                                // 定义个file 用于存储选中的模板
                                item.file=""
                                // JSON.parse(JSON.stringify())对象深度克隆,避免两个重复引用
                                that.form.pagePath.push(JSON.parse(JSON.stringify(item)));
                            })
                        }

                    }
                })
            },
            //获取主题模板数据源
            queryTemplateFileForColumn: function (appStyle) {
                var that = this;
                that.templateOptions =[];
                ms.http.get(ms.manager + '/basic/template/queryTemplateFileForColumn.do', {"appStyle": appStyle}).then(function (data) {
                    that.templateOptions = data.data; //寻找主页
                })
            },
        },

        created:function() {
            var that = this;
            this.dictListGet()
            this.rules.pageKey.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/mdiy/page/verify.do", {
                        fieldName: "page_key",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function(res){
                        if (res.result) {
                            if (!res.data) {
                                callback("访问路径已存在");
                            } else {
                                callback();
                            }
                        }
                    });
                },
                trigger: ['blur']
            })
        }
    });
</script>
