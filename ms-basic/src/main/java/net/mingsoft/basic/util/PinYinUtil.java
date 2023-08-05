/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.util;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * 中文转拼音工具类
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2020/6/16 14:40
 */
public class PinYinUtil {


    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param inputString
     * @return
     */
    public static String getPingYin(String inputString)  {
        if (!StringUtils.isBlank(inputString)) {
            try {
                return  PinyinHelper.convertToPinyinString(inputString,"", PinyinFormat.WITHOUT_TONE);
            } catch (PinyinException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
