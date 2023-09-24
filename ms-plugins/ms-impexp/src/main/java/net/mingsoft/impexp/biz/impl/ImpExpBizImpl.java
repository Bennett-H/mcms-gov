/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.biz.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.impexp.biz.IImpExpBiz;
import net.mingsoft.impexp.constant.e.IdEnum;
import net.mingsoft.impexp.dao.ISetDao;
import net.mingsoft.impexp.entity.SetEntity;
import net.mingsoft.impexp.util.WordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.valueOf;

/**
 * excel业务
 *
 * @author 铭软科技
 * 创建日期：2021-2-2 17:35:57<br/>
 * 历史修订：<br/>
 */
@Service("impExpBizImpl")
public class ImpExpBizImpl extends BaseBizImpl<ISetDao, SetEntity> implements IImpExpBiz {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImpExpBizImpl.class);

    @Autowired
    private ISetDao setDao;

    @Override
    protected IBaseDao getDao() {
        return setDao;
    }


    @Override
    public void impExcel(SetEntity set, String path, Map<String, Object> paraMap) {
        //获取上传文件
        String filePath = BasicUtil.getRealPath(path);
        File file = new File(filePath);

        //获取数据源
        JdbcTemplate jdbcTemplate = SpringUtil.getBean(JdbcTemplate.class);


        //读取json配置
        //id snow：雪花； auto:表控制；input：Excel控制  字段名：从表ID，
        String json = set.getImportJson();

        List<Map> importJson = JSONUtil.toList(json, Map.class);

        //读取所有行
        List<Map<String, Object>> dataList = ExcelUtil.getReader(file).readAll();

        AtomicInteger index = new AtomicInteger();
        final boolean[] isMaster = {false};
        final String[] masterId = {null}; //这里的数组是自动生成，可能是jdk8.foreach的变量特性
        //循环Excel数据
        dataList.forEach((m) -> {
            //记录主表
            if (!isMaster[0]) {
                isMaster[0] = true;
            }
            //循环表配置  importJson
            //第一层循环主表，其余是从表
            importJson.forEach((t) -> {
                Map columns = (Map) t.get("columns");
                //记录excel中的值
                List values = new ArrayList();
                //保存更新的列名
                StringBuilder columnStr = new StringBuilder();
                //保存更新的列值，对应生成sql预处理占位?,?,?
                StringBuilder valuesStr = new StringBuilder();
                //判断主键生成规则
                if (IdEnum.SNOW.toString().equals(t.get("id"))) { //雪花
                    columnStr.append(",").append("id"); //字段1,字段2
                    //雪花id
                    Snowflake snowflake = IdUtil.createSnowflake(0, 0);
                    valuesStr.append(",").append("?"); //?,?
                    long _id = snowflake.nextId();
                    values.add(_id);
                    masterId[0] = valueOf(_id);

                } else if (IdEnum.AUTO.toString().equals(t.get("id"))) { //自增长

                } else if (isMaster[0] && ObjectUtil.isEmpty(t.get("id"))) { //表的id是直接明文记录在excel
                    masterId[0] = (String) m.get(t.get("id"));  //直接读取excel值为主键
                    isMaster[0] = false; //方便从表读取

                } else { //从表，根据主表id关联
                    columnStr.append(",").append(t.get("id")); //字段1,字段2
                    valuesStr.append(",").append("?"); //?,?
                    values.add(masterId[0]);
                }

                //便利excel的列
                m.forEach((key, value) -> {
                    //如果excel列名没有在json的配置内，就跳过
                    if (columns.get(key) != null) {
                        columnStr.append(",").append(columns.get(key)); //字段1,字段2
                        valuesStr.append(",").append("?"); //?,?
                        values.add(value);//excel列值
                    }
                });

                //组织前端的columns
                paraMap.keySet().forEach((k) -> {
                    if (columns.get(k) != null && !columnStr.toString().contains((String)columns.get(k))) {
                        columnStr.append(",").append(columns.get(k)); //字段1,字段2
                        valuesStr.append(",").append("?"); //?,?
                        values.add(paraMap.get(k));
                    }
                });

                //默认值
                Map defaultColumns = (Map) t.get("defaults");
                if (defaultColumns != null) {
                    //defaultColumn
                    defaultColumns.keySet().forEach((k) -> {
                        columnStr.append(",").append(k); //字段1,字段2
                        valuesStr.append(",").append("?"); //?,?
                        values.add(defaultColumns.get(k));
                    });

                }


                //组织sql语 句
                String insert = "insert into {} ({}) values({})";
                LOG.debug("第{}行,{}", index.getAndIncrement(), insert);
                String _insert = StrUtil.format(insert, t.get("table"), columnStr.substring(1), valuesStr.substring(1));

                //如果是自增长就需要获取最新id
                if (t.get("id").equals("auto")) {
                    KeyHolder holder = new GeneratedKeyHolder();
                    jdbcTemplate.update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement ps = connection.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS);
                            for (int i = 1; i <= values.size(); i++) {
                                ps.setObject(i, values.get(i-1));
                            }
                            return ps;
                        }
                    }, holder);
                    //设置主键id
                    masterId[0] = valueOf(holder.getKey().intValue());
                } else {
                    //利用update方法实现数据写入操作
                    jdbcTemplate.update(_insert, values.toArray());
                }

            });
        });
        file.delete();
    }



    @Override
    public boolean impWord(SetEntity set, String docxPath, String imgDir, Map<String, Object> paraMap) throws ParserConfigurationException, TransformerException, IOException {
        //获取数据源
        JdbcTemplate jdbcTemplate = SpringUtil.getBean(JdbcTemplate.class);
        String html = "";
        try {
            html = WordUtil.docx2Html(docxPath, imgDir);
            LOGGER.debug("解析Word内容:{}",html);
            //读取json配置
            //id snow：雪花； auto:表控制；input：Excel控制
            String json = set.getImportJson();
            Map importJson = JSONUtil.toList(json, Map.class).get(0);
            String table = importJson.get("table").toString();
            String idType = importJson.get("id").toString();
            StringBuilder columnStr = new StringBuilder();
            StringBuilder valuesStr = new StringBuilder();
            List values = new ArrayList();
            Map<String, String> columns = (Map<String, String>) importJson.get("columns");
            Map<String, String> defaults = (Map<String, String>) importJson.get("defaults");
            paraMap.put("docFileContent", html);
            //默认的文件名和内容
            for (Map.Entry<String, String> entry : columns.entrySet()) {
                String mapKey = entry.getKey();
                columnStr.append(",").append(entry.getValue());
                valuesStr.append(",").append("?");
                //组织前端传递data
                if (paraMap.get(mapKey) != null) {
                    values.add(paraMap.get(mapKey));
                } else {
                    LOG.debug(mapKey, "paraMap中参数无对应的值");
                    values.add("");
                }
            }

            if (defaults != null) {
                //defaultColumn
                for (String key : defaults.keySet()) {
                    columnStr.append(",").append(key);
                    valuesStr.append(",").append("?");
                }
            }


            //defaultValue
            for (String v : defaults.values()) {
                values.add(v);
            }

            //添加默认时间
            columnStr.append(",").append("content_datetime");
            valuesStr.append(",").append("?");
            values.add(new Date());
            columnStr.append(",").append("create_date");
            valuesStr.append(",").append("?");
            values.add(new Date());

            //添加默认content_type
            columnStr.append(",").append("content_type");
            valuesStr.append(",").append("?");
            values.add("");

            //添加发布到 采取配置获取，暂时废弃此处代码
            /*columnStr.append(",").append("content_style");
            valuesStr.append(",").append("?");
            Object categoryId = paraMap.get("categoryId");
            List list = setDao.excuteSql("select category_urls from cms_category where id = " + categoryId);
            if (!list.isEmpty()) {
                Map map = (Map) list.get(0);
                String str = (String) map.get("category_urls");
                JSONArray array = JSONArray.parseArray(str);
                if (!array.isEmpty()) {
                    Map<String, Object> UrlMap = (Map<String, Object>) array.get(0);
                    System.out.println(UrlMap);
                    Object name = UrlMap.get("name");
                    values.add(DictUtil.getDictValue("模板类型",name.toString()));
                } else {
                    throw new BusinessException("当前栏目没有绑定模板!");
                }
            } else {
                throw new BusinessException("当前栏目没有绑定模板!");
            }*/

            //判断主键生成规则
            if (IdEnum.SNOW.toString().equals(idType)) { //雪花
                //主键
                columnStr.append(",").append("id");
                valuesStr.append(",").append("?");
                //雪花id
                Snowflake snowflake = IdUtil.createSnowflake(0, 0);
                values.add(snowflake.nextId());
            }

            //组织sql语 句
            String insert = "insert into {} ({}) values({})";
            AppEntity app = BasicUtil.getWebsiteApp();
            if (app != null) {
                columnStr.append(",").append("app_id");
                valuesStr.append(",").append("?");
                values.add(app.getId());
            }
            String _insert = StrUtil.format(insert, table, columnStr.substring(1), valuesStr.substring(1));


            if (IdEnum.AUTO.toString().equals(idType)) { //自增长
                KeyHolder holder = new GeneratedKeyHolder();
                jdbcTemplate.update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS);
                        return ps;
                    }
                }, holder);
            } else {
                jdbcTemplate.update(_insert, values.toArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
