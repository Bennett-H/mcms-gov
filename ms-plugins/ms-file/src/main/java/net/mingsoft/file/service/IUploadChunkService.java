/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.file.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.service.IUploadBaseService;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.file.constant.Const;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分片上传抽象类,其他存储方式继承该类即可
 */
public abstract class IUploadChunkService extends IUploadBaseService {

    protected static final Logger LOG = LoggerFactory.getLogger(IUploadChunkService.class);
    
    /**
     * 分片缓存,以总文件MD5作为key
     */
    private static Map<String, ChunkBean> fileCache = new ConcurrentHashMap<>();



    /**
     *
     * @param bean UploadConfigBean
     * @return
     */
    @Override
    public ResultData upload(UploadConfigBean bean) {
        LOG.debug("分片上传实现");

        String fileIdentifier = bean.getFileIdentifier();
        long totalFileSize = bean.getFileSize();
        String fileName = bean.getFileName();
        MultipartFile file = bean.getFile();
        String _uploadPath = bean.getUploadPath();

        // 上传路径
        String uploadPath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadPath", "upload");
        // 判断配置的上传路径是否绝对路径
        boolean isReal = new File(uploadPath).isAbsolute();
        String uploadMapping = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadMapping", "/upload/**");

        ChunkBean cacheBean = fileCache.get(fileIdentifier);

        String newName = "";
        String _fileMD5 = null;
        // 分片文件夹
        String uploadFolder = "";
        long _fileSize = file.getSize();


        ResultData resultData;
        try {
            _fileMD5 = String.valueOf(Hex.encodeHex(MessageDigest.getInstance("MD5").digest(file.getBytes())));
            // 1.设置缓存数据
            if (ObjectUtils.isEmpty(cacheBean)){
                // 修改文件名
                if (bean.isRename()) {
                    newName = System.currentTimeMillis() + "." + FileUtil.getSuffix(fileName);
                } else {
                    newName = fileName;
                }
                ArrayList<String> chunkMD5s = new ArrayList<>();
                chunkMD5s.add(_fileMD5);
                cacheBean = new ChunkBean(1,_fileSize,newName,chunkMD5s);
                // 初始化缓存
                fileCache.put(fileIdentifier,cacheBean);
            } else {
                // 该切片已经上传过了
                if (CollUtil.isNotEmpty(cacheBean.getChunkMD5s()) && cacheBean.getChunkMD5s().contains(_fileMD5)){
                    // 返回206信息及已经上传的分片数
                    return ResultData.build().code(HttpStatus.PARTIAL_CONTENT).data(cacheBean.getChunkNumber());
                }
                // 更新缓存
                cacheBean.setChunkNumber(cacheBean.getChunkNumber()+1);
                cacheBean.getChunkMD5s().add(_fileMD5);
                cacheBean.setTotalFileSize(cacheBean.getTotalFileSize() + _fileSize);
                fileCache.put(fileIdentifier,cacheBean);
            }

            // 切片文件名处理,以原文件加上中横杠加切片编号拼接而成
            String prefixName = FileUtil.getPrefix(cacheBean.getFileName());
            String chunkName = prefixName+"-"+ cacheBean.getChunkNumber() + "." + FileUtil.getSuffix(cacheBean.getFileName());

            String tempPth = FileUtil.getTmpDirPath();
            // 上传的分片文件路径以文件MD5作为文件夹区分
            uploadFolder = tempPth + File.separator + fileIdentifier + File.separator;
            // todo 2.保存分片文件到临时目录
            File saveFolder = new File(uploadFolder);
            File saveFile = new File(uploadFolder, chunkName);
            if (!saveFolder.exists()) {
                FileUtil.mkdir(saveFolder);
            }
            file.transferTo(saveFile);
            resultData = ResultData.build().code(HttpStatus.PARTIAL_CONTENT).data(cacheBean.getChunkNumber());
            if (cacheBean.getTotalFileSize() > totalFileSize){
                LOG.error("cache error，totalFilesize Not equal to the source file size");
                cacheBean.clearCache(fileIdentifier);
                resultData = ResultData.build().error("非法文件上传");
            }
            // 保存成功后检测分片是否上传完毕
            if (cacheBean.getTotalFileSize() == totalFileSize){
                LOG.debug("分片上传完毕");
                long size = ConfigUtil.getInt(Const.CONFIG_UPLOAD, "fileSize", 200);
                if (cacheBean.getTotalFileSize() > (size*1048576)){
                    throw new BusinessException("文件大小不可超过："+size+"MB");
                }
                // todo 3.合并分片成完整文件
                // 合并分片
                // 切割得到主文件名称及扩展名
                String suffixName = FileUtil.getSuffix(cacheBean.getFileName());
                String tempChunkPath = FileUtil.getTmpDirPath() + File.separator + fileIdentifier;
                // 目标存放的目录位置,以当前日期来区分同目录下的文件
                String uploadRealPath = isReal ? uploadPath : BasicUtil.getRealPath(uploadPath);
                String fileTargetPath = uploadRealPath + File.separator + _uploadPath;

                // 检测目标目录是否存在
                File targetDir = FileUtil.file(fileTargetPath);
                if (!targetDir.exists()){
                    FileUtil.mkdir(targetDir);
                    LOG.debug("mkdir：{}",fileTargetPath);
                }

                // 若目标文件存在则先删除文件
                String targetFile = fileTargetPath + File.separator + newName;
                if (FileUtil.file(targetFile).exists()) {
                    FileUtil.file(targetFile).delete();
                    LOG.debug("Overwrite target file: {}",targetFile);
                }

                // 预定义文件输出流，用于组装分片数据
                BufferedOutputStream destOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
                // 循环读取分片文件写出
                for (int i = 1; i <= cacheBean.getChunkNumber(); i++) {
                    File chunk = FileUtil.file(tempChunkPath+File.separator+prefixName+"-"+i+"."+suffixName);// 校验分片存储位置是否有文件
                    if (!chunk.exists()){
                        throw new BusinessException("文件: "+tempChunkPath+File.separator+prefixName+"-"+i+"."+suffixName+"不存在");
                    }
                    // 循环将每个分片的数据写入目标文件
                    byte[] fileBuffer = new byte[1024];// 文件读写缓存
                    int readBytesLength = 0; // 每次读取字节数
                    BufferedInputStream chunkFileStream = new BufferedInputStream(new FileInputStream(chunk));
                    // 写入文件
                    while ((readBytesLength = chunkFileStream.read(fileBuffer))!=-1){
                        destOutputStream.write(fileBuffer, 0 , readBytesLength);
                    }
                    // 关闭资源
                    chunkFileStream.close();
                }
                // 关闭输出流
                destOutputStream.flush();
                destOutputStream.close();

                // 替换成符合我们格式
                targetFile = uploadMapping.replace("**","") + targetFile.replace(uploadRealPath,"").replace("\\","/").replace("//","/");
                // todo aop已处理数据插入
                resultData = ResultData.build().code(HttpStatus.OK).data(new File(targetFile).getPath().replace("\\","/").replace("//","/"));
                // 清空分片临时存储文件夹
                if (FileUtil.file(uploadFolder).exists()) FileUtil.del(uploadFolder);
                // 删除分片记录
                fileCache.remove(fileIdentifier);
                Log.get(IUploadBaseService.class).debug("Upload success，fileCache：{}",fileCache);
            }
        } catch (Exception e){
            // 清空分片临时存储文件夹
            if (FileUtil.file(uploadFolder).exists()) FileUtil.del(uploadFolder);
            // 删除分片记录
            fileCache.remove(fileIdentifier);
            Log.get(IUploadBaseService.class).debug("Upload fail，fileCache：{}",fileCache);
            e.printStackTrace();
            resultData = ResultData.build().error("上传失败");
        }
        return resultData;
    }

    /**
     * 分片缓存bean，仅在本类使用
     */
    class ChunkBean {

        // 切片编号
        private int chunkNumber;

        // 切片总大小
        private long totalFileSize;

        // 切片新文件名
        private String FileName;

        // 切片MD5集合
        private List<String> chunkMD5s;

        public ChunkBean(int chunkNumber, long totalFileSize, String fileName, List<String> chunkMD5s) {
            this.chunkNumber = chunkNumber;
            this.totalFileSize = totalFileSize;
            FileName = fileName;
            this.chunkMD5s = chunkMD5s;
        }

        public long getTotalFileSize() {
            return totalFileSize;
        }

        public void setTotalFileSize(long totalFileSize) {
            this.totalFileSize = totalFileSize;
        }

        public String getFileName() {
            return FileName;
        }

        public void setFileName(String fileName) {
            FileName = fileName;
        }

        public int getChunkNumber() {
            return chunkNumber;
        }

        public void setChunkNumber(int chunkNumber) {
            this.chunkNumber = chunkNumber;
        }

        public List<String> getChunkMD5s() {
            return chunkMD5s;
        }

        public void setChunkMD5s(List<String> chunkMD5s) {
            this.chunkMD5s = chunkMD5s;
        }

        public void clearCache(String fileMD5){
            if (StringUtils.isBlank(fileMD5)) {
                fileCache.clear();
            } else {
                fileCache.remove(fileMD5);
            }
        }
    }
}
