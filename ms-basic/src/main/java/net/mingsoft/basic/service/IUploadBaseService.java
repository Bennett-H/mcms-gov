/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.basic.service;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.UploadConfigBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


/**
 * 抽象的上传类
 */
public abstract class IUploadBaseService {

    /**
     * 通用的上传方法
     *
     * @param bean 统一上传bean对象
     * @return 返回保存的文件路径
     */
    public abstract ResultData upload(UploadConfigBean bean);

    /**
     * 检测文件是否存在
     * @param realPath 绝对路径
     * @return 文件存在则返回true
     */
    public abstract boolean checkFileIfExist(String realPath);

}
