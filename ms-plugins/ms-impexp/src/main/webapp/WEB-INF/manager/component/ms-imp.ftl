<#--
导入：word\excel
id:导入模块中的导入导出标识
type:word/excel
data:上传的额外参数，用于替换import_json中columns中的参数，其中content、docFileName参数是固定的，为导入的内容和文件名称
import_json 格式范例
 {
		"table":"cms_content",//表名
		"id":"snow", //雪花id\auto自增长
		// 需要替换的变量和数据库中对应的字段
		"columns":{
				"docFileName":"content_title",
				 "content":"content_details",
				 "categoryId":"category_id"
		},
		// 导入的默认值
		"defaults":{
				 "content_display":"1",
				 "create_date":"2021-03-16 18:05:15"
		}
}
使用范例
引入<#include "/component/ms-imp.ftl">
<ms-imp id="文章导入" type="word" :data="{categoryId:'123',docFileName:'文件名称'}"></ms-imp>

导入：excel
id:导入模块中的导入导出标识
type:excel
import_json 格式范例
[
    {
        // 主表
        "table":"cms_content",
        "id":"snow",//雪花id\auto自增长
        // Excel中列名和数据库对应的字段名
        "columns":{
            "公司名称":"content_title",
             "国家或地区":"category_id"
        }
    },
    {
        //关联表
        "table":"mdiy_model_zd_enterprise",
        // 关联表的字段名称link_id
        "id":"link_id",
         // 关联表的字段名
        "columns":{
         "地址":"addr",
         "联系人":"poeple",
         "电话":"phone",
         "传真":"fax",
          "电邮":"email"
        }
    }
]
使用范例
引入<#include "/component/ms-imp.ftl">
<ms-imp id="文章导入" type="excel" ></ms-imp>
-->
<script type="text/x-template" id="ms-imp">
    <el-upload v-bind="$props"
               ref="upload"
               :action="ms.manager+url"
               :accept="accept"
               :limit="limit"
               :data="data"
               :img-dir="imgDir"
               :show-file-list="false"
               :disabled="disabled"
               :on-success="success"
               :before-upload="beforeAvatarUpload"
               :on-exceed="exceed">
        <el-tooltip effect="dark" :content="'只允许上传'+filePreFix+'文件！'" placement="left-end">
            <el-button size="mini" :disabled="disabled" icon="el-icon-upload2" type="primary">导入{{type}}</el-button>
        </el-tooltip>
    </el-upload>

</script>
<script>
    (function () {
        var props = Object.assign({
            // 配置名称
            id: String,
            // 图片存储地址
            imgDir: String,
            // 文件类型
            type: String,
            // json参数
            columns: Object,
        }, Vue.options.components.ElUpload.options.props)
        Vue.component('ms-imp', {
            template: '#ms-imp',
            props: props,
            data: function () {
                return {
                    id: this.id,
                    type: this.type,
                    // 文件格式类型
                    filePreFix: '',
                    ms: ms,
                    url: "/impexp/excel/imp.do",
                    uploadPath: "/improt"
                }
            },
            computed: {
                accept: function () {
                    if (this.type == "excel") {
                        return ".xlsx"
                    } else if (this.type == "word") {
                        this.url = "/impexp/word/imp.do";
                        return ".docx"
                    } else {
                        return ""
                    }
                },
                data: function () {
                    var uploadData = {uploadFolderPath: true, name: this.id };
                    if(this._props.columns !=undefined){
                        uploadData = Object.assign(this._props.columns, {imgDir: this._props.imgDir}, uploadData);
                    }else {
                        uploadData = Object.assign({imgDir: this._props.imgDir}, uploadData);
                    }
                    return uploadData;
                }
            },
            methods: {
                success: function (response, file, fileList) {
                    if (response.result) {
                        this.$notify({
                            title: "提示",
                            message: "导入成功",
                            type: 'success'
                        });
                        this.$emit('success');
                        this.disabled = false;
                    } else {
                        this.$notify({
                            title: '提示',
                            message: response.msg,
                            type: 'warning'
                        });
                        this.disabled = false;
                    }
                    // 清空文件列表
                    this.$refs.upload.clearFiles()
                },
                beforeAvatarUpload: function (file) {
                    this.disabled = true;
                    this.data.docFileName = file.name.substring(0, file.name.lastIndexOf("."));
                },
                exceed: function (file, fileList) {
                    this.$notify({title: '当前最多上传' + this.limit + '个附件', type: 'warning'});
                },
            },
            created: function () {
                if (this.type === 'word'){
                    this.filePreFix = 'docx';
                }else if (this.type === 'exel') {
                    this.filePreFix = 'xlsx';
                }else {
                    this.filePreFix = this.type;
                }
            }
        });

    })()
</script>



