/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.aop;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.progress.annotation.Progress;
import net.mingsoft.progress.biz.IProgressBiz;
import net.mingsoft.progress.biz.IProgressLogBiz;
import net.mingsoft.progress.biz.ISchemeBiz;
import net.mingsoft.progress.entity.ProgressEntity;
import net.mingsoft.progress.entity.ProgressLogEntity;
import net.mingsoft.progress.entity.SchemeEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 进度aop
 *
 * @author Administrator
 * @version 创建日期：2021/3/4 8:52<br/>
 * 历史修订：<br/>
 */
@Component("progressAop")
@Aspect
public class ProgressAop extends BaseAop {
    /**
     * 注入进度表业务层
     */
    @Autowired
    private IProgressBiz progressBiz;
    @Autowired
    private ISchemeBiz schemeBiz;
    @Autowired
    private IProgressLogBiz progressLogBiz;

    @Pointcut("@annotation(net.mingsoft.progress.annotation.Progress)")
    public void save(){

    }
    //保存和更新
    @AfterReturning("save() && @annotation(progress))")
    public void save(JoinPoint joinPoint, Progress progress) {
        BaseEntity baseEntity = this.getType(joinPoint, BaseEntity.class,true);
        LOG.info("开始审核，修改业务表状态id:"+baseEntity.getId());
        // 获取方案名
        SchemeEntity scheme = new SchemeEntity();
        scheme.setSchemeName(progress.schemeName());
        SchemeEntity schemeEntity = schemeBiz.getOne(new QueryWrapper<>(scheme));
        ProgressEntity progressEntity = progressBiz.queryProgress(progress.schemeName()).get(0);
        // 获取进度节点的第一条
        ProgressLogEntity progressLog = new ProgressLogEntity();
        // 创建一条待审核的日志信息
        progressLog.setPlNodeName(progressEntity.getProgressNodeName());
        progressLog.setSchemeId(progressEntity.getSchemeId());
        progressLog.setProgressId(progressEntity.getIntegerId());
        progressLog.setDataId(baseEntity.getId());
        progressLog.setCreateDate(new Date());
        progressLogBiz.saveOrUpdate(progressLog);
        // 更新业务表的状态
        String sql = "update {}  set PROGRESS_STATUS='{}' where ID='{}';";
        progressLogBiz.excuteSql(StrUtil.format(sql,schemeEntity.getSchemeTable(), "处理中",baseEntity.getId()));
    }

}
