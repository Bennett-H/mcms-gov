/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * 自动注入上传配置
 * @Description: TODO(字符串工具类)
 * @author 铭软开发团队
 * @date 2020年7月2日
 */
@ConfigurationProperties(prefix = "ms.upload.multipart", ignoreUnknownFields = false)
public class MultipartProperties {
	/**
	 * 文件大小
	 */
	private long maxFileSize = 1024;
	/**
	 * 最大请求大小
	 */
	private long maxRequestSize = 10240;
	/**
	 * 开启延时加载
	 */
	private boolean resolveLazily = false;
	/**
	 * 文件编码
	 */
	private String defaultEncoding =  "ISO-8859-1";
	/**
	 * 文件临时存放目录
	 */
	private Resource uploadTempDir = null;
	/**
	 * 临时文件大小
	 */
	private int maxInMemorySize = 4096;



	public long getMaxFileSize() {
		return maxFileSize*1000;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public long getMaxRequestSize() {
	    if(maxRequestSize>0){
            return maxRequestSize*1000;
        }else {
	        return maxRequestSize;
        }
	}

	public void setMaxRequestSize(long maxRequestSize) {
		this.maxRequestSize = maxRequestSize;
	}


	public boolean isResolveLazily() {
		return resolveLazily;
	}

	public void setResolveLazily(boolean resolveLazily) {
		this.resolveLazily = resolveLazily;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public Resource getUploadTempDir() {
		return uploadTempDir;
	}

	public void setUploadTempDir(Resource uploadTempDir) {
		this.uploadTempDir = uploadTempDir;
	}

	public int getMaxInMemorySize() {
		return maxInMemorySize;
	}

	public void setMaxInMemorySize(int maxInMemorySize) {
		this.maxInMemorySize = maxInMemorySize;
	}
}
