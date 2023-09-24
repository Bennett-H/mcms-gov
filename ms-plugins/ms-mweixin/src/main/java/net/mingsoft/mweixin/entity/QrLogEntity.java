/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

/**
 * 场景二维码日志实体
 * @author 铭飞开发团队
 * 创建日期：2023-6-5 14:21:08<br/>
 * 历史修订：<br/>
 */
@TableName("WX_QR_LOG")
public class QrLogEntity extends BaseEntity {

	private static final long serialVersionUID = 1685946068417L;


    /**
     * 雪花ID规则
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 会员openId
     */
    private String openId;

    /**
     * 微信编号
     */
    private String weixinId;

    /**
     * 场景二维码编号
     */
    private String qcId;


    /**
     * 设置会员openId
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获取会员openId
     */
    public String getOpenId() {
        return this.openId;
    }

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
     * 设置场景二维码编号
     */
    public void setQcId(String qcId) {
        this.qcId = qcId;
    }

    /**
     * 获取场景二维码编号
     */
    public String getQcId() {
        return this.qcId;
    }


}
