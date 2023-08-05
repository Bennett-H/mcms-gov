/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.people.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

/**
 * 会员日志实体
 *
 * @author 铭软科技
 * 创建日期：2023-6-6 11:51:32<br/>
 * 历史修订：<br/>
 */
@TableName("PEOPLE_LOG")
public class PeopleLogEntity extends BaseEntity {

    private static final long serialVersionUID = 1686023492415L;


    /**
     * 雪花ID规则
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 会员编号
     */
    private String peopleId;

    /**
     * 标题
     */
    private String logTitle;

    /**
     * IP
     */
    private String logIp;

    /**
     * 所在地区
     */
    private String logAddr;

    /**
     * 日志类型
     */

    private String logType;

    /**
     * 日志状态
     */

    private String logStatus;

    /**
     * 请求参数
     */

    private String logParam;

    /**
     * 返回参数
     */

    private String logResult;

    /**
     * 日志信息
     */

    private String logInfo;

    /**
     * 错误消息
     */

    private String logErrorMsg;

    /**
     * 设置标题
     */
    public void setLogTitle(String logTitle) {
        this.logTitle = logTitle;
    }

    /**
     * 获取标题
     */
    public String getLogTitle() {
        return this.logTitle;
    }

    /**
     * 设置会员编号
     */
    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    /**
     * 获取会员编号
     */
    public String getPeopleId() {
        return this.peopleId;
    }

    /**
     * 设置IP
     */
    public void setLogIp(String logIp) {
        this.logIp = logIp;
    }

    /**
     * 获取IP
     */
    public String getLogIp() {
        return this.logIp;
    }

    /**
     * 设置所在地区
     */
    public void setLogAddr(String logAddr) {
        this.logAddr = logAddr;
    }

    /**
     * 获取所在地区
     */
    public String getLogAddr() {
        return this.logAddr;
    }

    /**
     * 设置日志类型
     */
    public void setLogType(String logType) {
        this.logType = logType;
    }

    /**
     * 获取日志类型
     */
    public String getLogType() {
        return this.logType;
    }

    /**
     * 设置日志状态
     */
    public void setLogStatus(String logStatus) {
        this.logStatus = logStatus;
    }

    /**
     * 获取日志状态
     */
    public String getLogStatus() {
        return this.logStatus;
    }

    /**
     * 设置请求参数
     */
    public void setLogParam(String logParam) {
        this.logParam = logParam;
    }

    /**
     * 获取请求参数
     */
    public String getLogParam() {
        return this.logParam;
    }

    /**
     * 设置返回参数
     */
    public void setLogResult(String logResult) {
        this.logResult = logResult;
    }

    /**
     * 获取返回参数
     */
    public String getLogResult() {
        return this.logResult;
    }

    /**
     * 设置日志信息
     */
    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    /**
     * 获取日志信息
     */
    public String getLogInfo() {
        return this.logInfo;
    }

    /**
     * 设置错误消息
     */
    public void setlogErrorMsg(String logErrorMsg) {
        this.logErrorMsg = logErrorMsg;
    }

    /**
     * 获取错误消息
     */
    public String getlogErrorMsg() {
        return this.logErrorMsg;
    }


}
