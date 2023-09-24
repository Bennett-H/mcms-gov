/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.basic.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.basic.entity.AppEntity;

import java.util.List;

/**
 * 网站基本信息业务层接口
 * @author 史爱华
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：site重写,因为需要修改树形展示
 *         修改日期 2022-1-6
 */
public interface IAppBiz extends IBaseBiz<AppEntity> {

	/**
	 * 查找域名相同站点的个数
	 * @param websiteUrl 域名
	 * @return 返回站点个数
	 */
	int countByUrl(String websiteUrl);

	/**
	 * 根据站点域名获取站点实体
	 * 建议使用 getOne(Wrapper<T> queryWrapper, boolean throwEx)方法
	 * @return 返回站点实体
	 */
	@Deprecated
	AppEntity getFirstApp();


	/**
	 * 查询当前分类下的所有子分类,包含自身
	 * @param AppEntity 通过setId指定id
	 * @return
	 */
	List<AppEntity> queryChildren(AppEntity app);

	/**
	 * 保存节点，判断是否为叶子节点
	 * @param AppEntity
	 */
	void saveEntity(AppEntity app);

	/**更新父级及子集
	 * @param AppEntity
	 */
	void updateEntity(AppEntity app);

	/**
	 * 删除节点，并判断删除后那些兄弟节点变成了子节点
	 * @param parentId 父节点id
	 */
	void deleteEntity(String parentId);

	/**
	 * 判断是否是子节点
	 * @return
	 * @param AppEntity
	 */
	Boolean isChildren(AppEntity app);
}
