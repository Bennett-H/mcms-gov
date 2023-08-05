/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.dao;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mdiy.entity.DictEntity;

/**
 * 字典表持久层
 * @author 铭飞开发团队
 * @version
 * 版本号：1.0.0<br/>
 * 创建日期：2016-9-8 17:11:19<br/>
 * 历史修订：<br/>
 */
public interface IDictDao extends IBaseDao<DictEntity> {

    List<DictEntity> dictType(DictEntity dictEntity);

    /**
     * 使用站群排除appId拼接问题
     * @param dictEntity
     * @return
     */
    //@SqlParser(filter = true)
    @InterceptorIgnore(tenantLine = "1")
    List<DictEntity> queryExcludeApp(DictEntity dictEntity);

}
