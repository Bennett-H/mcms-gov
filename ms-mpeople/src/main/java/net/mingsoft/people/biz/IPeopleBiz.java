/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.people.constant.e.LoginTypeEnum;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleUserEntity;


/**
 * 
 * 用户业务层，继承IBaseBiz
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public interface IPeopleBiz  extends IBaseBiz{

	/**
	 * 根据用户ID删除用户实体，用于有子类的删除操作
	 * @param id 用户ID
	 */
	public void deletePeople(int id);	
	
	
	/**
	 * 批量删除用户
	 * @param peopleIds 用户id集合
	 */
	public void deletePeople(int[] peopleIds);	
	
	/**
	 * 更具用户实体获取用户信息,
	 * @param people 用户实体，可以设置用户名、用户邮箱、用户手机号
	 * @return 用户实体
	 */
	PeopleEntity getByPeople(PeopleEntity people);
	

	/**
	 * 根据用户名(帐号,手机,邮箱)和验证码查询用户信息开始
	 * @param userName 用户名
	 * @param peopleCode 验证码
	 * @return 用户实体
	 */
	public PeopleEntity getEntityByCode(String userName,String peopleCode);
	
	/**
	 * 根据用户用户名查询用户实体</br>
	 * @param userName 用户名(注:手机号,邮箱)
	 * @return 查询到的用户实体
	 */
	PeopleEntity getEntityByMailOrPhone(String userName);
	
	/**
	 * 根据用户用户名查询用户实体</br>
	 * @param userName 用户名(注:手机号,邮箱,用户名称都可作为用户名登录)
	 * @return 查询到的用户实体
	 */
	public PeopleEntity getEntityByUserName(String userName);
	

	/**
	 * 用户有子类添加
	 * @param people 用户实体
	 * @return 用户ID
	 */
	public int savePeople(PeopleEntity people);
	
	/**
	 * 根据用户ID进行用户实体的更新，用于有子类的更新操作
	 * @param people 用户实体
	 */
	public void updatePeople(PeopleEntity people);

	/**
	 * 判断账号是否存在，会依次判断类型 PeopleEntity、登陆账号、邮箱，只验证已经绑定的账号信息
	 * @param people 这里的 loginName 可能是账号peopleName、手机号peoplePhone、邮箱peopleMail
	 * @return   true：存在，false:不存在
	 */
	boolean isExists(PeopleUserEntity people);


	/**
	 * 判断账号是否存在，方法里面通过组织 PeopleEntity 再调用 isExists(people) 方法进行验证
	 * @param loginName 这里的 loginName 可能是账号、手机号、邮箱
	 * @param loginType 登陆类型
	 * @return   true：存在，false:不存在
	 */
	boolean isExists(String loginName, LoginTypeEnum loginType);

}
