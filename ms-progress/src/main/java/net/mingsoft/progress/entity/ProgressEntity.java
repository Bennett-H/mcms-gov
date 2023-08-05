/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.entity;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
* 进度表实体
* @author 铭飞科技
* 创建日期：2021-3-18 11:50:14<br/>
* 历史修订：<br/>
*/
@TableName("PROGRESS_PROGRESS")
public class ProgressEntity extends BaseEntity {

private static final long serialVersionUID = 1616039414961L;

	/**
	* 关联id
	*/
	private Integer schemeId;
	/**
	* 父节点
	*/
	private String progressId;
	/**
	* 进度节点名称
	*/
	private String progressNodeName;
	/**
	* 进度数
	*/
	private Integer progressNumber;
	/**
	* 进度状态
	*/
	private String progressStatus;
	/**
	 * 叶子节点
	 */
	private Boolean leaf;
	/**
	 * 父类型编号
	 */
	private String progressParentIds;

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public String getProgressParentIds() {
		return progressParentIds;
	}

	public void setProgressParentIds(String progressParentIds) {
		this.progressParentIds = progressParentIds;
	}

	/**
	* 设置关联id
	*/
	public void setSchemeId(Integer schemeId) {
	this.schemeId = schemeId;
	}

	/**
	* 获取关联id
	*/
	public Integer getSchemeId() {
	return this.schemeId;
	}
	/**
	* 设置父节点
	*/
	public void setProgressId(String progressId) {
	this.progressId = progressId;
	}

	/**
	* 获取父节点
	*/
	public String getProgressId() {
	return this.progressId;
	}
	/**
	* 设置进度节点名称
	*/
	public void setProgressNodeName(String progressNodeName) {
	this.progressNodeName = progressNodeName;
	}

	/**
	* 获取进度节点名称
	*/
	public String getProgressNodeName() {
	return this.progressNodeName;
	}
	/**
	* 设置进度数
	*/
	public void setProgressNumber(Integer progressNumber) {
	this.progressNumber = progressNumber;
	}

	/**
	* 获取进度数
	*/
	public Integer getProgressNumber() {
	return this.progressNumber;
	}
	/**
	* 设置进度状态
	*/
	public void setProgressStatus(String progressStatus) {
	this.progressStatus = progressStatus;
	}

	/**
	* 获取进度状态
	*/
	public String getProgressStatus() {
	return this.progressStatus;
	}

	/**
	 * 获取json状态中的value值
	 * @param key
	 * @return
	 */
	public String getProgressStatus(String key) {
		if(StringUtils.isNotBlank(key)){
			Map map = JSONUtil.toBean(progressStatus, Map.class);
			// 用于忽略大小写敏感
			Map treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
			treeMap.putAll(map);
			return treeMap.get(key).toString();
		}
		return this.progressStatus;
	}

}
