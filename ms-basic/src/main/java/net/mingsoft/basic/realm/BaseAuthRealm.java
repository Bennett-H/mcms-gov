/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 * shiro权限控制
 * 
 * @author 铭软团队
 * @version 版本号：<br/>
 *          创建日期：2015年9月9日<br/>
 *          历史修订：<br/>
 */
public abstract class BaseAuthRealm extends AuthorizingRealm {
	
	/**
	 * 构造
	 */
	public  BaseAuthRealm() {
		// TODO Auto-generated constructor stub
		super();
		// 设置认证token的实现类
		setAuthenticationTokenClass(UsernamePasswordToken.class);
		// 设置加密算法
		setCredentialsMatcher(new SimpleCredentialsMatcher());
	}

	/**
	 * 更新用户授权信息缓存.
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清除所有用户授权信息缓存.
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}



}
