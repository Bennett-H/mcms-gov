/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.aop;

import cn.hutool.core.util.StrUtil;
import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 站点管理aop
 * @Author: xierz
 * @Description:
 * @Date: Create in 2021/03/13 8:40
 */
@Component
@Aspect
public class SiteAop extends BaseAop {


    @Pointcut("execution(* net.mingsoft.basic.biz.IAppBiz.deleteEntity(..))")
    public void deleteSite() {
    }

    /**
     * 删除站点时,删除对应涉及表的数据
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @After("deleteSite()")
    public void deleteSite(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String appId = args[0].toString();
        String siteTables = ConfigUtil.getString("站群配置", "siteTables");
        if (StringUtils.isEmpty(siteTables)){
            return;
        }
        String[] tables = siteTables.split(",");
        String sql = "DELETE FROM {} WHERE app_id = "+appId;
        for (String table : tables) {
            IBaseBiz baseBiz = SpringUtil.getBean(IBaseBiz.class);
            baseBiz.excuteSql(StrUtil.format(sql,table));
        }
    }
}
