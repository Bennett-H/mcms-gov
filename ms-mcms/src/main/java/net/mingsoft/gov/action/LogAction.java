/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.gov.action;

import cn.hutool.core.io.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.base.exception.BusinessException;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.basic.util.BasicUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Api(tags = {"后端-日志接口"})
@Controller("LogAction")
@RequestMapping("/${ms.manager.path}/gov/log")
public class LogAction extends BaseAction {


    /**
     * 日志备份目录
     */
    @Value("${ms.bak-path.log:config/bak/log}")
    private String bakPathLog ;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("gov:log:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/gov/log/index";
    }

    /**
     * 返回日志list
     */
    @ApiIgnore
    @GetMapping("/list")
    @RequiresPermissions("gov:log:view")
    public String list(HttpServletResponse response, HttpServletRequest request) {
        return "/gov/log/list";
    }

    /**
     * 点击模版管理，获取所有的模版文件名
     *
     * @param response 响应
     * @param request  请求
     * @return 返回日志文件名集合
     */
    @ApiOperation(value = "点击日志备份管理，获取所有的模版文件名")
    @GetMapping("/queryLogBackSkin")
    @RequiresPermissions("gov:log:view")
    @ResponseBody
    protected ResultData queryLogBackSkin(HttpServletResponse response, HttpServletRequest request) {
        String path = BasicUtil.getRealPath(bakPathLog);
        File[] folderList = new File(path).listFiles();
        List<String> folderNameList = new LinkedList<>();
        if (folderList != null) {
            for (File file : folderList) {
                folderNameList.add(file.getName());
            }
        }

        Map<String, Object> map = new HashMap<>(1);
        map.put("folderNameList", folderNameList);
        return ResultData.build().success(map);
    }

    /**
     * 显示子文件和子文件夹
     *
     * @param response 响应
     * @param request  请求
     * @return 返回文件名集合
     */
    @ApiOperation(value = "显示子文件和子文件夹")
    @ApiImplicitParam(name = "skinFolderName", value = "skinFolderName", required = true, paramType = "query")
    @GetMapping("/showChildFileAndFolder")
    @RequiresPermissions("gov:log:view")
    @ResponseBody
    public ResultData showChildFileAndFolder(HttpServletResponse response, HttpServletRequest request) {
        List<String> folderNameList = null;
        String skinFolderName = request.getParameter("skinFolderName");
        String filter = BasicUtil.getRealPath(bakPathLog);
        LOG.debug("过滤路径" + filter);
        //非法路径过滤
        if (skinFolderName != null && (skinFolderName.contains("../") || skinFolderName.contains("..\\"))) {
            return ResultData.build().error("非法路径");
        }
        skinFolderName = bakPathLog + skinFolderName;
        File files[] = new File(BasicUtil.getRealTemplatePath(skinFolderName)).listFiles();
        Map<String, Object> map = new HashMap<>();
        if (files != null) {
            folderNameList = new ArrayList<>();
            List<String> fileNameList = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                File currFile = files[i];

                String temp = currFile.getPath();
                //以当前系统分隔符作判断，将不是当前系统的分隔符替换为当前系统的

                temp = temp.replace(File.separator.equals("\\") ? "/" : "\\", File.separator).replace(filter, "");
                if (currFile.isDirectory()) {
                    folderNameList.add(temp);
                } else {
                    fileNameList.add(temp);
                }
            }

            //记录文件夹数量
            map.put("folderNum", folderNameList.size());
            folderNameList.addAll(fileNameList);
            map.put("fileNameList", folderNameList);
        }
        String uploadFileUrl = skinFolderName;
        map.put("uploadFileUrl", uploadFileUrl);
        return ResultData.build().success(map);
    }

    /**
     * 下载日志文件内容
     *
     * @param map 请求
     * @return 返回文件内容
     */
    @ApiIgnore
    @PostMapping("/downLogFile")
    @ResponseBody
    @RequiresPermissions("gov:log:download")
    public ResponseEntity<byte[]> downLogFile(@ApiIgnore @RequestBody Map<String, String> map) {
        String folderName = map.get("folderName");
        String logName = map.get("fileName");
        String fileName = bakPathLog + folderName + "/" + logName;
        if (fileName.contains("..") || fileName.contains("../") || fileName.contains("..\\")){
            throw new BusinessException("非法路径");
        }
        String path = BasicUtil.getRealPath(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add("fileName", logName);
        headers.setContentDispositionFormData("fileName",logName);
        byte[] bytes = FileUtil.readBytes(new File(path));
        return new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);
    }

}
