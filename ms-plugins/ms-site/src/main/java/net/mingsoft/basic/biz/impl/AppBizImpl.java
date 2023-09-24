/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.basic.biz.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.dao.IAppDao;
import net.mingsoft.basic.entity.AppEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 网站基本信息业务层实现类
 * @author 史爱华
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：site重写,因为需要修改树形展示
 *         修改日期 2022-1-6
 */
@Service("appBiz")
@Transactional
public class AppBizImpl extends BaseBizImpl<IAppDao,AppEntity> implements IAppBiz{

	/**
	 * 声明IAppDao持久化层
	 */
	@Autowired
	private IAppDao appDao;

	/**
	 * 获取应用持久化层
	 * @return appDao 返回应用持久化层
	 */
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return appDao;
	}

	@Override
	public int countByUrl(String websiteUrl) {
		// TODO Auto-generated method stub
		return appDao.countByUrl(websiteUrl);
	}

	@Override
	@Deprecated
	public AppEntity getFirstApp() {
		// TODO Auto-generated method stub
		return appDao.selectList(new QueryWrapper<AppEntity>()).get(0);
	}
	@Override
	public List<AppEntity> queryChildren(AppEntity entity) {

		// TODO Auto-generated method stub
		QueryWrapper<AppEntity> wrapper = new QueryWrapper<>();
		wrapper.and(w -> {
			w.apply("find_in_set('"+entity.getId()+"',PARENT_IDS)>0")
					.or().eq("id", entity.getId());

		}).and(w -> w.eq("del", 0));
		List<AppEntity> appEntities = appDao.selectList(wrapper);
		return appEntities;
	}


	@Override
	public void saveEntity(AppEntity entity) {
		setParentId(entity);
		//更新新的父级
		if (StrUtil.isNotBlank(entity.getParentId()) && !"0".equals(entity.getParentId())) {
			AppEntity parent = appDao.selectById(entity.getParentId());
			//如果之前是叶子节点就更新
			if (parent.getLeaf()) {
				parent.setLeaf(false);
				appDao.updateById(parent);
			}
		}
		entity.setLeaf(false);
		//如果是新增栏目一定是叶子节点
		if (StrUtil.isEmpty(entity.getId())) {
			entity.setLeaf(true);
		}
		appDao.insert(entity);
		AppEntity parentapp = null;
		if (StringUtils.isNotBlank(entity.getParentId())) {
			parentapp = (AppEntity) appDao.selectById(entity.getParentId());
		}
		setTopId(entity);
		appDao.updateById(entity);
	}


	private void setParentId(AppEntity AppEntity) {
		if (StringUtils.isNotEmpty(AppEntity.getParentId()) && Long.parseLong(AppEntity.getParentId()) > 0) {
			AppEntity app = (AppEntity) appDao.selectById(AppEntity.getParentId());
			if (StringUtils.isEmpty(app.getParentIds())) {
				AppEntity.setParentIds(app.getId());
			} else {
				AppEntity.setParentIds(app.getParentIds() + "," + app.getId());
			}
		} else {
			AppEntity.setParentIds(null);
		}

	}

	private void setChildParentId(AppEntity AppEntity, String topId) {
		AppEntity app = new AppEntity();
		app.setParentId(AppEntity.getId());
		//改成mp      List<AppEntity> list = appDao.query(app);
		QueryWrapper<AppEntity> wrapper = new QueryWrapper<>();
		String parentId = app.getParentId();
		String parentIds = app.getParentIds();
		Integer del = app.getDel();
		String top = app.getTopId();
		Boolean leaf = app.getLeaf();
		if (StringUtils.isNotEmpty(parentId)){
			wrapper.eq("PARENT_ID",parentId);
		}
		if (StringUtils.isNotEmpty(parentIds)){
			wrapper.eq("parent_ids",parentIds);
		}
		if (del != null){
			wrapper.eq("del",del);
		}
		if (StringUtils.isNotEmpty(top)){
			wrapper.eq("topId",top);
		}
		if (leaf != null) {
			wrapper.eq("leaf", leaf);
		}
		List<AppEntity> list = appDao.selectList(wrapper);
		list.forEach(x -> {
			if (StringUtils.isEmpty(AppEntity.getParentIds())) {
				x.setParentIds(AppEntity.getId());
			} else {
				x.setParentIds(AppEntity.getParentIds() + "," + AppEntity.getId());
			}
			//更新topid
			x.setTopId(topId);
			//去除多余的/符号
			appDao.updateById(x);
			setChildParentId(x, topId);
		});
	}



	@Override
	public void updateEntity(AppEntity entity) {

		setParentId(entity);
		setParentLeaf(entity);
		setTopId(entity);
		//如果父级栏目和父级id为空字符串则转化成null
		if (StringUtils.isEmpty(entity.getParentId())) {
			entity.setParentId(null);
		}
		if (StringUtils.isEmpty(entity.getParentIds())) {
			entity.setParentIds(null);
		}
		appDao.updateById(entity);
		//更新子节点所有父节点id和topid
		//如果本节点的topid为0（顶级栏目）,则把自身的id作为子栏目的topid，非0所有的子栏目和本栏目使用同一个topid
		String topId = entity.getTopId();
		if (topId.equals("0")) {
			topId = entity.getId();
		}
		setChildParentId(entity, topId);
	}




	@Override
	public void deleteEntity(String parentId) {
		// TODO Auto-generated method stub
		AppEntity app = appDao.selectById(parentId);
		//删除父类
		if (app != null) {
			List<AppEntity> childrenList = queryChildren(app);
			List<String> ids = new ArrayList<>();
			for (int i = 0; i < childrenList.size(); i++) {
				//删除子类
				ids.add(childrenList.get(i).getId());
			}
			appDao.deleteBatchIds(ids);

			//获取被删节点的父节点
			AppEntity parentNode = appDao.selectById(app.getParentId());
			//判断删除的是否为主节点
			if (parentNode != null) {
				//获取被删节点的所属栏目的其他节点
				List<AppEntity> childNode = queryChildren(parentNode);
				//如果没有子节点进行更新代码
				if (childNode.size() == 1) {
					UpdateWrapper<AppEntity> updateWrapper = new UpdateWrapper<>();
					updateWrapper.eq("id", parentNode.getId()).set("leaf", 1);
					appDao.update(null, updateWrapper);
				}

			}

		}
	}

	@Override
	public Boolean isChildren(AppEntity app) {
		AppEntity _app = new AppEntity();
		_app.setId(app.getId());
		List<AppEntity> appList = queryChildren(_app);
		if(appList.size()>1) {
			for(AppEntity item:appList){
				if(item.getId().equals(app.getParentId())){
					return true;
				}
			}
			app.setLeaf(false);
		} else {
			app.setLeaf(true);
		}
		return false;

	}

	/**
	 * 设置父级叶子节点
	 *
	 * @param entity
	 */
	private void setParentLeaf(AppEntity entity) {
		AppEntity AppEntity = getById(entity.getId());
		//如果父级不为空并且修改了父级则需要更新父级
		if (entity.getParentId() != null && !entity.getParentId().equals(AppEntity.getParentId())) {
			//更新旧的父级
			if (StrUtil.isNotBlank(AppEntity.getParentId()) && !"0".equals(AppEntity.getParentId())) {
				AppEntity parent = appDao.selectById(AppEntity.getParentId());
				//如果修改了父级则需要判断父级是否还有子节点
				boolean leaf = parent.getLeaf();
				//查找不等于当前更新的分类子集，有则不是叶子节点
				QueryWrapper<AppEntity> queryWrapper = new QueryWrapper<>();
				parent.setLeaf(count(queryWrapper.eq("PARENT_ID", parent.getId()).ne("id", entity.getId())) == 0);
				if (leaf != parent.getLeaf()) {
					appDao.updateById(parent);
				}

			}
			//更新新的父级
			if (StrUtil.isNotBlank(entity.getParentId()) && !"0".equals(entity.getParentId())) {
				AppEntity parent = getById(entity.getParentId());
				//如果之前是叶子节点就更新
				if (parent.getLeaf()) {
					parent.setLeaf(false);
					appDao.updateById(parent);
				}
			}
		}
	}

	/**
	 * 设置顶级id
	 *
	 * @param entity
	 */
	private void setTopId(AppEntity entity) {
		String appParentId = entity.getParentIds();
		if (StrUtil.isNotBlank(appParentId)) {
			String[] ids = appParentId.split(",");
			//如果有ParentId就取第一个
			if (ids.length > 0) {
				entity.setTopId(ids[0]);
				return;
			}
		}
		entity.setTopId("0");
	}

}
