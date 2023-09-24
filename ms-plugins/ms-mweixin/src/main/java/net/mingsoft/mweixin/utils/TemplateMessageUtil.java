/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReUtil;
import freemarker.template.TemplateException;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import net.mingsoft.mdiy.util.ParserUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 模板消息工具类
 */
public class TemplateMessageUtil {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TemplateMessageUtil.class);
    /**
     * 微信模板参数默认颜色
     */
    private static String WX_TEMPLATE_DATA_COLOR = "#000000";

    /**
     *  发送模板消息
     *  userOpenIds 用户微信唯一ID数组 ，url用户点击模板消息后跳转url ，wxMpTemplateData 模板消息内容
     */

    public static void send(WxMpService wxMpService, List<WxMpTemplateData> wxMpTemplateData, String url, String templateId, String... userOpenIds){
        WxMpTemplateMessage templateMessage = null;
        // 异常情况下不能影响下一个人的发送
        for (String userOpenId : userOpenIds) {
            try {
                templateMessage = WxMpTemplateMessage.builder()
                        .toUser(userOpenId)
                        .templateId(templateId)
                        .url(url)
                        .data(wxMpTemplateData)
                        .build();
                wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            } catch (WxErrorException e) {
                LOGGER.debug("发送消息给用户: {} 失败");
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据模板名称及关键词集合获取wx模板数据
     * @param content 模板内容 必须
     * @param templateKeyword 模板关键字 必须
     * @param wordList 关键字值集合 必须
     * @return 消息模板参数集合
     */
    public static List<WxMpTemplateData> buildParams(String content,String templateKeyword,Map<String,String> wordList){
        if (StringUtils.isBlank(content) || StringUtils.isBlank(templateKeyword) || CollectionUtil.isEmpty(wordList)){
            LOGGER.debug("非法参数，有参数为空");
            return null;
        }
        if(StringUtils.isBlank(templateKeyword)){
            LOGGER.debug("模板关键字参数为空");
            return null;
        }
        String words = "";
        try {
            words = ParserUtil.rendering(wordList, templateKeyword);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            LOGGER.debug("模板关键字配置有误");
            e.printStackTrace();
        }
        Pattern pattern = Pattern.compile( "\\{\\{(.*)?\\.DATA}}");
        List<String> keyList = ReUtil.getAllGroups(pattern, content, false, true);
        return buildParams(keyList,Arrays.asList(words.split(",")));
    }


    /**
     * TODO: 2023/6/19  由于微信规则变动，消息模板不再支持头尾的first和remark参数，此方法只用于组装关键字参数
     * @param wordList 关键字值的集合，注意不能有null值，但是可以传空串（不推荐，模板一般都是关键字和参数一一对应，无对应值仍然显示关键字）
     * @param keyList 关键字key的集合，注意不能有null值，不能为空
     * @return 消息数据集合，如果返回值为null则表示传递的入参不合法，如集合中元素都为null或者集合为空
     */
    public static List<WxMpTemplateData> buildParams(List<String> keyList,List<String> wordList){
        if (CollectionUtil.isEmpty(wordList) || CollectionUtil.isEmpty(keyList)){
            LOGGER.debug("关键词的 key 或 值 为空");
            return null;
        }
        String keyword = null;
        WxMpTemplateData wxMpTemplateData = null;
        List<WxMpTemplateData> templateDataList = new ArrayList<>();
        // 取最小值开始遍历
        int size = Math.min(wordList.size(), keyList.size());
        for (int i = 0; i < size; i++) {
            keyword = wordList.get(i);
            wxMpTemplateData = new WxMpTemplateData();
            // 做判null即可，允许空串
            if (keyword!=null){
                // 名称随顺序递增，从keyword1开始算
                wxMpTemplateData.setName(keyList.get(i));
                wxMpTemplateData.setValue(keyword);
                wxMpTemplateData.setColor(WX_TEMPLATE_DATA_COLOR);
                templateDataList.add(wxMpTemplateData);
            }
        }
        if (keyword!=null){
            return templateDataList;
        }
        LOGGER.debug("没有在关键词值集合中找到有效参数");
        return null;
    }

    /**
     * 注意第一、二个入参必须是特殊的first、remark，后面可变长度参数是模板所需关键值，原则上不限制个数（23.06.16 微信官方目前限制五个关键值）
     * @param firstStr 模板开篇关键字
     * @param remarkStr 模板结尾关键字
     * @param keywords 模板关键值
     * @return 模板集合
     */
    // TODO: 2023/6/19 微信官方已不支持头尾，已增加其他方法 buildParams(List<String> keywords)
    @Deprecated
    public static List<WxMpTemplateData> buildParams(String firstStr,String remarkStr,String... keywords){
        List<WxMpTemplateData> data = new ArrayList<>();

        WxMpTemplateData firstData = new WxMpTemplateData();
        firstData.setName("first");
        firstData.setColor("#000000");
        firstData.setValue(firstStr);

        WxMpTemplateData remarkData = new WxMpTemplateData();
        remarkData.setName("remark");
        remarkData.setColor("#000000");
        remarkData.setValue(remarkStr);
        // 拼装数组
        WxMpTemplateData[] wxMpTemplateDatas = new WxMpTemplateData[keywords.length+2];
        for (int i = 0; i < keywords.length; i++) {
            WxMpTemplateData wxMpTemplateData = new WxMpTemplateData();
            wxMpTemplateData.setName("keyword"+(i+1));
            wxMpTemplateData.setColor("#000000");
            wxMpTemplateData.setValue(keywords[i]);
            wxMpTemplateDatas[i] = wxMpTemplateData;
        }
        wxMpTemplateDatas[wxMpTemplateDatas.length-1] = firstData;
        wxMpTemplateDatas[wxMpTemplateDatas.length-2] = remarkData;
        data.addAll(Arrays.asList(wxMpTemplateDatas));
        return data;
    }


}
