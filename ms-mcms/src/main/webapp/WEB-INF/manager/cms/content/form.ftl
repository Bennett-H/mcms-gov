<!-- 需要发布到选项，所以重写 -->
<!DOCTYPE html>
<html>
<head>
    <title>文章编辑</title>
    <#include "../../include/head-file.ftl">
    <#include "/component/ms-cropper.ftl">
    <#include "/component/ms-upload-img.ftl">
    <#include "/component/ms-ai.ftl">
    <script src="${base}/static/mdiy/index.js"></script>
    <script src="${base}/static/datascope/index.js"></script>
    <script src="${base}/static/plugins/tinymce/5.10.2/tinymce.min.js"></script>
    <script src="${base}/static/plugins/tinymce-vue/4.0.6-rc/tinymce-vue.min.js"></script>
</head>
<body>
<div id="form" v-cloak>

    <el-dialog
            :visible.sync="visibleUpload"
            :title="repository?'资源库选择':'上传图片'"
            :close-on-click-modal="false"
            modal-append-to-body
            top="10vh"
            width="760px"
    >
        <ms-upload-img
                v-if="visibleUpload"
                @on-close="closeUpload"
                @on-sync="syncImg"
                ref="uploadImg"
                :img-json="form.contentImg"
                :file-type="repository?'file':'img'">
        </ms-upload-img>
    </el-dialog>


    <el-header class="ms-header ms-tr" height="50px">
        <el-row type="flex" justify="space-between" align="middle">
            <el-col :xs="12" :sm="10" :md="10" :lg="10" :xl="10" style="display:flex;align-items:center;">

                <el-tooltip class="item" effect="dark" :content="form.id" placement="top-start">
                    <span v-if="form.id && categoryType=='2'" style="float: left; max-width:calc(30% - 40px);" class="header-info">编号：{{form.id}}</span>
                </el-tooltip>
                <el-button v-if="form.id && categoryType=='2'" type="text" style="float: left" icon="el-icon-document-copy" circle :data-clipboard-text="form.id" @click="copyString()" class="copyBtn"></el-button>
                <el-tag size="small"
                        v-if="categoryType=='2' && form.id"
                        type="primary"  size="small">
                    {{form.progressStatus}}
                </el-tag>
            </el-col>
            <el-col :xs="12" :sm="14" :md="14" :lg="14" :xl="16" class="ms-tr">
                <@shiro.hasPermission name="approval:config:approval">
                    <ms-button-approval
                            :ms-approval-loading="approvalDisabled"
                            @on-approval="toApproval"
                            @get-data='getComponentData'
                            :params="{categoryId: form.categoryId,dataId: form.id,schemeName: '文章审核'}"
                            action="/approval/config/getProgressLog"
                    ></ms-button-approval>
                </@shiro.hasPermission>
                <#--  文章更新保存  -->
                <#--  有更新权限，文章id，满足终审通过或者不通过，为单篇，在审批状态  -->
                <#--
                保存判断：审批状态是否为  xxx通过待xx审批  ，满足则以审批按钮权限进行展示，否则以修改权限+文章id或者文章类型进行展示
                    1.审批按钮权限，是否在文章审批状态
                    2.有修改权限，且有文章id（非新增）或者文章类型为非列表（！='1'）
                  -->

                <ms-ai ref="msAi"  :content="form.contentDetails"></ms-ai>
                <el-button
                        v-if="approvalView || (hasModelPermission('cms:content:update') && (form.id || categoryType!='1'))"
                        type="primary" icon="iconfont icon-baocun" size="mini"
                        @click="save"
                        :loading="saveDisabled">保存
                </el-button>
                <#--  文章新增  -->
                <el-button
                        v-if="hasModelPermission('cms:content:save') && !form.id && categoryType=='1'"
                        type="primary" icon="iconfont icon-baocun" size="mini"
                        @click="save"
                        :loading="saveDisabled">保存
                </el-button>

                <#--保存并预览-->
                <el-button
                        v-if="((hasModelPermission('cms:content:save') || hasModelPermission('cms:content:update')) && approvalView) && categoryType=='1'"
                        type="primary" icon="el-icon-s-platform" size="mini"
                        @click="save(true)"
                        :loading="saveDisabled">保存并预览
                </el-button>

                <el-button
                        v-if="hasModelPermission('cms:content:log') && categoryType=='2' && form.id && form.progressStatus!='草稿'"
                        type="primary" icon="iconfont icon-baocun" size="mini"
                        @click="log()"
                        :loading="saveDisabled">审批日志
                </el-button>

                <el-button
                        v-if="form.id && categoryType=='2' && form.progressStatus=='草稿'"
                        type="primary" icon="iconfont icon-baocun" size="mini"
                        @click="submit()"
                        :loading="saveDisabled">提交审核
                </el-button>

                <el-button
                        type="primary" icon="el-icon-s-platform" size="mini"
                        v-if="form.id"
                        @click="view()"
                        :loading="saveDisabled">预览
                </el-button>
                <el-button
                        v-if="form.id && categoryType=='2'"
                        type="danger" icon="el-icon-delete" size="mini"
                        @click="del()">删除
                </el-button>
                <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回
                </el-button>
            </el-col>
        </el-row>
    </el-header>
    <el-main class="ms-container" style="position:relative;">
        <el-scrollbar class="ms-scrollbar" style="height: 100%;">
            <el-tabs v-model="activeName" style="height: calc(100% - 10px);">
                <el-tab-pane style="position:relative;" v-for="(item, index) in editableTabs" :key="index"
                             :label="item.title" :name="item.name">
                    <el-form v-if="item.title=='文章编辑'" ref="form" :model="form" :rules="rules" label-width="120px"
                             size="mini">
                        <el-row gutter="0" justify="start" align="top">
                            <el-col :span="12">
                                <el-form-item label="文章标题" prop="contentTitle">
                                    <el-input v-model="form.contentTitle"
                                              :disabled="false"
                                              :style="{width:  '100%'}"
                                              :clearable="true"
                                              placeholder="请输入文章标题">
                                    </el-input>
                                    <div class="ms-form-tip">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                              target="_blank">${'$'}{field.title}</a>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col span="12">
                                <el-form-item label="所属栏目" prop="categoryId">
                                    <treeselect v-model="form.categoryId"
                                                :disabled="!returnIsShow"
                                                :disable-branch-nodes="true"
                                                :normalizer="function(node){
                                                return {
                                                    id: node.id,
                                                    label: node.categoryTitle,
                                                    children: node.children
                                                }}"
                                                @select="categoryChange"
                                                :options="contentCategoryIdOptions" placeholder="请选择"></treeselect>
                                    <div class="ms-form-tip">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                              target="_blank">${'$'}{field.typetitle}</a><br/>
                                        提示：不能为链接和封面类型新建文章
                                    </div>
                                </el-form-item>

                            </el-col>
                        </el-row>
                        <el-row
                                gutter="0"
                                justify="start" align="top">
                            <el-col :span="12">
                                <el-form-item label="文章副标题" prop="contentShortTitle">
                                    <el-input v-model="form.contentShortTitle"
                                              :disabled="false"
                                              :style="{width:  '100%'}"
                                              :clearable="true"
                                              placeholder="请输入文章副标题">
                                    </el-input>
                                    <div class="ms-form-tip">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html" target="_blank">${'$'}{field.shorttitle}</a>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col span="12">


                                <el-form-item label="文章外链接" prop="contentOutLink">
                                    <el-input v-model="form.contentOutLink"
                                              :disabled="false"
                                              :style="{width:  '100%'}"
                                              :clearable="true"
                                              placeholder="请输入文章外链接">
                                    </el-input>
                                    <div class="ms-form-tip">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                              target="_blank">${'$'}{field.outlink}</a> 文章外链接必须以http或者https等开头
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row
                                gutter="0"
                                justify="start" align="top">
                            <el-col span="12">
                                <el-form-item label="文章样式" prop="contentTitleCss">
                                    <div style=" display: flex">
                                        <el-color-picker v-model="contentTitleCss.color" style="margin-right: 10px"></el-color-picker>
                                        <el-input v-model="contentTitleCss.fontSize" placeholder="字体大小PX" style="width:100px;margin-right: 10px">
                                        </el-input>
                                        <el-checkbox v-model="contentTitleCss.fontStyle" style="margin-right: 10px">斜体</el-checkbox>
                                        <el-checkbox v-model="contentTitleCss.fontWeight">加粗</el-checkbox>
                                    </div>
                                    <div class="ms-form-tip">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                              target="_blank">${'$'}{field.css}</a>，字体大小必须有单位作为后缀，例如：10px,此处配置了标题样式需要在模板中使用标签获取才能生效
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col span="12">
                                <el-form-item label="附属栏目" prop="categoryIds">
                                    <treeselect v-model="form.categoryIds"
                                                :disable-branch-nodes="true"
                                                :multiple="true"
                                                :normalizer="function(node){
                                                return {
                                                    id: node.id,
                                                    label: node.categoryTitle,
                                                    children: node.children
                                                }}"
                                                :limit="3"
                                                :options="contentCategoryIdOptions" placeholder="请选择"></treeselect>
                                    <div class="ms-form-tip">
                                        静态化时，在前台页面所属栏目列表里也会出现该文章,最多选3个栏目且只能选择列表类型栏目
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row
                                gutter="0"
                                justify="start" align="top">
                            <el-col span="12">
                                <el-form-item label="文章类型" prop="contentType">
                                    <el-select v-model="form.contentType"
                                               :style="{width: '100%'}"
                                               :filterable="false"
                                               :disabled="false"
                                               :multiple="true" :clearable="true"
                                               placeholder="请选择文章类型">
                                        <el-option v-for='item in contentTypeOptions' :key="item.dictValue"
                                                   :value="item.dictValue"
                                                   :label="item.dictLabel"></el-option>
                                    </el-select>
                                    <div class="ms-form-tip">
                                        提示：用于筛选文章，在自定义字典添加
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col span="12">



                                <el-form-item label="文章来源" prop="contentSource">
                                    <el-input v-model="form.contentSource"
                                              :disabled="false"
                                              :style="{width:  '100%'}"
                                              :clearable="true"
                                              placeholder="请输入文章来源">
                                    </el-input>
                                    <div class="ms-form-tip">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                              target="_blank">${'$'}{field.source}</a>
                                    </div>
                                </el-form-item>

                            </el-col>
                        </el-row>
                        <el-row
                                gutter="0"
                                justify="start" align="top">
                            <el-col span="12">
                                <el-form-item label="文章作者" prop="contentAuthor">
                                    <el-input v-model="form.contentAuthor"
                                              :disabled="false"
                                              :style="{width:  '100%'}"
                                              :clearable="true"
                                              placeholder="请输入文章作者">
                                    </el-input>
                                    <div class="ms-form-tip">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                              target="_blank">${'$'}{field.author}</a>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col span="12">

                                <el-form-item label="发布时间" prop="contentDatetime">
                                    <el-date-picker
                                            v-model="form.contentDatetime"
                                            placeholder="请选择发布时间"
                                            start-placeholder=""
                                            end-placeholder=""
                                            :readonly="false"
                                            :disabled="false"
                                            :editable="true"
                                            :clearable="true"
                                            format="yyyy-MM-dd HH:mm:ss"
                                            value-format="yyyy-MM-dd HH:mm:ss"
                                            :style="{width:'100%'}"
                                            type="datetime">
                                    </el-date-picker>
                                    <div class="ms-form-tip">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                              target="_blank">${'$'}{field.date?string("yyyy-MM-dd")}</a>，未到发布时间的文章将不会被显示到前台，结合定时调度可以达到预发布效果。
                                            参考文档<a href="http://doc.mingsoft.net/mcms/chang-jian-wen-ti/qi-ye-ban-3001-zheng-wu-ban.html#%E5%AE%9A%E6%97%B6%E5%8F%91%E5%B8%83" target="_blank">定时发布</a>
                                            <br>注意：是否到发布时间是根据日期判断，不会受时分秒影响，只要发布时间的日期为今天的日期，则允许发布
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row
                                gutter="0"
                                justify="start" align="top">
                            <el-col span="12">
                                <el-form-item label="文章发布到" prop="contentStyle">
                                    <el-select v-model="form.contentStyle" :style="{width: '100%'}"
                                               :filterable="false" :disabled="false" :multiple="true" :clearable="true"
                                               placeholder="请选择需要发布到的模板">
                                        <el-option v-for='item in hasContentStyleOptions' :key="item.dictLabel"
                                                   :value="item.style" :label="item.dictLabel"></el-option>
                                    </el-select>
                                    <div class="ms-form-tip">
                                        提示：需要栏目绑定对应的模板，发布到才有数据。<br/>
                                        选择后才会在对应的皮肤静态化.例如:内网数据只在内网显示、外网只在外网显示。<br/>
                                        数据来源: 自定义字典 - 模板类型
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col span="12" >

                                <el-form-item label="自定义顺序" prop="contentSort">
                                    <el-input-number
                                            v-model="form.contentSort"
                                            :disabled="!returnIsShow"
                                            controls-position="">
                                    </el-input-number>
                                    <div class="ms-form-tip">
                                        提示：前台模板标签需要设置orderby属性为sort才能生效
                                    </div>
                                </el-form-item>
                            </el-col>

                        </el-row>

                        <el-row
                                gutter="0"
                                justify="start" align="top">
                            <el-col span="12">
                                <el-form-item label="是否显示" prop="contentDisplay">
                                    <el-radio-group v-model="form.contentDisplay"
                                                    :style="{width: ''}"
                                                    :disabled="false">
                                        <el-radio :style="{display: true ? 'inline-block' : 'block'}"
                                                  :label="item.value"
                                                  v-for='(item, index) in contentDisplayOptions'
                                                  :key="item.value + index">
                                            {{true? item.label : item.value}}
                                        </el-radio>
                                    </el-radio-group>
                                    <div class="ms-form-tip">
                                        提示：选择否后前台将不显示该篇文章，可以用于紧急撤回发布文章
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col span="12">
                                <el-form-item label="文章标签" prop="contentTags">
                                    <el-select v-model="form.contentTags"
                                               :style="{width: ''}"
                                               :filterable="false"
                                               :disabled="false"
                                               filterable
                                               :multiple="true" :clearable="true"
                                               placeholder="请选择文章标签">
                                        <el-option v-for='item in contentTagsOptions' :key="item.dictValue"
                                                   :value="item.dictValue"
                                                   :label="item.dictLabel"></el-option>
                                    </el-select>
                                    <div class="ms-form-tip">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html" target="_blank">${'$'}{field.tags}</a>
                                        通过自定义字典可扩展数据
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-form-item label="文章缩略图" prop="contentImg">
                            <!-- 拦截点击事件打开裁切素材库对话框，这里的elupload只用来回显图片 -->
                            <el-upload
                                    ref="contentImgUpload"
                                    @click.native="uploadClick"
                                    :on-remove="contentImghandleRemove"
                                    :file-list="form.contentImg"
                                    :auto-upload="false"
                                    :limit="1"
                                    accept="image/*"
                                    list-type="picture-card"
                                    style="display:inline;">
                                <i class="el-icon-plus"></i>
                                <div slot="tip" class="ms-form-tip">
                                    标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                          target="_blank">${'$'}{field.litpic}</a><br/>
                                    提示：最多上传10张图片，文章缩略图,支持jpg格式；多图情况下，{@ms:file field.litpic/}会只取第一张缩略图，其他用法参考文档arclist标签
                                </div>
                            </el-upload>
                        </el-form-item>
                        <el-form-item label="关键字" prop="contentKeyword">
                            <el-input
                                    type="textarea" :rows="5"
                                    :disabled="false"
                                    v-model="form.contentKeyword"
                                    :style="{width: '100%'}"
                                    placeholder="请输入文章关键字"
                                    maxlength="100"
                                    show-word-limit>
                            </el-input>
                            <div class="ms-form-tip">
                                标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                      target="_blank">${'$'}{field.keyword}</a>
                            </div>
                        </el-form-item>
                        <el-form-item label="描述" prop="contentDescription">
                            <el-input
                                    type="textarea" :rows="5"
                                    :disabled="false"
                                    v-model="form.contentDescription"
                                    :style="{width: '100%'}"
                                    maxlength="255"
                                    show-word-limit
                                    placeholder="请输入对该文章的简短描述，以便用户查看文章简略">
                            </el-input>
                            <div class="ms-form-tip">
                                <el-row>
                                    <el-col span="12">
                                        标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                              target="_blank">${'$'}{field.descrip}</a>
                                    </el-col>
                                    <el-col span="12" align="right">
                                        <a style="cursor: pointer;color: #00abea" @click="OutContentDescription">自动获取描述</a>
                                    </el-col>
                                </el-row>
                            </div>
                        </el-form-item>
                        <el-form-item label="文章内容" prop="contentDetails">
                            <editor v-model="form.contentDetails" ref="editor" :init="tinyInit"></editor>
                            <div class="ms-form-tip">
                                标签：<a href="http://doc.mingsoft.net/mcms/biao-qian/wen-zhang-lie-biao-ms-arclist.html"
                                      target="_blank">${'$'}{field.content}</a>
                            </div>
                        </el-form-item>
                    </el-form>
                    <div :id="'model'+index" v-else></div>
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
            var checkTags = function (rule, value, callback){
                if (value.length > 5){
                    return callback(new Error('文章标签最多选择5个'));
                }
                callback();
            }
            return {
                oneKeyFormatConfig: {},
                //  tiny配置
                tinyInit: {},
                // tiny 资源库状态
                repository: false,
                //  资源库模态框开启状态
                visibleUpload: false,
                //  图片裁切配置

                approvalDisabled: false,
                //	当前是否在审批流中
                approvalView: false,
                saveDisabled: false,
                activeName: 'form',
                datascopesModel: [false], //默认所有都没有权限
                datascopes: [false], //默认所有都没有权限
                //自定义模型实例
                model: undefined,
                editableTabs: [{
                    title: '文章编辑',
                    name: 'form'
                }],
                editorConfig: ms.editorConfig,
                contentCategoryIdOptions: [],
                returnIsShow: true,
                type: '',
                //表单数据
                form: {
                    // 文章标题
                    contentTitle: '',
                    // 文章标题
                    contentShortTitle: '',
                    // 所属栏目
                    categoryId: undefined,
                    // 文章类型
                    contentType: [],
                    // 是否显示
                    contentDisplay: '0',
                    // 文章作者
                    contentAuthor: '',
                    // 文章来源
                    contentSource: '',
                    // 自定义顺序
                    contentSort: 0,
                    // 文章缩略图
                    contentImg: [],
                    // 描述
                    contentDescription: '',
                    // 关键字
                    contentKeyword: '',
                    // 文章内容
                    contentDetails: '',
                    // 文章模型
                    contentStyle: [],
                    //文章存在多个栏目中
                    categoryIds: [],
                    //文章标题样式
                    contentTitleCss: '',
                    //文章外链接
                    contentOutLink: '',

                    contentDatetime: ms.util.date.fmt(Date.now(), "yyyy-MM-dd hh:mm:ss"),
                },
                contentTagsOptions: [],// 标签
                contentTitleCss: {
                    color:'',
                    fontSize:'',
                    fontWeight:false,
                    fontStyle:false
                },
                departmentStatus: '',	//	自定义模型部门状态
                categoryType: '1',
                contentTypeOptions: [],
                dictList: [],//模板类型字典数据
                //文章模型选项
                contentStyleOptions: [],
                hasContentStyleOptions: [],
                categoryIdOptions: [],
                contentDisplayOptions: [{
                    "value": "0",
                    "label": "是"
                }, {
                    "value": "1",
                    "label": "否"
                }],
                rules: {
                    // 文章标题
                    contentTitle: [{
                        "required": true,
                        "message": "请输入文章标题"
                    }],
                    // 发布时间
                    contentDatetime: [{
                        "required": true,
                        "message": "发布时间不能为空"
                    }],
                    categoryId: [{
                        "required": true,
                        "message": "所属栏目不能为空"
                    }],
                    // 文章外链接
                    contentOutLink: [{
                        "pattern":/(((http|ftp|https):\/\/)?)([a-zA-Z0-9.-])\/[a-zA-Z0-9&%.\/-~-]*/,
                        "message":"文章外链接格式不匹配"},{"min":0,"max":200,"message":"文章外链接长度必须为0-200"}],
                    // 文章标签
                    contentTags: [{
                        validator: checkTags, trigger: 'blur'
                    }]
                }
            };
        },
        components: {
            'editor': Editor,
        },
        watch: {
            //	监听栏目id变化，主要应用于左侧'全部'进行新增的时候，没有栏目id还未知的情况
            'form.categoryId': function (n) {
                var that = this
                //	初始化表单数据
                if (n) {
                    //	调用页面初始化的方法和接口
                    that.getContentStyle(that.form.categoryId)
                    that.queryDatascope();
                }
            },
            datascopes: function (n) {
                //编辑内容时，判断部门是否有权限
                var has = !((this.datascopes == true || this.datascopes[0] == true || this.datascopes.indexOf('cms:content:department') > -1) || this.categoryType != "1");
                //  当自定义业务模型没有加载完成时，跳过赋值
                this.departmentStatus = has
            },
            //	子组件（model组件）模型数据变化时
            model: function () {
                //	自定义模型渲染上页面之后，传入当前最大审批进度字段,是否单篇
                this.model.maxProgress = this.maxProgress
                this.model.categoryType = this.categoryType
                this.model.form.disabled = this.departmentStatus
            },
            visibleUpload: function (n) {
                // 把tiny资源库置为关闭状态
                if (!n) {
                    this.repository = false
                }
            }
        },
        computed: {
            currCategory: function () {
                var that = this;
                return this.categoryIdOptions.find(function (value) {
                    return value.id === that.form.categoryId;
                });
            }
        },
        methods: {
            // 查看日志
            log: function () {
                location.href = ms.manager + "/progress/progressLog/index.do?dataId=" + this.form.id;
            },
            //重新提交
            submit: function () {
                var that = this;
                ms.http.post(ms.manager + "/cms/content/submit.do", {
                    "schemeName": "文章审核",
                    "dataId": that.form.id
                }).then(function (res) {
                    if (res.result && res.data) {
                        that.$notify({
                            title: '成功',
                            type: 'success',
                            message: '提交成功',
                            onClose: function () {
                                location.reload();
                            }
                        });
                    } else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning'
                        });
                    }
                    that.list();
                }).catch(function (err) {
                    console.log(err);
                });
            },
            uploadClick: function (e) {
                this.visibleUpload = true
                e.stopPropagation()
                e.preventDefault()
            },
            //	同步组件上传的图片
            syncImg: function (imgObj) {
                var that = this
                // 当tiny资源库为开启状态时
                if (that.$refs.editor && that.repository) {
                    // 编辑器实例对象
                    var editor = null
                    // 文件对象
                    var file = imgObj[0]
                    // 插入内容
                    var content = null
                    if (that.$refs.editor.length) {
                        editor = that.$refs.editor[0].editor
                    } else {
                        editor = that.$refs.editor.editor
                    }
                    // 判断图片正则
                    var imgReg = /(bmp|jpg|gif|jpeg|png)/i
                    // 判断视频正则
                    var videoReg = /(mp4|rmvb|flv|mpeg|avi)/i
                    if (imgReg.test(file.type)) {
                        // 编辑器插入图片
                        content = '<img style="display: block; margin-left: auto; margin-right: auto;" src="' + file.url + '" />'
                    } else if (videoReg.test(file.type)) {
                        content = '<video style="display: block; margin-left: auto; margin-right: auto;"  controls="controls"><source src="' + file.url + '" type="video/' + file.type + '" /></video>'
                    } else {
                        content = '<a href="' + file.url + '">' + file.name + '</a>'
                    }
                    // 编辑器插入图片
                    editor.insertContent(content)
                } else {
                    if (!that.form.contentImg){
                        that.form.contentImg = [];
                    }
                    if (that.form.contentImg.length >= that.$refs.contentImgUpload[0].limit){
                        that.$notify({
                            title: '失败',
                            message: '当前最多上传'+that.$refs.contentImgUpload[0].limit+'个文件',
                            type: 'warning'
                        });
                        return;
                    }
                    this.form.contentImg.push({
                        url: imgObj[0].url,
                        name: imgObj[0].name,
                        path: imgObj[0].path,
                        uid: imgObj[0].uid,
                        status: 'success'
                    });
                    console.log(that.form.contentImg)
                }
            },
            //	关闭上传资源库
            closeUpload: function () {
                var that = this
                //  未成功进行上传操作，移除当前图片上传流程
                //that.contentImghandleRemove(that.form.contentImg[that.form.contentImg.length-1],that.form.contentImg)
                that.visibleUpload = false
            },
            //	获取子组件实时数据的方法,data为传入的数据
            getComponentData: function (data) {
                this.approvalView = data
            },
            //	审批按钮点击事件
            //	调整审批页相关参数
            toApproval: function () {
                var that = this
                that.approvalDisabled = true; //判断
                //	对象参数
                var params = {
                    categoryId: that.form.categoryId,
                    dataId: that.form.id,
                    schemeName: '文章审核'
                }
                //	实际url地址参数
                var url_params = ''
                Object.keys(params).forEach(function (key) {
                    url_params = url_params + key + '=' + params[key] + '&'
                })
                //	移除url地址字符串最后携带的一个&字符
                url_params = url_params.substring(0, url_params.length - 1)
                that.approvalDisabled = false; //判断
                location.href = ms.manager + "/approval/config/approvalForm.do?" + url_params;
            },
            // 保存
            save: function (isView) {
                var _this = this;
                var that = this; //自定义模型需要验证
                var model = undefined;
                if (that.currCategory.mdiyModelId && String(that.currCategory.mdiyModelId)!="0"){
                    try {
                        model = ms.mdiy.model.modelForm();
                    } catch (e) {
                        console.log(e)
                    }
                }
                if (model && !model.validate()) {
                    this.activeName = 'custom-name';
                    return;
                }

                var url = ms.manager + "/cms/content/save.do";

                if (that.form.id > 0) {
                    url = ms.manager + "/cms/content/update.do";
                }

                this.$refs.form[0].validate(function (valid) {
                    if (valid ) {
                        that.saveDisabled = true; //判断

                        var data = JSON.parse(JSON.stringify(that.form));
                        // 固定属性顺序为字典顺序
                        if (data.contentType) {
                            var orderTypes = [];
                            that.contentTypeOptions.forEach(function (dict) {
                                var orderType = data.contentType.find(function (type) {
                                    return type==dict.dictValue
                                })
                                if (orderType){
                                    orderTypes.push(orderType)
                                }
                            })
                            data.contentType = orderTypes;
                        }

                        if (data.contentType) {
                            data.contentType = data.contentType.join(',');
                        }
                        if (data.categoryIds) {
                            data.categoryIds = data.categoryIds.join(',');
                        }
                        if (data.contentStyle) {
                            data.contentStyle = data.contentStyle.join(',');
                        }
                        if (data.contentTags) {
                            data.contentTags = data.contentTags.join(',');
                        }

                        data.contentTitleCss = "";
                        if (that.contentTitleCss) {
                            data.contentTitleCss +="color:"+that.contentTitleCss.color+";";
                            if(that.contentTitleCss.fontSize) {
                                data.contentTitleCss +="font-size:"+that.contentTitleCss.fontSize+";";
                            }
                            if(that.contentTitleCss.fontStyle) {
                                data.contentTitleCss += "font-style:initial;";
                            }
                            if(that.contentTitleCss.fontWeight) {
                                data.contentTitleCss += "font-weight:bold;";
                            }
                        }


                        data.contentImg = JSON.stringify(data.contentImg);
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                //保存时需要赋值关联ID
                                if (model) {
                                    model.form.linkId = data.data.id;
                                    //设置type方便aop进行拦截
                                    model.form.type = "cms";
                                    model.save();
                                }
                                if (isView == true) {
                                    var style = data.data.contentStyle.split(",")[0];
                                    if (style) {
                                        window.open(ms.manager + "/cms/content/view.do?style=" + style + "&id=" + data.data.id);
                                    } else {
                                        window.open(ms.manager + "/cms/content/view.do?style=" + that.contentStyleOptions[0].dictValue + "&id=" + data.data.id);

                                    }
                                }
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success',
                                    duration: 1000,
                                    onClose: function () {
                                        if (that.returnIsShow) {
                                            javascript: history.go(-1);
                                        } else {
                                            //如果是顶级封面或封面，则重新加载,避免文章和自定义模型重复保存
                                            location.reload();
                                        }
                                        that.saveDisabled = false;
                                    }
                                });

                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: data.msg,
                                    type: 'warning'
                                });
                                that.saveDisabled = false;
                            }

                        }).catch(function(res){
                            // 此处是针对开启了手动替换敏感词，且被检测出包含敏感词从而抛出异常所做的catch处理
                            // 检测出的敏感词集合
                            var data = res.response.data.data;
                            // 判断size，避免其他异常影响
                            if (data && data.length>0){
                                that.$refs.msAi.sensitiveSave(data)
                            }
                            //关闭加载动画
                            that.saveDisabled = false;
                        });
                    } else {
                        _this.activeName = 'form';
                        return false;
                    }
                });
            },
            //删除
            del: function () {
                var that = this;
                that.$confirm('此操作将永久删除所选内容, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    var formData = that.form;
                    formData.contentImg = "";
                    formData.contentType = "";
                    if (formData.contentStyle && formData.contentStyle.length>0){
                        formData.contentStyle = formData.contentStyle.join(",")
                    } else {
                        formData.contentStyle = "";
                    }
                    formData.categoryIds = "";
                    ms.http.post(ms.manager + "/cms/content/delete.do", [formData], {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(function (res) {
                        if (res.result) {
                            that.$notify({
                                title: '成功',
                                type: 'success',
                                message: '删除成功!'
                            }); //删除成功，刷新页面
                            window.parent.location.reload();
                        } else {
                            that.$notify({
                                title: '失败',
                                message: res.msg,
                                type: 'warning'
                            });
                        }
                    });
                })
            },
            // 自动截取
            OutContentDescription: function (){
                var that = this;
                // 编辑器纯文本
                var getText = that.$refs.editor[0].editor.getContent({'format':'text'})
                if (getText!=''){
                    // 过滤换行
                    getText = getText.replace(/[\r\n]/g,"")
                    that.form.contentDescription = getText.substring( 0,120 )
                    this.$message({
                        message: '获取成功',
                        type: 'success'
                    });
                } else {
                    this.$message('正文不能为空!');
                }
            },
            //判断是否有权限
            hasModelPermission: function (permission) {
                //后台接口因为参数问题直接会返回false
                if (this.datascopesModel == false) {
                    return false;
                }
                var has = this.datascopesModel == true || this.datascopesModel.indexOf(permission) > -1;
                return has;
            },
            //判断是否有权限
            hasPermission: function (permission) {
                //后台接口因为参数问题直接会返回false
                if (this.datascopes == false) {
                    return false;
                }
                var has = this.datascopes == true || this.datascopes.indexOf(permission) > -1;
                return has;
            },
            //查询权限配置
            queryDatascope: function () {
                var that = this;
                //	请求到菜单相关表后，在发出业务配置权限表请求
                ms.datascope({
                    dataType: "管理员栏目权限",
                    dataId: that.form.categoryId,
                    isSuper: true,
                }).then(function (datascopesModel) {
                    that.datascopesModel = datascopesModel;
                });

                //	请求到菜单相关表后，在发出业务配置权限表请求，合并双重数据用于业务处理
                //ms.datascope({
                //	dataType: "approvalConfigOfAll",
                //	dataId: that.maxProgress,   //  审批最大级数
                //	dataTargetId: 'All',
                //	isSuper: true,
                //}).then((datascopes)=>{
                //	that.datascopes = datascopes;
                //});

            },
            removeModel: function () {
                var that = this;
                var model = document.getElementById('model1');
                var custom = document.getElementById('c_model');

                if (custom) {
                    model.removeChild(custom);
                }

                that.model = undefined;
            },
            categoryChange: function (selectRow) {
                this.form.categoryId = selectRow.id
                this.changeModel();
            },
            changeModel: function () {
                var that = this;
                that.editableTabs = [that.editableTabs[0]];

                if (this.currCategory) {
                    if (this.currCategory.mdiyModelId) {
                        that.rederModel(this.currCategory.mdiyModelId)
                    }
                }
            },
            rederModel: function (modelId) {
                var that = this;
                that.editableTabs.push({
                    title: '加载中.....',
                    name: 'custom-name'
                });
                ms.mdiy.model.extend("model1", {id: modelId}, {linkId: that.form.id}, true).then(function (obj) {
                    that.model = obj;
                    that.editableTabs[1].title = obj.modelName
                });

            },
            getValue: function (data) {
                this.form.categoryId = data.id;
            },
            //获取当前文章
            get: function (id) {
                var that = this;
                ms.http.get(ms.manager + "/cms/content/get.do", {
                    "id": id
                }).then(function (res) {
                    if (res.result && res.data) {
                        if (res.data.contentType && res.data.contentType != '') {
                            res.data.contentType = res.data.contentType.split(',');
                        } else {
                            res.data.contentType = [];
                        }

                        if (res.data.contentStyle && res.data.contentStyle != '') {
                            res.data.contentStyle = res.data.contentStyle.split(',');
                        } else {
                            res.data.contentStyle = [];
                        }

                        if (res.data.contentTags && res.data.contentTags != '') {
                            res.data.contentTags = res.data.contentTags.split(',');
                        } else {
                            res.data.contentTags = [];
                        }

                        if (res.data.categoryIds && res.data.categoryIds != '') {
                            res.data.categoryIds = res.data.categoryIds.split(',');
                        } else {
                            res.data.categoryIds = [];
                        }

                        if (res.data.contentImg) {
                            res.data.contentImg = JSON.parse(res.data.contentImg);
                            res.data.contentImg.forEach(function (value) {
                                value.url = ms.base + value.path;
                            });
                        } else {
                            res.data.contentImg = [];
                        }


                        //还原样式
                        if (res.data.contentTitleCss) {
                            var styles = res.data.contentTitleCss.split(";");
                            for (i = 0; i < styles.length; i++) {
                                var style = styles[i];
                                if(style) {
                                    var map = style.split(":");
                                    if(map[1]) {
                                        if(map[0]=="color") {
                                            that.contentTitleCss.color = map[1];
                                        } else if(map[0]=="font-size") {
                                            that.contentTitleCss.fontSize = map[1];
                                        } else if(map[0]=="font-weight") {
                                            that.contentTitleCss.fontWeight = true;
                                        } else if(map[0]=="font-style") {
                                            that.contentTitleCss.fontStyle = true;
                                        }
                                    }

                                }
                            }
                        }



                        that.form = res.data;
                        var category = that.categoryIdOptions.filter(function (f) {
                            return f['id'] == that.form.categoryId;
                        });

                        if (category.length == 1) {
                            if (category[0].categoryType == '2' || category[0].categoryType == '3') {
                                that.returnIsShow = false;
                            }
                        }
                        that.changeModel();
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //根据封面获取当前文章
            getFromFengMian: function (categoryId) {
                var that = this;
                ms.http.get(ms.manager + "/cms/content/getFromFengMian.do", {
                    "categoryId": categoryId
                }).then(function (res) {
                    if (res.result) {
                        if (res.data != null) {
                            if (res.data.contentType && res.data.contentType != '') {
                                res.data.contentType = res.data.contentType.split(',');
                            } else {
                                res.data.contentType = [];
                            }

                            if (res.data.contentImg) {
                                res.data.contentImg = JSON.parse(res.data.contentImg);
                                res.data.contentImg.forEach(function (value) {
                                    value.url = ms.base + value.path;
                                });
                            } else {
                                res.data.contentImg = [];
                            }
                            if (res.data.contentStyle) {
                                res.data.contentStyle = res.data.contentStyle.split(',')
                            } else {
                                res.data.contentStyle = [];
                            }
                            if (res.data.contentTags && res.data.contentTags != '') {
                                res.data.contentTags = res.data.contentTags.split(',');
                            } else {
                                res.data.contentTags = [];
                            }
                            //修复了内容附属栏目回显问题
                            if (res.data.categoryIds && res.data.categoryIds != '') {
                                res.data.categoryIds = res.data.categoryIds.split(',');
                            } else {
                                res.data.categoryIds = [];
                            }
                            that.form = res.data;
                            var category = that.categoryIdOptions.filter(function (f) {
                                return f['id'] == that.form.categoryId;
                            });

                            if (category.length == 1) {
                                if (category[0].categoryType == '2') {
                                    that.returnIsShow = false;
                                }
                            }
                        }
                        that.changeModel();
                    } else {
                        that.$notify({
                            title: '失败',
                            message: "获取错误",
                            type: 'warning'
                        });
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取contentCategoryId数据源
            contentCategoryIdOptionsGet: function () {
                var that = this;
                ms.http.get(ms.manager + "/cms/category/list.do").then(function (res) {
                    if (res.result) {
                        res.data.rows.forEach(function (item) {
                            if ((item.categoryType == '2' || item.categoryType == '3') && item.leaf) {
                                item.isDisabled = true;
                            }
                        });
                        that.contentCategoryIdOptions = ms.util.treeData(res.data.rows, 'id', 'categoryId', 'children');
                        that.categoryIdOptions = res.data.rows;
                        that.getContentStyle(that.form.categoryId);
                        //获取到栏目数据之后再进行初始化
                        that.init();
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取当前栏目详情绑定模板信息,id为当前栏目id
            getContentStyle: function (id) {
                var that = this;
                that.categoryIdOptions.forEach(function (item) {
                    //  栏目列表与当前文章栏目id相同
                    if (item.id == id) {
                        //  保存文章类型，当前页面调用
                        that.categoryType = item.categoryType
                        if (typeof item.categoryUrls == 'string') {
                            item.categoryUrls = JSON.parse(item.categoryUrls)
                        }
                        //	定义有设置的模板数据对象
                        //	templateInfo为null相关情况下，设置默认数组
                        if (!item.categoryUrls.length) {
                            item.categoryUrls = []
                        }
                        that.hasContentStyleOptions = []
                        //	循环是否有配置对应信息模板
                        item.categoryUrls.forEach(function (ite) {
                            //	栏目详情配置部门，是否有配置对应文件模板路径
                            if (ite.file !== '') {
                                that.hasContentStyleOptions.push(ite)
                            }
                        })
                        //  循环默认接口返回的发布到下拉列表
                        that.hasContentStyleOptions.forEach(function (ite) {
                            //  将默认发布到的下拉字典值修改
                            // ite.dictValue = ite.template
                            ite.dictLabel = ite.name
                            that.dictList.forEach(function (dict) {
                                //将模板名替换为字典值
                                if (ite.dictLabel == dict.dictLabel) {
                                    ite.style = dict.dictValue
                                }
                            })
                        })

                        //	将过滤的发布到字典的第一项添加到表单展示默认选项，没有匹配则设置为空: (默认展示：当contentStyle有值时不设置默认值)
                        that.hasContentStyleOptions.length && that.form.contentStyle.length == 0 ? that.form.contentStyle = [that.hasContentStyleOptions[0].style] : null
                        that.contentStyleOptions = that.hasContentStyleOptions;
                    }
                })
            },
            //获取contentType数据源
            contentTagsOptionsGet: function () {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {
                    dictType: '文章标签',
                    pageSize: 99999
                }).then(function (data) {
                    if(data.result){
                        data = data.data;
                        that.contentTagsOptions = data.rows;
                    }
                });
            },
            //获取contentType数据源
            contentTypeOptionsGet: function () {
                var that = this;
                ms.http.get(ms.base + '/mdiy/dict/list.do', {
                    dictType: '文章属性',
                    pageSize: 99999
                }).then(function (data) {
                    if (data.result) {
                        data = data.data;
                        that.contentTypeOptions = data.rows;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },

            view: function () {
                var that = this;
                if (that.form.id && that.form.contentStyle) {
                    if (that.form.contentStyle[0]) {
                        window.open(ms.manager + "/cms/content/view.do?style=" + that.form.contentStyle[0] + "&id=" + that.form.id);
                    } else {
                        window.open(ms.manager + "/cms/content/view.do?style=" + that.contentStyleOptions[0].dictValue + "&id=" + that.form.id);

                    }
                }
            },

            contentImghandleRemove: function (file, files) {
                var index = -1;
                index = this.form.contentImg.findIndex(function (text) {
                    return text == file;
                });

                if (index != -1) {
                    this.form.contentImg.splice(index, 1);
                }
            },

            //查询列表
            list: function (categoryId) {
                var that = this;
                ms.http.post(ms.manager + "/cms/content/list.do", {
                    categoryId: categoryId
                }).then(function (res) {
                    if (res.result && res.data.total > 0) {
                        if (res.data.rows[0].contentType) {
                            res.data.rows[0].contentType = res.data.rows[0].contentType.split(',');
                        }

                        if (res.data.rows[0].contentImg) {
                            res.data.rows[0].contentImg = JSON.parse(res.data.rows[0].contentImg);
                            res.data.rows[0].contentImg.forEach(function (value) {
                                value.url = ms.base + value.path;
                            });
                        } else {
                            res.data.rows[0].contentImg = [];
                        }

                        that.form = res.data.rows[0];
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //只有在渲染完栏目数据之后才会初始化
            init: function () {
                this.form.id = ms.util.getParameter("id");
                this.type = ms.util.getParameter("type");

                //在指定栏目下新增或编辑文章时
                if (this.form.categoryId) {
                    //如果是封面栏目直接跳转
                    if (this.type) {
                        this.getFromFengMian(this.form.categoryId);
                        this.returnIsShow = false;
                        //指定非封面栏目编辑文章
                    } else if (this.form.id) {
                        this.get(this.form.id);
                        //指定栏目新增文章渲染自定义模型
                    } else {
                        this.changeModel();
                    }
                    //不指定栏目编辑文章
                } else if (this.form.id) {
                    this.get(this.form.id);
                }//else 如果即不指定栏目新增文章，又不是编辑文章就不渲染自定义模型

            },
            //获取模板类型 字典数据
            dictListGet: function () {
                var that = this;
                //获取  模板类型 字典数据
                ms.mdiy.dict.list('模板类型').then(function (res) {
                    that.dictList = res.data.rows;
                    that.contentCategoryIdOptionsGet();
                })
            },


            // 递归添加样式
            addStyleForList: function (list, config) {
                var that = this
                // dom数组得到数组原型方法，便于操作
                list = [].slice.call(list);
                //  循环编辑器内的dom元素
                var flag = true;
                list.forEach(function (item) {
                    //对div标签进行替换p标签处理
                    if(item.tagName.toLowerCase() == "div" && item.parentNode!=null) {
                        var parent = item.parentNode;
                        var p = document.createElement("p");
                        p.innerHTML = item.innerHTML;
                        parent.replaceChild(p,item);
                        if (flag){
                            that.$notify({
                                title: '一键排版提示',
                                message: '检测到需要替换的标签，请重新点击一键排版',
                                type: 'warning'
                            });
                            flag = false;
                        }

                    }

                    //  匹配dom元素对应的标签类型，并转换为小写进行匹配
                    for ( dataKey in config) {
                        if (item.tagName.toLowerCase() == dataKey) {
                            //如果是显示标题的图片就不需要对图片进行一键格式化设置样式
                            if(item.tagName.toLowerCase() == "img" && item.nextSibling &&  item.nextSibling.tagName && item.nextSibling.tagName.toLowerCase() == "figcaption") {
                                continue;
                            }

                            //获取自定义配置对应标签style样式
                            for ( itemKey in config[dataKey]) {
                                if (typeof config[dataKey][itemKey] === "object") {
                                    for ( itemKeys in config[dataKey][itemKey]) {
                                        item[itemKey][itemKeys] = config[dataKey][itemKey][itemKeys]
                                    }
                                } else {
                                    item.setAttribute(itemKey, config[dataKey][itemKey]);
                                }
                            }
                        }
                    }
                    //  是否需要进入子元素再次进行循环排版
                    if (item.children && item.children.length) {
                        that.addStyleForList(item.children, config)
                    }
                })
            },
            //复制文章id
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
        created: function () {
            var that = this;
            that.form.categoryId = ms.util.getParameter("categoryId");
            that.dictListGet();
            that.contentTypeOptionsGet();
            that.contentTagsOptionsGet();

            //  编辑器配置挂载，在编辑器实例创建完成后进行
            that.tinyInit = {
                //  主程序目录
                base_url: '${base}/static/plugins/tinymce/5.10.2/',
                // 关闭路径转换功能
                convert_urls: false,
                //  语言包路径
                language_url: '${base}/static/plugins/tinymce/5.10.2/zh_CN.js',
                //  语言格式
                language: 'zh_CN',
                //  编辑器高度
                height: 560,
                //  是否开启头部文字菜单
                menubar: false,
                image_caption: true,
                image_description: false,
                content_style: 'figure img {margin-left:unset;transform:unset}figure {display: table;margin: 1rem auto;text-align: center;} figure figcaption {color: #999;display: block;margin-top: 0.25rem;text-align: center}',
                //placeholder（内容预展示文本）
                placeholder: '在此处编辑内容!',
                //  工具栏展示工具
                toolbar: [
                    'fullscreen  undo redo oneKeyFormat |  fontselect fontsizeselect formatselect | bold italic insertfile underline strikethrough lineheight | forecolor backcolor  | ' +
                    'alignleft aligncenter alignright alignjustify indent2em | outdent indent ltr rtl | numlist bullist | ' +
                    ' table pagebreak hr  | charmap emoticons removeformat  searchreplace | ' +
                    'image repository media link  |  code  codesample insertdatetime anchor preview visualblocks wordcount print bdmap'
                ],
                //  工具栏依赖插件包
                plugins: 'print preview powerpaste importcss searchreplace autolink bdmap directionality code visualblocks visualchars fullscreen image link media template codesample table charmap hr pagebreak nonbreaking anchor insertdatetime advlist lists wordcount textpattern noneditable charmap emoticons indent2em',

                //  引入外部样式对编辑器进行样式设置
                content_css: '${base}/static/plugins/tinymce/5.10.2/skins/content/snow/content.css',
                //  编辑器内部自定义样式（改动需要联同一键排版功能） (改为自定义配置)
                //content_style: '.example { text-indent: 2em;width: 100%;overflow: hidden; }' + '.tc {text-align: center}' + 'span {text-indent: initial;}',
                //  字体格式默认值
                fontsize_formats: '12px 14px 16px 18px 24px 36px 48px 56px 72px',
                //  字体风格默认值
                font_formats: '微软雅黑=Microsoft YaHei,Helvetica Neue,PingFang SC,sans-serif;苹果苹方=PingFang SC,Microsoft YaHei,sans-serif;宋体=simsun,serif;仿宋体=FangSong,serif;黑体=SimHei,sans-serif;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats;知乎配置=BlinkMacSystemFont, Helvetica Neue, PingFang SC, Microsoft YaHei, Source Han Sans SC, Noto Sans CJK SC, WenQuanYi Micro Hei, sans-serif;小米配置=Helvetica Neue,Helvetica,Arial,Microsoft Yahei,Hiragino Sans GB,Heiti SC,WenQuanYi Micro Hei,sans-serif',
                //  工具栏模式
                toolbar_mode: 'wrap',

                //  附件标题框的显示
                link_title: false,
                //  当前编辑器上传支持上传类型
                file_picker_types: 'file image media',//   file image media
                powerpaste_word_import: 'propmt',// 参数可以是propmt, merge, clear，效果自行切换对比
                powerpaste_html_import: 'propmt',// propmt, merge, clear
                powerpaste_allow_local_images: true,
                paste_data_images: true,
                images_upload_handler: function (blobInfo, success, failure) {
                    // 这个函数主要处理word中的图片，并自动完成上传；

                    var formData = new FormData();
                    formData.append('file', blobInfo.blob());
                    formData.append('uploadPath', '/cms/content');
                    formData.append('isRename', true);
                    formData.append('appId', true);

                    //  上传类型为图片
                    ms.http.post(ms.manager + '/file/upload.do', formData, {
                        timeout: 1000*60*5,
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }).then(function (res) {
                        if(res.result) {
                            //  成功回调设置图片路径为服务器的路径
                            success(ms.base+res.data);

                        } else {
                            that.$notify({
                                title: '失败',
                                message: res.msg,
                                type: 'error'
                            });
                            failure("请检查文件后重新上传");
                        }

                    }).catch(function (err) {
                        //  图片同步上传服务器失败，提示并移除插入的本地图片
                        that.$notify({
                            title: '失败',
                            message:  '上传资源库失败!',
                            type: 'error'
                        });
                        failure("请检查文件后重新上传");
                    })
                },
                //默认图片居中
                init_instance_callback: function(editor)  {
                    editor.on('ExecCommand', function(e)  {
                        console.log(e);
                        if(e.command=="mceUpdateImage") {
                            editor.execCommand("JustifyCenter");
                        }
                        if(e.command=="mceInsertContent" && e.value.indexOf("<video ") > -1) {
                            editor.execCommand("JustifyCenter");
                        }
                    });


                },
                // tinymce的其他配置参数
                //自定义文件选择器的回调内容
                file_picker_callback: function (callback, value, meta) {
                    //  创建表单，类型为文件格式
                    var input = document.createElement('input');
                    input.setAttribute('type', 'file');
                    var fileType = '附件'
                    //  上传类型为图片时，设置控件上传为image格式
                    if (meta.filetype == 'image') {
                        input.setAttribute('accept', 'image/*');
                        fileType = '图片'

                        //  上传类型为media媒体，设置上传格式为video和audio格式
                    } else if (meta.filetype == 'media') {
                        input.setAttribute('accept', 'video/*,audio/*');
                        fileType = '媒体'

                    }


                    input.onchange = function () {
                        var file = this.files[0];
                        var reader = new FileReader();
                        reader.onload = function () {
                            var formData = new FormData();
                            formData.append('file', file);
                            formData.append('uploadPath', '/cms/content');
                            formData.append('isRename', true);
                            formData.append('appId', true);

                            //  上传类型为图片
                            ms.http.post(ms.manager + '/file/upload.do', formData, {
                                headers: {
                                    'Content-Type': 'multipart/form-data'
                                }
                            }).then(function (res) {
                                if(res.result) {
                                    //  成功回调设置图片路径为服务器的路径
                                    callback(ms.base+res.data, {title: file.name, text: file.name});
                                    that.$notify({
                                        title: '成功',
                                        message: fileType + '成功上传资源库!',
                                        type: 'success'
                                    });
                                } else {
                                    that.$notify({
                                        title: '失败',
                                        message: res.msg,
                                        type: 'error'
                                    });
                                }

                            }).catch(function (err) {
                                //  图片同步上传服务器失败，提示并移除插入的本地图片
                                that.$notify({
                                    title: '失败',
                                    message: fileType + '上传资源库失败!',
                                    type: 'error'
                                });
                            })
                        };

                        reader.readAsDataURL(file);
                        document.body.removeChild(input)
                    };
                    //添加到body
                    document.body.appendChild(input)
                    input.click();
                },

                //  自定义TinyMCE 编辑器实例之前执行的回调
                setup: function (editor) {
                    //是否开启一键排版

                    //  添加自定义 (排版) 按钮
                    editor.ui.registry.addButton('oneKeyFormat', {
                        //text: '一键排版',
                        icon: 'accessibility-check',
                        tooltip: '一键排版',
                        disabled: false,
                        onAction: function (api) {
                            //  全选编辑器的内容
                            editor.execCommand('selectAll');
                            //  获取编辑器内选中的信息
                            var eleDom = editor.selection.getSelectedBlocks()
                            editor.execCommand('removeFormat');

                            //  调用匹配添加样式的方法，该方法写在addStyleForList中
                            if (that.oneKeyFormatConfig.oneKeyFormatStart != "true") {
                                that.$notify({
                                    title: "提示",
                                    message: "一键排版没有开启，请开启后再使用。",
                                    type: "warning"
                                })
                            } else {
                                try {
                                    var data = JSON.parse(that.oneKeyFormatConfig.oneKeyFormat);
                                    that.addStyleForList(eleDom, data);
                                } catch (err) {
                                    console.log(err)
                                    that.$notify({
                                        title: "警告",
                                        message: "一键排版配置JSON有误,请修改配置",
                                        type: "error"
                                    })
                                }
                            }

                        }
                    })

                    // 资源库上传按钮
                    editor.ui.registry.addButton('repository', {
                        icon: 'gallery',
                        tooltip: '资源库',
                        onAction: function () {
                            that.visibleUpload = true
                            // 把tiny资源库置为开启状态
                            that.repository = true
                        }
                    });
                },
            }

        },
        mounted: function () {
            var that = this
            that.init();

            ms.mdiy.config("一键排版配置", null, true).then(function (res) {
                if (res.result) {
                    that.oneKeyFormatConfig = res.data;
                }
            });
        }

    });
</script>
<style>
    .upload-img {
        overflow: hidden;
        cursor: pointer;
        width: 138px;
        height: 138px;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px dashed #E2E3E3;
        border-radius: 8px;
    }

    .upload-img:hover {
        border-color: #66b1ff;
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

    .el-select-dropdown .el-scrollbar > .el-select-dropdown__wrap {
        margin-bottom: 0 !important;
    }
    .header-info {
        white-space: nowrap;
        display:inline-block;
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>
