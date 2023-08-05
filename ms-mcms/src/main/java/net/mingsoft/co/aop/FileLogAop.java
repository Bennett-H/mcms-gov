/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.aop;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.file.FileNameUtil;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseFileAction;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.biz.IFileBiz;
import net.mingsoft.co.entity.FileEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 文件上传拦截，将文件上传记录到file表中
 * @author 铭软开发团队
 * @version
 * 版本号：1.0<br/>
 * 创建日期：2017-8-24 23:40:55<br/>
 * 历史修订：br/>
 */
@Component
@Aspect
public class FileLogAop extends BaseAop {


    @Autowired
    IFileBiz fileBiz;

    @AfterReturning(pointcut = "execution(* net.mingsoft.basic.action.web.FileAction.upload(..)) || execution(* net.mingsoft.basic.action.ManageFileAction.upload(..))", returning = "result")
    public void upload(JoinPoint joinPoint, Object result) {
        UploadConfigBean bean = this.getType(joinPoint, UploadConfigBean.class);
        FileEntity fileEntity = new FileEntity();
        ResultData _result = (ResultData) result;
        // 报错或者切片都不增加上传记录
        if (_result.getCode() != HttpStatus.OK.value()){
            return;
        }
        String filePath =(String) _result.get("data");
        ManagerEntity session =  BasicUtil.getManager();

        if (session != null && StringUtils.isBlank(bean.getFileUploadedPath(fileBiz))) {
            fileEntity.setCreateBy(session.getId());
            fileEntity.setCreateDate(DateTime.now());
            fileEntity.setFilePath(filePath);
            fileEntity.setFolderId(bean.getFolderId());
            fileEntity.setFileIdentifier(bean.getFileIdentifier());
            fileEntity.setFileSize(String.valueOf(bean.getFile().getSize()));
            fileEntity.setFileName(StringUtils.isBlank(bean.getFileName())?bean.getFile().getOriginalFilename():bean.getFileName());
            fileEntity.setFileType(FileNameUtil.getSuffix(filePath));
            fileBiz.save(fileEntity);
        }


    }

}
