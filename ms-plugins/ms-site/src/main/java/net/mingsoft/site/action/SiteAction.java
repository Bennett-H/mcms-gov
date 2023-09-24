/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.constant.Const;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.MapCacheUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.site.biz.ISiteAppManagerBiz;
import net.mingsoft.site.entity.SiteAppManagerEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用管理控制层
 * @author 铭飞科技
 * 创建日期：2020-6-22 18:36:33<br/>
 * 历史修订：site重写,因为需要修改树形展示 修改日期 2022-1-6
 * 2022-1-24 update() 更新当前站点,把session也更新
 */
@Api(tags = {"后端-站点应用接口"})
@Controller("siteAction")
@RequestMapping("/${ms.manager.path}/site")
public class SiteAction extends BaseAction {

    @Autowired
    private IAppBiz appBiz;

    @Autowired
    private IManagerBiz managerBiz;

    @Autowired
    private ISiteAppManagerBiz siteAppManagerBiz;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("site:app:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/basic/app/index";
    }

    /**
     * 返回编辑界面app_form
     */
    @ApiIgnore
    @GetMapping("/form")
    @RequiresPermissions(value = {"site:app:save", "site:app:update"}, logical = Logical.OR)
    public String form(@ModelAttribute AppEntity app, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        if (app.getId() != null) {
            BaseEntity appEntity = appBiz.getById(Integer.parseInt(app.getId()));
            model.addAttribute("appEntity", appEntity);
        }
        return "/basic/app/form";
    }

    /**
     * 返回编辑界面role_form
     */
    @ApiIgnore
    @GetMapping("/role/form")
    public String form(HttpServletResponse response, HttpServletRequest request) {
        return "/site/role/form";
    }

    /**
     * 查询应用列表
     *
     * @param app 应用实体
     */
    @ApiOperation(value = "查询应用列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appName", value = "站点名称", required = false, paramType = "query"),
            @ApiImplicitParam(name = "parentId", value = "上级站点", paramType = "query"),
            @ApiImplicitParam(name = "appState", value = "网站状态", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appStyle", value = "站点风格", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appLogo", value = "网站Logo", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appPayDate", value = "每年续费日期", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appUrl", value = "域名", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appKeyword", value = "关键字", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appDescription", value = "描述", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appCopyright", value = "版权信息", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appPay", value = "续费清单", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "del", value = "删除标记", required = false, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "编号", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore AppEntity app, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
//        BasicUtil.startPage();
//        QueryWrapper<AppEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like(StringUtils.isNotEmpty(app.getAppName()),"app_name",app.getAppName());
//        List<AppEntity> appList = appBiz.list(queryWrapper);
//        return ResultData.build().success(new EUListBean(appList, (int) BasicUtil.endPage(appList).getTotal()));

        BasicUtil.startPage();
        List appList = null;
        if (app.getSqlWhere() != null) {
            appList = appBiz.query(app);
        } else {
            appList = appBiz.list(new QueryWrapper(app));
        }
        return ResultData.build().success(new EUListBean(appList, (int) BasicUtil.endPage(appList).getTotal()));

    }


