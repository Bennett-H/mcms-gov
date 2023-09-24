/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.constant.e;

/**
 * 用户关注的渠道来源枚举
 */
public enum UserSubscribeSceneEnum {

    /**
     *公众号搜索
     */
    ADD_SCENE_SEARCH("ADD_SCENE_SEARCH","公众号搜索"),
    /**
     * 公众号迁移
     */
    ADD_SCENE_ACCOUNT_MIGRATION("ADD_SCENE_ACCOUNT_MIGRATION","公众号迁移"),
    /**
     *名片分享
     */
    ADD_SCENE_PROFILE_CARD("ADD_SCENE_PROFILE_CARD","名片分享"),
    /**
     * 扫描二维码
     */
    ADD_SCENE_QR_CODE("ADD_SCENE_QR_CODE","扫描二维码"),
    /**
     * 图文页内名称点击
     */
    ADD_SCENE_PROFILE_LINK("ADD_SCENE_PROFILE_LINK","图文页内名称点击"),
    /**
     * 图文页右上角菜单
     */
    ADD_SCENE_PROFILE_ITEM("ADD_SCENE_PROFILE_ITEM","图文页右上角菜单"),
    /**
     * 支付后关注
     */
    ADD_SCENE_PAID("ADD_SCENE_PAID","支付后关注"),
    /**
     * 微信广告
     */
    ADD_SCENE_WECHAT_ADVERTISEMENT("ADD_SCENE_WECHAT_ADVERTISEMENT","微信广告"),
    /**
     * 他人转载
     */
    ADD_SCENE_REPRINT("ADD_SCENE_REPRINT","他人转载"),
    /**
     * 视频号直播
     */
    ADD_SCENE_LIVESTREAM("ADD_SCENE_LIVESTREAM","视频号直播"),
    /**
     * 视频号
     */
    ADD_SCENE_CHANNELS("ADD_SCENE_CHANNELS","视频号"),

    /**
     * 其他
     */
    ADD_SCENE_OTHERS("ADD_SCENE_OTHERS","其他");

    UserSubscribeSceneEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    private String code;

    private String name;



    /**
     * 根据code获得对应的name
     * @param code
     * @return
     */
   public static String getNameByCode(String code){
       for (UserSubscribeSceneEnum subscribeSceneEnum : values()) {
           if (subscribeSceneEnum.code.equals(code)){
               return subscribeSceneEnum.name;
           }
       }
       return null;
   }

}
