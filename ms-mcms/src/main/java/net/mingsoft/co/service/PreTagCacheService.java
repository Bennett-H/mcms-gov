/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.service;



import net.mingsoft.co.cache.ContentCache;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.bean.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;


@Service
public class PreTagCacheService extends BaseTagClassService {

    @Autowired
    public ContentCache contentCache;

    @Override
    public Object excute(Map map) {
        try {
            Object obj = map.get(ParserUtil.PAGE);
            if (obj != null) {
                PageBean page = (PageBean) obj;
                if (StringUtils.isNotBlank(page.getPreId())) {
                    return contentCache.get(page.getPreId());
                }
            }
            return null;
        } catch (Exception e) {
            LOG.warn("pre标签解析失败");
            e.printStackTrace();
        }
        return null;
    }
}
