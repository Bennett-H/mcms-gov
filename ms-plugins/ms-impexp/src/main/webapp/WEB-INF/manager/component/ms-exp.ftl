<#--

-->
<script type="text/x-template" id="ms-exp">
    <el-button ref="export" type="primary" icon="el-icon-download" :disabled="disabled" size="mini" @click="exp">导出{{buttonText}}
    </el-button>
</script>
<script>
    (function () {
        var props = Object.assign({
            // 配置名称
            id: String,
            // 导出类型
            type: String,
            // 用于传递导出sql配置的参数
            ids: Array
        }, Vue.options.components.ElButton.options.props)
        Vue.component('ms-exp', {
            template: '#ms-exp',
            props: props,
            data: function () {
                return {
                    url: ms.manager + "/impexp/excel/exp.do",
                }
            },
            computed: {
                buttonText: function () {
                    if (this.type == "excel") {
                        return "excel"
                    } else if (this.type == "word") {
                        this.url = ms.manager + "/impexp/word/exp.do";
                        return "word"
                    } else {
                        return ""
                    }
                },
            },
            methods: {
                exp: function () {
                    debugger
                    if (this.id==''){
                        this.$notify({
                            type: 'warning',
                            title: '提示',
                            message: '请输入配置名称'
                        });
                        return
                    }
                    var that = this;
                    if (this.ids.length > 0) {
                        this.disabled = true;
                        ms.http.download(this.url,{
                            name: that.id, ids: that.ids.toString()
                        }).then((res)=> {
                            that.disabled = true;
                            this.$notify({
                                type: 'success',
                                title: '提示',
                                message: '导出数据成功'
                            });
                            that.disabled = false;
                        })

                    } else {
                        this.$notify({
                            type: 'warning',
                            title: '提示',
                            message: '请选择需要导出的数据'
                        });
                    }
                },
            }
        });
    })()
</script>



