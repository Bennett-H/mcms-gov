/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mdiy.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.base.util.SqlInjectionUtil;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.bean.ModelJsonBean;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.constant.e.ModelCustomTypeEnum;
import net.mingsoft.mdiy.entity.ModelEntity;
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
import java.sql.Clob;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 通用模型
 */
@Api(tags={"后端-自定义模块接口"})
@Controller("coMdiyModelAction")
@RequestMapping("/${ms.manager.path}/mdiy/model")
public class ModelAction extends BaseAction {

    /**
     * 注入自定义模型业务层
     */
    @Autowired
    private IModelBiz modelBiz;

    /**
     * 寻找文件正则
     */
    private Pattern filePattern = Pattern.compile("(src|href)=\"(upload/.*?(png|jpg|gif))");
    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    public String index(HttpServletResponse response,HttpServletRequest request){
        return "/mdiy/model/index";
    }

    /**
     * 返回编辑界面model_form
     */
    @ApiIgnore
    @GetMapping("/form")
    public String form(@ModelAttribute ModelEntity modelEntity,HttpServletResponse response,HttpServletRequest request,ModelMap modelMap){
        if(modelEntity.getId()!=null){
            BaseEntity _modelEntity = modelBiz.getById(modelEntity.getId());
            modelMap.addAttribute("modelEntity",_modelEntity);
        }
        return "/mdiy/model/form";
    }