    /**
     * 跳转站点
     */
    @ApiOperation(value = "跳转指定站点接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "站点id",dataType = "Integer",required = false, paramType = "query"),
    })
    @GetMapping("/jump")
    @ResponseBody
    public ResultData jump(Integer appId, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        ManagerEntity managerSession = BasicUtil.getManager();
        AppEntity appEntity = appBiz.getById(appId);
        if (appEntity == null) {
            return ResultData.build().error(this.getResString("app.id"));
        }
        // roleBiz继承了RoleEntity，导致 roleBiz.queryAll() 返回类型 List<RoleEntity>，这里做一步强转
        // 这里理解超级管理员可以跳转所有站点
        BasicUtil.setSession(Const.APP, appEntity);
        BasicUtil.setSession("appId", appEntity.getAppId());
        return ResultData.build().success(appEntity);
    }

    @ApiOperation(value = "管理员与对应站点绑定接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "站点id",dataType = "Integer",required = false, paramType = "query"),
            @ApiImplicitParam(name = "managerIds", value = "管理员id,多个逗号隔开",dataType = "Integer",required = false, paramType = "query"),
    })
    @PostMapping("/saveWebSiteRole")
    @ResponseBody
    @RequiresPermissions("basic:role:save")
    public ResultData saveWebSiteRole(HttpServletResponse response, HttpServletRequest request) {
        String managerIds = BasicUtil.getString("managerIds");
        if (StringUtils.isBlank(managerIds)) {
            return ResultData.build().error("至少选择一个管理员");
        }
        Integer appId = BasicUtil.getInt("appId");
        if (appId == 0) {
            return ResultData.build().error();
        }
        // 将管理员与对应站点绑定,绑定后的角色默认拥有super权限
        SiteAppManagerEntity siteAppManager = new SiteAppManagerEntity();
        siteAppManager.setAppId(appId);
        siteAppManagerBiz.remove(new QueryWrapper<>(siteAppManager));
        for (String managerId : managerIds.split(",")) {
            siteAppManager = new SiteAppManagerEntity();
            siteAppManager.setAppId(appId);
            siteAppManager.setManagerId(managerId);
            siteAppManagerBiz.save(siteAppManager);
        }
        return ResultData.build().success();
    }



    @ApiOperation(value = "根据AppId 查询站点下的角色接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "站点id",dataType = "Integer",required = false, paramType = "query"),
    })
    @GetMapping("/getWebSiteRole")
    @ResponseBody
    public ResultData getWebSiteRole(HttpServletResponse response, HttpServletRequest request) {
        // 获取appId
        Integer appId = BasicUtil.getInt("appId");
        // 根据AppId 查询站点下的角色
        SiteAppManagerEntity siteAppManagerEntity = new SiteAppManagerEntity();
        siteAppManagerEntity.setAppId(appId);
        List<String> managerIds = siteAppManagerBiz.list(new QueryWrapper<>(siteAppManagerEntity))
                .stream().map(SiteAppManagerEntity::getManagerId).collect(Collectors.toList());
        if (CollUtil.isEmpty(managerIds)) {
            return ResultData.build().success(managerIds);
        }
        List<String> managerList = managerBiz.listByIds(managerIds)
                .stream().map(ManagerEntity::getId).collect(Collectors.toList());
        return ResultData.build().success(managerList);
    }


    /*
     * 查询管理员所管理的app
     * */
    @ApiOperation(value = "查询管理员所管理的app接口")
    @RequestMapping(value = "/listOwnerApp", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultData listOwnerApp(HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
        ManagerEntity managerSession = BasicUtil.getManager();
        List<AppEntity> appList;
        // 超级管理员默认拥有所有站点权限
        if (ManagerAdminEnum.SUPER.toString().equals(managerSession.getManagerAdmin())) {
            appList = appBiz.list();
            return ResultData.build().success(appList);
        }
        // 查询当前管理员所关联的站点
        LambdaQueryWrapper<SiteAppManagerEntity> wrapper = new QueryWrapper<SiteAppManagerEntity>().lambda();
        wrapper.eq(SiteAppManagerEntity::getManagerId, managerSession.getId());
        List<SiteAppManagerEntity> siteAppManagerList = siteAppManagerBiz.list(wrapper);
        if (CollUtil.isEmpty(siteAppManagerList)) {
            // 当前管理员没有关联app则返回空数组
            return ResultData.build().success(ListUtil.empty());
        }
        appList = appBiz.listByIds(siteAppManagerList.stream().map(SiteAppManagerEntity::getAppId).collect(Collectors.toList()));
        return ResultData.build().success(appList);
    }


    @ApiOperation(value = "保存应用列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appName", value = "站点名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "appState", value = "网站状态", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appStyle", value = "站点风格", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appLogo", value = "网站Logo", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appPayDate", value = "每年续费日期", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appUrl", value = "域名", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appKeyword", value = "关键字", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appDescription", value = "描述", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appCopyright", value = "版权信息", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appPay", value = "续费清单", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
    })
    /**
     * 保存应用
     * @param app 应用实体
     */
    @PostMapping("/save")
    @ResponseBody
    @LogAnn(title = "保存应用", businessType = BusinessTypeEnum.INSERT)
    @RequiresPermissions("site:app:save")
    public ResultData save(@ModelAttribute @ApiIgnore AppEntity app, HttpServletResponse response, HttpServletRequest request) {

        if (!StringUtil.checkLength(app.getAppName(), 1, 50)) {
            ResultData.build().error(getResString("err.length", this.getResString("appTitle"), "1", "50"));
        }
        if (StringUtils.isNotBlank(app.getAppStyle()) && !StringUtil.checkLength(app.getAppStyle(), 1, 30)) {
            return ResultData.build().error(getResString("err.length", this.getResString("appStyle"), "1", "30"));
        }
        if (ObjectUtil.isNotNull(app.getAppHostUrl())) {
            if (!StringUtil.checkLength(app.getAppHostUrl(), 10, 150)) {
                return ResultData.build().error(getResString("err.length", this.getResString("appUrl"), "10", "150"));
            }
        }
        //验证站点名称的值是否合法
        if (StringUtils.isEmpty(app.getAppName())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("appTitle")));
        }
        if (!StringUtil.checkLength(app.getAppName() + "", 1, 30)) {
            return ResultData.build().error(getResString("err.length", this.getResString("appTitle"), "1", "30"));
        }
        if (super.validated("app", "app_name", app.getAppName())) {
            return ResultData.build().error(getResString("err.exist", this.getResString("appTitle")));
        }
        if (super.validated("app", "app_url", app.getAppUrl())) {
            return ResultData.build().error(getResString("err.exist", this.getResString("appUrl")));
        }
