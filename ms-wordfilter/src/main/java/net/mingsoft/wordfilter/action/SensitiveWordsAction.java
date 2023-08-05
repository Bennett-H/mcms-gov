/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.wordfilter.action;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import net.mingsoft.wordfilter.biz.ISensitiveWordsBiz;
import net.mingsoft.wordfilter.entity.SensitiveWordsEntity;
import net.mingsoft.wordfilter.util.SensitiveWordsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 敏感词管理控制层
 * @author 铭软科技
 * 创建日期：2021-1-7 15:54:50<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-敏感词模块接口"})
@Controller("wordfilterSensitiveWordsAction")
@RequestMapping("/${ms.manager.path}/wordfilter/sensitiveWords")
public class SensitiveWordsAction extends BaseAction{


	/**
	 * 注入敏感词业务层
	 */
	@Autowired
	private ISensitiveWordsBiz sensitiveWordsBiz;


	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("wordfilter:sensitiveWords:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/wordfilter/sensitive-words/index";
	}

	/**
	 * 返回编辑界面sensitiveWords_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"wordfilter:sensitiveWords:save", "wordfilter:sensitiveWords:update"}, logical = Logical.OR)
	public String form(@ModelAttribute SensitiveWordsEntity sensitiveWords,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		return "/wordfilter/sensitive-words/form";
	}

	/**
	 * 查询敏感词列表
	 * 此接口不可加权限标识，例如用户无替换词权限但是有文章管理权限
	 * @param sensitiveWords 敏感词实体
	 */
	@ApiOperation(value = "查询敏感词列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "word", value = "敏感词", required =false,paramType="query"),
    	@ApiImplicitParam(name = "replaceWord", value = "替换词", required =false,paramType="query"),
		@ApiImplicitParam(name = "wordType", value = "词汇类型", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore SensitiveWordsEntity sensitiveWords,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		// 重新初始化敏感词
		List<SensitiveWordsEntity> sensitiveWordsList = sensitiveWordsBiz.list(new LambdaQueryWrapper<>(sensitiveWords).orderByDesc(SensitiveWordsEntity::getId));
		return ResultData.build().success(new EUListBean(sensitiveWordsList,(int)BasicUtil.endPage(sensitiveWordsList).getTotal()));
	}

	/**
	 * 获取敏感词
	 * @param sensitiveWords 敏感词实体
	 */
	@ApiOperation(value = "获取敏感词列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore SensitiveWordsEntity sensitiveWords,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(sensitiveWords.getId()==null) {
			return ResultData.build().error();
		}
		// 重新初始化敏感词
		SensitiveWordsEntity _sensitiveWords = sensitiveWordsBiz.getById(sensitiveWords.getId());
		return ResultData.build().success(_sensitiveWords);
	}

