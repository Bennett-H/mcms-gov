/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.strategy;

import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 管理员菜单列表,
 * @author Administrator
 * @version 创建日期：2020/11/18 18:12<br/>
 * 历史修订：<br/>
 */
public class ManagerModelStrategy implements IModelStrategy{

    /**
     * 注入模块业务层
     */
    @Autowired
    private IModelBiz modelBiz;

    @Override
    public List<ModelEntity> list() {
        ManagerEntity manager = BasicUtil.getManager();
        assert manager != null;
        List<ModelEntity> parentModelList;
        if (manager.getManagerAdmin().equals(ManagerAdminEnum.SUPER.toString())) {
            parentModelList = modelBiz.list();
        }else {
            parentModelList = modelBiz.queryModelByRoleId(manager.getRoleId());
        }
        return parentModelList;
    }
}
