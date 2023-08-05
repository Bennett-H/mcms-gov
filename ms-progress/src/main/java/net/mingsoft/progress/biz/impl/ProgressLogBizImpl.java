/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.progress.biz.IProgressBiz;
import net.mingsoft.progress.biz.IProgressLogBiz;
import net.mingsoft.progress.biz.ISchemeBiz;
import net.mingsoft.progress.dao.IProgressDao;
import net.mingsoft.progress.dao.IProgressLogDao;
import net.mingsoft.progress.dao.ISchemeDao;
import net.mingsoft.progress.entity.ProgressEntity;
import net.mingsoft.progress.entity.ProgressLogEntity;
import net.mingsoft.progress.entity.SchemeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 进度日志管理持久化层
 *
 * @author 铭飞科技
 * 创建日期：2020-5-28 15:12:02<br/>
 * 历史修订：<br/>
 */
@Service("progressprogressLogBizImpl")
@Transactional
public class ProgressLogBizImpl extends BaseBizImpl<IProgressLogDao, ProgressLogEntity> implements IProgressLogBiz {


    @Autowired
    private IProgressLogDao progressLogDao;

    @Autowired
    private IProgressDao progressDao;

    @Autowired
    private ISchemeDao schemeDao;
    @Autowired
    private ISchemeBiz schemeBiz;

    @Autowired
    private IProgressBiz progressBiz;

    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return progressLogDao;
    }

    @Override
    public ProgressLogEntity getProgressLogBySchemeNameAndDataId(String schemeName, String dataId) {
        SchemeEntity schemeEntity = new SchemeEntity();
        schemeEntity.setSchemeName(schemeName);
        SchemeEntity scheme = schemeDao.selectOne(new QueryWrapper<>(schemeEntity));
        ProgressLogEntity progressLogEntity = new ProgressLogEntity();
        progressLogEntity.setSchemeId(scheme.getIntegerId());
        progressLogEntity.setDataId(dataId);
        // 获取dataId待初审的进度日志
        List<ProgressLogEntity> progressLogList = progressLogDao.selectList(new QueryWrapper<>(progressLogEntity).orderByDesc("create_date").isNull("pl_status"));
        if (CollUtil.isNotEmpty(progressLogList)) {
            return progressLogList.get(0);
        }
        return null;
    }

    /**
     * 根据方案和节点id获取进度日志的dataId集合
     *
     * @param schemeName 方案名称
     * @param progressIds  进度节点ids  非必填值
     * @param plStatus  进度日志节点状态
     * @return
     */
    @Override
    public List<String> queryDataIdBySchemeNameAndNodeNames(String schemeName, List progressIds, String plStatus) {
        return progressLogDao.queryDataIdBySchemeNameAndNodeNames(schemeName,progressIds,plStatus);
    }


    @Override
    public boolean insertProgressLog(String schemeName, String dataId,String createBy) {
        // 获取方案名
        SchemeEntity scheme = new SchemeEntity();
        scheme.setSchemeName(schemeName);
        SchemeEntity schemeEntity = schemeDao.selectOne(new QueryWrapper<>(scheme));
        if(schemeEntity==null){
            LOG.debug(schemeName,"方案不存在");
            return false;
        }
        ProgressEntity progressEntity = new ProgressEntity();
        progressEntity.setSchemeId(schemeEntity.getIntId());
        List<ProgressEntity> progressEntityList = progressDao.selectList(new QueryWrapper<>(progressEntity).orderByAsc("create_date"));
        if(progressEntityList.size()==0){
            LOG.debug(schemeName,"方案进度节点为空");
            return false;
        }
        progressEntity = progressEntityList.get(0);
        // 获取进度节点的第一条
        ProgressLogEntity progressLog = new ProgressLogEntity();
        // 创建一条待初审的日志信息
        progressLog.setPlNodeName(progressEntity.getProgressNodeName());
        progressLog.setSchemeId(progressEntity.getSchemeId());
        progressLog.setProgressId(progressEntity.getIntegerId());
        progressLog.setDataId(dataId);
        progressLog.setCreateDate(new Date());
        progressLog.setCreateBy(createBy);
        super.save(progressLog);
        return true;
    }

    @Override
    public Integer getMaxProgress(ProgressLogEntity progressLogEntity) {
        LambdaQueryWrapper<ProgressLogEntity> wrapper = new QueryWrapper<>(progressLogEntity).lambda();
        wrapper.orderByDesc(ProgressLogEntity::getId);
        ProgressLogEntity progressLog = progressLogDao.selectOne(wrapper);
        return progressLog.getPlNumber();
    }

    @Override
    public Integer getSumProgress(ProgressLogEntity progressLogEntity) {
        return progressLogDao.getSumProgress(progressLogEntity);
    }
}
