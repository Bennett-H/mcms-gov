/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.entity;


import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;
/**
 * 场景二维码管理实体
 * @author 铭飞开发团队
 * 创建日期：2023-6-5 15:16:50<br/>
 * 历史修订：<br/>
 */
@TableName("WX_QR_CODE")
public class QrCodeEntity extends BaseEntity {

	private static final long serialVersionUID = 1685949410127L;


	/**
	 * 雪花ID规则
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;


	/**
	 * 微信编号
	 */
	private String weixinId;

	/**
	 * 场景值id(名称)
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String qcSceneStr;

	/**
	 * 二维码类型
	 */
	private String qcActionName;

	/**
	 * 二维码有效期
	 */
	private Integer qcExpireSeconds;

	/**
	 * 实现类bean名称
	 */
	private String qcBeanName;


	/**
	 * 设置微信编号
	 */
	public void setWeixinId(String weixinId) {
		this.weixinId = weixinId;
	}

	/**
	 * 获取微信编号
	 */
	public String getWeixinId() {
		return this.weixinId;
	}
	/**
	 * 设置场景值id(名称)
	 */
	public void setQcSceneStr(String qcSceneStr) {
		this.qcSceneStr = qcSceneStr;
	}

	/**
	 * 获取场景值id(名称)
	 */
	public String getQcSceneStr() {
		return this.qcSceneStr;
	}
	/**
	 * 设置二维码类型
	 */
	public void setQcActionName(String qcActionName) {
		this.qcActionName = qcActionName;
	}

	/**
	 * 获取二维码类型
	 */
	public String getQcActionName() {
		return this.qcActionName;
	}
	/**
	 * 设置二维码有效期
	 */
	public void setQcExpireSeconds(Integer qcExpireSeconds) {
		this.qcExpireSeconds = qcExpireSeconds;
	}

	/**
	 * 获取二维码有效期
	 */
	public Integer getQcExpireSeconds() {
		return this.qcExpireSeconds;
	}
	/**
	 * 设置实现类bean名称
	 */
	public void setQcBeanName(String qcBeanName) {
		this.qcBeanName = qcBeanName;
	}

	/**
	 * 获取实现类bean名称
	 */
	public String getQcBeanName() {
		return this.qcBeanName;
	}


}
