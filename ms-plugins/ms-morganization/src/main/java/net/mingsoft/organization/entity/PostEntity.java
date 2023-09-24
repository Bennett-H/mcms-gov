/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 岗位管理实体
* @author 铭飞开源团队
* 创建日期：2020-1-6 18:25:28<br/>
* 历史修订：<br/>
*/
@TableName("ORGANIZATION_POST")
public class PostEntity extends BaseEntity {

	private static final long serialVersionUID = 1578306328716L;

	/**
	* 岗位名称
	*/
	private String postName;
	/**
	* 岗位编号
	*/
	private String postCode;
	/**
	* 岗位描述
	*/
	private String postDesc;

	/**
	 * 岗位人数
	 */
	@TableField(exist = false)
	private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
	* 设置岗位名称
	*/
	public void setPostName(String postName) {
	this.postName = postName;
	}

	/**
	* 获取岗位名称
	*/
	public String getPostName() {
	return this.postName;
	}
	/**
	* 设置岗位编号
	*/
	public void setPostCode(String postCode) {
	this.postCode = postCode;
	}

	/**
	* 获取岗位编号
	*/
	public String getPostCode() {
	return this.postCode;
	}
	/**
	* 设置岗位描述
	*/
	public void setPostDesc(String postDesc) {
	this.postDesc = postDesc;
	}

	/**
	* 获取岗位描述
	*/
	public String getPostDesc() {
	return this.postDesc;
	}
}
