/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.file.action;

import cn.hutool.core.io.file.FileNameUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseFileAction;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.co.biz.IFileBiz;
import net.mingsoft.co.entity.FileEntity;
import net.mingsoft.file.bean.FileBean;
import net.mingsoft.file.biz.IFileFolderBiz;
import net.mingsoft.file.entity.FileFolderEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2021/12/27 文件预览CRUD
 */
@Api(tags = {"后端-文件管理模块接口"})
@Controller("govFileAction")
@RequestMapping("/${ms.manager.path}/file")
public class FileAction extends BaseFileAction {



    @Autowired
    private IFileFolderBiz folderBiz;


    /**
     * 注入文件表业务层
     */
    @Autowired
    private IFileBiz fileBiz;



    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("basic:file:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/file/index";
    }

    /**
     * 模糊查询所有文件
     *
     * @param fileBean 文件和文件夹的模型
     */
    @ApiOperation(value = "模糊查询所有文件")
    @ApiImplicitParam(name = "fileName", value = "文件名", required = true, paramType = "query")
    @GetMapping("/query")
    @RequiresPermissions("basic:file:view")
    @ResponseBody
    public ResultData query(@ModelAttribute @ApiIgnore FileBean fileBean, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (fileBean.getFileName() == null) {
            return ResultData.build().error();
        }

        ArrayList<FileBean> fileBeans = new ArrayList<>();

        ManagerEntity manager = BasicUtil.getManager();
        QueryWrapper<FileEntity> wrapper = new QueryWrapper<FileEntity>().like("file_name", fileBean.getFileName());
        if (!ManagerAdminEnum.SUPER.toString().equals(manager.getManagerAdmin())){
            wrapper.eq("CREATE_BY", manager.getId());
        }
        List<FileEntity> fileEntities = fileBiz.list(wrapper);

        fileEntities.forEach(file -> {
            FileBean bean = new FileBean();
            bean.setId(file.getId());
            bean.setFileName(file.getFileName());
            bean.isFile(true);
            bean.setFilePath(file.getFilePath());
            bean.setFileDescribe(file.getFileDescribe());
            bean.setFileType(file.getFileType());
            bean.setFileSize(file.getFileSize());
            bean.setFolderId(file.getFolderId());
            fileBeans.add(bean);
        });

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("files", fileBeans);
        return ResultData.build().success(resultMap);
    }


