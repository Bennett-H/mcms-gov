/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.realm;

import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ModelEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 管理员shiro权限控制
 *
 * @author 铭软团队
 * @version 版本号：<br/>
 *          创建日期：2015年9月9日<br/>
 *          历史修订：<br/>
 */
public class ManagerAuthRealm extends BaseAuthRealm {
	/**
	 * 管理员业务层
	 */
	@Autowired
	private IManagerBiz managerBiz;

	/**
	 * 模块业务层
	 */
	@Autowired
	private IModelBiz modelBiz;

	/**
	 * 新登用户验证
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		ManagerEntity newManager = new ManagerEntity();
		newManager.setManagerName(upToken.getUsername());
		ManagerEntity manager = (ManagerEntity) managerBiz.getEntity(newManager);
		if (manager != null) {
			return new SimpleAuthenticationInfo(manager, manager.getManagerPassword(), getName());
		}
		return null;
	}

	/**
	 * 功能操作授权
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		ManagerEntity newManager = (ManagerEntity) principalCollection.fromRealm(getName()).iterator().next();
		ManagerEntity manager = (ManagerEntity) managerBiz.getEntity(newManager);
		if (null == manager) {
			return null;
		} else {
			SimpleAuthorizationInfo result = new SimpleAuthorizationInfo();
			// 查询管理员对应的角色
			List<ModelEntity> models = modelBiz.queryModelByRoleId(manager.getRoleId());
			for (BaseEntity e:models) {
				ModelEntity me = (ModelEntity) e;
				if (!StringUtils.isEmpty(me.getModelUrl())) {
					result.addStringPermission(me.getModelUrl());
				}
			}
			return result;

		}
	}

}
