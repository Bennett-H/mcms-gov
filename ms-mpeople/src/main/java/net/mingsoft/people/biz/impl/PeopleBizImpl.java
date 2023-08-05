/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.base.exception.BusinessException;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.people.biz.IPeopleBiz;
import net.mingsoft.people.constant.e.LoginTypeEnum;
import net.mingsoft.people.dao.IPeopleDao;
import net.mingsoft.people.dao.IPeopleUserDao;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 用户业务层实现类
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
@Service("peopleBiz")
@Transactional
public class PeopleBizImpl  extends BaseBizImpl implements IPeopleBiz{

	/**
	 * 用户持久化层
	 */
	@Autowired
	private IPeopleDao peopleDao;

	@Autowired
	private IPeopleUserDao peopleUserDao;
	
	/**
	 * 获取peopleDao
	 */
	@Override
	protected IBaseDao getDao() {
		return peopleDao;
	}

	/**
	 * 用户有子类增加
	 */
	@Override
	public int savePeople(PeopleEntity people) {
		peopleDao.insert(people);
	    return saveEntity(people);
	}
	
	/**
	 * 根据用户ID进行用户实体的更新，用于有子类的更新操作
	 * @param people
	 */	
	@Override
	public void updatePeople(PeopleEntity people) {
		//修改子类子类时，people中没有参数，保证执行updateSQL语句是一定正确
		peopleDao.updateEntity(people);
		updateEntity(people);
	}	
	
	/**
	 * 用户删除
	 */
	@Override
	public void deletePeople(int id) {
		peopleDao.deleteEntity(id);
		deleteEntity(id);
	}	
	
	/**
	 * 根据用户用户名查询用户实体</br>
	 * @param userName 用户名(注:手机号,邮箱,用户名称都可作为用户名登录)
	 * @return 查询到的用户实体
	 */
	public PeopleEntity getEntityByUserName(String userName){
		return this.peopleDao.getEntityByUserName(userName);
	}	
	

	@Override
	public PeopleEntity getEntityByCode(String userName, String peopleCode) {
		// TODO Auto-generated method stub
		return this.peopleDao.getEntityByCode(userName, peopleCode);
	}


	@Override
	public void deletePeople(int[] peopleIds) {
		if(peopleIds==null){
			return;
		}
		this.peopleDao.delete(peopleIds);
		delete(peopleIds);
	}

	@Override
	public PeopleEntity getByPeople(PeopleEntity people) {
		// TODO Auto-generated method stub
		return peopleDao.getByPeople(people);
	}



	@Override
	public PeopleEntity getEntityByMailOrPhone(String userName) {
		// TODO Auto-generated method stub
		return peopleDao.getEntityByMailOrPhone(userName);
	}

	@Override
	public boolean isExists(PeopleUserEntity peopleUser) {
		if (peopleUser==null){
			throw new BusinessException("检测对象不可以为空");
		}
		// 是否为更新
		boolean isUpdate = peopleUser.getPeopleId()!=null && peopleUser.getPeopleId()>0;
		//获取更改前的用户
		PeopleUserEntity oldPeopleUser = new PeopleUserEntity();
		if (isUpdate){
			oldPeopleUser = (PeopleUserEntity) peopleUserDao.getEntity(peopleUser.getPeopleId());
		}
		//如果填写了邮箱，则验证邮箱格式是否正确
		if (!StringUtils.isBlank(peopleUser.getPeopleMail()) && !StringUtil.isEmail(peopleUser.getPeopleMail())) {
			return false;
		}
		//验证用户名不能为空
		if(StringUtils.isBlank(peopleUser.getPeopleName())){
			return false;
		}

		//如果填写了手机号码，则验证手机号码填写是否正确
		if (!StringUtils.isBlank(peopleUser.getPeoplePhone()) && !StringUtil.isMobile(peopleUser.getPeoplePhone())) {
			return false;
		}

		//当用户名进行修改时验证用户名是否是唯一的
		if (!StringUtils.isBlank(peopleUser.getPeopleName())) {
			// 验证用户名是否唯一
			PeopleEntity peopleName = peopleDao.getEntityByUserName(peopleUser.getPeopleName());
			//判断之前是否已经存在用户名，如果不存在，则判断是否存在重名，如果存在,判断用户是否更改用户名如果更改则判断新更改的用户名是否已经存在
			//判断填写的用户名和之前用户名是否相同，如果不相同
			if(StringUtils.isBlank(oldPeopleUser.getPeopleName())){
				if (peopleName != null) {
					return false;
				}
			}else{
				if(!oldPeopleUser.getPeopleName().equals(peopleUser.getPeopleName())){
					if (peopleName != null) {
						return false;
					}
				}
			}

		}
		if(!StringUtils.isBlank(peopleUser.getPeoplePhone())){
			PeopleEntity peoplePhone = peopleDao.getEntityByMailOrPhone(peopleUser.getPeoplePhone());
			//判断之前是否已经存在手机号，如果不存在，则判断是否存在重名，如果存在,判断用户是否更改手机号如果更改则判断新更改的手机号是否已经存在
			//判断填写的手机号和之前手机号是否相同，如果不相同
			if(StringUtils.isBlank(oldPeopleUser.getPeoplePhone())){
				if (peoplePhone != null) {
					return false;
				}
			}else{
				if(!oldPeopleUser.getPeoplePhone().equals(peopleUser.getPeoplePhone())){
					if (peoplePhone != null) {
						return false;
					}
				}
			}
		}
		//验证邮箱的唯一性
		if(!StringUtils.isBlank(peopleUser.getPeopleMail())){
			PeopleEntity peopleMail = peopleDao.getEntityByMailOrPhone(peopleUser.getPeopleMail());
			//判断之前是否已经存在邮箱(且绑定)，如果不存在，则判断是否存在重名，如果存在,判断用户是否更改邮箱如果更改则判断新更改的邮箱是否已经存在
			//判断填写的邮箱和之前邮箱是否相同，如果不相同
			if(StringUtils.isBlank(oldPeopleUser.getPeopleMail())){
				if (peopleMail != null) {
					return false;
				}
			}else{
				if(!oldPeopleUser.getPeopleMail().equals(peopleUser.getPeopleMail())){
					if (peopleMail != null) {
						return false;
					}
				}
			}
		}
		//验证用户身份证号码
		return true;
	}

	@Override
	public boolean isExists(String loginName, LoginTypeEnum loginType) {
		PeopleUserEntity peopleUser = new PeopleUserEntity();
		if (LoginTypeEnum.NAME.toString().equals(loginType.toString())){
			peopleUser.setPeopleName(loginName);
		}else if (LoginTypeEnum.MAIL.toString().equals(loginType.toString())){
			peopleUser.setPeopleMail(loginName);
		}else {
			peopleUser.setPeoplePhone(loginName);
		}

		return isExists(peopleUser);
	}
}
