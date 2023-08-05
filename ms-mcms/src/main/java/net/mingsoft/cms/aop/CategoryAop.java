/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.cms.aop;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.cms.constant.e.CategoryDisplayEnum;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.dao.ICategoryDao;
import net.mingsoft.cms.dao.IContentDao;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.service.CmsParserService;
import net.mingsoft.datascope.dao.IDataDao;
import net.mingsoft.datascope.entity.DataEntity;
import net.mingsoft.mdiy.biz.IDictBiz;
import net.mingsoft.mdiy.entity.DictEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 重写co-增加移动审批权限
 * 增加 删除栏目后并删除文章对应的静态化文件功能
 */

@Aspect
@Component("CategoryAop")
public class CategoryAop extends net.mingsoft.basic.aop.BaseAop {

    @Autowired
    private ICategoryDao categoryDao;

    @Autowired
    private IContentDao contentDao;

    @Autowired
    private IDataDao dataDao;

    /**
     * 注入静态化服务
     */
    @Autowired
    private CmsParserService cmsParserService;
    /**
     * 注入静态化服务
     */
    @Autowired
    private IDictBiz dictBiz;

    /**
     * 栏目保存接口切面
     */
    @Pointcut("execution(* net.mingsoft.cms.action.CategoryAction.save(..)) ")
    public void save() {}

    /**
     * 栏目更新接口切面
     */
    @Pointcut("execution(* net.mingsoft.cms.action.CategoryAction.update(..)) ")
    public void update() {}

    @Pointcut("execution(* net.mingsoft.cms.action.CategoryAction.delete(..)) ")
    public void delete() {}

    @Around("save() || update()")
    public ResultData move(ProceedingJoinPoint pjp) throws Throwable {
        CategoryEntity category = getType(pjp, CategoryEntity.class);
        if (category == null) {
            throw new BusinessException("栏目不存在!");
        }
        // 如果栏目被设置为不显示，将栏目下所有子栏目也设置为不显示
        if (CategoryDisplayEnum.DISABLE.toString().equalsIgnoreCase(category.getCategoryDisplay())){
            List<String> ids = categoryDao.queryChildren(category).stream().map(CategoryEntity::getId).collect(Collectors.toList());
            LambdaUpdateWrapper<CategoryEntity> wrapper = new UpdateWrapper<CategoryEntity>().lambda();
            wrapper.set(CategoryEntity::getCategoryDisplay,CategoryDisplayEnum.DISABLE.toString());
            wrapper.in(CategoryEntity::getId,ids);
            categoryDao.update(new CategoryEntity(),wrapper);

        }
        // 获取返回值
        Object obj = pjp.proceed(pjp.getArgs());
        ResultData resultData = JSONUtil.toBean(JSONUtil.toJsonStr(obj), ResultData.class);
        CategoryEntity parent = categoryDao.selectById(category.getCategoryId());
        if (parent == null) {
            return resultData;
        }
        // 用于判断父级栏目之前是否是子栏目
        // 只有父节点之前为子节点 && 父栏目类型为列表 && 子栏目为列表
//        boolean flag = parent.getLeaf() && StringUtils.equals(parent.getCategoryType(), CategoryTypeEnum.LIST.toString());
        // parent.getLeaf()  由于在新增栏目前就更新完成，所以不能作为条件
        boolean flag =  StringUtils.equals(parent.getCategoryType(), CategoryTypeEnum.LIST.toString());
        if (flag) {
            // 将父栏目的内容模板清空
            parent.setCategoryUrls("[]");
            categoryDao.updateById(parent);
            CategoryEntity returnCategory = JSONUtil.toBean(resultData.get(ResultData.DATA_KEY).toString(), CategoryEntity.class);
            // 获取父栏目ID集合
            String categoryIds = StringUtils.isEmpty(parent.getCategoryParentIds())
                    ? returnCategory.getId() : parent.getCategoryParentIds() + "," + returnCategory.getId();
            if (!StringUtils.equals(returnCategory.getCategoryType(), CategoryTypeEnum.LIST.toString())) {
                // 如果子栏目不为列表,将直接删除父栏目下的文章
                LambdaUpdateWrapper<ContentEntity> contentDeleteWrapper = new UpdateWrapper<ContentEntity>().lambda();
                contentDeleteWrapper.eq(ContentEntity::getCategoryId, parent.getId());
                contentDao.delete(contentDeleteWrapper);
            }
            // 将父栏目下的文章移动到子栏目下
            LambdaUpdateWrapper<ContentEntity> contentWrapper = new UpdateWrapper<ContentEntity>().lambda();
            contentWrapper.set(ContentEntity::getCategoryId, returnCategory.getId());
            contentWrapper.set(ContentEntity::getCategoryIds, categoryIds);
            contentWrapper.eq(ContentEntity::getCategoryId, parent.getId());
            contentDao.update(new ContentEntity(), contentWrapper);

            // 移动栏目数据权限
            // 考虑到父栏目已有当前角色的权限的情况,先删除该权限再进行移动权限操作
            LambdaUpdateWrapper<DataEntity> deleteDataWrapper = new UpdateWrapper<DataEntity>().lambda();
            ManagerEntity session =  BasicUtil.getManager();
            // 此处默认赋予给第一个角色权限
            deleteDataWrapper.eq(DataEntity::getDataTargetId, String.valueOf(session.getRoleIds().split(",")[0]));
            deleteDataWrapper.eq(DataEntity::getDataId, parent.getId());
            dataDao.delete(deleteDataWrapper);
            LambdaUpdateWrapper<DataEntity> dataWrapper = new UpdateWrapper<DataEntity>().lambda();
            dataWrapper.set(DataEntity::getDataId, returnCategory.getId());
            dataWrapper.eq(DataEntity::getDataId, parent.getId());
            dataDao.update(new DataEntity(), dataWrapper);
            return resultData;
        }
        return resultData;
    }


    /**
     * 删除栏目后并删除文章对应的静态化文件
     * @param jp
     */
    @After("delete()")
    public void delete(JoinPoint jp) {
        List<CategoryEntity> categoryEntities = (List<CategoryEntity>) getJsonParam(jp);
        List<String> dictValues = dictBiz.list(new LambdaQueryWrapper<DictEntity>().eq(DictEntity::getDictEnable, true).eq(DictEntity::getDictType, "模板类型")).stream().map(DictEntity::getDictValue).collect(Collectors.toList());
        String contentStyle = StringUtils.join(dictValues,",");
        for (CategoryEntity categoryEntity : categoryEntities) {
            // 删除静态文件
            cmsParserService.deleteCategoryHtml(contentStyle,categoryEntity);
        }
    }
}
