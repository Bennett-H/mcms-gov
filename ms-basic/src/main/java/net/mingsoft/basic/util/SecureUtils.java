/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.basic.util;

import cn.hutool.crypto.digest.DigestUtil;
import net.mingsoft.base.constant.Const;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * 管理员安全工具类
 *
 * @author 铭软
 * @version 版本号：200-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
public class SecureUtils {

    /**
     * 密码加密
     *
     * @param password 密码
     * @param salt     盐
     * @return 字符串
     */
    public static String password(String password, String salt) {
        return DigestUtil.md5Hex(password, Const.UTF8);
    }


}
