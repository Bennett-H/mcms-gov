/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.biz;

import net.mingsoft.base.biz.IBaseBiz;

import java.util.List;
import java.util.Map;


/**
 * 自定义模型数据
 * @author 铭软团队
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
public interface IModelDataBiz extends IBaseBiz {


    /**
     * 保存自定义表单的数据
     * @param modelId 模型编号
     * @param params　参数值集合
     */
    boolean saveDiyFormData(String modelId, Map<String, Object> params);

    /**
     * 更新自定义表单的数据
     * @param modelId 模型编号
     * @param params　参数值集合
     */
    boolean updateDiyFormData(String modelId, Map<String, Object> params);


    /**
     * 查询自定义表单的列表数据
     * @param modelId　模型编号
     * @param map wheres查询条件map
     * @return 返回map fileds:字段列表 list:记录集合
     */
    List queryDiyFormData(String modelId, Map<String, Object> map);

    /**
     * 查询自定义表单的对象数据
     * @param modelId　模型编号
     * @param id 主键编号
     * @return 返回表单对象
     */
    Object getFormData(String modelId,String id);

    /**
     * 删除记录
     * @param id　记录编号
     * @param diyFormId 表单编号
     */
    void deleteQueryDiyFormData(int id, String diyFormId);

    /**
     * 查询总数
     * @param modelId 模型编号
     * @return 返回查询总数
     */
    int countDiyFormData(String modelId, Map<String, Object> params);

}
