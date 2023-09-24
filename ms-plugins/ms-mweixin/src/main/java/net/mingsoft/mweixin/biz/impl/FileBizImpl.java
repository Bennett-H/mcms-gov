/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.mweixin.biz.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Splitter;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mweixin.biz.IFileBiz;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.dao.IWxFileDao;
import net.mingsoft.mweixin.entity.FileEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 微信文件表管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2018-12-29 9:18:10<br/>
 * 历史修订：<br/>
 */
@Service("wxFileBizImpl")
public class FileBizImpl extends BaseBizImpl<IWxFileDao, FileEntity> implements IFileBiz {


    @Autowired
    private IWxFileDao fileDao;


    /**
     * 注入微信基础业务层
     */
    @Autowired
    private IWeixinBiz weixinBiz;




    @Override
    protected IBaseDao getDao() {
        return fileDao;
    }

    @Override
    public void deleteByIds(int[] ids,String weixinId) {
        WeixinEntity wx = (WeixinEntity) weixinBiz.getEntity(Integer.valueOf(weixinId));
        PortalService service = SpringUtil.getBean(PortalService.class);
        PortalService wxService = service.build(wx);
        List<FileEntity> fileEntities = fileDao.getEntitys(ids);
        for (FileEntity fileEntity : fileEntities) {
            if (StringUtils.isNotBlank(fileEntity.getFileMediaId())){
                try {
                    wxService.getMaterialService().materialDelete(fileEntity.getFileMediaId());
                } catch (WxErrorException e) {
                    e.printStackTrace();
                }

            }
        }
        this.delete(ids);
    }

    @Override
    public void deleteByCategoryId(String categoryId, String weixinId) {
        WeixinEntity wx = (WeixinEntity) weixinBiz.getEntity(Integer.valueOf(weixinId));
        PortalService service = SpringUtil.getBean(PortalService.class);
        PortalService wxService = service.build(wx);
        List<FileEntity> files = fileDao.selectList(new LambdaQueryWrapper<FileEntity>().eq(FileEntity::getCategoryId, categoryId));
        files.forEach(file ->{
            if (StringUtils.isNotBlank(file.getFileMediaId())){

                try {
                    wxService.getMaterialService().materialDelete(file.getFileMediaId());
                } catch (WxErrorException e) {
                    e.printStackTrace();
                }
            }
            fileDao.deleteById(file);
        });
    }


    @Override
    public void weiXinFileSyncLocal(WxMpMaterialFileBatchGetResult weiXinFileSyncLocal, int weixinId) {
        for (WxMpMaterialFileBatchGetResult.WxMaterialFileBatchGetNewsItem item : weiXinFileSyncLocal.getItems()) {
            //保存微信文件
             saveWeinXinImage(item.getMediaId(), weixinId, item.getUrl(), item.getName(),"image");
        }

    }

    @Override
    public void weiXinVoiceVideoSyncLocal(WxMpMaterialFileBatchGetResult weiXinFileSyncLocal, FileEntity file, WeixinEntity weixin) {
        for (WxMpMaterialFileBatchGetResult.WxMaterialFileBatchGetNewsItem item : weiXinFileSyncLocal.getItems()){
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileMediaId(item.getMediaId());
            if (ObjectUtil.isNull(getEntity(fileEntity))){
                fileEntity.setFileName(item.getName());
                fileEntity.setCreateDate(new Date());
                fileEntity.setFileMediaId(item.getMediaId());
                fileEntity.setFileUrl(item.getUrl());
                fileEntity.setFileType(file.getFileType());
                fileEntity.setWeixinId(weixin.getIntId());
                fileDao.insert(fileEntity);
            }
        }
    }


