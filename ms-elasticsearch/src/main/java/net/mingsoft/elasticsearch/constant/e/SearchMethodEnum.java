/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.elasticsearch.constant.e;

/**
 * ES搜搜方式枚举
 * Create in 2022/01/21 16:03
 */
public enum SearchMethodEnum {
    // 类似 || 只要有一个条件满足就匹配
    SHOULD_SEARCH("should_search"),
    // 高斯函数,用于某一数值加权,例如时间范围优先,价格范围优先
    GEO_FUNCTION_SEARCH("geo_function_search"),
    // 精确搜索
    MULTI_MATCH_SEARCH("multi_match_search");

    private final String code;

    SearchMethodEnum(String code) {
        this.code = code;
    }

    public String toString() {
        return this.code;
    }
}
