/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.dao;

import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.mdiy.entity.ModelEntity;
import org.springframework.stereotype.Component;

/**
 * 自定义模型持久层
 * @author SMILE
 * 创建日期：2019-11-7 10:48:00<br/>
 * 历史修订：<br/>
 */
@Component("mdiyModelDao")
public interface IModelDao extends IBaseDao<ModelEntity>  {
}
