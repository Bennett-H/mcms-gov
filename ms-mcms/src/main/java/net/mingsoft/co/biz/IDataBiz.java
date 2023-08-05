/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.co.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.co.bean.CategoryDataBean;
import net.mingsoft.datascope.entity.DataEntity;

import java.util.List;


/**
 * 数据权限业务
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
public interface IDataBiz extends IBaseBiz{

    List<CategoryDataBean> categoryDataList(DataEntity data);

    @Deprecated
    void saveBatch(List<CategoryDataBean> datas,String targetId);

    void saveToBatch(List<CategoryDataBean> datas,String targetId, String configId);
}
