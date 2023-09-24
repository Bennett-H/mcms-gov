/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.qa.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.qa.entity.QaEntity;
import org.springframework.stereotype.Component;

/**
 * 自定义模型持久层
 * @author SMILE
 * 创建日期：2019-11-7 10:48:00<br/>
 * 历史修订：<br/>
 */
@Component("qaDao")
public interface IQaDao extends IBaseDao<QaEntity>  {
}
