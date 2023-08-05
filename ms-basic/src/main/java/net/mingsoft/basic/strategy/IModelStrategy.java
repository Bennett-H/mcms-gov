/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.strategy;

import net.mingsoft.basic.entity.ModelEntity;

import java.util.List;

/**
 * 菜单策略
 * 员工和管理员的菜单modelList 接口不一样，避免重写问题
 */
public interface IModelStrategy {
    List<ModelEntity> list();
}
