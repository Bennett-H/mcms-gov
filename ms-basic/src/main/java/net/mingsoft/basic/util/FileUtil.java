/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.util;

import cn.hutool.json.JSONUtil;
import net.mingsoft.base.entity.BaseEntity;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: FileUtil
 * @Description: TODO(清理文件)
 * @author 铭软开发团队
 * @date 2018年7月29日
 *
 */
public class FileUtil {

	/**
	 * 文件后缀
	 */
	private static String fileSuffix = "[/|\\\\]upload.*?\\.(rmvb|mpga|mpg4|mpeg|docx|xlsx|pptx|jpeg|[a-z]{3})";

	/**
	 * @Title: del
	 * @Description: TODO(查找出json数据里面的附件路径，并执行删除)
	 * @param json
	 *            通常是业务实体转换之后的json字符串
	 */
	public static void del(String json) {
		Pattern pattern = Pattern.compile(fileSuffix);
		Matcher matcher = pattern.matcher(json);
		while (matcher.find()) {
			try {
				FileUtils.forceDelete(new File(BasicUtil.getRealPath(matcher.group())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @Title: del
	 * @Description: TODO(查找出list数据里面的附件路径，并执行删除)
	 * @param list
	 *            对象集合
	 */
	public static void del(List<?> list) {
		String json = "";
		for (Object entity : list) {
			json = JSONUtil.toJsonStr(entity);
			FileUtil.del(json);
		}
	}

	/**
	 * @Title: del
	 * @Description: TODO(查找出实体数据里面的附件路径，并执行删除)
	 * @param entity
	 *            实体对象
	 */
	public static void del(BaseEntity entity) {
		String json = JSONUtil.toJsonStr(entity);
		FileUtil.del(json);
	}

}
