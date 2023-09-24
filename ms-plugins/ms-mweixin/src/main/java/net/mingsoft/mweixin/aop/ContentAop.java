/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import me.chanjar.weixin.mp.api.WxMpFreePublishService;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mweixin.bean.ContentBean;
import net.mingsoft.mweixin.bean.DraftArticleBean;
import net.mingsoft.mweixin.biz.IArticleBiz;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.constant.e.DraftPublishStateEnum;
import net.mingsoft.mweixin.entity.ArticleEntity;
import net.mingsoft.mweixin.entity.DraftEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.quartz.DraftPublishStateQuartz;
import net.mingsoft.mweixin.service.PortalService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * mcms文章更新的aop
 */
@Component("wxContentAop")
@Aspect
public class ContentAop extends BaseAop {

    @Autowired
    IArticleBiz articleBiz;

    @Autowired
    IDraftBiz draftBiz;

    /**
     * 注入微信基础业务层
     */
    @Autowired
    private IWeixinBiz weixinBiz;

    @Autowired
    DraftPublishStateQuartz publishStateQuartz;

    /**
     * 文章更新的aop
     * 文章更新同步更新到微信草稿箱
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @After("execution(* net.mingsoft.cms.action.ContentAction.update(..))")
    public Object update(JoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        ContentBean contentEntity = JSONUtil.toBean(JSONUtil.toJsonStr(args[0]), ContentBean.class);
        ArticleEntity queryEntity = new ArticleEntity();
        queryEntity.setContentId(contentEntity.getId());
        List<ArticleEntity> articleEntitys = (List<ArticleEntity>) articleBiz.query(queryEntity);
        if (CollUtil.isEmpty(articleEntitys)){
            return null;
        }
        //更新图文的发布状态
        publishStateQuartz.getPublishedResults();
        Map<String, List<ArticleEntity>> listMap = articleEntitys.stream().collect(Collectors.groupingBy(ArticleEntity::getDraftId));
        for (String draftId : listMap.keySet()) {
            DraftEntity draftEntity = draftBiz.getById(draftId);
            List<ArticleEntity> articleList = listMap.get(draftId);
            for (ArticleEntity articleEntity : articleList) {
                //更新当前微信导入的文章
                copyProperties(contentEntity,articleEntity);
                articleBiz.updateEntity(articleEntity);
            }


            if (draftEntity.getPublishState() == DraftPublishStateEnum.UNPUBLISHED.toInt()
                    || draftEntity.getPublishState() == DraftPublishStateEnum.PUBLISHING_FAILED.toInt()){
                //若当前草稿未发布或发布失败,直接更新
                draftBiz.uploadDraft(draftEntity);
            }else {
                //当前草稿发布完成或发布中
                WeixinEntity wx = (WeixinEntity) weixinBiz.getEntity(draftEntity.getWeixinId());
                PortalService service = SpringUtil.getBean(PortalService.class);
                PortalService wxService = service.build(wx);
                WxMpFreePublishService freePublishService = wxService.getFreePublishService();
                //先删除微信服务器上传的的文章
                freePublishService.deletePush(draftEntity.getArticleId(),0);
                //重新上传
                draftEntity.setMediaId("");
                draftEntity.setArticleId("");
                draftEntity.setPublishId("");
                draftEntity.setPublishState(DraftPublishStateEnum.UNPUBLISHED.toInt());
                draftBiz.uploadDraft(draftEntity);
                //重新发布
                DraftArticleBean draftArticleBean = new DraftArticleBean();
                draftArticleBean.setId(draftEntity.getId());
                draftArticleBean.setPublishState(DraftPublishStateEnum.PUBLISHING.toInt());
                draftBiz.submit(draftArticleBean);
            }

        }
        return null;
    }


    /**
     * 把内容文章的参数copy到微信文章
     * @param contentEntity
     * @param articleEntity
     */
    private void copyProperties(ContentBean contentEntity, ArticleEntity articleEntity){
        articleEntity.setArticleTitle(contentEntity.getContentTitle());
        articleEntity.setArticleAuthor(contentEntity.getContentAuthor());
        articleEntity.setArticleDescription(contentEntity.getContentDescription());
        //处理img
        String contentImg = contentEntity.getContentImg();
        List list = JSONUtil.toList(contentImg,Map.class);
        //设置文章缩略图
        if (CollUtil.isNotEmpty(list)){
            Map map = (Map) list.get(0);
            String filePath = map.get("path").toString();
            File file = new File(BasicUtil.getRealPath("") + File.separator + filePath);
            if (!file.exists()){
                articleEntity.setArticleThumbnails(null);
            }else {
                articleEntity.setArticleThumbnails(filePath);
            }
        }else {
            articleEntity.setArticleThumbnails(null);
        }

        //把内容中的相对路径替换为绝对路径
        articleEntity.setArticleContent(convertRelativePathsToAbsolute(contentEntity.getContentDetails()));
    }


    /**
     * 把文本中的相对路径改为绝对路径
     * @param text
     * @return
     */
    private String convertRelativePathsToAbsolute(String text) {
        Pattern pattern = Pattern.compile("(src|href)=\"(?!http)(.*?)\"");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            String url = matcher.group(2);
            text = text.replace(url, BasicUtil.getUrl()+"/"+url);
        }
        return text;
    }

}
