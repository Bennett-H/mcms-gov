/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.util;

import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.gov.bean.ESContentBean;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mdiy.util.ParserUtil;

import java.io.File;

public class ESContentBeanUtil {
    public static void fixESContentBean(ESContentBean bean, ContentEntity content, CategoryEntity category) {
        bean.setType("content");
        bean.setId(content.getId());
        //将需要同步到es库的字段逐个赋值
        bean.setTitle(content.getContentTitle());
        bean.setShortTitle(content.getContentShortTitle());
        bean.setContent(content.getContentDetails());
        bean.setAuthor(content.getContentAuthor());
        bean.setDate(content.getContentDatetime());
        bean.setTypeId(content.getCategoryId());
        bean.setDescrip(content.getContentDescription());
        bean.setSort(content.getContentSort());
        bean.setLitPic(content.getContentImg());
        bean.setFlag(content.getContentType());
        bean.setCreateTime(content.getCreateDate());
        // 根据短链配置处理url值
        Boolean shortSwitch = ConfigUtil.getBoolean("静态化配置", "shortSwitch");
        String urlPrefix = "";
        if (category.getCategoryType().equals(CategoryTypeEnum.COVER.toString())) {
            urlPrefix = shortSwitch ? category.getCategoryPinyin() : category.getCategoryPath() + File.separator+"index";

        }else {
            urlPrefix = shortSwitch ? content.getId() : category.getCategoryPath() + File.separator + content.getId();
        }
        bean.setUrl(urlPrefix + ParserUtil.HTML_SUFFIX);
        // 有appId则存放
        AppEntity app = BasicUtil.getWebsiteApp();
        if (app != null) {
            // 查询时会自动拼接appID,也就是当前session,所以这里可以直接设置
            bean.setAppId(app.getId());
        }
        bean.setStyles(content.getContentStyle());
        bean.setProgressStatus(content.getProgressStatus());
        // 设置栏目父ID集合
        bean.setParentIds(category.getCategoryParentIds());
        bean.setTypeTitle(category.getCategoryTitle());
        bean.setTypeShortTitle(category.getCategoryShortTitle());
    }

}
