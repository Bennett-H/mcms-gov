/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.biz;

import net.mingsoft.people.entity.PeopleUserEntity;

import java.util.List;


/**
 * 
 * 
 * <p>
 * <b>铭飞MS平台</b>
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014 - 2015
 * </p>
 * 
 * <p>
 * Company:景德镇铭飞科技有限公司
 * </p>
 * 
 * @author 刘继平
 * 
 * @version 300-001-001
 * 
 * <p>
 * 版权所有 铭飞科技
 * </p>
 *  
 * <p>
 * Comments: 
 * </p>
 *  
 * <p>
 * Create Date:2014-9-4
 * </p>
 *
 * <p>
 * Modification history:
 * </p>
 */

/**
 * 
 * 用户信息业务层接口，继承IPeopleBiz接口
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public interface IPeopleUserBiz extends IPeopleBiz  {

	/**
	 * 用户信息实体保存</br>
	 * 只能有子类继承时调用的</br>
	 * @param peopleEntity 用户信息
	 * @return 新增成功后用户ID
	 */
	public int savePeopleUser(PeopleUserEntity peopleEntity);
	
	/**
	 * 更新用户信息,</br>
	 * 只能在有子类时调用</br>
	 * @param peopleEntity 用户信息
	 */
	public void updatePeopleUser(PeopleUserEntity peopleEntity);
	
	/**
	 * 删除用户信息</br>
	 * 只能在有子类时调用</br>
	 * @param peopleId 用户ID
	 */
	public void deletePeopleUser(int peopleId);
	
	/**
	 * 批量删除用户
	 * @param peopleIds 用户id集合
	 */
	public void deletePeopleUsers(int[] peopleIds);

	/**
	 * 批量查询用户
	 * @param peopleIds 用户id集合
	 */
	public List<PeopleUserEntity> queryBatchIds(List<String>  peopleIds);

	/**
	 * 通过实体获取详情
	 * @param peopleUser
	 */
	public PeopleUserEntity getByEntity(PeopleUserEntity peopleUser);


}
