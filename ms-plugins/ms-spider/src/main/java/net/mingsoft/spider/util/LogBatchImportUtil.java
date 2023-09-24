/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson.JSON;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.spider.bean.LogBean;
import net.mingsoft.spider.bean.LogContentBean;
import net.mingsoft.spider.constant.e.FiledTypeEnum;
import net.mingsoft.spider.exception.SpiderException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.mingsoft.spider.constant.Const.YES;
import static net.mingsoft.spider.constant.Const.EXCEPTION;

/**
 * 根据爬取日志表 批量处理的工具类
 * @author shenchen
 */
public class LogBatchImportUtil {

    private final DataSource dataSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(LogBatchImportUtil.class);

    public LogBatchImportUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * 根据将日志表中的content中的数据导入importTable
     * @param importTable 需要导入表名
     * @param list 日志实体类
     */
    public void batchInsert(String importTable, List<LogBean> list){
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        //替换内容的../
        list = list.stream().map(logBean -> {
            String content = logBean.getContent();
            content = content.replaceAll("\\.\\./", "");
            logBean.setContent(content);
            return logBean;
        }).collect(Collectors.toList());
        Connection conn = null;
        PreparedStatement statement = null;
        // 首先要获取连接，即连接到数据库
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            throw new SpiderException(e.getMessage());
        }

        //获取所有的字段和值
        List<List<LogContentBean>> sqls = list.stream().map(l ->
                //对应一条SQL
                JSON.parseArray(l.getContent(), LogContentBean.class))
                .collect(Collectors.toList());
        //给每条数据增加日志的id值和时间
        for (int i = 0; i < sqls.size(); i++) {
            if (CollectionUtil.isEmpty(sqls.get(i))) {
                return;
            }
            LogContentBean idLogContentBean = new LogContentBean();
            idLogContentBean.setFiled("id");
            idLogContentBean.setFiledType(FiledTypeEnum.STRING);
            idLogContentBean.setData(list.get(i).getId());
            sqls.get(i).add(idLogContentBean);

            String now = DateUtil.now();
            int update_date = sqls.get(i).stream().filter(logContentBean -> logContentBean.getFiled().equalsIgnoreCase("UPDATE_DATE")).collect(Collectors.toList()).size();
            if (update_date <= 0){
                LogContentBean updateDateLogContentBean = new LogContentBean();
                updateDateLogContentBean.setFiled("UPDATE_DATE");
                updateDateLogContentBean.setFiledType(FiledTypeEnum.DATE);
                updateDateLogContentBean.setData(now);
                sqls.get(i).add(updateDateLogContentBean);
            }
            int create_date = sqls.get(i).stream().filter(logContentBean -> logContentBean.getFiled().equalsIgnoreCase("CREATE_DATE")).collect(Collectors.toList()).size();
            if (create_date <= 0){
                LogContentBean createDateLogContentBean = new LogContentBean();
                createDateLogContentBean.setFiled("CREATE_DATE");
                createDateLogContentBean.setFiledType(FiledTypeEnum.DATE);
                createDateLogContentBean.setData(now);
                sqls.get(i).add(createDateLogContentBean);
            }

            int content_datetime = sqls.get(i).stream().filter(logContentBean -> logContentBean.getFiled().equalsIgnoreCase("CONTENT_DATETIME")).collect(Collectors.toList()).size();
            if (content_datetime <= 0 && importTable.equalsIgnoreCase("CMS_CONTENT")){
                LogContentBean contentDateTimeLogContentBean = new LogContentBean();
                contentDateTimeLogContentBean.setFiled("CONTENT_DATETIME");
                contentDateTimeLogContentBean.setFiledType(FiledTypeEnum.DATE);
                contentDateTimeLogContentBean.setData(now);
                sqls.get(i).add(contentDateTimeLogContentBean);
            }


        }
        //获取字段名
        List<String> fields = sqls.get(0).stream().map(LogContentBean::getFiled).collect(Collectors.toList());

        String prepareSql = JdbcUtils.makeInsertToTableSql(importTable, fields);
        try {
            statement = conn.prepareStatement(prepareSql);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            throw new SpiderException(e.getMessage());
        }

        // 有异常的日志
        List<String> logList = new ArrayList<>();
        for (List<LogContentBean> sql : sqls) {
            if (CollectionUtil.isEmpty(sql)) {
                return;
            }
            // 当前日志id
            String id = null;
            try {
                for (int i = 0; i < sql.size(); i++) {
                    LogContentBean bean = sql.get(i);
                    if (bean.getFiled().equalsIgnoreCase("id")) {
                        id = bean.getData();
                    }
                    // 去除前后多余空格
                    sql.get(i).setData(StringUtils.trim(sql.get(i).getData()));
                    switch (bean.getFiledType()){
                        case STRING:{
                            statement.setString(i + 1,sql.get(i).getData());
                            break;
                        }
                        case NUMBER:{
                            statement.setInt(i + 1,Integer.parseInt(sql.get(i).getData()));
                            break;
                        }
                        case DATE: {
                            if (sql.get(i).getData() == null) {
                                statement.setTimestamp(i + 1, null);
                            }else {
                                long time = DateUtil.parse(sql.get(i).getData()).toJdkDate().getTime();
                                statement.setTimestamp(i + 1,new Timestamp(time));
                            }
                            break;
                        }
                        default:
                            throw new SpiderException("未知类型");
                    }
                }
                statement.execute();
                conn.commit();
            }catch (SQLIntegrityConstraintViolationException e) {
                LOGGER.error(e.getMessage());
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                LOGGER.error(Arrays.toString(e.getStackTrace()));
                // 触发异常时增加id
                logList.add(id);
                e.printStackTrace();
            }

        }
        // 异常日志
        List<LogBean> excLogList = new ArrayList<>();
        // 异常日志填充处理
        if (CollUtil.isNotEmpty(logList)) {
            list = list.stream().filter(logBean -> {
                if (!logList.contains(logBean.getId())) {
                    return true;
                }
                excLogList.add(logBean);
                return false;
            }).collect(Collectors.toList());
        }
        //有异常日志则更改为异常状态
        if (CollUtil.isNotEmpty(excLogList)) {
            batchUpdateStatus(excLogList, EXCEPTION);
        }
        //修改插入状态
        batchUpdateStatus(list, YES);
        org.springframework.jdbc.support.JdbcUtils.closeStatement(statement);
        org.springframework.jdbc.support.JdbcUtils.closeConnection(conn);
    }

    /**
     * 批量修改导入状态
     * @param logs
     */
    public void batchUpdateStatus(List<LogBean> logs, String logType){
        if (CollectionUtil.isEmpty(logs)){
            throw new SpiderException("0 条日志??");
        }

        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            statement = conn.prepareStatement("UPDATE spider_log SET imported = '"+logType+"' WHERE id = ?;");

            for (LogBean log : logs) {
                statement.setString(1,log.getId());
                statement.addBatch();
            }
            statement.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            org.springframework.jdbc.support.JdbcUtils.closeStatement(statement);
            org.springframework.jdbc.support.JdbcUtils.closeConnection(conn);
        }
    }


//    public static void main(String[] args) throws IOException {
////        HttpUtil.downloadFile("//img.t.sinajs.cn/t5/style/images/face/male_180.png",new File("D:\\图片下载\\10.png"));
//        System.out.println(BasicUtil.getRealPath("upload"));
//    }
}
