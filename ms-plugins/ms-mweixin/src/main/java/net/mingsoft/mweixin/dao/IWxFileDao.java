/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.dao;

import net.mingsoft.mweixin.entity.FileEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;

import net.mingsoft.base.dao.IBaseDao;

import java.util.List;

/**
 * 微信文件表持久层
 * @author 铭飞开发团队
 * 创建日期：2018-12-29 9:18:10<br/>
 * 历史修订：<br/>
 */
@Controller("wxFileDao")
public interface IWxFileDao extends IBaseDao<FileEntity> {
    List<FileEntity> getEntitys(@Param("ids") int[] ids);
}
