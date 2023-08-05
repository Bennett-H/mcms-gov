/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.basic.action;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IRoleBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.RoleEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.co.bean.ManagerLoginCacheBean;
import net.mingsoft.co.cache.ManagerOnlineCache;
import net.mingsoft.co.constant.e.ManagerEnum;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员管理控制层
 * @author 铭软开发团队
 * @version
 * 版本号：1.0<br/>
 * 创建日期：2017-8-24 23:40:55<br/>
 * 历史修订：<br/>
 * 2022-03-16 10:00 重写co,增加默认角色禁用判断 <br/>
 */
@Api(tags={"后端-基础接口"})
@Controller("govManagerAction")
@RequestMapping("/${ms.manager.path}/basic/manager")
public class ManagerAction extends BaseAction {

    /**
     * 注入管理员业务层
     */
    @Autowired
    private IManagerBiz managerBiz;


    /**
     * 注入角色业务层
     */
    @Autowired
    private IRoleBiz roleBiz;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("basic:manager:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/basic/manager/index";
    }


    @ApiOperation(value = "查询管理员列表")
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @RequiresPermissions("basic:manager:view")
    public ResultData list(@ModelAttribute @ApiIgnore ManagerEntity manager, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        //更新缓存
        managerBiz.updateCache();
        BasicUtil.startPage();
        // 默认或企业站群启用
        LambdaQueryWrapper<ManagerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(manager.getManagerName()), ManagerEntity::getManagerName, manager.getManagerName());
        wrapper.like(StringUtils.isNotBlank(manager.getManagerNickName()), ManagerEntity::getManagerNickName, manager.getManagerNickName());
        List<ManagerEntity> managerList = managerBiz.list(wrapper);

        // 开源站群需要启用
//        AppEntity websiteApp = BasicUtil.getWebsiteApp();
//        List<ManagerEntity> managerList;
//        if (websiteApp != null){
//            int appId = websiteApp.getAppId();
//            QueryWrapper<ManagerEntity> wrapper = new QueryWrapper<ManagerEntity>().eq("app_id", appId);
//            managerList = managerBiz.list(wrapper);
//        }else {
//            managerList = managerBiz.list();
//        }

