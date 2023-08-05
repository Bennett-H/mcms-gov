/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.action;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.strategy.IModelStrategy;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.constant.Const;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.MissingResourceException;
import java.util.stream.Collectors;

/**
 * mdiy基础控制层
 * @author 铭飞开发团队
 * 创建日期：2018-10-24 8:44:33<br/>
 * 历史修订：<br/>
 */
public class BaseAction extends net.mingsoft.basic.action.BaseAction{


	@Autowired
	private IModelStrategy modelStrategy;

	@Override
	protected String getResString(String key) {
		// TODO Auto-generated method stub
		String str = "";
		try {
			str = super.getResString(key);
		} catch (MissingResourceException e) {
			str = getLocaleString(key, Const.RESOURCES);
		}

		return str;
	}

	/**
	 * 获取权限,若没有主权限,就以次要权限为准
	 * @param masterPermission 主权限
	 * @param permission 次要权限
	 * @return
	 */
	protected boolean getPermissions(String masterPermission,String permission) {
		//总权限
		int size = modelStrategy.list().stream().filter(model -> (masterPermission).equalsIgnoreCase(model.getModelUrl()))
				.collect(Collectors.toList()).size();
		if (size <= 0){
			//没有再查询细分权限
			size = modelStrategy.list().stream().filter(model -> (permission).equalsIgnoreCase(model.getModelUrl()))
					.collect(Collectors.toList()).size();
			if (size <= 0 ){
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证验证码
	 *
	 * @return 如果相同，返回true，否则返回false
	 */
	@Override
	protected boolean checkRandCode() {
		return checkRandCode( SessionConstEnum.CODE_SESSION.toString());
	}

	/**
	 * 验证验证码
	 *
	 * @param param   表单验证码参数名称
	 * @return 如果相同，返回true，否则返回false
	 */
	@Override
	protected boolean checkRandCode( String param) {
		boolean checkCode = ConfigUtil.getBoolean("后台开发配置","webCheckCode",true);
		if(!checkCode){
			return true;
		}
		String sessionCode = this.getRandCode();
		String requestCode = BasicUtil.getString(param);
		LOG.debug("session_code:" + sessionCode + " requestCode:" + requestCode);

		// 不请求code 验证码默认"null" 也可以一直登录
		if ("null".equals(sessionCode)){
			return false;
		}
		if (sessionCode.equalsIgnoreCase(requestCode)) {
			// 验证码正确 删除session中的验证码  删除后为"null"
			BasicUtil.removeSession(SessionConstEnum.CODE_SESSION);
			return true;
		}
		return false;
	}
}
