/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.basic.action.web;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.biz.ILogBiz;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.constant.e.OperatorTypeEnum;
import net.mingsoft.basic.entity.LogEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.strategy.ILoginStrategy;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.IpUtils;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.bean.ManagerLoginCacheBean;
import net.mingsoft.co.cache.ManagerOnlineCache;
import net.mingsoft.co.util.RSAUtils;
import net.mingsoft.gov.action.BaseAction;
import net.mingsoft.gov.biz.IPasswordBiz;
import net.mingsoft.gov.constant.e.SecurityPasswordTypeEnum;
import net.mingsoft.gov.entity.PasswordEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 登录
 * @author 铭飞开发团队
 * @version
 * 版本号：100<br/>
 * 创建日期：2021-03-12 11:40:29<br/>
 * 历史修订： checkLogin方法增加解密<br/>
 * 历史修订21-11-14： checkLogin更改为login,方便SingleSessionControlFilter过滤<br/>
 */
@Api(tags = {"前端-基础接口"})
@Controller
@RequestMapping("/${ms.manager.path}")
public class LoginAction extends BaseAction {

    @Value("${ms.manager.path}")
    private String managerPath;

    @Autowired
    private ILoginStrategy loginStrategy;

    @Autowired
    private IPasswordBiz passwordBiz;

    @Autowired
    private ILogBiz logBiz;

