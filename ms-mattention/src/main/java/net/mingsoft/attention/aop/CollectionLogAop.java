/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.aop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.attention.biz.ICollectionBiz;
import net.mingsoft.attention.biz.ICollectionLogBiz;
import net.mingsoft.attention.entity.CollectionEntity;
import net.mingsoft.attention.entity.CollectionLogEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 关注插件
 * @author 铭飞开源团队
 * @date 2022年1月21日
 */
@Component
@Aspect
public class CollectionLogAop extends BaseAop {

    @Resource(name = "collectionBizImpl")
    private ICollectionBiz collectionBiz;

    @Resource(name = "collectionLogBizImpl")
    private ICollectionLogBiz collectionLogBiz;

    @Pointcut("execution(* net.mingsoft.attention.dao.ICollectionDao.insert(..))")
    public void saveOrUpdate() {
    }

    @Pointcut("execution(* net.mingsoft.attention.dao.ICollectionLogDao.updateById(..))")
    public void delete() {
    }


    /**
     * 判断关注时是否新建关注，不是就count+1
     */
    @After("saveOrUpdate()")
    public Object save(JoinPoint jp) throws Throwable {
        CollectionEntity collectionEntity = getType(jp, CollectionEntity.class);
        //获取文章 商品 id
//        String dataId = BasicUtil.getString("dataId");
        //业务类型
//        String dataType = BasicUtil.getString("dataType");
        //查询关注记录是否有该关注
        CollectionLogEntity data = collectionLogBiz.getOne(new LambdaQueryWrapper<CollectionLogEntity>()
                .eq(CollectionLogEntity::getDataId,collectionEntity.getDataId())
                .eq(CollectionLogEntity::getDataType,collectionEntity.getDataType()), true);
        //没有该关注记录则新增 否则增加关注总数
        if(StringUtils.isEmpty(data)){
            CollectionLogEntity collectionLogEntity = new CollectionLogEntity();
            collectionLogEntity.setDataId(collectionEntity.getDataId());
            collectionLogEntity.setDataType(collectionEntity.getDataType());
            collectionLogEntity.setDataCount(1);
            collectionLogEntity.setCreateDate(new Date());
            collectionLogEntity.setUpdateDate(new Date());
            collectionLogBiz.save(collectionLogEntity);
        }else {
            //关注数+1
            data.setDataCount(data.getDataCount()+1);
            collectionLogBiz.updateById(data);
        }
        return ResultData.build().success();
    }

    /**
     * 判断更新关注时，如果count=0时，删除此关注
     */
    @After("delete()")
    public Object delete(JoinPoint jp) throws Throwable {
        CollectionLogEntity collectionLogEntity = getType(jp, CollectionLogEntity.class);
//        String dataId = BasicUtil.getString("dataId");
//        String dataType = BasicUtil.getString("dataType");
        //查询关注记录该数据
        CollectionLogEntity data = collectionLogBiz.getOne(new LambdaQueryWrapper<CollectionLogEntity>()
                .eq(CollectionLogEntity::getDataId,collectionLogEntity.getDataId())
                .eq(CollectionLogEntity::getDataType,collectionLogEntity.getDataType()), true);
        //当关注数为0时删除该条文章、商品关注信息
        if(data.getDataCount() == 0){
            collectionLogBiz.removeById(data.getId());
        }
        return ResultData.build().success();
    }


}
