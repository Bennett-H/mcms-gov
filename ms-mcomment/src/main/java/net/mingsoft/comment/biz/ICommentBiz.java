/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.biz;

import java.util.List;
import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.comment.bean.CommentBean;
import net.mingsoft.comment.entity.CommentEntity;

/**
 * 评论业务实现接口
 * 如果需要对接自己的会员系统，具体业务可以通过重写action层实现，业务层代码通用
 * 注意保存评论的接口需要的参数，需要在action做相应限制
 * peopleInfo字段灵活多变，具体存什么信息可以自由扩展
 * @author 铭飞开源团队
 * @date 2019年7月16日
 */
public interface ICommentBiz extends IBaseBiz<CommentEntity> {

	/**
	 * 通用评论业务保存方法
	 * @param comment - 评论类实体，具体根据以下描述规则设置<br>
	 * 1、方法内对基本的信息进行了判断，如：业务类型、评论配置信息判断 <br>
	 * dataType（必填）：评论业务类型，具体字典定义，保存业务会进行类型判断，如果不存在的业务类型系统会提示异常<br>
	 * 2、必须先通过设置字典类型 评论类型 值来区分不同的评论业务类型，如：文章评论、商品评论、店铺评论<br>
	 * 3、用户信息通常需要控制层来决定，根据自身的业务情况，传递用户信息（可选，可以做匿名评论）<br>
	 * peopleId（可选）：设置评论者编号，空为匿名<br>
	 * peopleName（可选）：建议用不可变动的业务字段数据，如评论者会员名称，（主要是避免关联查询）<br>
	 * peopleInfo（可选）：格式：json结构，具体前台展示需要结构进行展示，可以设置评论者其他扩展信息，如：头像、爱好（主要是避免关联查询）<br>
	 * 4、被评论的业务数据编号<br>
	 * dataId（必填）：被评论业务数据编号，（主要是避免关联查询）<br>
	 * dataTitle（必填）：被评论业务数据标题，建议用不可变动的业务字段数据（主要是避免关联查询）（主要是避免关联查询）<br>
	 * dataInfo（可选）：格式：json结构，具体前台展示需要结构进行展示，可以设置信息，如：缩略图、描述（主要是避免关联查询）<br>
	 */
	int saveComment(CommentEntity comment);


	/**
	 * 通用评论业务查询方法，根据dataType默认查询顶级评论<br>
	 * @param commentBean - 评论bean实体，具体根据以下描述规则设置<br>
	 * 1、方法内对基本的信息进行了判断，如：业务类型的判断 <br>
	 * dataType（必填）：评论业务类型，具体字典定义，根据业务类型查询评论，如果不存在的业务类型系统会提示异常<br>
	 * 2、必须先通过设置字典类型 评论类型 值来区分不同的评论业务类型，如：文章评论、商品评论、店铺评论<br>
	 * 3、被评论的业务数据编号<br>
	 * dataId（可选）：被评论业务数据编号，（主要是避免关联查询）<br>
	 * dataTitle（可选）：被评论业务数据标题，模糊查询，（主要是避免关联查询）<br>
	 * 4、评论时间查询<br>
	 * commentStartTime（可选）：起始时间<br>
	 * commentEndTime（可选）：结束时间<br>
	 * 4、其他<br>
	 * 参考xml中的 query 的条件 <br>
	 */
	List<CommentEntity> query(CommentBean commentBean);


	/**
	 * 根据布尔值传参栏进行+1或-1
	 * 可以结合关注插件来实现+1或-1，可以控制一个用户只能赞一次
	 * @param id 评论id
	 * @param flag true为点赞，false为取消
	 * @return
	 */
	CommentEntity like(String id,boolean flag);


}
