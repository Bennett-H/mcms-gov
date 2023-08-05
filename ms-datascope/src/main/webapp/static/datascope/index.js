/**
 * 数据权限
 */
(function () {

    /**
     * 判断是否拥有数据(dataId)管理权限，如果dataTargetId与dataId条件成立，表示具备权限，例如：在一个数据列表中只展示具备权限的数据
     * 还有存在data_id_model的权限控制，这个权限控制可以实现一下按钮、表单控件的权限。例如是否具备某一个新闻的发布时间编辑权限
     * @param dataType       数据业务类型(必填) 具体以 data_type定义为准,
     * @param dataId         关联权限数据id (必填) 具体以 data_id定义为准,
     * @param hasModels      多个权限以,分隔(非必填)，存在任意一个表示条件成立，具体以 data_id_model定义为准,
     * @param dataTargetId   默认为当前登陆用户角色id(不传递值)，可以根据业务需求来定义，具体以data_target_id字段定义为准
     * @param isSuper   true 默认检测超级管理，如果登陆用户为超级管理员，接口直接返回 super，如果
     * @param returnType     model（只返回data_id_model里面的modelUrl数组) all返回当前所有的数据列
     * @returns
     * 补充三种返回格式
     * 1、如果开启了isSuper且当前用户为超级管理员，则返回{data:'super'}
     * 2. returnType设置为model字段情况返回示例：{true or false}
     * 3. returnType未配置的情况下返回数据示例：
     * {
     dataId: "3"
     dataIdModel: "[{"modelTitle":"自定义排序","modelUrl":"cms:content:contentSort","check":true},{"modelTitle":"部门","modelUrl":"cms:content:department","check":true},{"modelTitle":"发布时间","modelUrl":"cms:content:contentDate","check":true},{"modelTitle":"错词检测","modelUrl":"cms:content:wordFilter"}]"
     dataTargetId: "All"
     dataType: "approvalConfigOfAll"
     }
     */
    function datascope(params) {

        //  相关配置项,未配置则抛出为空的错误
        if (!params.dataType) {
            console.info("数据权限的 dataType 为空")
            return new Promise(function (resolve, reject) {
                resolve(false);
            });
        }
        if (!params.dataId) {
            console.info("数据权限的 dataId 为空")
            return new Promise(function (resolve, reject) {
                resolve(false);
            });
        }
        // console.log(params);
        //  重新组合接口需要参数
        var paramsPack = {
            dataId: params.dataId,
            dataType: params.dataType,
            dataTargetId: params.dataTargetId,
            isSuper: !params.isSuper?true:params.isSuper,
            hasModels: params.hasModels
        }

        return new Promise(function (resolve, reject) {

            ms.http.get(ms.manager + "/datascope/data/getModel.do", paramsPack).then(function(res){
                if (res.result && res.data) {
                    //  返回res。data为supper时，为超管，拥有所有权限
                    if (res.data == 'super') {
                        resolve(true)
                        return
                    }
                    var models = []
                    //  hasModel 设置是否有匹配的状态
                    var hasModel = false
                    //  解析出dataIdmodel数组字符串
                    res.data.dataIdModel = JSON.parse(res.data.dataIdModel)
                    //  设置默认数据格式
                    if (res.data.dataIdModel == null) {
                        res.data.dataIdModel = []
                    }
                    //  循环提取modelUrl数据
                    res.data.dataIdModel.forEach(function (item) {
                        if (item.check) {
                            models.push(item.modelUrl)
                        }
                    })
                    if (params.returnType == 'model') { //只返回dataIdModel的modelUrl
                        //  ，切割字符串形式的hasmodels，循环权限数组中是否有models的业务
                        params.hasModels.split(',').forEach(function (item) {
                            //  判断hasModels的业务是否在业务表中
                            if (models.includes(item.trim())) {
                                hasModel = true
                                return
                            }
                        })
                        //抛出处理完成的结果数据
                        resolve(hasModel)
                    } else { //直接返回接口的返回的数据（完整数据）
                        resolve(models)
                    }
                } else if (res.result && !res.data) {
                    resolve([]);
                } else {
                    resolve(false);
                }

            })
        });
    }

    if (typeof ms != "object") {
        window.ms = {};
    }
    window.ms.datascope = datascope;
}());


/**
 * 通过指令方式实现对界面上控件的禁用与隐藏权限控制
 * @param dataType        数据业务类型(必填) 具体以 data_type定义为准,
 * @param dataId         关联权限数据id (必填) 具体以 data_id定义为准,
 * @param hasModels      多个权限,分隔(必填)，存在任意一个表示条件成立，具体以 data_id_model定义为准,
 * @param dataTargetId   拥有者(必填)，可以根据业务需求来定义，具体以data_target_id字段定义为准
 * @param isSuper        true 检测超级管理，如果登陆用户为超级管理员，接口直接返回 super
 * @returns 条件成立会直接移除dom
 */

// 自定义指令-控制

Vue.directive('ms-datascope', {
    /** 参数解析
     * 加载业务的权限数据
     * dom：指令所绑定的元素，可以用来直接操作 DOM。
     * binding：一个对象，包含以下 property：
     * vnode：Vue 编译生成的虚拟节点。移步 VNode API 来了解更多详情。
     * oldVnode：上一个虚拟节点，仅在 update 和 componentUpdated 钩子中可用。
     */

    //  只调用一次，指令第一次绑定到元素时调用。
    bind: function (dom, binding, vnode, oldVnode) {

    },
    // 当被绑定的元素插入到 DOM 中时……
    // 用户列表场景，少数据可以使用，推荐使用js业务代码控制
    inserted: function (dom, binding, vnode, oldVnode) {
        // console.log('inserted' + binding.value.hasModels + ":" + binding.value.dataId);
        if (binding.value.dataId !== undefined && binding.value.dataId !='' && binding.value.hasModels !== undefined) {
            //  dataId 与session会话的值相同的情况下，不进入函数，防止无意义的循环
            binding.value.returnType = 'model'
            ms.datascope(Object.assign({}, binding.value)).then(function (hasPermission) {
                hasPermission ? null : dom.parentNode && dom.parentNode.removeChild(dom)
            })
        }
    },
    //  所在组件的 VNode 更新时调用，但是可能发生在其子 VNode 更新之前
    update: function (dom, binding, vnode, oldVnode) {
        //  值为初始化变量时，直接退出函数，减少不必要的代码循环
        if (binding.value.dataId !== undefined && binding.value.dataId !='' && binding.value.hasModels !== undefined) {
            if (binding.oldValue.dataId === binding.value.dataId && binding.oldValue.hasModels === binding.value.hasModels && binding.oldValue.dataType === binding.value.dataType) {
                return;
            }
            //  dataId 与session会话的值相同的情况下，不进入函数，防止无意义的循环
            binding.value.returnType = 'model'

            ms.datascope(Object.assign({}, binding.value)).then(function (hasPermission) {
                hasPermission ? null : dom.parentNode && dom.parentNode.removeChild(dom)
            })
        }

    },
    //  指令所在组件的 VNode 及其子 VNode 全部更新后调用
    componentUpdated: function (dom, binding, vnode, oldVnode) {

    },
    //  只调用一次，指令与元素解绑时调用
    unbind: function (dom, binding, vnode, oldVnode) {
    }
})
