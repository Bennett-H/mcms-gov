/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.biz.impl;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.IpUtils;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.comment.bean.CommentBean;
import net.mingsoft.mdiy.entity.DictEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.comment.biz.ICommentBiz;
import net.mingsoft.comment.dao.ICommentDao;
import net.mingsoft.comment.entity.CommentEntity;

/**
 * 评论业务实现类
 * @author 铭飞开源团队
 * @date 2019年7月16日
 */
@Service("commentBiz")
public class CommentBizImpl extends BaseBizImpl<ICommentDao,CommentEntity> implements ICommentBiz {

	/**
	 * 评论持久化层
	 */
	private ICommentDao commentDao;


	/**
	 * 获取commentDao
	 * @return commentDao
	 */
	public ICommentDao getCommentDao() {
		return commentDao;
	}

	/**
	 * 设置commentDao
	 * @param commentDao
	 */
	@Autowired
	public void setCommentDao(ICommentDao commentDao) {
		this.commentDao = commentDao;
	}

	/**
	 * 获取commentDao
	 */
	@Override
	protected IBaseDao getDao() {
		return commentDao;
	}


	@Override
	public int saveComment(CommentEntity comment) {


		// 是否开启评论
		if (!ConfigUtil.getBoolean("评论配置","enableComment")){
			throw new BusinessException(BundleUtil.getBaseString("fail",
					BundleUtil.getString(net.mingsoft.comment.constant.Const.RESOURCES,"comment")));
		}

		// 判断评论类型
		String dataType = DictUtil.getDictValue("评论类型", comment.getDataType());
		if (StringUtils.isBlank(dataType)){
			throw new BusinessException(BundleUtil.getBaseString("err.error",
					BundleUtil.getString(net.mingsoft.comment.constant.Const.RESOURCES,"comment.type")));
		}

		// 判断评论编号
		if (StringUtils.isBlank(comment.getDataId())){
			throw new BusinessException(BundleUtil.getBaseString("err.error",
					BundleUtil.getString(net.mingsoft.comment.constant.Const.RESOURCES,"data.id")));
		}

		// 判断评论内容
		if (StringUtils.isBlank(comment.getCommentContent())){
			throw new BusinessException(BundleUtil.getBaseString("err.empty",
					BundleUtil.getString(net.mingsoft.comment.constant.Const.RESOURCES,"commentContent")));
		}
		if(!StringUtil.checkLength(comment.getCommentContent(), 0, 255)){
			throw new BusinessException(BundleUtil.getBaseString("err.length",
					BundleUtil.getString(net.mingsoft.comment.constant.Const.RESOURCES,"commentContent"),"0","255"));
		}

		// 判断评分是否正常
		if (comment.getCommentPoints()!=null && (comment.getCommentPoints() > 5 || comment.getCommentPoints() < 0)){
			throw new BusinessException(BundleUtil.getBaseString("err.error",
					BundleUtil.getString(net.mingsoft.comment.constant.Const.RESOURCES,"comment.points")));
		}


		// 评论类型
		comment.setDataType(dataType);
		// 涉及评论是否审核,开启取到配置为true，需要取反，false为未审核
		comment.setCommentAudit(!ConfigUtil.getBoolean("评论配置", "enableAudit"));
		// 评论ip
		HashMap<String, String> map = new HashMap<>();
		map.put("ipv4",BasicUtil.getIp());
		map.put("addr",IpUtils.getRealAddressByIp(BasicUtil.getIp()));
		comment.setCommentIp(JSONUtil.toJsonStr(map));
		//评论时间
		comment.setCommentTime(new Date());
		comment.setUpdateDate(new Date());
		comment.setCreateDate(new Date());
		comment.setCommentLike(0);
		// 附件及图片
		if (StringUtils.isBlank(comment.getCommentPicture()) || !comment.getCommentPicture().matches("^\\[.{1,}]$")) {
			comment.setCommentPicture("");
		}
		if (StringUtils.isBlank(comment.getCommentFileJson()) || !comment.getCommentFileJson().matches("^\\[.{1,}]$")) {
			comment.setCommentFileJson("");
		}
		// 设置顶级评论id
		setTopId(comment);

		return commentDao.insert(comment);
	}



	@Override
	public List<CommentEntity> query(CommentBean comment) {


		// 判断评论类型
		DictEntity dict = DictUtil.get("评论类型", comment.getDataType(), null);
		if (dict==null){
			throw new BusinessException(BundleUtil.getBaseString("err.error",
					BundleUtil.getString(net.mingsoft.comment.constant.Const.RESOURCES,"comment.type")));
		}
		comment.setDataType(dict.getDictValue());
		BasicUtil.startPage();
		return commentDao.query(comment);
	}

	@Override
	public CommentEntity like(String id,boolean flag) {

		// 判断评论id是否为空
		if (StringUtils.isBlank(id)) {
			throw new BusinessException(BundleUtil.getBaseString("err.not.exist",
					BundleUtil.getString(net.mingsoft.comment.constant.Const.RESOURCES,"comment")));
		}

		// 通过评论id查询出数据
		CommentEntity commentEntity = this.getById(id);
		if (flag){
			commentEntity.setCommentLike(commentEntity.getCommentLike()+1);
		} else {
			commentEntity.setCommentLike(commentEntity.getCommentLike()-1);
		}

		this.updateById(commentEntity);
		return commentEntity;
	}


	/**
	 * 设置评论的topId，为空则代表当前评论为顶级评论
	 * @param comment
	 */
	private void setTopId(CommentEntity comment) {
		// 判断是不是顶级id
		if (StringUtils.isNotBlank(comment.getCommentId())) {
			// 那就根据commentId获取父数据
			CommentEntity commentEntity = commentDao.selectById(comment.getCommentId());
			// 等于0说明他是顶级的子评论,反之则设置commentEntity的topId给当前评论
			if (commentEntity!=null) {
				if (commentEntity.getTopId() == null) {
					comment.setTopId(commentEntity.getId());
				} else {
					comment.setTopId(commentEntity.getTopId());
				}
			}
		} else {
			// 说明是顶评论赋值0
			comment.setTopId(null);
		}
	}

}
