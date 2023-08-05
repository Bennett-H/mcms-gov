/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.basic.action;


import io.swagger.annotations.Api;
import net.mingsoft.base.entity.ResultData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 枚举类
 */
@Api(tags = {"后端-枚举类接口"})
@Controller
@RequestMapping("/${ms.manager.path}/basic/enum")
public class EnumAction extends BaseAction {

    /**
     * 方便前端获取枚举类的list数据
     */
    @ApiIgnore
    @GetMapping("/list")
    @ResponseBody
    public ResultData list(String allClassPath) {
        List<HashMap<String, String>> list = new ArrayList<>();
        try {
            Class _class = Class.forName(allClassPath);
            Object[] enumConstants = _class.getEnumConstants();
            Arrays.stream(enumConstants).forEach(e-> {
                HashMap<String, String> map = new HashMap<>();
                map.put("value", e.toString().toLowerCase());
                try {
                    Method label = e.getClass().getMethod("getLabel");
                    map.put("label", label.invoke(e).toString());
                } catch (NoSuchMethodException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
                list.add(map);
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ResultData.build().success(list);
    }
}
