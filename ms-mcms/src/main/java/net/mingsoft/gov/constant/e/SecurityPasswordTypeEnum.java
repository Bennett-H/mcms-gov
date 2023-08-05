/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.constant.e;

/**
 * @Author: xierz
 * @Description:
 * @Date: Create in 2021/03/12 16:32
 */
public enum SecurityPasswordTypeEnum {
    PASS_WORD_TYPE("pwd");
    //业务类型
    private String passWordType;

    SecurityPasswordTypeEnum(String passWordType) {
        this.passWordType = passWordType;
    }

    public String getPassWordType() {
        return passWordType;
    }
}
