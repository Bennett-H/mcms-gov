/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.biz.IRoleModelBiz;
import net.mingsoft.basic.dao.IRoleModelDao;
import net.mingsoft.basic.entity.RoleModelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 角色模块关联业务层接口实现类
 * @author 张敏
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
@Service("roleModelBiz")
@Transactional
public class RoleModelBizImpl extends BaseBizImpl<IRoleModelDao, RoleModelEntity> implements IRoleModelBiz {

	/**
	 * 角色模块关联持久化层
	 */
	@Autowired
	private IRoleModelDao roleModelDao;

	/**
	 * 获取角色模块持久化层
	 * @return roleModelDao 返回角色模块持久化层
	 */
	@Override
	public IBaseDao getDao() {
		// TODO Auto-generated method stub
		return roleModelDao;
	}

	@Override
	public void saveEntity(List<RoleModelEntity> roleModelList){
		// TODO Auto-generated method stub
		roleModelDao.saveEntity(roleModelList);
	}

	@Override
	public void updateEntity(List<RoleModelEntity> roleModelList){
		// TODO Auto-generated method stub
		roleModelDao.updateEntity(roleModelList);
	}

	@Override
	public List<RoleModelEntity> queryByRoleId(int roleId) {
		// TODO Auto-generated method stub
		return roleModelDao.queryByRoleId(roleId);
	}

	@Override
	public void deleteByRoleId(int roleId) {
		roleModelDao.deleteByRoleId(roleId);
	}

	@Override
	public void deleteByRoleIds(int[] ids) {
		roleModelDao.deleteByRoleIds(ids);
	}


}
