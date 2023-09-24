<!-- 新建图片表单 -->
<div id='file-form' v-show='isShow' v-cloak>
    <el-dialog :title="'选择'+typeText" :visible.sync="isShow" :close-on-click-modal="false" custom-class='ms-weixin-dialog'>
        <el-form :model="fileForm">
            <el-form-item :label="'上传'+typeText" label-width="100px">
                <el-upload class="new-pic-upload"
                           :on-success="basicPicSuccess"
                           :data="{uploadPath:'/'+weixinId+'/weixin/'+type}"
                           :file-list="picList"
                           :action="ms.manager + '/file/upload.do'"
                           :before-upload="beforePicUpload"
                           :on-exceed="handleExceed"
                           :limit="1"
                           multiple='false'
                           :accept="acceptType"
                           :list-type="type=='picture'?'picture':''">
                    <el-button size="mini" type="primary">本地上传</el-button>
                    <div slot="tip" class="el-upload__tip">
                        <dl v-if="type=='picture'">
                            <dt>注意：1.图片上传仅支持bmp、png、jpeg、jpg、gif格式</dt>
                            <dd>2.同步至微信公众平台的图片最大2M</dd>
                            <dd>3.只支持单文件上传。</dd>
                        </dl>
                        <dl v-else-if="type=='voice'">
                            <dt>注意：1.语音上传仅支持mp3、wma、wav、amr格式</dt>
                            <dd>2.同步至微信公众平台的语音最大20M</dd>
                            <dd>3.只支持单文件上传。</dd>
                        </dl>
                        <dl v-else-if="type=='video'">
                            <dt>注意：1.视频上传仅支持MP4格式</dt>
                            <dd>2.同步至微信公众平台的视频最大200M</dd>
                            <dd>3.只支持单文件上传。</dd>
                        </dl>
                    </div>
                </el-upload>
            </el-form-item>
            <el-form-item label="选择分组" label-width="100px" v-if="type=='picture'">
                <ms-create-group @save-update="saveGroup"></ms-create-group>
                <el-select v-model="selectedOption" placeholder="请选择分组" size='mini'>
                    <el-option v-for="item in picGroup" :key="item.id" :label="item.categoryTitle" :value="item.id">
                    </el-option>
                </el-select>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="isShow = false" size='mini'>取 消</el-button>
            <el-button type="primary" @click="fileSave" size='mini' :loading="saveDisabled">确 定</el-button>
        </div>
    </el-dialog>
