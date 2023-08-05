/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.resolver;

import cn.hutool.core.util.ObjectUtil;
import net.mingsoft.base.resolver.MultipartResolver;
import net.mingsoft.config.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自动注入解决百度编辑器的上传组件与CommonsMultipartResolver的冲突问题
 * 不可同时配置 MultipartConfigElement 和 CommonsMultipartResolver
 * @author by Administrator
 * @Description TODO
 * @date 2019/9/29 17:11
 */
@Component
@EnableConfigurationProperties(MultipartProperties.class)
public class CustomMultipartResolver extends MultipartResolver {
    public CustomMultipartResolver(MultipartProperties multipartProperties) throws IOException {
      if(ObjectUtil.isNotNull(multipartProperties.getUploadTempDir())){
          setUploadTempDir(multipartProperties.getUploadTempDir());
      }
      setDefaultEncoding(multipartProperties.getDefaultEncoding());
      setMaxUploadSize(multipartProperties.getMaxFileSize());
      setMaxUploadSizePerFile(multipartProperties.getMaxRequestSize());
      setMaxInMemorySize(multipartProperties.getMaxInMemorySize());
      setResolveLazily(multipartProperties.isResolveLazily());
      setExcludeUrls("jsp/editor.do");
    }


}