    /**
     * 加载管理员登录界面
     *
     * @param request
     *            请求对象
     * @return 管理员登录界面地址
     */
    @ApiOperation(value = "加载管理员登录界面")
    @SuppressWarnings("resource")
    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        Subject currentSubject = SecurityUtils.getSubject();
        if (BasicUtil.getManager() != null && currentSubject.isAuthenticated()) {
            return "redirect:" + managerPath + "/index.do";
        }
        request.setAttribute("app", BasicUtil.getApp());
        return "/login";
    }


    /**
     * 验证登录
     *updateUserPasswordByUserName
     * @param manager
     *            管理员实体
     * @param request
     *            请求
     * @param response
     *            响应
     */
    @ApiOperation(value = "验证登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerName", value = "帐号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "managerPassword", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "managerAdmin", value = "角色身份", required = false, paramType = "query"),
    })
    @PostMapping("/login")
    @ResponseBody
    @LogAnn(title = "登录", businessType = BusinessTypeEnum.LOGIN)
    public ResultData checkLogin(@ModelAttribute @ApiIgnore ManagerEntity manager, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        //更新缓存
        IManagerBiz managerBiz = SpringUtil.getBean(IManagerBiz.class);
        LOG.debug("basic gov checkLogin");


        //验证码
        if (!(checkRandCode())) {
            return ResultData.build().error(getResString("err.error", this.getResString("rand.code")));
        }
        if(manager ==null || StringUtils.isEmpty(manager.getManagerName()) || StringUtils.isEmpty(manager.getManagerPassword())){
            return ResultData.build().error(getResString("err.empty", this.getResString("manager.name.or.password")));
        }
        String privateKey = ConfigUtil.getString("公私钥配置", "privateKey");
        if (StringUtils.isBlank(privateKey)) {
            return ResultData.build().error(this.getResString("error.private.key.empty"));
        }
        //开始解密
        manager.setManagerName(RSAUtils.rsaDecode(manager.getManagerName(),privateKey));
        manager.setManagerPassword(RSAUtils.rsaDecode(manager.getManagerPassword(),privateKey));
        ManagerEntity managerByManagerName = managerBiz.getManagerByManagerName(manager.getManagerName());
        // 24小时制
        SimpleDateFormat startFormat = new SimpleDateFormat("HH:mm:ss");
        String start = ConfigUtil.getString("安全设置","timeStart");
        String end = ConfigUtil.getString("安全设置","timeEnd");
        Date startTime = null;
        Date endTime = null;
        // 登录时间标识
        boolean dateFlag = false;
        // 超级管理员不限制
        if(managerByManagerName != null && !managerByManagerName.getManagerAdmin().equalsIgnoreCase(ManagerAdminEnum.SUPER.toString()) && StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)){
            // 判断登录范围
            startTime = startFormat.parse(start);
            endTime = startFormat.parse(end);
            String format = startFormat.format(new Date().getTime());
            Date date = startFormat.parse(format);
            if (!DateUtil.isIn(date, startTime, endTime)){
                return ResultData.build().error(this.getResString("error.not.login.time"));
            }
            dateFlag = true;
        }
        // true代表禁用默认管理员登录
        boolean flag = BooleanUtils.toBoolean(ConfigUtil.getString("安全设置", "defaultRoleEnable"));
        if (flag) {
            // 根据账号获取当前管理员信息
            ManagerEntity _manager = managerBiz.getManagerByManagerName(manager.getManagerName());
            if (_manager != null && _manager.getNotDel() == 1) {
                return ResultData.build().error(this.getResString("error.account.disabled"));
            }
        }

        if(loginStrategy.login(manager)) {

            boolean isFirst = passwordBiz.modifyPasswordForFirst();
            if(isFirst) {
                //重定向到修改密码界面
                return ResultData.build().error("error.first.update.passwords");
            }

            int day = passwordBiz.modifyPasswordForTime();
            if (day > 0 ) {
                //重定向到修改密码界面
                return ResultData.build().error("passwordMaxDay").add("day",day);
            }
            // 设置session超时时间
            long sessionTime = (long) ConfigUtil.getInt("安全设置", "sessionTimeOut", 30) * 1000 * 60;
            if (sessionTime <= 0) {
                SecurityUtils.getSubject().getSession().setTimeout(-1);
            }else {
                SecurityUtils.getSubject().getSession().setTimeout(sessionTime);
            }
            if (dateFlag) {
                // 设置登录时间范围
                SecurityUtils.getSubject().getSession().setAttribute("startTime", startTime);
                SecurityUtils.getSubject().getSession().setAttribute("endTime", endTime);
            }
            // 检查账号是否异地登录
            LambdaQueryWrapper<LogEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LogEntity::getLogBusinessType, StringUtils.lowerCase(BusinessTypeEnum.LOGIN.name()));
            wrapper.eq(LogEntity::getLogUserType, StringUtils.lowerCase(OperatorTypeEnum.MANAGE.name()));
            wrapper.eq(LogEntity::getLogUser,  BasicUtil.getManager().getManagerName());
            wrapper.orderByDesc(LogEntity::getCreateDate);
            List<LogEntity> logList = logBiz.list(wrapper);
            if (CollUtil.isNotEmpty(logList)) {
                // 当前登录地理位置
                String curAddress = IpUtils.getRealAddressByIp(BasicUtil.getIp());
                String oldAddress = IpUtils.getRealAddressByIp(logList.get(0).getLogIp());
                if (!curAddress.equals(oldAddress)) {
                    LogEntity log = new LogEntity();
                    log.setLogTitle("异常地理位置登录");
                    log.setLogLocation(curAddress);
                    log.setLogStatus("success");
                    log.setLogUser( BasicUtil.getManager().getManagerName());
                    log.setLogUrl(SpringUtil.getRequest().getRequestURI());
                    log.setCreateDate(new Date());
                    log.setLogBusinessType(StringUtils.lowerCase(BusinessTypeEnum.LOGIN.toString()));
                    log.setLogIp(BasicUtil.getIp());
                    String msg = "当前管理员上次登录地址[{}],与本次登录地址[{}]不同,请确认是否本人登录!";
                    log.setLogErrorMsg(StrUtil.format(msg, oldAddress, curAddress));
                    logBiz.save(log);
                }
            }
            return ResultData.build().success();
        } else {
            ManagerOnlineCache managerOnlineCache = SpringUtil.getBean(ManagerOnlineCache.class);
            //获取用户名
            String username = manager.getManagerName();
            ManagerLoginCacheBean loginCacheBean = managerOnlineCache.get(username);
            if (loginCacheBean == null){
                return ResultData.build().error(this.getResString("error.account.or.password"));
            }
            if(ManagerLoginCacheBean.LOCK.equals(loginCacheBean.getOnOfLockStatus())){
                return ResultData.build().error(this.getResString("error.account.locked"));
            }
            int loginFailCount = loginCacheBean.getLoginFailCount().get();
            int count = ConfigUtil.getInt("安全设置", "errorLogin");
            if(loginFailCount < count){
                return ResultData.build().error(this.getResString("error.account.or.password.lock",String.valueOf(count-loginFailCount)));
            }
            throw new LockedAccountException(this.getResString("error.account.locked.try.later",String.valueOf(ConfigUtil.getInt("安全设置", "timeout"))));

        }
    }

}
