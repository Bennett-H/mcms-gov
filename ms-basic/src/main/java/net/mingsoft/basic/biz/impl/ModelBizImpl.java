/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.biz.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.constant.e.BaseEnum;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.biz.IRoleModelBiz;
import net.mingsoft.basic.constant.e.ModelIsMenuEnum;
import net.mingsoft.basic.dao.IModelDao;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.entity.RoleModelEntity;
import net.mingsoft.basic.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 模块业务接口实现类
 * @author 张敏
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
@Service("modelBiz")
@Transactional
public class ModelBizImpl extends BaseBizImpl<IModelDao, ModelEntity> implements IModelBiz{



	@Override
	public ModelEntity getEntityByModelCode(BaseEnum modelCode){
		// TODO Auto-generated method stub
		return modelDao.getEntityByModelCode(modelCode.toString());
	}

	@Override
	public ModelEntity getEntityByModelCode(String modelCode) {
		// TODO Auto-generated method stub
		return modelDao.getEntityByModelCode(modelCode);
	}

	/**
	 * 模块持久化层
	 */
    private IModelDao modelDao;
	@Autowired
    private IRoleModelBiz roleModelBiz;


	/**
	 * 获取模块持久化层
	 * @return modelDao 返回模块持久化层
	 */
    public IModelDao getModelDao() {
        return modelDao;
    }

