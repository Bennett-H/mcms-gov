/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.biz;

import net.mingsoft.attention.entity.CollectionEntity;
import net.mingsoft.base.biz.IBaseBiz;

import java.util.List;


/**
 * 关注记录业务
 * @author 铭飞开发团队
 * 创建日期：2019-11-22 14:27:38<br/>
 * 历史修订：<br/>
 */
public interface ICollectionBiz extends IBaseBiz<CollectionEntity> {


    /**
     * 通用保存关注或者保存点赞业务方法(重复调用等于取消关注或取消点赞)
     * @param collectionEntity - 关注类实体，具体根据一下描述规则设置<br>
     * 1、方法内对基本的信息进行了判断，如：业务类型 <br>
     * dataType（必填）：关注业务类型，具体字典定义，保存业务会进行类型判断，如果不存在的业务类型系统会提示异常<br>
     * 2、必须先通过设置字典类型 关注类型 值来区分不同的关注业务类型，如：文章点赞、商品点赞或者关注、店铺点赞或者关注<br>
     * 3、用户信息通常需要控制层来决定，根据自身的业务情况，传递用户信息（可选）<br>
     * peopleId（必填）：设置关注者编号，不可为空<br>
     * peopleName（可选）：建议用不可变动的业务字段数据，如关注者会员名称，（主要是避免关联查询）<br>
     * peopleInfo（可选）：格式：json结构，具体前台展示需要结构进行展示，可以设置关注者其他扩展信息，如：头像、爱好（主要是避免关联查询）<br>
     * 4、被点赞或者关注的业务数据编号<br>
     * dataId（必填）：被点赞或者关注业务数据编号，（主要是避免关联查询）<br>
     * collectionDataTitle（必填）：被关注业务数据标题，建议用不可变动的业务字段数据（主要是避免关联查询）<br>
     * collectionDataImg（选填）：被点赞或者关注业务数据图片路径，如：关注文章的图片，关注商品的图片等<br>
     * collectionDataUrl（选填）：被点赞或者关注业务数据链接，可以设置关注或点赞数据业务链接，如：关注文章的链接地址，关注商品的链接地址等<br>
     * collectionDataJson（选填）：被点赞或者关注业务拓展数据，可以设置关注或点赞数据其他拓展数据<br>
     * @return 布尔值,true表示关注,false表示取消关注
     */
    boolean saveOrDelete(CollectionEntity collectionEntity);




}
