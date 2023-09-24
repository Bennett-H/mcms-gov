/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.action;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.biz.IQrCodeBiz;
import net.mingsoft.mweixin.constant.SessionConst;
import net.mingsoft.mweixin.entity.QrCodeEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
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
import java.util.List;
import java.util.stream.Collectors;
/**
 * 场景二维码管理管理控制层
 * @author 铭飞开发团队
 * 创建日期：2023-6-5 14:21:08<br/>
 * 历史修订：<br/>
 */
@Api(tags = "后台-场景二维码管理接口")
@Controller("mweixinQrCodeAction")
@RequestMapping("/${ms.manager.path}/mweixin/qrCode")
public class QrCodeAction extends BaseAction{


	/**
	 * 注入场景二维码管理业务层
	 */
	@Autowired
	private IQrCodeBiz qrCodeBiz;

	/**
	 * 返回主界面index
	 */
	@GetMapping("/index")
	@RequiresPermissions("mweixin:qrCode:view")
	public String index(HttpServletResponse response,HttpServletRequest request) {
		return "/mweixin/qr-code/index";
	}

	/**
	 * 查询场景二维码管理列表
	 * @param qrCode 场景二维码管理实体
	 */
	@ApiOperation(value = "查询场景二维码管理列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "qrSceneStr", value = "场景值id(名称)", paramType = "query"),
			@ApiImplicitParam(name = "qrActionName", value = "二维码类型", paramType = "query"),
			@ApiImplicitParam(name = "qrExpireSeconds", value = "二维码有效期", paramType = "query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore QrCodeEntity qrCode,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List qrCodeList = null;
		//取出微信实体
		WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		if (weixin == null) {
			return ResultData.build().error(getResString("weixin.not.found"));
		}
		// 设置微信id
		qrCode.setWeixinId(weixin.getId());
		if ( qrCode.getSqlWhere() != null){
			qrCodeList = qrCodeBiz.query(qrCode);
		} else {
			LambdaQueryWrapper<QrCodeEntity> wrapper = new LambdaQueryWrapper<>(qrCode).orderByDesc(QrCodeEntity::getCreateDate);
			qrCodeList = qrCodeBiz.list(wrapper);
		}
		return ResultData.build().success(new EUListBean(qrCodeList,(int)BasicUtil.endPage(qrCodeList).getTotal()));
	}

	/**
	 * 返回编辑界面qrCode的form
	 */
	@GetMapping("/form")
	public String form(@ModelAttribute QrCodeEntity qrCode,HttpServletResponse response,HttpServletRequest request,ModelMap model) {
		return "/mweixin/qr-code/form";
	}


	/**
	 * 获取场景二维码管理
	 * @param qrCode 场景二维码管理实体
	 */
	@ApiOperation(value = "获取场景二维码管理列表接口")
    @ApiImplicitParam(name = "id", value = "主键ID", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore QrCodeEntity qrCode,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		if (qrCode.getId()==null) {
			return ResultData.build().error();
		}
		QrCodeEntity _qrCode = (QrCodeEntity)qrCodeBiz.getById(qrCode.getId());
		return ResultData.build().success(_qrCode);
	}


	/**
	* 保存场景二维码管理
	* @param qrCode 场景二维码管理实体
	*/
	@ApiOperation(value = "保存场景二维码管理列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required =true, paramType = "query"),
			@ApiImplicitParam(name = "qrSceneStr", value = "场景值id(名称)", required =true, paramType = "query"),
			@ApiImplicitParam(name = "qrActionName", value = "二维码类型", required =true, paramType = "query"),
			@ApiImplicitParam(name = "qrExpireSeconds", value = "二维码有效期", required =true, paramType = "query"),
			@ApiImplicitParam(name = "qrBeanName", value = "实现类bean名称", required =true, paramType = "query"),
	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存场景二维码管理", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("mweixin:qrCode:save")
	public ResultData save(@ModelAttribute @ApiIgnore QrCodeEntity qrCode, HttpServletResponse response, HttpServletRequest request) {
		//取出微信实体
		WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		if (weixin == null) {
			return ResultData.build().error(getResString("weixin.not.found"));
		}
		// 设置微信id
		qrCode.setWeixinId(weixin.getId());
		if (super.validated("WX_QR_CODE","QC_SCENE_STR",qrCode.getQcSceneStr())) {
			return ResultData.build().error(getResString("err.exist", this.getResString("qr.scene.str")));
		}
		if (!StringUtil.checkLength(qrCode.getWeixinId()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("weixin.id"), "0", "255"));
		}
		//验证场景值id(名称)的值是否合法
		if (StringUtils.isBlank(qrCode.getQcSceneStr())) {
			return ResultData.build().error(getResString("err.empty", this.getResString("qr.scene.str")));
		}
		if (!StringUtil.checkLength(qrCode.getQcSceneStr()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("qr.scene.str"), "0", "255"));
		}
		//验证二维码类型的值是否合法
		if (StringUtils.isBlank(qrCode.getQcActionName())) {
			return ResultData.build().error(getResString("err.empty", this.getResString("qr.action.name")));
		}
		if (!StringUtil.checkLength(qrCode.getQcExpireSeconds()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("qr.expire.seconds"), "0", "255"));
		}
		if (!StringUtil.checkLength(qrCode.getQcBeanName()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("bean.name"), "0", "255"));
		}
		qrCodeBiz.save(qrCode);
		return ResultData.build().success(qrCode);
	}

	/**
     *  删除场景二维码管理
     *
	 * @param qrCodes 场景二维码管理实体
	 */
	@ApiOperation(value = "批量删除场景二维码管理列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除场景二维码管理", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("mweixin:qrCode:del")
	public ResultData delete(@RequestBody List<QrCodeEntity> qrCodes,HttpServletResponse response, HttpServletRequest request) {
	    List<String> ids = (List)qrCodes.stream().map((p) -> {return p.getId();}).collect(Collectors.toList());
		return this.qrCodeBiz.removeByIds(ids) ? ResultData.build().success() : ResultData.build().error(this.getResString("err.error", new String[]{this.getResString("id")}));
		}

	/**
	 *	更新场景二维码管理列表
	 *
	 * @param qrCode 场景二维码管理实体
	 */
	 @ApiOperation(value = "更新场景二维码管理列表接口")
	 @ApiImplicitParams({    		@ApiImplicitParam(name = "weixinId", value = "微信编号", required =true, paramType = "query"),
			 @ApiImplicitParam(name = "qrSceneStr", value = "场景值id(名称)", required =true, paramType = "query"),
			 @ApiImplicitParam(name = "qrActionName", value = "二维码类型", required =true, paramType = "query"),
			 @ApiImplicitParam(name = "qrExpireSeconds", value = "二维码有效期", required =true, paramType = "query"),
			 @ApiImplicitParam(name = "qrBeanName", value = "实现类bean名称", required =true, paramType = "query"),
	 })
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新场景二维码管理", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("mweixin:qrCode:update")
	public ResultData update(@ModelAttribute @ApiIgnore QrCodeEntity qrCode, HttpServletResponse response,
			HttpServletRequest request) {
		 //取出微信实体
		 WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		 if (weixin == null) {
			 return ResultData.build().error(getResString("weixin.not.found"));
		 }
		 // 设置微信id
		 qrCode.setWeixinId(weixin.getId());
		 QrCodeEntity _qrCode = qrCodeBiz.getById(qrCode.getId());
		 if(_qrCode == null) {
			 return ResultData.build().error(getResString("err.not.exist",qrCode.getId() ));
		 }
		 if (super.validated("WX_QR_CODE", "QC_SCENE_STR", qrCode.getQcSceneStr(), qrCode.getId(), "id")) {
			 return ResultData.build().error(getResString("err.exist", this.getResString("qr.scene.str")));
		 }
		 if (!StringUtil.checkLength(qrCode.getWeixinId()+"", 0, 255)) {
			 return ResultData.build().error(getResString("err.length", this.getResString("weixin.id"), "0", "255"));
		 }
		 //验证场景值id(名称)的值是否合法
		 if (StringUtils.isBlank(qrCode.getQcSceneStr())) {
			 return ResultData.build().error(getResString("err.empty", this.getResString("qr.scene.str")));
		 }
		 if (!StringUtil.checkLength(qrCode.getQcSceneStr()+"", 0, 255)) {
			 return ResultData.build().error(getResString("err.length", this.getResString("qr.scene.str"), "0", "255"));
		 }
		 //验证二维码类型的值是否合法
		 if (StringUtils.isBlank(qrCode.getQcActionName())) {
			 return ResultData.build().error(getResString("err.empty", this.getResString("qr.action.name")));
		 }
		 if (!StringUtil.checkLength(qrCode.getQcExpireSeconds()+"", 0, 255)) {
			 return ResultData.build().error(getResString("err.length", this.getResString("qr.expire.seconds"), "0", "255"));
		 }
		 if (!StringUtil.checkLength(qrCode.getQcBeanName()+"", 0, 255)) {
			 return ResultData.build().error(getResString("err.length", this.getResString("bean.name"), "0", "255"));
		 }
		 qrCodeBiz.updateById(qrCode);
		 return ResultData.build().success(qrCode);
	}

	/**
	 * 检测场景二维码id(昵称)是否唯一
	 * @param qrSceneStr 场景二维码id(昵称)
	 * @param id (主键编号)
	 * @return 返回场景二维码id(昵称)是否唯一 TRUE就是不唯一，反之唯一
	 */
	@GetMapping("verify")
	@ResponseBody
	public ResultData verify(String qrSceneStr, String id) {
		//取出微信实体
		WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		if (weixin == null) {
			return ResultData.build().error(getResString("weixin.not.found"));
		}
		boolean verify = false;
		// 拼接查询条件
		LambdaQueryWrapper<QrCodeEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(QrCodeEntity::getWeixinId, weixin.getId())
				.eq(QrCodeEntity::getQcSceneStr, qrSceneStr);

		List<QrCodeEntity> list;
		if (StringUtils.isNotEmpty(id)) {
			wrapper.eq(StringUtils.isNotEmpty(id), QrCodeEntity::getId, id);
			list = qrCodeBiz.list(wrapper);
			// 如果查询出来大于1，说明还有其他值
			if (list.size() > 1) {
				verify = true;
			}
		} else {
			list = qrCodeBiz.list(wrapper);
			// 不为空还有其他值
            if (CollectionUtil.isNotEmpty(list)) {
                verify = true;
            }
		}
		return ResultData.build().success(verify);
	}

}
