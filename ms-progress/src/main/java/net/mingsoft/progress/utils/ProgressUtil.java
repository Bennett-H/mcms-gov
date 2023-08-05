/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.progress.bean.ProgressBean;
import net.mingsoft.progress.biz.IProgressBiz;
import net.mingsoft.progress.biz.IProgressLogBiz;
import net.mingsoft.progress.biz.ISchemeBiz;
import net.mingsoft.progress.constant.e.SchemeTypeEnum;
import net.mingsoft.progress.entity.ProgressEntity;
import net.mingsoft.progress.entity.ProgressLogEntity;
import net.mingsoft.progress.entity.SchemeEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author by 铭飞开源团队
 * @Description TODO
 * @date 2020/5/28 11:27
 */
public class ProgressUtil {

    /**
     * 完成进度，自动完成所有有进度的节点
     * @param dataId  业务id
     * @param sName 方案名称
     * @param operator 操作人
     */
    public static void complete(String dataId, String sName, String operator) {
        IProgressBiz progressBiz = SpringUtil.getBean(IProgressBiz.class);
        IProgressLogBiz progressLogBiz = SpringUtil.getBean(IProgressLogBiz.class);
        ISchemeBiz schemeBiz = SpringUtil.getBean(ISchemeBiz.class);
        SchemeEntity schemeEntity = new SchemeEntity();
        schemeEntity.setSchemeName(sName);
        SchemeEntity scheme = schemeBiz.getOne(new QueryWrapper<>(schemeEntity));
        if (scheme != null) {
            ProgressBean progressBean = new ProgressBean();
            progressBean.setSchemeId(Integer.parseInt(scheme.getId()));
            //直接完成所有未完成的节点
            progressBean.setDataId(dataId);
            List<ProgressEntity> progressList = progressBiz.queryNotAddLog(progressBean);
            progressList.forEach(x -> {
                ProgressLogEntity progressLogEntity = new ProgressLogEntity();
                progressLogEntity.setDataId(dataId);
                progressLogEntity.setPlOperator(operator);
                progressLogEntity.setCreateDate(new Date());
                progressLogEntity.setPlStatus("skip");
                progressLogEntity.setProgressId(Integer.parseInt(x.getId()));
                progressLogEntity.setSchemeId(Integer.parseInt(scheme.getId()));
                progressLogEntity.setPlNumber(x.getProgressNumber());
                progressLogEntity.setPlNodeName(x.getProgressNodeName());
                //operator 跳过 title
                //XXX 跳过 审批
                progressLogEntity.setPlContent(StrUtil.format("{} {} {}", operator, "跳过", x.getProgressNodeName()));
                //保存日志
                progressLogBiz.save(progressLogEntity);
            });
        }
    }

