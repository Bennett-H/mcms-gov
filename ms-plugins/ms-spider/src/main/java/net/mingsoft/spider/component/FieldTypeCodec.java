/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import net.mingsoft.spider.constant.e.FiledTypeEnum;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 序列化和反序列化的时候能够自动将字符转换成枚举类
 * fastjson拓展功能
 * see net.mingsoft.spider.bean.LogContentBean#filedType
 */
public class FieldTypeCodec implements ObjectSerializer, ObjectDeserializer {

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        return null == value ? null : (T) FiledTypeEnum.findFiledTypeEnumByStr(value.toString());
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }


    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        serializer.write(((FiledTypeEnum)object).type);
    }
}
