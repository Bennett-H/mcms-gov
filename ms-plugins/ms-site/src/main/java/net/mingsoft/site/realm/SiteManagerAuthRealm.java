/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.site.realm;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.biz.IRoleBiz;
import net.mingsoft.basic.constant.Const;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.entity.RoleEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.realm.BaseAuthRealm;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.site.biz.ISiteAppManagerBiz;
import net.mingsoft.site.entity.SiteAppManagerEntity;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 站群员工shiro权限控制，需要ShiroConfig里面配置才生效
 *
 * @author 铭软科技
 * @version 版本号：<br/>
 *          创建日期：2015年9月9日<br/>
 *          历史修订：<br/>
 */
public class SiteManagerAuthRealm extends BaseAuthRealm {
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

	@Autowired
	private IRoleBiz roleBiz;


	@Autowired
	private ISiteAppManagerBiz siteAppManagerBiz;

	/**
	 * 新登用户验证
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		ManagerEntity manager = managerBiz.getManagerByManagerName(upToken.getUsername());
		if (manager != null) {
			//是否开启站群
			boolean siteEnable = BooleanUtils.toBoolean(ConfigUtil.getString("站群配置", "siteEnable"));
			if (!siteEnable) {
				// 如果没有开启站群则直接登录
				return new SimpleAuthenticationInfo(manager, manager.getManagerPassword(), getName());
			}
			IAppBiz appBiz = SpringUtil.getBean(IAppBiz.class);
			AppEntity appEntity = (AppEntity) BasicUtil.getSession(Const.APP);
			if (appEntity == null){
				appEntity = appBiz.getOne(
						Wrappers.<AppEntity>lambdaQuery()
								.like(AppEntity::getAppUrl, BasicUtil.getDomain()),false);
			}

			if (manager.getManagerAdmin().equals(ManagerAdminEnum.SUPER.toString())) {
				if(appEntity==null) {
					appEntity = appBiz.getFirstApp();
				}
				BasicUtil.setSession(Const.APP,appEntity);
				BasicUtil.setSession("appId",appEntity.getId());
				return new SimpleAuthenticationInfo(manager, manager.getManagerPassword(), getName());
			}
			LambdaQueryWrapper<SiteAppManagerEntity> wrapper = new QueryWrapper<SiteAppManagerEntity>().lambda();
			wrapper.eq(SiteAppManagerEntity::getManagerId, manager.getId());
			List<SiteAppManagerEntity> siteAppManagerList = siteAppManagerBiz.list(wrapper);
			for (SiteAppManagerEntity siteApp : siteAppManagerList) {
				if (appEntity == null){
					break;
				}
				if (siteApp.getAppId() == appEntity.getIntId()) {
					BasicUtil.setSession(Const.APP,appEntity);
					BasicUtil.setSession("appId",appEntity.getId());
					return new SimpleAuthenticationInfo(manager, manager.getManagerPassword(), getName());
				}
			}
			if (CollUtil.isNotEmpty(siteAppManagerList)){
				appEntity = appBiz.getById(siteAppManagerList.get(0).getAppId());
				BasicUtil.setSession(Const.APP,appEntity);
				BasicUtil.setSession("appId",appEntity.getId());
				return new SimpleAuthenticationInfo(manager, manager.getManagerPassword(), getName());
			}
		}
		throw new BusinessException(HttpStatus.FORBIDDEN,"当前管理员没有绑定站点");
	}

	/**
	 * 功能操作授权
	 * 此处决定功能菜单是否显示
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		ManagerEntity newManager = (ManagerEntity) principalCollection.fromRealm(getName()).iterator().next();
		ManagerEntity manager = managerBiz.getById(newManager.getIntId());
		if (manager == null) {
			return null;
		}
		SimpleAuthorizationInfo result = new SimpleAuthorizationInfo();
		List<ModelEntity> models = new ArrayList<>();
		if (ManagerAdminEnum.SUPER.toString().equals(manager.getManagerAdmin())){
			models = modelBiz.list();

			for (ModelEntity e:models) {
				if (!StringUtils.isEmpty(e.getModelUrl())) {
					result.addStringPermission(e.getModelUrl());
				}
			}
			return result;
		}
		// 获取所有角色的权限
		String[] split = manager.getRoleIds().split(",");
		List<String> managerRoleIds = Arrays.asList(split);
		RoleEntity roleEntity = new RoleEntity();
		List<RoleEntity> roleList = roleBiz.query(roleEntity);
		List<String> allRoleIdList = roleList.stream().map(RoleEntity::getId).collect(Collectors.toList());
		// 两个集合中共同的元素，获取当前角色列表是否隶属当前站点
		Collection<String> intersection = CollectionUtil.intersection(managerRoleIds, allRoleIdList);
		if(CollectionUtil.isNotEmpty(intersection)){
			int[] roleIds = intersection.stream().mapToInt(Integer::valueOf).toArray();
			HashSet<ModelEntity> modelEntitySet = new HashSet<>();
			for (int roleId : roleIds) {
				modelEntitySet.addAll(modelBiz.queryModelByRoleId(roleId));
			}
			// 获得去除重复权限后的权限菜单
			models = new ArrayList<>(modelEntitySet);
		}
		for (ModelEntity modelEntity:models) {
			if (StringUtils.isNotBlank(modelEntity.getModelUrl())) {
				result.addStringPermission(modelEntity.getModelUrl());
			}
		}
		//根据角色集合查询用户所拥有的的model权限
		for (ModelEntity e:models) {
			if (!StringUtils.isEmpty(e.getModelUrl())) {
				result.addStringPermission(e.getModelUrl());
			}
		}
		return result;
	}
}
