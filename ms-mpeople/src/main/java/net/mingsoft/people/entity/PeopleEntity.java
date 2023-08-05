/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.mingsoft.base.constant.e.BaseEnum;
import net.mingsoft.base.entity.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 * 用户基础表
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
@TableName("people")
public class PeopleEntity extends BaseEntity {

	/**
	 * 不参表结构 大于0表示自动登录
	 */
	@TableField(exist = false)
	private int peopleAutoLogin;

	/**
	 * 用户随机验证码
	 */
	private String peopleCode;

	/**
	 * 用户登录ip
	 */
	private String peopleIp;

	/**
	 * 发送验证码的时间
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@TableField(value = "PEOPLE_CODESENDDATE")
	private Date peopleCodeSendDate;

	/**
	 * 注册时间
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@TableField(value = "PEOPLE_DATETIME")
	private Date peopleDateTime;

	/**
	 * 用户邮箱</br>
	 * 可用作登录</br>
	 */
	private String peopleMail;

	/**
	 * 是否通过邮箱验证
	 */
	@TableField(value = "PEOPLE_MAILLCHECK")
	private int peopleMailCheck = -1;

	/**
	 * 登录帐号
	 */
	private String peopleName;


	/**
	 * 旧密码，不参与表结构
	 */
	@TableField(exist = false)
	@JsonIgnore
	private String peopleOldPassword;

	/**
	 * 登录密码
	 */
	@JsonIgnore
	private String peoplePassword;

	/**
	 * 用户电话</br>
	 * 可用作登录</br>
	 */
	private String peoplePhone;

	/**
	 * 是否通过手机验证
	 */
	@TableField(value = "PEOPLE_PHONECHECK")
	private int peoplePhoneCheck = -1;

	/**
	 * 用户状态 1.已审核 0.未审核
	 */
	private Integer peopleState;

	/**
	 * 用户真信息
	 */
	private PeopleUserEntity peopleUser;

	/**
	 * 是否为新用户，不参与表结构
	 */
	@TableField(exist = false)
	private boolean newUser;

	public boolean isNewUser() {
		return StringUtils.isBlank(this.getPeoplePassword());
	}

	public int getPeopleAutoLogin() {
		return peopleAutoLogin;
	}

	/**
	 * 获取用户随机码
	 *
	 * @return
	 */
	public String getPeopleCode() {
		return peopleCode;
	}

	public Date getPeopleCodeSendDate() {
		return peopleCodeSendDate;
	}

	/**
	 * 获取peopleDateTime
	 *
	 * @return peopleDateTime
	 */
	public Date getPeopleDateTime() {
		return peopleDateTime;
	}

	/**
	 * 获取peopleMail
	 *
	 * @return peopleMail
	 */
	public String getPeopleMail() {
		return peopleMail;
	}

	public int getPeopleMailCheck() {
		return peopleMailCheck;
	}

	/**
	 * 获取peopleName
	 *
	 * @return peopleName
	 */
	public String getPeopleName() {
		return peopleName;
	}

	public String getPeopleOldPassword() {
		return peopleOldPassword;
	}

	/**
	 * 获取peoplePassword
	 *
	 * @return peoplePassword
	 */
	public String getPeoplePassword() {
		return peoplePassword;
	}

	/**
	 * 获取peoplePhone
	 *
	 * @return peoplePhone
	 */
	public String getPeoplePhone() {
		return peoplePhone;
	}

	public int getPeoplePhoneCheck() {
		return peoplePhoneCheck;
	}

	/**
	 * 获取peopleState
	 *
	 * @return peopleState
	 */
	public Integer getPeopleState() {
		return peopleState;
	}

	public PeopleUserEntity getPeopleUser() {
		return peopleUser;
	}

	public void setPeopleAutoLogin(int peopleAutoLogin) {
		this.peopleAutoLogin = peopleAutoLogin;
	}

	/**
	 * 设置用户随机码
	 *
	 * @param peopleCode
	 */
	public void setPeopleCode(String peopleCode) {
		this.peopleCode = peopleCode;
	}

	public void setPeopleCodeSendDate(Date peopleCodeSendDate) {
		this.peopleCodeSendDate = peopleCodeSendDate;
	}

	/**
	 * 设置peopleDateTime
	 *
	 * @param peopleDateTime
	 */
	public void setPeopleDateTime(Date peopleDateTime) {
		this.peopleDateTime = peopleDateTime;
	}

	/**
	 * 设置peopleMail
	 *
	 * @param peopleMail
	 */
	public void setPeopleMail(String peopleMail) {
		this.peopleMail = peopleMail;
	}

	public void setPeopleMailCheck(BaseEnum check) {
		this.peopleMailCheck = check.toInt();
	}

	/**
	 * 推荐使用枚举类形参方法
	 *
	 * @param peopleMailCheck
	 */
	@Deprecated
	public void setPeopleMailCheck(int peopleMailCheck) {
		this.peopleMailCheck = peopleMailCheck;
	}

	/**
	 * 设置peopleName
	 *
	 * @param peopleName
	 */
	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
	}

	public void setPeopleOldPassword(String peopleOldPassword) {
		this.peopleOldPassword = peopleOldPassword;
	}

	/**
	 * 设置peoplePwd
	 *
	 * @param peoplePassword
	 */
	public void setPeoplePassword(String peoplePassword) {
		this.peoplePassword = peoplePassword;
	}

	/**
	 * 设置peoplePhone
	 *
	 * @param peoplePhone
	 */
	public void setPeoplePhone(String peoplePhone) {
		this.peoplePhone = peoplePhone;
	}


	public void setPeoplePhoneCheck(BaseEnum check) {
		this.peoplePhoneCheck = check.toInt();
	}

	/**
	 * 推荐使用枚举类形参方法
	 *
	 * @param peoplePhoneCheck
	 */
	@Deprecated
	public void setPeoplePhoneCheck(int peoplePhoneCheck) {
		this.peoplePhoneCheck = peoplePhoneCheck;
	}

	/**
	 * 设置peopleState
	 */
	@JsonIgnore
	public void setPeopleStateEnum(BaseEnum e) {
		this.peopleState = e.toInt();
	}

	/**
	 * 设置peopleState，控制层推荐使用setPeopleState(BaseEnum e) 方法
	 *
	 * @param peopleState
	 */
	public void setPeopleState(Integer peopleState) {
		this.peopleState = peopleState;
	}

	public void setPeopleUser(PeopleUserEntity peopleUser) {
		this.peopleUser = peopleUser;
	}

	/**
	 * 获取用户ip
	 */
	public String getPeopleIp() {
		return peopleIp;
	}
	/**
	 *设置用户ip
	 */
	public void setPeopleIp(String peopleIp) {
		this.peopleIp = peopleIp;
	}



}
