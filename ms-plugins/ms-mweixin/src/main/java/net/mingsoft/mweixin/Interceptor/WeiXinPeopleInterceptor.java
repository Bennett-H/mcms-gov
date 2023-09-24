/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.Interceptor;

import cn.hutool.core.util.StrUtil;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.people.constant.e.PeopleEnum;
import net.mingsoft.people.constant.e.SessionConstEnum;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.interceptor.ActionInterceptor;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hui se
 * @description 小程序拦截器
 * @create 2020-02-03 14:44
 **/
public class WeiXinPeopleInterceptor extends ActionInterceptor {

    @Value("${ms.mall.auth}")
    private String authUrl;
    @Value("${ms.mall.mweixin-no}")
    private String weixinNo;

    public WeiXinPeopleInterceptor(String loginUrl) {
        super(loginUrl);
    }

    public WeiXinPeopleInterceptor() {

    }

    /**
     * 用于重定向未绑定手机号的前往绑定页
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String userAgent = request.getHeader("user-agent").toLowerCase();
        //判断在微信浏览器内,并且不是ajax请求
        if(StrUtil.isNotBlank(userAgent)&&userAgent.contains("micromessenger")&& (StrUtil.isBlank(request.getHeader("x-requested-with")) || !"XMLHttpRequest".equals(request.getHeader("x-requested-with")))){
            //使用父类接收该对象,防止类型转换异常
            PeopleEntity peopleEntity= (PeopleEntity)BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
            //是否是小程序，或者公众号
            if(!userAgent.contains("miniprogram")){
                //如果是公众号判断是否授权过了，
                if(peopleEntity==null){
                    //组织重定向链接，通过链接进行授权
                    String oauthUrl = BasicUtil.getUrl()+"/mweixin/oauth/getUrl.do?weixinNo="+weixinNo+"&url="+BasicUtil.getUrl()+"&oauthLoginUrl="+authUrl;
                    response.sendRedirect(oauthUrl);
                    return  false;
                }
            }

            return true;
        }
        return super.preHandle(request,response,handler);
    }

}
