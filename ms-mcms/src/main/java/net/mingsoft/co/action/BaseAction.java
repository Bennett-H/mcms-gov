/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.action;

import java.util.MissingResourceException;

/**
 * @Author: 铭软开发团队
 * @Date: 2019/8/9 20:47
 */
public class BaseAction extends net.mingsoft.cms.action.BaseAction{
    @Override
    protected String getResString(String key) {
        // TODO Auto-generated method stub
        String str = "";
        try {
            str = super.getResString(key);
        } catch (MissingResourceException e) {
            str = getLocaleString(key, net.mingsoft.co.constant.Const.RESOURCES);
        }

        return str;
    }
}
