/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.cms.entity;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.co.bean.TemplateBean;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类实体
 *
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订： 重新co下的栏目，增加审批级数字段<br/>
 */
@TableName("cms_category")
public class CategoryEntity extends BaseEntity {

    private static final long serialVersionUID = 1574925152750L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 栏目管理名称
     */
    private String categoryTitle;
    /**
     * 栏目副标题
     */
    private String categoryShortTitle;
    /**
     * 栏目别名
     */
    private String categoryPinyin;
    /**
     * 所属栏目
     */
    @TableField(insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.IGNORED, whereStrategy = FieldStrategy.NOT_EMPTY)
    private String categoryId;
    /**
     * 栏目管理属性
     */
    private String categoryType;
    /**
     * 自定义顺序
     */
    private Integer categorySort;
    /**
     * 列表模板
     */
    private String categoryListUrl;
    /**
     * 内容模板
     */
    private String categoryUrl;
    /**
     * 列表多模版{name:"内网模版",template:"net",file:"内容.htm"}
     */
    private String categoryListUrls;
    /**
     * 内容多模版{name:"内网模版",template:"net",file:"内容.htm"}
     */
    private String categoryUrls;
    /**
     * 栏目管理关键字
     */
    private String categoryKeyword;
    /**
     * 栏目管理描述
     */
    private String categoryDescrip;
    /**
     * banner图
     */
    private String categoryImg;

    /**
     * 栏目小图
     */
    private String categoryIco;

    /**
     * 是否显示
     */
    private String categoryDisplay;
    /**
     * 是否可被搜索
     */
    private String categoryIsSearch;
    /**
     * 自定义链接
     */
    private String categoryDiyUrl;
    /**
     * 文章管理的内容模型id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String mdiyModelId;
    /**
     * 栏目管理的内容模型id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String mdiyCategoryModelId;
    /**
     * 字典对应编号
     */
    private Integer dictId;
    /**
     * 栏目属性
     */
    private String categoryFlag;
    /**
     * 栏目路径
     */
    private String categoryPath;
    /**
     * 父类型编号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String categoryParentIds;

    /**
     * 叶子节点
     */
    private Boolean leaf;

    /**
     * 顶级id,区别于categoryId字段，topId的默认值为0
     */
    private String topId;

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public String getTopId() {
        return topId;
    }

    public void setTopId(String topId) {
        this.topId = topId;
    }


    public String getMdiyCategoryModelId() {
        return mdiyCategoryModelId;
    }

