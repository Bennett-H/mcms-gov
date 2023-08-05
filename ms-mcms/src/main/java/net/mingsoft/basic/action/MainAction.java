/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.basic.action;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.ManagerModifyPwdBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.gov.biz.IPasswordBiz;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 主界面控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2014-7-14<br/>
 * 历史修订：
 * 2021-01-10 重写co 增加过期密码修改接口<br/>
 * 2021-11-10 重写co 增加系统信息接口<br/>
 * 2021-11-12 重写co index方法增加ms-form-tip控制<br/>
 */
@Api(tags = {"后端-基础接口"})
@Controller
@RequestMapping("/${ms.manager.path}")
public class MainAction extends BaseAction {

    /**
     * 模块业务层
     */
    @Autowired
    private IModelBiz modelBiz;

    /**
     * 管理员业务层
     */
    @Autowired
    private IManagerBiz managerBiz;


    @Value("${ms.manager.path}")
    private String managerPath;


    @Autowired
    private IPasswordBiz passwordBiz;


    /**
     * 加载后台主界面，并查询数据
     * @param request 请求对象
     * @return 主界面地址
     */
    @ApiOperation(value = "加载后台主界面，并查询数据")
    @GetMapping(value = {"/index", "/"})
    public String index(HttpServletRequest request) {
        ManagerEntity manager = BasicUtil.getManager();
        if (manager == null){
            return "/index";
        }
        HashSet<ModelEntity> modelSet = new HashSet<>();
        for (String roleId : manager.getRoleIds().split(",")) {
            modelSet.addAll(modelBiz.queryModelByRoleId(Integer.parseInt(roleId)));
        }
        List<ModelEntity> modelList = new ArrayList<>(modelSet);
        //如果ischild有值，则不显示
        List<BaseEntity> _modelList = new ArrayList<>();
        for (ModelEntity modelEntity : modelList) {
            if (StringUtils.isNotBlank(modelEntity.getIsChild())) {
                _modelList.add(modelEntity);
            }
        }
        request.setAttribute("modelList", JSONUtil.toJsonStr(_modelList));
        request.setAttribute("client", BasicUtil.getDomain() + "/" + managerPath);
        request.setAttribute("app", BasicUtil.getApp());

        //重写co index方法增加ms-form-tip控制
        BasicUtil.setSession("CSS-TIP", ConfigUtil.getObject("后台UI配置","uiTip"));
        return "/index";
    }

    @ApiIgnore
    @GetMapping("/main")
    public String main(HttpServletRequest request) {
        return "/main";
    }

