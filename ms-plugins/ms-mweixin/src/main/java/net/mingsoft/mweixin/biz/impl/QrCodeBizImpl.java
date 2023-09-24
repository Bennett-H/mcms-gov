/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.biz.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.mweixin.biz.IQrCodeBiz;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.dao.IQrCodeDao;
import net.mingsoft.mweixin.entity.QrCodeEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景二维码管理管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2023-6-5 15:16:50<br/>
 * 历史修订：<br/>
 */
@Service("mweixinqrCodeBizImpl")
public class QrCodeBizImpl extends BaseBizImpl<IQrCodeDao,QrCodeEntity> implements IQrCodeBiz {


	@Autowired
	private IQrCodeDao qrCodeDao;

    @Autowired
    private IWeixinBiz weixinBiz;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return qrCodeDao;
	}

    @Override
    public Map qrCodeCreateTmpTicket(QrCodeEntity qrCodeEntity) {
        if (qrCodeEntity == null && StringUtils.isEmpty(qrCodeEntity.getWeixinId())) {
            throw new BusinessException("未找到配置");
        }
        //获取微信配置
        WeixinEntity weixin = (WeixinEntity)weixinBiz.getEntity(Integer.parseInt(qrCodeEntity.getWeixinId()));
        if (weixin == null) {
            throw new BusinessException("未找到配置");
        }
        PortalService service = new PortalService().build(weixin);
        WxMpQrcodeService qrcodeService = service.getQrcodeService();
        WxMpQrCodeTicket wxMpQrCodeTicket = null;
        try {
            wxMpQrCodeTicket = qrcodeService.qrCodeCreateTmpTicket(qrCodeEntity.getQcSceneStr(), qrCodeEntity.getQcExpireSeconds());
            File file = qrcodeService.qrCodePicture(wxMpQrCodeTicket);
            HashMap<String, Object> map = new HashMap<>();
            map.put("qCode",FileUtil.readBytes(file));
            map.put("key", wxMpQrCodeTicket.getTicket());
            return map;
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public List<QrCodeEntity> queryQrCode(String weixinNo, String qrSceneStr, String qrActionName) {

        String weixinId = "";
        // 获取微信编号
        WeixinEntity weixinEntity = weixinBiz.getByWeixinNo(weixinNo);

        if (weixinEntity != null) {
            weixinId = weixinEntity.getId();
        }

        // 根据条件查询
        LambdaQueryWrapper<QrCodeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(weixinId),QrCodeEntity::getWeixinId, weixinId)
                .eq(StringUtils.isNotEmpty(qrSceneStr), QrCodeEntity::getQcSceneStr, qrSceneStr)
                .eq(StringUtils.isNotEmpty(qrActionName), QrCodeEntity::getQcActionName, qrActionName);

        return list(wrapper);
    }
}
