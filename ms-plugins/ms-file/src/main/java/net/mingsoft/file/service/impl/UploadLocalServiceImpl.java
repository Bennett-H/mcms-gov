/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.file.service.impl;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.file.service.IUploadChunkService;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 本地的上传实现类
 */
@Service("uploadLocalService")
public class UploadLocalServiceImpl extends IUploadChunkService {

    @Override
    public ResultData upload(UploadConfigBean bean) {
        return super.upload(bean);
    }

    @Override
    public boolean checkFileIfExist(String realPath) {
        return new File(realPath).exists();
    }
}
