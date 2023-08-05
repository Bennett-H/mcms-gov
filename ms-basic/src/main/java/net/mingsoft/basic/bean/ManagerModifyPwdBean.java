/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.bean;

import cn.hutool.crypto.SecureUtil;
import net.mingsoft.basic.entity.ManagerEntity;

/**
 * @Author: xierz
 * @Description:
 * @Date: Create in 2021/03/13 11:55
 */
public class ManagerModifyPwdBean extends ManagerEntity {
    //输入的旧密码
    private String oldManagerPassword;
    //输入的新密码
    private String newManagerPassword;

    public String getOldManagerPassword() {
        return oldManagerPassword;
    }

    public void setOldManagerPassword(String oldManagerPassword) {
        super.setManagerPassword(oldManagerPassword);
        this.oldManagerPassword = oldManagerPassword;
    }

    public String getNewManagerPassword() {
        return newManagerPassword;
    }

    public void setNewManagerPassword(String newManagerPassword) {
        this.newManagerPassword = newManagerPassword;
    }


}
