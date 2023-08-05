/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.approval.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.mingsoft.approval.biz.IConfigBiz;
import net.mingsoft.approval.constant.e.ProgressStatusEnum;
import net.mingsoft.approval.entity.ConfigEntity;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.cms.constant.e.ContentEnum;
import net.mingsoft.co.service.CmsParserService;
import net.mingsoft.co.service.FileTagCacheService;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.progress.biz.IProgressLogBiz;
import net.mingsoft.progress.entity.ProgressEntity;
import net.mingsoft.progress.entity.ProgressLogEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Order(10)
@Component("ConfigAop")
public class ConfigAop extends BaseAop {


    @Autowired
    private IConfigBiz configBiz;

    @Autowired
    private IProgressLogBiz progressLogBiz;

    @Autowired
    private IContentBiz contentBiz;



    /**
     * 注入栏目业务
     */
    @Autowired
    private ICategoryBiz categoryBiz;

    @Autowired
    private CmsParserService cmsParserService;

    @Autowired
    private FileTagCacheService fileTagCacheService;

    /**
     * 删除审批节点接口切面
     */
    @Pointcut("execution(* net.mingsoft.progress.action.ProgressAction.delete(..)) ")
    public void delete() {}

    @After("delete()")
    public void delete(JoinPoint joinPoint) {
        List<ProgressEntity> progressList = (List<ProgressEntity>) this.getJsonParam(joinPoint);
        if (CollUtil.isNotEmpty(progressList)) {
            // 移除所有的被删除的审批节点权限
            List<String> progressIds = progressList.stream().map(ProgressEntity::getId).collect(Collectors.toList());
            LambdaQueryWrapper<ConfigEntity> wrapper = new QueryWrapper<ConfigEntity>().lambda();
            wrapper.in(ConfigEntity::getProgressId, progressIds);
            configBiz.remove(wrapper);
        }

    }

    @Before("delete()")
    public void approvalContent(JoinPoint joinPoint) {
        List<ProgressEntity> delProgressList = (List<ProgressEntity>) this.getJsonParam(joinPoint);
        if (CollUtil.isEmpty(delProgressList)) {
            return;
        }
        List<String> ids = delProgressList.stream().map(ProgressEntity::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(delProgressList)) {
            List<String> dataIds = progressLogBiz.queryDataIdBySchemeNameAndNodeNames("文章审核", ids, null);
            // 自动完成所有待审批的文章
            for (String dataId : dataIds) {
                ContentEntity content = contentBiz.getById(dataId);
                if (content != null){
                    content.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
                    contentBiz.updateById(content);
                }
                LambdaUpdateWrapper<ProgressLogEntity> wrapper = new UpdateWrapper<ProgressLogEntity>().lambda();
                wrapper.set(ProgressLogEntity::getPlContent, "删减审批节点导致强制跳过审批并完成文章审批");
                wrapper.set(ProgressLogEntity::getPlStatus, "skip");
                wrapper.eq(ProgressLogEntity::getProgressId, ids.get(0));
                wrapper.eq(ProgressLogEntity::getDataId, dataId);
                wrapper.isNull(ProgressLogEntity::getPlStatus);
                progressLogBiz.update(wrapper);
            }
        }
    }


    /**
     * 会员和后台审批接口切面
     */
    @Pointcut("execution(* net.mingsoft.approval.action.ConfigAction.approval(..))")
    public void approval() {}

    /**
     * 会员和后台编辑或新增文章切面
     */
    @Pointcut(" execution(* net.mingsoft.cms.action.ContentAction.save(..)) ||" +
            " execution(* net.mingsoft.cms.action.ContentAction.update(..))"+
            "execution(* net.mingsoft.cms.action.people.ContentAction.save(..)) ||" +
            "execution(* net.mingsoft.cms.action.people.ContentAction.update(..))")
    public void saveOrUpdate(){

    }


    @After("approval()")
    public void approval(JoinPoint jp) throws Exception {
        ProgressLogEntity progressLog = getType(jp, ProgressLogEntity.class);
        if (ObjectUtil.isEmpty(progressLog)){
            LOG.debug("审批通过自动静态化文章失败");
            return;
        }
        ContentEntity content = contentBiz.getById(progressLog.getDataId());;

        //先缓存文章数据
        if(StringUtils.isNotBlank(content.getId())) {
            fileTagCacheService.cacheContent(content);
        }
        // 终审通过并且开启了配置，且文章被允许显示的条件下才会被静态化
        if (StringUtils.equals(content.getProgressStatus(), ProgressStatusEnum.APPROVED.toString())
                && ConfigUtil.getBoolean("静态化配置", "articleGeneraterServiceJob", false)
                && ContentEnum.DISPLAY.toString().equalsIgnoreCase(content.getContentDisplay())
        ) {
            parserContent(content);
        }
    }

