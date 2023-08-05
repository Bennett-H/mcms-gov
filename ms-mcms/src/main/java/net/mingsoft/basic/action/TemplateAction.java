/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.action;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.CookieConstEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.constant.Const;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 铭软开发团队
 * @ClassName: TemplateAction
 * @Description: TODO(模板控制层)
 * @date 2020年7月2日
 * 历史修订:
 * 2021-12-25 basic重写-文件配置使用自定义配置管理
 */
@Api(tags={"后端-基础接口"})
@Controller("/basicTemplate")
@RequestMapping("/${ms.manager.path}/basic/template")
public class TemplateAction extends BaseAction {

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("basic:template:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/basic/template/index";
    }

    /**
     * 返回模板list
     */
    @ApiIgnore
    @GetMapping("/list")
    @RequiresPermissions("basic:template:view")
    public String list(HttpServletResponse response, HttpServletRequest request) {
        return "/basic/template/list";
    }

    /**
     * 返回模板编辑页面
     */
    @ApiIgnore
    @GetMapping("/edit")
    @RequiresPermissions("basic:template:view")
    public String edit(HttpServletResponse response, HttpServletRequest request) {
        return "/basic/template/edit";
    }

    /**
     * 点击模版管理，获取所有的模版文件名
     *
     * @param response 响应
     * @param request  请求
     * @return 返回模版文件名集合
     */
    @ApiOperation(value = "点击模版管理，获取所有的模版文件名")
    @ApiImplicitParam(name = "pageNo", value = "pageNo", required = true, paramType = "query")
    @GetMapping("/queryTemplateSkin")
    @RequiresPermissions("basic:template:view")
    @ResponseBody
    protected ResultData queryTemplateSkin(HttpServletResponse response, HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        if (!NumberUtils.isNumber(pageNo)) {
            pageNo = "1";
        }
        List<String> folderNameList = this.queryTemplateFile();
        Map<String, Object> map = new HashMap<>(3);
        map.put("folderNameList", folderNameList);
        map.put("websiteId", BasicUtil.getApp().getAppId());
        map.put("pageNo", pageNo);
        BasicUtil.setCookie(response, CookieConstEnum.PAGENO_COOKIE, pageNo);
        return ResultData.build().success(map);
    }




