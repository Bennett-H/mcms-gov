/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.gov.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.biz.IRoleBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.LogEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.entity.RoleEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.gov.biz.ILogBiz;
import net.mingsoft.gov.biz.IPasswordBiz;
import net.mingsoft.gov.entity.PasswordEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = {"后端-审计日志接口"})
@Controller
@RequestMapping("/${ms.manager.path}/gov/auditor")
public class AuditorAction extends BaseAction {

    @Autowired
    private IManagerBiz managerBiz;

    @Autowired
    private IRoleBiz roleBiz;

    @Autowired
    private IModelBiz modelBiz;

    @Autowired
    private IPasswordBiz passwordBiz;

    @Autowired
    private ILogBiz logBiz;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/log")
    @RequiresPermissions("auditor:log:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/gov/auditor/log";
    }

    /**
     * 返回日志list
     */
    @ApiIgnore
    @GetMapping("/loginLog")
    @RequiresPermissions("auditor:loginLog:view")
    public String list(HttpServletResponse response, HttpServletRequest request) {
        return "/gov/auditor/login-log";
    }


    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/power")
    @RequiresPermissions("auditor:power:view")
    public String power(HttpServletResponse response, HttpServletRequest request) {
        return "/gov/auditor/power";
    }


    /**
     * 未修改过初始密码管理员
     *
     * @param response 响应
     * @param request  请求
     * @return 返回未修改过初始密码管理员集合
     */
    @ApiOperation(value = "未修改过初始密码管理员")
    @PostMapping("/auditManager")
    @RequiresPermissions("auditor:log:view")
    @ResponseBody
    public ResultData auditManager(HttpServletResponse response, HttpServletRequest request) {
        List<ManagerEntity> managerList = managerBiz.list();
        int day = ConfigUtil.getInt("安全设置", "passwordMaxDay", 30);
        LambdaQueryWrapper<PasswordEntity> wrapper = new QueryWrapper<PasswordEntity>().lambda();
        wrapper.orderByDesc(PasswordEntity::getCreateDate);
        List<PasswordEntity> passwordLogList = passwordBiz.list(wrapper);
        // 在规定时间内修改的日志
        List<String> logList = new ArrayList<>();
        // 过滤不在修改密码日期的日志
        List<PasswordEntity> passwordChangeList = passwordLogList.stream().filter(
                passwordEntity -> {
                    // 判断密码是否在时间范围内修改
                    if (DateUtil.isIn(passwordEntity.getCreateDate(), DateUtil.offsetDay(new Date(), -(day-1)), new Date())) {
                        logList.add(passwordEntity.getPasswordOwnerId());
                        return false;
                    }else {
                        return true;
                    }
                }
        ).collect(Collectors.toList());
        // 所有管理员id及修改密码时间映射
        Map<String, List<Date>> passwordEntityList = passwordChangeList.stream().collect(Collectors.groupingBy(PasswordEntity::getPasswordOwnerId, Collectors.mapping(PasswordEntity::getCreateDate, Collectors.toList())));

        // 未修改过密码管理员集合
        List<ManagerEntity> managers = new ArrayList<>();
        for (ManagerEntity manager : managerList) {
            if (!logList.contains(manager.getId())) {
                if (CollUtil.isNotEmpty(passwordEntityList.get(manager.getId()))) {
                    manager.setManagerAdmin(day+"天内未修改过密码");
                    manager.setCreateDate(passwordEntityList.get(manager.getId()).get(0));
                }else {
                    manager.setManagerAdmin("未修改过初始密码");
                    manager.setCreateDate(null);
                }
                manager.setManagerPassword("");
                managers.add(manager);
            }
        }
        // 分页
        int pageNo = BasicUtil.getPageNo();
        int pageSize = BasicUtil.getPageSize();
        int totalSize = managers.size();
        List<ManagerEntity> managerResultList = managers.stream().skip((long) (pageNo - 1) * pageSize).
                limit(pageSize).collect(Collectors.toList());

        return ResultData.build().success(new EUListBean(managerResultList, totalSize));
    }

