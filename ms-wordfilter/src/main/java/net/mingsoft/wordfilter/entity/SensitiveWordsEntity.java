/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.wordfilter.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

/**
* 敏感词实体
* @author 铭软科技
* 创建日期：2021-1-7 15:54:50<br/>
* 历史修订：<br/>
*/
@TableName("WORDFILTER_SENSITIVE_WORDS")
public class SensitiveWordsEntity extends BaseEntity {

private static final long serialVersionUID = 1610006090505L;

	/**
	* 敏感词
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String word;
	/**
	* 替换词
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String replaceWord;
	/**
	 * 词汇类型
	 */
	private String wordType;


	/**
	* 设置敏感词
	*/
	public void setWord(String word) {
	this.word = word;
	}

	/**
	* 获取敏感词
	*/
	public String getWord() {
	return this.word;
	}
	/**
	* 设置替换词
	*/
	public void setReplaceWord(String replaceWord) {
	this.replaceWord = replaceWord;
	}

	/**
	* 获取替换词
	*/
	public String getReplaceWord() {
	return this.replaceWord;
	}

	/**
	 * 获取词汇类型
	 */
	public String getWordType() {
		return wordType;
	}

	/**
	 * 设置词汇类型
	 */
	public void setWordType(String wordType) {
		this.wordType = wordType;
	}
}
