/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.bean;

import net.mingsoft.mweixin.entity.QrLogEntity;

public class QrLogBean extends QrLogEntity {

    /**
     * 场景值id(名称)
     */
    private String qcSceneStr;

    /**
     * 会员编号
     */
    private String peopleId;


    /**
     * 获取场景值id(名称)
     */
    public String getQcSceneStr() {
        return qcSceneStr;
    }

    /**
     * 设置场景值id(名称)
     */
    public void setQcSceneStr(String qcSceneStr) {
        this.qcSceneStr = qcSceneStr;
    }

    /**
     * 获取会员编号
     */
    public String getPeopleId() {
        return peopleId;
    }

    /**
     * 设置会员编号
     */
    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }
}
