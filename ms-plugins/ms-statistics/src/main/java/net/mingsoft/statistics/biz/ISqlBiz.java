/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.statistics.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.statistics.bean.SqlBean;
import net.mingsoft.statistics.entity.SqlEntity;

import java.util.List;
import java.util.Map;


/**
 * 统计业务
 * @author 铭软科技
 * 创建日期：2021-1-15 9:32:36<br/>
 * 历史修订：<br/>
 */
public interface ISqlBiz extends IBaseBiz<SqlEntity> {

    /**
     * 返回多个统计结果
     * @param sqlBeans
     * @return
     */
    Map<String, List<Map<String, Object>>> results(List<SqlBean> sqlBeans);

}