    /**
     * 添加进度
     * @since TODO 此方法待确认
     * @param dataId           业务关联ID
     * @param sName            方案名称
     * @param progressNodeName 节点类型
     * @param operator          操作人
     * @param title            标题
     */
    public static void addProgress(String dataId, String sName, String progressNodeName, String operator, String title) {
        IProgressBiz progressBiz = SpringUtil.getBean(IProgressBiz.class);
        IProgressLogBiz progressLogBiz = SpringUtil.getBean(IProgressLogBiz.class);
        ISchemeBiz schemeBiz = SpringUtil.getBean(ISchemeBiz.class);
        SchemeEntity schemeEntity = new SchemeEntity();
        schemeEntity.setSchemeName(sName);
        SchemeEntity scheme = schemeBiz.getOne(new QueryWrapper<>(schemeEntity));
        if (scheme != null) {
            ProgressBean progressBean = new ProgressBean();
            progressBean.setSchemeId(Integer.parseInt(scheme.getId()));
            progressBean.setProgressNodeName(progressNodeName);
            // 查询当前
            ProgressEntity progressBizEntity = progressBiz.getOne(new QueryWrapper<>(progressBean));
            if (progressBizEntity != null) {
                ProgressLogEntity progressLogEntity = new ProgressLogEntity();
                progressLogEntity.setDataId(dataId);
                progressLogEntity.setPlOperator(operator);
                progressLogEntity.setProgressId(Integer.parseInt(progressBizEntity.getId()));
                progressLogEntity.setSchemeId(Integer.parseInt(scheme.getId()));
                progressLogEntity.setCreateDate(new Date());
                progressLogEntity.setPlNumber(progressBizEntity.getProgressNumber());
                //operator title
                //XXX 发起了 审批
                progressLogEntity.setPlContent(StrUtil.format("{} {}", operator, title));
                //保存日志
                progressLogBiz.save(progressLogEntity);
            }
            //如果是直接设置进度需要把前面的节点也加入日志
            if (SchemeTypeEnum.SET.toString().equals(scheme.getSchemeType())) {
                progressBean.setDataId(dataId);
                List<ProgressEntity> progressList = progressBiz.queryNotAddLog(progressBean);
                progressList.forEach(x -> {
                    ProgressLogEntity progressLogEntity = new ProgressLogEntity();
                    progressLogEntity.setDataId(dataId);
                    progressLogEntity.setPlOperator(operator);
                    progressLogEntity.setProgressId(Integer.parseInt(x.getId()));
                    progressLogEntity.setSchemeId(Integer.parseInt(scheme.getId()));
                    progressLogEntity.setPlNumber(x.getProgressNumber());
                    //operator 跳过 title
                    //XXX 跳过 审批
                    progressLogEntity.setPlContent(StrUtil.format("{} {} {}", operator, "跳过", x.getProgressNodeName()));
                    //保存日志
                    progressLogBiz.save(progressLogEntity);
                });
            }
        }
    }


    /**
     * 获取当前进度
     *
     * @param dataId 业务id
     * @param sName  方案名
     * @return 当前进度数
     */
    public static Integer getProgress(String dataId, String sName) {
        IProgressLogBiz progressLogBiz = SpringUtil.getBean(IProgressLogBiz.class);
        ISchemeBiz schemeBiz = SpringUtil.getBean(ISchemeBiz.class);
        SchemeEntity schemeEntity = new SchemeEntity();
        schemeEntity.setSchemeName(sName);
        SchemeEntity scheme = schemeBiz.getOne(new QueryWrapper<>(schemeEntity));
        if (scheme != null) {
            //如果是直接设置进度直接查询最大值
            ProgressLogEntity progressLogEntity = new ProgressLogEntity();
            progressLogEntity.setDataId(dataId);
            progressLogEntity.setSchemeId(Integer.parseInt(scheme.getId()));
            if (SchemeTypeEnum.SET.toString().equals(scheme.getSchemeType())) {
                return progressLogBiz.getMaxProgress(progressLogEntity);
            } else {
                return progressLogBiz.getSumProgress(progressLogEntity);
            }
        }
        return 0;
    }

    /**
     * 获取所有节点日志
     *
     * @param dataId 业务id
     * @param sName  方案名
     * @return 日志实体数组
     */
    public static List<ProgressLogEntity> queryProgressLog(String dataId, String sName) {
        IProgressLogBiz progressLogBiz = SpringUtil.getBean(IProgressLogBiz.class);
        ISchemeBiz schemeBiz = SpringUtil.getBean(ISchemeBiz.class);
        SchemeEntity schemeEntity = new SchemeEntity();
        schemeEntity.setSchemeName(sName);
        SchemeEntity scheme = schemeBiz.getOne(new QueryWrapper<>(schemeEntity));
        if (scheme != null) {
            //查询所有进度日志
            ProgressLogEntity progressLogEntity = new ProgressLogEntity();
            progressLogEntity.setDataId(dataId);
            progressLogEntity.setSchemeId(Integer.parseInt(scheme.getId()));
            return progressLogBiz.list(new QueryWrapper<>(progressLogEntity));
        }
        return new ArrayList<>(0);
    }


}
