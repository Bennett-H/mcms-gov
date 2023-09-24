/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.bean.MenuBean;
import net.mingsoft.mweixin.biz.IMenuBiz;
import net.mingsoft.mweixin.entity.MenuEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 微信菜单管理控制层
 * @author 铭飞
 * @version
 * 版本号：100<br/>
 * 创建日期：2017-12-22 9:43:04<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-微信模块接口"})
@Controller("netMenuAction")
@RequestMapping("/${ms.manager.path}/mweixin/menu")
public class MenuAction extends BaseAction{

	/**
	 * 注入微信菜单业务层
	 */
	@Resource(name="netMenuBizImpl")
	private IMenuBiz menuBiz;




	/**
	 * 菜单发布 weixin:menu:sync
	 */
	@ApiOperation(value="微信菜单发布接口")
	@GetMapping("/create")
	@ResponseBody
	@RequiresPermissions("weixin:menu:sync")
	public ResultData create(HttpServletResponse response, HttpServletRequest request) throws WxErrorException {

		//取出微信实体
		WeixinEntity weixin = this.getWeixinSession(request);
		if(weixin == null || weixin.getIntId() <= 0){
			return ResultData.build().error();
		}
		try {
			menuBiz.releaseMenu(weixin);
			return ResultData.build().success();
		}catch (WxErrorException e){
			e.printStackTrace();
			return ResultData.build().error(e.getMessage());

		}

	}

	/**
	 * 返回主界面index
	 */
	@GetMapping("/index")
	@ApiIgnore
	@RequiresPermissions("weixin:menu:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/mweixin/menu/index";
	}

