/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.file.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.log.Log;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.util.ImageUtil;
import net.mingsoft.file.constant.Const;
import net.mingsoft.basic.service.IUploadBaseService;
import net.mingsoft.file.service.IUploadChunkService;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static net.mingsoft.base.util.BundleUtil.getResString;

/**
 * 七牛云的上传实现类
 */
@Service("uploadQiNiuService")
public class UploadQiNiuServiceImpl extends IUploadChunkService {

    @Override
    public ResultData upload(UploadConfigBean bean) {
        ResultData resultData = super.upload(bean);
        // 分片未上传完毕则返回
        if (resultData.getCode()!= HttpStatus.OK.value()){
            return resultData;
        }
        LOG.debug("七牛云上传实现");
        String targetFile = resultData.getData(String.class);

        try {
            ImageUtil.imgWatermark(targetFile);
        } catch (IOException e) {
            LOG.debug("水印添加失败");
            e.printStackTrace();
        }
        /**
         * 获取七牛云配置
         */
        Map<String,String> map = ConfigUtil.getMap("七牛云配置");
        String qiniuAk = map.get("qiniuAk");
        String qiniuSk = map.get("qiniuSk");
        String qiniuDomain = map.get("qiniuDomain");
        String bucketName = map.get("bucketName");
        if(!qiniuDomain.endsWith("/")){
            qiniuDomain += "/";
        }
        String uploadPath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadPath", "upload");
        // 判断配置的上传路径是否绝对路径
        boolean isReal = new File(uploadPath).isAbsolute();
        String uploadMapping = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadMapping", "/upload/**");
        targetFile = targetFile.replace(uploadMapping.replace("**", ""), "");
        if(isReal) {
            //源图片
            targetFile = uploadPath+"/"+targetFile.replace(uploadMapping.replace("**", ""), "");
        } else {
            targetFile = BasicUtil.getRealPath(uploadPath + File.separator +targetFile.replace(uploadMapping.replace("**", ""), ""));
        }
        /**
         * 七牛云上传逻辑
         */
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = qiniuAk;
        String secretKey = qiniuSk;
        String bucket =  bucketName;
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = bean.getFileName();
       String filePath = null;

        InputStream inputStream = FileUtil.getInputStream(targetFile);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(inputStream,key,upToken,null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            filePath = qiniuDomain + putRet.key;
            System.out.println("完整文件路径:\t"+ filePath);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
        // 清除本地服务器文件
        if (FileUtil.file(targetFile).exists()){
            FileUtil.del(targetFile);
        }
        return ResultData.build().success(filePath);

    }

    @Override
    public boolean checkFileIfExist(String realPath) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        //...其他参数参考类注释
        Map<String,String> map = ConfigUtil.getMap("七牛云配置");
        String qiniuAk = map.get("qiniuAk");
        String qiniuSk = map.get("qiniuSk");
        String bucketName = map.get("bucketName");
        String key = FileUtil.getName(realPath);
        Auth auth = Auth.create(qiniuAk, qiniuSk);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            FileInfo fileInfo = bucketManager.stat(bucketName, key);
            LOG.debug(fileInfo.md5);
            // todo 判断方式
            return StringUtils.isNotBlank(fileInfo.md5);
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
        return false;
    }
}
