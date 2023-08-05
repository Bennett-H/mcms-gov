/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.co.aop;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.co.util.ImageUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 给图片加水印的aop
 */
@Component
@Aspect
public class ImgAop extends BaseAop {


    private static final Logger LOGGER = LoggerFactory.getLogger(ImgAop.class);


    /**
     * 切入点
     */
    @Pointcut("execution(* net.mingsoft.basic.action.web.FileAction.upload(..)) || " +
            "execution(* net.mingsoft.basic.action.ManageFileAction.upload(..)) ")
    public void uploadPointCut() {
    }


    /**
     * 上传图片的时候使用工具类添加水印
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("uploadPointCut()")
    public Object uploadAop(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = joinPoint.proceed();
        ResultData result = (ResultData) proceed;

        //判断是否成功
        if (result.getCode() == 200) {
            String filePath = result.getData(String.class);
            try {
                ImageUtil.imgWatermark(filePath);
            } catch (RuntimeException e) {
                LOGGER.debug("图片添加水印失败,可能使用了云存储");
                e.printStackTrace();
            }
        }
        return result;


    }


}
