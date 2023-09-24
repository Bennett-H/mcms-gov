<!DOCTYPE html>
<html>
<head>
    <title>文件管理</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/plugins/wl-explorer/0.1.4/wl-explorer-file.umd.js"></script>
</head>
<body>
<div id="explorer" v-cloak>
    <wl-explorer
            ref="wl-explorer-cpt"
            :data="fileTableData"
            :columns="file_table_columns"
            :current="curInfo"
            @handle-folder="handleFolder"
            @open-folder="getAllFiles"
            @open-file="openFile"
            @upload-success="getAllFiles()"
            @upload-error="getAllFiles()"
            :upload-limit="1"
            @del="fileDel"
            @search="fileSearch"
            :loading="load.update"
            :upload-path="uploadPath"
            :upload-data="uploadData"
            :has-permission="getPermission()"
    >
        <!-- 操作文件夹滑入区 -->
        <el-dialog
                :title="(folder_form.id ? '编辑' : '新增') + (folder_form.file?'文件':'文件夹')"
                :visible.sync="fade.folder"
                :close-on-click-modal="false"
                width="30%"
        >
            <!-- 操作文件夹模态框 -->
            <el-form
                    size="medium"
                    ref="folder_form"
                    :model="folder_form"
                    :rules="folder_rules"
                    label-position="left"
                    label-width="auto"
                    class="folder_form rule-form"
                    @keyup.enter.native="submitFolderFrom('folder_form')"
            >
                <el-form-item :label="folder_form.file?'文件名称':'文件夹名称'" prop="fileName">
                    <el-input
                            v-model="folder_form.fileName"
                            maxlength="50"
                            :placeholder="'请输入' + (folder_form.file?'文件名称':'文件夹名称')"
                            :disabled="(!folder_form.file&&folder_form.id)?true:false"
                    ></el-input>
                    <div v-if="!folder_form.file" style="color:#909399;">注意：文件夹名称不能修改。</div>
                </el-form-item>
                <el-form-item label="备注说明" prop="fileDescribe">
                    <el-input
                            :rows="3"
                            type="textarea"
                            maxlength="200"
                            v-model="folder_form.fileDescribe"
                            placeholder="请输入备注说明"
                    ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button
                        type="primary"
                        size="mini"
                        :icon="load.folder?'el-icon-loading':'iconfont icon-baocun'"
                        :disabled="load.folder"
                        @click="submitFolderFrom('folder_form')"
                >保存
                </el-button
                >
                <el-button
                        size="mini"
                        icon="iconfont icon-fanhui"
                        @click="fade.folder = false"
                >返回
                </el-button
                >
            </div>
        </el-dialog>
        <!-- 预览文件弹窗 -->
        <el-dialog
                title="文件预览"
                :visible.sync="fade.file"
                :fullscreen="fileFullScreen"
                class="file"
        >
            <iframe v-if="fade.file" :style="fileFullScreen?'height:82vh':'height:50vh'" name="file_open" :src="filePath"></iframe>
            <template v-slot:title>
                <span class="el-dialog__title">文件预览</span>
                <el-button style="position:absolute;top:21px;right:50px;padding:0;border:0;font-size:12px;line-height:unset;"
                           :icon="fileFullScreen?'el-icon-copy-document':'el-icon-full-screen'" plain
                           @click="fileFullScreen=!fileFullScreen"></el-button>
            </template>
        </el-dialog>
    </wl-explorer>
</div>

