/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 关注记录实体
* @author 铭飞开发团队
* 创建日期：2019-11-22 14:34:49<br/>
* 历史修订：<br/>
*/
@TableName("COLLECTION")
public class CollectionEntity extends BaseEntity {

	private static final long serialVersionUID = 1574404489302L;

	/**
	 * id主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 用户编号
	 */
	private Integer peopleId;
	/**
	 * 业务编号
	 */
	private String dataId;
	/**
	 * 业务类型
	 */
	private String dataType;
	/**
	 * 用户名称
	 */
	private String peopleName;
	/**
	 * 用户拓展信息
	 */
	private String peopleInfo;
	/**
	 * 业务名称
	 */
	private String collectionDataTitle;
	/**
	 * 业务链接
	 */
	private String collectionDataUrl;
	/**
	 * 业务扩展信息
	 */
	private String collectionDataJson;

	/**
	 * 商品图片
	 */
	private String collectionDataImg;

	/**
	 * 关注IP
	 */
	private String collectionIp;


	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置用户编号
	 */
	public void setPeopleId(Integer peopleId) {
		this.peopleId = peopleId;
	}

	/**
	 * 获取用户编号
	 */
	public Integer getPeopleId() {
		return this.peopleId;
	}

	/**
	 * 设置业务编号
	 */
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	/**
	 * 获取业务编号
	 */
	public String getDataId() {
		return this.dataId;
	}

	/**
	 * 设置业务类型
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * 获取业务类型
	 */
	public String getDataType() {
		return this.dataType;
	}

	/**
	 * 设置用户名称
	 */
	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
	}

	/**
	 * 获取用户名称
	 */
	public String getPeopleName() {
		return this.peopleName;
	}

	/**
	 * 设置业务名称
	 */
	public void setCollectionDataTitle(String collectionDataTitle) {
		this.collectionDataTitle = collectionDataTitle;
	}

	/**
	 * 获取业务名称
	 */
	public String getCollectionDataTitle() {
		return this.collectionDataTitle;
	}

	/**
	 * 设置业务链接
	 */
	public void setCollectionDataUrl(String collectionDataUrl) {
		this.collectionDataUrl = collectionDataUrl;
	}

	/**
	 * 获取业务链接
	 */
	public String getCollectionDataUrl() {
		return this.collectionDataUrl;
	}

	/**
	 * 设置业务扩展信息
	 */
	public void setCollectionDataJson(String collectionDataJson) {
		this.collectionDataJson = collectionDataJson;
	}

	/**
	 * 获取业务扩展信息
	 */
	public String getCollectionDataJson() {
		return this.collectionDataJson;
	}

	/**
	 * 设置用户拓展信息
	 */
	public void setPeopleInfo(String peopleInfo) {
		this.peopleInfo = peopleInfo;
	}

	/**
	 * 获取用户拓展信息
	 */
	public String getPeopleInfo() {
		return this.peopleInfo;
	}

	/**
	 * 设置商品图片
	 */
	public void setCollectionDataImg(String collectionDataImg) {
		this.collectionDataImg = collectionDataImg;
	}

	/**
	 * 获取商品图片
	 */
	public String getCollectionDataImg() {
		return this.collectionDataImg;
	}

	/**
	 * 获取IP
	 */
	public String getCollectionIp() {
		return collectionIp;
	}

	/**
	 * 设置IP
	 */
	public void setCollectionIp(String collectionIp) {
		this.collectionIp = collectionIp;
	}
}
