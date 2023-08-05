/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.biz.IRoleModelBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.ModelIsMenuEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.entity.RoleModelEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.strategy.IModelStrategy;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模块控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：200-000-000<br/>
 * 创建日期：2014-6-29<br/>
 * 历史修订： 新增 getModelListByManagerSession方法,
 *          修改方法中所有ManagerSession.getRoleId()为getModelListByManagerSession
 *          修改日期: 2022-1-5
 * 2022-1-12 菜单结构调整,使用菜单策略,更改为超级管理员始终拥有所有菜单
 * 2022-1-14 添加菜单排序功能
 * 2023-1-08 优化权限标识及菜单标题校验规则，当菜单为导航链接时不允许标题重复，菜单为功能权限时不允许权限标识重复
 */
@Api(tags={"后端-基础接口"})
@Controller
@RequestMapping("/${ms.manager.path}/basic/model")
public class ModelAction extends BaseAction {

    /**
     * 注入模块业务层
     */
    @Autowired
    private IModelBiz modelBiz;

    @Autowired
    private IModelStrategy modelStrategy;

    @Autowired
    private IManagerBiz managerBiz;
    /**
     * 角色模块关联业务层
     */
    @Autowired
    private IRoleModelBiz roleModelBiz;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("basic:model:view")
    public String index(HttpServletResponse response,HttpServletRequest request,ModelMap mode){
        List<ModelEntity> parentModelList = modelStrategy.list();
        mode.addAttribute("parentModelList", JSONUtil.toJsonStr(parentModelList));
        return "/basic/model/index";
    }