<script>

    ms.base = "${basePath}";
    var Explorer = new Vue({
        el: '#explorer',
        data: function() {
            return {
                //权限管理
                hasPermission:{},
                //上传路径
                uploadPath: ms.manager + '/file/upload.do',
                load: {
                    folder: false,
                    update: false
                }, // loading管理
                all_file_data: {
                    files: [],
                    localPath: ''
                },
                //文件预览是否全屏
                fileFullScreen: false,
                //文件预览文件路径
                filePath: '',
                //当前路径id，默认为1
                id: 1,
                fade: {
                    folder: false,
                    file: false
                }, // 弹出类视图管理
                //文件表格列
                file_table_columns: [
                    {
                        label: "名称",
                        minWidth: 120,
                        prop: 'Name',
                        formatter: function(row) {
                            return row.fileName || '-'
                        },
                    },
                    {
                        label: "修改日期",
                        align: "center",
                        width: 160,
                        formatter: function(row) {
                            return row.updateDate || '-';
                        },
                    },
                    {
                        label: "类型",
                        align: "center",
                        width: 160,
                        formatter: function(row) {
                            return row.file ? row.fileType : '文件夹';
                        },
                    },
                    {
                        label: "大小",
                        width: 160,
                        align: "center",
                        formatter: function(row) {
                            let size = Number(row.fileSize)
                            if (size === null || size === undefined || size === '' || isNaN(size)) return "-";
                            if (size < 1024) {
                                // 1024以下显示Byte
                                return size + " B";
                            }
                            if (size < 1024 * 1024) {
                                return (size / 1024).toFixed(2) + ' KB'
                            }
                            if (size < 1024 * 1024 * 1024) {
                                // 1024*1024*1024
                                let _mb = (size / (1024 * 1024)).toFixed(2);
                                return parseFloat(_mb) + " MB";
                            }
                            return '-';
                        },
                    },
                    {
                        label: "创建日期",
                        align: "center",
                        width: 160,
                        formatter: function(row) {
                            return row.createDate || "-";
                        },
                    },
                    {
                        label: "作者",
                        width: 160,
                        align: "center",
                        formatter: function(row) {
                            return row.createBy || "-";
                        },
                    },
                    {
                        label: "描述",
                        minWidth: 160,
                        formatter: function(row) {
                            return row.fileDescribe || "-";
                        },
                    },
                ], // 自定义表格列
                folder_form: {
                    id: null,
                    file: null,
                    folderId: '',
                    fileName: "",
                    fileDescribe: "",
                }, // 文件夹表单
                folder_rules: {
                    fileName: [
                        {required: true, message: "请填写名称", trigger: "blur"},
                    ],
                }, // 文件夹表单验证
                search: '',//搜索词
                previewAddress:'',//浏览地址
                storeAddress:'',//存储地址
            };
        },
        computed: {
            //表格数据
            fileTableData: function () {
                return this.all_file_data.files
            },
            //当前信息
            curInfo: function () {
                let that = this
                if (this.search) {
                    return {
                        label: '搜索关键词:',
                        word: that.search
                    }
                }
                return {
                    label: '当前路径:',
                    id: that.id,
                    path: that.all_file_data.localPath || '/',
                    folderId: that.all_file_data.folderId
                }
            },
            //上传对象 type为上传的方式,例如'uploadQIniuServier' ,不传则默认为本地'uploadLocalServer'
            uploadData: function () {
                return {'isRename': true, 'uploadPath': this.curInfo.path, 'fileFolderId': this.id}
            },
        },
        watch: {
            'fade.folder': function (n) {
                if (!n) {
                    this.$refs["folder_form"].resetFields();
                    this.folder_form.id = this.folder_form.file = null
                }
            }
        },
        methods: {
            getPermission:function(){
                <@shiro.hasPermission name="basic:file:del">
                this.hasPermission.del = true;
                </@shiro.hasPermission>
                <@shiro.hasPermission name="basic:file:update">
                this.hasPermission.update = true;
                </@shiro.hasPermission>
                <@shiro.hasPermission name="basic:file:save">
                this.hasPermission.save = true;
                </@shiro.hasPermission>
                return this.hasPermission;

            },

            /**
             * @name 预览文件操作
             * @param Obiect 文件对象
             */
            openFile: function (file) {
                let that = this
                this.fade.file = true
                this.filePath = that.previewAddress + encodeURIComponent(that.base64Encode(that.storeAddress + file.filePath))

            },
            //转base64
            base64Encode: function (input) {
                var rv;
                rv = encodeURIComponent(input);
                rv = unescape(rv);
                rv = window.btoa(rv);
                return rv;
            },
            /**
             * 根据关键词搜索文件
             * data: 搜索词
             */
            fileSearch: function (data) {
                let that = this
                that.load.update = true
                that.search = data
                if (that.search) {
                    ms.http.get(ms.manager + '/file/query.do', {fileName: that.search}).then(function(res) {
                        if (res.result) {
                            that.all_file_data = res.data
                        } else {
                            this.$notify({
                                title: '失败',
                                message: res.msg || '搜索失败',
                                type: 'error'
                            });
                        }
                        that.load.update = false
                    }).catch(function(err) {
                        that.load.update = false
                    })
                } else {
                    that.getAllFiles()
                }
            },
            /**
             * 编辑文件夹
             * act:Object 当前选中文件夹
             * type:String 操作类型 add添加edit编辑
             */
            handleFolder: function (act, type) {
                var that = this
                // this.path = file;
                this.fade.folder = true;
                this.$nextTick(function() {
                    if (type === "add") {
                        this.folder_form.file = false
                    }
                    if (type === 'edit' && act) {
                        this.folder_form = {
                            file: act.file,
                            id: act.id,
                            fileName: act.fileName,
                            fileDescribe: act.fileDescribe
                        }
                    }
                    this.folder_form.folderId = this.curInfo.id;
                })
                // this.child_act_saved = act;
                // this.folder_form = { ...act };
            },
            // 提交文件夹表单
            submitFolderFrom: function (formName) {
                let that = this
                let url = ''
                this.$refs[formName].validate(function(valid){
                    if (valid) {
                        that.load.folder = true;
                        let res_data = that.folder_form
                        if (res_data.id == '' || !res_data.id) {
                            url = '/file/save.do'
                        } else {
                            url = '/file/update.do'
                        }
                        ms.http.post(ms.manager + url, res_data).then(function(res) {
                            if (res.result) {
                                that.fade.folder = false;
                                that.$notify({
                                    title: '成功',
                                    message: "操作成功",
                                    type: "success",
                                });
                                that.searchOrGetAllFiles()
                            } else {
                                that.$notify({
                                    title: '失败',
                                    message: res.msg || '操作失败',
                                    type: 'error'
                                });
                            }
                            that.load.folder = false;
                        }).catch(function(err) {
                            that.load.folder = false;
                        })
                    } else {
                        return false;
                    }
                });
            },
            // 删除文件
            fileDel: function (data_list) {
                data_list ? '' : data_list = []
                let that = this
                data_list = JSON.stringify(data_list)
                ms.http.post(ms.manager + '/file/delete.do', data_list, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(function(res) {
                    if (res.result) {
                        that.$notify({
                            title: '成功',
                            message: '删除成功！',
                            type: "success",
                        });
                        that.searchOrGetAllFiles()
                    } else {
                        that.$notify({
                            title: '失败',
                            message: res.msg || '删除失败',
                            type: 'error'
                        });
                    }
                })
            },
            //搜索或获取文件
            searchOrGetAllFiles: function () {
                var that = this
                if (that.search) {
                    that.fileSearch(that.search)
                } else {
                    that.getAllFiles()
                }
            },
            // 获取所有文件夹
            getAllFiles: function (data) {
                let that = this
                this.search = null
                let id = null
                that.load.update = true
                if (data) {
                    id = data.id
                } else {
                    id = that.id
                }
                ms.http.get(ms.manager + '/file/list.do', {id:id})
                    .then(function(res) {
                        if (res.result) {
                            that.all_file_data = res.data
                            that.id = id
                        } else {
                            that.$notify({
                                title: '失败',
                                message: res.msg || '查询失败！',
                                type: "success",
                            });
                        }
                        that.load.update = false
                    }).catch(function(err) {
                    that.load.update = false
                })
            },
        },
        created: function () {
            let that = this
            this.getAllFiles();
            //获取存储地址和预览地址
            ms.mdiy.config("存储设置", "storeAddress").then(function (res) {
                if (res.result) {
                    that.storeAddress = res.data
                } else {
                    this.$notify({
                        title: '失败',
                        message: res.msg || '获取存储地址失败',
                        type: 'error'
                    });
                }
                ms.mdiy.config("存储设置", "previewAddress").then(function (res) {
                    if (res.result) {
                        that.previewAddress = res.data
                    } else {
                        this.$notify({
                            title: '失败',
                            message: res.msg || '获取预览地址失败',
                            type: 'error'
                        });
                    }
                })
            })
        },
    })
</script>

<style scoped>


    /* 抵消mcms中app.css样式 */

    .wl-main-list .el-link {
        margin: 0 4px;
    }

    .wl-header-file .el-input-group__append,
    .el-input-group__prepend {
        background-color: #409eff;
        color: white;
        border: #409eff 1px solid;
    }

    .wl-explorer .el-upload,
    .wl-explorer .el-upload-dragger {
        width: 100%;
        height: 200px;
    }

    .wl-explorer .el-button + .el-button {
        margin-left: 10px;
    }

    /* 解决打包后上传列表图表过高 */
    .wl-explorer .el-upload-list__item-name [class^=el-icon] {
        height: unset;
    }

</style>
</body>
</html>
