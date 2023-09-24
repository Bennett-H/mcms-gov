<!--切换站点-->
<script type="text/x-template" id="ms-site-header">
    <el-dropdown v-if="siteEnable" class="app-point" trigger="hover" style="margin-right: 8px" placement="top-start" @visible-change="">
        <span style="cursor: pointer">当前站点：${app.appName}</span>
        <i class="el-icon-arrow-down"></i>
        <el-dropdown-menu class="ms-admin-login-down" slot="dropdown" @click.native='changeApp'>
            <el-dropdown-item v-for="item in appList" :id="item.id"
                              v-text="item.appName"></el-dropdown-item>
        </el-dropdown-menu>
    </el-dropdown>
</script>
<script type="text/javascript">
    Vue.component('ms-site-header', {
        template: '#ms-site-header',
        data: function () {
            return {
                indexApp: "",
                appList: [],
                siteEnable:false //是否开启站群

            }
        },
        watch:{

        },
        computed: {

        },
        methods: {
            getSiteEnable:function() {
                var that = this
                ms.mdiy.config("站群配置", "siteEnable").then(function (res) {
                    if (JSON.parse(res.data)) {
                        that.siteEnable = true;
                    }
                });
            },
            listOnwerApp:function() {
                var that = this;
                ms.http.get(ms.manager + "/site/listOwnerApp.do").then(function (data) {
                    that.appList = data.data
                    var stotage = sessionStorage.getItem("appName")
                    if (stotage == null || stotage == "") {
                        that.indexApp = that.appList[0].appName;
                    } else {
                        that.indexApp = stotage
                    }
                }, function (err) {
                    that.$message.error(err);
                });
            },
            changeApp: function () {
                var appName = event.target.innerText;
                var appId = event.target.id;
                var that = this;
                ms.http.get(ms.manager + "/site/jump.do", {
                    appId: appId
                }).then(function (res) {
                    if (res.result) {
                        sessionStorage.setItem("appName", res.data.appName);
                        //window.location.href = ${base} + ms.manager + "/index.do";
                        location.reload();
                    }else {
                        that.$notify({
                            title: '失败',
                            message: res.msg,
                            type: 'warning',
                            duration: 1000,
                        });
                    }
                }, function (err) {
                    that.$message.error(err);
                });
            },
        },
        created:function () {
            this.listOnwerApp();
            this.getSiteEnable();
        }
    })
</script>
