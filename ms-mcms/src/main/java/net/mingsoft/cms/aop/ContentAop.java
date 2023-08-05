/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */











package net.mingsoft.cms.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.biz.IHistoryLogBiz;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.cms.entity.HistoryLogEntity;
import net.mingsoft.cms.util.CmsParserUtil;
import net.mingsoft.co.job.GeneraterServiceJob;
import net.mingsoft.co.service.CmsParserService;
import net.mingsoft.co.service.FileTagCacheService;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.quartz.biz.IJobBiz;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 铭飞开源团队
 * 历史修订:
 * 2021-10-25 重写mcms 增加删除文章后同时删除静态文件方法
 */
@Component
@Aspect
public class ContentAop extends BaseAop {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentAop.class);



    /**
     * 注入文章业务
     */
    @Autowired
    private IContentBiz contentBiz;

    /**
     * 注入栏目业务
     */
    @Autowired
    private ICategoryBiz categoryBiz;

    /**
     * 注入静态化服务
     */
    @Autowired
    private CmsParserService cmsParserService;

    /**
     * 注入浏览记录业务
     */
    @Autowired
    private IHistoryLogBiz historyLogBiz;




    /**
     * 文章浏览记录，
     * 如果该文章该ip已经记录过，则不在重复记录
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("execution(* net.mingsoft.cms.action.web.ContentAction.get(..))")
    public Object get(ProceedingJoinPoint pjp) throws Throwable{

//        获取方法参数
        ContentEntity content = getType(pjp, ContentEntity.class);
//        如果id为空则直接发行
        if(content.getId()==null) {
            return pjp.proceed();
        }
        content = contentBiz.getById(content.getId());
        //如果文章不存在则直接发行
        if(content == null){
            return pjp.proceed();
        }

        //查询判断该ip是否已经有浏览记录了
        HistoryLogEntity historyLog = new HistoryLogEntity();
        historyLog.setContentId(content.getId());
        historyLog.setHlIp(BasicUtil.getIp());
        historyLog.setHlIsMobile(BasicUtil.isMobileDevice());
        HistoryLogEntity _historyLog = (HistoryLogEntity)historyLogBiz.getEntity(historyLog);
        //如果该ip该文章没有浏览记录则保存浏览记录
        //并且更新点击数
        if(_historyLog == null || StringUtils.isBlank(_historyLog.getId())){
            historyLog.setCreateDate(new Date());
            historyLogBiz.saveEntity(historyLog);
            //更新点击数
            ContentEntity updateContent = new ContentEntity();
            updateContent.setId(content.getId());
            if(content.getContentHit() == null){
                updateContent.setContentHit(1);
            }else{
                updateContent.setContentHit(content.getContentHit()+1);
            }
            contentBiz.updateById(updateContent);
        }

        return pjp.proceed();
    }

    @Pointcut("execution(* net.mingsoft.cms.action.ContentAction.delete(..))")
    public void delete() {}

    /**
     * 删除文章后并删除文章对应的静态化文件
     * @param jp
     */
    @After("delete()")
    public void delete(JoinPoint jp) {
        List<ContentEntity> contents = (List<ContentEntity>) getJsonParam(jp);
        // id对应style Map
        Map<String, String> idByStyle = contents.stream()
                .collect(Collectors.toMap(ContentEntity::getId, ContentEntity::getContentStyle));
        // 获取栏目ID对应文章ID数组 map
        Map<String, List<String>> categoryIdByContentIds = contents.stream()
                .collect(Collectors.groupingBy(ContentEntity::getCategoryId, Collectors.mapping(ContentEntity::getId, Collectors.toList())));
        List<String> categoryIds = new ArrayList<>(categoryIdByContentIds.keySet());

        //记录顶级栏目提供给栏目生成
        HashSet<String> topIds = CollUtil.newHashSet();

        // 考虑到在全部中删除文章 栏目不同
        for (String categoryId : categoryIds) {
            // 获取栏目
            CategoryEntity category = categoryBiz.getById(categoryId);
            if(StringUtils.isNotBlank(category.getCategoryParentIds())) {
                String[] parentIds = category.getCategoryParentIds().split(",");
                topIds.addAll(Arrays.stream(parentIds).collect(Collectors.toList()));
            }
            topIds.add(category.getId());
            // 获取栏目路径
            for (String contentId : categoryIdByContentIds.get(categoryId)) {
                // 删除静态文件，会存在多个皮肤
                String styles[] = idByStyle.get(contentId).split(",");
                Arrays.stream(styles).forEach(style ->{
                    if (CategoryTypeEnum.COVER.toString().equalsIgnoreCase(category.getCategoryType())){
                        cmsParserService.deleteCategoryHtml(style, category);
                    } else {
                        cmsParserService.deleteHtml(style, category, contentId);
                    }
                });
            }
        }
        LOG.debug("article is successfully deleted, the categorys need to be generate：{}", StrUtil.join(",",topIds));
        ParserUtil.generate(new ArrayList(topIds));

    }


}

