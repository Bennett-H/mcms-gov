<!DOCTYPE html>
<html>
<head>
    <title>采集规则</title>
    <#include "../../include/head-file.ftl">
</head>
<body>
<div id="form" v-loading="loading" v-cloak>

    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存</el-button>
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>



    <el-main class="ms-container">
        <el-scrollbar class="ms-scrollbar" style="height: 100%;">
        <el-alert
                title="功能介绍"
                type="info"
                description="首先配置好 列表页 ,内容链接会从规定的范围内(列表开始域和列表结束域)匹配内容链接;">
        </el-alert>
        <br/>
        <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right" size="small">
            <el-row :gutter="0" justify="start" align="top">
                <el-col :span="8">
                    <el-form-item  label="规则名称" prop="regularName">
                        <el-input
                                v-model="form.regularName"
                                :disabled="false"
                                :readonly="false"
                                :style="{width:  '100%'}"
                                :clearable="true"
                                maxlength="10"
                                placeholder="请输入规则名称">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item  label="线程数" prop="threadNum">
                        <el-input
                                v-model.number="form.threadNum"
                                type='number'
                                :disabled="false"
                                :readonly="false"
                                :style="{width:  '100%'}"
                                :clearable="true"
                                placeholder="请输入线程数">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item  label="字符编码" prop="charset">
                        <el-select  v-model="form.charset"
                                    :style="{width: '100%'}"
                                    :filterable="false"
                                    :disabled="false"
                                    :multiple="false" :clearable="true"
                                    placeholder="请选择字符编码">
                            <el-option v-for='item in charsetOptions' :key="item.value" :value="item.value"
                                       :label="item.label"></el-option>
                        </el-select>
                        <div class="ms-form-tip">根据被采集页面网页源码的编码来选择</div>
                    </el-form-item>

                </el-col>
            </el-row>
            <el-form-item  label="列表页地址" prop="linkUrl">
                <el-input
                        v-model="form.linkUrl"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        maxlength="150"
                        placeholder="请输入被采集页面">
                </el-input>
                <div class="ms-form-tip">
                    如果列表页地址结尾带有/则表示取列表的相对路径,这时内容的地址会拼接列表的地址<br>
                    反之则取内容地址的绝对地址<br>
                    可以填写get请求的接口地址,列表匹配区域使用固定的字符进行限定,内容链接匹配需要json数据中的地址
                </div>
            </el-form-item>
            <el-row
                    :gutter="0"
                    justify="start" align="top">
                <el-col :span="8">
                    <el-form-item  label="是否分页" prop="isPage">
                        <el-radio-group v-model="form.isPage"
                                        :style="{width: ''}"
                                        :disabled="false">
                            <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.value"
                                      v-for='(item, index) in isPageOptions' :key="item.value + index">
                                {{item.label}}
                            </el-radio>
                        </el-radio-group>

                        <div class="ms-form-tip">可使用分页通配符：%s<br/>
                            需要匹配：http://www.baidu.com/1.html<br/>
                            通配符使用：http://www.baidu.com/<span style="color: red">%s</span>.html
                        </div>

                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item  label="起始页" prop="startPage" v-show="form.isPage == 'yes'">
                        <el-input
                                v-model.number="form.startPage"
                                :disabled="false"
                                :readonly="false"
                                :style="{width:  '100%'}"
                                :clearable="true"
                                maxlength="150"
                                placeholder="请输入起始页">
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item  label="结束页" prop="endPage" v-show="form.isPage == 'yes'">
                        <el-input
                                v-model.number="form.endPage"
                                :disabled="false"
                                :readonly="false"
                                :style="{width:  '100%'}"
                                :clearable="true"
                                maxlength="150"
                                placeholder="请输入结束页">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row
                    :gutter="0"
                    justify="start" align="top">
                <el-col :span="12">
                    <el-form-item  label="列表开始区域" prop="startArea">

                        <el-input
                                type="textarea" :rows="5"
                                :disabled="false"
                                :readonly="false"
                                v-model="form.startArea"
                                :style="{width: '100%'}"
                                maxlength="150"
                                placeholder="请输入列表开始区域">
                        </el-input>
                        <div class="ms-form-tip"> 列表开始区域<br/>
                            <span style="color: red">&lt;div class='news'&gt;</span><br/>
                            &lt;div class='list'&gt;文章列表&lt;/div&gt;<br/>
                            &lt;/div&gt;&lt;div class='foot'&gt;
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item  label="列表结束区域" prop="endArea">

                        <el-input
                                type="textarea" :rows="5"
                                :disabled="false"
                                :readonly="false"
                                v-model="form.endArea"
                                :style="{width: '100%'}"
                                maxlength="150"
                                placeholder="请输入列表结束区域">
                        </el-input>
                        <div class="ms-form-tip"> 列表结束区域<br/>
                            &lt;div class='news'&gt;<br/>
                            &lt;div class='list'&gt;文章列表&lt;/div&gt;<br/>
                            <span style="color: red">&lt;/div&gt;&lt;div class='foot'&gt;</span>
                        </div>

                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item  label="内容链接匹配" prop="articleUrl">

                <el-input
                        v-model="form.articleUrl"
                        :disabled="false"
                        :readonly="false"
                        :style="{width:  '100%'}"
                        :clearable="true"
                        maxlength="100"
                        placeholder="请输入内容链接">
                </el-input>
                <div class="ms-form-tip">
                    正则匹配示例：<br/>
                    目标匹配地址：http://beijing.qianlong.com/zt/jcznl2020/index.shtml<br/>
                    正则匹配示例：http://beijing.qianlong.com/<span style="color: red">(.*?)</span>"<br/>
                    注意:需要用（）把链接地址包裹起来,不然会出现匹配链接不全的问题<br/>
                    href="<span style="color: red">(</span>http://beijing.qianlong.com/(.*?).shtml<span style="color: red">)</span>"

                </div>
            </el-form-item>

        </el-form>

        <el-divider></el-divider>


        <div class="ms-form-tip">
            <el-alert
                    title="注意"
                    type="info"
                    description="注意采集到的指定的栏目下的话,需要指定栏目id和栏目在数据的字段名,字段匹配规则的数据来源于采集匹配规则字典,推荐使用xpath,防止采集的数据不完整导致html转义出现问题;如果是列表规则,则只能使用xpath">
            </el-alert>
        </div>
        <el-row style="margin-bottom: 5px">
            <el-col>
                <el-button type="primary" icon="el-icon-circle-plus" size="mini" @click="newLine()">新增一列</el-button>
            </el-col>
        </el-row>



        <el-table tooltip-effect="dark" ref="multipleTable" border
                  :data="form.metaData"
                  style="width: 100%">
            <el-table-column  min-width="12%"
                              label="字段匹配名称">
                <template slot-scope="scope">
                    <el-input
                            type="text"
                            size="small"
                            v-model="scope.row.name"
                            :disabled="false"
                            :readonly="false"
                            :style="{width:  '100%'}"
                            :clearable="true"
                            maxlength="10"
                            placeholder="请输入字段匹配名称">
                    </el-input>
                </template>
            </el-table-column>
            <el-table-column width="400px"
                             label="字段匹配规则" :render-header="renderHeader">

                <template slot-scope="scope">
                    <el-radio-group v-model="scope.row.type"
                                    :style="{width: ''}"
                                    :disabled="false">
                        <el-radio :style="{display: true ? 'inline-block' : 'block'}" :label="item.dictValue"
                                  v-for='(item, index) in filedRegularOptions' :key="item.dictValue + index">
                            {{item.dictLabel}}
                        </el-radio>
                    </el-radio-group>
                </template>
            </el-table-column>

            <el-table-column min-width="12%"
                             label="映射类型" :render-header="renderHeader">

                <template slot-scope="scope">
                    <el-select
                            size="small"
                            v-model="scope.row.fieldType"
                            :style="{width: '100%'}"
                            :filterable="false"
                            :disabled="false"
                            :multiple="false" :clearable="true"
                            placeholder="请选择映射类型">
                        <el-option v-for='(item,index) in filedDfieldTypeOptions' :key="index" :value="item.value"
                                   :label="item.label"></el-option>
                    </el-select>
                </template>
            </el-table-column>

            <el-table-column min-width="25%"
                             label="字段匹配（默认值）" :render-header="renderHeader">

                <template slot-scope="scope">
                    <el-input
                            size="small"
                            v-model="scope.row.expression"
                            :disabled="false"
                            :readonly="false"
                            :style="{width:  '100%'}"
                            :clearable="true"
                            maxlength="30"
                            placeholder="请输入字段匹配">
                    </el-input>
                </template>
            </el-table-column>

            <el-table-column width="200px"
                             label="关联表列名" :render-header="renderHeader">
                <template slot-scope="scope">
                    <el-select
                            size="small"
                            v-model="scope.row.filed"
                            :style="{width: '100%'}"
                            :filterable="false"
                            :disabled="false"
                            :multiple="false" :clearable="true"
                            placeholder="请选择关联表列名">
                        <el-option v-for='item in filedDfiledOptions' :key="item" :value="item"
                                   :label="item"></el-option>
                    </el-select>
                </template>
            </el-table-column>

            <el-table-column min-width="20%"
                             label="结果替换规则" :render-header="renderHeader">
                <template slot-scope="scope">
                    <el-input
                            type="textarea"
                            v-model="scope.row.fieldFormat"
                            :disabled="false"
                            :readonly="false"
                            :style="{width:  '100%'}"
                            :clearable="true"
                            maxlength="100"
                            placeholder="请输入结果替换规则">
                    </el-input>
                </template>
            </el-table-column>

            <el-table-column width="100" align="center"
                             label="列表规则" :render-header="renderHeader">
                <template slot-scope="scope">
                    <el-switch
                            v-model="scope.row.isMatch">
                    </el-switch>
                </template>
            </el-table-column>

            <el-table-column width="100" align="center"
                             label="html内容" :render-header="renderHeader">
                <template slot-scope="scope">
                    <el-switch
                            v-model="scope.row.isHtml">
                    </el-switch>
                </template>
            </el-table-column>

            <el-table-column align="center" label="操作" width="100px">
                <template slot-scope="scope">
                    <el-link type="primary" :underline="false" @click="delLine(scope.$index)">删除</el-link>
                    <el-link type="primary" :underline="false" @click="newLine(scope.row,scope.$index)">复制</el-link>
                </template>
            </el-table-column>
        </el-table>
        </el-scrollbar>
    </el-main>
