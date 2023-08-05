/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.db.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.bean.ManagerLoginCacheBean;
import net.mingsoft.co.cache.ManagerOnlineCache;
import net.mingsoft.co.constant.Const;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 在线用户控制器
 *
 * @author 铭软开发团队
 * @version 版本号：1.0<br/>
 * 创建日期：2021-03-11 11:40:55<br/>
 * 历史修订：2022-1-21 把kick() 中catch的 UnknownSessionException 扩大为 SessionException,
 *                     防止捕获不到session过期异常
 */
@Api(tags={"后端-企业模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/basic/co/onlineManager")
public class OnlineManagerAction extends BaseAction {


    @Resource
    private DefaultWebSessionManager defaultWebSessionManager;


    @Autowired
    public ManagerOnlineCache managerOnlineCache;


    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("co:onlineManger:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/basic/co/online-manager/index";
    }

    /**
     * 在线列表模糊查询条件筛选
     *
     * @param loginCacheBean 缓存用户
     * @param managerName    登录名称
     * @param loginAddress   登录地址
     * @return
     */
    @RequiresPermissions("co:onlineManger:view")
    private boolean filterWhereHandle(ManagerLoginCacheBean loginCacheBean, String managerName, String loginAddress) {
        if (StringUtils.isEmpty(managerName) && StringUtils.isEmpty(loginAddress)) {
            return loginCacheBean.getOnline();
        } else if (StringUtils.isNotEmpty(managerName) && StringUtils.isEmpty(loginAddress)) {
            String filterManagerName = loginCacheBean.getManagerName();
            return (loginCacheBean.getOnline() && StringUtils.isNotEmpty(filterManagerName)
                    && filterManagerName.indexOf(managerName) > -1);
        } else if (StringUtils.isEmpty(managerName) && StringUtils.isNotEmpty(loginAddress)) {
            String filterLoginAddress = loginCacheBean.getLoginAddress();
            return (loginCacheBean.getOnline() && StringUtils.isNotEmpty(filterLoginAddress)
                    && filterLoginAddress.indexOf(loginAddress) > -1);
        } else if (StringUtils.isNotEmpty(managerName) && StringUtils.isNotEmpty(loginAddress)) {
            String filterManagerName = loginCacheBean.getManagerName();
            String filterLoginAddress = loginCacheBean.getLoginAddress();
            return (loginCacheBean.getOnline() && (StringUtils.isNotEmpty(filterManagerName)
                    && filterManagerName.indexOf(managerName) > -1 || StringUtils.isNotEmpty(filterLoginAddress)
                    && filterLoginAddress.indexOf(loginAddress) > -1));
        }
        return false;
    }

    /**
     * 在线列表
     *
     * @param managerLoginCacheBean 在线用户bean
     * @param page                  分页对象
     * @param response
     * @param request
     * @return
     */
    @ApiOperation(value = "获取在线用户列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginAddress", value = "登录地址", required = false, paramType = "query"),
            @ApiImplicitParam(name = "managerName", value = "登录名称", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore ManagerLoginCacheBean managerLoginCacheBean,
                           @ModelAttribute @ApiIgnore Page page, HttpServletResponse response,
                           HttpServletRequest request) {
        if (page.getPageNumber() == 0) {
            page.setPageNumber(1);
        }
        List<ManagerLoginCacheBean> list = managerOnlineCache.list();
        LOG.debug("manager cache size {}",list.size());
        // 过滤空对象
        List<ManagerLoginCacheBean> managerLoginCacheBeanList = list.stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing(ManagerLoginCacheBean::getLastLoginTime).reversed()).collect(Collectors.toList());

        List<ManagerLoginCacheBean> loginCacheBeanList = managerLoginCacheBeanList.stream().skip((long) (page.getPageNumber() - 1) * page.getPageSize()).
                limit(page.getPageSize()).collect(Collectors.toList());
        return ResultData.build().success(new EUListBean(loginCacheBeanList, managerOnlineCache.list().size()));
    }


    @ApiOperation(value = "解锁管理员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerName", value = "登录名称", required = true, paramType = "query"),
    })
    @RequiresPermissions("co:onlineManger:unlock")
    @LogAnn(title = "解锁管理员", businessType = BusinessTypeEnum.UPDATE)
    @RequestMapping(value = "/unlock", method = {RequestMethod.POST})
    @ResponseBody
    public ResultData unlock(String manageName, HttpServletResponse response, HttpServletRequest request) {
        if (StringUtils.isEmpty(manageName)) {
            return ResultData.build().error(getResString("err.empty", getResString("manager.name")));
        }
        ManagerLoginCacheBean loginCacheBean = (ManagerLoginCacheBean) managerOnlineCache.get(manageName);
        if (loginCacheBean == null) {
            return ResultData.build().error(getResString("err.error", getResString("manager.name")));
        }
        loginCacheBean.setOnOfLockStatus(ManagerLoginCacheBean.UNLOCK);
        // 将失败次数清零
        AtomicInteger atomicInteger = new AtomicInteger(0);
        loginCacheBean.setLoginFailCount(atomicInteger);
        managerOnlineCache.saveOrUpdate(loginCacheBean.getManagerName(),loginCacheBean);
        return ResultData.build().success(loginCacheBean);
    }

    @ApiOperation(value = "锁定管理员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerName", value = "登录名称", required = true, paramType = "query"),
    })
    @RequiresPermissions("co:onlineManger:lock")
    @LogAnn(title = "锁定管理员", businessType = BusinessTypeEnum.UPDATE)
    @RequestMapping(value = "/lock", method = {RequestMethod.POST})
    @ResponseBody
    public ResultData lock(String manageName, HttpServletResponse response, HttpServletRequest request) {
        if (StringUtils.isEmpty(manageName)) {
            return ResultData.build().error(getResString("err.empty", getResString("manager.name")));
        }
        ManagerLoginCacheBean loginCacheBean = (ManagerLoginCacheBean) managerOnlineCache.get(manageName);
        if (loginCacheBean == null) {
            return ResultData.build().error(getResString("err.error", getResString("manager.name")));
        }
        loginCacheBean.setOnOfLockStatus(ManagerLoginCacheBean.LOCK);
        managerOnlineCache.saveOrUpdate(loginCacheBean.getManagerName(),loginCacheBean);
        return ResultData.build().success(loginCacheBean);
    }


    /**
     * 强制下线
     */
    @ApiOperation(value = "强制下线")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerLoginCacheBeanList", value = "管理员集合", required = false, paramType = "query")
    })
    @RequiresPermissions("co:onlineManger:kick")
    @LogAnn(title = "强制下线", businessType = BusinessTypeEnum.UPDATE)
    @RequestMapping(value = "/kick", method = {RequestMethod.POST})
    @ResponseBody
    public ResultData kick(String manageName, HttpServletResponse response, HttpServletRequest request) {
        if (StringUtils.isBlank(manageName)) {
            return ResultData.build().error(getResString("err.empty", this.getResString("manager.name")));
        }

        ManagerLoginCacheBean loginCacheBean =  managerOnlineCache.get(manageName);
        managerOnlineCache.delete(loginCacheBean.getManagerName());
        if (loginCacheBean != null && CollUtil.isNotEmpty(Const.SESSION_IDS)) {
            try {
                for (Serializable sessionId : Const.SESSION_IDS) {
                    Session kickOutSession = defaultWebSessionManager.getSession(new DefaultSessionKey(sessionId));

                    if (kickOutSession != null) {
                        //设置会话的kickout属性表示踢出了
                        kickOutSession.setAttribute("kick", true);
                    }
                }

            } catch (SessionException e) {
            }
        }
        //如果是用户自己退出
        if (BasicUtil.getManager().getManagerName().equals(manageName)){
            SecurityUtils.getSubject().logout();
        }
        return ResultData.build().success();
    }


}
