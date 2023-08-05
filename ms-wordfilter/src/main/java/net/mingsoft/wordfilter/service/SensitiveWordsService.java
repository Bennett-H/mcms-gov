/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.wordfilter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.wordfilter.biz.ISensitiveWordsBiz;
import net.mingsoft.wordfilter.constant.e.WordTypeEnum;
import net.mingsoft.wordfilter.entity.SensitiveWordsEntity;
import net.mingsoft.wordfilter.util.SensitiveWordsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Component
public class SensitiveWordsService {



    @Autowired
    private ISensitiveWordsBiz sensitiveWordsBiz;

    /**
     * 初始化敏感词数据
     */
    @PostConstruct
    public void init(){
        LambdaQueryWrapper<SensitiveWordsEntity> wrapper = new LambdaQueryWrapper<>();
        // 只对敏感词进行替换
        wrapper.eq(SensitiveWordsEntity::getWordType, WordTypeEnum.SENSITIVE.toString());
        List<SensitiveWordsEntity> list = sensitiveWordsBiz.list(wrapper);
        //把所有敏感词添加到前缀树
        list.stream().filter(sensitiveWordsEntity -> StringUtils.isNotBlank(sensitiveWordsEntity.getWord()))
                .forEach(sensitiveWordsEntity -> {
                    SensitiveWordsUtil.WORDS_MAP.put(sensitiveWordsEntity.getWord(),sensitiveWordsEntity.getReplaceWord());
                    SensitiveWordsUtil.addKeyWord(sensitiveWordsEntity.getWord());
                });
    }
}
