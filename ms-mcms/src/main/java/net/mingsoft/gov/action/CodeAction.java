/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.action;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.gov.biz.IManagerInfoBiz;
import net.mingsoft.gov.constant.Const;
import net.mingsoft.gov.entity.ManagerInfoEntity;
import net.mingsoft.msend.util.SendUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;

/**
 * 双因子登录验证码控制层
 * @author 铭飞科技
 * 创建日期：2022-5-25 16:03:06<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-双因子登录验证码接口"})
@Controller("govCodeAction")
@RequestMapping("/${ms.manager.path}/gov/code")
public class CodeAction extends BaseAction{

    /**
     * 注入管理员扩展信息业务层
     */
    @Autowired
    private IManagerInfoBiz managerInfoBiz;

    /**
     * 返回发送验证码页面
     */
    @ApiIgnore
    @GetMapping("/index")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        ManagerEntity manager = BasicUtil.getManager();
        ManagerInfoEntity managerInfo = managerInfoBiz.getOne(new LambdaQueryWrapper<ManagerInfoEntity>().eq(ManagerInfoEntity::getManagerId, manager.getId()));
        if (managerInfo == null){
            request.setAttribute("phone","当前用户没有绑定手机,请联系管理员!");
        }else {
            request.setAttribute("phone",managerInfo.getManagerPhone());
        }
        return "/gov/code/index";
    }

    @ApiOperation(value = "发送验证码")
    @PostMapping("/sendCode")
    @ResponseBody
    public ResultData sendCode() {
        ManagerEntity manager = BasicUtil.getManager();
        ManagerInfoEntity managerInfo = managerInfoBiz.getOne(new LambdaQueryWrapper<ManagerInfoEntity>().eq(ManagerInfoEntity::getManagerId, manager.getId()));
        if(managerInfo==null) {
            throw  new BusinessException(this.getResString("error.account.unbind.phone",manager.getManagerName()));
        }
        String phone = managerInfo.getManagerPhone();
        HashMap map = new HashMap();
        String code = RandomUtil.randomNumbers(4);
        map.put("code", code);
        managerInfo.setManagerCode(code);
        managerInfo.setSendTime(new Date());
        managerInfoBiz.updateById(managerInfo);
        LOG.debug("验证码：{} ",code);
        boolean re = SendUtil.send("SendCloud短信配置","code",phone,map);
        if(re) {
            return ResultData.build().success();
        } else {
            return ResultData.build().error("验证码发送失败");
        }

    }

    @ApiOperation(value = "检查验证码")
    @PostMapping("/checkCode")
    @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "query")
    @ResponseBody
    public ResultData checkCode(String code) {
        if (StringUtils.isBlank(code)){
            return ResultData.build().error("验证码为空!");
        }

        ManagerEntity manager = BasicUtil.getManager();
        ManagerInfoEntity managerInfo = managerInfoBiz.getOne(new LambdaQueryWrapper<ManagerInfoEntity>().eq(ManagerInfoEntity::getManagerId, manager.getId()));
        Date sendTime = managerInfo.getSendTime();
        if(new Date().getTime() - sendTime.getTime() > 60 * 1000 || !code.equals(managerInfo.getManagerCode())){
            return ResultData.build().error("验证码错误!");
        }
        BasicUtil.setSession(Const.MANAGER_VERIFICATION,true);
        return ResultData.build().success();
    }
}