	/**
	 * 查询微信菜单列表
	 * @param menu 微信菜单实体
	 * <i>menu参数包含字段信息参考：</i><br/>
	 * menuId 菜单自增长编号<br/>
	 * appId 菜单所属商家编号<br/>
	 * menuTitle 菜单名称<br/>
	 * menuUrl 菜单链接地址<br/>
	 * menuStatus 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId 父菜单编号<br/>
	 * menuType 菜单属性 0:链接 1:回复<br/>
	 * menuSort <br/>
	 * menuStyle 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId 授权数据编号<br/>
	 * menuWeixinId 微信编号<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>[<br/>
	 * { <br/>
	 * menuId: 菜单自增长编号<br/>
	 * appId: 菜单所属商家编号<br/>
	 * menuTitle: 菜单名称<br/>
	 * menuUrl: 菜单链接地址<br/>
	 * menuStatus: 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId: 父菜单编号<br/>
	 * menuType: 菜单属性 0:链接 1:回复<br/>
	 * menuSort: <br/>
	 * menuStyle: 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId: 授权数据编号<br/>
	 * menuWeixinId: 微信编号<br/>
	 * }<br/>
	 * ]</dd><br/>
	 */
	@ApiOperation(value="微信菜单列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "menuTitle", value = "菜单名称", required =false,paramType="query"),
			@ApiImplicitParam(name = "menuUrl", value = "菜单链接地址", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuStatus", value = "菜单状态 0：不启用 1：启用", required =false,paramType="query"),
			@ApiImplicitParam(name = "menuMenuId", value = "父菜单编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "menuType", value = "菜单属性 0:链接 1:回复", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuSort", value = "菜单排序", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuStyle", value = "类型：text文本 image图片 link外链接", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuOauthId", value = "授权数据编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuWeixinId", value = "微信编号", required = false,paramType="query"),
	})
	@GetMapping("/list")
	@ResponseBody
	@RequiresPermissions("weixin:menu:view")
	public ResultData list(@ModelAttribute MenuEntity menu,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		WeixinEntity weixin = this.getWeixinSession(request);
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error();
		}
		menu.setMenuWeixinId(weixin.getIntId());
		List menuList = menuBiz.query(menu);
		return ResultData.build().success(new EUListBean(menuList,menuList.size()));
	}


	/**
	 * 获取微信菜单
	 * @param menu 微信菜单实体
	 * <i>menu参数包含字段信息参考：</i><br/>
	 * menuId 菜单自增长编号<br/>
	 * appId 菜单所属商家编号<br/>
	 * menuTitle 菜单名称<br/>
	 * menuUrl 菜单链接地址<br/>
	 * menuStatus 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId 父菜单编号<br/>
	 * menuType 菜单属性 0:链接 1:回复<br/>
	 * menuSort <br/>
	 * menuStyle 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId 授权数据编号<br/>
	 * menuWeixinId 微信编号<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * menuId: 菜单自增长编号<br/>
	 * appId: 菜单所属商家编号<br/>
	 * menuTitle: 菜单名称<br/>
	 * menuUrl: 菜单链接地址<br/>
	 * menuStatus: 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId: 父菜单编号<br/>
	 * menuType: 菜单属性 0:链接 1:回复<br/>
	 * menuSort: <br/>
	 * menuStyle: 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId: 授权数据编号<br/>
	 * menuWeixinId: 微信编号<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value = "获取微信菜单")
	@ApiImplicitParam(name = "menuId", value = "菜单编号", required = true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("weixin:menu:view")
	public ResultData get(@ModelAttribute MenuEntity menu,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(StringUtils.isEmpty(menu.getId())) {
			return ResultData.build().error(getResString("err.error", this.getResString("menu.id")));
		}
		MenuEntity _menu = (MenuEntity)menuBiz.getEntity(menu.getIntId());
		return ResultData.build().success(_menu);
	}

	/**
	 * 保存微信菜单实体
	 * @param menu 微信菜单实体
	 * <i>menu参数包含字段信息参考：</i><br/>
	 * menuId 菜单自增长编号<br/>
	 * appId 菜单所属商家编号<br/>
	 * menuTitle 菜单名称<br/>
	 * menuUrl 菜单链接地址<br/>
	 * menuStatus 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId 父菜单编号<br/>
	 * menuType 菜单属性 0:链接 1:回复<br/>
	 * menuSort <br/>
	 * menuStyle 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId 授权数据编号<br/>
	 * menuWeixinId 微信编号<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * menuId: 菜单自增长编号<br/>
	 * appId: 菜单所属商家编号<br/>
	 * menuTitle: 菜单名称<br/>
	 * menuUrl: 菜单链接地址<br/>
	 * menuStatus: 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId: 父菜单编号<br/>
	 * menuType: 菜单属性 0:链接 1:回复<br/>
	 * menuSort: <br/>
	 * menuStyle: 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId: 授权数据编号<br/>
	 * menuWeixinId: 微信编号<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value="微信菜单保存接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "menuTitle", value = "菜单名称", required =true,paramType="query"),
			@ApiImplicitParam(name = "menuUrl", value = "菜单链接地址", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuStatus", value = "菜单状态 0：不启用 1：启用", required =false,paramType="query"),
			@ApiImplicitParam(name = "menuMenuId", value = "父菜单编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "menuType", value = "菜单属性 0:链接 1:回复", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuSort", value = "菜单排序", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuStyle", value = "类型：text文本 image图片 link外链接", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuOauthId", value = "授权数据编号", required = false,paramType="query"),
	})
	@LogAnn(title = "微信菜单保存接口",businessType= BusinessTypeEnum.INSERT)
	@PostMapping("/save")
	@ResponseBody
	@RequiresPermissions("weixin:menu:save")
	public ResultData save(@ModelAttribute MenuEntity menu, HttpServletResponse response, HttpServletRequest request,BindingResult result) {
		//验证菜单名称的值是否合法
		if(StringUtils.isBlank(menu.getMenuTitle())){
			return ResultData.build().error(getResString("err.empty", this.getResString("menu.title")));
		}
		if(!StringUtil.checkLength(menu.getMenuTitle()+"", 1, 15)){
			return ResultData.build().error(getResString("err.length", this.getResString("menu.title"), "1", "15"));
		}
		WeixinEntity weixin = this.getWeixinSession(request);
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error();
		}
		menu.setMenuWeixinId(weixin.getIntId());
		menuBiz.saveEntity(menu);
		return ResultData.build().success(menu);
	}

	/**
	 * <i>menu参数包含字段信息参考：</i><br/>
	 * menuId:多个menuId直接用逗号隔开,例如menuId=1,2,3,4
	 * 批量删除微信菜单
	 *            <dt><span class="strong">返回</span></dt><br/>
	 *            <dd>{code:"错误编码",<br/>
	 *            result:"true｜false",<br/>
	 *            resultMsg:"错误信息"<br/>
	 *            }</dd>
	 */
	@ApiOperation(value = "微信删除接口")
	@LogAnn(title = "微信删除接口",businessType= BusinessTypeEnum.DELETE)
	@PostMapping("/delete")
	@ResponseBody
	@RequiresPermissions("weixin:menu:del")
	public ResultData delete(HttpServletResponse response, HttpServletRequest request) {
		int[] ids = BasicUtil.getInts("ids", ",");
		if (ArrayUtil.isEmpty(ids)) {
			return ResultData.build().error(getResString("err.empty",this.getResString("menu.id")));
		}
		menuBiz.delete(ids);
		return ResultData.build().success();
	}

	/**
	 * 更新微信菜单信息微信菜单
	 * @param menu 微信菜单实体
	 * <i>menu参数包含字段信息参考：</i><br/>
	 * menuId 菜单自增长编号<br/>
	 * appId 菜单所属商家编号<br/>
	 * menuTitle 菜单名称<br/>
	 * menuUrl 菜单链接地址<br/>
	 * menuStatus 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId 父菜单编号<br/>
	 * menuType 菜单属性 0:链接 1:回复<br/>
	 * menuSort <br/>
	 * menuStyle 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId 授权数据编号<br/>
	 * menuWeixinId 微信编号<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * menuId: 菜单自增长编号<br/>
	 * appId: 菜单所属商家编号<br/>
	 * menuTitle: 菜单名称<br/>
	 * menuUrl: 菜单链接地址<br/>
	 * menuStatus: 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId: 父菜单编号<br/>
	 * menuType: 菜单属性 0:链接 1:回复<br/>
	 * menuSort: <br/>
	 * menuStyle: 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId: 授权数据编号<br/>
	 * menuWeixinId: 微信编号<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value="微信菜单修改接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "menuId", value = "菜单编号", required =true,paramType="query"),
			@ApiImplicitParam(name = "menuTitle", value = "菜单名称", required =true,paramType="query"),
			@ApiImplicitParam(name = "menuUrl", value = "菜单链接地址", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuStatus", value = "菜单状态 0：不启用 1：启用", required =false,paramType="query"),
			@ApiImplicitParam(name = "menuMenuId", value = "父菜单编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "menuType", value = "菜单属性 0:链接 1:回复", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuSort", value = "菜单排序", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuStyle", value = "类型：text文本 image图片 link外链接", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuOauthId", value = "授权数据编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuWeixinId", value = "微信编号", required = false,paramType="query"),
	})
	@LogAnn(title = "微信菜单修改接口",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/update")
	@ResponseBody
	@RequiresPermissions("weixin:menu:update")
	public ResultData update(@ModelAttribute MenuEntity menu, HttpServletResponse response,
					   HttpServletRequest request) {
		//验证菜单名称的值是否合法			
		if(StringUtils.isBlank(menu.getMenuTitle())){
			return ResultData.build().error(getResString("err.empty", this.getResString("menu.title")));
		}
		if(!StringUtil.checkLength(menu.getMenuTitle()+"", 1, 15)){
			return ResultData.build().error(getResString("err.length", this.getResString("menu.title"), "1", "15"));
		}
		menuBiz.updateEntity(menu);
		return ResultData.build().success(menu);
	}

	/**
	 * 检查的菜单的数量是否符合微信的数量5个
	 * @param menu
	 * @return
	 */
	@ApiOperation(value="检查的菜单数量接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "menuId", value = "菜单编号", required =true,paramType="query"),
			@ApiImplicitParam(name = "menuTitle", value = "菜单名称", required =true,paramType="query"),
			@ApiImplicitParam(name = "menuUrl", value = "菜单链接地址", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuStatus", value = "菜单状态 0：不启用 1：启用", required =false,paramType="query"),
			@ApiImplicitParam(name = "menuMenuId", value = "父菜单编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "menuType", value = "菜单属性 0:链接 1:回复", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuSort", value = "菜单排序", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuStyle", value = "类型：text文本 image图片 link外链接", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuOauthId", value = "授权数据编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "menuWeixinId", value = "微信编号", required = false,paramType="query"),
	})
	@GetMapping("/check")
	@ResponseBody
	public ResultData check(@ModelAttribute MenuEntity menu, HttpServletResponse response,
					  HttpServletRequest request){
		WeixinEntity weixin = this.getWeixinSession(request);
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error();
		}
		MenuEntity menuEntity = new MenuEntity();
		menuEntity.setMenuWeixinId(weixin.getIntId());
		List<MenuEntity> menuList = menuBiz.query(menuEntity);
		//计算一级菜单的总数
		int i = 0;
		//判断是否是添加一级菜单
		if(menu.getMenuMenuId()!=0){
			MenuEntity subMenu = new MenuEntity();
			subMenu.setMenuMenuId(menu.getMenuMenuId());
			//判断当前菜单有是否超过5个子菜单
			if(menuBiz.query(subMenu).size() > 5){
				return ResultData.build().error(this.getResString("menu.son.max"));
			}
		}else{
			for(MenuEntity _menuEntity : menuList){
				//判断是否是添加一级菜单
				if(_menuEntity.getMenuMenuId() == 0){
					i++;
					//一级菜单最多只能存在3个
					if(i>=3){
						return ResultData.build().error(this.getResString("menu.parent.max"));
					}
				}
			}
		}
		return ResultData.build().success();
	}

	/**
	 * 保存微信菜单实体
	 * @param menus 微信菜单实体集合
	 * <i>menu参数包含字段信息参考：</i><br/>
	 * menuId 菜单自增长编号<br/>
	 * appId 菜单所属商家编号<br/>
	 * menuTitle 菜单名称<br/>
	 * menuUrl 菜单链接地址<br/>
	 * menuStatus 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId 父菜单编号<br/>
	 * menuType 菜单属性 0:链接 1:回复<br/>
	 * menuSort <br/>
	 * menuStyle 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId 授权数据编号<br/>
	 * menuWeixinId 微信编号<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * menuId: 菜单自增长编号<br/>
	 * appId: 菜单所属商家编号<br/>
	 * menuTitle: 菜单名称<br/>
	 * menuUrl: 菜单链接地址<br/>
	 * menuStatus: 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId: 父菜单编号<br/>
	 * menuType: 菜单属性 0:链接 1:回复<br/>
	 * menuSort: <br/>
	 * menuStyle: 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId: 授权数据编号<br/>
	 * menuWeixinId: 微信编号<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value="微信菜单排序接口")
	@PostMapping("/menuSort")
	@ResponseBody
	public ResultData menuSort(@RequestBody List<MenuBean> menus, HttpServletResponse response, HttpServletRequest request) {
		WeixinEntity weixin = this.getWeixinSession(request);
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error();
		}
		if(ObjectUtil.isNotNull(menus)){
			menuBiz.saveOrUpdate(weixin,menus,false);
			return ResultData.build().success();
		}else {
			return ResultData.build().error();
		}
	}
	/**
	 * 保存与更新微信菜单实体
	 * @param menus 微信菜单实体集合
	 * <i>menu参数包含字段信息参考：</i><br/>
	 * menuId 菜单自增长编号<br/>
	 * appId 菜单所属商家编号<br/>
	 * menuTitle 菜单名称<br/>
	 * menuUrl 菜单链接地址<br/>
	 * menuStatus 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId 父菜单编号<br/>
	 * menuType 菜单属性 0:链接 1:回复<br/>
	 * menuSort <br/>
	 * menuStyle 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId 授权数据编号<br/>
	 * menuWeixinId 微信编号<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * menuId: 菜单自增长编号<br/>
	 * appId: 菜单所属商家编号<br/>
	 * menuTitle: 菜单名称<br/>
	 * menuUrl: 菜单链接地址<br/>
	 * menuStatus: 菜单状态 0：不启用 1：启用<br/>
	 * menuMenuId: 父菜单编号<br/>
	 * menuType: 菜单属性 0:链接 1:回复<br/>
	 * menuSort: <br/>
	 * menuStyle: 类型：text文本 image图片 link外链接<br/>
	 * menuOauthId: 授权数据编号<br/>
	 * menuWeixinId: 微信编号<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value="微信保存与更新")
	@LogAnn(title = "微信保存与更新",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/saveOrUpdate")
	@ResponseBody
	public ResultData saveOrUpdate(@RequestBody List<MenuBean> menus, HttpServletResponse response, HttpServletRequest request) {
		WeixinEntity weixin = this.getWeixinSession(request);
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error();
		}
		if(ObjectUtil.isNotNull(menus)){
			menuBiz.saveOrUpdate(weixin,menus,true);
			return ResultData.build().success();
		}else {
			return ResultData.build().error();
		}
	}
}
