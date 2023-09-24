/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.quartz;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpFreePublishService;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishStatus;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.constant.e.DraftPublishStateEnum;
import net.mingsoft.mweixin.entity.DraftEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.service.PortalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定时获取草稿发布的结果
 */
@Component("draftPublishStateQuartz")
public class DraftPublishStateQuartz {

    @Resource
    IDraftBiz draftBiz;

    @Autowired
    IWeixinBiz weixinBiz;

    protected final static Logger LOG = LoggerFactory.getLogger(DraftPublishStateQuartz.class);

    public void getPublishedResults() {
        LOG.debug("开始获取草稿发布的结果");
        LambdaQueryWrapper<DraftEntity> wrapper = new LambdaQueryWrapper<DraftEntity>()
                .isNotNull(DraftEntity::getPublishId)
                .eq(DraftEntity::getPublishState, DraftPublishStateEnum.PUBLISHING.toInt());
        //获取所有发布中的草稿
        List<DraftEntity> list = draftBiz.list(wrapper);
        if (CollUtil.isEmpty(list)) {
            LOG.debug("暂无需要获取发布结果的草稿", list.size());
            return;
        }
        LOG.debug("共{}篇待获取发布结果的草稿", list.size());


        //通过微信id分组
        Map<Integer, List<DraftEntity>> listMap = list.stream().collect(Collectors.groupingBy(DraftEntity::getWeixinId));
        for (Integer weixinId : listMap.keySet()) {
            WeixinEntity wx = (WeixinEntity) weixinBiz.getEntity(weixinId);
            PortalService service = SpringUtil.getBean(PortalService.class);
            PortalService wxService = service.build(wx);
            WxMpFreePublishService publishService = wxService.getFreePublishService();
            //获取当前微信的所有发布中的草稿
            List<DraftEntity> draftEntities = listMap.get(weixinId);

            for (DraftEntity draftEntity : draftEntities) {
                try {
                    WxMpFreePublishStatus pushStatus = publishService.getPushStatus(draftEntity.getPublishId());
                    Integer publishStatus = pushStatus.getPublish_status();
                    if (publishStatus == 0) {//发布成功
                        draftEntity.setPublishState(DraftPublishStateEnum.PUBLISHED.toInt());
                        draftEntity.setArticleId(pushStatus.getArticle_id());
                        //删除同步已发布列表导致的重复数据
                        draftBiz.remove(new LambdaQueryWrapper<DraftEntity>().eq(DraftEntity::getArticleId,pushStatus.getArticle_id()));
                    } else if (publishStatus == 2) {//发布失败(审核不通过时)
                        draftEntity.setPublishState(DraftPublishStateEnum.PUBLISHING_FAILED.toInt());
                    }
                    draftBiz.updateById(draftEntity);

                } catch (WxErrorException e) {
                    e.printStackTrace();
                }
            }

        }

        LOG.debug("获取草稿发布的结果结束");

    }


}
