/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.filter;

import cn.hutool.core.date.DateUtil;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.bean.ManagerLoginCacheBean;
import net.mingsoft.co.cache.ManagerOnlineCache;
import net.mingsoft.co.constant.Const;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 基于shiro的登录控制，同一时间只能登录一个管理员
 *
 * @author by 铭软开发团队
 * 创建日期：2021-02-15<br/>
 * 历史修订：<br/>
 */
public class SingleSessionControlFilter extends AccessControlFilter {

    private final Logger logger = LoggerFactory.getLogger(SingleSessionControlFilter.class);

    @Autowired
    MSProperties msProperties;
    /**
     * 踢出后到的地址
     */
    private String loginPath;

    public void setKickAfter(boolean kickAfter) {
        this.kickAfter = kickAfter;
    }

    /**
     * 踢出之前登录的/之后登录的用户 默认踢出之前登录的用户
     */
    private boolean kickAfter = false;

    private SessionManager sessionManager;

    private Cache<String, Deque<Serializable>> cache;

    public void setLoginPath(String loginPath) {
        this.loginPath = loginPath;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("shiro-kickout-session");
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = getSubject(request, response);
        if (BasicUtil.getManager() != null && !isLoginRequest(request, response)) {
            ManagerEntity manager = BasicUtil.getManager();
            ManagerOnlineCache managerOnlineCache = SpringUtil.getBean(ManagerOnlineCache.class);
            ManagerLoginCacheBean managerLoginCacheBean = managerOnlineCache.get(manager.getManagerName());
            Deque<Serializable> deque = cache.get(BasicUtil.getManager().getManagerName());

            if (managerLoginCacheBean == null) {
                subject.logout();
                if (deque != null) {
                    deque.remove(subject.getSession().getId());
                }
                return Boolean.TRUE;
            } else if (!Const.SESSION_IDS.contains(subject.getSession().getId())) {
                if (subject.getPrincipal() == null) {
                    subject.logout();
                    if (deque != null) {
                        deque.remove(subject.getSession().getId());
                    }
                    return Boolean.TRUE;
                } else {
                    Const.SESSION_IDS.add(subject.getSession().getId());
                }
            }
            return Boolean.FALSE;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestedWith = httpServletRequest.getHeader("X-Requested-With");
        //先判断是否有权限
        if (!subject.isAuthenticated()) {
            //判断是否是登陆页面
            if (isLoginRequest(request, response) && httpServletRequest.getMethod().equalsIgnoreCase("get") && !SpringUtil.getRequest().getRequestURI().equals(msProperties.getManager().path + msProperties.getManager().loginPath)) {
                String referer = httpServletRequest.getHeader("Referer");
                System.out.println();
                //referer 不为空表示重定向
                if (StringUtils.isNotBlank(referer) || (StringUtils.isNotEmpty(requestedWith) && StringUtils.equals(requestedWith, "XMLHttpRequest"))) {//如果是ajax返回指定数据
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//403 禁止
                }
            }

            return true;
        }
        return Boolean.FALSE;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        Subject subject = getSubject(request, response);
        HttpServletRequest req = WebUtils.toHttp(request);
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        logger.debug("进入co 登录控制拦截{}", httpServletRequest.getRequestURI());
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return true;
        }
        Session session = subject.getSession();
//        logger.debug("当前登录信息:{}", JSONObject.toJSONString(subject.getPrincipal()));
        String managerName = ((ManagerEntity) subject.getPrincipal()).getManagerName();
        Serializable sessionId = session.getId();

        // 初始化用户的队列放到缓存里
        Deque<Serializable> deque = cache.get(managerName);
        if (deque == null) {
            deque = new LinkedList<Serializable>();
            cache.put(managerName, deque);
        }

        //如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if (!deque.contains(sessionId) && session.getAttribute("kick") == null) {
            deque.push(sessionId);
            cache.put(managerName, deque);
        }

        //同一个帐号最大会话数 默认1
        int maxSession = ConfigUtil.getInt("安全设置", "maxSession", 1);

        //如果队列里的sessionId数超出最大会话数，开始踢人
        while (maxSession > 0 && deque.size() > maxSession) {
            Serializable kickSessionId = null;
            if (kickAfter) { //如果踢出后者
                kickSessionId = deque.removeFirst();
            } else { //否则踢出前者
                kickSessionId = deque.removeLast();
            }
            cache.put(managerName, deque);
            try {
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickSessionId));
                if (kickoutSession != null) {
                    //设置会话的kickout属性表示踢出了
                    kickoutSession.setAttribute("kick", true);
                }
            } catch (SessionException e) {

            }
        }

        //如果被踢出了
        if (session.getAttribute("kick") != null) {
            subject.logout();
            //重定向退出到登入页面
            req.setAttribute("kick", "该账号异地登录，您被强制下线");
            request.getRequestDispatcher(loginPath).forward(request, response);
            return false;
        }
        if (session.getAttribute("startTime") != null && session.getAttribute("endTime") != null) {
            // 24小时制
            SimpleDateFormat startFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = startFormat.parse(startFormat.format(new Date().getTime()));
            if (!DateUtil.isIn(date,(Date) session.getAttribute("startTime"),(Date) session.getAttribute("endTime"))){
                subject.logout();
                //重定向退出到登入页面
                req.setAttribute("kick", "该时间段不允许访问系统，您被强制下线");
                request.getRequestDispatcher(loginPath).forward(request, response);
                return false;
            }
        }
        return true;
    }
}
