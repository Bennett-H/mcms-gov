/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.mweixin.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpDraftService;
import me.chanjar.weixin.mp.api.WxMpFreePublishService;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.bean.draft.WxMpAddDraft;
import me.chanjar.weixin.mp.bean.draft.WxMpDraftArticles;
import me.chanjar.weixin.mp.bean.draft.WxMpDraftItem;
import me.chanjar.weixin.mp.bean.draft.WxMpDraftList;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishArticles;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishItem;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishList;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishStatus;
import me.chanjar.weixin.mp.bean.material.WxMediaImgUploadResult;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mweixin.bean.DraftArticleBean;
import net.mingsoft.mweixin.biz.IArticleBiz;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.biz.IFileBiz;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.constant.e.DraftPublishStateEnum;
import net.mingsoft.mweixin.dao.IWxDraftDao;
import net.mingsoft.mweixin.entity.ArticleEntity;
import net.mingsoft.mweixin.entity.DraftEntity;
import net.mingsoft.mweixin.entity.FileEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.quartz.DraftPublishStateQuartz;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 草稿管理持久化层
 *
 * @author 陈思鹏
 * 创建日期：2022-3-1 11:23:09<br/>
 * 历史修订：<br/>
 */
@Service("mweixinwxDraftBizImpl")
public class DraftBizImpl extends BaseBizImpl<IWxDraftDao, DraftEntity> implements IDraftBiz {

    /**
     * 注入微信文章素材表业务层
     */
    @Autowired
    private IArticleBiz wxArticleBiz;



    @Autowired
    private IWxDraftDao wxDraftDao;


    /**
     * 注入微信基础业务层
     */
    @Autowired
    private IWeixinBiz weixinBiz;

    /**
     * 注入微信文件表业务层
     */
    @Autowired
    private IFileBiz fileBiz;

    @Autowired @Lazy
    DraftPublishStateQuartz publishStateQuartz;


    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return wxDraftDao;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateDraftArticleBean(DraftArticleBean draftArticleBean) throws WxErrorException {
        //判断是新增还是更新
        if (StringUtils.isNotEmpty(draftArticleBean.getId())) {
            //保存更新时间
            draftArticleBean.setCreateDate(new Timestamp(System.currentTimeMillis()));
            wxDraftDao.updateById(draftArticleBean);
        } else {
            //保存创建和更新时间
            draftArticleBean.setCreateDate(new Timestamp(System.currentTimeMillis()));
            draftArticleBean.setUpdateDate(new Timestamp(System.currentTimeMillis()));
            wxDraftDao.insert(draftArticleBean);
        }

        List<ArticleEntity> aticleList = draftArticleBean.getArticleList();
        int sort = 1;
        //判断是否添加了子图文
        if (ObjectUtil.isNotNull(aticleList)) {
            if (aticleList.size() > 0) {
                //判断是新增还是更新文章
                for (ArticleEntity articleEntity : aticleList) {
                    articleEntity.setArticleSort(sort);
                    articleEntity.setDraftId(draftArticleBean.getId());
                    if (StringUtils.isNotBlank(articleEntity.getId())) {
                        //wxArticleBiz.updateEntity(articleEntity);
                        wxArticleBiz.updateById(articleEntity);
                    } else {
                        //wxArticleBiz.saveEntity(articleEntity);
                        wxArticleBiz.save(articleEntity);
                    }
                    sort++;
                }
            }
        }

        //判断是否有微信图文素材主内容
        if (ObjectUtil.isNotNull(draftArticleBean.getMasterArticle())) {
            ArticleEntity article = draftArticleBean.getMasterArticle();
            article.setDraftId(draftArticleBean.getId());
            article.setArticleSort(0);
            //判断是新增还是更新文章
            if (ObjectUtil.isNotNull(draftArticleBean.getMasterArticleId()) && draftArticleBean.getMasterArticleId() > 0) {
                //wxArticleBiz.updateEntity(article);
                wxArticleBiz.updateById(article);
            } else {
                //wxArticleBiz.saveEntity(article);
                wxArticleBiz.save(article);
            }
            //保存主图文编号
            draftArticleBean.setMasterArticleId(Integer.parseInt(article.getId()));
            baseMapper.updateById(draftArticleBean);
        }
        //上传到微信服务器
        try {
            uploadDraft(this.getById(draftArticleBean.getId()));
        } catch (WxErrorException e) {
            e.printStackTrace();
            baseMapper.deleteById(draftArticleBean.getId());
            throw e;
        }
    }

