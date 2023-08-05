/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.gov.entity.PasswordEntity;


/**
 * 密码修改记录业务
 * @author 铭软科技
 * 创建日期：2021-3-12 15:06:18<br/>
 * 历史修订：<br/>
 */
public interface IPasswordBiz extends IBaseBiz<PasswordEntity> {


    /**
     * 密码3个月提示时间判断
     * @return true 需要修改密码， false 不需要
     */
    boolean modifyPasswordForFirst();

    /**
     * 密码3个月提示时间判断
     * @return 大于0表示需要修改密码
     */
    int modifyPasswordForTime();

    /**
     * 更新口令多少次内不能重复
     * @param number 次数
     * @param passwordEntity 密码修改记录
     * @return 是否重复
     */
    boolean modifyPasswordForRepeat(int number, PasswordEntity passwordEntity, String newPassWord);

}
