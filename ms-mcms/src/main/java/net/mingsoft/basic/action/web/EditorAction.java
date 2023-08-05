/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.action.web;

import cn.hutool.json.JSONUtil;
import com.mingsoft.ueditor.MsUeditorActionEnter;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.constant.Const;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Map;

/**
 * 百度编辑器上传
 * @author 铭软开发团队
 * @date 2019年7月16日
 * 历史修订:
 * 2021-12-25 basic重写-文件配置使用自定义配置管理
 * 2021-12-27 舍弃uploadMapping配置
 */
@ApiIgnore
@Controller("ueAction")
@RequestMapping("/static/plugins/ueditor/{version}/jsp")
public class EditorAction {

    @ResponseBody
    @RequestMapping(value = "editor", method = {RequestMethod.GET,RequestMethod.POST})
    public String editor(HttpServletRequest request, HttpServletResponse response, String jsonConfig) {
        String uploadFolderPath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadPath", "upload");
        String uploadMapping = File.separator + uploadFolderPath + File.separator + "**";
        String rootPath = BasicUtil.getRealPath("");
        //如果是绝对路径就直接使用配置的绝对路径
        File saveFloder=new File(uploadFolderPath);
        if (saveFloder.isAbsolute()) {
            rootPath = saveFloder.getPath();
            //因为绝对路径已经映射了路径所以隐藏
            jsonConfig = jsonConfig.replace("{ms.upload}", "");
        } else {
            //如果是相对路径替换成配置的路径
            jsonConfig = jsonConfig.replace("{ms.upload}","/"+uploadFolderPath);
        }
        //执行exec()方法才保存文件
        String json = new MsUeditorActionEnter(request, rootPath, jsonConfig, BasicUtil.getRealPath("")).exec();
        if (saveFloder.isAbsolute()) {
            //如果是配置的绝对路径需要在前缀加上映射路径
            Map data = JSONUtil.toBean(json,Map.class);
            data.put("url", uploadMapping.replace("/**", "") + data.get("url"));
            return JSONUtil.toJsonStr(data);
        }
        return json;
    }
}
