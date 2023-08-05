/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.cms.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.approval.biz.IConfigBiz;
import net.mingsoft.approval.constant.e.ProgressStatusEnum;
import net.mingsoft.approval.entity.ConfigEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.base.util.SqlInjectionUtil;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.cms.bean.CategoryBean;
import net.mingsoft.cms.bean.ContentBean;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.service.RenderingService;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.datascope.annotation.DataScope;
import net.mingsoft.datascope.biz.IDataBiz;
import net.mingsoft.elasticsearch.annotation.ESDelete;
import net.mingsoft.elasticsearch.annotation.ESSave;
import net.mingsoft.mdiy.bean.PageBean;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.entity.ModelEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.progress.bean.ProgressLogBean;
import net.mingsoft.progress.biz.IProgressLogBiz;
import net.mingsoft.progress.entity.ProgressLogEntity;
import net.mingsoft.wordfilter.annotation.SensitiveWord;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 文章管理控制层
 *
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：新增auditList方法和保存方法增加@Progress(schemeName = "文章审核"),新增提交方法<br/>
 * 2021-07-07 删除文章时不会将文章扩展字段的表里的数据进行删除，需要删除模型表里的数据，避免造成脏数据<br/>
 * 新增95行modelBiz，329-340行增加了删除文章扩展模型表里的数据的功能<br/>
 * 2022-02-12 save() update() 方法添加审批自定义配置开关是否开启
 */
@Api(tags = {"后端-文章接口"})
@Controller("cmsContentAction")
@RequestMapping("/${ms.manager.path}/cms/content")
public class ContentAction extends BaseAction {

    /**
     * 注入文章业务层
     */
    @Autowired
    private IContentBiz contentBiz;

    @Autowired
    private IConfigBiz configBiz;

    /**
     * 注入进度日志业务
     */
    @Autowired
    private IProgressLogBiz progressLogBiz;

    /**
     * 注入配置模型业务层
     */
    @Autowired
    private IModelBiz modelBiz;

    @Autowired
    private ICategoryBiz categoryBiz;

    @Autowired
    private IDataBiz dataBiz;

    @Value("${ms.diy.html-dir:html}")
    private String htmlDir;

    @Autowired
    private RenderingService renderingService;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("cms:content:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/cms/content/index";
    }

    /**
     * 返回主界面main
     */
    @ApiIgnore
    @GetMapping("/main")
    @RequiresPermissions("cms:content:view")
    public String main(HttpServletResponse response, HttpServletRequest request) {
        return "/cms/content/main";
    }

    /**
     * 返回编辑界面content_form
     */
    @ApiIgnore
    @GetMapping("/form")
    @RequiresPermissions(value = {"cms:content:save", "cms:content:update"}, logical = Logical.OR)
    public String form(@ModelAttribute ContentEntity content, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        model.addAttribute("appId", BasicUtil.getApp().getAppId());
        return "/cms/content/form";
    }

    /**
     * 待初审页面
     */
    @ApiIgnore
    @GetMapping("/review")
    @RequiresPermissions("approval:config:auditList")
    public String review(HttpServletResponse response, HttpServletRequest request) {
        return "/cms/content/review";
    }

    /**
     * 回收站页面
     */
    @ApiIgnore
    @GetMapping("/recycle")
    @RequiresPermissions("cms:content:recycleBin")
    public String recycle(HttpServletResponse response, HttpServletRequest request) {
        return "/cms/content/recycle-bin";
    }

