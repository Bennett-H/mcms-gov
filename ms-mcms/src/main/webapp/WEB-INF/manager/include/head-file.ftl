<!--2021-11-12 增加cssUI控制 ,通过session获取-->
<meta charset="utf-8">
<!--浏览器小图标-->
<script type="text/javascript" src="${base}/static/plugins/vue/2.6.9/vue.min.js"></script>
<script src="${base}/static/plugins/vue-i18n/8.18.2/vue-i18n.js"></script>
<!-- 图标 -->
<link rel="stylesheet" type="text/css" href="${base}/static/plugins/iconfont/1.0.0/iconfont.css" />
<script src="${base}/static/plugins/iconfont/1.0.0/iconfont.js"></script>

<!-- 引入样式 -->
<link rel="stylesheet" href="${base}/static/plugins/element-ui/2.15.8/theme-chalk/index.min.css">
<!-- 引入组件库 -->
<script src="${base}/static/plugins/element-ui/2.15.8/index.min.js"></script>
<script src="${base}/static/plugins/element-ui/2.15.8/local/en.min.js"></script>
<script src="${base}/static/plugins/element-ui/2.15.8/local/zh-CN.min.js"></script>
<!--图片懒加载-->
<script src="${base}/static/plugins/vue.lazyload/1.2.6/vue-lazyload.js"></script>
<!--网络请求框架-->
<script src="${base}/static/plugins/axios/0.18.0/axios.min.js"></script>
<script src="${base}/static/plugins/qs/6.6.0/qs.min.js"></script>
<!--金额转换-->
<script src="${base}/static/plugins/accounting/0.4.1/accounting.js"></script>
<!--时间转换-->
<script src="${base}/static/plugins/moment/2.24.0/moment.min.js"></script>
<!--铭飞-->
<script src="${base}/static/plugins/ms/2.0/ms.umd.js"></script>
<script src="${base}/static/plugins/ms/2.0/ms-el-form.umd.js"></script>
<script src="${base}/static/plugins/vue-ueditor-wrap/vue-ueditor-wrap.min.js"></script>

<#--复制-->
<script src="${base}/static/plugins/clipboard/clipboard.js"></script>
<!--通用样式-->
<link rel="stylesheet" href="${base}/static/css/app.css"/>
<#--主题-->
<link rel="stylesheet" href="${base}/static/css/theme.css">
<![if IE]>
<script src="${base}/static/plugins/babel-polyfill/7.8.3/polyfill.min.js"></script>
<![endif]>

<#--语言文件-->
<script src="${base}/static/locale/lang/base/zh_CN.js"></script>
<script src="${base}/static/locale/lang/base/en_US.js"></script>

<script src="${base}/static/mdiy/index.js"></script>

<#--下拉框-->
<script src="${base}/static/plugins/vue-treeselect/0.4.0/vue-treeselect.umd.min.js"></script>
<link rel="stylesheet" href="${base}/static/plugins/vue-treeselect/0.4.0/vue-treeselect.min.css">

<#--代码预览-->
<script src="${base}/static/plugins/clipboard/clipboard.js"></script>
<script src="${base}/static/plugins/codemirror/5.48.4/codemirror.js"></script>
<link href="${base}/static/plugins/codemirror/5.48.4/codemirror.css" rel="stylesheet">
<script src="${base}/static/plugins/codemirror/5.48.4/mode/css/css.js"></script>
<script src="${base}/static/plugins/vue-codemirror/vue-codemirror.js"></script>
<script src="${base}/static/plugins/codemirror/5.48.4/addon/scroll/annotatescrollbar.js"></script>
<script src="${base}/static/plugins/codemirror/5.48.4/mode/xml/xml.js"></script>
<script src="${base}/static/plugins/codemirror/5.48.4/mode/javascript/javascript.js"></script>
<script src="${base}/static/plugins/tinymce/5.10.2/tinymce.min.js"></script>
<script src="${base}/static/plugins/tinymce-vue/4.0.6-rc/tinymce-vue.min.js"></script>

