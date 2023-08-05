/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.MutablePair;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.biz.ILogBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.LogEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2019/11/20 12:04
 */
@Aspect
public abstract class BaseLogAop extends BaseAop{

    /**
     * 获取用户名
     * @return
     */
    public abstract String getUserName();

    /**
     * 是否切面
     * @return
     */
    public abstract boolean isCut(LogAnn log);

    /**
     * 切入点
     */
    @Pointcut("@annotation(net.mingsoft.basic.annotation.LogAnn)")
    public void logPointCut()
    { }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result)
    {
        handleLog(joinPoint, null, result);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e)
    {
        handleLog(joinPoint, e, null);
    }
    /**
     * 成功状态
     */
    private static final String SUCCESS="success";
    /**
     * 失败状态
     */
    private static final String ERROR="error";

    /**
     * 日志业务层
     */
    @Autowired
    private ILogBiz logBiz;

    private static final Logger LOG = LoggerFactory.getLogger(SystemLogAop.class);

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object result) {
        try{
            // 获得注解
            LogAnn controllerLog = getAnnotation(joinPoint, LogAnn.class);
            if (controllerLog == null){
                return;
            }
            if(!isCut(controllerLog)){
                return;
            }

            LogEntity logEntity = new LogEntity();
            //是否保存业务id
            if (controllerLog.saveId()){
                BaseEntity baseEntity = getType(joinPoint, BaseEntity.class,true);
                if (baseEntity != null){
                    logEntity.setBusinessId(baseEntity.getId());
                }
            }
            logEntity.setLogUser(getUserName());
            logEntity.setLogStatus(SUCCESS);
            // 请求的地址
            String ip = BasicUtil.getIp();
            //设置IP
            logEntity.setLogIp(ip);

            //设置返回参数
            logEntity.setLogResult(JSONUtil.parseObj(result).toJSONString(0, new Filter<MutablePair<Object, Object>>() {
                @Override
                public boolean accept(MutablePair<Object, Object> objectObjectMutablePair) {
                    return !objectObjectMutablePair.getKey().toString().equalsIgnoreCase("id");
                }
            }));
            //设置请求地址
            logEntity.setLogUrl(SpringUtil.getRequest().getRequestURI());

            if (e != null){
                logEntity.setLogStatus(ERROR);
                logEntity.setLogErrorMsg(StringUtils.substring(e.getMessage(), 0, 4000));
            }

            // 登录失败特殊处理
            if (BusinessTypeEnum.LOGIN.getLabel().equalsIgnoreCase(controllerLog.title())){
                ResultData resultData = (ResultData) result;
                if (!BooleanUtil.toBoolean(resultData.get("result").toString())){
                    logEntity.setLogStatus(ERROR);
                    logEntity.setLogErrorMsg(String.valueOf(resultData.get("msg")));
                }
            }

            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            logEntity.setLogMethod(className + "." + methodName + "()");
            // 设置请求方式
            logEntity.setLogRequestMethod(SpringUtil.getRequest().getMethod());

            // 设置action动作
            logEntity.setLogBusinessType(controllerLog.businessType().name().toLowerCase());
            // 设置标题
            logEntity.setLogTitle(controllerLog.title());
            // 设置操作人类别
            logEntity.setLogUserType(controllerLog.operatorType().name().toLowerCase());
            // 是否需要保存request，参数和值
            if (controllerLog.saveRequestData()){
                // 获取参数的信息，传入到数据库中。
                boolean isJson =StringUtils.isNotBlank(SpringUtil.getRequest().getContentType())&&MediaType.valueOf(SpringUtil.getRequest().getContentType()).includes(MediaType.APPLICATION_JSON);
              //如果是json请求参数需要获取方法体上的参数
               if(isJson){
                   Object jsonParam = getJsonParam(joinPoint);
                   if(ObjectUtil.isNotNull(jsonParam)){
                       String jsonString = JSONUtil.toJsonPrettyStr(jsonParam);
                       logEntity.setLogParam(jsonString);
                   }
               }else {
                   Map<String, String[]> map = SpringUtil.getRequest().getParameterMap();
                   String params = JSONUtil.toJsonPrettyStr(map);
                   logEntity.setLogParam(params);
               }
            }
            logEntity.setCreateDate(new Date());
            logBiz.saveData(logEntity);
        }
        catch (Exception exp){
            LOG.error("日志记录错误:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

}
