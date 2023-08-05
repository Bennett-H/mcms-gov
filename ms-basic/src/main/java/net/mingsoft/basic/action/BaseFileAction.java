/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.basic.action;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import net.mingsoft.base.constant.Const;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.config.MSProperties;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author by Administrator
 * @Description TODO
 * @date 2019/9/29 13:46
 */
public abstract class BaseFileAction extends BaseAction {

    /**
     * 统一上传文件方法
     *
     * @param config
     * @return
     * @throws IOException
     */
    public ResultData upload(UploadConfigBean config) throws IOException {
        String uploadMapping = MSProperties.upload.mapping;
        String uploadFileDenied = MSProperties.upload.denied;
        String uploadFolderPath = MSProperties.upload.path;
        // 过滤掉的文件类型
        String[] errorType = uploadFileDenied.split(",");
        //文件上传类型限制
        String fileName = config.getFile().getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return ResultData.build().error("文件名不能为空!");
        }
        //清理无效的类型
        fileName = FileNameUtil.cleanInvalid(fileName);
        if (fileName.lastIndexOf(".") < 0) {
            LOG.info("文件格式错误:{}", fileName);
            return ResultData.build().error(getResString("err.error", getResString("file.name")));
        }

        String fileType = FileUtil.getSuffix(fileName);
        //修改上传物理路径
        String realPath = config.isUploadFolderPath() ? BasicUtil.getRealPath("") : BasicUtil.getRealPath(uploadFolderPath);
        if (StringUtils.isNotBlank(config.getRootPath())) {
            realPath = config.getRootPath();
        }
        //先判断文件类型是否合法
        for (String type : errorType) {
            if ((fileType).equalsIgnoreCase(type)) {
                LOG.info("文件类型被拒绝:{}", fileType);
                return ResultData.build().error(getResString("err.error", getResString("file.type")));
            }
        }
        //修改文件名
        if (config.isRename()) {
            fileName = System.currentTimeMillis() + "." + fileType;
        }

        // 上传的文件路径,判断是否填的绝对路径
        String uploadFolder = realPath + File.separator;
        //修改upload下的上传路径
        if (StringUtils.isNotBlank(config.getUploadPath())) {
            uploadFolder += config.getUploadPath() + File.separator;
        }
        //保存文件
        File saveFolder = new File(uploadFolder);
        File saveFile = new File(uploadFolder, fileName);
        if (!saveFolder.exists()) {
            FileUtil.mkdir(saveFolder);
        }
        config.getFile().transferTo(saveFile);
        //绝对映射路径处理
        //如果uploadFolderPath = true则返回路径中不拼upload的路径
        String path = (config.isUploadFolderPath() ? "" : uploadMapping.replace("**", ""))
                //转为相对路径
                + uploadFolder.replace(realPath, "")
                //添加文件名
                + Const.SEPARATOR + fileName;
        //替换多余
        return ResultData.build().success(new File(Const.SEPARATOR + path).getPath().replace("\\", "/").replace("//", "/"));
    }

    public ResultData uploadTemplate(UploadConfigBean config) throws IOException {
        String uploadTemplatePath = MSProperties.upload.template;
        String uploadFileDenied = MSProperties.upload.denied;
        String[] errorType = uploadFileDenied.split(",");
        //文件上传类型限制
        //获取文件名字
        String fileName = config.getFile().getOriginalFilename();
        if (fileName.lastIndexOf(".") < 0) {
            LOG.info("文件格式错误:{}", fileName);
            return ResultData.build().error(getResString("err.error", getResString("file.name")));
        }
        //获取文件类型
        String fileType = FileUtil.getSuffix(fileName);
        //判断上传路径是否为绝对路径
        boolean isReal = new File(uploadTemplatePath).isAbsolute();
        String realPath = null;
        if (!isReal) {
            //如果不是就获取当前项目路径
            realPath = BasicUtil.getRealPath("");
        } else {
            //如果是就直接取改绝对路径
            realPath = uploadTemplatePath;
        }
        //修改文件名
        if (!config.isRename()) {
            fileName = config.getFile().getOriginalFilename();
            //Windows 系统下文件名最后会去掉. 这种文件默认拒绝  xxx.jsp. => xxx.jsp
            if (fileName.endsWith(".") && System.getProperty("os.name").startsWith("Windows")) {
                LOG.info("文件类型被拒绝:{}", fileName);
                return ResultData.build().error(getResString("err.error", getResString("file.type")));
            }
            fileType = FileUtil.getSuffix(fileName);
        } else {
            //取随机名
            fileName = System.currentTimeMillis() + "." + fileType;
        }
        for (String type : errorType) {
            //校验禁止上传的文件后缀名（忽略大小写）
            if ((fileType).equalsIgnoreCase(type)) {
                LOG.info("文件类型被拒绝:{}", fileType);
                return ResultData.build().error(getResString("err.error", getResString("file.type")));
            }
        }
        // 上传的文件路径,判断是否填的绝对路径
        String uploadFolder = realPath + File.separator;
        //修改upload下的上传路径
        if (StringUtils.isNotBlank(config.getUploadPath())) {
            uploadFolder += config.getUploadPath() + File.separator;
        }
        //保存文件
        File saveFolder = new File(uploadFolder);
        File saveFile = new File(uploadFolder, fileName);
        if (!saveFolder.exists()) {
            FileUtil.mkdir(saveFolder);
        }
        config.getFile().transferTo(saveFile);
        //绝对映射路径处理
        String path = uploadFolder.replace(realPath, "")
                //添加文件名
                + Const.SEPARATOR + fileName;
        //替换多余
        return ResultData.build().success(new File(Const.SEPARATOR + path).getPath().replace("\\", "/").replace("//", "/"));
    }
}
