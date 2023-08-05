/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.basic.action.web;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
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
import org.springframework.web.bind.annotation.*;
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
@Api(tags={"前端-基础接口"})
@Controller("govWebManagerAction")
@RequestMapping("/basic/manager")
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

    @ApiOperation(value = "获取管理员接口")
    @PostMapping("/get")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore ManagerEntity manager, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        //更新缓存
        managerBiz.updateCache();
        ManagerEntity managerEntity = null;
        // 传值不为空
        if(StringUtils.isNotBlank(manager.getManagerName())){
            // 根据账号查询信息
            managerEntity = managerBiz.getManagerByManagerName(manager.getManagerName());
        } else {
            // 查询已登录账号的信息
            managerEntity = BasicUtil.getManager();
            if (managerEntity == null) {
                return ResultData.build().error("管理员已失效!");
            }
        }
        // 脱敏
        if (managerEntity != null) {
            managerEntity.setManagerPassword("");
        }
        return ResultData.build().success(managerEntity);
    }
}
