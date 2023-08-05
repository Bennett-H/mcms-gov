/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.base.entity;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.mingsoft.base.constant.e.DeleteEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName:  BaseEntity
 * @Description:TODO(基础实体类，其他所有实体都需要继承)
 * @author: 铭飞开发团队
 * @date:   2018年3月19日 下午3:36:17
 *历史修订： 2022-02-17 setOrderBy() 修复orderBy字段可能存在的sql注入问题
 *
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
public abstract class  BaseEntity implements Serializable{

	/**
	 * 创建用户编号
	 */
	protected String createBy;
	/**
	 * 创建日期
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	protected Date createDate;

	/**
	 * 标记
	 */
	protected Integer del=0;

	/**
	 * 实体编号（唯一标识）
	 */
	protected String id;

	/**
	 * 备注
	 */
	@TableField(exist = false)
	protected String remarks;

	/**
	 * 最后更新用户编号
	 */
	protected String updateBy;

	/**
	 * 最后更新日期
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	protected Date updateDate;

	/**
	 * 自定义SQL where条件，需要配合对应dao.xml使用
	 */
	@JsonIgnore
	@TableField(exist = false)
	protected String sqlWhere;

	/**
	 * 自定义SQL where条件，需要配合对应dao.xml使用
	 */
	@JsonIgnore
	@TableField(exist = false)
	protected String sqlDataScope;

	/**
	 * 排序字段
	 */
	@JsonIgnore
	@TableField(exist = false)
	protected String orderBy;

	/**
	 * 排序方式
	 */
	@TableField(exist = false)
	protected String order;


	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getDel() {
		return del;
	}

	@JsonIgnore
	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	public void setDel(DeleteEnum del) {
		this.del = del.toInt();
	}

	public void setDel(Integer del) {
		this.del = del;
	}

	public String getId() {
		if(StringUtils.isEmpty(this.id) || this.id.equals("0")){
			return null;
		}
		return this.id;
	}

	/**
	 * 方便业务层获取id,为空返回null
	 * @return
	 */
	public Integer getIntegerId(){
		if(StringUtils.isEmpty(this.getId())){
			return null;
		}else {
			return Integer.parseInt(this.getId());
		}
	}

	/**
	 * 方便业务层获取id,为空返回0
	 * @return id
	 */
	public int getIntId(){
		if(StringUtils.isEmpty(this.getId())){
			return 0;
		}else {
			return Integer.parseInt(this.getId());
		}
	}


	/**
	 *
	 * @param id
	 */
	public void setId(String id) {
		if(StringUtils.isEmpty(id) || id.equals("0")){
			id = null;
		}
		this.id = id;
	}

	/**
	 * 方便业务层设置id
	 * @return
	 */
	@JsonIgnore
	public void setIntegerId(Integer id) {
		this.id = String.valueOf(id);
	}

	/**
	 * 方便业务层设置id
	 * @return
	 */
	@JsonIgnore
	public void setIntId(int id) {
		this.id = String.valueOf(id);
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@JsonIgnore
	public String getSqlWhere() {
		return sqlWhere;
	}

	@JsonIgnore
	public List getSqlWhereList() {
		if(StringUtils.isNotBlank(sqlWhere)){
			try {
				return JSONUtil.toList(sqlWhere,Map.class);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return Collections.EMPTY_LIST;
	}

	public void setSqlWhere(String sqlWhere) {
		this.sqlWhere = sqlWhere;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		//若orderBy存在空格可能有sql注入问题
		if (orderBy != null){
			orderBy = orderBy.replaceAll(" ","");
		}
		this.orderBy = orderBy;
	}


	public String getSqlDataScope() {
		return sqlDataScope;
	}

	public void setSqlDataScope(String sqlDataScope) {
		this.sqlDataScope = sqlDataScope;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}



}
