/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mdiy.biz.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.base.util.SqlInjectionUtil;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.biz.IModelDataBiz;
import net.mingsoft.mdiy.dao.IModelDao;
import net.mingsoft.mdiy.entity.ModelEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义表单接口实现类
 * @author 铭软
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：2022-1-21 queryDiyFormData() orderby非法参数
 */
@Service
public class ModelDataImpl extends BaseBizImpl implements IModelDataBiz {

	@Autowired
	private net.mingsoft.mdiy.biz.IModelBiz modelBiz;
	/**
	 * 注入自定义表单持久化层
	 */
	@Autowired
	private IModelDao modelDao;



	/**
	 * 获取类别持久化层
	 * @return diyFormDao 返回类别持久话层
	 */
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return modelDao;
	}

	@Override
	public boolean saveDiyFormData(String modelId, Map<String,Object> params) {
		ModelEntity model = modelBiz.getById(modelId);
		if (ObjectUtil.isNotNull(model) ) {
			Map fieldMap = model.getFieldMap();
			HashMap<String, Object> fields = new HashMap<>();
			//拼接字段
			for (String s : params.keySet()) {
				//判断是否存在此字段
				if (fieldMap.containsKey(s)) {
					fields.put(fieldMap.get(s).toString(), params.get(s));
				}
			}
			fields.put("CREATE_DATE", new Date());
			fields.put("UPDATE_DATE", new Date());
			modelBiz.insertBySQL(model.getModelTableName(), fields);
			return true;
		}else {
			return false;
		}
	}
	@Override
	public boolean updateDiyFormData(String modelId, Map<String,Object> params) {
		ModelEntity model = modelBiz.getById(modelId);
		if (ObjectUtil.isNotNull(model) ) {
			Map fieldMap = model.getFieldMap();
			HashMap<String, Object> fields = new HashMap<>();
			//拼接字段
			for (String s : params.keySet()) {
				//判断是否存在此字段
				if (fieldMap.containsKey(s)) {
					fields.put(fieldMap.get(s).toString(), params.get(s));
				}
			}
			if (StringUtils.isEmpty(params.get("id").toString())) {
				LOG.debug("请求数据不含主键id,无法更新");
				return false;
			}
			fields.put("UPDATE_DATE", new Date());
			Map<String, String> map = new HashMap<>();
			map.put("id",params.get("id").toString());
			modelBiz.updateBySQL(model.getModelTableName(), fields,map);
			return true;
		}else {
			return false;
		}
	}

	@Override
	public List queryDiyFormData(String modelId, Map<String,Object> params) {
		ModelEntity model = modelBiz.getById(modelId);
		if (ObjectUtil.isNotNull(model) ) {
			Map fieldMap = model.getFieldMap();
			HashMap<String, Object> fields = new HashMap<>();
			//拼接字段
			for (String s : params.keySet()) {
				//判断是否存在此字段
				if (fieldMap.containsKey(s)) {
					fields.put(fieldMap.get(s).toString(), params.get(s));
				}
			}
			List<Map> sqlWhereList =null;
			if(params.get("sqlWhere")!=null){
				sqlWhereList = JSONUtil.toList(params.get("sqlWhere").toString(),Map.class);
			}
			String orderby = null;
			if(params.get("orderBy") !=null){
				orderby = params.get("orderBy").toString();
			}
			String order = null;
			if(params.get("order") !=null){
				order = params.get("order").toString();
			}

			//TODO 分页插件在这使用会报错
			BasicUtil.startPage();
			//sql注入判断
			SqlInjectionUtil.filterContent(orderby);

			List list = modelBiz.queryBySQL(model.getModelTableName(), null, fields,sqlWhereList,orderby,order);
			return list;
		}
		return null;
	}

	@Override
	public Object getFormData(String modelId,String id) {
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(modelId)) {
			LOG.debug("模型或主键参数为空");
			return null;
		}
		ModelEntity model = modelBiz.getById(modelId);
		if(model==null){
			LOG.debug("模型不存在");
			return null;
		}
		HashMap<String, Object> fields = new HashMap<>();
		fields.put("id",id);
		List<Map<String, Object>> list = modelBiz.queryBySQL(model.getModelTableName(), null, fields);
		if(CollUtil.isEmpty(list)){
			return null;
		}
		HashMap<String, Object> modelEntity = new HashMap<>();
		Map<String,Object> fieldMap = model.getFieldMap();
		//拼接字段
		for (String s : list.get(0).keySet()) {
			//判断是否存在此字段
			for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
				if(s.equalsIgnoreCase(entry.getValue().toString())){
					modelEntity.put(entry.getKey(), list.get(0).get(s));
				}
			}
		}
		modelEntity.put("id",id);
		return modelEntity;
	}

	@Override
	public void deleteQueryDiyFormData(int id,String diyFormId) {
		ModelEntity model = modelBiz.getById(diyFormId);
		if (ObjectUtil.isNotNull(model) ) {
			HashMap hashMap = new HashMap();
			hashMap.put("id",id);
			modelBiz.deleteBySQL(model.getModelTableName(), hashMap);
		}
	}

	@Override
	public int countDiyFormData(String modelId,Map<String,Object> params) {
		ModelEntity model = modelBiz.getById(modelId);
		if (ObjectUtil.isNotNull(model) ) {
			HashMap fields = new HashMap();
			Map fieldMap = model.getFieldMap();
			for (String s : params.keySet()) {
				//判断是否存在此字段
				if (fieldMap.containsKey(s)) {
					fields.put(fieldMap.get(s).toString(), "'"+params.get(s)+"'");
				}
			}
			return  modelBiz.countBySQL(model.getModelTableName(), fields);
		}
		return 0;
	}



}
