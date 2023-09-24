/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.file.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.file.entity.FileFolderEntity;

import java.util.List;

/**
 * 文件夹表业务
 * @author biusp
 * 创建日期：2021-12-16 18:34:59<br/>
 * 历史修订：<br/>
 */
public interface IFileFolderBiz extends IBaseBiz<FileFolderEntity> {

 /**
 * 查询当前分类下的所有子分类,包含自身
 * @param FolderEntity 通过setId指定id
 * @return
 */
 List<FileFolderEntity> queryChildren(FileFolderEntity folder);

 /**
  * 查询当前分类下的儿子分类,不包含自身
  * @param FolderEntity 通过setId指定id
  * @return
  */
 List<FileFolderEntity> queryChild(String folderId);

  /**
  * 保存节点，判断是否为叶子节点
  * @param FolderEntity
  */
  void saveEntity(FileFolderEntity folder);

  /**更新父级及子集
  * @param FolderEntity
  */
  void updateEntity(FileFolderEntity folder);

  /**
  * 删除节点，并判断删除后那些兄弟节点变成了子节点
  * @param parentId 父节点id
  */
  void deleteEntity(String parentId);

  /**
  * 判断是否是子节点
  * @return
  * @param FolderEntity
  */
  Boolean isChildren(FileFolderEntity folder);

}