    @After("saveOrUpdate()")
    public void saveOrUpdate(JoinPoint jp) throws Exception{
        ContentEntity content = getType(jp, ContentEntity.class);
        if (ObjectUtil.isEmpty(content)){
            LOG.debug("更新或新增自动静态化文章失败");
            return;
        }
        // 先缓存文章数据
        if(StringUtils.isNotBlank(content.getId())) {
            fileTagCacheService.cacheContent(content);
        }
        // 开启了配置的才会被静态化，无需判断审批状态，生成只会生成终审通过文章
        if (ConfigUtil.getBoolean("静态化配置", "articleGeneraterServiceJob", false)) {
            // 如果文章不被允许显示或者为待审批状态,或者发布时间被延后,则将其上下篇文章静态化标识重置，删除对应html文件
            if (!ProgressStatusEnum.APPROVED.toString().equalsIgnoreCase(content.getProgressStatus()) || ContentEnum.HIDE.toString().equals(content.getContentDisplay()) || DateUtil.compare(DateUtil.date(), content.getContentDatetime(),"yyyy-MM-dd")>0){
                // 多皮肤删除html文件
                String[] styles = content.getContentStyle().split(",");
                CategoryEntity category = categoryBiz.getById(content.getCategoryId());
                for (String style : styles) {
                    if (CategoryTypeEnum.COVER.toString().equalsIgnoreCase(category.getCategoryType())){
                        cmsParserService.deleteCategoryHtml(style, category);
                    } else {
                        cmsParserService.deleteHtml(style, category, content.getId());
                    }
                }

            }
            // 重置上下篇静态化标识
            AppEntity app = BasicUtil.getApp();
            // 查询当前更新文章的上一篇文章和下一篇文章，条件为当前栏目、都是允许显示且未被删除的当前站点下的文章
            String sql = null;
            if (ConfigUtil.getBoolean("站群配置", "siteEnable")) {
                sql = StrFormatter.format("SELECT * FROM cms_content WHERE  id IN " +
                                "(( SELECT max( id ) FROM cms_content WHERE CATEGORY_ID = '" + content.getCategoryId() + "' AND id < '" + content.getId() + "' AND DEL = 0 AND CONTENT_DISPLAY = '0' AND APP_ID = '{}' ),\n" +
                                "( SELECT min( id ) FROM cms_content WHERE CATEGORY_ID = '" + content.getCategoryId() + "' AND id > '" + content.getId() + "'  AND DEL = 0 AND CONTENT_DISPLAY = '0' AND APP_ID = '{}'))",
                        app.getId(), app.getId());
            } else {
                sql = StrFormatter.format("SELECT * FROM cms_content WHERE  id IN " +
                        "(( SELECT max( id ) FROM cms_content WHERE CATEGORY_ID = '" + content.getCategoryId() + "' AND id < '" + content.getId() + "' AND DEL = 0 AND CONTENT_DISPLAY = '0'),\n" +
                        "( SELECT min( id ) FROM cms_content WHERE CATEGORY_ID = '" + content.getCategoryId() + "' AND id > '" + content.getId() + "'  AND DEL = 0 AND CONTENT_DISPLAY = '0'))");
            }
            // 直接执行sql，将查询出来的记录处理静态化标识
            List<Map> contents = (List<Map>)contentBiz.excuteSql(sql);
            List<ContentEntity> contentEntities = contents.stream().map(map -> {
                // todo 注意此处使用hutool的JSONUtil转bean对象时会出现dm版本下循环调用导致StackOverflowError的情况
//                ContentEntity contentEntity = JSONUtil.toBean(JSONUtil.toJsonStr(map),ContentEntity.class);
                ContentEntity contentEntity = BeanUtil.toBean(map,ContentEntity.class);
                // 设置文章更新标识即可
                contentEntity.setHasDetailHtml(0);
                // 由于表字段大小写不规范会影响取值,需做容错处理
                // todo 类似这种id处理需要谨慎,如果map中同时包含模型相关数据,需要考虑dm数据库适配问题,此处只有文章数据,无需处理
                contentEntity.setId(String.valueOf(ObjectUtils.isEmpty(map.get("ID"))?map.get("id"):map.get("ID")));
                // 设置更新时间为今天，以便静态化方法能够识别到文章
                contentEntity.setUpdateDate(new Date());
                return contentEntity;
            }).collect(Collectors.toList());
            // 更新文章
            contentBiz.updateBatchById(contentEntities);

            // 静态化相关页面
            parserContent(content);
        }
    }


    /**
     * 调用parserUtil工具类方法静态化相关页面
     * @param content 切面读取到的文章
     */
    private void parserContent(ContentEntity content){
        HashSet<String> topIds = CollUtil.newHashSet();
        // 获取栏目
        CategoryEntity category = categoryBiz.getById(content.getCategoryId());
        LOG.debug("manager：{},contentId：{} ready to write",BasicUtil.getManager().getManagerName(),content.getId());
        if(StringUtils.isNotBlank(category.getCategoryParentIds())) {
            String[] parentIds = category.getCategoryParentIds().split(",");
            topIds.addAll(Arrays.stream(parentIds).collect(Collectors.toList()));
        }
        if(StringUtils.isNotBlank(content.getCategoryIds())) {
            Arrays.stream(content.getCategoryIds().split(",")).forEach(id ->{
                topIds.add(id);
            });
        }
        topIds.add(category.getId());
        LOG.debug("category ids：{}", StrUtil.join(",",topIds));
        ParserUtil.generate(new ArrayList(topIds));
    }


}
