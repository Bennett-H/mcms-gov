/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.statistics.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.base.util.FtlUtil;
import net.mingsoft.base.util.SqlInjectionUtil;
import net.mingsoft.statistics.bean.SqlBean;
import net.mingsoft.statistics.biz.ISqlBiz;
import net.mingsoft.statistics.dao.ISqlDao;
import net.mingsoft.statistics.entity.SqlEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计管理持久化层
 * @author 铭软科技
 * 创建日期：2021-1-15 9:32:36<br/>
 * 历史修订：<br/>
 */
 @Service("statisticssqlBizImpl")
public class SqlBizImpl extends BaseBizImpl<ISqlDao,SqlEntity> implements ISqlBiz {


	@Autowired
	private ISqlDao sqlDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return sqlDao;
	}

	@Override
	public Map<String, List<Map<String, Object>>> results(List<SqlBean> sqlBeans) {
		Map<String, List<Map<String, Object>>> result = new HashMap<>();

		sqlBeans.forEach(item -> {

			if (StringUtils.isNotBlank(item.getName())) {
				SqlEntity sqlEntity = new SqlEntity();
				sqlEntity.setSsName(item.getName());
				SqlEntity entity = super.getOne(new QueryWrapper<>(sqlEntity));
				if(entity!=null){
					String sql = entity.getSsSql();
					Map<String,Object> params = item.getParams();
//					//便利params，判断p
					if (params != null && params.size() > 0) {

						Collection<Object> values = params.values();
						for (Object value : values) {
							// sql防注入
							if (value != null){
								SqlInjectionUtil.filterContent(value.toString());
							}
						}
					}
					// freeMaker渲染
					String _sql = FtlUtil.rendering(params,sql);
					List<Map<String, Object>> list = sqlDao.excuteSql(_sql);
					if (CollUtil.isNotEmpty(list)) {
						result.put(entity.getSsName(), list);
					}else {
						result.put(entity.getSsName(), null);
					}
				}else {
					result.put("", null);
				}
			}else {
				result.put(null, null);
			}

		});
		return result;
	}
}
