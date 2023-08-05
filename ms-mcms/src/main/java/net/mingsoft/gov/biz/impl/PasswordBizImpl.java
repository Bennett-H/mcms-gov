/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.gov.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.gov.biz.IPasswordBiz;
import net.mingsoft.gov.constant.e.SecurityPasswordTypeEnum;
import net.mingsoft.gov.dao.IPasswordDao;
import net.mingsoft.gov.entity.PasswordEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 密码修改记录管理持久化层
 * @author 铭软科技
 * 创建日期：2021-3-12 15:06:18<br/>
 * 历史修订：<br/>
 * 2022-1-22 modifyPasswordByTimeTips增加自定义配置
 */
@Service("govpasswordBizImpl")
public class PasswordBizImpl extends BaseBizImpl<IPasswordDao,PasswordEntity> implements IPasswordBiz {


	@Autowired
	private IPasswordDao passwordDao;

	@Autowired
	private IManagerBiz managerBiz;

	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return passwordDao;
	}

	@Override
	public boolean modifyPasswordForFirst() {
		PasswordEntity passwordEntity = new PasswordEntity();
		passwordEntity.setPasswordOwnerId(BasicUtil.getManager().getId());
		passwordEntity.setPasswordType(SecurityPasswordTypeEnum.PASS_WORD_TYPE.getPassWordType());

		List<PasswordEntity> passwordLogList = super.list(new QueryWrapper<>(passwordEntity).lambda().orderByDesc(PasswordEntity::getCreateDate));
		if (CollectionUtil.isEmpty(passwordLogList) || passwordLogList.size()==0) {
			// 新用户登录时需要修改密码
			String enable = ConfigUtil.getString("安全设置", "firstUpdatePasswordEnable", "false");
			if (Boolean.parseBoolean(enable)){
				return true;
			} else {
				return false;
			}
		}
		return false;

	}

	@Override
	public int modifyPasswordForTime() {
		PasswordEntity passwordEntity = new PasswordEntity();
		passwordEntity.setPasswordOwnerId(BasicUtil.getManager().getId());
		passwordEntity.setPasswordType(SecurityPasswordTypeEnum.PASS_WORD_TYPE.getPassWordType());

		List<PasswordEntity> passwordLogList = super.list(new QueryWrapper<>(passwordEntity).lambda().orderByDesc(PasswordEntity::getCreateDate));
		//拿到其最后一次修改密码的记录时间
		if(passwordLogList.size()>0) {
			PasswordEntity passwordLog = passwordLogList.get(0);
			Date createDate = passwordLog.getCreateDate();
			int _day = (int)DateUtil.between(createDate,DateUtil.date(), DateUnit.DAY)-1;
			return _day-ConfigUtil.getInt("安全设置","passwordMaxDay");
		} else {
			//如果关闭新用户必须修改密码，就需要通过管理员当创建时间来判断
			Date createDate =BasicUtil.getManager().getCreateDate();
			int _day = (int)DateUtil.between(createDate,DateUtil.date(), DateUnit.DAY)-1;
			return _day-ConfigUtil.getInt("安全设置","passwordMaxDay");
		}
	}


	@Override
	public boolean  modifyPasswordForRepeat(int number, PasswordEntity passwordEntity, String newPassWord) {
		BasicUtil.startPage(1,number,true);
		List<PasswordEntity> passwordLogList  =  super.list(new QueryWrapper<>(passwordEntity).lambda().orderByDesc(PasswordEntity::getCreateDate));
		if (CollectionUtil.isEmpty(passwordLogList))  {
			return false;
		}
		List<String> oldPwdList = passwordLogList.stream().map(PasswordEntity::getPasswordOwnerPwd).collect(Collectors.toList());
		return oldPwdList.contains(newPassWord);
	}

}
