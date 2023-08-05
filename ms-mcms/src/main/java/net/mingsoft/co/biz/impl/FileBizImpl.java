/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.co.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.co.biz.IFileBiz;
import net.mingsoft.co.dao.IFileDao;
import net.mingsoft.co.entity.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件管理持久化层
 * @author 铭软科技
 * 创建日期：2021-5-21 10:55:41<br/>
 * 历史修订：<br/>
 */
@Service("cofileBizImpl")
public class FileBizImpl extends BaseBizImpl<IFileDao,FileEntity> implements IFileBiz {


	@Autowired
	private IFileDao fileDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return fileDao;
	}

	@Override
	public List<FileEntity> listByFolderId(String folderId) {
		QueryWrapper<FileEntity> folder_id = new QueryWrapper<FileEntity>().eq("folder_id", folderId).isNotNull("file_type").isNotNull("file_path");
		return baseMapper.selectList(folder_id);
	}
}