//        if (app.getParentId() == null || app.getParentId().equals("0")) {
//            return ResultData.build().error("请检查上级站点是否填写!");
//        }
        appBiz.saveEntity(app);
        siteAppManagerBiz.saveSiteAndMDiyModel(app);
        appBiz.updateCache();
        return ResultData.build().success(app);
    }

    /**
     * @param apps 应用实体
     */
    @ApiOperation(value = "批量删除应用列表接口")
    @PostMapping("/delete")
    @ResponseBody
    @LogAnn(title = "删除应用", businessType = BusinessTypeEnum.DELETE)
    @RequiresPermissions("site:app:del")
    public ResultData delete(@RequestBody List<AppEntity> apps, HttpServletResponse response, HttpServletRequest request) {
        List<String> ids = new ArrayList<>();
        ArrayList<AppEntity> appEntities = new ArrayList<>();
        for (AppEntity app : apps) {
            appEntities.addAll(appBiz.queryChildren(app));
        }
        for (AppEntity app : appEntities) {
            if (BasicUtil.getApp().getAppId() == app.getAppId()) {
                return ResultData.build().error("不能删除自己的站点");
            }
            if (!(app.getNotDel() == 1)) {
                ids.add(app.getId());
            }
        }
        for (AppEntity app : apps) {
            if (!(app.getNotDel() == 1)) {
                appBiz.deleteEntity(app.getId());
            }
        }
        //siteAppManagerBiz.deleteByAppIds(ids);
        appBiz.updateCache();
        MapCacheUtil.remove(BasicUtil.getDomain());
        return ResultData.build().success();
    }

    /**
     * 更新站点信息
     *
     * @param mode     ModelMap实体对象
     * @param app      站点对象
     * @param request  请求对象
     * @param response 相应对象
     */
    @ApiOperation(value = "更新站点信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appName", value = "应用名称", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appDescription", value = "应用描述", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appLogo", value = "应用logo", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appDatetime", value = "站点日期", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appKeyword", value = "网站关键字", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appCopyright", value = "网站版权信息", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appStyle", value = "网站采用的模板风格", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appUrl", value = "网站域名", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appManagerId", value = "管理站点的管理员id", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appPayDate", value = "应用续费时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appPay", value = "应用费用清单", required = false, paramType = "query"),
    })
    @PostMapping("/update")
    @LogAnn(title = "更新站点信息", businessType = BusinessTypeEnum.UPDATE)
    @RequiresPermissions("site:app:update")
    @ResponseBody
    public ResultData update(@ModelAttribute @ApiIgnore AppEntity app, ModelMap mode, HttpServletRequest request,
                             HttpServletResponse response) {

        if (!StringUtil.checkLength(app.getAppName(), 1, 50)) {
            ResultData.build().error(getResString("err.length", this.getResString("appTitle"), "1", "50"));
        }
        if (StringUtils.isNotBlank(app.getAppStyle()) && !StringUtil.checkLength(app.getAppStyle(), 1, 30)) {
            return ResultData.build().error(getResString("err.length", this.getResString("appStyle"), "1", "30"));
        }
        if (ObjectUtil.isNotNull(app.getAppHostUrl())) {
            if (!StringUtil.checkLength(app.getAppHostUrl(), 10, 150)) {
                return ResultData.build().error(getResString("err.length", this.getResString("appUrl"), "10", "150"));
            }
        }
        //验证网站生成目录的值是否合法
        if (StringUtils.isEmpty(app.getAppDir())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("app.dir")));
        }
        if (!StringUtil.checkLength(app.getAppDir() + "", 0, 50)) {
            return ResultData.build().error(getResString("err.length", this.getResString("app.dir"), "0", "10"));
        }
        //验证重复
        if (super.validated("app", "app_dir", app.getAppDir(), app.getId(), "id")) {
            return ResultData.build().error(getResString("err.exist", this.getResString("app.dir")));
        }
        if (super.validated("app", "app_url", app.getAppUrl(), app.getId(), "id")) {
            return ResultData.build().error(getResString("err.exist", this.getResString("appUrl")));
        }
        //若选择的为自己直接返回错误
        if (StringUtils.isNotEmpty(app.getParentId()) && app.getParentId().equals(app.getId())) {
            return ResultData.build().error();
        }
        //判断是否选择子级为所属栏目
        if (appBiz.isChildren(app)) {
            return ResultData.build().error();
        }
        appBiz.updateEntity(app);
        appBiz.updateCache();
        MapCacheUtil.remove(BasicUtil.getDomain());
        //update
        AppEntity appEntity = (AppEntity) BasicUtil.getSession(Const.APP);
        if (appEntity!=null && app.getIntegerId() == appEntity.getIntegerId()) {
            BasicUtil.setSession(Const.APP, app);
        }
        return ResultData.build().success();
    }
    @ApiOperation(value = "检查app接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fieldName", value = "字段", required = false, paramType = "query"),
    })
    @GetMapping("verify")
    @ResponseBody
    public ResultData verify(String fieldName, String fieldValue, String id, String idName) {
        boolean verify = false;
        if (StringUtils.isBlank(id)) {
            verify = super.validated("app", fieldName, fieldValue);
        } else {
            verify = super.validated("app", fieldName, fieldValue, id, idName);
        }
        if (verify) {
            return ResultData.build().success(false);
        } else {
            return ResultData.build().success(true);
        }
    }

    /**
     * 获取顶级站点
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取顶级节点")
    @GetMapping("/getTop")
    @ResponseBody
    public ResultData getTop(HttpServletRequest request) {
        List<AppEntity> appEntityList = appBiz.list().stream().filter(appEntity -> appEntity.getNotDel() == 1).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(appEntityList)) {
            return ResultData.build().success(appEntityList.get(0));
        }
        return ResultData.build().error();

    }


    /**
     * 插入appId方法
     */
    @ApiOperation(value = "插入表appId接口")
    @PostMapping("/init")
    @ResponseBody
    @RequiresPermissions("site:app:init")
    public ResultData init() {
        Map map = ConfigUtil.getMap("站群配置");
        Object siteEnable = map.get("siteEnable");
        if (siteEnable != null && "true".equals(siteEnable.toString())) {
            Object table = map.get("siteTables");
            if (table == null || StringUtils.isBlank(table.toString())) {
                return ResultData.build().error("站群涉及表为空,请检查站群配置!");
            }
            List<String> tables = StrUtil.split(table.toString(), ",");
            for (String tableName : tables) {
                try {
                    if (StringUtils.isNotEmpty(tableName)) {
                        siteAppManagerBiz.initAppId(tableName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.warn("{}插入站群编号字段未成功，可能表已经存在APP_ID字段", tableName);
                    throw  new BusinessException("插入站群编号字段未成功，可能表已经存在APP_ID字段");
                }
            }
            return ResultData.build().success();
        }

        return ResultData.build().error("请先开启站群!");

    }

    /**
     * 取消插入appId方法
     */
    @ApiOperation(value = "删除表appId接口")
    @PostMapping("/remove")
    @ResponseBody
    @RequiresPermissions("site:app:remove")
    public ResultData remove() {
        Map map = ConfigUtil.getMap("站群配置");
        Object siteEnable = map.get("siteEnable");
        if (siteEnable != null && "true".equals(siteEnable.toString())) {
            Object table = map.get("siteTables");
            if (table == null || StringUtils.isBlank(table.toString())) {
                return ResultData.build().error("站群涉及表为空,请检查站群配置!");
            }
            List<String> tables = StrUtil.split(table.toString(), ",");
            for (String tableName : tables) {
                try {
                    if (StringUtils.isNotEmpty(tableName)) {
                        siteAppManagerBiz.removeAppId(tableName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.warn("{}插入站群编号字段未成功，可能该表不存在", tableName);
                    throw  new BusinessException("插入站群编号字段未成功，可能该表不存在");
                }
            }
            return ResultData.build().success();
        }
        return ResultData.build().error("请先开启站群!");


    }

}