    /**
     * 设置栏目模型id
     * @param mdiyCategoryModelId
     */
    public void setMdiyCategoryModelId(String mdiyCategoryModelId) {
        this.mdiyCategoryModelId = mdiyCategoryModelId;
    }
    /**
     * 设置栏目管理名称，不允许含有空格
     */
    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle.trim();
    }

    /**
     * 获取栏目管理名称,不允许含有空格
     */
    public String getCategoryTitle() {
        return StringUtils.isNotBlank(this.categoryTitle)?this.categoryTitle.trim():this.categoryTitle;
    }

    /**
     * 设置所属栏目
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryPinyin() {
        return categoryPinyin;
    }

    public void setCategoryPinyin(String categoryPinyin) {
        this.categoryPinyin = categoryPinyin;
    }

    /**
     * 获取所属栏目
     */
    public String getCategoryId() {
        return this.categoryId;
    }

    /**
     * 设置栏目管理属性
     */
    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    /**
     * 获取栏目管理属性
     */
    public String getCategoryType() {
        return this.categoryType;
    }

    /**
     * 设置自定义顺序
     */
    public void setCategorySort(Integer categorySort) {
        this.categorySort = categorySort;
    }

    /**
     * 获取自定义顺序
     */
    public Integer getCategorySort() {
        return this.categorySort;
    }

    /**
     * 设置列表模板
     */
    public void setCategoryListUrl(String categoryListUrl) {
        this.categoryListUrl = categoryListUrl;
    }

    /**
     * 获取列表模板
     */
    public String getCategoryListUrl() {
        return this.categoryListUrl;
    }

    /**
     * 设置内容模板
     */
    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    /**
     * 获取内容模板
     */
    public String getCategoryUrl() {
        return this.categoryUrl;
    }

    /**
     * 设置栏目管理关键字
     */
    public void setCategoryKeyword(String categoryKeyword) {
        this.categoryKeyword = categoryKeyword;
    }

    /**
     * 获取栏目管理关键字
     */
    public String getCategoryKeyword() {
        return this.categoryKeyword;
    }

    /**
     * 设置栏目管理描述
     */
    public void setCategoryDescrip(String categoryDescrip) {
        this.categoryDescrip = categoryDescrip;
    }

    /**
     * 获取栏目管理描述
     */
    public String getCategoryDescrip() {
        return this.categoryDescrip;
    }

    /**
     * 设置banner图
     */
    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }

    /**
     * 获取banner图
     */
    public String getCategoryImg() {
        return this.categoryImg;
    }

    /**
     * 获取副标题
     */
    public String getCategoryShortTitle() {
        return categoryShortTitle;
    }

    /**
     * 设置副标题
     */
    public void setCategoryShortTitle(String categoryShortTitle) {
        this.categoryShortTitle = categoryShortTitle;
    }

    /**
     * 获取栏目小图
     */
    public String getCategoryIco() {
        return categoryIco;
    }

    /**
     * 设置栏目小图
     */
    public void setCategoryIco(String categoryIco) {
        this.categoryIco = categoryIco;
    }

    public String getCategoryDisplay() {
        return categoryDisplay;
    }

    public void setCategoryDisplay(String categoryDisplay) {
        this.categoryDisplay = categoryDisplay;
    }

    public String getCategoryIsSearch() {
        return categoryIsSearch;
    }

    public void setCategoryIsSearch(String categoryIsSearch) {
        this.categoryIsSearch = categoryIsSearch;
    }

    /**
     * 设置自定义链接
     */
    public void setCategoryDiyUrl(String categoryDiyUrl) {
        this.categoryDiyUrl = categoryDiyUrl;
    }

    /**
     * 获取自定义链接
     */
    public String getCategoryDiyUrl() {
        return this.categoryDiyUrl;
    }

    public String getMdiyModelId() {
        return mdiyModelId;
    }

    public void setMdiyModelId(String mdiyModelId) {
        this.mdiyModelId = mdiyModelId;
    }

    /**
     * 设置字典对应编号
     */
    public void setDictId(Integer dictId) {
        this.dictId = dictId;
    }

    /**
     * 获取字典对应编号
     */
    public Integer getDictId() {
        return this.dictId;
    }

    /**
     * 设置栏目属性
     */
    public void setCategoryFlag(String categoryFlag) {
        this.categoryFlag = categoryFlag;
    }

    /**
     * 获取栏目属性
     */
    public String getCategoryFlag() {
        return this.categoryFlag;
    }

    /**
     * 设置栏目路径
     */
    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    /**
     * 获取栏目路径
     */
    public String getCategoryPath() {
        return this.categoryPath;
    }

    /**
     * 设置父类型编号
     */
    public void setCategoryParentIds(String categoryParentIds) {
        this.categoryParentIds = categoryParentIds;
    }

    /**
     * 获取父类型编号
     */
    public String getCategoryParentIds() {
        return this.categoryParentIds;
    }


    public String getCategoryListUrls() {
        return categoryListUrls;
    }

    public void setCategoryListUrls(String categoryListUrls) {
        this.categoryListUrls = categoryListUrls;
    }

    public String getCategoryUrls() {
        return categoryUrls;
    }

    public void setCategoryUrls(String categoryUrls) {
        this.categoryUrls = categoryUrls;
    }

    /**
     * 根据模板名称获取htm文件
     * @param template 模板名称
     * @return
     */
    public String getCategoryListUrls(String template) {
        return getUrlByTemplate(template, categoryListUrls);
    }

    /**
     * 根据模板名称获取htm文件
     * @param template 模板名称
     * @return
     */
    public String getCategoryUrls(String template) {
        return getUrlByTemplate(template, categoryUrls);
    }

    /**
     * 根据模板名称从urls中获取第一个file（模板名称）参数
     * @param template 模板名称
     * @param urls 指定json格式的数组
     * @return
     */
    private String getUrlByTemplate(String template, String urls) {
        if(StringUtils.isNotBlank(urls)){
            List<TemplateBean> categoryListUrlList = JSONUtil.toList(urls, TemplateBean.class);
            List<TemplateBean> result = categoryListUrlList.stream().filter(templateBean -> templateBean.getTemplate().equals(template)).collect(Collectors.toList());
            if(result.size()>0){
                return result.get(0).getFile();
            } else {
                throw new BusinessException(this.getCategoryTitle() + "栏目绑定的模版不存在");
            }
        }

        return categoryListUrls;
    }

