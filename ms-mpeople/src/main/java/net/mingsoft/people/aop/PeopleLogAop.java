/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.aop;

import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.MutablePair;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.IpUtils;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.people.annotation.PeopleLogAnn;
import net.mingsoft.people.biz.IPeopleLogBiz;
import net.mingsoft.people.constant.e.PeopleLogTypeEnum;
import net.mingsoft.people.constant.e.SessionConstEnum;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleLogEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * @author by 铭飞开源团队
 * @Description TODO
 * @date 2019/11/20 10:28
 */
@Aspect
@Component
public class PeopleLogAop extends BaseAop {

    @Autowired
    private IPeopleLogBiz peopleLogBiz;


    //保存和更新
    @Pointcut("@annotation(net.mingsoft.people.annotation.PeopleLogAnn)")
    public void peopleLogCut() {
    }

    @AfterReturning(pointcut = "peopleLogCut()", returning = "result")
    public void peopleLogCut(JoinPoint joinPoint, ResultData result) {
        handleLog(joinPoint, null, result);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "peopleLogCut()", throwing = "e")
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

    protected void handleLog(final JoinPoint joinPoint, final Exception exception, Object result) {
        try {
            // 获得注解
            PeopleLogAnn controllerLog = getAnnotation(joinPoint, PeopleLogAnn.class);
            if (controllerLog == null) {
                return;
            }
            PeopleLogEntity peopleLogEntity = new PeopleLogEntity();
            // 默认成功
            peopleLogEntity.setLogStatus(SUCCESS);

            // 请求IP
            String ip = BasicUtil.getIp();
            peopleLogEntity.setLogIp(ip);
            // 获取地区
            String address = IpUtils.getRealAddressByIp(ip);
            peopleLogEntity.setLogAddr(address);

            //设置返回参数
            peopleLogEntity.setLogResult(JSONUtil.parseObj(result).toJSONString(0, new Filter<MutablePair<Object, Object>>() {
                @Override
                public boolean accept(MutablePair<Object, Object> objectObjectMutablePair) {
                    return !objectObjectMutablePair.getKey().toString().equalsIgnoreCase("id");
                }
            }));

            // 获取会员信息
            PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);

            peopleLogEntity.setPeopleId("未知");
            if (peopleEntity != null) {
                peopleLogEntity.setPeopleId(peopleEntity.getId());
            }

            if (exception != null) {
                // 保存异常信息
                peopleLogEntity.setLogStatus(ERROR);
                peopleLogEntity.setlogErrorMsg(StringUtils.substring(exception.getMessage(), 0, 4000));
            }

            // 登录失败特殊处理
            if (PeopleLogTypeEnum.LOGIN.getLabel().equalsIgnoreCase(controllerLog.businessType().label)){
                ResultData resultData = (ResultData) result;
                if (resultData != null && !BooleanUtil.toBoolean(resultData.get("result").toString())) {
                    peopleLogEntity.setLogStatus(ERROR);
                    peopleLogEntity.setlogErrorMsg(String.valueOf(resultData.get("msg")));
                }
            }

            // 设置类型
            peopleLogEntity.setLogType(controllerLog.businessType().label);
            // 设置日志标题
            peopleLogEntity.setLogTitle(controllerLog.title());

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) requestAttributes
                    .resolveReference(RequestAttributes.REFERENCE_REQUEST);
            String header = request.getHeader("User-Agent");
            if (StringUtils.isNotEmpty(header)) {
                peopleLogEntity.setLogInfo(header);
            } else {
                peopleLogEntity.setLogInfo("未知详情");
            }

            // 是否需要保存request，参数和值
            if (controllerLog.saveRequestData()){
                // 获取参数的信息，传入到数据库中。
                boolean isJson =StringUtils.isNotBlank(SpringUtil.getRequest().getContentType())&& MediaType.valueOf(SpringUtil.getRequest().getContentType()).includes(MediaType.APPLICATION_JSON);
                //如果是json请求参数需要获取方法体上的参数
                if(isJson){
                    Object jsonParam = getJsonParam(joinPoint);
                    if(ObjectUtil.isNotNull(jsonParam)){
                        String jsonString = JSONUtil.toJsonPrettyStr(jsonParam);
                        peopleLogEntity.setLogParam(jsonString);
                    }
                }else {
                    Map<String, String[]> map = SpringUtil.getRequest().getParameterMap();
                    String params = JSONUtil.toJsonPrettyStr(map);
                    peopleLogEntity.setLogParam(params);
                }
            }
            peopleLogEntity.setCreateDate(new Date());

            peopleLogBiz.save(peopleLogEntity);
        } catch (Exception exp) {
            LOG.error("日志记录错误:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }


}
