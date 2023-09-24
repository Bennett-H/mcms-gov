/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.biz.IArticleBiz;
import net.mingsoft.mweixin.entity.ArticleEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 * 文章管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-12-25 9:27:11<br/>
 * 历史修订：<br/>
 * @deprecated 权限标识不存在，对此文件是否被使用以及是否有用存疑
 */
@Api(tags = {"后端-微信模块接口"})
@Controller("mweixinArticleAction")
@RequestMapping("/${ms.manager.path}/mweixin/article")
public class ArticleAction extends BaseAction{
	
	
	/**
	 * 注入文章业务层
	 */	
	@Autowired
	private IArticleBiz articleBiz;

	
	/**
	 * 查询文章列表
	 * @param article 文章实体
	 */
	@ApiOperation(value = "查询文章列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "articleTitle", value = "文章标题", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleAuthor", value = "文章作者", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleDescription", value = "文章描述", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleSource", value = "文章来源", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleUrl", value = "原文链接", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleType", value = "素材类型", required =false,paramType="query"),
    	@ApiImplicitParam(name = "newsId", value = "微信素材编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleContent", value = "文章内容", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleDisplay", value = "是否显示", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleHit", value = "文章点击数", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleSort", value = "文章排序", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleThumbnails", value = "文章图片", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore ArticleEntity article,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List articleList = articleBiz.query(article);
		return ResultData.build().success(new EUListBean(articleList,(int)BasicUtil.endPage(articleList).getTotal()));
	}


	/**
	 * 获取文章
	 * @param article 文章实体
	 */
	@ApiOperation(value = "获取文章列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore ArticleEntity article,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(article.getId()==null) {
			return ResultData.build().error();
		}
		ArticleEntity _article = (ArticleEntity)articleBiz.getEntity(Integer.parseInt(article.getId()));
		return ResultData.build().success(_article);
	}
	


	/**
	* 保存文章
	* @param article 文章实体
	*/
	@ApiOperation(value = "保存文章列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "articleTitle", value = "文章标题", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleAuthor", value = "文章作者", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleDescription", value = "文章描述", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleSource", value = "文章来源", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleUrl", value = "原文链接", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleType", value = "素材类型", required =false,paramType="query"),
			@ApiImplicitParam(name = "newsId", value = "微信素材编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleContent", value = "文章内容", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleDisplay", value = "是否显示", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleHit", value = "文章点击数", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleSort", value = "文章排序", required =false,paramType="query"),
			@ApiImplicitParam(name = "articleThumbnails", value = "文章图片", required =false,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存文章", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("news:save")
	public ResultData save(@ModelAttribute @ApiIgnore ArticleEntity article, HttpServletResponse response, HttpServletRequest request) {
		if(!StringUtil.checkLength(article.getArticleTitle()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.title"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getArticleAuthor()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.author"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getArticleDescription()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.description"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getArticleSource()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.source"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getArticleUrl()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.url"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getDraftId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("news.id"), "0", "11"));
		}
		articleBiz.saveEntity(article);
		return ResultData.build().success(article);
	}
	
	/**
	 * @param article 文章实体
	 */
	@ApiOperation(value = "批量删除文章列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除文章", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("news:del")
	public ResultData delete(@RequestBody List<ArticleEntity> articles,HttpServletResponse response, HttpServletRequest request) {
		if (articles.size()<=0){
			return ResultData.build().error("未选择文章删除");
		}
		int[] ids = new int[articles.size()];
		for(int i = 0;i<articles.size();i++){
			ids[i] =Integer.parseInt(articles.get(i).getId()) ;
		}
		articleBiz.delete(ids);
		return ResultData.build().success();
	}
	/**
	*	更新文章列表
	* @param article 文章实体
	*/
	 @ApiOperation(value = "更新文章列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "articleTitle", value = "文章标题", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleAuthor", value = "文章作者", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleDescription", value = "文章描述", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleSource", value = "文章来源", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleUrl", value = "原文链接", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleType", value = "素材类型", required =false,paramType="query"),
		@ApiImplicitParam(name = "newsId", value = "微信素材编号", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleContent", value = "文章内容", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleDisplay", value = "是否显示", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleHit", value = "文章点击数", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleSort", value = "文章排序", required =false,paramType="query"),
		@ApiImplicitParam(name = "articleThumbnails", value = "文章图片", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新文章", businessType = BusinessTypeEnum.UPDATE)
	 @RequiresPermissions("news:update")
	public ResultData update(@ModelAttribute @ApiIgnore ArticleEntity article, HttpServletResponse response,
			HttpServletRequest request) {
		if(!StringUtil.checkLength(article.getArticleTitle()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.title"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getArticleAuthor()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.author"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getArticleDescription()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.description"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getArticleSource()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.source"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getArticleUrl()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("article.url"), "0", "11"));
		}
		if(!StringUtil.checkLength(article.getDraftId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("news.id"), "0", "11"));
		}
		articleBiz.updateEntity(article);
		return ResultData.build().success(article);
	}


		
}
