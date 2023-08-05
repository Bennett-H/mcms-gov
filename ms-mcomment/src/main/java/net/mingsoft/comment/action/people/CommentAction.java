/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.comment.action.people;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.comment.bean.CommentBean;
import net.mingsoft.comment.biz.ICommentBiz;
import net.mingsoft.comment.entity.CommentEntity;
import net.mingsoft.people.biz.IPeopleUserBiz;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论插件
 *
 * @author 铭飞开源团队
 * @date 2019年7月16日
 */
@Api(tags = {"前端-用户-评论模块接口"})
@Controller("peopleComment")
@RequestMapping("/people/comment")
public class CommentAction extends net.mingsoft.comment.action.BaseAction {

    /**
     * 注入评论表业务层
     */
    @Autowired
    private ICommentBiz commentBiz;

    /**
     * 会员业务层层
     */
    @Autowired
    private IPeopleUserBiz peopleUserBiz;

    /**
     * 查询评论表列表
     *
     * @param comment 评论表实体
     */
    @ApiOperation(value = "获取评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "评论类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dataId", value = "数据Id", required =true,paramType="query"),
            @ApiImplicitParam(name = "pageSize", value = "一页显示数量", required =false,paramType="query"),
            @ApiImplicitParam(name = "pageNo", value = "当前页面", required =false,paramType="query")
    })
    @PostMapping(value ="/list")
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore CommentBean comment, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {

        comment.setPeopleId(this.getPeopleBySession().getIntegerId());
        List<CommentEntity> commentList = commentBiz.query(comment);
        EUListBean list = new EUListBean(commentList, (int) BasicUtil.endPage(commentList).getTotal());
        return ResultData.build().success(list);
    }

    @ApiOperation(value = "发布评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataId", value = "数据id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "业务类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "commentId", value = "父评论id", required = false, paramType = "query"),
            @ApiImplicitParam(name = "commentPoints", value = "评价打分", required = false, paramType = "query"),
            @ApiImplicitParam(name = "commentContent", value = "评论的内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "commentPicture", value = "图片", required = false, paramType = "query"),
            @ApiImplicitParam(name = "commentFileJson", value = "附件json", required = false, paramType = "query"),
            @ApiImplicitParam(name = "rand_code", value = "会员模式默认验证码非必须", required = false, paramType = "query")
    })
    @PostMapping("/save")
    @ResponseBody
    public ResultData save(@ModelAttribute @ApiIgnore CommentEntity comment, HttpServletRequest request, HttpServletResponse response) {

        // 判断登陆设置peopleId
        PeopleEntity people = this.getPeopleBySession();
        PeopleUserEntity peopleUserEntity = (PeopleUserEntity) peopleUserBiz.getEntity(people.getIntId());
        // 设置会员相关信息
        if (peopleUserEntity != null){

            Map commentPeopleInfo = new HashMap();
            // 这里可以根据业务需求填充用户数据
            commentPeopleInfo.put("puIcon",peopleUserEntity.getPuIcon());
            String peopleInfo = JSONUtil.toJsonStr(commentPeopleInfo);
            comment.setPeopleInfo(peopleInfo);

            comment.setPeopleName(peopleUserEntity.getPuNickname());
            comment.setPeopleId(peopleUserEntity.getPeopleId());
        }
        commentBiz.saveComment(comment);
        return ResultData.build().success(JSONUtil.toJsonStr(comment));
    }

    @ApiOperation(value = "删除评论记录")
    @LogAnn(title = "删除评论记录", businessType = BusinessTypeEnum.DELETE)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "主键id", required = true, paramType = "query"),
    })
    @PostMapping("/delete")
    @ResponseBody
    public ResultData delete(String id){
        PeopleEntity people = this.getPeopleBySession();
        if (StringUtils.isBlank(id)){
            return ResultData.build().error(getResString("err.not.exist",this.getResString("comment")));
        }
        CommentEntity comment = commentBiz.getById(id);
        if (comment==null || !people.getIntegerId().equals(comment.getPeopleId())){
            return ResultData.build().error(getResString("err.not.exist",this.getResString("comment")));
        }

        commentBiz.removeById(id);
        return ResultData.build().success();
    }


    /**
     * 根据id获取评论详情及评论人信息
     * @param id
     * @return
     */
    @ApiOperation("获取评论信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "主键id",required = true,paramType = "query")
    })
    @GetMapping("/getCommentById")
    @ResponseBody
    public ResultData getCommentById(String id){
        if (StringUtils.isBlank(id)){
            return ResultData.build().error(getResString("err.not.exist",this.getResString("comment")));
        }
        CommentEntity commentEntity = commentBiz.getById(id);
        return ResultData.build().success(BasicUtil.filter(commentEntity,"commentIp","del"));
    }
}
