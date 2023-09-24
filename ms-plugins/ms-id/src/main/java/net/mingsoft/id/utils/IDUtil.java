/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.id.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.id.biz.IRuleDataBiz;
import net.mingsoft.id.biz.impl.RuleBizImpl;
import net.mingsoft.id.entity.RuleDataEntity;
import net.mingsoft.id.entity.RuleEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author by 铭飞开源团队
 * @Description TODO
 * @date 2020/5/26 17:38
 */
public class IDUtil {

    /** 获取编码
     * @param type 类型
     * @param number 序号
     * @return
     */
    public static String getId(String type, Long number) {
       return getId(type,number,null);
    }

    /** 获取编码
     * @param type 类型
     * @param number 序号
     * @param env 变量
     * @return
     */
    public static String getId(String type, Long number, Map env) {
        IRuleDataBiz ruleDataBiz = SpringUtil.getBean(IRuleDataBiz.class);
        RuleBizImpl ruleBiz = SpringUtil.getBean(RuleBizImpl.class);
        RuleEntity ruleEntity = new RuleEntity();
        ruleEntity.setIrName(type);
        RuleEntity rule = (RuleEntity) ruleBiz.getEntity(ruleEntity);
        StringBuilder ruleData = new StringBuilder();
        if (rule != null) {
            RuleDataEntity ruleDataEntity = new RuleDataEntity();
            ruleDataEntity.setIrId(Integer.parseInt(rule.getId()));
            List<RuleDataEntity> list = ruleDataBiz.query(ruleDataEntity);
            for (RuleDataEntity x : list) {
                try {
                    switch (x.getIrdType()) {
                        //固定文字
                        case "text":
                            //分隔符
                        case "separator":
                            ruleData.append(x.getIrdOption());
                            break;
                        //日期变量
                        case "date":
                            ruleData.append(DateUtil.format(new Date(), x.getIrdOption()));
                            break;
                        //序号
                        case "number":
                            StringBuilder format = new StringBuilder();
                            //拼接格式化字符串
                            for (int i = 0; i < Integer.parseInt(x.getIrdOption()); i++) {
                                format.append("0");
                            }
                            ruleData.append(NumberUtil.decimalFormat(format.toString(), number));
                            break;
                        //自定义变量
                        case "custom":
                            if (env!=null&&env.containsKey(x.getIrdOption())) {
                                ruleData.append(env.get(x.getIrdOption()));
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ruleData.toString();
    }
}