</div>
</body>
</html>
<script>
    var form = new Vue({
        el: '#form',
        data:function() {
            return {
                tableName:'',
                taskId:'',
                loading:false,
                saveDisabled: false,
                //表单数据
                form: {
                    // 规则名称
                    regularName:'',
                    // 线程数
                    threadNum:1,
                    // 字符编码
                    charset:'custom',
                    // 被采集页面
                    linkUrl:'',
                    // 是否分页
                    isPage:'no',
                    // 起始页
                    startPage:'',
                    // 结束页
                    endPage:'',
                    // 列表开始区域
                    startArea:'',
                    // 列表结束区域
                    endArea:'',
                    // 内容链接匹配
                    articleUrl:'',
                    metaData:
                        [
                            {
                                name:'',
                                type:'regular',
                                expression:'',
                                fieldType:'string',
                                filed:'',
                                isMatch:false,
                                isHtml: false,
                                fieldFormat:''

                            },
                        ]
                },
                charsetOptions:[{"value":"UTF-8","label":"UTF-8"},{"value":"GBK","label":"GBK"},{"value":"custom","label":"自适应"}],
                isPageOptions:[{"value":"yes","label":"是"},{"value":"no","label":"否"}],
                filedRegularOptions:[],
                filedDfiledOptions:[],
                filedDfieldTypeOptions:[{"label":"字符串","value":"string"},{"label":"数字","value":"number"},{"label":"日期","value":"date"}],
                rules:{
                    // 规则名称
                    regularName: [{"type":"string","message":"规则名称格式不正确"},{"required":true,"message":"规则名称不能为空"}],
                    // 线程数
                    threadNum: [{"type":"number","message":"线程数格式不正确"}],
                    // startPage: [{"type":"number","message":"起始页格式不正确"}],
                    // endPage: [{"type":"number","message":"结束页格式不正确"}],
                    // 被采集页面
                    linkUrl: [{"type":"string","message":"被采集页面格式不正确"},{"required":true,"message":"被采集页面不能为空"}],
                    // 内容链接匹配
                    articleUrl: [{"required":true,"message":"内容链接匹配不能为空"}],
                    // 字段匹配名称
                    filedName: [{"required":true,"message":"字段匹配名称不能为空"}],
                    // 字段匹配
                    filedText: [{"type":"string","message":"字段匹配格式不正确"},{"required":true,"message":"字段匹配不能为空"}],
                },

            }
        },
        watch:{
        },
        computed:{
        },
        methods: {
            // 渲染element表头（实际功能时为表头增加tooltip
            renderHeader: function(h, obj) {
                var tipText = '';
                switch (obj.column.label) {
                    case "字段匹配规则":tipText = "默认值：直接存储到关联表列\n"+
                        "css选择器：如 .context\n"+
                        "xpath：如 //*[@id=\"context\"]\n"+
                        "获取图片src属性值\n"+
                        "//*[@class=\"img_wrapper\"]/img/@src\n"+
                        "获取文章内容\n"+
                        "//*[@class=\"context\"]/text()\n"+
                        "XPath 语法网址https://www.runoob.com/xpath/xpath-syntax.html\n"+
                        "具体官方文档: http://webmagic.io/docs/zh/posts/ch4-basic-page-processor/xsoup.html\n"+
                        "正则：\n "+
                        "常用匹配一（匹配文本内容） \n"+
                        "源:<h1 class=\"title\">匹配内容</h1>\n"+
                        "例:<h1 class=\"title\">(.*?)</h1> \n "+
                        "常用匹配二(匹配日期) \n"+
                        "源:<span class=\"date\">2020-01-01</span> \n"+
                        "例:<span class=\"date\">\\d{4}-\\d{2}-\\d{2}</span> \n"+
                        "源:<span class=\"date\">2020-01-01 08:24</span> \n"+
                        "例:<span class=\"date\">\\d{4}年\\d{2}月\\d{2}日 \d{2}:\d{2}</span> \n"+
                        "常用匹配二(匹配图片) \n"+
                        "源:<img src=\"http://beijing.qianlong.com/zt/jcznl2020/index.jpg\">\n"+
                        "例:http://beijing.qianlong.com/(.*?)jpg \n"+
                        "常用匹配二(匹配连接地址) \n"+
                        "源:http://beijing.qianlong.com/zt/jcznl2020/index.shtml\n"+
                        "例:http://beijing.qianlong.com/(.*?)shtml\n"+
                        "需要采集栏目列表上的文章缩略图:\n"+
                        "xpath 语法: //a[@href=\"{}\"]/img/@src\n"+
                        "内容链接匹配更改为：href=\"(https://beijing.qianlong.com.cn/(.*?))\"\n"+
                        "结果替换规则(需要打开右侧 列表规则):\n"+
                        "<#noparse>[<#list content as item>\n"+
                        "{\"path\": \"${\"/upload/\"+app.id+\"/content\"+item?substring(item?last_index_of(\"/\"))}\"}\n"+
                         "<#if item_has_next>,</#if></#list>]</#noparse>";
                        break;
                    case "映射类型": tipText = '根据关联表列名字段类型选择';
                        break;
                    case "关联表列名": tipText = '采集的数据导入的目标表的对应字段';
                        break;
                    case "字段匹配（默认值）": tipText = '根据匹配规则填写相应的表达式\n' +
                        '字段匹配规则为默认时则返回当前填写的默认值\n'+
                        '开启列表页匹配后"{}"占位符表示当前详情页的链接';
                        break;
                    case "列表规则": tipText = '开启后只在列表页匹配,详情页不匹配\n'+
                        '例如用于获取网站列表上的文章缩略图\n'+"列表规则的字段匹配规则建议使用xpath";
                        break;
                    case "html内容": tipText = '开启后会校验html格式,通过才保存';
                        break;
                    case "结果替换规则": tipText = '可以对采集到结果进行内容修改，支持freeMark语法\n'+
                        '可用参数：content采集到的内容,app当前应用信息\n'+
                        '例如：将采集到的图片路径修改为json格式，这样做的目的是提供给el-upload控件使用:\n' +
                        '<#noparse><#if content??>\n'+
                        '<#--拼装el-upload的数据格式，这里通过app.id区分了站点的路径-->\n'+
                            '[{"path": "${"/upload/"+app.id+"/content"+content?substring(content?last_index_of("/"))}"}]\n'+
                            '<#else>\n'+
                            '<#--否则为空数组-->\n'+
                            '[]\n'+
                        '</#if></#noparse>';
                        break;

                    default:break;
                }

                //返回数组交给el-column渲染
                return [
                    obj.column.label,
                    h('el-tooltip', {
                        props: {
                            content: tipText,
                        }
                    }, [
                        h('span', {
                            class: {
                                'el-icon-question': true
                            }
                        })
                    ])
                ];
            },
            //删除一列
            delLine:function(index){
                this.form.metaData.splice(index, 1);
            },
            //新增一列
            newLine:function(row,index){
                var that = this
                if (row){
                    that.form.metaData.splice(index,0,{
                        name:row.name,
                        type:row.type,
                        expression:row.expression,
                        fieldType:row.fieldType,
                        filed:row.filed,
                        fieldFormat:row.fieldFormat,
                        isMatch:row.isMatch,
                        isHtml:row.isHtml,
                    });
                }else {
                    that.form.metaData.push({
                        name:'',
                        type:'regular',
                        expression:'',
                        fieldType:'string',
                        filed:'',
                    })
                }

            },
            save:function() {
                var that = this;
                var url = ms.manager + "/spider/taskRegular/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/spider/taskRegular/update.do";
                }
                this.$refs.form.validate(function(valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        //转换数据 防止多次转义
                        if (!(typeof(that.form.metaData) == 'string')) {
                            var metaStr = JSON.stringify(that.form.metaData);
                            that.form.metaData = metaStr;
                        }
                        if (that.form.isPage === 'no'){
                            that.form.startPage = undefined
                            that.form.endPage = undefined
                        }
                        that.form.taskId = that.taskId;
                        that.saveDisabled = true;
                        var data = JSON.parse(JSON.stringify(that.form));
                        ms.http.post(url, data).then(function (data) {
                            if (data.result) {
                                that.$notify({
                                    title: '成功',
                                    message: '保存成功',
                                    type: 'success'
                                });
                                history.go(-1)
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

            //获取当前采集规则
            get:function(id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/spider/taskRegular/get.do", {"id":id}).then(function (res) {
                    that.loading = false
                    if(res.result&&res.data){
                        that.form = res.data;
                        that.form.metaData = JSON.parse(res.data.metaData)
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取filedRegula数据源
            filedRegularOptionsGet:function() {
                var that = this;
                ms.http.get(ms.base+'/mdiy/dict/listExcludeApp.do', {dictType:'采集匹配规则'}).then(function (res) {
                    that.filedRegularOptions = res.data;
                }).catch(function (err) {
                    console.log(err);
                });
            },
            //获取filedDfiled数据源
            filedDfiledOptionsGet:function(tabaleName) {
                var that = this;
                ms.http.get(ms.manager+'/spider/taskRegular/fileds/'+ tabaleName, {}).then(function (res) {
                    that.filedDfiledOptions = res.data;
                }).catch(function (err) {
                    console.log(err);
                });
            }
        },
        created:function() {
            var that = this;
            this.filedRegularOptionsGet();
            this.form.id = ms.util.getParameter("id");
            var tableName = ms.util.getParameter("tableName");
            if (tableName) {
                this.tableName = tableName;
                this.filedDfiledOptionsGet(tableName);
            }
            var taskId = ms.util.getParameter("taskId");
            if (taskId) {
                this.taskId = taskId;
            }

            if (this.form.id) {
                this.get(this.form.id);
            }
            this.rules.regularName.push({
                validator: function (rule, value, callback) {
                    ms.http.get(ms.manager + "/spider/taskRegular/verify.do", {
                        fieldName: "regular_name",
                        fieldValue: value,
                        id: that.form.id,
                        idName: 'id'
                    }).then(function(res){
                        if (res.result) {
                            if (!res.data) {
                                callback("规则名称已存在！");
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
<style>
    /* 让column渲染的数据识别\n*/
    .el-tooltip__popper {
        white-space: pre-line;
    }
</style>
