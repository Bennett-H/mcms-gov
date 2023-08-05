/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.co.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import net.mingsoft.approval.constant.e.ProgressStatusEnum;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.biz.IContentBiz;
import net.mingsoft.co.cache.ContentCache;
import net.mingsoft.co.service.FileTagCacheService;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章缓存，保存、更新、删除的时候会进行缓存处理，为了提高静态化的生成效率
 * @author 铭软开发团队
 * @version
 * 版本号：1.0<br/>
 * 创建日期：2017-8-24 23:40:55<br/>
 * 历史修订：重写co-增加回收站还原添加缓存 <br/>
 */
@Order(1)
@Component("govContentAop")
@Aspect
public class ContentAop extends BaseAop {

    @Autowired
    private FileTagCacheService fileTagCacheService;

    @Autowired
    private ContentCache contentCache;

    @Autowired
    private IContentBiz contentBiz;

    @Autowired
    private ICategoryBiz categoryBiz;

    @Autowired
    private IAppBiz appBiz;



    // 针对批量复制移动文章 文章回收站，同时生成相关页面
    @AfterReturning(value = "execution(* net.mingsoft.co.action.ContentAction.saveRemoveOrCopy(..)) || execution(* net.mingsoft.cms.action.ContentAction.reduction(..))", returning = "result")
    public void updateContentBeanCache(JoinPoint joinPoint, Object result) throws Throwable {
        ResultData result1 = (ResultData) result;
        List<String> ids = (List<String>) result1.get("data");
        List<ContentEntity> contents = null;
        if(CollUtil.isNotEmpty(ids)) {
            contents = contentBiz.listByIds(ids);
            for (ContentEntity entity : contents) {
                fileTagCacheService.cacheContent(entity);
            }
        }
        //如果文章集合不为空并且启用自动静态化则调用静态化生成文章
        // 静态化开关，默认关闭
        Boolean enableGenerate = ConfigUtil.getBoolean("静态化配置", "articleGeneraterServiceJob", false);
        if (CollectionUtil.isNotEmpty(contents) && enableGenerate){
            // 获取栏目ID对应文章ID数组 map
            Map<String, List<String>> categoryIdByContentIds = contents.stream()
                    .collect(Collectors.groupingBy(ContentEntity::getCategoryId, Collectors.mapping(ContentEntity::getId, Collectors.toList())));
            List<String> categoryIds = new ArrayList<>(categoryIdByContentIds.keySet());

            //记录顶级栏目提供给栏目生成
            HashSet<String> topIds = CollUtil.newHashSet();

            // 栏目不同
            for (String categoryId : categoryIds) {
                // 获取栏目
                CategoryEntity category = categoryBiz.getById(categoryId);
                if(StringUtils.isNotBlank(category.getCategoryParentIds())) {
                    String[] parentIds = category.getCategoryParentIds().split(",");
                    topIds.addAll(Arrays.stream(parentIds).collect(Collectors.toList()));
                }
                topIds.add(category.getId());
            }
            LOG.debug("article is successfully reduction, the categorys need to be generate：{}", StrUtil.join(",",topIds));
            ParserUtil.generate(new ArrayList(topIds));
        }
    }

    // 针对站群文章分发文章缓存
    @AfterReturning(value = "execution(* net.mingsoft.*.action.ContentAction.distribution(..))", returning = "result")
    public void updateDistributionContentCache(JoinPoint joinPoint, Object result) throws Throwable {
        ResultData result1 = (ResultData) result;
        // 获取分发的所有文章id
        List<String> ids = (List<String>) result1.get("data");
        if(CollUtil.isNotEmpty(ids)) {
            // 静态化开关，默认关闭
            Boolean flag = ConfigUtil.getBoolean("静态化配置", "articleGeneraterServiceJob", false);
            String sql = "select * from cms_content where id in ({})";
            List<Map> list = (List<Map>) contentBiz.excuteSql(StrUtil.format(sql, StringUtils.strip(ArrayUtil.toString(ids), "[]")));
            for (Map map : list) {
                // 转成文章实体，缓存文章
                ContentEntity entity = BeanUtil.toBean(map, ContentEntity.class, new CopyOptions().ignoreCase());
                fileTagCacheService.cacheContent(entity);
                // 针对分发文章调用自动静态化，终审通过且开启自动静态化才会被调用
                if (flag && StringUtils.equals(ProgressStatusEnum.APPROVED.toString(),entity.getProgressStatus())){
                    //栏目id集合
                    HashSet<String> topIds = CollUtil.newHashSet();
                    // 获取栏目实体,直接查询会拼接分发站点appId为条件，该栏目属于被分发的站点下栏目
                    String queryCategory = "select * from cms_category where id = {}";
                    // 根据文章id查出所属栏目，不区分站点
                    List<Map> categoryList = (List<Map>)categoryBiz.excuteSql(StrUtil.format(queryCategory, entity.getCategoryId()));
                    CategoryEntity category = BeanUtil.toBean(categoryList.get(0),CategoryEntity.class,new CopyOptions().ignoreCase());
                    //被分发到的站点实体
                    AppEntity app = appBiz.getById((Integer)categoryList.get(0).get("APP_ID"));
                    if(StringUtils.isNotBlank(category.getCategoryParentIds())) {
                        String[] parentIds = category.getCategoryParentIds().split(",");
                        topIds.addAll(Arrays.stream(parentIds).collect(Collectors.toList()));
                    }
                    // 父ids拆解添加至集合
                    if(StringUtils.isNotBlank(entity.getCategoryIds())) {
                        Arrays.stream(entity.getCategoryIds().split(",")).forEach(id ->{
                            topIds.add(id);
                        });
                    }
                    topIds.add(category.getId());
                    ParserUtil.generate(new ArrayList(topIds),app);
                }
            }
        }
    }

    // 针对采集的文章缓存
    @After(value = "execution(* net.mingsoft.*.util.LogBatchImportUtil.batchInsert(..))")
    public void LogBatchImportContentCache(JoinPoint joinPoint) {
        String importTable = this.getType(joinPoint, String.class);
        List<BaseEntity> list = this.getType(joinPoint, ArrayList.class);
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<String> ids = list.stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<ContentEntity> contents = contentBiz.listByIds(ids);
        if (importTable.equalsIgnoreCase("CMS_CONTENT") && CollUtil.isNotEmpty(contents)) {
            for (ContentEntity content : contents) {
                fileTagCacheService.cacheContent(content);
            }
        }
    }


    @After("execution(* net.mingsoft.mdiy.action.ModelAction.save(..)) || execution(* net.mingsoft.mdiy.action.ModelAction.update(..))")
    public void updateModelContentCache(JoinPoint joinPoint) throws Throwable {
        String linkId = this.getType(joinPoint, String.class);
        ShiroHttpServletRequest request = this.getType(joinPoint, ShiroHttpServletRequest.class);
        String type = request.getParameter("type");
        if(StringUtils.isNotBlank(type) && type.equalsIgnoreCase("cms")) {
            if(StringUtils.isNotBlank(linkId)) {
                ContentEntity content = contentBiz.getById(linkId);
                fileTagCacheService.cacheContent(content);
            }
        }

    }

    @After("execution(* net.mingsoft.cms.action.ContentAction.delete(..)) ||" +
            "execution(* net.mingsoft.cms.action.people.ContentAction.delete(..))")
    public void removeContentCache(JoinPoint joinPoint) throws Throwable {
        List<ContentEntity> contents = (List<ContentEntity>) this.getType(joinPoint, ArrayList.class);
        contents.forEach(content ->
                contentCache.delete(content.getId())
        );
    }

}
