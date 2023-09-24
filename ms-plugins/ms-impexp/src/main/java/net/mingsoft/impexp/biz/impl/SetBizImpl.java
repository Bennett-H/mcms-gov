/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.impexp.entity.SetEntity;
import net.mingsoft.impexp.biz.ISetBiz;
import net.mingsoft.impexp.dao.ISetDao;

/**
 * 导入导出配置管理持久化层
 * @author 铭软科技
 * 创建日期：2021-2-2 17:35:57<br/>
 * 历史修订：<br/>
 */
 @Service("impexpsetBizImpl")
public class SetBizImpl extends BaseBizImpl<ISetDao,SetEntity> implements ISetBiz {

	
	@Autowired
	private ISetDao setDao;
	
	
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return setDao;
	} 
}