    /**
     * 获取当前文件夹的子文件夹和对应的文件
     *
     * @param fileBean 文件和文件夹的模型
     */
    @ApiOperation(value = "获取当前文件夹的子文件夹和对应的文件")
    @ApiImplicitParams({

            @ApiImplicitParam(name = "id", value = "文件夹id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "file", value = "是否是文件", defaultValue = "true", required = true, paramType = "query"),
    })
    @GetMapping("/list")
    @ResponseBody
    @RequiresPermissions("basic:file:view")
    public ResultData list(@ModelAttribute @ApiIgnore FileBean fileBean, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (fileBean.getId() == null) {
            return ResultData.build().error();
        }
        FileFolderEntity entity = folderBiz.getById(fileBean.getId());

        List<FileFolderEntity> fileFolderEntities = folderBiz.queryChild(fileBean.getId());
        List<FileEntity> fileEntities =  fileBiz.listByFolderId(fileBean.getId());

        /**
         * 构造返回对象
         */
        List<FileBean> fileBeans = new ArrayList<>();
        fileFolderEntities.forEach(folder -> {
            QueryWrapper folderWrapper = new QueryWrapper<FileFolderEntity>().eq("parent_id", folder.getId());

            long folderCount = folderBiz.count(folderWrapper);

            QueryWrapper fileWrapper = new QueryWrapper<FileFolderEntity>().eq("folder_id", folder.getId());

            long fileCount = fileBiz.count(fileWrapper);

            boolean childFile = fileCount > 0 || folderCount > 0;

            FileBean bean = new FileBean();
            bean.setId(folder.getId());
            bean.setFileName(folder.getFolderName());
            bean.isFile(false);
            bean.setFilePath(folder.getPath());
            bean.setFileDescribe(folder.getFolderDescribe());
            bean.setFileType("-");
            bean.setFileSize("-");
            bean.setCreateBy(folder.getCreateBy());
            bean.setCreateDate(folder.getCreateDate());
            bean.setUpdateDate(folder.getUpdateDate());
            bean.setFolderId(folder.getParentId());
            bean.setChildFile(childFile);
            fileBeans.add(bean);
        });

        fileEntities.forEach(file -> {
            FileBean bean = new FileBean();
            bean.setId(file.getId());
            bean.setFileName(file.getFileName());
            bean.isFile(true);
            bean.setFilePath(file.getFilePath());
            bean.setFileDescribe(file.getFileDescribe());
            bean.setFileType(file.getFileType());
            bean.setFileSize(file.getFileSize());
            bean.setCreateBy(file.getCreateBy());
            bean.setCreateDate(file.getCreateDate());
            bean.setUpdateDate(file.getUpdateDate());
            bean.setFolderId(file.getFolderId());
            fileBeans.add(bean);
        });

        ManagerEntity manager = BasicUtil.getManager();
        if (!ManagerAdminEnum.SUPER.toString().equals(manager.getManagerAdmin())){
            fileBeans.stream().filter(_fileBean -> manager.getId().equals(fileBean.getCreateBy())).collect(Collectors.toList());
        }

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("files", fileBeans);
        resultMap.put("localPath", entity.getPath());
        resultMap.put("id", entity.getId());
        resultMap.put("folderId", entity.getParentId());
        return ResultData.build().success(resultMap);
    }

    /**
     * 保存文件夹
     *
     * @param fileBean 文件和文件夹的模型
     */
    @ApiOperation(value = "保存文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileDescribe", value = "文件描述", required = true, paramType = "query"),
            @ApiImplicitParam(name = "fileName", value = "文件夹名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "folderId", value = "请选择父节点", required = true, paramType = "query"),
    })
    @PostMapping("/save")
    @ResponseBody
    @LogAnn(title = "保存文件夹", businessType = BusinessTypeEnum.INSERT)
    @RequiresPermissions("basic:file:save")
    public ResultData save(@ModelAttribute @ApiIgnore FileBean fileBean, HttpServletResponse response, HttpServletRequest request) {
        if (!StringUtil.checkLength(fileBean.getFileName() + "", 0, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("folder.name"), "0", "255"));
        }
        if (fileBean.isFile()) {
            return ResultData.build().error(getResString("err.type", this.getResString("folder.type"), "0", "255"));

        }
        String fileName = fileBean.getFileName();
        String newName = FileNameUtil.cleanInvalid(fileName);
        if (!fileName.equals(newName)) {
            return ResultData.build().error("文件夹名称包括非法字符!");
        }
        QueryWrapper<FileFolderEntity> wrapper = new QueryWrapper<FileFolderEntity>()
                .eq("folder_name", fileName)
                .and(fileWrapper -> {
                    fileWrapper.eq("parent_id", fileBean.getFolderId());
                });
        if (folderBiz.getOne(wrapper) != null) {
            return ResultData.build().error("文件夹名称重复!");
        }
        FileFolderEntity fileFolderEntity = new FileFolderEntity();
        fileFolderEntity.setParentId(fileBean.getFolderId());
        fileFolderEntity.setFolderName(fileName);
        fileFolderEntity.setFolderDescribe(fileBean.getFileDescribe());
        fileFolderEntity.setCreateBy(BasicUtil.getManager().getId());
        folderBiz.saveEntity(fileFolderEntity);
        return ResultData.build().success(fileFolderEntity);
    }


    /**
     * 删除文件表
     *
     * @param files 文件和文件夹的模型
     */
    @ApiOperation(value = "批量删除文件和文件夹接口")
    @PostMapping("/delete")
    @ResponseBody
    @LogAnn(title = "删除文件或文件夹", businessType = BusinessTypeEnum.DELETE)
    @RequiresPermissions("basic:file:del")
    public ResultData delete(@RequestBody List<FileBean> files, HttpServletResponse response, HttpServletRequest request) {
        List<String> FileFolderIds = files.stream()
                .filter(fileBean -> !fileBean.isFile())
                .map(fileBean -> fileBean.getId())
                .collect(Collectors.toList());

        List<String> FileIds = files.stream()
                .filter(fileBean -> fileBean.isFile())
                .map(fileBean -> fileBean.getId())
                .collect(Collectors.toList());

        if (FileFolderIds != null && FileFolderIds.size() > 0) {
            folderBiz.removeByIds(FileFolderIds);

        }
        if (FileIds != null && FileIds.size() > 0) {
            fileBiz.removeByIds(FileIds);

        }
        return ResultData.build().success();
    }

    /**
     * 更新文件表列表
     *
     * @param fileBean 文件和文件夹的模型
     */
    @ApiOperation(value = "更新文件或文件夹接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileDescribe", value = "多行文本", required = true),
            @ApiImplicitParam(name = "file", value = "是否是文件", required = true),
            @ApiImplicitParam(name = "file  Name", value = "文件名", required = true),
    })
    @PostMapping("/update")
    @ResponseBody
    @LogAnn(title = "更新文件或文件夹", businessType = BusinessTypeEnum.UPDATE)
    @RequiresPermissions("basic:file:update")
    public ResultData update(@ModelAttribute @ApiIgnore FileBean fileBean, HttpServletResponse response,
                             HttpServletRequest request) {

        if (!StringUtil.checkLength(fileBean.getFilePath() + "", 0, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("file.path"), "0", "255"));
        }
        if (!StringUtil.checkLength(fileBean.getFileSize() + "", 0, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("file.size"), "0", "255"));
        }
        if (!StringUtil.checkLength(fileBean.getFileType() + "", 0, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("file.type"), "0", "255"));
        }
        if (!StringUtil.checkLength(fileBean.getFileName() + "", 0, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("file.name"), "0", "255"));
        }


        try {
            String fileName = fileBean.getFileName();

            /**
             * 更新文件
             */
            if (fileBean.isFile()) {
                String newName = FileNameUtil.cleanInvalid(fileName);
                if (!fileName.equals(newName)){
                    throw new RuntimeException("文件名称包括非法字符!");
                }
                QueryWrapper<FileEntity> wrapper = new QueryWrapper<FileEntity>()
                        .eq("file_name", fileName)
                        .and(fileEntityQueryWrapper -> {
                            fileEntityQueryWrapper.eq("folder_id",fileBean.getFolderId());
                        });
                FileEntity fileEntity = fileBiz.getOne(wrapper);
                if(fileEntity != null && !fileEntity.getId().equals(fileBean.getId())){
                    throw new RuntimeException("文件名称重复!");

                }
                FileEntity entity = fileBiz.getById(fileBean.getId());
                entity.setFileName(fileName);
                entity.setFileDescribe(fileBean.getFileDescribe());
                fileBiz.updateById(entity);
            } else {
                String newName = FileNameUtil.cleanInvalid(fileName);
                if (!fileName.equals(newName)){
                    throw new RuntimeException("文件夹名称包括非法字符!");

                }
                QueryWrapper<FileFolderEntity> wrapper = new QueryWrapper<FileFolderEntity>()
                        .eq("folder_name", fileName)
                        .and(fileWrapper -> {
                            fileWrapper.eq("parent_id",fileBean.getFolderId());
                        });
                FileFolderEntity folderEntity = folderBiz.getOne(wrapper);
                if(folderEntity != null && !folderEntity.getId().equals(fileBean.getId())){
                    throw new RuntimeException("文件夹名称重复!");

                }
                FileFolderEntity entity = folderBiz.getById(fileBean.getId());
                entity.setFolderDescribe(fileBean.getFileDescribe());
                folderBiz.updateById(entity);
            }
        } catch (Exception e) {
            return ResultData.build().error(e.getMessage());

        }
        return ResultData.build().success();

    }




}

