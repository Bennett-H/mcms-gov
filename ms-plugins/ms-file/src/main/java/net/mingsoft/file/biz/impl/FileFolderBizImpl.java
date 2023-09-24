/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.file.biz.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.co.dao.IFileDao;
import net.mingsoft.co.entity.FileEntity;
import net.mingsoft.file.biz.IFileFolderBiz;
import net.mingsoft.file.dao.IFolderDao;
import net.mingsoft.file.entity.FileFolderEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 文件夹表管理持久化层
 * @author biusp
 * 创建日期：2021-12-16 18:34:59<br/>
 * 历史修订：<br/>
 */
 @Service("file_biuspfolderBizImpl")
public class FileFolderBizImpl extends BaseBizImpl<IFolderDao, FileFolderEntity> implements IFileFolderBiz {


	@Autowired
	private IFolderDao folderDao;

	@Autowired
	private IFileDao fileDao;



	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return folderDao;
	}



    @Override
	public List<FileFolderEntity> queryChildren(FileFolderEntity entity) {

		// TODO Auto-generated method stub
		QueryWrapper<FileFolderEntity> wrapper = new QueryWrapper<>();
		wrapper.and(w -> {
			w.apply("find_in_set('"+entity.getId()+"',PARENT_IDS)>0")
					.or().eq("id", entity.getId());

		}).and(w -> w.eq("del", 0));
		List<FileFolderEntity> folderEntities = folderDao.selectList(wrapper);
		return folderEntities;
	}

	@Override
	public List<FileFolderEntity> queryChild(String folderId) {
		QueryWrapper<FileFolderEntity> wrapper = new QueryWrapper<FileFolderEntity>().eq("parent_id", folderId);

		return baseMapper.selectList(wrapper);
	}


	@Override
	public void saveEntity(FileFolderEntity entity) {
		setPath(entity);
		setParentId(entity);
		//更新新的父级
		if (StrUtil.isNotBlank(entity.getParentId()) && !"0".equals(entity.getParentId())) {
			FileFolderEntity parent = folderDao.selectById(entity.getParentId());
			//如果之前是叶子节点就更新
			if (parent.getLeaf()) {
				parent.setLeaf(false);
				folderDao.updateById(parent);
			}
		}
		entity.setLeaf(false);
		//如果是新增栏目一定是叶子节点
		if (StrUtil.isEmpty(entity.getId())) {
			entity.setLeaf(true);
		}
		folderDao.insert(entity);
		FileFolderEntity parentfolder = null;
		if (StringUtils.isNotBlank(entity.getParentId())) {
			parentfolder = (FileFolderEntity) folderDao.selectById(entity.getParentId());
		}
		setTopId(entity);


		folderDao.updateById(entity);
	}

	/**
	 * 设置文件夹路径
	 * @param FileFolderEntity
	 */
	private void setPath(FileFolderEntity FileFolderEntity) {
		if (StringUtils.isNotEmpty(FileFolderEntity.getParentId()) && Long.parseLong(FileFolderEntity.getParentId()) > 0) {
			net.mingsoft.file.entity.FileFolderEntity folder = (net.mingsoft.file.entity.FileFolderEntity) folderDao.selectById(FileFolderEntity.getParentId());
			if (StringUtils.isEmpty(folder.getPath())) {
				FileFolderEntity.setPath("/"+FileFolderEntity.getFolderName());
			} else {
				FileFolderEntity.setPath(folder.getPath()+"/"+FileFolderEntity.getFolderName());
			}
		}
	}


	private void setParentId(FileFolderEntity FileFolderEntity) {
		if (StringUtils.isNotEmpty(FileFolderEntity.getParentId()) && Long.parseLong(FileFolderEntity.getParentId()) > 0) {
			net.mingsoft.file.entity.FileFolderEntity folder = (net.mingsoft.file.entity.FileFolderEntity) folderDao.selectById(FileFolderEntity.getParentId());
			if (StringUtils.isEmpty(folder.getParentIds())) {
				FileFolderEntity.setParentIds(folder.getId());
			} else {
				FileFolderEntity.setParentIds(folder.getParentIds() + "," + folder.getId());
			}
		} else {
			FileFolderEntity.setParentIds(null);
		}

	}

	private void setChildParentId(FileFolderEntity FileFolderEntity, String topId) {
		net.mingsoft.file.entity.FileFolderEntity folder = new FileFolderEntity();
		folder.setParentId(FileFolderEntity.getId());
		//改成mp      List<FolderEntity> list = folderDao.query(folder);
		QueryWrapper<net.mingsoft.file.entity.FileFolderEntity> wrapper = new QueryWrapper<>();
		String parentId = folder.getParentId();
		String parentIds = folder.getParentIds();
		Integer del = folder.getDel();
		String top = folder.getTopId();
		Boolean leaf = folder.getLeaf();
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
		List<net.mingsoft.file.entity.FileFolderEntity> list = folderDao.selectList(wrapper);
		list.forEach(x -> {
			if (StringUtils.isEmpty(FileFolderEntity.getParentIds())) {
				x.setParentIds(FileFolderEntity.getId());
			} else {
				x.setParentIds(FileFolderEntity.getParentIds() + "," + FileFolderEntity.getId());
			}
			//更新topid
			x.setTopId(topId);
			//去除多余的/符号
			folderDao.updateById(x);
			setChildParentId(x, topId);
		});
	}

	
	
	@Override
	public void updateEntity(FileFolderEntity entity) {
		
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
		folderDao.updateById(entity);
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
		FileFolderEntity folder = folderDao.selectById(parentId);
		//删除父类
		if (folder != null) {
			List<FileFolderEntity> childrenList = queryChildren(folder);
			List<String> ids = new ArrayList<>();
			for (int i = 0; i < childrenList.size(); i++) {
				//删除子类
				ids.add(childrenList.get(i).getId());
			}
			/**
			 * 删除对应文件夹下的文件
			 */
			fileDao.delete(new QueryWrapper<FileEntity>().in("folder_id",ids));

			folderDao.deleteBatchIds(ids);

			//获取被删节点的父节点
			FileFolderEntity parentNode = folderDao.selectById(folder.getParentId());
			//判断删除的是否为主节点
			if (parentNode != null) {
				//获取被删节点的所属栏目的其他节点
				List<FileFolderEntity> childNode = queryChildren(parentNode);
				//如果没有子节点进行更新代码
				if (childNode.size() == 1) {
					UpdateWrapper<FileFolderEntity> updateWrapper = new UpdateWrapper<>();
					updateWrapper.eq("id", parentNode.getId()).set("leaf", 1);
					folderDao.update(null, updateWrapper);
				}

			}

		}
	}

	@Override
	public Boolean isChildren(FileFolderEntity folder) {
		FileFolderEntity _folder = new FileFolderEntity();
		_folder.setId(folder.getId());
		List<FileFolderEntity> folderList = queryChildren(_folder);
		if(folderList.size()>1) {
			for(FileFolderEntity item:folderList){
				if(item.getId().equals(folder.getParentId())){
					return true;
				}
			}
			folder.setLeaf(false);
		} else {
			folder.setLeaf(true);
		}
		return false;
		
	}

	/**
	 * 设置父级叶子节点
	 *
	 * @param entity
	 */
	private void setParentLeaf(FileFolderEntity entity) {
		FileFolderEntity FileFolderEntity = getById(entity.getId());
		//如果父级不为空并且修改了父级则需要更新父级
		if (entity.getParentId() != null && !entity.getParentId().equals(FileFolderEntity.getParentId())) {
			//更新旧的父级
			if (StrUtil.isNotBlank(FileFolderEntity.getParentId()) && !"0".equals(FileFolderEntity.getParentId())) {
				net.mingsoft.file.entity.FileFolderEntity parent = folderDao.selectById(FileFolderEntity.getParentId());
				//如果修改了父级则需要判断父级是否还有子节点
				boolean leaf = parent.getLeaf();
				//查找不等于当前更新的分类子集，有则不是叶子节点
				QueryWrapper<net.mingsoft.file.entity.FileFolderEntity> queryWrapper = new QueryWrapper<>();
				parent.setLeaf(count(queryWrapper.eq("PARENT_ID", parent.getId()).ne("id", entity.getId())) == 0);
				if (leaf != parent.getLeaf()) {
					folderDao.updateById(parent);
				}

			}
			//更新新的父级
			if (StrUtil.isNotBlank(entity.getParentId()) && !"0".equals(entity.getParentId())) {
				net.mingsoft.file.entity.FileFolderEntity parent = getById(entity.getParentId());
				//如果之前是叶子节点就更新
				if (parent.getLeaf()) {
					parent.setLeaf(false);
					folderDao.updateById(parent);
				}
			}
		}
	}

	/**
	 * 设置顶级id
	 *
	 * @param entity
	 */
	private void setTopId(FileFolderEntity entity) {
		String folderParentId = entity.getParentIds();
		if (StrUtil.isNotBlank(folderParentId)) {
			String[] ids = folderParentId.split(",");
			//如果有ParentId就取第一个
			if (ids.length > 0) {
				entity.setTopId(ids[0]);
				return;
			}
		}
		entity.setTopId("0");
	}


}
