<script type="text/x-template" id="ms-select-style">
    <div>
        <template  v-for='(item,index) in app.appStyles'>
            <el-row v-if="(selectedCategoryStyle.length == 0 || selectedCategoryStyle.indexOf(item.template)!=-1)">
                <el-col span="12">
                    <el-form-item :label="item.name+'列表模版'" v-if="categoryType == '1'">
                        <el-select v-model="categoryListUrls[index].file"
                                   @visible-change="queryTemplateFileForColumn(item.template)"
                                   :value="item.template"
                                   :style="{width: '100%'}"
                                   :filterable="true"
                                   :disabled="false"
                                   :multiple="false" :clearable="true"
                                   :placeholder="'请选择'+item.name+'列表模版'" :key="item">
                            <el-option v-for='(htm,i) in templateFiles' :key="htm" :value="htm"
                                       :label="htm"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col span="12">
                    <el-form-item :label="item.name+'内容模版'" v-if="categoryType != '3'">
                        <el-select v-model="categoryUrls[index].file"
                                   @visible-change="queryTemplateFileForColumn(item.template)"
                                   :value="item.template"
                                   :style="{width: '100%'}"
                                   :filterable="true"
                                   :disabled="false"
                                   :multiple="false" :clearable="true"
                                   :placeholder="'请选择'+item.name+'内容模版'" :key="item">
                            <el-option v-for='(htm,i) in templateFiles' :key="htm" :value="htm"
                                       :label="htm"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
        </template>
    </div>
</script>
<script>
    (function () {
        var props = Object.assign({

                categoryType: {
                    type: Number,
                    default: function () {
                        return 0;
                    }
                },
                categoryUrls: {
                    type: Array,
                    default: function () {
                        return [];
                    }
                },
                categoryListUrls: {
                    type: Array,
                    default: function () {
                        return [];
                    }
                },
                //发布到选中的站点
                selectedCategoryStyle: {
                    type: Array,
                    default: function () {
                        return [];
                    }
                },
                leaf: { //用来区分是否是子节点
                    type: Boolean,
                    default: function () {
                        return true;
                    }
                },
            },
            Vue.options.components.ElSelect.options.props
        )
        Vue.component('ms-select-style', {
            template: '#ms-select-style',
            props: props,
            data: function () {
                return {
                    app: {appStyles: ""},
                    templateFiles: [],//模版文件列表
                    dictTemplates: [],  //风格定义列表，数据来自字典
                    appTemplates: [],//当前站点模版文件夹下所有的模版
                    oldCategoryListUrls: [],
                    oldCategoryUrls: [],
                    id: '',
                }
            },
            watch: {

                selectedCategoryStyle: function (n, o) {
                    debugger
                    if (n) {
                        //发布到没选中的元素模板清空
                        this.categoryListUrls.forEach(function (item) {
                            if (!n.includes(item.template)) {
                                item.file = ''
                            }
                        })
                        this.categoryUrls.forEach(function (item) {
                            if (!n.includes(item.template)) {
                                item.file = ''
                            }
                        })
                    }
                }
            },
            methods: {
                //获取当前应用表
                getApp: function () {
                    var that = this;
                    ms.http.get(ms.manager + "/basic/app/get.do", {}).then(function (res) {
                        if (res.result && res.data) {
                            that.app = res.data;

                            that.app.appStyles = JSON.parse(that.app.appStyles);

                            //检测当前站点是否绑定模版
                            var hasTemplate = false;
                            that.app.appStyles.forEach(function (app, index) {
                                if(app.template!="") {
                                    hasTemplate = true;
                                }
                            });
                            if(!hasTemplate) {
                                that.$notify({
                                    title: '提示',
                                    message: "当前站点没有绑定模版",
                                    type: 'warning'
                                });

                            }

                            //如果站点风格有增加或或者删除，就需要对栏目绑定对皮肤进行清理操作
                            //例如：应用设置绑定了内网、外网。后面有增加了其他风格或者删除对外网风格，这个时候栏目绑定对模版数据还是原来对风格数据
                            //如果不处理会导致栏目的模版json长度与应用设置里面绑定的风格长度不一致
                            //临时存储
                            var _categoryListUrls = that.categoryListUrls; //列表模版
                            var _categoryUrls = that.categoryUrls; //内容模版
                            //清空
                            that.categoryListUrls = [];
                            that.categoryUrls = [];

                            //that.app.appStyles 应用设置对皮肤为标准，如果增加了皮肤必须在应用设置里面保存更新一下
                            that.app.appStyles.forEach(function(item) {
                                var listItem = JSON.parse(JSON.stringify(item));
                                var urlItem = JSON.parse(JSON.stringify(item))
                                //item.name 指 内网、外网，具体是字典的名称
                                var listUrl = that.getByStyle(_categoryListUrls,item.name);
                                //如果不存在表示新增了皮肤风格
                                if(listUrl == null) {
                                    listItem.file = "";
                                    that.categoryListUrls.push(listItem); //组织新的皮肤json，
                                } else {
                                    listItem.file = listUrl.file;
                                    that.categoryListUrls.push(listItem) //还原老的数据
                                }

                                //内容模版也需要与上面的列表模版一样处理 ，
                                var url = that.getByStyle(_categoryUrls,item.name);
                                if(url == null) {
                                    urlItem.file = "";
                                    that.categoryUrls.push(urlItem);
                                } else {
                                    urlItem.file = url.file;
                                    that.categoryUrls.push(urlItem)
                                }

                            })

                            that.$emit('update:categoryListUrls', that.categoryListUrls);
                            that.$emit('update:categoryUrls', that.categoryUrls);
                        }
                    })
                },

                /**
                 * 获取已经绑定栏目皮肤，
                 */
                getByStyle: function(arr,s) {
                    var has = null;
                    arr.forEach(function(item){
                        if(item.name == s) {
                            has = item;
                        }
                    })
                    return has;
                },


                /**
                 * 获取当前站点下所有的模版文件夹
                 * @returns {[]}
                 */
                queryAppTemplateSkin: function () {
                    var that = this;
                    ms.http.get(ms.manager + '/basic/template/queryAppTemplateSkin.do').then(function (data) {
                        that.appTemplates = data.data.appTemplates;
                    });
                },

                /**
                 * 获取字典对应的 '模板类型' 数据
                 * @returns {[]}
                 */
                queryDictForTemplate: function () {
                    var that = this;
                    ms.http.get(ms.base + '/mdiy/dict/list.do', {
                        dictType: '模板类型'
                    }).then(function (res) {
                        if (res.result) {

                            res = res.data;
                            that.dictTemplates = res.rows;
                            that.queryAppTemplateSkin();
                            that.getApp();
                        }
                    });
                },


                //获取当前应用设置绑定的皮肤
                queryTemplateFileForColumn: function (appStyle) {
                    var that = this;
                    templateFiles = [];
                    ms.http.get(ms.manager + "/basic/template/queryTemplateFileForColumn.do", {"appStyle": appStyle}).then(function (data) {
                        that.templateFiles = data.data;
                    })
                },
            },
            created: function () {

            }
        });

    })()
</script>



