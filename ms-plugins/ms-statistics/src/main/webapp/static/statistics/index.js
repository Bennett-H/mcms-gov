/**
 * 自定义模块前端工具
 */
(function () {


    if (typeof ms != "object") {
        window.ms = {};
    }

    /**
     * 获取统计数据,可以一次性查出多个统计数据，
     * @param params [{"ssName": "统计名称1", "params": ["参数1","参数1"]},{"ssName": "统计名称1", "params": ["参数1","参数1"]}]
     */
    window.ms.statistics = function (params) {
        // SELECT count(0) as 'num' FROM cms_content where category_id={}
       return ms.http.post(ms.base + "/statistics/sql/list.do", params,
            {headers: {'Content-Type': 'application/json'}});
    };
}());
