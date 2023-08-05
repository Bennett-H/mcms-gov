/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.basic.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import net.mingsoft.base.constant.Const;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.action.BaseFileAction;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.base.exception.BusinessException;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 验证zip是否合法的aop
 * 重写basic增加自定义配置
 */
@Component
@Aspect
public class FileVerifyAop extends BaseAop{

    private static final Logger LOGGER = LoggerFactory.getLogger(FileVerifyAop.class);


    /**
     * 切入点
     */
    @Pointcut("execution(* net.mingsoft.basic.action.ManageFileAction.upload(..)) || " +
            "execution(* net.mingsoft.basic.action.ManageFileAction.uploadTemplate(..))")
    public void uploadPointCut(){}


    /**
     * 后台上传文件的时候，将验证zip里的文件
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Before("uploadPointCut()")
    public void uploadAop(JoinPoint joinPoint) throws Throwable {
        UploadConfigBean bean = super.getType(joinPoint, UploadConfigBean.class);
        //检查文件类型
        String uploadFileName = FileNameUtil.cleanInvalid(bean.getFile().getOriginalFilename());
        if (StringUtils.isBlank(uploadFileName)) {
            throw new BusinessException("文件名不能为空!");
        }
        String mimeType = FileUtil.getSuffix(bean.getFile().getOriginalFilename());
        //校验压缩包里的文件
        if ("zip".equalsIgnoreCase(mimeType)){
            try {
                checkZip(bean.getFile(),false);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new BusinessException(e.getMessage());
            }
        }
    }

    /**
     * web上传文件的时候，将验证zip里的文件
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Before("execution(* net.mingsoft.basic.action.web.FileAction.upload(..))")
    public void webUploadAop(JoinPoint joinPoint) throws Throwable {
        UploadConfigBean bean = super.getType(joinPoint, UploadConfigBean.class);
        //检查文件类型
        String uploadFileName = FileNameUtil.cleanInvalid(bean.getFile().getOriginalFilename());
        if (StringUtils.isBlank(uploadFileName)) {
            throw new BusinessException("文件名不能为空!");
        }
        //文件的真实类型
        String mimeType = FileUtil.getSuffix(bean.getFile().getOriginalFilename());
        //校验压缩包里的文件
        if ("zip".equalsIgnoreCase(mimeType)){
            try {
                checkZip(bean.getFile(),true);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new BusinessException(e.getMessage());
            }
        }
    }


    /**
     * 检查压缩包
     */
    private void checkZip(MultipartFile multipartFile, boolean isWeb) throws Exception{
        //创建临时解压文件夹
        File tempFilePath = FileUtil.mkdir(FileUtil.getTmpDirPath()+"/Zip"+IdUtil.simpleUUID());
        File zipFile = FileUtil.file(tempFilePath.getAbsolutePath() + "/" + IdUtil.simpleUUID() +".zip");
        InputStream inputStream = multipartFile.getInputStream();
        FileUtils.copyInputStreamToFile(inputStream, zipFile);

        try {
            unzip(zipFile,tempFilePath.getAbsolutePath());
            //获取文件夹下所有文件
            List<File> files = FileUtil.loopFiles(tempFilePath);
            //移除压缩包自身
            files.remove(zipFile);

            //禁止上传的格式
            List<String> deniedList = Arrays.stream(MSProperties.upload.denied.split(",")).map(String::toLowerCase).collect(Collectors.toList());

            Map<String,String> map = ConfigUtil.getMap("文件上传配置");
            //允许上传的格式
            List<String> types = new ArrayList<>();
            //是否是web层的上传
            if (isWeb){
                String[] fileTypes = ConfigUtil.getString("文件上传配置","webFileType","").split(",");
                CollUtil.addAll(types,fileTypes);

            }else if (CollUtil.isNotEmpty(map)){
                String[] imageTypes = ConfigUtil.getString("文件上传配置","imageType","").split(",");
                String[] videoTypes = ConfigUtil.getString("文件上传配置","videoType","").split(",");
                String[] fileTypes = ConfigUtil.getString("文件上传配置","fileType","").split(",");
                CollUtil.addAll(types,imageTypes);
                CollUtil.addAll(types,videoTypes);
                CollUtil.addAll(types,fileTypes);
            }
            //统一转换成大写
            types = types.stream().map(String::toLowerCase).collect(Collectors.toList());
            for (File file : files) {
                //文件的真实类型
                String fileType = FileTypeUtil.getType(file);

                //上传配置为空,通过yml的配置检查文件格式是否合法
                if(CollUtil.isEmpty(map)){
                   if (deniedList.contains(fileType)){
                        throw new BusinessException(StrUtil.format("压缩包内文件{}的类型{}禁止上传",file.getName(),fileType));
                    }
                }else {

                    if (!types.contains(fileType)){
                        throw new BusinessException(StrUtil.format("压缩包内文件{}的类型{}禁止上传",file.getName(),fileType));
                    }

                }
            }
            //删除临时文件
            zipFile.delete();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
    }


    /**
     * 解压压缩包
     * @param file
     * @param descDir
     * @throws IOException
     */
    private  void unzip(File file, String descDir) throws IOException {
        ZipArchiveInputStream inputStream = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(file)));
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipArchiveEntry entry = null;
        while ((entry = inputStream.getNextZipEntry()) != null) {
            //校验路径
            if (entry.getName() == null && (entry.getName().contains("../") || entry.getName().contains("..\\"))) {
                throw new BusinessException(BundleUtil.getString(Const.RESOURCES,"err.error",BundleUtil.getString(net.mingsoft.basic.constant.Const.RESOURCES,"file.name")));
            }
            String[] dirs = entry.getName().split("/");
            String tempDir = descDir;
            for(String dir:dirs) {
                if(dir.indexOf(".")==-1) {
                    tempDir += File.separator.concat(dir);
                    FileUtil.mkdir(tempDir);
                }
            }
            if (entry.isDirectory()) {
                File directory = new File(descDir, entry.getName());
                directory.mkdirs();
            } else {
                OutputStream os = null;
                try {
                    LOGGER.debug("file name => {}",entry.getName());
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(new File(descDir, entry.getName())));
                        //输出文件路径信息
                        IOUtils.copy(inputStream, os);
                    } catch (FileNotFoundException e) {
                        LOGGER.error("解压{}不存在",entry.getName());
                        e.printStackTrace();

                    }
                } finally {
                    IOUtils.closeQuietly(os);
                }
            }
        }
        //IOUtils.closeQuietly(inputStream);
    }
}