    @Override
    public String saveWeinXinImage(String mediaId, int weixinId, String url, String fileName,String fileType) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileMediaId(mediaId);
        FileEntity file = (FileEntity) getEntity(fileEntity);
        String uploadPath = ConfigUtil.getString("文件上传配置", "uploadPath", MSProperties.upload.path);
        if (ObjectUtil.isNotNull(file)) {
            if (!new File(BasicUtil.getRealPath("") + File.separator +file.getFileUrl()).exists()){//若本地没有则重新下载
                //下载文件本地
                String dbPath = uploadPath + "/" + weixinId + "/weixin/picture/image/" +
                        System.currentTimeMillis() + "." + getParam(url, "wx_fmt");
                String path = BasicUtil.getRealPath("") + File.separator + dbPath;
                //先创建文件
                FileUtil.touch(path);
                //下载文件
                long fileSize= HttpUtil.downloadFile(url, FileUtil.file(path));
                //传入本地地址
                file.setFileUrl("/" + dbPath);
                //传入文件大小
                file.setFileSize((int) fileSize);
            }
            file.setWeixinId(weixinId);
            file.setCreateDate(new Date());
            file.setFileName(fileName);
            fileDao.updateEntity(file);
            return file.getFileUrl();
        } else {
            fileEntity.setWeixinId(weixinId);
            fileEntity.setCreateDate(new Date());
            fileEntity.setFileName(fileName);
            fileEntity.setFileType(fileType);
            //下载文件本地
            String dbPath = uploadPath + "/" + weixinId + "/weixin/picture/image/" +
                    System.currentTimeMillis() + "." + getParam(url, "wx_fmt");
            String path = BasicUtil.getRealPath("") + File.separator  + dbPath;
//				先创建文件
            FileUtil.touch(path);
            //下载文件
           long fileSize= HttpUtil.downloadFile(url, FileUtil.file(path));
            //传入本地地址
            fileEntity.setFileUrl("/" + dbPath);
            //传入文件大小
            fileEntity.setFileSize((int) fileSize);
            //先保存文件
            fileDao.insert(fileEntity);
            return fileEntity.getFileUrl();
        }
    }

    /**
     * 通过mediaId请求微信服务器下载图片
     */
    @Override
    public String saveWeinXinImage(String mediaId, int weixinId, String fileName, String fileType) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileMediaId(mediaId);
        FileEntity file = (FileEntity) getEntity(fileEntity);
        String uploadPath = ConfigUtil.getString("文件上传配置", "uploadPath", MSProperties.upload.path);
        if (ObjectUtil.isNotNull(file)) {
            file.setWeixinId(weixinId);
            file.setCreateDate(new Date());
            file.setFileName(fileName);
            fileDao.updateEntity(file);
            return file.getFileUrl();
        }else {
            WeixinEntity wx = (WeixinEntity) weixinBiz.getEntity(weixinId);
            PortalService service = SpringUtil.getBean(PortalService.class);
            PortalService wxService = service.build(wx);
            WxMpMaterialService materialService = wxService.getMaterialService();
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                fileEntity.setWeixinId(weixinId);
                fileEntity.setCreateDate(new Date());
                fileEntity.setFileName(fileName);
                fileEntity.setFileType(fileType);
                String filePath = uploadPath + "/" + weixinId + "/weixin/picture/image/"+System.currentTimeMillis() + ".jpg";
                inputStream = materialService.materialImageOrVoiceDownload(mediaId);
                fileOutputStream = new FileOutputStream(BasicUtil.getRealPath("") + File.separator +filePath);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                    fileOutputStream.flush();
                }
                //传入本地地址
                fileEntity.setFileUrl(filePath);
                //传入文件大小
                fileEntity.setFileSize((int) inputStream.available());
                //先保存文件
                fileDao.insert(fileEntity);
                return fileEntity.getFileUrl();

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    inputStream.close();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }

    @Override
    public void weiXinFileUpload(WeixinEntity weixin, FileEntity fileEntity) throws WxErrorException {
        //上传素材到微信服务器，返回madieid和图片url
        File file = new File(BasicUtil.getRealPath("") + File.separator  + fileEntity.getFileUrl());
        if (StringUtil.isBlank(fileEntity.getFileMediaId())) {
            WxMpMaterial wxMaterial = new WxMpMaterial();
            wxMaterial.setFile(file);
            wxMaterial.setName(file.getName());
            wxMaterial.setVideoTitle(file.getName());
            PortalService wxService =  SpringUtil.getBean(PortalService.class).build(weixin);
            WxMpMaterialUploadResult res = wxService.getMaterialService()
                    .materialFileUpload(null, wxMaterial);
            fileEntity.setFileMediaId(res.getMediaId());
            fileDao.updateById(fileEntity);
        }
    }

    /**
     * 获取URL地址的参数
     * @param url
     * @param name
     * @return
     */
    public static String getParam(String url, String name) {
        String params = url.substring(url.indexOf("?") + 1, url.length());
        Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(params);
        return split.get(name);
    }


}
