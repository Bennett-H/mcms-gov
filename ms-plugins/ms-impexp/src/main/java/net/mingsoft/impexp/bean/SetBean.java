/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.bean;

import net.mingsoft.impexp.entity.SetEntity;

/**
 * 导入导出额外参数定义
 *
 * @author Administrator
 * @version 创建日期：2021/3/17 10:44<br/>
 * 历史修订：<br/>
 */
public class SetBean extends SetEntity {
    /**
     * SQL查询条件中的编号
     */
    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
