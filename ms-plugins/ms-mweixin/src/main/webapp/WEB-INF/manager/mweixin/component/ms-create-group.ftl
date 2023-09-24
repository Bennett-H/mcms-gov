<!-- 创建修改图片分组组件 -->
<script type="text/x-template" id='create-group'>
        <el-popover placement="top-start" width="350" trigger="click" v-model='popoverShow'>
            <el-form label-width="100px" :model="groupForm" ref="groupForm" :rules='groupRule'>
                <el-form-item :label="tips" prop='categoryTitle'>
                    <el-input v-model="groupForm.categoryTitle" size='mini' clearable></el-input>
                </el-form-item>
                <div class="dialog-footer" style="float:right">
                    <el-button @click="cancel" size='mini'>取消</el-button>
                    <@shiro.hasPermission name="picture:save,picture:update">
                        <el-button type="primary" @click="save" size='mini'>确定</el-button>
                    </@shiro.hasPermission>
                </div>
            </el-form>
            <i v-if="edit" class="el-icon-edit" slot="reference" size='mini'></i>
            <el-button v-if="!edit" slot="reference" icon='el-icon-plus' size='mini'>{{tips}}</el-button>
        </el-popover>
</script>
<script>
    Vue.component('ms-create-group', {
        template: '#create-group',
        props: {
            tips:{
                require: false,
                default: '添加分组'
            },
            edit:{//是否是修改模式
                require: false,
                default: false
            },
            id:{//分组id
                require: false,
                default: 0
            },
            categoryTitle:{//分组名
                require: false,
                default: ''
            }
        },
        data:function() {
            return {
                groupForm:{
                    categoryTitle:''
                },
                // 添加分组验证规则
                groupRule: {
                    categoryTitle: [{
                        required: true,
                        message: '请输入分组名称',
                        trigger: ['blur', 'change']
                    },
                        {
                            min: 1,
                            max: 5,
                            message: '长度在 1 到 5 个字符',
                            trigger: ['blur', 'change']
                        }
                    ]
                },
                popoverShow: false,
            }
        },
        watch:{

        },
        methods: {
            save:function (){//点击确定先进行表单验证之后回调父页面的保存方法
                var that = this;
                this.$refs.groupForm.validate(function (pass, object) {
                    if (pass) {
                        that.groupForm.id = that.id
                        that.$emit('save-update', that.groupForm)
                        that.popoverShow = false
                        that.$refs.groupForm.resetFields()
                    }
                })
            
            },
            cancel: function () { //点击取消关闭
                this.popoverShow = false
                this.$refs.groupForm.resetFields()
                if(this.edit){
                    this.groupForm.categoryTitle = this.categoryTitle;
                }
            }
        },
        mounted: function () {//初始赋值
           this.groupForm.categoryTitle = this.categoryTitle;
        }
    })
</script>
<style>

</style>