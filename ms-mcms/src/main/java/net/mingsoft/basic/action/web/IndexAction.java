/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.basic.action.web;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.action.BaseAction;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2020/1/10 8:39
 * 历史修订2021-12-17:basic重写、针对企业版多皮肤业务进行处理
 */
@ApiIgnore
@Controller("indexAction")
public class IndexAction extends BaseAction {

    @Value("${ms.diy.html-dir:html}")
    private String htmlDIr;

    private static String INDEX = "index.html", DEFAULT = "default.html";



    /**
     * 访问站点主页
     *
     * @param req
     *            HttpServletRequest对象
     * @param res
     *            HttpServletResponse 对象
     * @throws ServletException
     *             异常处理
     * @throws IOException
     *             异常处理
     */
    @ApiOperation(value = "访问站点主页")
    @GetMapping("/msIndex")
    public String index(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.debug("co index");
        boolean flag = Boolean.parseBoolean(ConfigUtil.getString("静态化配置", "dynamicSwitch", "false"));
        if (flag) {
            return "mcms/index.do";
        }
        JSONArray array = JSONUtil.parseArray(BasicUtil.getApp().getAppStyles());
        String template = "";
        if (array != null) {
            JSONObject jsonObject = JSONUtil.parseObj(array.get(0));
            template = jsonObject.get("template").toString();
        }
        HashMap<String, Object> map = new HashMap<>();
        ParserUtil.putBaseParams(map,template);
        String htmlPath = ParserUtil.buildViewHtmlPath( map.get(ParserUtil.APP_DIR).toString(),map.get(ParserUtil.APP_TEMPLATE).toString(),"");
        String defaultHtmlPath = BasicUtil.getRealPath(MSProperties.diy.htmlDir + File.separator + htmlPath + File.separator + DEFAULT);
        LOG.debug("defaultHtmlPath {}", defaultHtmlPath);
        String url = htmlPath;
        LOG.debug("url {}", url);
        //跳转default首页
        if (FileUtil.exist(defaultHtmlPath)) {
            LOG.debug("default  {}", DEFAULT);
            return "forward:" + url + DEFAULT;
        }
        return "forward:" + url + (url.endsWith("\\") || url.endsWith("/") ? "" : File.separator) + INDEX;
    }
}
