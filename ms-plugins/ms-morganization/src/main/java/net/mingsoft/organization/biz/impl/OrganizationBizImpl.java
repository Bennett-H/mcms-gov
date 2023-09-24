/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.organization.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.organization.bean.OrganizationBean;
import net.mingsoft.organization.biz.IOrganizationBiz;
import net.mingsoft.organization.dao.IOrganizationDao;
import net.mingsoft.organization.entity.OrganizationEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织机构管理持久化层
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：<br/>
 */
 @Service("organizationorganizationBizImpl")
 @Transactional
public class OrganizationBizImpl extends BaseBizImpl<IOrganizationDao, OrganizationEntity> implements IOrganizationBiz {


	@Autowired
	private IOrganizationDao organizationDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return organizationDao;
	}

	@Override
	public void saveEntity(OrganizationEntity entity) {
		setParentId(entity);
		super.saveEntity(entity);

	}
	@Override
	public void updateEntity(OrganizationEntity entity) {
		setParentId(entity);
		super.updateEntity(entity);
		setChildParentId(entity);
	}

	public List<OrganizationEntity> queryChildren(int id) {
		return organizationDao.queryChildren(id);
	}

	@Override
	public OrganizationBean queryHierarchy(OrganizationEntity entity) {
		if (entity == null){
			return null;
		}
		List<OrganizationEntity> entityList = new ArrayList<>();
		if (StringUtils.isBlank(entity.getOrganizationParentId())){
			//若没有父id
			entityList.add(entity);
		}else {
			String[] ids = entity.getOrganizationParentId().split(",");
			entityList = baseMapper.selectList(new LambdaQueryWrapper<OrganizationEntity>().in(OrganizationEntity::getId, ids));
			entityList.add(entity);
		}
		List<OrganizationEntity> list = entityList.stream().filter(organizationEntity -> StringUtils.isBlank(organizationEntity.getOrganizationParentId())).collect(Collectors.toList());
		//获取根节点
		OrganizationEntity parent = list.get(0);
		OrganizationBean organizationBean = new OrganizationBean();
		BeanUtils.copyProperties(parent,organizationBean);

		constitutionOrganizationBean(entityList,organizationBean);

		return organizationBean;
	}

	/**
	 * 填充子节点
	 */
	private void constitutionOrganizationBean(List<OrganizationEntity> entityList,OrganizationBean organizationBean){
		String organizationId = organizationBean.getId();

		 //获取当前节点的所有子节点
		List<OrganizationBean> children = entityList.stream().filter(organizationEntity -> organizationEntity.getOrganizationId().equals(organizationId))
				.map(organizationEntity -> {
					OrganizationBean bean = new OrganizationBean();
					BeanUtils.copyProperties(organizationEntity, bean);
					return bean;
				}).collect(Collectors.toList());

		organizationBean.setChildren(children);

		//递归填充子节点的子节点
		children.forEach(bean -> this.constitutionOrganizationBean(entityList,bean));

	}

	@Override
	public void delete(int[] ids) {
		List<Integer> deleteIds=new ArrayList();
		for (int id : ids) {
			OrganizationEntity entity = (OrganizationEntity) organizationDao.getEntity(id);
			//删除父类
			if(entity != null){
				entity.setOrganizationParentId(null);
				List<OrganizationEntity> childrenList = organizationDao.queryChildren(id);
				for(int i = 0; i < childrenList.size(); i++){
					//删除子类
					deleteIds.add(Integer.parseInt(childrenList.get(i).getId()));
				}
			}
			deleteIds.add(id);
		}
		organizationDao.delete(deleteIds.stream().mapToInt(Integer::valueOf).toArray());
 	}

	/**
	 * 设置父级Id
	 * @param organizationEntity
	 */
	private void setParentId(OrganizationEntity organizationEntity) {
		if(StringUtils.isNotEmpty(organizationEntity.getOrganizationId())&&Integer.parseInt(organizationEntity.getOrganizationId())>0) {
			OrganizationEntity entity = (OrganizationEntity)organizationDao.getEntity(Integer.parseInt(organizationEntity.getOrganizationId()));
			if(StringUtils.isEmpty(entity.getOrganizationParentId())) {
				organizationEntity.setOrganizationParentId(entity.getId());
			} else {
				organizationEntity.setOrganizationParentId(entity.getOrganizationParentId()+","+entity.getId());
			}
		}else {
			organizationEntity.setOrganizationParentId(null);
		}
	}

	/**
	 * 给所有子集设置父级Id
	 * @param organizationEntity
	 */
	private void setChildParentId(OrganizationEntity organizationEntity) {
		OrganizationEntity entity=new OrganizationEntity();
		entity.setOrganizationId(organizationEntity.getId());
		List<OrganizationEntity> list = organizationDao.query(entity);
		list.forEach(x->{
			if(StringUtils.isEmpty(organizationEntity.getOrganizationParentId())) {
				x.setOrganizationParentId(organizationEntity.getId());
			} else {
				x.setOrganizationParentId(organizationEntity.getOrganizationParentId()+","+organizationEntity.getId());
			}
			super.updateEntity(x);
			setChildParentId(x);
		});
	}

}
