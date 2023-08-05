/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */











package net.mingsoft.base.biz.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.util.SqlInjectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @ClassName:  BaseAction
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 铭飞开发团队
 * @date:   2018年3月19日 下午3:28:27
 *
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
public abstract class BaseBizImpl<M extends BaseMapper<T>,T> extends ServiceImpl<M,T> implements IBaseBiz<T> {


	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Override
	public int saveEntity(BaseEntity entity) {
		return getDao().saveEntity(entity);
	}

	@Override
	public void deleteEntity(int id) {
		// TODO Auto-generated method stub
		getDao().deleteEntity(id);
	}

	@Override
	public void updateEntity(BaseEntity entity) {

		// TODO Auto-generated method stub
		getDao().updateEntity(entity);
	}

	@Override
	public List<T> queryAll() {
		// TODO Auto-generated method stub
		return getDao().queryAll();
	}



	@Override
	@Deprecated
	public int queryCount() {
		return getDao().queryCount();
	}

	@Override
	public BaseEntity getEntity(int id) {
		return getDao().getEntity(id);
	}

	@Override
	public List queryBySQL(String table, List fields, Map wheres, Integer begin, Integer end) {
		SqlInjectionUtil.filterContent(table);
		// TODO Auto-generated method stub
		return getDao().queryBySQL(table, fields, wheres,null,  begin, end, null,null);
	}

	@Override
	public int countBySQL(String table, Map wheres) {
		SqlInjectionUtil.filterContent(table);
		// TODO Auto-generated method stub
		return getDao().countBySQL(table, wheres, null);
	}

	@Override
	public int countBySQL(String table, Map wheres, List<Map> sqlWhereList) {
		SqlInjectionUtil.filterContent(table);
		//对高级搜索进行了注入过滤
		if(sqlWhereList!=null) {
			sqlWhereList.forEach(map->{
				SqlInjectionUtil.filterContent(map.get("field")+"");
			});
		}

		return getDao().countBySQL(table, wheres, sqlWhereList);
	}

	@Override
	public List queryBySQL(String table, List fields, Map wheres) {
		SqlInjectionUtil.filterContent(table);
		// TODO Auto-generated method stub
		return getDao().queryBySQL(table, fields, wheres, null,null,  null, null,null);
	}
	@Override
	public List queryBySQL(String table, List<String> fields, Map wheres, List<Map>sqlWhereList) {
		SqlInjectionUtil.filterContent(table);
		//对高级搜索进行了注入过滤
		if(sqlWhereList!=null) {
			sqlWhereList.forEach(map->{
				SqlInjectionUtil.filterContent(map.get("field")+"");
			});
		}
		// TODO Auto-generated method stub
		return getDao().queryBySQL(table, fields, wheres,sqlWhereList,null,  null, null,null);
	}
	@Override
	public List queryBySQL(String table, List<String> fields, Map wheres, List<Map>sqlWhereList, Integer begin, Integer end) {
		SqlInjectionUtil.filterContent(table);
		//对高级搜索进行了注入过滤
		if(sqlWhereList!=null) {
			sqlWhereList.forEach(map->{
				SqlInjectionUtil.filterContent(map.get("field")+"");
			});
		}
		// TODO Auto-generated method stub
		return getDao().queryBySQL(table, fields, wheres,sqlWhereList,  begin, end, null,null);
	}
	@Override
	public List queryBySQL(String table, List<String> fields, Map wheres, List<Map>sqlWhereList,String orderBy,String order) {
		SqlInjectionUtil.filterContent(table);
		//对高级搜索进行了注入过滤
		if(sqlWhereList!=null) {
			sqlWhereList.forEach(map->{
				SqlInjectionUtil.filterContent(map.get("field")+"");
			});
		}
		// TODO Auto-generated method stub
		return getDao().queryBySQL(table, fields, wheres,sqlWhereList,  null, null, orderBy,order);
	}
	@Override
	public List queryBySQL(String table, List<String> fields, Map wheres, List<Map>sqlWhereList, Integer begin, Integer end ,String orderBy,String order) {
		SqlInjectionUtil.filterContent(table);
		//对高级搜索进行了注入过滤
		if(sqlWhereList!=null) {
			sqlWhereList.forEach(map->{
				SqlInjectionUtil.filterContent(map.get("field")+"");
			});
		}
		// TODO Auto-generated method stub
		return getDao().queryBySQL(table, fields, wheres,sqlWhereList,  begin, end, orderBy,order);
	}
	@Override
	public void updateBySQL(String table, Map fields, Map wheres) {
		SqlInjectionUtil.filterContent(table);
		// TODO Auto-generated method stub
		getDao().updateBySQL(table, fields, wheres);
	}

	@Override
	public void deleteBySQL(String table, Map wheres) {
		SqlInjectionUtil.filterContent(table);
		// TODO Auto-generated method stub
		getDao().deleteBySQL(table, wheres);
	}

	@Override
	public void insertBySQL(String table, Map fields) {
		SqlInjectionUtil.filterContent(table);
		// TODO Auto-generated method stub
		getDao().insertBySQL(table, fields);
	}

	@Override
	public void createTable(String table, Map fileds) {
		SqlInjectionUtil.filterContent(table);
		// TODO Auto-generated method stub
		getDao().createTable(table, fileds);
	}


	@Override
	public void dropTable(String table) {
		SqlInjectionUtil.filterContent(table);
		// TODO Auto-generated method stub
		getDao().dropTable(table);
	}

	@Override
	public Object excuteSql(String sql) {
		// TODO Auto-generated method stub
		return getDao().excuteSql(sql);
	}

	@Override
	public void alterTable(String table, Map fileds, String type) {
		SqlInjectionUtil.filterContent(table);
		getDao().alterTable(table, fileds, type);
	}

	/**
	 * 不需要重写此方法，自动会
	 *
	 * @return
	 */
	protected abstract IBaseDao<T> getDao();

	@Override
	public void saveBatch(List list) {
		getDao().saveBatch(list);
	}

	@Override
	public void updateCache(){
		getDao().updateCache();
	}
	@Override
	public void delete(int[] ids) {
		getDao().delete(ids);
	}
	@Override
	public void delete(String[] ids) {
		getDao().delete(ids);
	}

	@Override
	public void deleteEntity(BaseEntity entity) {
		// TODO Auto-generated method stub
		getDao().deleteByEntity(entity);
	}

	@Override
	public T getEntity(BaseEntity entity) {
		// TODO Auto-generated method stub
		return getDao().getByEntity(entity);
	}

	@Override
	public List<T> query(BaseEntity entity) {
		// TODO Auto-generated method stub
		return getDao().query(entity);
	}

	@Override
	public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
		return super.getOne(queryWrapper, throwEx);
	}


}