    /**
     * 查询自定义模型列表
     * @param model 自定义模型实体
     * <i>model参数包含字段信息参考：</i><br/>
     * modelName 模型名称<br/>
     * modelTableName 模型表名<br/>
     * appId 应用编号<br/>
     * modelJson json<br/>
     * id 编号<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>[<br/>
     * { <br/>
     * modelName: 模型名称<br/>
     * modelTableName: 模型表名<br/>
     * appId: 应用编号<br/>
     * modelJson: json<br/>
     * id: 编号<br/>
     * }<br/>
     * ]</dd><br/>
     */
    @ApiOperation(value = "查询自定义模型列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "模型名称", required =false,paramType="query"),
            @ApiImplicitParam(name = "modelTableName", value = "模型表名", required =false,paramType="query"),
            @ApiImplicitParam(name = "appId", value = "应用编号", required =false,paramType="query"),
            @ApiImplicitParam(name = "modelJson", value = "json", required =false,paramType="query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
            @ApiImplicitParam(name = "del", value = "删除标记", required =false,paramType="query"),
            @ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
    })
    @GetMapping("/list")
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore ModelEntity modelEntity, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
        modelEntity.setModelCustomType(ModelCustomTypeEnum.MODEL.getLabel());
        BasicUtil.startPage();
        List modelList = modelBiz.query(modelEntity);
        return ResultData.build().success(new EUListBean(modelList,(int)BasicUtil.endPage(modelList).getTotal()));
    }

    /**
     * 返回自定义模型数据
     */
    @ApiOperation(value = "查询自定义模型数据接口")
    @GetMapping("/data")
    @ResponseBody
    public ResultData data(String modelId,String linkId,HttpServletResponse response,HttpServletRequest request,ModelMap modelMap){
        // 检查SQL注入
        SqlInjectionUtil.filterContent(linkId);
        ModelEntity model = modelBiz.getById(modelId);
        Map data=null;
        if (StringUtils.isBlank(linkId)) {
            linkId = "0";
        }
        if(ObjectUtil.isNotNull(model)){
            List<Map> listMap = (List<Map>)modelBiz.excuteSql(StrUtil.format("select * from {} where link_id = '{}'",model.getModelTableName(),linkId));
            if(listMap.size()>0){
                data=new HashMap();
                for (Object o : listMap.get(0).keySet()) {
                    Object _o = listMap.get(0).get(o);
                    if (_o instanceof Clob) {
                        _o = StringUtil.clobStr((Clob) _o);
                    }
                    data.put(getCamelCaseString(o.toString(),false), _o);
                }
            }
        }
        return ResultData.build().success(data);
    }

    private  String getCamelCaseString(String inputString, boolean firstCharacterUppercase) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            switch (c) {
                case '_':
                case '-':
                case '@':
                case '$':
                case '#':
                case ' ':
                case '/':
                case '&':
                    if (sb.length() > 0) {
                        nextUpperCase = true;
                    }
                    break;

                default:
                    if (nextUpperCase) {
                        sb.append(Character.toUpperCase(c));
                        nextUpperCase = false;
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                    break;
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        return sb.toString();
    }

    @ApiOperation(value = "保存模型接口")
    @LogAnn(title = "保存模型",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("/data/save")
    @ResponseBody
    public ResultData save(String linkId,String modelId,HttpServletResponse response,HttpServletRequest request,ModelMap modelMap){
        // 检查SQL注入
        SqlInjectionUtil.filterContent(linkId);
        ModelEntity model = modelBiz.getById(modelId);
        if(ObjectUtil.isNotNull(model)&&StringUtils.isNotBlank(linkId)){
            List<Map> listMap = (List<Map>)modelBiz.excuteSql(StrUtil.format("select * from {} where link_id = '{}'",model.getModelTableName(),linkId));
            if (CollUtil.isNotEmpty(listMap)) {
                // 如果当前link_id存在数据
                return ResultData.build().error(linkId);
            }
            Map<String, Object> requestMap = BasicUtil.assemblyRequestMap();
            Map fieldMap= model.getFieldMap();
            HashMap<String, Object> fields = new HashMap<>();
            //拼接字段
            for (String s : requestMap.keySet()) {
                //判断是否存在此字段
                if(fieldMap.containsKey(s)){
                    fields.put(fieldMap.get(s).toString(),requestMap.get(s));
                }
            }
            fields.put("CREATE_DATE",new Date());
            fields.put("UPDATE_DATE",new Date());
            fields.put("CREATE_BY",BasicUtil.getManager().getId());
            fields.put("LINK_ID",linkId);
            modelBiz.insertBySQL(model.getModelTableName(),fields);
            return ResultData.build().success(linkId);
        }
        return ResultData.build().error(linkId);
    }

    @ApiOperation(value = "更新模型接口")
    @LogAnn(title = "更新模型",businessType= BusinessTypeEnum.UPDATE)
    @PostMapping("/data/update")
    @ResponseBody
    public ResultData update(String linkId, String modelId,HttpServletResponse response,HttpServletRequest request,ModelMap modelMap){
        // 检查SQL注入
        SqlInjectionUtil.filterContent(linkId);
        ModelEntity model = modelBiz.getById(modelId);
        //效验参数
        if(ObjectUtil.isNotNull(model)&&StringUtils.isNotBlank(linkId)){
            Map<String, Object> requestMap = BasicUtil.assemblyRequestMap();
            Map fieldMap= model.getFieldMap();
            HashMap<String, Object> fields = new HashMap<>();
            //拼接字段
            for (String s : requestMap.keySet()) {
                //判断是否存在此字段
                if(fieldMap.containsKey(s)){
                    fields.put(fieldMap.get(s).toString(),requestMap.get(s));
                }
            }
            Map where = new HashMap();
            where.put("LINK_ID",linkId);

            List<Map> listMap = (List<Map>)modelBiz.excuteSql(StrUtil.format("select * from {} where link_id = '{}'",model.getModelTableName(),linkId));

            //如果没有数据，又可能是后面使用了自定义模型
            if(CollUtil.isEmpty(listMap)) {
                fields.put("LINK_ID",linkId);
                fields.put("CREATE_DATE",new Date());
                fields.put("UPDATE_DATE",new Date());
                fields.put("CREATE_BY",BasicUtil.getManager().getId());
                // 这里有个疑问,如果不需要,需要删除
                // fields.put("link_id",linkId);
                modelBiz.insertBySQL(model.getModelTableName(),fields);
            } else {
                //更新
                fields.put("UPDATE_DATE",new Date());
                fields.put("UPDATE_BY",BasicUtil.getManager().getId());
                modelBiz.updateBySQL(model.getModelTableName(),fields,where);
            }


            return ResultData.build().success(linkId);
        }
        return ResultData.build().error(linkId);
    }


    /**
     * 通用渲染表单
     * @param response
     * @param request
     * @return
     */
    @ApiOperation(value = "查询自定义模型接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "模型名称", required =false,paramType="query"),
            @ApiImplicitParam(name = "modelTableName", value = "模型表名", required =false,paramType="query"),
            @ApiImplicitParam(name = "appId", value = "应用编号", required =false,paramType="query"),
            @ApiImplicitParam(name = "modelJson", value = "json", required =false,paramType="query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(@ApiIgnore ModelEntity modelEntity, HttpServletResponse response, HttpServletRequest request){
        if(modelEntity==null && StringUtils.isEmpty(modelEntity.getModelName())){
            return ResultData.build().error(this.getResString("err.error",this.getResString("model.name")));
        }
        modelEntity.setModelCustomType(ModelCustomTypeEnum.MODEL.getLabel());
        ModelEntity model = modelBiz.getOne(new QueryWrapper<>(modelEntity));
        return ResultData.build().success(model);
    }


    @ApiOperation(value = "导入自定义模型接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelJson", value = "json", required =true,paramType="query"),
    })
    @LogAnn(title = "导入自定义模型",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("/importJson")
    @ResponseBody
    @RequiresPermissions("mdiy:model:importJson")
    public ResultData importJson(@ModelAttribute @ApiIgnore ModelEntity modelEntity, HttpServletResponse response, HttpServletRequest request,BindingResult result) {


        //验证json的值是否合法
        if(StringUtils.isBlank(modelEntity.getModelJson())){
            return ResultData.build().error(getResString("err.empty", this.getResString("model.json")));
        }

        modelEntity.setModelCustomType(ModelCustomTypeEnum.MODEL.getLabel());

        ModelJsonBean modelJsonBean = new ModelJsonBean();
        try{
            modelJsonBean = JSONUtil.toBean(modelEntity.getModelJson(), ModelJsonBean.class);
        }catch (Exception e){
            return ResultData.build().error(getResString("err.error", this.getResString("model.json")));
        }

        // 保存导入的json模型
        if(modelBiz.importModel(ModelCustomTypeEnum.MODEL.getLabel(), modelJsonBean,modelEntity.getModelType())){
            return ResultData.build().success();
        }else {
            return ResultData.build().error(getResString("err.exist", this.getResString("table.name")));
        }

    }

    @ApiOperation(value = "更新导入自定义模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelJson", value = "json", required =true,paramType="query"),
    })
    @LogAnn(title = "更新自定义模型",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("/updateJson")
    @ResponseBody
    @RequiresPermissions("mdiy:model:update")
    public ResultData updateJson(@ModelAttribute @ApiIgnore ModelEntity modelEntity, HttpServletResponse response, HttpServletRequest request, BindingResult result) {
        //验证json的值是否合法
        if(StringUtils.isBlank(modelEntity.getModelJson())){
            return ResultData.build().error(getResString("err.empty", this.getResString("model.json")));
        }
        if(StringUtils.isBlank(modelEntity.getId())){
            return ResultData.build().error(getResString("err.empty", this.getResString("id")));
        }
        ModelJsonBean modelJsonBean = null;
        try{
            modelJsonBean = JSONUtil.toBean(modelEntity.getModelJson(), ModelJsonBean.class);
        }catch (Exception e){
            return ResultData.build().error(getResString("err.error", this.getResString("model.json")));
        }
        // 保存导入的json模型
        if(modelBiz.updateConfig(modelEntity.getId(), modelJsonBean,modelEntity.getModelType())){
            return ResultData.build().success();
        }else {
            return ResultData.build().error(getResString("err.exist", this.getResString("table.name")));
        }
    }


    /**
     * @param models 自定义模型实体
     * <i>model参数包含字段信息参考：</i><br/>
     * id:多个id直接用逗号隔开,例如id=1,2,3,4
     * 批量删除自定义模型
     *            <dt><span class="strong">返回</span></dt><br/>
     *            <dd>{code:"错误编码",<br/>
     *            result:"true｜false",<br/>
     *            resultMsg:"错误信息"<br/>
     *            }</dd>
     */
    @ApiOperation(value = "批量删除自定义模型列表接口")
    @LogAnn(title = "批量删除自定义模型",businessType= BusinessTypeEnum.DELETE)
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("mdiy:model:del")
    public ResultData delete(@RequestBody List<ModelEntity> models,HttpServletResponse response, HttpServletRequest request) {
        List<String> ids = models.stream().map(ModelEntity::getId).collect(Collectors.toList());
        modelBiz.delete(ids);
        return ResultData.build().success();
    }

    /**
     * 渲染自定义表单页面
     * @param url 请求地址
     * @param formUrls 模型所用到的地址,json格式，参考 mdiy/index.js 配置
     * @param model 模型名称,json根式
     * @param id 业务数据编号
     * @param request
     * @return 表单页面
     */
    @ApiOperation(value = "自定义页面输出")
    @GetMapping("/formTmpl")
    public String formTmpl(String url,String formUrls,String model,String id, HttpServletRequest request) {
        request.setAttribute("url",url);
        request.setAttribute("formUrls",formUrls);
        request.setAttribute("model",model);
        return "/mdiy/model/form-tmpl";
    }


}
