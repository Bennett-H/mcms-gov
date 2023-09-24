/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.co.dao;


import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.co.entity.FileEntity;
import org.springframework.stereotype.Component;

/**
 * 文件持久层
 * @author biusp
 * 创建日期：2021-12-16 18:34:59<br/>
 * 历史修订：<br/>
 * 2021-1-22 重写co  增加folderId字段，将file表与文件夹表进行关联
 */
@Component("fileDao")
public interface IFileDao extends IBaseDao<FileEntity> {
}