//    public void setTag() {
//        this.typetitle = this.categoryTitle;
//        this.typelink = "3".equals(this.categoryType) ? this.categoryDiyUrl : this.categoryPath + "/index.html";
//        this.typekeyword = this.categoryKeyword;
//        this.typeurl = this.categoryDiyUrl;
//        this.flag = this.categoryParentIds;
//        this.parentids = this.categoryKeyword;
//        this.typedescrip = this.categoryDescrip;
//        this.typeid = this.id;
//        this.typeleaf = this.leaf;
//        this.typelitpic = this.categoryImg;
//    }


    @TableField(exist = false)
    private String typetitle;
    /**
     * 获取栏目标题 （标签使用）
     */
    public String getTypetitle() {
        return this.categoryTitle;
    }

    @TableField(exist = false)
    private String typelink;
    /**
     * 获取栏目链接 （标签使用，动态链接不考虑）
     */
    public String getTypelink() {
        Boolean shortSwitch = ConfigUtil.getBoolean("静态化配置", "shortSwitch", false);
        return "3".equals(this.categoryType) ? this.categoryDiyUrl : shortSwitch? "/"+this.categoryPinyin + ".html" : this.categoryPath + "/index.html";
    }

    @TableField(exist = false)
    private String typekeyword;
    /**
     * 获取栏目关键字 （标签使用）
     */
    public String getTypekeyword() {
        return this.categoryKeyword;
    }

    @TableField(exist = false)
    private String typeurl;
    /**
     * 获取栏目url （标签使用）
     */
    public String getTypeurl() {
        return this.categoryDiyUrl;
    }

    @TableField(exist = false)
    private String flag;
    /**
     * 获取栏目属性 （标签使用）
     */
    public String getFlag() {
        return this.categoryFlag;
    }

    @TableField(exist = false)
    private String parentids;
    /**
     * 获取栏目父级Id （标签使用）
     */
    public String getParentids() {
        return this.categoryParentIds;
    }

    @TableField(exist = false)
    private String typedescrip;
    /**
     * 获取栏目描述（标签使用）
     */
    public String getTypedescrip() {
        return this.categoryDescrip;
    }

    @TableField(exist = false)
    private String typeid;
    /**
     * 获取栏目Id（标签使用）
     */
    public String getTypeid() {
        return this.id;
    }

    @TableField(exist = false)
    private Integer typeleaf;
    /**
     * 获取栏目是否是叶子节点（标签使用） 1 是叶子(子栏目) 0 不是叶子(顶级栏目)
     */
    public Integer getTypeleaf() {
        return ObjectUtil.isNotNull(this.leaf)?BooleanUtil.toInt(this.leaf):0;
    }


    @TableField(exist = false)
    private String typelitpic;
    /**
     * 获取栏目图片 (标签使用）
     */
    public String getTypelitpic() {
        return this.categoryImg;
    }

    /**
     * 获取栏目属性 (标签使用）
     */
    @TableField(exist = false)
    private String type;

    public String getType() {
        return this.categoryType;
    }

    /**
     * 获取子分类数量 (标签使用）
     */
    @TableField(exist = false)
    private String childsize;

    public String getChildsize() {
        return this.childsize;
    }

    public void setChildsize(String childsize) {
        this.childsize = childsize;
    }
}
