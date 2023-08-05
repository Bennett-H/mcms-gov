/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.entity;

import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;

/**
 * 管理员角色表
 * @author 铭软团队
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
@TableName("role")
public class RoleEntity extends BaseEntity {

	/**
	 * 角色名称
	 */
	private String roleName;

	/**
	 * 不允许删除标记
	 */
	@TableField(whereStrategy = FieldStrategy.NEVER)
	private int notDel;

	/**
	 *获取 roleName
	 * @return roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 *设置roleName
	 * @param roleName
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getNotDel() {
		return notDel;
	}

	public void setNotDel(int notDel) {
		this.notDel = notDel;
	}
}