<script>
    //极速模式提示信息-最简练的判断方式
    if (navigator.userAgent.indexOf(".NET") > -1) {
        var svg_ie = "<svg t='1587836980741' class='icon' style='width: 2em; height: 2em; vertical-align: middle; fill: currentcolor; overflow: hidden; font-size:30px;' viewBox='0 0 1024 1024' version='1.1' xmlns='http://www.w3.org/2000/svg' p-id='2422' width='32' height='32'><path d='M804.5 334c12.7-44 16.7-129.9-62.1-140.5C679 184.9 604 227.2 564.5 253.3c-13-1.8-26.6-3-41-3.1-102.7-1.3-169.4 33.6-225.9 107.5-20.8 27.2-39.3 73.3-45.1 125.6 29.2-49.6 116.3-139.1 209.9-175.9 0 0-140.5 100.4-209.4 243.7l-0.2 0.2c0.1 0.7 0.1 1.4 0.2 2.1-2.9 6.5-6 13-8.8 19.9-68.6 168.5-12.5 241.4 38.8 255 47.3 12.4 113.8-10.7 166.7-67.5 90.6 21.3 179.5-2.6 213.4-21.2 63.6-34.9 106.8-96.5 117-159.8L606.7 579.8c0 0-7.3 56.1-100.4 56.1-85.8 0-89.4-99.3-89.4-99.3l368.7 0c0 0 7-107-45.9-178.3-29.3-39.6-69.5-74.5-125.4-93.3 17.1-12.6 46.6-32.2 71.5-38.7 47.2-12.3 79.5-5 99.8 29.3 27.5 46.7-15.2 156-15.2 156S791.3 380 804.5 334zM423.6 753.4c-73 59.4-133.5 52.9-156.8 17.1-20.2-31.2-23.8-87.3-0.1-163.7 11.1 29.6 28.5 58.3 54.3 83.8C352.4 721.6 387.7 741.3 423.6 753.4zM420.9 444.9c0 0 3.7-70.4 80.2-76.8 66.8-5.5 101.3 23.6 111.6 80.2L420.9 444.9z' p-id='2423' fill='#51b72f' data-spm-anchor-id='a313x.7781069.0.i11'></path></svg>";
        var svg_thunder = "<svg t='1588218903014' class='icon' style='width: 1.5em; height: 1.5em; vertical-align: middle; fill: currentcolor; overflow: hidden; font-size: 30px;' viewBox='0 0 1024 1024' version='1.1' xmlns='http://www.w3.org/2000/svg' p-id='4411' width='30' height='30'><path d='M395.765 586.57H224.032c-22.421 0-37.888-22.442-29.91-43.38L364.769 95.274a32 32 0 0 1 29.899-20.608h287.957c22.72 0 38.208 23.018 29.632 44.064l-99.36 243.882h187.05c27.51 0 42.187 32.427 24.043 53.099l-458.602 522.56c-22.294 25.408-63.627 3.392-54.976-29.28l85.354-322.421z' p-id='4412' fill='#51b72f'></path></svg>";
        var html = document.querySelector("html");
        html.innerHTML = "<div style='position:fixed;width: 100%;height: 100%;background:white;z-index:999999999999;font-size:30px;'><p style='font-family: \"Microsoft YaHei\"'>当前为IE兼容模式，推荐使用谷歌、360、QQ、火狐等新版本浏览器使用系统，如果是360浏览器请开启<b style='color:#51b72f'>极速模式</b><br>操作方式：点击浏览器地址栏右侧的IE符号" + svg_ie + "→选择“" + svg_thunder + "<b style='color:#51b72f;'>极速模式(推荐)</b>”</p></div>";
        html.style.overflow = "hidden";
    }

    ms.base = "${base}";
  	ms.login = "${managerPath}/login.do";
    ms.manager = "${managerPath}";
    ms.web = ms.base;
    //百度编辑器默认配置
    ms.editorConfig={
        imageScaleEnabled :true,
        autoHeightEnabled: true,
        autoFloatEnabled: false,
        scaleEnabled: true,
        compressSide:0,
        maxImageSideLength:1000,
        maximumWords: 2000,
        initialFrameWidth: '100%',
        initialFrameHeight: 400,
        serverUrl: ms.base + "/static/plugins/ueditor/1.4.3.3/jsp/editor.do?jsonConfig=%7BvideoUrlPrefix:\'\',fileManagerListPath:\'\',imageMaxSize:204800000,videoMaxSize:204800000,fileMaxSize:204800000,fileUrlPrefix:\'\',imageUrlPrefix:\'\',imagePathFormat:\'/upload/${appId}/cms/content/editor/%7Btime%7D\',filePathFormat:\'/upload/${appId}/cms/content/editor/%7Btime%7D\',videoPathFormat:\'/upload/${appId}/cms/content/editor/%7Btime%7D\'%7D",
        UEDITOR_HOME_URL: ms.base + '/static/plugins/ueditor/1.4.3.3/'
    }

    //ms.base = "http://192.168.0.54:90/";
    //ms.manager = "http://192.168.0.54:90/apis/ms/";
    //ms.web = "http://192.168.0.54:90/apis/";
    //图片懒加载
    Vue.use(VueLazyload, {
        error: ms.base + '/static/images/error.png',
        loading: ms.base + '/static/images/loading.png',
    })
    Vue.component('treeselect', VueTreeselect.Treeselect)
    Vue.component('vue-ueditor-wrap', VueUeditorWrap);

</script>
<style>
    .ms-admin-menu .is-active {
        border: 0px !important;
    }
    .vue-treeselect__placeholder,.vue-treeselect__single-value {
        font-size: 12px;
        padding-top: -8px;
    }
    .vue-treeselect__control {
        height: 28px;
    }
    .vue-treeselect__label {
        font-size: 12px;
    }

    .vue-treeselect__menu-container {
        z-index: 9999!important;
    }
</style>

<!--2021-11-12 增加cssUI控制 ,通过session获取-->
<#if Session["CSS-TIP"]?exists && Session["CSS-TIP"]=="true">
<style>
    .ms-form-tip,.ms-form-tip-err,.ms-alert-tip,.el-icon-question:before {
        display: none!important;
    }
</style>
</#if>

