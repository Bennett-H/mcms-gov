/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.baidu.ai.action;

import cn.hutool.json.JSONUtil;
import com.baidu.aip.nlp.AipNlp;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.action.BaseAction;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.wordfilter.biz.ISensitiveWordsBiz;
import net.mingsoft.wordfilter.constant.e.WordTypeEnum;
import net.mingsoft.wordfilter.entity.SensitiveWordsEntity;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"后端-百度AI接口"})
@Controller("aipNlpAction")
@RequestMapping("/${ms.manager.path}/baidu/ai/nlp")
public class AipNlpAction extends BaseAction {
    private static AipNlp client = null;

    public AipNlpAction() {

    }

    @ApiOperation(value = "错词检测接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentText", value = "检测内容", required = true, paramType = "query"),
    })
    @PostMapping("/ecnet")
    @ResponseBody
    public ResultData ecnet(HttpServletRequest request, HttpServletResponse response) {

        String appId = ConfigUtil.getString("百度AI配置","appId");
        String apiKey = ConfigUtil.getString("百度AI配置","apiKey");
        String secretKey = ConfigUtil.getString("百度AI配置","secretKey");

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(apiKey) || StringUtils.isBlank(secretKey)) {
            return ResultData.build().error("百度AI配置为空,请检查百度AI配置!");
        }

        client = new AipNlp(appId, apiKey, secretKey);
        String text = BasicUtil.getString("contentText");
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 传入可选参数调用接口
        HashMap<String, Object> options = new HashMap<>();

        // 调用接口
        List<String> strs = getStrList(text,150);
        List<Map> errList = new ArrayList<>();
        strs.forEach(str->{
            JSONObject res = client.ecnet(str, options);
            try {
                JSONArray err = res.getJSONObject("item").getJSONArray("vec_fragment");
                if(err.length()>0) {
                    for(int i=0; i < err.length(); i++){
                        errList.add(JSONUtil.toBean(err.get(i).toString(), Map.class));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        // 增加敏感词与替换词提示
        errList.addAll(checkWord(text));

        if(errList.size()>0) {
            return new ResultData().success().data(errList);
        }
        return new ResultData().success();
    }

    public static List<Map<String, String>> checkWord(String text){
        // 用于存放敏感字与替换词的 List
        List<Map<String, String>> wordList = new ArrayList<>();
        if (StringUtils.isEmpty(text)) {
            return wordList;
        }
        // 注入敏感词业务层
        ISensitiveWordsBiz sensitiveWordsBiz = SpringUtil.getBean(ISensitiveWordsBiz.class);
        // 查询纠错词与提示词
        LambdaQueryWrapper<SensitiveWordsEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensitiveWordsEntity::getWordType, WordTypeEnum.CORRECTION.toString());
        List<SensitiveWordsEntity> list = sensitiveWordsBiz.list(wrapper);
        for (SensitiveWordsEntity sensitiveWordsEntity : list) {
            boolean flag = text.contains(sensitiveWordsEntity.getWord());
            if (flag){
                Map<String, String> map = new HashMap<>(2);
                // 敏感词
                map.put("ori_frag", sensitiveWordsEntity.getWord());
                // 替换词
                map.put("correct_frag", sensitiveWordsEntity.getReplaceWord());
                wordList.add(map);
            }
        }
        return wordList;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString
     *            原始字符串
     * @param length
     *            指定长度
     * @return
     */
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }


    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString
     *            原始字符串
     * @param length
     *            指定长度
     * @param size
     *            指定列表大小
     * @return
     */
    public static List<String> getStrList(String inputString, int length,
                                          int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str
     *            原始字符串
     * @param f
     *            开始位置
     * @param t
     *            结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }
}
