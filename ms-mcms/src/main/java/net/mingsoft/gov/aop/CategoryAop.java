/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.aop;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.mingsoft.approval.dao.IConfigDao;
import net.mingsoft.approval.entity.ConfigEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.dao.ICategoryDao;
import net.mingsoft.cms.entity.CategoryEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Aspect
@Component("govCategoryAop")
public class CategoryAop extends net.mingsoft.basic.aop.BaseAop {

    @Autowired
    private ICategoryDao categoryDao;


    @Autowired
    private IConfigDao configDao;



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

    @Around("save() || update()")
    public ResultData move(ProceedingJoinPoint pjp) throws Throwable {
        CategoryEntity category = getType(pjp, CategoryEntity.class);
        if (category == null) {
            throw new BusinessException("栏目不存在!");
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
        boolean flag = parent.getLeaf() && StringUtils.equals(parent.getCategoryType(), CategoryTypeEnum.LIST.toString());
        if (flag) {
            CategoryEntity returnCategory = JSONUtil.toBean(resultData.get(ResultData.DATA_KEY).toString(), CategoryEntity.class);
            // 移动父栏目的审批权限
            LambdaUpdateWrapper<ConfigEntity> configWrapper = new UpdateWrapper<ConfigEntity>().lambda();
            configWrapper.set(ConfigEntity::getCategoryId, returnCategory.getId());
            configWrapper.eq(ConfigEntity::getCategoryId, parent.getId());
            configDao.update(new ConfigEntity(), configWrapper);
            return resultData;
        }
        return resultData;
    }


}
