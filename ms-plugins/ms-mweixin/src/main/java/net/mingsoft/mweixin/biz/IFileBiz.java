/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.biz;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.mweixin.entity.FileEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;

/**
 * 微信文件表业务
 * @author 铭飞开发团队
 * 创建日期：2018-12-29 9:18:10<br/>
 * 历史修订：<br/>
 */
public interface IFileBiz extends IBaseBiz<FileEntity> {

    /**
     * 根据id删除文件并同步到微信服务器
     * @param ids 栏目ids
     * @param weixinId 微信编号
     */
    void deleteByIds(int[] ids,String weixinId);

    /**
     * 根据id删除文件并同步到微信服务器
     * @param categoryId 栏目编号
     * @param weixinId 微信编号
     */
    void deleteByCategoryId(String categoryId,String weixinId);

    /**
     * 微信图片同步至本地
     * @return
     */
    void weiXinFileSyncLocal(WxMpMaterialFileBatchGetResult weiXinFileSyncLocal, int weixinId);

    /**
     * 微信语音同步至本地
     * @return
     */
    void weiXinVoiceVideoSyncLocal(WxMpMaterialFileBatchGetResult weiXinFileSyncLocal,FileEntity file,WeixinEntity weixin);
    /**
     * 保存微信图片
     * @param mediaId
     * @param weixinId
     * @param url
     * @param fileName
     */
    String saveWeinXinImage(String mediaId, int weixinId, String url, String fileName,String fileType);

    /**
     * 保存微信图片
     * @param mediaId
     * @param weixinId
     * @param fileName
     */
    String saveWeinXinImage(String mediaId, int weixinId, String fileName,String fileType);

    /**
     * 上传文件到微信服务器
     * @param wixin
     * @param fileEntity
     * @return
     */
    void weiXinFileUpload(WeixinEntity wixin, FileEntity fileEntity) throws WxErrorException;

}
