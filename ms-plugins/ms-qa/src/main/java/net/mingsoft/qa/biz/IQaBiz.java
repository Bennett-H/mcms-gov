/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.qa.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.mdiy.bean.ModelJsonBean;
import net.mingsoft.qa.entity.QaEntity;

import java.util.List;


/**
 * 自定义表单接口
 * @author 铭软团队
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
public interface IQaBiz extends IBaseBiz<QaEntity> {

    /**
     * 导入模型，提供自定义配置和自定义表单使用
     * @param customType 自定义类型（表单、配置）
     * @param modelJsonBean 来自代码生成器的自定义模型json转换成的bean
     * @return
     */
    boolean importConfig(ModelJsonBean modelJsonBean);

    /**
     * 导入模型，提供自定义模型
     * @param customType 自定义类型（模型）
     * @param modelJsonBean 来自代码生成器的自定义模型json转换成的bean
     * @param modelType 自定义模型类型
     * @return
     */
    boolean importModel(ModelJsonBean modelJsonBean,String modelType);


    /**
     * 更新导入模型，提供自定义配置和自定义表单使用
     * @param modelId 自定义模型编号
     * @param modelJsonBean 来自代码生成器的自定义模型json转换成的bean
     * @return
     */
    boolean updateConfig(String modelId, ModelJsonBean modelJsonBean);

    /**
     * 更新导入模型，提供自定义配置和自定义表单使用
     * @param modelId 自定义模型编号
     * @param modelJsonBean 来自代码生成器的自定义模型json转换成的bean
     * @param modelType 自定义模型类型，导入模型时候下拉选择的业务类型，如：文章类型，只能在内容管理业务使用
     * @return
     */
    boolean updateConfig(String modelId, ModelJsonBean modelJsonBean,String modelType);

    /**
     * 批量删除，并且删除表
     * @param ids
     * @return
     */
    boolean delete (List<String> ids);

}