    /**
     * 查询模块表列表
     * @param model 模块表实体
     * <i>model参数包含字段信息参考：</i><br/>
     * id 模块自增长id<br/>
     * modelTitle 模块标题<br/>
     * modelCode 模块编码<br/>
     * modelId 模块的父模块id<br/>
     * modelUrl 模块连接地址<br/>
     * modelDatetime <br/>
     * modelIcon 模块图标<br/>
     * modelSort 模块的排序<br/>
     * modelIsmenu 模块是否是菜单<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>[<br/>
     * { <br/>
     * id: 模块自增长id<br/>
     * modelTitle: 模块标题<br/>
     * modelCode: 模块编码<br/>
     * modelId: 模块的父模块id<br/>
     * modelUrl: 模块连接地址<br/>
     * modelDatetime: <br/>
     * modelIcon: 模块图标<br/>
     * modelSort: 模块的排序<br/>
     * modelIsmenu: 模块是否是菜单<br/>
     * }<br/>
     * ]</dd><br/>
     */
    @ApiOperation(value="菜单列表接口")
    @GetMapping("/list")
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore ModelEntity modelEntity, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        List<ModelEntity> modelList = modelStrategy.list();
        if(CollectionUtil.isEmpty(modelList)){
            // 该角色在站点中无对应角色
            return ResultData.build().success();
        }
        modelList.sort((o1, o2) -> {
            int sort1 = o1.getModelSort() == null ? 0 : o1.getModelSort();
            int sort2 = o2.getModelSort() == null ? 0 : o2.getModelSort();
            return sort2 - sort1;
        });
        EUListBean _list = new EUListBean(modelList, modelList.size());
        return ResultData.build().success(_list);
    }

    @ApiOperation(value="菜单子集列表")
    @GetMapping("/childList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelTitle", value = "菜单名称", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelId", value = "父级菜单编号", required = false,paramType="query")
    })
    @RequiresPermissions("basic:model:view")
    @ResponseBody
    public ResultData childList(@ModelAttribute @ApiIgnore ModelEntity modelEntity, HttpServletResponse response, HttpServletRequest request) {
        List<ModelEntity> list = modelBiz.queryChildList(modelEntity);
        return ResultData.build().success(list);
    }

    @ApiOperation(value="菜单导入接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuStr", value = "菜单json", required = true,paramType="query"),
            @ApiImplicitParam(name = "modelId", value = "父级菜单编号", required = true,paramType="query")
    })
    @LogAnn(title = "导入菜单",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("/import")
    @ResponseBody
    public ResultData importMenu(String menuStr,int modelId) {
        if(StringUtils.isBlank(menuStr)){
            return ResultData.build().error(getResString("err.empty", this.getResString("menu")));
        }
        try{
            List<ModelEntity> list = JSONUtil.toList(menuStr, ModelEntity.class);
            ManagerEntity manager = BasicUtil.getManager();
            assert manager != null;


            // 检查是否有重复的菜单标题或者权限标识
            List<String> modelUrlList = new ArrayList<>();
            List<String> modelTitleList = new ArrayList<>();
            // 取出菜单的标题以及非菜单的权限标识
            this.addModelUrlAndTitleForList(list,modelUrlList,modelTitleList);
            LambdaQueryWrapper<ModelEntity> wrapper = null;
            // 判断是否有重复的菜单标题
            if (CollectionUtil.isNotEmpty(modelTitleList)){
                wrapper = new LambdaQueryWrapper<>();
                wrapper.in(ModelEntity::getModelTitle,modelTitleList);
                wrapper.in(ModelEntity::getModelIsMenu,ModelIsMenuEnum.MODEL_MEUN.toInt());
                List<ModelEntity> duplicateModelTitles = modelBiz.list(wrapper);
                if (CollectionUtil.isNotEmpty(duplicateModelTitles)){
                    // 已存在的标题集合，方便打印日志及相应到页面
                    List<String> collect = duplicateModelTitles.stream().map(ModelEntity::getModelUrl).collect(Collectors.toList());
                    Log.get(ModelAction.class).error("以下标题已存在：{}",StringUtils.join(collect,","));
                    return ResultData.build().error(getResString("err.exist",this.getResString("model.title"))+": "+StringUtils.join(collect,","));
                }
            }
            // 判断是否有重复的权限标识
            if (CollectionUtil.isNotEmpty(modelUrlList)){
                wrapper = new LambdaQueryWrapper<>();
                wrapper.in(ModelEntity::getModelUrl,modelUrlList);
                List<ModelEntity> duplicateModelUrls = modelBiz.list(wrapper);
                if (CollectionUtil.isNotEmpty(duplicateModelUrls)){
                    // 已存在的权限标识集合，方便打印日志及相应到页面
                    List<String> collect = duplicateModelUrls.stream().map(ModelEntity::getModelUrl).collect(Collectors.toList());
                    Log.get(ModelAction.class).error("以下标识已存在：{}",StringUtils.join(collect,","));
                    return ResultData.build().error(getResString("err.exist",this.getResString("model.url"))+": "+StringUtils.join(collect,","));
                }
            }
            // 导入菜单
            for (ModelEntity modelEntity : list){
                if (modelEntity.getModelIsMenu() == 0) {
                    return ResultData.build().error("功能权限按钮不能作为菜单导入!");
                }
                modelBiz.importModel(modelEntity, manager.getRoleId(), String.valueOf(modelId), modelId);
            }

        }catch (RuntimeException e){
            e.printStackTrace();
            return ResultData.build().error(getResString("model.title.or.json"));
        }catch (Exception e){
            return ResultData.build().error(getResString("err.error", this.getResString("menu")));
        }
        modelBiz.updateCache();
        return ResultData.build().success();
    }

    /**
     * 获取模块表
     * @param model 模块表实体
     * <i>model参数包含字段信息参考：</i><br/>
     * id 模块自增长id<br/>
     * modelTitle 模块标题<br/>
     * modelCode 模块编码<br/>
     * modelId 模块的父模块id<br/>
     * modelUrl 模块连接地址<br/>
     * modelDatetime <br/>
     * modelIcon 模块图标<br/>
     * modelSort 模块的排序<br/>
     * modelIsmenu 模块是否是菜单<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>{ <br/>
     * id: 模块自增长id<br/>
     * modelTitle: 模块标题<br/>
     * modelCode: 模块编码<br/>
     * modelId: 模块的父模块id<br/>
     * modelUrl: 模块连接地址<br/>
     * modelDatetime: <br/>
     * modelIcon: 模块图标<br/>
     * modelSort: 模块的排序<br/>
     * modelIsmenu: 模块是否是菜单<br/>
     * }</dd><br/>
     */
    @ApiOperation(value = "获取模块表")
    @ApiImplicitParam(name = "id", value = "模块的编号", required = true,paramType="query")
    @GetMapping("/get")
    @RequiresPermissions("basic:model:view")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore ModelEntity modelEntity,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
        if(StringUtils.isEmpty(modelEntity.getId())) {
            return ResultData.build().error(getResString("err.error", this.getResString("model.id")));
        }
        //根据父模块id查寻模块
        ModelEntity _model = (ModelEntity)modelBiz.getEntity(Integer.parseInt(modelEntity.getId()));
        if(_model != null){
            Map<String, ModelEntity> mode = new HashMap<String, ModelEntity>();
            if(_model.getModelId() != null){
                ModelEntity parentModel = (ModelEntity) modelBiz.getEntity(_model.getModelId());
                mode.put("parentModel", parentModel);
            }
            mode.put("model", _model);
            return ResultData.build().success(mode);
        }
        return ResultData.build().success(_model);
    }

    /**
     * 保存模块表实体
     * @param model 模块表实体
     * <i>model参数包含字段信息参考：</i><br/>
     * id 模块自增长id<br/>
     * modelTitle 模块标题<br/>
     * modelCode 模块编码<br/>
     * modelId 模块的父模块id<br/>
     * modelUrl 模块连接地址<br/>
     * modelDatetime <br/>
     * modelIcon 模块图标<br/>
     * modelSort 模块的排序<br/>
     * modelIsmenu 模块是否是菜单<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>{ <br/>
     * id: 模块自增长id<br/>
     * modelTitle: 模块标题<br/>
     * modelCode: 模块编码<br/>
     * modelId: 模块的父模块id<br/>
     * modelUrl: 模块连接地址<br/>
     * modelDatetime: <br/>
     * modelIcon: 模块图标<br/>
     * modelSort: 模块的排序<br/>
     * modelIsmenu: 模块是否是菜单<br/>
     * }</dd><br/>
     */
    @ApiOperation(value = "保存模块表实体")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelTitle", value = "模块的标题", required = true,paramType="query"),
            @ApiImplicitParam(name = "modelCode", value = "模块编码", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelId", value = "模块父id", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelUrl", value = "链接地址", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelDatetime", value = "发布时间", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelIcon", value = "模块图标", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelSort", value = "模块排序", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelIsMenu", value = "是否是菜单", required = false,paramType="query"),
            @ApiImplicitParam(name = "isChild", value = "菜单类型", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelParentIds", value = "父级编号集合", required = false,paramType="query"),
    })
    @LogAnn(title = "保存模块表实体",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("basic:model:save")
    public ResultData save(@ModelAttribute @ApiIgnore ModelEntity model, HttpServletResponse response, HttpServletRequest request) {
        //模块标题验证
        if(StringUtils.isBlank(model.getModelTitle())){
            return ResultData.build().error(getResString("err.empty", this.getResString("model.title")));
        }
        if(!StringUtil.checkLength(model.getModelTitle()+"", 1, 10)){
            return ResultData.build().error(getResString("err.length", this.getResString("model.title"), "1", "10"));
        }
        //判断菜单名称不能相同
        if(model.getModelIsMenu() == ModelIsMenuEnum.MODEL_MEUN.toInt()){
            LambdaQueryWrapper<ModelEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ModelEntity::getModelTitle,model.getModelTitle()).eq(ModelEntity::getModelIsMenu,ModelIsMenuEnum.MODEL_MEUN.toInt());
            List<ModelEntity> list = modelBiz.list(wrapper);
            if(CollectionUtil.isNotEmpty(list)){
                return ResultData.build().error(getResString("err.exist",this.getResString("model.title")));
            }
        }
        // 判断菜单url不能为空且不能相同
        if (model.getModelIsMenu()==ModelIsMenuEnum.MODEL_NOTMENU.toInt()){//权限标识的情况下
            //对菜单权限标识进行去空格处理
            model.setModelUrl(model.getModelUrl().trim());
            if (StringUtils.isBlank(model.getModelUrl()))
            return ResultData.build().error(getResString("err.empty",this.getResString("model.url")));
            List<ModelEntity> modelList = modelBiz.list(new LambdaQueryWrapper<ModelEntity>().eq(ModelEntity::getModelUrl, model.getModelUrl()));
            if (CollectionUtil.isNotEmpty(modelList)){
                return ResultData.build().error(getResString("err.exist",this.getResString("model.url")));
            }
        }

        // 获取模块保存时间
        model.setModelDatetime(new Timestamp(System.currentTimeMillis()));
        //判断图标是否为空，不为空去掉,图标地址中含有的“|”
        //空值判断
        if(!StringUtils.isBlank(model.getModelIcon())) {
            model.setModelIcon( model.getModelIcon().replace("|", ""));
        }
        //重复判断，modelCode不能重复
        if(StringUtils.isNotBlank(model.getModelCode())){
            ModelEntity _model = modelBiz.getEntityByModelCode(model.getModelCode());
            if (_model != null){
                return ResultData.build().error(getResString("err.exist",this.getResString("modelCode")));
            }
        }
        if(model.getModelSort() == null){
            model.setModelSort(0);
        }

        // 防止最顶级栏目为空时报NP异常
        if (model.getModelId() != null){
            // 获取到父级model实体
            ModelEntity modelEntity = modelBiz.getById(model.getModelId());
            // 如果父级getModelParentIds为空则必然为顶级
            if (StringUtils.isBlank(modelEntity.getModelParentIds())) {
                model.setModelParentIds(model.getModelId().toString());
            }else {
                model.setModelParentIds(modelEntity.getModelParentIds()+","+model.getModelId().toString());
            }
        }

        modelBiz.save(model);
        //保存成功后给当前管理就就加上对应的权限
        if(StringUtils.isNotEmpty(model.getId())){
            ManagerEntity manager = BasicUtil.getManager();
            assert manager != null;
            List<RoleModelEntity> roleModels = new ArrayList<>();
            for (String roleId : manager.getRoleIds().split(",")) {
                RoleModelEntity roleModel = new RoleModelEntity();
                roleModel.setModelId(Integer.parseInt(model.getId()));
                roleModel.setRoleId(Integer.parseInt(roleId));
                roleModels.add(roleModel);
            }

            roleModelBiz.saveBatch(roleModels, roleModels.size());
        }
        modelBiz.updateCache();
        //返回模块id到页面
        return ResultData.build().success(model.getId());
    }


    @ApiOperation(value = "批量删除模块表")
    @ApiImplicitParam(name = "ids", value = "模块编号，多个以逗号隔开", required = false,paramType="query")
    @LogAnn(title = "批量删除模块表",businessType= BusinessTypeEnum.DELETE)
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("basic:model:del")
    public ResultData delete(HttpServletResponse response, HttpServletRequest request) {
        int[] ids = BasicUtil.getInts("ids", ",");
        modelBiz.delete(ids);
        return ResultData.build().success();
    }

    /**
     * 更新模块表信息模块表
     * @param model 模块表实体
     * <i>model参数包含字段信息参考：</i><br/>
     * id 模块自增长id<br/>
     * modelTitle 模块标题<br/>
     * modelCode 模块编码<br/>
     * modelId 模块的父模块id<br/>
     * modelUrl 模块连接地址<br/>
     * modelDatetime <br/>
     * modelIcon 模块图标<br/>
     * modelSort 模块的排序<br/>
     * modelIsmenu 模块是否是菜单<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>{ <br/>
     * id: 模块自增长id<br/>
     * modelTitle: 模块标题<br/>
     * modelCode: 模块编码<br/>
     * modelId: 模块的父模块id<br/>
     * modelUrl: 模块连接地址<br/>
     * modelDatetime: <br/>
     * modelIcon: 模块图标<br/>
     * modelSort: 模块的排序<br/>
     * modelIsmenu: 模块是否是菜单<br/>
     * }</dd><br/>
     */
    @ApiOperation(value = "更新模块表信息模块表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模块的编号", required = true,paramType="query"),
            @ApiImplicitParam(name = "modelTitle", value = "模块的标题", required = true,paramType="query"),
            @ApiImplicitParam(name = "modelCode", value = "模块编码", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelId", value = "模块父id", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelUrl", value = "链接地址", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelDatetime", value = "发布时间", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelIcon", value = "模块图标", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelSort", value = "模块排序", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelIsMenu", value = "是否是菜单", required = false,paramType="query"),
            @ApiImplicitParam(name = "isChild", value = "菜单类型", required = false,paramType="query"),
            @ApiImplicitParam(name = "modelParentIds", value = "父级编号集合", required = false,paramType="query"),
    })
    @LogAnn(title = "更新模块表信息模块表",businessType= BusinessTypeEnum.UPDATE)
    @PostMapping("/update")
    @RequiresPermissions("basic:model:update")
    @ResponseBody
    public ResultData update(@ModelAttribute @ApiIgnore ModelEntity model, HttpServletResponse response,
                             HttpServletRequest request) {
        //模块标题验证
        if(StringUtil.isBlank(model.getModelTitle())){
            return ResultData.build().error(getResString("err.empty", this.getResString("model.title")));
        }
        if(!StringUtil.checkLength(model.getModelTitle()+"", 1, 10)){
            return ResultData.build().error(getResString("err.length", this.getResString("model.title"), "1", "10"));
        }
        //判断菜单名称不能相同
        if(model.getModelIsMenu() == ModelIsMenuEnum.MODEL_MEUN.toInt()){
            LambdaQueryWrapper<ModelEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ModelEntity::getModelTitle,model.getModelTitle()).eq(ModelEntity::getModelIsMenu,ModelIsMenuEnum.MODEL_MEUN.toInt());
            List<ModelEntity> list = modelBiz.list(wrapper);
            // 查出数据不为空，且集合元素大于一个或者有一个但不是自己
            if(CollectionUtil.isNotEmpty(list) && (list.size()>1 || !list.get(0).getId().equals(model.getId()))){
                return ResultData.build().error(getResString("err.exist",this.getResString("model.title")));
            }
        }
        //判断当前修改的菜单是否是三级菜单
        ModelEntity _model = (ModelEntity) modelBiz.getEntity(Integer.parseInt(model.getId()));
        if(_model.getModelIsMenu() == 1 && model.getModelIsMenu() == 0){
            return ResultData.build().error(this.getResString("model.is.menu"));
        }
        // 判断菜单url不能为空且不能相同
        if (model.getModelIsMenu()==ModelIsMenuEnum.MODEL_NOTMENU.toInt()){
            //对菜单权限标识进行去空格处理
            model.setModelUrl(model.getModelUrl().trim());
            if (StringUtils.isBlank(model.getModelUrl()))
                return ResultData.build().error(getResString("err.empty",this.getResString("model.url")));
            List<ModelEntity> modelList = modelBiz.list(new LambdaQueryWrapper<ModelEntity>().eq(ModelEntity::getModelUrl, model.getModelUrl()));
            if (CollectionUtil.isNotEmpty(modelList) && !modelList.get(0).getId().equals(model.getId())){
                return ResultData.build().error(getResString("err.exist",this.getResString("model.url")));
            }
        }
        //判断图标是否为空，不为空去掉,图标地址中含有的“|”
        //空值判断
        if(!StringUtil.isBlank(model.getModelIcon())) {
            model.setModelIcon( model.getModelIcon().replace("|", ""));
        }
        modelBiz.updateEntity(model);
        return ResultData.build().success(model.getId());
    }

    /**
     * 根据管理员ID查询模块集合
     * @param managerId 管理员id
     * @param request 请求对象
     * @param response 响应对象
     */
    @ApiOperation(value = "根据管理员ID查询模块集合")
    @ApiImplicitParam(name = "managerId", value = "管理员id", required = true,paramType="path")
    @GetMapping("/{managerId}/queryModelByRoleId")
    @ResponseBody
    public ResultData queryModelByRoleId(@PathVariable @ApiIgnore int managerId,HttpServletRequest request, HttpServletResponse response) {
        ManagerEntity manager =(ManagerEntity) managerBiz.getEntity(managerId);
        if(manager==null){
            return ResultData.build().error();
        }
        HashSet<ModelEntity> modelSet = new HashSet<>();
        for (String roleId : manager.getRoleIds().split(",")) {
            modelSet.addAll(modelBiz.queryModelByRoleId(Integer.parseInt(roleId)));
        }
        List<ModelEntity> modelList = new ArrayList<>(modelSet);
        return ResultData.build().success(modelList);
    }

    /**
     * 查询模块表列表
     * @param model 模块表实体
     * <i>model参数包含字段信息参考：</i><br/>
     * id 模块自增长id<br/>
     * modelTitle 模块标题<br/>
     * modelCode 模块编码<br/>
     * modelId 模块的父模块id<br/>
     * modelUrl 模块连接地址<br/>
     * modelDatetime <br/>
     * modelIcon 模块图标<br/>
     * modelSort 模块的排序<br/>
     * modelIsmenu 模块是否是菜单<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>[<br/>
     * { <br/>
     * id: 模块自增长id<br/>
     * modelTitle: 模块标题<br/>
     * modelCode: 模块编码<br/>
     * modelId: 模块的父模块id<br/>
     * modelUrl: 模块连接地址<br/>
     * modelDatetime: <br/>
     * modelIcon: 模块图标<br/>
     * modelSort: 模块的排序<br/>
     * modelIsmenu: 模块是否是菜单<br/>
     * }<br/>
     * ]</dd><br/>
     */
    @ApiOperation(value = "查询模块表列表")
    @ApiImplicitParam(name = "roleId", value = "角色编号", required = true,paramType="query")
    @GetMapping("/modelList")
    @ResponseBody
    public ResultData modelList(@ModelAttribute @ApiIgnore ModelEntity modelEntity,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
        int roleId = BasicUtil.getInt("roleId");
        ManagerEntity managerSession = BasicUtil.getManager();
        boolean updateFlag = roleId != 0;
        //新增角色roleId为0，默认当前管理员的roleId
        List<ModelEntity> modelList = modelStrategy.list();

        List<ModelEntity> _modelList = new ArrayList<>();
        List<RoleModelEntity> roleModelList = new ArrayList<>();
        if(roleId>0){
            roleModelList = roleModelBiz.queryByRoleId(roleId);
        }else {
            HashSet<RoleModelEntity> roleSet = new HashSet<>();
            for (String id : managerSession.getRoleIds().split(",")) {
                roleSet.addAll(roleModelBiz.queryByRoleId(Integer.parseInt(id)));
            }
            roleModelList.addAll(roleSet);
        }
        List<ModelEntity> childModelList = new ArrayList<>();
        //将菜单和功能区分开
        for(BaseEntity base : modelList){
            ModelEntity _model = (ModelEntity) base;
            if(_model.getModelIsMenu() == 1){
                _model.setModelChildList(new ArrayList<ModelEntity>());
                _modelList.add(_model);
            }else if(_model.getModelIsMenu() == 0){
                childModelList.add(_model);
            }
        }
        //菜单和功能一一匹配
        for(ModelEntity _modelEntity : _modelList){
            for(ModelEntity childModel : childModelList){
                if(childModel.getModelId() == Integer.parseInt(_modelEntity.getId())){
                    _modelEntity.getModelChildList().add(childModel);
                    //选中状态
                    for(RoleModelEntity roleModelEntity : roleModelList){
                        if(roleModelEntity.getModelId() == Integer.parseInt(childModel.getId()) && updateFlag){
                            childModel.setChick(1);
                        }
                    }

                }
            }
        }
        EUListBean _list = new EUListBean(_modelList, _modelList.size());
        return ResultData.build().success(_list);
    }


    /**
     * 递归遍历菜单实体集合，将菜单标题及非菜单的权限标识分别添加至modelTitleList及modelUrlList
     * 递归的执行条件为 当前遍历到的实体子菜单集合不为空
     * @param modelEntityList 菜单实体集合 不允许为空
     * @param modelUrlList 菜单权限标识集合 不允许为空
     * @param modelTitleList 菜单标题集合 不允许为空
     * @throws BusinessException 实体标题长度不合格，或者实体不是菜单且实体的权限标识为空
     */
    private void addModelUrlAndTitleForList(List<ModelEntity> modelEntityList,List<String> modelUrlList,List<String> modelTitleList){
        // 空判断,集合中没有元素则直接返回
        if (CollectionUtil.isEmpty(modelEntityList) || modelUrlList == null || modelTitleList == null){
            return;
        }
        // 遍历菜单集合
        for (ModelEntity model : modelEntityList) {
            // 不合规的标题直接抛出异常
            if (!StringUtil.checkLength(model.getModelTitle()+"", 1, 20)){
                throw new BusinessException(getResString("err.length", this.getResString("model.title"), "1", "20"));
            }
            // 当实体不为菜单且权限标识不为空，则向modelUrlList添加一条记录;实体为菜单则向modelTitleList添加一条记录
            if (model.getModelIsMenu()==ModelIsMenuEnum.MODEL_NOTMENU.toInt()){
                if (StringUtils.isBlank(model.getModelUrl())){
                    throw new BusinessException(getResString("err.empty", this.getResString("model.url")));
                }
                //对菜单权限标识进行去空格处理
                model.setModelUrl(model.getModelUrl().trim());
                modelUrlList.add(model.getModelUrl());
            }else {
                modelTitleList.add(model.getModelTitle());
            }
            // 当前实体有子菜单，则递归执行
            if (CollectionUtil.isNotEmpty(model.getModelChildList())){
                this.addModelUrlAndTitleForList(model.getModelChildList(),modelUrlList,modelTitleList);
            }
        }
    }

}