    /**
     * http://localhost:5118/ms/file/uploadTemplate.do
     * 写入模版文件内容
     *
     * @param model
     * @param request  请求
     * @param response 响应
     * @throws IOException
     */
    @ApiOperation(value = "写入模版文件内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "文件名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "oldFileName", value = "旧文件名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "fileContent", value = "文件内容", required = true, paramType = "query"),
    })
    @LogAnn(title = "写入模版文件内容", businessType = BusinessTypeEnum.UPDATE)
    @PostMapping("/writeFileContent")
    @ResponseBody
    public ResultData writeFileContent(@ApiIgnore ModelMap model, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        LOG.debug("ready modify template");
        String fileName = BasicUtil.getString("fileName");
        // 文件路径
        String uploadTemplatePath = ConfigUtil.getString(net.mingsoft.co.constant.Const.CONFIG_UPLOAD, "uploadTemplate", "template");
        if (!new File(uploadTemplatePath).isAbsolute()) {
            fileName = uploadTemplatePath + File.separator + fileName;
        }
        String templets = BasicUtil.getRealTemplatePath(fileName);
        if (!FileUtil.exist(templets)) {
            return ResultData.build().error(this.getResString("failed.to.edit.a.template"));
        }
        //校验后缀文件名
        if (!checkFileType(fileName)) {
            return ResultData.build().error(this.getResString("failed.to.edit.a.template"));
        }

        String fileContent = BasicUtil.getString("fileContent");
        if (!StringUtils.isEmpty(fileName)) {
            FileWriter.create(new File(templets)).write(fileContent);
            LOG.debug("edit template file：{} success!",fileName);
            return ResultData.build().success();
        }
        return ResultData.build().error();
    }


    /**
     * 删除模版
     * <p>
     * 模版名称
     *
     * @param request 响应
     */
    @ApiOperation(value = "删除模版")
    @ApiImplicitParam(name = "fileName", value = "模版名称", required = true, paramType = "query")
    @LogAnn(title = "删除模版", businessType = BusinessTypeEnum.DELETE)
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("basic:template:del")
    public ResultData delete(HttpServletRequest request) {
        String uploadTemplatePath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadTemplate", "template");
        String fileName = request.getParameter("fileName");
        if (fileName != null && (fileName.contains("..") || fileName.contains("../") || fileName.contains("..\\"))) {
            return ResultData.build().error("非法路径");
        }
        boolean isAbsolute = new File(uploadTemplatePath).isAbsolute();
        String path = (isAbsolute?uploadTemplatePath:BasicUtil.getRealTemplatePath(uploadTemplatePath)) + File.separator + BasicUtil.getApp().getAppId() + File.separator + fileName;
        try {
            FileUtils.deleteDirectory(new File(path));
            return ResultData.build().success();
        } catch (Exception e) {
            return ResultData.build().error();
        }
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
    @ResponseBody
    public ResultData showChildFileAndFolder(HttpServletResponse response, HttpServletRequest request) {
        String uploadTemplatePath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadTemplate", "template");
        List<String> folderNameList = null;
        String skinFolderName = request.getParameter("skinFolderName");
        String uploadFileUrl = skinFolderName;
        boolean isReal = new File(uploadTemplatePath).isAbsolute();
        String filter = isReal ? uploadTemplatePath:BasicUtil.getRealTemplatePath(uploadTemplatePath);
        filter += File.separator + BasicUtil.getApp().getAppId() + File.separator;
        LOG.debug("过滤路径" + filter);
        //非法路径过滤
        if (skinFolderName != null && (skinFolderName.contains("../") || skinFolderName.contains("..\\"))) {
            return ResultData.build().error("非法路径");
        }

        if (isReal){
            skinFolderName = uploadTemplatePath + File.separator + skinFolderName;
        } else {
            skinFolderName = BasicUtil.getRealPath(uploadTemplatePath + File.separator + skinFolderName);
        }
        File files[] = new File(skinFolderName).listFiles();
        Map<String, Object> map = new HashMap<>();
        if (files != null) {
            folderNameList = new ArrayList<String>();
            List<String> fileNameList = new ArrayList<String>();
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
        map.put("uploadFileUrl", uploadFileUrl);
        map.put("websiteId", BasicUtil.getApp().getAppId());
        return ResultData.build().success(map);
    }

    /**
     * 读取模版文件内容
     *
     * @param model
     * @param request 请求
     * @return 返回文件内容
     */
    @ApiOperation(value = "读取模版文件内容")
    @ApiImplicitParam(name = "fileName", value = "文件名称", required = true, paramType = "query")
    @GetMapping("/readFileContent")
    @ResponseBody
    @RequiresPermissions("basic:template:update")
    public ResultData readFileContent(@ApiIgnore ModelMap model, HttpServletRequest request) {
        String fileName = request.getParameter("fileName");
        String filePath = fileName;
        String uploadTemplatePath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadTemplate", "template");
        boolean isReal = new File(uploadTemplatePath).isAbsolute();
        //非法路径过滤
        if (filePath != null && (filePath.contains("../") || filePath.contains("..\\"))) {
            return ResultData.build().error("非法路径");
        }
        filePath = uploadTemplatePath + File.separator + filePath;
        Map<String, Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(filePath)) {
            if (isReal){
                map.put("fileContent", FileReader.create(new File(filePath)).readString());
                map.put("name", new File(filePath).getName());
            } else {
                map.put("fileContent", FileReader.create(new File(BasicUtil.getRealTemplatePath(filePath))).readString());
                map.put("name", new File(BasicUtil.getRealTemplatePath(filePath)).getName());
            }
        }

        map.put("fileName", fileName);
        map.put("fileNamePrefix", fileName.substring(0, fileName.lastIndexOf(File.separator) + 1));
        return ResultData.build().success(map);
    }

    /**
     * 删除模版文件
     * <p>
     * 文件名称
     *
     * @param request 请求
     */
    @ApiOperation(value = "删除模版文件")
    @ApiImplicitParam(name = "fileName", value = "文件名称", required = true, paramType = "query")
    @LogAnn(title = "删除模版文件", businessType = BusinessTypeEnum.DELETE)
    @PostMapping("/deleteTemplateFile")
    @ResponseBody
    public ResultData deleteTemplateFile(HttpServletRequest request) {
        String uploadTemplatePath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadTemplate", "template");
        String fileName = request.getParameter("fileName");
        //非法路径过滤
        if (fileName != null && (fileName.contains("../") || fileName.contains("..\\"))) {
            return ResultData.build().error("非法路径");
        }
        fileName = uploadTemplatePath + File.separator
                + BasicUtil.getApp().getAppId() + File.separator + fileName;
        if (new File(fileName).isAbsolute()) {
            FileUtil.del(fileName);
        } else {
            FileUtil.del(BasicUtil.getRealTemplatePath(fileName));
        }
        return ResultData.build().success();
    }

    /**
     * 递归获取所有的html\htm文件
     *
     * @param list    最终返回列表集合
     * @param fileDir 模版文件夹
     * @param style   风格
     */
    private void files(List list, File fileDir, String style) {
        if (fileDir.isDirectory()) {
            File files[] = fileDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File currFile = files[i];
                if (currFile.isFile()) {
                    String ex = currFile.getName();
                    if (ex.endsWith("htm") || ex.endsWith("html")) {
                        String _pathName = new String();
                        _pathName = files(currFile, style, _pathName);
                        list.add(_pathName + currFile.getName());
                    }
                } else if (currFile.isDirectory()) {
                    files(list, currFile, style);
                }
            }
        }
    }

    /**
     * 递归获取当前风格下所有的文件路径名称
     *
     * @param file
     * @param style
     * @param pathName
     * @return
     */
    private String files(File file, String style, String pathName) {
        if (!file.getParentFile().getName().equals(style)) {
            pathName = file.getParentFile().getName() + "/" + pathName;
            pathName = files(file.getParentFile(), style, pathName);
        }
        return pathName;
    }


    /**
     * 获取当前应用的所有的模版文件夹列表
     *
     * @return 文件夹名称
     */
    private List<String> queryTemplateFile() {
        List<String> folderNameList = null;
        String uploadTemplatePath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadTemplate", "template");
        String templates = "";
        templates = new File(uploadTemplatePath).isAbsolute()?uploadTemplatePath:BasicUtil.getRealTemplatePath(uploadTemplatePath);
        templates += File.separator + BasicUtil.getApp().getAppId() + File.separator;
        File file = new File(templates);
        String[] str = file.list();
        if (str != null) {
            folderNameList = new ArrayList<String>();
            for (int i = 0; i < str.length; i++) {
                // 避免不为文件夹的文件显示
                if (str[i].indexOf(".") < 0) {
                    folderNameList.add(str[i]);
                }
            }
        }
        return folderNameList;
    }

    /**
     * 校验文件后缀名是否符合要求
     *
     * @param fileName 文件名
     * @return false 不合法 true 符合
     */
    protected boolean checkFileType(String fileName) {
        String uploadFileDenied = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadDenied", "exe,jsp");
        //校验后缀文件名
        String[] errorType = uploadFileDenied.split(",");
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        for (String type : errorType) {
            //校验禁止上传的文件后缀名（忽略大小写）
            if ((fileType).equalsIgnoreCase(type)) {
                LOG.info("文件类型被拒绝:{}", fileName);
                return false;
            }
        }
        return true;
    }


    /**
     * 接口：获取当前应用下的模版文件夹列表,提供给应用设置页面调用
     *
     * @param request 请求
     * @return 模版文件集合
     */
    @ApiOperation(value = "查询模版风格供站点选择")
    @GetMapping("/queryAppTemplateSkin")
    @ResponseBody
    public ResultData queryAppTemplateSkin(HttpServletRequest request) {
        List<String> folderNameList = this.queryTemplateFile();
        Map map = new HashMap();
        if (folderNameList != null) {
            map.put("appTemplates", folderNameList);
        }
        return ResultData.build().success(map);
    }


    /**
     * 接口：获取指定模下面所有的模版文件
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询模版文件供栏目选择，可指定模板名称，不传查询应用设置中选择的模板")
    @GetMapping("/queryTemplateFileForColumn")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appStyle", value = "可选，可以指定template下的文件夹名称", required = false, paramType = "query"),
    })
    @ResponseBody
    public ResultData queryTemplateFileForColumn(HttpServletRequest request) {
        //优先 appStyle 接口传递过来的 模版风格
        String appStyle = BasicUtil.getString("appStyle", BasicUtil.getApp().getAppStyle());
        if (StringUtils.isBlank(appStyle)){
            return ResultData.build().success();
        }
        String uploadTemplatePath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadTemplate", "template");
        boolean isReal = new File(uploadTemplatePath).isAbsolute();
        String path = isReal?(uploadTemplatePath + File.separator + BasicUtil.getApp().getAppId() + File.separator)
                :(BasicUtil.getRealPath(uploadTemplatePath + File.separator + BasicUtil.getApp().getAppId() + File.separator).replaceAll("\\\\","/"));
        List<File> list = FileUtil.loopFiles(path + appStyle, new FileFilter() {
            @Override
            public boolean accept(File pathname) {

                //遇到文件乱码，获取类型会失败，需要进行异常捕获
                try {
                    if (FileTypeUtil.getType(pathname).equalsIgnoreCase("html") || FileTypeUtil.getType(pathname).equalsIgnoreCase("htm")) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e){
                    return false;
                }

            }
        });

        List<String> collect = list.stream().map(file -> {
            return file.getPath().replaceAll("\\\\","/").replace(path.replaceAll("\\\\","/"), "").substring(appStyle.length() + 1);
        }).collect(Collectors.toList());

        return ResultData.build().success(collect);
    }

}
