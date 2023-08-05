/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.gov.action.web;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.elasticsearch.service.IESService;
import net.mingsoft.gov.bean.ESContentBean;
import net.mingsoft.elasticsearch.bean.ESSearchBean;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

/**
 * es搜索接口
 *
 * @author 铭飞开发团队
 * 创建日期：2020/12/29 9:53<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-政务版接口"})
@Controller("govWebESSearchAction")
@ConditionalOnExpression("${spring.data.elasticsearch.repositories.enable:true}")
@RequestMapping("/cms/es/search")
public class ESSearchAction extends BaseAction {


    @Autowired
    private IAppBiz appBiz;

    @Autowired
    private IESService esService;

    /**
     * 高亮搜索，保留搜索字段keyword、order、orderBy、pageNo、pageSize，其余字段根据实际业务调整
     *
     * @param keyword 关键字
     * @return 内容
     */
    @ApiOperation(value = "高亮搜索接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序方式", required = false, paramType = "query"),
            @ApiImplicitParam(name = "orderBy", value = "排序的字段", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页数", required = false, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "页数大小", required = false, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "styles", value = "皮肤", required = false, paramType = "query"),
            @ApiImplicitParam(name = "typeId", value = "所属栏目(用于搜索指定栏目)", required = false, paramType = "query"),
            @ApiImplicitParam(name = "parentIds", value = "父栏目ID(用于搜索所有子栏目)", required = false, paramType = "query"),
            @ApiImplicitParam(name = "fields", value = "搜索关键字字段,值输入ES索引字段值,多个用逗号隔开 例:content,title", required = false, paramType = "query"),
            @ApiImplicitParam(name = "searchMethod", value = "搜索方式,不传默认分词搜索(传值参考SearchMethodEnum)", required = false, paramType = "query"),
            @ApiImplicitParam(name = "rangeField", value = "范围字段,需要传开始范围和结束范围才能生效,可以对时间进行范围筛选,值填写对应es索引的索引字段", required = false, paramType = "query"),
            @ApiImplicitParam(name = "start", value = "开始范围 时间格式:yyyy-MM-dd HH:mm:ss 例:2021-08-06 12:25:36", required = false, paramType = "query"),
            @ApiImplicitParam(name = "end", value = "结束范围 时间格式:yyyy-MM-dd HH:mm:ss 例:2021-08-06 12:25:36", required = false, paramType = "query"),
            @ApiImplicitParam(name = "noflag", value = "不等于的文章类型,多个逗号隔开", required = false, paramType = "query"),
    })
    @PostMapping("/highlightSearch")
    @ResponseBody
    public ResultData highlightSearch(String keyword) {
        //检查是否开启es
        if (!BooleanUtil.toBoolean(ConfigUtil.getString("ES搜索配置", "isEnable"))){
            LOG.debug("es is closed");
            return ResultData.build().error(this.getResString("error.es.close"));
        }
        ESSearchBean esSearchBean = new ESSearchBean();
        String noflag = BasicUtil.getString("noflag");
        Map<String, Object> notEquals = new HashMap<>();
        if (StringUtils.isNotBlank(noflag)) {
            List<String> collect = Arrays.stream(noflag.split(",")).collect(Collectors.toList());
            // es flag字段
            notEquals.put("flag", collect);
        }
        // 过滤条件
        String filterStr = BasicUtil.getString("filter");
        Map<Object,Object> filter = new HashMap<>();
        if (StringUtils.isNotBlank(filterStr)) {
            try {
                filter = JSONUtil.toBean(filterStr, Map.class);
            } catch (Exception e) {
                LOG.debug("过滤条件转json失败");
                e.printStackTrace();
            }
        }
        //如果有站群插件，需要根据站群插件过滤
        if (BasicUtil.getWebsiteApp() != null) {
            // 站点过滤
            LambdaQueryWrapper<AppEntity> wrapper = new QueryWrapper<AppEntity>().lambda();
            wrapper.like(AppEntity::getAppUrl, BasicUtil.getDomain());
            AppEntity appEntity = appBiz.getOne(wrapper, false);
            filter.put("appId", appEntity.getId());
        }

        // 设置基本过滤字段
        String styles = BasicUtil.getString("styles");
        if (StringUtils.isNotBlank(styles)) {
            filter.put("styles", styles);
        }

        //审核过滤
        filter.put("progressStatus", "终审通过");

        esSearchBean.setFilterWhere(filter);
        esSearchBean.setKeyword(keyword);

        // 设置排序
        String order = BasicUtil.getString("order", "desc");
        String orderBy = BasicUtil.getString("orderBy", "sort");
        esSearchBean.setOrder(order);
        esSearchBean.setOrderBy(orderBy);

        //设置分页
        Integer pageNo = BasicUtil.getInt("pageNo", 1);
        Integer pageSize = BasicUtil.getInt("pageSize", 10);
        esSearchBean.setPageNo(pageNo);
        esSearchBean.setPageSize(pageSize);

        //设置范围
        String rangeFields = BasicUtil.getString("rangeFields");
        if (StringUtils.isNotBlank(rangeFields)){
            List<Map> fieldList = new ArrayList<>();
            try {
                fieldList = JSONUtil.toList(rangeFields, Map.class);
            } catch (Exception e) {
                LOG.debug("范围查询转json失败");
                e.printStackTrace();
            }
            esSearchBean.setRangeFields(fieldList);
        }

        //设置高亮搜索配置
        Map<String, Object> highlightConfig = new HashMap<>();
        //设置高亮样式
        highlightConfig.put("preTags", "<span style='color:red'>");
        highlightConfig.put("postTags", "</span>");

        //设置搜索字段
        String fields = BasicUtil.getString("fields");
        LinkedHashMap<String, Float> searchFields = new LinkedHashMap<>();

        //设置搜索字段，数值f标识用于权重排序,一般多字段搜索使用,数值越大该字段的权重越大,当searchMethod等于geo_function_search生效
        if (StringUtils.isNotBlank(fields)) {
            String[] fieldList = fields.split(",");
            // 权重字段 越靠后权重越大
            float weight = 5f;
            for (String field : fieldList) {
                if (StringUtils.isBlank(field)) {
                    return ResultData.build().error("fields非法参数!");
                }
                searchFields.put(field, weight);
                weight += 5f;
            }
        }else {
            // 默认设置搜索标题
            searchFields.put("title", 10f);
        }

        // 设置搜索方式,默认使用分词搜索
        String searchMethod = BasicUtil.getString("searchMethod", "");

        return esService.search(ESContentBean.class, searchFields, highlightConfig,
                notEquals, esSearchBean, searchMethod);

    }

}
