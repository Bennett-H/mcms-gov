/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.gov.listener;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

public class ShiroSessionListener implements SessionListener {
    @Override
    public void onStart(Session session) {
    }

    @Override
    public void onStop(Session session) {

    }

    @Override
    public void onExpiration(Session session) {

    }
}