    /**
     * 查询文章列表
     *
     * @param content 文章实体
     */
    @ApiOperation(value = "查询文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentTitle", value = "文章标题", required = false, paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "所属栏目", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentType", value = "文章类型", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDisplay", value = "是否显示", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentAuthor", value = "文章作者", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentSource", value = "文章来源", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDatetime", value = "发布时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentSort", value = "自定义顺序", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentImg", value = "文章缩略图", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDescription", value = "描述", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentKeyword", value = "关键字", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDetails", value = "文章内容", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentUrl", value = "文章跳转链接地址", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appid", value = "文章管理的应用id", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    //@RequiresPermissions("cms:content:view") 此处权限标识导致拥有文章批量操作权限无文章查看权限的用户无法正常使用功能
    public ResultData list(@ModelAttribute @ApiIgnore ContentBean content, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
        // 检查SQL注入
        SqlInjectionUtil.filterContent(content.getCategoryId());
        ManagerEntity manager =  BasicUtil.getManager();
        List<String> dataIdList = new ArrayList<>();
        List<CategoryEntity> categoryEntities = null;
        // 超级管理员不进行过滤，获取所有栏目
        if (ManagerAdminEnum.SUPER.toString().equals(manager.getManagerAdmin())) {
            categoryEntities = categoryBiz.list();
            for (CategoryEntity categoryEntity : categoryEntities) {
                dataIdList.add(categoryEntity.getId());
            }
        } else {
            for (String roleId : manager.getRoleIds().split(",")) {
                dataIdList.addAll(dataBiz.queryProjectList(roleId, "管理员栏目权限"));
            }
        }

        //一个栏目都没有,直接返回
        if (dataIdList.size() == 0) {
            return ResultData.build().success(new EUListBean(CollUtil.newArrayList(), 0));
        }
        if (content.getCategoryId() == null) {
            //获取单篇的栏目id
            List<String> categoryIds = categoryBiz.list(new LambdaQueryWrapper<CategoryEntity>()
                    .eq(CategoryEntity::getCategoryType, CategoryTypeEnum.COVER.toString()))
                    .stream().map(CategoryEntity::getId).collect(Collectors.toList());
            //删除所有单篇的id
            dataIdList.removeAll(categoryIds);
            content.setCategoryIds(CollUtil.join(dataIdList, ","));
        } else {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setId(content.getCategoryId());
            categoryEntities = categoryBiz.queryChildren(categoryEntity);
            categoryEntity.setId(content.getCategoryId());
            categoryEntities.add(categoryEntity);
            //过滤单篇的栏目
            categoryEntities = categoryEntities.stream()
                    .filter(category -> !CategoryTypeEnum.COVER.toString().equals(category.getCategoryType()))
                    .collect(Collectors.toList());
            List<String> ids = new ArrayList<>();
            categoryEntities.forEach(item -> {
                ids.add(item.getId());
            });
            content.setCategoryIds(CollUtil.join(CollUtil.intersection(ids, dataIdList), ","));
        }
        BasicUtil.startPage();
        List<ContentEntity> contentList = contentBiz.query(content);
        return ResultData.build().success(new EUListBean(contentList, (int) BasicUtil.endPage(contentList).getTotal()));
    }

    /**
     * 获取文章
     *
     * @param content 文章实体
     */
    @ApiOperation(value = "获取文章列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required = true, paramType = "query")
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (content.getId() == null) {
            return ResultData.build().error();
        }
        ContentEntity _content = contentBiz.getById(content.getId());
        return ResultData.build().success(_content);
    }

    /**
     * 获取文章
     *
     * @param content 文章实体
     */
    @ApiOperation(value = "根据封面获取文章列表接口")
    @ApiImplicitParam(name = "categoryId", value = "分类编号", required = true, paramType = "query")
    @GetMapping("/getFromFengMian")
    @ResponseBody
    @RequiresPermissions("cms:content:view")
    public ResultData getFromFengMian(@ModelAttribute @ApiIgnore ContentEntity content) {
        if (content.getCategoryId() == null) {
            return ResultData.build().error();
        }
        List<ContentEntity> list = contentBiz.lambdaQuery().eq(ContentEntity::getCategoryId, content.getCategoryId()).list();
        if (list.size() > 1) {
            list.forEach(c->{
                LOG.error("封面文章异常，文章ID：{}",c.getId());
            });
            LOG.error("获取封面文章异常，默认获取第一条，需要手动删除数据库");
        }
        return ResultData.build().success(list.size() > 0 ? list.get(0) : null);
    }

    /**
     * 保存文章
     *
     * @param content 文章实体
     */
    @ApiOperation(value = "保存文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentTitle", value = "文章标题", required = true, paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "所属栏目", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentType", value = "文章类型", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDisplay", value = "是否显示", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentAuthor", value = "文章作者", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentSource", value = "文章来源", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDatetime", value = "发布时间", required = true, paramType = "query"),
            @ApiImplicitParam(name = "contentSort", value = "自定义顺序", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentImg", value = "文章缩略图", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDescription", value = "描述", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentKeyword", value = "关键字", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDetails", value = "文章内容", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentUrl", value = "文章跳转链接地址", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appid", value = "文章管理的应用id", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "del", value = "删除标记", required = false, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "编号", required = false, paramType = "query"),
    })
    @PostMapping("/save")
    @ESSave
    @ResponseBody
    @LogAnn(title = "保存文章", businessType = BusinessTypeEnum.CONTENT_INSERT, saveId = true)
    @RequiresPermissions("cms:content:save")
    @SensitiveWord
    @DataScope(type = "管理员栏目权限", id = "getCategoryId", requiresPermissions = "cms:content:save")
    public ResultData save(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response, HttpServletRequest request) {

        //验证缩略图参数值是否合法
        if (content.getContentImg()==null || !content.getContentImg().matches("^\\[.{1,}]$")){
            content.setContentImg("");
        }
        //验证文章标题的值是否合法
        if (StringUtil.isBlank(content.getContentTitle())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("content.title")));
        }
        if (!StringUtil.checkLength(content.getContentTitle() + "", 0, 200)) {
            return ResultData.build().error(getResString("err.length", this.getResString("content.title"), "0", "200"));
        }
        if (!StringUtil.checkLength(content.getContentAuthor() + "", 0, 200)) {
            return ResultData.build().error(getResString("err.length", this.getResString("content.author"), "0", "200"));
        }
        if (!StringUtil.checkLength(content.getContentSource() + "", 0, 200)) {
            return ResultData.build().error(getResString("err.length", this.getResString("content.source"), "0", "200"));
        }
        //验证发布时间的值是否合法
        if (StringUtil.isBlank(content.getContentDatetime())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("content.datetime")));
        }
        // 文章标签限制5个
        if (StringUtils.isNotEmpty(content.getContentTags()) && content.getContentTags().split(",").length > 5){
            return ResultData.build().error(getResString("err.length",this.getResString("content.tags"),"0","5"));
        }
        boolean progressSwitch = ConfigUtil.getBoolean("审批开关", "progressSwitch");
        LambdaQueryWrapper<ConfigEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigEntity::getCategoryId, content.getCategoryId());
        List<ConfigEntity> configList = configBiz.list(wrapper);
        if (progressSwitch && CollUtil.isNotEmpty(configList)) {
            //判断当前栏目是单篇
//            CategoryEntity categoryEntity = categoryBiz.getById(content.getCategoryId());
//            if (categoryEntity != null && categoryEntity.getCategoryType().equals(CategoryTypeEnum.COVER.toString())) {
//                content.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
//            } else {
                content.setProgressStatus(ProgressStatusEnum.DRAFT.toString());
//            }
        } else {
            content.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
        }

        content.setHasDetailHtml(0);
        content.setHasListHtml(0);
        contentBiz.save(content);
        return ResultData.build().success(content);
    }

    /**
     * @param contents 文章实体
     */
    @ApiOperation(value = "批量删除文章列表接口")
    @PostMapping("/delete")
    @ESDelete
    @ResponseBody
    @LogAnn(title = "逻辑删除文章", businessType = BusinessTypeEnum.CONTENT_DELETE, saveId = true)
    @RequiresPermissions("cms:content:del")
    @DataScope(type = "管理员栏目权限", id = "getCategoryId", requiresPermissions = "cms:content:del")
    public ResultData delete(@RequestBody List<ContentEntity> contents, HttpServletResponse response, HttpServletRequest request) {
        List<String> ids = contents.stream().map(ContentEntity::getId).collect(Collectors.toList());
        contentBiz.removeByIds(ids);
        return ResultData.build().success(ids);
    }

    /**
     * 更新文章列表
     *
     * @param content 文章实体
     */
    @ApiOperation(value = "更新文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "contentTitle", value = "文章标题", required = true, paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "所属栏目", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentType", value = "文章类型", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDisplay", value = "是否显示", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentAuthor", value = "文章作者", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentSource", value = "文章来源", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDatetime", value = "发布时间", required = true, paramType = "query"),
            @ApiImplicitParam(name = "contentSort", value = "自定义顺序", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentImg", value = "文章缩略图", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDescription", value = "描述", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentKeyword", value = "关键字", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentDetails", value = "文章内容", required = false, paramType = "query"),
            @ApiImplicitParam(name = "contentUrl", value = "文章跳转链接地址", required = false, paramType = "query"),
            @ApiImplicitParam(name = "appid", value = "文章管理的应用id", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "del", value = "删除标记", required = false, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "编号", required = false, paramType = "query"),
    })
    @PostMapping("/update")
    @ESSave
    @ResponseBody
    @LogAnn(title = "更新文章", businessType = BusinessTypeEnum.CONTENT_UPDATE, saveId = true)
    @RequiresPermissions("cms:content:update")
    @SensitiveWord
    @DataScope(type = "管理员栏目权限", id = "getCategoryId", requiresPermissions = "cms:content:update")
    public ResultData update(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response,
                             HttpServletRequest request) {
        //读取老完整内容，需要记录老文章付附属栏目
        ContentEntity oldContent = contentBiz.getById(content);

        //验证缩略图参数值是否合法
        if (content.getContentImg()==null || !content.getContentImg().matches("^\\[.{1,}]$")){
            content.setContentImg("");
        }
        //验证文章标题的值是否合法
        if (StringUtil.isBlank(content.getContentTitle())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("content.title")));
        }
        if (!StringUtil.checkLength(content.getContentTitle() + "", 0, 200)) {
            return ResultData.build().error(getResString("err.length", this.getResString("content.title"), "0", "200"));
        }
        if (!StringUtil.checkLength(content.getContentAuthor() + "", 0, 200)) {
            return ResultData.build().error(getResString("err.length", this.getResString("content.author"), "0", "200"));
        }
        if (!StringUtil.checkLength(content.getContentSource() + "", 0, 200)) {
            return ResultData.build().error(getResString("err.length", this.getResString("content.source"), "0", "200"));
        }
        //限制文章关键字0-200
        if (!StringUtil.checkLength(content.getContentKeyword() + "", 0, 200)) {
            return ResultData.build().error(getResString("err.length", this.getResString("content.keyword"), "0", "200"));
        }
        //验证发布时间的值是否合法
        if (StringUtil.isBlank(content.getContentDatetime())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("content.datetime")));
        }
        // 文章标签限制5个
        if (StringUtils.isNotEmpty(content.getContentTags()) && content.getContentTags().split(",").length > 5){
            return ResultData.build().error(getResString("err.length",this.getResString("content.tags"),"0","5"));
        }
        boolean progressSwitch = ConfigUtil.getBoolean("审批开关", "progressSwitch");
        if (progressSwitch) {
//            CategoryEntity categoryEntity = categoryBiz.getById(content.getCategoryId());
            //判断当前栏目是单篇,true为不是单篇
//            boolean flag = categoryEntity != null && !categoryEntity.getCategoryType().equals(CategoryTypeEnum.COVER.toString());
            LambdaQueryWrapper<ConfigEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ConfigEntity::getCategoryId, content.getCategoryId());
            List<ConfigEntity> configList = configBiz.list(wrapper);
            if(CollUtil.isNotEmpty(configList)){
                if (StringUtils.equals(content.getProgressStatus(), ProgressStatusEnum.APPROVED.toString())) {
                    content.setProgressStatus(ProgressStatusEnum.DRAFT.toString());
                }else {
                    // 防止修改状态,aop需要原来的最新的文章审批状态
                    content.setProgressStatus(null);
                }
            }else {
                LambdaUpdateWrapper<ProgressLogEntity> progressLogWrapper = new UpdateWrapper<ProgressLogEntity>().lambda();
                progressLogWrapper.set(ProgressLogEntity::getPlContent, "更改审批配置节点导致强制跳过审批并完成文章审批");
                progressLogWrapper.set(ProgressLogEntity::getPlStatus, "skip");
                progressLogWrapper.eq(ProgressLogEntity::getDataId, content.getId());
                progressLogWrapper.isNull(ProgressLogEntity::getPlStatus);
                progressLogBiz.update(progressLogWrapper);
                content.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
            }
        } else {
            content.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
        }
        // 重置静态化状态
        content.setHasDetailHtml(0);
        content.setHasListHtml(0);
        contentBiz.updateById(content);

        
        //为了能让 contentaop 能联动自动静态化附属栏目，可以联动静态化附属栏目
        if(StringUtils.isNotBlank(oldContent.getCategoryIds())) { //如果历史附属栏目为空可以跳过，就不需要生成附属栏目
            if(StringUtils.isNotBlank(content.getCategoryIds())) { //表示当前文章修改过附属栏目，需要将 老的附属栏目与新的附属栏目一同静态化
                content.setCategoryIds(content.getCategoryIds().concat(",").concat(oldContent.getCategoryIds()));
            } else {
                content.setCategoryIds(oldContent.getCategoryIds()); //表示当前文章删除老附属栏目，也需要对上一次绑定的附属栏目进行静态化
            }
        }

        return ResultData.build().success(content);
    }

    /**
     * 根据方案名称获取文章
     *
     * @return
     */
    @ApiOperation(value = "根据方案名称获取文章")
    @GetMapping("/auditList")
    @ResponseBody
    @RequiresPermissions("approval:config:auditList")
    public ResultData auditList(@ModelAttribute @ApiIgnore ContentBean content, HttpServletResponse response, HttpServletRequest request) {
        if (StringUtils.isEmpty(content.getSchemeName())) {
            return ResultData.build().error();
        }
        // 角色A  新闻栏目   1级 3级权限  后勤 2级  3级
        // 1.获取到当前角色的审批权限列表
        // 获取角色ID
        String roleIds = BasicUtil.getManager().getRoleIds();
        // 查询当前角色拥有的审批配置 update
        List<ConfigEntity> configs = new ArrayList<>();
        for (String roleId : roleIds.split(",")) {
            configs.addAll(configBiz.queryListForRole(content.getSchemeName(), null, Integer.parseInt(roleId), null));
        }
        if (CollUtil.isEmpty(configs)) {
            // 无审批权限直接返回空
            return ResultData.build().success(new EUListBean(null, 0));
        }
        // 2.获取当前角色拥有的栏目权限，栏目审核级数权限
        // 组装栏目对应的审批级数
        Map<String, List<String>> categoryIdByProgressId = configs.stream().
                collect(Collectors.groupingBy(ConfigEntity::getCategoryId, Collectors.mapping(ConfigEntity::getProgressId, Collectors.toList())));
        Set<String> categorySets = categoryIdByProgressId.keySet();
        // 获取当前角色下的所有拥有权限的栏目ID
        List<String> categoryIds = new ArrayList<>(categorySets);
        // 用于前端筛选
        String plStatus = StringUtils.equals(BasicUtil.getString("plStatus"), "") ? null : BasicUtil.getString("plStatus");
        // 3.从审批日志中获取到所有的拥有栏目权限的待审文章
        List<ProgressLogBean> progressLogBeans = configBiz.auditListForCategoryIds(categoryIds, plStatus);
        if (CollUtil.isEmpty(progressLogBeans)) {
            // 无待审文章直接返回空
            return ResultData.build().success(new EUListBean(null, 0));
        }
        // 4.进行审批级数过滤
        // 文章ID数组
        List<String> contentIds = new ArrayList<>();
        //for 过滤审核级别数据
        for (ProgressLogBean progressLogBean : progressLogBeans) {
            // 日志审核等级=拥有的权限
            boolean flag = categoryIdByProgressId.get(progressLogBean.getCategoryId()).stream().anyMatch(
                    progressId -> progressLogBean.getProgressId().toString().equals(progressId)
            );
            if (flag) {
                contentIds.add(progressLogBean.getDataId());
            }
        }
        if (CollUtil.isEmpty(contentIds)) {
            // 无审核权限的待审文章直接返回空
            return ResultData.build().success(new EUListBean(null, 0));
        }
//        ManagerEntity managerSession = (ManagerEntity)BasicUtil.getSession(SessionConstEnum.MANAGER_SESSION);
//        // 普通管理员需要做栏目权限校验
//        if (!ManagerAdminEnum.SUPER.toString().equals(managerSession.getManagerAdmin()) ) {
//            List<String> dataIdList = dataBiz.queryProjectList(String.valueOf(managerSession.getRoleId()),"managerRole");
//            if(CollUtil.isNotEmpty(dataIdList)){
//                queryWrapper.in("category_id",dataIdList);
//            }else {
//                LOG.debug("用户没有栏目权限");
//                return ResultData.build().success(new EUListBean(null,0));
//            }
//        }
        BasicUtil.startPage();
        String contentTitle = content.getContentTitle();
        List<ContentBean> contentList = contentBiz.auditList(contentTitle,contentIds, plStatus, ProgressStatusEnum.APPROVED.toString());
        return ResultData.build().success(new EUListBean(contentList, (int) BasicUtil.endPage(contentList).getTotal()));
    }

    /**
     * 提交审核
     */
    @ApiOperation(value = "提交文章审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataId", value = "文章ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "schemeName", value = "方案名称", required = true, paramType = "query"),
    })
    @PostMapping("/submit")
    @ResponseBody
    @RequiresPermissions("cms:content:submit")
    public ResultData submit(String schemeName, String dataId, HttpServletResponse response, HttpServletRequest request) {
        if (StringUtils.isEmpty(schemeName)) {
            return ResultData.build().error(this.getResString("schemeName"));
        }
        if (StringUtils.isEmpty(dataId)) {
            return ResultData.build().error(getResString("err.empty", this.getResString("dataId")));
        }
        ContentEntity content = contentBiz.getById(dataId);
        CategoryEntity category = categoryBiz.getById(content.getCategoryId());
        if (category.getCategoryType().equals(CategoryTypeEnum.LINK.toString())) {
            content.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
            contentBiz.updateById(content);
            return ResultData.build().success("链接不用审核");
        }
        ManagerEntity manager =  BasicUtil.getManager();
        boolean flag = configBiz.submit(schemeName, dataId, manager.getId());
        if (flag) {
            return ResultData.build().success(content);
        } else {
            return ResultData.build().error("该文章已被提交审核,不需要再次提交!");
        }

    }

    /**
     * 查询回收站列表
     */
    @ApiOperation(value = "查询回收站列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "del", value = "删除标记", required = false, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "编号", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/recycleBin", method = {RequestMethod.GET, RequestMethod.POST})
    @RequiresPermissions("cms:content:recycleBin")
    @ResponseBody
    public ResultData recycleBin(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response, HttpServletRequest request) {
        BasicUtil.startPage();
        List<ContentBean> contents = contentBiz.listForRecycle(content);
        return ResultData.build().success(new EUListBean(contents, (int) BasicUtil.endPage(contents).getTotal()));
    }

    /**
     * 彻底删除文章
     */
    @ApiOperation(value = "彻底删除文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "del", value = "删除标记", required = false, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "编号", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/cleanRecycleBin", method = {RequestMethod.GET, RequestMethod.POST})
    @LogAnn(title = "彻底删除文章", businessType = BusinessTypeEnum.CONTENT_DELETE, saveId = true)
    @RequiresPermissions("cms:content:cleanRecycleBin")
    @ResponseBody
    public ResultData cleanRecycleBin(@RequestBody List<ContentEntity> contents, HttpServletResponse response, HttpServletRequest request) {
        List<String> ids = new ArrayList<>();
        // 存放冗余CategoryEntity
        Map<String, CategoryEntity> categoryMap = new HashMap<>();
        for (ContentEntity content : contents) {
            ids.add(content.getId());
            //获取栏目实体
            if (categoryMap.get(content.getCategoryId()) == null) {
                categoryMap.put(content.getCategoryId(), categoryBiz.getById(content.getCategoryId()));
            }
            CategoryEntity categoryEntity = categoryMap.get(content.getCategoryId());
            //如果栏目绑定的模型ID为空
            if (categoryEntity != null && categoryEntity.getMdiyModelId() != null) {
                //获取到配置模型实体
                ModelEntity modelEntity = modelBiz.getById(categoryEntity.getMdiyModelId());
                //删除模型表的数据
                //模型可能会被删除
                if(modelEntity!=null) {
                    //删除模型表的数据
                    Map<String, String> map = new HashMap<>();
                    map.put("link_id", content.getId());
                    modelBiz.deleteBySQL(modelEntity.getModelTableName(), map);
                }
            }

        }
        contentBiz.completeDelete(ids);
        return ResultData.build().success(ids);
    }

    /**
     * 还原逻辑删除的文章
     *
     * @param contentEntities 文章集合
     */
    @ApiOperation(value = "还原逻辑删除的文章接口")
    @ApiImplicitParams({

    })
