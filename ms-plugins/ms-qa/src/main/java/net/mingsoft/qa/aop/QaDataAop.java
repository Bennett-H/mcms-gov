package net.mingsoft.qa.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.qa.biz.IQaBiz;
import net.mingsoft.qa.entity.QaEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Administrator
 * 此AOP是处理当前管理创建栏目，没有此栏目的增删改权限，需要重新在超级管理员重新赋权的需求
 */

@Aspect
@Component("QaDataAop")
public class QaDataAop extends net.mingsoft.basic.aop.BaseAop {

    @Autowired
    private IQaBiz qaBiz;

    /**
     * 问卷接口切面
     */
    @Pointcut("execution(* net.mingsoft.qa.action.QaDataAction.save(..)) || execution(* net.mingsoft.qa.action.web.QaDataAction.save(..))")
    public void save() {}

    /**
     * 点击保存时拦截
     */
    @Before("save()")
    public void save(JoinPoint jp) throws BusinessException {
        String modelId = BasicUtil.getString("modelId");
        QaEntity qa = qaBiz.getById(modelId);
        //设置ip,地址,网络设备等参数
        String ip = BasicUtil.getIp();

        String answerLimitJson = qa.getAnswerLimit();
        Map<String, Object> jsonMap = JSONUtil.toBean(answerLimitJson,Map.class);
        // 没有设置答题限制次数
        if (CollUtil.isEmpty(jsonMap)) {
            throw new BusinessException("没有设置答题限制次数!");
        }
        String type = jsonMap.get("type").toString();
        int value = Integer.parseInt(jsonMap.get("value").toString());
        Map<String, Object> qaMap = new HashMap<>();
        List<Map> whereList = new ArrayList<>();
        // 默认筛选IP
        qaMap.put("el", "eq");
        qaMap.put("field", "qa_ip");
        qaMap.put("value", ip);
        whereList.add(qaMap);
        switch (type) {
            case "hour":
                Map<String, Object> hourMap = new HashMap<>();
                hourMap.put("el", "gt");
                hourMap.put("field", "create_date");
                hourMap.put("type", "date");
                // 筛选上一个小时之后的数据
                hourMap.put("value", DateUtil.offsetHour(new Date(), -1));
                whereList.add(hourMap);
                break;
            case "day":
                Map<String, Object> dayMap = new HashMap<>();
                dayMap.put("el", "gt");
                dayMap.put("field", "create_date");
                dayMap.put("type", "date");
                // 筛选上一天之后的数据
                dayMap.put("value", DateUtil.offsetDay(new Date(), -1));
                whereList.add(dayMap);
                break;
            default:
                break;
        }
        int count = qaBiz.countBySQL(qa.getQaTableName(), null, whereList);
        // 超出答题次数
        if (count >= value) {
            throw new BusinessException("超出答题限制次数!");
        }
        // 问卷时间限制
        if(!DateUtil.isIn(new Date(),qa.getStartTime(),qa.getEndTime())){
            throw new BusinessException("当前时间不支持问卷提交!");
        }

    }
}
