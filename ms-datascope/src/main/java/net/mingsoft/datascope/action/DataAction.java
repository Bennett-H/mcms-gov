/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.datascope.action;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.datascope.bean.DataBatchBean;
import net.mingsoft.datascope.bean.DataBean;
import net.mingsoft.datascope.biz.IDataBiz;
import net.mingsoft.datascope.biz.IDataConfigBiz;
import net.mingsoft.datascope.entity.DataConfigEntity;
import net.mingsoft.datascope.entity.DataEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限管理控制层
 *
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-数据权限接口"})
@Controller("datascopeDataAction")
@RequestMapping("/${ms.manager.path}/datascope/data")
public class DataAction extends net.mingsoft.datascope.action.BaseAction {


    /**
     * 注入数据权限业务层
     */
    @Autowired
    private IDataBiz dataBiz;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/datascope/data/index";
    }

    /**
     * 返回编辑界面data_form
     */
    @ApiIgnore
    @GetMapping("/form")
    public String form(@ModelAttribute DataEntity data, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        return "/datascope/data/form";
    }

    /**
     * 查询数据权限列表
     *
     * @param data 数据权限实体
     */
    @ApiOperation(value = "查询数据权限列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataId", value = "关联id", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataTargetId", value = "目标编号", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "业务分类", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "del", value = "删除标记", required = false, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "编号", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore DataEntity data, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
        BasicUtil.startPage();
        List<DataEntity> dataList = dataBiz.list(new QueryWrapper<>(data));
        return ResultData.build().success(new EUListBean(dataList, (int) BasicUtil.endPage(dataList).getTotal()));
    }

    /**
     * 获取数据权限
     *
     * @param data 数据权限实体
     */
    @ApiOperation(value = "获取数据权限列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required = true, paramType = "query")
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore DataEntity data, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (data.getId() == null) {
            return ResultData.build().error();
        }
        DataEntity _data = dataBiz.getById(Integer.parseInt(data.getId()));
        return ResultData.build().success(_data);
    }

    /**
     * 获取数据权限
     *
     * @param data 数据权限实体
     */
    @ApiOperation(value = "获取数据权限列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "业务类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dataId", value = "关联权限数据id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dataTargetId", value = "拥有权限方(不传默认为管理员角色ID), 如果开启了超级管理员判断,并且当前管理员是超级管理员,会直接返回super字段", required = false, paramType = "query"),
            @ApiImplicitParam(name = "isSuper", value = "用于控制超级管理员判断(默认关闭)", required = false, dataType = "boolean", paramType = "query"),
    })
    @GetMapping("/getModel")
    @ResponseBody
    public ResultData getModel(@ModelAttribute @ApiIgnore DataEntity data, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        ManagerEntity manager = BasicUtil.getManager();
        // 用于开启超级管理员判断,默认关闭
        boolean isSuper = Boolean.parseBoolean(BasicUtil.getString("isSuper", "false"));
        if(isSuper && (ManagerAdminEnum.SUPER.toString().equals(manager.getManagerAdmin()))) {
            // 如果开启了超级管理员判断,并且当前管理员是超级管理员,会直接返回super字段
            return ResultData.build().success("super");
        }
        if(StringUtils.isBlank(data.getDataId())) {
            return ResultData.build().error("没有设置dataId");
        }
        if(StringUtils.isBlank(data.getDataType())) {
            return ResultData.build().error("没有设置dataType");
        }
//         不传默认为使用管理员角色为目标
        if (StringUtils.isEmpty(data.getDataTargetId())) {
            LambdaQueryWrapper<DataEntity> wrapper = new QueryWrapper<DataEntity>().lambda();
            wrapper.eq(DataEntity::getDataId, data.getDataId());
            wrapper.eq(DataEntity::getDataType, data.getDataType());
            wrapper.in(DataEntity::getDataTargetId, Arrays.asList(manager.getRoleIds().split(",")));
            List<DataEntity> dataList = dataBiz.list(wrapper);
            if (CollUtil.isNotEmpty(dataList)) {
                return ResultData.build().success(dataBiz.mergeDataBatch(dataList));
            }
        }else {
            data = dataBiz.getOne(new QueryWrapper<>(data));
            if (StringUtils.isNotBlank(data.getId())) {
                return ResultData.build().success(data);
            }
        }
        return ResultData.build().error("没有对应权限配置");
    }




    /**
     * 保存数据权限
     * @param data 数据权限实体
     */
    @ApiOperation(value = "保存数据权限表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataId", value = "关联id", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataTargetId", value = "目标编号", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "业务分类", required = false, paramType = "query"),
    })
    @PostMapping("/save")
    @ResponseBody
    @LogAnn(title = "保存数据权限", businessType = BusinessTypeEnum.INSERT)
    @RequiresPermissions("datascope:data:save")
    public ResultData save(@ModelAttribute @ApiIgnore DataEntity data, HttpServletResponse response, HttpServletRequest request) {
        //// 查询数据权限配置sql
        //QueryWrapper<Object> wrapper = new QueryWrapper<>();
        //wrapper.eq("configId",data.getConfigId());
        //DataConfigEntity one = dataConfigBiz.getOne(wrapper, false);
        if (!StringUtil.checkLength(data.getDataId() + "", 0, 11)) {
            return ResultData.build().error(getResString("err.length", this.getResString("data.id"), "0", "11"));
        }
        DataEntity dataEntity = new DataEntity();
        dataEntity.setDataId(data.getDataId());
        dataEntity.setDataTargetId(data.getDataTargetId());
        dataEntity.setDataType(data.getDataType());
        if (dataBiz.getOne(new QueryWrapper<>(dataEntity)) != null) {
            return ResultData.build().error(getResString("err.exist", this.getResString("data.manager.id")));

        }
        dataBiz.saveOrUpdate(data);
        return ResultData.build().success(data);
    }

    /**
     * @param datas 数据权限实体
     */
    @ApiOperation(value = "批量删除数据权限列表接口")
    @PostMapping("/delete")
    @ResponseBody
    @LogAnn(title = "删除数据权限", businessType = BusinessTypeEnum.DELETE)
    @RequiresPermissions("datascope:data:del")
    public ResultData delete(@RequestBody List<DataEntity> datas, HttpServletResponse response, HttpServletRequest request) {
        List<String> ids = datas.stream().map(p -> p.getId()).collect(Collectors.toList());
        dataBiz.removeByIds(ids);
        return ResultData.build().success();
    }

    /**
     * 更新数据权限列表
     *
     * @param data 数据权限实体
     */
    @ApiOperation(value = "更新数据权限列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dataId", value = "关联id", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataTargetId", value = "目标编号", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "业务分类", required = false, paramType = "query"),
    })
    @PostMapping("/update")
    @ResponseBody
    @LogAnn(title = "更新数据权限", businessType = BusinessTypeEnum.UPDATE)
    @RequiresPermissions("datascope:data:update")
    public ResultData update(@ModelAttribute @ApiIgnore DataEntity data, HttpServletResponse response,
                             HttpServletRequest request) {
        if (!StringUtil.checkLength(data.getDataId() + "", 0, 11)) {
            return ResultData.build().error(getResString("err.length", this.getResString("data.id"), "0", "11"));
        }
        dataBiz.saveOrUpdate(data);
        return ResultData.build().success(data);
    }

    @PostMapping("/saveBatchDataScope")
    @ResponseBody
    @LogAnn(title = "保存数据权限", businessType = BusinessTypeEnum.INSERT)
    @RequiresPermissions("datascope:data:save")
    @ApiOperation(value = "批量保存数据权限接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataTargetId", value = "角色或用户组id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "权限类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dataIdList", value = "关联id集合", required = true, paramType = "query")
    })
    public ResultData saveBatchDataScope(@ModelAttribute @ApiIgnore DataBean dataBean,
                                         HttpServletResponse response, HttpServletRequest request) {
        if (StringUtils.isEmpty(dataBean.getDataTargetId())) {
            return ResultData.build().error(getResString("err.error", this.getResString("data.target.id")));
        }
        if (StringUtils.isEmpty(dataBean.getDataType())) {
            return ResultData.build().error(getResString("err.error", this.getResString("data.type")));
        }
        if (CollUtil.isEmpty(dataBean.getDataIdList())) {
            return ResultData.build().error(getResString("err.error", this.getResString("data.data.ids")));
        }
        String managerId = BasicUtil.getManager().getId();
        if (StringUtils.isNotEmpty(managerId)) {
            dataBiz.saveBatchByDataTargetIdAndDataType(dataBean.getDataType(), dataBean.getDataTargetId(),
                    managerId, dataBean.getDataIdList());
        }
        return ResultData.build().success();
    }

    /**
     * 批量保存数据权限
     *
     * @param dates 数据权限实体
     */
    @ApiOperation(value = "批量保存数据权限接口")
    @PostMapping("/saveBatch")
    @ResponseBody
    @LogAnn(title = "保存数据权限", businessType = BusinessTypeEnum.INSERT)
    @RequiresPermissions("datascope:data:save")
    public ResultData saveBatch(@RequestBody List<DataBatchBean> dates, HttpServletResponse response, HttpServletRequest request) {
        dataBiz.saveDataBatch(dates);
        return ResultData.build().success();
    }

    /**
     * 根据角色或用户组id获取已勾选的角色栏目权限id集合
     *
     * @param dataEntity 角色栏目权限实体
     * @param response
     * @param request
     * @return
     */
    @ApiOperation(value = "获取已勾选的数据权限接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataTargetId", value = "角色或用户组id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "权限类型", required = true, paramType = "query")
    })
    @GetMapping("/getByDataTargetId")
    @ResponseBody
    public ResultData getByDataTargetId(@ModelAttribute @ApiIgnore DataEntity dataEntity, HttpServletResponse response, HttpServletRequest request) {
        if (dataEntity.getDataTargetId() == null || StringUtils.isEmpty(dataEntity.getDataType())) {
            return ResultData.build().error();
        }
        List<String> dataIdList = dataBiz.queryProjectList(dataEntity.getDataTargetId(), dataEntity.getDataType());
        return ResultData.build().success(dataIdList);
    }


}