    /**
     * 修改登录密码，若不填写密码则表示不修改
     *
     * @param request
     *            请求
     * @param response
     *            响应
     */
    @ApiOperation(value = "修改登录密码，若不填写密码则表示不修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldManagerPassword", value = "旧密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "newManagerPassword", value = "新密码", required = true, paramType = "query"),
    })
    @LogAnn(title = "修改登录密码", businessType = BusinessTypeEnum.UPDATE)
    @PostMapping("/updatePassword")
    @ResponseBody
    public ResultData updatePassword(@ModelAttribute @ApiIgnore ManagerModifyPwdBean managerModifyPwdBean, HttpServletResponse response, HttpServletRequest request) {
        //获取新的密码
        String newManagerPassword = managerModifyPwdBean.getNewManagerPassword();
        //获取管理员信息
        ManagerEntity manager =  BasicUtil.getManager();
        // 判断新密码和旧密码是否为空
        if (StringUtils.isBlank(newManagerPassword) || StringUtils.isBlank(managerModifyPwdBean.getOldManagerPassword())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("managerPassword")));
        }

        //判断旧的密码是否正确
        if (!managerModifyPwdBean.getOldManagerPassword().equals(manager.getManagerPassword())) {
            return ResultData.build().error(this.getResString("manager.password.old.err"));
        }
        // 判断新密码长度
        if (!StringUtil.checkLength(newManagerPassword, 6, 30)) {
            return ResultData.build().error(getResString("err.length", this.getResString("managerPassword"), "6", "30"));
        }
        //更改密码
        manager.setManagerPassword(SecureUtil.md5(newManagerPassword));
        //更新
        managerBiz.updateUserPasswordByUserName(manager);
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return ResultData.build().success();
    }

    /**
     * 验证密码修改的安全周期
     *
     * @param request
     *            请求
     * @param response
     *            响应
     */
    @ApiOperation(value = "修改登录密码，若不填写密码则表示不修改")
    @LogAnn(title = "修改登录密码", businessType = BusinessTypeEnum.UPDATE)
    @PostMapping("/checkPassword")
    @ResponseBody
    public ResultData checkPassword(HttpServletResponse response, HttpServletRequest request) {
        ManagerEntity manager =  BasicUtil.getManager();

        int day = passwordBiz.modifyPasswordForTime();
        //增加3个月如果未修改密码提示
        if (day > 0) {
            return ResultData.build().error(day + "");
        }

        return ResultData.build().success();
    }


    @ApiOperation(value = "修改登录密码，若不填写密码则表示不修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldManagerPassword", value = "旧密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "newManagerPassword", value = "新密码", required = true, paramType = "query"),
    })
    @GetMapping("/password")
    public String password(HttpServletResponse response, HttpServletRequest request) {

        return "/password";
    }

    /**
     * 退出系统
     * @param request 请求对象
     * @return true退出成功
     */
    @ApiOperation(value = "退出系统")
    @GetMapping("/loginOut")
    @ResponseBody
    public ResultData loginOut(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return ResultData.build().success();
    }

    /**
     * 系统信息页面
     * @param request
     * @return 表单页面地址
     */
    @ApiIgnore
    @GetMapping("/system")
    @RequiresPermissions("system:view")
    public String system(HttpServletRequest request) {
        return "/system";
    }

    @Autowired
    DataSource dataSource;

    /**
     * 获取系统配置信息
     * @param request 请求对象
     * @return true退出成功
     */
    @ApiOperation(value = "获取系统配置信息")
    @PostMapping("/system")
    @ResponseBody
    @RequiresPermissions("system:view")
    public ResultData system(HttpServletResponse response, HttpServletRequest request) {

        Properties props = System.getProperties();// 获取当前的系统属性
        HashMap<String, Object> map = new HashMap<>();
        map.put("CPU核数", String.valueOf(Runtime.getRuntime().availableProcessors()));

        // 单位B 转换为M
        map.put("虚拟机内存总量", String.valueOf(Runtime.getRuntime().totalMemory() / 1048576));
        map.put("虚拟机空闲内存量", String.valueOf(Runtime.getRuntime().freeMemory() / 1048576));
        map.put("虚拟机使用最大内存量", String.valueOf(Runtime.getRuntime().maxMemory() / 1048576));

        map.put("系统名称", props.getProperty("os.name"));
        map.put("系统构架", props.getProperty("os.arch"));
        map.put("系统版本", props.getProperty("os.version"));

        map.put("Java版本", props.getProperty("java.version"));
        map.put("Java安装路径", props.getProperty("java.home"));

        CpuInfo cpu = OshiUtil.getCpuInfo();
        map.put("cpu信息", cpu.getCpuModel() + "" + cpu.getCpuNum());
        map.put("内存总量", OshiUtil.getMemory().getTotal() / 1048576);
        map.put("内存可用", OshiUtil.getMemory().getAvailable() / 1048576);

        //数据库
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData mtdt = connection.getMetaData();
            map.put("数据库链接", mtdt.getURL());
            map.put("数据库",mtdt.getDatabaseProductName());
            map.put("数据库版本",mtdt.getDatabaseProductVersion());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ServletContext context = request.getServletContext();
        map.put("web容器",context.getServerInfo());
        map.put("发布路径",context.getRealPath(""));
//
        return ResultData.build().success(map);
    }

}