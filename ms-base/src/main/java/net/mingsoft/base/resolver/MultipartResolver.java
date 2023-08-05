/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.base.resolver;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.multipart.commons.CommonsMultipartResolver;  

/**
 * 
 * @ClassName:  MultipartResolver   
 * @Description:TODO(同时使用了MultipartResolver 和ServletFileUpload 的冲突解决)   
 * @author: 铭飞开发团队
 * @date:   2018年3月19日 下午3:46:38   
 *     
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
public class MultipartResolver extends CommonsMultipartResolver {  
    private String excludeUrls;     
    private String[] excludeUrlArray;  
      
    public String getExcludeUrls() {  
        return excludeUrls;  
    }  
    public void setExcludeUrls(String excludeUrls) {  
        this.excludeUrls = excludeUrls;  
        this.excludeUrlArray = excludeUrls.split(",");  
    }  
  
    /** 
     * 这里是处理Multipart http的方法。如果这个返回值为true,那么Multipart http body就会MyMultipartResolver 消耗掉.如果这里返回false 
     * 那么就会交给后面的自己写的处理函数处理例如刚才ServletFileUpload 所在的函数 
     * @see CommonsMultipartResolver#isMultipart(HttpServletRequest)
     */  
    @Override  
    public boolean isMultipart(HttpServletRequest request) {  
        for (String url: excludeUrlArray) {  
            // 这里可以自己换判断  
            if (request.getRequestURI().contains(url)) {  
                return false;  
            }  
        }  
        return super.isMultipart(request);  
    }  
       
}  

