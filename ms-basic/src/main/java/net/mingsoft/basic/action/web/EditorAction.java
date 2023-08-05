/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.action.web;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.mingsoft.ueditor.MsUeditorActionEnter;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.config.MSProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 百度编辑器上传
 *
 * @author 铭软开发团队
 * @date 2019年7月16日
 * 历史修订 2022-1-21 新增normalize(),
 * editor()方法过滤非法上传路径
 */
@ApiIgnore
@Controller("ueAction")
@RequestMapping("/static/plugins/ueditor/{version}/jsp")
public class EditorAction {

    @ResponseBody
    @RequestMapping(value = "editor", method = {RequestMethod.GET, RequestMethod.POST})
    public String editor(HttpServletRequest request, HttpServletResponse response, String jsonConfig) {
        String uploadFolderPath = MSProperties.upload.path;
        String rootPath = BasicUtil.getRealPath(uploadFolderPath);
        jsonConfig = jsonConfig.replace("{ms.upload}", "/" + uploadFolderPath);
        //过滤非法上传路径
        Map<String, Object> map = (Map<String, Object>) JSONUtil.toBean(jsonConfig,Map.class);
        String imagePathFormat = (String) map.get("imagePathFormat");
        imagePathFormat = FileUtil.normalize(imagePathFormat);

        String filePathFormat = (String) map.get("filePathFormat");
        filePathFormat = FileUtil.normalize(filePathFormat);

        String videoPathFormat = (String) map.get("videoPathFormat");
        videoPathFormat = FileUtil.normalize(videoPathFormat);

        map.put("imagePathFormat", imagePathFormat);
        map.put("filePathFormat", filePathFormat);
        map.put("videoPathFormat", videoPathFormat);

        jsonConfig = JSONUtil.toJsonStr(map);
        MsUeditorActionEnter actionEnter = new MsUeditorActionEnter(request, rootPath, jsonConfig, BasicUtil.getRealPath(""));
        String json = actionEnter.exec();
        Map jsonMap = JSONUtil.toBean(json,Map.class);
        jsonMap.put("url","/".concat(uploadFolderPath).concat(jsonMap.get("url")+""));
        return JSONUtil.toJsonStr(jsonMap);
    }

}