    /**
     * 长时间未登录管理员
     *
     * @param response 响应
     * @param request  请求
     * @return 长时间未登录管理员
     */
    @ApiOperation(value = "长时间未登录管理员")
    @PostMapping("/unLoginManager")
    @RequiresPermissions("auditor:loginLog:view")
    @ResponseBody
    public ResultData unLoginManager(HttpServletResponse response, HttpServletRequest request) {
        List<ManagerEntity> managerList = managerBiz.list();
        int day = ConfigUtil.getInt("安全设置", "managerLoginDay", 30);
        List<String> managerNameList = managerList.stream().map(ManagerEntity::getManagerName).collect(Collectors.toList());
        managerNameList = logBiz.unLoginManagers(managerNameList, day);
        LambdaQueryWrapper<LogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogEntity::getLogBusinessType, BusinessTypeEnum.LOGIN.toString().toLowerCase());
        wrapper.orderByDesc(LogEntity::getCreateDate);
        List<LogEntity> logs = logBiz.list(wrapper);
        // 管理员账号及最后登录时间映射
        Map<String, List<Date>> logManagers = logs.stream().filter(logEntity -> StringUtils.isNotBlank(logEntity.getLogUser())).collect(Collectors.groupingBy(LogEntity::getLogUser, Collectors.mapping(LogEntity::getCreateDate, Collectors.toList())));
        // 长时间未登录管理员集合
        List<ManagerEntity> managers = new ArrayList<>();
        for (ManagerEntity manager : managerList) {
            if (managerNameList.contains(manager.getManagerName())) {
                manager.setManagerAdmin(day+"天内管理员未登录过系统");
                manager.setManagerPassword("");
                // NP
                if (CollUtil.isNotEmpty(logManagers.get(manager.getManagerName()))) {
                    manager.setCreateDate(logManagers.get(manager.getManagerName()).get(0));
                }else {
                    manager.setCreateDate(null);
                }
                managers.add(manager);
            }
        }
        // 分页
        int pageSize = BasicUtil.getPageSize();
        int pageNo = BasicUtil.getPageNo();
        int totalSize = managers.size();
        List<ManagerEntity> managerResultList = managers.stream().skip((long) (pageNo - 1) * pageSize).
                limit(pageSize).collect(Collectors.toList());

        return ResultData.build().success(new EUListBean(managerResultList, totalSize));
    }

    /**
     * 管理员菜单
     *
     * @param response 响应
     * @param request  请求
     * @return 管理员菜单
     */
    @ApiOperation(value = "管理员菜单")
    @PostMapping("/managerModel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerId", value = "管理员ID", required = true,paramType="query"),
    })
    @RequiresPermissions("auditor:power:view")
    @ResponseBody
    public ResultData ManagerModel(HttpServletResponse response, HttpServletRequest request) {
        String managerId = BasicUtil.getString("managerId");
        if (StringUtils.isBlank(managerId)) {
            return ResultData.build().error("管理员ID不能为空");
        }
        ManagerEntity manager = managerBiz.getById(managerId);
        if (manager == null) {
            return ResultData.build().error("没有当前管理员!");
        }
        //获取管理员角色id
        String[] split = manager.getRoleIds().split(",");
        List<String> managerRoleIds = Arrays.asList(split);
        //查询所有角色，因为需要与管理员角色进行比对，避免管理员绑定了已删除的角色
        RoleEntity roleEntity = new RoleEntity();
        List<RoleEntity> roleList = roleBiz.query(roleEntity);
        List<String> allRoleIdList = roleList.stream().map(RoleEntity::getId).collect(Collectors.toList());
        List<ModelEntity> empModelList = new ArrayList<>();
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
        return ResultData.build().success(new EUListBean(empModelList, empModelList.size()));
    }


}
