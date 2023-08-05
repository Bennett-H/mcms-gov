/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.aop;

import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.OperatorTypeEnum;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.springframework.stereotype.Component;

/**
 *
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2019/11/20 10:28
 */
@Component
public class SystemLogAop extends BaseLogAop{


    @Override
    public String getUserName() {
        //后台用户获取用户名
        ManagerEntity managerSession = BasicUtil.getManager();
        if(managerSession==null) {
            return "";
        }
        return managerSession.getManagerName();
    }

    @Override
    public boolean isCut(LogAnn log) {
        //只有后台用户操作才走这个AOP
        return log.operatorType() == OperatorTypeEnum.MANAGE;
    }
}
