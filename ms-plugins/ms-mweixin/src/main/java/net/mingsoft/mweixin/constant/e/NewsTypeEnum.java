/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 铭飞科技
 * Copyright: Copyright (c) 2014 - 2015
 * @author 史爱华
 * Comments:消息回复类型
 * Create Date:2015-1-19
 * Modification history:
 * *imageText、单图文 </br>  moreImageText、多图文</br> text、文本</br> image、图片</br>
 */
public enum NewsTypeEnum {
	/**
	 *单图文
	 */
	IMAGE_TEXT("imageText"),
	/**
	 * 多图文
	 */
	MORE_IMAGE_TEXT("moreImageText"),
	/**
	 *文本
	 */
	TEXT("text"),
	/**
	 * 图片
	 */
	IMAGE("image"),
	/**
	 * 声音
	 */
	VOICE("voice"),
	/**
	 * 视频
	 */
	VIDEO("video");



	NewsTypeEnum(String value) {
		this.value = value;
	}

	private String value;

	/**
	 * 根据值获取枚举对象
	 * @param value
	 * @return
	 */
	public static NewsTypeEnum getValue(String value){
		for (NewsTypeEnum newTypeEnum:
				values()) {
			if(newTypeEnum.toString().equals(value)){
				return newTypeEnum;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return value;
	}

}
