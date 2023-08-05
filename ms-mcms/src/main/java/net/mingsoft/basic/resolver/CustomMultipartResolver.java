/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.basic.resolver;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.base.resolver.MultipartResolver;
import net.mingsoft.co.constant.Const;
import net.mingsoft.config.MultipartProperties;
import net.mingsoft.mdiy.biz.IConfigBiz;
import net.mingsoft.mdiy.entity.ConfigEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * 自动注入解决百度编辑器的上传组件与CommonsMultipartResolver的冲突问题
 * 不可同时配置 MultipartConfigElement 和 CommonsMultipartResolver
 * @author by Administrator
 * @Description TODO
 * @date 2019/9/29 17:11
 * 历史修订:将文件大小管理统一交给FileAction进行管理,这里不进行限制
 */

@Component
@EnableConfigurationProperties(MultipartProperties.class)
public class CustomMultipartResolver extends MultipartResolver {

    @Autowired
    public CustomMultipartResolver(IConfigBiz configBiz,MultipartProperties multipartProperties) throws IOException {
        if(ObjectUtil.isNotNull(multipartProperties.getUploadTempDir())){
            setUploadTempDir(multipartProperties.getUploadTempDir());
        }
        setDefaultEncoding(multipartProperties.getDefaultEncoding());
        ConfigEntity config = configBiz.getOne(new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getConfigName, Const.CONFIG_UPLOAD));
        HashMap<String,String> map = JSONUtil.toBean(config.getConfigData(), HashMap.class);
        int imageSize = 10,videoSize = 10,fileSize = 10,webFileSize = 10;
        if (CollUtil.isNotEmpty(map)){
            if (StringUtils.isNotBlank(map.get("imageSize"))){
                imageSize = Integer.parseInt(map.get("imageSize"));
            }
            if (StringUtils.isNotBlank(map.get("videoSize"))){
                videoSize = Integer.parseInt(map.get("videoSize"));

            }
            if (StringUtils.isNotBlank(map.get("fileSize"))){
                fileSize = Integer.parseInt(map.get("fileSize"));

            }
            if (StringUtils.isNotBlank(map.get("webFileSize"))){
                webFileSize = Integer.parseInt(map.get("webFileSize"));

            }

        }

        int maxFileSize = ArrayUtil.max(imageSize, videoSize, fileSize, webFileSize);

        /**
         * 单位字节，运算需要和Long型数据结合，不能直接运算
         */
        setMaxUploadSize(1024L * 1024L * maxFileSize);
        setMaxUploadSizePerFile(1024L * 1024L * maxFileSize);
        setMaxInMemorySize(multipartProperties.getMaxInMemorySize());
        setResolveLazily(multipartProperties.isResolveLazily());
        setExcludeUrls("jsp/editor.do");
    }

}
