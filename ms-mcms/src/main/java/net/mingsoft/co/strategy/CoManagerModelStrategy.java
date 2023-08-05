/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.strategy;

import cn.hutool.core.collection.CollectionUtil;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.biz.IRoleBiz;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.entity.RoleEntity;
import net.mingsoft.basic.strategy.IModelStrategy;
import net.mingsoft.basic.util.BasicUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 员工菜单列表,需要在MSConfig里面配置才生效，登录采用企业版本
 *
 * @author Administrator
 * @version 创建日期：2020/11/18 18:12<br/>
 * 历史修订：<br/>
 */
public class CoManagerModelStrategy implements IModelStrategy {

    /**
     * 注入模块业务层
     */
    @Autowired
    private IModelBiz modelBiz;

    @Autowired
    private IRoleBiz roleBiz;

    @Autowired
    IManagerBiz managerBiz;

    @Override
    public List<ModelEntity> list() {
        ManagerEntity manager = BasicUtil.getManager();
        List<ModelEntity> empModelList = null;
        //超级管理员是没有关联员工的，是没有员工编号的
        assert manager != null;
        if (ManagerAdminEnum.SUPER.toString().equals(manager.getManagerAdmin())) {
            empModelList = modelBiz.list();
        } else {
            ManagerEntity managerEntity = managerBiz.getManagerByManagerName(manager.getManagerName());
            //获取管理员角色id
            String[] split = managerEntity.getRoleIds().split(",");
            List<String> managerRoleIds = Arrays.asList(split);
            //查询所有角色，因为需要与管理员角色进行比对，避免管理员绑定了已删除的角色
            RoleEntity roleEntity = new RoleEntity();
            List<RoleEntity> roleList = roleBiz.query(roleEntity);
            List<String> allRoleIdList = roleList.stream().map(RoleEntity::getId).collect(Collectors.toList());
            // 比对管理员角色id与系统现有都角色id
            Collection<String> intersection = CollectionUtil.intersection(managerRoleIds, allRoleIdList);
            if (CollectionUtil.isNotEmpty(intersection)) {
                int[] roleIds = intersection.stream().mapToInt(Integer::valueOf).toArray();
                HashSet<ModelEntity> modelEntitySet = new HashSet<>();
                for (int roleId : roleIds) {
                    List<ModelEntity> modelEntities = modelBiz.queryModelByRoleId(roleId);
                    modelEntitySet.addAll(modelEntities);
                }
                empModelList = new ArrayList<>(modelEntitySet);
            }
        }
        return empModelList;
    }
}
