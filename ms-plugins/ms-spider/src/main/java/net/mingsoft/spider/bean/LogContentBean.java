/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.bean;

import com.alibaba.fastjson.annotation.JSONField;
import net.mingsoft.spider.component.FieldTypeCodec;
import net.mingsoft.spider.constant.e.FiledTypeEnum;

/**
 *  spider_log 表 content 字段 保存的数据结构
 */
public class LogContentBean {

    /**
     * 映射字段
     */
    private String filed;
    /**
     * 对应数据
     */
    private String data;
    /**
     * 映射类型
     */
    @JSONField(serializeUsing = FieldTypeCodec.class,deserializeUsing = FieldTypeCodec.class)
    private FiledTypeEnum filedType;

    public LogContentBean() {
    }

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public FiledTypeEnum getFiledType() {
        return filedType;
    }

    public void setFiledType(FiledTypeEnum filedType) {
        this.filedType = filedType;
    }
}