	/**
	* 保存敏感词
	* @param sensitiveWords 敏感词实体
	*/
	@ApiOperation(value = "保存敏感词列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "word", value = "敏感词", required =false,paramType="query"),
			@ApiImplicitParam(name = "replaceWord", value = "替换词", required =false,paramType="query"),
			@ApiImplicitParam(name = "wordType", value = "词汇类型", required =true,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存敏感词", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("wordfilter:sensitiveWords:save")
	public ResultData save(@ModelAttribute @ApiIgnore SensitiveWordsEntity sensitiveWords, HttpServletResponse response, HttpServletRequest request) {
		if(!StringUtil.checkLength(sensitiveWords.getWord()+"", 1, 20)){
			return ResultData.build().error(getResString("err.length", this.getResString("word"), "0", "20"));
		}
		if(!StringUtil.checkLength(sensitiveWords.getReplaceWord()+"", 0, 20)){
			return ResultData.build().error(getResString("err.length", this.getResString("replace.word"), "0", "20"));
		}
		if(StringUtils.isEmpty(sensitiveWords.getWordType())) {
			return ResultData.build().error(getResString("err.empty",getResString("word.type")));
		}
		// 清除多余空格
		sensitiveWords.setWord((sensitiveWords.getWord()+"").trim());
		sensitiveWords.setReplaceWord((sensitiveWords.getReplaceWord()+"").trim());
		// 插入一条敏感词
		SensitiveWordsUtil.setSensitiveMap(sensitiveWords.getWord(),sensitiveWords.getReplaceWord());
		// 保存
		sensitiveWordsBiz.save(sensitiveWords);

		// 重新初始化敏感词
		return ResultData.build().success(sensitiveWords);
	}

	@ApiOperation(value = "批量删除敏感词列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除敏感词", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("wordfilter:sensitiveWords:del")
	public ResultData delete(@RequestBody List<SensitiveWordsEntity> sensitiveWordss,HttpServletResponse response, HttpServletRequest request) {
		List<String> ids = sensitiveWordss.stream().map(SensitiveWordsEntity::getId).collect(Collectors.toList());
		if(CollUtil.isNotEmpty(ids)){
			if (sensitiveWordsBiz.removeByIds(ids)) {
				// 删除前缀树中的敏感词
				for (SensitiveWordsEntity sensitive : sensitiveWordss){
					SensitiveWordsUtil.removeSensitiveWord(sensitive.getWord());
				}

				return ResultData.build().success();
			} else {
				return ResultData.build().error();
			}
		}
		return ResultData.build().error(getResString("err.empty",getResString("id")));
	}

	/**
	*	更新敏感词列表
	* @param sensitiveWords 敏感词实体
	*/
	 @ApiOperation(value = "更新敏感词列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "word", value = "敏感词", required =false,paramType="query"),
		@ApiImplicitParam(name = "replaceWord", value = "替换词", required =false,paramType="query"),
	 	@ApiImplicitParam(name = "wordType", value = "词汇类型", required =true,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新敏感词", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("wordfilter:sensitiveWords:update")
	public ResultData update(@ModelAttribute @ApiIgnore SensitiveWordsEntity sensitiveWords, HttpServletResponse response,
			HttpServletRequest request) {
		if(!StringUtil.checkLength(sensitiveWords.getWord()+"", 1, 20)){
			return ResultData.build().error(getResString("err.length", this.getResString("word"), "0", "20"));
		}
		if(!StringUtil.checkLength(sensitiveWords.getReplaceWord()+"", 0, 20)){
			return ResultData.build().error(getResString("err.length", this.getResString("replace.word"), "0", "20"));
		}
		if(StringUtils.isEmpty(sensitiveWords.getWordType())) {
			return ResultData.build().error(getResString("err.empty",getResString("word.type")));
		}

		 // 清除多余空格
		sensitiveWords.setWord((sensitiveWords.getWord()+"").trim());
		sensitiveWords.setReplaceWord((sensitiveWords.getReplaceWord()+"").trim());

		 //先获取老数据并同步缓存
		 SensitiveWordsEntity oldSensitiveWordsEntity = sensitiveWordsBiz.getById(sensitiveWords);

		 SensitiveWordsUtil.removeSensitiveWord(oldSensitiveWordsEntity.getWord());
		 // 插入一条敏感词
		 SensitiveWordsUtil.setSensitiveMap(sensitiveWords.getWord(),sensitiveWords.getReplaceWord());

		sensitiveWordsBiz.updateById(sensitiveWords);
		 // 重新初始化敏感词
		return ResultData.build().success(sensitiveWords);
	}

	@ApiOperation(value = "检验敏感词是否重复接口")
	@GetMapping("verify")
	@ResponseBody
	public ResultData verify(String fieldName, String fieldValue, String id,String idName){
		boolean verify = false;
		if(StringUtils.isBlank(id)){
			verify = super.validated("wordfilter_sensitive_words",fieldName,fieldValue);
		}else{
			verify = super.validated("wordfilter_sensitive_words",fieldName,fieldValue,id,idName);
		}
		if(verify){
			return ResultData.build().success(false);
		}else {
			return ResultData.build().success(true);
		}
	}
}
