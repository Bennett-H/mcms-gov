/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.constant.e;

/**
 * @package: net.mingsoft.mweixin.constant.e
 * @description: 菜单类型枚举
 * @author: jemor1999@qq.com
 * @create: 2020-07-02 17:54
 **/
public enum MenuStyleEnum {
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
    VIDEO("video"),
    /**
     * 链接
     */
    LINK("link"),
    /**
     * 小程序
     */
    MINIPROGRAM("miniprogram");

    private final String value;

    MenuStyleEnum(String value){
        this.value = value;
    }

    /**
     * 根据值获取枚举对象
     * @param value
     * @return
     */
    public static MenuStyleEnum getValue(String value){
        for (MenuStyleEnum newTypeEnum:
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
