/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.strategy.IModelStrategy;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.PinYinUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.biz.IDictBiz;
import net.mingsoft.mdiy.entity.DictEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典表管理控制层,对每个单独的字典类型进行管理
 */
@Api(tags={"后端-自定义模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/mdiy/dict/data")
public class DictDataAction extends BaseAction {

    /**
     * 注入字典表业务层
     */
    @Autowired
    private IDictBiz dictBiz;


    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/mdiy/dict/data/index";
    }

	/**
	 * 返回编辑界面dict_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	public String form(@ModelAttribute DictEntity dict, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {

		return "/mdiy/dict/data/form";
	}

    /**
     * 查询字典表列表
     */
    @ApiOperation(value = "查询字典表列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictType", value = "类型", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dictLabel", value = "标签名", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dictValue", value = "数据值", required = false, paramType = "query"),
            @ApiImplicitParam(name = "isChild", value = "子业务关联", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore DictEntity dict, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (StringUtils.isBlank(dict.getDictType())){
            return ResultData.build().error("字典类型不能为空");
        }
        if(!getPermissions("mdiy:dict:view", "mdiy:dictData:" +dict.getDictType() + ":settings")){
            return ResultData.build().error("没有权限!");
        }
        BasicUtil.startPage();
        List dictList = dictBiz.query(dict);
        return ResultData.build().success(new EUListBean(dictList, (int) BasicUtil.endPage(dictList).getTotal()));
    }


    /**
     * 获取字典表
     */
    @ApiOperation(value = "获取字典详情接口")
    @ApiImplicitParam(name = "id", value = "字典编号", required = true, paramType = "query")
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore DictEntity dict, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (StringUtils.isBlank(dict.getId())) {
            return ResultData.build().error(getResString("err.error", this.getResString("dict.id")));
        }
        DictEntity _dict = dictBiz.getById(dict.getId());
        if (StringUtils.isBlank(_dict.getDictType())){
            return ResultData.build().error("字典类型不能为空");
        }
        if(!getPermissions("mdiy:dict:view", "mdiy:dictData:" +_dict.getDictType() + ":settings")){
            return ResultData.build().error("没有权限!");
        }
        return ResultData.build().success(_dict);
    }

    /**
     * 保存字典表实体
     *
     */
    @ApiOperation(value = "保存字典接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictLabel", value = "标签名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dictType", value = "类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dictValue", value = "数据值", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dictDescription", value = "描述", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dictSort", value = "排序（升序）", required = false, paramType = "query"),
            @ApiImplicitParam(name = "isChild", value = "子业务关联", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dictRemarks", value = "备注信息", required = false, paramType = "query")
    })
    @LogAnn(title = "保存字典接口", businessType = BusinessTypeEnum.INSERT)
    @PostMapping("/save")
    @ResponseBody
    public ResultData save(@ModelAttribute @ApiIgnore DictEntity dict, HttpServletResponse response, HttpServletRequest request) {
        if(!getPermissions("mdiy:dict:save", "mdiy:dictData:" +dict.getDictType() + ":settings")){
            return ResultData.build().error("没有权限!");
        }
        // type和lable不能为重复
        if (dictBiz.getByTypeAndLabelAndValue(dict.getDictType(), dict.getDictLabel(), null) != null) {
            return ResultData.build().error(getResString("diy.dict.type.and.label.repeat"));
        }
        // type和value不能为重复
        if (dictBiz.getByTypeAndLabelAndValue(dict.getDictType(), null, dict.getDictValue()) != null) {
            return ResultData.build().error(getResString("diy.dict.type.and.value.repeat"));
        }
        dictBiz.saveEntity(dict);
        if (StringUtil.isBlank(dict.getDictValue())) {
            dict.setDictValue(dict.getId());
            dictBiz.updateEntity(dict);
        }
        return ResultData.build().success();
    }

    /**
     * @param dicts 字典表实体
     *              <i>dict参数包含字段信息参考：</i><br/>
     *              id:id,id=1,2,3,4
     *              批量删除字典表
     *              <dt><span class="strong">返回</span></dt><br/>
     *              <dd>{code:"错误编码",<br/>
     *              result:"true｜false",<br/>
     *              resultMsg:"错误信息"<br/>
     *              }</dd>
     */
    @ApiOperation(value = "批量删除字典")
    @LogAnn(title = "批量删除字典", businessType = BusinessTypeEnum.DELETE)
    @PostMapping("/delete")
    @ResponseBody
    public ResultData delete(@RequestBody List<DictEntity> dicts, HttpServletResponse response, HttpServletRequest request) {
        int[] ids = new int[dicts.size()];
        for (int i = 0; i < dicts.size(); i++) {
            ids[i] = Integer.parseInt(dicts.get(i).getId());
        }
        if(!getPermissions("mdiy:dict:del", "mdiy:dictData:" + dicts.get(0).getDictType() + ":settings")){
            return ResultData.build().error("没有权限!");
        }
        dictBiz.delete(ids);
        return ResultData.build().success();
    }

    /**
     * 更新字典表信息字典表
     *
     */
    @ApiOperation(value = "更新字典信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字典编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dictLabel", value = "标签名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dictType", value = "类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dictValue", value = "数据值", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dictDescription", value = "描述", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dictSort", value = "排序（升序）", required = false, paramType = "query"),
            @ApiImplicitParam(name = "isChild", value = "子业务关联", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dictRemarks", value = "备注信息", required = false, paramType = "query")
    })
    @LogAnn(title = "更新字典信息接口", businessType = BusinessTypeEnum.UPDATE)
    @PostMapping("/update")
    @ResponseBody
    public ResultData update(@ModelAttribute @ApiIgnore DictEntity dict, HttpServletResponse response,
                             HttpServletRequest request) {

        if(!getPermissions("mdiy:dict:update", "mdiy:dictData:" +dict.getDictType() + ":settings")){
            return ResultData.build().error("没有权限!");
        }
        DictEntity _dict = dictBiz.getByTypeAndLabelAndValue(dict.getDictType(), dict.getDictLabel(), null);
        // type和lable不能为重复
        if (_dict != null) {
            if (!_dict.getId().equals(dict.getId())) {
                return ResultData.build().error(getResString("diy.dict.type.and.label.repeat"));
            }
        }
        // type和value不能为重复
        DictEntity _dict2 = dictBiz.getByTypeAndLabelAndValue(dict.getDictType(), null, dict.getDictValue());
        if (_dict2 != null) {
            if (!_dict2.getId().equals(dict.getId())) {
                return ResultData.build().error(getResString("diy.dict.type.and.value.repeat"));
            }
        }

        if (StringUtils.isBlank(dict.getDictValue())) {
            dict.setDictValue(null);
        }
        dictBiz.updateEntity(dict);
        return ResultData.build().success();
    }

}
