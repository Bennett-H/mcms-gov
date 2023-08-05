/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.people.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.people.biz.IPeopleLogBiz;
import net.mingsoft.people.dao.IPeopleLogDao;
import net.mingsoft.people.entity.PeopleLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.dao.IBaseDao;

/**
 * 会员日志管理持久化层
 * @author 铭软科技
 * 创建日期：2023-6-5 17:34:20<br/>
 * 历史修订：<br/>
 */
@Service("mpeoplepeopleLogBizImpl")
public class PeopleLogBizImpl extends BaseBizImpl<IPeopleLogDao, PeopleLogEntity> implements IPeopleLogBiz {


    @Autowired
    private IPeopleLogDao peopleLogDao;


    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return peopleLogDao;
    }


}
