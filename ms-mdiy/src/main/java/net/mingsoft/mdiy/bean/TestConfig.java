/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.bean;

/**
 * @Author: xierz
 * @Description:
 * @Date: Create in 2021/05/18 18:25
 */
public class TestConfig {
    String mailType;
    String mailName ;
    String mailForm;
    String mailFormName;
    String mailPassword;

    public TestConfig() {
    }

    public TestConfig(String mailType, String mailName, String mailForm, String mailFormName, String mailPassword) {
        this.mailType = mailType;
        this.mailName = mailName;
        this.mailForm = mailForm;
        this.mailFormName = mailFormName;
        this.mailPassword = mailPassword;
    }

    public String getMailType() {
        return mailType;
    }

    public void setMailType(String mailType) {
        this.mailType = mailType;
    }

    public String getMailName() {
        return mailName;
    }

    public void setMailName(String mailName) {
        this.mailName = mailName;
    }

    public String getMailForm() {
        return mailForm;
    }

    public void setMailForm(String mailForm) {
        this.mailForm = mailForm;
    }

    public String getMailFormName() {
        return mailFormName;
    }

    public void setMailFormName(String mailFormName) {
        this.mailFormName = mailFormName;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }
}
