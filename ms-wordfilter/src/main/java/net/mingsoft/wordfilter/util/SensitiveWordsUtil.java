/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.wordfilter.util;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.wordfilter.annotation.Sensitive;
import net.mingsoft.wordfilter.biz.ISensitiveWordsBiz;
import net.mingsoft.wordfilter.constant.e.WordTypeEnum;
import net.mingsoft.wordfilter.entity.SensitiveWordsEntity;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 敏感词过滤工具,采用前缀树结构
 */
public class SensitiveWordsUtil {
    //记录日志
    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordsUtil.class);

    //前缀树的根节点
    private static final TrieNode rootNode = new TrieNode();

    public static final HashMap<String, String> WORDS_MAP = new HashMap<>();


    /**
     * 把一个敏感词添加到前缀树中去
     * @param keyword 敏感词名称
     */
    public static void addKeyWord(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0 ; i< keyword.length() ; ++i){
            char key = keyword.charAt(i);
            //找子节点
            TrieNode subNode = tempNode.getSubNode(key);
            if(subNode == null){//如果没有这个子节点
                //初始化子节点；
                subNode = new TrieNode();
                tempNode.addSubNode(key,subNode);
            }
            //指向子节点，进入下一次循环
            tempNode = subNode;
            if(i == keyword.length() -1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 删除前缀树对应的敏感词
     * @param keyword 敏感词名称
     */
    public static void removeKeyWord(String keyword) {
        TrieNode tempNode = rootNode;
        for(int i = 0 ; i< keyword.length() ; ++i){
            char key = keyword.charAt(i);
            //找子节点
            TrieNode subNode = tempNode.getSubNode(key);
            //指向子节点，进入下一次循环
            tempNode = subNode;
            if(i == keyword.length() -1){
                System.out.println(i);
                tempNode.setKeyWordEnd(false);
                break;
            }
            if(subNode == null){
                // 如果没有这个子节点说明前缀树不存在该词,跳过循环
                break;
            }
        }

    }


    /**
     * 检测文本中包含的敏感词信息，与自动替换敏感词追求最大粒度替换的规则不同，此方法检测追求文章中存在的最小粒度敏感词
     * 例如：中国人民-->**，这是一个敏感词及替换词为**；在检测时会检测出 "中国"、"人民"、"中国人民" 三个敏感词
     * @param fields 待检测的文章字段数组
     * @param arg 文章实体对象
     * @return 返回list集合，集合中元素为map，保存检测出的敏感词以及被替换词，便于在前端页面中提示
     */
    public static List<Map<String,String>> filter(Field[] fields, BaseEntity arg) throws IllegalAccessException {
        // 用于存放敏感字与替换词的 List集合
        List<Map<String, String>> wordList = new ArrayList<>();
        // 注入敏感词业务层
        ISensitiveWordsBiz sensitiveWordsBiz = SpringUtil.getBean(ISensitiveWordsBiz.class);
        // 查询所有敏感词与对应替换词
        LambdaQueryWrapper<SensitiveWordsEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensitiveWordsEntity::getWordType, WordTypeEnum.SENSITIVE.toString());
        List<SensitiveWordsEntity> list = sensitiveWordsBiz.list(wrapper);
        // 预先定义集合中map元素
        Map<String, String> map = null;
        // 遍历文章所有字段，检测有敏感词注解的字段的值包含多少敏感词
        for (Field field : fields) {
            // 记录访问权限
            boolean flag = field.isAccessible();
            // 修改成员属性为可以访问
            field.setAccessible(true);
            // 判断字段是否存在敏感词注解
            if (field.getAnnotation(Sensitive.class) == null) {
                // 没有标注注解的字段跳过
                continue;
            }
            // 获取属性的文本值
            String str = (String) field.get(arg);
            if (StringUtils.isBlank(str)) {
                continue;
            }
            // 遍历敏感词，判断字段值中是否含有敏感词
            for (SensitiveWordsEntity sensitiveWordsEntity : list) {
                if(StringUtils.isNotBlank(sensitiveWordsEntity.getWord())) {
                    // 是否存在敏感词的标识
                    boolean isSensitiveWord = str.contains(sensitiveWordsEntity.getWord());
                    if (isSensitiveWord){
                        map = new HashMap<>(2);

                        // 敏感词
                        map.put("ori_frag", sensitiveWordsEntity.getWord());
                        // 替换词
                        map.put("correct_frag", sensitiveWordsEntity.getReplaceWord());
                        wordList.add(map);
                    }
                }

            }
            //恢复访问权限
            field.setAccessible(flag);
        }
        return wordList;
    }



    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return    过滤后的文本
     */
    public static String filter(String text) {
        if(StringUtils.isBlank(text)){
            return text;
        }
        //指针1 ，指向树
        TrieNode tempNode = rootNode;
        //指针2 指向字符串的慢指针，一直往前走
        int begin = 0;
        //指针3 指向字符串的快指针，往后走检查，如果不是就归位。
        int position = 0;
        //结果
        StringBuilder result = new StringBuilder();
        // 用于存放暂时的敏感词
        // 有 [中国] [中国人] [中国人民] 三个敏感词,用于存放[中国],并进入到[人]判断,存放上一个最大匹配的敏感词
        // 效果是有[中国人民]替换[中国人民], 有[中国人们]替换[中国人], [中国梦]替换[中国]
        String momentSensitiveWord = null;
        while(position < text.length()){
            char c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                //若指针处于根节点。则将此符号计入结果，让指针向下走一步。
                if(tempNode == rootNode){
                    result.append(c);
                    begin++;
                }
                //无论结构在开头或中间指针3都向下走一步。
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                // 判断 momentSensitiveWord 是否为空
                if (StringUtils.isNotBlank(momentSensitiveWord)) {
                    // 不为空表示有暂存的敏感词,需要先压暂存的敏感词
                    result.append(WORDS_MAP.get(momentSensitiveWord));
                    // begin指向到最新的地方
                    begin += momentSensitiveWord.length();
                    momentSensitiveWord = null;
                }else {
                    // 以begin为开头的字符串不存在敏感词
                    result.append(text.charAt(begin));
                    //进入下一个位置
                    position = ++begin;
                }
                // 重新指向根节点。
                tempNode  = rootNode;

            }else if(tempNode.isKeyWordEnd()){
                //发现了敏感词,判断是否有更长的敏感词
                if (CollUtil.isEmpty(tempNode.subNode)) {
                    // 没有更长的敏感词,将begin-position中的字符串替换
                    result.append(WORDS_MAP.get(text.substring(begin,position + 1).replace(" ","")));
                    momentSensitiveWord = null;
                    // 进入下一个位置
                    begin = ++ position;
                    tempNode = rootNode;
                }else {
                    momentSensitiveWord = text.substring(begin,position + 1);
                    //检查下一个字符
                    position++;
                }

            }else{
                //检查下一个字符
                position++;
            }
        }
        if (StringUtils.isNotBlank(momentSensitiveWord) && StringUtils.isNotBlank(WORDS_MAP.get(momentSensitiveWord.replace(" ","")))) {
            result.append(WORDS_MAP.get(momentSensitiveWord));
        }else {
            //将最后一批字符计入结构
            result.append(text.substring(begin));
        }
        return result.toString();
    }

    /**
     * 判断是否为符号,是的话返回true，不是的话返回false
     */
    public static boolean isSymbol(Character c){
        //!CharUtils.isAsciiAlphanumeric(c)判断合法字符
        //c < 0x2E80 || c > 0x9fff 东亚文字的范围是0x2E80到0x9fff
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9fff);
    }

    /**
     * 插入一条敏感词到前缀树
     * @param word 敏感词名称
     * @param replaceWord 替换词
     */
    public static void setSensitiveMap(String word,String replaceWord){
        WORDS_MAP.put(word,replaceWord);
        SensitiveWordsUtil.addKeyWord(word);
    }

    /**
     * 删除前缀树的一条数据
     * @param word 敏感词名称
     */
    public static void removeSensitiveWord(String word){
        WORDS_MAP.remove(word);
        SensitiveWordsUtil.removeKeyWord(word);
    }

    /**
     *  定义一个内部类，作为前缀树的结构
     */
    private static class TrieNode{
        //关键词结束的标识
        private boolean isKeyWordEnd = false;
        //子节点(key 代表下级的节点字符， value是下级节点)
        private Map<Character, TrieNode> subNode = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        /**
         * 添加子节点
         * @param key 下标
         * @param subNode 前缀树
         */
        public void addSubNode(Character key,TrieNode subNode){
            this.subNode.put(key,subNode);
        }

        /**
         * 获取子节点
         * @param key 下标
         */
        public TrieNode getSubNode(Character key){
            return subNode.get(key);
        }
    }


}
