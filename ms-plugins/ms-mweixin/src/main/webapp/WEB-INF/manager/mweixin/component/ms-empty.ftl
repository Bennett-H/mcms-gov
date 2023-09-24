<!-- 暂无数据组件 -->
<script type="text/x-template" id='empty'>
    <div :class="['ms-empty',size == 'large'?'ms-empty-large':'ms-empty-small']">
        <i class="iconfont icon-zanwujilu"></i>
        <p>暂无{{msg}}，<a href="#" @click.self="click()">请添加{{msg}}</a><p>
    </div>
</script>
<script>
    Vue.component('ms-empty', {
        template: '#empty',
        props: {
            size: {
                require: false,
                default: 'large',
            },
            msg: {
                require: false,
                default: '',
            },
            click: {
                require: false,
                default: '',
            },
        },
        data:function() {
            return {

            }
        },
        methods: {

        }
    })
</script>
<style>
    /* 暂无数据样式 */
    /* 设置居中 */
    .ms-empty {
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        z-index: 10;
    }
    /* 设置大列表样式 */
    .ms-empty-large i{
        font-size: 90px;
        margin-left: 27px;
        color: #999;
    }
    /* 设置大列表字体样式 */
    .ms-empty-large p {
        font-size: 14px;
        color: #999;
    }
    .ms-empty-large p a{
        font-size: 16px;
        color: #0099ff;
    }
    /* 设置小列表样式 */
    .ms-empty-small i{
        font-size: 60px;
        margin-left: 42px;
        color: #999;
    }
    /* 设置小列表字体样式 */
    .ms-empty-small p{
        color: #999;
        font-size: 14px;
    }
    .ms-empty-small p a{
        font-size: 16px;
        color: #0099ff;
    }
</style>