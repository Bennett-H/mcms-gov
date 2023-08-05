/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.co.realm;

import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.realm.BaseAuthRealm;
import net.mingsoft.basic.util.SecureUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 管理员登录，实际登录业务控制
 *
 * @author Administrator
 * @version 版本号：<br/>
 *          创建日期：2015年9月9日<br/>
 *          历史修订：<br/>
 *          20210430 增加登陆盐的处理 73行
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
			return new SimpleAuthenticationInfo(manager, manager.getManagerPassword(), SecureUtils.getSalt(manager.getManagerName()), getName());
		}
		return null;

	}

	/**
	 * 功能操作授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		ManagerEntity newManager = (ManagerEntity) principalCollection.fromRealm(getName()).iterator().next();
		ManagerEntity manager = managerBiz.getEntity(newManager);
		SimpleAuthorizationInfo result = new SimpleAuthorizationInfo();
		if (null == manager) {
			return null;
		} else if(ManagerAdminEnum.SUPER.toString().equals(manager.getManagerAdmin())) {
			List<ModelEntity> models = modelBiz.list();
			for (ModelEntity model : models ) {
				if (!StringUtils.isEmpty(model.getModelUrl())) {
					result.addStringPermission(model.getModelUrl());
				}
			}
			return result;
		}else {

				// 查询管理员对应的角色
				Set<ModelEntity> models = new HashSet<>();
				for (String roleId : manager.getRoleIds().split(",")) {
					models.addAll(modelBiz.queryModelByRoleId(Integer.parseInt(roleId)));
				}
				for (ModelEntity model : models ) {
					if (!StringUtils.isEmpty(model.getModelUrl())) {
						result.addStringPermission(model.getModelUrl());
					}
				}
				return result;

			}
		}


	@Override
	public  boolean isPermitted(PrincipalCollection principals, String permission){
		ManagerEntity manager = (ManagerEntity)principals.getPrimaryPrincipal();
		return manager.getManagerAdmin().equalsIgnoreCase("super")||super.isPermitted(principals,permission);
	}
	@Override
	public boolean hasRole(PrincipalCollection principals, String roleIdentifier) {
		ManagerEntity manager = (ManagerEntity)principals.getPrimaryPrincipal();
		return manager.getManagerAdmin().equalsIgnoreCase("super")||super.hasRole(principals,roleIdentifier);
	}

}

