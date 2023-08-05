/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.basic.util;

import net.mingsoft.basic.biz.ILogBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.LogEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;

/**
 * 快速记录日志的工具类
 */
public class LogUtil {

    /**
     * 快速记录日志
     * @param title 日志标题
     * @param msg   日志信息
     * @param businessType   业务类型
     * @param businessId   业务id
     */
    public static void log(String title,String msg,String businessType,String businessId){
        //日志业务层
        ILogBiz logBiz = SpringUtil.getBean(ILogBiz.class);
        //获取调用的类
        String className=new Exception().getStackTrace()[1].getClassName();

        LogEntity log = new LogEntity();
        log.setLogMethod(className); //出错的类
        log.setLogUrl(""); //请求地址
        log.setLogErrorMsg(msg); //详细异常信息
        log.setLogResult("");
        log.setLogLocation(IpUtils.getRealAddressByIp(BasicUtil.getIp())); // ip地理位置
        log.setLogTitle(title); //异常标题
        log.setCreateDate(new Date());
        log.setLogBusinessType(businessType);
        log.setBusinessId(businessId);
        log.setLogIp(BasicUtil.getIp());

        logBiz.save(log);
    }
}
