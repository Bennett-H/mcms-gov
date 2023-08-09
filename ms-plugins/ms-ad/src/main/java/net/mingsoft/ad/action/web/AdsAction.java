/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.ad.action.web;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.ad.bean.AdsBean;
import net.mingsoft.ad.biz.IAdsBiz;
import net.mingsoft.ad.biz.IPositionBiz;
import net.mingsoft.ad.entity.AdsEntity;
import net.mingsoft.ad.entity.PositionEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 广告管理控制层
 *
 * @author 铭飞开发团队
 * 创建日期：2019-11-23 8:49:39<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-广告模块接口"})
@Controller("WebadAdsAction")
@RequestMapping("/ad/ads")
public class AdsAction extends net.mingsoft.ad.action.BaseAction {


    /**
     * 注入广告业务层
     */
    @Autowired
    private IAdsBiz adsBiz;


    /**
     * 注入广告位业务层
     */
    @Autowired
    private IPositionBiz positionBiz;

    /**
     * 查询广告列表
     *
     * @param ads 广告实体
     */
    @ApiOperation(value = "查询广告列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "positionId", value = "广告位编号", required = false, paramType = "query"),
            @ApiImplicitParam(name = "adsName", value = "广告名称", required = false, paramType = "query"),
            @ApiImplicitParam(name = "positionName", value = "广告位名称", required = false, paramType = "query"),
            @ApiImplicitParam(name = "queryDate", value = "广告时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "adsType", value = "广告类型", required = false, paramType = "query"),
            @ApiImplicitParam(name = "adsStartTime", value = "开始时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "adsEndTime", value = "结束时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "adsContent", value = "广告内容", required = false, paramType = "query"),
            @ApiImplicitParam(name = "adsState", value = "是否开启", required = false, paramType = "query"),
            @ApiImplicitParam(name = "adsPeopleName", value = "广告联系人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "adsPeoplePhone", value = "广告联系人电话", required = false, paramType = "query"),
            @ApiImplicitParam(name = "adsPeopleEmail", value = "广告联系人邮箱", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
    })
    @GetMapping("/list")
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore AdsBean ads, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
        if (ads.getQueryDate() == null) {
            ads.setQueryDate(new Date());
        }
        BasicUtil.startPage();
        List adsList = adsBiz.query(ads);
        return ResultData.build().success(new EUListBean(adsList, (int) BasicUtil.endPage(adsList).getTotal()));
    }

    /**
     * 获取广告
     *
     * @param ads 广告实体
     */
    @ApiOperation(value = "获取广告列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required = true, paramType = "query")
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore AdsEntity ads, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (ads.getId() == null) {
            return ResultData.build().error();
        }
        AdsEntity _ads = (AdsEntity) adsBiz.getEntity(Integer.parseInt(ads.getId()));
        return ResultData.build().success(_ads);
    }

    /**
     * 根据广告位名称获取广告
     *
     * @param bean 广告位实体
     */
    @ApiOperation(value = "根据广告位名称获取广告接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "positionName", value = "广告位名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "adsName", value = "广告名称", required = false, paramType = "query")
    })
    @GetMapping("/getAd")
    @ResponseBody
    public String getAd(@ModelAttribute @ApiIgnore AdsBean bean, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        // 获取广告位的数据
        PositionEntity position = positionBiz.getOne(new LambdaQueryWrapper<PositionEntity>().eq(PositionEntity::getPositionName, bean.getPositionName()));
        // 广告位不存在
        if (position == null){
            return "广告位不存在";
        }
        // 获取当前广告位的所有 未到期广告数据（支持精确查询）
        List<AdsEntity> adsList = adsBiz.list(new LambdaQueryWrapper<AdsEntity>()
                .eq(AdsEntity::getPositionId, position.getId())
                // 广告位状态开启
                .eq(AdsEntity::getAdsState,"open")
                .eq(StringUtils.isNotBlank(bean.getAdsName()),AdsEntity::getAdsName,bean.getAdsName())
                // 大于开始
                .le(AdsEntity::getAdsStartTime,new Date())
                // 小于结束
                .ge(AdsEntity::getAdsEndTime,new Date()));
        // 用于临时存储广告
        StringBuffer stringBuffer = new StringBuffer();
        for (AdsEntity asd:adsList){
            List imagePath = JSONUtil.toList(asd.getAdsImg(),Map.class);
            //  非空判断
            if (CollUtil.isEmpty(imagePath)) {
                // 默认白图片
                stringBuffer.append("<img src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQImWP4////fwAJ+wP9CNHoHgAAAABJRU5ErkJggg=='  width='" + position.getPositionWidth() + "px' height='" + position.getPositionHeight() + "px' />");
            } else {
                Map map = (Map) imagePath.get(0);
                // 存储广告图片
                stringBuffer.append("<a href='"+asd.getAdsUrl()+"'><img src='" + map.get("path") + "'  width='" + position.getPositionWidth() + "px' height='" + position.getPositionHeight() + "px' />");
            }
        }
        return "document.write(\""+stringBuffer+"\")";
    }
}
