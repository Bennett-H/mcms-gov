/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.bean;

import net.mingsoft.mweixin.entity.MenuEntity;

import java.util.List;

/**
 * @Author: 铭飞开源团队--huise
 * @Date: 2019/6/3 13:42
 */
public class MenuBean extends MenuEntity {

    /**
     * 子菜单集合
     */
    private List<MenuEntity> subMenuList;

    public List<MenuEntity> getSubMenuList() {
        return subMenuList;
    }

    public void setSubMenuList(List<MenuEntity> subMenuList) {
        this.subMenuList = subMenuList;
    }
}
