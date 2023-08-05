/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.basic.util;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * 管理员安全工具类
 *
 * @author 铭软
 * @version 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：
 * 2021-07-12 重写co，通过ConfigUtil来动态获取<br/>
 */
public class SecureUtils {



    /**
     * 默认加密方式
     * @return
     */
    public static String getHashAlgorithmName() {
        return ConfigUtil.getString("安全设置", "algorithm");
    }

    /**
     * 默认散列次数
     * @return
     */
    public static int getHashIterations() {
        return ConfigUtil.getInt("安全设置", "hashIterations");
    }

    /**
     * 获取对应盐，主要是对盐进行md5+散列
     *
     * @param salt 盐
     * @return
     */
    public static Md5Hash getSalt(String salt) {
        return SecureUtils.getSalt(salt,  ConfigUtil.getInt("安全设置", "saltHashIterations"));
    }

    /**
     * 获取对应盐，主要是对盐进行md5+散列
     *
     * @param salt               盐
     * @param saltHashIterations 盐的散列次数
     * @return
     */
    public static Md5Hash getSalt(String salt, int saltHashIterations) {
        return new Md5Hash(salt, null, saltHashIterations);
    }

    /**
     * 密码加盐，默认salt 盐的散列次数 2次，passwd 密码散列次数 1024次
     *
     * @param password 密码
     * @param salt     盐
     * @return 字符串
     */
    public static String password(String password, String salt) {
        return SecureUtils.password(password, salt, ConfigUtil.getInt("安全设置", "saltHashIterations"), ConfigUtil.getInt("安全设置", "hashIterations"));
    }


    /**
     * 是否是国密
     * @return true是国密，false是md5
     */
    public static boolean isSM4() {
     return ConfigUtil.getString("安全设置", "algorithm").equalsIgnoreCase("SM4");
    }
    /**
     * 密码加盐，
     *
     * @param password           密码
     * @param salt               盐
     * @param saltHashIterations 盐的散列次数
     * @param hashIterations     密码散列次数
     * @return 字符串
     */
    public static String password(String password, String salt, int saltHashIterations, int hashIterations) {
        //管理员账号为盐
        Md5Hash _salt = SecureUtils.getSalt(salt, saltHashIterations);
        if (SecureUtils.isSM4()) {
            //判断用户账号和密码是否正确
            SymmetricCrypto sm4 = SmUtil.sm4(_salt.getBytes());
            return sm4.encryptHex(password);
        } else {
            return new Md5Hash(password, _salt, hashIterations).toString();
        }

    }
}
