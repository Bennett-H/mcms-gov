<#--在ms-mcms版本上进行了优化,不能直接覆盖开源版本-->
<#--增加多模版选择 ms-select-style.ftl ,移除默认模版-->
<#--关键变量 categoryListUrls categoryUrls -->
<!DOCTYPE html>
<html>
<head>
    <title>分类</title>
    <#include "../../include/head-file.ftl">
    <#include "../../component/ms-select-style.ftl">
</head>
<body>
<div id="form" v-cloak>
    <el-header class="ms-header" height="50px">
        <el-row type="flex" justify="space-between" align="middle">
            <el-col :xs="12" :sm="14" :md="16" :lg="18" :xl="18" style="display:flex;align-items:center;">
                <el-tooltip class="item" effect="dark" :content="form.id" placement="top-start">
                    <span v-if="form.id" style="max-width:calc(30% - 40px);" class="header-info">编号：{{form.id}}</span>
                </el-tooltip>
                <el-button type="text" icon="el-icon-document-copy" circle :data-clipboard-text="form.id" @click="copyString()" class="copyBtn"></el-button>
                <el-tooltip class="item" effect="dark" :content="typeLinkString" placement="top-start">
                    <span v-if="typeLinkString" style="max-width:calc(70% - 40px);" class="header-info">链接：{{ (form.categoryType!='3' ? "{ms:global.url/}" : "") + typeLinkString}}</span>
                </el-tooltip>
                <el-button type="text" icon="el-icon-document-copy" circle :data-clipboard-text="typeLinkString" @click="copyString()" class="copyBtn"></el-button>
            </el-col>
            <el-col :xs="12" :sm="10" :md="8" :lg="6" :xl="6" class="ms-tr">
                <@shiro.hasPermission name="cms:category:save,cms:category:update">
                    <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
                    </el-button>
                </@shiro.hasPermission>
                <@shiro.hasPermission name="cms:category:view">
                    <el-button type="primary" icon="el-icon-s-platform" size="mini" @click="preview()" v-if="form.id && form.categoryType!='3'" :loading="saveDisabled">预览
                    </el-button>
                </@shiro.hasPermission>
                <@shiro.hasPermission name="cms:category:save">
                    <el-button type="primary" icon="el-icon-document-copy" size="mini" :loading="copyLoading" @click="copyCategory()">克隆</el-button>
                </@shiro.hasPermission>
                <@shiro.hasPermission name="cms:category:del">
                    <el-button
                            v-if="form.leaf"
                            type="danger"
                            size="mini"
                            icon="el-icon-delete"
                            @click="del()">删除</el-button>
                </@shiro.hasPermission>
            </el-col>
        </el-row>
    </el-header>
    <el-main class="ms-container">
        <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-form ref="form" :model="form" :rules="rules" label-width="130px" size="mini">
                <el-row
                        gutter="0"
                        justify="start" align="top">
                    <el-col span="12">
                        <el-form-item label="栏目名称" prop="categoryTitle">
                            <el-input v-model="form.categoryTitle"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入栏目管理名称">
                            </el-input>
                            <div class="ms-form-tip">
                                注意：不允许栏目名称不允许含有空格，标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/lan-mu-lie-biao-ms-channel.html" target="_blank">${'$'}{field.typetitle}</a>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col span="12">
                        <el-form-item label="所属栏目" prop="categoryId">
                            <ms-tree-select :key="treeList" ref="tree" :props="{value: 'id',label: 'categoryTitle',children: 'children'}"
                                         :options="treeList" :style="{width:'100%'}"
                                         v-model="form.categoryId"></ms-tree-select>
                            <div class="ms-form-tip">
                                不指定栏目表示为顶级栏目
                            </div>
                        </el-form-item>

                    </el-col>
                </el-row>
                <el-row
                        :gutter="0"
                        justify="start" align="top">
                    <el-col span="12">
                        <el-form-item label="栏目副标题" prop="categoryShortTitle">
                            <el-input v-model="form.categoryShortTitle"
                                      :disabled="false"
                                      :style="{width:  '100%'}"
                                      :clearable="true"
                                      placeholder="请输入栏目副标题">
                            </el-input>
                            <div class="ms-form-tip">
                                标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/lan-mu-lie-biao-ms-channel.html"
                                      target="_blank">${'$'}{field.typeshorttitle}</a>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="0"
                        justify="start" align="top">
                    <el-col span="12">
                        <el-form-item label="是否显示" prop="categoryDisplay">
                            <el-radio-group v-model="form.categoryDisplay"
                                            :style="{width: ''}"
                                            :disabled="false">
                                <el-radio :style="{display: true ? 'inline-block' : 'block'}"
                                          :label="item.value"
                                          v-for='(item, index) in categoryDisplayOptions'
                                          :key="item.value + index">
                                    {{true? item.label : item.value}}
                                </el-radio>
                            </el-radio-group>
                            <div class="ms-form-tip">
                                选择否后需重新生成，与文章是否显示功能一致；若该栏目禁用，则所有子栏目也会被禁用，请谨慎操作；
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col span="12">
                        <el-form-item label="是否可被搜索" prop="categoryIsSearch">
                            <el-radio-group v-model="form.categoryIsSearch"
                                            :style="{width: ''}"
                                            :disabled="false">
                                <el-radio :style="{issearch: true ? 'inline-block' : 'block'}"
                                          :label="item.value"
                                          v-for='(item, index) in categoryIsSearchOptions'
                                          :key="item.value + index">
                                    {{true? item.label : item.value}}
                                </el-radio>
                            </el-radio-group>
                            <div class="ms-form-tip">
                                选择否后不需重新生成，该栏目下的文章将不会被搜索页搜索；若该栏目选择不可被搜索，则所有子栏目也会不可被搜索；
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row
                        gutter="0"
                        justify="start" align="top">
                    <el-col span="12">
                        <el-form-item label="栏目类型" prop="categoryType">
                            <el-radio-group v-model="form.categoryType"
                                            :style="{width: ''}"
                                            :disabled="categoryTypeDisabled">
                                <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.value"
                                          v-for='(item, index) in categoryTypeOptions' :key="item.value + index">
                                    {{true? item.label : item.value}}
                                </el-radio>
                            </el-radio-group>
                            <div class="ms-form-tip">
                                列表：常用于带列表、详情的业务，例如：新闻列表、图片列表<br>
                                内容：常用单篇文章显示，例如：关于我们、公司介绍<br>
                                链接：栏目外链接，需要配合逻辑判断&lt;#if&gt;和自定义链接使用<br>
                                注意: 内容栏目下必须发布文章才能被静态化，且内容栏目需静态化栏目才有效。
                                修改栏目时如果该栏目存在文章则不能修改栏目类型
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col span="12">
                        <el-form-item label="自定义顺序" prop="categorySort">
                            <el-input-number
                                    v-model="form.categorySort"
                                    :disabled="false"
                                    controls-position="">
                            </el-input-number>
                        </el-form-item>
                    </el-col>
                </el-row>

                <#--多风格模版选择-->
                <ms-select-style ref="msSelectStyle"  :category-list-urls.sync="categoryListUrls"
                                 :category-urls.sync="categoryUrls" ref="selectStyle" :leaf="form.leaf" :id="form.id" :category-type="form.categoryType"
                                  ></ms-select-style>


                <el-row gutter="0" justify="start" align="top">
                    <el-col span="12">
                        <el-form-item label="内容自定义模型" prop="mdiyModelId">
                            <el-select v-model="form.mdiyModelId"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="mdiyModelDisabled"
                                       :clearable="true"
                                       :multiple="false"
                                       placeholder="请选择栏目的自定义模型">
                                <el-option v-for='item in mdiyModelIdOptions' :key="item.id" :value="item.id"
                                           :label="item.modelName"></el-option>
                            </el-select>
                            <div class="ms-form-tip">
                                如果发布时候文章字段信息不够，可以采用铭飞代码生成器生成自定义模型，再通过“自定义管理->自定义模型->导入”功能导入模型，注意类型是cms
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col span="12">
                        <el-form-item prop="mdiyCategoryModelId" label="栏目自定义模型">
                            <el-select v-model="form.mdiyCategoryModelId"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="false"
                                       @change="setCategoryModel"
                                       :multiple="false" :clearable="true"
                                       placeholder="请选择栏目自定义模型">
                                <el-option v-for='item in mdiyCategoryModelListOptions' :key="item.id"
                                           :value="item.id"
                                           :label="item.modelName"></el-option>
                            </el-select>
                            <div class="ms-form-tip">
                                栏目字段不满足，使用<b>代码生成器</b>生成<b>自定义模型</b>来扩展，参考文章自定义模型<br/>
                                绑定的模型会展示在表单下方，滑动页面到底部即可查看模型效果
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col span="12">

                    </el-col>

                    <el-col span="12" v-if="form.categoryType == 3">
                        <el-form-item label="自定义链接" prop="categoryDiyUrl">
                            <el-input
                                    :disabled="false"
                                    v-model="form.categoryDiyUrl"
                                    :style="{width: '100%'}"
                                    placeholder="请输入自定义链接">
                            </el-input>
                            <div class="ms-form-tip">
                                标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/lan-mu-lie-biao-ms-channel.html" target="_blank">${'$'}{field.typeurl}</a>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row
                        :gutter="0"
                        justify="start" align="top">
                    <el-col :span="12">
                        <el-form-item label="栏目属性" prop="categoryFlag">
                            <el-select v-model="form.categoryFlag"
                                       :style="{width: '100%'}"
                                       :filterable="false"
                                       :disabled="false"
                                       :multiple="true" :clearable="true"
                                       placeholder="请选择栏目属性">
                                <el-option v-for='item in categoryFlagOptions' :key="item.dictValue"
                                           :value="item.dictValue"
                                           :label="item.dictLabel"></el-option>
                            </el-select>
                            <div class="ms-form-tip">
                                类型不满足可以在自定义字典菜单中新增,字段类型为“栏目属性”
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目拼音" prop="categoryPinyin">
                            <el-input
                                    v-model="form.categoryPinyin"
                                    :disabled="false"
                                    :readonly="false"
                                    :style="{width:  '100%'}"
                                    :clearable="true"
                                    placeholder="默认拼音根据名称生成，含有特殊字符请手动输入">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                    </el-col>
                </el-row>
                <el-form-item label="栏目管理关键字" prop="categoryKeyword">
                    <el-input
                            type="textarea" :rows="5"
                            :disabled="false"
                            v-model="form.categoryKeyword"
                            :style="{width: '100%'}"
                            placeholder="栏目管理关键字，有助于搜索"
                            maxlength="100"
                            show-word-limit>
                    </el-input>
                    <div class="ms-form-tip">
                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/lan-mu-lie-biao-ms-channel.html" target="_blank">${'$'}{field.typekeyword}</a>
                    </div>
                </el-form-item>
                <el-form-item label="栏目管理描述" prop="categoryDescrip">
                    <el-input
                            type="textarea" :rows="5"
                            :disabled="false"
                            v-model="form.categoryDescrip"
                            :style="{width: '100%'}"
                            placeholder="栏目管理描述，对栏目管理关键字的扩展">
                    </el-input>
                    <div class="ms-form-tip">
                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/lan-mu-lie-biao-ms-channel.html" target="_blank">${'$'}{field.typedescrip}</a>
                    </div>
                </el-form-item>
                <el-form-item label="banner图" prop="categoryImg">
                    <el-upload
                            :file-list="form.categoryImg"
                            :action="ms.manager+'/file/upload.do'"
                            :on-remove="categoryImghandleRemove"
                            :style="{width:''}"
                            :limit="1"
                            :on-exceed="categoryImghandleExceed"
                            :disabled="false"
                            :data="{uploadPath:'/cms/category','isRename':true,'appId':true}"
                            :on-success="categoryImgSuccess"
                            accept="image/*"
                            list-type="picture-card">
                        <i class="el-icon-plus"></i>
                        <div slot="tip" class="el-upload__tip">最多上传1张图片</div>
                    </el-upload>
                    <div class="ms-form-tip">
                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/lan-mu-lie-biao-ms-channel.html" target="_blank">${'$'}{field.typelitpic}</a>
                    </div>
                </el-form-item>
                <el-form-item label="栏目小图" prop="categoryIco">
                    <el-upload
                            :file-list="form.categoryIco"
                            :action="ms.manager+'/file/upload.do'"
                            :on-remove="categoryIcohandleRemove"
                            :style="{width:''}"
                            :limit="1"
                            :on-exceed="categoryIcohandleExceed"
                            :disabled="false"
                            :data="{uploadPath:'/cms/category','isRename':true,'appId':true}"
                            :on-success="categoryIcoSuccess"
                            accept="image/*"
                            list-type="picture-card">
                        <i class="el-icon-plus"></i>
                        <div slot="tip" class="ms-form-tip">
                            只能上传1张图片
                            标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/lan-mu-lie-biao-ms-channel.html"
                                  target="_blank"><#noparse>{@ms:file field.typeico/}</#noparse></a><br/>
                        </div>
                    </el-upload>
                </el-form-item>
            </el-form>
            <el-divider content-position="left" v-if="ctModel">栏目自定义模型</el-divider>
            <el-form ref="ctModel" :model="ctModel" label-width="130px" size="mini">
                <div id="categoryModel"></div>
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
            var that = this
            //验证拼音是否存在
            var validatorCategoryPinyin = function (rule, value, callback) {
                //
                ms.http.get(ms.manager + '/cms/category/verifyPingYin.do', {
                    id: that.form.id,
                    categoryPinyin: that.form.categoryPinyin,
                }).then(function (res) {
                    if (!res.result) {
                        callback(new Error(res.msg));
                    } else {
                        return callback();
                    }
                })
            }
            return {
                //  loading状态
                copyLoading: false,
                mdiyModelDisabled:false,//自定义模型是否可以修改
                treeList: [],
                categoryList: [],
                saveDisabled: false,
                categoryTypeDisabled: true,
                category:{},//当前栏目对象
                // 绑定模型
                ctModel: undefined,
                // 栏目模型id
                mdiyCategoryModelId: '',
                // 栏目模型列表
                mdiyCategoryModelListOptions: [],
                // 栏目是否显示
                categoryDisplayOptions: [{
                    "value": "enable",
                    "label": "是"
                }, {
                    "value": "disable",
                    "label": "否"
                }],
                // 栏目是否被搜索
                categoryIsSearchOptions: [{
                    "value": "enable",
                    "label": "是"
                }, {
                    "value": "disable",
                    "label": "否"
                }],
                //表单数据
                form: {
                    // 栏目管理名称
                    categoryTitle: '',
                    // 栏目父标题
                    categoryShortTitle: '',
                    // 所属栏目
                    categoryId: undefined,
                    // 栏目管理属性
                    categoryType: '1',
                    // 自定义顺序
                    categorySort: 0,
                    // 列表模板
                    categoryListUrl: '',
                    // 内容模板
                    categoryUrl: '',
                    // 多列表模板
                    categoryListUrls: '',
                    // 多内容模板
                    categoryUrls: '',
                    // 栏目拼音
                    categoryPinyin: '',
                    // 栏目管理关键字
                    categoryKeyword: '',
                    // 栏目是否显示
                    categoryDisplay: 'enable',
                    // 栏目是否被搜索
                    categoryIsSearch: 'enable',
                    // 栏目管理描述
                    categoryDescrip: '',
                    // banner图
                    categoryImg: [],
                    // 栏目小图
                    categoryIco: [],
                    // 自定义链接
                    categoryDiyUrl: '',
                    // 栏目管理的内容模型id
                    mdiyModelId: '',
                    //栏目字典
                    categoryFlag: []
                },
                categoryTypeOptions: [{
                    "value": "1",
                    "label": "列表"
                }, {
                    "value": "2",
                    "label": "内容"
                }, {
                    "value": "3",
                    "label": "链接"
                }],
                templateFiles: [],
                mdiyModelIdOptions: [],
                categoryFlagOptions: [],
                rules: {
                    // 栏目管理名称
                    categoryTitle: [{
                        "required": true,
                        "message": "请选择栏目管理名称"
                    }],
                    categoryIsSearch: [{
                        "required": true,
                        "message": "请选择栏目是否可被搜索"
                    }],
                    categoryPinyin: [{
                        validator: validatorCategoryPinyin, trigger: 'blur'
                    }, {
                        "pattern": /^[^[!@#$"'%^&*()_+-/~?！@#￥%…&*（）——+—？》《：“‘’]+$/,
                        "message": "拼音格式不匹配"
                    }],

                },

                categoryListUrls: [], //临时组织栏目列表模版json
                categoryUrls: [], //临时组织栏目内容模版json
            };
        },
        watch: {
            'form.categoryId': function (n, o) {
                var _this = this;

                if (n == this.form.id && this.form.id!='') {
                    //获取当前节点的父栏目
                    let  parentids  = _this.form.parentids;
                    if (parentids) {
                        let parentNode = parentids.split(',');
                        //获取最近的父节点
                        _this.form.categoryId = parentNode[parentNode.length - 1];
                    }
                    else{
                        //无父栏目就恢复顶级
                        _this.form.categoryId = '0';
                    }

                    this.$notify({
                        title: '提示',
                        message: '所属栏目不能为自身',
                        type: 'warning'
                    });
                    return;
                }

                this.categoryList.forEach(function (item) {
                    if (item.categoryParentId != null && item.categoryParentId != "" && item.categoryParentId.indexOf(_this.form.id) != -1) {
                        if (item.id == n) {
                            _this.form.categoryId = null;

                            _this.$refs.tree.clearHandle();

                            _this.$notify({
                                title: '提示',
                                message: '不能选择子分类',
                                type: 'warning'
                            });
                        }
                    }
                });
            },
            'form.categoryTitle': function (n) {
                var regu = "[[!@'\"#$%^&*()_+-/~?！@#￥%…&*（）——+—？》《：“‘’]";
                this.rules.categoryPinyin = [{
                    "validator": this.validatorCategoryPinyin, trigger: 'blur'
                }, {
                    "pattern": /^[^[!@#$"'%^&*()_+-/~?！\\、@#￥%…&*（）——+—？》《：“‘’\s]+$/,
                    "message": "拼音格式不匹配"
                }];
                if (this.regularCheck(regu, n)) {
                    this.rules.categoryPinyin.push({
                        "required": true,
                        "message": "请输入栏目拼音名称"
                    });
                }
            },

        },
        computed: {
            // 用于显示链接地址
            typeLinkString : function(){
                // 这里返回的地址必须和空串拼接一下，否则会导致鼠标悬停没有详细地址显示
                if(this.form.categoryType == '1' || this.form.categoryType == '2'){
                    return ''+this.form.typelink;
                }
                if(this.form.categoryType == '3'){
                    return ''+this.form.typeurl;
                }
                return null
            }
        },
        methods: {
            //删除
            del: function () {
                var that = this;

                that.$confirm('此操作将永久删除分类和分类下的文章, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {

                    var formData = JSON.stringify(that.form);
                    formData = JSON.parse(formData);
                    formData.categoryImg = '';
                    formData.categoryIco = '';
                    formData.categoryFlag = '';
                    ms.http.post(ms.manager + "/cms/category/delete.do", [formData], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(function (res) {
                        if (res.result) {

                            //	刷新列表
                            window.parent.location.reload();

                            that.$notify({
                                title: "成功",
                                type: 'success',
                                message: '删除成功!'
                            });

                        } else {
                            that.$notify({
                                title: '失败',
                                message: res.msg,
                                type: 'warning'
                            });
                        }
                    });
                }).catch(function() {

                })
            },
            // 预览
            preview: function (){
                var that = this;
                var template = that.categoryUrls[0].template;
                if (template) {
                    if (that.form.categoryType == 1 ) {
                        window.open(ms.manager + "/cms/category/view.do?style=" + template + "&typeid=" + that.form.id);
                    } else if (that.form.categoryType == 2) {
                        window.open(ms.manager + "/cms/content/view.do?style=" + template + "&typeid=" + that.form.id);
                    }
                }
            },
            //复制栏目
            copyCategory: function() {
                var that = this;
                that.copyLoading = true
                ms.http.get(ms.manager + "/cms/category/copyCategory.do", {
                    id: that.form.id
                }).then(function (res) {
                    if (res.result) {
                        that.copyLoading = false
                        that.$notify({
                            title: '成功',
                            message: '复制成功',
                            type: 'success'
                        });
                        window.parent.location.reload();
                    } else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                });
            },
            //正则校验regu 正则表达式，str被校验的字段，符合返回true否则false
            regularCheck: function (regu, str) {
                var re = new RegExp(regu);
                if (re.test(str)) {
                    return true;
                } else {
                    return false;
                }
            },
            queryCategory: function () {
                var that = this;
                ms.http.get(ms.manager + "/cms/category/list.do").then(function (res) {
                    if (res.result) {
                        //res.data.rows.push({id:0,categoryId: null,categoryTitle:'顶级栏目管理'});
                        that.categoryList = res.data.rows;
                        that.treeList = ms.util.treeData(res.data.rows, 'id', 'categoryId', 'children');

                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //设置栏目模型
            setCategoryModel: function (mdiyCategoryModelId) {
                var that = this;
                if (!mdiyCategoryModelId || String(mdiyCategoryModelId)=="0") {
                    that.form.mdiyCategoryModelId = null;
                    that.ctModel = undefined;
                    document.getElementById("categoryModel").innerHTML = "";
                    return;
                }
                that.form.mdiyCategoryModelId = String(mdiyCategoryModelId);
                that.changeModel();
            },
            //获取栏目内容模型
            queryCategoryModelList: function () {
                var that = this;
                ms.http.get(ms.manager + "/mdiy/model/list.do", {
                    modelType: 'category'
                }).then(function (res) {
                    if (res.result) {
                        that.mdiyCategoryModelListOptions = res.data.rows;
                    }
                });
            },
            // 修改绑定模型
            changeModel: function () {
                var that = this;
                if (that.form && that.form.mdiyCategoryModelId) {
                    that.rederModel(that.form.mdiyCategoryModelId)
                }
            },
            // 读取模型
            rederModel: function (modelId) {
                var that = this;
                ms.mdiy.model.extend("categoryModel", {id: modelId}, {linkId: that.form.id}, true).then(function (obj) {
                    that.ctModel = obj;
                });
            },
            // 保存
            save: function () {
                var that = this;
                var url = ms.manager + "/cms/category/save.do";
                if (that.form.id > 0) {
                    url = ms.manager + "/cms/category/update.do";
                }
                var model = undefined;
                if (that.form.mdiyCategoryModelId && String(that.form.mdiyCategoryModelId)!="0"){
                    model = ms.mdiy.model.modelForm();
                }
                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        //栏目属性为封面则不需要列表模板
                        if (that.form.categoryType == '2') {
                            that.form.categoryListUrl = '';
                        }
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));

                        if (data.id && data.id == data.categoryId) {
                            that.$notify({
                                title: '提示',
                                message: '所属栏目不能为自身',
                                type: 'warning'
                            });
                            that.saveDisabled = false;
                            return;
                        }

                        if (data.categoryId == '0') {
                            data.categoryId = '';
                        }

                        if (data.categoryFlag) {
                            data.categoryFlag = data.categoryFlag.join(',');
                        }

                        //没有图片就直接为空,不能为[]
                        if (data.categoryImg.length>0) {
                            data.categoryImg = JSON.stringify(data.categoryImg);
                        } else {
                            data.categoryImg = '';
                        }
                        //没有图片就直接为空,不能为[]
                        if (data.categoryIco.length>0) {
                            data.categoryIco = JSON.stringify(data.categoryIco);
                        } else {
                            data.categoryIco = '';
                        }


                        data.categoryListUrls= JSON.stringify(that.categoryListUrls);
                        data.categoryUrls= JSON.stringify(that.categoryUrls);
                        //如果是链接去掉栏目的模版配置
                        if(that.form.categoryType == 3) {
                            data.categoryListUrls = "";
                            data.categoryUrls = "";
                        }
                        ms.http.post(url, data).then(function (res) {
                            if (res.result) {
                                //保存时需要赋值关联ID
                                if (model) {
                                    model.form.linkId = res.data.id;
                                    model.save();
                                }
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success',
                                    duration: 500,
                                    onClose: function() {
                                        //保存成功重写获取数据
                                        that.get(res.data.id);
                                        //  刷新iframe父级地址加载
                                        window.parent.indexVue.treeList(res.data.id);
                                        //默认选择新增的栏目
                                    }
                                });
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg,
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
            //获取分类内容模型
            queryContentModel: function () {
                var that = this;
                ms.http.get(ms.manager + "/mdiy/model/list.do", {
                    modelType: 'cms'
                }).then(function (data) {
                    if (data.result) {
                        that.mdiyModelIdOptions = data.data.rows;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取当前分类
            get: function (id) {
                var that = this;
                ms.http.get(ms.manager + "/cms/category/get.do", {
                    "id": id
                }).then(function (res) {
                    if (res.result && res.data) {
                        if (res.data.categoryFlag) {
                            res.data.categoryFlag = res.data.categoryFlag.split(',');
                        }

                        if (res.data.categoryImg) {
                            res.data.categoryImg = JSON.parse(res.data.categoryImg);
                            res.data.categoryImg.forEach(function (value) {
                                value.url = ms.base + value.path;
                            });
                        } else {
                            res.data.categoryImg = [];
                        }

                        if (res.data.categoryIco) {
                            res.data.categoryIco = JSON.parse(res.data.categoryIco);
                            res.data.categoryIco.forEach(function (value) {
                                value.url = ms.base + value.path;
                            });
                        } else {
                            res.data.categoryIco = [];
                        }

                        if (!res.data.categoryId) {
                            res.data.categoryId = '0';
                        }
                        // 模型数据预处理
                        var mdiyModelId = res.data.mdiyModelId;
                        if (mdiyModelId) {
                            mdiyModelId += "";
                            if (mdiyModelId == "0") {
                                mdiyModelId = null;
                            }
                            res.data.mdiyModelId = mdiyModelId;
                        }
                        // 模型数据预处理
                        var mdiyCategoryModelId = res.data.mdiyCategoryModelId;
                        if (mdiyCategoryModelId) {
                            mdiyCategoryModelId += "";
                            if (mdiyCategoryModelId == "0") {
                                mdiyCategoryModelId = null;
                            }
                            res.data.mdiyCategoryModelId = mdiyCategoryModelId;
                        }
                        that.form = res.data; //判断该分类是否存在文章，存在则不能修改栏目属性
                        that.category = JSON.parse(JSON.stringify(res.data));
                        if(that.form.categoryListUrls) {
                            that.categoryListUrls = JSON.parse(that.form.categoryListUrls);
                        }
                        if(that.form.categoryUrls) {
                            that.categoryUrls = JSON.parse(that.form.categoryUrls);
                        }

                        that.$refs.msSelectStyle.queryDictForTemplate();
                        that.contentList(that.form.id);
                        that.changeModel();
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            contentList: function (id) {
                var that = this;
                ms.http.post(ms.manager + "/cms/content/list.do", {
                    categoryId: id
                }).then(function (data) {
                    if (data.result) {
                        if (data.data.total > 0) {
                            that.categoryTypeDisabled = true;
                        } else {
                            that.categoryTypeDisabled = false;
                        }
                    }
                    that.queryMdiyModelDisabled();
                }).catch(function (err) {
                    console.log(err);
                });
            },
            queryMdiyModelDisabled: function () {
                var that = this;
                //没有文章
                if (!that.categoryTypeDisabled){
                    that.mdiyModelDisabled = false;
                }else {
                    if(that.category.mdiyModelId){
                        that.mdiyModelDisabled = true;
                    }else {
                        that.mdiyModelDisabled = false;
                    }
                    var mdiyModelIds = [];
                    that.mdiyModelIdOptions.forEach(function (value) {
                        mdiyModelIds.push(value.id)
                    })
                    // 模型不存在时不禁用
                    if (!mdiyModelIds.includes(that.category.mdiyModelId)) {
                        that.mdiyModelDisabled = false;
                    }
                }
                // 栏目为父栏目并且为列表时禁用
                if (!that.form.leaf && that.form.categoryType == '1') {
                    that.mdiyModelDisabled = true;
                }
            },



            //获取categoryFlag数据源
            queryCategoryFlag: function () {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {
                    dictType: '栏目属性',
                    pageSize: 99999
                }).then(function (res) {
                    if (res.result) {
                        res = res.data;
                        that.categoryFlagOptions = res.rows;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },

            //categoryImg文件上传完成回调
            categoryImgSuccess: function (response, file, fileList) {
                if (response.result) {
                    this.form.categoryImg.push({
                        url: file.url,
                        name: file.name,
                        path: response.data,
                        uid: file.uid
                    });
                } else {
                    this.$notify({
                        title: '失败',
                        message: response.msg,
                        type: 'warning'
                    });
                }

            },
            //上传超过限制
            categoryImghandleExceed: function (files, fileList) {
                this.$notify({
                    title: '失败',
                    message: '当前最多上传1个文件',
                    type: 'warning'
                });
            },
            categoryImghandleRemove: function (file, files) {
                var index = -1;
                index = this.form.categoryImg.findIndex(function (text) {
                    return text == file;
                });

                if (index != -1) {
                    this.form.categoryImg.splice(index, 1);
                }
            },
            //categoryIco文件上传完成回调
            categoryIcoSuccess: function (response, file, fileList) {
                if (response.result) {
                    this.form.categoryIco.push({
                        url: file.url,
                        name: file.name,
                        path: response.data,
                        uid: file.uid
                    });
                } else {
                    this.$notify({
                        title: '失败',
                        message: response.msg,
                        type: 'warning'
                    });
                }

            },
            //上传超过限制
            categoryIcohandleExceed: function (files, fileList) {
                this.$notify({
                    title: '失败',
                    message: '当前最多上传1个文件',
                    type: 'warning'
                });
            },
            categoryIcohandleRemove: function (file, files) {
                var index = -1;
                index = this.form.categoryIco.findIndex(function (text) {
                    return text == file;
                });

                if (index != -1) {
                    this.form.categoryIco.splice(index, 1);
                }
            },
            queryTemplateFileForColumn: function () {
                var that = this;
                templateFiles = [];
                ms.http.get(ms.manager + "/basic/template/queryTemplateFileForColumn.do").then(function (data) {
                    that.templateFiles = data.data;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //复制栏目id
            copyString: function () {
                var clipboard = new ClipboardJS('.copyBtn');
                var self = this;
                clipboard.on('success', function (e) {
                    self.$notify({
                        title: '提示',
                        message: '已成功复制到剪切板',
                        type: 'success'
                    });
                    clipboard.destroy();
                });
            },

        },
        mounted:function () {
            this.$refs.msSelectStyle.queryDictForTemplate();
        },
        created: function () {
            this.queryCategoryModelList();
            this.queryContentModel();
            this.queryCategory();
            this.queryCategoryFlag();

            this.form.id = ms.util.getParameter("id");
            this.form.childId = ms.util.getParameter("childId");// 判断是否增加子栏目

            // 判断三种状态，默认为新增状态
            this.categoryTypeDisabled = false;// 控制栏目分类是否可编辑
            if (this.form.id != undefined && (this.form.childId == undefined || this.form.childId == "undefined")) {
                // 切换编辑状态，id不为空 childId 为空
                this.categoryTypeDisabled = true;
                this.get(this.form.id);
            } else if (this.form.childId) {
                // 切换新增子栏目状态，id&childId 不为空
                this.form.id = null;
                this.form.categoryId = this.form.childId;
            }

        }
    });
</script>
<style>
    .el-select {
        width: 100%;
    }
    .header-info {
        white-space: nowrap;
        display:inline-block;
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>
