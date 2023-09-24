/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.mweixin.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.bean.DraftArticleBean;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.biz.IMessageBiz;
import net.mingsoft.mweixin.constant.e.NewsTypeEnum;
import net.mingsoft.mweixin.dao.IMessageDao;
import net.mingsoft.mweixin.entity.DraftEntity;
import net.mingsoft.mweixin.entity.MessageEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 微信被动消息回复管理持久化层
 * 
 * @author 铭飞
 * @version 版本号：100<br/>
 *          创建日期：2017-12-22 9:43:04<br/>
 *          历史修订：<br/>
 */
@Service("netMessageBizImpl")
public class MessageBizImpl extends BaseBizImpl implements IMessageBiz {

	@Resource(name = "netMessageDao")
	private IMessageDao messageDao;

	@Autowired
	private IDraftBiz draftBiz;



	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return messageDao;
	}

	@Override
	public MessageEntity getEntity(MessageEntity message) {
		// TODO Auto-generated method stub
		return (MessageEntity) messageDao.getByEntity(message);
	}

	/**
	 * 查询关键字列表，后台使用如果类型的图文，newsTitle显示素材的标题
	 */
	@Override
	public List<MessageEntity> query(MessageEntity message) {
		// TODO Auto-generated method stub
		List<MessageEntity> messageList = messageDao.query(message);
		//用于过滤删掉的素材,并将关联的关键字删除
		if(messageList.size() > 0){
			for(MessageEntity _message : messageList){
				if( NewsTypeEnum.IMAGE_TEXT.toString().equals(_message.getPmNewType())){
					//获取素材实体
					DraftEntity draftEntity = draftBiz.getById(_message.getPmContent());
					if(ObjectUtil.isNotNull(draftEntity)){
						DraftArticleBean draftArticleBean = new DraftArticleBean();
						BeanUtil.copyProperties(draftEntity,draftArticleBean);
						draftBiz.setDraftBean(draftArticleBean);
						if(ObjectUtil.isNotNull(draftArticleBean.getMasterArticle())){
							_message.setNewsTitle(draftArticleBean.getMasterArticle().getArticleTitle());
						}
					}else{
						int[] ids =  new int[1];
						ids[0] = _message.getIntId();
						messageDao.delete(ids);
					}
				}
			}
		}
		return messageList;
	}
	@Override
	public int saveMessage(MessageEntity messageEntity) {
		messageDao.saveEntity(messageEntity);
		return  saveEntity(messageEntity);
	}

	@Override
	public void updateFile(MessageEntity messageEntity) {
		messageDao.updateEntity(messageEntity);
		updateEntity(messageEntity);
	}

}
