/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.pipline;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import net.mingsoft.base.constant.Const;
import net.mingsoft.basic.util.BasicUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static net.mingsoft.spider.constant.Const.*;

/**
 * 注意加载顺序,静态资源文件下载
 */
@Component
public class StaticPipeline implements Pipeline {

    private Logger log = LoggerFactory.getLogger(StaticPipeline.class);

    //存放图片下载地址的线程变量
    public static final ThreadLocal<String> staticFolderThreadLocal= new ThreadLocal<>();

    @Override
    public void process(ResultItems resultItems, Task task) {

        List<Map<String, String>> data = resultItems.get(DESTINATION);
        String staticFolder = resultItems.get(STATIC_FOLDER);
        if (StringUtils.isNotBlank(staticFolderThreadLocal.get())){
            staticFolder = staticFolderThreadLocal.get();
            staticFolderThreadLocal.remove();
        }
        //获取域名
        String domain = task.getSite().getDomain();
        /**
         * 数据格式
         * [
         *    {
         *         field: 数据库对应字段1
         *         data:  匹配内容1
         *         fieldType: 映射类型1
         *    },
         *    {
         *         field: 数据库对应字段2
         *         data:  匹配内容2
         *         fieldType: 映射类型2
         *    },
         * ]
         */
        String http = resultItems.getRequest().getUrl().split(":")[0]+"://";
        log.debug("http=>{}" ,http);
        log.debug("domain=>{}" ,domain);
        String finalStaticFolder = staticFolder;
        data.forEach(map -> {
            map.forEach((k, v) -> {
                //非空校验
                if (!StrUtil.isBlank(v)) {
                    Matcher matcher = IMAGE_PATTERN.matcher(v);
                    while (matcher.find()) {
                        String url = matcher.group("url");
                        // 替换多余路径
                        url = url.replaceAll("\\.\\./|..\\\\", "");
                        log.debug("url=>{}" ,url);
                        String fileName = url.substring(url.lastIndexOf(Const.SEPARATOR) + 1);
//                        fileName =  Base64.encode(fileName);
                        //替换数据v的图片链接
                        String path = finalStaticFolder.replace(BasicUtil.getRealPath(null), "") + Const.SEPARATOR + fileName;
                        v = v.replace(url, new File(Const.SEPARATOR + path).getPath().replace("\\", "/").replace("//", "/"));
                        try {
                            File destFile = new File(BasicUtil.getRealPath(finalStaticFolder), fileName);
                            //存在则跳过
                            if (destFile.exists()){
                                continue;
                            }
                            //判断是否是完整的url链接，不是则拼接域名
                            if (!HttpUtil.isHttp(url) && !HttpUtil.isHttps(url)) {
                                url = http + domain + Const.SEPARATOR + url;
                            }
                            FileUtil.writeBytes(HttpUtil.downloadBytes(url),destFile);
                        }catch (Exception e){
                            log.error("下载图片失败: 目标链接=>{}图片地址{}" ,url);
                        }
                    }
                    map.put(k,v);
                }
            });

            if (map.get(SOURCE) != null) {
                Matcher resourceMatcher = RESOURCE_PATTERN.matcher(map.get(SOURCE));
                while (resourceMatcher.find()) {
                    List<String> urls = JSON.parseObject(map.get(SOURCE), List.class);
                    for (String url : urls) {
                        log.debug("url=>{}" ,url);
                        String fileName = url.substring(url.lastIndexOf(Const.SEPARATOR) + 1);
                        //替换数据v的图片链接
                        try {
                            File destFile = new File(BasicUtil.getRealPath(finalStaticFolder), fileName);
                            //存在则跳过
                            if (destFile.exists()){
                                continue;
                            }
                            //判断是否是完整的url链接，不是则拼接域名
                            if (!HttpUtil.isHttp(url) && !HttpUtil.isHttps(url)) {
                                url = http + domain + Const.SEPARATOR + url;
                            }
                            FileUtil.writeBytes(HttpUtil.downloadBytes(url),destFile);
                        }catch (Exception e){
                            log.error("下载图片失败: 目标链接=>{}图片地址{}" ,url);
                        }
                    }
                }
            }

        });

        //覆盖 处理了静态资源的数据
        resultItems.put(DESTINATION,data);
    }

}
