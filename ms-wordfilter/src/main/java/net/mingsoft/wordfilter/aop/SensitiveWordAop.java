/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */










package net.mingsoft.wordfilter.aop;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.LogUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.wordfilter.annotation.Sensitive;
import net.mingsoft.wordfilter.entity.SensitiveWordsEntity;
import net.mingsoft.wordfilter.util.SensitiveWordsUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author: wjj
 * @Description: 敏感词aop拦截, 默认拦截保存和更新接口，其他接口可以使用@SensitiveWord注解标记
 * @Date: Create in 2021/01/18 14:11
 */
@Component("SensitiveWordAop")
@Aspect
public class SensitiveWordAop extends BaseAop {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveWordAop.class);

    // 因为co重写了BusinessTypeEnum枚举类,敏感词只依赖basic,所以此处使用常量
    private static final String CONTENT_UPDATE = "content_update";

    /*
     * 默认只拦截标注了@SensitiveWord的方法,如需拦截所有save和update只需打开下方注释
     *
    @Before("execution(*  net.mingsoft..*Action.save(..))")
    public void save(JoinPoint joinPoint) throws IllegalAccessException {
        startReplace(joinPoint);
    }

    @Before("execution(* net.mingsoft..*Action.update(..))")
    public void update(JoinPoint joinPoint) throws IllegalAccessException {
        startReplace(joinPoint);
    }

     */
    @Pointcut("@annotation(net.mingsoft.wordfilter.annotation.SensitiveWord)")
    public void sensitiveWord() {
    }


    @Before(value = "sensitiveWord()")
    public void sensitiveWord(JoinPoint joinPoint) throws IllegalAccessException, IOException {
        //检查是否开启替换词开关
        if(!ConfigUtil.getBoolean("安全设置", "isEnableWord")){
            LOG.debug("替换词配置关闭了");
            return;
        }
        startReplace(joinPoint);
    }


    private void startReplace(JoinPoint joinPoint) throws IllegalAccessException, IOException {
        BaseEntity arg = this.getType(joinPoint, BaseEntity.class, true);
        if (arg == null || arg instanceof SensitiveWordsEntity) {
            return;
        }
        Field[] fields = arg.getClass().getDeclaredFields();
        //若关闭自动替换敏感词则检测
        if (!ConfigUtil.getBoolean("安全设置","enableAutoWord")){
            LOG.debug("准备校验文章存在多少敏感词");
            List<Map<String, String>> list = SensitiveWordsUtil.filter(fields,arg);
            int size = list.size();
            //若检测出敏感词，则将检测出的信息通过异常抛出
            if (size > 0){
                LOG.debug("共检测出:{}个敏感词",size);
                throw new BusinessException("共检测出"+size+"个敏感词",list);
            }else {
                //若无敏感词则无需继续往下过滤敏感词
                LOG.debug("未检测到敏感词");
                return;
            }
        }
        for (Field field : fields) {
            //记录访问权限
            boolean flag = field.isAccessible();
            //修改成员属性为可以访问
            field.setAccessible(true);
            //获取字段的注解
            Sensitive annotation = field.getAnnotation(Sensitive.class);
            if (annotation == null) {
                //没有标注注解的字段跳过
                continue;
            }

            //是否是html内容
            boolean isHtml = annotation.isHtml();
            if (isHtml) {
                String str = (String) field.get(arg);
                if (StringUtils.isBlank(str)) {
                    //恢复访问权限
                    field.setAccessible(flag);
                    continue;
                }
                // 防止空格被剔除
                str = str.replaceAll("&nbsp;","ms_nbsp");
                // 防止内容没有根标签,解析报错
                Document document = Jsoup.parse(str);
                // 解决存入数据库html标签未正确闭合问题
                document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
                document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
                //获取文档所有的子节点
                List<Element> elements = this.getAllNode(document);
                if (field.getType() == String.class && StringUtils.isNotEmpty((CharSequence) field.get(arg))) {
                    elements.forEach(element -> {
                        List<Node> content = element.childNodes();
                        content.forEach(node -> {
                            if (node instanceof TextNode){
                                TextNode textNode = (TextNode)node;
                                String text = textNode.text();
                                String replace = SensitiveWordsUtil.filter(text);
                                if (StringUtils.isNotBlank(text) && !text.equalsIgnoreCase(replace)) {
                                    LOGGER.debug("{} 字段的敏感词被替换了", field);
                                    LogUtil.log("敏感词替换",
                                            StrUtil.format("{}字段的敏感词[{}]被替换成了[{}]", field,text,replace),
                                            CONTENT_UPDATE,
                                            arg.getId());
                                }
                                textNode.text(replace);
                            }
                        });
                    });
                    field.set(arg, document.body().html().replaceAll("ms_nbsp","&nbsp; "));
                }

            } else {
                if (field.getType() == String.class && StringUtils.isNotEmpty((CharSequence) field.get(arg))) {
                    String str = (String) field.get(arg);
                    String replace = SensitiveWordsUtil.filter(str);
                    if (StringUtils.isNotBlank(str) && !str.equalsIgnoreCase(replace)) {
                        LOGGER.debug("{}字段的敏感词被替换了", field);
                        LogUtil.log("敏感词替换",
                                StrUtil.format("{}字段的敏感词{}被替换成了{}", field,str,replace),
                                CONTENT_UPDATE,
                                arg.getId());
                        field.set(arg, replace);
                    }
                }
            }

            //恢复访问权限
            field.setAccessible(flag);
        }
    }


    /**
     * 获取xml文档的所有子节点
     * @param doc xml文档对象
     * @return
     */
    private List<Element> getAllNode(Document doc) {
        //根节点
        Element root = (Element)doc.body();
        //子节点
        List<Element> childElements = root.children();

        //所有子节点
        List<Element> allElement = new ArrayList<>();
        //遍历子节点
        allElement = getAllElements(childElements, allElement);
        return allElement;
    }

    /**
     * 获取节点的所有子节点
     * @param childElements
     * @param allElement
     * @return
     */
    private List<Element> getAllElements(List<Element> childElements, List<Element> allElement) {
        for (Element ele : childElements) {
            allElement.add(ele);
            if (ele.children().size() > 0) {
                allElement = getAllElements(ele.children(), allElement);
            }
        }

        return allElement;
    }
}