//    @ESSave
    @PostMapping("/reduction")
    @ResponseBody
    @LogAnn(title = "还原文章", businessType = BusinessTypeEnum.CONTENT_UPDATE, saveId = true)
    @RequiresPermissions("cms:content:reduction")
    public ResultData reduction(@RequestBody List<ContentEntity> contentEntities , HttpServletResponse response, HttpServletRequest request) {
        //参数校验
        if (null == contentEntities || contentEntities.size() == 0){
            return ResultData.build().error(getResString("err.empty",this.getResString("content.collection.parameter")));
        }
        //待还原文章id集合
        List<String> ids = new ArrayList<>();
        ids = contentEntities.stream().map(contentEntity -> {
            return contentEntity.getId();
        }).collect(Collectors.toList());
        int beforeSize = ids.size();

        CategoryEntity categoryEntity = null;
        for (ContentEntity contentEntity : contentEntities) {
            categoryEntity = categoryBiz.getById(contentEntity.getCategoryId());
            //若文章所属栏目不存在或者文章所属栏目非叶子栏目
            if (categoryEntity == null || !categoryEntity.getLeaf()){
                ids.remove(contentEntity.getId());
            }
        }
        int afterSize = ids.size();
        //根据id集合还原文章，根据id集合前后size返回提示
        if (afterSize>0) {
            contentBiz.reduction(ids);
            if (afterSize == beforeSize){
                return ResultData.build().success(this.getResString("content.reduction.success"),ids);
            }
            return ResultData.build().success(this.getResString("content.warn.reduction.success"),ids);
        }
        return ResultData.build().error(this.getResString("content.reduction.failed"));
    }

    /**
     * 动态详情页
     */
    @GetMapping("/view.do")
    @RequiresPermissions("cms:content:view")
    @ResponseBody
    public String view(HttpServletRequest reqeust, HttpServletResponse response) {
        String style = BasicUtil.getString("style");
        String templateName = ParserUtil.getTemplateName(style);
        if (StringUtils.isBlank(templateName)) {
            AppEntity app = BasicUtil.getApp();
            List<Map> list = JSONUtil.toList(app.getAppStyles(),Map.class);
            if (CollUtil.isNotEmpty(list)) {
                templateName = (String)list.get(0).get("template");
            }
        }
        //参数文章编号
        //如果存在栏目id表示是单篇内容栏目
        String _typeId = BasicUtil.getString(ParserUtil.TYPE_ID);
        String id = BasicUtil.getString(ParserUtil.ID);
        ContentEntity article = null;
        if(StringUtils.isNotBlank(_typeId)) {
            article = contentBiz.getOne(new QueryWrapper<ContentEntity>().lambda().eq(ContentEntity::getCategoryId,_typeId));
        } else if(StringUtils.isNotBlank(id)) {
            //参数文章编号
            article = contentBiz.getById(BasicUtil.getString(ParserUtil.ID));
        }
        if (ObjectUtil.isNull(article)) {
            throw new BusinessException(this.getResString("err.empty", this.getResString("id")));
        }


        PageBean page = new PageBean();
        //用于详情上下页获取当前文章列表对应的分类，根据文章查询只能获取自身分类
        String typeId = BasicUtil.getString(ParserUtil.TYPE_ID, article.getCategoryId());
        //根据文章编号查询栏目详情模版
        CategoryEntity column = categoryBiz.getById(typeId);
        //解析后的内容
        String content = "";
        Map map = BasicUtil.assemblyRequestMap();
        map.forEach((k, v) -> {
            //sql注入过滤
            map.put(k, v==null?null:v.toString().replaceAll("('|\"|\\\\)", "\\\\$1"));
        });
        ParserUtil.putBaseParams(map,templateName);

        map.put("style", style);
        //设置栏目编号
        map.put(ParserUtil.TYPE_ID, typeId);
        //设置动态请求的模块路径
        map.put(ParserUtil.PAGE, page);
        map.put(ParserUtil.ID, article.getId());

        map.put("modelName", "mcms");

        ContentBean contentBean = new ContentBean();
        contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
        contentBean.setCategoryId(String.valueOf(typeId));
        //这里会根据栏目类型执行不同的语句
        contentBean.setCategoryType(column.getCategoryType());
        AppEntity siteApp = BasicUtil.getWebsiteApp();
        if (siteApp != null){
            contentBean.setAppId(String.valueOf(siteApp.getAppId()));
        }
        contentBean.setOrderBy("date");
        List<CategoryBean> articleIdList = contentBiz.queryIdsByCategoryIdForParser(contentBean);
        Map<Object, Object> contentModelMap = new HashMap<>();
        ModelEntity contentModel = null;
        // 文章总数
        int size = articleIdList.size();
        for (int artId = 0; artId < size; ) {
            //如果不是当前文章则跳过
            if (!articleIdList.get(artId).getArticleId().equals(article.getId())) {
                artId++;
                continue;
            }
            // 文章的栏目模型编号
            String columnContentModelId = articleIdList.get(artId).getMdiyModelId();
            Map<String, Object> parserParams = new HashMap<>();
            parserParams.put(ParserUtil.COLUMN, articleIdList.get(artId));
            // 判断当前栏目是否有自定义模型
            if (columnContentModelId != null && StringUtils.isNotBlank(columnContentModelId)) {
                // 通过当前栏目的模型编号获取，自定义模型表名
                if (contentModelMap.containsKey(columnContentModelId)) {
                    parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
                } else {
                    // 通过栏目模型编号获取自定义模型实体
                    contentModel = (ModelEntity) modelBiz.getById(columnContentModelId);
                    // 将自定义模型编号设置为key值
                    contentModelMap.put(columnContentModelId, contentModel.getModelTableName());
                    parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
                }
            }
            map.putAll(parserParams);
            // 大于0则表示文章有上一页
            if (artId > 0) {
                page.setPreId(articleIdList.get(artId - 1).getArticleId());
            }
            // 当前文章数小于文章总数则表示有下一页
            if (artId < size - 1) {
                page.setNextId(articleIdList.get(artId + 1).getArticleId());
            }
            break;
        }

        String buildTemplatePath = ParserUtil.buildTemplatePath();
        String templateFolder = buildTemplatePath.concat(templateName);
        String templateContent = FileUtil.readString(FileUtil.file(templateFolder, column.getCategoryUrls(templateName)), CharsetUtil.CHARSET_UTF_8);
        templateContent = ParserUtil.replaceTag(templateContent);
        map.put(ParserUtil.IS_DO, false);
        Future<String> r = renderingService.rendering(map, templateFolder, column.getCategoryUrl(), templateContent, null);

        //根据模板路径，参数生成
        try {
            content = r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return content;
    }

}
