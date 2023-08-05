/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.entity.RoleModelEntity;

import java.util.List;

/**
 * 角色模块关联持久化层，接口，继承IBaseDao
 * @author 张敏
 * @version 
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
public interface IRoleModelDao extends IBaseDao<RoleModelEntity>{
	
	/**
	 * 保存该角色对应的模块集合
	 * @param roleModelList 集合
	 * 过期理由：批量保存不适配oracle
	 */
	@Deprecated
	public void saveEntity(List<RoleModelEntity> roleModelList);
	
	/**
	 * 更新该角色对应的模块集合
	 * @param roleModelList 集合
	 */
	public void updateEntity(List<RoleModelEntity> roleModelList);
	
	/**
	 * 根据角色编号删除对应功能
	 * @param id 角色编号
	 */
	public void deleteByRoleId(int id);
	
	/**
	 * 通过角色获取所有关联的模块id
	 * @param roleId
	 */
	public List<RoleModelEntity> queryByRoleId(int roleId);

    void deleteByRoleIds(int[] ids);
}