        List<ManagerEntity> allManager = managerBiz.queryAllManager(managerList);
        return ResultData.build().success(new EUListBean(allManager, (int) BasicUtil.endPage(allManager).getTotal()));
    }

    @ApiOperation(value = "查询管理员列表,去掉当前管理员id，确保不能删除和修改自己")
    @GetMapping("/query")
    @ResponseBody
    @RequiresPermissions("basic:manager:view")
    public ResultData query(HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        //更新缓存
        managerBiz.updateCache();
        ManagerEntity manager = BasicUtil.getManager();
        BasicUtil.startPage();
        // 默认或企业站群启用
        List<ManagerEntity> managerList = managerBiz.list();

        // 开源站群需要启用
//        AppEntity websiteApp = BasicUtil.getWebsiteApp();
//        List<ManagerEntity> managerList;
//        if (websiteApp != null){
//            int appId = websiteApp.getAppId();
//            QueryWrapper<ManagerEntity> wrapper = new QueryWrapper<ManagerEntity>().eq("app_id", appId);
//            managerList = managerBiz.list(wrapper);
//        }else {
//            managerList = managerBiz.list();
//        }

        List<ManagerEntity> allManager = managerBiz.queryAllManager(managerList);
        for (ManagerEntity _manager : allManager) {
            assert manager != null;
            if (_manager.getId().equals(manager.getId())) {
                _manager.setId("0");
            }
        }
        return ResultData.build().success(new EUListBean(allManager, (int) BasicUtil.endPage(allManager).getTotal()));
    }

    @ApiOperation(value = "获取管理员接口")
    @GetMapping("/get")
    @ResponseBody
    @RequiresPermissions("basic:manager:view")
    public ResultData get(@ModelAttribute @ApiIgnore ManagerEntity manager, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        //更新缓存
        managerBiz.updateCache();
        ManagerEntity managerEntity;
        //判断是否传managerId
        if (StringUtils.isNotEmpty(manager.getId())) {
            managerEntity = managerBiz.getById(manager.getId());
        } else {
            ManagerEntity managerSession = BasicUtil.getManager();
            if (managerSession == null) {
                return ResultData.build().error("管理员已失效!");
            }
            managerEntity = managerBiz.getById(managerSession.getId());
        }
        if (managerEntity != null){

            managerEntity.setManagerPassword("");
        }
        return ResultData.build().success(managerEntity);
    }

    @ApiOperation(value="获取当前管理员信息接口")
    @GetMapping("/info")
    @ResponseBody
    public ResultData info(HttpServletResponse response, HttpServletRequest request){
        ManagerEntity managerEntity;
        ManagerEntity manager =  BasicUtil.getManager();
        if (manager == null) {
            return ResultData.build().error("管理员已失效!");
        }
        managerEntity = managerBiz.getById(manager.getId());
        if (managerEntity != null){
            managerEntity.setManagerPassword("");
        }
        return ResultData.build().success(managerEntity);
    }


    @ApiOperation(value = "保存管理员实体")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerName", value = "帐号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "managerNickName", value = "昵称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "managerPassword", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "角色ID", required = false, paramType = "query"),
            @ApiImplicitParam(name = "peopleId", value = "用户ID", required = false, paramType = "query"),
    })
    @LogAnn(title = "保存管理员实体", businessType = BusinessTypeEnum.INSERT)
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("basic:manager:save")
    public ResultData save(@ModelAttribute @ApiIgnore ManagerEntity manager, HttpServletResponse response, HttpServletRequest request) {
        //用户名是否存在
        if (managerBiz.getManagerByManagerName(manager.getManagerName()) != null) {
            return ResultData.build().error(getResString("err.exist", this.getResString("manager.name")));
        }
        //验证管理员用户名的值是否合法
        if (StringUtils.isBlank(manager.getManagerName())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("manager.name")));
        }
        if (!StringUtil.checkLength(manager.getManagerName() + "", 1, 15)) {
            return ResultData.build().error(getResString("err.length", this.getResString("manager.name"), "1", "15"));
        }
        //验证管理员昵称的值是否合法
        if (StringUtils.isBlank(manager.getManagerNickName())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("manager.nickname")));
        }
        if (!StringUtil.checkLength(manager.getManagerNickName() + "", 1, 15)) {
            return ResultData.build().error(getResString("err.length", this.getResString("manager.nickname"), "1", "15"));
        }
        //验证管理员密码的值是否合法
        if (StringUtils.isBlank(manager.getManagerPassword())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("manager.password")));
        }
        if (!StringUtil.checkLength(manager.getManagerPassword() + "", 1, 45)) {
            return ResultData.build().error(getResString("err.length", this.getResString("manager.password"), "1", "45"));
        }
        if (manager.getRoleIds().split(",").length > 5) {
            return ResultData.build().error(getResString("err.RoleIds.length", this.getResString("manager.roleIds"), "1", "5"));
        }

        if (BooleanUtils.toBoolean(ConfigUtil.getString("安全设置", "defaultRoleEnable"))) {
            RoleEntity role = new RoleEntity();
            List<RoleEntity> roles = roleBiz.list(new QueryWrapper<>(role));
            roles = roles.stream().filter(roleEntity -> roleEntity.getNotDel() == 1).collect(Collectors.toList());
            List<String> roleIds = Arrays.asList(manager.getRoleIds().split(","));
            for (RoleEntity entity : roles) {
                if (roleIds.contains(entity.getId())) {
                    return ResultData.build().error("默认角色已被停用!");
                }
            }
        }

        manager.setManagerPassword(SecureUtil.md5(manager.getManagerPassword()));
        managerBiz.save(manager);
        return ResultData.build().success(manager);
    }


    @ApiOperation(value = "批量删除管理员")
    @LogAnn(title = "批量删除管理员", businessType = BusinessTypeEnum.DELETE)
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("basic:manager:del")
    public ResultData delete(@RequestBody List<ManagerEntity> managers, HttpServletResponse response, HttpServletRequest request) {
        int[] ids = new int[managers.size()];
        for (int i = 0; i < managers.size(); i++) {
            ids[i] = Integer.parseInt(managers.get(i).getId());
        }
        managerBiz.delete(ids);
        return ResultData.build().success();
    }

    @ApiOperation(value = "更新管理员信息管理员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerName", value = "帐号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "managerNickName", value = "昵称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "managerPassword", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "角色ID", required = false, paramType = "query"),
            @ApiImplicitParam(name = "peopleId", value = "用户ID", required = false, paramType = "query"),
    })
    @LogAnn(title = "更新管理员信息管理员", businessType = BusinessTypeEnum.UPDATE)
    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("basic:manager:update")
    public ResultData update(@ModelAttribute @ApiIgnore ManagerEntity manager) {

        ManagerEntity _manager = managerBiz.getManagerByManagerName(manager.getManagerName());
        //用户名是否存在
        if (_manager != null) {
            if (!_manager.getId().equals(manager.getId())) {
                return ResultData.build().error(getResString("err.exist", this.getResString("manager.name")));
            }
        }
        //验证管理员用户名的值是否合法
        if (StringUtils.isBlank(manager.getManagerName())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("manager.name")));
        }
        if (!StringUtil.checkLength(manager.getManagerName() + "", 1, 15)) {
            return ResultData.build().error(getResString("err.length", this.getResString("manager.name"), "1", "15"));
        }
        //验证管理员昵称的值是否合法
        if (StringUtils.isBlank(manager.getManagerNickName())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("manager.nickname")));
        }
        if (!StringUtil.checkLength(manager.getManagerNickName() + "", 1, 15)) {
            return ResultData.build().error(getResString("err.length", this.getResString("manager.nickname"), "1", "15"));
        }
        //验证管理员密码的值是否合法
        if (StringUtils.isNotBlank(manager.getManagerPassword())) {
            if (!StringUtil.checkLength(manager.getManagerPassword() + "", 1, 45)) {
                return ResultData.build().error(getResString("err.length", this.getResString("manager.password"), "1", "45"));
            }
            manager.setManagerPassword(SecureUtil.md5(manager.getManagerPassword()));
        }

        if (BooleanUtils.toBoolean(ConfigUtil.getString("安全设置", "defaultRoleEnable"))) {
            RoleEntity role = new RoleEntity();
            List<RoleEntity> roles = roleBiz.list(new QueryWrapper<>(role));
            roles = roles.stream().filter(roleEntity -> roleEntity.getNotDel() == 1).collect(Collectors.toList());
            List<String> roleIds = Arrays.asList(manager.getRoleIds().split(","));
            for (RoleEntity entity : roles) {
                if (roleIds.contains(entity.getId())) {
                    return ResultData.build().error("默认角色已被停用!");
                }
            }
        }

        managerBiz.updateById(manager);
        return ResultData.build().success(manager);
    }

    @ApiOperation(value = "更新锁定状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "管理员id", required = true, paramType = "query"),
    })
    @LogAnn(title = "更新锁定状态", businessType = BusinessTypeEnum.OTHER)
    @PostMapping("/managerLock")
    @ResponseBody
    @RequiresPermissions("basic:manager:lock")
    public ResultData managerLock(int id, HttpServletResponse response, HttpServletRequest request) {
        ManagerEntity manager = managerBiz.getById(id);
        if (manager == null) {
            return ResultData.build().error("管理员不存在");
        }
        ManagerOnlineCache managerOnlineCache = SpringUtil.getBean(ManagerOnlineCache.class);
        ManagerLoginCacheBean loginCacheBean = managerOnlineCache.get(manager.getManagerName());
        if (loginCacheBean != null) {
            //将登录失败次数置为初始值
            loginCacheBean.getLoginFailCount().set(0);
            loginCacheBean.setOnOfLockStatus(ManagerLoginCacheBean.UNLOCK);
            managerOnlineCache.saveOrUpdate(manager.getManagerName(),loginCacheBean);
        }else{
            managerOnlineCache.delete(manager.getManagerName());
        }
        //如果为空值，则为锁定状态
        if (StringUtils.isBlank(manager.getManagerLock())) {
            manager.setManagerLock(ManagerEnum.LOCK.toString());
            managerBiz.updateById(manager);
            return ResultData.build().success(manager);
        } else if (manager.getManagerLock().equals(ManagerEnum.LOCK.toString())){
            manager.setManagerLock("");
            managerBiz.updateById(manager);
            return ResultData.build().success(manager);
        }
        return ResultData.build().error("更新锁定状态失败");
    }

}
