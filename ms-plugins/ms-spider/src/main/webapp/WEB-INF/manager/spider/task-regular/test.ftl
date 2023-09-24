<el-dialog id="form" v-loading="loading" :close-on-click-modal="false" v-cloak title="采集测试" :visible.sync="dialogVisible" width="80%" @close="closeDialog()">
            <el-form ref="form" :model="form" label-width="120px" label-position="right" size="small">

                <el-form-item label="页面地址" prop="linkUrl">
                    <el-input type="textarea" v-model="form.linkUrl" disabled></el-input>
                    <div class="ms-form-tip">
                        匹配到的列表页地址
                    </div>
                </el-form-item>

                <el-form-item label="内容地址" prop="contentUrl">
                    <el-input type="textarea" v-model="form.contentUrl" disabled></el-input>
                    <div class="ms-form-tip">
                        匹配到的内容页地址
                    </div>
                </el-form-item>

                <el-form-item label="采集数据">
                    <el-table tooltip-effect="dark" ref="multipleTable" border
                              height="250"
                              :data="dataList"
                              style="width: 100%">
                        <el-table-column v-for="(t,index) in table" :key="index"
                            :show-overflow-tooltip="true"
                            :prop="t.filed"
                            :label="t.name" >
                        </el-table-column>
                        <el-table-column label="信息" prop="msg" :show-overflow-tooltip="true">
                        </el-table-column>
                    </el-table>
                    <div class="ms-form-tip">
                        匹配到数据会在日志表中,出现无日志数据的情况请检查匹配规则是否正常
                    </div>
                </el-form-item>
            </el-form>
        <div slot="footer">
            <el-button size="mini" @click="dialogVisible = false">取 消</el-button>
            <el-button size="mini" type="primary" @click="startTest()" :loading="startDisabled">启动</el-button>
            <el-button size="mini" type="primary" @click="stopTest()" :disabled="stopDisabled">停止</el-button>
        </div>
</el-dialog>
<script>
        var form = new Vue({
        el: '#form',
        data:function() {
            return {
                user:'',
                regular:null,
                stompClient:null,
                loading:false,
                startDisabled: false,
                stopDisabled: true,
                dialogVisible:false,
                //表单数据
                form: {
                    linkUrl:'',
                    contentUrl:'',
                },
                dataList:[],
                table:null,
            }
        },
        watch:{
            dialogVisible:function (v) {
                if(!v){
                    this.$refs.form.resetFields();
					 this.form.id =0
                }
            }
        },
        computed:{
        },
        methods: {
            closeDialog(){
                this.stopTest();
            },
            appendLinkUrl(url){
                this.form.linkUrl = this.form.linkUrl +  url + '\r\n'
            },
            appendContentUrl(url){
                this.form.contentUrl = this.form.contentUrl +  url + '\r\n'
            },
            appendData(data){
                this.dataList.push(data);
            },
            //完成爬取需要修改状态
            finish(){
                this.startDisabled= false;
                this.stopDisabled = !this.startDisabled;
            },

            startTest(){
                this.clearData();
                this.startDisabled = true;
                this.stopDisabled = !this.startDisabled;
                var body = {"action":"1","data":JSON.stringify(this.regular),"user":this.user};
                body = JSON.stringify(body);
                this.stompClient.send("/app/spider/test",{"Content-Type":"application/json"},body)
            },
            stopTest(){
                this.startDisabled= false;
                this.stopDisabled = !this.startDisabled;
                var body = {"action":"2","user":this.user};
                body = JSON.stringify(body);
                this.stompClient.send("/app/spider/test",{"Content-Type":"application/json"},body)
            },

            open:function(row,stompClient,user){
                var that = this;
                //清空数据
                that.clearData();
                that.user = user;
                that.regular = row;
                that.table = JSON.parse(row.metaData);
                that.stompClient = stompClient;
                that.$nextTick(function () {
                    that.dialogVisible = true;
                })
            },
            clearData(){
                this.dataList = [];
                this.form.linkUrl = '';
                this.form.contentUrl = '';
            },
        },
        created:function() {

        },
    });
</script>
