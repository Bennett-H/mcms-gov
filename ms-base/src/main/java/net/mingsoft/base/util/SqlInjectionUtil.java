/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */















package net.mingsoft.base.util;

import net.mingsoft.base.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL注入工具类
 *
 * @author Administrator
 * @version 创建日期：2021/4/7 8:43<br/>
 * 历史修订：<br/>
 */
public class SqlInjectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SqlInjectionUtil.class);


    private static final String REG = "\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or|updatexml|extractvalue|floor|exp|linestring|multpolygon|multlinestring|multipoint|polygon|GeometryCollection|name_const|if|exists)\\b|(\\*|;|\\+|'|%)\n";


    /**
     * 表示忽略大小写
     */
    private static final Pattern sqlPattern = Pattern.compile(REG, Pattern.CASE_INSENSITIVE);

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     *
     * @param values
     * @return
     */
    public static void filterContent(String... values) {
        for (String value : values) {
            if (value == null || "".equals(value)) {
                continue;
            }
            if (!SqlInjectionUtil.isSqlValid(value)) {
                LOG.info("请注意，值可能存在SQL注入风险: {}", value);
                throw new BusinessException("当前操作存在sql注入风险，bad sql word:"+ value);
            }
        }
        return;
    }


    /**
     * 过滤map的sql注入过滤处理，遇到注入关键字抛异常
     * @param fields
     */
    public static void filterContent(Map<String,String> fields) {
        Iterator iterator = fields.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = fields.get(key);
            SqlInjectionUtil.filterContent(key);
            SqlInjectionUtil.filterContent(value);
        }
    }


    /**
     * sql注入验证
     * @param str 需要验证的内容
     * @return false是否非法的，true通过
     */
    public static boolean isSqlValid(String str) {
        Matcher matcher = sqlPattern.matcher(str);
        if (matcher.find()) {
            // TODO: 2023/1/4 有可能matcher.group() 没有匹配到内容
            if(StringUtils.isNotBlank(matcher.group())) {
                //获取非法字符：or
                LOG.info("参数存在非法字符，请确认："+matcher.group());
                return false;
            }
        }
        return true;
    }
}