    @Autowired
    public void setModelDao(IModelDao modelDao) {
        this.modelDao = modelDao;
    }

    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return modelDao;
    }

	@Override
	public ModelEntity getModel(String modelType,int modelId) {
		// TODO Auto-generated method stub
		return modelDao.getModel(modelType,modelId);
	}

	@Override
	public List<ModelEntity> queryModelByRoleId(int roleId) {
		return modelDao.queryModelByRoleId(roleId);
	}

	@Deprecated
	@Override
	@Transactional(rollbackFor = {Exception.class, Error.class})
	public void reModel(ModelEntity modelParent, String parentIds, int mangerRoleId, List<RoleModelEntity> roleModels, Integer parentId){
		//判断是否有子集，有则是菜单没有则是功能
		modelParent.setModelIsMenu(ObjectUtil.isNotNull(modelParent.getModelChildList())&&modelParent.getModelChildList().size()>0
				? Integer.valueOf(1):Integer.valueOf(0));
		//设置父级id,null不会存进数据库
		modelParent.setModelId(parentId);
		modelParent.setModelDatetime(new Timestamp(System.currentTimeMillis()));
		modelParent.setModelParentIds(parentIds);
		ModelEntity modelParentEntity = getEntityByModelCode(modelParent.getModelCode());
		if (modelParentEntity == null) {
			//判断菜单名称是否已存在
			if(modelParent.getModelIsMenu() == ModelIsMenuEnum.MODEL_MEUN.toInt()){
				ModelEntity modelEntity = new ModelEntity();
				modelEntity.setModelIsMenu(ModelIsMenuEnum.MODEL_MEUN.toInt());
				modelEntity.setModelTitle(modelParent.getModelTitle());
				modelEntity.setModelId(modelParent.getModelId());
				if(ObjectUtil.isNotEmpty(getEntity(modelEntity))){
					throw new BusinessException("菜单标题重复");//抛异常事务回滚
				}
			}
			saveEntity(modelParent);
			RoleModelEntity roleModel = new RoleModelEntity();
			roleModel.setRoleId(mangerRoleId);
			roleModel.setModelId(Integer.parseInt(modelParent.getId()));
			roleModels.add(roleModel);
		} else {
			modelParent.setId(modelParentEntity.getId());
			updateEntity(modelParent);
		}
		if(ObjectUtil.isNotNull(modelParent.getModelChildList())&&modelParent.getModelChildList().size()>0){
			for (ModelEntity modelEntity : modelParent.getModelChildList()) {
				reModel(modelEntity, StringUtils.isBlank(parentIds)?modelParent.getId():parentIds+","+modelParent.getId(),mangerRoleId,roleModels,Integer.parseInt(modelParent.getId()));
			}
		}

	}

	@Deprecated
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void jsonToModel(String menuStr,int mangerRoleId,int modelId) {
		List<RoleModelEntity> roleModels = new ArrayList<>();
		if (StringUtils.isNotBlank(menuStr))
		{
			List<ModelEntity> list = JSONUtil.toList(menuStr, ModelEntity.class);
			for (ModelEntity modelParent : list) {
				ModelEntity entity = getEntity(modelParent);
				//如果存在此父级菜单则删除重新导入
				if(entity !=null&&(entity.getModelId()==null|| entity.getModelId() ==0)){
					deleteEntity(Integer.parseInt(entity.getId()));
				}
				reModel(modelParent,null,mangerRoleId,roleModels,modelId);
			}
			// 添加角色权限
			if (roleModels.size() > 0) {
				roleModelBiz.saveBatch(roleModels, roleModels.size());
			}
		}
	}

	@Transactional(rollbackFor = {Exception.class, Error.class})
	public void importModel(ModelEntity modelEntity, int mangerRoleId, String parentIds, Integer modelId){
		List<RoleModelEntity> roleModels = new ArrayList<>();
		//1.清空ID
		modelEntity.setId(null);
		modelEntity.setModelDatetime(new Timestamp(System.currentTimeMillis()));

		//2.设置modelId 与 parentIds
		if (modelId != null && modelId != 0) {
			modelEntity.setModelId(modelId);
			modelEntity.setModelParentIds(parentIds);
		} else {
			modelEntity.setModelId(null);
			modelEntity.setModelParentIds(null);
		}
		//3.插入SQL,这里不能使用雪花ID,只能使用自增长,因为使用雪花ID改动太大
		modelDao.insert(modelEntity);

		//4.创建新的角色实体
		RoleModelEntity roleModel = new RoleModelEntity();
		roleModel.setRoleId(mangerRoleId);
		roleModel.setModelId(Integer.parseInt(modelEntity.getId()));
		roleModels.add(roleModel);
		//5.添加角色权限
		if (roleModels.size() > 0) {
			roleModelBiz.saveBatch(roleModels, roleModels.size());
		}
		//5.如果有子级就递归
		if(ObjectUtil.isNotNull(modelEntity.getModelChildList()) && modelEntity.getModelChildList().size()>0){
			for (ModelEntity curModelEntity : modelEntity.getModelChildList()) {
				// curModelEntity当前遍历model   parentIds判断是否顶级，是存Id，否则保存modelId,父级ID,
				if (StringUtils.isBlank(parentIds) || modelId == null || modelId == 0) {
					parentIds = modelEntity.getId();
				}else {
					parentIds = modelId+","+modelEntity.getId();
				}
				importModel(curModelEntity, mangerRoleId, parentIds,Integer.parseInt(modelEntity.getId()));
			}
		}
	}

	@Override
	public void updateEntity(ModelEntity model) {
		setParentId(model);
		modelDao.updateById(model);
		setChildParentId(model);
		//更新缓存
		modelDao.updateCache();
	}

	@Override
	public void saveEntity(ModelEntity model) {
		setParentId(model);
		modelDao.insert(model);
		//更新缓存
		modelDao.updateCache();
	}

	@Override
	public List<ModelEntity> queryChildList(ModelEntity modelEntity) {
		ModelEntity model = modelDao.selectOne(new QueryWrapper<>(modelEntity));
		if(model == null){
			return null;
		}
		ModelEntity _model = new ModelEntity();
		_model.setModelId(model.getIntId());
		List<ModelEntity> list = modelDao.selectList(new QueryWrapper<>(_model));
		return list;
	}

	private void setParentId(ModelEntity model) {
		if(model.getModelId() != null && model.getModelId()>0) {
			ModelEntity _model = modelDao.selectById(model.getModelId());
			if(StringUtils.isEmpty(_model.getModelParentIds())) {
				model.setModelParentIds(_model.getId());
			} else {
				model.setModelParentIds(_model.getModelParentIds()+","+_model.getId());
			}
		}else {
			model.setModelParentIds(null);
			model.setModelId(null);
		}
	}
	private void setChildParentId(ModelEntity model) {
		ModelEntity _model=new ModelEntity();
		_model.setModelId(Integer.parseInt(model.getId()));
		List<ModelEntity> list = modelDao.query(_model);
		list.forEach(x->{
			if(StringUtils.isEmpty(model.getModelParentIds())) {
				x.setModelParentIds(model.getId());
			} else {
				x.setModelParentIds(model.getModelParentIds()+","+model.getId());
			}
			super.updateEntity(x);
			setChildParentId(x);
		});
	}

}
