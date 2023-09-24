/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.qa.biz.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.qa.biz.IQaBiz;
import net.mingsoft.qa.biz.IQaDataBiz;
import net.mingsoft.qa.dao.IQaDao;
import net.mingsoft.qa.entity.QaEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class QaDataImpl extends BaseBizImpl implements IQaDataBiz {

	@Autowired
	private IQaBiz qaBiz;
	/**
	 * 注入自定义表单持久化层
	 */
	@Autowired
	private IQaDao qalDao;



	/**
	 * 获取类别持久化层
	 * @return diyFormDao 返回类别持久话层
	 */
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return qalDao;
	}

	@Override
	public boolean saveDiyFormData(int modelId, Map<String,Object> params) {
		QaEntity qa = qaBiz.getById(modelId);
		if (ObjectUtil.isNotNull(qa) ) {
			Map fieldMap = qa.getFieldMap();
			HashMap<String, Object> fields = new HashMap<>();
			//拼接字段
			for (String s : params.keySet()) {
				//判断是否存在此字段
				if (fieldMap.containsKey(s)) {
					fields.put(fieldMap.get(s).toString(), params.get(s));
				}
			}
			//插入额外字段
            fields.put("qa_ip",params.get("qa_IP"));
            fields.put("qa_address",params.get("qa_address"));
            fields.put("qa_device_type",params.get("qa_device_type"));
            fields.put("create_date",params.get("create_date"));
            fields.put("update_date",params.get("update_date"));
			qaBiz.insertBySQL(qa.getQaTableName(), fields);
			return true;
		}else {
			return false;
		}
	}
	@Override
	public boolean updateDiyFormData(int modelId, Map<String,Object> params) {
		QaEntity qa = qaBiz.getById(modelId);
		if (ObjectUtil.isNotNull(qa) ) {
			Map fieldMap = qa.getFieldMap();
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
			Map<String, String> map = new HashMap<>();
			map.put("id",params.get("id").toString());
			qaBiz.updateBySQL(qa.getQaTableName(), fields,map);
			return true;
		}else {
			return false;
		}
	}

	@Override
	public List queryDiyFormData(int modelId, Map<String,Object> params) {
		QaEntity qa = qaBiz.getById(modelId);
		if (ObjectUtil.isNotNull(qa) ) {
			Map fieldMap = qa.getFieldMap();
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
			//如果orderby里存在空格就有可能有子查询
			if (StringUtils.isNotEmpty(orderby) && orderby.contains(" ")){
				throw new BusinessException("非法参数!");
			}
			List list = qaBiz.queryBySQL(qa.getQaTableName(), null, fields,sqlWhereList,orderby,order);
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
		QaEntity qa = qaBiz.getById(modelId);
		if(qa==null){
			LOG.debug("模型不存在");
			return null;
		}
		HashMap<String, Object> fields = new HashMap<>();
		fields.put("id",id);
		List<Map<String, Object>> list = qaBiz.queryBySQL(qa.getQaTableName(), null, fields);
		if(CollUtil.isEmpty(list)){
			return null;
		}
		HashMap<String, Object> modelEntity = new HashMap<>();
		Map<String,Object> fieldMap = qa.getFieldMap();
		//拼接字段
		for (String s : list.get(0).keySet()) {
			//判断是否存在此字段
			for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
				if(entry.getValue().equals(s)){
					modelEntity.put(entry.getKey(), list.get(0).get(s));
				}
			}
		}
		modelEntity.put("id",id);
		return modelEntity;
	}

	@Override
	public void deleteQueryDiyFormData(int id,String diyFormId) {
		QaEntity qa = qaBiz.getById(diyFormId);
		if (ObjectUtil.isNotNull(qa) ) {
			HashMap hashMap = new HashMap();
			hashMap.put("id",id);
			qaBiz.deleteBySQL(qa.getQaTableName(), hashMap);
		}
	}

	@Override
	public int countDiyFormData(int modelId,Map<String,Object> params) {
		QaEntity qa = qaBiz.getById(modelId);
		if (ObjectUtil.isNotNull(qa) ) {
			HashMap fields = new HashMap();
			Map fieldMap = qa.getFieldMap();
			for (String s : params.keySet()) {
				//判断是否存在此字段
				if (fieldMap.containsKey(s)) {
					fields.put(fieldMap.get(s).toString(), "'"+params.get(s)+"'");
				}
			}
			return  qaBiz.countBySQL(qa.getQaTableName(), fields);
		}
		return 0;
	}



}
