/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.bean;

import net.mingsoft.mweixin.entity.FileEntity;

/**
 *
 */
public class FileBean extends FileEntity {

    /**
     * 是否同步至微信，用于查询
     */
    private String isSync;

    public String getIsSync() {
        return isSync;
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync;
    }
}
