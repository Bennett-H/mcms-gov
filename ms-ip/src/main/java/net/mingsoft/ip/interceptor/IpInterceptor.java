/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.ip.interceptor;


import cn.hutool.extra.servlet.ServletUtil;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * ip黑白名单拦截器
 * @author by 铭飞开源团队
 * @Description TODO
 * @date 2022/1/26 11:44
 */
public class IpInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(IpInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String clientIP = ServletUtil.getClientIP(request);
        if(clientIP.equals("0:0:0:0:0:0:0:1")){
            return true;
        }
        String loginPath = SpringUtil.getRequest().getContextPath()+MSProperties.manager.path;
        if (uri.startsWith(loginPath)){
            return whiteInterceptor(clientIP);
        }else {
            return blackInterceptor(clientIP);
        }

    }


    /**
     * 白名单拦截规则
     * @param clientIP 客户端ip
     * @return
     */
    private boolean whiteInterceptor(String clientIP){
        String whiteSwitch = ConfigUtil.getString("ip黑白名单配置", "whiteSwitch");
        if ("true".equals(whiteSwitch)) {
            String[] whiteList = ConfigUtil.getString("ip黑白名单配置", "whiteList").split(",");
            for (String ip : whiteList) {
                if (isMatch(clientIP,ip)){
                    return true;
                }
            }

            logger.debug("ip:{}不允许登录后台",clientIP);
            throw new BusinessException("您的ip被拦截");
        }

        return true;

    }

    /**
     * 黑名单拦截规则
     * @param clientIP 客户端ip
     * @return
     */
    private boolean blackInterceptor(String clientIP){
        String blackSwitch = ConfigUtil.getString("ip黑白名单配置", "blackSwitch");
        if ("true".equals(blackSwitch)) {
            String[] blackList = ConfigUtil.getString("ip黑白名单配置", "blackList").split(",");
            for (String ip : blackList) {
                if (isMatch(clientIP,ip)){
                    logger.debug("ip:{}被黑名单拦截",clientIP);
                    //不知道什么原因前台获取不到消息，只能手动再压入一次消息
                    SpringUtil.getRequest().setAttribute("msg","您的ip被拦截");
                    throw new BusinessException("您的ip被拦截");
                }
            }

        }
        return true;
    }


    /**
     * 判断客户端的ip是否和目标ip匹配
     *
     * @param clientIp 客户端的ip
     * @param matchIp  目标ip
     * @return
     */
    private boolean isMatch(String clientIp, String matchIp) {
        String[] white = matchIp.split("\\.");
        String[] client = clientIp.split("\\.");
        for (int i = 0; i < 4; i++) {
            String j = white[i];
            String k = client[i];
            if ("*".equals(j) || j.equals(k)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }


}
