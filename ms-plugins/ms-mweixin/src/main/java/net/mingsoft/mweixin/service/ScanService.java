/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mweixin.biz.IQrCodeBiz;
import net.mingsoft.mweixin.entity.QrCodeEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.handle.IScanHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * 扫码服务类
 * @author Binary Wang
 *
 */
@Component
public class ScanService extends AbstractService {


	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IQrCodeBiz qrCodeBiz;

	@Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
			WxSessionManager wxSessionManager) throws WxErrorException {
		WeixinEntity weixin =  ((PortalService) wxMpService).getWeixin();
		PortalService weixinService = (PortalService) wxMpService;
		// 获取扫码处理对象，为null不会执行
		IScanHandle scanHandle = getScanHandleBySceneStrAndWeixinId(wxMessage.getEventKey(), weixin.getId());
		if (scanHandle!=null){
			return scanHandle.handleSpecial(wxMessage,weixinService);
		}
		return null;
	}

	/**
	 * 根据场景值id和微信编号获取场景二维码实体，根据实体获取对应的处理bean
	 * @param sceneStr 场景值id
	 * @param weixinId 微信编号
	 * @return IScanHandle，没找到返回null
	 */
	public IScanHandle getScanHandleBySceneStrAndWeixinId(String sceneStr,String weixinId) {
		// 判断是否是被管理的场景二维码扫码关注
		LambdaQueryWrapper<QrCodeEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(QrCodeEntity::getQcSceneStr,sceneStr).eq(QrCodeEntity::getWeixinId,weixinId);
		QrCodeEntity qrCode = qrCodeBiz.getOne(wrapper);
		if (qrCode!=null){
			try {
				return (IScanHandle) SpringUtil.getBean(qrCode.getQcBeanName());
			} catch (NoSuchBeanDefinitionException e) {
				logger.debug("没有找到对应的bean");
				e.printStackTrace();
			} catch (ClassCastException e) {
				logger.debug("错误的配置，bean: {}不是实现了IScanHandle的实现类",qrCode.getQcBeanName());
				e.printStackTrace();
			}
		}
		return null;
	}
}
