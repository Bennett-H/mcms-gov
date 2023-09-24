/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.statistics.bean;

import java.util.Map;

/**
 * @Author: elims
 * @Description:
 * @Date: Create in 2021/01/15 9:48
 */
public class SqlBean  {

    private String name;

    /**
     * 用于SQL后面拼接的条件
     */
    private Map<String,Object> params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
