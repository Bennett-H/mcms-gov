/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import net.mingsoft.base.entity.BaseEntity;
import java.util.Date;

 /**
 * 群发消息实体
 * @author 铭飞开发团队
 * 创建日期：2019-6-6 12:03:07<br/>
 * 历史修订：<br/>
 */
public class GroupMessageEntity extends MessageEntity {

	private static final long serialVersionUID = 1559793787728L;
	
	/**
	 * 关联主键
	 */
	private Integer gmPmId;
	/**
	 * 发送时间
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date gmSendTime; 
	/**
	 * 发送对象组
	 */
	private String gmSendGroup; 
	
		
	/**
	 * 设置关联主键
	 */
	public void setGmPmId(Integer gmPmId) {
		this.gmPmId = gmPmId;
	}

	/**
	 * 获取关联主键
	 */
	public Integer getGmPmId() {
		return this.gmPmId;
	}
	/**
	 * 设置发送时间
	 */
	public void setGmSendTime(Date gmSendTime) {
		this.gmSendTime = gmSendTime;
	}

	/**
	 * 获取发送时间
	 */
	public Date getGmSendTime() {
		return this.gmSendTime;
	}
	/**
	 * 设置发送对象组
	 */
	public void setGmSendGroup(String gmSendGroup) {
		this.gmSendGroup = gmSendGroup;
	}

	/**
	 * 获取发送对象组
	 */
	public String getGmSendGroup() {
		return this.gmSendGroup;
	}
}
