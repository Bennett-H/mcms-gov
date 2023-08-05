/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.listener;

import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.bean.ManagerLoginCacheBean;
import net.mingsoft.co.cache.ManagerOnlineCache;
import net.mingsoft.co.constant.Const;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.shiro.subject.support.DefaultSubjectContext.PRINCIPALS_SESSION_KEY;

/**
 * shiro session过期的监听器 用于清空线上管理员缓存
 */
public class ManagerOnlineListener implements SessionListener {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onStart(Session session) {//会话创建时触发
        SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection)session.getAttribute(PRINCIPALS_SESSION_KEY);

    }
    @Override
    public void onExpiration(Session session) {//会话过期时触发
        ManagerOnlineCache managerOnlineCache = SpringUtil.getBean(ManagerOnlineCache.class);
        SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection)session.getAttribute(PRINCIPALS_SESSION_KEY);
        if (simplePrincipalCollection==null){
            LOG.debug("会话异常，获取身份信息为空:simplePrincipalCollection");
            return;
        }
        ManagerEntity manager = (net.mingsoft.basic.entity.ManagerEntity) simplePrincipalCollection.getPrimaryPrincipal();
        if (managerOnlineCache != null && manager != null){
            ManagerLoginCacheBean loginCacheBean = managerOnlineCache.get(manager.getManagerName());
            if (loginCacheBean == null){
                return;
            }
            //会话过期重置登录失败次数
            loginCacheBean.getLoginFailCount().set(0);
            Const.SESSION_IDS.remove(session.getId());
            managerOnlineCache.delete(manager.getManagerName());
            //刷新缓存
            managerOnlineCache.flush();
        }
    }
    @Override
    public void onStop(Session session) {//退出时触发
        ManagerOnlineCache managerOnlineCache = SpringUtil.getBean(ManagerOnlineCache.class);
        Const.SESSION_IDS.remove(session.getId());
        List<ManagerLoginCacheBean> list = managerOnlineCache.list();
        for(ManagerLoginCacheBean m:list) {
            if(m.getToken().equalsIgnoreCase(String.valueOf(session.getId()))) {
                managerOnlineCache.delete(m.getManagerName());
            }

        }
        managerOnlineCache.flush();

    }
}
