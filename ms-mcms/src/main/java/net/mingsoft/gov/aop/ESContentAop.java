/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.gov.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.constant.e.CategoryDisplayEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.cms.constant.e.ContentEnum;
import net.mingsoft.co.constant.e.ProgressStatusEnum;
import net.mingsoft.elasticsearch.aop.ESBaseAop;
import net.mingsoft.gov.bean.ESContentBean;
import net.mingsoft.gov.service.IESContentService;
import net.mingsoft.gov.util.ESContentBeanUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.progress.entity.ProgressLogEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * es文章搜索拦截，通过拦截的方式将模型Bean的内容同步到es库
 *
 * @author 铭软开发团队
 * @version 创建日期：2021/2/23 11:54<br/>
 * 历史修订：<br/>
 */
@Component("esContentAop")
@Aspect
public class ESContentAop extends ESBaseAop {


    @Autowired
    private ICategoryBiz categoryBiz;

    @Autowired
    private IContentBiz contentBiz;

    //保存更新同步
    @Override
    public void save(JoinPoint joinPoint, ResultData result) {
        //是否开启es
        if (!BooleanUtil.toBoolean(ConfigUtil.getString("ES搜索配置", "isEnable"))){
            LOG.debug("es is closed");
            return;
        }
        try {
            IESContentService esContentService = SpringUtil.getBean(IESContentService.class);
            BaseEntity obj = this.getType(joinPoint, BaseEntity.class, true);
            List<ESContentBean> beanList = new ArrayList<>();
            if(obj instanceof ContentEntity){
                ContentEntity content = (ContentEntity) obj;
                // 文章不添加至es的情况，1.文章未终审通过，2.文章不显示，3.文章发布时间未到
                if (!ProgressStatusEnum.APPROVED.toString().equalsIgnoreCase(content.getProgressStatus()) ||
                        ContentEnum.HIDE.toString().equals(content.getContentDisplay()) ||
                        DateUtil.compare(new Date(),content.getContentDatetime(),"yyyy-MM-dd")<0 )
                {
                    ESContentBean esContentBean = new ESContentBean();
                    BeanUtils.copyProperties(content, esContentBean);
                    esContentService.delete(esContentBean);
                    return;
                }
                ESContentBean bean = new ESContentBean();
                CategoryEntity category = categoryBiz.getById(content.getCategoryId());
                if (CategoryDisplayEnum.ENABLE.toString().equalsIgnoreCase(category.getCategoryDisplay())){
                    ESContentBeanUtil.fixESContentBean(bean, content, category);
                    beanList.add(bean);
                }
            } else if (obj instanceof ProgressLogEntity) {
                ProgressLogEntity progressLogEntity = (ProgressLogEntity) obj;
                ESContentBean bean = new ESContentBean();
                ContentEntity content = contentBiz.getById(progressLogEntity.getDataId());
                // 文章不添加至es的情况，1.文章未终审通过，2.文章不显示，3.文章发布时间未到
                if (!ProgressStatusEnum.APPROVED.toString().equalsIgnoreCase(content.getProgressStatus()) ||
                        ContentEnum.HIDE.toString().equalsIgnoreCase(content.getContentDisplay()) ||
                        DateUtil.compare(new Date(),content.getContentDatetime(),"yyyy-MM-dd")<0 )
                {
                    ESContentBean esContentBean = new ESContentBean();
                    BeanUtils.copyProperties(content, esContentBean);
                    esContentService.delete(esContentBean);
                    return;
                }
                CategoryEntity category = categoryBiz.getById(content.getCategoryId());
                if (CategoryDisplayEnum.ENABLE.toString().equalsIgnoreCase(category.getCategoryDisplay())){
                    ESContentBeanUtil.fixESContentBean(bean, content, category);
                    beanList.add(bean);
                }
            } else {
                List<String> ids = (List<String>) result.get("data");
                if (CollUtil.isEmpty(ids)) {
                    return;
                }
                // 存放冗余CategoryEntity
                Map<String, CategoryEntity> categoryMap = new HashMap<>();
                List<ContentEntity> list = contentBiz.listByIds(ids);
                // 只存放终审通过、允许显示、已过发布时间的文章
                List<ContentEntity> contents = list.stream().filter(contentEntity -> {
                    return ProgressStatusEnum.APPROVED.toString().equalsIgnoreCase(contentEntity.getProgressStatus()) &&
                            ContentEnum.DISPLAY.toString().equalsIgnoreCase(contentEntity.getContentDisplay()) &&
                            DateUtil.compare(new Date(),contentEntity.getContentDatetime(),"yyyy-MM-dd")>=0 ;
                }).collect(Collectors.toList());
                // 需在es中移除非终审通过状态的文章
                List<ESContentBean> collects = list.stream().map(contentEntity -> {
                    ESContentBean esContentBean = new ESContentBean();
                    if (!ProgressStatusEnum.APPROVED.toString().equalsIgnoreCase(contentEntity.getProgressStatus()) ||
                            ContentEnum.HIDE.toString().equalsIgnoreCase(contentEntity.getContentDisplay()) ||
                            DateUtil.compare(new Date(),contentEntity.getContentDatetime(),"yyyy-MM-dd")<0 )
                    {
                        BeanUtils.copyProperties(contentEntity, esContentBean);
                    }
                    return esContentBean;
                }).collect(Collectors.toList());
                esContentService.deleteAll(collects);

                for (ContentEntity content : contents) {
                    ESContentBean bean = new ESContentBean();
                    //获取栏目实体
                    if (categoryMap.get(content.getCategoryId()) == null) {
                        categoryMap.put(content.getCategoryId(), categoryBiz.getById(content.getCategoryId()));
                    }
                    CategoryEntity category = categoryMap.get(content.getCategoryId());
                    if (CategoryDisplayEnum.ENABLE.toString().equalsIgnoreCase(category.getCategoryDisplay())){
                        ESContentBeanUtil.fixESContentBean(bean, content, category);
                        beanList.add(bean);
                    }
                }
            }
            LOG.info("保存es库");
            esContentService.saveAll(beanList);
        } catch (NoSuchBeanDefinitionException e) {
            e.printStackTrace();
            throw new BusinessException("未检测到es环境，请检查配置");
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    //删除同步
    @Override
    public void delete(JoinPoint joinPoint, ResultData result) {
        //是否开启es
        if (!BooleanUtil.toBoolean(ConfigUtil.getString("ES搜索配置", "isEnable"))){
            LOG.debug("es is closed");
            return;
        }
        try {
            IESContentService esContentService = SpringUtil.getBean(IESContentService.class);
            //注意！！！默认接收切入方法第一个参数
            List<BaseEntity> arrayList = this.getType(joinPoint, ArrayList.class, true);
            BaseEntity obj ;
            obj = this.getType(joinPoint, BaseEntity.class, true);
            if (CollUtil.isNotEmpty(arrayList)) {
                List<ESContentBean> collect = arrayList.stream().map(baseEntity -> {
                    ESContentBean esContentBean = new ESContentBean();
                    BeanUtils.copyProperties(baseEntity, esContentBean);
                    return esContentBean;
                }).collect(Collectors.toList());
                esContentService.deleteAll(collect);
            }
            // 单个删除
            if(ObjectUtil.isNotEmpty(obj)){
                esContentService.deleteById(obj.getId());
            }

        } catch (NoSuchBeanDefinitionException e) {
            e.printStackTrace();
            throw new BusinessException("未检测到es环境，请检查配置");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