</div>
<script>
    var fileUploadDialogVue = new Vue({
        el: '#file-form',
        data: function () {
            return {
                saveDisabled: false,
                isShow: false,//文件弹框是否显示
                fileForm: {
                    fileName: "",
                    fileUrl: "",
                    categoryId: "",
                    fileSize: "",
                    fileType: "",
                },
                picGroup: [],
                selectedOption: '', //被选中的选项
                picList: [], //图片列表
                type: '',//上传类型
                typeText: '',//类型文字
                acceptType: '',//限制文件上传类型
            }
        },
        computed: {
      weixinId: function () {
        return localStorage.getItem('weixinId')
      }
    },
        methods: {
            // 表单打开
            open: function (type) {
                this.type = type
                switch (type) {
                    case 'picture':
                        this.typeText = '图片';
                        this.acceptType = '.bpm,.png,.jpeg,.gif,.jpg';
                        break;
                    case 'voice':
                        this.typeText = '语音'
                        this.acceptType = '.mp3,.wma,.wav,.amr'
                        break;
                    case 'video':
                        this.typeText = '视频'
                        this.acceptType = '.mp4'
                        break;
                }
                if (type) {
                    this.list();
                }
                this.isShow = true;
            },
            // 图片刚加载完
            beforePicUpload: function (file) {
                this.fileForm.fileName = file.name;
                this.fileForm.fileSize = file.size;
                //如果不是图片类型需要赋值类型
                switch (this.type) {
                    case 'picture':
                        this.fileForm.fileType = "image";
                        //图片限制大小为2M
                        if (file.size > 1024 * 1024 * 2) {
                            this.$notify({
                                title: '提示',
                                message: "图片超出大小，上传图片最大2M",
                                type: 'warning'
                            });
                            return false;
                        }
                        break;
                    case 'voice':
                        this.fileForm.fileType = "voice";
                        //音频限制大小为2M
                        if (file.size > 1024 * 1024 * 2) {
                            this.$notify({
                                title: '提示',
                                message: "语音超出大小，上传语音最大2M",
                                type: 'warning'
                            });
                            return false;
                        }
                        break;
                    case 'video':
                        this.fileForm.fileType = "video";
                        //视频限制大小为10M
                        if (file.size > 1024 * 1024 * 10) {
                            this.$notify({
                                title: '提示',
                                message: "视频超出大小，上传视频最大10M",
                                type: 'warning'
                            });
                            return false;
                        }
                        break;
                }

            },

            handleExceed: function (files, fileList) {
                this.$notify({
                    title:'提示',
                    message: "只能选择上传 1个文件,你共选择了" + (files.length + fileList.length) + "个文件！",
                    type: 'warning'
                });

            },
            //   图片上传成功函数
            basicPicSuccess: function (response) {
                if (response.result) {
                    this.fileForm.fileUrl = response.data;
                } else {
                    this.$notify({
                        title: "提示",
                        message: response.msg,
                        type: 'warning'
                    });
                }
            },
            list: function () {
                var that = this;
                var data = {
                    weixinId: that.weixinId
                }
                ms.http.get(ms.manager + "/mweixin/category/list.do", data).then(function (res) {
                    that.picGroup = res.data.rows;
                    if (that.picGroup != null && that.picGroup.length > 0) {
                        that.selectedOption = that.picGroup[0].id;
                    }
                }, function (err) {
                    that.$notify({
                        title: '错误',
                        type: 'err',
                        message: err
                    });
                })
            },
            fileSave: function () {

                var that = this;
                if(!this.fileForm.fileUrl){
                    that.$notify({
                        title: '提示',
                        type: 'warning',
                        message: "文件不能为空"
                    });
                    return
                }
                if (this.type == "picture") {
                    this.fileForm.categoryId = that.selectedOption;
                }
                that.saveDisabled = true; //判断
                ms.http.post(ms.manager + "/mweixin/file/save.do", that.fileForm).then(function (res) {
                    if (res.result) {
                        that.isShow = false
                        that.selectedOption = "";
                        that.picList = [];
                        that.saveDisabled = false;
                        switch (that.type) {
                            case 'picture':
                                pictureVue.picList();
                                pictureVue.categoryList();
                                break;
                            case 'voice':
                                voiceVue.voiceInitData();
                                break;
                            case 'video':
                                videoVue.videoInitData();
                                break;
                        }
                    }else {
                        that.saveDisabled = false;
                        that.$notify({
                            title: '错误',
                            type: 'error',
                            message: res.msg
                        });
                    }
                }, function (err) {
                    that.$notify({
                        title: '错误',
                        type: 'err',
                        message: err
                    });
                    that.saveDisabled = false;
                })
            },
            saveGroup: function (groupForm) {
                var that = this;
                // 校验成功
                ms.http.post(ms.manager + "/mweixin/category/save.do", {
                    categoryTitle: groupForm.categoryTitle,
                }).then(function (res) {
                    that.list();
                    pictureVue.categoryList();
                }, function (err) {
                    that.$notify({
                        title: '错误',
                        type: 'err',
                        message: err
                    });
                })
            },
        },
        mounted: function () {
            var that = this;

        }
    })
</script>
<style>
    #file-form dl {
        margin: 0;
        display: flex;
        flex-flow: column nowrap;
    }

    #file-form dl > dd {
        margin-top: 7px;
    }

    #file-form dd,
    #file-form dt {
        line-height: 1;
        font-weight: initial;
        font-size: 12px;
        color: #999;
    }

    #file-form dt {
        display: inline-block;
        margin-bottom: 0px;
        margin-left: 4px;
    }

    #file-form .el-dialog {
        width: 600px;
    }


</style>
