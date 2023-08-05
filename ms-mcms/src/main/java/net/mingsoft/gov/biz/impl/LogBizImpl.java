/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.gov.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.bean.LogBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.dao.ILogDao;
import net.mingsoft.basic.entity.LogEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.gov.biz.ILogBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("govLogBizImpl")
public class LogBizImpl extends BaseBizImpl<ILogDao, LogEntity>  implements ILogBiz  {

    @Autowired
    private ILogDao logDao;

    /**
     * 日志备份目录
     */
    @Value("${ms.bak-path.log:config/bak/log/}")
    private String bakPathLog ;

    private static final Logger LOG = LoggerFactory.getLogger(LogBizImpl.class);

    @Override
    protected IBaseDao<LogEntity> getDao() {
        return logDao;
    }

    @Override
    public void exportFile() {
        // 通过每天定时调度调用此方法
        Date now = new Date();
        // 将当前日期设置为前一天
        Date date = DateUtil.offsetDay(now, -1);
        // 在WEB-INF/log/back 文件夹下创建 月文件夹
        String dirPath = BasicUtil.getRealPath(bakPathLog+DateUtil.format(date, "yyyy-MM"));
        if (!FileUtil.exist(dirPath)) {
            FileUtil.newFile(dirPath);
        }
        // 查询前一天的日志记录
        LambdaQueryWrapper<LogEntity> qw = new LambdaQueryWrapper<LogEntity>();
        Optional.ofNullable(date).ifPresent(
                _date -> qw.ge(LogEntity::getCreateDate, date));
        Optional.ofNullable(date).ifPresent(
                _date -> qw.le(LogEntity::getCreateDate, now));
        List<LogEntity> logList =  logDao.selectList(qw);
        // 在月文件夹下创建每天的日期文件并写入查询前一天的日志
        String filePath = dirPath+ File.separator + DateUtil.format(date, "yyyy-MM-dd-HH-mm-ss")+".xlsx";
        if (!FileUtil.exist(filePath)) {
            ExcelWriter writer = ExcelUtil.getWriter(filePath);
            writer.write(logList, true);
            writer.close();
        }else {
            LOG.info("日志备份文件已存在!");
        }

    }

    public List<String> unLoginManagers(List<String> managerNameList, int day) {
        LambdaQueryWrapper<LogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogEntity::getLogBusinessType, BusinessTypeEnum.LOGIN.toString().toLowerCase());
        wrapper.gt(LogEntity::getCreateDate, DateUtil.offsetDay(new Date(), -day));
        List<LogEntity> logs = logDao.selectList(wrapper);
        List<String> managers = logs.stream().map(LogEntity::getLogUser).distinct().collect(Collectors.toList());
        // 查询管理员与日志进行比对,不在日志里的均为长时间未登录管理员
        managers = (List<String>) CollectionUtil.disjunction(managers, managerNameList);
        return managers;
    }

}
