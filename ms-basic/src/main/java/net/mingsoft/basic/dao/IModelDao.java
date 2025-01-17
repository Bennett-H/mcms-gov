/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.entity.ModelEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模块持久化
 * @author ms dev group
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
public interface IModelDao extends IBaseDao<ModelEntity>{

	/**
	 * 根据角色ID查询模块集合
	 * @param roleId 角色ID
	 * @return 返回模块集合
	 */
	List<ModelEntity> queryModelByRoleId(int roleId);

	/**
	 * 根据模块编号查询模块实体
	 * @deprecated ModelCode字段以后不再使用了
	 * @param modelCode 模块编号
	 * @return 返回模块实体
	 */
	@Deprecated
	ModelEntity getEntityByModelCode(@Param("modelCode")String modelCode);

	/**
	 * 根据模块id获取当前项目中的分类模块id，规则根据modelcode定。**99******,只用是第３位与第４位９９
	 * @param modelCodeRegex 规则。详细见IModelBiz
	 * @see IModelBiz
	 * @param modelId 模块根id
	 * @return 返回模块集合
	 */
	ModelEntity getModel(@Param("modelCodeRegex") String modelCodeRegex,@Param("modelId") int modelId);


}
