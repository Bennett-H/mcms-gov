/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mdiy.biz.IDictBiz;
import net.mingsoft.mdiy.dao.IDictDao;
import net.mingsoft.mdiy.entity.DictEntity;

import java.util.List;


/**
 * 字典表管理持久化层
 * @author 铭飞开发团队
 * @version
 * 版本号：1.0.0<br/>
 * 创建日期：2016-9-8 17:11:19<br/>
 * 历史修订：<br/>
 */
 @Service("dictBizImpl")
public class DictBizImpl extends BaseBizImpl<IDictDao, DictEntity> implements IDictBiz {


	@Autowired
	private IDictDao dictDao;


		@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return dictDao;
	}


	@Override
	public DictEntity getByTypeAndLabelAndValue(String dictType, String dictLabel , String dictValue) {
		DictEntity dict = new DictEntity();
		dict.setDictType(dictType);
		dict.setDictLabel(dictLabel);
		dict.setDictValue(dictValue);
		return (DictEntity) dictDao.getByEntity(dict);
	}

    @Override
    public List<DictEntity> dictType(DictEntity dictEntity)
    {
        return dictDao.dictType(dictEntity);
    }

	@Override
	public List queryExcludeApp(DictEntity dictEntity) {
		return dictDao.queryExcludeApp(dictEntity);
	}
}
