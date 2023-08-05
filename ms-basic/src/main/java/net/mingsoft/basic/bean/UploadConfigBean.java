/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.basic.bean;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: mingsoft
 * @Description: 统一上传bean对象
 * @Date: Create in 2023/04/07 9:55 代替基础上传的内置类
 */
public class UploadConfigBean {

    /**
     * 上传文件夹
     */
    private String uploadPath;

    private MultipartFile file;

    /**
     * 文件重命名
     */
    private boolean rename = true;
    /**
     * 上传地址拼接appId
     */
    private boolean appId = false;

    /**
     * 上传根目录，由业务决定
     */
    private String rootPath;
    /**
     * 是否重定向到项目目录,针对老版本兼容的临时处理
     */
    private boolean uploadFolderPath;

    /**
     * 文件MD5标识符
     */
    private String fileIdentifier;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件大小
     */
    private long fileSize;

    public UploadConfigBean() {
    }

    public UploadConfigBean(String fileName, String rootPath) {
        this.rootPath = rootPath;
    }

    public UploadConfigBean(String uploadPath, MultipartFile file, String rootPath, boolean uploadFolderPath) {
        this.rootPath = rootPath;
        this.uploadFolderPath = uploadFolderPath;
        this.setUploadPath(uploadPath);
        this.setFile(file);
    }

    public UploadConfigBean(String uploadPath, MultipartFile file, String rootPath) {
        this.rootPath = rootPath;
        this.setUploadPath(uploadPath);
        this.setFile(file);
    }

    public UploadConfigBean(String uploadPath, MultipartFile file, String rootPath, boolean uploadFolderPath, boolean rename) {
        this.rootPath = rootPath;
        this.uploadFolderPath = uploadFolderPath;
        this.setUploadPath(uploadPath);
        this.setFile(file);
        this.setRename(rename);
    }

    public UploadConfigBean(String uploadPath, MultipartFile file, String rootPath, boolean uploadFolderPath, boolean rename, String fileIdentifier, String fileName, long fileSize) {
        this.uploadPath = uploadPath;
        this.file = file;
        this.rename = rename;
        this.rootPath = rootPath;
        this.uploadFolderPath = uploadFolderPath;
        this.fileIdentifier = fileIdentifier;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public boolean isAppId() {
        return appId;
    }

    public void setAppId(boolean appId) {
        this.appId = appId;
    }

    public boolean isRename() {
        return rename;
    }

    public void setRename(boolean rename) {
        this.rename = rename;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public boolean isUploadFolderPath() {
        return uploadFolderPath;
    }

    public void setUploadFolderPath(boolean uploadFolderPath) {
        this.uploadFolderPath = uploadFolderPath;
    }

    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
