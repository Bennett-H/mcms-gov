/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.aop;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.comment.entity.CommentEntity;
import net.mingsoft.datascope.utils.DataScopeUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("categoryCommentAop")
@Aspect
public class CommentAop extends BaseAop {

    /**
     * 注入栏目业务
     */
    @Autowired
    private ICategoryBiz categoryBiz;

    /**
     * 注入文章业务
     */
    @Autowired
    private IContentBiz contentBiz;


    @Around("execution(* net.mingsoft.comment.action.CommentAction.save(..))")
    public ResultData commentSave(ProceedingJoinPoint pjp) throws Throwable {
        CommentEntity comment = this.getType(pjp, CommentEntity.class);
        ResultData resultData = (ResultData) pjp.proceed(pjp.getArgs());
        if (comment == null) {
            return resultData;
        }

        ContentEntity content = contentBiz.getById(comment.getDataId());
        if (content == null) {
            return resultData;
        }
        // todo 这里是通过数据权限配置给评论加上权限管理,只有拥有该权限的管理员才可以评论
        // TODO: 2023/7/18 可以不用角色控制，达到只是通过栏目是否允许评论的效果
//        ManagerEntity manager = BasicUtil.getManager();
//        DataScopeUtil.start("0", manager.getRoleId()+"","管理员评论权限",true);
        CategoryEntity categoryEntity = categoryBiz.getById(content.getCategoryId());
        if (categoryEntity == null) {
            LOG.debug("文章：{}查询所属栏目为空",content.getId());
            return ResultData.build().error("评论失败，没有权限!");
        }
        return resultData;
    }
}