    @Override
    public DraftArticleBean setDraftBean(DraftArticleBean draftArticleBean) {
        //重新组装图文
        if (draftArticleBean != null) {
            ArticleEntity article = new ArticleEntity();
            article.setDraftId(draftArticleBean.getId());
            List<ArticleEntity> list = wxArticleBiz.query(article);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    //主图文
                    if (list.get(i).getId().equals(draftArticleBean.getMasterArticleId().toString())) {
                        draftArticleBean.setMasterArticle(list.get(i));
                        list.remove(i);
                        break;
                    }
                }
                //保存子图文列表
                draftArticleBean.setArticleList(list);
            }
            return draftArticleBean;
        } else {
            return null;
        }
    }

    /**
     * 查询未发布的草稿
     *
     * @param DraftArticleBeans 需要转换的草稿信息集合
     * @return 转换后的草稿信息
     */
    private List<DraftArticleBean> setDraftBeanList(List<DraftArticleBean> DraftArticleBeans) {
        //重新组装图文
        if (DraftArticleBeans != null) {
            List<DraftArticleBean> list = new ArrayList<>();
            for (DraftArticleBean draftArticleBean : DraftArticleBeans) {
                list.add(this.setDraftBean(draftArticleBean));
            }
            return list;
        } else {
            return null;
        }
    }

    /**
     * 查询未发布的草稿
     *
     * @param draftArticleBean 草稿微信ID
     * @return 草稿列表
     */
    @Override
    public List<DraftArticleBean> queryDraft(DraftArticleBean draftArticleBean) {
        List<DraftEntity> draftList = wxDraftDao.queryDraftList(draftArticleBean);
        List<DraftArticleBean> draftArticleBeans = draftList.stream().map(draftEntity -> {
            DraftArticleBean draftBean = new DraftArticleBean();
            BeanUtil.copyProperties(draftEntity, draftBean);
            return draftBean;
        }).collect(Collectors.toList());

        //重新组装图文
        if (draftList != null) {
            return setDraftBeanList(draftArticleBeans);
        }
        return null;
    }



    @Override
    public String uploadDraft(DraftEntity draftEntity) throws WxErrorException {
        if (ObjectUtil.isNull(draftEntity)) {
            return null;
        }
        String mediaId = null;
        if (StringUtils.isEmpty(draftEntity.getMediaId())) {//没有MediaId新增
            mediaId = addDraft(draftEntity);
        } else {
            //否则更新
            mediaId = updateDraft(draftEntity);
        }
        draftEntity.setMediaId(mediaId.replaceAll("\"", ""));
        baseMapper.updateById(draftEntity);
        return mediaId;
    }


    /**
     * 同步未发布草稿
     * @param wxMpDraftList
     * @param weixinId
     */
    @Override
    public void weiXinDraftSyncLocal(WxMpDraftList wxMpDraftList, int weixinId) {
        List<WxMpDraftItem> draftList = wxMpDraftList.getItems();
        //获取素材集合
        for (WxMpDraftItem item : draftList) {
            //获取微信素材文章集合
            List<WxMpDraftArticles> draftArticles = item.getContent().getNewsItem();
            DraftArticleBean draftArticleBean = new DraftArticleBean();
            //设置文章集合
            List<ArticleEntity> articles = new ArrayList<ArticleEntity>();
            for (WxMpDraftArticles wxMpMaterialNewsArticle : draftArticles) {
                //设置文章详情
                ArticleEntity article = new ArticleEntity();
                article.setArticleAuthor(wxMpMaterialNewsArticle.getAuthor());
                //正则替换微信防盗链链接
                StringBuffer repl = new StringBuffer(wxMpMaterialNewsArticle.getContent());
//                String pattern = "data-src=\"(https://mmbiz.qpic.cn/[^\\s]*)\"";
                // 创建 Pattern 对象
//                Pattern r = Pattern.compile(pattern);
                // 现在创建 matcher 对象
//                Matcher m = r.matcher(wxMpMaterialNewsArticle.getContent());
//                while (m.find()) {
//                    //重定向链接
//                    String src = BasicUtil.getRealPath("") + File.separator + "/mweixin/forward/image?src=" + m.group(1);
//                    repl = repl.replace(m.start(1), m.end(1), src);
//                    m = r.matcher(repl.toString());
//                }
                wxMpMaterialNewsArticle.getThumbMediaId();
                //将data-src替换为src 不然无法显示图片
                article.setArticleContent(repl.toString().replace("data-src=\"", "src=\""));
                article.setArticleSource(wxMpMaterialNewsArticle.getContentSourceUrl());
                String path = fileBiz.saveWeinXinImage(wxMpMaterialNewsArticle.getThumbMediaId(), weixinId, wxMpMaterialNewsArticle.getThumbMediaId(), "image/png");
                article.setArticleThumbnails(path);
                article.setArticleTitle(wxMpMaterialNewsArticle.getTitle());
                article.setArticleDescription(wxMpMaterialNewsArticle.getDigest());
                articles.add(article);
                // 这里是获取未发布的临时链接
                draftArticleBean.setDraftUrl(wxMpMaterialNewsArticle.getUrl());
            }
            draftArticleBean.setWeixinId(weixinId);
            draftArticleBean.setArticleList(articles);
            draftArticleBean.setMediaId(item.getMediaId());
            //保存微信同步素材
            int sort = 0;
            //判断是否有文章
            if (articles.size() > 0) {
                DraftEntity draftEntity = wxDraftDao.selectOne(new LambdaQueryWrapper<DraftEntity>().eq(DraftEntity::getMediaId, draftArticleBean.getMediaId()));
                //判断是新增还是更新素材
                if (ObjectUtil.isNotNull(draftEntity)) {
                    draftArticleBean.setId(draftEntity.getId());
                } else {
                    wxDraftDao.insert(draftArticleBean);
                }
                ArticleEntity articl = new ArticleEntity();
                articl.setDraftId(draftArticleBean.getId());
                List<ArticleEntity> articls = wxArticleBiz.query(articl);
                //插入之前先全部删除，因为无法判断文章是否已经存在
                for (ArticleEntity articleEntity : articls) {
                    wxArticleBiz.deleteEntity(articleEntity.getIntId());
                }
                //判断是新增还是更新文章
                for (ArticleEntity articleEntity : articles) {
                    //排序第0位是图文主素材，
                    articleEntity.setArticleSort(sort);
                    articleEntity.setDraftId(draftArticleBean.getId());
                    wxArticleBiz.saveEntity(articleEntity);

                    //设置主图文
                    if (sort == 0) {
                        draftArticleBean.setMasterArticle(articleEntity);
                        draftArticleBean.setMasterArticleId(Integer.parseInt(articleEntity.getId()));
                        wxDraftDao.updateById(draftArticleBean);
                    }
                    sort++;
                }
            }
        }
    }


    /**
     * 同步已发布草稿
     * @param publishList
     * @param weixinId
     */
    @Override
    public void weiXinPublishedSyncLocal(WxMpFreePublishList publishList, int weixinId) {
        //同步的时候手动更新下发布状态,防止重复数据的产生
        publishStateQuartz.getPublishedResults();
        List<WxMpFreePublishItem> publishListItems = publishList.getItems();
        //获取素材集合
        for (WxMpFreePublishItem item : publishListItems) {
            //获取微信素材文章集合
            List<WxMpFreePublishArticles> publishArticles = item.getContent().getNewsItem();
            DraftArticleBean draftArticleBean = new DraftArticleBean();
            //设置文章集合
            List<ArticleEntity> articles = new ArrayList<ArticleEntity>();
            for (WxMpFreePublishArticles wxMpFreePublishArticles : publishArticles) {
                //设置文章详情
                ArticleEntity article = new ArticleEntity();
                article.setArticleAuthor(wxMpFreePublishArticles.getAuthor());
                //正则替换微信防盗链链接
                StringBuffer repl = new StringBuffer(wxMpFreePublishArticles.getContent());
//                String pattern = "data-src=\"(https://mmbiz.qpic.cn/[^\\s]*)\"";
                // 创建 Pattern 对象
//                Pattern r = Pattern.compile(pattern);
                // 现在创建 matcher 对象
//                Matcher m = r.matcher(wxMpFreePublishArticles.getContent());
//                while (m.find()) {
//                    //重定向链接
//                    String src = BasicUtil.getRealPath("") + File.separator + "/mweixin/forward/image?src=" + m.group(1);
//                    repl = repl.replace(m.start(1), m.end(1), src);
//                    m = r.matcher(repl.toString());
//                }
                wxMpFreePublishArticles.getThumbMediaId();
                //将data-src替换为src 不然无法显示图片
                article.setArticleContent(repl.toString().replace("data-src=\"", "src=\""));
                article.setArticleSource(wxMpFreePublishArticles.getContentSourceUrl());
                String path = fileBiz.saveWeinXinImage(wxMpFreePublishArticles.getThumbMediaId(), weixinId, wxMpFreePublishArticles.getThumbMediaId(), "image/png");
                article.setArticleThumbnails(path);
                article.setArticleTitle(wxMpFreePublishArticles.getTitle());
                article.setArticleDescription(wxMpFreePublishArticles.getDigest());
                articles.add(article);
                // 添加图文url
                draftArticleBean.setDraftUrl(wxMpFreePublishArticles.getUrl());
            }
            draftArticleBean.setWeixinId(weixinId);
            draftArticleBean.setArticleList(articles);
            draftArticleBean.setArticleId(item.getArticleId());
            draftArticleBean.setPublishState(DraftPublishStateEnum.PUBLISHED.toInt());
            //保存微信同步素材
            int sort = 0;
            //判断是否有文章
            if (articles.size() > 0) {
                DraftEntity draftEntity = wxDraftDao.selectOne(new LambdaQueryWrapper<DraftEntity>().eq(DraftEntity::getArticleId, draftArticleBean.getArticleId()));
                //判断是新增还是更新素材
                if (ObjectUtil.isNotNull(draftEntity)) {
                    draftArticleBean.setId(draftEntity.getId());
                } else {
                    wxDraftDao.insert(draftArticleBean);
                }
                ArticleEntity articl = new ArticleEntity();
                articl.setDraftId(draftArticleBean.getId());
                List<ArticleEntity> articls = wxArticleBiz.query(articl);
                //插入之前先全部删除，因为无法判断文章是否已经存在
                for (ArticleEntity articleEntity : articls) {
                    wxArticleBiz.removeById(articleEntity.getId());
                }
                //判断是新增还是更新文章
                for (ArticleEntity articleEntity : articles) {
                    //排序第0位是图文主素材，
                    articleEntity.setArticleSort(sort);
                    articleEntity.setDraftId(draftArticleBean.getId());
                    wxArticleBiz.save(articleEntity);

                    //设置主图文
                    if (sort == 0) {
                        draftArticleBean.setMasterArticle(articleEntity);
                        draftArticleBean.setMasterArticleId(Integer.parseInt(articleEntity.getId()));
                        wxDraftDao.updateById(draftArticleBean);
                    }
                    sort++;
                }
            }
        }
    }

    /**
     * 根据publishId来查询发布状态并更新状态和返回一个更新后实体类
     * @param draftArticleBean 文章草稿实体类
     * @return 草稿实体类
     */
    @Override
    public DraftEntity weiXinGetPushStatus(DraftArticleBean draftArticleBean) {
        DraftEntity draftEntity = this.wxDraftDao.selectById(draftArticleBean.getId());
        if (draftEntity != null && StringUtils.isNotEmpty(draftEntity.getMediaId()) && StringUtils.isNotEmpty(draftEntity.getPublishId())) {
            WeixinEntity wx = (WeixinEntity) weixinBiz.getEntity(draftEntity.getWeixinId());
            PortalService service = SpringUtil.getBean(PortalService.class);
            PortalService wxService = service.build(wx);
            WxMpFreePublishService publishService = wxService.getFreePublishService();
            try {
                // 获取返回状态信息
                WxMpFreePublishStatus pushStatus = publishService.getPushStatus(draftEntity.getPublishId());
                Integer publish_status = pushStatus.getPublish_status();
                // 只有状态码返回零的时候才是发布成功
                if (publish_status.equals(0)){
                    draftEntity.setPublishState(DraftPublishStateEnum.PUBLISHED.toInt());
                    draftEntity.setArticleId(pushStatus.getArticle_id());
                    List<WxMpFreePublishStatus.ArticleDetail.Item> articleDetailList = pushStatus.getArticle_detail().getItem();
                    // 循环取出需要的数据
                    for (WxMpFreePublishStatus.ArticleDetail.Item articleDetail : articleDetailList) {
                        // 取出文章的永久链接地址
                        draftEntity.setDraftUrl(articleDetail.getArticle_url());
                    }
                }
                // 保存最新数据
                wxDraftDao.updateById(draftEntity);
                return draftEntity;
            } catch (WxErrorException e) {
                e.printStackTrace();
            }
        }
        return draftEntity;
    }


    @Override
    public void submit(DraftArticleBean draftArticleBean) {
        DraftEntity draftEntity = this.wxDraftDao.selectById(draftArticleBean.getId());
        if (draftEntity != null && StringUtils.isNotEmpty(draftEntity.getMediaId()) && StringUtils.isEmpty(draftEntity.getPublishId())) {
            WeixinEntity wx = (WeixinEntity) weixinBiz.getEntity(draftEntity.getWeixinId());
            PortalService service = SpringUtil.getBean(PortalService.class);
            PortalService wxService = service.build(wx);
            WxMpFreePublishService publishService = wxService.getFreePublishService();
            try {
                String publishId = publishService.submit(draftEntity.getMediaId());
                draftEntity.setPublishId(publishId);
                //设置为发布中
                draftEntity.setPublishState(DraftPublishStateEnum.PUBLISHING.toInt());
                this.wxDraftDao.updateById(draftEntity);

            } catch (WxErrorException e) {
                e.printStackTrace();
                throw new BusinessException(e.getMessage());
            }
        }
    }

    /**
     * 更新草稿到微信服务器 返回madieid
     *
     * @param draftEntity
     * @return madieid
     * @throws WxErrorException
     */
    private String updateDraft(DraftEntity draftEntity) throws WxErrorException {
        WeixinEntity wx = (WeixinEntity) weixinBiz.getEntity(draftEntity.getWeixinId());
        PortalService service = SpringUtil.getBean(PortalService.class);
        PortalService wxService = service.build(wx);
        WxMpDraftService draftService = wxService.getDraftService();
        //删除原来的草稿
        draftService.delDraft(draftEntity.getMediaId());
        //重新上传
        return addDraft(draftEntity);
    }


    /**
     * 新增草稿到微信服务器 返回madieid
     *
     * @param draftEntity
     * @return madieid
     * @throws WxErrorException
     */
    private String addDraft(DraftEntity draftEntity) throws WxErrorException {
        WeixinEntity wx = (WeixinEntity) weixinBiz.getEntity(draftEntity.getWeixinId());
        PortalService service = SpringUtil.getBean(PortalService.class);
        PortalService wxService = service.build(wx);
        ArticleEntity article = new ArticleEntity();
        article.setDraftId(draftEntity.getId());
        List<ArticleEntity> articleEntityList = wxArticleBiz.query(article);

        articleEntityList.sort((o1, o2) -> o1.getArticleSort() - o2.getArticleSort());
        //封装的草稿对象
        WxMpAddDraft addDraft = new WxMpAddDraft();
        WxMpDraftService draftService = wxService.getDraftService();
        if (CollUtil.isNotEmpty(articleEntityList)) {

            //上传的图文集合
            ArrayList<WxMpDraftArticles> wxMpDraftArticles = new ArrayList<>();
            for (ArticleEntity articleEntity : articleEntityList) {
                //设置首页图片的媒体id
                WxMpDraftArticles draftArticles = new WxMpDraftArticles();
                FileEntity articleFileEntity = new FileEntity();
                articleFileEntity.setFileUrl(articleEntity.getArticleThumbnails());
                FileEntity fileEntity = fileBiz.getEntity(articleFileEntity);
                if (ObjectUtil.isNull(fileEntity)) {
                    articleFileEntity.setCreateDate(new Date());
                    articleFileEntity.setWeixinId(wx.getIntId());
                    articleFileEntity.setFileName(articleEntity.getArticleTitle());
                    fileBiz.save(articleFileEntity);
                    fileBiz.weiXinFileUpload(wx, articleFileEntity);
                    draftArticles.setThumbMediaId(articleFileEntity.getFileMediaId());
                } else {
                    draftArticles.setThumbMediaId(fileEntity.getFileMediaId());
                }

                //标题
                draftArticles.setTitle(articleEntity.getArticleTitle());
                //作者
                draftArticles.setAuthor(articleEntity.getArticleAuthor());

                String content  = replacePic(service.getMaterialService(),articleEntity.getArticleContent());
                //内容
                draftArticles.setContent(content);

                //摘要
                draftArticles.setDigest(articleEntity.getArticleDescription());
                draftArticles.setContentSourceUrl(articleEntity.getArticleUrl());
                wxMpDraftArticles.add(draftArticles);
            }
            addDraft.setArticles(wxMpDraftArticles);
        }
        return draftService.addDraft(addDraft);
    }

    /**
     * 获取图片url，并且根据列表页分页进行文件夹分类，同时下载图片
     * @param content	详情页内容
     */
    public String replacePic(WxMpMaterialService wxMpMaterialService,String content) {
        LOG.debug("xxxxx");
        HashSet<String> images = CollUtil.newHashSet();
        HashSet<String> videos = CollUtil.newHashSet();
        HashSet<String> voices = CollUtil.newHashSet();
        ReUtil.findAll(Pattern.compile("<img.*?src=\\\"(.*?)\\\".*?>",2), content,1).forEach(img-> {
            images.add(img);
        });

//        ReUtil.findAll(Pattern.compile("<video.*?src=\\\"(.*?)\\\".*?>",2), content,1).forEach(img-> {
//            videos.add(img);
//        });
//
//        ReUtil.findAll(Pattern.compile("<audio.*?src=\\\"(.*?)\\\".*?>",2), content,1).forEach(img-> {
//            voices.add(img);
//        });

        Iterator<String> imagesIt = images.iterator();
        while (imagesIt.hasNext()) {
            String _imgUrl = imagesIt.next();
            try {

                LOG.debug("上传素材"+_imgUrl);
                WxMediaImgUploadResult wxMediaImgUploadResult = wxMpMaterialService.mediaImgUpload(new File(BasicUtil.getRealPath(_imgUrl)));
                content = content.replaceAll(_imgUrl,wxMediaImgUploadResult.getUrl());
            } catch (WxErrorException e) {
                LOG.error("草稿同步内容里面的素材异常{}",_imgUrl);
                e.printStackTrace();
            }
        }
//确认微信公众号暂时不同通过接口方式获取视频，只能是图片
//https://developers.weixin.qq.com/community/minihome/doc/0006c2e7c207d0d0243e836555b000
//        Iterator<String> videoIt = videos.iterator();
//        while (videoIt.hasNext()) {
//            String _videoUrl = videoIt.next();
//            try {
//                LOG.debug("上传视频素材{}",_videoUrl);
//                WxMediaUploadResult wxMediaUploadResult = wxMpMaterialService.mediaUpload(WxConsts.MaterialType.VIDEO,new File(BasicUtil.getRealPath(_videoUrl)));
//
//                content = content.replaceAll(_videoUrl,wxMediaUploadResult.getMediaId());
//            } catch (WxErrorException e) {
//                LOG.error("草稿同步内容里面的素材异常{}",_videoUrl);
//                e.printStackTrace();
//            }
//        }
//
//        Iterator<String> voicesIt = voices.iterator();
//        while (voicesIt.hasNext()) {
//            String _voiceUrl = voicesIt.next();
//            try {
//                LOG.debug("上传音频素材{}",_voiceUrl);
//                WxMediaUploadResult wxMediaUploadResult = wxMpMaterialService.mediaUpload(WxConsts.MaterialType.VOICE,new File(BasicUtil.getRealPath(_voiceUrl)));
//                content = content.replaceAll(_voiceUrl,wxMediaUploadResult.getMediaId());
//            } catch (WxErrorException e) {
//                LOG.error("草稿同步内容里面的素材异常{}",_voiceUrl);
//                e.printStackTrace();
//            }
//        }


        return content;
    }

}
