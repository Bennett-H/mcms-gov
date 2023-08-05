/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mdiy.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.cache.ConfigCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 自定义配置缓存控制层
 * @author 铭软开发团队
 * @version
 * 版本号：1<br/>
 * 创建日期：2022-1-26 15:58:29<br/>
 */
@Api(tags={"后端-自定义模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/mdiy/configCache")
public class ConfigCacheAction extends BaseAction {


    /**
     * 2022-1-26 添加refreshCache() 刷新缓存方法
     */
    @ApiOperation(value = "刷新自定义缓存接口")
    @LogAnn(title = "刷新自定义缓存接口",businessType= BusinessTypeEnum.OTHER)
    @PostMapping("/refreshCache")
    @ResponseBody
    public ResultData refreshCache() {
        ConfigCache configCache = SpringUtil.getBean(ConfigCache.class);
        configCache.deleteAll();
        return ResultData.build().success();
    }

}
