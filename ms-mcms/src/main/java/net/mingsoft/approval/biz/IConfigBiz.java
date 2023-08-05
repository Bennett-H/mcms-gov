/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.approval.biz;

import net.mingsoft.approval.entity.ConfigEntity;
import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.progress.bean.ProgressLogBean;
import net.mingsoft.progress.entity.ProgressLogEntity;
import net.mingsoft.progress.entity.SchemeEntity;

import java.util.List;


/**
 * 审批配置业务，依赖进度处理模块
 * @author 铭软科技
 * 创建日期：2021-3-3 16:52:39<br/>
 * 历史修订：<br/>
 */
public interface IConfigBiz extends IBaseBiz<ConfigEntity> {

    /**
     * 审核权限校验
     * @param configBusinessType 业务类型，与进度模块的SchemeEntity.schemeName一致
     * @param managerId 管理员id
     * @param configName 审核等级，与进度模块的ProgressEntity.progressNodeName一致
     * @return true 验证通过
     *          false 验证失败
     */

    @Deprecated
    boolean auditPermissionsVerify(String configBusinessType,String managerId,String configName);

    /**
     * 审核权限校验
     * @param schemeName 业务类型，与进度模块的SchemeEntity.schemeName一致
     * @param categoryId 栏目ID
     * @param roleId 角色id
     * @param configName 审核等级，与进度模块的ProgressEntity.progressNodeName一致
     * @return true 验证通过
     *          false 验证失败
     */
    boolean auditPermissionsVerifyForRole(String schemeName, String categoryId, int roleId, String configName);

    /**
     * 审核权限校验
     * @param schemeName 业务类型，与进度模块的SchemeEntity.schemeName一致
     * @param categoryId 栏目ID
     * @param roleIds 角色id集合
     * @param configName 审核等级，与进度模块的ProgressEntity.progressNodeName一致
     * @return true 验证通过
     *          false 验证失败
     */
    boolean auditPermissionsVerifyForRoles(String schemeName, String categoryId, String roleIds, String configName);

    /**
     *  审核类型获取进度日志的dataId集合
     * @param schemeName 业务类型，与进度模块的SchemeEntity.schemeName一致
     * @param managerId 管理员编号
     * @param plStatus 进度日志节点状态   状态为空查询所有日志为null的记录
     * @return
     */
    @Deprecated
    List<String> auditList(String schemeName,String managerId,String plStatus);

    /**
     *  审核类型获取进度日志的集合
     * @param categoryIds 栏目ID集合
     * @param plStatus 审核状态
     * @return
     */
    List<ProgressLogBean> auditListForCategoryIds(List<String> categoryIds, String plStatus);

    /**
     * 方案、角色、栏目ID、等级编号查询管理员拥有的权限
     * @param schemeName 方案
     * @param roleId 角色ID
     * @param categoryId 栏目ID
     * @param progressNodeName 等级编号
     * @return
     */
    List<ConfigEntity> queryListForRole(String schemeName, String categoryId, int roleId, String progressNodeName);

    /**
     * 根据栏目ID数组删除相应的审批配置
     * @param categoryIds 栏目ID数组
     */
    void deleteByCategoryIds(List<String> categoryIds);

    /**
     * 审核并创建下一条审核记录信息
     * @param schemeEntity  方案
     * @param progressLogEntity 进度
     * @param progressId 栏目的审批级数
     */
    void approval(SchemeEntity schemeEntity,ProgressLogEntity progressLogEntity,int progressId);

    /**
     * 该数据是否已经在审核中
     * @param schemeName 方案名称
     * @param dataId 业务编号
     * @return true 存在没有审核的记录
     */
    boolean isCheck(String schemeName,String dataId);

    /**
     * 提交审核
     * @param schemeName 方案名称
     * @param dataId 业务编号
     * @param createBy 创建人
     * @return true 存在没有审核的记录
     */
    boolean submit(String schemeName,String dataId,String createBy);

}
