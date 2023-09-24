/**
 * 自定义模块前端工具
 */
(function () {


    let URLS = {
        "form": {
            "get": "/qa/qa/get.do",
            formURL: {
                update: {
                    "url": "/qa/qaData/update.do",
                    "params": {}
                },
                save: {
                    "url": "/qa/qaData/save.do",
                    "params": {}
                },
                get: {
                    "url": "/qa/qaData/getData.do",
                    "params": {}
                }
            },
        },
        "model": {
            "get": "/qa/model/get.do",
            formURL: {
                update: {
                    "url": "/qa/model/data/update.do",
                    "params": {}
                },
                save: {
                    "url": "/qa/model/data/save.do",
                    "params": {}
                },
                get: {
                    "url": "/qa/model/data.do",
                    "params": {}
                }
            },
        }
    };
    
    var model = {

        /**
         * 业务表单模型
         * @param renderDomId 绑定的domid,表单会显示在dom里面
         * @param model 模型对象 示例：业务调用{qaName:模型名称}，自动业务使用{id:模型编号}
         * @param params 请求参数 示例：{参数名称:值}
         * @param isSystem true 后台调用会拼接后台地址 false前台调用
         * @returns {Promise<custom_model>}
         */
        form:  function(renderDomId, model, params, isSystem) {
            URLS.form.formURL.get.params = params;
            var urlStrs = JSON.stringify(URLS.form.formURL);
            return this.render(renderDomId, model, JSON.parse(urlStrs), URLS["form"].get, isSystem);
        },

        /**
         * 渲染自定义模型对象
         * @param renderDomId 绑定的domid,表单会显示在dom里面
         * @param model 模型对象 示例：业务调用{qaName:模型名称}，自动业务使用{id:模型编号}
         * @param url 模型获取地址
         * @param isSystem true 后台调用会拼接后台地址 false前台调用
         * @returns {Promise<custom_model>} 返回model对象
         */
        render:  function(renderDomId, model, formUrls, url, isSystem) {
            // if(model.qaName!=undefined || model.modelId!=undefined){
            //     return null;
            // }
            //查模型

            if (isSystem) {
                url = ms.manager + url;
                formUrls.update.url = ms.manager + formUrls.update.url;
                formUrls.save.url = ms.manager + formUrls.save.url;
                formUrls.get.url = ms.manager + formUrls.get.url;
            }
            var modelId = 0;
            var qaName = "";

            return new Promise(function(resolve,reject) {

                ms.http.get(url, model).then(function (res) {
                    if (res.result && res.data) {

                        form.qaModel = res.data;
                        modelId = res.data.id;
                        qaName = res.data.qaName;
                        var data = JSON.parse(form.qaModel.modelJson);
                        form.$nextTick(function () {

                            var modelDom = document.getElementById(renderDomId);
                            modelDom.innerHTML="";
                            var modelDom = document.getElementById(renderDomId);
                            var scriptDom = document.createElement('script');
                            scriptDom.innerHTML = data.script;
                            var divDom = document.createElement('div');
                            divDom.id = 'custom-model';
                            divDom.innerHTML = data.html;
                            modelDom.appendChild(scriptDom);
                            modelDom.appendChild(divDom); //初始化自定义模型并传入关联参数
                            //  promise抛出resolve进行外部调用自定义模型
                            resolve(new custom_model({
                                data: {
                                    qaName: qaName,
                                    modelId: modelId,
                                    formURL: formUrls
                                }
                            }))
                        });

                    }
                });
            })
        }
    }

    var qa = {
        model: model
    }


    if (typeof qa != "object") {
        window.qa = {};
    }
    window.qa = qa;
    window.qa.debug = false
}());
