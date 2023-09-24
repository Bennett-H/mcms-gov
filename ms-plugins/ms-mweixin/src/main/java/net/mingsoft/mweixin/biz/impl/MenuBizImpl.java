/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.mweixin.biz.impl;

import javax.annotation.Resource;

import cn.hutool.core.lang.Assert;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mweixin.bean.MenuBean;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.biz.IFileBiz;
import net.mingsoft.mweixin.constant.e.MenuStatusEnum;
import net.mingsoft.mweixin.constant.e.MenuStyleEnum;
import net.mingsoft.mweixin.entity.*;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mweixin.biz.IMenuBiz;
import net.mingsoft.mweixin.constant.SessionConst;
import net.mingsoft.mweixin.dao.IMenuDao;

import java.util.Collections;
import java.util.List;

/**
 * 微信菜单管理持久化层
 * @author 铭飞
 * @version
 * 版本号：100<br/>
 * 创建日期：2017-12-22 9:43:04<br/>
 * 历史修订：<br/>
 */
@Service("netMenuBizImpl")
public class MenuBizImpl extends BaseBizImpl implements IMenuBiz {


    @Resource(name = "netMenuDao")
    private IMenuDao menuDao;

    /**
     * 注入文件业务层
     */
    @Autowired
    IFileBiz fileBiz;
    /**
     * 注入素材业务层
     */
    @Autowired
    private IDraftBiz draftBiz;

    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return menuDao;
    }


    @Override
    public MenuEntity getEntity(MenuEntity menu) {
        // TODO Auto-generated method stub
        WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
        if (weixin == null || weixin.getIntId() <= 0) {
            return null;
        }
        menu.setMenuWeixinId(weixin.getIntId());
        return null;
    }

    @Override
    public void saveOrUpdate(WeixinEntity weixin, List<MenuBean> menuBeans, boolean isSave) {
        for (MenuBean menuBean : menuBeans) {
            saveOrUpdate(weixin, menuBean, isSave);
            for (MenuEntity menuEntity : menuBean.getSubMenuList()) {
                menuEntity.setMenuMenuId(menuBean.getIntId());
                saveOrUpdate(weixin, menuEntity, isSave);
            }
        }
    }

    @Override
    public void releaseMenu(WeixinEntity weixin) throws WxErrorException {
        PortalService wxService = SpringUtil.getBean(PortalService.class).build(weixin);
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setMenuWeixinId(weixin.getIntId());
        //第一步读取当前微信对应的所有菜单信息
        List<MenuEntity> menuList = menuDao.query(menuEntity);
        //根据sort对菜单排序
        Collections.sort(menuList);
        WxMenu menu = new WxMenu();
        //第二步将本地菜单赋值到WxMenuButton对象
        for (MenuEntity _menuEntity : menuList) {
            WxMenuButton parentButton = new WxMenuButton();
            //按钮启用才发布
            if (_menuEntity.getMenuStatus() == MenuStatusEnum.ENABLE.toInt()) {
                //判断是否为图片回复
                MenuStyleEnum menuStyleEnum = MenuStyleEnum.getValue(_menuEntity.getMenuStyle());
                switch (menuStyleEnum) {
                    //判断是否回复图文消息
                    case IMAGE_TEXT:
                        Assert.notBlank(_menuEntity.getMenuContent(), "{}菜单内容不能为空", _menuEntity.getMenuTitle());
                        break;
                    //判断是否回复图片消息
                    case IMAGE:
                        //判断是否回复语音消息
                    case VOICE:
                        //判断是否回复语音消息
                    case VIDEO:
                        Assert.notBlank(_menuEntity.getMenuContent(), "{}菜单内容不能为空", _menuEntity.getMenuTitle());
                        FileEntity fileEntity = (FileEntity) fileBiz.getEntity(Integer.valueOf(_menuEntity.getMenuContent()));
                        if (StringUtils.isBlank(fileEntity.getFileMediaId())) {
                            fileBiz.weiXinFileUpload(weixin, fileEntity);
                        }
                        break;
                    default:
                        break;
                }
                //判断是否主菜单
                if (_menuEntity.getMenuMenuId() == null) {
                    //获取一级按钮
                    editWeiXinButton(_menuEntity, parentButton);
                    parentButton.setUrl(_menuEntity.getMenuUrl());
                    MenuEntity parenMenutEntity = new MenuEntity();
                    parenMenutEntity.setMenuMenuId(_menuEntity.getIntId());
                    //查询该按钮下的子按钮
                    menuList = menuDao.query(parenMenutEntity);
                    Collections.sort(menuList);
                    for (MenuEntity menuSubEntity : menuList) {
                        if (menuSubEntity.getMenuStatus() == MenuStatusEnum.ENABLE.toInt()) {
                            WxMenuButton subButton = new WxMenuButton();
                            editWeiXinButton(menuSubEntity, subButton);
                            parentButton.getSubButtons().add(subButton);
                        }
                    }
                    //第三步将WxMenuButton赋值WxMenu
                    menu.getButtons().add(parentButton);
                }
            }
        }
        wxService.getMenuService().menuCreate(menu);
    }

    /**
     * 设置按钮属性
     * @param _menuEntity
     * @param parentButton
     */
    private void editWeiXinButton(MenuEntity _menuEntity, WxMenuButton parentButton) {
        MenuStyleEnum menuStyleEnum = MenuStyleEnum.getValue(_menuEntity.getMenuStyle());
        switch (menuStyleEnum) {
            //回复文本
            case TEXT:
                parentButton.setType(WxConsts.MenuButtonType.CLICK);
                parentButton.setKey(_menuEntity.getId());
                break;
            //回复图文
            case IMAGE_TEXT:
				DraftEntity draftEntity = draftBiz.getById(_menuEntity.getMenuContent());
                parentButton.setType("article_id");
                parentButton.setArticleId(draftEntity.getArticleId());
				//跳转链接
				break;
            case LINK:
                parentButton.setType(WxConsts.MenuButtonType.VIEW);
                parentButton.setUrl(_menuEntity.getMenuUrl());
                break;

            //回复图片
            case IMAGE:
                //回复语音
            case VOICE:
            case VIDEO:
                FileEntity file = (FileEntity) fileBiz.getEntity(Integer.valueOf(_menuEntity.getMenuContent()));
                parentButton.setType(WxConsts.MenuButtonType.MEDIA_ID);
                parentButton.setMediaId(file.getFileMediaId());
                break;
            //跳转微信小程序
            case MINIPROGRAM:
                parentButton.setType(WxConsts.MenuButtonType.MINIPROGRAM);
                parentButton.setPagePath(_menuEntity.getMenuPagePath());
                parentButton.setUrl(_menuEntity.getMenuUrl());
                parentButton.setAppId(_menuEntity.getMiniprogramAppid());
                break;
            default:
                break;
        }
        parentButton.setName(_menuEntity.getMenuTitle());
    }


    private void saveOrUpdate(WeixinEntity weixin, MenuEntity menuEntity, boolean isSave) {
        if (StringUtils.isNotEmpty(menuEntity.getId())) {
            menuDao.updateEntity(menuEntity);
        } else if (isSave) {
            menuEntity.setMenuWeixinId(weixin.getIntId());
            menuDao.saveEntity(menuEntity);
        }
    }
}
