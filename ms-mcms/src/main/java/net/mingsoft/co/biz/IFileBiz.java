/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.co.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.co.entity.FileEntity;

import java.util.List;

/**
 * 文件业务
 * @author 铭软科技
 * 创建日期：2021-5-21 10:55:41<br/>
 * 历史修订：<br/>
 */
public interface IFileBiz extends IBaseBiz<FileEntity> {
    /**
     * 查询指定文件夹下的所有文件
     * @param folderId 文件夹id
     * @return
     */
    List<FileEntity> listByFolderId(String folderId);
}
