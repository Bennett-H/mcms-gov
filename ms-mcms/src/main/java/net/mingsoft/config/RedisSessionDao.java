/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @Author csp
 * @create 2022/4/12 10:47
 * @Description  实现sessionDao，从而将session信息保存到redis中，达到集群环境共享session目的
 */
@Component
public class RedisSessionDao extends AbstractSessionDAO {

    /**
     * 保存到redis中session的前缀
     */
    public static final String SESSION_PREFIX = "shiro_session::";

    /**
     * 注入Redis操作类
     */
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取活跃的session，可以用来统计在线人数，如果要实现这个功能，可以在将session加入redis时指定一个session前缀，统计的时候则使用keys("session-prefix*")的方式来模糊查找redis中所有的session集合
     * @return
     */
    @Override
    public Collection<Session> getActiveSessions() {
        return redisTemplate.keys(SESSION_PREFIX+"*");
    }

    /**
     * 新增和保存session到redis中
     * @param session
     * @return
     */
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        // 设置session超时时间
        long sessionTime = (long) ConfigUtil.getInt("安全设置", "sessionTimeOut", 30) * 1000 * 60;
        if (sessionTime <= 0){
            redisTemplate.opsForValue().set(SESSION_PREFIX+session.getId(), session);
        }else {
            redisTemplate.opsForValue().set(SESSION_PREFIX+session.getId(), session, sessionTime, TimeUnit.MILLISECONDS);
        }
        return sessionId;
    }

    /**
     * 读取redis中的sessioin
     * @param sessionId
     * @return
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }
        Session session = (Session) redisTemplate.opsForValue().get(SESSION_PREFIX+sessionId);
        return session;
    }

    /**
     * 用户请求接口，然后修改session的有效期
     * @param session
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            return;
        }
        // 设置session超时时间
        long sessionTime = (long) ConfigUtil.getInt("安全设置", "sessionTimeOut", 30) * 1000 * 60;
        if (sessionTime <= 0){
            redisTemplate.opsForValue().set(SESSION_PREFIX+session.getId(), session);
        }else {
            redisTemplate.opsForValue().set(SESSION_PREFIX+session.getId(), session, sessionTime, TimeUnit.MILLISECONDS);
        }
    }
    /**
     * session到期后删除session，比如说退出登录 logout
     * @param session
     */
    @Override
    public void delete(Session session) {
        if (null == session) {
            return;
        }
        redisTemplate.opsForValue().getOperations().delete(SESSION_PREFIX+session.getId());
    }
}