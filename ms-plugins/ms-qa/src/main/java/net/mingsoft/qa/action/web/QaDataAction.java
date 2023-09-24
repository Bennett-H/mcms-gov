/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.qa.action.web;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.IpUtils;
import net.mingsoft.qa.action.BaseAction;
import net.mingsoft.qa.bean.FieldBean;
import net.mingsoft.qa.bean.SearchBean;
import net.mingsoft.qa.biz.IQaBiz;
import net.mingsoft.qa.biz.IQaDataBiz;
import net.mingsoft.qa.constant.Const;
import net.mingsoft.qa.entity.QaEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通用问卷调查数据
 */
@Api(tags = {"前端-问卷调查模块接口"})
@Controller("webQaDataAction")
@RequestMapping("/qa/qaData")
public class QaDataAction extends BaseAction {


    @Autowired
    private IQaDataBiz qaDataBiz;


    @Autowired
    private IQaBiz qaBiz;


    @ApiOperation("保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "业务模型ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "rand_code", value = "验证码", required = true, paramType = "query"),
    })
    @LogAnn(title = "前端提交调查问卷信息", businessType = BusinessTypeEnum.INSERT)
    @PostMapping("save")
    @ResponseBody
    public ResultData save(HttpServletRequest request, HttpServletResponse response) {
        String modelId = BasicUtil.getString("modelId");
        if (StringUtils.isEmpty(modelId)) {
            return ResultData.build().error(getResString("err.empty", getResString("model.id")));
        }
        LOG.debug("保存表单");
        //验证码
        if (!checkRandCode()) {
            LOG.debug("验证码错误");
            return ResultData.build().error(getResString("err.error", this.getResString("rand.code")));
        }

        QaEntity qa = qaBiz.getById(modelId);
        if (qa != null) {
            Map<String, Object> map = BasicUtil.assemblyRequestMap();
            //设置ip,地址,网络设备等参数
            String ip = BasicUtil.getIp();
            //访问设备
            String deviceType = BasicUtil.isMobileDevice() ? "phone" : "pc";
            //根据ip查询用户地址
            String address = IpUtils.getRealAddressByIp(ip);
            map.put("qa_IP", ip);
            map.put("qa_device_type", deviceType);
            map.put("qa_address", address);
            map.put("create_date", new Date());
            map.put("update_date", new Date());

            if (qaDataBiz.saveDiyFormData(Integer.parseInt(qa.getId()), map)) {
                return ResultData.build().success();
            } else {
                return ResultData.build().error();
            }
        }

        return ResultData.build().error();

    }

    /**
     * 提供前端查询自定义表单提交数据
     *
     * @param qaName   投票名称
     * @param request
     * @param response
     */
    @ApiOperation(value = "提供前端查询自定义表单提交数据")
    @ApiImplicitParam(name = "qaName", value = "投票名称", required = true, paramType = "query")
    @GetMapping("list")
    @ResponseBody
    public ResultData list(@RequestParam("qaName") String qaName, HttpServletRequest request, HttpServletResponse response) {
        //判断传入的加密数字是否能转换成整形
        QueryWrapper<QaEntity> queryWrapper = new QueryWrapper<QaEntity>();
        queryWrapper.like("qa_name", qaName);
        QaEntity qa = qaBiz.getOne(queryWrapper);
        if (qa != null) {
            //判断是否允许外部提交
            if (!Boolean.parseBoolean(JSONUtil.toBean(qa.getModelJson(), Map.class).get("isWebSubmit").toString())) {
                ResultData.build().error();
            }
            List list = qaDataBiz.queryDiyFormData(Integer.parseInt(qa.getId()), BasicUtil.assemblyRequestMap());
            if (ObjectUtil.isNotNull(list)) {
                return ResultData.build().success(new EUListBean(list, (int) BasicUtil.endPage(list).getTotal()));
            }
            return ResultData.build().error();
        }
        return ResultData.build().error();
    }


    /**
     * 查询当前投票的统计列表信息
     *
     * @param qaName   投票名称
     * @param request
     * @param response
     */
    @ApiOperation(value = "查询当前投票的统计列表信息")
    @ApiImplicitParam(name = "qaName", value = "问卷调查名称", required = true, paramType = "query")
    @GetMapping("/statistics/info")
    @ResponseBody
    public ResultData info(@RequestParam("qaName") String qaName, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(qaName)) {
            return ResultData.build().error(getResString("err.empty", getResString("model.name")));
        }
        QaEntity qaEntity = qaBiz.getOne(new LambdaQueryWrapper<QaEntity>().eq(QaEntity::getQaName, qaName));
        if (qaEntity == null) {
            return ResultData.build().error();
        }
        List<FieldBean> fieldBeans = JSONUtil.toList(qaEntity.getModelField(), FieldBean.class)
                .stream()
                .filter(fieldBean -> Const.TYPES.contains(fieldBean.getType())).collect(Collectors.toList());
        String sql = "SELECT {} FROM " + qaEntity.getQaTableName() + " WHERE {} IS NOT NULL";
        List resultList = new ArrayList<>();
        fieldBeans.forEach(fieldBean -> {
            String field = fieldBean.getField();
            String selectSql = StrUtil.format(sql, field, field);
            List<Map<String, String>> result = (List<Map<String, String>>) qaDataBiz.excuteSql(selectSql);

            Map<String, Long> hashMap =  new CaseInsensitiveMap<String, Long>();

            result.stream().forEach(s -> {
                s =  new CaseInsensitiveMap(s);
                String[] split = s.get(field).split(",");
                for (String str : split) {
                    if (hashMap.get(str) == null) {
                        hashMap.put(str, 1l);
                    } else {
                        hashMap.put(str, hashMap.get(str) + 1);
                    }
                }
            });

            //组装统计数据
            Map data = MapUtil.newHashMap();
            data.put("title",fieldBean.getName());
            ArrayList list = new ArrayList();
            for (String key : hashMap.keySet()) {
                HashMap map = new HashMap();
                map.put("name", key);
                map.put("value", hashMap.get(key));
                list.add(map);
            }
            data.put("data",list);
            resultList.add(data);
        });

        return ResultData.build().success(resultList);
    }


    /**
     * 查询当前选项的统计详情信息
     *
     * @param searchBean
     * @param request
     * @param response
     */
    @ApiOperation(value = "查询当前选项的统计详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qaName", value = "问卷调查名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "表单选项名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "query"),
    })
    @GetMapping("/statistics/details")
    @ResponseBody
    public ResultData details(@ApiIgnore SearchBean searchBean, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(searchBean.getQaName())) {
            return ResultData.build().error(getResString("err.empty", getResString("model.name")));
        }
        if (StringUtils.isEmpty(searchBean.getName())) {
            return ResultData.build().error(getResString("err.empty", getResString("model.name")));
        }

        QaEntity qaEntity = qaBiz.getOne(new LambdaQueryWrapper<QaEntity>().eq(QaEntity::getQaName, searchBean.getQaName()));
        if (qaEntity == null) {
            return ResultData.build().error("问卷调查名称有误");
        }
        List<FieldBean> fieldBeans = JSONUtil.toList(qaEntity.getModelField(), FieldBean.class);
        List<FieldBean> beanList = fieldBeans.stream().filter(fieldBean -> fieldBean.getName().equalsIgnoreCase(searchBean.getName())).collect(Collectors.toList());
        if (CollUtil.isEmpty(beanList)) {
            return ResultData.build().error("题目不存在");
        }

        String field = beanList.get(0).getField();

        String sql = "select create_date ,qa_device_type ,qa_ip,qa_address,{} as qa_content from {} where {} IS NOT NULL";
        if (searchBean.getStartTime() != null && searchBean.getEndTime() != null) {
            sql += StrUtil.format(" and create_date>='{}' and create_date<='{}'", DateUtil.format(searchBean.getStartTime(), "yyyy-MM-dd HH:mm:ss"), DateUtil.format(searchBean.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
        }

        String selectSql = StrUtil.format(sql, field, qaEntity.getQaTableName(), field);

        List<Map<String, Object>> list = (List<Map<String, Object>>) qaDataBiz.excuteSql(selectSql);

        // 分页
        int pageNo = BasicUtil.getPageNo();
        int pageSize = BasicUtil.getPageSize();
        int totalSize = list.size();
        List<Map<String, Object>> resultList = list.stream()
                .skip((long) (pageNo - 1) * pageSize)
                .limit(pageSize).collect(Collectors.toList());

        return ResultData.build().success(new EUListBean(resultList, totalSize));
    }


}
