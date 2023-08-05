/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package com.baidu.ueditor.upload;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.baidu.ueditor.define.State;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.biz.IFileBiz;
import net.mingsoft.co.entity.FileEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xierz
 * @Description:
 * @Date: Create in 2021/05/27 15:20
 */


public class Uploader {


    private HttpServletRequest request = null;
    private Map<String, Object> conf = null;

    public Uploader(HttpServletRequest request, Map<String, Object> conf) {
        this.request = request;
        this.conf = conf;
    }

    public final State doExec() {
        String filedName = (String)this.conf.get("fieldName");
        State state = null;
        if ("true".equals(this.conf.get("isBase64"))) {
            state = Base64Uploader.save(this.request.getParameter(filedName), this.conf);
        } else {
            state = BinaryUploader.save(this.request, this.conf);
        }

        HashMap<String,String> hashMap = JSONUtil.toBean(state.toJSONString(), HashMap.class);
        FileEntity fileEntity = new FileEntity();
        IFileBiz fileBiz = SpringUtil.getBean(IFileBiz.class);
        ManagerEntity session =  BasicUtil.getManager();


        if (session != null && state.isSuccess()) {
            fileEntity.setFileType(hashMap.get("type"));
            fileEntity.setFileName(hashMap.get("title"));
            fileEntity.setFileSize(hashMap.get("size"));
            fileEntity.setFilePath(hashMap.get("url"));
            fileEntity.setCreateBy(session.getId());
            fileEntity.setCreateDate(DateTime.now());
            fileBiz.save(fileEntity);
        }


        return state;
    }
}
